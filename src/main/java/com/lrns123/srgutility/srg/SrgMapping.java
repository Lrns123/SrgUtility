/**
 * Copyright (c) 2013, Lourens "Lrns123" Elzinga
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the author nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.lrns123.srgutility.srg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import lombok.Cleanup;
import lombok.Getter;

import com.lrns123.srgutility.transformer.IdentityTransformer;
import com.lrns123.srgutility.transformer.MappingTransformer;

/**
 * Represents an srg mapping.
 * 
 */
public class SrgMapping
{
	/**
	 * Mapping tables. Contains input -> output mapping.
	 */
	@Getter private Map<SrgClass, SrgClass> classMapping;
	@Getter private Map<SrgField, SrgField> fieldMapping;
	@Getter private Map<SrgMethod, SrgMethod> methodMapping;

	
	public SrgMapping()
	{
		this.classMapping = new HashMap<SrgClass, SrgClass>();
		this.fieldMapping = new HashMap<SrgField, SrgField>();
		this.methodMapping = new HashMap<SrgMethod, SrgMethod>();
	}
	
	SrgMapping(int classMappings, int fieldMappings, int methodMappings)
	{
		this.classMapping = new HashMap<SrgClass, SrgClass>(classMappings);
		this.fieldMapping = new HashMap<SrgField, SrgField>(fieldMappings);
		this.methodMapping = new HashMap<SrgMethod, SrgMethod>(methodMappings);
	}
	
	/**
	 * Adds a class mapping to the mapping tables.
	 * 
	 * @param input
	 * @param output
	 */
	public void addClassMapping(SrgClass input, SrgClass output)
	{
		if (classMapping.put(input, output) != null)
			System.out.println("Duplicate class entry for " + input.getQualifiedName() + "!");
	}

	/**
	 * Adds a field mapping to the mapping tables.
	 * 
	 * @param input
	 * @param output
	 */
	public void addFieldMapping(SrgField input, SrgField output)
	{
		if (fieldMapping.put(input, output) != null)
			System.out.println("Duplicate field entry for " + input.getQualifiedName() + "!");
		
	}

	/**
	 * Adds a method mapping to the mapping tables.
	 * 
	 * @param input
	 * @param output
	 */
	public void addMethodMapping(SrgMethod input, SrgMethod output)
	{
		if (methodMapping.put(input, output) != null)
			System.out.println("Duplicate method entry for " + input.getQualifiedName() + "!");
		
	}

	/**
	 * Makes a deep copy of this mapping.
	 * 
	 * @return A new SrgMapping instance with identical mappings.
	 */
	@Override
	public SrgMapping clone()
	{
		SrgMapping temp = new SrgMapping(classMapping.size(), fieldMapping.size(), methodMapping.size());

		for (Entry<SrgClass, SrgClass> entry : classMapping.entrySet())
		{
			temp.addClassMapping(entry.getKey(), entry.getValue());
		}

		for (Entry<SrgField, SrgField> entry : fieldMapping.entrySet())
		{
			temp.addFieldMapping(entry.getKey(), entry.getValue());
		}

		for (Entry<SrgMethod, SrgMethod> entry : methodMapping.entrySet())
		{
			temp.addMethodMapping(entry.getKey(), entry.getValue());
		}

		return temp;
	}

	/**
	 * Reverses the mappings.
	 * 
	 * @return The current (now reversed) instance of SrgMapping.
	 */
	public SrgMapping reverse()
	{
		SrgMapping temp = new SrgMapping(classMapping.size(), fieldMapping.size(), methodMapping.size());

		for (Entry<SrgClass, SrgClass> entry : classMapping.entrySet())
		{
			temp.addClassMapping(entry.getValue(), entry.getKey());
		}

		for (Entry<SrgField, SrgField> entry : fieldMapping.entrySet())
		{
			temp.addFieldMapping(entry.getValue(), entry.getKey());
		}

		for (Entry<SrgMethod, SrgMethod> entry : methodMapping.entrySet())
		{
			temp.addMethodMapping(entry.getValue(), entry.getKey());
		}

		/**
		 * Swap data
		 */
		this.classMapping = temp.classMapping;
		this.fieldMapping = temp.fieldMapping;
		this.methodMapping = temp.methodMapping;

		return this;
	}

