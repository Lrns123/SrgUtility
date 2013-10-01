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
package com.lrns123.srgutility.mcp;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import lombok.Data;
import au.com.bytecode.opencsv.CSVReader;

@Data
public class MinecraftCodersPackMapping
{
	/**
	 * Field mappings (field_xxxx -> decorated name)
	 */
	private final Map<String, String> fieldMapping = new HashMap<String, String>();
	
	/**
	 * Method mappings (func_xxxx -> decorated name)
	 */
	private final Map<String, String> methodMapping = new HashMap<String, String>();
	
	/**
	 * Package mappings (class -> package)
	 */
	private final Map<String, String> packageMapping = new HashMap<String, String>();

	public MinecraftCodersPackMapping(File fieldsFile, File methodsFile, File packagesFile) throws IOException
	{
		if (fieldsFile != null && fieldsFile.exists())
		{
			readCSV(fieldMapping, fieldsFile);
		}
		
		if (methodsFile != null && methodsFile.exists())
		{
			readCSV(methodMapping, methodsFile);
		}
		
		if (packagesFile != null && packagesFile.exists())
		{
			readCSV(packageMapping, packagesFile);
		}
	}

	private void readCSV(Map<String, String> map, File file) throws IOException
	{
		FileReader fileReader = null;
		CSVReader csvReader = null;
		try
		{
			fileReader = new FileReader(file);
			csvReader = new CSVReader(fileReader);
			
			// Skip header
			csvReader.readNext();

			String[] line;
			while ((line = csvReader.readNext()) != null)
			{
				if (line.length == 0)
				{
					continue;
				}
				if (line.length >= 2)
				{
					map.put(line[0], line[1]);
				}
			}
		}
		finally
		{
			if (csvReader != null)
			{
				csvReader.close();
			}
			if (fileReader != null)
			{
				fileReader.close();
			}
		}
	}
}
