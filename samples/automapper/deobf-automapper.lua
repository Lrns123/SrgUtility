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
	{ Mapping.OBFUSCATED, Mapping.DESCRIPTIVE_PACKAGED, "obf2pkgmcp.srg" },
	{ Mapping.NUMERIC, Mapping.DESCRIPTIVE_PACKAGED, "num2pkgmcp.srg" },
	{ Mapping.NUMERIC_PACKAGED, Mapping.DESCRIPTIVE_PACKAGED, "pkgnum2pkgmcp.srg" },
	{ Mapping.BUKKIT, Mapping.DESCRIPTIVE_PACKAGED, "cb2pkgmcp.srg" }
}

local targetInheritance = {
	{ Inheritance.NMS, Mapping.DESCRIPTIVE_PACKAGED, "nms.inheritmap" },
	{ Inheritance.BUKKIT, Mapping.DESCRIPTIVE_PACKAGED, "cb.inheritmap" }
}

AutoMapper.generate(targetMappings, targetInheritance)
