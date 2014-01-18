package net.ixios.advancedthaumaturgy.misc;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;

import thaumcraft.common.tiles.TileInfusionMatrix;
import net.ixios.advancedthaumaturgy.AdvThaum;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.ForgeSubscribe;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.SideOnly;
import cpw.mods.fml.relauncher.Side;

@SideOnly(Side.CLIENT)
public class TickManager implements ITickHandler
{
	ArrayList<TileInfusionMatrix> waiting = new ArrayList<TileInfusionMatrix>(); 
	HashMap<TileInfusionMatrix, Integer> monitoring = new HashMap<TileInfusionMatrix, Integer>();
	
	@ForgeSubscribe
	public void onRenderWorld(RenderWorldLastEvent event)
	{
		
	}
		
	////////////////////////////////////////////////////////////////////////////////////////////
	// OVERRIDES
	////////////////////////////////////////////////////////////////////////////////////////////
	
	@Override
	public String getLabel() 
	{
		return "AdvThaum RenderTickManager";
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData)
	{
		// check IMs we're waiting on to see if they're active, and if so, get their base instability
		// and move them to the monitoring hashmap
		for (Iterator<TileInfusionMatrix> i = waiting.iterator(); i.hasNext();)
		{
			TileInfusionMatrix im = i.next();
			if (im.active)
			{
				int instability = ReflectionHelper.getPrivateValue(TileInfusionMatrix.class, im, "instability");
				i.remove();
				monitoring.put(im, instability);
			}
		}
		
		for (Iterator<TileInfusionMatrix> i = monitoring.keySet().iterator(); i.hasNext();)
		{
			TileInfusionMatrix im = i.next();
			
			boolean isactive = im.active;
			
			if (!isactive)
			{
				i.remove();
				continue;
			}
			int baseinstability = monitoring.get(im);
			int currinstability = ReflectionHelper.getPrivateValue(TileInfusionMatrix.class, im, "instability");
	    	
			if (currinstability > (baseinstability * .75f))
			{
	    		ReflectionHelper.setPrivateValue(TileInfusionMatrix.class, im, (baseinstability * .75f), "instability");
			}
		}
	}

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) { }

	@Override
	public EnumSet<TickType> ticks()
	{
		return EnumSet.of(TickType.SERVER);
	}
	
	public void beginMonitoring(TileInfusionMatrix im)
	{
		waiting.add(im);
	}
}

