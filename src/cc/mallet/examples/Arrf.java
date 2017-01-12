package cc.mallet.examples;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class Arrf {
    public static void main(String[] args) throws IOException {
        StringBuilder builder = new StringBuilder();
        String[] contents = new String(Files.readAllBytes(Paths.get("/Users/wuwenjun/Downloads/component_related-to_concern.arff")))
                .split("@DATA\n",2)[1].split("\n");
        for (String line: contents) {
            String[] con = line.split("\"");
            if (con.length > 3)
                builder.append(con[1]).append('\n');
        }
        Files.write(Paths.get(Main.source + "/test/flat"), builder.toString().getBytes());
    }
}
