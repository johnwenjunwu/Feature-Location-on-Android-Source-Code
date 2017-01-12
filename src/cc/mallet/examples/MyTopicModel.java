package cc.mallet.examples;

import cc.mallet.types.*;
import cc.mallet.pipe.*;
import cc.mallet.pipe.iterator.*;
import cc.mallet.topics.*;
import cc.mallet.util.Maths;
import de.erichseifert.gral.data.DataTable;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.*;
import java.io.*;

import static java.util.Arrays.sort;

public class MyTopicModel implements Runnable{

    int numIterations;
    int numTopics;
    int mini;
    String name, source;
    InstanceList instances;
    ParallelTopicModel model;
    //Integer[][] sim;


    public void generateInstances() {
        ArrayList<Pipe> pipeList = new ArrayList<Pipe>(Arrays.asList(
                new CharSequenceLowercase(), new StemPipe(),
                new CharSequence2TokenSequence(Pattern.compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")),
                new TokenSequenceRemoveStopwords(new File("stoplists/java.txt"), "UTF-8", false, false, false),
                new TokenSequenceRemoveStopwords(new File("stoplists/en.txt"), "UTF-8", false, false, false),
                new TokenSequence2FeatureSequence()));

        // Pipes: lowercase, tokenize, remove stopwords, map to features
        instances = new InstanceList(new SerialPipes(pipeList));

        Reader fileReader = null;
        try {
            fileReader = new InputStreamReader(new FileInputStream(new File(source + "/feature/wordsMoreThan" + mini)), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        instances.addThruPipe(new CsvIterator(fileReader, Pattern.compile("^(\\S*)[\\s,]*(\\S*)[\\s,]*(.*)$"),
                3, 1, 2)); // data, label, name fields
        instances.save(new File(source + "/instance/" + name));
        //sim = new Integer[instances.size()][];
    }

    MyTopicModel(String src, int topics, int train, int mini) {
        source = src;
        name = topics + "_" + train + "_" + mini;
        numTopics = topics;
        numIterations = train;
        this.mini = mini;
    }


    public void generateModel() {
        // Begin by importing documents from text to feature sequences

        // Create a model with 100 topics, alpha_t = 0.01, beta_w = 0.01
        //  Note that the first parameter is passed as the sum over topics, while
        //  the second is the parameter for a single dimension of the Dirichlet prior.
        model = new ParallelTopicModel(numTopics, 50, 0.01);
        model.setSaveSerializedModel(numIterations, source + "/model/model/" + name);
        model.addInstances(instances);
        // Use two parallel samplers, which each look at one half the corpus and combine
        //  statistics after every iteration.
        model.setNumThreads(1);
        // Run the model for 50 iterations and stop (this is for testing only,
        //  for real applications, use 1000 to 2000 iterations)
        model.setNumIterations(numIterations);
//        model.setTopicDisplay(numIterations, 5);
//        model.printLogLikelihood = true;

        //model.printLogLikelihood = true;
        //model.printState(new File("state.gz"));
        try {
            model.estimate();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //PrintModel(instances, model);

    }


    private void PrintModel() {
        ArrayList<TreeSet<IDSorter>> topicSortedWords = model.getSortedWords();

        StringBuilder builder = new StringBuilder();
        // Show top 5 words in topics with proportions for the first document
        for (int topic = 0; topic < model.getNumTopics(); topic++) {
            Iterator<IDSorter> iterator = topicSortedWords.get(topic).iterator();

            int rank = 0;
            while (iterator.hasNext() && rank < 10) {
                IDSorter idCountPair = iterator.next();
                builder.append(String.format("%s (%.0f) ", model.alphabet.lookupObject(idCountPair.getID()), idCountPair.getWeight()));
                rank++;
            }
            builder.append('\n');
        }
        try {
            Files.write(Paths.get(source + "/model/gson/" + name), builder.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        generateInstances();
        generateModel();
        PrintModel();
    }
}