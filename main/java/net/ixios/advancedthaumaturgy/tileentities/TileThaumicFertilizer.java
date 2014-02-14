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
import vazkii.botania.api.ISpecialFlower;
import net.ixios.advancedthaumaturgy.AdvThaum;
import net.ixios.advancedthaumaturgy.misc.Utilities;
import net.ixios.advancedthaumaturgy.misc.Vector3F;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.BlockLilyPad;
import net.minecraft.block.BlockMushroom;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.BlockStem;
import net.minecraft.block.BlockVine;
import net.minecraft.block.BlockWood;
import net.minecraft.block.material.Material;
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
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.FakePlayer;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.ForgeDummyContainer;

public class TileThaumicFertilizer extends TileEntity implements IAspectContainer
{
	
	private int ticktracker = 0;
	private AspectList aspects = null;
	
	private int fertilizechance = 20;
	
    private final float costperfertilize = 0.5f;
    
    private ArrayList<Vector3F> blockstomonitor = null;
    private int arrayposition = 0;
    
    private float herbapool = 0.0f;
    
    public TileThaumicFertilizer()
    {
    	aspects = new AspectList();
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
    		int cy = yCoord;
    		//for (int cy = yCoord + 2; cy > yCoord - 2; cy--)
    		//{
	    		for (int cz = zCoord - 8; cz <= zCoord + 8; cz++)
	    		{
	    			blockstomonitor.add(new Vector3F(cx, cy, cz));
	    		}
    		//}
    	}
    	
