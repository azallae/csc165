package gameEngine;


import game.MyCharacter;
import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;
import net.java.games.input.Event;
import sage.input.action.AbstractInputAction;

public class MoveForward extends AbstractInputAction{
	private MyCharacter player;
	private float speed;
	public MoveForward(MyCharacter p2, float s){
		player = p2;
		speed = s;
	}
	public void performAction(float time, Event e){
		Matrix3D rot = player.getLocalRotation(); 
		Vector3D dir = new Vector3D(0,0,1);
		dir = dir.mult(rot); 
		dir.scale((double)(speed * time)); 
		player.translate((float)dir.getX(),(float)dir.getY(),(float)dir.getZ());
	} 
}