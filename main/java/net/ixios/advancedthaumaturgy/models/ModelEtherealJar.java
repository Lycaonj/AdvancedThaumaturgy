package net.ixios.advancedthaumaturgy.models;


import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import org.lwjgl.opengl.GL11;

public class ModelEtherealJar extends ModelBase implements IModelContainer
{

    ModelRenderer Core;
    ModelRenderer Brine;
    ModelRenderer Lid;

    public ModelEtherealJar()
    {
        super.textureWidth = 64;
        super.textureHeight = 32;
        Core = new ModelRenderer(this, 0, 0);
        Core.addBox(-5F, -12F, -5F, 10, 12, 10);
        Core.setRotationPoint(0.0F, 0.0F, 0.0F);
        Core.setTextureSize(64, 32);
        Core.mirror = true;
        setRotation(Core, 0.0F, 0.0F, 0.0F);
        Brine = new ModelRenderer(this, 0, 0);
        Brine.addBox(-4F, -11F, -4F, 8, 10, 8);
        Brine.setRotationPoint(0.0F, 0.0F, 0.0F);
        Brine.setTextureSize(64, 32);
        Brine.mirror = true;
        setRotation(Brine, 0.0F, 0.0F, 0.0F);
        Lid = new ModelRenderer(this, 0, 24);
        Lid.addBox(-3F, 0.0F, -3F, 6, 2, 6);
        Lid.setRotationPoint(0.0F, -14F, 0.0F);
        Lid.setTextureSize(64, 32);
        Lid.mirror = true;
        setRotation(Lid, 0.0F, 0.0F, 0.0F);
    }

    private void renderBrine()
    {
        GL11.glDepthMask(false);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        Brine.render(0.0625F);
        GL11.glDisable(3042);
        GL11.glDepthMask(true);
    }

    @Override
    public void render()
    {
        Lid.render(0.0625F);
        GL11.glDepthMask(false);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        Core.render(0.0625F);
        GL11.glDisable(3042);
        GL11.glDepthMask(true);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z)
    {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

}
