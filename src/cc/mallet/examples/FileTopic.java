package cc.mallet.examples;

import cc.mallet.pipe.*;
import cc.mallet.pipe.iterator.CsvIterator;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.types.IDSorter;
import cc.mallet.types.InstanceList;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.regex.Pattern;

public class FileTopic implements Runnable{

    int numIterations;
    int numTopics;
    int mini;
    String name, dir;
    InstanceList instances;
    ParallelTopicModel model;
    //Integer[][] sim;


    public void generateInstances() {
        ArrayList<Pipe> pipeList = new ArrayList<Pipe>(Arrays.asList(
                new CharSequenceLowercase(),
                new StemPipe(),
                new CharSequence2TokenSequence(Pattern.compile("\\S+")),
//                new TokenSequenceRemoveStopwords(new File("stoplists/activity.txt"), "UTF-8", false, false, false),
                new TokenSequenceRemoveStopwords(new File("stoplists/en.txt"), "UTF-8", false, false, false),
                new TokenSequence2FeatureSequence()));

        // Pipes: lowercase, tokenize, remove stopwords, map to features
        instances = new InstanceList(new SerialPipes(pipeList));

        Reader fileReader = null;
        try {
            fileReader = new InputStreamReader(new FileInputStream(new File(dir + "/feature/wordsMoreThan" + mini)), "UTF-8");
        } catch (UnsupportedEncodingException | FileNotFoundException e) {
            e.printStackTrace();
        }
        instances.addThruPipe(new CsvIterator(fileReader, Pattern.compile("^(\\S*)[\\s,]*(\\S*)[\\s,]*(.*)$"),
                3, 1, 2)); // data, label, name fields
        new File(dir + "/instance").mkdirs();
        instances.save(new File(dir + "/instance/" + name));
        //sim = new Integer[instances.size()][];
    }

    FileTopic(String src, int topics, int train, int mini) {
        dir = src;
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
        new File(dir + "/model/model").mkdirs();
        model.setSaveSerializedModel(numIterations, dir + "/model/model/" + name);
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
        // Show mini 5 words in topics with proportions for the first document
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
            new File(dir + "/model/gson/").mkdirs();
            Files.write(Paths.get(dir + "/model/gson/" + name), builder.toString().getBytes());
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