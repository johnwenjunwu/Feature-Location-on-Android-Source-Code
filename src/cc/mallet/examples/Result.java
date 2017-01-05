package cc.mallet.examples;

import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.util.Maths;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Result implements Runnable{
    ParallelTopicModel model;
    String name;
    int size, test;
    int[] kl, cos;
    Result(int topic, int train, int mini, int test) throws Exception {
        this.test = test;
        name = topic + "_" + train + "_" + mini;
        model = ParallelTopicModel.read(new File("model/" + name + "." + train));
        size = model.getData().size();
        kl = new int[size];
        cos = new int[size];
    }



    @Override
    public void run() {
        TestMyself();
        save();
    }

//    int found(int topList) {
//        int sum = 0;
//        for(int i = 0; i < instances.size(); ++i)
//            for(int t = 0; t < topList; ++t)
//                if(kl[i][t] == i) {
//                    sum++;
//                    break;
//                }
//        return sum;
//    }

    public void save() {
        try {
            //Files.write(Paths.get("kl/" + name), new Gson().toJson(kl).getBytes());
            Files.write(Paths.get("cos/" + name), new Gson().toJson(cos).getBytes());

            //new ObjectOutputStream(new FileOutputStream("cos/" + name)).writeObject(cos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void TestMyself() {
        for (int i = 0; i < model.getData().size(); ++i) {
            //similar(i);
            cosRank(i);
        }
    }

    void similar(int pos) {
        double[] request = model.getInferencer().getSampledDistribution(model.getData().get(pos).instance, test, 1, 5);
        Double[] distance = new Double[size];

        // KL divergence
        for (int i = 0; i < size; ++i)
            distance[i] = Maths.klDivergence(model.getTopicProbabilities(i), request);

        int no = 0;
        for (int i = 0; i < size; ++i)
            if (distance[i] < distance[pos])
                no++;
        kl[pos] = no;
    }

    void cosRank(int pos) {
        double[] request = model.getInferencer().getSampledDistribution(model.getData().get(pos).instance, test, 1, 5);
        Double[] distance = new Double[size];

        for (int i = 0; i < size; ++i) {
            double[] other = model.getTopicProbabilities(i);
            distance[i] = cosine(other, request);
        }

        int no = 0;
        for (int i = 0; i < size; ++i)
            if (distance[i] > distance[pos])
                no++;
        cos[pos] = no;
    }

    static double cosine(double[] other, double[] request) {
        double up = 0, left = 0, right = 0;
        for (int j = 0; j < other.length; ++j) {
            up += other[j] * request[j];
            left += Math.pow(other[j], 2);
            right += Math.pow(request[j], 2);
        }
        return up / Math.sqrt(left * right);
    }
}
