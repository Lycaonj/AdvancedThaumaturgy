package net.ixios.advancedthaumaturgy.items;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.ixios.advancedthaumaturgy.AdvThaum;
import net.ixios.advancedthaumaturgy.items.ItemPommel;
import net.ixios.advancedthaumaturgy.misc.ATResearchItem;
import net.ixios.advancedthaumaturgy.misc.Utilities;
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
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.research.ResearchPage;
import thaumcraft.api.wands.WandCap;
import thaumcraft.api.wands.WandRod;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.config.ConfigResearch;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.items.wands.WandManager;
import thaumcraft.common.lib.research.ResearchManager;
import thaumcraft.common.tiles.TileInfusionMatrix;

public class ItemMercurialWand extends ItemWandCasting
{

	private DecimalFormat myFormatter;
    private boolean classictooltip = false;
    
    public ItemMercurialWand(int i)
    {
        super(i);
        myFormatter = new DecimalFormat("#######.##");
        animation = null;
        super.maxStackSize = 1;
        setMaxDamage(0);
        setHasSubtypes(false);
        setCreativeTab(AdvThaum.tabAdvThaum);
        ResourceLocation texture = new ResourceLocation("advthaum:textures/models/wand_rod_mercurial.png");
        getRod(new ItemStack(this)).setTexture(texture);
        getRod(new ItemStack(this)).setGlowing(true);
        setUnlocalizedName("at.mercurialwand");
        
        classictooltip = AdvThaum.config.get("Feature Control", "classic_wand_tooltip", true).getBoolean(true);

        
    }

    public void register()
    {
    	 GameRegistry.registerItem(this, "MercurialWand");
		 	 
		 this.setCap(new ItemStack(this), ConfigItems.WAND_CAP_THAUMIUM);
		 this.setRod(new ItemStack(this), AdvThaum.MercurialRod);
		 
		 ItemStack cap = ConfigItems.WAND_CAP_THAUMIUM.getItem();
		 
		/*/// add recipe
		 Shaped
        InfusionRecipe recipe = ThaumcraftApi.addInfusionCraftingRecipe("MERCURIALWAND", new ItemStack(this), 6,
                (new AspectList()).add(Aspect.METAL, 256).add(Aspect.FIRE, 256).add(Aspect.MAGIC, 256).add(Aspect.TREE, 256).add(Aspect.CRYSTAL, 256),
                new ItemStack(AdvThaum.MercurialCore),
                new ItemStack[] { cap, cap });
        
        
        ConfigResearch.recipes.put("MercurialWand", recipe);*/
    
        // add research
         ATResearchItem ri = new ATResearchItem("MERCURIALWAND", "THAUMATURGY",
                (new AspectList()).add(Aspect.METAL, 1).add(Aspect.SENSES, 1).add(Aspect.POISON, 1).add(Aspect.TREE, 1),
                0, 7, 3,
                new ItemStack(this));
        ri.setTitle("at.research.mercurialwand.title");
        ri.setInfo("at.research.mercurialwand.desc");
        ri.setParents("MERCURIALCORE");
        ri.setPages(new ResearchPage("at.research.mercurialwand.pg1"),
        		new ResearchPage("at.research.mercurialwand.pg2")/*,
                new ResearchPage(recipe)*/);
        
        ri.setSpecial();
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
        
    }
   
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister icon)
    {
        itemIcon = icon.registerIcon("advancedthaumaturgy:wand_rod_mercurial");
        
    }

    public void setPommelType(ItemStack stack, int type)
    {
    	setDamage(stack, type);
    }
    
    public int getPommelType(ItemStack stack)
    {
    	return (this.getDamage(stack));
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
        items.add(w1);
        ItemStack w2 = new ItemStack(this, 1, 0);
        ((ItemWandCasting)w2.getItem()).addVis(w2, Aspect.EARTH, 500, true);
        ((ItemWandCasting)w2.getItem()).addVis(w2, Aspect.FIRE, 500, true);
        ((ItemWandCasting)w2.getItem()).addVis(w2, Aspect.AIR, 500, true);
        ((ItemWandCasting)w2.getItem()).addVis(w2, Aspect.WATER, 500, true);
        ((ItemWandCasting)w2.getItem()).addVis(w2, Aspect.ORDER, 500, true);
        ((ItemWandCasting)w2.getItem()).addVis(w2, Aspect.ENTROPY, 500, true);
        
        items.add(w2);
        
        items.add(new ItemStack(this, 1, 1));
        items.add(new ItemStack(this, 1, 2));
        items.add(new ItemStack(this, 1, 3));
        items.add(new ItemStack(this, 1, 4));
        items.add(new ItemStack(this, 1, 5));
        
    }
    
    @Override
    public EnumRarity getRarity(ItemStack itemstack)
    {
        return EnumRarity.epic;
    }

    @Override
    public String getItemDisplayName(ItemStack is)
    {
        return StatCollector.translateToLocal("item.at.mercurialwand.name") + (getPommelType(is) != ItemPommel.POMMEL_PLAIN ? " [test]" : "");
    }

    @Override
    public WandRod getRod(ItemStack stack)
    {
        return AdvThaum.MercurialRod;
    }

    @Override
    public WandCap getCap(ItemStack stack)
    {
        return ConfigItems.WAND_CAP_THAUMIUM;
    }

    @Override
    public int addVis(ItemStack stack, Aspect aspect, int amount, boolean doit)
    {
    	int amt = amount;
    	
        if (getPommelType(stack) == ItemPommel.POMMEL_MULTIPLIER)
        	amt *= 2;
        
        if (!aspect.isPrimal() && getPommelType(stack) == ItemPommel.POMMEL_COMPOUND)
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
    	if (getPommelType(stack) == ItemPommel.POMMEL_DISCOUNT)
    		discount = 0.2f;
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
    	float discount = WandManager.getTotalVisDiscount(player);
    	boolean shiftdown = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
    	WandCap cap = getCap(stack);
    	
    	discount += (1 - (cap.getBaseCostModifier()));
    	
    	if (classictooltip || shiftdown)
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
    	
    	if (getPommelType(stack) != 0)
    		list.add("Pommel: " + ItemPommel.getName(stack));
    }
    
    @Override
    public void onUpdate(ItemStack stack, World world, Entity player, int invslot, boolean held)
    {
    	if (world.isRemote)
    		return;
    	
    	if (getPommelType(stack) != ItemPommel.POMMEL_RECHARGE)
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
    	if (getPommelType(stack) != ItemPommel.POMMEL_COMPOUND)
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
		
    	if (getPommelType(stack) == ItemPommel.POMMEL_STABILIZER)
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
    
}
