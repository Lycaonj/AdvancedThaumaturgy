package net.ixios.advancedthaumaturgy.tileentities;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.nodes.NodeModifier;
import thaumcraft.api.nodes.NodeType;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.items.ItemWispEssence;
import thaumcraft.common.tiles.TileJarFillable;
import thaumcraft.common.tiles.TileJarNode;
import thaumcraft.common.tiles.TilePedestal;
import net.ixios.advancedthaumaturgy.AdvThaum;
import net.ixios.advancedthaumaturgy.fx.FloatyLineFX;
import net.ixios.advancedthaumaturgy.misc.Vector3F;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;

public class TileNodeModifier extends TileEntity implements IAspectContainer
{
	public class Requirements implements Cloneable
	{
		private AspectList m_essentia = null;
		private AspectList m_essences = null;
		
		public Requirements()
		{
			m_essentia = new AspectList();
			m_essences = new AspectList();
		}
		
		public Requirements(AspectList aspects, AspectList essences)
		{
			this();
			this.m_essentia = aspects;
			this.m_essences = essences;
		}
		
		public Aspect[] getEssentiaArray()
		{
			return m_essentia.getAspects();
		}
		
		public Aspect[] getEssenceArray()
		{
			return m_essences.getAspects();
		}
		
		public int getEssentiaAmount(Aspect aspect)
		{
			return m_essentia.getAmount(aspect);
		}
		
		public int getEssenceAmount(Aspect aspect)
		{
			return m_essences.getAmount(aspect);
		}
		
		public void modifyEssentia(Aspect aspect, int amount)
		{
			m_essentia.add(aspect,  amount);
			if (m_essentia.getAmount(aspect) <= 0)
				m_essentia.remove(aspect);
		}
		
		public void modifyEssence(Aspect aspect, int amount)
		{
			m_essences.add(aspect, amount);
			if (m_essences.getAmount(aspect) <= 0)
				m_essences.remove(aspect);
		}
		
		public int getCurrentAspectAmount()
		{
			if (m_essentia.size() == 0)
				return 0;
			else
				return m_essentia.getAmount(getCurrentEssentia());
		}
		
		public int getCurrentEssenceAmount()
		{
			if (m_essences.size() == 0)
				return 0;
			else
				return m_essences.getAmount(getCurrentEssence());
		}
		
		public Aspect getCurrentEssentia()
		{
			if (m_essentia.size() == 0)
				return null;
			if (m_essentia.getAmount(m_essentia.getAspects()[0]) == 0)
				return null;
			return m_essentia.getAspects()[0];
		}
		
		public Aspect getCurrentEssence()
		{
			if (m_essences.size() == 0)
				return null;
			if (m_essences.getAmount(m_essences.getAspects()[0]) == 0)
				return null;
			return m_essences.getAspects()[0];
		}

		public boolean essentiaRequired()
		{
			if (m_essentia.size() == 0)
				return false;
			if (m_essentia.getAmount(m_essentia.getAspects()[0]) == 0)
				return false;
			return true;
		}
		
		public boolean essenceRequired()
		{
			if (m_essences.size() == 0)
				return false;
			if (m_essences.getAmount(m_essences.getAspects()[0]) == 0)
				return false;
			return true;
		}
		
		@Override
		protected Requirements clone()
		{
			Requirements clone = new Requirements(m_essentia.copy(), m_essences.copy());
			return clone;
		}
		
		public void writeToNBT(NBTTagCompound nbt)
		{
			nbt.setInteger("numessentia",  m_essentia.size());
			for (int x = 0; x < m_essentia.size(); x++)
			{
				Aspect a = m_essentia.getAspects()[x];
				String name = a == null ? "null" : a.getName().toLowerCase();
				nbt.setString("essentia_name_" + x, name);
				nbt.setInteger("essentia_amt_" + x, m_essentia.getAmount(a));
			}
			
			nbt.setInteger("numessences",  m_essences.size());
			for (int x = 0; x < m_essences.size(); x++)
			{
				Aspect a = m_essences.getAspects()[x];
				String name = a == null ? "null" : a.getName().toLowerCase();
				nbt.setString("essence_name_" + x, name);
				nbt.setInteger("essence_amt_" + x, m_essences.getAmount(a));
			}
		}
		
