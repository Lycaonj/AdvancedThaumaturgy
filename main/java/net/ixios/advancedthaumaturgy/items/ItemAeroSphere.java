package net.ixios.advancedthaumaturgy.items;

import java.util.ArrayList;
import java.util.Iterator;

import cpw.mods.fml.common.registry.GameRegistry;
import net.ixios.advancedthaumaturgy.AdvThaum;
import net.ixios.advancedthaumaturgy.blocks.BlockPlaceholder;
import net.ixios.advancedthaumaturgy.misc.Vector3;
import net.ixios.advancedthaumaturgy.misc.Vector3F;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSand;
import net.minecraft.block.BlockStationary;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class ItemAeroSphere extends Item
{

	public ItemAeroSphere(int id)
    {
	    super(id);
	    this.setCreativeTab(AdvThaum.tabAdvThaum);
	    this.setUnlocalizedName("at.aerosphere");
    }
	
	public void register()
	{
		GameRegistry.registerItem(this, "aerosphere");
	}
	
	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int slotid, boolean holding)
	{
	    super.onUpdate(stack, world, entity, slotid, holding);
		
		if (world.isRemote)
			return;
		
	    if (!(entity instanceof EntityPlayer))
	    	return;
	    
	    EntityPlayer player = (EntityPlayer)entity;
	    NBTTagCompound tag = player.getEntityData();
	    
	    // check for blocks in range and if not placeholder, replace with placeholder and store
	    Vector3F plrpos = new Vector3F((float)player.posX, (float)player.posY, (float)player.posZ);
	    
	    for (int cx = (int)plrpos.x - 8; cx <= (int)plrpos.x + 8; cx++)
	    {
	    	for (int cy = (int)plrpos.y - 8; cy <= (int)plrpos.y + 8; cy++)
	    	{
	    		for (int cz = (int)plrpos.z - 8; cz <= (int)plrpos.z + 8; cz++)
	    		{
	    			Vector3 blockpos = new Vector3(cx, cy, cz);
	    			Block block = Block.blocksList[world.getBlockId(blockpos.x, blockpos.y, blockpos.z)];
	    			if (block == null || block.blockID == BlockPlaceholder.blockID)
	    				continue;
	    			if (plrpos.distanceTo(blockpos) <= 6 && world.getBlockMaterial(cx, cy,  cz).isLiquid())
    				{
	    				AdvThaum.proxy.beginMonitoring(player, blockpos, block);			
    				}
	    		}
	    	}
	    }
	    
	  
	    
	}

	
}
