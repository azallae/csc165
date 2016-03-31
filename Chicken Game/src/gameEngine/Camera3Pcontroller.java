package gameEngine;

import net.java.games.input.Event;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;
import sage.camera.ICamera;
import sage.input.IInputManager;
import sage.input.action.AbstractInputAction;
import sage.input.action.IAction;
import sage.scene.SceneNode;
import sage.util.MathUtils;

public class Camera3Pcontroller {
	private ICamera cam; //the camera being controlled 
	private SceneNode target; //the target the camera looks at 
	private float cameraAzimuth; //rotation of camera around target Y axis 
	private float cameraElevation; //elevation of camera above target 
	private float cameraDistanceFromTarget; 
	private Point3D targetPos; // avatar’s position in the world 
	private Vector3D worldUpVec; 
	private boolean isDetached;
	 
	public Camera3Pcontroller(ICamera cam, SceneNode target, IInputManager inputMgr, String controllerName) { 
		this.cam = cam; 
		this.target = target; 
		worldUpVec = new Vector3D(0,1,0); 
		cameraDistanceFromTarget = 20f; 
		cameraAzimuth = 180; // start from BEHIND and ABOVE the target 
		cameraElevation = 20f; // elevation is in degrees 
		update(0.0f); // initialize camera state 
		setupInput(inputMgr, controllerName); 
	}
	 
	public void update(float time) { 
		updateTarget(); 
		updateCameraPosition(); 
		cam.lookAt(targetPos, worldUpVec); // SAGE built-in function 
	} 

	private void updateTarget() { 
		targetPos = new Point3D(target.getWorldTranslation().getCol(3));
	} 
	 
	private void updateCameraPosition() { 
		double theta = cameraAzimuth; 
		double phi = cameraElevation ; 
		double r = cameraDistanceFromTarget; 
	
		// calculate new camera position in Cartesian coords 
		Point3D relativePosition = MathUtils.sphericalToCartesian(theta, phi, r); 
		Point3D desiredCameraLoc = relativePosition.add(targetPos); 
		cam.setLocation(desiredCameraLoc); 
	} 
	
	private void setupInput(IInputManager im, String cn) { 
		IAction turnLR = new TurnLR();
		im.associateAction(cn,
				net.java.games.input.Component.Identifier.Axis.RX,
				turnLR,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN); 
		IAction orbitActionLeft = new OrbitAroundActionLeft();
		im.associateAction(cn, 
				net.java.games.input.Component.Identifier.Key.LEFT, 
				orbitActionLeft, 
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		IAction orbitActionRight = new OrbitAroundActionRight();
		im.associateAction(cn, 
				net.java.games.input.Component.Identifier.Key.RIGHT, 
				orbitActionRight,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		IAction zoomIn = new ZoomIn();
		im.associateAction(cn,
				net.java.games.input.Component.Identifier.Key.UP,
				zoomIn,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		IAction zoomOut = new ZoomOut();
		im.associateAction(cn, 
				net.java.games.input.Component.Identifier.Key.DOWN,
				zoomOut,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		IAction zoomIO = new ZoomIO();
		im.associateAction(cn,
				net.java.games.input.Component.Identifier.Axis.RY,
				zoomIO, 
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		IAction toggleDetached = new ToggleDetached();
		im.associateAction(cn, 
				net.java.games.input.Component.Identifier.Key.Q,
				toggleDetached,
				IInputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
		im.associateAction(cn,
				net.java.games.input.Component.Identifier.Button._5,
				toggleDetached, 
				IInputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
	} 
	
	public boolean isDetached() {
		return isDetached;
	}

	private class ToggleDetached extends AbstractInputAction {

		@Override
		public void performAction(float time, Event evt) {
			isDetached = !isDetached;
		}
	}
	
	private class TurnLR extends AbstractInputAction { 
		public void performAction(float time, Event evt) { 
			float rotAmount=0.1f*time;
			
			if (evt.getValue() < -0.2) {
				cameraAzimuth += rotAmount;
				cameraAzimuth = cameraAzimuth % 360; 
				if (!isDetached())
					target.rotate(rotAmount, worldUpVec);
			}
			else if (evt.getValue() > 0.2) {
				rotAmount = rotAmount*(-1);
				cameraAzimuth += rotAmount;
				cameraAzimuth = cameraAzimuth % 360; 
				if (!isDetached())
					target.rotate(rotAmount, worldUpVec);
			}			
		}
	}

	private class OrbitAroundActionLeft extends AbstractInputAction { 
		public void performAction(float time, Event evt) { 
			float rotAmount=1f;
			
			cameraAzimuth += rotAmount; 
			cameraAzimuth = cameraAzimuth % 360; 
			
			if (!isDetached())
				target.rotate(rotAmount, worldUpVec);
		}
	}
	
	private class OrbitAroundActionRight extends AbstractInputAction { 
		public void performAction(float time, Event evt) { 
			float rotAmount=-1f;
			
			cameraAzimuth += rotAmount; 
			cameraAzimuth = cameraAzimuth % 360;
			
			if (!isDetached())
				target.rotate(rotAmount, worldUpVec);
		}
	}
	
	private class ZoomIO extends AbstractInputAction {
		@Override
		public void performAction(float time, Event evt) {
			float zoomAmount=0.1f; 
						
			if (evt.getValue() < -0.2) {
				if (cameraDistanceFromTarget > 5)
					cameraDistanceFromTarget -= zoomAmount;
			}	
			else if (evt.getValue() > 0.2) {
				if (cameraDistanceFromTarget < 50)
					cameraDistanceFromTarget += zoomAmount;
			}	
		}		
	}
	
	private class ZoomIn extends AbstractInputAction {
		@Override
		public void performAction(float time, Event evt) {
			float zoomAmount=0.1f; 
			
			if (cameraDistanceFromTarget > 5)
				cameraDistanceFromTarget -= zoomAmount;	
		}		
	}
	
	private class ZoomOut extends AbstractInputAction {
		@Override
		public void performAction(float time, Event evt) {
			float zoomAmount=0.1f; 
			
			if (cameraDistanceFromTarget < 50)
				cameraDistanceFromTarget += zoomAmount;	
		}		
	}

	public Point3D getTargetPosition() {
		return targetPos;
	}
}