		public void readFromNBT(NBTTagCompound nbt)
		{
			m_essentia = new AspectList();
			m_essences = new AspectList();
			
			int numessentia = nbt.getInteger("numessentia");
			for (int x = 0; x < numessentia; x++)
			{
				String name = nbt.getString("essentia_name_" + x);
				Aspect a = Aspect.getAspect(name);
				int amt = nbt.getInteger("essentia_amt_" + x);
				m_essentia.add(a, amt);				
			}
			
			int numessences = nbt.getInteger("numessences");
			for (int x = 0; x < numessences; x++)
			{
				String name = nbt.getString("essence_name_" + x);
				Aspect a = Aspect.getAspect(name);
				int amt = nbt.getInteger("essence_amt_" + x);
				m_essences.add(a, amt);			
			}
		}
	}
	
	private TileJarFillable essentiajar = null;
	private Requirements requirements = null;
	private int ticktracker = 0;
	private int soundticks = 0;
	private Operation currentoperation = Operation.Idle;
	private ArrayList<TilePedestal> pedestals = null;
	private int stage = 0;
	public ArrayList<Operation> availableOperations;
	private TilePedestal currentpedestal = null;
	private int currParticleColor = 0xffffffff;
	
	// stages
	// 0 = draining essentia
	// 1 = absorbing essences
	// 2 = time?
	
	public Aspect aspecttoincrease = null;
	
	public enum Operation
	{
	    Idle (0),
	    RemoveUnstable(1),
	    RemoveSinister(2),
	    RemoveTainted(3),
	    AddBright(4),
        RemoveHungry(5),
        RemovePale(6),
        RemoveFading(7),
        AddAspect(8),
        IncreaseAspect(9),
        AddHungry(10),
        AddTainted(11),
        AddSinister(12),
        AddPure(13);
        
        private byte id;
        Operation(int id)
	    {
	        this.id = (byte)id;
	    }
        public byte getId()
        {
            return this.id;
        }
        public static Operation parse(byte id)
        {
            for (Operation o : Operation.values())
            {
                if (o.id == id)
                    return o;
            }
            throw new IndexOutOfBoundsException(id + " is not a valid Operation value.");
        }
        @Override
        public String toString()
        {
        	switch (id)
        	{
        	case 0:
        		 return "Idle";
        	case 1:
        		 return "Remove Unstable";
        	case 2:
        		 return "Remove Sinister";
        	case 3:
        		return "Remove Tainted";
        	case 4:
        		return "Add Bright";
        	case 5:
        		return "Remove Hungry";
        	case 6:
        		return "Remove Pale";
        	case 7:
        	    return "Remove Fading";
        	case 8:
        	     return "Add Aspect";
        	case 9:
        	     return "Increase Aspect";
        	case 10:
        	     return "Add Hungry";
        	case 11:
        	     return "Add Tainted";
        	case 12:
        	     return "Add Sinister";
        	case 13:
        		return "Add Pure";
        	default:
        	    return "Unknown (" + id + ")";
        	}
        }
	}
	
	private static HashMap<Operation, Requirements> requirementlist = null;
	
	public TileNodeModifier()
	{
		
		if (requirementlist == null)
			setupRequirements();
		
		pedestals = new ArrayList<TilePedestal>();
		availableOperations = new ArrayList<Operation>();
	}

