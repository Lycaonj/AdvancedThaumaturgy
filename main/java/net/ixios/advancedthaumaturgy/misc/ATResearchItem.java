package net.ixios.advancedthaumaturgy.misc;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.research.ResearchItem;

public class ATResearchItem extends ResearchItem
{

	private String title = "";
	private String info = "";

	public ATResearchItem(String key, String something, AspectList tags, int column, int row, int instability , ItemStack icon)
	{
		super(key, something, tags, column, row, instability, icon);
	}

	@Override
	public String getName() 
	{
		return StatCollector.translateToLocal(this.title);
	}

	@Override
	public String getText() 
	{
		return StatCollector.translateToLocal(this.info);
	}
	
	public void setTitle(String str)
	{
		this.title = str;
	}
	
	public void setInfo(String str)
	{
		this.info = str;
	}
}
	