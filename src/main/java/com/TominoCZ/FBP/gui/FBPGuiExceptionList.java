package com.TominoCZ.FBP.gui;

import java.io.IOException;

import com.TominoCZ.FBP.FBP;
import com.TominoCZ.FBP.handler.FBPConfigHandler;

import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class FBPGuiExceptionList extends GuiScreen {
	private GuiTextField search;

	int ID = 1;

	Block b;

	GuiScreen parent;

	boolean okToAdd = false;

	FBPGuiButton buttonAdd, buttonRemove;

	public FBPGuiExceptionList(GuiScreen parent) {
		this.parent = parent;
	}

	public void initGui() {
		this.buttonList.clear();

		int width = 100;
		int height = 20;

		search = new GuiTextField(0, mc.fontRendererObj, this.width / 2 - width / 2, this.height / 2 - 75, width,
				height);
		search.setFocused(true);
		search.setCanLoseFocus(true);
		search.setText((ID = FBP.lastIDAdded) + "");

		buttonAdd = new FBPGuiButton(1, search.xPosition, search.yPosition + 130, "\u00A7aADD", false, false);
		buttonRemove = new FBPGuiButton(2, buttonAdd.xPosition, buttonAdd.yPosition + 20, "\u00A7cREMOVE", false,
				false);
		buttonAdd.width = buttonRemove.width = width;

		okToAdd = !FBP.INSTANCE.isInExceptions(b = Block.getBlockById(FBP.lastIDAdded));

		this.buttonList.add(buttonAdd);
		this.buttonList.add(buttonRemove);
	}

	@Override
	protected void mouseClicked(int x, int y, int button) throws IOException {
		super.mouseClicked(x, y, button);
		search.mouseClicked(x, y, button);
		if (button == 1 && x >= search.xPosition && x < search.xPosition + search.width && y >= search.yPosition
				&& y < search.yPosition + search.height) {
			search.setText("");
		}
	}

	@Override
	protected void keyTyped(char c, int keyCode) throws IOException {
		if (keyCode == 1) {
			closeGui();
			return;
		}

		super.keyTyped(c, keyCode);

		if (keyCode != 14 && keyCode != 200 && keyCode != 208)
			Integer.parseInt("" + c);

		search.textboxKeyTyped(c, keyCode);
		try {
			ID = Integer.parseInt(search.getText());

			if (keyCode == 200 && ID < Integer.MAX_VALUE - 1)
				search.setText(++ID + "");
			else if (keyCode == 208 && ID > 0)
				search.setText(--ID + "");
			b = Block.getBlockById(FBP.lastIDAdded = ID);
		} catch (Exception e) {
			for (ResourceLocation rl : Block.REGISTRY.getKeys()) {
				if (rl.toString().contains(search.getText())) {
					b = Block.getBlockFromName(rl.toString());
				}
			}
		}

		okToAdd = !FBP.INSTANCE.isInExceptions(b);
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		search.updateCursorCounter();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		parent.width = this.width;
		parent.height = this.height;

		parent.initGui();
		parent.drawScreen(0, 0, partialTicks);

		this.drawDefaultBackground();
		search.drawTextBox();

		Block b = Block.REGISTRY.getObjectById(ID);

		String name = "";

		buttonAdd.enabled = okToAdd;
		buttonRemove.enabled = !okToAdd;

		if (b != null) {
			String itemName = I18n.format(b.getRegistryName().getResourcePath());
			// Item i = Item.getItemFromBlock(b);
			name = itemName;
			
			// if (i == null)
			// i = Item.getByNameOrId(b.getRegistryName().getResourceDomain() + ':' +
			// itemName);

			// if (i != null)
			// drawStack(new ItemStack(i));
			// else

			drawStack(new ItemStack(b, 1, 0));
		} else {
			buttonAdd.enabled = false;
			name = "";
		}

		this.drawCenteredString(fontRendererObj, name, buttonAdd.xPosition + buttonAdd.width / 2,
				buttonAdd.yPosition - 25, fontRendererObj.getColorCode('6'));

		this.drawCenteredString(fontRendererObj, "\u00A7LAdd Exception For Blocks", width / 2, 20,
				fontRendererObj.getColorCode('a'));

		this.drawCenteredString(fontRendererObj, "NOTE:", width / 2, height - 22, fontRendererObj.getColorCode('6'));

		this.drawCenteredString(fontRendererObj, "*NOT ALL BLOCKS ARE LISTED HERE*", width / 2, height - 12,
				fontRendererObj.getColorCode('c'));

		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	private void drawStack(ItemStack itemstack) {
		GlStateManager.enableDepth();
		GlStateManager.enableLight(0);

		int x = search.xPosition + search.width / 2 - 32;
		int y = search.yPosition + 30;

		GlStateManager.translate(x, y, 0);
		GlStateManager.scale(4, 4, 4);

		this.itemRender.renderItemAndEffectIntoGUI(itemstack, 0, 0);

		GlStateManager.scale(0.25, 0.25, 0.25);
		GlStateManager.translate(-x, -y, 0);

		this.itemRender.zLevel = 0.0F;
		this.zLevel = 0.0F;
	}

	protected void actionPerformed(GuiButton button) throws IOException {
		switch (button.id) {
		case 1:
			FBP.INSTANCE.addException(b);

			FBPConfigHandler.writeExceptions();
			break;
		case 2:
			FBP.INSTANCE.removeException(b);

			FBPConfigHandler.writeExceptions();
			break;
		}

		okToAdd = !FBP.INSTANCE.isInExceptions(b);
	}

	void closeGui() {
		mc.displayGuiScreen(parent);
	}
}