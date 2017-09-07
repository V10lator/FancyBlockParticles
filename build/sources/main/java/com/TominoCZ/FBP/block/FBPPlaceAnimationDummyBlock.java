package com.TominoCZ.FBP.block;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import com.TominoCZ.FBP.BlockNode;
import com.TominoCZ.FBP.FBP;
import com.TominoCZ.FBP.particle.FBPBlockPlaceAnimationDummyParticle;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class FBPPlaceAnimationDummyBlock extends Block {

	public ConcurrentHashMap<BlockPos, BlockNode> blockNodes = new ConcurrentHashMap<BlockPos, BlockNode>();

	public FBPPlaceAnimationDummyBlock() {
		super(Material.BARRIER);

		this.setRegistryName("FBPPlaceAnimationCollisionBoundingBoxPlaceholderBlock");
	}

	public void copyState(World w, BlockPos pos, IBlockState state, FBPBlockPlaceAnimationDummyParticle p) {
		FBP.FBPBlock.cleanHashMap();

		if (blockNodes.containsKey(pos))
			return;

		blockNodes.put(pos, new BlockNode(state, p));
	}

	public void cleanHashMap() {
		List<BlockPos> toRemove = new ArrayList<BlockPos>();

		for (BlockPos bp : blockNodes.keySet()) {
			BlockNode bn = blockNodes.get(bp);

			if (Minecraft.getMinecraft().theWorld.getBlockState((BlockPos) bp).getBlock() != FBP.FBPBlock)
				toRemove.add((BlockPos) bp);
		}

		for (BlockPos bp : toRemove)
			blockNodes.remove(bp);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, World worldIn, BlockPos pos) {
		if (blockNodes.containsKey(pos)) {
			BlockNode n = blockNodes.get(pos);

			return n.state.getCollisionBoundingBox(worldIn, pos);
		}

		return this.FULL_BLOCK_AABB.offset(pos);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
		if (blockNodes.containsKey(pos)) {
			BlockNode n = blockNodes.get(pos);

			return n.state.getBoundingBox(worldIn, pos);
		}

		return this.FULL_BLOCK_AABB.offset(pos);
	}

	@Override
	public AxisAlignedBB getSelectedBoundingBox(IBlockState blockState, World worldIn, BlockPos pos) {
		if (blockNodes.containsKey(pos)) {
			BlockNode n = blockNodes.get(pos);

			return n.state.getSelectedBoundingBox(worldIn, pos);
		}

		return this.FULL_BLOCK_AABB.offset(pos);
	}

	@Override
	public float getBlockHardness(IBlockState blockState, World w, BlockPos pos) {
		if (blockNodes.containsKey(pos)) {
			BlockNode n = blockNodes.get(pos);

			return n.state.getBlockHardness(w, pos);
		}

		return blockState.getBlockHardness(w, pos);
	}

	@Override
	public void onBlockDestroyedByPlayer(World worldIn, BlockPos pos, IBlockState state) {
		BlockNode node = FBP.FBPBlock.blockNodes.get(pos);

		if (node == null)
			return;

		if (node.particle != null)
			node.particle.killParticle();

		if (worldIn.isRemote && node != null && state.getBlock() != node.originalBlock
				&& state.getBlock() instanceof FBPPlaceAnimationDummyBlock)
			Minecraft.getMinecraft().effectRenderer.addBlockDestroyEffects(pos,
					node.originalBlock.getStateFromMeta(node.meta));
	}

	@Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox,
			List<AxisAlignedBB> collidingBoxes, Entity entityIn) {

		if (blockNodes.containsKey(pos))
			blockNodes.get(pos).state.addCollisionBoxToList(worldIn, pos, entityBox, collidingBoxes, entityIn);
	}

	@Override
	public float getExplosionResistance(World w, BlockPos p, Entity e, Explosion ex) {
		if (blockNodes.containsKey(p))
			return blockNodes.get(p).originalBlock.getExplosionResistance(w, p, e, ex);

		return super.getExplosionResistance(w, p, e, ex);
	}

	@Override
	public float getExplosionResistance(Entity e) {
		if (blockNodes.containsKey(e.getPosition()))
			return blockNodes.get(e.getPosition()).originalBlock.getExplosionResistance(e);

		return super.getExplosionResistance(e);
	}

	@Override
	public float getEnchantPowerBonus(World w, BlockPos p) {
		if (blockNodes.containsKey(p))
			return blockNodes.get(p).originalBlock.getEnchantPowerBonus(w, p);

		return super.getEnchantPowerBonus(w, p);
	}

	@Override
	public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
		if (blockNodes.containsKey(pos))
			return blockNodes.get(pos).originalBlock.getFlammability(world, pos, face);

		return super.getFlammability(world, pos, face);
	}

	@Override
	public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
		if (blockNodes.containsKey(pos))
			return blockNodes.get(pos).originalBlock.getFireSpreadSpeed(world, pos, face);

		return super.getFireSpreadSpeed(world, pos, face);
	}

	@Override
	public boolean getWeakChanges(IBlockAccess world, BlockPos pos) {
		if (blockNodes.containsKey(pos))
			return blockNodes.get(pos).originalBlock.getWeakChanges(world, pos);

		return super.getWeakChanges(world, pos);
	}

	@Override
	public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		if (blockNodes.containsKey(pos))
			return blockNodes.get(pos).state.getWeakPower(blockAccess, pos, side);

		return super.getWeakPower(blockState, blockAccess, pos, side);
	}

	public IBlockState getExtendedState(IBlockState s, IBlockAccess w, BlockPos p) {
		if (blockNodes.containsKey(p))
			return blockNodes.get(p).originalBlock.getExtendedState(s, w, p);

		return super.getExtendedState(s, w, p);
	}

	@Override
	public SoundType getSoundType(IBlockState state, World world, BlockPos pos, Entity entity) {
		if (!blockNodes.containsKey(pos))
			return SoundType.STONE;

		BlockNode n = blockNodes.get(pos);

		return n.state.getBlock().getSoundType(state, world, pos, entity);
	}

	@Override
	public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
		if (blockNodes.containsKey(pos))
			return blockNodes.get(pos).originalBlock.getItem(worldIn, pos, state);

		return super.getItem(worldIn, pos, state);
	}

	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		if (blockNodes.containsKey(pos))
			return blockNodes.get(pos).originalBlock.getDrops(world, pos, state, fortune);

		return super.getDrops(world, pos, state, fortune);
	}

	@Override
	public int getExpDrop(IBlockState state, IBlockAccess world, BlockPos pos, int fortune) {
		if (blockNodes.containsKey(pos))
			return blockNodes.get(pos).originalBlock.getExpDrop(state, world, pos, fortune);

		return super.getExpDrop(state, world, pos, fortune);
	}

	@Override
	public Item getItemDropped(IBlockState state, Random r, int i) {
		return null;
	}

	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.INVISIBLE;
	}

	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@SideOnly(Side.CLIENT)
	public float getAmbientOcclusionLightValue(IBlockState state) {
		return 0.0F;
	}
}
