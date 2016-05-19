package gameEngine;


import java.awt.geom.Point2D;

import game.MyCharacter;
import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;
import sage.input.action.AbstractInputAction;
import sage.physics.IPhysicsObject;

public class MoveXAxis extends AbstractInputAction{
	private MyCharacter player;
	private IPhysicsObject playerP;
	private float speed;
	public MoveXAxis(MyCharacter p1,IPhysicsObject playerP, float s){
		player = p1;
		this.playerP = playerP;
		speed = s;
	}
	public void performAction(float time, net.java.games.input.Event e){
		if (e.getValue() < -0.2) {
			Matrix3D rot = player.getLocalRotation(); 
			Vector3D dir = new Vector3D(10f,0,0); 
			dir = dir.mult(rot); 
			dir.scale((double)(speed * time)); 
			float move[] = {(float)dir.getX() + playerP.getLinearVelocity()[0],(float)dir.getY()+playerP.getLinearVelocity()[1],(float)dir.getZ()+playerP.getLinearVelocity()[2]};

			Point2D.Double pt = new Point2D.Double(move[0],move[2]);
			if(pt.distance(0,0)>30f){
				
				double hyp = pt.distance(0,0);
				double opp = move[0];
				float theta = (float) (float)Math.asin(opp/hyp) ;
				move[0] = (float) (Math.sin(theta)*30f);
				System.out.println(hyp);
			}
			
			
			playerP.setLinearVelocity(move);
		}	
		else if (e.getValue() > 0.2) {
			Matrix3D rot = player.getLocalRotation(); 
			Vector3D dir = new Vector3D(10f,0,0); 
			dir = dir.mult(rot); 
			dir.scale(-(double)(speed * time)); 
			float move[] = {(float)dir.getX() + playerP.getLinearVelocity()[0],(float)dir.getY()+playerP.getLinearVelocity()[1],(float)dir.getZ()+playerP.getLinearVelocity()[2]};
			Point2D.Double pt = new Point2D.Double(move[0],move[2]);
			if(pt.distance(0,0)>30f){
				
				double hyp = pt.distance(0,0);
				double opp = move[0];
				float theta = (float) (float)Math.asin(opp/hyp) ;
				move[0] = (float) (Math.sin(theta)*30f);
				System.out.println(hyp);
			}
			playerP.setLinearVelocity(move);
		}	
	}
}
