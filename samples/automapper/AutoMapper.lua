--[[
**
**  Srg Utility - AutoMapper Library
**  Copyright (c) 2013 Lourens "Lrns123" Elzinga
**
**  Automates the process of generating MCP and Bukkit mappings.
**
**  Usage:
**
**  * Execute this script before your own. (i.e. java -jar SrgUtility.jar /path/to/AutoMapper.lua /path/to/your/script.lua)
**
**  * Load AutoMapper using require("AutoMapper")
**  
**  * Initialize AutoMapper's required parameters, for example:
**      AutoMapper.setCacheDir("/some/cache/directory/")
**      AutoMapper.setOutputDir("/some/output/directory/")
**      AutoMapper.setMinecraftVersion("1.6.2")
**      AutoMapper.setBukkitVersion("1.6.2") [Optional, defaults to Minecraft version]
**      AutoMapper.setForgeVersion("9.10.1.871") OR AutoMapper.setConfDir("/path/to/mcp/conf/")
** 
**  * Create a table with your desired mappings, with the following format:
**
**  {
**      { <input mapping>, <output mapping>, <output filename> },
**      { <input mapping>, <output mapping>, <output filename> },
**      { <input mapping>, <output mapping>, <output filename> }
**  }
**
**  * Create a table with your desired inheritance mappings, with the following format:
**
**  {
**      { <inheritance map>, <mapping>, <output filename> },
**      { <inheritance map>, <mapping>, <output filename> },
**      { <inheritance map>, <mapping>, <output filename> }
**  }
**
**  * Constants for mappings are available in AutoMapper.Mapping.
**    Valid constants are: OBFUSCATED, NUMERIC, DESCRIPTIVE, NUMERIC_PACKAGED, DESCRIPTIVE_PACKAGED, BUKKIT.
**  
**  * Constants for inheritance maps are available in AutoMapper.Inheritance.
**    Valid constants are: NMS, BUKKIT.
**
**  * Generate mappings by calling AutoMapper.generate(mappings, inheritance) providing the tables mentioned above.
**
]]--

--[[-----------------------------------------------------------------------------
  || Package registration
--]]-----------------------------------------------------------------------------
local AutoMapper = {}
package.loaded["AutoMapper"] = AutoMapper

--[[-----------------------------------------------------------------------------
  || Constants
--]]-----------------------------------------------------------------------------

local function constantProxy(tbl)
	local proxy = {}
	setmetatable(proxy, {__index = tbl, __newindex = function() error("Attempted to modify a constant.") end })
	return proxy
end

local Mapping = {
	OBFUSCATED = 0x0,
	NUMERIC = 0x1,
	DESCRIPTIVE = 0x2,
	NUMERIC_PACKAGED = 0x3,
	DESCRIPTIVE_PACKAGED = 0x4,
	BUKKIT = 0x5
}
AutoMapper.Mapping = constantProxy(Mapping)

local Inheritance = {
	NMS = 0x10,
	BUKKIT = 0x11
}
AutoMapper.Inheritance = constantProxy(Inheritance)

--[[-----------------------------------------------------------------------------
  || Internal variables
--]]-----------------------------------------------------------------------------
local d_cacheDir
local d_outDir
local d_minecraftVersion
local d_bukkitVersion
local d_forgeVersion
local d_confDir

local d_clientJar
local d_serverJar
local d_bukkitJar
local d_forgeZip
local d_mcpDir

local d_targetMappings
local d_targetInheritance
local d_neededMaps = {}
local d_maps = {}

--[[-----------------------------------------------------------------------------
  || API FUNCTION
  ||
  || AutoMapper.setCacheDir(cacheDir:string)
  ||
  || Sets the cache directory. This directory will be used to to store
  || downloaded jars and conf files.
--]]-----------------------------------------------------------------------------
function AutoMapper.setCacheDir(cacheDir)
	assert(cacheDir ~= nil)
	d_cacheDir = fs.combine(cacheDir, "") .. "/"
end

--[[-----------------------------------------------------------------------------
  || API FUNCTION
  ||
  || AutoMapper.setOutputDir(outDir:string)
  ||
  || Sets the output directory. The target mappings will be saved in this dir.
--]]-----------------------------------------------------------------------------
function AutoMapper.setOutputDir(outDir)
	assert(outDir ~= nil)
	d_outDir = fs.combine(outDir, "") .. "/"
