package com.lrns123.srgutility.lua.util;

import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;

import com.lrns123.srgutility.mcp.MinecraftCodersPackMapping;
import com.lrns123.srgutility.srg.SrgMapping;
import com.lrns123.srgutility.transformer.IdentityTransformer;
import com.lrns123.srgutility.transformer.MappingTransformer;
import com.lrns123.srgutility.transformer.MinecraftCodersPackTransformer;
import com.lrns123.srgutility.transformer.SrgMappingTransformer;

public class LuaUtil
{
	public static MappingTransformer getTransformerFromArg(LuaValue arg)
	{
		if (arg.isnil())
			return new IdentityTransformer();

		if (arg.isuserdata(SrgMapping.class))
			return new SrgMappingTransformer((SrgMapping) arg.checkuserdata(SrgMapping.class));

		if (arg.isuserdata(MinecraftCodersPackMapping.class))
			return new MinecraftCodersPackTransformer((MinecraftCodersPackMapping) arg.checkuserdata(MinecraftCodersPackMapping.class));

		throw new LuaError("Cannot convert " + arg.toString() + " into a mapping transformer.");
	}
}
