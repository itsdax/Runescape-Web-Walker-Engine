package scripts.dax_api.teleport_logic;

import org.tribot.api.General;
import org.tribot.api.types.generic.Filter;
import org.tribot.api2007.*;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSTile;
import scripts.dax_api.shared.helpers.RSItemHelper;
import scripts.dax_api.shared.helpers.magic.Spell;
import scripts.dax_api.walker_engine.WaitFor;
import scripts.dax_api.walker_engine.interaction_handling.NPCInteraction;

import java.util.ArrayList;
import java.util.Arrays;

import static scripts.dax_api.teleport_logic.TeleportLocation.*;

public enum TeleportMethod implements Validatable {

    VARROCK_TELEPORT(TeleportConstants.LEVEL_20_WILDERNESS_LIMIT, VARROCK_CENTER),
    LUMBRIDGE_TELEPORT(TeleportConstants.LEVEL_20_WILDERNESS_LIMIT, LUMBRIDGE_CASTLE),
    FALADOR_TELEPORT(TeleportConstants.LEVEL_20_WILDERNESS_LIMIT, FALADOR_CENTER),
    CAMELOT_TELEPORT(TeleportConstants.LEVEL_20_WILDERNESS_LIMIT, CAMELOT),
    ARDOUGNE_TELPORT(TeleportConstants.LEVEL_20_WILDERNESS_LIMIT, ARDOUGNE_MARKET_PLACE),
    GLORY(TeleportConstants.LEVEL_30_WILDERNESS_LIMIT, EDGEVILLE, DRAYNOR_VILLAGE, KARAMJA_BANANA_PLANTATION, AL_KHARID),
    COMBAT_BRACE(TeleportConstants.LEVEL_30_WILDERNESS_LIMIT, CHAMPIONS_GUILD, WARRIORS_GUILD, RANGED_GUILD, MONASTRY_EDGE),
    GAMES_NECKLACE(TeleportConstants.LEVEL_20_WILDERNESS_LIMIT, CORPOREAL_BEAST, BURTHORPE_GAMES_ROOM, WINTERTODT_CAMP, BARBARIAN_OUTPOST),
    DUELING_RING(TeleportConstants.LEVEL_20_WILDERNESS_LIMIT, DUEL_ARENA, CASTLE_WARS, CLAN_WARS),
    ECTOPHIAL(TeleportConstants.LEVEL_20_WILDERNESS_LIMIT, ECTO),
    SKILLS_NECKLACE(TeleportConstants.LEVEL_30_WILDERNESS_LIMIT, FISHING_GUILD, MOTHERLOAD_MINE, CRAFTING_GUILD, COOKING_GUILD, WOOD_CUTTING_GUILD),
    RING_OF_WEALTH(TeleportConstants.LEVEL_30_WILDERNESS_LIMIT, GRAND_EXCHANGE, FALADOR_PARK),
    BURNING_AMULET(TeleportConstants.LEVEL_20_WILDERNESS_LIMIT, CHAOS_TEMPLE, BANDIT_CAMP, LAVA_MAZE);

    private TeleportLocation[] destinations;
    private TeleportLimit teleportLimit;

    TeleportMethod(TeleportLimit teleportLimit, TeleportLocation... destinations) {
        this.teleportLimit = teleportLimit;
        this.destinations = destinations;
    }

    private static final Filter<RSItem>
            GLORY_FILTER = Filters.Items.nameContains("Glory").combine(Filters.Items.nameContains("("), true).combine(notNotedFilter(), false),
            GAMES_FILTER = Filters.Items.nameContains("Games").combine(Filters.Items.nameContains("("), true).combine(notNotedFilter(), false),
            DUELING_FILTER = Filters.Items.nameContains("dueling").combine(Filters.Items.nameContains("("), true).combine(notNotedFilter(), false),
            COMBAT_FILTER = Filters.Items.nameContains("Combat b").combine(Filters.Items.nameContains("("), true).combine(notNotedFilter(), false),
            SKILLS_FILTER = Filters.Items.nameContains("Skills necklace").combine(Filters.Items.nameContains("("), true).combine(notNotedFilter(), false),
            WEALTH_FILTER = Filters.Items.nameContains("Ring of wealth").combine(Filters.Items.nameContains("("), true).combine(notNotedFilter(), false),
            BURNING_FILTER = Filters.Items.nameContains("Burning amulet").combine(Filters.Items.nameContains("("), true);

