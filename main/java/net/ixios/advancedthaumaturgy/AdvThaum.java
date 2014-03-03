package net.ixios.advancedthaumaturgy;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import javax.print.attribute.standard.MediaSize.Engineering;

import net.ixios.advancedthaumaturgy.blocks.BlockAltarDeployer;
import net.ixios.advancedthaumaturgy.blocks.BlockCreativeNode;
import net.ixios.advancedthaumaturgy.blocks.BlockEssentiaEngine;
import net.ixios.advancedthaumaturgy.blocks.BlockEtherealJar;
import net.ixios.advancedthaumaturgy.blocks.BlockMicrolith;
import net.ixios.advancedthaumaturgy.blocks.BlockNodeModifier;
import net.ixios.advancedthaumaturgy.blocks.BlockPlaceholder;
import net.ixios.advancedthaumaturgy.blocks.BlockThaumicFertilizer;
import net.ixios.advancedthaumaturgy.blocks.BlockThaumicVulcanizer;
import net.ixios.advancedthaumaturgy.compat.energy.EnergyCompatBase;
import net.ixios.advancedthaumaturgy.compat.energy.BCCompatChecker;
import net.ixios.advancedthaumaturgy.items.ItemAeroSphere;
import net.ixios.advancedthaumaturgy.items.ItemArcaneCrystal;
import net.ixios.advancedthaumaturgy.items.ItemEndstoneChunk;
import net.ixios.advancedthaumaturgy.items.ItemEtherealJar;
import net.ixios.advancedthaumaturgy.items.ItemFocusVoidCage;
import net.ixios.advancedthaumaturgy.items.ItemInfusedThaumium;
import net.ixios.advancedthaumaturgy.items.ItemMercurialRod;
import net.ixios.advancedthaumaturgy.items.ItemMercurialRodBase;
import net.ixios.advancedthaumaturgy.items.ItemMercurialWand;
import net.ixios.advancedthaumaturgy.misc.ATCreativeTab;
import net.ixios.advancedthaumaturgy.misc.ATEventHandler;
import net.ixios.advancedthaumaturgy.misc.ATResearchItem;
import net.ixios.advancedthaumaturgy.misc.ATServerCommand;
import net.ixios.advancedthaumaturgy.misc.ArcingDamageManager;
import net.ixios.advancedthaumaturgy.misc.ChunkLoadingClass;
import net.ixios.advancedthaumaturgy.misc.Utilities;
import net.ixios.advancedthaumaturgy.proxies.CommonProxy;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.api.research.ResearchPage;
import thaumcraft.api.wands.WandRod;
import thaumcraft.api.wands.WandTriggerRegistry;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.tiles.TileInfusionMatrix;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.common.registry.EntityRegistry.EntityRegistration;
import cpw.mods.fml.relauncher.Side;

@Mod(modid="AdvancedThaumaturgy", version="0.0.25", name="Advanced Thaumaturgy", 
	dependencies="required-after:Thaumcraft", acceptedMinecraftVersions="1.6.4")
@NetworkMod(clientSideRequired=true, channels={"AdvThaum"}, packetHandler = CommonProxy.class)

public class AdvThaum 
{

	@Instance
	public static AdvThaum instance;
	
	@SidedProxy(clientSide="net.ixios.advancedthaumaturgy.proxies.ClientProxy",
				serverSide="net.ixios.advancedthaumaturgy.proxies.CommonProxy")
	public static CommonProxy proxy;
	
	public static CreativeTabs tabAdvThaum = new ATCreativeTab("advthaum");
	public static Configuration config = null;
	
	// items
	public static ItemMercurialRod MercurialRod;
	public static ItemMercurialRodBase MercurialRodBase;
	public static ItemMercurialWand MercurialWand;
	public static ItemInfusedThaumium InfusedThaumium;
	//public static ItemThaumInkwell ThaumicInkwell;
	
