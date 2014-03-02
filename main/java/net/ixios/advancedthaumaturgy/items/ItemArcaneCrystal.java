package net.ixios.advancedthaumaturgy.items;

import java.util.List;

import org.bouncycastle.jcajce.provider.symmetric.util.BaseWrapCipher;

import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.crafting.ShapedArcaneRecipe;
import thaumcraft.api.research.ResearchPage;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.config.ConfigResearch;
import cpw.mods.fml.common.registry.GameRegistry;
import net.ixios.advancedthaumaturgy.AdvThaum;
import net.ixios.advancedthaumaturgy.misc.ATResearchItem;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemArcaneCrystal extends Item
{

	public enum Upgrades
	{
		None(0),
		Recharge(1),
		CompoundDrain(2),
		MultiplyDrain(4),
		Stabilizer(8),
		Discount(16),
		Potent(32);
			
		private int flag;
		Upgrades(int flag)
		{
			this.flag = flag;
		}
		public int getFlag()
		{
			return flag;
		}
		public static Upgrades parse(int flag)
		{
			for (Upgrades u : Upgrades.values())
				if (flag == u.getFlag())
					return u;
			return None;
		}
	}
	
	public ItemArcaneCrystal(int id)
    {
	    super(id);
	    setHasSubtypes(true);
	    setUnlocalizedName("at.arcanecrystal");
    }

	public void register()
	{
		GameRegistry.registerItem(this, "upgradecrystal");
		setCreativeTab(AdvThaum.tabAdvThaum);
		
		 ATResearchItem ri = new ATResearchItem("ARCANECRYSTAL", "ADVTHAUM",
					new AspectList().add(Aspect.CRYSTAL, 10).add(Aspect.MAGIC, 10).add(Aspect.ORDER, 10),
					0, 5, 5,
					new ItemStack(this, 1, 0));
					 
		 ri.setTitle("at.research.arcanecrystal.title");
		 ri.setInfo("at.research.arcanecrystal.desc");
		 
		 ri.setParents("MERCURIALWAND");
		 
		 ri.setPages(new ResearchPage("at.research.arcanecrystal.pg1"), new ResearchPage("at.research.arcanecrystal.pg2"));
		 
		 ri.setStub();
		 ri.setConcealed();
		 
		 ri.registerResearchItem();
		 
		 // these are wand upgrade researches
		 
		 registerRechargeUpgrade();
		 registerDrainMultiplier();
		 registerCompoundDrain();
		 registerVisDiscount();
		 registerStabilizer();
		 registerPotency();
	}
	
	@Override
	public void getSubItems(int par1, CreativeTabs tab, List list)
	{
	    list.add(new ItemStack(this, 1, 0));
	    list.add(new ItemStack(this, 1, 1));
	    list.add(new ItemStack(this, 1, 2)); // compound
	    list.add(new ItemStack(this, 1, 3)); // multiplier
	    list.add(new ItemStack(this, 1, 4)); // discount
	    list.add(new ItemStack(this, 1, 5));  // stabilizer
	    list.add(new ItemStack(this, 1, 6)); // potency
	    
	    list.add(new ItemStack(this, 1, 20)); // flux dissipator
	    list.add(new ItemStack(this, 1, 21)); // watchful
	    list.add(new ItemStack(this, 1, 22)); // healing
	    list.add(new ItemStack(this, 1, 23)); // lightning
	    list.add(new ItemStack(this, 1, 24)); // fire
	    list.add(new ItemStack(this, 1, 25)); // frost
	    
	}
	
	@Override
	public void registerIcons(IconRegister ir)
	{
	    itemIcon = ir.registerIcon("advthaum:wandupgrade");
	}
	
	@Override
	public int getColorFromItemStack(ItemStack stack, int par2)
	{
	    switch (stack.getItemDamage())
	    {
	    	case 0:
	    		return 0xFFFFFFFF;
	    	case 1:
	    		return 0xFFFF0000;
	    	case 2:
	    		return 0xFF00FFFF;
	    	case 3:
	    		return 0xFF0000FF;
	    	case 4:
	    		return 0xFFFF00FF;
	    	case 5:
	    		return 0xFF00FFFF;
	    	case 6:
	    		return 0xFF00FF00;
	    		
	    	case 20: // flux dissipator crystal
	    		return 0xFF800080;
	    	case 21: // watchful crystal
	    		return 0xFF0000FF;
	    	case 22: // heal
	    		return Aspect.PLANT.getColor();
	    	case 23: // lightning
	    		return Aspect.AIR.getColor();
	    	case 24: // fire
	    		return Aspect.FIRE.getColor();
	    	case 25: // frost
	    		return Aspect.ICE.getColor();
	    	default:
	    		return 0xFFFFFFFF;
	    }
	}
	
	@Override
	public String getItemDisplayName(ItemStack stack)
	{
	    return StatCollector.translateToLocal("item.at.arcanecrystal." + stack.getItemDamage() + ".name");
	}
	
	private void registerRechargeUpgrade()
	{
		InfusionRecipe recipe = ThaumcraftApi.addInfusionCraftingRecipe("UPGRADERECHARGE", new ItemStack(this, 1, 1), 5,
				new AspectList().add(Aspect.EARTH, 32).add(Aspect.FIRE,  32).add(Aspect.AIR, 32).add(Aspect.WATER, 32).add(Aspect.ORDER, 32).add(Aspect.ENTROPY, 32).add(Aspect.EXCHANGE, 32),
				new ItemStack(this, 1, 0), new ItemStack[] { ConfigItems.WAND_ROD_BLAZE.getItem(), ConfigItems.WAND_ROD_BONE.getItem(),
															 ConfigItems.WAND_ROD_ICE.getItem(), ConfigItems.WAND_ROD_OBSIDIAN.getItem(),
															 ConfigItems.WAND_ROD_QUARTZ.getItem(), ConfigItems.WAND_ROD_REED.getItem() });
	
		 NBTTagInt tag = new NBTTagInt("upgrade", Upgrades.Recharge.getFlag());
		 InfusionRecipe upgrade = ThaumcraftApi.addInfusionCraftingRecipe("UPGRADERECHARGE", tag, 4, 
				 new AspectList().add(Aspect.MAGIC, 1), ItemMercurialWand.AnyWand, 
				 new ItemStack[] { new ItemStack(this, 1, 1) });
		 ConfigResearch.recipes.put("WANDUPGRADEREHARGE", recipe);
		 
		ATResearchItem ri = new ATResearchItem("UPGRADERECHARGE", "ADVTHAUM",
					new AspectList().add(Aspect.AIR, 20).add(Aspect.FIRE, 20).add(Aspect.WATER, 20).add(Aspect.EARTH, 20).add(Aspect.ORDER, 20).add(Aspect.ENTROPY, 20),
					-2, 3, 5,
					new ItemStack(this, 1, 1));
					 
		 ri.setTitle("item.at.arcanecrystal.1.name");
		 ri.setInfo("at.research.rechargeupgrade.desc");
		 
		 ri.setPages(new ResearchPage("at.research.rechargeupgrade.pg1"), new ResearchPage(recipe), new ResearchPage(upgrade));
		 
		 ri.setParents("ARCANECRYSTAL", "INFUSION", "ROD_reed", "ROD_blaze", "ROD_obsidian", "ROD_ice", "ROD_quartz", "ROD_bone");
		 		 
		 ri.setStub();
		 ri.setSecondary();
		 ri.setConcealed();
		 
		 ri.registerResearchItem();
		
	}
	
	private void registerDrainMultiplier()
	{
		InfusionRecipe recipe = ThaumcraftApi.addInfusionCraftingRecipe("UPGRADEDRAIN", new ItemStack(this, 1, 3), 5,
				new AspectList().add(Aspect.SLIME, 16).add(Aspect.EXCHANGE,  16),
				new ItemStack(this, 1, 0), new ItemStack[] { TCItems.arcanefurance, TCItems.arcanefurance, TCItems.arcanefurance, TCItems.arcanefurance });
		
		NBTTagInt tag = new NBTTagInt("upgrade", Upgrades.MultiplyDrain.getFlag());
		InfusionRecipe upgrade = ThaumcraftApi.addInfusionCraftingRecipe("UPGRADEDRAIN", tag, 4, 
				 new AspectList().add(Aspect.MAGIC, 1), ItemMercurialWand.AnyWand, 
				 new ItemStack[] { new ItemStack(this, 1, 3) });
		 ConfigResearch.recipes.put("WANDUPGRADEDRAIN", recipe);
		 
		 // drain multiplier
		ATResearchItem ri = new ATResearchItem("UPGRADEDRAIN", "ADVTHAUM",
					recipe.aspects,
					-2, 7, 5,
					new ItemStack(this, 1, 3));
					 
		 ri.setTitle("item.at.arcanecrystal.3.name");
		 ri.setInfo("at.research.upgradedrain.desc");
		 
		 ri.setParents("ARCANECRYSTAL", "INFUSION", "DISTILESSENTIA");
		 
		 ri.setPages(new ResearchPage("at.research.upgradedrain.pg1"), new ResearchPage(recipe), new ResearchPage(upgrade));
		 
		 ri.setStub();
		 ri.setSecondary();
		 ri.setConcealed();
		 
		 ri.registerResearchItem();
		 
	}

	private void registerCompoundDrain()
	{
		// compound drainer
		ItemStack blaze = new ItemStack(Item.blazePowder);
		ItemStack tear = new ItemStack(Item.ghastTear);
		ItemStack cream = new ItemStack(Item.magmaCream);
		ItemStack poisonpotato = new ItemStack(Item.poisonousPotato);
		
		ItemStack potato = new ItemStack(Item.potato);
		ItemStack eye = new ItemStack(Item.spiderEye);
		
		ItemStack pearl = new ItemStack(Item.enderPearl);
		
		GameRegistry.addRecipe(new ItemStack(Item.poisonousPotato), new Object[] { "EEE", "EPE", "EEE", 'E', eye, 'P', potato });
		
		InfusionRecipe recipe = ThaumcraftApi.addInfusionCraftingRecipe("UPGRADECOMPOUND", new ItemStack(this, 1, 2), 5,
				new AspectList().add(Aspect.ARMOR, 10).add(Aspect.DEATH,  10).add(Aspect.SEED, 10).add(Aspect.ICE, 10).add(Aspect.SLIME, 10).add(Aspect.SLIME, 10),
				new ItemStack(this, 1, 0), new ItemStack[] { blaze, pearl, tear, pearl, cream, pearl, poisonpotato, pearl });
		
		 NBTTagInt tag = new NBTTagInt("upgrade", Upgrades.CompoundDrain.getFlag());
		 InfusionRecipe upgrade = ThaumcraftApi.addInfusionCraftingRecipe("UPGRADECOMPOUND", tag, 4, 
				 new AspectList().add(Aspect.MAGIC, 1), ItemMercurialWand.AnyWand, 
				 new ItemStack[] { new ItemStack(this, 1, 2) });
		 
		 ConfigResearch.recipes.put("WANDUPGRADECOMPOUND", recipe);
		 
		 
		ATResearchItem ri = new ATResearchItem("UPGRADECOMPOUND", "ADVTHAUM",
					new AspectList().add(Aspect.BEAST, 32).add(Aspect.DARKNESS,  32).add(Aspect.CLOTH, 16).add(Aspect.FLESH, 16).add(Aspect.POISON, 8).add(Aspect.SLIME, 8),
					-3, 5, 5,
					new ItemStack(this, 1, 2));
					 
		 ri.setTitle("item.at.arcanecrystal.2.name");
		 ri.setInfo("at.research.upgradecompound.desc");
		 
		 ri.setParents("ARCANECRYSTAL", "INFUSION");
		 
		 ri.setPages(new ResearchPage("at.research.upgradecompound.pg1"), new ResearchPage(recipe), new ResearchPage(upgrade));
		 
		 ri.setStub();
		 ri.setSecondary();
		 ri.setConcealed();
		 
		 ri.registerResearchItem();
		
	}

	private void registerVisDiscount()
	{
		InfusionRecipe recipe = ThaumcraftApi.addInfusionCraftingRecipe("UPGRADEDISCOUNT", new ItemStack(this, 1, 4), 5,
				new AspectList().add(Aspect.MAGIC, 10).add(Aspect.ELDRITCH,  10).add(Aspect.AURA, 10).add(Aspect.ENERGY, 10).add(Aspect.ENTROPY, 10),
				new ItemStack(this, 1, 0), new ItemStack [] { TCItems.cloth, TCItems.aircluster, TCItems.cloth, TCItems.watercluster,
															  TCItems.cloth, TCItems.firecluster, TCItems.cloth, TCItems.earthcluster,
															  TCItems.cloth, TCItems.ordocluster, TCItems.cloth, TCItems.entropycluster });
		
		 NBTTagInt tag = new NBTTagInt("upgrade", Upgrades.Discount.getFlag());
		 InfusionRecipe upgrade = ThaumcraftApi.addInfusionCraftingRecipe("UPGRADEDISCOUNT", tag, 4, 
				 new AspectList().add(Aspect.MAGIC, 1), ItemMercurialWand.AnyWand, 
				 new ItemStack[] { new ItemStack(this, 1, 4) });
		 
		 ConfigResearch.recipes.put("WANDUPGRADEDISCOUNT", recipe);
		 
		ATResearchItem ri = new ATResearchItem("UPGRADEDISCOUNT", "ADVTHAUM",
					recipe.aspects,
					2, 3, 5,
					new ItemStack(this, 1, 4));
					 
		ri.setTitle("item.at.arcanecrystal.4.name");
		ri.setInfo("at.research.upgradediscount.desc");
		
		ri.setParents("ARCANECRYSTAL", "INFUSION", "ENCHFABRIC");
		
		ri.setPages(new ResearchPage("at.research.upgradediscount.pg1"), new ResearchPage(recipe), new ResearchPage(upgrade));
		
		ri.setStub();
		ri.setSecondary();
		ri.setConcealed();
		
		ri.registerResearchItem();
		 
	}

	private void registerStabilizer()
	{
		InfusionRecipe recipe = ThaumcraftApi.addInfusionCraftingRecipe("UPGRADESTABILIZER", new ItemStack(this, 1, 5), 5,
				new AspectList().add(Aspect.ORDER, 128).add(Aspect.MAGIC,  64),
				new ItemStack(this, 1, 0), new ItemStack [] { TCItems.ordocluster, TCItems.ordocluster, TCItems.ordocluster, TCItems.ordocluster,
															  TCItems.ordocluster, TCItems.ordocluster, TCItems.ordocluster, TCItems.ordocluster});
	
		 NBTTagInt tag = new NBTTagInt("upgrade", Upgrades.Stabilizer.getFlag());
		 InfusionRecipe upgrade = ThaumcraftApi.addInfusionCraftingRecipe("UPGRADESTABILIZER", tag, 4, 
				 new AspectList().add(Aspect.MAGIC, 1), ItemMercurialWand.AnyWand, 
				 new ItemStack[] { new ItemStack(this, 1, 5) });
		 
		 ConfigResearch.recipes.put("WANDUPGRADESTABILIZER", recipe);
		 
		ATResearchItem ri = new ATResearchItem("UPGRADESTABILIZER", "ADVTHAUM",
					new AspectList().add(Aspect.ORDER, 16).add(Aspect.MAGIC, 16),
					3, 5, 5,
					new ItemStack(this, 1, 5));
					 
		 ri.setTitle("item.at.arcanecrystal.5.name");
		 ri.setInfo("at.research.upgradestabilizer.desc");
		 
		 ri.setParents("ARCANECRYSTAL", "INFUSION");
		 
		 ri.setPages(new ResearchPage("at.research.upgradestabilizer.pg1"), new ResearchPage(recipe), new ResearchPage(upgrade));
		 
		 ri.setStub();
		 ri.setSecondary();
		 ri.setConcealed();
		 
		 ri.registerResearchItem();
		 
	}
	
	private void registerPotency()
	{
		InfusionRecipe recipe = ThaumcraftApi.addInfusionCraftingRecipe("UPGRADEPOTENCY", new ItemStack(this, 1, 6), 5,
				new AspectList().add(Aspect.ORDER, 128).add(Aspect.MAGIC,  64),
				new ItemStack(this, 1, 0), new ItemStack [] { TCItems.ordocluster, TCItems.ordocluster, TCItems.ordocluster, TCItems.ordocluster });
	
		 NBTTagInt tag = new NBTTagInt("upgrade", Upgrades.Potent.getFlag());
		 InfusionRecipe upgrade = ThaumcraftApi.addInfusionCraftingRecipe("UPGRADEPOTENCY", tag, 4, 
				 new AspectList().add(Aspect.MAGIC, 1), ItemMercurialWand.AnyWand, 
				 new ItemStack[] { new ItemStack(this, 1, 6) });
		 
		 ConfigResearch.recipes.put("WANDUPGRADEPOTENCY", recipe);
		 
		ATResearchItem ri = new ATResearchItem("UPGRADEPOTENCY", "ADVTHAUM",
					new AspectList().add(Aspect.ENERGY, 32),
					2, 7, 5,
					new ItemStack(this, 1, 6));
					 
		 ri.setTitle("item.at.arcanecrystal.6.name");
		 ri.setInfo("at.research.upgradepotency.desc");
		 
		 ri.setParents("ARCANECRYSTAL", "INFUSION");
		 
		 ri.setPages(new ResearchPage("at.research.upgradepotency.pg1"), new ResearchPage(recipe), new ResearchPage(upgrade));
		 
		 ri.setStub();
		 ri.setSecondary();
		 ri.setConcealed();
		 
		 ri.registerResearchItem();
		 
	}
}
