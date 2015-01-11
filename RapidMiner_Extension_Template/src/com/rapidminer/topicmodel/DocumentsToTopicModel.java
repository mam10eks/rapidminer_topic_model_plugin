package com.rapidminer.topicmodel;

import static com.rapidminer.topicmodel.util.ExampleSetHelper.getInstanceName;
import static com.rapidminer.topicmodel.util.ExampleSetHelper.getInstanceSource;
import static com.rapidminer.topicmodel.util.ExampleSetHelper.getInstanceTarget;
import static com.rapidminer.topicmodel.util.MalletInputTransformationHelper.getAlphabetFromExampleSet;
import static com.rapidminer.topicmodel.util.MalletInputTransformationHelper.termFrequenceExampleToFeatures;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureSequence;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;

import com.rapidminer.example.Example;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.UserError;
import com.rapidminer.operator.ports.InputPortExtender;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.operator.ports.metadata.MetaData;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeDouble;
import com.rapidminer.parameter.ParameterTypeInt;
import com.rapidminer.parameter.UndefinedParameterError;
import com.rapidminer.topicmodel.inferencer.MalletTopicInferencerIOObject;
import com.rapidminer.topicmodel.mallet.TopicTrainerPostProcessor;

/**
 * An basic operator-template for topic models.
 * 
 * @author maik
 *
 */
public class DocumentsToTopicModel extends Operator
{
	/**
	 * Unique name values for each Port.
	 */
	private static final String
		UNIQUE_TOPIC_OVERVIEW_OUTPUT_PORT_NAME = "example set topic overview",
		UNIQUE_TOPIC_ALLOCATION_OUTPUT_PORT_NAME = "example set topic distribution per document",
		UNIQUE_TOPIC_INSTANCE_ASSIGNMENT_PORT_NAME = "example set topic allocation for each word",
		UNIQUE_INFERENCER_OUTPUT_PORT_NAME = "inferencer output",
		UNIQUE_EXAMPLESET_INPUT_PORT_NAME = "exampleset input";
	
	
	public static final String
		NUMBER_THREADS_KEY = "number_of_threads",
		NUMBER_ITERATIONS_KEY = "number_of_iterations",
		NUMBER_TOPICS_KEY = "number_of_topics",
		NUMBER_WORDS_PER_TOPIC = "number_of_words_per_topic",
		ALPHA_SUM = "alpha_sum",
		BETA = "beta";
	
	/**
	 * Arbitrarily (greater equals) one amount of input ports.
	 */
	private InputPortExtender
		exampleSetInputPorts = new InputPortExtender(UNIQUE_EXAMPLESET_INPUT_PORT_NAME, getInputPorts(), new MetaData(ExampleSet.class), false);
		
	
	
	/**
	 * unique outputports.
	 */
	private OutputPort
		topicOverviewOutput = getOutputPorts().createPort(UNIQUE_TOPIC_OVERVIEW_OUTPUT_PORT_NAME),
		inferencerOutput = getOutputPorts().createPort(UNIQUE_INFERENCER_OUTPUT_PORT_NAME),
		topicAllocationOutput = getOutputPorts().createPort(UNIQUE_TOPIC_ALLOCATION_OUTPUT_PORT_NAME),
		topicAllocationForEachWordOutput = getOutputPorts().createPort(UNIQUE_TOPIC_INSTANCE_ASSIGNMENT_PORT_NAME );

	
	/**
	 * 
	 * @param description
	 */
	public DocumentsToTopicModel(OperatorDescription description) 
	{
		super(description);
		
		this.exampleSetInputPorts.start();
		
		this.topicOverviewOutput.deliverMD(new MetaData(ExampleSet.class));
		this.topicAllocationOutput.deliverMD(new MetaData(ExampleSet.class));
		this.topicAllocationForEachWordOutput.deliverMD(new MetaData(ExampleSet.class));
		this.inferencerOutput.deliverMD(new MetaData(MalletTopicInferencerIOObject.class));
	}
	
	private int
		numTopics,
		numThreads,
		numIterations,
		numberWordsPerTopic;
	
	private double
		alphaSum,
		beta;
	