	public static ItemFocusVoidCage FocusVoidCage;
	public static ItemEtherealJar itemEtherealJar;
	public static ItemAeroSphere AeroSphere;
	public static ItemArcaneCrystal ArcaneCrystal;
	public static ItemEndstoneChunk EndstoneChunk;
	
	// blocks
	public static BlockNodeModifier NodeModifier;
	public static BlockThaumicFertilizer ThaumicFertilizer;
	public static BlockCreativeNode CreativeNode;
	//public static ItemCreativeNode CreativeNodeItem;
	public static BlockEssentiaEngine EssentiaEngine;
	public static BlockThaumicVulcanizer ThaumicVulcanizer;
	public static BlockPlaceholder Placeholder;
	public static BlockEtherealJar EtherealJar;
	public static BlockMicrolith Microlith;
	public static BlockAltarDeployer AltarDeployer;
	
	//public static RenderTickManager rendermanager = new RenderTickManager();
	
	private static Logger logger = Logger.getLogger("Advanced Thaumaturgy");
	
	public static boolean debug = false;
	
	 @EventHandler
     public void preInit(FMLPreInitializationEvent event)
	 {
	     logger.setParent(FMLLog.getLogger());
	     
	     NetworkRegistry.instance().registerGuiHandler(this, proxy);

	     config = new Configuration(event.getSuggestedConfigurationFile());
	     
	     config.load();
	     
	     int infusedthaumiumid = config.getItem("ItemIDs", "infusedthaumium", 13335).getInt();
	     //int thaumicwellid = config.getItem("ItemIDs", "thaumicinkwell", 13336).getInt();
	     int pommelid = config.getItem("ItemIDs", "pommel", 13337).getInt();
	     int focusvoidcageid = config.getItem("ItemIDs", "focusvoidcage", 13338).getInt();
	     int itemetherealjar = config.getItem("ItemIDs", "ethereal_jar", 13339).getInt();
	     int aerosphereid = config.getItem("ItemIDs", "aerosphere", 13340).getInt();
	     int wandcrystalid = config.getItem("ItemIDs", "arcanecrystal", 13341).getInt();
	     int endstonechunk = config.getItem("ItemIDs", "endstone", 13342).getInt();;
	     
	     int nodemodifierid = config.getBlock("BlockIDs", "nodemodifier", 3433).getInt();
	     int thaumicfertilizerid = config.getBlock("BlockIDs", "thaumicfertilizer", 3434).getInt();
	     int creativenodeid = config.getBlock("BlockIDs", "creativenode", 3435).getInt();
	     int essentiaengineid = AdvThaum.config.getBlock("BlockIDs", "essentiaengine", 3436).getInt();
	     int vulcanizerid = config.getBlock("BlockIDs", "vulcanizer", 3437).getInt();
	     int placeholderid = config.getBlock("BlockIDs", "placeholder", 3438).getInt();
	     int etherealjarid = config.getBlock("BlockIDs", "etherealjar", 3439).getInt();
	     int fluxdissipatorid = config.getBlock("BlockIDs", "fluxdissipator", 3440).getInt();
	     int altardeployerid = config.getBlock("BlockIDs", "altardeployer", 3441).getInt();;
	     
	     boolean useClassicTooltip = config.get("Feature Control", "classic_wand_tooltip", false).getBoolean(false);
	     
	     ////////////////////////////////////////////////////////
	 	     
	     if (config.get("Feature Control", "enable_altar_deployer", true).getBoolean(true))
	    	 AltarDeployer = new BlockAltarDeployer(altardeployerid);
	     
	     if (config.get("Feature Control", "enable_infused_thaumium", true).getBoolean(true))
	    	 InfusedThaumium = new ItemInfusedThaumium(infusedthaumiumid);
	     
	     if (config.get("Feature Control", "enable_node_modifier", true).getBoolean(true))
	    	 NodeModifier = new BlockNodeModifier(nodemodifierid, Material.ground);
	     
	     if (config.get("Feature Control", "enable_fertilizer", true).getBoolean(true))
	    	 ThaumicFertilizer = new BlockThaumicFertilizer(thaumicfertilizerid, Material.ground);
	     
	     if (config.get("Feature Control", "enable_creative_node", true).getBoolean(true))
	    	 CreativeNode = new BlockCreativeNode(creativenodeid);
	     
	     if (config.get("Feature Control", "enable_vulcanizer", true).getBoolean(true))
	    	 ThaumicVulcanizer = new BlockThaumicVulcanizer(vulcanizerid, Material.ground);
	     
	     if (config.get("Feature Control", "enable_ethereal_jar", true).getBoolean(true))
	     {
	    	 EtherealJar = new BlockEtherealJar(etherealjarid);
	    	 itemEtherealJar = new ItemEtherealJar(itemetherealjar); 
	     }
	     
	     if (config.get("Feature Control", "enable_miniligh", true).getBoolean(true))
	    	 Microlith = new BlockMicrolith(fluxdissipatorid, Material.ground);
	      
	     if (config.get("Feature Control", "enable_focus_void_cage", true).getBoolean(true))
	    	 FocusVoidCage = new ItemFocusVoidCage(focusvoidcageid);
	     
	     if (config.get("Feature Control", "enable_aerosphere", true).getBoolean(true))
	    	 AeroSphere = new ItemAeroSphere(aerosphereid);
	     
	     if (config.get("Feature Control", "enable_wand_upgrades", true).getBoolean(true))
	     {
	    	 ArcaneCrystal = new ItemArcaneCrystal(wandcrystalid);	    
	    	 EndstoneChunk = new ItemEndstoneChunk(endstonechunk);
	     }
	     	
	     if (AdvThaum.config.get("Feature Control", "enable_engine", true).getBoolean(true))
	    	 AdvThaum.EssentiaEngine = new BlockEssentiaEngine(essentiaengineid, Material.rock);
		
	     Placeholder = new BlockPlaceholder(placeholderid, Material.air);
	  
	     // these must be done before proxy.register
		 new BCCompatChecker().register();
	
		 if (config.get("Feature Control", "force_enable_essentia_engine", false).getBoolean(false))
			 EnergyCompatBase.forceEnable();
	
	     proxy.register();
	  
	     LanguageRegistry.instance().addStringLocalization("itemGroup.advthaum", "en_US", "Advanced Thaumaturgy");
	     LanguageRegistry.instance().addStringLocalization("tc.research_category.ADVTHAUM", "en_US", "Advanced Thaumaturgy");
	     
	     MinecraftForge.EVENT_BUS.register(new ATEventHandler());
	     
	     TickRegistry.registerTickHandler(new ArcingDamageManager(), Side.SERVER);
	     
	     ForgeChunkManager.setForcedChunkLoadingCallback(instance, new ChunkLoadingClass());
	    
     }
	
