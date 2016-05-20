package game;

import gameEngine.*;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.UUID;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import game.network.GameClientTCP;
import gameEngine.Camera3Pcontroller;
import gameEngine.MoveYAxis;
import gameEngine.MyDisplaySystem;
import gameEngine.Quit;
import graphicslib3D.Matrix3D;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;
import sage.app.BaseGame;
import sage.audio.AudioManagerFactory;
import sage.audio.AudioResource;
import sage.audio.AudioResourceType;
import sage.audio.IAudioManager;
import sage.audio.Sound;
import sage.audio.SoundType;
import sage.camera.ICamera;
import sage.camera.JOGLCamera;
import sage.display.IDisplaySystem;
import sage.event.EventManager;
import sage.event.IEventManager;
import sage.input.IInputManager;
import sage.input.action.IAction;
import sage.networking.IGameConnection.ProtocolType;
import sage.renderer.IRenderer;
import sage.scene.HUDString;
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
import sage.physics.IPhysicsEngine;
import sage.physics.IPhysicsObject;
import sage.physics.PhysicsEngineFactory;

public class ChickenGame extends BaseGame{

	protected Chicken player;
	protected Kitty kitty;
	protected GhostAvatar ghost;
	private PowerUps pwrUp;

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

	protected IPhysicsEngine physicsEngine;
	private IPhysicsObject playerP, groundPlaneP;
	protected IPhysicsObject kittyP;
	private IPhysicsObject ghostP;

	private IAudioManager audioMgr;
	private Sound chickenNoise1, catNoise1;
	private AudioResource resource1, resource2;
	private HUDString timeString;
	private float time;
	private boolean isPowerUpOn = false;
	private float timeStamp;
	private boolean canJump = true;
	private float powerUpRespawnTimer;
	private boolean pwrUpIsGone;
	private boolean ghostExists;
	protected boolean kittyExists;

	private String serverAddress;
	private int serverPort;
	private ProtocolType serverProtocol;
	private GameClientTCP thisClient;
	private boolean connected;
	private boolean mainClient;
	private GhostKitty gKitty;
	public float gameTime;
	// assumes main() gets address/port from command line
	public ChickenGame(){
		super();
	}
	public ChickenGame(String serverAddr, int sPort)	{ 
		super();
		this.serverAddress = serverAddr;
		this.serverPort = sPort;
		this.serverProtocol = ProtocolType.TCP;
	}


	@Override
	protected void initGame(){
		try{
			thisClient = new GameClientTCP(InetAddress.getByName(serverAddress),
					serverPort, serverProtocol, this); 
		}
		catch (UnknownHostException e) {
			e.printStackTrace(); 
		}
		catch (IOException e) { 
			e.printStackTrace(); 
		}
		if (thisClient != null) {
			thisClient.sendJoinMessage(); 
		}
		initScript();
		initDisplay();
		im = getInputManager();
		gpName = im.getFirstGamepadName();
		kpName = im.getKeyboardName();

		eventMgr = EventManager.getInstance();


		createScene();
		initTerrain();
		initGameObjects();
		initHUD();
		initPlayers();

		super.update((float) 0.0);
		initPhysicsSystem();
		createSagePhysicsWorld();

		initInput();
		initAudio();


	}

	protected void createSagePhysicsWorld(){
		float mass = 1.0f;

		playerP = physicsEngine.addSphereObject(physicsEngine.nextUID(), 
				mass, player.getWorldTransform().getValues(), 2.3f);
		playerP.setDamping(.9f, .9f);;;

		player.setPhysicsObject(playerP);

		float up[] = {0, 1f, 0}; // {0,1,0} is flat

		/*		kittyP = physicsEngine.addSphereObject(physicsEngine.nextUID(), mass, kitty.getWorldTransform().getValues(), 1f);
		kitty.setPhysicsObject(kittyP);
		kittyP.setDamping(.9f, .9f);*/

		groundPlaneP =
				physicsEngine.addStaticPlaneObject(physicsEngine.nextUID(),
						terrain.getWorldTransform().getValues(), up, 0.0f);
		groundPlaneP.setBounciness(1.0f);
		terrain.setPhysicsObject(groundPlaneP);

	}

