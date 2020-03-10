package scripts.dax_api.teleport_logic;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api2007.*;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSInterface;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSTile;
import scripts.dax_api.shared.helpers.InterfaceHelper;
import scripts.dax_api.shared.helpers.RSItemHelper;
import scripts.dax_api.shared.helpers.magic.Spell;
import scripts.dax_api.teleport_logic.teleport_utils.MasterScrollBook;
import scripts.dax_api.teleport_logic.teleport_utils.TeleportScrolls;
import scripts.dax_api.walker_engine.WaitFor;
import scripts.dax_api.walker_engine.interaction_handling.NPCInteraction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import static scripts.dax_api.teleport_logic.TeleportLocation.*;

public enum TeleportMethod implements Validatable {

    VARROCK_TELEPORT(TeleportConstants.LEVEL_20_WILDERNESS_LIMIT, VARROCK_CENTER),
    GRAND_EXCHANGE_TELEPORT(TeleportConstants.LEVEL_20_WILDERNESS_LIMIT, GRAND_EXCHANGE),
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
    BURNING_AMULET(TeleportConstants.LEVEL_20_WILDERNESS_LIMIT, CHAOS_TEMPLE, BANDIT_CAMP, LAVA_MAZE),
    DIGSITE_PENDANT(TeleportConstants.LEVEL_20_WILDERNESS_LIMIT,DIGSITE_BARGE),
    NECKLACE_OF_PASSAGE(TeleportConstants.LEVEL_20_WILDERNESS_LIMIT,OUTPOST),
    ELF_CRYSTAL(TeleportConstants.LEVEL_20_WILDERNESS_LIMIT,LLETYA),
    KOUREND_TELEPORT(TeleportConstants.LEVEL_20_WILDERNESS_LIMIT,KOUREND),
    XERICS_TALISMAN(TeleportConstants.LEVEL_20_WILDERNESS_LIMIT,XERICS_INFERNO),

    PISCATORIS_TELEPORT_SCROLL(TeleportConstants.LEVEL_20_WILDERNESS_LIMIT,PISCATORIS),
    NARDAH_TELEPORT_SCROLL(TeleportConstants.LEVEL_20_WILDERNESS_LIMIT,NARDAH),
    DIGSITE_TELEPORT_SCROLL(TeleportConstants.LEVEL_20_WILDERNESS_LIMIT,DIGSITE),
    FELDIP_HILLS_TELEPORT_SCROLL(TeleportConstants.LEVEL_20_WILDERNESS_LIMIT,FELDIP_HILLS),
    LUNAR_ISLE_TELEPORT_SCROLL(TeleportConstants.LEVEL_20_WILDERNESS_LIMIT,LUNAR_ISLE),
    MORTTON_TELEPORT_SCROLL(TeleportConstants.LEVEL_20_WILDERNESS_LIMIT,MORTTON),
    PEST_CONTROL_TELEPORT_SCROLL(TeleportConstants.LEVEL_20_WILDERNESS_LIMIT,PEST_CONTROL),
    TAI_BWO_WANNAI_TELEPORT_SCROLL(TeleportConstants.LEVEL_20_WILDERNESS_LIMIT,TAI_BWO_WANNAI),
    ELF_CAMP_TELEPORT_SCROLL(TeleportConstants.LEVEL_20_WILDERNESS_LIMIT,ELF_CAMP),
    MOS_LE_HARMLESS_TELEPORT_SCROLL(TeleportConstants.LEVEL_20_WILDERNESS_LIMIT,MOS_LE_HARMLESS),
    LUMBERYARD_TELEPORT_SCROLL(TeleportConstants.LEVEL_20_WILDERNESS_LIMIT,LUMBERYARD),
    ZULLANDRA_TELEPORT_SCROLL(TeleportConstants.LEVEL_20_WILDERNESS_LIMIT,ZULLANDRA),
    KEY_MASTER_TELEPORT_SCROLL(TeleportConstants.LEVEL_20_WILDERNESS_LIMIT,KEY_MASTER),
    REVENANT_CAVES_TELEPORT_SCROLL(TeleportConstants.LEVEL_20_WILDERNESS_LIMIT,REVENANT_CAVES),

    WEST_ARDOUGNE_TELEPORT(TeleportConstants.LEVEL_20_WILDERNESS_LIMIT,WEST_ARDOUGNE),

    RELLEKKA_TELEPORT(TeleportConstants.LEVEL_20_WILDERNESS_LIMIT,RELLEKKA_POH),

    RADAS_BLESSING(TeleportConstants.LEVEL_20_WILDERNESS_LIMIT,MOUNT_KARUULM,KOUREND_WOODLAND)
    ;

    private TeleportLocation[] destinations;
    private TeleportLimit teleportLimit;