	/**
	 * Transforms the mappings through the provided mapping transformers.
	 * 
	 * @param inputTransformer
	 *            The transformer to use on the input mappings.
	 * @param outputTransformer
	 *            The transformer to use on the output mappings.
	 * @return The current (now transformed) instance of SrgMapping.
	 */
	public SrgMapping transform(MappingTransformer inputTransformer, MappingTransformer outputTransformer)
	{

		if (inputTransformer == null)
		{
			inputTransformer = new IdentityTransformer();
		}

		if (outputTransformer == null)
		{
			outputTransformer = new IdentityTransformer();
		}

		SrgMapping temp = new SrgMapping(classMapping.size(), fieldMapping.size(), methodMapping.size());

		for (Entry<SrgClass, SrgClass> entry : classMapping.entrySet())
		{
			SrgClass input = inputTransformer.transform(entry.getKey());
			SrgClass output = outputTransformer.transform(entry.getValue());
			temp.addClassMapping(input, output);
		}

		for (Entry<SrgField, SrgField> entry : fieldMapping.entrySet())
		{
			SrgField input = inputTransformer.transform(entry.getKey());
			SrgField output = outputTransformer.transform(entry.getValue());
			temp.addFieldMapping(input, output);
		}

		for (Entry<SrgMethod, SrgMethod> entry : methodMapping.entrySet())
		{
			SrgMethod input = inputTransformer.transform(entry.getKey());
			SrgMethod output = outputTransformer.transform(entry.getValue());
			temp.addMethodMapping(input, output);
		}

		/**
		 * Swap data
		 */
		this.classMapping = temp.classMapping;
		this.fieldMapping = temp.fieldMapping;
		this.methodMapping = temp.methodMapping;

		return this;
	}	
	
	/**
	 * Turns this mapping into an identity map (input == output) based on the current input mappings.
	 * @return 
	 */
	public SrgMapping identity()
	{
		SrgMapping temp = new SrgMapping(classMapping.size(), fieldMapping.size(), methodMapping.size());

		for (Entry<SrgClass, SrgClass> entry : classMapping.entrySet())
		{
			temp.addClassMapping(entry.getKey(), entry.getKey());
		}

		for (Entry<SrgField, SrgField> entry : fieldMapping.entrySet())
		{
			temp.addFieldMapping(entry.getKey(), entry.getKey());
		}

		for (Entry<SrgMethod, SrgMethod> entry : methodMapping.entrySet())
		{
			temp.addMethodMapping(entry.getKey(), entry.getKey());
		}

		/**
		 * Swap data
		 */
		this.classMapping = temp.classMapping;
		this.fieldMapping = temp.fieldMapping;
		this.methodMapping = temp.methodMapping;
		
		return this;
	}
	
	
	/**
	 * Filters this mapping, removing all entries that (in terms of input) are not present in the filter SrgMapping
	 * @param filter The mapping to use as filter
	 * @return 
	 */
	public SrgMapping filter(SrgMapping filter)
	{
		SrgMapping temp = new SrgMapping(classMapping.size(), fieldMapping.size(), methodMapping.size());

		for (Entry<SrgClass, SrgClass> entry : classMapping.entrySet())
		{
			if (filter.getClassMapping(entry.getKey()) != null)
			{
				temp.addClassMapping(entry.getKey(), entry.getValue());
			}
		}

		for (Entry<SrgField, SrgField> entry : fieldMapping.entrySet())
		{
			if (filter.getFieldMapping(entry.getKey()) != null)
			{
				temp.addFieldMapping(entry.getKey(), entry.getValue());
			}
		}

		for (Entry<SrgMethod, SrgMethod> entry : methodMapping.entrySet())
		{
			if (filter.getMethodMapping(entry.getKey()) != null)
			{
				temp.addMethodMapping(entry.getKey(), entry.getValue());
			}
		}

		/**
		 * Swap data
		 */
		this.classMapping = temp.classMapping;
		this.fieldMapping = temp.fieldMapping;
		this.methodMapping = temp.methodMapping;

		return this;
	}
	
	// Optimized split function for faster parsing.
	private static int fastSplit(String input, String[] array, char delimiter, int skip)
	{
		int off = 0;
        int next = 0;
        int idx = 0;
  
        while ((next = input.indexOf(delimiter, off)) != -1)
        {
        	if (idx == array.length)
        		return idx;
        	
        	if (skip > 0)        	
        		--skip;        	
        	else        	
        		array[idx++] = input.substring(off, next);
        	
        	off = next + 1;
        }
        
        if (idx != array.length)
        {
        	array[idx++] = input.substring(off);
        }
        
        return idx;
	}
	
