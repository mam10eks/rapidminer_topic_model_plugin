package com.rapidminer.topicmodel.inferencer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import cc.mallet.topics.TopicInferencer;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;

import com.rapidminer.example.Attribute;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.example.table.AttributeFactory;
import com.rapidminer.example.table.ExampleTable;
import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.ports.InputPortExtender;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.operator.ports.metadata.MetaData;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeFile;
import com.rapidminer.parameter.ParameterTypeInt;
import com.rapidminer.tools.Ontology;
import com.rapidminer.topicmodel.Helper;


/**
 * An {@link com.rapidminer.operator.Operator Operator} that inferences with a given {@link cc.mallet.topics.TopicInferencer TopicInferencer}
 * the topic distribution for an arbitrarily amount of documents.
 * 
 * @author maik
 * @since December 2014
 */
public class ProcessTopicInferencerFromFile extends Operator
{
	/** Constant for input/output port or parameter.*/
	public final String
		UNIQUE_TOPIC_ALLOCATION_OUTPUT_PORT_NAME = "example set topic distribution per document",
		UNIQUE_EXAMPLESET_INPUT_PORT_NAME = "exampleset input - word vector with term occurences",
		INFERENCER_FILE_TOTAL_PATH = "inferencer file",
		NUMBER_ITERATIONS = "iterations",
		NUMBER_THINNING = "thinning",
		NUMBER_BURNIN = "burnin";

	/**
	 * Single {@link com.rapidminer.operator.ports.OutputPort OutputPort}, 
	 * which delivers the topic-distribution for the documents.
	 */
	private final OutputPort
		exampleSetOutput = getOutputPorts().createPort(UNIQUE_TOPIC_ALLOCATION_OUTPUT_PORT_NAME);
	
