package net.ixios.advancedthaumaturgy.misc;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.common.config.ConfigItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class ATCreativeTab extends CreativeTabs 
{

    public ATCreativeTab(String label)
    {
    	super(label);
    }
    
    @SideOnly(Side.CLIENT)
    public int getTabIconItemIndex()
    {
    	ItemStack quicksilver = new ItemStack(ConfigItems.itemResource, 1, 3);
    	return quicksilver.itemID;
    }
}
