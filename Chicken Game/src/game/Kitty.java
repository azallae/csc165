package game;

import java.io.File;
import sage.model.loader.OBJLoader;
import sage.scene.TriMesh;


public class Kitty extends MyCharacter{
	public Kitty(){
		super();
		OBJLoader loader = new OBJLoader();
		TriMesh kitty = loader.loadModel("models"+File.separator + "KITTY.obj");
		addModel(kitty);
	}
   
}