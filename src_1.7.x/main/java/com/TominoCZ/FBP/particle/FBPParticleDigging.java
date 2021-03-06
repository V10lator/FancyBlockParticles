package com.TominoCZ.FBP.particle;

import java.util.List;

import com.TominoCZ.FBP.FBP;
import com.TominoCZ.FBP.keys.FBPKeyBindings;
import com.TominoCZ.FBP.util.FBPMathUtil;
import com.TominoCZ.FBP.util.FBPRenderUtil;
import com.TominoCZ.FBP.vector.FBPVector3d;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityDiggingFX;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

@SideOnly(Side.CLIENT)
public class FBPParticleDigging extends EntityDiggingFX {
	private final Block sourceBlock;
	private final int blockSide;

	Minecraft mc;

	int vecIndex;

	float prevGravity;

	double startY, scaleAlpha, prevParticleScale, prevParticleAlpha, prevMotionX, prevMotionZ;
	double endMult = 0.75;

	boolean modeDebounce, wasFrozen, destroyed, killToggle;

	FBPVector3d rot, prevRot, rotStep;

	FBPVector3d[] par;

	protected FBPParticleDigging(World w, double X, double Y, double Z, double mx, double my, double mz, float scale,
			float R, float G, float B, Block b, int meta, int side) {
		super(w, X, Y, Z, mx, my, mz, b, meta < 0 ? 0 : meta, 0);

		this.particleRed = R;
		this.particleGreen = G;
		this.particleBlue = B;

		mc = Minecraft.getMinecraft();

		rot = new FBPVector3d();
		prevRot = new FBPVector3d();

		createRotationMatrix();

		if (scale == -1) {
			if (side == 1 && FBP.smartBreaking) {
				motionX *= 1.5D;
				motionY *= 0.1D;
				motionZ *= 1.5D;

				double particleSpeed = Math.sqrt(motionX * motionX + motionZ * motionZ);

				Vec3 vec = mc.thePlayer.getLookVec();

				double x = FBPMathUtil.add(vec.xCoord, 0.01D);
				double z = FBPMathUtil.add(vec.zCoord, 0.01D);

				motionX = x * particleSpeed;
				motionZ = z * particleSpeed;
			}
		} else
			particleScale = scale;

		if (modeDebounce = !FBP.randomRotation) {
			this.rot.zero();
			calculateYAngle();
		}

		particleGravity = (float) (b.blockParticleGravity * FBP.gravityMult);

		particleScale = (float) (FBP.scaleMult * (FBP.randomizedScale ? particleScale : 1));
		particleMaxAge = (int) FBP.random.nextDouble(FBP.minAge, FBP.maxAge + 0.5);

		scaleAlpha = particleScale * 0.82;

		this.sourceBlock = b;

		if (destroyed = side == -1)
			side = w.rand.nextInt(6);

		this.blockSide = side;

		this.particleIcon = b.getIcon(side, meta);

		if (FBP.randomFadingSpeed)
			endMult = MathHelper.clamp_double(FBP.random.nextDouble(0.5, 0.9), 0.55, 0.8);

		prevGravity = particleGravity;

		startY = posY;

		multipleParticleScaleBy(1);
	}

	protected FBPParticleDigging(World w, double x, double y, double z, double mx, double my, double mz, float scale,
			float r, float g, float b, Block bl, int meta) {
		this(w, x, y, z, mx, my, mz, scale, r, g, b, bl, meta, -1);
	}

	@Override
	public EntityFX multipleParticleScaleBy(float scale) {
		EntityFX p = super.multipleParticleScaleBy(scale);

		float f = particleScale / 10;

		if (FBP.restOnFloor && destroyed)
			posY = prevPosY = startY - f;

		this.boundingBox
				.setBB(AxisAlignedBB.getBoundingBox(posX - f, posY, posZ - f, posX + f, posY + 2 * f, posZ + f));

		return p;
	}