    TeleportMethod(TeleportLimit teleportLimit, TeleportLocation... destinations) {
        this.teleportLimit = teleportLimit;
        this.destinations = destinations;
    }

    private static final Predicate<RSItem>
            GLORY_FILTER = Filters.Items.nameContains("Glory").and(Filters.Items.nameContains("(")).and(notNotedFilter()),
            GAMES_FILTER = Filters.Items.nameContains("Games").and(Filters.Items.nameContains("(")).and(notNotedFilter()),
            DUELING_FILTER = Filters.Items.nameContains("dueling").and(Filters.Items.nameContains("(")).and(notNotedFilter()),
            COMBAT_FILTER = Filters.Items.nameContains("Combat b").and(Filters.Items.nameContains("(")).and(notNotedFilter()),
            SKILLS_FILTER = Filters.Items.nameContains("Skills necklace").and(Filters.Items.nameContains("(")).and(notNotedFilter()),
            WEALTH_FILTER = Filters.Items.nameContains("Ring of wealth").and(Filters.Items.nameContains("(")).and(notNotedFilter()),
            BURNING_FILTER = Filters.Items.nameContains("Burning amulet").and(Filters.Items.nameContains("(")),
            DIGSITE_FILTER = Filters.Items.nameContains("Digsite pendant"),
            PASSAGE_FILTER = Filters.Items.nameContains("Necklace of passage").and(notNotedFilter()),
            TELEPORT_CRYSTAL_FILTER = Filters.Items.nameContains("Teleport crystal ("),
            XERICS_TALISMAN_FILTER = Filters.Items.nameEquals("Xeric's talisman"),
            RADAS_BLESSING_FILTER = Filters.Items.nameEquals("Rada's blessing 4");

