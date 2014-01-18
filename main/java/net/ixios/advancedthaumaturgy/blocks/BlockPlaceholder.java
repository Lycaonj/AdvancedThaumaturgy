package net.ixios.advancedthaumaturgy.blocks;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.ixios.advancedthaumaturgy.tileentities.TilePlaceholder;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockPlaceholder extends Block implements ITileEntityProvider
{

	public static int blockID;
	public final int renderID;
	
	public BlockPlaceholder(int id, Material material)
	{
		super(id, Material.air);
		blockID = id;
		  renderID = RenderingRegistry.getNextAvailableRenderId();
	}

	public void register()
	{
		GameRegistry.registerBlock(this, "blockPlaceholder");
		LanguageRegistry.addName(this, "Block Placeholder");
	}
	
	@Override
	public void onBlockPreDestroy(World world, int x, int y, int z, int something)
	{
		super.onBlockPreDestroy(world, x, y, z, something);
		TilePlaceholder te = (TilePlaceholder)world.getBlockTileEntity(x, y, z);
		if (te.broadcastDestroy())
		{
			te.disableBroadcast();
			world.destroyBlock(te.parentX, te.parentY,  te.parentZ, true);
		}
	}

	@Override
	public TileEntity createNewTileEntity(World world)
	{
		return null;
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
