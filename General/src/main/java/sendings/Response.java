package sendings;

import constants.Messages;

import java.io.*;

public class Response {
    public Response() {
    }

    public Response(String message) {
        this.report = message;
    }

    private String report;

    public String getReport() {
        return report;
    }

}
