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
    StringBuilder rank = new StringBuilder();
    ResultRanking(String source, String[] ans) {
        try {
            Files.list(Paths.get(source + "/test/cos")).sorted(new AlphanumComparator())
                    .forEach(p -> {
                        if (p.getFileName().toString().contains(""))
                        try {
                            rank.append(p).append('\n');
                            String[] feature = p.getFileName().toString().split("\\.json", 2)[0].split("_");

//                            int lines = Files.readAllLines(Paths.get(source + "/feature/wordsMoreThan" + feature[2])).size();
//                            System.out.println(p.getFileName() + " " + lines);

                            Vector<Test.Item> v = new Gson().fromJson(new String(Files.readAllBytes(p)),
                                    new TypeToken<Vector<Test.Item>>(){}.getType());
    //                        findFirst("AccountSetupBasics.java", v);
                            //findFirst("calculateAvailableColors", v);
    //                        findFirst("FontSizes.java", v);
                            for (String s: ans) {
//                                System.out.print(s + " ");
                                findFirst(s, v);
                            }
                            rank.append("\n");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    });
            Files.write(Paths.get(source + "/test/rank"), rank.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void findFirst(String s, Vector<Test.Item> v) {
        for (Test.Item item: v)
            if (item.target.contains(s) || item.name.contains(s)) {
                rank.append(s + ": " + item.No + "\n");
                break;
            }
    }

}
