package cc.mallet.examples;

import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.topics.TopicInferencer;
import cc.mallet.types.*;
import cc.mallet.util.Maths;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.Vector;

public class Test implements Runnable{
    ParallelTopicModel model;
    Instance testInstance;
    InstanceList instances;
    String name, source;
    List<Instance> features;

    Test(String src, String name, String test) throws Exception {
        source = src;
        this.name = name;
        model = ParallelTopicModel.read(new File(source + "/model/model/" + name + "." + name.split("_")[1]));
        instances = InstanceList.load(new File(source + "/instance/" + name));
        InstanceList testing = new InstanceList(instances.getPipe());
        testing.addThruPipe(new Instance(test, null, "test instance", null));
        testInstance = testing.get(0);
        //features = Files.readAllLines(Paths.get("feature/wordsMoreThan" + name.split("_")[2]));
        features = new ArrayList<>();
        model.getData().forEach(d -> {
            features.add(d.instance);
        });
        System.out.println(model.alphabet);
    }


    @Override
    public void run() {
        Vector<Item> cosList = new Vector<>(), klList = new Vector<>();

        //System.out.println(Integer.parseInt(name.split("_")[1]));
        TopicInferencer inferencer = model.getInferencer();
        double[] request = inferencer.getSampledDistribution(testInstance,
                Integer.parseInt(name.split("_")[1]), 1, 5);

        try {
            for (int i = 0; i < model.getData().size(); ++i) {
                cosList.add(new Item(i,
                        Result.cosine(model.getTopicProbabilities(i), request), model.getTopicProbabilities(i)));
                klList.add(new Item(i, Maths.klDivergence(model.getTopicProbabilities(i), request), model.getTopicProbabilities(i)));
            }
            cosList.sort((a,b)->a.sim>b.sim?-1:1);
            klList.sort((a,b)->a.sim<b.sim?-1:1);

            //System.out.println(cosList.get(0).pos);
            Files.write(Paths.get(source + "/test/cos/" + name), new Gson().toJson(cosList).getBytes());
            Files.write(Paths.get(source + "/test/kl/" + name), new Gson().toJson(klList).getBytes());

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    class Item {
        String target, name;
        String data;
        int pos;
        double sim;
        //double[] topic;
        Item(int i, double sim, double[] topic) {
            pos = i;
            target = features.get(i).getTarget().toString();
            name = features.get(i).getName().toString();
            data = features.get(i).getData().toString().replace("\n", "   ");
            this.sim = sim;
            //  this.topic = topic;
        }
    }



}


