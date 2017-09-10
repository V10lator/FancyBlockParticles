package com.TominoCZ.FBP.node;

import java.util.HashMap;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import scala.actors.threadpool.Arrays;

public class BlockPosNode {
	HashMap<BlockPos, IBlockState> possible = new HashMap<BlockPos, IBlockState>();
	
	public BlockPosNode() {
		
	}
	
	public void add(BlockPos pos, IBlockState stateAtPos)
	{
		possible.put(pos, stateAtPos);
	}
	
	public boolean hasPos(BlockPos pos)
	{
		return possible.containsKey(pos);
	}
	
	public boolean hasState(IBlockState state)
	{
		return possible.containsValue(state);
	}
	
	public IBlockState stateAt(BlockPos pos) {
		return possible.get(pos);
	}

	public boolean isSame(BlockPos pos) {
		return Minecraft.getMinecraft().theWorld.getBlockState(pos) == stateAt(pos);
	}
}