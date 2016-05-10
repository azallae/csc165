package game;

import java.io.File;

import sage.model.loader.ogreXML.OgreXMLParser;
import sage.scene.Group;
import sage.scene.Model3DTriMesh;
import sage.scene.SceneNode;


public class Kitty extends MyCharacter{
	private Model3DTriMesh myObject;
	private Group model;
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

}