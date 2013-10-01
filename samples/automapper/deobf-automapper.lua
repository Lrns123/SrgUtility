--[[
**
**	Srg Utility - Mod deobfuscation mappings (Using AutoMapper)
**
**  Target mappings:
**  - Obfuscated -> Packaged MCP (obf2pkgmcp.srg)
**  - Numeric -> Packaged MCP (num2pkgmcp.srg)
**  - Packaged Numeric -> Packaged MCP (pkgnum2pkgmcp.srg)
**  - Bukkit -> Packaged MCP (cb2nummcp.srg)
**
**  Target inheritance:
**  - NMS inheritance remapped to Packaged MCP (nms.inheritmap)
**  - Bukkit inheritance remapped to Packaged MCP (cb.inheritmap)
**
]]--

AutoMapper = require("AutoMapper")

AutoMapper.setCacheDir("./cache/")
AutoMapper.setOutputDir("./1.6.2.deobf/")
AutoMapper.setMinecraftVersion("1.6.2")
AutoMapper.setForgeVersion("9.10.1.871")

-- Add shortcuts for Mapping and Inheritance
local Mapping = AutoMapper.Mapping
local Inheritance = AutoMapper.Inheritance

local targetMappings = {
	{ Mapping.OBFUSCATED, Mapping.DECORATED_PACKAGED, "obf2pkgmcp.srg" },
	{ Mapping.NUMERIC, Mapping.DECORATED_PACKAGED, "num2pkgmcp.srg" },
	{ Mapping.NUMERIC_PACKAGED, Mapping.DECORATED_PACKAGED, "pkgnum2pkgmcp.srg" },
	{ Mapping.BUKKIT, Mapping.DECORATED_PACKAGED, "cb2pkgmcp.srg" }
}

local targetInheritance = {
	{ Inheritance.NMS, Mapping.DECORATED_PACKAGED, "nms.inheritmap" },
	{ Inheritance.BUKKIT, Mapping.DECORATED_PACKAGED, "cb.inheritmap" }
}

AutoMapper.generate(targetMappings, targetInheritance)
