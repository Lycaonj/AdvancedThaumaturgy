package net.ixios.advancedthaumaturgy.enchants;

import net.ixios.advancedthaumaturgy.AdvThaum;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.item.ItemBook;
import net.minecraft.item.ItemStack;
import thaumcraft.common.items.wands.foci.ItemFocusShock;

public class EnchantArcing extends Enchantment
{

	public static int enchantID;
	
    public EnchantArcing(int id, int something)
    {
        super(id, something, EnumEnchantmentType.all);
        enchantID = id;
        setName("arcing");
    }

    public int getMinEnchantability(int par1)
    {
        return 20 + (par1 - 1) * 10;
    }

    public int getMaxEnchantability(int par1)
    {
        return super.getMinEnchantability(par1) + 50;
    }

    public int getMaxLevel()
    {
        return 5;
    }

    public boolean canApply(ItemStack stack)
    {
    	// can only apply to books and shock foci
    	if (AdvThaum.proxy.getEquippedFocus(stack) instanceof ItemFocusShock)
    		return true;
        return (stack.getItem() instanceof ItemBook);
    }

    public boolean canApplyAtEnchantingTable(ItemStack stack)
    {
    	return canApply(stack);
    }

    public boolean canApplyTogether(Enchantment enchant)
    {
        return super.canApplyTogether(enchant);
    }
}
