package net.ixios.advancedthaumaturgy.misc;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.research.ResearchItem;

public class ATResearchItem extends ResearchItem
{

	private String title = "";
	private String info = "";

	public ATResearchItem(String key, String tab, AspectList tags, int column, int row, int difficulty , ItemStack icon)
	{
		super(key, tab, tags, column, row, difficulty, icon);
	}

	@Override
	public String getName() 
	{
		return (category == "ADVTHAUM" ? "" : "[AT] ") + StatCollector.translateToLocal(this.title);
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
	
