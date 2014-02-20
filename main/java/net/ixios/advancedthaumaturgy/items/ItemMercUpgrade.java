package net.ixios.advancedthaumaturgy.items;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.common.registry.GameRegistry;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.AspectList;
import net.ixios.advancedthaumaturgy.AdvThaum;
import net.ixios.advancedthaumaturgy.misc.ATResearchItem;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.ixios.advancedthaumaturgy.items.ItemMercurialWand;

public class ItemMercUpgrade extends Item
{

	public enum ItemMercUpgrades
	{
		None(0),
		Recharge(1),
		CompoundDrain(2),
		MultiplyDrain(4),
		Stabilizer(8),
		Discount(16);
			
		private int flag;
		ItemMercUpgrades(int flag)
		{
			this.flag = flag;
		}
		public int getFlag()
		{
			return flag;
		}
	}
	
	public ItemMercUpgrade(int id)
	{
		super(id);
		setHasSubtypes(true);
	}

	@Override
	public void getSubItems(int par1, CreativeTabs tab, List list)
	{
	    list.add(new ItemStack(this, 1, 0)); // plain pommel
	    list.add(new ItemStack(this, 1, 1)); // recharge
	    list.add(new ItemStack(this, 1, 2)); // compound draining
	    list.add(new ItemStack(this, 1, 3)); // drain multiplier
	    list.add(new ItemStack(this, 1, 4)); // instability reduction
	    list.add(new ItemStack(this, 1, 5)); // extra vis discount
	}
	
	public void register()
	{
		/*ItemStack wand = new ItemStack(AdvThaum.MercurialWand);
	
		GameRegistry.registerItem(this, "itemPommel");

		ArrayList<ItemStack> list = new ArrayList<ItemStack>();
		getSubItems(0, null, list);
		
		ATResearchItem ri = new ATResearchItem("POMMELS", "ARTIFICE", new AspectList(), 0, 0, 0, new ItemStack(this));
		ri.setVirtual();
		ri.setParentsHidden("MERCURIALWAND");
		ri.setAutoUnlock();*/
		//ri.registerResearchItem();
		
	/*	for (ItemStack stack : list)
		{
			((ItemMercurialWand)wand.getItem()).setPommelType(wand, stack.getItemDamage());
			ThaumcraftApi.addArcaneCraftingRecipe("POMMELS", wand, new AspectList(), 
					"  W", 
					" P ",
					"   ",
					'W', wand, 'P', stack);
		}*/
	}
	
	@Override
	public String getItemDisplayName(ItemStack stack)
	{
		switch (stack.getItemDamage())
		{
			case 0:
				return "Inert Pommel";
			case 1:
				return "Recharge Pommel";
			case 2:
				return "Compound Draining Pommel";
			case 3:
				return "Drain Multiplier Pommel";
			case 4:
				return "Stabilizing Pommel";
			case 5:
				return "Vis Discount Pommel";
			default:
				return "Unknown Pommel (" + stack.getItemDamage() + ")";
		}
	}
	
}
