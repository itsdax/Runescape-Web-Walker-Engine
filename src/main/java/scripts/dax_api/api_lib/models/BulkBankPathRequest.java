package scripts.dax_api.api_lib.models;


import com.allatori.annotations.DoNotRename;

import java.util.List;


@DoNotRename
public class BulkBankPathRequest {
    @DoNotRename
    private PlayerDetails player;
    @DoNotRename
    private List<BankPathRequestPair> requests;

    public BulkBankPathRequest(PlayerDetails player, List<BankPathRequestPair> requests) {
        this.player = player;
        this.requests = requests;
    }

    public PlayerDetails getPlayer() {
        return player;
    }

    public List<BankPathRequestPair> getRequests() {
        return requests;
    }
}
