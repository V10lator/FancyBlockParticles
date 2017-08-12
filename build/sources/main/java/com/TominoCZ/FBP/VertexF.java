package com.TominoCZ.FBP;

import javax.vecmath.Vector2f;

import org.lwjgl.util.vector.Vector3f;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraftforge.client.model.pipeline.LightUtil;

public class VertexF {
	public Vector3f pos;
	public Vector3f normal;

	public Vector2f UV;

	public VertexF(float x, float y, float z, float nX, float nY, float nZ, float U, float V) {
		pos = new Vector3f(x, y, z);
		normal = new Vector3f(x, y, z);

		UV = new Vector2f(U, V);
	}

	public int[] getParsedVertexData(BakedQuad actualQuad, VertexFormat format, int index) {/*
		float[] pos = new float[] { this.pos.x, this.pos.y, this.pos.z };
		float[] normal = new float[] { this.normal.x, this.normal.y, this.normal.z };
		float[] UV = new float[] { this.UV.x, this.UV.y };
		
		int[] _pos = new int[24];
		int[] _normal = new int[28];
		int[] _UV = new int[27];
		
		LightUtil.pack(pos, _pos, format, index, 0);
		LightUtil.pack(normal, _normal, format, index, 3);
		LightUtil.pack(UV, _UV, format, index, 2);
		
		int[] data = new int[] {
				_pos[0],
				_pos[1],
				_pos[2],
				_normal[0],
				_normal[1],
				_normal[2],
				_UV[0],
				_UV[1]
		};*/
		
		int size = format.getIntegerSize();
		
		int[] data = new int[9];
		
        int uv = format.getUvOffsetById(0) / 4;
        int n = format.getNormalOffset() / 4;

        int nX = ((int)(normal.x * 127) & 0xFF) << 0;
        int nY = ((int)(normal.y * 127) & 0xFF) << 8;
        int nZ = ((int)(normal.y * 127) & 0xFF) << 16;
        
		data[0] = Float.floatToRawIntBits(pos.x);
		data[1] = Float.floatToRawIntBits(pos.y);
		data[2] = Float.floatToRawIntBits(pos.z);
		
		data[6] = Float.floatToRawIntBits(nX);
		data[7] = Float.floatToRawIntBits(nY);
		data[8] = Float.floatToRawIntBits(nZ);
		
		data[4] = Float.floatToRawIntBits(UV.x);
		data[5] = Float.floatToRawIntBits(UV.y);
		
		return data;
	}
}