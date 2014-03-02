package net.ixios.advancedthaumaturgy.items;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.bouncycastle.jcajce.provider.symmetric.util.BaseWrapCipher;
import org.lwjgl.input.Keyboard;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.ixios.advancedthaumaturgy.AdvThaum;
import net.ixios.advancedthaumaturgy.items.ItemArcaneCrystal.Upgrades;
import net.ixios.advancedthaumaturgy.misc.ATResearchItem;
import net.ixios.advancedthaumaturgy.misc.Utilities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.event.terraingen.BiomeEvent.GetWaterColor;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.crafting.ShapedArcaneRecipe;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.api.research.ResearchPage;
import thaumcraft.api.wands.WandCap;
import thaumcraft.api.wands.WandRod;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.config.ConfigResearch;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.items.wands.WandManager;
import thaumcraft.common.lib.ArcaneWandRecipe;
import thaumcraft.common.lib.research.ResearchManager;
import thaumcraft.common.tiles.TileInfusionMatrix;

public class ItemMercurialWand extends ItemWandCasting
{

	private DecimalFormat myFormatter;
    private boolean classictooltip = false;
    private Upgrades upgrades = Upgrades.None;
    
    public static ItemStack BasicWand;
    public static ItemStack AnyWand;
    
    public ItemMercurialWand(int i)
    {
        super(i);
        myFormatter = new DecimalFormat("#######.##");
        animation = null;
        super.maxStackSize = 1;
        setMaxDamage(0);
        setHasSubtypes(false);
        BasicWand = new ItemStack(this, 1, 0);
        
        ((ItemWandCasting)BasicWand.getItem()).setCap(BasicWand, WandCap.caps.get("thaumium"));
        ((ItemWandCasting)BasicWand.getItem()).setRod(BasicWand, WandRod.rods.get("mercurial"));
        
        AnyWand = BasicWand.copy();
        AnyWand.setItemDamage(32767);
        
        classictooltip = AdvThaum.config.get("Feature Control", "classic_wand_tooltip", true).getBoolean(true);
    }

