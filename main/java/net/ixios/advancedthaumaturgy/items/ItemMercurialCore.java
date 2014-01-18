package net.ixios.advancedthaumaturgy.items;

import java.util.List;

import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.research.ResearchPage;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.config.ConfigResearch;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.ixios.advancedthaumaturgy.AdvThaum;
import net.ixios.advancedthaumaturgy.misc.ATResearchItem;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.util.StatCollector;

public class ItemMercurialCore extends Item
{

	public ItemMercurialCore(int id)
	{
		super(id);
		setCreativeTab(AdvThaum.tabAdvThaum);
		setUnlocalizedName("at.mercurialrod");
	}

	public void register()
	{
		GameRegistry.registerItem(this, "MercurialRod");
		 
		ItemStack quicksilver = new ItemStack(ConfigItems.itemResource, 1, 3);
        ItemStack silverwoodrod = ConfigItems.WAND_ROD_SILVERWOOD.getItem();
        
		// add recipe
        InfusionRecipe recipe = ThaumcraftApi.addInfusionCraftingRecipe("MERCURIALCORE", new ItemStack(this), 5,
	 			(new AspectList()).add(Aspect.METAL, 256).add(Aspect.FIRE, 256).add(Aspect.MAGIC, 256).add(Aspect.TREE, 256),
	 			new ItemStack(Item.netherStar),
	 			new ItemStack[] { quicksilver, silverwoodrod, quicksilver, silverwoodrod,
	 			quicksilver, silverwoodrod, quicksilver, silverwoodrod });
	 	
	 	
	 	ConfigResearch.recipes.put("MercurialCore", recipe);
	
		// add research
		 ATResearchItem ri = new ATResearchItem("MERCURIALCORE", "THAUMATURGY",
				(new AspectList()).add(Aspect.METAL, 1).add(Aspect.SENSES, 1).add(Aspect.POISON, 1).add(Aspect.TREE, 1),
				0, 5, 2,
				new ItemStack(this));
		ri.setTitle("at.research.mercurialcore.title");
		ri.setInfo("at.research.mercurialcore.desc");
		ri.setParents("ROD_silverwood");
		ri.setPages(new ResearchPage("at.research.mercurialcore.pg1"),
				new ResearchPage("at.research.mercurialcore.pg2"),
				new ResearchPage(recipe));
		
		ri.setConcealed();
		
		ri.registerResearchItem();

		
		AspectList list = new AspectList();
        list.add(Aspect.WATER, 10);
        list.add(Aspect.AIR, 10);
        list.add(Aspect.FIRE, 10);
        list.add(Aspect.EARTH, 10);
        list.add(Aspect.METAL, 10);
        list.add(Aspect.ORDER, 10);
        list.add(Aspect.ENTROPY, 10);
        list.add(Aspect.CRYSTAL, 10);
        list.add(Aspect.TREE, 10);
        
        ThaumcraftApi.registerObjectTag(this.itemID, -1, list);
        
	}
	
	@Override
	public EnumRarity getRarity(ItemStack stack)
	{
		return EnumRarity.rare;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister icon)
	{
		itemIcon = icon.registerIcon("advthaum:wand_rod_quicksilver");	
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public Icon getIcon(ItemStack stack, int pass) 
	{	
		return itemIcon;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void addInformation(ItemStack stack,	EntityPlayer player, List list, boolean par4)
	{
		String desc = StatCollector.translateToLocal("item.at.mercurialrod.desc");
		String[] lines = desc.split("\\|");
		for (String line : lines)
			list.add(line);
	}
}
