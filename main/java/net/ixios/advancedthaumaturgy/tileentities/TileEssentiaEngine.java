package net.ixios.advancedthaumaturgy.tileentities;

import java.util.HashMap;

import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.client.fx.FXLightningBolt;
import thaumcraft.common.tiles.TileJarFillable;
import net.ixios.advancedthaumaturgy.AdvThaum;
import net.ixios.advancedthaumaturgy.misc.Utilities;
import net.ixios.advancedthaumaturgy.misc.Vector3F;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import buildcraft.api.power.IPowerEmitter;
import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.power.PowerHandler;
import buildcraft.api.power.PowerHandler.PowerReceiver;
import buildcraft.api.transport.IPipeConnection;
import buildcraft.api.transport.IPipeTile.PipeType;
import cpw.mods.fml.common.Optional;
//import cpw.mods.fml.common.Optional.Method;
import cpw.mods.fml.common.Optional.Method;

@Optional.InterfaceList({
	@Optional.Interface(modid = "BuildCraft|Energy", iface = "IPipeConnection"), 
	@Optional.Interface(modid = "BuildCraft|Energy", iface = "IPowerEmitter")})
public class TileEssentiaEngine extends TileEntity implements IPowerEmitter, IPipeConnection, IEssentiaTransport
{
	//private final double costperRF = 1D / x;
	//private final double costperEU = 1D / 4000D;
	private final double costperMJ = 1D / 1800D;

	private Aspect curraspect = null;
	private float energy = 0.0F;
	private final float maxEnergy = 100F;

	private HashMap<Aspect, Float> aspectvalues = null;
	
	private boolean currentlyactive = false;
	
	public TileEssentiaEngine()
	{
		aspectvalues = new HashMap<Aspect, Float>();
		aspectvalues.put(Aspect.FIRE, 1.0F);
		aspectvalues.put(Aspect.EARTH, 0.5F);
		aspectvalues.put(Aspect.AIR, 0.5F);
		aspectvalues.put(Aspect.WATER, 0.5F);
		aspectvalues.put(Aspect.ORDER, 1.0F);
		aspectvalues.put(Aspect.ENTROPY, 0.5F);
		
		aspectvalues.put(Aspect.TREE, 0.25F);
		aspectvalues.put(Aspect.PLANT, 0.25F);
		aspectvalues.put(Aspect.STONE, 0.25F);
		aspectvalues.put(Aspect.ENERGY, 2.0F);
		
	}

	public void setActive(boolean value)
	{
		currentlyactive = value;
		if (!worldObj.isRemote)
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}
	
	@Override
	public boolean canUpdate() 
	{
		return true;
	}
	
	private void restockEnergy()
	{

		if ((int)energy >= maxEnergy)
			return;
	
		if (hasEssentiaTubeConnection())
		{
			if (fillFromPipe() == 0)
				return;
			energy += aspectvalues.get(curraspect);
            if (!worldObj.isRemote)
            	worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			return;
		}
		
		// findEssentia is in common proxy
		TileJarFillable essentiajar = null;
		
		for (Aspect aspect : aspectvalues.keySet())
		{
			essentiajar = Utilities.findEssentiaJar(worldObj, aspect, this, 20, 2, 20);
			if (essentiajar != null)
			{
				curraspect = essentiajar.aspect;
				break;
			}
		}
		
		if (essentiajar != null && essentiajar.amount > 0)
        {
			// createParticls is a blank method in common proxy, and has actual code in client proxy
            AdvThaum.proxy.createParticle(worldObj, (float)essentiajar.xCoord + 0.5F, essentiajar.yCoord + 1, (float)essentiajar.zCoord + 0.5F, 
            		(float)xCoord + 0.5F, (float)yCoord + 0.8F, (float)zCoord + 0.5F, essentiajar.aspect.getColor());
            essentiajar.takeFromContainer(curraspect, 1);
            energy += aspectvalues.get(curraspect);
            if (!worldObj.isRemote)
            {
            	worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
            	worldObj.markBlockForUpdate(essentiajar.xCoord, essentiajar.yCoord, essentiajar.zCoord);
            }
            return;
        }
	
	}
	
