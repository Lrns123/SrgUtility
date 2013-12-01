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

import lombok.Getter;

public class SrgMethodMutator
{
	private SrgMethod reference;
	@Getter private boolean modified = false;
			
	@Getter private String packageName;
	@Getter private String className;
	@Getter private String methodName;
	
	private boolean argumentsModified = false;
	private List<SrgTypeDescriptor> arguments;
	
	@Getter private SrgTypeDescriptor returnType;
	
	/**
	 * Qualified name of the *unmodified* class.
	 */
	@Getter private String qualifiedName;
	
	/**
	 * Instantiates a new class name representation from an fully qualified name.
	 * @param qualifiedName Fully qualified name of the class (e.g. 'package/of/the/Class')
	 */
	SrgMethodMutator(SrgMethod reference)
	{
		this.reference = reference;
		
		this.className = reference.getClassName();
		this.packageName = reference.getPackageName();
		this.methodName = reference.getMethodName();
		this.arguments = reference.getArguments();
		this.returnType = reference.getReturnType();
		
		this.qualifiedName = reference.getQualifiedName();
	}
	
	public void setPackageName(String name)
	{
		modified = true;
		packageName = name;
	}
	
	public void setClassName(String name)
	{
		modified = true;
		className = name;
	}
	
	public void setMethodName(String name)
	{
		modified = true;
		methodName = name;
	}
	
	public void setReturnType(SrgTypeDescriptor descriptor)
	{
		modified = true;
		returnType = descriptor;
	}
	
	public void setReturnType(SrgClass type, int arrayDepth)
	{
		modified = true;
		returnType = new SrgTypeDescriptor(type, arrayDepth);
	}
	
	public void setReturnType(SrgTypeDescriptor.Type type, int arrayDepth)
	{
		modified = true;
		returnType = new SrgTypeDescriptor(type, arrayDepth);
	}
	
	public int getArgumentCount()
	{
		return arguments.size();
	}
	
	public SrgTypeDescriptor getArgument(int idx)
	{
		return arguments.get(idx);
	}
	
	public void clearArguments()
	{
		cloneArguments();
		arguments.clear();
	}
	
	public void addArgument(SrgTypeDescriptor descriptor)
	{
		cloneArguments();
		arguments.add(descriptor);
	}
	
	public void addArgument(SrgClass type, int arrayDepth)
	{
		cloneArguments();
		arguments.add(new SrgTypeDescriptor(type, arrayDepth));
	}
	
	public void addArgument(SrgTypeDescriptor.Type type, int arrayDepth)
	{
		cloneArguments();
		arguments.add(new SrgTypeDescriptor(type, arrayDepth));
	}
	
	public void setArgument(int idx, SrgTypeDescriptor descriptor)
	{
		cloneArguments();
		arguments.set(idx, descriptor);
	}
	
	public void setArgument(int idx, SrgClass type, int arrayDepth)
	{
		cloneArguments();
		arguments.set(idx, new SrgTypeDescriptor(type, arrayDepth));
	}
	
	public void setArgument(int idx, SrgTypeDescriptor.Type type, int arrayDepth)
	{
		cloneArguments();
		arguments.set(idx, new SrgTypeDescriptor(type, arrayDepth));
	}
	
	private void cloneArguments()
	{
		if (argumentsModified)
			return;
		
		arguments = new ArrayList<SrgTypeDescriptor>(arguments);
		
		argumentsModified = true;
		modified = true;
	}
	
	/**
	 * Returns the mutated SrgField. (If no elements have been changed, the original SrgField is returned)
	 */
	public SrgMethod get()
	{
		return modified ? new SrgMethod(packageName, className, methodName, arguments, returnType) : reference;
	}
}
