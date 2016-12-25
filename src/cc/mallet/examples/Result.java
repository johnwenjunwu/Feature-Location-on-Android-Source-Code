package cc.mallet.examples;

public class Result{
    int topic, train, test, notFound, found;
    Result(int topic, int train, int test, int notFound, int found) {
        this.topic = topic;
        this.train = train;
        this.test = test;
        this.notFound = notFound;
        this.found = found;
    }


    @Override
    public String toString() {
        return "train/topic:" + (double)train/(double)topic + " topic:" + topic + " train:" + train +
                " test:" + test +
                " notFound:" + notFound + " found:" + found;
    }

}
