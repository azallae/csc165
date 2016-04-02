package game;

import java.util.UUID;

public class GhostAvatar extends MyCharacter{

	public GhostAvatar(UUID ghostID, String[] ghostPosition) {
		// TODO Auto-generated constructor stub
		id = ghostID;
		setLocation(ghostPosition);
	}

}