	private void setupRequirements()
	{
		requirementlist = new HashMap<Operation, Requirements>();
		
		Requirements reqs;
		
		reqs = new Requirements(new AspectList().add(Aspect.ORDER, 64), new AspectList().add(null, 1));
		requirementlist.put(Operation.RemoveUnstable, reqs);
		
		reqs = new Requirements(new AspectList().add(Aspect.LIFE, 64), new AspectList().add(null, 2));
		requirementlist.put(Operation.RemoveSinister, reqs);
		
		reqs = new Requirements(new AspectList().add(Aspect.ORDER, 64), new AspectList().add(null, 4));
		requirementlist.put(Operation.RemoveTainted, reqs);
		
		reqs = new Requirements(new AspectList().add(Aspect.AURA, 64), new AspectList().add(Aspect.AURA, 4));
		requirementlist.put(Operation.AddBright, reqs);
		
		reqs = new Requirements(new AspectList().add(Aspect.CROP, 32), new AspectList().add(Aspect.CROP, 1));
		requirementlist.put(Operation.RemoveHungry, reqs);
		
		reqs = new Requirements(new AspectList().add(Aspect.AURA, 32), new AspectList().add(null, 2));
		requirementlist.put(Operation.RemovePale, reqs);
		
		reqs = new Requirements(new AspectList().add(Aspect.LIGHT, 32), new AspectList().add(null, 2));
		requirementlist.put(Operation.RemoveFading, reqs);
		
		reqs = new Requirements(new AspectList().add(Aspect.AURA, 32), new AspectList().add(null, 4));
		requirementlist.put(Operation.AddAspect, reqs);
		
		reqs = new Requirements(new AspectList().add(Aspect.AURA, 64), new AspectList().add(Aspect.AURA, 2));
		requirementlist.put(Operation.IncreaseAspect, reqs);
		
		reqs = new Requirements(new AspectList().add(Aspect.HUNGER, 64), new AspectList().add(Aspect.HUNGER, 2));
		requirementlist.put(Operation.AddHungry, reqs);
		
		reqs = new Requirements(new AspectList().add(Aspect.TAINT, 64), new AspectList().add(Aspect.TAINT, 2));
		requirementlist.put(Operation.AddTainted, reqs);
		
		reqs = new Requirements(new AspectList().add(Aspect.UNDEAD, 32), new AspectList().add(Aspect.UNDEAD, 2));
		requirementlist.put(Operation.AddSinister, reqs);
		
		reqs = new Requirements(new AspectList().add(Aspect.AURA, 32), new AspectList().add(Aspect.AURA, 4));
		requirementlist.put(Operation.AddPure, reqs);
		
	}
	
	public Requirements getRequirements(Operation op)
	{
		if (requirementlist.containsKey(op))
			return requirementlist.get(op);
		else
			return null;
	}
	
	public void cleanup()
	{
		stage = 0;
		availableOperations.clear();
	    currentoperation = Operation.Idle;
	    aspecttoincrease = null;
		essentiajar = null;
		requirements = null;
		ticktracker = 0;
		worldObj.markBlockForUpdate(xCoord,  yCoord, zCoord);
	}
	
	private boolean hasRequiredItems()
	{
		boolean allavailable = true;
		
		for (Aspect a : requirements.getEssenceArray())
		{
			int numneeded = requirements.getEssenceAmount(a);
			for (TilePedestal p : pedestals)
			{
				ItemStack stack = p.getStackInSlot(0);
				if (stack != null && stack.getItem() instanceof ItemWispEssence)
				{
					ItemWispEssence essence = (ItemWispEssence)stack.getItem();
					if (essence == null)
						continue;
					if (essence.getAspects(stack).getAmount(a) > 0 || a == null)
					{
						numneeded--;
					}
				}
			}
			if (numneeded > 0)
			{
				allavailable = false;
				break;
			}
		}
		return allavailable;
	}
	
	@Override
	public boolean canUpdate()
	{
	    return true;
	}
	