    static boolean canTeleportToKourend = true;

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
            case GRAND_EXCHANGE_TELEPORT:
                if(!TeleportConstants.isVarrockTeleportAtGE())
                    return false;
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
                return (Inventory.find(BURNING_FILTER).length > 0 || Equipment.find(BURNING_FILTER).length > 0) && inMembersWorld();
            case DIGSITE_PENDANT:
                return (Inventory.find(DIGSITE_FILTER).length > 0 || Equipment.isEquipped(DIGSITE_FILTER)) && inMembersWorld();
            case NECKLACE_OF_PASSAGE:
                return (Inventory.find(PASSAGE_FILTER).length > 0 || Equipment.isEquipped(PASSAGE_FILTER)) && inMembersWorld();
            case ELF_CRYSTAL:
                return Inventory.find(TELEPORT_CRYSTAL_FILTER).length > 0 && inMembersWorld();
            case KOUREND_TELEPORT:
                return Spell.KOUREND_TELEPORT.canUse() && canTeleportToKourend && inMembersWorld();
            case XERICS_TALISMAN:
                return (Inventory.find(XERICS_TALISMAN_FILTER).length > 0 || Equipment.isEquipped(XERICS_TALISMAN_FILTER)) && inMembersWorld();
            case NARDAH_TELEPORT_SCROLL:
                return TeleportScrolls.NARDAH.canUse() && inMembersWorld();
            case PISCATORIS_TELEPORT_SCROLL:
                return TeleportScrolls.PISCATORIS.canUse() && inMembersWorld();
            case DIGSITE_TELEPORT_SCROLL:
                return TeleportScrolls.DIGSITE.canUse() && inMembersWorld();
            case FELDIP_HILLS_TELEPORT_SCROLL:
                return TeleportScrolls.FELDIP_HILLS.canUse() && inMembersWorld();
            case LUNAR_ISLE_TELEPORT_SCROLL:
                return TeleportScrolls.LUNAR_ISLE.canUse() && inMembersWorld();
            case MORTTON_TELEPORT_SCROLL:
                return TeleportScrolls.MORTTON.canUse() && inMembersWorld();
            case PEST_CONTROL_TELEPORT_SCROLL:
                return TeleportScrolls.PEST_CONTROL.canUse() && inMembersWorld();
            case TAI_BWO_WANNAI_TELEPORT_SCROLL:
                return TeleportScrolls.TAI_BWO_WANNAI.canUse() && inMembersWorld();
            case ELF_CAMP_TELEPORT_SCROLL:
                return TeleportScrolls.ELF_CAMP.canUse() && inMembersWorld();
            case MOS_LE_HARMLESS_TELEPORT_SCROLL:
                return TeleportScrolls.MOS_LE_HARMLESS.canUse() && inMembersWorld();
            case LUMBERYARD_TELEPORT_SCROLL:
                return TeleportScrolls.LUMBERYARD.canUse() && inMembersWorld();
            case ZULLANDRA_TELEPORT_SCROLL:
                return TeleportScrolls.ZULLANDRA.canUse() && inMembersWorld();
            case KEY_MASTER_TELEPORT_SCROLL:
                return TeleportScrolls.KEY_MASTER.canUse() && inMembersWorld();
            case REVENANT_CAVES_TELEPORT_SCROLL:
                return TeleportScrolls.REVENANT_CAVES.canUse() && inMembersWorld();
            case WEST_ARDOUGNE_TELEPORT:
                return Inventory.getCount("West ardougne teleport") > 0 && inMembersWorld();
            case RELLEKKA_TELEPORT:
                return inMembersWorld() && Inventory.getCount("Rellekka teleport") > 0;
            case RADAS_BLESSING:
                return inMembersWorld() && (Inventory.find(RADAS_BLESSING_FILTER).length > 0 || Equipment.find(RADAS_BLESSING_FILTER).length > 0);
        }
        return false;
    }

    public boolean use(TeleportLocation teleportLocation) {
        if (Banking.isBankScreenOpen()) {
            Banking.close();
        }
        switch (teleportLocation) {

            case VARROCK_CENTER:
                if(TeleportConstants.isVarrockTeleportAtGE()){
                    return RSItemHelper.click("Varrock t.*", "Varrock") || Spell.VARROCK_TELEPORT.cast();
                }
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
                return teleportWithScrollInterface(SKILLS_FILTER, ".*Fishing.*");
            case MOTHERLOAD_MINE:
                return teleportWithScrollInterface(SKILLS_FILTER, ".*Mining.*");
            case CRAFTING_GUILD:
                return teleportWithScrollInterface(SKILLS_FILTER, ".*Crafting.*");
            case COOKING_GUILD:
                return teleportWithScrollInterface(SKILLS_FILTER, ".*Cooking.*");
            case WOOD_CUTTING_GUILD:
                return teleportWithScrollInterface(SKILLS_FILTER, ".*Woodcutting.*");

            case GRAND_EXCHANGE:
                if(TeleportConstants.isVarrockTeleportAtGE() && VARROCK_TELEPORT.canUse()){
                    return RSItemHelper.click("Varrock t.*", "Break") || selectSpell("Varrock Teleport","Grand Exchange");
                }
                return teleportWithItem(WEALTH_FILTER, "Grand.*");
            case FALADOR_PARK:
                return teleportWithItem(WEALTH_FILTER, "Falad.*");

            case CHAOS_TEMPLE:
                return teleportWithItem(BURNING_FILTER, "(Chaos.*|Okay, teleport to level.*)");
            case BANDIT_CAMP:
                return teleportWithItem(BURNING_FILTER, "(Bandit.*|Okay, teleport to level.*)");
            case LAVA_MAZE:
                return teleportWithItem(BURNING_FILTER, "(Lava.*|Okay, teleport to level.*)");

            case DIGSITE_BARGE:
                return teleportWithItem(DIGSITE_FILTER,"Digsite");

            case OUTPOST:
                return teleportWithItem(PASSAGE_FILTER,"The Outpost");

            case LLETYA:
                return teleportWithItem(TELEPORT_CRYSTAL_FILTER,"Lletya");

            case KOUREND:
                if(Spell.KOUREND_TELEPORT.cast()){
                    if(getMatchingChatInterface("This spell requires the caster to recite an incantation.") != null){
                        canTeleportToKourend = false;
                        return false;
                    }
                    return true;
                }
                return false;

            case XERICS_INFERNO:
                return teleportWithScrollInterface(XERICS_TALISMAN_FILTER, ".*Xeric's Inferno");

            case NARDAH:
                return MasterScrollBook.Teleports.NARDAH.use() || TeleportScrolls.NARDAH.teleportTo(false);
            case DIGSITE:
                return MasterScrollBook.Teleports.DIGSITE.use() || TeleportScrolls.DIGSITE.teleportTo(false);
            case FELDIP_HILLS:
                return MasterScrollBook.Teleports.FELDIP_HILLS.use() || TeleportScrolls.FELDIP_HILLS.teleportTo(false);
            case LUNAR_ISLE:
                return MasterScrollBook.Teleports.LUNAR_ISLE.use() || TeleportScrolls.LUNAR_ISLE.teleportTo(false);
            case MORTTON:
                return MasterScrollBook.Teleports.MORTTON.use() || TeleportScrolls.MORTTON.teleportTo(false);
            case PEST_CONTROL:
                return MasterScrollBook.Teleports.PEST_CONTROL.use() || TeleportScrolls.PEST_CONTROL.teleportTo(false);
            case PISCATORIS:
                return MasterScrollBook.Teleports.PISCATORIS.use() || TeleportScrolls.PISCATORIS.teleportTo(false);
            case TAI_BWO_WANNAI:
                return MasterScrollBook.Teleports.TAI_BWO_WANNAI.use() || TeleportScrolls.TAI_BWO_WANNAI.teleportTo(false);
            case ELF_CAMP:
                return MasterScrollBook.Teleports.ELF_CAMP.use() || TeleportScrolls.ELF_CAMP.teleportTo(false);
            case MOS_LE_HARMLESS:
                return MasterScrollBook.Teleports.MOS_LE_HARMLESS.use() || TeleportScrolls.MOS_LE_HARMLESS.teleportTo(false);
            case LUMBERYARD:
                return MasterScrollBook.Teleports.LUMBERYARD.use() || TeleportScrolls.LUMBERYARD.teleportTo(false);
            case ZULLANDRA:
                return MasterScrollBook.Teleports.ZULLANDRA.use() || TeleportScrolls.ZULLANDRA.teleportTo(false);
            case KEY_MASTER:
                return MasterScrollBook.Teleports.KEY_MASTER.use() || TeleportScrolls.KEY_MASTER.teleportTo(false);
            case REVENANT_CAVES:
                return MasterScrollBook.Teleports.REVENANT_CAVES.use() || TeleportScrolls.REVENANT_CAVES.teleportTo(false);
            case WEST_ARDOUGNE:
                return RSItemHelper.click("West ardougne t.*", "Break");
            case RELLEKKA_POH:
                return RSItemHelper.click("Rellekka teleport", "Break");
            case KOUREND_WOODLAND:
                return teleportWithItem(RADAS_BLESSING_FILTER, "Kourend .*");
            case MOUNT_KARUULM:
                return teleportWithItem(RADAS_BLESSING_FILTER, "Mount.*");
        }
        return false;
    }

    private static boolean inMembersWorld() {
        return WorldHopper.isMembers(WorldHopper.getWorld());
    }

    private static Predicate<RSItem> notNotedFilter() {
        return rsItem -> rsItem.getDefinition() != null && !rsItem.getDefinition().isNoted();
    }

    private static boolean itemAction(String name, String... actions) {
        RSItem[] items = Inventory.find(name);
        if (items.length == 0) {
            return false;
        }
        return items[0].click(actions);
    }

    private static boolean teleportWithItem(Predicate<RSItem> itemFilter, String regex) {
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

    private static boolean teleportWithScrollInterface(Predicate<RSItem> itemFilter, String regex){
        ArrayList<RSItem> items = new ArrayList<>();
        items.addAll(Arrays.asList(Inventory.find(itemFilter)));
        items.addAll(Arrays.asList(Equipment.find(itemFilter)));

        if (items.size() == 0) {
            return false;
        }

        if(!Interfaces.isInterfaceSubstantiated(TeleportConstants.SCROLL_INTERFACE_MASTER)){
            RSItem teleportItem = items.get(0);
            if (!RSItemHelper.clickMatch(teleportItem, "(Rub|" + regex + ")") ||
                    !Timing.waitCondition(() -> Interfaces.isInterfaceSubstantiated(TeleportConstants.SCROLL_INTERFACE_MASTER),2500)) {
                return false;
            }
        }

        return handleScrollInterface(regex);
    }

    private static boolean handleScrollInterface(String regex){
        RSInterface box = Interfaces.get(187, 3);
        if(box == null)
            return false;
        RSInterface[] children = box.getChildren();
        if(children == null)
            return false;
        for(RSInterface child:children){
            String txt = child.getText();
            if(txt != null && General.stripFormatting(txt).matches(regex)){
                return child.click();
            }
        }
        return false;
    }

    private static RSInterface getMatchingChatInterface(String text){
        RSInterface chatBox = getChatBox();
        if(chatBox == null)
            return null;
        for(RSInterface child: chatBox.getChildren()){
            if(child.getIndex() > 10){
                return null;
            }
            String txt = child.getText();
            if(txt != null && txt.contains(text)){
                return child;
            }
        }
        return null;
    }

    private static RSInterface getChatBox(){
       return InterfaceHelper.getAllInterfaces(162).stream().filter(i -> {
            RSInterface[] children = i.getChildren();
            return children != null && children.length >= 900;
        }).findFirst().orElse(null);
    }


    private static boolean selectSpell(String spellName, String action){
        if(!GameTab.TABS.MAGIC.open()){
            return false;
        }
        List<RSInterface> spells = InterfaceHelper.getAllInterfaces(TeleportConstants.SPELLBOOK_INTERFACE_MASTER);
        RSInterface target = spells.stream().filter(spell -> {
            String name = spell.getComponentName();
            return name != null && name.contains(spellName) && !spell.isHidden();
        }).findFirst().orElse(null);
        return target != null && target.click(action);
    }
}
