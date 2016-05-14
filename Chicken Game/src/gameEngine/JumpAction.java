package gameEngine;

import game.MyCharacter;
import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;
import sage.input.action.AbstractInputAction;
import sage.physics.IPhysicsObject;

public class JumpAction extends AbstractInputAction{
	private MyCharacter player;
	private IPhysicsObject playerP;
	private float speed;
	public JumpAction(MyCharacter p2, IPhysicsObject playerP, float s){
		player = p2;
		this.playerP = playerP;
		speed = s;
	}

	public void performAction(float time, net.java.games.input.Event e){
		System.out.println(playerP.getLinearVelocity()[1]);
		if(playerP.getLinearVelocity()[1] < .1f && playerP.getLinearVelocity()[1] > -.1f){
			Matrix3D rot = player.getLocalRotation(); 
			Vector3D dir = new Vector3D(0,300f,0); 
			dir = dir.mult(rot); 
			dir.scale((double)(speed * time)); 
			float move[] = {(float)dir.getX() + playerP.getLinearVelocity()[0],(float)dir.getY()+playerP.getLinearVelocity()[1],(float)dir.getZ()+playerP.getLinearVelocity()[2]};
			if(move[0] > 30){
				move[0] = 30f;
			}
			if(move[2] > 30){
				move[2] = 30f;
			}
			playerP.setLinearVelocity(move);
		}	
	}
	
}