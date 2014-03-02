package net.ixios.advancedthaumaturgy.items;

import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategoryList;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.api.research.ResearchPage;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.config.ConfigResearch;
import cpw.mods.fml.common.registry.GameRegistry;
import net.ixios.advancedthaumaturgy.AdvThaum;
import net.ixios.advancedthaumaturgy.misc.ATResearchItem;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.util.ResourceLocation;

public class ItemMercurialRodBase extends Item
{
	
	public ItemMercurialRodBase(int id)
	{
		super(id);
		setUnlocalizedName("at.mercurialrod");
	}
	
	@Override
	public void registerIcons(IconRegister ic)
	{
		itemIcon = ic.registerIcon("advthaum:wand_rod_quicksilver");

	}
	
	@Override
	public Icon getIcon(ItemStack stack, int pass)
	{
	    return itemIcon;
	}
	
	public void register()
	{
		GameRegistry.registerItem(this, "mercurialrod");
		setCreativeTab(AdvThaum.tabAdvThaum);
		
        ItemStack silverwoodrod = ConfigItems.WAND_ROD_SILVERWOOD.getItem();
        
		// add recipe
        InfusionRecipe recipe = ThaumcraftApi.addInfusionCraftingRecipe("MERCURIALROD", new ItemStack(this), 5,
	 			(new AspectList()).add(Aspect.METAL, 256).add(Aspect.FIRE, 256).add(Aspect.MAGIC, 256).add(Aspect.TREE, 256).add(Aspect.CRYSTAL, 256),
	 			new ItemStack(Item.netherStar),
	 			new ItemStack[] { TCItems.quicksilver, silverwoodrod, TCItems.quicksilver, silverwoodrod,
        		TCItems.quicksilver, silverwoodrod, TCItems.quicksilver, silverwoodrod });
	 	
	 	
	 	ConfigResearch.recipes.put("MERCURIALROD", recipe);
	
		// add research
		 ATResearchItem ri = new ATResearchItem("MERCURIALROD", "ADVTHAUM",
				(new AspectList()).add(Aspect.METAL, 1).add(Aspect.CRYSTAL, 1).add(Aspect.POISON, 1).add(Aspect.TREE, 1),
				0, 0, 5,
				new ItemStack(this));
		ri.setTitle("at.research.mercurialcore.title");
		ri.setInfo("at.research.mercurialcore.desc");
		ri.setParents("ROD_silverwood");
		ri.setParentsHidden("INFUSION");
		ri.setSiblings("ROD_mercurial", "MERCURIALWAND");
		ri.setPages(new ResearchPage("at.research.mercurialcore.pg1"),
				new ResearchPage("at.research.mercurialcore.pg2"),
				new ResearchPage(recipe));
		
		ri.registerResearchItem();
		
		ResearchItem r = new ResearchItem("ROD_mercurial", "ADVTHAUM");
		r.setParents("MERCURIALROD");
		r.setStub();
		r.registerResearchItem();
		 
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
}
