package game;

import java.io.File;

import graphicslib3D.Vector3D;
import sage.model.loader.OBJLoader;
import sage.scene.TriMesh;

public class Fence extends MyCharacter{
	
	public Fence(){
		super();
		OBJLoader loader = new OBJLoader();
		TriMesh fence = loader.loadModel("models"+File.separator + "fence.obj");
		this.scale(3f, 3f, 3f);
		addModel(fence);
	}
}
