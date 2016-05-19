package game;

import java.awt.Color;

import gameEngine.RotationController;
import gameEngine.TranslationController;
import graphicslib3D.Vector3D;
import sage.event.IEventListener;
import sage.event.IGameEvent;
import sage.scene.Group;
import sage.scene.shape.Sphere;

public class PowerUps extends Group implements IEventListener{

	public PowerUps(){
		Sphere blueSphere1 = new Sphere(0.5, 20, 20, Color.BLUE);
		Sphere blueSphere2 = new Sphere(0.5, 20, 20, Color.BLUE);
		Sphere redSphere = new Sphere(1.0, 20, 20, Color.WHITE);

		Group group2 = new Group(); // planet system rotation
		Group group3 = new Group(); // planet system position
		Group group4 = new Group(); // moon system rotation

		this.addChild(redSphere);
		this.addChild(group2);
		group2.addChild(group3);
		group3.addChild(blueSphere1);
		group3.addChild(blueSphere2);
		this.setIsTransformSpaceParent(true);
		group2.setIsTransformSpaceParent(true);
		group3.setIsTransformSpaceParent(true);
		group4.setIsTransformSpaceParent(true);
		blueSphere2.setIsTransformSpaceParent(true);
		blueSphere1.setIsTransformSpaceParent(true);
		redSphere.setIsTransformSpaceParent(true);
		
		blueSphere1.translate(1.5f,-1,0);

		blueSphere2.translate(-1.5f,-1,0);
		
		
		Vector3D planetRevV = new Vector3D(0,1,0);
		RotationController planetRev = new RotationController(10,planetRevV);
		planetRev.addControlledNode(group2);
		group2.addController(planetRev);
		TranslationController move = new TranslationController(1000);
		move.addControlledNode(group3);
		group3.addController(move);
		
		
	}
	public boolean handleEvent(IGameEvent event){
		return false;
		
	}
	
}
