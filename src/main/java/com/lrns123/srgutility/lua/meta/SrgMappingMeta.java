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
	}

	public static final class SrgMappingMetaV extends VarArgFunction
	{
		@Override
		public Varargs invoke(Varargs args)
		{
			switch (opcode)
			{
				case OP_SAVE:
					// mapping:save(filename)
					return saveToFile(args.arg1(), args.arg(2));
				case OP_CLONE:
					// mapping:clone()
					return SrgMappingMeta.clone(args.arg1());
				case OP_REVERSE:
					// mapping:reverse()
					return reverse(args.arg1());
				case OP_IDENTITY:
					// mapping:identity()
					return identity(args.arg1());
				case OP_TRANSFORM:
					// mapping:identity(inputTransformer, outputTransformer)
					return transform(args.arg1(), args.arg(2), args.arg(3));
				case OP_FILTER:
					// mapping:filter(filterSrg)
					return filter(args.arg1(), args.arg(2));
			}
			return LuaValue.NIL;
		}
	}

	private static LuaValue saveToFile(LuaValue instance, LuaValue fileArg)
	{
		SrgMapping mapping = (SrgMapping) instance.checkuserdata(SrgMapping.class);
		String filename = fileArg.checkjstring();

		try
		{
			mapping.write(new File(filename));
			return LuaValue.NIL;
		}
		catch (Exception e)
		{
			throw new LuaError(e);
		}
	}

	private static LuaValue clone(LuaValue instance)
	{
		SrgMapping mapping = (SrgMapping) instance.checkuserdata(SrgMapping.class);
		return new LuaUserdata(mapping.clone(), SrgMappingMeta.getMetaTable());
	}

	private static LuaValue reverse(LuaValue instance)
	{
		SrgMapping mapping = (SrgMapping) instance.checkuserdata(SrgMapping.class);
		return new LuaUserdata(mapping.reverse(), SrgMappingMeta.getMetaTable());
	}
	
	private static LuaValue identity(LuaValue instance)
	{
		SrgMapping mapping = (SrgMapping) instance.checkuserdata(SrgMapping.class);
		return new LuaUserdata(mapping.identity(), SrgMappingMeta.getMetaTable());
	}

	private static LuaValue transform(LuaValue instance, LuaValue inputTransformerArg, LuaValue outputTransformerArg)
	{
		SrgMapping mapping = (SrgMapping) instance.checkuserdata(SrgMapping.class);

		MappingTransformer inputTransformer = getTransformerFromArg(inputTransformerArg);
		MappingTransformer outputTransformer = getTransformerFromArg(outputTransformerArg);

		return new LuaUserdata(mapping.transform(inputTransformer, outputTransformer), SrgMappingMeta.getMetaTable());
	}
	
	private static LuaValue filter(LuaValue instance, LuaValue filter)
	{
		SrgMapping mapping = (SrgMapping) instance.checkuserdata(SrgMapping.class);
		SrgMapping filterMapping = (SrgMapping) filter.checkuserdata(SrgMapping.class);

		return new LuaUserdata(mapping.filter(filterMapping), SrgMappingMeta.getMetaTable());
	}
}
