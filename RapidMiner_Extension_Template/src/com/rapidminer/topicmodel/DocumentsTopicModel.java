package com.rapidminer.topicmodel;

import static com.rapidminer.test_stuff.Blaa.termFrequenceExampleToFeatures;
import static com.rapidminer.test_stuff.Blaa.getAlphabetFromExampleSet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;
import java.util.regex.Pattern;

import cc.mallet.pipe.CharSequence2TokenSequence;
import cc.mallet.pipe.CharSequenceLowercase;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.TokenSequence2FeatureSequence;
import cc.mallet.pipe.iterator.StringArrayIterator;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.topics.TopicInferencer;
import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureSequence;
import cc.mallet.types.IDSorter;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.LabelSequence;

import com.rapidminer.example.Attribute;
import com.rapidminer.example.AttributeTransformation;
import com.rapidminer.example.Attributes;
import com.rapidminer.example.Example;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.example.Statistics;
import com.rapidminer.example.table.AttributeFactory;
import com.rapidminer.example.table.DataRow;
import com.rapidminer.example.table.ExampleTable;
import com.rapidminer.example.table.NominalMapping;
import com.rapidminer.operator.Annotations;
import com.rapidminer.operator.IOObject;
import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.UserError;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.InputPortExtender;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.operator.ports.metadata.MetaData;
import com.rapidminer.operator.text.Document;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeDouble;
import com.rapidminer.parameter.ParameterTypeInt;
import com.rapidminer.parameter.UndefinedParameterError;
import com.rapidminer.tools.Ontology;
import com.rapidminer.topicmodel.inferencer.MalletTopicInferencerIOObject;

/**
 * An basic operator-template for topic models.
 * 
 * @author maik
 *
 */
public class DocumentsTopicModel extends Operator
{
	/**
	 * Unique name values for each Port.
	 */
	private static final String
		UNIQUE_DOCUMENT_INPUT_PORT_NAME = "document input",
		UNIQUE_TOPIC_OVERVIEW_OUTPUT_PORT_NAME = "example set topic overview",
		UNIQUE_TOPIC_ALLOCATION_OUTPUT_PORT_NAME = "example set topic distribution per document",
		UNIQUE_TOPIC_INSTANCE_ASSIGNMENT_PORT_NAME = "example set topic allocation for each word",
		UNIQUE_INFERENCER_OUTPUT_PORT_NAME = "inferencer output",
		UNIQUE_INFERENCER_INPUT_PORT_NAME = "inferencer input",
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
		documentInputPorts = new InputPortExtender (UNIQUE_DOCUMENT_INPUT_PORT_NAME, getInputPorts(), new MetaData(Document.class), false);
	
	private InputPortExtender
		exampleSetInputPorts = new InputPortExtender(UNIQUE_EXAMPLESET_INPUT_PORT_NAME, getInputPorts(), new MetaData(ExampleSet.class), false);
	
