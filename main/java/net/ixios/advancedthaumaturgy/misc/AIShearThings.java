package net.ixios.advancedthaumaturgy.misc;

import thaumcraft.common.entities.golems.EntityGolemBase;
import net.minecraft.entity.ai.EntityAIBase;

public class AIShearThings extends EntityAIBase
{

	public AIShearThings(EntityGolemBase base)
    {
        
    }

	@Override
    public boolean shouldExecute()
    {
	    return false;
    }
	
}
