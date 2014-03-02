package net.ixios.advancedthaumaturgy.tileentities;

import java.awt.Color;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.ItemEssence;
import net.ixios.advancedthaumaturgy.AdvThaum;
import net.ixios.advancedthaumaturgy.misc.Vector3F;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.common.ForgeDirection;

public class TileFluxDissipator extends TileMicrolithBase implements IAspectContainer
{
	private int tickcount = 0;
	private AspectList aspects = new AspectList().add(Aspect.TAINT, 0);
	
	public TileFluxDissipator()
	{
		super(new Color(255, 0, 255));
	}
	
	@Override
	public boolean canUpdate() 
	{
		return true;
	}
	
	@Override
	public void updateEntity()
	{
		super.updateEntity();
	
		tickcount++;
		
		if (tickcount % 20 != 0)
			return;
		
		for (int cx = xCoord - 12; cx < xCoord + 12; cx++)
		{
			for (int cy = yCoord - 12; cy < yCoord + 12; cy++)
			{
				for (int cz = zCoord - 12; cz < zCoord + 12; cz++)
				{
					int blockid = worldObj.getBlockId(cx, cy, cz);
				
					if (blockid == ConfigBlocks.blockFluxGoo.blockID)
					{
						worldObj.playSoundEffect(cx, cy, cz, "liquid.swim", 0.3F, 1.0F);
						worldObj.setBlock(cx,  cy,  cz, ConfigBlocks.blockFluxGas.blockID);
						return;
					}
					else if (blockid == ConfigBlocks.blockFluxGas.blockID && tickcount % 60 == 0)
					{
						Vector3F src = new Vector3F(xCoord + 0.5F, yCoord + 1F, zCoord + 0.5F);
						
						Vector3F dst = new Vector3F(cx + 0.2F, cy, cz + 0.2F);
						AdvThaum.proxy.createFloatyLine(worldObj, src, dst, Aspect.TAINT.getColor(), true);
						
						dst = new Vector3F(cx + 0.7F, cy, cz + 0.2F);
						AdvThaum.proxy.createFloatyLine(worldObj, src, dst, Aspect.TAINT.getColor(), true);

						dst = new Vector3F(cx + 0.2F, cy, cz + 0.7F);
						AdvThaum.proxy.createFloatyLine(worldObj, src, dst, Aspect.TAINT.getColor(), true);
						
						dst = new Vector3F(cx + 0.7F, cy, cz + 0.7F);
						AdvThaum.proxy.createFloatyLine(worldObj, src, dst, Aspect.TAINT.getColor(), true);
						
						worldObj.setBlockToAir(cx,  cy,  cz);
						
						worldObj.playSoundEffect(cx, cy, cz, "random.fizz", 0.3F, 1.0F);
						
						if (worldObj.rand.nextInt(100) <= 15)
						{
							aspects.add(Aspect.TAINT,  1);
							worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
						}
						return;
					}
				}	
			}
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
	    super.readFromNBT(tag);
	    if (aspects != null)
	    	aspects.readFromNBT(tag);
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag)
	{
	    super.writeToNBT(tag);
	    if (aspects != null)
	    	aspects.writeToNBT(tag);
	}
	
	@Override
	public Packet getDescriptionPacket()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		writeToNBT(nbt);
		return new Packet132TileEntityData(xCoord, yCoord, zCoord, 0, nbt);
	}
	
	@Override
	public void onDataPacket(INetworkManager net, Packet132TileEntityData pkt)
	{
		readFromNBT(pkt.data);
	}

	
	
	
	
	@Override
	public int addToContainer(Aspect aspect, int amount)
	{
		if (aspect != Aspect.TAINT)
			return 0;
		aspects.add(aspect, amount);
		return amount;
	}

	@Override
	public int containerContains(Aspect aspect)
	{
		return (aspect == Aspect.TAINT ? aspects.getAmount(aspect) : 0);
	}

	@Override
	public boolean doesContainerAccept(Aspect aspect)
	{
		return false;
	}

	@Override
	public boolean doesContainerContain(AspectList list)
	{
		return (list.aspects.size() == 1) && list.aspects.containsKey(Aspect.TAINT);
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
	public void setAspects(AspectList list)	{ }

	@Override
	public boolean takeFromContainer(AspectList list)
	{
		boolean success = list.size() == 1 && list.getAmount(Aspect.TAINT) > 0;
		if (success)
		{
			aspects.remove(Aspect.TAINT, list.getAmount(Aspect.TAINT));
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
		return success;
	}

	@Override
	public boolean takeFromContainer(Aspect aspect, int amount)
	{
		boolean success = aspect == Aspect.TAINT && aspects.getAmount(aspect) >= amount;
		if (success)
		{
			aspects.remove(aspect, amount);
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
		return success;
	}

	@Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
	        float hitY, float hitZ)
    {
		ItemStack helditem = player.getHeldItem();
	    TileEntity te = world.getBlockTileEntity(x,  y,  z);
	    
	    if (helditem == null || !(helditem.getItem() instanceof IEssentiaContainerItem))
	    	return false;
	 
	    if (!(te instanceof TileFluxDissipator))
	    	return false;
	    
	    TileFluxDissipator fd = (TileFluxDissipator)te;
	 
	    if (helditem.getItemDamage() == 0)
	    {
	         if (fd.doesContainerContainAmount(Aspect.TAINT, 8)) 
	         {
	            if (world.isRemote) 
	            {
	               player.swingItem();
	               return false;
	            }

	            if (fd.takeFromContainer(Aspect.TAINT, 8))
	            {
	               --helditem.stackSize;
	               ItemStack stack = new ItemStack(ConfigItems.itemEssence, 1, 1);
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
	
}
