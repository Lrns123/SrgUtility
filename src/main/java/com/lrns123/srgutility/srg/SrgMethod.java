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

import java.util.ArrayList;
import java.util.List;

import com.lrns123.srgutility.util.ParseUtil;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Represents a method of a class.
 */
@EqualsAndHashCode
public class SrgMethod
{
	@Getter private final String packageName;
	@Getter private final String className;
	@Getter private final String methodName;
	@Getter(value = AccessLevel.PACKAGE) private final List<SrgTypeDescriptor> arguments;
	@Getter private final SrgTypeDescriptor returnType;
	
	@Getter private final String methodDescriptor;
	@Getter private final String qualifiedName;
	@Getter private final String qualifiedNameAndDescriptor;
	
	public SrgMethod(String qualifiedNameAndDescriptor)
	{
		int idx = qualifiedNameAndDescriptor.indexOf(' ');
		if (idx == -1)
			throw new IllegalArgumentException("Invalid method descriptor");
		
		this.qualifiedName = qualifiedNameAndDescriptor.substring(0, idx - 1);
		this.methodDescriptor = qualifiedNameAndDescriptor.substring(idx + 1);
		this.qualifiedNameAndDescriptor = qualifiedNameAndDescriptor;
		
		String[] parts = ParseUtil.splitFQMN(qualifiedName);
		
		this.methodName = parts[2];
		this.className = parts[1];
		this.packageName = parts[0];
		this.arguments = new ArrayList<SrgTypeDescriptor>();
		
		if (methodDescriptor.charAt(0) != '(')
			throw new IllegalArgumentException("Could not parse method descriptor. Expected '(', got '" + methodDescriptor.charAt(0) + "' in " + methodDescriptor);
		
		for (int i = 1, len = methodDescriptor.length();;)
		{
			if (i >= len)
				throw new IllegalArgumentException("Premature end of method descriptor");
			
			if (methodDescriptor.charAt(i) == ')')
			{
				returnType = new SrgTypeDescriptor(methodDescriptor, ++i);
				break;
			}
			
			SrgTypeDescriptor type = new SrgTypeDescriptor(methodDescriptor, i);
			arguments.add(type);
			i += type.getQualifiedName().length();
		}		
	}
	
	public SrgMethod(String qualifiedName, String methodDescriptor)
	{
		String[] parts = ParseUtil.splitFQMN(qualifiedName);
		
		this.qualifiedName = qualifiedName;
		this.methodDescriptor = methodDescriptor;
		this.qualifiedNameAndDescriptor = qualifiedName + " " + methodDescriptor;
		
		this.methodName = parts[2];
		this.className = parts[1];
		this.packageName = parts[0];
		this.arguments = new ArrayList<SrgTypeDescriptor>();		
		
		if (methodDescriptor.charAt(0) != '(')
			throw new IllegalArgumentException("Could not parse method descriptor. Expected '(', got '" + methodDescriptor.charAt(0) + "' in " + methodDescriptor);
		
		for (int i = 1, len = methodDescriptor.length();;)
		{
			if (i >= len)
				throw new IllegalArgumentException("Premature end of method descriptor");
			
			if (methodDescriptor.charAt(i) == ')')
			{
				returnType = new SrgTypeDescriptor(methodDescriptor, ++i);
				break;
			}
			
			SrgTypeDescriptor type = new SrgTypeDescriptor(methodDescriptor, i);
			arguments.add(type);
			i += type.getQualifiedName().length();
		}

	}
	
	SrgMethod(String packageName, String className, String methodName, List<SrgTypeDescriptor> arguments, SrgTypeDescriptor returnType)
	{
		this.packageName = packageName;
		this.className = className;
		this.methodName = methodName;
		this.arguments = arguments;
		this.returnType = returnType;
		
		this.qualifiedName = generateQualifiedName();
		this.methodDescriptor = generateMethodDescriptor();
		this.qualifiedNameAndDescriptor = qualifiedName + " " + methodDescriptor;
	}
	
	/**
	 * Copy constructor
	 * @param other Instance to copy
	 */
	private SrgMethod(SrgMethod other)
	{
		this.packageName = other.packageName;
		this.className = other.className;
		this.methodName = other.methodName;
		this.arguments = other.arguments;		
		this.returnType = other.returnType;
		
		this.methodDescriptor = other.methodDescriptor;
		this.qualifiedName = other.qualifiedName;
		this.qualifiedNameAndDescriptor = other.qualifiedNameAndDescriptor;
	}
			
	public int getArgumentCount()
	{
		return arguments.size();
	}
	
	public SrgTypeDescriptor getArgument(int idx)
	{
		return arguments.get(idx);
	}
	
	private String generateQualifiedName()
	{
		if (packageName.isEmpty())
			return className + "/" + methodName;
		return packageName + "/" + className + "/" + methodName;
	}
	
	private String generateMethodDescriptor()
	{
		StringBuilder signature = new StringBuilder();
		
		signature.append('(');
		for (SrgTypeDescriptor descriptor : arguments)
		{
			signature.append(descriptor.getQualifiedName());
		}
		signature.append(')');
		signature.append(returnType.getQualifiedName());
		
		return signature.toString();
	}

	@Override
	public SrgMethod clone()
	{
		return new SrgMethod(this);
	}
	
	@Override
	public String toString()
	{
		return qualifiedNameAndDescriptor;
	}
	
	/**
	 * Returns a mutator for this SrgClass. Allows the class to be modified using COW mechanics.
	 * @return
	 */
	public SrgMethodMutator getMutator()
	{
		return new SrgMethodMutator(this);
	}
}
