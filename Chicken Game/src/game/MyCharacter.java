package game;


import java.util.UUID;

import graphicslib3D.Matrix3D;
import graphicslib3D.Point3D;
import sage.scene.TriMesh;
import sage.scene.Model3DTriMesh;

public class MyCharacter extends Model3DTriMesh{
	private Point3D location;
	
	protected UUID id;
	
	public MyCharacter()
	{
		location = new Point3D(this.getLocalTranslation().elementAt(0, 3),this.getLocalTranslation().elementAt(1, 3),this.getLocalTranslation().elementAt(2, 3));
		id = UUID.randomUUID();
	}
	
	
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
