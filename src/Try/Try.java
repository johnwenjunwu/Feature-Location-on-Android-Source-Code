package Try;

import cc.mallet.pipe.*;
import cc.mallet.pipe.iterator.CsvIterator;
import cc.mallet.types.InstanceList;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Try {
    public static void generateInstances() {
        ArrayList<Pipe> pipeList = new ArrayList<Pipe>(Arrays.asList(
                new CharSequenceLowercase(), new CharSequence2TokenSequence(Pattern.compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")),
                new TokenSequenceRemoveStopwords(new File("stoplists/java.txt"), "UTF-8", false, false, false),
                new TokenSequence2FeatureSequence()));

        // Pipes: lowercase, tokenize, remove stopwords, map to features
        InstanceList instances = new InstanceList(new SerialPipes(pipeList));

        Reader fileReader = null;
        try {
            fileReader = new InputStreamReader(new FileInputStream(new File("/Users/wuwenjun/Downloads/mallet-2.0.8/data/try/wordsMoreThan2")), "UTF-8");
        } catch (UnsupportedEncodingException | FileNotFoundException e) {
            e.printStackTrace();
        }
        instances.addThruPipe(new CsvIterator(fileReader, Pattern.compile("^(\\S*)[\\s,]*(\\S*)[\\s,]*(.*)$"),
                3, 1, 2)); // data, label, name fields
        //sim = new Integer[instances.size()][];
        System.out.println(instances.get(0).getData());
    }
    public static void main(String[] args) throws FileNotFoundException {
//        CompilationUnit compilationUnit = JavaParser.parse(new File("/Users/wuwenjun/Downloads/k-9-5.112/k9mail/src/main/java/com/fsck/k9/activity/Accounts.java"));
//        Pattern pattern = Pattern.compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}");
//        Matcher matcher = pattern.matcher("jkj jk");
//        String[] s = "jk ,m".split(pattern.pattern());
//        System.out.println(String.join(":", s));

        generateInstances();
//        compilationUnit.getComments().forEach(c -> {
//            if(c.getContent().contains("param"))
//                System.out.println(c.getContent());
//        });
    }
}