end

--[[-----------------------------------------------------------------------------
  || API FUNCTION
  ||
  || AutoMapper.setMinecraftVersion(version:string)
  ||
  || Sets the desired minecraft version (x.x.x).
--]]-----------------------------------------------------------------------------
function AutoMapper.setMinecraftVersion(version)
	assert(version ~= nil)
	d_minecraftVersion = version
end

--[[-----------------------------------------------------------------------------
  || API FUNCTION
  ||
  || AutoMapper.setForgeVersion(version:string)
  ||
  || Sets the desired forge version (x.x.x.x).
  || The version number must not include the minecraft version.
  ||
  || Setting this will remove any previously set MCP conf directory.
--]]-----------------------------------------------------------------------------
function AutoMapper.setForgeVersion(version)
	assert(version ~= nil)
	d_forgeVersion = version
	d_confDir = nil
end

--[[-----------------------------------------------------------------------------
  || API FUNCTION
  ||
  || AutoMapper.setBukkitVersion(version:string)
  ||
  || Sets the desired forge version (x.x.x.x).
  || The version number must not include the bukkit version.
--]]-----------------------------------------------------------------------------
function AutoMapper.setBukkitVersion(version)
	assert(version ~= nil)
	d_bukkitVersion = version
end

--[[-----------------------------------------------------------------------------
  || API FUNCTION
  ||
  || AutoMapper.setConfDir(dir:string)
  ||
  || Sets the MCP conf directory.
  || This will disable the use of Forge, and wipe the forge version setting.
  ||
  || Use this to use a custom set of csv's and base mappings instead of
  ||  obtaining them from Forge.
--]]-----------------------------------------------------------------------------
function AutoMapper.setConfDir(dir)
	assert(dir ~= nil)
	d_confDir = fs.combine(dir, "") .. "/"
	d_forgeVersion = nil
end

--[[-------------------------------------------------------------------
  || INTERNAL FUNCTION
  ||
  || Verifies if all necessary settings are provided.
  || Throws an error if any setting is missing.
--]]-------------------------------------------------------------------
local function verifySettings()
    if (d_bukkitVersion == nil) then
        print("Defaulting Bukkit version to " .. d_minecraftVersion)
        d_bukkitVersion = d_minecraftVersion
    end

	if (d_cacheDir == nil) then
		error("Please set the cache directory before generating mappings.")
	elseif (d_outDir == nil) then
		error("Please set the output directory before generating mappings.")
	elseif (d_minecraftVersion == nil) then
		error("Please set the desired minecraft version before generating mappings.")
	elseif (d_forgeVersion == nil and d_confDir == nil) then
		error("Please set either the desired forge version or the conf directory location before generating mappings.")
	elseif (d_forgeVersion ~= nil and d_confDir ~= nil) then
		-- Sanity check, this should never be reached.
		error("Both the Forge version and conf dir are specified. This should never happen. Please report this bug immediately.")
	end
end

--[[-------------------------------------------------------------------
  || INTERNAL FUNCTION
  ||
  || Returns whether the provided Mapping ID is valid,
  ||  and queues necessary maps.
--]]-------------------------------------------------------------------
local function checkMappingType(id)
	if (id == Mapping.OBFUSCATED) then
		return true
	elseif (id == Mapping.NUMERIC) then
		d_neededMaps.numeric = true
		return true
	elseif (id == Mapping.DESCRIPTIVE) then
		d_neededMaps.descriptive = true
		d_neededMaps.mcpNames = true
		return true
	elseif (id == Mapping.NUMERIC_PACKAGED) then
		d_neededMaps.numericPackaged = true
		d_neededMaps.mcpPackages = true
		return true
	elseif (id == Mapping.DESCRIPTIVE_PACKAGED) then
		d_neededMaps.descriptivePackaged = true
		d_neededMaps.mcpNames = true
		d_neededMaps.mcpPackages = true
		return true
	elseif (id == Mapping.BUKKIT) then
		d_neededMaps.bukkitBaseline = true
		d_neededMaps.bukkit = true
		return true
	else
		return false
	end
end