	public void cancel()
	{
		if (currentoperation != Operation.Idle)
			worldObj.playSoundEffect(xCoord, yCoord, zCoord, "thaumcraft:craftfail", 0.5F, 1.0F);
		cleanup();
	}
	
	// gets available essentia and itemstacks (on pedestals) and sets the private variables availableEssentia and availableItems
	private void refreshResources()
	{
		pedestals.clear();
		
		for (int xc = xCoord - 4; xc < xCoord + 4; xc++)
		{
			for (int zc = zCoord - 4; zc < zCoord + 4; zc++)
			{
				TileEntity te = worldObj.getBlockTileEntity(xc, yCoord, zc);
				if (te instanceof TilePedestal)
				{
					pedestals.add((TilePedestal)te);
				}
				
			}
		}
	}
	
	private void drainEssentia(boolean doDrain)
	{
		Aspect aspect = requirements.getCurrentEssentia();
		
		if (aspect == null)
			return;
		
		if (essentiajar == null || essentiajar.amount == 0)
			essentiajar = AdvThaum.proxy.findEssentiaJar(worldObj, aspect, this, 16, 8, 16);
		
		if (essentiajar == null || essentiajar.aspect != aspect)
		    return;
		
		if (doDrain)
		{
			essentiajar.takeFromContainer(aspect, 1);
			requirements.modifyEssentia(aspect, -1);
			worldObj.markBlockForUpdate(essentiajar.xCoord, essentiajar.yCoord, essentiajar.zCoord);
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
		
		if (essentiajar != null)
		    AdvThaum.proxy.createParticle(worldObj, essentiajar.xCoord + 0.5F, essentiajar.yCoord + 1, essentiajar.zCoord + 0.5F,
		    	xCoord + 0.5F, yCoord + 1.4F, zCoord + 0.5F, aspect.getColor());
		
		
		
	}
	
	public boolean startProcess(Operation op)
	{
		cleanup();
		TileEntity te = (TileJarNode) worldObj.getBlockTileEntity(xCoord,  yCoord + 1, zCoord);
		if (te == null || !(te instanceof TileJarNode))
		{
			worldObj.playSoundEffect(xCoord, yCoord, zCoord, "thaumcraft:craftfail", 1.0F, 0.6F);
			return false;
		}
	
		requirements = requirementlist.get(op).clone();
	
		refreshResources();
		
		if (op == Operation.AddAspect)
		{
			AspectList pedaspects = new AspectList();
			// Make sure only ONE type of wisp essence is present
			for (TilePedestal p : pedestals)
			{
				if (p.getStackInSlot(0) != null && (p.getStackInSlot(0).getItem() instanceof ItemWispEssence))
				{
					for (Aspect tmp : ((ItemWispEssence)p.getStackInSlot(0).getItem()).getAspects(p.getStackInSlot(0)).getAspects())
					{
						pedaspects.add(tmp, 1);
					}
				}
			}
			if (pedaspects.getAspects().length != 1)
			{
				worldObj.playSoundEffect(xCoord, yCoord, zCoord, "thaumcraft:craftfail", 1.0F, 0.6F);
				return false;
			}
			int req = requirements.getEssenceAmount(null);
			requirements.modifyEssence(null, -5000); // essentially remove the aspect, heh
			requirements.modifyEssence(pedaspects.getAspects()[0], req);
			aspecttoincrease = pedaspects.getAspects()[0];
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
		
		if (!hasRequiredItems())
		{
			cleanup();
		    worldObj.playSoundEffect(xCoord, yCoord, zCoord, "thaumcraft:craftfail", 0.5F, 1.0F);
		    return false;
		}
		
		stage = 1;
		currentoperation = op;

		currParticleColor = Aspect.AURA.getColor();
		worldObj.playSoundEffect(xCoord, yCoord, zCoord, "thaumcraft:craftstart", 0.5F, 1.0F);
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		
		return true;
	}
	
	public boolean isActive()
	{
		return currentoperation != Operation.Idle;
	}
  
	private TilePedestal findPedestalWithTypeOfEssence(Aspect aspect)
	{
		for (TilePedestal tp : pedestals)
		{
			ItemStack stack = tp.getStackInSlot(0);
			if (stack == null)
				continue;
			if (!(stack.getItem() instanceof ItemWispEssence))
				continue;
			if (aspect == null)
				return tp;
			ItemWispEssence essence = (ItemWispEssence)stack.getItem();
			Aspect aspects[] = essence.getAspects(stack).getAspects();
			if (aspects == null || aspects.length == 0)
				continue;
			return tp;
		}
		return null;
	}
	
	@Override
	public void updateEntity()
	{
	    super.updateEntity();
	    
    	if (!isActive())
			return;
		
		if (ticktracker % 20 == 0)
			refreshResources();
		
		AdvThaum.proxy.createCustomParticle(worldObj, xCoord + 0.5F, yCoord, zCoord + 0.5F, 0F, 0.05F, 0F, currParticleColor);
			
		ticktracker++;
		soundticks++;
		
		if (soundticks % 65 == 0)
		{
			worldObj.playSoundEffect(xCoord, yCoord, zCoord, "thaumcraft:infuser", 0.5F, 1.0F);
			soundticks = 0;
		}
		
		if (ticktracker % 10 == 0 && worldObj.isRemote)
			Thaumcraft.proxy.blockRunes(worldObj, xCoord, yCoord, zCoord, 0.5F + worldObj.rand.nextFloat() * 0.2F, 0.1F, 0.7F + worldObj.rand.nextFloat() * 0.3F, 25, -0.03F);
		
		switch (stage)
		{
			case 1: // grab essentia if we need it
			{
				if (requirements.getCurrentAspectAmount() != 0)
				{
					drainEssentia(ticktracker % 20 == 0);
				}
				else if (!requirements.essentiaRequired())
				{
					stage = 2;
					ticktracker = 0;
					worldObj.markBlockForUpdate(xCoord,  yCoord,  zCoord);
				}	
			}
			break;
			
			case 2: // do item eating
			{
				if (ticktracker == 1 && requirements.getCurrentEssenceAmount() != 0)
				{
					if (requirements.getCurrentEssenceAmount() == 0)
						break;
					
					Aspect aspect = requirements.getCurrentEssence();
					
					currentpedestal = findPedestalWithTypeOfEssence(aspect);

					if (currentpedestal == null)
						break;
					
					if (aspect != null)
						currParticleColor = aspect.getColor();
					else
						currParticleColor = ((ItemWispEssence)currentpedestal.getStackInSlot(0).getItem()).getAspects(currentpedestal.getStackInSlot(0)).getAspects()[0].getColor(); 
						
					Vector3F dst = new Vector3F(currentpedestal.xCoord + 0.5F, currentpedestal.yCoord + 1.3F, currentpedestal.zCoord + 0.5F);
				
					AspectList aspects = ((ItemWispEssence)currentpedestal.getStackInSlot(0).getItem()).getAspects(currentpedestal.getStackInSlot(0));
					aspect = (Aspect) aspects.getAspects()[0];
				
					Vector3F src = new Vector3F(xCoord + 0.2F, yCoord + 1.5F, zCoord + 0.2F);
					AdvThaum.proxy.createFloatyLine(worldObj, src, dst, aspect.getColor(), 200, true);

					src = new Vector3F(xCoord + 0.2F, yCoord + 1.5F, zCoord + 0.7F);
					AdvThaum.proxy.createFloatyLine(worldObj, src, dst, aspect.getColor(), 200, true);
					
					src = new Vector3F(xCoord + 0.7F, yCoord + 1.5F, zCoord + 0.2F);
					AdvThaum.proxy.createFloatyLine(worldObj, src, dst, aspect.getColor(), 200, true);
					
					src = new Vector3F(xCoord + 0.7F, yCoord + 1.5F, zCoord + 0.7F);
					AdvThaum.proxy.createFloatyLine(worldObj, src, dst, aspect.getColor(), 200, true);
									
				}
				else if (ticktracker >= 200 && requirements.getCurrentEssenceAmount() != 0)
				{
					
					if (currentpedestal != null && currentpedestal.getStackInSlot(0) != null && 
							currentpedestal.getStackInSlot(0).getItem() instanceof ItemWispEssence)
					{
						ItemStack stack = currentpedestal.getStackInSlot(0);
						if (stack == null || !(stack.getItem() instanceof ItemWispEssence))
							return;
						
						Aspect reqaspect = requirements.getCurrentEssence();
						Aspect aspect = ((ItemWispEssence)stack.getItem()).getAspects(stack).getAspects()[0];
						
						AdvThaum.proxy.createSparkleBurst(worldObj, currentpedestal.xCoord + 0.5f, 
							currentpedestal.yCoord + 1.5f, currentpedestal.zCoord + 0.5f, 8, aspect.getColor());
						
						currentpedestal.setInventorySlotContents(0, null);
						worldObj.markBlockForUpdate(currentpedestal.xCoord, currentpedestal.yCoord, 
								currentpedestal.zCoord);
						
						requirements.modifyEssence(reqaspect, -1);
					}
					
					ticktracker = 0;
				}
				else if (!requirements.essenceRequired() && !requirements.essentiaRequired())
				{
					updateNode();
					cleanup();
				}
				
			}
	
		}
	   
	}
		
	private void updateNode()
	{
		TileEntity te = worldObj.getBlockTileEntity(xCoord, yCoord + 1, zCoord);
		
		if (!(te instanceof TileJarNode))
			return;
		
		TileJarNode jar = (TileJarNode)te;
		
		AdvThaum.proxy.createSparkleBurst(worldObj, xCoord + 0.5f, yCoord + 2f, zCoord + 0.5f, 8, -1);
		
		worldObj.playSoundEffect(xCoord, yCoord, zCoord, "thaumcraft:zap", 0.3F, 1.0F);
		
		switch (currentoperation)
		{
			case AddPure:
			{
				jar.setNodeType(NodeType.PURE);
			}
			break;
			
			case AddAspect:
			{
				jar.getAspects().add(aspecttoincrease, 1);
			}
			break;
			
			case IncreaseAspect:
			{
				jar.setNodeVisBase((short)(jar.getNodeVisBase() + 1));
			}
			break;
			
			case AddBright:
			{
				jar.setNodeModifier(NodeModifier.BRIGHT);
			}
			break;
				
			case AddHungry:
			{
				jar.setNodeType(NodeType.HUNGRY);
			}
			break;
			
			case AddSinister:
			{
				jar.setNodeType(NodeType.DARK);
			}
			break;
			
			case AddTainted:
			{
				jar.setNodeType(NodeType.TAINTED);
			}
			break;
			
			case RemoveFading:
			{
				jar.setNodeModifier(NodeModifier.PALE);
			}
			break;
			
			case RemovePale:
			{
				jar.setNodeModifier(null);
			}
			break;
			
			case RemoveHungry:
			case RemoveSinister:
			case RemoveTainted:
			{
				jar.setNodeType(NodeType.NORMAL);
			}
			break;
			
			case RemoveUnstable:
			{
				jar.setNodeType(NodeType.NORMAL);
			}
			break;
			
			default:
				break;
		}
		
		worldObj.markBlockForUpdate(jar.xCoord, jar.yCoord, jar.zCoord);
		
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
	public void readFromNBT(NBTTagCompound nbt)
	{
	    super.readFromNBT(nbt);
	    
	    if (nbt.hasKey("jarx"))
	    {
	    	int x = nbt.getInteger("jarx");
	    	int y = nbt.getInteger("jarx");
	    	int z = nbt.getInteger("jarx");
	    	TileEntity te = worldObj == null ? null : worldObj.getBlockTileEntity(x, y,  z);
	    	if (te != null && te instanceof TileJarFillable)
	    		essentiajar = (TileJarFillable)te;
	    }
	    
	    if (requirements == null)
	    	requirements = new Requirements();
	    
	    requirements.readFromNBT(nbt);
	    
		ticktracker = nbt.getInteger("ticks");
		soundticks = nbt.getInteger("sound");
		currentoperation = Operation.parse(nbt.getByte("operation"));
		
		int numpeds = nbt.getInteger("npeds");
		
		pedestals.clear();
		
		for (int i = 0; i < numpeds; i++)
		{
			int x = nbt.getInteger("pedx_" + i);
			int y = nbt.getInteger("pedy_" + i);
			int z = nbt.getInteger("pedz_" + i);
			TileEntity te = worldObj == null ? null : worldObj.getBlockTileEntity(x,  y,  z);
			if (te != null && te instanceof TilePedestal)
				pedestals.add((TilePedestal)te);
		}
		
		stage = nbt.getInteger("stage");

		if (nbt.hasKey("cpedx"))
		{
			int x = nbt.getInteger("cpedx");
			int y = nbt.getInteger("cpedy");
			int z = nbt.getInteger("cpedz");
			TileEntity te = worldObj == null ? null : worldObj.getBlockTileEntity(x, y, z);
			if (te != null && te instanceof TilePedestal)
				currentpedestal = (TilePedestal)te;
		}
		
		currParticleColor = nbt.getInteger("currParticleColor");
	}
	
	@Override 
	public void writeToNBT(NBTTagCompound nbt)
	{
	    super.writeToNBT(nbt);
	    
	    if (essentiajar != null)
	    {
	    	nbt.setInteger("jarx", essentiajar.xCoord);
	    	nbt.setInteger("jary", essentiajar.yCoord);
	    	nbt.setInteger("jarz", essentiajar.zCoord);
	    }
	    
	    if (requirements != null)
	    	requirements.writeToNBT(nbt);
	    
		nbt.setInteger("ticks", ticktracker);
		nbt.setInteger("sound", soundticks);
		nbt.setByte("operation", currentoperation.id);
		
		nbt.setInteger("npeds", pedestals.size());
		
		for (int i = 0; i < pedestals.size(); i++)
		{
			nbt.setInteger("pedx_" + i, pedestals.get(i).xCoord);
			nbt.setInteger("pedy_" + i, pedestals.get(i).yCoord);
			nbt.setInteger("pedz_" + i, pedestals.get(i).zCoord);
		}
		
		nbt.setInteger("stage", stage);

		if (currentpedestal != null)
		{
			nbt.setInteger("cpedx", currentpedestal.xCoord);
			nbt.setInteger("cpedy", currentpedestal.yCoord);
			nbt.setInteger("cpedz", currentpedestal.zCoord);
		}
		
		nbt.setInteger("currParticleColor", currParticleColor);
	}

	@Override
	public int addToContainer(Aspect arg0, int arg1) { return 0; }

	@Override
	public int containerContains(Aspect aspect) { return 0; }

	@Override
	public boolean doesContainerContain(AspectList aspects) { return false;	}

	@Override
	public boolean doesContainerContainAmount(Aspect aspect, int amount) { return false; }

	@Override
	public AspectList getAspects() 
	{
		return requirements == null ? null : requirements.m_essentia;
	}

	@Override
	public void setAspects(AspectList list) { }

	@Override
	public boolean takeFromContainer(AspectList list) { return false; }
	
	@Override
	public boolean takeFromContainer(Aspect arg0, int arg1) { return false; }

	@Override
	public boolean doesContainerAccept(Aspect arg0) 
	{
		return false;
	}

}
