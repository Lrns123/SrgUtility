--[[
**
**	Srg Utility - MCPC+ mapping generator
**
**  Target mappings:
**  - Packaged MCP -> Packaged Numeric (pkgmcp2numpkg.srg)
**  - Packaged MCP -> Obfuscated (pkgmcp2obf.srg)
**  - CraftBukkit -> Packaged Numeric (cb2numpkg.srg)
**
**  Target inheritance:
**  - Bukkit inheritance remapped to Packaged (nms.inheritmap)
**
]]--

--[[---------------
  || Configuration
--]]---------------
local config = {}

-- [Required] Minecraft version to generate mappings for
config.minecraftVersion = "1.6.4"
-- [Required] Forge version to generate mappings for
config.forgeVersion = "9.11.0.908"

-- [Optional] Root directory, used for convenience
config.rootDir = "./"
-- [Required] The directory jars will be downloaded to and confs will be extracted to
config.cacheDir = config.rootDir .. "cache/"
-- [Required] Directory to output all generated mappings to
config.outDir = config.rootDir .. config.minecraftVersion .. ".mcpc/"

-- [Optional] Enable/disable verbose mode
config.verbose = true

--[[------------
  || Entrypoint
--]]------------

function main()
	downloadJars()
	prepareConfDir()

	printf("Generating mappings for MC %s / Forge %s...", config.minecraftVersion, config.forgeVersion)
	
	-- Load base mapping from MCP/FML
	local obfToNum = nil
	
	if (fs.exists(config.mcpDir .. "joined.srg")) then
		verbosePrint("Loading joined.srg...")
		obfToNum = MappingFactory.loadSrg(config.mcpDir .. "joined.srg")
	else
		verbosePrint("Loading client.srg and server.srg...")
		obfToNum = MappingFactory.loadSrg(config.mcpDir .. "client.srg", config.mcpDir .. "server.srg")
	end

	verbosePrint("Loading MCP mappings...")
	-- Load MCP package mapping
	local packageMapping = MappingFactory.loadMCP(nil, nil, config.mcpDir .. "packages.csv")
	
	-- Load full MCP mapping
	local MCPMapping = MappingFactory.loadMCP(config.mcpDir .. "fields.csv", config.mcpDir .. "methods.csv", config.mcpDir .. "packages.csv")
	
	verbosePrint("Transforming mappings...")
	verbosePrint("\tTransforming obfuscated -> numeric to obfuscated -> packged numeric")
	local obfToPkgNum = obfToNum:clone():transform(nil, packageMapping)
	
	verbosePrint("\tTransforming obfuscated -> numeric to packged MCP -> obfuscated")
	local mcpToObf = obfToNum:clone():transform(nil, MCPMapping):reverse()
	
	verbosePrint("\tTransforming packaged MCP -> obfuscated to packaged MCP -> packaged numeric")
	local mcpToPkgNum = mcpToObf:clone():transform(nil, obfToPkgNum)
	
	verbosePrint("\tGenerating obfuscated -> bukkit...")
	local obfToCb = MappingFactory.compareJars(config.serverJar, config.bukkitJar):filter(obfToNum)
	
	verbosePrint("\tTransforming obfuscated -> bukkit to bukkit -> packaged numeric")
	local cbToPkgNum = obfToCb:clone():reverse():transform(nil, obfToPkgNum)
	
	verbosePrint("Generating cb inheritance map...")
	local cbInherit = MappingFactory.makeInheritanceMap(config.bukkitJar, cbToPkgNum):transform(cbToPkgNum)
	
	verbosePrint("Saving mappings...")
	verbosePrint("\tpkgmcp2obf.srg")
	mcpToObf:saveToFile(config.outDir .. "pkgmcp2obf.srg")
	
	verbosePrint("\tpkgmcp2numpkg.srg")
	mcpToPkgNum:saveToFile(config.outDir .. "pkgmcp2numpkg.srg")
	
	verbosePrint("\tcb2numpkg.srg")
	cbToPkgNum:saveToFile(config.outDir .. "cb2numpkg.srg")
	
	verbosePrint("\tnms.inheritmap")
	cbInherit:saveToFile(config.outDir .. "nms.inheritmap")
	
	print("Mapping generation complete.")
end

--[[-------------------
  || Utility Functions
--]]-------------------


function verbosePrint(...)
	if (config.verbose) then
		print(...)
	end
end

