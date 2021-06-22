package scripts.dax_api.api_lib.models;

import com.allatori.annotations.DoNotRename;

import java.util.List;

@DoNotRename
public class BulkPathRequest {
	@DoNotRename
	private PlayerDetails player;
	@DoNotRename
	private List<PathRequestPair> requests;

	public BulkPathRequest(PlayerDetails player, List<PathRequestPair> requests) {
		this.player = player;
		this.requests = requests;
	}

	public PlayerDetails getPlayer() {
		return player;
	}

	public List<PathRequestPair> getRequests() {
		return requests;
	}
}