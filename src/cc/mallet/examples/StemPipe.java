package cc.mallet.examples;

import cc.mallet.pipe.Pipe;
import cc.mallet.types.Instance;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class StemPipe extends Pipe implements Serializable {

    public Instance pipe (Instance carrier) {

        if (carrier.getData() instanceof CharSequence) {
            CharSequence data = (CharSequence) carrier.getData();
            String[] words = data.toString().split(" ");
            StringBuilder builder = new StringBuilder();
            for (String word: words)
                builder.append(new newStem().stem(word)).append(' ');
            carrier.setData(builder);
        }
        else {
            throw new IllegalArgumentException("CharSequenceLowercase expects a CharSequence, found a " + carrier.getData().getClass());
        }

        return carrier;
    }

    // Serialization

    private static final long serialVersionUID = 1;
    private static final int CURRENT_SERIAL_VERSION = 0;

    private void writeObject (ObjectOutputStream out) throws IOException {
        out.writeInt (CURRENT_SERIAL_VERSION);
    }

    private void readObject (ObjectInputStream in) throws IOException, ClassNotFoundException {
        int version = in.readInt ();
    }

}
