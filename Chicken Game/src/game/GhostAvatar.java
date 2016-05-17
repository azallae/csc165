package game;

import java.util.UUID;
import java.io.File;

import graphicslib3D.Point3D;
import sage.model.loader.OBJLoader;
import sage.scene.TriMesh;

public class GhostAvatar extends MyCharacter{

	public GhostAvatar(UUID ghostID, Point3D ghostPosition) {
		// TODO Auto-generated constructor stub
		super();
		id = ghostID;
		setLocation(ghostPosition);
		//OBJLoader loader = new OBJLoader();
		//TriMesh ghost = loader.loadModel("models"+File.separator + "chicken.obj");
		//addModel(ghost);
	}
	public void move(Point3D ghostPosition){
		setLocation(ghostPosition);
	}

}