    public void register()
    {
    	 GameRegistry.registerItem(this, "MercurialWand");
    	 setCreativeTab(AdvThaum.tabAdvThaum);
    	 
    	 // add upgrade recipes
		 //ShapedArcaneRecipe recipe = new ShapedArcaneRecipe("merc_, result, aspects, recipe)
    
		 // add research
		 ATResearchItem ri = new ATResearchItem("MERCURIALWAND", "ADVTHAUM",
			new AspectList(),
			0, 2, 5,
			new ItemStack(this));
			 
		 ri.setTitle("at.research.mercurialwand.title");
		 ri.setInfo("at.research.mercurialwand.desc");
		 
		 ri.setParents("MERCURIALROD");
		 
		 ri.setPages(new ResearchPage("at.research.mercurialwand.pg1"), new ResearchPage("at.research.mercurialwand.pg2"));
		 
		 ri.setStub();
		 ri.setRound();
		 ri.setConcealed();
		 ri.registerResearchItem();
	 
		 AspectList list = new AspectList();
		 list.add(Aspect.WATER, 25);
		 list.add(Aspect.AIR, 25);
		 list.add(Aspect.FIRE, 25);
		 list.add(Aspect.EARTH, 25);
		 list.add(Aspect.METAL, 25);
		 list.add(Aspect.ORDER, 25);
		 list.add(Aspect.ENTROPY, 25);
		 list.add(Aspect.CRYSTAL, 25);
		 list.add(Aspect.TREE, 25);
        
        ThaumcraftApi.registerObjectTag(this.itemID, -1, list);
        
        for (String key : WandCap.caps.keySet())
        {
        	WandCap cap = WandCap.caps.get(key);
        	ItemStack wand = new ItemStack(AdvThaum.MercurialWand, 1, 0);
        	ItemWandCasting iwc = ((ItemWandCasting)wand.getItem());
        	WandRod rod = AdvThaum.MercurialRod;
        	iwc.setCap(wand, cap);
        	iwc.setRod(wand, rod);
        	AspectList aspectcost = new AspectList();
        	int cost = cap.getCraftCost() + rod.getCraftCost();
        	for (Aspect aspect : Aspect.getPrimalAspects())
        		aspectcost.add(aspect, cost);
        
        	ShapedArcaneRecipe recipe = ThaumcraftApi.addArcaneCraftingRecipe("MERCURIALWAND", 
        			wand, aspectcost, new Object[] 
        			{ "  C", " R ", "C  ", 'C', cap.getItem(), 'R', new ItemStack(AdvThaum.MercurialRodBase) });
        	ConfigResearch.recipes.put("WAND_" + cap.getTag() + "_" + rod.getTag(), recipe);
        }
        
    }
   
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister icon)
    {
        itemIcon = icon.registerIcon("advthaum:wand_quicksilver");
    }

    @Override
    public Icon getIcon(ItemStack stack, int pass)
    {
        return itemIcon;
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
    public void getSubItems(int par1, CreativeTabs tab, List items)
    {
        ItemStack w1 = new ItemStack(this, 1, 0);
        ((ItemWandCasting)w1.getItem()).setRod(w1, AdvThaum.MercurialRod);
        ((ItemWandCasting)w1.getItem()).setCap(w1, ConfigItems.WAND_CAP_THAUMIUM);
        
        items.add(w1);
        
        ItemStack w2 = new ItemStack(this, 1, 0);
        ((ItemWandCasting)w2.getItem()).setRod(w2, AdvThaum.MercurialRod);
        ((ItemWandCasting)w2.getItem()).setCap(w2, ConfigItems.WAND_CAP_THAUMIUM);
        
        ((ItemWandCasting)w2.getItem()).addVis(w2, Aspect.EARTH, 500, true);
        ((ItemWandCasting)w2.getItem()).addVis(w2, Aspect.FIRE, 500, true);
        ((ItemWandCasting)w2.getItem()).addVis(w2, Aspect.AIR, 500, true);
        ((ItemWandCasting)w2.getItem()).addVis(w2, Aspect.WATER, 500, true);
        ((ItemWandCasting)w2.getItem()).addVis(w2, Aspect.ORDER, 500, true);
        ((ItemWandCasting)w2.getItem()).addVis(w2, Aspect.ENTROPY, 500, true);
        
        items.add(w2);
               
    }
    
    @Override
    public EnumRarity getRarity(ItemStack itemstack)
    {
        return EnumRarity.epic;
    }

    @Override
    public int addVis(ItemStack stack, Aspect aspect, int amount, boolean doit)
    {
    	int amt = amount;
    	
        if ((getDamage(stack) & Upgrades.MultiplyDrain.getFlag()) != 0)
        	amt *= 2;
        
        if (!aspect.isPrimal() && hasUpgrade(stack, Upgrades.CompoundDrain))
        {
        	for (int i = 0; i < amount; i++)
        	{
        		AspectList aspects = ResearchManager.reduceToPrimals((new AspectList()).add(aspect, 1));
        		for (Aspect a : aspects.getAspects())
        			super.addVis(stack, a, aspects.getAmount(a), doit);
        	}	
        	return 0;
        }
        
        int res = super.addVis(stack, aspect, amt, doit);
        return res;
    }
        
    @Override
    public float getConsumptionModifier(ItemStack stack, EntityPlayer player, Aspect aspect)
    {
    	float discount = 0.0f;
    	if (hasUpgrade(stack, Upgrades.Discount))
    		discount = 0.1f;
    	float cost = 1f - discount;
    	float cm = super.getConsumptionModifier(stack, player, aspect);
    	return (super.getConsumptionModifier(stack, player, aspect) - discount);
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean detailed)
    {
    	AspectList vis = this.getAllVis(stack);
    	StringBuilder result = new StringBuilder();
    	result.append("\u00a7r");
    	float discount = WandManager.getTotalVisDiscount(player, null);
    	
    	if (hasUpgrade(stack, Upgrades.Discount))
    		discount += 0.1f;
    	
    	boolean shiftdown = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
    	WandCap cap = getCap(stack);
    	
    	discount += (1 - (cap.getBaseCostModifier()));
    	
    	if (classictooltip && !shiftdown)
    	{
    		super.addInformation(stack, player, list, detailed);
    	}
    	else
    	{
	    	for (Aspect a  : vis.getAspects())
	    	{
	    		int amt = ((int)vis.getAmount(a) / 100);
	    		result.append("\u00a7");
	    		result.append(a.getChatcolor());
	    		result.append(amt);
	    		result.append("\u00a7r | ");
	    	}
	    	result.setLength(result.length() - 2);
	    	
	    	list.add(result.toString());
	    	list.add("Vis discount: " + (int)(discount * 100) + "% total");
    	}
    	
    	String upgrades = "Upgrades: ";
    	
    	if (stack.getItemDamage() == 32767)
    	{
    		// display based on upgrade tag
    		NBTTagCompound tag = stack.stackTagCompound;
    		if (tag != null && tag.hasKey("upgrade"))
    		{
    			int flag = tag.getInteger("upgrade");
    			Upgrades up = Upgrades.parse(flag);
    			list.add("Upgrade: " + up.toString());
    		}
    		return; // this is a stack with wildcard meta, not an actual upgraded wand
    	}
    	
    	for (Upgrades u : Upgrades.values())
    		if (hasUpgrade(stack, u))
    			upgrades += u.toString() + ", ";
    	
    	upgrades = upgrades.substring(0, upgrades.length() - 2);
    	
    	if (upgrades.length() != 8)
    		list.add(upgrades);
    	
    }
    
    @Override
    public void onUpdate(ItemStack stack, World world, Entity player, int invslot, boolean held)
    {
    	if (world.isRemote)
    		return;
    	
    	NBTTagCompound tag = stack.stackTagCompound;
    
    	// check for temp nbt tags added by infusion and migrate them to the item dramage, if needed
    	if (tag != null && tag.hasKey("upgrade"))
    	{
    		int upgrade = tag.getInteger("upgrade");
    		addtUpgrade(stack, Upgrades.parse(upgrade));
    		tag.removeTag("upgrade");
    	}
    	
    	if (!hasUpgrade(stack, Upgrades.Recharge))
    		return;
    	
    	AspectList aspects = this.getAllVis(stack);
    	AspectList temp = aspects.copy();
    	
    	for (int a = 0; a < temp.getAspects().length; a++)
    	{
    		Aspect aspect = temp.getAspects()[a];
    		if (temp.getAmount(aspect) >= ((getMaxVis(stack) * 0.15f) - 1))
    			aspects.remove(aspect);
    	}
    	
    	if (aspects.size() == 0)
    		return;
    	
    	int which = world.rand.nextInt(aspects.size());
        Aspect aspect = aspects.getAspects()[which];
    	
        boolean timetoadd = (player.ticksExisted % 30) == 0;
    	
    	if (!timetoadd)
    		return;
    	
    	if (aspects.getAmount(aspect) < ((getMaxVis(stack) * 0.15f) - 1))
    	{
    		addVis(stack, aspect, 1, true);
    		return;
    	}
    
    }
    
    @Override
    public AspectList getAspectsWithRoom(ItemStack stack)
    {
    	if (!this.hasUpgrade(stack, Upgrades.CompoundDrain))
    		return super.getAspectsWithRoom(stack);
     	
    	AspectList result = new AspectList();
    	ItemMercurialWand wand = (ItemMercurialWand)stack.getItem();
    	AspectList aspects = super.getAspectsWithRoom(stack);
    	
    	// add any compound aspect that can be broken down into this aspect
    	
    	for (Aspect a : Aspect.getCompoundAspects())
    	{
    		AspectList primals = ResearchManager.reduceToPrimals(new AspectList().add(a, 1));
    		for (Aspect tmp : primals.getAspects())
    		{
    			if (aspects.getAmount(tmp) == 1)
    				result.add(a, 1);
    		}
    	}
    	
    	for (Aspect a : Aspect.getPrimalAspects())
    	{
    		if (aspects.getAmount(a) == 1) // for getAspectsWithRoom, 0 = no room and 1 = room
    			result.add(a, 1);
    	}
    	
    	return result;
    	
    }
    
    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
    	
    	TileEntity te = world.getBlockTileEntity(x, y, z);
		if (!(te instanceof TileInfusionMatrix))
			return super.onItemUseFirst(stack, player, world, x, y, z, side, hitX, hitY, hitZ);;
		TileInfusionMatrix im = (TileInfusionMatrix)te;
		
    	if (hasUpgrade(stack, Upgrades.Stabilizer))
    	{
    		if (!im.active)
    		{
    			AdvThaum.proxy.beginMonitoring(im);
    		}
    		else if (im.active && Utilities.isOp(player.username))
    		{
    			int currinstability = ReflectionHelper.getPrivateValue(TileInfusionMatrix.class, im, "instability");
    			player.addChatMessage("[OP Info]: Instability: " + currinstability);
    			return true;
    		}
    	}
    	
    	return super.onItemUseFirst(stack, player, world, x, y, z, side, hitX, hitY, hitZ);
    }
     
    private void removeUpgrade(ItemStack stack, Upgrades upgrade)
    {
    	int dmg = this.getDamage(stack);
    	dmg = (dmg & (~upgrade.getFlag()));
    	this.setDamage(stack, dmg);
    }
    
    private void addtUpgrade(ItemStack stack, Upgrades upgrade)
    {
    	int dmg = this.getDamage(stack);
    	dmg |= upgrade.getFlag();
    	setDamage(stack,  dmg);
    }
    
    private boolean hasUpgrade(ItemStack stack, Upgrades upgrade)
    {
    	int dmg = this.getDamage(stack);
    	return ((dmg & upgrade.getFlag()) != 0);
    }

    @Override
    public int getFocusPotency(ItemStack stack)
    {
        return super.getFocusPotency(stack) + (hasUpgrade(stack, Upgrades.Potent) ? 2 : 0);
        
    }
}
