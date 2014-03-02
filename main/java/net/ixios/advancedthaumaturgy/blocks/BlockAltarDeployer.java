package net.ixios.advancedthaumaturgy.blocks;

import cpw.mods.fml.common.registry.GameRegistry;
import thaumcraft.api.wands.WandTriggerRegistry;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.blocks.BlockStoneDevice;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.lib.research.ResearchManager;
import net.ixios.advancedthaumaturgy.AdvThaum;
import net.ixios.advancedthaumaturgy.items.TCItems;
import net.ixios.advancedthaumaturgy.misc.Utilities;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class BlockAltarDeployer extends Block
{

	public BlockAltarDeployer(int id)
    {
	    super(id, Material.ground);
    }

	public void register()
	{
		GameRegistry.registerBlock(this, "blockAltarDeployer");
		setCreativeTab(AdvThaum.tabAdvThaum);
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
	        float hitY, float hitZ)
	{
		ItemStack helditem = player.getHeldItem();
		
		if (helditem == null || !(helditem.getItem() instanceof ItemWandCasting) || world.isRemote)
			return super.onBlockActivated(world, x, y, z, player, side, hitX, hitY, hitZ); 
	    
		 // center pedestal
		world.setBlock(x,  y,  z, ConfigBlocks.blockStoneDevice.blockID, 1, 3);
		
		// west pedestals
		world.setBlock(x - 4,  y,  z - 1, ConfigBlocks.blockStoneDevice.blockID, 1, 3);
		world.setBlock(x - 4,  y,  z + 1, ConfigBlocks.blockStoneDevice.blockID, 1, 3);
		
		// east pedestals
		world.setBlock(x + 4,  y,  z - 1, ConfigBlocks.blockStoneDevice.blockID, 1, 3);
		world.setBlock(x + 4,  y,  z + 1, ConfigBlocks.blockStoneDevice.blockID, 1, 3);
		
		// south pedestals
		world.setBlock(x - 1,  y,  z + 4, ConfigBlocks.blockStoneDevice.blockID, 1, 3);
		world.setBlock(x + 1,  y,  z + 4, ConfigBlocks.blockStoneDevice.blockID, 1, 3);
		
		// north pedestals
		world.setBlock(x - 1,  y,  z - 4, ConfigBlocks.blockStoneDevice.blockID, 1, 3);
		world.setBlock(x + 1,  y,  z - 4, ConfigBlocks.blockStoneDevice.blockID, 1, 3);
		
		// diagonals
		world.setBlock(x + 3,  y,  z - 3, ConfigBlocks.blockStoneDevice.blockID, 1, 3);
		world.setBlock(x + 3,  y,  z + 3, ConfigBlocks.blockStoneDevice.blockID, 1, 3);
		world.setBlock(x - 3,  y,  z - 3, ConfigBlocks.blockStoneDevice.blockID, 1, 3);
		world.setBlock(x - 3,  y,  z + 3, ConfigBlocks.blockStoneDevice.blockID, 1, 3);
		
		// bricks
		world.setBlock(x + 1,  y,  z - 1, ConfigBlocks.blockCosmeticSolid.blockID, 7, 3);
		world.setBlock(x + 1,  y,  z + 1, ConfigBlocks.blockCosmeticSolid.blockID, 7, 3);
		world.setBlock(x - 1,  y,  z - 1, ConfigBlocks.blockCosmeticSolid.blockID, 7, 3);
		world.setBlock(x - 1,  y,  z +	 1, ConfigBlocks.blockCosmeticSolid.blockID, 7, 3);
		
		// blocks
		world.setBlock(x + 1,  y + 1,  z - 1, ConfigBlocks.blockCosmeticSolid.blockID, 6, 3);
		world.setBlock(x + 1,  y + 1,  z + 1, ConfigBlocks.blockCosmeticSolid.blockID, 6, 3);
		world.setBlock(x - 1,  y + 1,  z - 1, ConfigBlocks.blockCosmeticSolid.blockID, 6, 3);
		world.setBlock(x - 1,  y + 1,  z + 1, ConfigBlocks.blockCosmeticSolid.blockID, 6, 3);
		
		// runic matrix
		world.setBlock(x,  y + 2,  z, ConfigBlocks.blockStoneDevice.blockID, 2, 3);
		
		// candles
		for (int cx = x - 8; cx <= x + 8; cx++)
		{
			for (int cz = z - 8; cz <= z + 8; cz++)
			{
				world.setBlock(cx,  y - 2,  cz, ConfigBlocks.blockCandle.blockID, 0, 3);
			}
		}
		
		boolean hasinfusion = ResearchManager.isResearchComplete(player.username, "INFUSION");
		
		if (!hasinfusion)
			Thaumcraft.proxy.getResearchManager().completeResearch(player, "INFUSION");
		
		boolean result = WandTriggerRegistry.performTrigger(world, helditem, player, x, y, z, side, ConfigBlocks.blockStoneDevice.blockID, 2);
		
		if (!hasinfusion)
			Utilities.removeResearch(player, "INFUSION");
		
		return result;
		
	}
}
