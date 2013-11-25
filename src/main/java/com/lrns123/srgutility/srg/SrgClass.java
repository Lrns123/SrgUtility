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

import com.lrns123.srgutility.util.ParseUtil;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Represents a class name for srg mappings.
 */
@Data
@AllArgsConstructor
public class SrgClass
{
	private String packageName;
	private String className;
	
	/**
	 * Instantiates a new class name representation from an fully qualified name.
	 * @param qualifiedName Fully qualified name of the class (e.g. 'package/of/the/Class')
	 */
	public SrgClass(String qualifiedName)
	{
		String[] parts = ParseUtil.splitFQN(qualifiedName);
		
		className = parts[1];
		packageName = parts[0];
	}
	
	/**
	 * Returns the fully qualified name of the class represented by this object.
	 * @return
	 */
	public String getQualifiedName()
	{
		if (packageName.isEmpty())
			return className;
		
		return packageName + "/" + className;
	}
	
	/**
	 * Creates a new SrgClass instance representing the same class.
	 */
	@Override
	public SrgClass clone()
	{
		return new SrgClass(packageName, className);
	}
	
	@Override
	public String toString()
	{
		return getQualifiedName();
	}
}
