package com.TominoCZ.FBP.gui;

import java.awt.Desktop;
import java.awt.Dimension;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;

import javax.vecmath.Vector2d;

import com.TominoCZ.FBP.FBP;
import com.TominoCZ.FBP.handler.FBPConfigHandler;
import com.TominoCZ.FBP.math.FBPMathHelper;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FBPGuiMenuPage1 extends GuiScreen {
	GuiButton Reload, Done, Defaults, Next, ReportBug, Enable, MinDurationPlus, MinDurationMinus, MaxDurationPlus,
			InfiniteDuration, MaxDurationMinus, ScaleMultPlus, ScaleMultMinus, GravitiyForcePlus, GravitiyForceMinus,
			RotSpeedPlus, RotSpeedMinus;

	boolean mouseOver = false;

	Vector2d lastHandle = new Vector2d(0, 0);
	Vector2d lastSize = new Vector2d(0, 0);

	Vector2d handle = new Vector2d(0, 0);
	Vector2d size = new Vector2d(0, 0);

	long time, lastTime;

	int selected = 0;

	double offsetX = 0;

	public void initGui() {
		this.buttonList.clear();

		int x1 = this.width / 2 + 78 + 2;
		int x2 = this.width / 2 - 100;

		MinDurationPlus = new FBPGuiButton(1, x1, (int) (this.height / 5) - 10, "+", false, false);
		MinDurationMinus = new FBPGuiButton(2, x2, (int) MinDurationPlus.yPosition, "-", false, false);

		MaxDurationPlus = new FBPGuiButton(3, x1, (int) MinDurationMinus.yPosition + MinDurationMinus.height + 1, "+",
				false, false);
		MaxDurationMinus = new FBPGuiButton(4, x2, (int) MinDurationMinus.yPosition + MinDurationMinus.height + 1, "-",
				false, false);

		InfiniteDuration = new FBPGuiButton(11, x1 + 25, (int) MinDurationPlus.yPosition + 10,
				(FBP.infiniteDuration ? "\u00A7a" : "\u00A7c") + "\u221e", false, false);

		ScaleMultPlus = new FBPGuiButton(5, x1,
				(int) MaxDurationPlus.yPosition + MaxDurationPlus.height + 6 + MaxDurationPlus.height / 2, "+", false,
				false);
		ScaleMultMinus = new FBPGuiButton(6, x2,
				(int) MaxDurationPlus.yPosition + MaxDurationPlus.height + 6 + MaxDurationPlus.height / 2, "-", false,
				false);
		GravitiyForcePlus = new FBPGuiButton(7, x1,
				(int) ScaleMultMinus.yPosition + ScaleMultPlus.height / 2 + 1 + ScaleMultMinus.height + 6, "+", false,
				false);
		GravitiyForceMinus = new FBPGuiButton(8, x2,
				(int) ScaleMultMinus.yPosition + ScaleMultPlus.height / 2 + 1 + ScaleMultMinus.height + 6, "-", false,
				false);

		RotSpeedPlus = new FBPGuiButton(9, x1, (int) GravitiyForceMinus.yPosition + GravitiyForceMinus.height + 1, "+",
				false, false);
		RotSpeedMinus = new FBPGuiButton(10, x2, (int) GravitiyForceMinus.yPosition + GravitiyForceMinus.height + 1,
				"-", false, false);

		Defaults = new FBPGuiButton(0, this.width / 2 + 2, RotSpeedMinus.yPosition + RotSpeedMinus.height + 24,
				"Defaults", false, false);
		Done = new FBPGuiButton(-1, x2, (int) Defaults.yPosition, "Done", false, false);
		Reload = new FBPGuiButton(-2, x2, (int) Defaults.yPosition + Defaults.height + 1, "Reload Config", false,
				false);
		ReportBug = new FBPGuiButtonBugReport(-4, this.width - 27, 2, new Dimension(width, height),
				this.fontRendererObj);
		Enable = new FBPGuiButtonEnable(-6, (this.width - 25 - 27) - 4, 2, new Dimension(width, height),
				this.fontRendererObj);
		Defaults.width = Done.width = 98;
		Reload.width = 96 * 2 + 8;
		Next = new FBPGuiButton(-3, RotSpeedMinus.xPosition + RotSpeedMinus.width + 3 + 2,
				(int) RotSpeedMinus.yPosition, ">>", false, false);

		InfiniteDuration.width = MinDurationPlus.width = MinDurationMinus.width = MaxDurationPlus.width = MaxDurationMinus.width = ScaleMultPlus.width = ScaleMultMinus.width = GravitiyForcePlus.width = GravitiyForceMinus.width = RotSpeedPlus.width = RotSpeedMinus.width = Next.width = 20;

		this.buttonList.addAll(Arrays.asList(new GuiButton[] { MinDurationPlus, MinDurationMinus, MaxDurationPlus,
				InfiniteDuration, MaxDurationMinus, ScaleMultPlus, ScaleMultMinus, GravitiyForcePlus,
				GravitiyForceMinus, RotSpeedPlus, RotSpeedMinus, Defaults, Done, Reload, Next, Enable, ReportBug }));
	}

	protected void actionPerformed(GuiButton button) throws IOException {
		switch (button.id) {
		case -6:
			FBP.enabled = !FBP.enabled;
			break;
		case -5:
			FBP.showInMillis = !FBP.showInMillis;
			break;
		case -4:
			try {
				Desktop.getDesktop().browse(new URI("https://github.com/TominoCZ/FancyBlockParticles/issues"));
			} catch (Exception e) {

			}
			break;
		case -3:
			this.mc.displayGuiScreen(new FBPGuiMenuPage2());
			break;
		case -2:
			FBPConfigHandler.init();
			break;
		case -1:
			this.mc.displayGuiScreen((GuiScreen) null);
			break;
		case 0:
			this.mc.displayGuiScreen(new FBPGuiYesNo(this));
			break;
		case 1:
			if (FBP.minAge == FBP.maxAge)
				FBP.maxAge += 5;
			FBP.minAge += 5;
			break;
		case 2:
			FBP.minAge -= 5;
			break;
		case 3:
			FBP.maxAge += 5;
			break;
		case 4:
			if (FBP.minAge == FBP.maxAge)
				FBP.minAge -= 5;
			FBP.maxAge -= 5;
			break;
		case 5:
			FBP.scaleMult = FBPMathHelper.round(FBP.scaleMult += 0.05D, 2);
			break;
		case 6:
			FBP.scaleMult = FBPMathHelper.round(FBP.scaleMult -= 0.05D, 2);
			break;
		case 7:
			FBP.gravityMult = FBPMathHelper.round(FBP.gravityMult += 0.1D, 1);
			break;
		case 8:
			FBP.gravityMult = FBPMathHelper.round(FBP.gravityMult -= 0.1D, 1);
			break;
		case 9:
			FBP.rotationMult = FBPMathHelper.round(FBP.rotationMult += 0.1D, 1);
			break;
		case 10:
			FBP.rotationMult = FBPMathHelper.round(FBP.rotationMult -= 0.1D, 1);
			break;
		case 11:
			InfiniteDuration.displayString = ((FBP.infiniteDuration = !FBP.infiniteDuration) ? "\u00A7a" : "\u00A7c")
					+ "\u221e";
			break;
		}

		FBPConfigHandler.check();
		FBPConfigHandler.write();
	}

	public boolean doesGuiPauseGame() {
		return true;
	}

	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawBackground(0);

		FBPGuiHelper.background(MinDurationPlus.yPosition - 6, Done.yPosition - 4, width, height);

		drawInfo();

		drawMouseOverSelection(mouseX, mouseY, partialTicks);

		FBPGuiHelper.drawTitle(MinDurationPlus.yPosition, width, height, fontRendererObj);

		update();

		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	private void drawMouseOverSelection(int mouseX, int mouseY, float partialTicks) {
		mouseOver = false;

		int posY = Done.yPosition - 18;

		if (((mouseX >= MinDurationMinus.xPosition + MinDurationMinus.width) && (mouseX <= MinDurationPlus.xPosition))
				&& (mouseY >= MinDurationMinus.yPosition)
				&& (mouseY <= MaxDurationMinus.yPosition + MaxDurationMinus.height - 2)) {
			handle.y = MinDurationPlus.yPosition;
			size = new Vector2d(MinDurationPlus.xPosition - (MinDurationMinus.xPosition + MinDurationMinus.width), 39);
			selected = 1;
			mouseOver = true;
		} else if (((mouseX >= ScaleMultMinus.xPosition + ScaleMultMinus.width) && (mouseX <= ScaleMultPlus.xPosition))
				&& (mouseY >= (ScaleMultMinus.yPosition + 1))
				&& (mouseY <= (ScaleMultPlus.yPosition + ScaleMultPlus.height - 1) - 1)) {
			handle.y = ScaleMultPlus.yPosition;
			size = new Vector2d(ScaleMultPlus.xPosition - (ScaleMultMinus.xPosition + ScaleMultMinus.width), 18);
			selected = 2;
		} else if (((mouseX >= GravitiyForceMinus.xPosition + GravitiyForceMinus.width)
				&& (mouseX <= GravitiyForcePlus.xPosition)) && (mouseY >= GravitiyForceMinus.yPosition + 1)
				&& (mouseY <= GravitiyForcePlus.yPosition + GravitiyForcePlus.height - 1)) {
			handle.y = GravitiyForceMinus.yPosition;
			size = new Vector2d(GravitiyForcePlus.xPosition - (GravitiyForceMinus.xPosition + GravitiyForceMinus.width),
					18);
			selected = 3;
		} else if (((mouseX >= RotSpeedMinus.xPosition + RotSpeedMinus.width) && (mouseX <= RotSpeedPlus.xPosition))
				&& (mouseY >= RotSpeedMinus.yPosition + 1)
				&& (mouseY <= RotSpeedPlus.yPosition + RotSpeedMinus.height - 1)) {
			handle.y = RotSpeedMinus.yPosition;
			size = new Vector2d(RotSpeedPlus.xPosition - (RotSpeedMinus.xPosition + RotSpeedMinus.width), 18);
			selected = 4;
		} else if (InfiniteDuration.isMouseOver())
			selected = 5;

		int step = 1;
		time = System.currentTimeMillis();

		if (lastTime > 0)
			step = (int) (time - lastTime);

		lastTime = time;

		if (lastHandle != new Vector2d(0, 0)) {
			if (lastHandle.y > handle.y) {
				if (lastHandle.y - handle.y <= step)
					lastHandle.y = handle.y;
				else
					lastHandle.y -= step;
			}

			if (lastHandle.y < handle.y) {
				if (handle.y - lastHandle.y <= step)
					lastHandle.y = handle.y;
				else
					lastHandle.y += step;
			}

			lastHandle.x = MinDurationMinus.xPosition + MinDurationPlus.width;
		}

		if (lastSize != new Vector2d(0, 0)) {
			if (lastSize.y > size.y)
				if (lastSize.y - size.y <= step)
					lastSize.y = size.y;
				else
					lastSize.y -= step;

			if (lastSize.y < size.y)
				if (size.y - lastSize.y <= step)
					lastSize.y = size.y;
				else
					lastSize.y += step;

			if (lastSize.x > size.x)
				lastSize.x -= step;
			if (lastSize.x < size.x)
				lastSize.x += step;

			lastSize.x = GravitiyForcePlus.xPosition - (GravitiyForceMinus.xPosition + GravitiyForceMinus.width);
		}

		String text = "";

		switch (selected) {
		case 1:
			String _text = (FBP.minAge != FBP.maxAge
					? ("range\u00A7a to between \u00A76" + (FBP.showInMillis ? FBP.minAge * 50 : FBP.minAge)
							+ "\u00A7a and \u00A76" + (FBP.showInMillis ? FBP.maxAge * 50 : FBP.maxAge)
							+ (FBP.showInMillis ? "ms" : (FBP.maxAge > 1 ? " ticks" : " tick")))
					: ("\u00A7ato \u00A76" + (FBP.showInMillis ? FBP.maxAge * 50 : FBP.maxAge)
							+ (FBP.showInMillis ? "ms" : (FBP.maxAge > 1 ? " ticks" : " tick"))));

			text = "Sets \u00A76particle life duration " + _text + "\u00A7a.";
			break;
		case 2:
			text = "Sets \u00A76particle scale multiplier \u00A7ato \u00A76" + FBP.scaleMult + "\u00A7a.";
			break;
		case 3:
			text = "Multiplies \u00A76default particle gravity force\u00A7a by \u00A76" + FBP.gravityMult + "\u00A7a.";
			break;
		case 4:
			text = "Multiplies \u00A76particle rotation\u00A7a by \u00A76" + FBP.rotationMult + "\u00A7a.";
			break;
		case 5:
			text = (FBP.infiniteDuration ? "\u00A7cDisables" : "Enables")
					+ " \u00A76infinite particle life duration\u00A7a.";
			break;
		default:
			text = "";
		}

		if (((mouseX >= MinDurationMinus.xPosition + MinDurationMinus.width && mouseX <= MinDurationPlus.xPosition)
				&& (mouseY < RotSpeedMinus.yPosition + RotSpeedMinus.height && mouseY >= MinDurationMinus.yPosition)
				&& (lastSize.y <= 20 || (lastSize.y < 50 && lastSize.y > 20))
				&& lastHandle.y >= MinDurationPlus.yPosition) || InfiniteDuration.isMouseOver()) {
			moveText(text);

			if (selected == 1)
				this.drawCenteredString(fontRendererObj, !FBP.showInMillis ? "show in ms" : "show in ticks",
						this.width / 2, MinDurationPlus.yPosition + MinDurationPlus.width - 5,
						fontRendererObj.getColorCode('c'));

			if (selected != 5)
				FBPGuiHelper.drawRect(lastHandle.x, lastHandle.y + 1, lastSize.x, lastSize.y, 200, 200, 200, 35);

			this.drawCenteredString(fontRendererObj, text, (int) (this.width / 2 + offsetX), posY,
					fontRendererObj.getColorCode('a'));
		}
	}

	private void drawInfo() {
		int posY = Done.yPosition - 18;

		String s;

		if (FBP.infiniteDuration)
			s = "Min. Duration" + " [\u00A76" + "\u221e" + (FBP.showInMillis ? " ms" : " ticks") + "\u00A7f]";
		else
			s = "Min. Duration [\u00A76" + (FBP.showInMillis ? ((FBP.minAge * 50) + "ms")
					: (FBP.minAge + (FBP.minAge > 1 ? " ticks" : " tick"))) + "\u00A7f]";
		this.drawCenteredString(fontRendererObj, s, this.width / 2, MinDurationPlus.yPosition + 6,
				fontRendererObj.getColorCode('f'));

		if (FBP.infiniteDuration)
			s = "Max. Duration" + " [\u00A76" + "\u221e" + (FBP.showInMillis ? " ms" : " ticks") + "\u00A7f]";
		else
			s = "Max. Duration [\u00A76" + (FBP.showInMillis ? ((FBP.maxAge * 50) + "ms")
					: (FBP.maxAge + (FBP.maxAge > 1 ? " ticks" : " tick"))) + "\u00A7f]";
		this.drawCenteredString(fontRendererObj, s, this.width / 2, MaxDurationPlus.yPosition + 6,
				fontRendererObj.getColorCode('f'));

		this.drawCenteredString(fontRendererObj,
				"Scale Mult. [\u00A76" + FBPMathHelper.round(FBP.scaleMult, 2) + "\u00A7f]", this.width / 2,
				ScaleMultMinus.yPosition + 6, fontRendererObj.getColorCode('f'));
		this.drawCenteredString(fontRendererObj,
				"Gravity Force Mult. [\u00A76" + FBPMathHelper.round(FBP.gravityMult, 1) + "\u00A7f]", this.width / 2,
				GravitiyForcePlus.yPosition + 6, fontRendererObj.getColorCode('f'));
		this.drawCenteredString(fontRendererObj,
				"Rotation Speed Mult. [\u00A76" + (FBP.rotationMult != 0
						? String.valueOf(FBPMathHelper.round(FBP.rotationMult, 1)) : FBPGuiHelper.off) + "\u00A7f]",
				this.width / 2, RotSpeedPlus.yPosition + 6, fontRendererObj.getColorCode('f'));
	}

	private void moveText(String text) {
		int textWidth = this.fontRendererObj.getStringWidth(text);
		int outsideSizeX = textWidth - this.width;

		if (textWidth > width) {
			double speedOfSliding = 2400;
			long time = System.currentTimeMillis();

			float normalValue = (float) ((time / speedOfSliding) % 2);

			if (normalValue > 1)
				normalValue = 2 - normalValue;

			offsetX = (outsideSizeX * 2) * normalValue - outsideSizeX;
		} else
			offsetX = 0;
	}

	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		if (mouseButton == 0) {
			for (int i = 0; i < this.buttonList.size(); ++i) {
				GuiButton guibutton = (GuiButton) this.buttonList.get(i);

				if (guibutton.mousePressed(this.mc, mouseX, mouseY)) {
					net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Pre event = new net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Pre(
							this, guibutton, this.buttonList);
					if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event))
						break;
					guibutton = event.getButton();
					guibutton.playPressSound(this.mc.getSoundHandler());
					this.actionPerformed(guibutton);
					if (this.equals(this.mc.currentScreen))
						net.minecraftforge.common.MinecraftForge.EVENT_BUS
								.post(new net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Post(this,
										event.getButton(), this.buttonList));
				}
			}
		}

		if (mouseOver)
			this.actionPerformed(new GuiButton(-5, 0, 0, 0, 0, ""));
	}

	void update() {
		MinDurationPlus.enabled = FBP.minAge < 100 && !FBP.infiniteDuration;
		MinDurationMinus.enabled = FBP.minAge > 10 && !FBP.infiniteDuration;

		MaxDurationPlus.enabled = FBP.maxAge < 100 && !FBP.infiniteDuration;
		MaxDurationMinus.enabled = FBP.maxAge > 10 && !FBP.infiniteDuration;

		ScaleMultPlus.enabled = FBP.scaleMult < 1.25D;
		ScaleMultMinus.enabled = FBP.scaleMult > 0.75D;

		GravitiyForcePlus.enabled = FBP.gravityMult < 2.0D;
		GravitiyForceMinus.enabled = FBP.gravityMult > 0.5D;
		RotSpeedPlus.enabled = FBP.rotationMult < 1.5D;
		RotSpeedMinus.enabled = FBP.rotationMult > 0;
	}
}