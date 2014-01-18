package net.ixios.advancedthaumaturgy.tileentities;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import cpw.mods.fml.relauncher.ReflectionHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.tiles.TileJarFillable;
import net.ixios.advancedthaumaturgy.AdvThaum;
import net.ixios.advancedthaumaturgy.misc.Vector3F;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockLilyPad;
import net.minecraft.block.BlockMushroom;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.BlockStem;
import net.minecraft.block.BlockVine;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.common.FakePlayer;
import net.minecraftforge.common.ForgeDirection;

public class TileThaumicFertilizer extends TileEntity implements IAspectContainer
{
	
	private static Aspect aspect = Aspect.WATER;
	private float current = 0F;
	private float max = 64.0F;
	private int ticktracker = 0;
	
	private int fertilizechance = 20;
	
    private final float costperhydration = 0.05F;
    private final float costperfertilize = 0.5f;
    
    private ArrayList<Vector3F> blockstomonitor = null;
    private int arrayposition = 0;
    
    public TileThaumicFertilizer()
    {
    	
    }
    
    @Override
    public boolean canUpdate()
    {
        return true;
    }
    
    private void checkArrayList()
    {
    	if (blockstomonitor != null)
    		return;
    	if (worldObj == null)
    		return;
    	
    	blockstomonitor = new ArrayList<Vector3F>();
    	
    	for (int cx = xCoord - 8; cx <= xCoord + 8; cx++)
    	{
    		for (int cz = zCoord - 8; cz <= zCoord + 8; cz++)
    		{
    			blockstomonitor.add(new Vector3F(cx, yCoord - 1, cz));
    		}
    	}
    	
    	Collections.shuffle(blockstomonitor);
    	
    }
    
    @Override
    public void updateEntity()
    {
    	super.updateEntity();
    	
    	if (worldObj == null)
    		return;
    
    	checkArrayList();
    	
    	if (blockstomonitor == null)
    		return;
    	
    	/*AdvThaum.proxy.createCustomParticle(worldObj, xCoord - 8, yCoord, zCoord - 8, 0f, .1f, 0f, 0xFFFF0000);
    	AdvThaum.proxy.createCustomParticle(worldObj, xCoord - 8, yCoord, zCoord + 8, 0f, .1f, 0f, 0xFFFF0000);
    	AdvThaum.proxy.createCustomParticle(worldObj, xCoord + 8, yCoord, zCoord - 8, 0f, .1f, 0f, 0xFFFF0000);
    	AdvThaum.proxy.createCustomParticle(worldObj, xCoord + 8, yCoord, zCoord + 8, 0f, .1f, 0f, 0xFFFF0000);*/
    	
    	if (((int)current < (int)max) && ticktracker % 2 == 0)
    	{
    		TileJarFillable jar = AdvThaum.proxy.findEssentiaJar(worldObj, Aspect.WATER, this, 16, 2, 16);
    		if (jar != null && jar.amount > 0)
			{
    			jar.takeFromContainer(Aspect.WATER, 1);
    			AdvThaum.proxy.createParticle(jar, xCoord, yCoord, zCoord, Aspect.WATER.getColor());
    			current +=1;
    			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			}
    	}
    	
        ticktracker++;
        
    	if (ticktracker < 2)
        	return;
        
        ticktracker = 0;
  
        if ((int)current == 0) // don't scan if we can't afford to do anything
            return;
        
        int triggermeta = 4;
             
        int cx = (int)blockstomonitor.get(arrayposition).x;
        int cy = (int)blockstomonitor.get(arrayposition).y;
        int cz = (int)blockstomonitor.get(arrayposition).z;
        arrayposition++;
        
        if (arrayposition >= blockstomonitor.size())
        	arrayposition = 0;
        
        int baseid = worldObj.getBlockId(cx, cy, cz);
        int basemeta = worldObj.getBlockMetadata(cx, cy, cz);
        int targetid = worldObj.getBlockId(cx, cy + 1, cz);
        int targetmeta = worldObj.getBlockMetadata(cx, cy + 1, cz);
        
        Block block = Block.blocksList[targetid];
            
        // check if the block is farmland and if it needs watered
        if ((baseid == Block.tilledField.blockID))
        {
        	if (basemeta <= triggermeta)
        	{
                int reqtofullyhydrate = 7 - basemeta;
                float costtofullyhydrate = costperhydration * Math.min(reqtofullyhydrate, current);
                
                worldObj.setBlockMetadataWithNotify(cx, cy, cz, 7, 3);
                	
                current -= costtofullyhydrate;
                
                AdvThaum.proxy.createParticle(worldObj, xCoord + 0.5F, yCoord + 1, zCoord + 0.5F, (float)cx + 0.5F, (float)cy + 1, (float)cz + 0.5F, 0xFF00FFFF);
        	}
            
            if (block != null)
        	{
        		if ((targetmeta < 7) && ((block instanceof BlockCrops) || (block instanceof BlockStem)) && (worldObj.rand.nextInt(100) <= fertilizechance))
        			fertilize(cx, cy + 1, cz);
        		else if (targetmeta == 7 && block instanceof BlockStem)
        			growFruitFromStem(cx, cy + 1, cz);
        	}
            
        }
        else if (baseid == Block.dirt.blockID || baseid == Block.tilledField.blockID || baseid == Block.sand.blockID || baseid == Block.grass.blockID) // could be a tree
        {
        	
        	if (block instanceof BlockSapling && worldObj.rand.nextInt(100) < fertilizechance)
        		fertilize(cx, cy + 1, cz);
        	
        	if ((targetid == Block.reed.blockID || targetid == Block.cactus.blockID) && worldObj.rand.nextInt(100) <= fertilizechance)
        	{
        		for (int height = cy; height <= cy + 4; height++)
        		{
        			baseid = worldObj.getBlockId(cx,  height, cz);
        			if (baseid == 0)
        			{
        				worldObj.setBlock(cx,  height, cz, targetid);
        				fertilize(cx, height, cz);
        				break;
        			}
        		}
        	}
        	
        }
        
        if (block instanceof BlockMushroom)
        {
        	if (worldObj.rand.nextInt(100) < fertilizechance)
        		spreadMushroomFrom(cx, cy + 1, cz);
        }
        else if (block instanceof BlockLilyPad && baseid == Block.waterStill.blockID)
        {
        	if (worldObj.rand.nextInt(100) < fertilizechance)
        		spreadLilypadFrom(cx, cy + 1, cz);
        }
       
    	for (cy = yCoord; cy < yCoord + 15; cy++)
        {
    		targetid = worldObj.getBlockId(cx, cy, cz);
    		block = Block.blocksList[targetid];
    		if (block instanceof BlockVine && worldObj.rand.nextInt(100) < fertilizechance)
    		{
    			spreadVineFrom(cx, cy, cz);
    			break;	
    		}
        }
    
        for (cy = yCoord; cy < yCoord + 5; cy++)
        {
        	baseid = worldObj.getBlockId(cx, cy, cz);
        	basemeta = worldObj.getBlockMetadata(cx, cy, cz);
        	int direction = basemeta & 0x03;
        	int stage = (basemeta >> 2) & 0x03;
        	if (baseid == ConfigBlocks.blockManaPod.blockID && stage < 2 && worldObj.rand.nextInt(100) <= fertilizechance)
        	{
        		stage++;
        		worldObj.setBlockMetadataWithNotify(cx, cy, cz, (stage << 2) | direction, 3);
        		fertilize(cx, cy, cz);
        		break;
        	}

        }
        
    }
   
