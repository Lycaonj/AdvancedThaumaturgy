package net.ixios.advancedthaumaturgy.blocks;

import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.research.ResearchPage;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigResearch;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.ixios.advancedthaumaturgy.AdvThaum;
import net.ixios.advancedthaumaturgy.items.ItemEngine;
import net.ixios.advancedthaumaturgy.misc.ATResearchItem;
import net.ixios.advancedthaumaturgy.tileentities.TileEssentiaEngine;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockEssentiaEngine extends Block implements ITileEntityProvider
{

	public static int blockID;
	public static int renderID;
	
	public BlockEssentiaEngine(int id, Material material)
	{
		super(id, material);
		blockID = id;
		setTextureName("obsidian");
		setUnlocalizedName("at.essentiaengine");
		setCreativeTab(AdvThaum.tabAdvThaum);
		renderID = RenderingRegistry.getNextAvailableRenderId();
	}

	@Override
	public TileEntity createNewTileEntity(World world)
	{
		return new TileEssentiaEngine();
	}
	
	public void register()
	{
		GameRegistry.registerBlock(this, ItemEngine.class, "blockEssentiaEngine");
		GameRegistry.registerTileEntity(TileEssentiaEngine.class, "tileEssentiaEngine");
	   
		ItemStack obsidian = new ItemStack(Block.obsidian);
		ItemStack cluster = new ItemStack(ConfigBlocks.blockCrystal, 1, 6);
		
		 InfusionRecipe recipe = ThaumcraftApi.addInfusionCraftingRecipe("ESSENTIAENGINE", new ItemStack(this), 3,
	                (new AspectList()).add(Aspect.STONE, 64).add(Aspect.FIRE, 64).add(Aspect.MAGIC, 64).add(Aspect.TREE, 64),
	                new ItemStack(ConfigBlocks.blockStoneDevice, 1, 0),
	                new ItemStack[] { obsidian, cluster, obsidian, cluster, obsidian, cluster, obsidian, cluster });
	        
	        
	        ConfigResearch.recipes.put("EssentiaEngine", recipe);
	    
	        // add research
	         ATResearchItem ri = new ATResearchItem("ESSENTIAENGINE", "ARTIFICE",
	                (new AspectList()).add(Aspect.STONE, 1).add(Aspect.FIRE, 1).add(Aspect.MAGIC, 1).add(Aspect.TREE, 1),
	                -6, -1, 2,
	                new ItemStack(this));
	        ri.setTitle("at.research.essentiaengine.title");
	        ri.setInfo("at.research.essentiaengine.desc");
	        ri.setParents("INFERNALFURNACE", "INFUSION");
	        ri.setPages(new ResearchPage("at.research.essentiaengine.pg1"),
	                new ResearchPage(recipe));
	        
	        ri.setRound();
	        ri.setConcealed();
	        	        
	        ri.registerResearchItem();
	        
	        
	        
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack)
	{
		super.onBlockPlacedBy(world, x, y, z, entity, stack);
		onNeighborBlockChange(world, x, y, z, z);
	}
	
	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int blockID)
	{
		super.onNeighborBlockChange(world, x, y, z, blockID);
	
		boolean hassignal = false;
		
		if (world.isBlockIndirectlyGettingPowered(x,  y,  z))
			hassignal = true;
		
		TileEssentiaEngine te = (TileEssentiaEngine)world.getBlockTileEntity(x,  y,  z);
		te.setActive(hassignal);
	}
	
	@Override
	public boolean isOpaqueCube() 
	{
		return false;
	}
	
	@Override
	public boolean renderAsNormalBlock() 
	{
		return false;
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
}
