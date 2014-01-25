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
import thaumcraft.common.items.ItemResearchNotes;
import thaumcraft.common.items.ItemResource;
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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

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
    	//list.clear(); //add(new ItemStack(blockid, 1, 0));
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
      ATResearchItem ri = new ATResearchItem("ETHEREALJAR", "ALCHEMY",
    		  (new AspectList()).add(Aspect.AIR, 50).add(Aspect.ENTROPY, 50),
             7, -7, 3,
             new ItemStack(AdvThaum.itemEtherealJar, 1, 0));
     ri.setTitle("at.research.etherealjar.title");
     ri.setInfo("at.research.etherealjar.desc");
     ri.setParents("JARVOID");
     ri.setPages(new ResearchPage("at.research.etherealjar.pg1"), new ResearchPage("at.research.etherealjar.pg2"),
             new ResearchPage(recipe));
     
     ri.setConcealed();
     	        
     ri.registerResearchItem();
     
     
     
    }
    
    @Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
	        float hitY, float hitZ)
	{
    	TileEntity te = world.getBlockTileEntity(x, y, z);
        ItemStack helditem = player.getHeldItem();
        
        if (!(te instanceof TileEtherealJar))
    		return true;
          
    	// world.playSound((double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F), "thaumcraft:jar", 0.2F, 1.0F, false);
    	TileEtherealJar jar = (TileEtherealJar)te;;
    	ItemEssence phial = null;
    	boolean noitem = (helditem == null);
    	
    	if (!noitem && helditem.getItem() instanceof ItemEssence)
    		phial = (ItemEssence)helditem.getItem();
    	
        if (noitem && player.isSneaking() && jar.aspectFilter != null && side == jar.facing)
        {
        	jar.aspectFilter = null;
        	if (world.isRemote) 
        	{
        		world.playSound(x + 0.5F, y + 0.5F, z + 0.5F, "thaumcraft:page", 1.0F, 1.0F, false);
        	}
        	else
        	{
        		ForgeDirection fd = ForgeDirection.getOrientation(side);
        		world.spawnEntityInWorld(new EntityItem(world, x + 0.5F + fd.offsetX / 3.0F, y + 0.5F, z + 0.5F + fd.offsetZ / 3.0F, new ItemStack(ConfigItems.itemResource, 1, 13)));
        	}
        }
        else if (player.isSneaking() && noitem)
        {
        	jar.amount = 0;
        	if (world.isRemote) 
        	{
        		world.playSound(x + 0.5F, y + 0.5F, z + 0.5F, "thaumcraft:jar", 0.4F, 1.0F, false);
        		world.playSound(x + 0.5F, y + 0.5F, z + 0.5F, "liquid.swim", 0.5F, 1.0F + world.rand.nextFloat() - world.rand.nextFloat() * 0.3F, false);
        	}
        }
        else if (!noitem && jar.aspectFilter == null && helditem.itemID == ConfigItems.itemResource.itemID && helditem.getItemDamage() == 13) 
        {
        	if (jar.amount == 0 || jar.aspect == null) 
        		return true;

           jar.aspectFilter = jar.aspect;
           
           --helditem.stackSize;
           this.onBlockPlacedBy(world, x, y, z, player, (ItemStack)null);
           
           if (world.isRemote)
        	   world.playSound(x + 0.5F, y + 0.5F, z + 0.5F, "thaumcraft:jar", 0.4F, 1.0F, false);
           
        }
        else if (helditem != null && (helditem.getItem() instanceof ItemEssence))
    	{
        	AspectList aspects = phial.getAspects(helditem);
        	if (jar.amount >= 8 && jar.aspect != null && aspects == null)
        	{ // empty phial, full jar
        		helditem.stackSize--;
        		ItemStack phialstack = new ItemStack(ConfigItems.itemEssence, 1, 1);
        		((ItemEssence)phialstack.getItem()).setAspects(phialstack, new AspectList().add(jar.aspect, 8));
        		
        		jar.takeFromContainer(jar.aspect, 8);
        		world.markBlockForUpdate(jar.xCoord, jar.yCoord, jar.zCoord);
        		
        		if (!player.inventory.addItemStackToInventory(phialstack))
        			world.spawnEntityInWorld(new EntityItem(world,x + 0.5f, y + 0.5f, z + 0.5f, phialstack));
        		
        		if (world.isRemote)
        			world.playSound(x + 0.5F, y + 0.5F, z + 0.5F, "liquid.swim", 0.5F, 1.0F + world.rand.nextFloat() - world.rand.nextFloat() * 0.3F, false);
        	}
        	else if (phial.getAspects(helditem) != null && jar.amount <= jar.maxAmount - 8)
        	{ // empty jar, full phial
        		if (jar.aspect != null && phial.getAspects(helditem).getAspects()[0] != jar.aspect)
        			return true;
        		
        		helditem.stackSize--;
        		ItemStack emptyphial = new ItemStack(ConfigItems.itemEssence, 1, 0);
        		
        		Aspect aspect = phial.getAspects(helditem).getAspects()[0];
        		jar.addToContainer(aspect, 8);
        		jar.aspect = aspect;
        		
        		world.markBlockForUpdate(jar.xCoord, jar.yCoord, jar.zCoord);
        		
        		if (!player.inventory.addItemStackToInventory(emptyphial))
        			world.spawnEntityInWorld(new EntityItem(world,x + 0.5f, y + 0.5f, z + 0.5f, emptyphial));
        		
        		if (world.isRemote)
        			world.playSound(x + 0.5F, y + 0.5F, z + 0.5F, "liquid.swim", 0.5F, 1.0F + world.rand.nextFloat() - world.rand.nextFloat() * 0.3F, false);
        	}
		}
        return true;
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
    	TileEntity te = world.getBlockTileEntity(x, y, z);
    	
    	if (!(te instanceof TileEtherealJar))
    		return;
    	
    	TileEtherealJar ej = (TileEtherealJar)te; 
    	
    	if (ej == null)
    		ej = new TileEtherealJar();
    	
    	int facing = MathHelper.floor_double((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
		
		if (facing == 0)
			ej.facing = 2;
		else if (facing == 1)
            ej.facing = 5;
		else if (facing == 2)
            ej.facing = 3;
		else if (facing == 3)
            ej.facing = 4;
		
		NBTTagCompound nbt = (stack == null) ? null : stack.getTagCompound();
		
    	if (nbt != null && nbt.hasKey("aspect") && nbt.hasKey("amount"))
    	{
    		ej.amount = nbt.getInteger("amount");
    		ej.aspect = Aspect.getAspect(nbt.getString("aspect"));
    	}
    	
    	if (nbt != null && nbt.hasKey("filter"))
    		ej.aspectFilter = Aspect.getAspect(nbt.getString("filter"));
    		
    	world.setBlockTileEntity(x,  y,  z, ej);
    }
    
}
