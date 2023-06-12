package logic;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import constants.Messages;
import elements.Person;
import exceptions.StartingProblemException;

import java.io.*;
import java.util.Hashtable;
import java.util.Scanner;

public class JsonHandler implements Closeable {
    private final Scanner input;
    private final BufferedOutputStream output;
    private final File file;

    public JsonHandler(String fileName) throws StartingProblemException {
        try {
            this.file = new File(fileName);
            input = new Scanner(new FileInputStream(file));
            output = new BufferedOutputStream(new FileOutputStream(file, true));
        } catch (FileNotFoundException e) {
            throw new StartingProblemException(Messages.getMessage("warning.format.file_not_found", fileName));
        }
    }

    private String readFileAsString() {
        StringBuilder res = new StringBuilder();
        while (input.hasNextLine()) {
            res.append(input.nextLine()).append('\n');
        }
        return res.toString();
    }


    public void clear() throws IOException {
        new FileOutputStream(file).close();
    }

    public void writeData(Hashtable collection) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.registerModule(new JavaTimeModule());
        byte[] buf = mapper.writeValueAsBytes(collection);
        output.write(buf);
        output.flush();
    }

    public Hashtable<String, Person> readCollection() throws StartingProblemException {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<Hashtable<String, Person>> typeRef = new TypeReference<>() {
        };
        String fileAsString = readFileAsString();
        mapper.registerModule(new JavaTimeModule());
        if (fileAsString.isBlank()) {
            return new Hashtable<>();
        }
        try {
            return mapper.readValue(fileAsString, typeRef);
        } catch (JsonProcessingException e) {
            throw new StartingProblemException(String.format("В файле %s: %s", file.getName(), e.getMessage()));
        }
    }

    @Override
    public void close() {
        try {
            output.close();
            input.close();
        } catch (IOException e) {
            throw new RuntimeException(Messages.getMessage("warning.file_not_closed"));
        }
    }
}