	/**
	 * Arbitrarily amount of {@link com.rapidminer.operator.ports.InputPort InputPort`s},
	 * which deliver documents in the form of term-occurence vectors to the Operator.
	 */
	private InputPortExtender
		exampleSetInputPorts = new InputPortExtender(UNIQUE_EXAMPLESET_INPUT_PORT_NAME, getInputPorts(), new MetaData(ExampleSet.class), false);

	
	/** 
	 * Create a instance of {@link com.rapidminer.topicmodel.inferencer.ProcessTopicInferencerFromFile ProcessTopicInferencerFromFile} 
	 * (which is a {@link com.rapidminer.operator.Operator Operator})  with the following ports:<br>
	 * arbitrarily input ports for example-sets(term occurence vectors)<br>
	 * one output port, with the topic distribution per document<br>
	 * 
	 * @param description will be delegated to the constructor of the parent {@link com.rapidminer.operator.Operator Operator}
	 *
	 * @see com.rapidminer.operator.Operator
	 */
	public ProcessTopicInferencerFromFile(OperatorDescription _description) 
	{
		super(_description);
		
		exampleSetInputPorts.start();
		
		this.exampleSetOutput.deliverMD(new MetaData(ExampleSet.class));
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * <br>
	 * This implementation will instantiate a {@link cc.mallet.topics.TopicInferencer TopicInferencer}
	 * by trying to de-serialize one from the {@link java.io.File File} specified by 
	 * {@link ProcessTopicInferencerFromFile#INFERENCER_FILE_TOTAL_PATH INFERENCER_FILE_TOTAL_PATH}.<br>
	 * <br>
	 * That {@link cc.mallet.topics.TopicInferencer TopicInferencer} will be used to calculate the topic distribution
	 * (represented by the inferencer) for each document.<br>
	 * Finally (iff no OperatorException is thrown, this distribution will delivered to the OutputPort.
	 * 
	 * @throws OperatorException if<br> 
	 * UserError is thrown during access of parameters specified by a user<br>
	 * ClassNotFoundException or IOException is thrown during de-serializing the {@link cc.mallet.topics.TopicInferencer TopicInferencer}.
	 */
	@Override
	public void doWork() throws OperatorException
	{
		final int
			iterations = getParameterAsInt(NUMBER_ITERATIONS),
			thinning = getParameterAsInt(NUMBER_THINNING),
			burnIn = getParameterAsInt(NUMBER_BURNIN);
		
		try
		{
			TopicInferencer inferencer = getTopicInferencer(getParameterAsFile(INFERENCER_FILE_TOTAL_PATH));
			
			List<ExampleSet> exampleSetList = exampleSetInputPorts.getData(ExampleSet.class, true);

			
			InstanceList instances = Helper.getInstanceList(exampleSetList);
			
			List<Attribute> attributes = new ArrayList<Attribute>();
			attributes.add(AttributeFactory.createAttribute("label", Ontology.NOMINAL));
			attributes.add(AttributeFactory.createAttribute("metadata_file", Ontology.NOMINAL));
			attributes.add(AttributeFactory.createAttribute("metadata_path", Ontology.NOMINAL));

			boolean isAttributeListComplete = false;
			
			List<List<Object>> rows = new ArrayList<List<Object>>();
			for(Instance inst : instances)
			{
				
				double[] testProbabilities = inferencer.getSampledDistribution(inst, iterations, thinning, burnIn);
				
				List<Object> row = new ArrayList<Object>();
				
				row.add(inst.getName());
				row.add(inst.getTarget());
				row.add(inst.getSource());

				
				for(int i=0;i<testProbabilities.length; i++)
				{
					row.add(testProbabilities[i]);
				}
				rows.add(row);
		        
		        if(!isAttributeListComplete)
		        {
		        	isAttributeListComplete = true;
		        	
		        	for(int i=0;i<testProbabilities.length; i++)
		        	{
		        		attributes.add(AttributeFactory.createAttribute("Topic "+ i, Ontology.NUMERICAL));
		        	}
		        }
			}
			
			ExampleTable table = Helper.createObjectExampleTable(attributes, rows);
			ExampleSet es = table.createExampleSet();
			
			exampleSetOutput.deliver(es);
		}
		catch(Exception _exception)
		{
			_exception.printStackTrace();
			throw new OperatorException("", _exception);
		}
	}
	
	
	/**
	 * The Operator can accept the following parameters:<br><br>
	 * 
	 * {@link ProcessTopicInferencerFromFile#INFERENCER_FILE_TOTAL_PATH INFERENCER_FILE_TOTAL_PATH}<br>
	 * {@link ProcessTopicInferencerFromFile#NUMBER_ITERATIONS NUMBER_ITERATIONS}<br>
	 * {@link ProcessTopicInferencerFromFile#NUMBER_THINNING NUMBER_THINNING}<br>
	 * {@link ProcessTopicInferencerFromFile#NUMBER_BURNIN NUMBER_BURNIN}<br>
	 * 
	 * <br><br>
	 * {@inheritDoc}
	 */
	@Override
	public List<ParameterType> getParameterTypes() 
	{
		List<ParameterType> ret = super.getParameterTypes();
		
		ret.add(new ParameterTypeFile(INFERENCER_FILE_TOTAL_PATH,"The file where the serialized TopicInferencer Object is stored.",null,false)); 
		ret.add(new ParameterTypeInt(NUMBER_ITERATIONS,"The number of iterations of Gibbs sampling.", 1, Integer.MAX_VALUE,1000, false));
		ret.add(new ParameterTypeInt(NUMBER_THINNING,"Thinning.", 0, Integer.MAX_VALUE, 1, true));
		ret.add(new ParameterTypeInt(NUMBER_BURNIN, "burnIn.", 0, Integer.MAX_VALUE, 5, true));	
		
		return ret;
	}
	
	
	/**
	 * Deserialize a {@link cc.mallet.topics.TopicInferencer TopicInferencer} from a {@link java.io.File File 0.
	 * 
	 * 
	 * @param _file place where the {@link cc.mallet.topics.TopicInferencer TopicInferencer} to de-serialize can be found.
	 * 
	 * @return Deserialized {@link cc.mallet.topics.TopicInferencer TopicInferencer-Object} from _file.
	 * 
	 * @throws ClassNotFoundException if a Class of a serialized object cannot be found.
	 * @throws IOException Any of the usual Input/Output related exceptions since we reading from _file.
	 */
	public static TopicInferencer getTopicInferencer(File _file) throws ClassNotFoundException, IOException
	{
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		TopicInferencer ret = null;
		
		try
		{
			fis = new FileInputStream(_file);
			ois = new ObjectInputStream(fis);
		
			ret = (TopicInferencer) ois.readObject();
			
			ois.close();
			fis.close();
		}
		catch(ClassNotFoundException _classNotFoundException)
		{
			Helper.close(ois);
			Helper.close(fis);
			
			throw _classNotFoundException;
		}
		catch(IOException _ioException)
		{
			Helper.close(ois);
			Helper.close(fis);
			
			throw _ioException;			
		}
			
		return ret;
	}
}