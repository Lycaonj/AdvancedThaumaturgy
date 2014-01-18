package net.ixios.advancedthaumaturgy;

import java.util.List;
import java.util.logging.Logger;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import net.ixios.advancedthaumaturgy.blocks.BlockCreativeNode;
import net.ixios.advancedthaumaturgy.blocks.BlockEssentiaEngine;
import net.ixios.advancedthaumaturgy.blocks.BlockEtherealJar;
import net.ixios.advancedthaumaturgy.blocks.BlockFluxDissipator;
import net.ixios.advancedthaumaturgy.blocks.BlockNodeModifier;
import net.ixios.advancedthaumaturgy.blocks.BlockPlaceholder;
import net.ixios.advancedthaumaturgy.blocks.BlockThaumicFertilizer;
import net.ixios.advancedthaumaturgy.blocks.BlockThaumicVulcanizer;
import net.ixios.advancedthaumaturgy.compat.buildcraft.BCCompatRegistrar;
import net.ixios.advancedthaumaturgy.items.ItemEtherealJar;
import net.ixios.advancedthaumaturgy.items.ItemFocusVoidCage;
import net.ixios.advancedthaumaturgy.items.ItemInfusedThaumium;
import net.ixios.advancedthaumaturgy.items.ItemMercurialCore;
import net.ixios.advancedthaumaturgy.items.ItemMercurialWand;
import net.ixios.advancedthaumaturgy.items.ItemPommel;
import net.ixios.advancedthaumaturgy.misc.ATCreativeTab;
import net.ixios.advancedthaumaturgy.misc.ATEventHandler;
import net.ixios.advancedthaumaturgy.misc.ATServerCommand;
import net.ixios.advancedthaumaturgy.misc.ArcingDamageManager;
import net.ixios.advancedthaumaturgy.proxies.CommonProxy;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.wands.WandRod;
import thaumcraft.api.wands.WandTriggerRegistry;
import thaumcraft.common.Thaumcraft;
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
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.common.registry.EntityRegistry.EntityRegistration;
import cpw.mods.fml.relauncher.Side;

