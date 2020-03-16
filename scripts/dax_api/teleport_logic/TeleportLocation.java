package scripts.dax_api.teleport_logic;

import org.tribot.api2007.types.RSTile;

import java.util.Arrays;


public enum TeleportLocation {

    AL_KHARID(3292, 3163, 0),
    VARROCK_CENTER (3212, 3424, 0),
    LUMBRIDGE_CASTLE (3221, 3218, 0),
    DUEL_ARENA (3316, 3235, 0),
    DRAYNOR_VILLAGE (3104, 3249, 0),
    FALADOR_CENTER (2965, 3379, 0),
    CAMELOT (2757, 3480, 0),
    RANGED_GUILD (2654, 3440, 0),
    ARDOUGNE_MARKET_PLACE (2661,3300,0),
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

    GRAND_EXCHANGE (3164, 3472, 0),
    FALADOR_PARK (2995, 3376, 0),

    CHAOS_TEMPLE (3236, 3635, 0),
    BANDIT_CAMP (3039, 3652, 0),
    LAVA_MAZE (3029, 3843, 0),

    DIGSITE_BARGE(3346,3445,0),

    OUTPOST(2432, 3348, 0),

    LLETYA(2330,3172,0),

    KOUREND(1639,3672,0),

    XERICS_INFERNO(1505,3809,0),
    XERICS_GLADE(1753, 3565, 0),
    XERICS_LOOKOUT(1575, 3531, 0),

    NARDAH(3419, 2916, 0),
    DIGSITE(3325, 3411, 0),
    FELDIP_HILLS(2540, 2924, 0),
    LUNAR_ISLE(2095, 3913, 0),
    MORTTON(3487, 3287, 0),
    PEST_CONTROL(2658, 2658, 0),
    PISCATORIS(2340, 3650, 0),
    TAI_BWO_WANNAI(2789,3065,0),
    ELF_CAMP(2193, 3258, 0),
    MOS_LE_HARMLESS(3700, 2996, 0),
    LUMBERYARD(3302, 3487, 0),
    ZULLANDRA(2195, 3055, 0),
    KEY_MASTER(1311, 1251, 0),
    REVENANT_CAVES(3130, 3832, 0),

    WEST_ARDOUGNE(2500,3290,0),

    RELLEKKA_POH(2668, 3631, 0),
    BRIMHAVEN_POH(2758, 3178, 0),
    RIMMINGTON_POH(2954,3224, 0),
    HOSIDIUS_POH(1744, 3517, 0),
    TAVERLY_POH(2894, 3465, 0),
    YANILLE_POH(2544, 3095, 0),
    POLLNIVNEACH_POH(3340, 3004, 0),

    MOUNT_KARUULM(1310, 3796, 0),
    KOUREND_WOODLAND(1558, 3458, 0),

    CRAFTING_GUILD_INTERIOR(2931, 3286, 0),

    CABBAGE_PATCH(3049, 3287, 0),

    LEGENDS_GUILD(2729, 3348, 0)

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

    public boolean canTeleportTo(){
        return Arrays.stream(TeleportMethod.values())
                .anyMatch(m -> Arrays.asList(m.getDestinations()).contains(this) && m.canUse());
    }

}
