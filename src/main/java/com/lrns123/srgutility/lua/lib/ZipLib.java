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
package com.lrns123.srgutility.lua.lib;

import java.io.File;
import java.util.zip.ZipFile;

import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaUserdata;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;

import com.lrns123.srgutility.lua.meta.ZipFileMeta;

public class ZipLib extends TwoArgFunction
{
	public ZipLib()
	{
	}

	public LuaValue call(LuaValue modname, LuaValue env)
	{
		LuaTable t = new LuaTable();
		
		bind(t, ZipLibV.class, new String[] {"open"});

		env.set("zip", t);
		env.get("package").get("loaded").set("zip", t);

		return t;
	}
	
	public static final class ZipLibV extends VarArgFunction
	{
		@Override
		public Varargs invoke(Varargs args)
		{
		    switch (opcode)
		    {
		    	case 0:	// zip.open(filename)
		    		return open(args.arg1());
		    	
		    }
		    return LuaValue.NIL;
		}
	}
	
	private static LuaValue open(LuaValue pathArg)
	{
		try
		{
			return new LuaUserdata(new ZipFile(new File(pathArg.checkjstring())), ZipFileMeta.getMetaTable());
		}
		catch (Exception e)
		{
			throw new LuaError(e);
		}
	}
}