	private ParallelTopicModel 
		model;
	
	private InstanceList 
		instances;
	
	/**
	 * This method will be executed during the processing of the Operator.<br>
	 * Take care to deliver some data via the OutputPort.
	 * 
	 */
	@Override
	public void doWork() throws OperatorException
	{
		getAndsetParameters();
		
		getAndSetInput();
		
		
		model = new ParallelTopicModel(numTopics, alphaSum, beta);
		model.addInstances(instances);
		model.setNumThreads(numThreads);
		model.setNumIterations(numIterations);

		try
		{
			model.estimate();
		}
		catch(IOException _ioException)
		{
			_ioException.printStackTrace();
			throw new OperatorException("exception while estimating model!", _ioException);
		}

			
		//delivery of interesting data that where generated during doWork of this instance.
		TopicTrainerPostProcessor postProcessor = new TopicTrainerPostProcessor(model, instances);
		
		inferencerOutput.deliver(postProcessor.getTrainedInferencer());
		try
		{
			topicOverviewOutput.deliver(postProcessor.getAllTopics(numberWordsPerTopic));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		topicAllocationOutput.deliver(postProcessor.getTopicDistributionForAllInstances());
		topicAllocationForEachWordOutput.deliver(postProcessor.getTokenTopicAssignmentForAllInstances());
	}
	
	
	 @Override
	 public List<ParameterType> getParameterTypes() 
	 {
		 List<ParameterType> ret = super.getParameterTypes();
		 
		 ret.add(new ParameterTypeInt(NUMBER_THREADS_KEY, "The number of threads for parallel training.", 1, 4, 1, true));
		 ret.add(new ParameterTypeInt(NUMBER_TOPICS_KEY, "The number of topics to fit.", 1, Integer.MAX_VALUE,10, false));
		 ret.add(new ParameterTypeInt(NUMBER_ITERATIONS_KEY , "The number of iterations of Gibbs sampling.", 1, Integer.MAX_VALUE,1000, false));
		 ret.add(new ParameterTypeDouble(ALPHA_SUM, "Alpha parameter: smoothing over topic distribution.", 0.0, Double.MAX_VALUE, 50.0, true));
		 ret.add(new ParameterTypeDouble(BETA, "Beta parameter: smoothing over unigram distribution.", 0.0, Double.MAX_VALUE, 0.1, true));
		 ret.add(new ParameterTypeInt(NUMBER_WORDS_PER_TOPIC, "The number of words to show for eacht topic(descending).", 2,Integer.MAX_VALUE,5, false));
		 
		 return ret;
	 }

	 
	 private void getAndsetParameters() throws UndefinedParameterError
	 {
			numThreads = getParameterAsInt(NUMBER_THREADS_KEY);
			numTopics = getParameterAsInt(NUMBER_TOPICS_KEY);
			numIterations = getParameterAsInt(NUMBER_ITERATIONS_KEY);
			alphaSum = getParameterAsDouble(ALPHA_SUM);
			beta = getParameterAsDouble(BETA);
			numberWordsPerTopic = getParameterAsInt(NUMBER_WORDS_PER_TOPIC);
	 }
	 
	 private void getAndSetInput() throws UserError
	 {
			List<ExampleSet> allExampleSets = new ArrayList<ExampleSet>();
			
			for(ExampleSet exampleSet : exampleSetInputPorts.getData(ExampleSet.class, true))
			{
				allExampleSets.add(exampleSet);
			}

			if(allExampleSets.size() > 0)
			{
				for(ExampleSet exampleSet : allExampleSets)
				{
					Alphabet alph = getAlphabetFromExampleSet(exampleSet);
					instances = new InstanceList(alph, null);
					for(Example example : exampleSet)
					{
						int[] features = termFrequenceExampleToFeatures(example, instances.getAlphabet());
						FeatureSequence data = new FeatureSequence(instances.getAlphabet(), features);
						
						instances.add(new Instance(data, getInstanceTarget(example), getInstanceName(example), getInstanceSource(example)));
					}
					
					if(allExampleSets.size() > 1)
					{
						System.out.println("TODO implement several ExampleSets");
					}
					
					break;
				}
			}
	 }
}