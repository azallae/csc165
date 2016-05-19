package game;

import java.io.File;

import graphicslib3D.Point3D;
import sage.model.loader.OBJLoader;
import sage.scene.TriMesh;


public class Chicken extends MyCharacter{
	public Chicken(){
		super();
		OBJLoader loader = new OBJLoader();
		TriMesh chick = loader.loadModel("models"+File.separator + "chicken.obj");
		addModel(chick);
	}
	
   
}