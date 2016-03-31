package game;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import sage.scene.TriMesh;

public class MyCharacter extends TriMesh{
	private static float[] vrts = new float[] {0,1,0,	    //0
			-1,-1,1,		//1
			1,-1,1,			//2
			1,-1,-1,		//3
			-1,-1,-1,		//4
			.25f,.25f,.25f,	//5			
			-.25f,.25f,.25f,	//6
			-.25f,-.25f,.25f,	//7
			.25f,-.25f,.25f,	//8
			0,0,3};				//9
	private static float[] cl = new float[] {1,0,0,1,	//0
			1,1,1,1,	//1
			1,1,1,1,	//2
			1,1,1,1,	//3
			1,1,1,1,	//4
			1,1,1,1,	//5
			1,1,1,1,	//6
			1,1,1,1,	//7
			1,1,1,1,	//8
			1,1,1,1};	//9
	private static int[] triangles = new int[] {0,1,2,
			0,2,3,
			0,3,4,
			0,4,1,
			1,4,2,
			4,3,2,
			5,9,6,		//head thing
			5,9,8,
			8,9,7,
			6,9,7
	};
	public MyCharacter(){
		FloatBuffer vertBuf =
				com.jogamp.common.nio.Buffers.newDirectFloatBuffer(vrts);
		FloatBuffer colorBuffer1 =
				com.jogamp.common.nio.Buffers.newDirectFloatBuffer(cl);
		IntBuffer triangleBuf =
				com.jogamp.common.nio.Buffers.newDirectIntBuffer(triangles);
		this.setVertexBuffer(vertBuf);
		this.setIndexBuffer(triangleBuf); 
		this.setColorBuffer(colorBuffer1);
	}
}
