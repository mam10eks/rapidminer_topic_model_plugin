package com.rapidminer.topicmodel.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.metadata.MetaData;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeFile;
import com.rapidminer.topicmodel.inferencer.MalletTopicInferencerIOObject;

public class SaveTopicInferencerToFile extends Operator
{
	public final String
		TOPIC_INFERENCER_INPUT_PORT = "inferencer inport",
		INFERENCER_FILE_TOTAL_PATH = "inferencer file";
	
	private final InputPort
		topicInferencerInput = getInputPorts().createPort(TOPIC_INFERENCER_INPUT_PORT);
	
	public SaveTopicInferencerToFile(OperatorDescription description) 
	{
		super(description);
		
		topicInferencerInput.receiveMD(new MetaData(MalletTopicInferencerIOObject.class));
	}

	
	@Override
	public void doWork() throws OperatorException
	{
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		
		try
		{
			MalletTopicInferencerIOObject iooInferencer = topicInferencerInput.getData(MalletTopicInferencerIOObject.class);
			File file = getParameterAsFile(INFERENCER_FILE_TOTAL_PATH);
			fos = new FileOutputStream(file);
			oos = new ObjectOutputStream(fos);
			
			oos.writeObject(iooInferencer.getTopicInferencer());
			
			fos.flush();
			oos.flush();
		}
		catch(Exception _exception) {}
		finally
		{
			if(fos != null)
			{
				try
				{
					fos.close();
				}
				catch(IOException _e) {}
			}
			if(oos != null)
			{
				try
				{
					oos.close();
				}
				catch(IOException _e) {}
			}
		}
	}
	
	
	@Override
	public List<ParameterType> getParameterTypes() 
	{
		List<ParameterType> ret = super.getParameterTypes();
		ret.add(new ParameterTypeFile(INFERENCER_FILE_TOTAL_PATH,"The file where the serialized TopicInferencer Object will be stored.",null,false)); 
		
		 
		return ret;
	}
}