package net.ixios.advancedthaumaturgy.blocks;

import java.util.List;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
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
import net.ixios.advancedthaumaturgy.misc.ATResearchItem;
import net.ixios.advancedthaumaturgy.tileentities.TileEtherealJar;
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
    	ItemStack silverwoodplank = new ItemStack(ConfigBlocks.blockWoodenDevice, 1 , 7);
    	
   	 	ShapedArcaneRecipe recipe = ThaumcraftApi.addArcaneCraftingRecipe("ETHEREALJAR", new ItemStack(AdvThaum.itemEtherealJar, 1, 0),
             (new AspectList()).add(Aspect.AIR, 25).add(Aspect.ENTROPY, 25),
             new Object[] { "JCJ",
   	 		  	            "CPC",
   	 			 		    "JCJ", 'C', crystal, 'J', new ItemStack(ConfigBlocks.blockJar, 1, 0), 'P', silverwoodplank });
     
     ConfigResearch.recipes.put("EtherealJar", recipe);
 
     // add research
      ATResearchItem ri = new ATResearchItem("ETHEREALJAR", "BASICS",
    		  (new AspectList()).add(Aspect.AIR, 50).add(Aspect.ENTROPY, 50),
             -10, 2, 2,
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
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
    {
    	TileEntity te = world.getBlockTileEntity(x,  y,  z);
    	if (te == null || !(te instanceof TileEtherealJar))
    		return false;
    		
    	TileEtherealJar jar = (TileEtherealJar)te;
    	ItemStack helditem = player.getHeldItem();
    	
    	if (helditem == null || !(helditem.getItem() instanceof ItemEssence))
    		return false;
    	
    	ItemEssence phial = (ItemEssence)helditem.getItem();
    	Aspect aspect = null;
    	
    	Aspect[] aspects = phial.getAspects(helditem).getAspects();
    		
    	if (aspects.length > 0)
    		aspect = aspects[0];
    	
    	int amount = phial.getAspects(helditem).getAmount(aspect);
    	
    	if (helditem != null && jar.amount <= (jar.maxAmount - 8) && (jar.aspect == null || (jar.aspect != null && phial.getAspects(helditem).getAmount(jar.aspect) >= 8)))
    	{
    		player.getHeldItem().stackSize--;
    		jar.addToContainer(aspect, amount);
      
    		if (world.isRemote)
    			world.playSoundAtEntity(player, "liquid.swim", 0.25F, 1.0F);
    	}
    	else if (helditem != null && helditem.getItem() instanceof ItemEssence && jar.amount >= 8 && aspect == null)
    	{
    		phial.setAspects(helditem, new AspectList().add(aspect, 8));
    		jar.takeFromContainer(aspect, 8);
	 
    		if  (world.isRemote)
    			world.playSoundAtEntity(player, "liquid.swim", 0.25F, 1.0F);
	 
    	}
    	return true;
  
    }
    
    @Override
    public void breakBlock(World world, int x, int y, int z, int arg4, int arg5)
    {
    	TileEntity te = world.getBlockTileEntity(x, y, z);
        TileEtherealJar ej = (TileEtherealJar)te;
         
        ItemStack drop = new ItemStack(AdvThaum.itemEtherealJar);
        NBTTagCompound tag = new NBTTagCompound();
        
        if (ej.amount > 0)
        {
            ((ItemJarFilled)drop.getItem()).setAspects(drop, (new AspectList()).add(ej.aspect, ej.amount));
            if (!drop.hasTagCompound())
                drop.setTagCompound(new NBTTagCompound("tag"));
            if (ej.aspectFilter != null)
                drop.stackTagCompound.setString("filter", ej.aspectFilter.getTag());
        }

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
