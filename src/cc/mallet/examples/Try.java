package cc.mallet.examples;
import cc.mallet.topics.ParallelTopicModel;

import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Try {

    public static void main( String args[] ) throws Exception {
//        int[][] a = {{1,2}, {3,4}};
//        new ObjectOutputStream(new FileOutputStream("a")).writeObject(a);
//        int[][] b = (int[][]) new ObjectInputStream(new FileInputStream("a")).readObject();
//        System.out.println(b.length);

        String s = "authMD5BigCat";
        System.out.println(s.replaceAll("(?<=\\p{Ll})(?=\\p{Lu})|(?=\\p{Lu}\\p{Ll})", " "));
        System.out.println(s);
//        ParallelTopicModel model = ParallelTopicModel.read(new File("/Users/wuwenjun/Downloads/mallet-2.0.8/model/40_800_2.800"));
//        System.out.println(model.getData().get(0).instance.getData());
//        for (File f:new File("data/model/gson").listFiles()) {
//            List<String> topics = Files.readAllLines(f.toPath());
//            System.out.print(f.getName());
//            for (String topic: topics) {
//                String[] words = topic.split("\\) ");
//                for (int i = 0; i < words.length; ++i) {
//                    if (words[i].contains("mark")) {
//                        System.out.print("\t" + i);
//                        break;
//                    }
//                }
//            }
//            System.out.println();
//        }
//        // String to be scanned to find the pattern.
//        String line = "This order was placed for QT3000! OK?";
//        //String pattern = "(.*)(\\d+)(.*)";
//
//        // Create a Pattern object
//        Pattern r = Pattern.compile("^(\\S*)[\\s,]*(\\S*)[\\s,]*(.*)$");
//
//        // Now create matcher object.
//        Matcher m = r.matcher(line);
//        if (m.find( )) {
//            System.out.println("Found value: " + m.group(3) );
//            System.out.println("Found value: " + m.group(1) );
//            System.out.println("Found value: " + m.group(2) );
//        }else {
//            System.out.println("NO MATCH");
//        }
    }
}
