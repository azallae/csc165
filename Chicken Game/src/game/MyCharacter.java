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
		location.setX(this.getLocalTranslation().elementAt(0, 3));
		location.setY(this.getLocalTranslation().elementAt(1, 3));
		location.setZ(this.getLocalTranslation().elementAt(2, 3));
		return location;
	}
	public void rotateCharacter(double[] m){
		Matrix3D mat = this.getLocalRotation();
		mat.setElementAt(0, 0, m[0]);
		mat.setElementAt(0, 2, m[1]);
		mat.setElementAt(2, 0, m[2]);
		mat.setElementAt(2, 2, m[3]);
		this.setLocalRotation(mat);
	}
	public double[]	getRot(){
		double[] m = new double[4];
		m[0] = this.getLocalRotation().elementAt(0, 0);
		m[1] = this.getLocalRotation().elementAt(0, 2);
		m[2] = this.getLocalRotation().elementAt(2, 0);
		m[3] = this.getLocalRotation().elementAt(2, 2);
		return m;
	}

}
