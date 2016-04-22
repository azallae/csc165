package game;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

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
import graphicslib3D.Vector3D;
import sage.app.BaseGame;
import sage.camera.ICamera;
import sage.camera.JOGLCamera;
import sage.display.IDisplaySystem;
import sage.event.EventManager;
import sage.event.IEventManager;
import sage.input.IInputManager;
import sage.input.action.IAction;
import sage.renderer.IRenderer;
import sage.scene.SceneNode;
import sage.scene.SkyBox;
import sage.scene.state.RenderState.RenderStateType;
import sage.scene.state.RenderState;
import sage.scene.state.TextureState;
import sage.terrain.AbstractHeightMap;
import sage.terrain.ImageBasedHeightMap;
import sage.terrain.TerrainBlock;
import sage.texture.Texture;
import sage.texture.TextureManager;

import sage.model.loader.OBJLoader;
import sage.scene.TriMesh;

public class ChickenGame extends BaseGame{

	private Chicken player;
	private Kitty kitty;

	private ICamera camera;
	private Camera3Pcontroller cc;
	private IRenderer renderer;
	private IDisplaySystem display;

	private IEventManager eventMgr;

	private IInputManager im;
	private String gpName;
	private String kpName;

	private String scriptName = "axisLines.js";
	private ScriptEngine engine;
	private File scriptFile;
	private long fileLastModifiedTime = 0;
	private SceneNode rootNode;

	private TerrainBlock terrain;
	private SkyBox skyBox;
	private String textures= "textures" + File.separator;
	
	OBJLoader loader = new OBJLoader();
	TriMesh chicken = loader.loadModel("models" + File.separator + "chicken.obj");

	protected void initGame(){

		initScript();
		initDisplay();
		im = getInputManager();
		gpName = im.getFirstGamepadName();
		kpName = im.getKeyboardName();


		createScene();

		initTerrain();
		initGameObjects();
		initHUD();
		initPlayers();
		initInput();


	}
	
	private void createScene(){
		//rootNode = new Group("Root Node");
		skyBox = new SkyBox("world");
      		skyBox.scale(50.0f, 50.0f, 50.0f);
		Texture w1 = TextureManager.loadTexture2D(textures + "SkyBox1.jpg");
		Texture w2 = TextureManager.loadTexture2D(textures + "SkyBox2.jpg");
		Texture w3 = TextureManager.loadTexture2D(textures + "SkyBox3.jpg");
		Texture w4 = TextureManager.loadTexture2D(textures + "SkyBox4.jpg");
		Texture w5 = TextureManager.loadTexture2D(textures + "SkyBox5.jpg");
		Texture w6 = TextureManager.loadTexture2D(textures + "SkyBox6.jpg");
		skyBox.setTexture(SkyBox.Face.North, w1);
		skyBox.setTexture(SkyBox.Face.South, w3);
		skyBox.setTexture(SkyBox.Face.West, w4);
		skyBox.setTexture(SkyBox.Face.East, w2);
		skyBox.setTexture(SkyBox.Face.Up, w5);
		skyBox.setTexture(SkyBox.Face.Down, w6);
		addGameWorldObject(skyBox);
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
		
		
		player = new Chicken();
		//player.scale(.30f,.30f,.30f);
		textureObj(player, "chicken.png");
		addGameWorldObject(player);
		player.updateLocalBound();
		

		
		//camera.setLocation(new Point3D(0,25,-23));
		//camera.lookAt(new Point3D(0,0,0), new Vector3D(0,1,0));
		
		
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
		kitty = new Kitty();
		textureObj(kitty, "KITTY.png");
		addGameWorldObject(kitty);
		kitty.updateLocalBound();
		Matrix3D k1M = kitty.getLocalTranslation(); 
		kitty.translate(20f,1f,20f); 
		kitty.setLocalTranslation(k1M); 
		addGameWorldObject(kitty); 



	}
	public void textureObj(MyCharacter c, String file) {
		Texture objTexture = TextureManager.loadTexture2D("materials" + File.separator + file); 
		objTexture.setApplyMode(Texture.ApplyMode.Replace); 
		TextureState objTextureState = (TextureState) display.getRenderer().createRenderState(RenderState.RenderStateType.Texture); 
		objTextureState.setTexture(objTexture, 0); 
		objTextureState.setEnabled(true); 
		c.setRenderState(objTextureState); 
		c.updateRenderStates();
	}


