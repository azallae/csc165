package gameEngine;



import game.MyCharacter;
import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;
import sage.input.action.AbstractInputAction;

public class MoveYAxis extends AbstractInputAction{
	private MyCharacter player;
	private float speed;
	public MoveYAxis(MyCharacter player2, float s){
		player = player2;
		speed = s;
	}
	public void performAction(float time, net.java.games.input.Event e) {
		if (e.getValue() < -0.5) {
			Matrix3D rot = player.getLocalRotation(); 
			Vector3D dir = new Vector3D(0,0,1); 
			dir = dir.mult(rot); 
			dir.scale((double)(speed * time)); 
			player.translate((float)dir.getX(),(float)dir.getY(),(float)dir.getZ());
		}	
		else if (e.getValue() > 0.2) {
			Matrix3D rot = player.getLocalRotation(); 
			Vector3D dir = new Vector3D(0,0,1);
			dir = dir.mult(rot); 
			dir.scale(-(double)(speed * time)); 
			player.translate((float)dir.getX(),(float)dir.getY(),(float)dir.getZ());
		}	
	}
}