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

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Represents a field within a class.
 */
@EqualsAndHashCode
public final class SrgField
{
	@Getter private final String packageName;
	@Getter private final String className;
	@Getter private final String fieldName;
	@Getter private final String qualifiedName;
	
	public SrgField(String qualifiedName)
	{
		String[] parts = ParseUtil.splitFQMN(qualifiedName);

		this.packageName = parts[0];
		this.className = parts[1];	
		this.fieldName = parts[2];
		
		this.qualifiedName = packageName.isEmpty() ? (className + "/" + fieldName) : (packageName + "/" + className + "/" + fieldName);
	}
	
	public SrgField(String packageName, String className, String fieldName)
	{
		this.packageName = packageName;
		this.className = className;	
		this.fieldName = fieldName;
		
		this.qualifiedName = packageName.isEmpty() ? (className + "/" + fieldName) : (packageName + "/" + className + "/" + fieldName);
	}
	
	private SrgField(SrgField other)
	{
		this.packageName = other.packageName;
		this.className = other.className;
		this.fieldName = other.packageName;
		this.qualifiedName = other.qualifiedName;
	}
			
	@Override
	public SrgField clone()
	{
		return new SrgField(this);
	}
	
	@Override
	public String toString()
	{
		return qualifiedName;
	}
	
	/**
	 * Returns a mutator for this SrgClass. Allows the class to be modified using COW mechanics.
	 * @return
	 */
	public SrgFieldMutator getMutator()
	{
		return new SrgFieldMutator(this);
	}
}
