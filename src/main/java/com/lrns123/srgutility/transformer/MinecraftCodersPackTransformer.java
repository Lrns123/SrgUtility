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
package com.lrns123.srgutility.transformer;

import com.lrns123.srgutility.mcp.MinecraftCodersPackMapping;
import com.lrns123.srgutility.srg.SrgClass;
import com.lrns123.srgutility.srg.SrgClassMutator;
import com.lrns123.srgutility.srg.SrgField;
import com.lrns123.srgutility.srg.SrgFieldMutator;
import com.lrns123.srgutility.srg.SrgMethod;
import com.lrns123.srgutility.srg.SrgMethodMutator;
import com.lrns123.srgutility.srg.SrgTypeDescriptor;
import com.lrns123.srgutility.srg.SrgTypeDescriptor.Type;

/**
 * Uses MCP's csv files to transform mappings.
 */
public class MinecraftCodersPackTransformer implements MappingTransformer
{

	private final MinecraftCodersPackMapping mapping;
	
	public MinecraftCodersPackTransformer(MinecraftCodersPackMapping mapping)
	{
		this.mapping = mapping;
	}
	

	public SrgClass transform(SrgClass input)
	{
		SrgClassMutator mutator = null;
		
		if (mapping.getPackageMapping().containsKey(input.getClassName()))
		{
			mutator = input.getMutator();
			mutator.setPackageName(mapping.getPackageMapping().get(input.getClassName()));
		}
		
		return mutator != null ? mutator.get() : input;
	}

	public SrgField transform(SrgField input)
	{
		SrgFieldMutator mutator = null;
		
		if (input.getPackageName().equals("net/minecraft/src") && mapping.getPackageMapping().containsKey(input.getClassName()))
		{
			mutator = input.getMutator();
			mutator.setPackageName(mapping.getPackageMapping().get(input.getClassName()));
		}
		
		if (mapping.getFieldMapping().containsKey(input.getFieldName()))
		{
			if (mutator == null)
				mutator = input.getMutator();
			
			mutator.setFieldName(mapping.getFieldMapping().get(input.getFieldName()));
		}
		
		return mutator != null ? mutator.get() : input;
	}

	public SrgMethod transform(SrgMethod input)
	{
		SrgMethodMutator mutator = null;
		
		if (input.getPackageName().equals("net/minecraft/src") && mapping.getPackageMapping().containsKey(input.getClassName()))
		{
			mutator = input.getMutator();
			mutator.setPackageName(mapping.getPackageMapping().get(input.getClassName()));
		}
		
		if (mapping.getMethodMapping().containsKey(input.getMethodName()))
		{
			if (mutator == null)
				mutator = input.getMutator();
			
			mutator.setMethodName(mapping.getMethodMapping().get(input.getMethodName()));
		}
		
		int numArgs = input.getArgumentCount();
		for (int i = 0; i != numArgs; ++i)
		{
			SrgTypeDescriptor descriptor = input.getArgument(i);
			if (descriptor.getType() == Type.OBJECT)
			{
				SrgClass typeClass = descriptor.getClassType();
				if (typeClass.getPackageName().equals("net/minecraft/src") && mapping.getPackageMapping().containsKey(typeClass.getClassName()))
				{
					if (mutator == null)
						mutator = input.getMutator();
					
					mutator.setArgument(i, new SrgClass(mapping.getPackageMapping().get(typeClass.getClassName()), typeClass.getClassName()), descriptor.getArrayDepth());
				}
			}
		}
		
		if (input.getReturnType().getType() == Type.OBJECT)
		{
			SrgClass typeClass = input.getReturnType().getClassType();
			if (typeClass.getPackageName().equals("net/minecraft/src") && mapping.getPackageMapping().containsKey(typeClass.getClassName()))
			{
				if (mutator == null)
					mutator = input.getMutator();
				
				mutator.setReturnType(new SrgClass(mapping.getPackageMapping().get(typeClass.getClassName()), typeClass.getClassName()), input.getReturnType().getArrayDepth());
			}
		}
		
		return mutator != null ? mutator.get() : input;
	}

}
