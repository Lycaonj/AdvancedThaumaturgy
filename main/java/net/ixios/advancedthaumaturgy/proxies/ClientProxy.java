package net.ixios.advancedthaumaturgy.proxies;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import thaumcraft.client.fx.FXEssentiaTrail;
import thaumcraft.client.fx.FXSparkle;
import thaumcraft.client.renderers.item.ItemWandRenderer;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.tiles.TileInfusionMatrix;
import net.ixios.advancedthaumaturgy.AdvThaum;
import net.ixios.advancedthaumaturgy.blocks.BlockCreativeNode;
import net.ixios.advancedthaumaturgy.blocks.BlockEssentiaEngine;
import net.ixios.advancedthaumaturgy.blocks.BlockEtherealJar;
import net.ixios.advancedthaumaturgy.blocks.BlockMicrolith;
import net.ixios.advancedthaumaturgy.blocks.BlockNodeModifier;
import net.ixios.advancedthaumaturgy.blocks.BlockPlaceholder;
import net.ixios.advancedthaumaturgy.blocks.BlockThaumicFertilizer;
import net.ixios.advancedthaumaturgy.blocks.BlockThaumicVulcanizer;
import net.ixios.advancedthaumaturgy.fx.ColorableSparkleFX;
import net.ixios.advancedthaumaturgy.fx.CustomParticleFX;
import net.ixios.advancedthaumaturgy.fx.EntityOrbiterFX;
import net.ixios.advancedthaumaturgy.fx.FloatyLineFX;
import net.ixios.advancedthaumaturgy.gui.GuiNodeModifier;
import net.ixios.advancedthaumaturgy.items.ItemEtherealJar;
import net.ixios.advancedthaumaturgy.misc.Vector3;
import net.ixios.advancedthaumaturgy.misc.Vector3F;
import net.ixios.advancedthaumaturgy.models.ModelEngine;
import net.ixios.advancedthaumaturgy.models.ModelEtherealJar;
import net.ixios.advancedthaumaturgy.models.ModelFertilizer;
import net.ixios.advancedthaumaturgy.models.ModelMinilith;
import net.ixios.advancedthaumaturgy.models.ModelNodeModifier;
import net.ixios.advancedthaumaturgy.models.ModelVulcanizer;
import net.ixios.advancedthaumaturgy.renderers.BlockEtherealJarRenderer;
import net.ixios.advancedthaumaturgy.renderers.GenericRenderer;
import net.ixios.advancedthaumaturgy.renderers.ItemEtherealJarRenderer;
import net.ixios.advancedthaumaturgy.renderers.ItemNodeRenderer;
import net.ixios.advancedthaumaturgy.tileentities.TileEtherealJar;
import net.ixios.advancedthaumaturgy.tileentities.TileFluxDissipator;
import net.ixios.advancedthaumaturgy.tileentities.TileMicrolithBase;
import net.ixios.advancedthaumaturgy.tileentities.TileNodeModifier;
import net.ixios.advancedthaumaturgy.tileentities.TileNodeModifier.Operation;
import net.ixios.advancedthaumaturgy.tileentities.TilePlaceholder;
import net.ixios.advancedthaumaturgy.tileentities.TileThaumicFertilizer;
import net.ixios.advancedthaumaturgy.tileentities.TileEssentiaEngine;
import net.ixios.advancedthaumaturgy.tileentities.TileVulcanizer;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityAuraFX;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends CommonProxy
{
	
	@Override
	public void preRegister()
	{
		
		super.preRegister();
		
        GenericRenderer renderer = new GenericRenderer(new ModelFertilizer());
        MinecraftForgeClient.registerItemRenderer(BlockThaumicFertilizer.blockID, renderer);
        ClientRegistry.bindTileEntitySpecialRenderer(TileThaumicFertilizer.class, renderer);

        renderer = new GenericRenderer(new ModelNodeModifier(), 1F, -0.2F, 1.1F, .4F);
        renderer.setScale(0.3F);
		MinecraftForgeClient.registerItemRenderer(BlockNodeModifier.blockID, renderer);
        ClientRegistry.bindTileEntitySpecialRenderer(TileNodeModifier.class, renderer);
     
		renderer = new GenericRenderer(new ModelVulcanizer());
		MinecraftForgeClient.registerItemRenderer(BlockThaumicVulcanizer.blockID, renderer);
        ClientRegistry.bindTileEntitySpecialRenderer(TileVulcanizer.class, renderer);

    	renderer = new GenericRenderer(null);
		MinecraftForgeClient.registerItemRenderer(BlockPlaceholder.blockID, renderer);
		ClientRegistry.bindTileEntitySpecialRenderer(TilePlaceholder.class, renderer);
        
        renderer = new GenericRenderer(new ModelMinilith());
        MinecraftForgeClient.registerItemRenderer(BlockMicrolith.blockID, renderer);
        ClientRegistry.bindTileEntitySpecialRenderer(TileMicrolithBase.class, renderer);
        
        TileEntitySpecialRenderer special = new BlockEtherealJarRenderer();

        if (AdvThaum.itemEtherealJar != null)
        {
        	MinecraftForgeClient.registerItemRenderer(AdvThaum.itemEtherealJar.itemID, new ItemEtherealJarRenderer((BlockEtherealJarRenderer)special));
        	ClientRegistry.bindTileEntitySpecialRenderer(TileEtherealJar.class, special);
        }
        
        MinecraftForgeClient.registerItemRenderer(BlockCreativeNode.blockID, new ItemNodeRenderer());
        
	}
	
	@Override
	public void postRegister()
	{
		if (AdvThaum.MercurialWand != null)
			MinecraftForgeClient.registerItemRenderer(AdvThaum.MercurialWand.itemID, new ItemWandRenderer());
    
	}
	
	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world,	int x, int y, int z)
	{
		TileEntity te = world.getBlockTileEntity(x,  y,  z);
		switch (ID)
		{
			case GuiNodeModifier.id:
			{
				if (te instanceof TileNodeModifier)
					return new GuiNodeModifier(player, x, y, z);
			}
			break;
			
			default:
				return null;
		}
		return null;
	}
	
	public void createParticle(World world, float srcx, float srcy, float srcz, float dstx, float dsty, float dstz, int color)
	{
		if (Minecraft.getMinecraft().renderViewEntity == null)
			return;
		FXEssentiaTrail fx = new FXEssentiaTrail(world, srcx, srcy, srcz, dstx, dsty, dstz, 25, color, 0.5F);
		fx.noClip = true;
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);
	}
	
	public void createParticle(TileEntity src, float dstx, float dsty, float dstz, int color)
	{
		createParticle(src.worldObj, src.xCoord, src.yCoord, src.zCoord, dstx, dsty, dstz, color);
	}
	
    @Override
    public void createOrbitingParticle(World world, TileEntity te, int lifeticks, double distance, int color) 
    {
    	EntityOrbiterFX fx = new EntityOrbiterFX(world, te.xCoord + 0.5F, te.yCoord + 1.0F, te.zCoord + 0.5F, distance, lifeticks, color);
    	fx.setScale(.3F);
    	fx.angleH = world.rand.nextInt(360);
    	fx.angleV = world.rand.nextInt(360);
    	
        fx.angleHchg = ((world.rand.nextFloat() * 20F) - 10F) * 2F;
    	fx.angleVchg = ((world.rand.nextFloat() * 20F) - 10F) * 2F;
    	//fx.noClip = true;
    	/*fx.angleXchg = 5F;
    	fx.angleYchg = 5F;
    	fx.angleZchg = 5F;*/
    	Minecraft.getMinecraft().effectRenderer.addEffect(fx);
    }
    
    @Override
    public void createEngineParticle(World world, int x, int y, int z, ForgeDirection dir, int color) 
    {
    	CustomParticleFX fx = new CustomParticleFX(world, x + 0.5F, y + 0.5F, z + 0.5F, dir.offsetX / 50D, dir.offsetY / 50D, dir.offsetZ / 50D);
    	fx.noClip = true;
    	fx.setAge(20);
    	fx.setScale(0.5F);
    	fx.setColor(color);
    	Minecraft.getMinecraft().effectRenderer.addEffect(fx);
    }
    
    @Override
    public void createCustomParticle(World world, double srcx, double srcy, double srcz, double chgx, double chgy, double chgz, int color)
    {
    	CustomParticleFX fx = new CustomParticleFX(world, srcx, srcy, srcz);
    	fx.motionX = chgx;
    	fx.motionY = chgy;
    	fx.motionZ = chgz;
    	fx.setColor(color);
    	fx.setAge(20);
    	Minecraft.getMinecraft().effectRenderer.addEffect(fx);
    }
    
    @Override
    public void createFloatyLine(World world, Vector3F src, Vector3F dst, int color)
    {
    	createFloatyLine(world, src, dst, color, false);	
    }
    
    @Override
    public void createFloatyLine(World world, Vector3F src, Vector3F dst, int color, boolean random)
    {
    	FloatyLineFX fx = new FloatyLineFX(world, src, dst, color, random);
    	Minecraft.getMinecraft().effectRenderer.addEffect(fx);
    }

    @Override
    public void createFloatyLine(World world, Vector3F src, Vector3F dst,int color, int age, boolean random)
    {
    	FloatyLineFX fx = new FloatyLineFX(world, src, dst, color, random);
    	fx.setAge(age);
    	Minecraft.getMinecraft().effectRenderer.addEffect(fx);
    }
    
    @Override
    public void createSparkleBurst(World world, float x, float y, float z, int count, int color) 
    {
    	Color c;
     	if (color == -1)
    		c = new Color(world.rand.nextInt(128) + 128, world.rand.nextInt(128) + 128, world.rand.nextInt(128) + 128);
    	else
    		c = new Color(color);
    	
	   for (int i = 0; i < Thaumcraft.proxy.particleCount(count); i++)
        {
		   // world x y z scale r g b multiplier
		   	ColorableSparkleFX fx = new ColorableSparkleFX(world, x + (world.rand.nextFloat() - 0.5f), y + (world.rand.nextFloat() - 0.5f),
            	z + (world.rand.nextFloat() - 0.5f), 1F, c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f,
            	3 + world.rand.nextInt(3));
            fx.setGravity(0.2F);	
            Minecraft.getMinecraft().effectRenderer.addEffect(fx);
        }
    }
      
    @Override
    public void startModification(TileNodeModifier nm, Operation op)
    {
    	super.startModification(nm, op);
    	
    	ByteArrayOutputStream b = new ByteArrayOutputStream();
    	DataOutputStream out = new DataOutputStream (b);
    	
    	try
    	{
	    	out.writeByte(1);
	    	out.writeInt(nm.xCoord);
	    	out.writeInt(nm.yCoord);
	    	out.writeInt(nm.zCoord);
	    	out.writeByte(op.getId());
	    	
	    	Packet250CustomPayload pkt = new Packet250CustomPayload();
	    	pkt.channel = "AdvThaum";
	    	pkt.length = out.size();
	    	pkt.data = b.toByteArray();
	    	
	    	PacketDispatcher.sendPacketToServer(pkt);

    	}
    	catch (IOException io) { }
    }

}
