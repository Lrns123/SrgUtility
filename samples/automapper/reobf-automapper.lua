--[[
**
**	Srg Utility - Mod reobfuscation mappings (Using AutoMapper)
**
**  Target mappings:
**  - Packaged MCP -> Obfuscated  (pkgmcp2obf.srg)
**  - Packaged MCP -> Packaged Numeric (pkgmcp2pkgnum.srg)
**
**  Target inheritance:
**  - NMS inheritance in obfuscated form (obf.inheritmap)
**  - NMS inheritance remapped to Packaged MCP (nms.inheritmap)
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
	{ Mapping.DECORATED_PACKAGED, Mapping.OBFUSCATED, "pkgmcp2obf.srg" },
	{ Mapping.DECORATED_PACKAGED, Mapping.NUMERIC_PACKAGED, "pkgmcp2pkgnum.srg" }
}

local targetInheritance = {
	{ Inheritance.NMS, Mapping.OBFUSCATED, "obf.inheritmap" },
	{ Inheritance.NMS, Mapping.DECORATED_PACKAGED, "nms.inheritmap" }
}

AutoMapper.generate(targetMappings, targetInheritance)
