package net.ixios.advancedthaumaturgy.tileentities;

import thaumcraft.api.IRepairable;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class TileThaumicMender extends TileEntity implements IAspectContainer, ISidedInventory
{

	@Override
	public void updateEntity()
	{
		super.updateEntity();
		if ((worldObj == null) || (worldObj.isRemote))
			return;
		
		ItemStack stack = getStackInSlot(0);
		
		if (stack.getItem() instanceof IRepairable)
		{
			//IRepairable is = (IRepairable)stack.getItem();
			
		}
		
	}
	
	
	
	
	
	
	
	
	@Override
	public int addToContainer(Aspect arg0, int arg1) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int containerContains(Aspect arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean doesContainerContain(AspectList arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean doesContainerContainAmount(Aspect arg0, int arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public AspectList getAspects() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAspects(AspectList arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean takeFromContainer(AspectList arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean takeFromContainer(Aspect arg0, int arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getSizeInventory() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getInvName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isInvNameLocalized() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void openChest() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void closeChest() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int var1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean canInsertItem(int i, ItemStack itemstack, int j) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canExtractItem(int i, ItemStack itemstack, int j) {
		// TODO Auto-generated method stub
		return false;
	}








	@Override
	public boolean doesContainerAccept(Aspect arg0)
	{
		return true;
	}

}