	@Override
	public FBPParticleDigging applyColourMultiplier(int x, int y, int z) {
		if (worldObj.getBlock(x, y, z) != this.sourceBlock) {
			boolean found = false;

			for (int i = -1; i < 2; i++) {
				for (int j = -1; j < 2; j++) {
					for (int k = -1; k < 2; k++) {
						if (worldObj.getBlock(x + i, y + j, z + k) == this.sourceBlock) {
							x += i;
							y += j;
							z += k;

							found = true;
							break;
						}
					}

					if (found)
						break;
				}

				if (found)
					break;
			}

		}

		if (this.sourceBlock == Blocks.grass && this.blockSide != 1)
			return this;

		int l = this.sourceBlock.colorMultiplier(this.worldObj, x, y, z);
		this.particleRed *= (float) (l >> 16 & 255) / 255.0F;
		this.particleGreen *= (float) (l >> 8 & 255) / 255.0F;
		this.particleBlue *= (float) (l & 255) / 255.0F;

		return this;
	}

	@Override
	public FBPParticleDigging applyRenderColor(int p_90019_1_) {
		if (this.sourceBlock == Blocks.grass && this.blockSide != 1) {
			return this;
		} else {
			int j = this.sourceBlock.getRenderColor(p_90019_1_);
			this.particleRed *= (float) (j >> 16 & 255) / 255.0F;
			this.particleGreen *= (float) (j >> 8 & 255) / 255.0F;
			this.particleBlue *= (float) (j & 255) / 255.0F;
			return this;
		}
	}

	@Override
	public FBPParticleDigging multiplyVelocity(float p_70543_1_) {
		this.motionX *= (double) p_70543_1_;
		this.motionY = (this.motionY - 0.10000000149011612D) * (double) p_70543_1_ + 0.10000000149011612D;
		this.motionZ *= (double) p_70543_1_;
		return this;
	}

	@Override
	public int getFXLayer() {
		return 1;
	}

