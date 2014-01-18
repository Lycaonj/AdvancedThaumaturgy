package net.ixios.advancedthaumaturgy.blocks;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import net.ixios.advancedthaumaturgy.AdvThaum;
import net.ixios.advancedthaumaturgy.tileentities.TileThaumicFertilizer;
import net.ixios.advancedthaumaturgy.tileentities.TileFluxDissipator;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class BlockFluxDissipator extends BlockContainer
{

	public static int blockID;
	public static int renderID;
	
    public BlockFluxDissipator(int id, Material material)
    {
        super(id, material);
        renderID = RenderingRegistry.getNextAvailableRenderId();
        blockID = id;
        this.setCreativeTab(AdvThaum.tabAdvThaum);
        this.setUnlocalizedName("at.ventilator");
    }

    
    public void register()
    {
        GameRegistry.registerBlock(this, "blockFluxDissipator");
        GameRegistry.registerTileEntity(TileThaumicFertilizer.class, "tileentityFluxDissipator");
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
	
     @Override
     public int getRenderType()
     {
    	 return renderID;
     }
     
     
	 @Override
	 public TileEntity createTileEntity(World world, int metadata)
	 {
	 	return new TileFluxDissipator();
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
	 public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack)
	 {
		 ForgeDirection direction  = ForgeDirection.UNKNOWN;
		 TileFluxDissipator te = (TileFluxDissipator)world.getBlockTileEntity(x,  y,  z);
		 te.setDirection(direction);
		 super.onBlockPlacedBy(world, x, y, z, entity, stack);
	 }
}