    	Collections.shuffle(blockstomonitor);
    	
    }
    
    private void refillPoolsIfNeeded()
    {
    	if (herbapool < 1 && aspects.getAmount(Aspect.PLANT) > 0)
    	{
    		aspects.remove(Aspect.PLANT, 1);
    		herbapool++;
    		worldObj.markBlockForUpdate(xCoord,  yCoord,  zCoord);
    	}
    	
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
    	
    	if (AdvThaum.debug)
    	{
	    	AdvThaum.proxy.createCustomParticle(worldObj, xCoord - 8, yCoord, zCoord - 8, 0f, .1f, 0f, 0xFFFF0000);
	    	AdvThaum.proxy.createCustomParticle(worldObj, xCoord - 8, yCoord, zCoord + 8, 0f, .1f, 0f, 0xFFFF0000);
	    	AdvThaum.proxy.createCustomParticle(worldObj, xCoord + 8, yCoord, zCoord - 8, 0f, .1f, 0f, 0xFFFF0000);
	    	AdvThaum.proxy.createCustomParticle(worldObj, xCoord + 8, yCoord, zCoord + 8, 0f, .1f, 0f, 0xFFFF0000);
    	}
    	
    	if (aspects.getAmount(Aspect.WATER) < 64 && ticktracker % 2 == 0)
    	{
    		TileJarFillable jar = Utilities.findEssentiaJar(worldObj, Aspect.WATER, this, 16, 2, 16);
    		if (jar != null && jar.amount > 0)
			{
    			jar.takeFromContainer(Aspect.WATER, 1);
    			AdvThaum.proxy.createParticle(worldObj, jar.xCoord + 0.5f, jar.yCoord + 1f, jar.zCoord + 0.5f, xCoord + 0.5f, yCoord + 1f, zCoord + 0.5f, Aspect.WATER.getColor());
    			aspects.add(Aspect.WATER, 1);
    			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			}
    	}
    	
    	if (aspects.getAmount(Aspect.PLANT) < 64 && ticktracker % 2 == 0)
    	{
    		TileJarFillable jar = Utilities.findEssentiaJar(worldObj, Aspect.PLANT, this, 16, 2, 16);
    		if (jar != null && jar.amount > 0)
			{
    			jar.takeFromContainer(Aspect.PLANT, 1);
    			AdvThaum.proxy.createParticle(worldObj, jar.xCoord + 0.5f, jar.yCoord + 1f, jar.zCoord + 0.5f, xCoord + 0.5f, yCoord + 1f, zCoord + 0.5f, Aspect.PLANT.getColor());
    			aspects.add(Aspect.PLANT, 1);
    			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			}
    	}
    	
        ticktracker++;
        
    	if (ticktracker < 2)
        	return;
        
        ticktracker = 0;
  
        refillPoolsIfNeeded();
        
        int triggermeta = 4;
             
        int cx = (int)blockstomonitor.get(arrayposition).x;
        int cy = (int)blockstomonitor.get(arrayposition).y;
        int cz = (int)blockstomonitor.get(arrayposition).z;
        arrayposition++;
        
        if (arrayposition >= blockstomonitor.size())
        	arrayposition = 0;
        
        Material mat = worldObj.getBlockMaterial(cx, cy, cz);
        
        Block block = Block.blocksList[worldObj.getBlockId(cx, cy, cz)];
        
        if (AdvThaum.debug)
        	AdvThaum.proxy.createCustomParticle(worldObj, cx, cy, cz, 0f, .1f, 0f, 0xFFFF0000);
        
        if (block == null)
        	return;
        
        // check if the block is farmland and if it needs watered
        if (block.blockID == Block.tilledField.blockID)
        {
        	int meta = worldObj.getBlockMetadata(cx, cy, cz);
        	
        	if (meta <= triggermeta)
                hydrateFarmland(cx, cy, cz);
        }
       
        /*if (worldObj.rand.nextInt(100) > fertilizechance)
        	return;*/
        
        if (block instanceof BlockFlower && !(block instanceof ISpecialFlower))
        	if (!(block instanceof BlockSapling) && worldObj.rand.nextInt(100) < 30)
        		spreadSurfaceBlockFrom(cx, cy, cz);
        
        if (mat == Material.plants || block instanceof BlockSapling)
        {
    		fertilize(cx, cy, cz);
    		return;
        }
        
        // special case for mana pods because they do not grow horizontally
        if (!BiomeDictionary.isBiomeOfType(worldObj.getBiomeGenForCoords(cx, cz), Type.MAGICAL))
        	return;
        
       
        if (!(block.blockID == ConfigBlocks.blockManaPod.blockID))
    		return;
    	
    	fertilize(cx, cy, cz);
        
    }

    /*private void spreadVineFrom(int x, int y, int z)
    {
    	if (herbapool < costperfertilize)
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
    }*/
  
    private void hydrateFarmland(int x, int y, int z)
    {
    	int basemeta = worldObj.getBlockMetadata(x,  y,  z);
    	if (basemeta >= 7)
    		return;
    	worldObj.setBlockMetadataWithNotify(x, y, z, basemeta + 1, 3);
        AdvThaum.proxy.createParticle(worldObj, xCoord + 0.5F, yCoord + 1, zCoord + 0.5F, (float)x + 0.5F, (float)y + 1, (float)z + 0.5F, 0xFF00FFFF);	
    }
    
    private void fertilize(int x, int y, int z)
    {
    	if (herbapool > costperfertilize && ItemDye.applyBonemeal(new ItemStack(Item.dyePowder, 1, 0), worldObj, x, y, z, new FakePlayer(worldObj, "advthaumPlayer")))
    	{
    		AdvThaum.proxy.createParticle(worldObj, xCoord + 0.5f, yCoord + 0.7f, zCoord + 0.5f, x, y, z, Aspect.SLIME.getColor());
    		AdvThaum.proxy.createSparkleBurst(worldObj, x + 0.5F, y + 0.5F, z + 0.5F, 8, 0xFF00FF00);
    		herbapool -= costperfertilize;
    		Block b = Block.blocksList[worldObj.getBlockId(x,  y,  z)];
    		b.updateTick(worldObj, x, y, z, worldObj.rand);
    	}
    }
    
    private void spreadSurfaceBlockFrom(int x, int y, int z)
    {
    	if (herbapool < costperfertilize)
    		return;
    	
    	Block block = Block.blocksList[worldObj.getBlockId(x,  y,  z)];
    	
    	ForgeDirection fd = ForgeDirection.VALID_DIRECTIONS[worldObj.rand.nextInt(ForgeDirection.VALID_DIRECTIONS.length - 2) + 2];

    	if (worldObj.getBlockId(x + fd.offsetX, y + fd.offsetY, z + fd.offsetZ) != 0)
    		return;
    	
    	if (!block.canPlaceBlockAt(worldObj, x + fd.offsetX, y + fd.offsetY, z + fd.offsetZ))
    		return;
    	
    	int meta = worldObj.getBlockMetadata(x,  y,  z);
    	
    	worldObj.setBlock(x + fd.offsetX, y + fd.offsetY, z + fd.offsetZ, block.blockID, meta, 3);
    	fertilize(x + fd.offsetX, y + fd.offsetY, z + fd.offsetZ);
    }
    
    @Override 
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        aspects.writeToNBT(nbt);
    }
    
    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);	
        aspects = new AspectList();
        aspects.readFromNBT(nbt);
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

	@Override
	public int addToContainer(Aspect aspect, int amount)
	{
		if (aspect != Aspect.WATER && aspect != Aspect.PLANT)
			return 0;
		aspects.add(aspect, amount);
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		return amount;
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
		return aspects.getAmount(aspect) >= amount;
	}

	@Override
	public AspectList getAspects()
	{
		return aspects;
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
		if (aspect != Aspect.WATER && aspect != Aspect.PLANT)
			return false;
		int amt = aspects.getAmount(aspect) - aspects.remove(aspect, amount).getAmount(aspect) * -1;
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		return amt == amount;
	}

	@Override
	public boolean doesContainerAccept(Aspect arg0) 
	{
		return (arg0 == Aspect.WATER);
	}
	
}
