package gameEngine;

import game.Chicken;
import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;
import sage.input.action.AbstractInputAction;

public class JumpAction extends AbstractInputAction{
	private Chicken player;
	private float speed;
	public JumpAction(Chicken p2, float s){
		player = p2;
		speed = s;
	}
	public void performAction(float time, net.java.games.input.Event e){		
		//Matrix3D rot = player.getLocalRotation(); 
		Vector3D dir = new Vector3D(0,1,0);
		//dir = dir.mult(rot); 
		dir.scale((double)(speed * time)); 
		player.translate((float)dir.getX(),(float)dir.getY(),(float)dir.getZ());
	} 
}