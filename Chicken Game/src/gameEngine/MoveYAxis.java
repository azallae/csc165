package gameEngine;



import game.MyCharacter;
import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;
import sage.input.action.AbstractInputAction;
import sage.physics.IPhysicsObject;

public class MoveYAxis extends AbstractInputAction{
	private MyCharacter player;
	private IPhysicsObject playerP;
	private float speed;
	public MoveYAxis(MyCharacter player2,IPhysicsObject playerP, float s){
		player = player2;
		this.playerP = playerP;
		speed = s;
	}

	public void performAction(float time, net.java.games.input.Event e){
		if (e.getValue() < -0.5) {
			Matrix3D rot = player.getLocalRotation(); 
			Vector3D dir = new Vector3D(0,0,10f); 
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
		else if (e.getValue() > 0.2) {
			Matrix3D rot = player.getLocalRotation(); 
			Vector3D dir = new Vector3D(0,0,10f); 
			dir = dir.mult(rot); 
			dir.scale(-(double)(speed * time)); 
			float move[] = {(float)dir.getX() + playerP.getLinearVelocity()[0],(float)dir.getY()+playerP.getLinearVelocity()[1],(float)dir.getZ()+playerP.getLinearVelocity()[2]};
			
			if(move[0] < -30){
				move[0] = -30f;
			}
			if(move[2] < -30){
				move[2] = -30f;
			}
			playerP.setLinearVelocity(move);
		}	
	}
}