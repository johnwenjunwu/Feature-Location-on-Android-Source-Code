package cc.mallet.examples;

import cc.mallet.pipe.*;
import cc.mallet.pipe.iterator.CsvIterator;
import cc.mallet.types.InstanceList;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

public class Preprocess {
    public static void main(String[] args) {
        String[] cmd = {"a", "2"};
        System.out.println(String.join("_", cmd));
        //saveInstances();
    }

//    public static void saveInstances() {
//        ArrayList<Pipe> pipeList = new ArrayList<Pipe>(Arrays.asList(
//                new CharSequenceLowercase(), new CharSequence2TokenSequence(Pattern.compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")),
//                new TokenSequenceRemoveStopwords(new File("stoplists/java.txt"), "UTF-8", false, false, false),
//                new TokenSequence2FeatureSequence()));
//
//        // Pipes: lowercase, tokenize, remove stopwords, map to features
//        instances = new InstanceList(new SerialPipes(pipeList));
//        Reader fileReader = null;
//        try {
//            fileReader = new InputStreamReader(new FileInputStream(new File(featurePath)), "UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        instances.addThruPipe(new CsvIterator(fileReader, Pattern.compile("^(\\S*)[\\s,]*(\\S*)[\\s,]*(.*)$"),
//                3, 1, 2)); // data, label, name fields
//        //instances.save(new File(instancePath));
//        sim = new Integer[instances.size()][];
//    }
}