	@Override
	public void onUpdate() {
		boolean allowedToMove = MathHelper.abs((float) motionX) > 0.0001D || MathHelper.abs((float) motionZ) > 0.0001D;

		if (!FBP.frozen && FBP.bounceOffWalls && !mc.isGamePaused() && particleAge > 0) {
			if (!wasFrozen && allowedToMove) {
				boolean xCollided = prevPosX == posX;
				boolean zCollided = prevPosZ == posZ;

				if (xCollided)
					motionX = -prevMotionX * 0.625f;
				if (zCollided)
					motionZ = -prevMotionZ * 0.625f;

				if (!FBP.randomRotation && (xCollided || zCollided))
					calculateYAngle();
			} else
				wasFrozen = false;
		}
		if (FBP.frozen && FBP.bounceOffWalls && !wasFrozen)
			wasFrozen = true;

		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;

		prevRot.copyFrom(rot);

		prevParticleAlpha = particleAlpha;
		prevParticleScale = particleScale;

		if (!mc.isGamePaused() && (!FBP.frozen || killToggle)) {
			if (!killToggle) {
				if (!FBP.randomRotation) {
					if (!modeDebounce) {
						modeDebounce = true;

						rot.z = 0;

						calculateYAngle();
					}

					if (allowedToMove) {
						double x = MathHelper.abs((float) (rotStep.x * getMult()));

						if (motionX > 0) {
							if (motionZ > 0)
								rot.x -= x;
							else if (motionZ < 0)
								rot.x += x;
						} else if (motionX < 0) {
							if (motionZ < 0)
								rot.x += x;
							else if (motionZ > 0) {
								rot.x -= x;
							}
						}
					}
				} else {
					if (modeDebounce) {
						modeDebounce = false;

						rot.z = FBP.random.nextDouble(30, 400);
					}

					if (allowedToMove)
						rot.add(rotStep.multiply(getMult()));
				}
			}

			if (!FBP.infiniteDuration)
				particleAge++;

			if (this.particleAge >= this.particleMaxAge || killToggle) {
				particleScale *= 0.887654321F * endMult;

				if (particleAlpha > 0.01 && particleScale <= scaleAlpha)
					particleAlpha *= 0.68752F * endMult;

				if (particleAlpha <= 0.01)
					setDead();
			}

			if (!killToggle) {
				if (!isCollided)
					motionY -= 0.04D * particleGravity;

				moveEntity(motionX, motionY, motionZ);

				if (isCollided && FBP.restOnFloor) {
					rot.x = (float) Math.round(rot.x / 90) * 90;
					rot.z = (float) Math.round(rot.z / 90) * 90;
				}

				if (MathHelper.abs((float) motionX) > 0.00001D)
					prevMotionX = motionX;
				if (MathHelper.abs((float) motionZ) > 0.00001D)
					prevMotionZ = motionZ;

				if (allowedToMove) {
					motionX *= 0.9800000190734863D;
					motionZ *= 0.9800000190734863D;
				}

				motionY *= 0.9800000190734863D;

				// PHYSICS
				if (FBP.entityCollision) {
					List<Entity> list = worldObj.getEntitiesWithinAABB(Entity.class, this.boundingBox);

					for (Entity entityIn : list) {
						if (!entityIn.noClip) {
							double d0 = this.posX - entityIn.posX;
							double d1 = this.posZ - entityIn.posZ;
							double d2 = MathHelper.abs_max(d0, d1);

							if (d2 >= 0.009999999776482582D) {
								d2 = Math.sqrt(d2);
								d0 /= d2;
								d1 /= d2;

								double d3 = 1.0D / d2;

								if (d3 > 1.0D)
									d3 = 1.0D;

								this.motionX += d0 * d3 / 20;
								this.motionZ += d1 * d3 / 20;

								if (!FBP.randomRotation)
									calculateYAngle();
								if (!FBP.frozen)
									this.isCollided = false;
							}
						}
					}
				}

				if (FBP.waterPhysics) {
					if (isInWater()) {
						handleWaterMovement();

						if (FBP.INSTANCE.doesMaterialFloat(this.sourceBlock.getMaterial())) {
							motionY = 0.11f + (particleScale / 1.25f) * 0.02f;
						} else {
							motionX *= 0.932515086137662D;
							motionZ *= 0.932515086137662D;
							particleGravity = 0.35f;

							motionY *= 0.85f;
						}

						if (!FBP.randomRotation)
							calculateYAngle();

						if (isCollided)
							isCollided = false;
					} else {
						particleGravity = prevGravity;
					}
				}

				if (isCollided) {
					if (FBP.lowTraction) {
						motionX *= 0.932515086137662D;
						motionZ *= 0.932515086137662D;
					} else {
						motionX *= 0.6654999988079071D;
						motionZ *= 0.6654999988079071D;
					}
				}
			}
		}
	}

	@Override
	public boolean isInWater() {
		double scale = particleScale / 20;

		int minX = MathHelper.floor_double(posX - scale);
		int maxX = MathHelper.ceiling_double_int(posX + scale);

		int minY = MathHelper.floor_double(posY - scale);
		int maxY = MathHelper.ceiling_double_int(posY + scale);

		int minZ = MathHelper.floor_double(posZ - scale);
		int maxZ = MathHelper.ceiling_double_int(posZ + scale);

		if (worldObj.checkChunksExist(minX, minY, minZ, maxX, maxY, maxZ)) {
			for (int x = minX; x < maxX; ++x) {
				for (int y = minY; y < maxY; ++y) {
					for (int z = minZ; z < maxZ; ++z) {
						Block block = worldObj.getBlock(x, y, z);

						if (block.getMaterial() == Material.water) {
							double d0 = (double) ((float) (y + 1)
									- BlockLiquid.getLiquidHeightPercent(worldObj.getBlockMetadata(x, y, z)));

							if (posY <= d0)
								return true;
						}
					}
				}
			}
		}

		return false;
	}

