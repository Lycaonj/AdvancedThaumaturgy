package net.ixios.advancedthaumaturgy.items;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.item.Item;

public class ItemInfusedThaumium extends Item
{

	public ItemInfusedThaumium(int id)
	{
		super(id);
	}

	public void register()
	{
		GameRegistry.registerItem(this, "InfusedThaumium");
		LanguageRegistry.addName(this, "Infused Thaumium");
		
		/*ItemStack quicksilver = new ItemStack(ConfigItems.itemResource, 1, 3);
		ItemStack thaumiumblock = new ItemStack(ConfigBlocks.blockCosmeticSolid.blockID, 1, 4);*/
	
		
		// add recipe
        /*InfusionRecipe recipe = ThaumcraftApi.addInfusionCraftingRecipe("INFUSEDTHAUMIUMRESEARCH", new ItemStack(this), 3,
	 			(new AspectList()).add(Aspect.METAL, 64).add(Aspect.FIRE, 64).add(Aspect.MAGIC, 64),
	 			thaumiumblock,
	 			new ItemStack[] { quicksilver, quicksilver, quicksilver, quicksilver,
	 			quicksilver, quicksilver, quicksilver, quicksilver });
	 	
	 	
	 	ConfigResearch.recipes.put("InfusedThaumium", recipe);
	
		// add research
		 ATResearchItem ri = new ATResearchItem("INFUSEDTHAUMIUMRESEARCH", "THAUMATURGY",
				(new AspectList()).add(Aspect.METAL, 1).add(Aspect.MAGIC, 1),
				-0, 5, 2,
				new ItemStack(this));
		ri.setTitle("Infused Thaumium");
		ri.setInfo("?");
		ri.setParents("THAUMIUM");
		ri.setPages(new ResearchPage("While useful, plain thaumium just wasn't enough to create a wand cap worthy of " + 
				"some of your more advanced work.  By infusing quicksilver into a regular block of thaumium, you have " +
				"created a metal strong enough to enforce your creations, but magical enough to be useful to one such as yourself.\n\n" + 
				"\"This just won't do!  Thaumium simply disintegrates the moment I attempt to enhcnace it with the amount of " +
				"Praecantatio required to make it even remotely useful in my experiements.  Thankfully, the impressive Silverwood has " +
				"inadvertently provided me with the perfect solution, in the form of those remarkable flowers that grow exclusively " +
				"beneath it's spanning branches.  The quicksilver flower yields the perfect material to infuse into thaumium, " +
				"strengthing the metal to a point where it is is the perfect ingredient for much of my experiements.\""),
				new ResearchPage(recipe));

		ri.setConcealed();
		
		ri.registerResearchItem();*/
		
	}
	
}