    public TeleportLocation[] getDestinations() {
        return destinations;
    }

    public TeleportLimit getLimit() {
        return teleportLimit;
    }

    @Override
    public boolean canUse() {
        if (!this.getLimit().canCast())
            return false;
        switch (this) {
            case ECTOPHIAL:
                return Inventory.find(Filters.Items.nameContains("Ectophial")).length > 0;
            case VARROCK_TELEPORT:
                return Spell.VARROCK_TELEPORT.canUse() || Inventory.getCount("Varrock teleport") > 0;
            case LUMBRIDGE_TELEPORT:
                return Spell.LUMBRIDGE_TELEPORT.canUse() || Inventory.getCount("Lumbridge teleport") > 0;
            case FALADOR_TELEPORT:
                return Spell.FALADOR_TELEPORT.canUse() || Inventory.getCount("Falador teleport") > 0;
            case CAMELOT_TELEPORT:
                return Spell.CAMELOT_TELEPORT.canUse() || Inventory.getCount("Camelot teleport") > 0;
            case ARDOUGNE_TELPORT:
                return Game.getSetting(165) >= 30 && (Spell.ARDOUGNE_TELEPORT.canUse() || Inventory.getCount("Ardougne teleport") > 0);
            case GLORY:
                return Inventory.find(GLORY_FILTER).length > 0 || Equipment.find(GLORY_FILTER).length > 0;
            case COMBAT_BRACE:
                return Inventory.find(COMBAT_FILTER).length > 0 || Equipment.find(COMBAT_FILTER).length > 0;
            case GAMES_NECKLACE:
                return Inventory.find(GAMES_FILTER).length > 0 || Equipment.find(GAMES_FILTER).length > 0;
            case DUELING_RING:
                return Inventory.find(DUELING_FILTER).length > 0 || Equipment.find(DUELING_FILTER).length > 0;
            case SKILLS_NECKLACE:
                return Inventory.find(SKILLS_FILTER).length > 0 || Equipment.find(SKILLS_FILTER).length > 0;
            case RING_OF_WEALTH:
                return Inventory.find(WEALTH_FILTER).length > 0 || Equipment.find(WEALTH_FILTER).length > 0;
            case BURNING_AMULET:
                return Inventory.find(BURNING_FILTER).length > 0 || Equipment.find(BURNING_FILTER).length > 0;
        }
        return false;
    }

