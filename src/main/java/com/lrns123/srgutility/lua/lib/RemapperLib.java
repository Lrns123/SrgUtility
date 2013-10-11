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
import net.md_5.specialsource.Jar;
import net.md_5.specialsource.JarMapping;
import net.md_5.specialsource.JarRemapper;
import net.md_5.specialsource.provider.JarProvider;
import net.md_5.specialsource.provider.JointProvider;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;

import com.lrns123.srgutility.adapter.InheritanceProviderAdapter;
import com.lrns123.srgutility.adapter.JarMappingAdapter;
import com.lrns123.srgutility.srg.SrgInheritanceMap;
import com.lrns123.srgutility.srg.SrgMapping;

public class RemapperLib extends TwoArgFunction
{
	private static final int OP_REMAPJAR = 0;
	
	@Override
	public LuaValue call(LuaValue modname, LuaValue env)
	{
		LuaTable t = new LuaTable();
		
		bind(t, RemapperLibV.class, new String[] {"remapJar"});

		env.set("Remapper", t);
		env.get("package").get("loaded").set("Remapper", t);

		return t;
	}
	
	public static final class RemapperLibV extends VarArgFunction
	{
		@Override
		public Varargs invoke(Varargs args)
		{
		    switch (opcode)
		    {
		    	case OP_REMAPJAR:
		    		// Remapper.remapJar(inJar, outJar, mapping, inheritance)
		    		return remapJar(new File(args.arg(1).checkjstring()), new File(args.arg(2).checkjstring()), (SrgMapping)args.arg(3).checkuserdata(SrgMapping.class), (SrgInheritanceMap)args.arg(4).optuserdata(SrgInheritanceMap.class, null));
		    }
		    return LuaValue.NIL;
		}
	}

	private static LuaValue remapJar(File inJar, File outJar, SrgMapping mapping, SrgInheritanceMap inheritance)
	{
		try
		{
	        Jar jar = Jar.init(inJar);
	        JointProvider inheritanceProviders = new JointProvider();
	        JarMapping jarMapping = JarMappingAdapter.convertSrgMapping(mapping);
	        jarMapping.setFallbackInheritanceProvider(inheritanceProviders);
	        inheritanceProviders.add(new JarProvider(jar));
	        if (inheritance != null)
	        {
	        	inheritanceProviders.add(new InheritanceProviderAdapter(inheritance));
	        }
	
	        JarRemapper jarRemapper = new JarRemapper(jarMapping);
        	jarRemapper.remapJar(jar, outJar);
		}
        catch (Throwable e)
        {
        	throw new LuaError(e);
        }
		
		return LuaValue.NIL;
	}
}
