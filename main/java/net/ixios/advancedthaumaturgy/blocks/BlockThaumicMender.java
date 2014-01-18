package net.ixios.advancedthaumaturgy.blocks;

import net.ixios.advancedthaumaturgy.AdvThaum;
import net.ixios.advancedthaumaturgy.tileentities.TileThaumicMender;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockThaumicMender extends BlockContainer
{

	protected BlockThaumicMender(int id)
	{
		super(id, Material.rock);
		this.setCreativeTab(AdvThaum.tabAdvThaum);
	}

	@Override
	public TileEntity createNewTileEntity(World world) 
	{
		return null;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side,
			float hitX, float hitY, float hitZ)
	{
		if (world.isRemote)
			return true;
		
		TileEntity te = world.getBlockTileEntity(x, y, z);
		
		if (!(te instanceof TileThaumicMender))
			return true;
		
		TileThaumicMender tm = (TileThaumicMender)te;
		
		// model after  TilePedestal
		 if (tm.getStackInSlot(0) != null)
        {
            //give item to player //InventoryHelper.dropItems(world, x, y, z);
            world.markBlockForUpdate(x, y, z);
            world.playSoundEffect(x, y, z, "random.pop", 0.2F, ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.7F + 1.0F) * 1.5F);
            return true;
        }
           
		 if(player.getCurrentEquippedItem() != null)
        {
            ItemStack i = player.getCurrentEquippedItem().copy();
            i.stackSize = 1;
            tm.setInventorySlotContents(0, i);
            player.getCurrentEquippedItem().stackSize--;
            if(player.getCurrentEquippedItem().stackSize == 0)
                player.setCurrentItemOrArmor(0, null);
            player.inventory.onInventoryChanged();
            world.markBlockForUpdate(x, y, z);
            world.playSoundEffect(x, y, z, "random.pop", 0.2F, ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.7F + 1.0F) * 1.6F);
            return true;
        }
	        
		return super.onBlockActivated(world, x, y, z, player, side, hitX, hitY, hitZ);
		
	}

}
