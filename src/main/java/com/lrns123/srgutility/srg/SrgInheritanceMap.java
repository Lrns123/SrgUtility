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

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.base.Joiner;
import com.lrns123.srgutility.transformer.MappingTransformer;

import net.md_5.specialsource.provider.InheritanceProvider;

public class SrgInheritanceMap
{
	private Map<SrgClass, List<SrgClass>> inheritanceMap = new HashMap<SrgClass, List<SrgClass>>();

	public SrgInheritanceMap()
	{
	}
	
	/**
	 * Use SpecialSource to generate the inheritance map, mimics SpecialSource's behavior
	 */
	public SrgInheritanceMap(InheritanceProvider provider, SrgMapping mapping)
	{
		for (SrgClass clazz : mapping.getClassMappings().keySet())
		{
			Collection<String> parents = provider.getParents(clazz.getQualifiedName());
			
			if (parents == null)
				continue;
			
			List<SrgClass> filteredList = new ArrayList<SrgClass>();
			
			for (String candidate : parents)
			{
				if (mapping.getClassMapping(candidate) != null)
				{
					filteredList.add(new SrgClass(candidate));
				}
			}
			
			if (!filteredList.isEmpty())
			{
				inheritanceMap.put(clazz, filteredList);
			}
		}
	}
	
	public void setParent(SrgClass clazz, List<SrgClass> parents)
	{
		inheritanceMap.put(clazz, parents);
	}
	
	public List<SrgClass> getParent(SrgClass clazz)
	{
		return inheritanceMap.get(clazz);
	}
	
	public Map<SrgClass, List<SrgClass>> getInheritanceMap()
	{
		return inheritanceMap;
	}
	
	@Override
	public SrgInheritanceMap clone()
	{
		SrgInheritanceMap cloned = new SrgInheritanceMap();
		for (Entry<SrgClass, List<SrgClass>> entry : inheritanceMap.entrySet())
		{
			List<SrgClass> parents = new ArrayList<SrgClass>();
			
			for (SrgClass clz : entry.getValue())
			{
				parents.add(clz.clone());
			}
			
			cloned.setParent(entry.getKey().clone(), parents);
		}
		
		return cloned;
	}
	
	public SrgInheritanceMap transform(MappingTransformer transformer)
	{
		SrgInheritanceMap temp = new SrgInheritanceMap();
		for (Entry<SrgClass, List<SrgClass>> entry : inheritanceMap.entrySet())
		{
			List<SrgClass> parents = new ArrayList<SrgClass>();
			
			for (SrgClass clz : entry.getValue())
			{
				parents.add(transformer.transform(clz));
			}
			
			temp.setParent(transformer.transform(entry.getKey()), parents);
		}
		
		this.inheritanceMap = temp.inheritanceMap;
		
		return this;
	}
	
	public void write(File outFile) throws IOException
	{
		List<String> lines = new ArrayList<String>();
		
		for (Entry<SrgClass, List<SrgClass>> entry : inheritanceMap.entrySet())
		{
			lines.add(entry.getKey() + " " + Joiner.on(' ').join(entry.getValue()));
		}

		Collections.sort(lines);
		
		PrintWriter writer = null;
		
		if (!outFile.getParentFile().exists())
		{
			outFile.getParentFile().mkdirs();
		}
		
		try
		{
			writer = new PrintWriter(outFile);
			
			for (String line : lines)
			{
				writer.println(line);
			}
		}
		finally
		{
			if (writer != null)
				writer.close();
		}
	}
	
}
