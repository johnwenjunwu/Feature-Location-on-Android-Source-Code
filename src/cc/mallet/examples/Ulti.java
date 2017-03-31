package cc.mallet.examples;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Ulti {
    static void createDirectory(String source) throws IOException {

        Files.createDirectories(Paths.get(source));
        Files.createDirectory(Paths.get(source + "/feature"));
        Files.createDirectory(Paths.get(source + "/instance"));
        Files.createDirectory(Paths.get(source + "/model"));
        Files.createDirectory(Paths.get(source + "/model/gson"));
        Files.createDirectory(Paths.get(source + "/model/model"));
        Files.createDirectory(Paths.get(source + "/test"));
        Files.createDirectory(Paths.get(source + "/test/cos"));
        Files.createDirectory(Paths.get(source + "/test/kl"));
    }
    static void deleteAllFiles(String p) throws IOException {
        Files.walk(Paths.get(p), FileVisitOption.FOLLOW_LINKS)
                .map(Path::toFile)
                .filter(File::isFile)
                .peek(System.out::println)
                .forEach(File::delete);
    }
}
