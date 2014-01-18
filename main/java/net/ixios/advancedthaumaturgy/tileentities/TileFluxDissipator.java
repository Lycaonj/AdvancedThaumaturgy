package net.ixios.advancedthaumaturgy.tileentities;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.common.config.ConfigBlocks;
import net.ixios.advancedthaumaturgy.AdvThaum;
import net.ixios.advancedthaumaturgy.misc.Vector3F;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;

public class TileFluxDissipator extends TileEntity implements IAspectContainer
{
	private int tickcount = 0;
	private AspectList aspects = new AspectList().add(Aspect.TAINT, 0);
	
	public TileFluxDissipator()
	{
		
	}
	
	public void setDirection(ForgeDirection d)
	{
		//orientation = d;
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
		
		if (tickcount < 60)
			return;
		
		tickcount = 0;
		
		for (int cx = xCoord - 12; cx < xCoord + 12; cx++)
		{
			for (int cy = yCoord - 12; cy < yCoord + 12; cy++)
			{
				for (int cz = zCoord - 12; cz < zCoord + 12; cz++)
				{
					int blockid = worldObj.getBlockId(cx, cy, cz);
				
					if (blockid == ConfigBlocks.blockFluxGoo.blockID)
					{
						worldObj.playSoundEffect(cx, cy, cz, "liquid.swim", 1F, 1.0F);
						worldObj.setBlock(cx,  cy,  cz, ConfigBlocks.blockFluxGas.blockID);
						return;
					}
					else if (blockid == ConfigBlocks.blockFluxGas.blockID)
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
						
						worldObj.playSoundEffect(cx, cy, cz, "random.fizz", 1F, 1.0F);
						
						if (worldObj.rand.nextInt(100) <= 10)
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
		return list.size() == 1 && list.getAmount(Aspect.TAINT) > 0;
	}

	@Override
	public boolean takeFromContainer(Aspect aspect, int amount)
	{
		return aspect == Aspect.TAINT && aspects.getAmount(aspect) >= amount;
	}
	
}
