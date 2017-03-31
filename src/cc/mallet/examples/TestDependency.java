package cc.mallet.examples;

import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.topics.TopicInferencer;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.util.Maths;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class TestDependency implements Runnable {
    ParallelTopicModel model;
    Instance testInstance;
    InstanceList instances;
    String name, source;
    int iter, top;
    List<Instance> features;

    TestDependency(String src, String name, String test, int iter, int top) {
        try {
            this.iter = iter;
            source = src;
            this.name = name;
            this.top = top;
            try {
                new File(source + "/model/model").mkdirs();
                model = ParallelTopicModel.read(new File(source + "/model/model/" + name + "." + name.split("_")[1]));
            } catch (Exception e) {
                e.printStackTrace();
            }
            instances = InstanceList.load(new File(source + "/instance/" + name));
            InstanceList testing = new InstanceList(instances.getPipe());

            Vector<Test.Item> v = new Gson().fromJson(new String(Files.readAllBytes(Paths.get(test))),
                    new TypeToken<Vector<Test.Item>>() {
                    }.getType());

            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < top; ++i)
                builder.append(v.get(i).name).append(" ");

            testing.addThruPipe(new Instance(builder.toString(), null, "test instance", null));
            testInstance = testing.get(0);
            //features = Files.readAllLines(Paths.get("feature/wordsMoreThan" + name.split("_")[2]));
            features = new ArrayList<>();
            model.getData().forEach(d -> {
                features.add(d.instance);
            });
            //System.out.println(model.alphabet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void questionResult() {
        TopicInferencer inferencer = model.getInferencer();
        double[] request = inferencer.getSampledDistribution(testInstance,
                Integer.parseInt(name.split("_")[1]), 1, 5);
        StringBuilder question = new StringBuilder();
        for (int t = 0; t < request.length; ++t) {
            if (request[t] > 0.01)
                question.append(t + " " + new DecimalFormat("##.##").format(request[t]) + "\n");
        }
        System.out.println("\n\n" + name + '\n' + question);
    }

    @Override
    public void run() {
        Vector<Item> cosList = new Vector<>(), klList = new Vector<>();

        //System.out.println(Integer.parseInt(name.split("_")[1]));
        TopicInferencer inferencer = model.getInferencer();
        double[] request = inferencer.getSampledDistribution(testInstance, iter
                , 1, 5);


        StringBuilder question = new StringBuilder();
        for (int t = 0; t < request.length; ++t) {
            if (request[t] > 0.01)
                question.append(t + 1 + " " + new DecimalFormat("##.##").format(request[t]) + "\n");
        }


        try {
            String result = "\n\n" + name + '\n' + testInstance.getData().toString() + '\n' + question;
            new File(source + "/test").mkdirs();
            if (new File(source + "/test/result").exists())
                Files.write(Paths.get(source + "/test/result"), result.getBytes(), StandardOpenOption.APPEND);
            else
                Files.write(Paths.get(source + "/test/result"), result.getBytes());

            for (int i = 0; i < model.getData().size(); ++i) {
                cosList.add(new Item(i,
                        Result.cosine(model.getTopicProbabilities(i), request), model.getTopicProbabilities(i)));
                klList.add(new Item(i, Maths.klDivergence(model.getTopicProbabilities(i), request), model.getTopicProbabilities(i)));
            }
            cosList.sort((a, b) -> a.sim > b.sim ? -1 : 1);
            for (Item item : cosList)
                item.No = cosList.indexOf(item);

            klList.sort((a, b) -> a.sim < b.sim ? -1 : 1);

            //System.out.println(cosList.get(0).pos);
            new File(source + "/test/cos/").mkdirs();
            Files.write(Paths.get(source + "/test/cos/" + name + "_" + iter + "_" + top + ".json"), new Gson().toJson(cosList).getBytes());
            //Files.write(Paths.get(dir + "/test/kl/" + name + ".json"), new Gson().toJson(klList).getBytes());

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    class Item {
        String target, name;
        int No;
        String data;
        //int pos;
        double sim;
        Vector<String> topics;

        Item(int i, double sim, double[] topic) {
            topics = new Vector<>();
            for (int t = 0; t < topic.length; ++t) {
                if (topic[t] > 0.01)
                    topics.add(t + " " + new DecimalFormat("##.##").format(topic[t]));
            }
            //pos = i;
            target = features.get(i).getTarget().toString();
            name = features.get(i).getName().toString();
            data = features.get(i).getData().toString().replace("\n", "   ");
            this.sim = sim;
            //  this.topic = topic;
        }
    }


}


