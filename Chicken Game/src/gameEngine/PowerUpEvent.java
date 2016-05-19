package gameEngine;




	import sage.event.*;
	public class PowerUpEvent extends AbstractGameEvent
	{
		// programmer-defined parts go here
		private int whichCrash;
		public PowerUpEvent(int n) { whichCrash = n; }
		public int getWhichCrash() { return whichCrash; }
	}