--[[-------------------------------------------------------------------
  || INTERNAL FUNCTION
  ||
  || Returns whether the provided Mapping ID is valid,
  ||  and queues necessary maps.
--]]-------------------------------------------------------------------
local function checkInheritanceType(id)
	if (id == Inheritance.NMS) then
		d_neededMaps.nmsInherit = true
		return true
	elseif (id == Inheritance.BUKKIT) then
		d_neededMaps.bukkitInherit = true
		d_neededMaps.bukkit = true
		return true
	else
		return false
	end
end

--[[-------------------------------------------------------------------
  || INTERNAL FUNCTION
  ||
  || Verifies the provided mapping tables and determines which 
  ||  internal maps (baselines and transformers) are needed.
  || Throws an error if invalid entries are found.
--]]-------------------------------------------------------------------
local function verifyInput()
	local k,v
	print("Verify mappings...")
	d_neededMaps = {}
	
	for k,v in pairs(d_targetMappings) do
		if not (checkMappingType(v[1])) then
			error("Invalid mapping on line " .. k)
		end
		
		if not (checkMappingType(v[2])) then
			error("Invalid mapping on line " .. k)
		end
		
		if (type(v[3]) ~= "string") then
			error("Invalid filename on line " .. k)
		end
	end

	for k,v in pairs(d_targetInheritance) do
		if not (checkInheritanceType(v[1])) then
			error("Invalid inheritance on line " + k)
		end
		
		if not (checkMappingType(v[2])) then
			error("Invalid inheritance on line " .. k)
		end
		
		if (type(v[3]) ~= "string") then
			error("Invalid filename on line " .. k)
		end
	end
end

--[[-------------------------------------------------------------------
  || INTERNAL FUNCTION
  ||
  || Checks the conf directory to make sure all necessary files exist,
  ||  extracting them from the forge zip if necessary.
  || Throws an error if files could not be found and/or extracted.
--]]-------------------------------------------------------------------
local function prepareConfDir()
	print("Preparing conf directory...")
	d_mcpDir = string.format("%sconf_%s-%s/", d_cacheDir, d_minecraftVersion, d_forgeVersion)
	fs.makeDir(d_mcpDir)
	local zipFile = zip.open(d_forgeZip)
		
	if (not (fs.exists(d_mcpDir .. "joined.srg") or (fs.exists(d_mcpDir .. "client.srg") and fs.exists(d_mcpDir .. "server.srg")))) then
		local joined = zipFile:getEntry("forge/fml/conf/joined.srg")
		local client = zipFile:getEntry("forge/fml/conf/client.srg")
		local server = zipFile:getEntry("forge/fml/conf/server.srg")
		if (joined ~= nil) then
			zipFile:extract(joined, d_mcpDir .. "joined.srg")
		elseif (client ~= nil and server ~= nil) then
			zipFile:extract(client, d_mcpDir .. "client.srg")
			zipFile:extract(server, d_mcpDir .. "server.srg")
		else
			error("Could not find base srg(s).")
		end
	end
	
	if (not fs.exists(d_mcpDir .. "fields.csv")) then
		local entry = zipFile:getEntry("forge/fml/conf/fields.csv")
		if (entry ~= nil) then
			zipFile:extract(entry, d_mcpDir .. "fields.csv")
		else
			error("Could not find fields.csv.")
		end
	end
	
	if (not fs.exists(d_mcpDir .. "methods.csv")) then
		local entry = zipFile:getEntry("forge/fml/conf/methods.csv")
		if (entry ~= nil) then
			zipFile:extract(entry, d_mcpDir .. "methods.csv")
		else
			error("Could not find methods.csv.")
		end
	end
	
	if (not fs.exists(d_mcpDir .. "packages.csv")) then
		local entry = zipFile:getEntry("forge/fml/conf/packages.csv")
		if (entry ~= nil) then
			zipFile:extract(entry, d_mcpDir .. "packages.csv")
		else
			error("Could not find packages.csv.")
		end
	end
	
	zipFile:close()
end