	 private void registerStuff()
	 {
		if (EnergyCompatBase.isPresent())
			AdvThaum.EssentiaEngine.register();
			
		 if (InfusedThaumium != null)
			 InfusedThaumium.register();
		 
		 if (NodeModifier != null)
			 NodeModifier.register();
		 
		 if (ThaumicFertilizer != null)
			 ThaumicFertilizer.register();
		 
		 if (CreativeNode != null)
			 CreativeNode.register();
		 
		 if (EtherealJar != null && itemEtherealJar != null)
			 EtherealJar.register();
		 
		 if (Microlith != null)
			 Microlith.register();
		  
		 if (FocusVoidCage != null)
			 FocusVoidCage.register();
		 
		 if (AeroSphere != null)
			 AeroSphere.register();
		 
		 if (ArcaneCrystal != null)
			 ArcaneCrystal.register();
		 
		 if (EndstoneChunk != null)
			 EndstoneChunk.register();

		 if (AltarDeployer != null)
			 AltarDeployer.register();
		 
	 }
	 
	 public static void log(String text)
	 {
	     logger.info(FMLCommonHandler.instance().getEffectiveSide().toString() + " " + text);
	 }
	 
	 @EventHandler
     public void load(FMLInitializationEvent event) 
     {
		 
     }
    