	public void update(float elapsedTimeMS){
		//script
		long modTime = scriptFile.lastModified();
		if (modTime > fileLastModifiedTime)
		{ fileLastModifiedTime = modTime;
		this.runScript();
		removeGameWorldObject(rootNode);
		rootNode = (SceneNode) engine.get("rootNode");
		addGameWorldObject(rootNode);

		}


  		
		cc.update(elapsedTimeMS);
		super.update(elapsedTimeMS);
		
		Point3D camLoc = camera.getLocation();
  		Matrix3D camTranslation = new Matrix3D();
  		camTranslation.translate(camLoc.getX(), camLoc.getY(), camLoc.getZ());
  		skyBox.setLocalTranslation(camTranslation);
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

	public MyCharacter getPlayer(){
		return player;
	}
	public void initScript(){
		ScriptEngineManager factory = new ScriptEngineManager(); 
		List<ScriptEngineFactory> list = factory.getEngineFactories(); 
		engine = factory.getEngineByName("js"); 
		scriptFile = new File(scriptName); 
		runScript();
		rootNode = (SceneNode) engine.get("rootNode");
		addGameWorldObject(rootNode);

	}


	private void runScript(){
		try{
			FileReader fileReader = new FileReader(scriptFile);
			engine.eval(fileReader);
			fileReader.close();
		}
		catch (FileNotFoundException e1){
			System.out.println(scriptFile + " not found " + e1); }
		catch (IOException e2){
			System.out.println("IO problem with " + scriptFile + e2); }
		catch (ScriptException e3){
			System.out.println("ScriptException in " + scriptFile + e3); }
		catch (NullPointerException e4){
			System.out.println ("Null ptr exception reading " + scriptFile + e4); }
	}


	private void initTerrain()
	{ // create height map and terrain block
		ImageBasedHeightMap myHeightMap =
				new ImageBasedHeightMap("height.jpg");
		TerrainBlock imageTerrain = createTerBlock(myHeightMap);
		// create texture and texture state to color the terrain
		TextureState grassState;
		Texture grassTexture = TextureManager.loadTexture2D("perfect-cloud.jpg");
		grassTexture.setApplyMode(sage.texture.Texture.ApplyMode.Replace);
		grassState = (TextureState)
				display.getRenderer().createRenderState(RenderStateType.Texture);
		grassState.setTexture(grassTexture,0);
		grassState.setEnabled(true);
		// apply the texture to the terrain
		imageTerrain.setRenderState(grassState);
		addGameWorldObject(imageTerrain);
	}
	private TerrainBlock createTerBlock(AbstractHeightMap heightMap){
		float heightScale = .005f;
		Vector3D terrainScale = new Vector3D(.5, heightScale, .5);
		// use the size of the height map as the size of the terrain
		int terrainSize = heightMap.getSize();
		// specify terrain origin so heightmap (0,0) is at world origin
		float cornerHeight =
				heightMap.getTrueHeightAtPoint(0, 0) * heightScale;
		Point3D terrainOrigin = new Point3D(-100, -cornerHeight, -100);
		// create a terrain block using the height map
		String name = "Terrain:" + heightMap.getClass().getSimpleName();
		TerrainBlock tb = new TerrainBlock(name, terrainSize, terrainScale,
				heightMap.getHeightData(), terrainOrigin);
		return tb;
	}
}
