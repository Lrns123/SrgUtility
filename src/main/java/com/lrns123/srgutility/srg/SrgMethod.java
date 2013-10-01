package com.lrns123.srgutility.srg;

import java.util.ArrayList;

import com.lrns123.srgutility.srg.SrgTypeDescriptor.Type;
import com.lrns123.srgutility.util.RegexUtil;

import lombok.Data;

/**
 * Represents a method of a class.
 */
@Data
public class SrgMethod
{
	private String packageName;
	private String className;
	private String methodName;
	private ArrayList<SrgTypeDescriptor> arguments = new ArrayList<SrgTypeDescriptor>();
	private SrgTypeDescriptor returnType;
	
	public SrgMethod(String qualifiedName, String methodDescriptor)
	{
		String[] parts = RegexUtil.parseJavaIdentifiers(qualifiedName);
		
		if (parts.length < 2)
		{
			throw new IllegalArgumentException("Invalid srg indentifier");
		}
		methodName = parts[parts.length - 1];
		className = parts[parts.length - 2];
		
		StringBuilder packageBuilder = new StringBuilder();
		
		for (int i = 0; i < parts.length - 2; i++)
		{
			if (i != 0)
				packageBuilder.append('/');
			packageBuilder.append(parts[i]);
		}
		
		
		packageName = packageBuilder.toString();
		
		parseMethodDescriptor(methodDescriptor);
	}
	
	private void parseMethodDescriptor(String signature)
	{
		boolean preArgs = true;
		boolean postArgs = false;
		
		int len = signature.length();
		int arrayDepth = 0;
		
		for (int i = 0; i < len; i++)
		{
			char ch = signature.charAt(i);
			
			if (preArgs)
			{
				if (ch != '(')
					throw new IllegalArgumentException("Could not parse method descriptor. Expected '(', got '" + ch + "' in " + signature);
				
				preArgs = false;
				continue;
			}
			
			
			SrgTypeDescriptor descriptor = null;
			switch (ch)
			{
				case '[':
					arrayDepth++;
					break;
				case 'Z':
					descriptor = new SrgTypeDescriptor(Type.BOOLEAN, arrayDepth);
					break;
				case 'B':
					descriptor = new SrgTypeDescriptor(Type.BYTE, arrayDepth);
					break;
				case 'C':
					descriptor = new SrgTypeDescriptor(Type.CHAR, arrayDepth);
					break;
				case 'S':
					descriptor = new SrgTypeDescriptor(Type.SHORT, arrayDepth);
					break;
				case 'I':
					descriptor = new SrgTypeDescriptor(Type.INT, arrayDepth);
					break;
				case 'J':
					descriptor = new SrgTypeDescriptor(Type.LONG, arrayDepth);
					break;
				case 'F':
					descriptor = new SrgTypeDescriptor(Type.FLOAT, arrayDepth);
					break;
				case 'D':
					descriptor = new SrgTypeDescriptor(Type.DOUBLE, arrayDepth);
					break;
				case 'V':
					descriptor = new SrgTypeDescriptor(Type.VOID, arrayDepth);
					break;					
				case 'L':
				{
					String remainder = signature.substring(i + 1);
					int end = remainder.indexOf(';');
					if (end == -1)
					{
						throw new IllegalArgumentException("Could not parse method descriptor: Could not parse Object argument in " + signature);
					}
					
					String classType = remainder.substring(0, end);
					i += classType.length() + 1;
					
					descriptor = new SrgTypeDescriptor(new SrgClass(classType), arrayDepth);
					break;
				}
				case ')':
					if (postArgs)
						throw new IllegalArgumentException("Could not pase method descriptor: Invalid character '" + ch + "' found in " + signature);
					postArgs = true;
					break;
				default:
					throw new IllegalArgumentException("Could not pase method descriptor: Invalid character '" + ch + "' found in " + signature);
			}
			
			if (descriptor != null)
			{
				if (postArgs)
				{
					if (returnType != null)
						throw new IllegalArgumentException("Could not parse method signature: Found multiple return types in " + signature);
					
					returnType = descriptor;
				}
				else
				{
					arguments.add(descriptor);
				}
				arrayDepth = 0;
			}
		}
	}
	
	public String getMethodDescriptor()
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
	
	public String getQualifiedName()
	{
		if (packageName.isEmpty())
			return className + "/" + methodName + " " + getMethodDescriptor();
		return packageName + "/" + className + "/" + methodName + " " + getMethodDescriptor();
	}
	
	@Override
	public SrgMethod clone()
	{
		return new SrgMethod(packageName + "/" + className + "/" + methodName, getMethodDescriptor());
	}
	
	@Override
	public String toString()
	{
		return getQualifiedName();
	}
}
