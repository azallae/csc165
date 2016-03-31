package game;

import java.awt.Color;

import gameEngine.Camera3Pcontroller;
import gameEngine.MoveBackward;
import gameEngine.MoveForward;
import gameEngine.MoveLeft;
import gameEngine.MoveRight;
import gameEngine.MoveXAxis;
import gameEngine.MoveYAxis;
import gameEngine.MyDisplaySystem;
import gameEngine.Quit;
import graphicslib3D.Matrix3D;
import graphicslib3D.Point3D;
import sage.app.BaseGame;
import sage.camera.ICamera;
import sage.camera.JOGLCamera;
import sage.display.IDisplaySystem;
import sage.event.EventManager;
import sage.event.IEventManager;
import sage.input.IInputManager;
import sage.input.action.IAction;
import sage.renderer.IRenderer;
import sage.scene.shape.Line;

public class ChickenGame extends BaseGame{

	private MyCharacter player;
	
	private ICamera camera;
	private Camera3Pcontroller cc;
	private IRenderer renderer;
	private IDisplaySystem display;

	private IEventManager eventMgr;
	
	private IInputManager im;
	private String gpName;
	private String kpName;
	
	protected void initGame(){
		
		initDisplay();
		im = getInputManager();
		gpName = im.getFirstGamepadName();
		kpName = im.getKeyboardName();
		
		

		initGameObjects();
		initHUD();
		initPlayers();
		initInput();

		
	}

	private void initDisplay() {		
		display = createDisplaySystem(); 
		setDisplaySystem(display); 
		renderer = display.getRenderer();
		
	}

	private void initInput() {


		//controls
		if(gpName!=null){
			//GAMEPAD CONTROLS
			IAction yAxisMove = new MoveYAxis(player, 0.01f);
			im.associateAction(gpName,
					net.java.games.input.Component.Identifier.Axis.Y, yAxisMove,
					IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

			IAction xAxisMove = new MoveXAxis(player, 0.01f);
			im.associateAction(gpName,
					net.java.games.input.Component.Identifier.Axis.X, xAxisMove,
					IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
			super.update((float) 0.0);


		}
		//KEYBOARD CONTROLS
		IAction wPress = new MoveForward(player, 0.01f);
		im.associateAction(kpName,
				net.java.games.input.Component.Identifier.Key.W, wPress,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		IAction sPress = new MoveBackward(player, 0.01f);
		im.associateAction(kpName,
				net.java.games.input.Component.Identifier.Key.S, sPress,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		IAction aPress = new MoveLeft(player, 0.01f);
		im.associateAction(kpName,
				net.java.games.input.Component.Identifier.Key.A, aPress,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		IAction dPress = new MoveRight(player, 0.01f);
		im.associateAction(kpName,
				net.java.games.input.Component.Identifier.Key.D, dPress,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		IAction ESCAPE = new Quit(this);
		im.associateAction(kpName,
				net.java.games.input.Component.Identifier.Key.ESCAPE, ESCAPE,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

		
	}

	private void initPlayers() {
		player = new MyCharacter();
		Matrix3D p1M = player.getLocalTranslation(); 
		player.translate(0,1f,0); 
		player.setLocalTranslation(p1M); 
		addGameWorldObject(player); 
		camera = new JOGLCamera(renderer); 
		camera.setPerspectiveFrustum(60, 2, 1, 1000); 
		cc = new Camera3Pcontroller(camera, player, im, gpName);
		// TODO Auto-generated method stub
		
	}

	private void initHUD() {
		// TODO Auto-generated method stub
		
	}

	private void initGameObjects() {
		display = getDisplaySystem();
		// TODO Auto-generated method stub
		
		
		Point3D origin = new Point3D(0,0,0);
		Point3D xEnd = new Point3D(100,0,0);
		Point3D yEnd = new Point3D(0,100,0);
		Point3D zEnd = new Point3D(0,0,100);
		Line xAxis = new Line (origin, xEnd, Color.red, 2);
		Line yAxis = new Line (origin, yEnd, Color.green, 2);
		Line zAxis = new Line (origin, zEnd, Color.blue, 2);
		addGameWorldObject(xAxis); 
		addGameWorldObject(yAxis);
		addGameWorldObject(zAxis);
		
	}
	
	
	public void update(float elapsedTimeMS){
		cc.update(elapsedTimeMS);
		super.update(elapsedTimeMS);
	}
	
	protected void render() { 
		renderer.setCamera(camera); 
		super.render(); 
	}
	
	private IDisplaySystem createDisplaySystem() { 
		IDisplaySystem display = new MyDisplaySystem(700, 300, 24, 20, true, "sage.renderer.jogl.JOGLRenderer"); 
		System.out.print("\nWaiting for display creation..."); 
		int count = 0; 

		// wait until display creation completes or a timeout occurs 
		while (!display.isCreated()) { 
			try 
			{ Thread.sleep(10); } 
			catch (InterruptedException e) 
			{ throw new RuntimeException("Display creation interrupted"); } 

			count++; 
			System.out.print("+"); 
			if (count % 80 == 0) { System.out.println(); } 

			if (count > 2000) // 20 seconds (approx.) 
			{ throw new RuntimeException("Unable to create display"); } 
		} 
		System.out.println(); 
		return display ; 
	} 

	protected void shutdown() { 
		display.close(); 
		//...other shutdown methods here as necessary... 
	}


}