@Mod(modid="AdvancedThaumaturgy", version="0.0.17", name="Advanced Thaumaturgy", 
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
	public static ItemMercurialCore MercurialCore;
	public static WandRod MercurialRod;
	public static ItemMercurialWand MercurialWand;
	public static ItemInfusedThaumium InfusedThaumium;
	//public static ItemThaumInkwell ThaumicInkwell;
	public static ItemPommel PommelBase;
	public static ItemFocusVoidCage FocusVoidCage;
	public static ItemEtherealJar itemEtherealJar;
	
	// blocks
	public static BlockNodeModifier NodeModifier;
	public static BlockThaumicFertilizer ThaumicFertilizer;
	public static BlockCreativeNode CreativeNode;
	//public static ItemCreativeNode CreativeNodeItem;
	public static BlockEssentiaEngine EssentiaEngine;
	public static BlockThaumicVulcanizer ThaumicVulcanizer;
	public static BlockPlaceholder Placeholder;
	public static BlockEtherealJar EtherealJar;
	public static BlockFluxDissipator FluxDissipator;
		
	//public static RenderTickManager rendermanager = new RenderTickManager();
	
	private static Logger logger = Logger.getLogger("Advanced Thaumaturgy");
	
	 @EventHandler
     public void preInit(FMLPreInitializationEvent event)
	 {
	     logger.setParent(FMLLog.getLogger());
	     
	     NetworkRegistry.instance().registerGuiHandler(this, proxy);

	     config = new Configuration(event.getSuggestedConfigurationFile());
	     
	     config.load();
	     
	     config.addCustomCategoryComment("ItemIDs", "Set any ID to -1 to disable it in the mod.");
	     config.addCustomCategoryComment("BlockIDs", "Set any ID to -1 to disable it in the mod.");
	     
	     int mercurialcoreid = config.getItem("ItemIDs", "mercurialcore", 13333).getInt();
	     int mercurialwandid = config.getItem("ItemIDs", "mercurialwand", 13334).getInt();
	     int infusedthaumiumid = config.getItem("ItemIDs", "infusedthaumium", 13335).getInt();
	     //int thaumicwellid = config.getItem("ItemIDs", "thaumicinkwell", 13336).getInt();
	     int pommelid = config.getItem("ItemIDs", "pommel", 13337).getInt();
	     int focusvoidcageid = config.getItem("ItemIDs", "focusvoidcage", 13338).getInt();
	     int itemetherealjar = config.getItem("ItemIDs", "ethereal_jar", 13339).getInt();
	     
	     int nodemodifierid = config.getBlock("BlockIDs", "nodemodifier", 3433).getInt();
	     int thaumicfertilizerid = config.getBlock("BlockIDs", "thaumicfertilizer", 3434).getInt();
	     int creativenodeid = config.getBlock("BlockIDs", "creativenode", 3435).getInt();
	     int vulcanizerid = config.getBlock("BlockIDs", "vulcanizer", 3437).getInt();
	     int placeholderid = config.getBlock("BlockIDs", "placeholder", 3438).getInt();
	     int etherealjarid = config.getBlock("BlockIDs", "etherealjar", 3439).getInt();
	     int fluxdissipatorid = config.getBlock("BlockIDs", "fluxdissipator", 3440).getInt();
	     
	     boolean useClassicTooltip = config.get("Feature Control", "classic_wand_tooltip", false).getBoolean(false);
	     
	     ////////////////////////////////////////////////////////
	     
	     if (config.get("Feature Control", "enable_mercurial_core", true).getBoolean(true))
	     {
	    	 MercurialCore = new ItemMercurialCore(mercurialcoreid);
	    	 MercurialRod = new WandRod("mercurial", 500, new ItemStack(MercurialCore), 90);
	    	 
	    	 if (config.get("Feature Control", "enable_mercurial_wand", true).getBoolean(true))
	    		 MercurialWand = new ItemMercurialWand(mercurialwandid);
	     }
	     
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
	     
	     if (config.get("Feature Control", "enable_ventilator", true).getBoolean(true))
	    	 FluxDissipator = new BlockFluxDissipator(fluxdissipatorid, Material.ground);
	     
	     if (config.get("Feature Control", "enable_pommels", true).getBoolean(true))
	    	 PommelBase = new ItemPommel(pommelid);
	     
	     if (config.get("Feature Control", "enable_focus_void_cage", true).getBoolean(true))
	    	 FocusVoidCage = new ItemFocusVoidCage(focusvoidcageid);
	     
	     Placeholder = new BlockPlaceholder(placeholderid, Material.air);
	     
	     //ThaumicInkwell = new ItemThaumInkwell(thaumicwellid);
	     
	     LanguageRegistry.instance().addStringLocalization("itemGroup.advthaum", "en_US", "Advanced Thaumaturgy");
	     

	     
	     MinecraftForge.EVENT_BUS.register(new ATEventHandler());
	     
	     TickRegistry.registerTickHandler(new ArcingDamageManager(), Side.SERVER);
	     
	     proxy.registerAllTheThings();
	 	
     }
	
	 public static void log(String text)
	 {
	     logger.info(FMLCommonHandler.instance().getEffectiveSide().toString() + " " + text);
	 }
	 
	 @SuppressWarnings("unchecked")
	 public static void broadcastMessage(String text)
	 {
		 
	     List<EntityPlayer> players;
	     
	     if (Minecraft.getMinecraft().theWorld.isRemote)
	    	 players = Minecraft.getMinecraft().theWorld.playerEntities;
	     else
	    	 players = MinecraftServer.getServer().worldServers[0].playerEntities;
	     
	     for (int t = 0; t < players.size(); t++)
	     {
	         players.get(t).addChatMessage(text);
	     }
	 }
	 
	 @EventHandler
     public void load(FMLInitializationEvent event) 
     {
		 
     }
    
	 @EventHandler  
     public void postInit(FMLPostInitializationEvent event) 
     {
		 if (MercurialCore != null)
			 MercurialCore.register();
		 
		 if (MercurialWand != null)
			 MercurialWand.register();
		 
		 if (InfusedThaumium != null)
			 InfusedThaumium.register();
		 
		 if (NodeModifier != null)
			 NodeModifier.register();
		 
		 if (ThaumicFertilizer != null)
			 ThaumicFertilizer.register();
		 
		 if (CreativeNode != null)
			 CreativeNode.register();
		 
		 if (EtherealJar != null && itemEtherealJar != null)
		 {
			 EtherealJar.register();
			 //itemEtherealJar.register();
		 }
		 
		 if (FluxDissipator != null)
			 FluxDissipator.register();
		 
		 if (PommelBase != null && MercurialWand != null)
			 PommelBase.register();
		 
		 if (FocusVoidCage != null)
			 FocusVoidCage.register();
			 
		 //ThaumicInkwell.register();
		 
		 //ThaumicVulcanizer.register();
		 
		 // implement bc related blocks if the api is detected
		 new BCCompatRegistrar().register();
		 
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
		 
		// ConfigItems.itemInkwell.setMaxDamage(400);
		 BiMap<Class<? extends Entity>, EntityRegistration> registrations = ObfuscationReflectionHelper.getPrivateValue(EntityRegistry.class, EntityRegistry.instance(), "entityClassRegistrations");
		 
		 for (Class<? extends Entity> e : registrations.keySet())
		 {
			 
		 }
     }
	 
	 @EventHandler
	  public void serverLoad(FMLServerStartingEvent event)
	  {
		 event.registerServerCommand(new ATServerCommand());
	  }
}