	public void loadMapping(File srgFile) throws IOException, IllegalArgumentException
	{
		@Cleanup BufferedReader reader = new BufferedReader(new FileReader(srgFile));
		String tokens[] = new String[4];
		String line;
		
		while ((line = reader.readLine()) != null)
		{
			int numArgs = fastSplit(line, tokens, ' ', 1);
			
			if (numArgs == 0)
				continue;
							
			if (line.startsWith("CL:"))
			{
				if (numArgs < 2)
					System.out.println("Invalid CL entry found in srg: " + line);
				
				SrgClass input = new SrgClass(tokens[0]);
				SrgClass output = new SrgClass(tokens[1]);
				
				addClassMapping(input, output);				
			}
			else if (line.startsWith("FD:"))
			{
				if (numArgs < 2)
					System.out.println("Invalid FD entry found in srg: " + line);
				
				SrgField input = new SrgField(tokens[0]);
				SrgField output = new SrgField(tokens[1]);
				
				addFieldMapping(input, output);			
			}
			else if (line.startsWith("MD:"))
			{
				if (numArgs < 4)
					System.out.println("Invalid MD entry found in srg: " + line);
				
				SrgMethod input = new SrgMethod(tokens[0], tokens[1]);
				SrgMethod output = new SrgMethod(tokens[2], tokens[3]);
				
				addMethodMapping(input, output);		
			}
		}		
	}
	
	public void writeSorted(File outFile) throws IOException
	{
		List<String> lines = new ArrayList<String>();
		
		for (Entry<SrgClass, SrgClass> entry : classMapping.entrySet())
		{
			lines.add("CL: " + entry.getKey() + " " + entry.getValue());
		}

		for (Entry<SrgField, SrgField> entry : fieldMapping.entrySet())
		{
			lines.add("FD: " + entry.getKey() + " " + entry.getValue());
		}

		for (Entry<SrgMethod, SrgMethod> entry : methodMapping.entrySet())
		{
			lines.add("MD: " + entry.getKey() + " " + entry.getValue());
		}
		
		Collections.sort(lines);
	
		if (!outFile.getParentFile().exists())
		{
			outFile.getParentFile().mkdirs();
		}
		
		@Cleanup PrintWriter writer = new PrintWriter(outFile);
			
		writer.println("# Mapping generated by Srg Utility (c) 2013 Lourens \"Lrns123\" Elzinga");
		writer.println("# Generated on " + new Date());
		
		for (String line : lines)
		{
			writer.println(line);
		}

	}
	
	public void write(File outFile) throws IOException
	{
		if (!outFile.getParentFile().exists())
		{
			outFile.getParentFile().mkdirs();
		}
		
		@Cleanup PrintWriter writer = new PrintWriter(outFile);
			
		writer.println("# Mapping generated by Srg Utility (c) 2013 Lourens \"Lrns123\" Elzinga");
		writer.println("# Generated on " + new Date());
		
		for (Entry<SrgClass, SrgClass> entry : classMapping.entrySet())
		{
			writer.print("CL: ");
			writer.print(entry.getKey());
			writer.print(' ');
			writer.print(entry.getValue());
			writer.println();
		}

		for (Entry<SrgField, SrgField> entry : fieldMapping.entrySet())
		{
			writer.print("FD: ");
			writer.print(entry.getKey());
			writer.print(' ');
			writer.print(entry.getValue());
			writer.println();
		}

		for (Entry<SrgMethod, SrgMethod> entry : methodMapping.entrySet())
		{
			writer.print("MD: ");
			writer.print(entry.getKey());
			writer.print(' ');
			writer.print(entry.getValue());
			writer.println();
		}

	}
	
	public SrgClass getClassMapping(SrgClass input)
	{
		return classMapping.get(input);
	}
	
	public SrgClass getClassMapping(String inputSignature)
	{
		return classMapping.get(new SrgClass(inputSignature));
	}
	
	public SrgField getFieldMapping(SrgField input)
	{
		return fieldMapping.get(input);
	}
	
	public SrgField getFieldMapping(String inputSignature)
	{
		return fieldMapping.get(new SrgField(inputSignature));
	}
		
	public SrgMethod getMethodMapping(SrgMethod input)
	{
		return methodMapping.get(input);
	}
	
	public SrgMethod getMethodMapping(String inputSignature)
	{
		return methodMapping.get(new SrgMethod(inputSignature));
	}
}