	 @EventHandler  
     public void postInit(FMLPostInitializationEvent event) 
     {
		 
		 ResearchCategories.registerCategory("ADVTHAUM",
				 new ResourceLocation("thaumcraft", "textures/items/thaumonomiconcheat.png"),
				 new ResourceLocation("thaumcraft", "textures/gui/gui_researchback.png"));
		 	 
	     registerStuff();

	    if (config.get("Feature Control", "enable_mercurial_core", true).getBoolean(true))
	     {
	    	int capacity = 500;
	    	for (WandRod rod : WandRod.rods.values())
	    		capacity = Math.max(capacity, rod.getCapacity());
	    	
	    	int mercurialcoreid = config.getItem("ItemIDs", "mercurialcore", 13333).getInt();
	    	int mercurialwandid = config.getItem("ItemIDs", "mercurialwand", 13334).getInt();
		     
	    	 MercurialRodBase = new ItemMercurialRodBase(mercurialcoreid);
	    	 MercurialRod = new ItemMercurialRod(capacity);
	    	 
	    	 if (config.get("Feature Control", "enable_mercurial_wand", true).getBoolean(true))
	    		 MercurialWand = new ItemMercurialWand(mercurialwandid);
	     }
		    
		 if (MercurialRodBase != null)
			 MercurialRodBase.register();
		
		 if (MercurialWand != null)
			 MercurialWand.register();
		 
		 //ThaumicInkwell.register();
		 //ThaumicVulcanizer.register();
		 
		 // enable activating node in a jar by wanding the top wood slabs
		 WandTriggerRegistry.registerWandBlockTrigger(Thaumcraft.proxy.wandManager, 4, Block.woodSingleSlab.blockID, -1);
		 //WandTriggerRegistry.registerWandBlockTrigger(Thaumcraft.proxy.wandManager, 5, Block.obsidian.blockID, -1);
		 
		 if (config.get("Feature Control", "add_permutatio_to_eggs", true).getBoolean(true))
		 {
			 AspectList list = ThaumcraftApiHelper.getObjectAspects(new ItemStack(Item.egg));
			 if (!list.aspects.containsKey(Aspect.EXCHANGE))
			 {
				list.add(Aspect.EXCHANGE, 1); 
				 ThaumcraftApi.registerObjectTag(Item.egg.itemID, -1, list);
			 }
		 }
		 
		 if (config.get("Feature Control", "add_exanimus_to_bones", true).getBoolean(true))
		 {
			 AspectList list = ThaumcraftApiHelper.getObjectAspects(new ItemStack(Item.bone));
			 if (!list.aspects.containsKey(Aspect.UNDEAD))
			 {
				 list.add(Aspect.UNDEAD, 1);
				 ThaumcraftApi.registerObjectTag(Item.bone.itemID, -1, list);
			 }
		 }
			 
		 config.save();
		 
		 LanguageRegistry.instance().addStringLocalization("tc.research_name.TESTBUILD", "en_US",  "Test Build Notes");
		 ResearchItem ri = new ResearchItem("TESTBUILD", "ADVTHAUM", new AspectList(), 0, -2, 0, new ItemStack(CreativeNode));
		 
		 ri.setAutoUnlock();
		 ri.setRound();
		 
		 ri.setPages(new ResearchPage("This build is for testing only.  You should NOT be using this on a live server / map.  Doing so will likely kill your world save.\nAny Research with an unset localized name (eg at.research.something.name) is likely something I haven't quite finished but it will be in the public release build.\n\n- Lycaon"));
		 
		 ri.registerResearchItem();
		 
     }
	 
	 @EventHandler
	 public void serverLoad(FMLServerStartingEvent event)
	 {
		 event.registerServerCommand(new ATServerCommand());
	 }
	 
	 @EventHandler
	 public void serverStarted(FMLServerStartingEvent event)
	 {
		 proxy.loadData();
	 }
	 
	 @EventHandler 
	 public void serverStopping(FMLServerStoppingEvent event)
	 {
		 proxy.saveData();	
	 }
}

