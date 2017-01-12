package cc.mallet.examples;

import cc.mallet.topics.ParallelTopicModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class Main {
    static String source = "show password";
    static String[] test = {
            "SSL file based session cache",
            "use client certificate authentication",
            "get Open Key chain",
    };

    static void createDirectory() throws IOException {
        Files.createDirectory(Paths.get(Main.source));
        Files.createDirectory(Paths.get(Main.source + "/feature"));
        Files.createDirectory(Paths.get(Main.source + "/instance"));
        Files.createDirectory(Paths.get(Main.source + "/model"));
        Files.createDirectory(Paths.get(Main.source + "/model/gson"));
        Files.createDirectory(Paths.get(Main.source + "/model/model"));
        Files.createDirectory(Paths.get(Main.source + "/test"));
        Files.createDirectory(Paths.get(Main.source + "/test/cos"));
        Files.createDirectory(Paths.get(Main.source + "/test/kl"));
    }

    public static void main(String[] args) throws Exception {
        // System.out.println(new Stemmer().stem("yes"));
//        createDirectory();
//        JsonReadWrite.main(args);

        //int[] b = (int[]) new ObjectInputStream(new FileInputStream("kl/" + name)).readObject();
        //GenerateModel();
        runTest();
//        analyzeModel();
//        sortModel();

//        Vector<MissResult> results = new Gson().fromJson(new String(Files.readAllBytes(Paths.get("missResult_cos"))),
//                new TypeToken<Vector<MissResult>>() {
////                }.getType());
//        for (int i = 0; i < results.size(); ++i) {
//            String name = results.get(i).modelName;
////            if (!name.split("_")[2].equals("4"))
////                continue;
//            System.out.println(name);
////            List<String> feature = Files.readAllLines(Paths.get("feature/wordsMoreThan" + name.split("_")[2]));
////            for (int j = 10; j < 11; ++j) {
////                System.out.println(feature.get(j));
//        new Test(source, "80_2000_4", source).run();

        //            //}
//            //break;
//        }

//        ExecutorService executor = Executors.newFixedThreadPool(8);
//
//        for (int topic = 10; topic <= 80; topic *= 2) {
//            for (int train = 100; train <= 2000; train *= 2) {
//                for (int mini = 1; mini <= 20; mini *= 2) {
//                    int a = topic, b = train, c = mini;
//                    executor.execute(new MyTopicModel(a, b, c));
//                }
//            }
//        }
//
//        executor.shutdown();
//        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

    }

//    static void sortModel() throws IOException, ClassNotFoundException {
//        Vector<MissResult> sort = new Vector<>();
//        tag:
//        for (int topic = 10; topic <= 80; topic *= 2) {
//            for (int train = topic * 3; train <= topic * 20; train *= 1.2) {
//                for (int mini = 1; mini <= 20; mini *= 2) {
////                    int a = topic, b = train, c = mini;
//                    String name = topic + "_" + train + "_" + mini;
//
//                    int[] pos = new Gson().fromJson(new String(Files.readAllBytes(Paths.get("cos/" + name))), int[].class);
//                    sort.add(new MissResult(name, IntStream.of(pos).sum()));
//                }
//            }
//        }
//        sort.sort(Comparator.comparingInt(m -> m.missSum));
//
//        Files.write(Paths.get("missResult_cos"), new Gson().toJson(sort).getBytes());
//        //Map<String, Integer> j = gson.fromJson(new FileReader("a"), sortedMap.getClass());
//        //System.out.println(j);
//        //new ObjectOutputStream(new FileOutputStream("sortedResult")).writeObject(sortedMap);
//    }
//
//    private static void analyzeModel() throws Exception {
//        ExecutorService executor = Executors.newFixedThreadPool(8);
//
//        tag:
//        for (int topic = 10; topic <= 80; topic *= 2) {
//            for (int train = topic * 3; train <= topic * 20; train *= 1.2) {
//                for (int mini = 1; mini <= 20; mini *= 2) {
//                    int a = topic, b = train, c = mini;
//                    executor.execute(new Result(a, b, c, b));
//                }
//            }
//        }
//
//        executor.shutdown();
//        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
//    }

    public static void deleteGson() throws IOException {
        Arrays.stream(new File(source + "/model/gson").listFiles()).forEach(f -> f.delete());
        Arrays.stream(new File(source + "/model").listFiles()).forEach(f -> {
            if (f.isFile())
                f.delete();
        });

//        Files.deleteIfExists(Paths.get(source + "/model/gson"));
//        Files.createDirectory(Paths.get(source + "/model/gson"));
    }

    private static void GenerateModel() throws InterruptedException, IOException {
        deleteGson();

        ExecutorService executor = Executors.newFixedThreadPool(8);

        tag:
        for (int topic = 20; topic <= 160; topic *= 1.5) {
            for (int train = 100; train <= 2000; train *= 2) {
                for (int mini = 1; mini <= 20; mini *= 2) {
                    int a = topic, b = train, c = mini;
                    executor.execute(new MyTopicModel(source, a, b, c));
                    // break tag;
                }
            }
        }

        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
    }

    private static void runTest() throws InterruptedException, IOException {
        Arrays.stream(new File(source + "/test/cos").listFiles()).forEach(File::delete);
        new File(source + "/test/result").delete();

        ExecutorService executor = Executors.newFixedThreadPool(8);

        tag:
        for (int topic = 45; topic <= 160; topic *= 1.5) {
            for (int train = 800; train <= 2000; train *= 2) {
                for (int mini = 8; mini <= 20; mini *= 2) {
                    for (int iter = 100; iter <= 200; iter *= 2) {
                        int a = topic, b = train, c = mini, i = iter;
                        executor.execute(new Test(source, a + "_" + b + "_" + c,
                                "view password by clicking on checkbox Show password", i));
                    }
                }
            }
        }

        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
    }

//    private static void AnalyzeModel(Map<Integer, Vector<Result>> results) throws FileNotFoundException {
//
//        System.setOut(new PrintStream("MethodFeature_SelfCheck"));
//
//        results.forEach((k, v)->{
//            v.sort(Comparator.comparingInt(a -> a.notFound));
//            System.out.println("top:" + k);
//            v.forEach(System.out::println);
//            System.out.println();
//        });
//    }

}
