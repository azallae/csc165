package game;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.UUID;

import graphicslib3D.Matrix3D;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;
import sage.scene.TriMesh;
import sage.scene.Model3DTriMesh;

public class MyCharacter extends Model3DTriMesh{
	private Point3D location;
	
	protected UUID id;
	
	public MyCharacter(){
		location = new Point3D(0,0,0);
		id = UUID.randomUUID();}
	/*
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
		id = UUID.randomUUID();
	}*/
	
	public void addModel(TriMesh m)
	{
		setColorBuffer(m.getColorBuffer());  
		setFaceMaterialIndices(m.getFaceMaterialIndices()); 
		setFaceMaterials(m.getFaceMaterials()); 
		setIndexBuffer(m.getIndexBuffer()); 
		setNormalBuffer(m.getNormalBuffer()); 
		setTextureBuffer(m.getTextureBuffer()); 
		setVertexBuffer(m.getVertexBuffer()); 
		
		updateLocalBound();
	}
	
	public void setLocation(Point3D ghostPosition){
		location = ghostPosition;
		Matrix3D m = new Matrix3D();
		m.translate(location.getX(), location.getY(), location.getZ());
		setLocalTranslation(m);
	}
	public Point3D getLocation(){
		return location;
	}
}
