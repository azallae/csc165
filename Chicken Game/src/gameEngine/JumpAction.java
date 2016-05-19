package gameEngine;

import game.ChickenGame;
import game.MyCharacter;
import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;
import sage.input.action.AbstractInputAction;
import sage.physics.IPhysicsObject;

public class JumpAction extends AbstractInputAction{
	private MyCharacter player;
	private IPhysicsObject playerP;
	private float speed;
	private ChickenGame g;
	public JumpAction(MyCharacter p2, IPhysicsObject playerP, float s, ChickenGame g){
		player = p2;
		this.playerP = playerP;
		speed = s;
		this.g = g;
	}

	public void performAction(float time, net.java.games.input.Event e){
		if(g.canChickenJump()){
			if(playerP.getLinearVelocity()[1] < .1f && playerP.getLinearVelocity()[1] > -.1f){
				Matrix3D rot = player.getLocalRotation(); 
				Vector3D dir = new Vector3D(0,200f,0); 
				dir = dir.mult(rot); 
				dir.scale((double)(speed * time)); 
				float move[] = {(float)dir.getX() + playerP.getLinearVelocity()[0],(float)dir.getY()+playerP.getLinearVelocity()[1],(float)dir.getZ()+playerP.getLinearVelocity()[2]};
				playerP.setLinearVelocity(move);
			}	
		}
	}

}