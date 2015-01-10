package com.rapidminer.topicmodel.inferencer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import cc.mallet.topics.TopicInferencer;

import com.rapidminer.operator.ResultObjectAdapter;


/**
 * Wrapping-Helper to transport a {@link cc.mallet.topics.TopicInferencer TopicInferencer} between RapidMiner interfaces.<br>
 * E.g. from one Operator to another one.
 * 
 * @author maik
 *
 * @see com.rapidminer.operator.ResultObjectAdapter
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
	 * @param _topicInferencer will be wrapped.
	 */
	public MalletTopicInferencerIOObject(TopicInferencer _topicInferencer)
	{
		this.inferencer = _topicInferencer;
	}
	
	
	/**
	 * 
	 * @return the wrapped object.
	 */
	public TopicInferencer getTopicInferencer()
	{
		return this.inferencer;
	}

	/**
	 * This implementation returns the wrapped Object, with some meta informations like a header,
	 * in serialized form.<br>
	 * 
	 * {@inheritDoc}
	 */
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
	
	
	/**
	 * @return The wrapped Object as serialized String.
	 * @throws IOException Any exception thrown by ByteArrayOutputStream.
	 */
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
