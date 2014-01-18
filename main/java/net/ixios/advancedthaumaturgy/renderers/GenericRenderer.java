package net.ixios.advancedthaumaturgy.renderers;

import net.ixios.advancedthaumaturgy.models.IModelContainer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.SideOnly;
import cpw.mods.fml.relauncher.Side;

@SideOnly(Side.CLIENT)
public class GenericRenderer extends TileEntitySpecialRenderer implements IItemRenderer
{
	
	private IModelContainer model = null;
	private float inventoryX = 1.0F;
	private float inventoryY = 0.0F;
	private float inventoryZ = 1.0F;
	private float inventoryScale = 0.5F;
	private float scaleAll = 0.5F;
	
	public GenericRenderer(IModelContainer model)
	{
		this.model = model;
	}

	public GenericRenderer(IModelContainer model, float inventoryx, float inventoryy, float inventoryz, float inventoryscale)
	{
		this(model);
		inventoryX = inventoryx;
		inventoryY = inventoryy;
		inventoryZ = inventoryz;
		inventoryScale = inventoryscale;
	}
	
	public void setScale(float scale)
	{
		scaleAll = scale;
	}
	
	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type)
	{
		return true;
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
        		renderHWB(-0.5F, 0.0F, 0.5F, scaleAll);
                return;
        	}
          
        	case EQUIPPED:
        	{
        		renderHWB(0.0F, 0.0F, 1.0F, scaleAll);
                return;
        	}
          
        	case INVENTORY:
        	{
        		renderHWB(inventoryX, inventoryY, inventoryZ, inventoryScale);
                return;
          }
          default:
                  return;
		}
	}
		
	 private void renderHWB(float x, float y, float z, float scale) 
	 {
		 if (model == null)
			 return;
		 
		 GL11.glPushMatrix();
         GL11.glDisable(GL11.GL_LIGHTING);

         // Scale, Translate, Rotate
         GL11.glScalef(scale, scale, scale);
         GL11.glTranslatef(x, y, z);

         model.render();

         GL11.glEnable(GL11.GL_LIGHTING);
         GL11.glPopMatrix();
	 }

	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z, float scale)
	{
		if (model == null)
			return;
		
		 GL11.glPushMatrix();
         //GL11.glDisable(GL11.GL_LIGHTING);

         GL11.glTranslated(x + .5, y, z + .5);
         GL11.glScaled(.5, .5, .5);
     
         model.render();

         GL11.glPopMatrix();	
	}
 
}
