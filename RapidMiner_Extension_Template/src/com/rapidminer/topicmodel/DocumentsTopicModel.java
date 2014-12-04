package com.rapidminer.topicmodel;

import java.util.ArrayList;
import java.util.List;

import com.rapidminer.example.ExampleSet;
import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.ports.InputPortExtender;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.operator.ports.metadata.MetaData;
import com.rapidminer.operator.text.Document;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeInt;

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
		UNIQUE_INPUT_PORT_NAME = "document input",
		UNIQUE_OUTPUT_PORT_NAME = "example set output";
	
	
	public static final String
		NUMBER_THREADS_KEY = "number_of_threads",
		NUMBER_ITERATIONS_KEY = "number_of_iterations",
		NUMBER_TOPICS_KEY = "number_of_topics";
	
	/**
	 * Arbitrarily (greater equals) one amount of input ports.
	 */
	private InputPortExtender
		inputPorts = new InputPortExtender (UNIQUE_INPUT_PORT_NAME, getInputPorts(), new MetaData(Document.class), true);
	
	/**
	 * unique port.
	 */
	private OutputPort
		exampleSetOutput = getOutputPorts().createPort(UNIQUE_OUTPUT_PORT_NAME);

	
	/**
	 * 
	 * @param description
	 */
	public DocumentsTopicModel(OperatorDescription description) 
	{
		super(description);
		
		this.inputPorts.start();
		
		this.exampleSetOutput.deliverMD(new MetaData(ExampleSet.class));
	}
	
	
	/**
	 * This method will be executed during the processing of the Operator.<br>
	 * Take care to deliver some data via the OutputPort.
	 * 
	 */
	@Override
	public void doWork() throws OperatorException
	{
		final int 
			numberThreads = getParameterAsInt(NUMBER_THREADS_KEY),
			numTopics = getParameterAsInt(NUMBER_TOPICS_KEY),
			numIterations = getParameterAsInt(NUMBER_ITERATIONS_KEY);
		
		System.out.println("vorverarbeitung");
		
		List<String> allDocs = new ArrayList<String>();
		
		for(Document doc : inputPorts.getData(Document.class, true))
		{	
			allDocs.add(doc.getText());
		}
		
		
		for(String doc : allDocs)
		{
			System.out.println(doc);
		}
		
		
//		exampleSetOutput.deliver();
	}
	
	
	
	 @Override
	 public List<ParameterType> getParameterTypes() 
	 {
		 List<ParameterType> ret = super.getParameterTypes();
		 
		 ret.add(new ParameterTypeInt(NUMBER_THREADS_KEY, "The number of threads for parallel training.", 1, 4, 1, true));
		 ret.add(new ParameterTypeInt(NUMBER_TOPICS_KEY, "The number of topics to fit.", 1, Integer.MAX_VALUE,10, false));
		 ret.add(new ParameterTypeInt(NUMBER_ITERATIONS_KEY , "The number of iterations of Gibbs sampling.", 1, Integer.MAX_VALUE,1000, false));
		 
		 
		 
		 return ret;
	 }

}