	@Override
	public boolean handleWaterMovement() {
		double scale = particleScale / 20;

		int minX = MathHelper.floor_double(posX - scale);
		int maxX = MathHelper.ceiling_double_int(posX + scale);

		int minY = MathHelper.floor_double(posY - scale);
		int maxY = MathHelper.ceiling_double_int(posY + scale);

		int minZ = MathHelper.floor_double(posZ - scale);
		int maxZ = MathHelper.ceiling_double_int(posZ + scale);

		if (worldObj.checkChunksExist(minX, minY, minZ, maxX, maxY, maxZ)) {
			boolean flag = false;
			Vec3 vec3 = Vec3.createVectorHelper(0.0D, 0.0D, 0.0D);

			for (int x = minX; x < maxX; ++x) {
				for (int y = minY; y < maxY; ++y) {
					for (int z = minZ; z < maxZ; ++z) {
						Block block = worldObj.getBlock(x, y, z);

						if (block.getMaterial() == Material.water) {
							double d0 = (double) ((float) (y + 1)
									- BlockLiquid.getLiquidHeightPercent(worldObj.getBlockMetadata(x, y, z)));

							if (posY <= d0) {
								flag = true;
								block.velocityToAddToEntity(worldObj, x, y, z, this, vec3);
							}
						}
					}
				}
			}

			if (vec3.lengthVector() > 0.0D && this.isPushedByWater()) {
				vec3 = vec3.normalize();
				double d1 = 0.014D;
				this.motionX += vec3.xCoord * d1;
				this.motionY += vec3.yCoord * d1;
				this.motionZ += vec3.zCoord * d1;
			}

			return flag;
		}

		return false;
	}

	@Override
	public void moveEntity(double x, double y, double z) {
		double X = x;
		double Y = y;
		double Z = z;

		List list = this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox.addCoord(x, y, z));

		for (int i = 0; i < list.size(); ++i) {
			y = ((AxisAlignedBB) list.get(i)).calculateYOffset(this.boundingBox, y);
		}

		this.boundingBox.offset(0.0D, y, 0.0D);

		for (int j = 0; j < list.size(); ++j) {
			x = ((AxisAlignedBB) list.get(j)).calculateXOffset(this.boundingBox, x);
		}

		this.boundingBox.offset(x, 0.0D, 0.0D);

		for (int j = 0; j < list.size(); ++j) {
			z = ((AxisAlignedBB) list.get(j)).calculateZOffset(this.boundingBox, z);
		}

		this.boundingBox.offset(0.0D, 0.0D, z);

		// RESET
		AxisAlignedBB axisalignedbb = this.boundingBox;
		this.posX = (axisalignedbb.minX + axisalignedbb.maxX) / 2.0D;
		this.posY = axisalignedbb.minY;
		this.posZ = (axisalignedbb.minZ + axisalignedbb.maxZ) / 2.0D;

		this.isCollided = y != Y && Y < 0.0D;

