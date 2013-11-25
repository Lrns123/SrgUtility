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
package com.lrns123.srgutility.lua.meta;

import java.io.File;

import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaUserdata;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.LibFunction;
import org.luaj.vm2.lib.VarArgFunction;

import com.lrns123.srgutility.srg.SrgMapping;
import com.lrns123.srgutility.transformer.MappingTransformer;
import static com.lrns123.srgutility.lua.util.LuaUtil.getTransformerFromArg;

public class SrgMappingMeta extends LibFunction
{
	private static LuaTable metatable;
	
	private static final int OP_SAVE = 0;
	private static final int OP_CLONE = 1;
	private static final int OP_REVERSE = 2;
	private static final int OP_IDENTITY = 3;
	private static final int OP_TRANSFORM = 4;
	private static final int OP_FILTER = 5;

	public static LuaTable getMetaTable()
	{
		if (metatable == null)
			new SrgMappingMeta();
		return metatable;
	}

	private SrgMappingMeta()
	{
		metatable = new LuaTable();

		bind(metatable, SrgMappingMetaV.class, new String[] { "saveToFile", "clone", "reverse", "identity", "transform", "filter" });
		metatable.set(INDEX, metatable);
		metatable.set(METATABLE, LuaValue.FALSE);
	}

	public static final class SrgMappingMetaV extends VarArgFunction
	{
		@Override
		public Varargs invoke(Varargs args)
		{
			SrgMapping instance = (SrgMapping)args.arg1().checkuserdata(SrgMapping.class);
			
			switch (opcode)
			{
				case OP_SAVE:
					// mapping:save(filename)
					return saveToFile(instance, args.arg(2).checkjstring());
				case OP_CLONE:
					// mapping:clone()
					return SrgMappingMeta.clone(instance);
				case OP_REVERSE:
					// mapping:reverse()
					return reverse(instance);
				case OP_IDENTITY:
					// mapping:identity()
					return identity(instance);
				case OP_TRANSFORM:
					// mapping:identity(inputTransformer, outputTransformer)
					return transform(instance, getTransformerFromArg(args.arg(2)), getTransformerFromArg(args.arg(3)));
				case OP_FILTER:
					// mapping:filter(filterSrg)
					return filter(instance, (SrgMapping)args.arg(2).checkuserdata(SrgMapping.class));
			}
			return LuaValue.NIL;
		}
	}

	private static LuaValue saveToFile(SrgMapping instance, String filename)
	{
		try
		{
			instance.write(new File(filename));
			return LuaValue.NIL;
		}
		catch (Exception e)
		{
			throw new LuaError(e);
		}
	}

	private static LuaValue clone(SrgMapping instance)
	{
		return new LuaUserdata(instance.clone(), SrgMappingMeta.getMetaTable());
	}

	private static LuaValue reverse(SrgMapping instance)
	{
		return new LuaUserdata(instance.reverse(), SrgMappingMeta.getMetaTable());
	}
	
	private static LuaValue identity(SrgMapping instance)
	{
		return new LuaUserdata(instance.identity(), SrgMappingMeta.getMetaTable());
	}

	private static LuaValue transform(SrgMapping instance, MappingTransformer inputTransformer, MappingTransformer outputTransformer)
	{
		return new LuaUserdata(instance.transform(inputTransformer, outputTransformer), SrgMappingMeta.getMetaTable());
	}
	
	private static LuaValue filter(SrgMapping instance, SrgMapping filter)
	{
		return new LuaUserdata(instance.filter(filter), SrgMappingMeta.getMetaTable());
	}
}
