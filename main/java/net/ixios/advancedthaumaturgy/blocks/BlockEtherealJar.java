package net.ixios.advancedthaumaturgy.blocks;

import java.util.List;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.crafting.ShapedArcaneRecipe;
import thaumcraft.api.research.ResearchPage;
import thaumcraft.common.blocks.BlockJar;
import thaumcraft.common.blocks.ItemJarFilled;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.config.ConfigResearch;
import thaumcraft.common.items.ItemEssence;
import thaumcraft.common.tiles.TileJarFillable;
import net.ixios.advancedthaumaturgy.AdvThaum;
import net.ixios.advancedthaumaturgy.items.ItemEtherealJar;
import net.ixios.advancedthaumaturgy.items.TCItems;
import net.ixios.advancedthaumaturgy.misc.ATResearchItem;
import net.ixios.advancedthaumaturgy.tileentities.TileEtherealJar;
import net.ixios.advancedthaumaturgy.tileentities.TileFluxDissipator;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockEtherealJar extends BlockJar
{
    
	public static int blockID;
	
    public BlockEtherealJar(int id)
    {
        super(id);
        blockID = id;
        //this.setCreativeTab(AdvThaum.tabAdvThaum);
        this.setUnlocalizedName("at.etherealjar");
    }

    @Override
    public void getSubBlocks(int blockid, CreativeTabs tab, List list) 
    {
    	// don't show in creative menu
    	list.clear(); //add(new ItemStack(blockid, 1, 0));
    }
    
    @Override
    public TileEntity createTileEntity(World world, int metadata)
    {
        return new TileEtherealJar();
    }
    
    public void register()
    {
    	GameRegistry.registerBlock(this, "blockEtherealJar");
    	GameRegistry.registerTileEntity(TileEtherealJar.class, "tileEtherealJar");
    	
    	// do research
    	ItemStack crystal = new ItemStack(ConfigBlocks.blockCrystal, 1, 32767);
    	ItemStack silverwood = new ItemStack(ConfigBlocks.blockMagicalLog, 1 , 1);
    	ItemStack quicksilver = TCItems.quicksilver;
    	
   	 	ShapedArcaneRecipe recipe = ThaumcraftApi.addArcaneCraftingRecipe("ETHEREALJAR", new ItemStack(AdvThaum.itemEtherealJar, 1, 0),
             (new AspectList()).add(Aspect.AIR, 25).add(Aspect.ENTROPY, 25),
             new Object[] { "SSS",
   	 		  	            "JQJ",
   	 			 		    "JCJ", 'C', crystal, 'J', new ItemStack(ConfigBlocks.blockJar, 1, 0), 'S', silverwood,
   	 			 		    		'Q', quicksilver });
     
     ConfigResearch.recipes.put("EtherealJar", recipe);
 
     // add research
      ATResearchItem ri = new ATResearchItem("ETHEREALJAR", "BASICS",
    		  (new AspectList()).add(Aspect.AIR, 50).add(Aspect.ENTROPY, 50),
             -10, 2, 3,
             new ItemStack(AdvThaum.itemEtherealJar, 1, 0));
     ri.setTitle("at.research.etherealjar.title");
     ri.setInfo("at.research.etherealjar.desc");
     ri.setParents("NODEJAR");
     ri.setPages(new ResearchPage("at.research.etherealjar.pg1"),
             new ResearchPage(recipe));
     
     ri.setConcealed();
     	        
     ri.registerResearchItem();
     
     
     
    }
    
    @Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
	        float hitY, float hitZ)
	{
	    ItemStack helditem = player.getHeldItem();
	    TileEntity te = world.getBlockTileEntity(x,  y,  z);
	    
	    if (!(helditem.getItem() instanceof IEssentiaContainerItem))
	    	return false;
	 
	    if (!(te instanceof TileEtherealJar))
	    	return false;
	 
	    IEssentiaContainerItem container = (IEssentiaContainerItem)helditem.getItem();
	    
	    TileEtherealJar jar = (TileEtherealJar)te;
	 
	    if (helditem.getItemDamage() == 0)
	    {
	    	Aspect aspect = jar.getAspects().getAspects()[0];
	    	
	         if (jar.doesContainerContainAmount(aspect, 8)) 
	         {
	            if (world.isRemote) 
	            {
	               player.swingItem();
	               return false;
	            }

	            if (jar.takeFromContainer(aspect, 8))
	            {
	               --helditem.stackSize;
	               ItemStack stack = new ItemStack(ConfigItems.itemEssence, 1, 1);
	               ((ItemEssence)container).setAspects(stack, (new AspectList()).add(Aspect.TAINT, 8));
	               if (!player.inventory.addItemStackToInventory(stack)) 
	               {
	                  world.spawnEntityInWorld(new EntityItem(world, (double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F), stack));
	               }

	               world.playSoundAtEntity(player, "liquid.swim", 0.25F, 1.0F);
	               player.inventoryContainer.detectAndSendChanges();
	               return true;
	            }
	         }
	    }
	    else
	    {
	    	Aspect aspect = container.getAspects(helditem).getAspects()[0];
	    	
	         if (jar.getAspects().size() == 0 || jar.doesContainerContainAmount(aspect, 1)) 
	         {
	            if (world.isRemote) 
	            {
	               player.swingItem();
	               return false;
	            }

	            if (jar.addToContainer(aspect, 8) == 0)
	            {
	               --helditem.stackSize;
	               ItemStack stack = new ItemStack(ConfigItems.itemEssence, 1, 0);
	               ((ItemEssence)helditem.getItem()).setAspects(stack, (new AspectList()).add(Aspect.TAINT, 8));
	               if (!player.inventory.addItemStackToInventory(stack)) 
	               {
	                  world.spawnEntityInWorld(new EntityItem(world, (double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F), stack));
	               }

	               world.playSoundAtEntity(player, "liquid.swim", 0.25F, 1.0F);
	               player.inventoryContainer.detectAndSendChanges();
	               return true;
	            }
	         }
	    }
	    
	    return false;
	}
    
    @Override
    public void breakBlock(World world, int x, int y, int z, int arg4, int arg5)
    {
    	TileEntity te = world.getBlockTileEntity(x, y, z);
        TileEtherealJar ej = (TileEtherealJar)te;
         
        ItemStack drop = new ItemStack(AdvThaum.itemEtherealJar);
       
        if (ej.amount > 0)
            ((ItemJarFilled)drop.getItem()).setAspects(drop, (new AspectList()).add(ej.aspect, ej.amount));
        if (!drop.hasTagCompound())
            drop.setTagCompound(new NBTTagCompound("tag"));
        if (ej.aspectFilter != null)
            drop.stackTagCompound.setString("filter", ej.aspectFilter.getTag());
        

        dropBlockAsItem_do(world, x, y, z, drop);

        world.removeBlockTileEntity(x, y, z);
         
    }
   
    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack)
    {
    	if (!stack.hasTagCompound())
    		return;
    	
    	NBTTagCompound nbt = stack.getTagCompound();
    	TileEtherealJar ej = new TileEtherealJar();
    	
    	if (nbt.hasKey("aspect") && nbt.hasKey("amount"))
    	{
    		ej.amount = nbt.getInteger("amount");
    		ej.aspect = Aspect.getAspect(nbt.getString("aspect"));
    	}
    	
    	if (nbt.hasKey("filter"))
    		ej.aspectFilter = Aspect.getAspect(nbt.getString("filter"));
    		
    	world.setBlockTileEntity(x,  y,  z, ej);
    }
    
}