		if (!FBP.lowTraction && !FBP.bounceOffWalls) {
			if (x != X)
				motionX *= 0.699999988079071D;
			if (z != Z)
				motionZ *= 0.699999988079071D;
		}
	}

	@Override
	public void renderParticle(Tessellator tes, float partialTicks, float rotationX, float rotationZ, float rotationYZ,
			float rotationXY, float rotationXZ) {
		if (!FBP.isEnabled() && particleMaxAge != 0)
			particleMaxAge = 0;
		if (FBPKeyBindings.FBPSweep.getIsKeyPressed() && !killToggle)
			killToggle = true;

		float f = 0, f1 = 0, f2 = 0, f3 = 0;

		float f4 = (float) (prevParticleScale + (particleScale - prevParticleScale) * partialTicks);

		if (particleIcon != null) {
			if (!FBP.cartoonMode) {
				f = particleIcon.getInterpolatedU(particleTextureJitterX / 4 * 16);
				f2 = particleIcon.getInterpolatedV(particleTextureJitterY / 4 * 16);
			}

			f1 = particleIcon.getInterpolatedU((particleTextureJitterX + 1) / 4 * 16);
			f3 = particleIcon.getInterpolatedV((particleTextureJitterY + 1) / 4 * 16);
		} else {
			f = (particleTextureIndexX + particleTextureJitterX / 4) / 16;
			f1 = f + 0.015609375F;
			f2 = (particleTextureIndexY + particleTextureJitterY / 4) / 16;
			f3 = f2 + 0.015609375F;
		}

		float f5 = (float) (prevPosX + (posX - prevPosX) * partialTicks - interpPosX);
		float f6 = (float) (prevPosY + (posY - prevPosY) * partialTicks - interpPosY);
		float f7 = (float) (prevPosZ + (posZ - prevPosZ) * partialTicks - interpPosZ);

		int i = getBrightnessForRender(partialTicks);

		par = new FBPVector3d[] { new FBPVector3d(f1, f3, 0), new FBPVector3d(f1, f2, 0), new FBPVector3d(f, f2, 0),
				new FBPVector3d(f, f3, 0) };

		float alpha = (float) (prevParticleAlpha + (particleAlpha - prevParticleAlpha) * partialTicks);

		if (FBP.restOnFloor)
			f6 += f4 / 10;

		FBPVector3d smoothRot = new FBPVector3d();

		if (FBP.rotationMult > 0) {
			smoothRot.y = rot.y;
			smoothRot.z = rot.z;

			if (!FBP.randomRotation)
				smoothRot.x = rot.x;

			// SMOOTH ROTATION
			if (!FBP.frozen) {
				FBPVector3d vec = rot.partialVec(prevRot, partialTicks);

				if (FBP.randomRotation) {
					smoothRot.y = vec.y;
					smoothRot.z = vec.z;
				} else {
					smoothRot.x = vec.x;
				}
			}
		}

		// RENDER
		FBPRenderUtil.renderCubeShaded_S(tes, par, f5, f6, f7, f4 / 10, smoothRot, i, particleRed, particleGreen,
				particleBlue, alpha, FBP.cartoonMode);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getBrightnessForRender(float partialTicks) {
		int i = MathHelper.floor_double(this.posX);
		int j = MathHelper.floor_double(this.posZ);

		if (this.worldObj.blockExists(i, 0, j)) {
			double d0 = (this.boundingBox.maxY - this.boundingBox.minY) * 0.66D;
			int k = MathHelper.floor_double(this.posY + d0 + 0.01 - (FBP.restOnFloor ? particleScale / 10 : 0));
			return this.worldObj.getLightBrightnessForSkyBlocks(i, k, j, 0);
		} else {
			return 0;
		}
	}

	private void createRotationMatrix() {
		double rx0 = FBP.random.nextDouble();
		double ry0 = FBP.random.nextDouble();
		double rz0 = FBP.random.nextDouble();

		rotStep = new FBPVector3d(rx0 > 0.5 ? 1 : -1, ry0 > 0.5 ? 1 : -1, rz0 > 0.5 ? 1 : -1);

		rot.copyFrom(rotStep);
	}

	private void calculateYAngle() {
		double angleSin = Math.toDegrees(Math.asin(motionX / Math.sqrt(motionX * motionX + motionZ * motionZ)));

		if (motionX > 0) {
			if (motionZ > 0)
				rot.y = -angleSin;
			else
				rot.y = angleSin;
		} else {
			if (motionZ > 0)
				rot.y = -angleSin;
			else
				rot.y = angleSin;
		}
	}

	double getMult() {
		return Math.sqrt(motionX * motionX + motionZ * motionZ) * (FBP.randomRotation ? 200 : 500) * FBP.rotationMult;
	}
}