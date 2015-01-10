package com.rapidminer.topicmodel.mallet;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.Iterator;
import java.util.Locale;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

import cc.mallet.pipe.CharSequence2TokenSequence;
import cc.mallet.pipe.CharSequenceLowercase;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.TokenSequence2FeatureSequence;
import cc.mallet.pipe.iterator.StringArrayIterator;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureSequence;
import cc.mallet.types.IDSorter;
import cc.mallet.types.InstanceList;
import cc.mallet.types.LabelSequence;

/**
 * Deprecated since we do the post-processing in RapidMiner via other Operators and for post-processing use
 * {@link TopicTrainerPostProcessor} instead.
 * 
 * @author maik
 *
 */
@Deprecated
public class ParallelTopicTrainer 
{
	/**
	 * Small Hello-World example for mallet topic model processing.<br>
	 * Deprecated since the only aim is to get a feeling for the API.
	 * 
	 * @param _documents One document is a Single String.
	 * @param _numberTopics number of topics to train
	 * @param _numThreads number of threads to use for training.
	 * @param _iterations number of Gibb's iterations
	 * 
	 * @throws Exception
	 */
	@Deprecated
	public static void trainTopics(String[] _documents, int _numberTopics, int _numThreads, int _iterations) throws Exception
	{
		final double 
			alphaSum = 1.0,
			beta = 0.1;
	 
		ArrayList<Pipe> pipeList = new ArrayList<Pipe>();

		// Pipes: lowercase, map to features
		pipeList.add( new CharSequenceLowercase() );
		pipeList.add( new CharSequence2TokenSequence(Pattern.compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")) );
		pipeList.add( new TokenSequence2FeatureSequence() );
	
		InstanceList instances = new InstanceList (new SerialPipes(pipeList));

		instances.addThruPipe(new StringArrayIterator(_documents));
	 
		ParallelTopicModel model = new ParallelTopicModel(_numberTopics, alphaSum, beta);
		model.addInstances(instances);
		model.setNumThreads(_numThreads);
		model.setNumIterations(_iterations);
		model.estimate();
	 
		Alphabet dataAlphabet = instances.getDataAlphabet();
	
		Formatter out = new Formatter(new StringBuilder(), Locale.GERMAN);

		for(int i=0;i<model.getData().size();i++)
		{
			out = new Formatter(new StringBuilder(), Locale.GERMAN);
			out.format("\n################  DOCUMENT %d  ################\n", i);
			out.format("\nTopic assignment:\n\n");
			FeatureSequence tokens = (FeatureSequence) model.getData().get(i).instance.getData();
			LabelSequence topics = model.getData().get(i).topicSequence;
		
			for(int position = 0; position < tokens.getLength(); position++) 
			{
				out.format("%s-%d ", dataAlphabet.lookupObject(tokens.getIndexAtPosition(position)), topics.getIndexAtPosition(position));
			}
		
			// Estimate the topic distribution of the first instance, given the current Gibbs state.
			double[] topicDistribution = model.getTopicProbabilities(i);
    
			// Get an array of sorted sets of word ID/count pairs
			ArrayList<TreeSet<IDSorter>> topicSortedWords = model.getSortedWords();
			// Show top 5 words in topics with proportions for the first document
			out.format("\n\nTopic distribution\n\n");
		
			for (int topic = 0; topic < _numberTopics; topic++) 
			{
				Iterator<IDSorter> iterator = topicSortedWords.get(topic).iterator();
        
			
				out.format("\n%d\t%.3f\t", topic, topicDistribution[topic]);
				int rank = 0;
				while (iterator.hasNext() && rank < 5) 
				{
					IDSorter idCountPair = iterator.next();
					out.format("%s (%.0f) ", dataAlphabet.lookupObject(idCountPair.getID()), idCountPair.getWeight());
					rank++;
				}
			}
			System.out.println(out);
		}
	}
	
	
	

	/**
	 * Small testcase.
	 * Shows a small example for LDA-processing with mallet.
	 */
	 @Test
	 @Deprecated
	 public void testTrainTopics()
	 {
		 String[] documents = new String[]
			{
			 	"Hallo, dies ist mein erster testsatz. Ich denke er ist sehr toll geschrieben.",
			 	"Das ist mein zweiter Testsatz. Auch er ist toll."
			};
		 try
		 {
			 trainTopics(documents, 3, 1, 10);
		 }
		 catch(Exception _e)
		 {
			 _e.printStackTrace();
			 Assert.assertTrue(false);
		 }
	 }
}