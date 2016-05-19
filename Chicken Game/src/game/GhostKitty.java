package game;



import graphicslib3D.Point3D;

public class GhostKitty extends Kitty {
	public GhostKitty() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public void move(Point3D ghostPosition){
		setLocation(ghostPosition);
	}

}