	public void initAudio(){

		audioMgr = AudioManagerFactory.createAudioManager("sage.audio.joal.JOALAudioManager");
		if(!audioMgr.initialize()){
			System.out.println("Audio Manager failed to initialize!");
			return;
		}
		resource1 = audioMgr.createAudioResource("chickenNoise.wav", AudioResourceType.AUDIO_SAMPLE);
		resource2 = audioMgr.createAudioResource("catScream.wav", AudioResourceType.AUDIO_SAMPLE);
		chickenNoise1 = new Sound(resource1, SoundType.SOUND_EFFECT, 100, true);
		catNoise1 = new Sound(resource2, SoundType.SOUND_EFFECT, 100, true);
		chickenNoise1.initialize(audioMgr);
		catNoise1.initialize(audioMgr);
		chickenNoise1.setMaxDistance(50.0f);
		chickenNoise1.setMinDistance(3.0f);
		chickenNoise1.setRollOff(5.0f);
		chickenNoise1.setLocation(new Point3D(player.getWorldTransform().getCol(3)));
		catNoise1.setMaxDistance(50.0f);
		catNoise1.setMinDistance(3.0f);
		catNoise1.setRollOff(5.0f);

		setEarParameters();

		chickenNoise1.play();
		catNoise1.play();
	}

	public void turnOffSound(){
		chickenNoise1.release(audioMgr);
		catNoise1.release(audioMgr);

		resource1.unload();
		resource2.unload();

		audioMgr.shutdown();
	}

	@Override
	public void exit(){
		turnOffSound();
		super.exit();
	}

