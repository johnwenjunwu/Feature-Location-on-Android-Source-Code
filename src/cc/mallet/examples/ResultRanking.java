package cc.mallet.examples;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Vector;

public class ResultRanking {
    ResultRanking(String source) throws IOException {
        Files.list(Paths.get(source + "/test/cos")).sorted(new AlphanumComparator())
                .forEach(p -> {
                    if (p.getFileName().toString().contains(""))
                    try {
//                        String[] s = p.getFileName().toString().split("\\.json", 2)[0].split("_");
                        int lines = Files.readAllLines(Paths.get(source + "/feature/activity")).size();
                        System.out.println(p.getFileName() + " " + lines);

                        Vector<Test.Item> v = new Gson().fromJson(new String(Files.readAllBytes(p)),
                                new TypeToken<Vector<Test.Item>>(){}.getType());
//                        findFirst("AccountSetupBasics.java", v);
                        //findFirst("calculateAvailableColors", v);
                        findFirst("FontSizes.java", v);
                        findFirst("FontSizeSettings.java", v);
                        findFirst("SliderPreference.java", v);
                        findFirst("MessageWebView.java", v);
                        System.out.println();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                });
//        File dir = new File(Main.source + "/test/cos");
//        for (File file:dir.listFiles()) {
//            if (file.getName().endsWith(".json") && file.getName().contains("")) {
//                try {
//                    Vector<Test.Item> tests = new Gson().fromJson(new String(Files.readAllBytes(file.toPath())),
//                            new TypeToken<Vector<Test.Item>>(){}.getType());
//                    tests.forEach(item -> {
//                        if (item.target.endsWith("SliderPreference.java"))
//                            System.out.println(file.getName() + " " + item.No);
//                    });
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
    }

    public void findFirst(String s, Vector<Test.Item> v) {
        for (Test.Item item: v)
            if (item.target.contains(s) || item.name.contains(s)) {
                System.out.println(item.No);
                break;
            }
    }

}
