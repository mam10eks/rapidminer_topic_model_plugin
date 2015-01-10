package com.rapidminer.topicmodel.inferencer;

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

/**
 * 
 * @author maik
 * @see com.rapidminer.operator.Operator
 */
public class SaveTopicInferencerToFile extends Operator
{
	/** input port name*/
	public static final String
		TOPIC_INFERENCER_INPUT_PORT = "inferencer inport";
	
	/** total path to the place where the {@link cc.mallet.topics.TopicInferencer TopicInferencer} will be stored.*/
	public static final String
		INFERENCER_FILE_TOTAL_PATH = "inferencer file";
	
	/** Single InputPort which expect a {@link com.rapidminer.topicmodel.inferencer.MalletTopicInferencerIOObject MalletTopicInferencerIOObject}. */
	private final InputPort
		topicInferencerInput = getInputPorts().createPort(TOPIC_INFERENCER_INPUT_PORT);
	
	
	/** 
	 * Create a instance of {@link com.rapidminer.topicmodel.inferencer.SaveTopicInferencerToFile SaveTopicInferencerToFile} 
	 * (which is a {@link com.rapidminer.operator.Operator Operator})  with the following ports:<br>
	 * one input ports which expect the {@link com.rapidminer.topicmodel.inferencer.MalletTopicInferencerIOObject Wrapped TopicIferencer}.<br>
	 * no output ports<br>
	 * 
	 * @param description will be delegated to the constructor of the parent {@link com.rapidminer.operator.Operator Operator}
	 *
	 * @see com.rapidminer.operator.Operator
	 */
	public SaveTopicInferencerToFile(OperatorDescription description) 
	{
		super(description);
		
		topicInferencerInput.receiveMD(new MetaData(MalletTopicInferencerIOObject.class));
	}

	
	/**
	 * {@inheritDoc}
	 * <br><br>
	 * This implementation serialize the given {@link cc.mallet.topics.TopicInferencer TopicInferencer} from the InputPort
	 * to a file specified by {@link SaveTopicInferencerToFile#INFERENCER_FILE_TOTAL_PATH INFERENCER_FILE_TOTAL_PATH}.
	 * 
	 */
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
	

	/**
	 * The Operator can accept the following parameters:<br><br>
	 * 
	 * {@link SaveTopicInferencerToFile#INFERENCER_FILE_TOTAL_PATH INFERENCER_FILE_TOTAL_PATH}<br>
	 * 
	 * <br><br>
	 * {@inheritDoc}
	 */
	@Override
	public List<ParameterType> getParameterTypes() 
	{
		List<ParameterType> ret = super.getParameterTypes();
		ret.add(new ParameterTypeFile(INFERENCER_FILE_TOTAL_PATH,"The file where the serialized TopicInferencer Object will be stored.",null,false)); 
		
		 
		return ret;
	}
}