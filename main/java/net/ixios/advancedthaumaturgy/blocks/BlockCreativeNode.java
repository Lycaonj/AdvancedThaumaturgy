package net.ixios.advancedthaumaturgy.blocks;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import net.ixios.advancedthaumaturgy.AdvThaum;
import net.ixios.advancedthaumaturgy.items.ItemCreativeNode;
import net.ixios.advancedthaumaturgy.tileentities.TileCreativeNode;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import thaumcraft.common.blocks.BlockAiry;

public class BlockCreativeNode extends BlockAiry
{
	public static int blockID;
	public static int renderID;
	
	public BlockCreativeNode(int id)
	{
		super(id);
		blockID = id;
		this.setUnlocalizedName("at.creativenode");
		this.setCreativeTab(AdvThaum.tabAdvThaum);
		this.setBlockUnbreakable();
		renderID = RenderingRegistry.getNextAvailableRenderId();
	}

	@Override
	public void registerIcons(IconRegister ir)
	{
		blockIcon = ir.registerIcon("advthaum:node");
	}
	
	@Override
	public Icon getIcon(int par1, int par2) 
	{
		return blockIcon;
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
	public TileEntity createTileEntity(World world, int metadata)
	{
		return new TileCreativeNode();
	}
	
		public void register()
	{
		GameRegistry.registerBlock(this, ItemCreativeNode.class, "blockCreativeNode");
		GameRegistry.registerTileEntity(TileCreativeNode.class, "tileentityCreativeNode");
	}
	
}
