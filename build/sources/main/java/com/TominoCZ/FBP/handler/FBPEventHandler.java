package com.TominoCZ.FBP.handler;

import com.TominoCZ.FBP.FBP;
import com.TominoCZ.FBP.particle.BlockPlaceAnimationDummy;
import com.TominoCZ.FBP.particle.FBPParticleManager;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.particle.ParticleDigging.Factory;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class FBPEventHandler {

	Minecraft mc;

	public FBPEventHandler() {
		mc = Minecraft.getMinecraft();
	}

	@SubscribeEvent
	public void onEntityJoinWorldEvent(EntityJoinWorldEvent e) {
		if (e.getEntity() instanceof EntityPlayerSP)
			Minecraft.getMinecraft().effectRenderer = new FBPParticleManager(e.getWorld(),
					Minecraft.getMinecraft().getTextureManager(), new Factory());
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onPlayerPlaceBlockEvent(BlockEvent.PlaceEvent e) {
		if (FBP.enabled) {
			IBlockState bs = e.getPlacedBlock();

			BlockPos pos = e.getPos();

			long seed = MathHelper.getPositionRandom(pos);
			
			mc.effectRenderer.addEffect(new BlockPlaceAnimationDummy(mc.theWorld, pos.getX() + 0.5f, pos.getY() + 0.5f,
					pos.getZ() + 0.5f, bs, seed));
		}
	}
}