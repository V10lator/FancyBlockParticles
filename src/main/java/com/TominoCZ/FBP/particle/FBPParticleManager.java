package com.TominoCZ.FBP.particle;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.Map;

import javax.annotation.Nullable;

import com.TominoCZ.FBP.FBP;
import com.google.common.base.Throwables;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleDigging;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FBPParticleManager extends ParticleManager {
	private static MethodHandle getParticleTypes;

	private static IParticleFactory particleFactory;
	private static IBlockState blockState;

	public FBPParticleManager(World worldIn, TextureManager rendererIn, IParticleFactory particleFactory) {
		super(worldIn, rendererIn);

		this.particleFactory = particleFactory;

		MethodHandles.Lookup lookup = MethodHandles.publicLookup();

		try {
			getParticleTypes = lookup.unreflectGetter(
					ReflectionHelper.findField(ParticleManager.class, "field_178932_g", "particleTypes"));
		} catch (IllegalAccessException e) {
			throw Throwables.propagate(e);
		}
	}

	@Nullable
	@Override
	public Particle spawnEffectParticle(int particleId, double xCoord, double yCoord, double zCoord, double xSpeed,
			double ySpeed, double zSpeed, int... parameters) {
		IParticleFactory iparticlefactory = null;

		try {
			iparticlefactory = (IParticleFactory) ((Map<Integer, IParticleFactory>) getParticleTypes
					.invokeExact((ParticleManager) this)).get(Integer.valueOf(particleId));
		} catch (Throwable e) {
			e.printStackTrace();
		}

		if (iparticlefactory != null) {
			Particle particle = iparticlefactory.createParticle(particleId, this.worldObj, xCoord, yCoord, zCoord,
					xSpeed, ySpeed, zSpeed, parameters), toSpawn = null;

			if (FBP.enabled) {
				if (particle instanceof ParticleDigging) {
					blockState = Block.getStateById(parameters[0]);
					if (blockState != null
							&& (!(blockState.getBlock() instanceof BlockLiquid)
									&& !(FBP.frozen && !FBP.spawnWhileFrozen))
							&& (FBP.spawnRedstoneBlockParticles || blockState.getBlock() != Blocks.REDSTONE_BLOCK))
						toSpawn = new FBPParticle(this.worldObj, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed,
								blockState, EnumFacing.UP).multipleParticleScaleBy(0.6F);
				}
			} else
				toSpawn = particle;
			
			this.addEffect(toSpawn);
			return toSpawn;
		}

		return null;
	}

	@Override
	public void addBlockDestroyEffects(BlockPos pos, IBlockState state) {
		if (!state.getBlock().isAir(state, worldObj, pos) && !state.getBlock().addDestroyEffects(worldObj, pos, this)) {
			state = state.getActualState(worldObj, pos);
			int i = 4;

			for (int j = 0; j < 4; ++j) {
				for (int k = 0; k < 4; ++k) {
					for (int l = 0; l < 4; ++l) {
						double d0 = (double) pos.getX() + ((double) j + 0.5D) / 4.0D;
						double d1 = (double) pos.getY() + ((double) k + 0.5D) / 4.0D;
						double d2 = (double) pos.getZ() + ((double) l + 0.5D) / 4.0D;

						try {
							if (FBP.enabled) {
								if (state != null
										&& (!(state.getBlock() instanceof BlockLiquid)
												&& !(FBP.frozen && !FBP.spawnWhileFrozen))
										&& (FBP.spawnRedstoneBlockParticles
												|| state.getBlock() != Blocks.REDSTONE_BLOCK))
									addEffect(new FBPParticle(worldObj, d0, d1, d2, d0 - (double) pos.getX() - 0.5D,
											d1 - (double) pos.getY() - 0.5D, d2 - (double) pos.getZ() - 0.5D, state,
											null));
							} else
								addEffect((particleFactory.createParticle(0, this.worldObj, d0, d1, d2,
										d0 - (double) pos.getX() - 0.5D, d1 - (double) pos.getY() - 0.5D,
										d2 - (double) pos.getZ() - 0.5D, Block.getStateId(state))));
						} catch (Throwable e) {

						}
					}
				}
			}
		}
	}

	@Override
	public void addBlockHitEffects(BlockPos pos, EnumFacing side) {
		IBlockState iblockstate = worldObj.getBlockState(pos);

		if (iblockstate.getRenderType() != EnumBlockRenderType.INVISIBLE) {
			int i = pos.getX();
			int j = pos.getY();
			int k = pos.getZ();
			float f = 0.1F;
			AxisAlignedBB axisalignedbb = iblockstate.getBoundingBox(worldObj, pos);
			double d0 = (double) i
					+ worldObj.rand.nextDouble() * (axisalignedbb.maxX - axisalignedbb.minX - 0.20000000298023224D)
					+ 0.10000000149011612D + axisalignedbb.minX;
			double d1 = (double) j
					+ worldObj.rand.nextDouble() * (axisalignedbb.maxY - axisalignedbb.minY - 0.20000000298023224D)
					+ 0.10000000149011612D + axisalignedbb.minY;
			double d2 = (double) k
					+ worldObj.rand.nextDouble() * (axisalignedbb.maxZ - axisalignedbb.minZ - 0.20000000298023224D)
					+ 0.10000000149011612D + axisalignedbb.minZ;

			if (side == EnumFacing.DOWN) {
				d1 = (double) j + axisalignedbb.minY - 0.10000000149011612D;
			}

			if (side == EnumFacing.UP) {
				d1 = (double) j + axisalignedbb.maxY + 0.10000000149011612D;
			}

			if (side == EnumFacing.NORTH) {
				d2 = (double) k + axisalignedbb.minZ - 0.10000000149011612D;
			}

			if (side == EnumFacing.SOUTH) {
				d2 = (double) k + axisalignedbb.maxZ + 0.10000000149011612D;
			}

			if (side == EnumFacing.WEST) {
				d0 = (double) i + axisalignedbb.minX - 0.10000000149011612D;
			}

			if (side == EnumFacing.EAST) {
				d0 = (double) i + axisalignedbb.maxX + 0.10000000149011612D;
			}

			try {
				if (FBP.enabled) {
					if (iblockstate != null
							&& (!(iblockstate.getBlock() instanceof BlockLiquid)
									&& !(FBP.frozen && !FBP.spawnWhileFrozen))
							&& (FBP.spawnRedstoneBlockParticles || iblockstate.getBlock() != Blocks.REDSTONE_BLOCK))
						addEffect(new FBPParticle(worldObj, d0, d1, d2, 0.0D, 0.0D, 0.0D, iblockstate, side)
								.multiplyVelocity(0.2F).multipleParticleScaleBy(0.6F));
				} else
					addEffect(particleFactory
							.createParticle(0, worldObj, d0, d1, d2, 0.0D, 0.0D, 0.0D, Block.getStateId(iblockstate))
							.multiplyVelocity(0.2F).multipleParticleScaleBy(0.6F));
			} catch (Throwable e) {

			}
		}
	}
}/*
	 * if (FBP.enabled && iblockstate != null && (!(iblockstate.getBlock()
	 * instanceof BlockLiquid) && !(FBP.frozen && !FBP.spawnWhileFrozen)) &&
	 * (FBP.spawnRedstoneBlockParticles || iblockstate.getBlock() !=
	 * Blocks.REDSTONE_BLOCK))
	 */