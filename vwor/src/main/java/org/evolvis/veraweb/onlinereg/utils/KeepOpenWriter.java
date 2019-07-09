package org.evolvis.veraweb.onlinereg.utils;
import java.io.IOException;
import java.io.Writer;

public class KeepOpenWriter extends Writer {

    private final Writer delegatee;

    public KeepOpenWriter(Writer delegatee) {
        this.delegatee = delegatee;
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        delegatee.write(cbuf, off, len);
    }

    @Override
    public void flush() throws IOException {
        delegatee.flush();
    }

    @Override
    public void close() throws IOException {
        delegatee.flush();
    }
}
