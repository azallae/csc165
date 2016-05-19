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
	protected Point3D location;
	
	protected UUID id;
	
	public MyCharacter()
	{
		location = new Point3D(0,0,0);
		id = UUID.randomUUID();
	}
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
	
	public void addModel(Model3DTriMesh model)
	{
		setAnimatedNormals(model.getAnimatedNormals()); 
		setAnimatedVertices(model.getAnimatedVertices()); 
		setAnimations(model.getAnimations()); 
		setJoints(model.getJoints()); 
	    setShaderProgram(model.getShaderProgram()); 
		setTextureFilename(model.getTextureFileName()); 
		setVertexBoneIDs(model.getVertexBoneIDs()); 
		setVertexBoneWeights(model.getVertexBoneWeights());
		
		setColorBuffer(model.getColorBuffer());  
		setFaceMaterialIndices(model.getFaceMaterialIndices()); 
		setFaceMaterials(model.getFaceMaterials()); 
		setIndexBuffer(model.getIndexBuffer()); 
		setNormalBuffer(model.getNormalBuffer()); 
		setTextureBuffer(model.getTextureBuffer()); 
		setVertexBuffer(model.getVertexBuffer()); 
		
		updateLocalBound();
	}
	
	public UUID getId(){
		return id;
	}
	
	public void setLocation(Point3D ghostPosition){
		this.location = ghostPosition;
		Matrix3D m = new Matrix3D();
		m.translate(location.getX(), location.getY(), location.getZ());
		setLocalTranslation(m);
	}
	public Point3D getLocation(){
		return location;
	}
	public void updateTranslation(){
		Matrix3D m = new Matrix3D();
		m.translate(location.getX(), location.getY(), location.getZ());
		setLocalTranslation(m);
	}
	
}
