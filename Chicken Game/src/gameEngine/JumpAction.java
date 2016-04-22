package gameEngine;

import game.ChickenGame;
import game.Chicken;
import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;
import sage.input.action.AbstractInputAction;

public class JumpAction extends AbstractInputAction{
	private Chicken player;
	private ChickenGame game;
	public JumpAction(Chicken p2, ChickenGame g){
		player = p2;
		game = g;
	}
	public void performAction(float time, net.java.games.input.Event e){
		game.setRunning(true);
	} 
}