--[[-------------------------------------------------------------------
  || INTERNAL FUNCTION
  ||
  || Checks the conf directory to make sure all necessary files exist.
  || Throws an error if files could not be found.
--]]-------------------------------------------------------------------
local function checkConfDir()
	print("Checking conf directory...")
	assert(d_confDir)
	d_mcpDir = d_confDir
	if not (fs.exists(d_mcpDir) and fs.isDir(d_mcpDir)) then
		error("Could not find conf directory")
	end
	
	if (not (fs.exists(d_mcpDir .. "joined.srg") or (fs.exists(d_mcpDir .. "client.srg") and fs.exists(d_mcpDir .. "server.srg")))) then
		error("Could not find base srg(s).")
	end
	
	if (not fs.exists(d_mcpDir .. "fields.csv")) then
		error("Could not find fields.csv.")
	end
	
	if (not fs.exists(d_mcpDir .. "methods.csv")) then
		error("Could not find methods.csv.")
	end
	
	if (not fs.exists(d_mcpDir .. "packages.csv")) then
		error("Could not find packages.csv.")
	end
end

--[[-------------------------------------------------------------------
  || INTERNAL FUNCTION
  ||
  || Downloads the required jars as specified by requiredFiles.
--]]-------------------------------------------------------------------
local function downloadJars(requiredFiles)
	if (not fs.exists(d_cacheDir)) then
		fs.makeDir(d_cacheDir)
	end

	print("Acquiring required jars...")
	
	if (requiredFiles.client) then
		d_clientJar = string.format("%sclient_%s.jar", d_cacheDir, d_minecraftVersion)
		
		if (not fs.exists(d_clientJar)) then
			print("\tDownloading minecraft client jar...")
			HTTP.download(string.format("http://s3.amazonaws.com/Minecraft.Download/versions/%s/%s.jar", d_minecraftVersion, d_minecraftVersion), d_clientJar)
		else
			print("\tUsing cached Minecraft client jar...")
		end
	end
	
	if (requiredFiles.server) then
		d_serverJar = string.format("%sserver_%s.jar", d_cacheDir, d_minecraftVersion)
		
		if (not fs.exists(d_serverJar)) then
			print("\tDownloading minecraft server jar...")
			HTTP.download(string.format("http://s3.amazonaws.com/Minecraft.Download/versions/%s/minecraft_server.%s.jar", d_minecraftVersion, d_minecraftVersion), d_serverJar)
		else
			print("\tUsing cached Minecraft server jar...")
		end
	end
	
	if (requiredFiles.bukkit) then
		d_bukkitJar = string.format("%sbukkit_%s.jar", d_cacheDir, d_bukkitVersion)
		
		if (not fs.exists(d_bukkitJar)) then
			print("\tDownloading Bukkit server jar...")
			HTTP.download(string.format("http://repo.bukkit.org/content/repositories/releases/org/bukkit/minecraft-server/%s/minecraft-server-%s.jar", d_bukkitVersion, d_bukkitVersion), d_bukkitJar)
		else
			print("\tUsing cached Bukkit server jar...")
		end
	end
	
	if (requiredFiles.forge) then
		assert(d_forgeVersion)
		d_forgeZip = string.format("%sforge_%s-%s.zip", d_cacheDir, d_minecraftVersion, d_forgeVersion)
		
		if (not fs.exists(d_forgeZip)) then
			print("\tDownloading Minecraft Forge zip...")
			HTTP.download(string.format("http://files.minecraftforge.net/minecraftforge/minecraftforge-src-%s-%s.zip", d_minecraftVersion, d_forgeVersion), d_forgeZip)
		else
			print("\tUsing cached Minecraft Forge zip...")
		end
	end
end

--[[-------------------------------------------------------------------
  || INTERNAL FUNCTION
  ||
  || Determines which jars are required to generate the desired
  ||  mappings, and will download them if necessary.
--]]-------------------------------------------------------------------
local function prepareJars()
	local neededFiles = {}
	local k,v
	
	if (d_forgeVersion) then
		neededFiles.forge = true
	end
	
	for k,v in pairs(d_targetMappings) do
		if (v[1] == Mapping.BUKKIT or v[2] == Mapping.BUKKIT) then
			neededFiles.server = true
			neededFiles.bukkit = true
		end
	end
	
	for k,v in pairs(d_targetInheritance) do
		if (v[1] == Inheritance.NMS) then
			neededFiles.client = true
		elseif (v[1] == Inheritance.BUKKIT) then
			neededFiles.server = true
			neededFiles.bukkit = true
		end
		
		if (v[2] == Mapping.BUKKIT) then
			neededFiles.server = true
			neededFiles.bukkit = true
		end
	end
	
	downloadJars(neededFiles)
