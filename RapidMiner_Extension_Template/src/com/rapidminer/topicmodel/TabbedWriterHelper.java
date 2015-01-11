package com.rapidminer.topicmodel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.junit.Test;

public class TabbedWriterHelper 
{
	/**
	 * @param _text a matrix from Strings, a row is a document and the i.th cell of this row is a word at the i.th position of the document. 
	 * @param _writer the class where _text will be written to.
	 * @throws IOException If an I/O error occurs.
	 */
	public static void write(String[][] _text, Writer _writer) throws IOException
	{
		BufferedWriter writer = new BufferedWriter(_writer);
		
		int wordLength = getMaxStringLength(_text);
		for(String[] row : _text)
		{
			String line = "";
			for(String cell : row)
			{
				while(cell.length() <= wordLength)
				{
					cell += " ";
				}
				line += cell;
			}
			writer.write(line);
			writer.newLine();
		}
		
		
		writer.close();
		_writer.close();
	}

	
	/**
	 * junit-test
	 * @throws IOException
	 */
	@Test
	public void testWriter() throws IOException
	{
		File file = new File("/home/maik/Schreibtisch/wordVector");
		FileWriter fileWriter = new FileWriter(file);
		
		write(new String[][]{new String[]{"","hhhhh", "dsfdsf","dfsf"}, new String[]{"Row1", "sfa", "dssd", "fsafdsaf"}, new String[]{"Row 2", "fasf","fsa", "fsa"}}, fileWriter);
	}
	
	/**
	 * 
	 * @param _input matrix of strings
	 * @return the length of the largest String in this matrix.
	 */
	public static int getMaxStringLength(String[][] _input)
	{
		int maxLength = 0;
		for(String[]row : _input)
		{	
			for(String word : row)
			{
				maxLength = word.length() > maxLength ? word.length() : maxLength;
			}
		}
		
		return maxLength;
	}
}