    private void spreadVineFrom(int x, int y, int z)
    {
    	if (current < costperfertilize)
    		return;
    		
    	Block block = Block.blocksList[worldObj.getBlockId(x,  y,  z)];
    	if (!(block instanceof BlockVine))
    		return;
    
    	Block blockbelow = Block.blocksList[worldObj.getBlockId(x,  y - 1,  z)];
    	
    	BlockVine vine = (BlockVine)block;
    	int metadata = worldObj.getBlockMetadata(x,  y,  z);
    	
    	Vector3F target = new Vector3F(x, y - 1, z);
    
    	if (!worldObj.isAirBlock((int)target.x, (int)target.y, (int)target.z))
    		return;
    	
    	Method m = null;
    	boolean result = false;
    	
    	try
    	{
    		m = ReflectionHelper.findMethod(BlockVine.class, vine, new String[] { "canVineStay" }, World.class, int.class, int.class, int.class);
    		if (m == null)
    			return;
    	}
    	catch (Exception ex)
    	{ 
    		AdvThaum.log(ex.toString());
    	}
    	
         if (worldObj.isAirBlock((int)target.x, (int)target.y, (int)target.z))
         {
        	 worldObj.setBlock((int)target.x, (int)target.y, (int)target.z, block.blockID, metadata, 2);
             try
             { 
            	 result = (Boolean) m.invoke(vine, worldObj, (int)target.x, (int)target.y, (int)target.z);
             }
             catch (Exception ex) { }
             if (result)
             {
            	 fertilize((int)target.x, (int)target.y, (int)target.z);
            	 AdvThaum.broadcastMessage("Setting new vine");
             }
         }
    }
    
