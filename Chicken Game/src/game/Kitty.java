package game;

import java.awt.geom.Point2D;
import java.io.File;

import graphicslib3D.Matrix3D;
import graphicslib3D.Quaternion;
import graphicslib3D.Vector3D;
import sage.model.loader.ogreXML.OgreXMLParser;
import sage.physics.IPhysicsObject;
import sage.scene.Group;
import sage.scene.Model3DTriMesh;
import sage.scene.SceneNode;


public class Kitty extends MyCharacter{
	private Model3DTriMesh myObject;
	private Group model;
	private float kittySpd, kittyMaxSpd;
	private boolean isWalking = true;
	public Kitty(){
		super();
		OgreXMLParser loader = new OgreXMLParser(); 
		try 
		{ 
			model = loader.loadModel("models" + File.separator + "Kitty.mesh.xml", "materials" + File.separator + "kitty.material", "models" + File.separator + "Kitty.skeleton.xml"); 
			model.updateGeometricState(0, true); 
			java.util.Iterator<SceneNode> modelIterator = model.iterator(); 
			myObject = (Model3DTriMesh) modelIterator.next(); 
		} 
		catch (Exception e) 
		{ 
			e.printStackTrace(); 
			System.exit(1); 
		} 

		addModel(myObject);
		/*OBJLoader loader = new OBJLoader();
		TriMesh kitty = loader.loadModel("models"+File.separator + "KITTY.obj");
		addModel(kitty);*/
	}
	
	public void kittyMove(){
		if(isWalking){
			kittySpd = 1f;
			kittyMaxSpd = 5f;
		}
		if(!isWalking){
			kittySpd = 1f;
			kittyMaxSpd = 10f;
		}
		Matrix3D rot = this.getLocalRotation(); 
		Vector3D dir = new Vector3D(0,0,kittySpd); 
		dir = dir.mult(rot); 
		float move[] = {(float)dir.getX() + this.getPhysicsObject().getLinearVelocity()[0],(float)dir.getY()+this.getPhysicsObject().getLinearVelocity()[1],(float)dir.getZ()+this.getPhysicsObject().getLinearVelocity()[2]};
		if(move[0] > kittyMaxSpd){
			move[0] = kittyMaxSpd;
		}
		if(move[2] > kittyMaxSpd){
			move[2] = kittyMaxSpd;
		}
		this.getPhysicsObject().setLinearVelocity(move);


	}


	public void kittyWalk(){
		if(!isWalking){
			this.startAnimation("KittyWalk");
			isWalking = true;
		}
	}
	public void kittyRun(){
		if(isWalking){
			this.startAnimation("Run");
			isWalking = false;
		}
	}

	public void kittyJump(){
		if(this.getPhysicsObject().getLinearVelocity()[1] < .1f && this.getPhysicsObject().getLinearVelocity()[1] > -.1f){
			Matrix3D rot = this.getLocalRotation(); 
			Vector3D dir = new Vector3D(0,100f,0); 
			dir = dir.mult(rot); 
			float move[] = {(float)dir.getX() + this.getPhysicsObject().getLinearVelocity()[0],(float)dir.getY()+this.getPhysicsObject().getLinearVelocity()[1],(float)dir.getZ()+this.getPhysicsObject().getLinearVelocity()[2]};
			this.getPhysicsObject().setLinearVelocity(move);
		}	
	}

	public void kittyNotFollowing(){
		kittyWalk();
		

	}
	
	public void kittyFollow(MyCharacter target){
		kittyRun();
		/*Vector3D tPos = target.getLocalTranslation().getCol(3);
		Vector3D cPos = this.getLocalTranslation().getCol(3);
		Vector3D viewDir = tPos.minus(cPos);
		viewDir.normalize();
		
		Vector3D wUp = new Vector3D (0,1f,0);
		wUp.normalize();
		Vector3D left = viewDir.cross(wUp);
		left.normalize();
		Vector3D viewUp = left.cross(viewDir);
		viewUp.normalize();

		Matrix3D m = new Matrix3D();
		m.setCol(0, left);
		m.setCol(1, wUp);
		m.setCol(2, viewDir);
		m.setCol(3, cPos);
		//m.setCol(3, tPos);
		Matrix3D kittyRot= this.getLocalRotation();
		m.concatenate(kittyRot);
		this.setLocalRotation(m);*/
		
		Vector3D currViewDir = this.getLocalRotation().getCol(0);
		Vector3D tPos = target.getLocalTranslation().getCol(3);
		Vector3D cPos = this.getLocalTranslation().getCol(3);
		Vector3D viewDir = tPos.minus(cPos);
		viewDir.normalize();
		float angle = (float) viewDir.dot(currViewDir);
		Vector3D wUp = new Vector3D (0,1f,0);
		this.rotate(angle, wUp);
		
		
		//System.out.println(this.getLocalScale());

	/*	Vector3D wUp = new Vector3D (0,1f,0);
		Point2D.Double x = new Point2D.Double(target.getLocalTranslation().elementAt(0, 3), target.getLocalTranslation().elementAt(2, 3));
		double hyp = x.distance(this.getLocalTranslation().elementAt(0, 3), this.getLocalTranslation().elementAt(2, 3));
		double opp = target.getLocalTranslation().elementAt(2, 3);
		float theta = (float) Math.toDegrees((float)Math.asin(opp/hyp));
		this.rotate(theta, wUp);
		System.out.println(hyp);
		System.out.println(opp);
		System.out.println(theta);*/
	}

}