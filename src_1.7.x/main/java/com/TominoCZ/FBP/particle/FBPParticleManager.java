package com.TominoCZ.FBP.particle;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import com.TominoCZ.FBP.FBP;
import com.TominoCZ.FBP.block.FBPAnimationDummyBlock;
import com.TominoCZ.FBP.block.FBPBlockPos;
import com.TominoCZ.FBP.node.BlockNode;
import com.google.common.base.Throwables;

import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityDiggingFX;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.particle.EntityFlameFX;
import net.minecraft.client.particle.EntityRainFX;
import net.minecraft.client.particle.EntitySmokeFX;
import net.minecraft.client.renderer.DestroyBlockProgress;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

@SideOnly(Side.CLIENT)
public class FBPParticleManager extends EffectRenderer {
	private static MethodHandle getBlockDamage;
	private static MethodHandle getParticleScale;
	private static MethodHandle getSourceBlock;
	private static MethodHandle getParticleMaxAge;
	private static MethodHandle getParticleIcon;
	private static MethodHandle getParticleBlockSide;

	private static MethodHandle X, Y, Z;
	private static MethodHandle mX, mY, mZ;

	Minecraft mc;
	private Random rand;

	public FBPParticleManager(World worldIn, TextureManager rendererIn) {
		super(worldIn, rendererIn);

		mc = Minecraft.getMinecraft();

		rand = new Random();
		MethodHandles.Lookup lookup = MethodHandles.publicLookup();

		try {
			X = lookup.unreflectGetter(ReflectionHelper.findField(Entity.class, "field_70165_t", "posX"));
			Y = lookup.unreflectGetter(ReflectionHelper.findField(Entity.class, "field_70163_u", "posY"));
			Z = lookup.unreflectGetter(ReflectionHelper.findField(Entity.class, "field_70161_v", "posZ"));

			mX = lookup.unreflectGetter(ReflectionHelper.findField(Entity.class, "field_70159_w", "motionX"));
			mY = lookup.unreflectGetter(ReflectionHelper.findField(Entity.class, "field_70181_x", "motionY"));
			mZ = lookup.unreflectGetter(ReflectionHelper.findField(Entity.class, "field_70179_y", "motionZ"));

			getParticleScale = lookup
					.unreflectGetter(ReflectionHelper.findField(EntityFX.class, "field_70544_f", "particleScale"));
			getParticleMaxAge = lookup
					.unreflectGetter(ReflectionHelper.findField(EntityFX.class, "field_70547_e", "particleMaxAge"));

			getSourceBlock = lookup.unreflectGetter(
					ReflectionHelper.findField(EntityDiggingFX.class, "field_145784_a", "field_145784_a"));
			getBlockDamage = lookup
					.unreflectGetter(ReflectionHelper.findField(RenderGlobal.class, "field_72738_E", "damagedBlocks"));
			getParticleIcon = lookup
					.unreflectGetter(ReflectionHelper.findField(EntityFX.class, "field_70550_a", "particleIcon"));
			getParticleBlockSide = lookup.unreflectGetter(ReflectionHelper.findField(EntityDiggingFX.class, "side"));
		} catch (Throwable e) {
			throw Throwables.propagate(e);
		}
	}

