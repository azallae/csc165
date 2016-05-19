package game;

import sage.scene.Controller;
import sage.scene.SceneNode;

public class BaseController extends Controller{
	public BaseController(){
		
	}
	public void update(double elapsedTime){
		
	}
	public void removeNode(SceneNode s){
		controlledNodes.remove(s);
	}
}
