package dax.api_lib.models;

import com.allatori.annotations.DoNotRename;

import java.util.List;

@DoNotRename
public class BulkPathRequest {
	@DoNotRename
	private PlayerDetails player;
	@DoNotRename
	private List<PathRequestPair> requests;


	// Seed to use for behavior randomization on paths. Can be anything as long as it's consistent per account.
	// Only the first 20 characters are used. Alphanumeric only. Do NOT use a new random string each time.
	// This parameter prevents accounts from following similar paths at scale.
	// Note that this feature is still experimental, and may produce suboptimal pathing.
	// Leaving empty or null will disable profiling.
	@DoNotRename
	private String seed;

	// Actual player location that this bulk path request is being generated for.
    @DoNotRename
    private Point3D seedLocation;

	public BulkPathRequest(PlayerDetails player, List<PathRequestPair> requests, String seed, Point3D seedLocation) {
		this.player = player;
		this.requests = requests;
		this.seed = seed;
		this.seedLocation = seedLocation;
	}

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

	public String getSeed() {
		return seed;
	}

	public Point3D getSeedLocation() {
		return seedLocation;
	}
}