    private void spreadLilypadFrom(int x, int y, int z)
    {
    	if (current < costperfertilize)
    		return;
    		
    	Block block = Block.blocksList[worldObj.getBlockId(x,  y,  z)];
    	if (!(block instanceof BlockLilyPad))
    		return;
    	
    	int whichdir = worldObj.rand.nextInt(4);

    	Vector3F target = null;
    	
    	switch (whichdir)
    	{
    		case 0:
    		{
    			if (worldObj.getBlockId(x - 1, y, z) == 0)
    				target = new Vector3F(x - 1, y, z);
    		}
    		break;
    		
    		case 1:
			{
				if (worldObj.getBlockId(x + 1, y, z) == 0)
					target = new Vector3F(x + 1, y, z);
			}
	        break;

    		case 2:
    		{
    			if (worldObj.getBlockId(x, y, z - 1) == 0)
    				target = new Vector3F(x, y, z - 1);
    		}
    		break;
    		
    		case 3:
    		{
    			if (worldObj.getBlockId(x, y, z + 1) == 0)
    				target = new Vector3F(x, y, z + 1);
    		}
    		break;
    	}

    	if (target == null)
    		return; // no spot to grow
    	
    	Block undertarget = Block.blocksList[worldObj.getBlockId((int)target.x, (int)target.y - 1, (int)target.z)];
    	
         if (worldObj.isAirBlock((int)target.x, (int)target.y, (int)target.z) && undertarget.blockID == Block.waterStill.blockID)
         {
             worldObj.setBlock((int)target.x, (int)target.y, (int)target.z, block.blockID);
             fertilize((int)target.x, (int)target.y, (int)target.z);
         }
    }
    
    private void growFruitFromStem(int x, int y, int z)
    {
    	if (current < costperfertilize)
    		return;
    		
    	Block block = Block.blocksList[worldObj.getBlockId(x,  y,  z)];
    	if (!(block instanceof BlockStem))
    		return;
    	BlockStem stem = (BlockStem)block;
    	
    	Block fruitType = ReflectionHelper.getPrivateValue(BlockStem.class, stem, "fruitType");
    	int whichdir = worldObj.rand.nextInt(4);

    	Vector3F target = null;
    	
    	if (worldObj.getBlockId(x - 1, y, z) == fruitType.blockID)
    		return;
    	if (worldObj.getBlockId(x + 1, y, z) == fruitType.blockID)
    		return;
    	if (worldObj.getBlockId(x, y, z - 1) == fruitType.blockID)
    		return;
    	if (worldObj.getBlockId(x, y, z + 1) == fruitType.blockID)
    		return;
    	
    	switch (whichdir)
    	{
    		case 0:
    		{
    			if (worldObj.getBlockId(x - 1, y, z) == 0)
    				target = new Vector3F(x - 1, y, z);
    		}
    		break;
    		
    		case 1:
			{
				if (worldObj.getBlockId(x + 1, y, z) == 0)
					target = new Vector3F(x + 1, y, z);
			}
	        break;

    		case 2:
    		{
    			if (worldObj.getBlockId(x, y, z - 1) == 0)
    				target = new Vector3F(x, y, z - 1);
    		}
    		break;
    		
    		case 3:
    		{
    			if (worldObj.getBlockId(x, y, z + 1) == 0)
    				target = new Vector3F(x, y, z + 1);
    		}
    		break;
    	}

    	if (target == null)
    		return; // no spot to grow
    	
         int targetid = worldObj.getBlockId((int)target.x, (int)target.y - 1, (int)target.z);

         boolean isSoil = (Block.blocksList[targetid] != null && Block.blocksList[targetid].canSustainPlant(worldObj, (int)target.x, (int)target.y - 1, (int)target.z, ForgeDirection.UP, stem));
         
         if (worldObj.isAirBlock((int)target.x, (int)target.y, (int)target.z) && (isSoil || targetid == Block.dirt.blockID || targetid == Block.grass.blockID))
         {
             worldObj.setBlock((int)target.x, (int)target.y, (int)target.z, fruitType.blockID);
             fertilize((int)target.x, (int)target.y, (int)target.z);
         }
    }
    
    private void fertilize(int x, int y, int z)
    {
    	if (current > costperfertilize && ItemDye.applyBonemeal(new ItemStack(Item.dyePowder, 1, 0), worldObj, x, y, z, new FakePlayer(worldObj, "advthaumPlayer")))
    	{
    		AdvThaum.proxy.createParticle(worldObj, xCoord + 0.5f, yCoord + 0.7f, zCoord + 0.5f, x, y, z, Aspect.SLIME.getColor());
    		AdvThaum.proxy.createSparkleBurst(worldObj, x + 0.5F, y + 0.5F, z + 0.5F, 8, 0xFF00FF00);
    		current -= costperfertilize;
    	}
    }
    