	public void setEarParameters(){
		audioMgr.getEar().setLocation(player.getLocation());

		audioMgr.getEar().setOrientation(new Vector3D(0,0,1), new Vector3D(0,1,0));
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
		//GAMEPAD CONTROLS
		IAction yAxisMove = new MoveYAxis(player,playerP, .01f);
		im.associateAction(gpName,
				net.java.games.input.Component.Identifier.Axis.Y, yAxisMove,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

		/*IAction xAxisMove = new MoveXAxis(player, playerP, 1f);
		im.associateAction(gpName,
				net.java.games.input.Component.Identifier.Axis.X, xAxisMove,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);*/
		IAction jPress = new JumpAction(player, playerP, 0.01f, this);
		im.associateAction(gpName,
				net.java.games.input.Component.Identifier.Button._1,
				jPress, 
				IInputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);



		//KEYBOARD CONTROLS
		/*IAction wPress = new MoveForward(player[], 0.01f);
		im.associateAction(kpName,
				net.java.games.input.Component.Identifier.Key.W, wPress,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		IAction sPress = new MoveBackward(player[], 0.01f);
		im.associateAction(kpName,
				net.java.games.input.Component.Identifier.Key.S, sPress,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		IAction aPress = new MoveLeft(player[], 0.01f);
		im.associateAction(kpName,
				net.java.games.input.Component.Identifier.Key.A, aPress,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		IAction dPress = new MoveRight(player[], 0.01f);
		im.associateAction(kpName,
				net.java.games.input.Component.Identifier.Key.D, dPress,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);*/
		IAction ESCAPE = new Quit(this);
		im.associateAction(kpName,
				net.java.games.input.Component.Identifier.Key.ESCAPE, ESCAPE,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		super.update((float) 0.0);

	}

	private void initPlayers() {




		player = new Chicken();
		//player.scale(.30f,.30f,.30f);
		textureObj(player, "chicken.png");
		player.updateLocalBound();

		player.updateGeometricState(1.0f, true);


		//camera.setLocation(new Point3D(0,25,-23));
		//camera.lookAt(new Point3D(0,0,0), new Vector3D(0,1,0));


		Matrix3D p1M = player.getLocalTranslation(); 
		player.translate(0,5f,0); 
		player.setLocalTranslation(p1M); 
		addGameWorldObject(player); 

		camera = new JOGLCamera(renderer); 
		camera.setPerspectiveFrustum(60, 2, 1, 1000); 
		cc = new Camera3Pcontroller(camera, player, im, gpName);

		// TODO Auto-generated method stub

		//ground plane

		// add a graphical Ground plane
		/*		groundPlane = new Rectangle();
		groundPlane.rotate(90, new Vector3D(1,0,0));

		groundPlane.scale(20, 20, 20);
		groundPlane.translate(0, 0, 0);
		groundPlane.setCullMode(CULL_MODE.NEVER);
		groundPlane.updateLocalBound();
		groundPlane.setShowBound(true);
		addGameWorldObject(groundPlane);*/

	}

	private void initHUD() {
		// TODO Auto-generated method stub		
		timeString = new HUDString("Time = " + time);
		timeString.setLocation(0,0.05); // (0,0) [lower-left] to (1,1)
		addGameWorldObject(timeString);

	}

	protected void initGameObjects() {
		display = getDisplaySystem();
		/*		kitty = new Kitty();
		textureObj(kitty, "kitty.png");
		addGameWorldObject(kitty); 
		kitty.updateLocalBound();
		Matrix3D k1M = kitty.getLocalTranslation(); 
		kitty.translate(5f,1f,0f); 
		kitty.setLocalTranslation(k1M); */



		pwrUp = new PowerUps();
		pwrUp.translate(20f, 3f, 20f);
		addGameWorldObject(pwrUp);
		pwrUpIsGone = false;
		eventMgr.addListener(pwrUp, CrashEvent.class);
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

	protected void initPhysicsSystem(){
		String engine = "sage.physics.JBullet.JBulletPhysicsEngine";
		physicsEngine = PhysicsEngineFactory.createPhysicsEngine(engine);
		physicsEngine.initSystem();
		float[] gravity = {0, -100f, 0};
		physicsEngine.setGravity(gravity);

		/*		float up[] = {0,1, 0};  // {0,1,0} is flat
		groundPlaneP = physicsEngine.addStaticPlaneObject(physicsEngine.nextUID(),
				groundPlane.getWorldTransform().getValues(), up, 0.0f);
		groundPlaneP.setBounciness(1.0f);
		groundPlane.setPhysicsObject(groundPlaneP);*/
	}

	@Override
	public void update(float elapsedTimeMS){
		//script
		long modTime = scriptFile.lastModified();
		if (modTime > fileLastModifiedTime){ 
			fileLastModifiedTime = modTime;
			this.runScript();
			removeGameWorldObject(rootNode);
			rootNode = (SceneNode) engine.get("rootNode");
			addGameWorldObject(rootNode);
		}
		if (thisClient != null){
			thisClient.processPackets(); 
			thisClient.sendMoveMessage(getPlayerPosition());
			thisClient.sendGhostRotMessage(player.getRot());
		}
		if (mainClient && thisClient != null) {
			thisClient.sendSyncKittyMessage(kitty.getLocation());
			thisClient.sendKittyRotMessage(kitty.getRot());
			//thisClient.sendTime();
		}
		//AI
		if(mainClient){
			if(kittyExists){
				kitty.kittyMove();
				if(ghost != null){
					Point2D.Double kittyLoc = new Point2D.Double(kitty.getLocalTranslation().elementAt(0, 3),kitty.getLocalTranslation().elementAt(2, 3));
					Point2D.Double chickenLoc = new Point2D.Double(player.getLocalTranslation().elementAt(0, 3),player.getLocalTranslation().elementAt(2, 3));
					Point2D.Double ghostLoc = new Point2D.Double(ghost.getLocalTranslation().elementAt(0, 3),ghost.getLocalTranslation().elementAt(2, 3));;
					float distToChicken = (float) kittyLoc.distance(chickenLoc);
					float distToGhost = (float) kittyLoc.distance(ghostLoc);
					if(distToChicken < distToGhost){
						kitty.kittyFollow(player);
					}// CHASE PRIORITY
					if(distToChicken > distToGhost){
						kitty.kittyFollow(ghost);
					}
				}
				if(ghost == null)
					kitty.kittyFollow(player);
				kitty.updateAnimation(elapsedTimeMS);
			}
		}
		if(gKitty!=null){
			gKitty.kittyRun();
			gKitty.updateAnimation(elapsedTimeMS);
		}
		//END AI

		//PHYSICS
		Matrix3D mat;
		Vector3D translateVec;
		physicsEngine.update(elapsedTimeMS);
		for(SceneNode s : getGameWorld()){
			if(s.getPhysicsObject() != null){
				mat = new Matrix3D(s.getPhysicsObject().getTransform());
				translateVec = mat.getCol(3);
				s.getLocalTranslation().setCol(3, translateVec);

			}
		}
		//END PHYSICS
		//System.out.println(kitty.getLocalTranslation());

		//TIMER
		time += elapsedTimeMS;
		DecimalFormat df = new DecimalFormat("0.0");
		timeString.setText("Time = " + df.format(time/1000));
		//END TIMER

		//SOUND
		chickenNoise1.setLocation(new Point3D(player.getWorldTransform().getCol(3)));
		setEarParameters();
		//END SOUND


		//SKYBOX
		Point3D camLoc = camera.getLocation();
		Matrix3D camTranslation = new Matrix3D();
		camTranslation.translate(camLoc.getX(), camLoc.getY(), camLoc.getZ());
		skyBox.setLocalTranslation(camTranslation);
		//END SKYBOX

		//EATING CHICKEN
		/*if(kitty!=null && player!=null)
			if(kitty.getWorldBound().intersects(player.getWorldBound()))
				this.deadChicken();*/
		//END EATING CHICKEN
		//POWER UPS
		if (pwrUp.getWorldBound().intersects(player.getWorldBound())){

			this.usePowerUp();
			/*CrashEvent newCrash = new CrashEvent();
			eventMgr.triggerEvent(newCrash);
			isPowerUpOn = true;
			timeStamp = time;
			this.removeGameWorldObject(pwrUp);
			powerUpRespawnTimer = time;
			pwrUpIsGone = true;*/

		}
		if(powerUpRespawnTimer + 20000 < time && pwrUpIsGone == true){
			this.addGameWorldObject(pwrUp);
			pwrUpIsGone = false;
			//TODO translate pwrUp randomly away from chickens
		}
		//END POWER UPS
		if(isPowerUpOn == true){

			canJump = false;
			if(timeStamp + 2000 < time){
				canJump = true;
				isPowerUpOn = false;

			}
		}

		cc.update(elapsedTimeMS);
		super.update(elapsedTimeMS);

	}

	public void usePowerUp() {
		CrashEvent newCrash = new CrashEvent();
		eventMgr.triggerEvent(newCrash);
		isPowerUpOn = true;
		timeStamp = time;
		this.removeGameWorldObject(pwrUp);
		powerUpRespawnTimer = time;
		pwrUpIsGone = true;

	}

	public void deadChicken(){
		CrashEvent newCrash = new CrashEvent();
		eventMgr.triggerEvent(newCrash);
		this.removeGameWorldObject(player);
	}
	@Override
	protected void render() { 
		renderer.setCamera(camera); 
		super.render(); 
	}

	private IDisplaySystem createDisplaySystem() { 
		IDisplaySystem display = new MyDisplaySystem(800, 600, 24, 20, false, "sage.renderer.jogl.JOGLRenderer"); 
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

	@Override
	protected void shutdown() { 
		display.close(); 
		super.shutdown();
		if(thisClient != null){ 
			thisClient.sendByeMessage();

			try{ 
				thisClient.shutdown();  // shutdown() is inherited
			}
			catch (IOException e) { 
				e.printStackTrace(); 
			}
		} 
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
		terrain = createTerBlock(myHeightMap);
		// create texture and texture state to color the terrain
		TextureState grassState;
		Texture grassTexture = TextureManager.loadTexture2D("perfect-cloud.jpg");
		grassTexture.setApplyMode(sage.texture.Texture.ApplyMode.Replace);
		grassState = (TextureState)
				display.getRenderer().createRenderState(RenderStateType.Texture);
		grassState.setTexture(grassTexture,0);
		grassState.setEnabled(true);
		// apply the texture to the terrain
		terrain.setRenderState(grassState);
		addGameWorldObject(terrain);
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

	public void createGhostAvatar(UUID ghostID, Point3D ghostPosition){
		ghost = new GhostAvatar(ghostID, ghostPosition);
		textureObj(ghost, "chicken.png");
		addGameWorldObject(ghost);

		/*float mass = 1.0f;

		ghostP = physicsEngine.addSphereObject(physicsEngine.nextUID(), 
				mass, ghost.getWorldTransform().getValues(), 2.3f);
		ghost.setPhysicsObject(ghostP);*/
		ghostExists = true;

	}

	public void moveGhostAvatar(Point3D ghostPosition){
		/*double[] m = ghostP.getTransform();
		m[3] = ghostPosition.getX();
		m[7] = ghostPosition.getY();
		m[11] = ghostPosition.getZ();
		ghostP.setTransform(m);*/
		ghost.move(ghostPosition);
	}

	public boolean doesGhostExist(){
		return ghostExists;
	}

	public boolean canChickenJump(){
		return canJump;
	}
	public void setIsConnected(boolean b) {
		connected = b;
		// TODO Auto-generated method stub
	}
	public boolean isConnected() {
		return connected;
	}
	public Point3D getPlayerPosition() {
		// TODO Auto-generated method stub
		return getPlayer().getLocation();
	}
	public void addGameWorldObject(SceneNode s) {
		super.addGameWorldObject(s);
	}

	public boolean removeGameWorldObject(SceneNode s) {
		return super.removeGameWorldObject(s);
	}
	public void setMainClient(boolean b) {
		mainClient = b;
	}
	public void moveKitty(Point3D p) {
		gKitty.move(p);

	}
	public void rotateKitty(double[] rot) {
		gKitty.rotateCharacter(rot);

	}
	public void rotateGhost(double[] rot) {
		if(ghost!=null)
			ghost.rotateCharacter(rot);

	}
	public void addKitty(){

		kitty = new Kitty();
		textureObj(kitty, "kitty.png");
		addGameWorldObject(kitty); 
		kitty.updateLocalBound();
		Matrix3D k1M = kitty.getLocalTranslation(); 
		kitty.translate(45f,1f,0f); 
		kitty.setLocalTranslation(k1M); 
		kittyExists = true;

		kittyP = physicsEngine.addSphereObject(physicsEngine.nextUID(), 1.0f, kitty.getWorldTransform().getValues(), 1f);
		kitty.setPhysicsObject(kittyP);
		kittyP.setDamping(.9f, .9f);

		catNoise1.setLocation(new Point3D(kitty.getWorldTransform().getCol(3)));
	}
	public void addGhostKitty(){
		gKitty = new GhostKitty();
		textureObj(gKitty,"kitty.png");
		addGameWorldObject(gKitty);
		gKitty.translate(45f,1f,0f); 
		catNoise1.setLocation(new Point3D(gKitty.getWorldTransform().getCol(3)));
		//gKitty.startAnimation("Run");
	}
}