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
