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

    static String name = "f24";
    static String root = "data/" + name;

    static HashMap<String, String[]> ans = new HashMap<>();
    static {
        question.put("f2", "Client Certificate Authentication");
        question.put("f5", "Change message body font size with slider");
        question.put("f10", "Use json for serializing pending commands");
        question.put("f11", "Test and fix database upgrade process");
        question.put("f13", "Message list widget");
        question.put("f21", "Detect instant uploads with different observer implementations depending on device manufacturer");
        question.put("f22", "Retry transfers interrupted by connectivity loss");
        question.put("f23", "Multiple public shares");
        question.put("f24", "Support hidden version in status.php taking version from capabilities API as prefered");
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
        String[] s13 = {
                "MessageListRemoteViewFactory.java",
                        "MessageListWidgetProvider.java",
                        "MessageListWidgetService.java"
        };
        String[] s21 = {
                "FileObserverService.java",
                "InstantUploadsHandler.java",
                "InstantUploadsObserver.java",
                "InstantUploadsObserverBasedOnCommonsIOFileMonitor.java",
                "InstantUploadsObserverBasedOnINotify.java",
                "InstantUploadsObserverFactory.java"
        };
        String[] s22 = {
                "FileDownloader.java",
                "FileUploader.java",
                "RetryDownloadJobService.java",
                "RetryUploadJobService.java",
                "TransferRequester.java",
                "Extras.java",
                "PowerUtils.java",
        };
        String[] s23 = {
                "FileDataStorageManager.java",
                "OCFile.java",
                "CreateShareViaLinkOperation.java",
                "RemoveShareOperation.java",
                "UpdateShareViaLinkOperation.java",
                "FileContentProvider.java",
                "OperationsService.java",
                "ShareActivity.java",
                "SharePublicLinkListAdapter.java",
                "ExpirationDatePickerDialogFragment.java",
                "RemoveShareDialogFragment.java",
                "PublicShareDialogFragment.java",
                "ShareFileFragment.java",
                "FileOperationsHelper.java",
                "DateUtils.java"
        };
        String[] s24 = {
                "AccountUtils.java",
                "DetectAuthenticationMethodOperation.java",
                "GetServerInfoOperation.java",
                "RefreshFolderOperation.java",
                "SyncCapabilitiesOperation.java",

        };
        ans.put("f2", s2);
        ans.put("f5", s5);
        ans.put("f10", s10);
        ans.put("f11", s11);
        ans.put("f13", s13);
        ans.put("f21", s21);
        ans.put("f22", s22);
        ans.put("f23", s23);
        ans.put("f24", s24);
    }

    public static void main(String[] args) throws Exception {
//        Ulti.deleteAllFiles(root);
//        new Arrf(root, name);
//        new JsonReadWrite(base + name, root + "/file");
//        Dependency.generateDependencyFeature(root + "/dependency", base + name + "/a.xml");
        lda();
        dependency();
    }

    public static void lda() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(8);

        tag:
        for (String f: JsonReadWrite.types) {
            for (int topic = 40; topic <= 40; topic *= 1.2) {
                for (int train = 4000; train <= 4000; train *= 2) {
                    for (int mini = 4; mini <= 8; mini *= 2) {
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
        for (int dTopic = 40; dTopic <= 40; dTopic *= 1.2) {
            for (int dTrain = 1000; dTrain <= 2000; dTrain *= 2) {
                for (int dMini = 1; dMini <= 1; dMini *= 2) {
                    for (int diter = dTrain; diter <= dTrain; diter *= 2) {
                        for (int top = 2; top <= 4; top ++) {

                            int dtp = dTopic, dtr = dTrain, dm = dMini,
                                    di = diter, dtop = top;
                            executor.execute(()->{
                                String dRoot = root + "/dependency";
                                new DependencyTopic(dRoot, dtp, dtr, dm).run();
                                new TestDependency(dRoot, dtp + "_" + dtr + "_" + dm,
                                        root + "/file/R/test/cos/40_4000_4_4000.json"
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
