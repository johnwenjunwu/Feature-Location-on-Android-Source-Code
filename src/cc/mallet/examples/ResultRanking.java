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
    public static void main(String[] args) throws IOException {
        Files.list(Paths.get(Main.source + "/test/cos")).sorted(new AlphanumComparator())
                .forEach(p -> {
                    Vector<Test.Item> tests = null;
                    try {
                        ((Vector<Test.Item>)new Gson().fromJson(new String(Files.readAllBytes(p)),
                                new TypeToken<Vector<Test.Item>>(){}.getType()))
                                .stream()
                                .filter(item -> item.target.endsWith("SliderPreference.java"))
                                .forEach(item -> System.out.println(p.getFileName() + " " + item.No));
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
}