end

--[[-------------------------------------------------------------------
  || INTERNAL FUNCTION
  ||
  || Generates the base maps and transformers.
--]]-------------------------------------------------------------------
local function generateBaseMaps()
	print("Generating base maps...")
	
	print("\tLoading MCP base mapping")
	if (fs.exists(d_mcpDir .. "joined.srg")) then
		d_maps["base"] = MappingFactory.loadSrg(d_mcpDir .. "joined.srg")
	else
		d_maps["base"] = MappingFactory.loadSrg(d_mcpDir .. "client.srg", d_mcpDir .. "server.srg")
	end
	
	print("\tGenerating NMS identity mapping")
	d_maps["nmsIdentity"] = d_maps["base"]:clone():identity()
	
	if (d_neededMaps.numeric) then
		print("\tGenerating numeric transformer")
		d_maps["numeric"] = d_maps["base"]
	end
	
	if (d_neededMaps.mcpPackages) then
		print("\tLoading package information")
		d_maps["mcpPackage"] = MappingFactory.loadMCP(nil, nil, d_mcpDir .. "packages.csv")
	end
	
	if (d_neededMaps.mcpNames) then
		print("\tLoading descriptive symbol information")
		d_maps["mcpNames"] = MappingFactory.loadMCP(d_mcpDir .. "fields.csv", d_mcpDir .. "methods.csv", nil)
	end
	
	if (d_neededMaps.descriptive) then
		print("\tGenerating descriptive transformer")
		assert(d_maps["mcpNames"])
		d_maps["descriptive"] = d_maps["base"]:clone():transform(nil, d_maps["mcpNames"])
	end
	
	if (d_neededMaps.numericPackaged) then
		print("\tGenerating packaged numeric transformer")
		assert(d_maps["mcpPackage"])
		d_maps["numericPackaged"] = d_maps["base"]:clone():transform(nil, d_maps["mcpPackage"])
	end
	
	if (d_neededMaps.descriptivePackaged) then
		print("\tGenerating packaged descriptive transformer")
		assert(d_maps["mcpPackage"])
		assert(d_maps["mcpNames"])
		d_maps["descriptivePackaged"] = d_maps["base"]:clone():transform(nil, d_maps["mcpPackage"]):transform(nil, d_maps["mcpNames"])
	end
	
	if (d_neededMaps.bukkit) then
		print("\tGenerating Bukkit transformer")
		d_maps["bukkit"] = MappingFactory.compareJars(d_serverJar, d_bukkitJar):filter(d_maps["nmsIdentity"])
	end
	
	if (d_neededMaps.bukkitBaseline) then
		print("\tGenerating Bukkit identity mapping")
		if (d_maps["bukkit"]) then
			d_maps["bukkitIdentity"] = d_maps["bukkit"]:clone():identity()
		else
			d_maps["bukkitIdentity"] = MappingFactory.compareJars(d_serverJar, d_bukkitJar):filter(d_maps["nmsIdentity"]):identity()
		end
	end
	
	if (d_neededMaps.nmsInherit) then
		print("\tGenerating NMS inheritance mapping")
		d_maps["nmsInherit"] = MappingFactory.makeInheritanceMap(d_clientJar, d_maps["nmsIdentity"])
	end
	
	if (d_neededMaps.bukkitInherit) then
		print("\tGenerating Bukkit inheritance mapping")
		assert(d_maps["bukkit"])
		local bukkitRev = d_maps["bukkit"]:clone():reverse()
		d_maps["bukkitInherit"] = MappingFactory.makeInheritanceMap(d_bukkitJar, bukkitRev):transform(bukkitRev)
	end
end

--[[-------------------------------------------------------------------
  || INTERNAL FUNCTION
  ||
  || Asserts the value is not nil and returns it.
--]]-------------------------------------------------------------------
local function assertReturn(x)
	assert(x)
	return x
end

--[[-------------------------------------------------------------------
  || INTERNAL FUNCTION
  ||
  || Returns the baseline to use for the requested mapping.
--]]-------------------------------------------------------------------
local function getBaselineMap(inputMapping, outputMapping)
	if (inputMapping == Mapping.BUKKIT or outputMapping == Mapping.BUKKIT) then
		return assertReturn(d_maps["bukkitIdentity"])
	else
		return assertReturn(d_maps["nmsIdentity"])
	end
