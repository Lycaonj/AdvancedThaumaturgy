package net.ixios.advancedthaumaturgy.renderers;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.common.blocks.BlockJar;
import thaumcraft.common.blocks.ItemJarFilled;
import thaumcraft.common.config.Config;
import net.ixios.advancedthaumaturgy.AdvThaum;
import net.ixios.advancedthaumaturgy.tileentities.TileEtherealJar;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Icon;
import net.minecraftforge.client.IItemRenderer;

public class ItemEtherealJarRenderer implements IItemRenderer
{

	private BlockEtherealJarRenderer renderer = null;
	
	public ItemEtherealJarRenderer(BlockEtherealJarRenderer renderer)
	{
		this.renderer = renderer;
	}
	
	@Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type)
    {
 	    return true;//(type == ItemRenderType.EQUIPPED) || (type == ItemRenderType.INVENTORY) || (type == ItemRenderType.ENTITY);
    }

	@Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper)
    {
	    return true;
    }

	@Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data)
    {
		switch (type)
		{
        	case ENTITY:
        	{
        		renderInventoryItem(item, -0.5F, 0.0F, 0.5F, 0.5F);
                return;
        	}
          
        	case EQUIPPED:
        	{
        		renderInventoryItem(item, 0.0F, 0.0F, 1.0F, 0.5F);
                return;
        	}
          
        	case INVENTORY:
        	{
        		renderInventoryItem(item, 1.0F, 0.8F, 1.0F, 0.5F);
                return;
        	}
        
        	case EQUIPPED_FIRST_PERSON:
        	{
        		renderInventoryItem(item, 0.0F, 0.0F, 0.0F, 0.5F);
        		return;
        	}
        	
          default:
                  return;
		}
    }

	  public void renderInventoryItem(ItemStack stack, double x, double y, double z, float f)
	    {
	    	AspectList aspects = ((ItemJarFilled)stack.getItem()).getAspects(stack);
	    	
	    	Aspect aspect = null;
	    	int amount = 0;
	    	
	    	if (aspects != null && aspects.size() > 0)
	    	{
	    		aspect = aspects.getAspects()[0];
	    		amount = aspects.getAmount(aspect);
	    	}
	    	
	    	NBTTagCompound stacktag = stack.stackTagCompound;
	    	
	        GL11.glPushMatrix();
	        GL11.glDisable(2884);
	        //GL11.glTranslatef((float)x + 0.5F, (float)y + 0.01F, (float)z + 0.5F);
	        GL11.glRotatef(180F, 1.0F, 0.0F, 0.0F);
	        GL11.glRotatef(0.0F, 0.0F, 0.0F, 1.0F);
	        GL11.glRotatef(0.0F, 1.0F, 0.0F, 0.0F);
	        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	        
	        if (amount > 0)
	            renderInventoryLiquid(stack, x, y, z, f);
	            
	        if (stacktag != null && stacktag.hasKey("filter"))
	        {
	            GL11.glPushMatrix();
	            
	            Aspect filter = Aspect.getAspect(stacktag.getString("filter"));
	            GL11.glPushMatrix();
	            GL11.glTranslatef(0.0F, -0.4F, 0.315F);
	            UtilsFX.renderQuadCenteredFromTexture("textures/models/label.png", 0.5F, 1.0F, 1.0F, 1.0F, -99, 771, 1.0F);
	            GL11.glPopMatrix();
	            GL11.glPushMatrix();
	            GL11.glTranslatef(0.0F, -0.4F, 0.316F);
	            GL11.glScaled(0.021000000000000001D, 0.021000000000000001D, 0.021000000000000001D);
	            GL11.glRotatef(180F, 0.0F, 1.0F, 0.0F);
	            UtilsFX.drawTag(-8, -8, filter);
	            GL11.glPopMatrix();
	            GL11.glPopMatrix();
	        }
	        
	        FMLClientHandler.instance().getClient().renderEngine.bindTexture(renderer.getTexture());
	        
	        //GL11.glColor4f(1F, 1F, 1F, 0.5F);
	        //GL11.glEnable(GL11.GL_BLEND);
	        renderer.getModel().renderAll();
	        //GL11.glDisable(GL11.GL_BLEND);
	        
	        GL11.glEnable(2884);
	        GL11.glPopMatrix();
	    }

	    public void renderInventoryLiquid(ItemStack stack, double x, double y, double z, float f)
	    {
	       
	        AspectList aspects = ((ItemJarFilled)stack.getItem()).getAspects(stack);
	    	Aspect aspect = aspects.getAspects()[0];
	    	int amount = aspects.getAmount(aspect);
	        int maxAmount = TileEtherealJar.maxAmt;
	        
	        GL11.glPushMatrix();
	        GL11.glRotatef(180F, 1.0F, 0.0F, 0.0F);
	        
	        RenderBlocks renderBlocks = new RenderBlocks();
	        GL11.glDisable(2896);
	        float level = ((float)amount / (float)maxAmount) * 0.625F;
	        Tessellator t = Tessellator.instance;
	        renderBlocks.setRenderBounds(0.25D, 0.0625D, 0.25D, 0.75D, 0.0625D + (double)level, 0.75D);
	        t.startDrawingQuads();
	        
	        if (aspect != null)
	            t.setColorOpaque_I(aspect.getColor());
	        
	        int bright = 200;
	        t.setBrightness(bright);
	        
	        Icon icon = ((BlockJar)Block.blocksList[Config.blockJarId]).iconLiquid;
	        
	        FMLClientHandler.instance().getClient().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
	        
	        Block jar = AdvThaum.EtherealJar;
	        
	        renderBlocks.renderFaceYNeg(jar, -0.5D, 0.0D, -0.5D, icon);
	        renderBlocks.renderFaceYPos(jar, -0.5D, 0.0D, -0.5D, icon);
	        renderBlocks.renderFaceZNeg(jar, -0.5D, 0.0D, -0.5D, icon);
	        renderBlocks.renderFaceZPos(jar, -0.5D, 0.0D, -0.5D, icon);
	        renderBlocks.renderFaceXNeg(jar, -0.5D, 0.0D, -0.5D, icon);
	        renderBlocks.renderFaceXPos(jar, -0.5D, 0.0D, -0.5D, icon);
	        
	        t.draw();
	        
	        GL11.glEnable(2896);
	        GL11.glPopMatrix();
	        GL11.glColor3f(1.0F, 1.0F, 1.0F);
	    }
	    
}
