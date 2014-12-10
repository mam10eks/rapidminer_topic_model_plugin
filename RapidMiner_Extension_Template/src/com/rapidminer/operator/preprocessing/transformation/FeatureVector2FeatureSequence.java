package com.rapidminer.operator.preprocessing.transformation;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import cc.mallet.pipe.Pipe;
import cc.mallet.types.FeatureSequence;
import cc.mallet.types.FeatureVector;
import cc.mallet.types.Instance;

public class FeatureVector2FeatureSequence extends Pipe implements Serializable
{
	boolean binary;

	public FeatureVector2FeatureSequence (boolean binary)
	{
		this.binary = binary;
	}

	public FeatureVector2FeatureSequence ()
	{
		this (false);
	}
	
	
	public Instance pipe (Instance _carrier)
	{
		FeatureVector fv = (FeatureVector) _carrier.getData();
		
		FeatureSequence fs = new FeatureSequence(fv.getAlphabet());
		_carrier.setData(fs);
		return _carrier;
	}

	// Serialization 
	
	private static final long serialVersionUID = 1;
	private static final int CURRENT_SERIAL_VERSION = 1;
	
	private void writeObject (ObjectOutputStream _out) throws IOException 
	{
		_out.writeInt (CURRENT_SERIAL_VERSION);
		_out.writeBoolean (binary);
	}
	
	private void readObject (ObjectInputStream _in) throws IOException, ClassNotFoundException 
	{
		int version = _in.readInt ();
		if (version > 0)
		{
			binary = _in.readBoolean();
		}
	}
}