	@Override
	public void addEffect(EntityFX effect) {
		EntityFX toAdd = effect;

		if (FBP.enabled && toAdd != null && !(toAdd instanceof FBPParticleSnow)
				&& !(toAdd instanceof FBPParticleRain)) {
			if (FBP.fancyFlame && toAdd instanceof EntityFlameFX && !(toAdd instanceof FBPParticleFlame)
					&& Minecraft.getMinecraft().gameSettings.particleSetting < 2) {
				EntityFlameFX p = (EntityFlameFX) effect;

				try {
					toAdd = new FBPParticleFlame(worldObj, (double) X.invokeExact((Entity) effect),
							(double) Y.invokeExact((Entity) effect), (double) Z.invokeExact((Entity) effect), 0,
							FBP.random.nextDouble() * 0.25, 0, true);
					effect.setDead();
				} catch (Throwable t) {
					t.printStackTrace();
				}
			} else if (FBP.fancySmoke && toAdd instanceof EntitySmokeFX && !(toAdd instanceof FBPParticleSmokeNormal)
					&& Minecraft.getMinecraft().gameSettings.particleSetting < 2) {
				EntitySmokeFX p = (EntitySmokeFX) effect;

				try {
					toAdd = new FBPParticleSmokeNormal(worldObj, (double) X.invokeExact((Entity) effect),
							(double) Y.invokeExact((Entity) effect), (double) Z.invokeExact((Entity) effect),
							(double) mX.invokeExact((Entity) effect), (double) mY.invokeExact((Entity) effect),
							(double) mZ.invokeExact((Entity) effect),
							(float) getParticleScale.invokeExact((EntityFX) effect), p);

					toAdd.setRBGColorF(MathHelper.clamp_float(effect.getRedColorF() + 0.1f, 0.1f, 1),
							MathHelper.clamp_float(effect.getGreenColorF() + 0.1f, 0.1f, 1),
							MathHelper.clamp_float(effect.getBlueColorF() + 0.1f, 0.1f, 1));

					((FBPParticleSmokeNormal) toAdd).setMaxAge((int) getParticleMaxAge.invokeExact((EntityFX) effect));
				} catch (Throwable t) {
					t.printStackTrace();
				}
			} else if (FBP.fancyRain && toAdd instanceof EntityRainFX) {
				effect.setDead();
				return;
			} else if (toAdd instanceof EntityDiggingFX && !(toAdd instanceof FBPParticleDigging)) {
				try {
					Block b = (Block) getSourceBlock.invokeExact((EntityDiggingFX) effect);
					IIcon icon = null;

 					double x = (double) X.invokeExact((Entity) effect);
					double y = (double) Y.invokeExact((Entity) effect);
					double z = (double) Z.invokeExact((Entity) effect);

					if (b instanceof FBPAnimationDummyBlock) {
						FBPBlockPos pos = new FBPBlockPos(x, y, z);
						BlockNode n = FBP.FBPBlock.getNode(pos);

						if (n == null)
							n = FBP.FBPBlock.getNode(pos.offset(EnumFacing.DOWN));

						if (n != null)
							b = n.block;
					} else
						icon = (IIcon) getParticleIcon.invokeExact((EntityFX) effect);

					if (b != null && !(FBP.frozen && !FBP.spawnWhileFrozen)
							&& (FBP.spawnRedstoneBlockParticles || b != Blocks.redstone_block)) {
						effect.setDead();

						if (!(b instanceof BlockLiquid) && !FBP.INSTANCE.isInExceptions(b, true)) {
							FBPBlockPos pos = new FBPBlockPos(x, y, z);

							toAdd = new FBPParticleDigging(worldObj, x, y + 0.05000000149011612D, z, 0, 0, 0,
									toAdd.getRedColorF(), toAdd.getGreenColorF(), toAdd.getBlueColorF(),
									(float) getParticleScale.invokeExact((EntityFX) effect), b, 0,
									(int) getParticleBlockSide.invokeExact((EntityDiggingFX) effect))
											.applyColourMultiplier(pos.getX(), pos.getY(), pos.getZ());

							if (!(b instanceof FBPAnimationDummyBlock) && icon != null)
								toAdd.setParticleIcon(icon);

							icon = (IIcon) getParticleIcon.invokeExact((EntityFX) toAdd);
							if (icon == null || icon.getIconName().equals("missingno")) {
								effect.setDead();
								toAdd.setDead();
								return;
							}
						} else
							return;
					}
				} catch (Throwable e) {
					e.printStackTrace();
				}
			} else if (toAdd instanceof FBPParticleDigging) {
				try {
					Block b = (Block) getSourceBlock.invokeExact((EntityDiggingFX) effect);

					if (b != null && !(FBP.frozen && !FBP.spawnWhileFrozen)
							&& (FBP.spawnRedstoneBlockParticles || b != Blocks.redstone_block)) {

						if (b instanceof BlockLiquid || FBP.INSTANCE.isInExceptions(b, true))
							toAdd = null;
					}
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}

		if (toAdd != null)
			super.addEffect(toAdd);
	}

	@Override
	public void addBlockDestroyEffects(int x, int y, int z, Block b, int meta) {
		if (!b.isAir(worldObj, x, y, z) && !b.addDestroyEffects(worldObj, x, y, z, meta, this)) {
			byte b0 = 4;

			for (int i1 = 0; i1 < FBP.particlesPerAxis; ++i1) {
				for (int j1 = 0; j1 < FBP.particlesPerAxis; ++j1) {
					for (int k1 = 0; k1 < FBP.particlesPerAxis; ++k1) {
						double d0 = (double) x + ((double) i1 + 0.5D) / (double) b0;
						double d1 = (double) y + ((double) j1 + 0.5D) / (double) b0;
						double d2 = (double) z + ((double) k1 + 0.5D) / (double) b0;

						if (FBP.enabled) {
							if ((!(b instanceof BlockLiquid) && !(FBP.frozen && !FBP.spawnWhileFrozen))
									&& (FBP.spawnRedstoneBlockParticles || b != Blocks.redstone_block)
									&& !FBP.INSTANCE.isInExceptions(b, true)) {
								if (b == FBP.FBPBlock) {
									BlockNode n = FBP.FBPBlock.getNode(new FBPBlockPos(x, y, z));

									if (n != null)
										b = n.block;
								}

								if (b == FBP.FBPBlock)
									return;

								EntityDiggingFX toSpawn = new FBPParticleDigging(this.worldObj, d0, d1, d2,
										d0 - (double) x - 0.5D, d1 - (double) y - 0.5D, d2 - (double) z - 0.5D, 1, 1, 1,
										-1, b, meta).applyColourMultiplier(x, y, z);

								addEffect(toSpawn);
							}
						} else
							this.addEffect((new EntityDiggingFX(this.worldObj, d0, d1, d2, d0 - (double) x - 0.5D,
									d1 - (double) y - 0.5D, d2 - (double) z - 0.5D, b, meta)).applyColourMultiplier(x,
											y, z));
					}
				}
			}
		}
	}

	@Override
	public void addBlockHitEffects(int x, int y, int z, int side) {
		Block block = this.worldObj.getBlock(x, y, z);

		if (block.getMaterial() != Material.air) {
			MovingObjectPosition obj = Minecraft.getMinecraft().objectMouseOver;

			if (obj == null || obj.hitVec == null) {
				obj = new MovingObjectPosition(mc.thePlayer);

				obj.blockX = x;
				obj.blockY = y;
				obj.blockZ = z;
			}
			float f = 0.1F;

			double d0, d1, d2;

			if (FBP.enabled && FBP.smartBreaking
					&& (!(block instanceof BlockLiquid) && !(FBP.frozen && !FBP.spawnWhileFrozen))
					&& (FBP.spawnRedstoneBlockParticles || block != Blocks.redstone_block)) {
				d0 = obj.hitVec.xCoord + FBP.random.nextDouble(-0.21, 0.21)
						* Math.abs(block.getBlockBoundsMaxX() - block.getBlockBoundsMinX());
				d1 = obj.hitVec.yCoord + FBP.random.nextDouble(-0.21, 0.21)
						* Math.abs(block.getBlockBoundsMaxY() - block.getBlockBoundsMinY());
				d2 = obj.hitVec.zCoord + FBP.random.nextDouble(-0.21, 0.21)
						* Math.abs(block.getBlockBoundsMaxZ() - block.getBlockBoundsMinZ());
			} else {

				d0 = (double) x
						+ this.rand.nextDouble()
								* (block.getBlockBoundsMaxX() - block.getBlockBoundsMinX() - (double) (f * 2.0F))
						+ (double) f + block.getBlockBoundsMinX();
				d1 = (double) y
						+ this.rand.nextDouble()
								* (block.getBlockBoundsMaxY() - block.getBlockBoundsMinY() - (double) (f * 2.0F))
						+ (double) f + block.getBlockBoundsMinY();
				d2 = (double) z
						+ this.rand.nextDouble()
								* (block.getBlockBoundsMaxZ() - block.getBlockBoundsMinZ() - (double) (f * 2.0F))
						+ (double) f + block.getBlockBoundsMinZ();
			}

			if (side == 0) {
				d1 = (double) y + block.getBlockBoundsMinY() - (double) f;
			} else if (side == 1) {
				d1 = (double) y + block.getBlockBoundsMaxY() + 2 * f;
			} else if (side == 2) {
				d2 = (double) z + block.getBlockBoundsMinZ() - (double) f;
			} else if (side == 3) {
				d2 = (double) z + block.getBlockBoundsMaxZ() + (double) f;
			} else if (side == 4) {
				d0 = (double) x + block.getBlockBoundsMinX() - (double) f;
			} else if (side == 5) {
				d0 = (double) x + block.getBlockBoundsMaxX() + (double) f;
			}

			if (FBP.enabled) {
				if (!(block instanceof BlockLiquid) && !(FBP.frozen && !FBP.spawnWhileFrozen)
						&& (FBP.spawnRedstoneBlockParticles || block != Blocks.redstone_block)) {
					int damage = 0;

					try {
						DestroyBlockProgress progress = null;
						Map mp = (Map<Integer, DestroyBlockProgress>) getBlockDamage
								.invokeExact(Minecraft.getMinecraft().renderGlobal);

						if (!mp.isEmpty()) {
							Iterator it = mp.values().iterator();

							while (it.hasNext()) {
								progress = (DestroyBlockProgress) it.next();

								FBPBlockPos _pos = new FBPBlockPos(progress.getPartialBlockX(),
										progress.getPartialBlockY(), progress.getPartialBlockZ());
								FBPBlockPos pos = new FBPBlockPos(x, y, z);

								if (_pos.equals(pos)) {
									damage = progress.getPartialBlockDamage();
									break;
								}
							}
						}
					} catch (Throwable e) {

					}

					EntityFX toSpawn;

					if (!FBP.INSTANCE.isInExceptions(block, true)) {
						if (block == FBP.FBPBlock) {
							BlockNode n = FBP.FBPBlock.getNode(new FBPBlockPos(x, y, z));

							if (n != null)
								block = n.block;
						}

						toSpawn = new FBPParticleDigging(worldObj, d0, d1, d2, 0.0D, 0.0D, 0.0D, 1.0f, 1.0f, 1.0f, -1,
								block, worldObj.getBlockMetadata(x, y, z), side).applyColourMultiplier(x, y, z);

						if (FBP.smartBreaking) {
							toSpawn = toSpawn.multiplyVelocity(side == 1 ? 0.7F : 0.15F);
							toSpawn = toSpawn.multipleParticleScaleBy(0.325F + (damage / 8.125F) * 0.325F);
						} else {
							toSpawn = toSpawn.multiplyVelocity(0.2F);
							toSpawn = toSpawn.multipleParticleScaleBy(0.6F);
						}

						addEffect(toSpawn);
					}
				}
			} else
				this.addEffect((new EntityDiggingFX(this.worldObj, d0, d1, d2, 0.0D, 0.0D, 0.0D, block,
						this.worldObj.getBlockMetadata(x, y, z))).applyColourMultiplier(x, y, z).multiplyVelocity(0.2F)
								.multipleParticleScaleBy(0.6F));
		}
	}
}