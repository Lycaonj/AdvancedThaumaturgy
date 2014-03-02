package net.ixios.advancedthaumaturgy.blocks;

import java.awt.Color;
import java.util.List;

import com.google.common.collect.SetMultimap;

import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.crafting.ShapedArcaneRecipe;
import thaumcraft.api.research.ResearchPage;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.config.ConfigResearch;
import thaumcraft.common.items.ItemEssence;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mcp.mobius.waila.api.IWailaBlock;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.ixios.advancedthaumaturgy.AdvThaum;
import net.ixios.advancedthaumaturgy.items.ItemMercurialRod;
import net.ixios.advancedthaumaturgy.items.ItemMicrolith;
import net.ixios.advancedthaumaturgy.items.TCItems;
import net.ixios.advancedthaumaturgy.misc.ATResearchItem;
import net.ixios.advancedthaumaturgy.tileentities.TileMicrolithBase;
import net.ixios.advancedthaumaturgy.tileentities.TileNodeModifier;
import net.ixios.advancedthaumaturgy.tileentities.TileThaumicFertilizer;
import net.ixios.advancedthaumaturgy.tileentities.TileFluxDissipator;
import net.ixios.advancedthaumaturgy.tileentities.TileWatchfulMicrolith;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class BlockMicrolith extends BlockContainer implements IWailaBlock
{

	public static int blockID;
	public static int renderID;
	
    public BlockMicrolith(int id, Material material)
    {
        super(id, material);
        renderID = RenderingRegistry.getNextAvailableRenderId();
        blockID = id;
        this.setCreativeTab(AdvThaum.tabAdvThaum);
        this.setHardness(1.0f);
        
    }

    @Override
    public void getSubBlocks(int dunno, CreativeTabs tab, List list)
    {
    	list.add(new ItemStack(this, 1, 1)); // flux dissipator
    	list.add(new ItemStack(this, 1, 2)); // chunk loader
    	//list.add(new ItemStack(this, 1, 3)); // burning
    	list.add(new ItemStack(this, 1, 10)); // excavator
    }
    
	 @Override
	 public TileEntity createTileEntity(World world, int metadata)
	 {
		 switch (metadata)
		 {
		 	case 0:
		 		return null;
		 	case 1:
		 		return new TileFluxDissipator();
		 	case 2:
		 		return new TileWatchfulMicrolith();
		 	//case 3:
		 		//return new TileBurningSentry();
			 default:
				 return null;
		 }
	 }
	 
    public void register()
    {
    	GameRegistry.registerBlock(this, ItemMicrolith.class, "blockMicrolith");
    	
    	GameRegistry.registerTileEntity(TileFluxDissipator.class, "tileFluxDissipator");
    	GameRegistry.registerTileEntity(TileWatchfulMicrolith.class, "tileWatchfulMicrolith");
    	//GameRegistry.registerTileEntity(TileBurningSentry.class, "tileBurningSentry");
    	
        ShapedArcaneRecipe recipe = ThaumcraftApi.addArcaneCraftingRecipe("MINILITHBASE", new ItemStack(this, 1, 0), 
        		new AspectList().add(Aspect.ENTROPY, 250), 
        		new Object[] {" M ",
        			  		  " M ",
        			  		  " T ", 'T', TCItems.tile, 'M', TCItems.totem});
        
        ConfigResearch.recipes.put("MICROLITH", recipe);
        
        ATResearchItem ri = new ATResearchItem("MINILITHBASE", "ADVTHAUM",
        		new AspectList().add(Aspect.TAINT, 16).add(Aspect.ORDER, 8).add(Aspect.MAGIC, 8), 0, 10, 0, new ItemStack(this, 1, 0));
        
        ri.setTitle("at.research.microlith.title");
        ri.setInfo("at.research.microlith.desc");
        ri.setParents("ARCANECRYSTAL");
        ri.setConcealed();
        ri.setPages(new ResearchPage("at.research.microlith.pg1"), new ResearchPage("at.research.microlith.pg2"),
        		new ResearchPage(recipe));
        
       ri.registerResearchItem();
  
       
       // flux dissipator
       addMicrolith("MINILITHFLUX", 20, 1, -5, 12, "microlithflux");
             
       //Healing Microlith
       addMicrolith("MINILITHHEAL", 22, 3, -1, 12, "microlithheal");
       
       //Lightning microlith
       addMicrolith("MINILITHZAP", 23, 4, 1, 12, "microlithlightning");
       
       //Flame Minimith
       addMicrolith("MINILITHFIRE", 24, 5, 3, 12, "microlithfire");
       
       //Icy Microlith - tosses frost at mobs
       addMicrolith("MINILITHFROST", 25, 6, -3, 12, "microlithfrost");
       
       //Gusty Microlith - repels mobs
       
       //Vacuous Microlith - attracts mobs
    
       // maybe do one of those for items?

       //Chilling Microlith - slows mobs in range
       
       //Watchful Microlith - Chunkloader 3x3
      addMicrolith("MINILITHWATCHFUL", 21, 2, 5, 12, "microlithwatch");
      
       //Stormy Microlith - Causes rain when right clicked
       
       //Tropical Microlith - Stops rain when right clicked
       
       //Warming Microlith - removes snow from blocks in a 16x16x16 area
       
       //Calming Microlith - Makes hostile mobs in range neutral / ignore you (see Allomancy mod in modjam)
       

       
       
    }
    
    private void addMicrolith(String research, int crystalmeta, int microlithmeta, int row, int col, String name)
    {
        ItemStack orb = new ItemStack(AdvThaum.ArcaneCrystal, 1, crystalmeta);
        ItemStack base = new ItemStack(this, 1, 0);
        
        //NBTTagCompound tag = new NBTTagCompound("");
        ShapedArcaneRecipe recipe = ThaumcraftApi.addArcaneCraftingRecipe(research, new ItemStack(this, 1, microlithmeta), new AspectList().add(Aspect.ENTROPY,  50),
     		   new Object[] { "   ", " O ", " B ", 'O', orb, 'B', base });
        ConfigResearch.recipes.put(research, recipe);
        ATResearchItem ri = new ATResearchItem(research, "ADVTHAUM", new AspectList(), row, col, 0, orb);
        ri.setTitle("at.microlith." + microlithmeta + ".name");
        ri.setInfo("at." + name + ".desc");
        ri.setParents("MINILITHBASE");
        ri.setPages(new ResearchPage("at.research." + name + ".pg1"), new ResearchPage(recipe));
        ri.setSecondary();
        ri.setConcealed();
        ri.registerResearchItem();
    }
    
	@Override
 	public boolean renderAsNormalBlock() 
 	{
 		return false;
 	}
 	
 	@Override
 	public boolean isOpaqueCube() 
 	{
 		return false;
 	}
 	
     @Override
     public TileEntity createNewTileEntity(World world)
     {
    	 return null;
     }
	
 	@SideOnly(Side.CLIENT)
 	@Override
 	public void registerIcons(IconRegister ir)
 	{
 		blockIcon = ir.registerIcon("minecraft:obsidian");
 	}
 	
     @Override
     public int getRenderType()
     {
    	 return renderID;
     }
	 
	 @Override
	public boolean canRenderInPass(int pass)
	{
		return true;	
	}

	 @Override
	public int getRenderBlockPass()
	{
		return 1;
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
	        float hitY, float hitZ)
	{
		TileEntity te = world.getBlockTileEntity(x, y, z);
		player.addChatMessage("Ze metadata eez: " + world.getBlockMetadata(x, y, z));
		if (te == null)
			return super.onBlockActivated(world, x, y, z, player, side, hitX, hitY, hitZ);
		
		TileMicrolithBase base = (TileMicrolithBase)te;
		
		return base.onBlockActivated(world, x, y, z, player, side, hitX, hitY, hitZ);
	    	
	}

	@Override
	public float getBlockBrightness(IBlockAccess ba, int x, int y, int z)
	{
	    return 12;
	}
	
	@Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor,
            IWailaConfigHandler config)
    {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public List<String> getWailaHead(ItemStack stack, List<String> currenttip, IWailaDataAccessor accessor,
            IWailaConfigHandler config)
    {
		currenttip.clear();
		MovingObjectPosition mop = accessor.getPosition();
		currenttip.add(ItemMicrolith.getName(accessor.getWorld().getBlockMetadata(mop.blockX, mop.blockY, mop.blockZ)));
	    return currenttip;
    }

	@Override
    public List<String> getWailaBody(ItemStack itemStack,
            List<String> currenttip, IWailaDataAccessor accessor,
            IWailaConfigHandler config)
    {
	    return currenttip;
    }

	@Override
    public List<String> getWailaTail(ItemStack itemStack,
            List<String> currenttip, IWailaDataAccessor accessor,
            IWailaConfigHandler config)
    {
	    return currenttip;
    }
	
}
