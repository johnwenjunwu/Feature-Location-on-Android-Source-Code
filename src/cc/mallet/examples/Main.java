package cc.mallet.examples;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class Main {
    static String base = "/Users/wuwenjun/Documents/study/features/";
    String question = "activity.setup.AccountSetupIncoming.java";
    String source = "data/" + question;

    public Main() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
//        createDirectory();
//
//        new JsonReadWrite(base + "/view password by clicking on checkbox Show password", source);

        //int[] b = (int[]) new ObjectInputStream(new FileInputStream("kl/" + name)).readObject();
//        GenerateModel();
        DependencyModel();
        runTest();
        Dependency.generateDependencyFeature(source);
//        new ResultRanking(source);
    }

    void createDirectory() throws IOException {
//        Files.createDirectory(Paths.get(Main.source.split("/", 2)[0]));
        Files.createDirectory(Paths.get(source));
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

    public static void main(String[] args) throws Exception {
        // System.out.println(new Stemmer().stem("yes"));

        new Main();
    }

    private void GenerateModel() throws InterruptedException, IOException {
        deleteAllFiles(source + "/model");

        ExecutorService executor = Executors.newFixedThreadPool(8);

        tag:
        for (int topic = 10; topic <= 100; topic *= 2) {
            for (int train = 400; train <= 4000; train *= 2) {
                for (int mini = 1; mini <= 1; mini *= 2) {
                    int a = topic, b = train, c = mini;
                    executor.execute(new MyTopicModel(source, a, b, c));
                    break tag;
                }
            }
        }

        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
    }

    private void DependencyModel() throws InterruptedException, IOException {
        deleteAllFiles(source + "/model");

        ExecutorService executor = Executors.newFixedThreadPool(8);

        tag:
        for (int topic = 10; topic <= 100; topic *= 2) {
            for (int train = 400; train <= 4000; train *= 2) {
                for (int mini = 1; mini <= 1; mini *= 2) {
                    int a = topic, b = train, c = mini;
                    executor.execute(new DependencyTopic(source, a, b, c));
                    break tag;
                }
            }
        }

        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
    }

    private void runTest() throws InterruptedException, IOException {
        deleteAllFiles(source + "/test");
        ExecutorService executor = Executors.newFixedThreadPool(8);

        tag:
        for (int topic = 10; topic <= 100; topic *= 2) {
            for (int train = 400; train <= 4000; train *= 2) {
                for (int mini = 1; mini <= 1; mini *= 2) {
                    for (int iter = train; iter <= train; iter *= 2) {
                        int a = topic, b = train, c = mini, i = iter;
                        executor.execute(new Test(source, a + "_" + b + "_" + c,
                                question, i));
                        break tag;
                    }
                }
            }
        }

        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
    }



}