	private InputPort 
		inferencerInput = getInputPorts().createPort(UNIQUE_INFERENCER_INPUT_PORT_NAME);
		
	
	
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
	public DocumentsTopicModel(OperatorDescription description) 
	{
		super(description);
		
		this.documentInputPorts.start();
		this.exampleSetInputPorts.start();
		
		this.inferencerInput.receiveMD(new MetaData(MalletTopicInferencerIOObject.class));
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
	
	private TopicInferencer 
		inferencer = null;
	
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
		
		if(inferencer == null)
		{

			model = new ParallelTopicModel(numTopics, alphaSum, beta);
			model.addInstances(instances);
			model.setNumThreads(numThreads);
			model.setNumIterations(numIterations);

			try
			{
				model.estimate();
				System.out.println("estimated");
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
		else
		{
			List<Attribute> attributes = null;
			List<List<Number>> rows = new ArrayList<List<Number>>();
			for(Instance inst : instances)
			{
				
				double[] testProbabilities = inferencer.getSampledDistribution(inst, numIterations, 1, 5);
				
				List<Number> row = new ArrayList<Number>();
				for(int i=0;i<testProbabilities.length; i++)
				{
					row.add(testProbabilities[i]);
				}
				rows.add(row);
		        
		        if(attributes == null)
		        {
		        	attributes = new ArrayList<Attribute>();
		        	for(int i=0;i<testProbabilities.length; i++)
		        	{
		        		attributes.add(AttributeFactory.createAttribute("Topic "+ i, Ontology.NUMERICAL));
		        	}
		        }
			}
			
			ExampleTable table = helper.createExampleTable(attributes, rows);
			ExampleSet es = table.createExampleSet();
			
			topicAllocationOutput.deliver(es);
			inferencerOutput.deliver(new MalletTopicInferencerIOObject( inferencer));
		}
		
		
		

	}
	
	
//	/**
//	 * I 
//	 * 
//	 * 
//	 * 
//	 * @param _model The already trained topic model.<br>
//	 * I.e. the only thing to do is to collect the result informations and give them back.
//	 * 
//	 * @return interesting facts about the found topics
//	 */
//	private List<IOObject> doPostprocessingForModel(ParallelTopicModel _model, InstanceList _instances)
//	{
//		List<IOObject> ret = new ArrayList<IOObject>();
//		
//		
//
//		
//
//		
//		return ret;
//	}
	
	
	
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
	 
	 
	 private InstanceList createSampleInstanceList()
	 {
			ArrayList<Pipe> pipeList = new ArrayList<Pipe>();

		    pipeList.add( new CharSequenceLowercase() );
		    pipeList.add( new CharSequence2TokenSequence(Pattern.compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")) );
		    pipeList.add( new TokenSequence2FeatureSequence() );
			
			return new InstanceList (new SerialPipes(pipeList));
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
			List<String> allDocs = new ArrayList<String>();
			List<ExampleSet> allExampleSets = new ArrayList<ExampleSet>();
			
			inferencer = null;
			
			for(Document doc : documentInputPorts.getData(Document.class, true))
			{
				allDocs.add(doc.getText());
			}
			
			for(ExampleSet exampleSet : exampleSetInputPorts.getData(ExampleSet.class, true))
			{
				allExampleSets.add(exampleSet);
			}
			
			
			try
			{
				MalletTopicInferencerIOObject malletTopicInferencerIOObject = inferencerInput.getDataOrNull(MalletTopicInferencerIOObject.class);
				if(malletTopicInferencerIOObject != null)
				{
					inferencer = malletTopicInferencerIOObject.getTopicInferencer();
				}
			}
			catch(UserError _userError)
			{
				_userError.printStackTrace();
			}
			
			
			
			String[] documents = new String[allDocs.size()];
			documents = allDocs.toArray(documents);
			
			instances = createSampleInstanceList();
			
			if(documents.length > 0)
			{
				instances.addThruPipe(new StringArrayIterator(documents));
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
						
						System.out.println(data.toString());
						instances.add(new Instance(data, "target", "name", "source"));
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


class TopicTrainerPostProcessor
{
	private final ParallelTopicModel
		model;
	
	private final InstanceList
		instances;
	
	
	public TopicTrainerPostProcessor(ParallelTopicModel _model, InstanceList _instances)
	{
		this.model = _model;
		
		this.instances = _instances;
	}
	
	
	/**
	 * this method should return an ExampleSet as overview of all trained topics.<br>
	 * For each Topic there will be shown the _topWords most frequently words of the topic.
	 * 
	 * @return
	 */
	public ExampleSet getAllTopics(int _topWords)
	{
		List<Attribute> attributes = new ArrayList<Attribute>();
		List<List<Object>> rows = new ArrayList<List<Object>>();
		Object[][] topWords = model.getTopWords(_topWords);
		List<Object>[] row = new ArrayList[_topWords];
		
		for(int i = 0; i < _topWords; i++)
		{
			row[i] = new ArrayList<Object>();
		}
		
		for(int i = 0; i < topWords.length; i++)
		{			
			attributes.add(AttributeFactory.createAttribute("Topic " + i, Ontology.NOMINAL));
			
			for(int f = 0; f < topWords[i].length; f++)
			{
				row[f].add(topWords[i][f]);
			}
		}	
		
		for(int i = 0; i < _topWords; i++)
		{
			rows.add(row[i]);
		}
		
		ExampleTable table = helper.createObjectExampleTable(attributes, rows);
		ExampleSet es = table.createExampleSet();	
		
		return es;
	}
	
	/**
	 * this method should return an ExampleSet with the probability that a topic is part of a document.<br>
	 * For each Topic there will be shown the probability that Topic is in document.
	 * 
	 * @return 
	 */
	public ExampleSet getTopicDistributionForAllInstances()
	{
		List<Attribute> attributes = new ArrayList<Attribute>();
		List<List<Number>> rows = new ArrayList<List<Number>>();
		
		for(int topic = 0; topic < model.getNumTopics(); topic++)
		{
			attributes.add(AttributeFactory.createAttribute("Topic " + topic, Ontology.NUMERICAL));
		}
		
		for(int doc = 0; doc < model.getData().size(); doc++)
		{
			List<Number> row = new ArrayList<Number>();
			
			double[] topicDistribution = model.getTopicProbabilities(doc);
						
			for(int topic = 0; topic < model.numTopics; topic++) row.add(topicDistribution[topic]);
		
			rows.add(row);
		}
		
		System.out.println(rows);
		
		ExampleTable table = helper.createExampleTable(attributes, rows);
		ExampleSet es = table.createExampleSet();	
		
		return es;
	}
	
	
	//TODO convert console output to an exampleset
	public ExampleSet getTokenTopicAssignmentForAllInstances()
	{
		Formatter out = new Formatter(new StringBuilder(), Locale.GERMAN);
		Alphabet dataAlphabet = instances.getDataAlphabet();
		
		for(int i=0;i< model.getData().size();i++)
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
		}
		
		System.out.println(out);
		
		return null;
	}
	
	
	public IOObject getTrainedInferencer()
	{
		MalletTopicInferencerIOObject generatedInferencer = new MalletTopicInferencerIOObject(model.getInferencer());
		return generatedInferencer;
	}
}