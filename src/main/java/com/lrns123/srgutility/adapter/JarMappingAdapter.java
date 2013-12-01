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
package com.lrns123.srgutility.adapter;

import java.util.Map.Entry;

import com.lrns123.srgutility.srg.SrgClass;
import com.lrns123.srgutility.srg.SrgField;
import com.lrns123.srgutility.srg.SrgMapping;
import com.lrns123.srgutility.srg.SrgMethod;

import net.md_5.specialsource.JarMapping;

public final class JarMappingAdapter
{
	/**
	 * Converts an SrgMapping into SpecialSource's JarMapping.
	 * @param mapping
	 * @return
	 */
	public static JarMapping convertSrgMapping(SrgMapping mapping)
	{
		JarMapping outMapping = new JarMapping();
		
		for (Entry<SrgClass, SrgClass> entry : mapping.getClassMapping().entrySet())
		{
			outMapping.classes.put(entry.getKey().getQualifiedName(), entry.getValue().getQualifiedName());
		}
		
		for (Entry<SrgField, SrgField> entry : mapping.getFieldMapping().entrySet())
		{
			outMapping.fields.put(entry.getKey().getQualifiedName(), entry.getValue().getFieldName());
		}
		
		for (Entry<SrgMethod, SrgMethod> entry : mapping.getMethodMapping().entrySet())
		{
			outMapping.methods.put(entry.getKey().getQualifiedName(), entry.getValue().getMethodName());
		}
		
		return outMapping;
	}
}
