package net.ixios.advancedthaumaturgy.blocks;

import net.ixios.advancedthaumaturgy.AdvThaum;
import net.ixios.advancedthaumaturgy.tileentities.TilePlaceholder;
import net.ixios.advancedthaumaturgy.tileentities.TileVulcanizer;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockThaumicVulcanizer extends BlockContainer
{
	
	public final int renderID;
	public static int blockID;
	
	public BlockThaumicVulcanizer(int id, Material material)
	{
		super(id, material);
		blockID = id;
		this.setCreativeTab(AdvThaum.tabAdvThaum);
		this.setUnlocalizedName("at.vulcanizer");
	    renderID = RenderingRegistry.getNextAvailableRenderId();
	}

	public void register()
	{
		GameRegistry.registerBlock(this, "blockThaumicVulcanizer");
		GameRegistry.registerTileEntity(TileVulcanizer.class, "tileVulcanizer");
		
        
		 /*ATResearchItem ri = new ATResearchItem("NODEMODIFIERRESEARCH", "BASICS",
					(new AspectList()).add(Aspect.AURA, 1).add(Aspect.SENSES, 1).add(Aspect.MAGIC, 1),
					-5, 6, 2,
					new ItemStack(this));
			ri.setTitle("at.research.nodemodifier.title");
			ri.setInfo("at.research.nodemodifier.desc");
			ri.setParents("NODEJAR");
			ri.setPages(new ResearchPage("at.research.nodemodifier.pg1"),
					new ResearchPage("at.research.nodemodifier.pg2"), new ResearchPage("Recipe pending")
					new ResearchPage(recipe));
			ri.setConcealed();
			ri.setSpecial();
			
			ri.registerResearchItem();*/
			
	}

	@Override
    public TileEntity createTileEntity(World world, int metadata)
	{
		return new TileVulcanizer();
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IconRegister ir)
	{
		blockIcon = ir.registerIcon("advancedthaumaturgy:node_modifier");
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
      
    // this is a 3x3 multiblock so we need to check and make sure the area is okay to place it in
    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack)
    {
    	super.onBlockPlacedBy(world, x, y, z, entity, stack);
    	
    	for (int xc = x - 1; xc <= x + 1; xc++)
    	{
    		for (int zc = z - 1; zc <= z + 1; zc++)
    		{
    			int id = world.getBlockId(xc, y, zc);
    			if (id != 0 && id != BlockThaumicVulcanizer.blockID)
    				return;
    		}
    	}
    	
    	for (int xc = x - 1; xc <= x + 1; xc++)
    	{
    		for (int zc = z - 1; zc <= z + 1; zc++)
    		{
    			// set all but the middle block to 'fake' air
    			if (!(xc == x && zc == z))
    			{
    				world.setBlock(xc, y, zc, BlockPlaceholder.blockID);
    				TilePlaceholder te = new TilePlaceholder(x, y, z);
    				world.setBlockTileEntity(xc, y, zc, te);
    			}
    		}
    	}
    }
    
    @Override
    public void onBlockPreDestroy(World world, int x, int y, int z, int dunno) 
    {
    	// disable all block break events for the surrounding placeholders to prevent stackoverflow
    	for (int xc = x - 1; xc <= x + 1; xc++)
    	{
    		for (int zc = z - 1; zc <= z + 1; zc++)
    		{
    			if (!(xc == x && zc == z))
    			{
    				TilePlaceholder te = (TilePlaceholder)world.getBlockTileEntity(xc, y, zc);
	    			te.disableBroadcast();
    			}
    		}
    	}
    	
    	// now go through and destroy our fake air blocks
    	for (int xc = x - 1; xc <= x + 1; xc++)
    	{
    		for (int zc = z - 1; zc <= z + 1; zc++)
    		{
    			if (!(xc == x && zc == z))
    				world.setBlockToAir(xc,  y,  zc);
    		}
    	}
    	super.onBlockPreDestroy(world, x, y, z, dunno);
    }

	@Override
	public TileEntity createNewTileEntity(World world) {
		// TODO Auto-generated method stub
		return null;
	}
}
	