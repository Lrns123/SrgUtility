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

import lombok.Data;

/**
 * Represents a type descriptor.
 */
@Data
public class SrgTypeDescriptor
{
	public enum Type
	{
		BOOLEAN,	// Z
		BYTE,		// B
		CHAR,		// C
		SHORT,		// S
		INT,		// I
		LONG,		// J
		FLOAT,		// F
		DOUBLE,		// D
		OBJECT,		// L<typename>;
		VOID		// V
	}
	
	private Type type;
	private int arrayDepth = 0;
	private SrgClass classType;

	/**
	 * Constructs a new SrgTypeDescriptor. Use for every type *except* Object.
	 * @param type The descriptor's type.
	 * @param arrayDepth The number of array dimensions (0 if none)
	 */
	public SrgTypeDescriptor(Type type, int arrayDepth)
	{
		if (type == Type.OBJECT)
		{
			throw new IllegalArgumentException();
		}
		
		this.type = type;
		this.arrayDepth = arrayDepth;
		this.classType = null;
	}
	
	/**
	 * Constructs a new SrgTypeDescriptor. Use for OBJECT type only.
	 * @param classType The class type for this type descriptor.
	 * @param arrayDepth The number of array dimensions (0 if none)
	 */
	public SrgTypeDescriptor(SrgClass classType, int arrayDepth)
	{
		this.type = Type.OBJECT;
		this.arrayDepth = arrayDepth;
		this.classType = classType;
	}
	
	public String getQualifiedName()
	{
		StringBuilder builder = new StringBuilder(arrayDepth + 1 + (type == Type.OBJECT ? 1 + classType.getQualifiedName().length() : 0) );
		
		for (int i = 0; i < arrayDepth; i++)
		{
			builder.append('[');
		}
		
		switch (type)
		{
			case BOOLEAN:
				builder.append('Z');
				break;
			case BYTE:
				builder.append('B');
				break;
			case CHAR:
				builder.append('C');
				break;
			case SHORT:
				builder.append('S');
				break;
			case INT:
				builder.append('I');
				break;
			case LONG:
				builder.append('J');
				break;
			case FLOAT:
				builder.append('F');
				break;
			case DOUBLE:
				builder.append('D');
				break;
			case OBJECT:
				builder.append('L');
				builder.append(classType.getQualifiedName());
				builder.append(';');
				break;
			case VOID:
				builder.append('V');
				break;
			default:
				// Should not be reachable...
				throw new RuntimeException("Invalid TypeDescriptor type");
			
		}
		
		return builder.toString();
	}
	
	@Override
	public String toString()
	{
		return getQualifiedName();
	}
}
