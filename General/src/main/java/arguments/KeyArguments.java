package arguments;

import logic.IODevice;
public class KeyArguments implements Readable {
    /**
     * Класс представляет считыватель ключа в виде строки
     */
    public KeyArguments(){
    }
    @Override
    public String read(IODevice io) {
        return io.read();
    }
}
