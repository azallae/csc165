package game;

import java.util.UUID;

import graphicslib3D.Point3D;

public class GhostAvatar extends Chicken{

	public GhostAvatar(UUID ghostID, Point3D ghostPosition) {
		// TODO Auto-generated constructor stub
		super();
		id = ghostID;
		setLocation(ghostPosition);
	}
	public void move(Point3D ghostPosition){
		setLocation(ghostPosition);
	}

}
