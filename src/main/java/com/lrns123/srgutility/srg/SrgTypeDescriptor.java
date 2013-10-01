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
		StringBuilder builder = new StringBuilder();
		
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
