package cc.mallet.examples;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Vector;

public class ResultRanking {
    public static void main(String[] args) {
        File dir = new File("/Users/wuwenjun/Documents/study/mallet-2.0.8/show password/test/cos");
        for (File file:dir.listFiles()) {
            if (file.getName().endsWith(".json") && file.getName().contains("67_")) {
                try {
                    Vector<Test.Item> tests = new Gson().fromJson(new String(Files.readAllBytes(file.toPath())),
                            new TypeToken<Vector<Test.Item>>(){}.getType());
                    tests.forEach(item -> {
                        if (item.target.endsWith("AccountSetupBasics.java"))
                            System.out.println(file.getName() + " " + item.No);
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
