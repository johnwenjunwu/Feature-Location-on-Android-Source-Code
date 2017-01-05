package cc.mallet.examples;

public class MissResult {
    String modelName;
    int missSum;
    public MissResult(String modelName, int missSum) {
        this.modelName = modelName;
        this.missSum = missSum;
    }
}
