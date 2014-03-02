/*package net.ixios.advancedthaumaturgy.tileentities;

import java.util.List;

import net.ixios.advancedthaumaturgy.misc.Utilities;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.FakePlayer;

import org.lwjgl.util.Color;

import cpw.mods.fml.client.FMLClientHandler;
import thaumcraft.client.fx.FXScorch;
import thaumcraft.client.lib.UtilsFX;

public class TileBurningSentry extends TileMinilithBase
{

	private boolean m_active = false;
	private EntityLivingBase m_target = null;
	private FakePlayer player = null;
	
	public TileBurningSentry()
    {
	    super(new Color(255, 127, 0, 128));
    }

	@Override
	public void validate()
	{
	    super.validate();
	    player = new FakePlayer(worldObj, "ATFakePlayer");
	    player.setPosition(xCoord,  yCoord, zCoord);
	}
	
	@Override
	public void updateEntity()
	{
	    super.updateEntity();
	    if (!m_active || player == null)
	    	return;
	    
	    if (m_target == null && (Minecraft.getMinecraft().renderViewEntity.ticksExisted % 20 != 0))
	    {
	    	AxisAlignedBB bb = AxisAlignedBB.getBoundingBox(xCoord - 12, yCoord - 4, zCoord - 12, xCoord + 12,  yCoord + 8, zCoord + 12);
	    	List<EntityMob> mobs = worldObj.getEntitiesWithinAABB(EntityMob.class, bb);
	    	
	    	if (mobs.size() == 0)
	    		return;
	    	
	    	m_target = mobs.get(0);
	    }
	    
	    if (m_target != null && m_target.isDead)
	    {
	    	m_target = null;
	    	return;
	    }
	    
	    // FIRE ZE... FIRE
	    
	    Vec3 target = Vec3.createVectorHelper(m_target.posX, m_target.posY,  m_target.posZ);
	    Vec3 src = Vec3.createVectorHelper(xCoord,  yCoord, zCoord);
	    Vec3 vector = target.subtract(src);
	    
        if (Minecraft.getMinecraft().renderViewEntity.ticksExisted % 10 == 0)
            worldObj.playSoundEffect(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D, "thaumcraft:fireloop", 0.25F, 2.0F);

        if (worldObj.isRemote)
        	Utilities.shootFireInDirection(worldObj, vector);
        
	}
	
	@Override
    public boolean onBlockActivated(World world, int x, int y, int z,
            EntityPlayer player, int side, float hitX, float hitY, float hitZ)
    {
		m_active = !m_active;
	    return true;
    }

}
*/