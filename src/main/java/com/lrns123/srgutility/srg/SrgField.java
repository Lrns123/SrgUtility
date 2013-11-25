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
 * Represents a field within a class.
 */
@Data
@AllArgsConstructor
public class SrgField
{
	private String packageName;
	private String className;
	private String fieldName;
	
	public SrgField(String qualifiedName)
	{
		String[] parts = ParseUtil.splitFQMN(qualifiedName);

		fieldName = parts[2];
		className = parts[1];		
		packageName = parts[0];
	}
	
	public String getQualifiedName()
	{
		if (packageName.isEmpty())
			return className + "/" + fieldName;
		return packageName + "/" + className + "/" + fieldName;
	}
	
	@Override
	public SrgField clone()
	{
		return new SrgField(packageName, className, fieldName);
	}
	
	@Override
	public String toString()
	{
		return getQualifiedName();
	}
}
