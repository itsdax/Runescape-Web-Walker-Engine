package scripts.webwalker_logic.teleport_logic;

import org.tribot.api2007.types.RSTile;


public enum TeleportLocation {

    AL_KHARID(3292, 3168, 0),
    VARROCK_CENTER (3212, 3424, 0),
    LUMBRIDGE_CASTLE (3221, 3218, 0),
    DUEL_ARENA (3316, 3235, 0),
    DRAYNOR_VILLAGE (3104, 3249, 0),
    FALADOR_CENTER (2965, 3379, 0),
    CAMELOT (2757, 3480, 0),
    RANGED_GUILD (2654, 3440, 0),
    ARDOUGNE_MARKET_PLACE (2631, 3300, 0),
    CASTLE_WARS (2440, 3089, 0),
    BURTHORPE_GAMES_ROOM (2899, 3552, 0),
    CLAN_WARS (3389, 3158, 0),
    KARAMJA_BANANA_PLANTATION (2919, 3174, 0),
    WINTERTODT_CAMP (1624, 3940, 0),
    CORPOREAL_BEAST (2966, 4383, 2),
    BARBARIAN_OUTPOST (2518, 3572, 0),
    WARRIORS_GUILD (2882, 3548, 0),
    CHAMPIONS_GUILD (3191, 3366, 0),
    MONASTRY_EDGE (3053, 3489, 0),
    EDGEVILLE (3087, 3497, 0),
    ECTO (3660, 3524, 0),

    FISHING_GUILD (2610, 3391, 0),
    MOTHERLOAD_MINE (3737, 5690, 0),
    CRAFTING_GUILD (2934, 3293, 0),
    COOKING_GUILD (3143, 3443, 0),
    WOOD_CUTTING_GUILD (1660, 3504, 0),

    GRAND_EXCHANGE (3164, 3465, 0),
    FALADOR_PARK (2995, 3376, 0)

    ;

    private int x, y, z;
    TeleportLocation(int x, int y, int z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public RSTile getRSTile(){
        return new RSTile(x, y, z);
    }

}
