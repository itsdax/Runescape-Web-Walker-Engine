package dax.api_lib.models;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum PlayerPreferences {
    SPIRIT_TREE_ETCETERIA,
    SPIRIT_TREE_PORT_SARIM,
    SPIRIT_TREE_BRIMHAVEN,
    SPIRI_TREE_HOSIDIUS,
    SPIRIT_TREE_FARMING_GUILD,
    SPIRIT_TREE_POH,

    MAGIC_MUSHTREE_HOUSE_ON_THE_HILL,
    MAGIC_MUSHTREE_MUSHROOM_MEADOW,
    MAGIC_MUSHTREE_STICKY_SWAMP,
    MAGIC_MUSHTREE_VERDANT_VALLEY
    ;

    @Getter @Setter
    public boolean enabled;
    PlayerPreferences(){
        setEnabled(false);
    }

    public static List<Boolean> getPlayerPreferences(){
        return Arrays.stream(values()).map(PlayerPreferences::isEnabled).collect(Collectors.toList());
    }
}
