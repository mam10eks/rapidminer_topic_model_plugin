package com.rapidminer.topicmodel.inferencer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import cc.mallet.topics.TopicInferencer;

import com.rapidminer.operator.ResultObjectAdapter;


/**
 * 
 * @author maik
 *
 */
public class MalletTopicInferencerIOObject extends ResultObjectAdapter
{

	/**
	 * Default serial version id.<br>
	 * Since the Ohbject is serializable.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The mallet TopicInferencer that was overgiven at construction time of this element.
	 */
	private final TopicInferencer 
		inferencer;
	
	/**
	 * 
	 * @param _topicInferencer
	 */
	public MalletTopicInferencerIOObject(TopicInferencer _topicInferencer)
	{
		this.inferencer = _topicInferencer;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public TopicInferencer getTopicInferencer()
	{
		return this.inferencer;
	}

	
	@Override
	public String toResultString()
	{
		StringBuilder builder = new StringBuilder();
		
		builder.append("##################################################################################################\n");
		builder.append("#  serialisiertes "+ toString() +" \n");
		builder.append("##################################################################################################\n\n\n");
		
		try
		{
			builder.append(getSerializedStringOfInferencer());
		}
		catch (IOException _ioException)
		{
			builder.append("EXCEPTION During getSerializedStringOfInferencer(),\n");
			builder.append("message: "+ _ioException.getMessage());
			
			_ioException.printStackTrace();
		}
		
		return builder.toString();
	}
	
	
	public String getSerializedStringOfInferencer() throws IOException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(inferencer);
		
		oos.flush();
		baos.flush();
		
		return baos.toString();
	}
}
