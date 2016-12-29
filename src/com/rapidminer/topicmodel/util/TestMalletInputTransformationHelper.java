package com.rapidminer.topicmodel.util;

import java.io.StringWriter;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureSequence;
import cc.mallet.types.FeatureVector;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;

import com.rapidminer.example.Attribute;
import com.rapidminer.example.Example;
import com.rapidminer.example.ExampleSet;

import com.rapidminer.topicmodel.util.MalletInputTransformationHelper;
import com.rapidminer.topicmodel.util.ExampleSetFormatHelper;

/**
 * This is a junit test class to determine the correct functionality of:<br>
 * -{@link com.rapidminer.topicmodel.util.MalletInputTransformationHelper MalletInputTransformationHelper}<br>
 * -{@link com.rapidminer.topicmodel.util.ExampleSetFormatHelper ExampleSetFormatHelper}<br>
 * 
 * @author maik
 *
 */
public class TestMalletInputTransformationHelper 
{
	/** Alphabet of the documents from es. */
	private static Alphabet 
		alph = null;
	
	/** Some dummy documents. Only for test purpose. */
	private static ExampleSet 
		es = null;
	
	/**
	 * initialise es and alph.
	 */
	@BeforeClass
	public static void setUp()
	{
		es = MalletInputTransformationHelper.getSmallTestExampleSet();
		alph = MalletInputTransformationHelper.getAlphabetFromExampleSet(es);
	}
	
	
	/**
	 * determine whether ExampleSetFormatHelper.writeExampleSetFormatted would write correct to stdout.
	 */
	@Test
	public void testCreateExampleSetAndPrint()
	{
		StringWriter writer = new StringWriter();
		ExampleSetFormatHelper.writeExampleSetFormatted(es, writer);
		
		String ret = writer.toString();
		
		Assert.assertTrue(ret.length() > 0);
	}
	
	
	/**
	 * Determine whether the es will be transformed and added correctly to 
	 * the feature vector instancelist.
	 */
	@Test
	public void testExampleVector()
	{
		
		InstanceList featureVectorInstances = new InstanceList(alph, null);
		
		for(Example example : es)
		{
			double[] values = new double[es.getAttributes().size()];
			int row = 0;
			for(Attribute attr : es.getAttributes())
			{
				values[row++] = example.getNumericalValue(attr);
			}
			FeatureVector exampleVector = new FeatureVector(featureVectorInstances.getAlphabet(), values);
			
			
			String val = exampleVector.toString();
			
			Assert.assertTrue(val.length() > 0);
			
			featureVectorInstances.add(new Instance(exampleVector, "target", "name", "source"));
		}
	}
	
	
	/**
	 * transform each document from es to a featuresequence.
	 * Add this featureSequence to a InstanceList.
	 */
	@Test
	public void testFeatureSequence()
	{
		InstanceList featureSequenceInstances = new InstanceList(alph, null);
		for(Example example : es)
		{
			int[] features = MalletInputTransformationHelper.termFrequenceExampleToFeatures(example, featureSequenceInstances.getAlphabet());
			FeatureSequence data = new FeatureSequence(featureSequenceInstances.getAlphabet(), features);
			
			String val = data.toString();
			
			Assert.assertTrue(val.length() > 0);
			
			featureSequenceInstances.add(new Instance(data, "target", "name", "source"));
		}
	}
	
	/**
	 * An test, this time a bit more complex.
	 *  
	 */
	@Test
	public void testInteraction()
	{
		StringWriter writer = new StringWriter();
		ExampleSetFormatHelper.writeExampleSetFormatted(es, writer);
		System.out.println(writer.toString());
		
		InstanceList featureVectorInstances = new InstanceList(alph, null);
		
		for(Example example : es)
		{
			double[] values = new double[es.getAttributes().size()];
			int row = 0;
			for(Attribute attr : es.getAttributes())
			{
				values[row++] = example.getNumericalValue(attr);
			}
			FeatureVector exampleVector = new FeatureVector(featureVectorInstances.getAlphabet(), values);
			
			System.out.println(exampleVector.toString());
			featureVectorInstances.add(new Instance(exampleVector, "target", "name", "source"));
		}
		
		
		InstanceList featureSequenceInstances = new InstanceList(alph, null);
		for(Example example : es)
		{
			int[] features = MalletInputTransformationHelper.termFrequenceExampleToFeatures(example, featureSequenceInstances.getAlphabet());
			FeatureSequence data = new FeatureSequence(featureSequenceInstances.getAlphabet(), features);
			
			System.out.println(data.toString());
			featureSequenceInstances.add(new Instance(data, "target", "name", "source"));
		}
		
		Assert.assertTrue(true);
	}
}