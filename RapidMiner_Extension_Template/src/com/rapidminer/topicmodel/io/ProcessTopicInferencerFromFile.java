package com.rapidminer.topicmodel.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;

import cc.mallet.topics.TopicInferencer;

import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.operator.ports.metadata.MetaData;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeFile;
import com.rapidminer.topicmodel.inferencer.MalletTopicInferencerIOObject;

public class ProcessTopicInferencerFromFile extends Operator
{
	public final String
		TOPIC_INFERENCER_OUTPUT_PORT = "inferencer output",
		INFERENCER_FILE_TOTAL_PATH = "inferencer file";
	
	private final OutputPort
		topicInferencerOutput = getOutputPorts().createPort(TOPIC_INFERENCER_OUTPUT_PORT);
	
	public ProcessTopicInferencerFromFile(OperatorDescription description) 
	{
		super(description);
		
		this.topicInferencerOutput.deliverMD(new MetaData(MalletTopicInferencerIOObject.class));
	}
	
	
	@Override
	public void doWork() throws OperatorException
	{
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		
		try
		{
			File file = getParameterAsFile(INFERENCER_FILE_TOTAL_PATH);
			
			fis = new FileInputStream(file);
			ois = new ObjectInputStream(fis);
			
			TopicInferencer inferencer = (TopicInferencer) ois.readObject();
			
			MalletTopicInferencerIOObject ret = new MalletTopicInferencerIOObject(inferencer);
			
			topicInferencerOutput.deliver(ret);
		}
		catch(Exception _exception)
		{
			throw new OperatorException("", _exception);
		}
		finally
		{
			if(ois != null)
			{
				try 
				{
					ois.close();
				} 
				catch (IOException _e) {}
			}
			if(fis != null)
			{
				try
				{
					fis.close();
				}
				catch (IOException _e) {}
			}
		}
	}
	
	
	@Override
	public List<ParameterType> getParameterTypes() 
	{
		List<ParameterType> ret = super.getParameterTypes();
		ret.add(new ParameterTypeFile(INFERENCER_FILE_TOTAL_PATH,"The file where the serialized TopicInferencer Object is stored.",null,false)); 
		
		 
		return ret;
	}
}