function printf(fmt, ...)
	print(string.format(fmt, ...))
end


-- Checks Conf directory to make sure all necessary files exist, extracting them from the forge zip if necessary
function prepareConfDir()
	print("Preparing conf directory...")
	config.mcpDir = string.format("%sconf_%s-%s/", config.cacheDir, config.minecraftVersion, config.forgeVersion)
	fs.makeDir(config.mcpDir)
	local zipFile = zip.open(config.forgeZip)
		
	if (not (fs.exists(config.mcpDir .. "joined.srg") or (fs.exists(config.mcpDir .. "client.srg") and fs.exists(config.mcpDir .. "server.srg")))) then
		local joined = zipFile:getEntry("forge/fml/conf/joined.srg")
		local client = zipFile:getEntry("forge/fml/conf/client.srg")
		local server = zipFile:getEntry("forge/fml/conf/server.srg")
		if (joined ~= nil) then
			zipFile:extract(joined, config.mcpDir .. "joined.srg")
		elseif (client ~= nil and server ~= nil) then
			zipFile:extract(client, config.mcpDir .. "client.srg")
			zipFile:extract(server, config.mcpDir .. "server.srg")
		else
			error("Could not find base srg(s).")
		end
	end
	
	if (not fs.exists(config.mcpDir .. "fields.csv")) then
		local entry = zipFile:getEntry("forge/fml/conf/fields.csv")
		if (entry ~= nil) then
			zipFile:extract(entry, config.mcpDir .. "fields.csv")
		else
			error("Could not find fields.csv.")
		end
	end
	
	if (not fs.exists(config.mcpDir .. "methods.csv")) then
		local entry = zipFile:getEntry("forge/fml/conf/methods.csv")
		if (entry ~= nil) then
			zipFile:extract(entry, config.mcpDir .. "methods.csv")
		else
			error("Could not find methods.csv.")
		end
	end
	
	if (not fs.exists(config.mcpDir .. "packages.csv")) then
		local entry = zipFile:getEntry("forge/fml/conf/packages.csv")
		if (entry ~= nil) then
			zipFile:extract(entry, config.mcpDir .. "packages.csv")
		else
			error("Could not find packages.csv.")
		end
	end
	
	zipFile:close()
end

function downloadJars()
	if (not fs.exists(config.cacheDir)) then
		fs.makeDir(config.cacheDir)
	end

	--config.clientJar = string.format("%sclient_%s.jar", config.cacheDir, config.minecraftVersion)
	config.serverJar = string.format("%sserver_%s.jar", config.cacheDir, config.minecraftVersion)
	config.bukkitJar = string.format("%sbukkit_%s.jar", config.cacheDir, config.minecraftVersion)
	config.forgeZip = string.format("%sforge_%s-%s.zip", config.cacheDir, config.minecraftVersion, config.forgeVersion)
	
	print("Checking jars...")
	--[[
	if (not fs.exists(config.clientJar)) then
		print("\tDownloading minecraft client jar...")
		HTTP.download(string.format("http://s3.amazonaws.com/Minecraft.Download/versions/%s/%s.jar", config.minecraftVersion, config.minecraftVersion), config.clientJar)
	else
		print("\tUsing cached minecraft client jar...")
	end
	]]--
	
	if (not fs.exists(config.serverJar)) then
		print("\tDownloading minecraft server jar...")
		HTTP.download(string.format("http://s3.amazonaws.com/Minecraft.Download/versions/%s/minecraft_server.%s.jar", config.minecraftVersion, config.minecraftVersion), config.serverJar)
	else
		print("\tUsing cached minecraft server jar...")
	end
	
	if (not fs.exists(config.bukkitJar)) then
		print("\tDownloading bukkit server jar...")
		HTTP.download(string.format("http://repo.bukkit.org/content/repositories/releases/org/bukkit/minecraft-server/%s/minecraft-server-%s.jar", config.minecraftVersion, config.minecraftVersion), config.bukkitJar)
	else
		print("\tUsing cached bukkit server jar...")
	end
	
	if (not fs.exists(config.forgeZip)) then
		print("\tDownloading minecraft forge zip...")
		HTTP.download(string.format("http://files.minecraftforge.net/minecraftforge/minecraftforge-src-%s-%s.zip", config.minecraftVersion, config.forgeVersion), config.forgeZip)
	else
		print("\tUsing cached minecraft forge zip...")
	end
end

main()
