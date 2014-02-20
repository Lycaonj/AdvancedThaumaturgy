	package net.ixios.advancedthaumaturgy.blocks;

import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.research.ResearchPage;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.config.ConfigResearch;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.ixios.advancedthaumaturgy.AdvThaum;
import net.ixios.advancedthaumaturgy.items.ItemFertilizer;
import net.ixios.advancedthaumaturgy.misc.ATResearchItem;
import net.ixios.advancedthaumaturgy.tileentities.TileThaumicFertilizer;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockThaumicFertilizer extends BlockContainer
{

	public final int renderID;
	public static int blockID;
	
    public BlockThaumicFertilizer(int id, Material material)
    {
        super(id, material);
        blockID = id;
        this.setCreativeTab(AdvThaum.tabAdvThaum);
        this.setUnlocalizedName("at.fertilizer");
        this.setHardness(1.0f);
        renderID = RenderingRegistry.getNextAvailableRenderId();
    }

    public void register()
    {
        GameRegistry.registerBlock(this, ItemFertilizer.class, "blockThaumicFertilizer");
        GameRegistry.registerTileEntity(TileThaumicFertilizer.class, "tileentityThaumicFertilizer");
  
        
        ItemStack watershard = new ItemStack(ConfigItems.itemShard, 1, 2);
        ItemStack jar = new ItemStack(ConfigBlocks.blockJar, 1, 0);
		ItemStack water = new ItemStack(Item.bucketWater);
					
		 InfusionRecipe recipe = ThaumcraftApi.addInfusionCraftingRecipe("FERTILIZER", new ItemStack(this), 2,
	                (new AspectList()).add(Aspect.WATER, 16).add(Aspect.AIR, 16).add(Aspect.MAGIC, 16).add(Aspect.METAL, 16),
	                jar,
	                new ItemStack[] { watershard, water, watershard, water });
	        
	        
	        ConfigResearch.recipes.put("Fertilizer", recipe);
	        
	        
	        
        // to the right od research table
        ATResearchItem ri = new ATResearchItem("FERTILIZER", "ARTIFICE",
				(new AspectList()).add(Aspect.PLANT, 1).add(Aspect.WATER, 1).add(Aspect.AIR, 1).add(Aspect.CROP, 1),
				5, 3, 3,
				new ItemStack(this));
		ri.setTitle("at.research.fertilizer.title");
		ri.setInfo("at.research.fertilizer.desc");
		ri.setPages(new ResearchPage("at.research.fertilizer.pg1"), new ResearchPage(recipe));

		ri.setConcealed();
		
		//ri.registerResearchItem();
    }
    
    @Override
    public TileEntity createNewTileEntity(World world)
    {
        return null;
    }
    
    @Override
    public TileEntity createTileEntity(World world, int metadata)
    {
        return new TileThaumicFertilizer();
    }
  
	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IconRegister ir)
	{
		blockIcon = ir.registerIcon("advthaum:thaumic_fertilizer");
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
    public int getRenderType()
    {
    	return renderID;
    }
    
}
	
