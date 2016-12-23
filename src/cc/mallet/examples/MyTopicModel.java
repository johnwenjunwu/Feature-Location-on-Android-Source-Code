package cc.mallet.examples;

import cc.mallet.types.*;
import cc.mallet.pipe.*;
import cc.mallet.pipe.iterator.*;
import cc.mallet.topics.*;

import java.util.*;
import java.util.regex.*;
import java.io.*;

import static java.util.Arrays.sort;

public class MyTopicModel {

	private final int numIterations = 500;
	int numTopics = 100;
	private String modelPath = "model/" + numTopics + "topics." + numIterations;
	String instancePath = "instances";

	public void saveInstances() throws Exception {
		ArrayList<Pipe> pipeList = new ArrayList<Pipe>(Arrays.asList(
				new CharSequenceLowercase(), new CharSequence2TokenSequence(Pattern.compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")),
				new TokenSequenceRemoveStopwords(new File("stoplists/java.txt"), "UTF-8", false, false, false),
				new TokenSequence2FeatureSequence()));

		// Pipes: lowercase, tokenize, remove stopwords, map to features
		InstanceList instances = new InstanceList (new SerialPipes(pipeList));
		Reader fileReader = new InputStreamReader(new FileInputStream(new File("/Users/wuwenjun/IdeaProjects/CodeParse/classFeature")), "UTF-8");
		instances.addThruPipe(new CsvIterator (fileReader, Pattern.compile("^(\\S*)[\\s,]*(\\S*)[\\s,]*(.*)$"),
				3, 1, 2)); // data, label, name fields
		instances.save(new File(instancePath));
	}


	public static void main(String[] args) throws Exception {
		new MyTopicModel().My();
	}

	public void My() throws Exception {
		InstanceList instances = InstanceList.load(new File(instancePath));
		ParallelTopicModel model = ParallelTopicModel.read(new File(modelPath));

		InstanceList testing = new InstanceList(instances.getPipe());
		testing.addThruPipe(new Instance("load message",null, "test instance", null));
		//Instance testInstance = new Instance("save instance", null, "test instance", null);//model.getData().get(1).instance;
//		TopicInferencer inferencer = model.getInferencer();
//		double[] request = inferencer.getSampledDistribution(testInstance, 10, 1, 5);
//		Arrays.stream(request).forEach(d -> System.out.format("%.2f ", d));
		//System.out.println(Arrays.toString(request));
//
//		PrintModel(instances, model);
//		model.getData().forEach(e -> System.out.println(e.instance.getData()));
//		System.out.println(testInstance.getName() + " " + testInstance.getTarget() + " " + testInstance.getData());
		getClassTestResultTopicModel(model, testing.get(0));

//		GenerateModel();
//		Vector<ClassTestResultTopicModel> result = new Vector<>();
//		for(int i = 0; i < model.getData().size(); ++i)
//			result.add(getClassTestResultTopicModel(model, i));
//
//
//		try (FileWriter writer = new FileWriter("ClassTestResultTopicModel")) {
//			//writer.write(dir.getClassFeature().toString());
//			new Gson().toJson(result, writer);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}

	private static ClassTestResultTopicModel getClassTestResultTopicModel(ParallelTopicModel model, Instance testInstance) throws Exception {
		TopicInferencer inferencer = model.getInferencer();
		double[] request = inferencer.getSampledDistribution(testInstance, 10, 1, 5);

		Double[] distance = new Double[model.getData().size()];
		for(int i = 0; i < model.getData().size(); ++i) {
			double[] topicDistribution = model.getTopicProbabilities(i);
			double dis = 0, topic = 0, re = 0;
			for(int j = 0; j < model.getNumTopics(); ++j) {
				dis += topicDistribution[j] * request[j];
				topic += Math.pow(topicDistribution[j], 2);
				re += Math.pow(request[j], 2);
			}
			distance[i] = dis / Math.sqrt(topic * re);
		}

		ArrayIndexComparator comparator = new ArrayIndexComparator(distance);
		Integer[] indexes = comparator.createIndexArray();
		Arrays.sort(indexes, comparator);
		ClassTestResultTopicModel result = new ClassTestResultTopicModel(testInstance.getName().toString(), "");
		for(int i = 0; i < 10; ++i) {
			System.out.println("val:" + distance[indexes[i]] + " findClass:" + model.getData().get(indexes[i]).instance.getName().toString()
			+ " findPackage:" + model.getData().get(indexes[i]).instance.getTarget().toString());
			//System.out.println(model.getData().get(indexes[i]).instance.getData());
			result.findPackage.add(model.getData().get(indexes[i]).instance.getTarget().toString());
			result.findClass.add(model.getData().get(indexes[i]).instance.getName().toString());
		}
		return result;
	}


//	public double[] Test(InstanceList testing, ParallelTopicModel model) throws Exception {
//		// Create a new instance named "test instance" with empty target and source fields.
//
//
//		TopicInferencer inferencer = model.getInferencer();
//		return inferencer.getSampledDistribution(testing.get(0), 10, 1, 5);
//
//        //		Formatter out = new Formatter(new StringBuilder(), Locale.US);
////		for(double d: testProbabilities)
////			out.format("%.3f ", d);
////		System.out.println("testProbabilities:" + out);
//	}

	public void GenerateModel() throws Exception {
		// Begin by importing documents from text to feature sequences


		InstanceList instances = InstanceList.load(new File(instancePath));


		// Create a model with 100 topics, alpha_t = 0.01, beta_w = 0.01
		//  Note that the first parameter is passed as the sum over topics, while
		//  the second is the parameter for a single dimension of the Dirichlet prior.
		ParallelTopicModel model = new ParallelTopicModel(numTopics, 1.0, 0.01);
		model.setSaveSerializedModel(numIterations, "model/" + numTopics + "topics");
		model.addInstances(instances);

		// Use two parallel samplers, which each look at one half the corpus and combine
		//  statistics after every iteration.
		model.setNumThreads(2);

		// Run the model for 50 iterations and stop (this is for testing only,
		//  for real applications, use 1000 to 2000 iterations)
		model.setNumIterations(numIterations);
		model.estimate();
		PrintModel(instances, model);


	}

	private void PrintModel(InstanceList instances, ParallelTopicModel model) {
		// Show the words and topics in the first instance

		// The data alphabet maps word IDs to strings
		Alphabet dataAlphabet = instances.getDataAlphabet();

		FeatureSequence tokens = (FeatureSequence) model.getData().get(0).instance.getData();
		LabelSequence topics = model.getData().get(0).topicSequence;

		Formatter out = new Formatter(new StringBuilder(), Locale.US);
		for (int position = 0; position < tokens.getLength(); position++) {
			out.format("%s-%d ", dataAlphabet.lookupObject(tokens.getIndexAtPosition(position)), topics.getIndexAtPosition(position));
		}
		System.out.println(out);

		// Estimate the topic distribution of the first instance,
		//  given the current Gibbs state.
		double[] topicDistribution = model.getTopicProbabilities(0);

		// Get an array of sorted sets of word ID/count pairs
		ArrayList<TreeSet<IDSorter>> topicSortedWords = model.getSortedWords();

		// Show top 5 words in topics with proportions for the first document
		for (int topic = 0; topic < model.getNumTopics(); topic++) {
			Iterator<IDSorter> iterator = topicSortedWords.get(topic).iterator();

			out = new Formatter(new StringBuilder(), Locale.US);
			out.format("%d\t%.3f\t", topic, topicDistribution[topic]);
			int rank = 0;
			while (iterator.hasNext() && rank < 5) {
				IDSorter idCountPair = iterator.next();
				out.format("%s (%.0f) ", dataAlphabet.lookupObject(idCountPair.getID()), idCountPair.getWeight());
				rank++;
			}
			System.out.println(out);
		}

		// Create a new instance with high probability of topic 0
		StringBuilder topicZeroText = new StringBuilder();
		Iterator<IDSorter> iterator = topicSortedWords.get(0).iterator();

		int rank = 0;
		while (iterator.hasNext() && rank < 5) {
			IDSorter idCountPair = iterator.next();
			topicZeroText.append(dataAlphabet.lookupObject(idCountPair.getID()) + " ");
			rank++;
		}

		// Create a new instance named "test instance" with empty target and source fields.
		InstanceList testing = new InstanceList(instances.getPipe());
		testing.addThruPipe(new Instance(topicZeroText.toString(), null, "test instance", null));

		TopicInferencer inferencer = model.getInferencer();
		double[] testProbabilities = inferencer.getSampledDistribution(testing.get(0), 10, 1, 5);

		out = new Formatter(new StringBuilder(), Locale.US);
		for(double d: testProbabilities)
			out.format("%.3f ", d);
		System.out.println("testProbabilities:" + out);
	}

}