package net.ixios.advancedthaumaturgy.items;

import cpw.mods.fml.common.registry.GameRegistry;
import net.ixios.advancedthaumaturgy.AdvThaum;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemEndstoneChunk extends Item
{

	public ItemEndstoneChunk(int id)
    {
	    super(id);
	    setUnlocalizedName("at.endstonechunk");
    }

	public void register()
	{
		GameRegistry.registerItem(this, "endstonechunk");
		setCreativeTab(AdvThaum.tabAdvThaum);
		
		ItemStack endstone = new ItemStack(Block.whiteStone);
		
		GameRegistry.addRecipe(new ItemStack(this, 4, 0), new Object[] 
				{ "ESE", "SCS", "ESE", 'S', endstone, 'C', TCItems.anyshard, 'E', Item.emerald });
		
		GameRegistry.addSmelting(this.itemID, new ItemStack(AdvThaum.ArcaneCrystal, 1, 0), 0);
		
	}
}
