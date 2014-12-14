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
import com.rapidminer.topicmodel.helper;

public class ProcessTopicInferencerFromFile extends Operator
{
	public final String
		UNIQUE_TOPIC_ALLOCATION_OUTPUT_PORT_NAME = "example set topic distribution per document",
		UNIQUE_EXAMPLESET_INPUT_PORT_NAME = "exampleset input - word vector with term occurences",
		INFERENCER_FILE_TOTAL_PATH = "inferencer file",
		NUMBER_ITERATIONS = "iterations",
		NUMBER_THINNING = "thinning",
		NUMBER_BURNIN = "burnin";
	
	private final OutputPort
		exampleSetOutput = getOutputPorts().createPort(UNIQUE_TOPIC_ALLOCATION_OUTPUT_PORT_NAME);
	
	private InputPortExtender
		exampleSetInputPorts = new InputPortExtender(UNIQUE_EXAMPLESET_INPUT_PORT_NAME, getInputPorts(), new MetaData(ExampleSet.class), false);

	
	public ProcessTopicInferencerFromFile(OperatorDescription description) 
	{
		super(description);
		
		exampleSetInputPorts.start();
		
		this.exampleSetOutput.deliverMD(new MetaData(ExampleSet.class));
	}
	
	
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

			
			InstanceList instances = helper.getInstanceList(exampleSetList);
			
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
			
			ExampleTable table = helper.createObjectExampleTable(attributes, rows);
			ExampleSet es = table.createExampleSet();
			
			exampleSetOutput.deliver(es);
		}
		catch(Exception _exception)
		{
			_exception.printStackTrace();
			throw new OperatorException("", _exception);
		}
	}
	
	
	@Override
	public List<ParameterType> getParameterTypes() 
	{
		List<ParameterType> ret = super.getParameterTypes();
		
		ret.add(new ParameterTypeFile(INFERENCER_FILE_TOTAL_PATH,"The file where the serialized TopicInferencer Object is stored.",null,false)); 
		ret.add(new ParameterTypeInt(NUMBER_ITERATIONS,"The number of iterations of Gibbs sampling.", 1, Integer.MAX_VALUE,1000, false));
		ret.add(new ParameterTypeInt(NUMBER_THINNING,"Thinning. What is that?", 0, Integer.MAX_VALUE, 1, true));
		ret.add(new ParameterTypeInt(NUMBER_BURNIN, "burnIn. What is that?", 0, Integer.MAX_VALUE, 5, true));	
		
		return ret;
	}
	
	
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
			helper.close(ois);
			helper.close(fis);
			
			throw _classNotFoundException;
		}
		catch(IOException _ioException)
		{
			helper.close(ois);
			helper.close(fis);
			
			throw _ioException;			
		}
			
		return ret;
	}
}