end

--[[-------------------------------------------------------------------
  || INTERNAL FUNCTION
  ||
  || Returns the baseline to use for the requested inheritance mapping.
--]]-------------------------------------------------------------------
local function getInheritanceBaselineMap(inheritance)
	if (inheritance == Inheritance.NMS) then
		return assertReturn(d_maps["nmsInherit"])
	elseif (inheritance ==  Inheritance.BUKKIT) then
		return assertReturn(d_maps["bukkitInherit"])
	else
		error("Invalid inheritance map requested")
	end
end

--[[-------------------------------------------------------------------
  || INTERNAL FUNCTION
  ||
  || Returns the baseline to use for the requested mapping.
--]]-------------------------------------------------------------------
local function getTransformer(mapping)
	if (mapping == Mapping.OBFUSCATED) then
		return nil
	elseif (mapping == Mapping.NUMERIC) then
		return assertReturn(d_maps["numeric"])
	elseif (mapping == Mapping.DESCRIPTIVE) then
		return assertReturn(d_maps["descriptive"])
	elseif (mapping == Mapping.NUMERIC_PACKAGED) then
		return assertReturn(d_maps["numericPackaged"])
	elseif (mapping == Mapping.DESCRIPTIVE_PACKAGED) then
		return assertReturn(d_maps["descriptivePackaged"])
	elseif (mapping == Mapping.BUKKIT) then
		return assertReturn(d_maps["bukkit"])
	else
		error("Invalid mapping requested")
	end
end

--[[-------------------------------------------------------------------
  || INTERNAL FUNCTION
  ||
  || Returns a textual name for the provided mapping/inheritance.
--]]-------------------------------------------------------------------

local function getName(mapping)
	if (mapping == Mapping.OBFUSCATED) then
		return "Obfuscated"
	elseif (mapping == Mapping.NUMERIC) then
		return "Numeric"
	elseif (mapping == Mapping.DESCRIPTIVE) then
		return "Descriptive"
	elseif (mapping == Mapping.NUMERIC_PACKAGED) then
		return "Numeric (Packaged)"
	elseif (mapping == Mapping.DESCRIPTIVE_PACKAGED) then
		return "Descriptive (Packaged)"
	elseif (mapping == Mapping.BUKKIT) then
		return "Bukkit"
	elseif (mapping == Inheritance.NMS) then
		return "NMS"
	elseif (mapping == Inheritance.BUKKIT) then
		return "Bukkit"
	else
		return "<error>"
	end
end

--[[-----------------------------------------------------------------------------
  || API FUNCTION
  ||
  || AutoMapper.generate(mappings:table, inheritance:table)
  ||
  || Generates the mappings as specified in the provided tables.
  || See header for information on the table format.
--]]-----------------------------------------------------------------------------
function AutoMapper.generate(mappings, inheritance)
	print("Running AutoMapper...")
	verifySettings()
	
	d_targetMappings = mappings or {}
	d_targetInheritance = inheritance or {}
	
	verifyInput()
	prepareJars()
	
	if (d_forgeVersion) then
		prepareConfDir()
	else
		checkConfDir()
	end
	
	generateBaseMaps()
	
	print("Generating mappings..")
	-- Generate mappings
	local k,v
	for k,v in pairs(d_targetMappings) do
		local input = v[1]
		local output = v[2]
		local file = v[3]
		
		print(string.format("\tGenerating mapping: %s -> %s (%s)", getName(input), getName(output), file))
		
		local map = getBaselineMap(input, output):clone():transform(getTransformer(input), getTransformer(output))	
		
		map:saveToFile(d_outDir .. file)
	end
	
	for k,v in pairs(d_targetInheritance) do
		local inherit = v[1]
		local mapping = v[2]
		local file = v[3]
		
		print(string.format("\tGenerating inheritance: %s with %s mapping (%s)", getName(inherit), getName(mapping), file))
		
		local map = getInheritanceBaselineMap(inherit):clone():transform(getTransformer(mapping))	
		
		map:saveToFile(d_outDir .. file)
	end
	
	print("Mapping generation finished.")
end