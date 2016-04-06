package game;

import java.util.UUID;

import graphicslib3D.Point3D;

public class GhostAvatar extends MyCharacter{

	public GhostAvatar(UUID ghostID, Point3D ghostPosition) {
		// TODO Auto-generated constructor stub
		id = ghostID;
		setLocation(ghostPosition);
	}

}