	@Override
	public void updateEntity()
	{
		super.updateEntity();
		
		if ((worldObj.isRemote) && (curraspect != null))
		{
			Vector3F src = new Vector3F(xCoord + 0.5F, yCoord + 1.0F, zCoord + 0.5F);
			Vector3F dst = new Vector3F(src.x, yCoord, src.z);
			
			/*src.x += (worldObj.rand.nextFloat() - 0.5F);
			src.y += (worldObj.rand.nextFloat() - 0.5F);
			src.z += (worldObj.rand.nextFloat() - 0.5F);*/
			
			dst.x += (worldObj.rand.nextFloat() - 0.5F);
			dst.y += (worldObj.rand.nextFloat() - 0.5F);
			dst.z += (worldObj.rand.nextFloat() - 0.5F);
			
			if (Minecraft.getMinecraft().renderViewEntity.ticksExisted % 60 == 0)
			{
				FXLightningBolt bolt = new FXLightningBolt(worldObj, src.x, src.y, src.z, dst.x, dst.y, dst.z, worldObj.rand.nextLong(), 5, 1);
				bolt.defaultFractal();
				bolt.setType(0);
				bolt.finalizeBolt();
			}
			
			if (curraspect != null)
			{
				AdvThaum.proxy.createOrbitingParticle(worldObj, this, 20, 0.2F, curraspect.getColor());
				AdvThaum.proxy.createOrbitingParticle(worldObj, this, 20, 0.2F, curraspect.getColor());
				AdvThaum.proxy.createOrbitingParticle(worldObj, this, 20, 0.2F, curraspect.getColor());
				AdvThaum.proxy.createOrbitingParticle(worldObj, this, 20, 0.2F, curraspect.getColor());
			}
		}
		
		restockEnergy();
				
		if (!currentlyactive || energy <= 0.0F)
			return;

		for (ForgeDirection orientation : ForgeDirection.values())
		{
			TileEntity tile = worldObj.getBlockTileEntity(xCoord + orientation.offsetX, yCoord + orientation.offsetY, zCoord + orientation.offsetZ);
			
			if (tile != null && isPoweredTile(tile, orientation))
			{
				PowerReceiver receptor = ((IPowerReceptor) tile).getPowerReceiver(orientation.getOpposite());
	
				float allowed = 10;
					
				float needed = receptor.receiveEnergy(PowerHandler.Type.ENGINE, allowed, orientation.getOpposite());
				double cost = (costperMJ * needed);
				
				energy -= cost;
				
				if (worldObj.getWorldTime() % 4 == 0)
					AdvThaum.proxy.createEngineParticle(worldObj, xCoord, yCoord, zCoord, orientation, 0xFF00FFFF);
				
				if ((!worldObj.isRemote))
					worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			}
		}
	}
	
	public boolean isPoweredTile(TileEntity tile, ForgeDirection side)
	{
		if (tile instanceof IPowerReceptor)
			return ((IPowerReceptor) tile).getPowerReceiver(side.getOpposite()) != null;

		return false;
	}
	
	private boolean hasEssentiaTubeConnection()
	{
		TileEntity te = worldObj.getBlockTileEntity(xCoord, yCoord + 1, zCoord);
		return (te instanceof IEssentiaTransport);
	}
	
	
	
	@Method(modid = "BuildCraft|Energy")
	@Override
	public ConnectOverride overridePipeConnection(PipeType type, ForgeDirection with)
	{
		if (type != PipeType.POWER)
			return ConnectOverride.DISCONNECT;
		else
			return ConnectOverride.CONNECT;
	}

	@Method(modid = "BuildCraft|Energy")
	@Override
	public boolean canEmitPowerFrom(ForgeDirection side) 
	{
		return (side != ForgeDirection.DOWN);
	}



	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		if (nbt.hasKey("aspect"))
			curraspect = Aspect.getAspect(nbt.getString("aspect").toLowerCase());
		energy = nbt.getFloat("energy");
		currentlyactive = nbt.getBoolean("active");
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		if (curraspect != null)
			nbt.setString("aspect", curraspect.getName().toLowerCase());
		nbt.setFloat("energy", energy);
		nbt.setBoolean("active", currentlyactive);
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
	public boolean canInputFrom(ForgeDirection direction) 
	{
		return (direction != ForgeDirection.UP);
	}

	@Override
	public boolean canOutputTo(ForgeDirection arg0)
	{
		return false;
	}

	@Override
	public int getMinimumSuction() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isConnectable(ForgeDirection direction) 
	{
		return (direction != ForgeDirection.UP);
	}

	@Override
	public boolean renderExtendedTube() 
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setSuction(Aspect arg0, int arg1)
	{
		
	}

	@Override
	public int takeVis(Aspect arg0, int arg1)
	{
		return 0;
	}
	
	private int fillFromPipe()
	{
		TileEntity te = ThaumcraftApiHelper.getConnectableTile(worldObj, xCoord, yCoord, zCoord, ForgeDirection.DOWN);
		
		if (te != null) 
		{
			IEssentiaTransport ic = (IEssentiaTransport)te;
			if (!ic.canOutputTo(ForgeDirection.UP))
				return 0;

			for (Aspect aspect : aspectvalues.keySet())
			{
				if (ic.getSuctionType(ForgeDirection.UP) == aspect &&
						ic.getSuctionAmount(ForgeDirection.UP) < getSuctionAmount(ForgeDirection.DOWN) &&
						ic.takeVis(aspect, 1) == 1)
					return 1;
			}
		}
		return 0;
	}

	@Override
    public int addVis(Aspect arg0, int arg1)
    {
	    // TODO Auto-generated method stub
	    return 0;
    }

	@Override
    public int getEssentiaAmount(ForgeDirection arg0)
    {
	    // TODO Auto-generated method stub
	    return 0;
    }

	@Override
    public Aspect getEssentiaType(ForgeDirection arg0)
    {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public int getSuctionAmount(ForgeDirection arg0)
    {
	    // TODO Auto-generated method stub
	    return 0;
    }

	@Override
    public Aspect getSuctionType(ForgeDirection arg0)
    {
	    // TODO Auto-generated method stub
	    return null;
    }
}
