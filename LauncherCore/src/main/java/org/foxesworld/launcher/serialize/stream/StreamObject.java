package org.foxesworld.launcher.serialize.stream;

import org.foxesworld.launcher.serialize.HInput;
import org.foxesworld.launcher.serialize.HOutput;
import org.foxesworld.utils.helper.IOHelper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public abstract class StreamObject {
    /* public StreamObject(HInput input) */

    public final byte[] write() throws IOException {
        try (ByteArrayOutputStream array = IOHelper.newByteArrayOutput()) {
            try (HOutput output = new HOutput(array)) {
                write(output);
            }
            return array.toByteArray();
        }
    }

    public abstract void write(HOutput output) throws IOException;


    @FunctionalInterface
    public interface Adapter<O extends StreamObject> {

        O convert(HInput input);
    }
}