    public boolean use(TeleportLocation teleportLocation) {
        if (Banking.isBankScreenOpen()) {
            Banking.close();
        }
        switch (teleportLocation) {

            case VARROCK_CENTER:
                return RSItemHelper.click("Varrock t.*", "Break") || Spell.VARROCK_TELEPORT.cast();
            case LUMBRIDGE_CASTLE:
                return RSItemHelper.click("Lumbridge t.*", "Break") || Spell.LUMBRIDGE_TELEPORT.cast();
            case FALADOR_CENTER:
                return RSItemHelper.click("Falador t.*", "Break") || Spell.FALADOR_TELEPORT.cast();
            case CAMELOT:
                return RSItemHelper.click("Camelot t.*", "Break") || Spell.CAMELOT_TELEPORT.cast();
            case ARDOUGNE_MARKET_PLACE:
                return RSItemHelper.click("Ardougne t.*", "Break") || Spell.ARDOUGNE_TELEPORT.cast();

            case DUEL_ARENA:
                return teleportWithItem(DUELING_FILTER, "(Duel.*|Al K.*)");
            case CASTLE_WARS:
                return teleportWithItem(DUELING_FILTER, "Castle War.*");
            case CLAN_WARS:
                return teleportWithItem(DUELING_FILTER, "Clan Wars.*");

            case AL_KHARID:
                return teleportWithItem(GLORY_FILTER, "Al .*");
            case EDGEVILLE:
                return teleportWithItem(GLORY_FILTER, "Edge.*");
            case KARAMJA_BANANA_PLANTATION:
                return teleportWithItem(GLORY_FILTER, "Karamja.*");
            case DRAYNOR_VILLAGE:
                return teleportWithItem(GLORY_FILTER, "Draynor.*");

            case BURTHORPE_GAMES_ROOM:
                return teleportWithItem(GAMES_FILTER, "Burthorpe.*");
            case WINTERTODT_CAMP:
                return teleportWithItem(GAMES_FILTER, "Winter.*");
            case CORPOREAL_BEAST:
                return teleportWithItem(GAMES_FILTER, "Corp.*");
            case BARBARIAN_OUTPOST:
                return teleportWithItem(GAMES_FILTER, "Barb.*");

            case WARRIORS_GUILD:
                return teleportWithItem(COMBAT_FILTER, "Warrior.*");
            case CHAMPIONS_GUILD:
                return teleportWithItem(COMBAT_FILTER, "Champ.*");
            case MONASTRY_EDGE:
                return teleportWithItem(COMBAT_FILTER, "Mona.*");
            case RANGED_GUILD:
                return teleportWithItem(COMBAT_FILTER, "Rang.*");

            case ECTO:
                return RSItemHelper.click(Filters.Items.nameContains("Ectophial"), "Empty");

            case FISHING_GUILD:
                return teleportWithItem(SKILLS_FILTER, "Fishing.*");
            case MOTHERLOAD_MINE:
                return teleportWithItem(SKILLS_FILTER, "Mother.*");
            case CRAFTING_GUILD:
                return teleportWithItem(SKILLS_FILTER, "Crafting.*");
            case COOKING_GUILD:
                return teleportWithItem(SKILLS_FILTER, "Cooking.*");
            case WOOD_CUTTING_GUILD:
                return teleportWithItem(SKILLS_FILTER, "Woodcutting.*");

            case GRAND_EXCHANGE:
                return teleportWithItem(WEALTH_FILTER, "Grand.*");
            case FALADOR_PARK:
                return teleportWithItem(WEALTH_FILTER, "Falad.*");

            case CHAOS_TEMPLE:
                return teleportWithItem(BURNING_FILTER, "(Chaos.*|Okay, teleport to level.*)");
            case BANDIT_CAMP:
                return teleportWithItem(BURNING_FILTER, "(Bandit.*|Okay, teleport to level.*)");
            case LAVA_MAZE:
                return teleportWithItem(BURNING_FILTER, "(Lava.*|Okay, teleport to level.*)");
        }
        return false;
    }

    private static boolean inMembersWorld() {
        return WorldHopper.isMembers(WorldHopper.getWorld());
    }

    private static Filter<RSItem> notNotedFilter() {
        return new Filter<RSItem>() {
            @Override
            public boolean accept(RSItem rsItem) {
                return rsItem.getDefinition() != null && !rsItem.getDefinition().isNoted();
            }
        };
    }

    private static boolean itemAction(String name, String... actions) {
        RSItem[] items = Inventory.find(name);
        if (items.length == 0) {
            return false;
        }
        return items[0].click(actions);
    }

    private static boolean teleportWithItem(Filter<RSItem> itemFilter, String regex) {
        ArrayList<RSItem> items = new ArrayList<>();
        items.addAll(Arrays.asList(Inventory.find(itemFilter)));
        items.addAll(Arrays.asList(Equipment.find(itemFilter)));

        if (items.size() == 0) {
            return false;
        }

        RSItem teleportItem = items.get(0);
        if (!RSItemHelper.clickMatch(teleportItem, "(Rub|" + regex + ")")) {
            return false;
        }

        RSTile startingPosition = Player.getPosition();
        return WaitFor.condition(General.random(3800, 4600), () -> {
            NPCInteraction.handleConversationRegex(regex);
            if (startingPosition.distanceTo(Player.getPosition()) > 5) {
                return WaitFor.Return.SUCCESS;
            }
            return WaitFor.Return.IGNORE;
        }) == WaitFor.Return.SUCCESS;
    }

}