    private void spreadMushroomFrom(int x, int y, int z)
    {
    	if (current <= costperfertilize)
    		return;
    	
    	Block block = Block.blocksList[worldObj.getBlockId(x,  y,  z)];
    	if (!(block instanceof BlockMushroom))
    		return;
    	Block shroom = (BlockMushroom)block;
    	
    	int whichdir = worldObj.rand.nextInt(4);

    	Vector3F target = null;
    	
    	if (worldObj.getBlockId(x - 1, y, z) == shroom.blockID)
    		return;
    	if (worldObj.getBlockId(x + 1, y, z) == shroom.blockID)
    		return;
    	if (worldObj.getBlockId(x, y, z - 1) == shroom.blockID)
    		return;
    	if (worldObj.getBlockId(x, y, z + 1) == shroom.blockID)
    		return;
    	
    	switch (whichdir)
    	{
    		case 0:
    		{
    			if (worldObj.getBlockId(x - 1, y, z) == 0)
    				target = new Vector3F(x - 1, y, z);
    		}
    		break;
    		
    		case 1:
			{
				if (worldObj.getBlockId(x + 1, y, z) == 0)
					target = new Vector3F(x + 1, y, z);
			}
	        break;

    		case 2:
    		{
    			if (worldObj.getBlockId(x, y, z - 1) == 0)
    				target = new Vector3F(x, y, z - 1);
    		}
    		break;
    		
    		case 3:
    		{
    			if (worldObj.getBlockId(x, y, z + 1) == 0)
    				target = new Vector3F(x, y, z + 1);
    		}
    		break;
    	}

    	if (target == null)
    		return; // no spot to grow
    	
    	if (!shroom.canPlaceBlockAt(worldObj,  (int)target.x, (int)target.y, (int)target.z))
    		return;
    	
    	worldObj.setBlock((int)target.x, (int)target.y, (int)target.z, shroom.blockID);
    	fertilize((int)target.x, (int)target.y, (int)target.z);
    }
    
    @Override 
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setString("essentianame", aspect == null ? "null" : aspect.getName().toLowerCase());
        nbt.setFloat("essentiacurr", current);
        nbt.setFloat("essentiamax", max);
    }
    
    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);	
        if (nbt.getString("essentianame") != "null")
        	aspect = Aspect.getAspect(nbt.getString("essentianame").toLowerCase());
        current = nbt.getFloat("essentiacurr");
        max = nbt.getFloat("essentiamax");
    }
 
    @Override
    public Packet getDescriptionPacket()
    {
        NBTTagCompound nbt = new NBTTagCompound();
        this.writeToNBT(nbt);
        return new Packet132TileEntityData(xCoord, yCoord, zCoord, 0, nbt);
    }
    
    @Override
    public void onDataPacket(INetworkManager net, Packet132TileEntityData pkt)
    {
        this.readFromNBT(pkt.data);
    }
    
    public float getStoredEssentia()
    {
    	return current;
    }
    
    public float getMaxEssentia()
    {
    	return max;
    }

	@Override
	public int addToContainer(Aspect aspect, int amount)
	{
		if (aspect != Aspect.WATER)
			return 0;
		
		int added = (int)Math.min(current + amount, max - current);
		
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		return added;
	}

	@Override
	public int containerContains(Aspect aspect)
	{
		return aspect == Aspect.WATER ? 1 : 0;
	}

	@Override
	public boolean doesContainerContain(AspectList aspectlist)
	{
		if (aspectlist.size() != 1)
			return false;
		return (aspectlist.getAspects()[0] == Aspect.WATER);
	}

	@Override
	public boolean doesContainerContainAmount(Aspect aspect, int amount)
	{
		return ((aspect == Aspect.WATER) && ((int)current >= amount));
	}

	@Override
	public AspectList getAspects()
	{
		return (new AspectList().add(Aspect.WATER, (int)current));
	}

	@Override
	public void setAspects(AspectList aspectlist)
	{
		new Exception("setAspects is not valid for TileEntityThaumicFertilizer.class");
	}

	@Override
	public boolean takeFromContainer(AspectList aspectlist)
	{
		if (aspectlist.size() !=1 || aspectlist.getAspects()[0] != Aspect.WATER)
			return false;
		if (aspectlist.getAmount(Aspect.WATER) < 1)
			return false;
		return true;
	}

	@Override
	public boolean takeFromContainer(Aspect aspect, int amount)
	{
		if (aspect != Aspect.WATER)
			return false;
		if (amount < (int)current)
			return false;
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		return true;
	}

	@Override
	public boolean doesContainerAccept(Aspect arg0) 
	{
		return (arg0 == Aspect.WATER);
	}
	
}
