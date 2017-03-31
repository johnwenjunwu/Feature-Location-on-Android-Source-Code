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
    static HashMap<String, String> question = new HashMap<>();

    static String name = "f11";
    static String root = "data/" + name;

    static HashMap<String, String[]> ans = new HashMap<>();
    static {
        question.put("f2", "Client Certificate Authentication");
        question.put("f5", "Change message body font size with slider");
        question.put("f10", "Use json for serializing pending commands");
        question.put("f11", "Test and fix database upgrade process");
        question.put("f12", "Add unit tests for ImapConnection");

        String[] s2 = {"ClientCertificateSpinner.java",
                "KeyChainKeyManager.java",
                "SslHelper.java",
                "ClientCertificateRequiredException.java"
        };
        String[] s5 = {"FontSizeSettings.java",
                "FontSizes.java",
                "SliderPreference.java"};
        String[] s10 = {
                "MessagingControllerCommands.java",
        "PendingCommandSerializer.java",
        "MigrationTo60.java",
        };

        String[] s11 = {
                "MigrationTo59.java",
                "StoreSchemaDefinitionTest.java"
        };
        String[] s12 = {
                "ImapConnection.java",
                "ImapConnectionTest.java",
                "SimpleImapSettings.java",
                "MockImapServer.java",
        };
        ans.put("f2", s2);
        ans.put("f5", s5);
        ans.put("f10", s10);
        ans.put("f11", s11);
    }

    public static void main(String[] args) throws Exception {
//        Ulti.deleteAllFiles(root);
//        new Arrf(root, name);
//        new JsonReadWrite(base + name, root + "/file");
//        Dependency.generateDependencyFeature(root + "/dependency", base + name + "/a.xml");
        lda();
//        dependency();
    }

    public static void lda() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(8);

        tag:
        for (String f: JsonReadWrite.types) {
            for (int topic = 40; topic <= 80; topic *= 2) {
                for (int train = 4000; train <= 4000; train *= 2) {
                    for (int mini = 8; mini <= 8; mini *= 2) {
                        for (int iter = train; iter <= train; iter *= 2) {
                            int tp = topic, tr = train, m = mini, i = iter;
                            executor.execute(() -> {
                                String fileRoot = root + "/file/" + f;
                                new FileTopic(fileRoot, tp, tr, m).run();
                                new Test(fileRoot, tp + "_" + tr + "_" + m,
                                        question.get(name), i).run();
                                new ResultRanking(fileRoot, ans.get(name));
                            });
                        }

                    }
                }
            }
        }

        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
    }

    public static void dependency() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(8);

        tag:
        for (int dTopic = 20; dTopic <= 60; dTopic *= 1.2) {
            for (int dTrain = 1000; dTrain <= 4000; dTrain *= 2) {
                for (int dMini = 1; dMini <= 1; dMini *= 2) {
                    for (int diter = dTrain; diter <= dTrain; diter *= 2) {
                        for (int top = 2; top <= 4; top ++) {

                            int dtp = dTopic, dtr = dTrain, dm = dMini,
                                    di = diter, dtop = top;
                            executor.execute(()->{
                                String dRoot = root + "/dependency";
                                new DependencyTopic(dRoot, dtp, dtr, dm).run();
                                new TestDependency(dRoot, dtp + "_" + dtr + "_" + dm,
                                        root + "/file/R/test/cos/40_4000_8_4000.json"
                                        , di, dtop).run();
                                new ResultRanking(dRoot, ans.get(name));
                            });
//                            break tag;
                        }
                    }
                }
            }
        }
        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
    }
//    private void GenerateModel() throws InterruptedException, IOException {
//        deleteAllFiles(dir + "/model");
//
//        ExecutorService executor = Executors.newFixedThreadPool(8);
//

//
//        executor.shutdown();
//        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
//    }


}
