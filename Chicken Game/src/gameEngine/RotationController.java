package gameEngine;

import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;
import sage.scene.Controller;
import sage.scene.SceneNode;

public class RotationController extends Controller { 
	private double rotationRate = .002 ; // movement per second 
	private Vector3D direction; 

	public RotationController(double r, Vector3D d) { 
		direction = d;
		rotationRate = r;
	}

	public void update(double time) { 
		Matrix3D rot = new Matrix3D();
		rot.rotate(rotationRate, direction);
		for (SceneNode node : controlledNodes) { 
			Matrix3D curRot = node.getLocalRotation(); 
			curRot.concatenate(rot); 
			node.setLocalRotation(curRot); 
		} 
	} 
} 