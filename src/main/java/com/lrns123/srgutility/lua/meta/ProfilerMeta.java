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

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.LibFunction;
import org.luaj.vm2.lib.VarArgFunction;

import com.lrns123.srgutility.lua.util.Profiler;

public class ProfilerMeta extends LibFunction
{
	private static LuaTable metatable;

	private static final int OP_START = 0;
	private static final int OP_STOP = 1;
	private static final int OP_GETSEC = 2;
	private static final int OP_GETMSEC = 3;
	private static final int OP_GETNSEC = 4;
	
	public static LuaTable getMetaTable()
	{
		if (metatable == null)
			new ProfilerMeta();
		return metatable;
	}

	private ProfilerMeta()
	{
		metatable = new LuaTable();

		bind(metatable, ZipEntryMetaV.class, new String[] { "start", "stop", "getSec", "getMSec", "getNSec" });
		metatable.set(INDEX, metatable);
		metatable.set(METATABLE, LuaValue.FALSE);
	}

	public static final class ZipEntryMetaV extends VarArgFunction
	{
		@Override
		public Varargs invoke(Varargs args)
		{
			Profiler entry = (Profiler)args.arg1().checkuserdata(Profiler.class);
			
			switch (opcode)
			{
				case OP_START:
					entry.start();
					return LuaValue.NONE;
				case OP_STOP:
					entry.stop();
					return LuaValue.NONE;
				case OP_GETSEC:
					return LuaValue.valueOf(entry.getSec());
				case OP_GETMSEC:
					return LuaValue.valueOf(entry.getMSec());
				case OP_GETNSEC:
					return LuaValue.valueOf(entry.getNSec());
			}
			return LuaValue.NONE;
		}
	}
}
