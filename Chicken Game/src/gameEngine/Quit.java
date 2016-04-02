package gameEngine;


import sage.input.action.AbstractInputAction;
import sage.app.AbstractGame;
import net.java.games.input.Event;

public class Quit extends AbstractInputAction{
	private AbstractGame game;

	public Quit(AbstractGame g) { 
		this.game = g; 
	}

	public void performAction(float time, Event event){ 
		game.setGameOver(true); 
	}
}