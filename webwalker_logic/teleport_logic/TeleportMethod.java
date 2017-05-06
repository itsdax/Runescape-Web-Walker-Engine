package scripts.webwalker_logic.teleport_logic;

import org.tribot.api.General;
import org.tribot.api.types.generic.Filter;
import org.tribot.api2007.Equipment;
import org.tribot.api2007.Game;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.Player;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSTile;
import scripts.webwalker_logic.local.walker_engine.interaction_handling.NPCInteraction;
import scripts.webwalker_logic.local.walker_engine.WaitFor;
import scripts.webwalker_logic.shared.helpers.RSItemHelper;
import scripts.webwalker_logic.shared.helpers.magic.Spell;

import java.util.ArrayList;
import java.util.Arrays;

import static scripts.webwalker_logic.teleport_logic.TeleportLocation.*;

public enum TeleportMethod implements Validatable {

    VARROCK_TELEPORT (VARROCK_CENTER),
    LUMBRIDGE_TELEPORT  (LUMBRIDGE_CASTLE),
    FALADOR_TELEPORT  (FALADOR_CENTER),
    CAMELOT_TELEPORT  (CAMELOT),
    ARDOUGNE_TELPORT  (ARDOUGNE_MARKET_PLACE),
    GLORY (EDGEVILLE, DRAYNOR_VILLAGE, KARAMJA_BANANA_PLANTATION, AL_KHARID),
    COMBAT_BRACE  (CHAMPIONS_GUILD, WARRIORS_GUILD, RANGED_GUILD, MONASTRY_EDGE),
    GAMES_NECKLACE  (CORPOREAL_BEAST, BURTHORPE_GAMES_ROOM, WINTERTODT_CAMP, BARBARIAN_OUTPOST),
    DUELING_RING (DUEL_ARENA, CASTLE_WARS, CLAN_WARS),
    ECTOPHIAL (ECTO),
    SKILLS_NECKLACE (FISHING_GUILD, MOTHERLOAD_MINE, CRAFTING_GUILD, COOKING_GUILD, WOOD_CUTTING_GUILD),
    RING_OF_WEALTH (GRAND_EXCHANGE, FALADOR_PARK)
    ;

    private TeleportLocation[] destinations;

    TeleportMethod(TeleportLocation... destinations){
        this.destinations = destinations;
    }

    private static final Filter<RSItem>
            GLORY_FILTER = Filters.Items.nameContains("Glory").combine(Filters.Items.nameContains("("), true),
            GAMES_FILTER = Filters.Items.nameContains("Games").combine(Filters.Items.nameContains("("), true),
            DUELING_FILTER = Filters.Items.nameContains("dueling").combine(Filters.Items.nameContains("("), true),
            COMBAT_FILTER = Filters.Items.nameContains("Combat").combine(Filters.Items.nameContains("("), true),
            SKILLS_FILTER = Filters.Items.nameContains("Skills necklace").combine(Filters.Items.nameContains("("), true),
            WEALTH_FILTER = Filters.Items.nameContains("Ring of wealth").combine(Filters.Items.nameContains("("), true)
    ;

    public TeleportLocation[] getDestinations() {
        return destinations;
    }

    @Override
    public boolean canUse() {
        switch (this){
            case ECTOPHIAL: return Inventory.find(Filters.Items.nameContains("Ectophial")).length > 0;
            case VARROCK_TELEPORT: return Spell.VARROCK_TELEPORT.canUse() || Inventory.getCount("Varrock teleport") > 0;
            case LUMBRIDGE_TELEPORT: return Spell.LUMBRIDGE_TELEPORT.canUse() || Inventory.getCount("Lumbridge teleport") > 0;
            case FALADOR_TELEPORT: return Spell.FALADOR_TELEPORT.canUse() || Inventory.getCount("Falador teleport") > 0;
            case CAMELOT_TELEPORT: return Spell.CAMELOT_TELEPORT.canUse() || Inventory.getCount("Camelot teleport") > 0;
            case ARDOUGNE_TELPORT: return Game.getSetting(165) >= 30 && (Spell.ARDOUGNE_TELEPORT.canUse() || Inventory.getCount("Ardougne teleport") > 0);
            case GLORY: return Inventory.find(GLORY_FILTER).length > 0 || Equipment.find(GLORY_FILTER).length > 0;
            case COMBAT_BRACE: return Inventory.find(COMBAT_FILTER).length > 0 || Equipment.find(COMBAT_FILTER).length > 0;
            case GAMES_NECKLACE: return Inventory.find(GAMES_FILTER).length > 0 || Equipment.find(GAMES_FILTER).length > 0;
            case DUELING_RING: return Inventory.find(DUELING_FILTER).length > 0 || Equipment.find(DUELING_FILTER).length > 0;
        }
        return false;
    }

    public boolean use(TeleportLocation teleportLocation){
        switch (teleportLocation) {

            case VARROCK_CENTER: return RSItemHelper.click("Varrock t.*", "Break") || Spell.VARROCK_TELEPORT.cast();
            case LUMBRIDGE_CASTLE: return RSItemHelper.click("Lumbridge t.*", "Break") || Spell.LUMBRIDGE_TELEPORT.cast();
            case FALADOR_CENTER: return RSItemHelper.click("Falador t.*", "Break") || Spell.FALADOR_TELEPORT.cast();
            case CAMELOT: return RSItemHelper.click("Camelot t.*", "Break") || Spell.CAMELOT_TELEPORT.cast();
            case ARDOUGNE_MARKET_PLACE: return RSItemHelper.click("Ardougne t.*", "Break") || Spell.ARDOUGNE_TELEPORT.cast();

            case DUEL_ARENA: return teleportWithItem(DUELING_FILTER, "(Duel.*|Al K.*)");
            case CASTLE_WARS: return teleportWithItem(DUELING_FILTER, "Castle War.*");
            case CLAN_WARS: return teleportWithItem(DUELING_FILTER, "Clan Wars.*");

            case AL_KHARID: return teleportWithItem(GLORY_FILTER, "Al .*");
            case EDGEVILLE: return teleportWithItem(GLORY_FILTER, "Edge.*");
            case KARAMJA_BANANA_PLANTATION: return teleportWithItem(GLORY_FILTER, "Karamja.*");
            case DRAYNOR_VILLAGE: return teleportWithItem(GLORY_FILTER, "Draynor.*");

            case BURTHORPE_GAMES_ROOM: return teleportWithItem(GAMES_FILTER, "Burthorpe.*");
            case WINTERTODT_CAMP: return teleportWithItem(GAMES_FILTER, "Winter.*");
            case CORPOREAL_BEAST: return teleportWithItem(GAMES_FILTER, "Corp.*");
            case BARBARIAN_OUTPOST: return teleportWithItem(GAMES_FILTER, "Barb.*");

            case WARRIORS_GUILD: return teleportWithItem(COMBAT_FILTER, "Warrior.*");
            case CHAMPIONS_GUILD: return teleportWithItem(COMBAT_FILTER, "Champ.*");
            case MONASTRY_EDGE: return teleportWithItem(COMBAT_FILTER, "Mona.*");
            case RANGED_GUILD: return teleportWithItem(COMBAT_FILTER, "Rang.*");

            case ECTO: return RSItemHelper.click(Filters.Items.nameContains("Ectophial"), "Empty");

            case FISHING_GUILD: return teleportWithItem(SKILLS_FILTER, "Fishing.*");
            case MOTHERLOAD_MINE: return teleportWithItem(SKILLS_FILTER, "Mother.*");
            case CRAFTING_GUILD: return teleportWithItem(SKILLS_FILTER, "Crafting.*");
            case COOKING_GUILD: return teleportWithItem(SKILLS_FILTER, "Cooking.*");
            case WOOD_CUTTING_GUILD: return teleportWithItem(SKILLS_FILTER, "Woodcutting.*");

            case GRAND_EXCHANGE: return teleportWithItem(WEALTH_FILTER, "Grand.*");
            case FALADOR_PARK: return teleportWithItem(WEALTH_FILTER, "Falad.*");
        }
        return false;
    }


    private static boolean itemAction(String name, String... actions){
        RSItem[] items = Inventory.find(name);
        if (items.length == 0){
            return false;
        }
        return items[0].click(actions);
    }

    private static boolean teleportWithItem(Filter<RSItem> itemFilter, String regex){
        ArrayList<RSItem> items = new ArrayList<>();
        items.addAll(Arrays.asList(Inventory.find(itemFilter)));
        items.addAll(Arrays.asList(Equipment.find(itemFilter)));

        if (items.size() == 0){
            return false;
        }

        RSItem teleportItem = items.get(0);
        if (!RSItemHelper.clickMatch(teleportItem, "(Rub|" + regex + ")")){
            return false;
        }

        RSTile startingPosition = Player.getPosition();
        return WaitFor.condition(General.random(3800, 4600), () -> {
            NPCInteraction.handleConversationRegex(regex);
            if (startingPosition.distanceTo(Player.getPosition()) > 5){
                return WaitFor.Return.SUCCESS;
            }
            return WaitFor.Return.IGNORE;
        }) == WaitFor.Return.SUCCESS;
    }

}
