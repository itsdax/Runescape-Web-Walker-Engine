package dax.teleports;

import dax.api_lib.models.Requirement;
import dax.shared.helpers.InterfaceHelper;
import dax.shared.helpers.RSItemHelper;
import dax.shared.helpers.magic.Spell;
import dax.shared.helpers.magic.SpellBook;
import dax.shared.helpers.questing.QuestHelper;
import dax.teleports.teleport_utils.TeleportConstants;
import dax.teleports.teleport_utils.TeleportLimit;
import dax.teleports.teleport_utils.TeleportScrolls;
import dax.walker_engine.WaitFor;
import dax.walker_engine.interaction_handling.NPCInteraction;
import org.tribot.api.General;
import org.tribot.api.ScriptCache;
import org.tribot.api.Timing;
import org.tribot.api.input.Keyboard;
import org.tribot.api2007.*;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSInterface;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSTile;
import org.tribot.api2007.types.RSVarBit;
import org.tribot.script.sdk.Minigame;
import org.tribot.script.sdk.Quest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public enum Teleport {

	VARROCK_TELEPORT(
		35, new RSTile(3212, 3424, 0),
		Spell.VARROCK_TELEPORT::canUse,
		() -> selectSpell("Varrock Teleport","Cast"),
		false
	),

	VARROCK_TELEPORT_TAB(
		35, new RSTile(3212, 3424, 0),
		() -> Inventory.getCount("Varrock teleport") > 0,
		() -> RSItemHelper.click("Varrock t.*", "Break")
	),

	VARROCK_TELEPORT_GRAND_EXCHANGE(
		35, new RSTile(3161, 3478, 0),
		() -> Spell.VARROCK_TELEPORT.canUse() && TeleportConstants.isVarrockTeleportAtGE(),
		() -> selectSpell("Varrock Teleport","Grand Exchange")
	),

	LUMBRIDGE_TELEPORT(
		35, new RSTile(3225, 3219, 0),
		Spell.LUMBRIDGE_TELEPORT::canUse,
		() -> selectSpell("Lumbridge Teleport","Cast"),
		false
	),

	LUMBRIDGE_TELEPORT_TAB(
		35, new RSTile(3225, 3219, 0),
		() -> Inventory.getCount("Lumbridge teleport") > 0,
		() -> RSItemHelper.click("Lumbridge t.*", "Break"),
		false
	),

	FALADOR_TELEPORT(
		35, new RSTile(2966, 3379, 0),
		Spell.FALADOR_TELEPORT::canUse,
		() -> selectSpell("Falador Teleport","Cast"),
		false
	),

	FALADOR_TELEPORT_TAB(
		35, new RSTile(2966, 3379, 0),
		() -> Inventory.getCount("Falador teleport") > 0,
		() -> RSItemHelper.click("Falador t.*", "Break")
	),

	CAMELOT_TELEPORT(
		35, new RSTile(2757, 3479, 0),
		Spell.CAMELOT_TELEPORT::canUse,
		() -> selectSpell("Camelot Teleport","Cast")

	),

	CAMELOT_TELEPORT_TAB(
		35, new RSTile(2757, 3479, 0),
		() -> Inventory.getCount("Camelot teleport") > 0,
		() -> RSItemHelper.click("Camelot t.*", "Break")
	),

	SEERS_TELEPORT(
		35, new RSTile(2757, 3479, 0),
		() -> Spell.CAMELOT_TELEPORT.canUse() && RSVarBit.get(4560).getValue() == 1,
		() -> selectSpell("Camelot Teleport","Seers'")
	),

	ARDOUGNE_TELEPORT(
		35, new RSTile(2661, 3300, 0),
		Spell.ARDOUGNE_TELEPORT::canUse,
		() -> selectSpell("Ardougne Teleport","Cast")

	),

	ARDOUGNE_TELEPORT_TAB(
		35, new RSTile(2661, 3300, 0),
		() -> Inventory.getCount("Ardougne teleport") > 0,
		() -> RSItemHelper.click("Ardougne t.*", "Break")
	),

	NARDAH_TELEPORT(
		35, TeleportScrolls.NARDAH
	),
	DIGSITE_TELEPORT(
		35, TeleportScrolls.DIGSITE
	),
	FELDIP_HILLS_TELEPORT(
		35, TeleportScrolls.FELDIP_HILLS
	),
	LUNAR_ISLE_TELEPORT(
		35, TeleportScrolls.LUNAR_ISLE
	),
	MORTTON_TELEPORT(
		35, TeleportScrolls.MORTTON
	),
	PEST_CONTROL_TELEPORT(
		35, TeleportScrolls.PEST_CONTROL
	),
	PISCATORIS_TELEPORT(
		35, TeleportScrolls.PISCATORIS
	),
	TAI_BWO_WANNAI_TELEPORT(
		35, TeleportScrolls.TAI_BWO_WANNAI
	),
	ELF_CAMP_TELEPORT(
		35, TeleportScrolls.ELF_CAMP
	),
	MOS_LE_HARMLESS_TELEPORT(
		35, TeleportScrolls.MOS_LE_HARMLESS
	),
	LUMBERYARD_TELEPORT(
		35, TeleportScrolls.LUMBERYARD
	),
	ZULLANDRA_TELEPORT(
		35, TeleportScrolls.ZULLANDRA
	),
	KEY_MASTER_TELEPORT(
		35, TeleportScrolls.KEY_MASTER
	),
	REVENANT_CAVES_TELEPORT(
		35, TeleportScrolls.REVENANT_CAVES
	),
	WATSON_TELEPORT(
		35, TeleportScrolls.WATSON
	),


	RING_OF_WEALTH_GRAND_EXCHANGE(
		35, new RSTile(3161, 3478, 0),
		() -> WearableItemTeleport.has(WearableItemTeleport.RING_OF_WEALTH_FILTER),
		() -> WearableItemTeleport.teleport(WearableItemTeleport.RING_OF_WEALTH_FILTER, "(?i)Grand Exchange"),
		TeleportConstants.LEVEL_30_WILDERNESS_LIMIT
	),

	RING_OF_WEALTH_FALADOR(
		35, new RSTile(2994, 3377, 0),
		() -> WearableItemTeleport.has(WearableItemTeleport.RING_OF_WEALTH_FILTER),
		() -> WearableItemTeleport.teleport(WearableItemTeleport.RING_OF_WEALTH_FILTER, "(?i)falador.*"),
		TeleportConstants.LEVEL_30_WILDERNESS_LIMIT
	),

	RING_OF_WEALTH_MISCELLANIA(
		35, new RSTile(2535, 3861, 0),
		() -> WearableItemTeleport.has(WearableItemTeleport.RING_OF_WEALTH_FILTER) && Game.getSetting(359) >= 100,
		() -> WearableItemTeleport.teleport(WearableItemTeleport.RING_OF_WEALTH_FILTER, "(?i)misc.*"),
		TeleportConstants.LEVEL_30_WILDERNESS_LIMIT
	),

	RING_OF_DUELING_PVP_ARENA (
		35, new RSTile(3313, 3233, 0),
		() -> WearableItemTeleport.has(WearableItemTeleport.RING_OF_DUELING_FILTER),
		() -> WearableItemTeleport.teleport(WearableItemTeleport.RING_OF_DUELING_FILTER, "(?i).*PvP Arena.*")
	),

	RING_OF_DUELING_CASTLE_WARS (
		35, new RSTile(2440, 3090, 0),
		() -> WearableItemTeleport.has(WearableItemTeleport.RING_OF_DUELING_FILTER),
		() -> WearableItemTeleport.teleport(WearableItemTeleport.RING_OF_DUELING_FILTER, "(?i).*Castle Wars.*")
	),

	RING_OF_DUELING_FEROX_ENCLAVE (
		35, new RSTile(3150, 3635, 0),
		() -> WearableItemTeleport.has(WearableItemTeleport.RING_OF_DUELING_FILTER),
		() -> WearableItemTeleport.teleport(WearableItemTeleport.RING_OF_DUELING_FILTER, "(?i).*Ferox Enclave.*")
	),

	NECKLACE_OF_PASSAGE_WIZARD_TOWER (
		35, new RSTile(3113, 3179, 0),
		() -> WearableItemTeleport.has(WearableItemTeleport.NECKLACE_OF_PASSAGE_FILTER),
		() -> WearableItemTeleport.teleport(WearableItemTeleport.NECKLACE_OF_PASSAGE_FILTER, "(?i).*wizard.+tower.*")
	),

	NECKLACE_OF_PASSAGE_OUTPOST (
		35, new RSTile(2430, 3347, 0),
		() -> WearableItemTeleport.has(WearableItemTeleport.NECKLACE_OF_PASSAGE_FILTER),
		() -> WearableItemTeleport.teleport(WearableItemTeleport.NECKLACE_OF_PASSAGE_FILTER, "(?i).*the.+outpost.*")
	),

	NECKLACE_OF_PASSAGE_EYRIE (
		35, new RSTile(3406, 3156, 0),
		() -> WearableItemTeleport.has(WearableItemTeleport.NECKLACE_OF_PASSAGE_FILTER),
		() -> WearableItemTeleport.teleport(WearableItemTeleport.NECKLACE_OF_PASSAGE_FILTER, "(?i).*eagl.+eyrie.*")
	),

	COMBAT_BRACE_WARRIORS_GUILD (
		35, new RSTile(2882, 3550, 0),
		() -> WearableItemTeleport.has(WearableItemTeleport.COMBAT_BRACE_FILTER),
		() -> WearableItemTeleport.teleport(WearableItemTeleport.COMBAT_BRACE_FILTER, "(?i).*warrior.+guild.*")
	),

	COMBAT_BRACE_CHAMPIONS_GUILD (
		35, new RSTile(3190, 3366, 0),
		() -> WearableItemTeleport.has(WearableItemTeleport.COMBAT_BRACE_FILTER),
		() -> WearableItemTeleport.teleport(WearableItemTeleport.COMBAT_BRACE_FILTER, "(?i).*champion.+guild.*")
	),

	COMBAT_BRACE_MONASTARY (
		35, new RSTile(3053, 3486, 0),
		() -> WearableItemTeleport.has(WearableItemTeleport.COMBAT_BRACE_FILTER),
		() -> WearableItemTeleport.teleport(WearableItemTeleport.COMBAT_BRACE_FILTER, "(?i).*monastery.*")
	),

	COMBAT_BRACE_RANGE_GUILD (
		35, new RSTile(2656, 3442, 0),
		() -> WearableItemTeleport.has(WearableItemTeleport.COMBAT_BRACE_FILTER),
		() -> WearableItemTeleport.teleport(WearableItemTeleport.COMBAT_BRACE_FILTER, "(?i).*rang.+guild.*")
	),

	GAMES_NECK_BURTHORPE (
		35, new RSTile(2897, 3551, 0),
		() -> WearableItemTeleport.has(WearableItemTeleport.GAMES_NECKLACE_FILTER),
		() -> WearableItemTeleport.teleport(WearableItemTeleport.GAMES_NECKLACE_FILTER, "(?i).*burthorpe.*")
	),

	GAMES_NECK_BARBARIAN_OUTPOST (
		35, new RSTile(2520, 3570, 0),
		() -> WearableItemTeleport.has(WearableItemTeleport.GAMES_NECKLACE_FILTER),
		() -> WearableItemTeleport.teleport(WearableItemTeleport.GAMES_NECKLACE_FILTER, "(?i).*barbarian.*")
	),

	GAMES_NECK_CORPOREAL (
		35, new RSTile(2965, 4382, 2),
		() -> WearableItemTeleport.has(WearableItemTeleport.GAMES_NECKLACE_FILTER),
		() -> WearableItemTeleport.teleport(WearableItemTeleport.GAMES_NECKLACE_FILTER, "(?i).*corporeal.*")
	),

	GAMES_NECK_WINTERTODT (
		35, new RSTile(1623, 3937, 0),
		() -> hasBeenToZeah() && WearableItemTeleport.has(WearableItemTeleport.GAMES_NECKLACE_FILTER),
		() -> WearableItemTeleport.teleport(WearableItemTeleport.GAMES_NECKLACE_FILTER, "(?i).*wintertodt.*")
	),

	GLORY_EDGEVILLE (
		35, new RSTile(3087, 3496, 0),
		() -> WearableItemTeleport.has(WearableItemTeleport.GLORY_FILTER),
		() -> WearableItemTeleport.teleport(WearableItemTeleport.GLORY_FILTER, "(?i).*edgeville.*"),
		TeleportConstants.LEVEL_30_WILDERNESS_LIMIT
	),

	GLORY_KARAMJA (
		35, new RSTile(2918, 3176, 0),
		() -> WearableItemTeleport.has(WearableItemTeleport.GLORY_FILTER),
		() -> WearableItemTeleport.teleport(WearableItemTeleport.GLORY_FILTER,"(?i).*karamja.*"),
		TeleportConstants.LEVEL_30_WILDERNESS_LIMIT
	),

	GLORY_DRAYNOR (
		35, new RSTile(3105, 3251, 0),
		() -> WearableItemTeleport.has(WearableItemTeleport.GLORY_FILTER),
		() -> WearableItemTeleport.teleport(WearableItemTeleport.GLORY_FILTER,"(?i).*draynor.*"),
		TeleportConstants.LEVEL_30_WILDERNESS_LIMIT
	),

	GLORY_AL_KHARID (
		35, new RSTile(3293, 3163, 0),
		() -> WearableItemTeleport.has(WearableItemTeleport.GLORY_FILTER),
		() -> WearableItemTeleport.teleport(WearableItemTeleport.GLORY_FILTER, "(?i).*al kharid.*"),
		TeleportConstants.LEVEL_30_WILDERNESS_LIMIT
	),

	SKILLS_FISHING_GUILD (
		35, new RSTile(2610, 3391, 0),
		() -> WearableItemTeleport.has(WearableItemTeleport.SKILLS_FILTER),
		() -> teleportWithScrollInterface(WearableItemTeleport.SKILLS_FILTER, ".*Fishing.*"),
		TeleportConstants.LEVEL_30_WILDERNESS_LIMIT
	),

	SKILLS_MINING_GUILD (
		35, new RSTile(3052, 9764, 0),
		() -> WearableItemTeleport.has(WearableItemTeleport.SKILLS_FILTER),
		() -> teleportWithScrollInterface(WearableItemTeleport.SKILLS_FILTER, ".*Mining.*"),
		TeleportConstants.LEVEL_30_WILDERNESS_LIMIT
	),

	SKILLS_CRAFTING_GUILD (
		35, new RSTile(2935, 3293, 0),
		() -> WearableItemTeleport.has(WearableItemTeleport.SKILLS_FILTER),
		() -> teleportWithScrollInterface(WearableItemTeleport.SKILLS_FILTER, ".*Craft.*"),
		TeleportConstants.LEVEL_30_WILDERNESS_LIMIT
	),

	SKILLS_COOKING_GUILD (
		35, new RSTile(3145, 3442, 0),
		() -> WearableItemTeleport.has(WearableItemTeleport.SKILLS_FILTER),
		() -> teleportWithScrollInterface(WearableItemTeleport.SKILLS_FILTER, ".*Cooking.*"),
		TeleportConstants.LEVEL_30_WILDERNESS_LIMIT
	),

	SKILLS_WOODCUTTING_GUILD (
		35, new RSTile(1663, 3507, 0),
		() -> hasBeenToZeah() && WearableItemTeleport.has(WearableItemTeleport.SKILLS_FILTER),
		() -> teleportWithScrollInterface(WearableItemTeleport.SKILLS_FILTER, ".*Woodcutting.*"),
		TeleportConstants.LEVEL_30_WILDERNESS_LIMIT
	),

	SKILLS_FARMING_GUILD_OUTSIDE (
			35, new RSTile(1248, 3719, 0),
			() -> WearableItemTeleport.has(WearableItemTeleport.SKILLS_FILTER) && (RSVarBit.get(4895).getValue() < 600 || Skills.SKILLS.FARMING.getActualLevel() < 45),
			() -> teleportWithScrollInterface(WearableItemTeleport.SKILLS_FILTER, ".*Farming.*"),
			TeleportConstants.LEVEL_30_WILDERNESS_LIMIT
	),

	SKILLS_FARMING_GUILD_INSIDE (
			35, new RSTile(1249, 3727, 0),
			() -> WearableItemTeleport.has(WearableItemTeleport.SKILLS_FILTER)
					&& RSVarBit.get(4895).getValue() >= 600 && Skills.SKILLS.FARMING.getActualLevel() >= 45,
			() -> teleportWithScrollInterface(WearableItemTeleport.SKILLS_FILTER, ".*Farming.*"),
			TeleportConstants.LEVEL_30_WILDERNESS_LIMIT
	),

	BURNING_AMULET_CHAOS_TEMPLE (
		35, new RSTile(3236, 3635, 0),
		() -> WearableItemTeleport.has(WearableItemTeleport.BURNING_AMULET_FILTER),
		() -> WearableItemTeleport.teleport(WearableItemTeleport.BURNING_AMULET_FILTER, "(Chaos.*|Okay, teleport to level.*)")
	),

	BURNING_AMULET_BANDIT_CAMP (
		35, new RSTile(3039, 3652, 0),
		() -> WearableItemTeleport.has(WearableItemTeleport.BURNING_AMULET_FILTER),
		() -> WearableItemTeleport.teleport(WearableItemTeleport.BURNING_AMULET_FILTER, "(Bandit.*|Okay, teleport to level.*)")
	),

	BURNING_AMULET_LAVA_MAZE (
		35, new RSTile(3029, 3843, 0),
		() -> WearableItemTeleport.has(WearableItemTeleport.BURNING_AMULET_FILTER),
		() -> WearableItemTeleport.teleport(WearableItemTeleport.BURNING_AMULET_FILTER, "(Lava.*|Okay, teleport to level.*)")
	),

	DIGSITE_PENDANT (
		35, new RSTile(3346,3445,0),
		() -> WearableItemTeleport.has(WearableItemTeleport.DIGSITE_PENDANT_FILTER),
		() -> WearableItemTeleport.teleport(WearableItemTeleport.DIGSITE_PENDANT_FILTER, "Digsite")
	),

	ECTOPHIAL (
		0, new RSTile(3660, 3524, 0),
		() -> Inventory.find(Filters.Items.nameContains("Ectophial")).length > 0,
		() -> RSItemHelper.click(Filters.Items.nameContains("Ectophial"), "Empty")
	),

	LLETYA (
		35, new RSTile(2330,3172,0),
		() -> WearableItemTeleport.has(WearableItemTeleport.TELEPORT_CRYSTAL_FILTER),
		() -> WearableItemTeleport.teleport(WearableItemTeleport.TELEPORT_CRYSTAL_FILTER, "Lletya")
	),

	PRIFDDINAS(
			35, new RSTile(3264, 6068, 0),
			() -> inMembersWorld() && WearableItemTeleport.has(WearableItemTeleport.TELEPORT_CRYSTAL_FILTER),
			() -> WearableItemTeleport.teleport(WearableItemTeleport.TELEPORT_CRYSTAL_FILTER, "Prifddinas")
	),

	XERICS_GLADE(
		35, new RSTile(1753, 3565, 0),
		() -> WearableItemTeleport.has(WearableItemTeleport.XERICS_TALISMAN_FILTER),
		() -> teleportWithScrollInterface(WearableItemTeleport.XERICS_TALISMAN_FILTER, ".*Xeric's Glade")
	),
	XERICS_INFERNO(
		35, new RSTile(1505,3809,0),
		() -> WearableItemTeleport.has(WearableItemTeleport.XERICS_TALISMAN_FILTER),
		() -> teleportWithScrollInterface(WearableItemTeleport.XERICS_TALISMAN_FILTER, ".*Xeric's Inferno")
	),
	XERICS_LOOKOUT(
		35, new RSTile(1575, 3531, 0),
		() -> WearableItemTeleport.has(WearableItemTeleport.XERICS_TALISMAN_FILTER),
		() -> teleportWithScrollInterface(WearableItemTeleport.XERICS_TALISMAN_FILTER, ".*Xeric's Lookout")
	),

	WEST_ARDOUGNE_TELEPORT_TAB(
		35, new RSTile(2500,3290,0),
		() -> Inventory.getCount("West ardougne teleport") > 0,
		() -> RSItemHelper.click("West ardougne t.*", "Break")
	),

	RADAS_BLESSING_KOUREND_WOODLAND(
		0, new RSTile(1558, 3458, 0),
		() -> WearableItemTeleport.has(WearableItemTeleport.RADAS_BLESSING_FILTER),
		() -> WearableItemTeleport.teleport(WearableItemTeleport.RADAS_BLESSING_FILTER, "Kourend .*")
	),
	RADAS_BLESSING_MOUNT_KARUULM(
		0, new RSTile(1310, 3796, 0),
		() -> WearableItemTeleport.has(WearableItemTeleport.RADAS_BLESSING_FILTER.and(Filters.Items.nameContains("3","4"))),
		() -> WearableItemTeleport.teleport(WearableItemTeleport.RADAS_BLESSING_FILTER, "Mount.*")
	),

	CRAFTING_CAPE_TELEPORT(
		0, new RSTile(2931, 3286, 0),
		() -> WearableItemTeleport.has(WearableItemTeleport.CRAFTING_CAPE_FILTER),
		() -> WearableItemTeleport.teleport(WearableItemTeleport.CRAFTING_CAPE_FILTER, "Teleport")
	),

	CABBAGE_PATCH_TELEPORT(
		0, new RSTile(3049, 3287, 0),
		() -> WearableItemTeleport.has(WearableItemTeleport.EXPLORERS_RING_FILTER),
		() -> WearableItemTeleport.teleport(WearableItemTeleport.EXPLORERS_RING_FILTER, "Teleport")
	),

	LEGENDS_GUILD_TELEPORT(
		0, new RSTile(2729, 3348, 0),
		() -> WearableItemTeleport.has(WearableItemTeleport.QUEST_CAPE_FILTER),
		() -> WearableItemTeleport.teleport(WearableItemTeleport.QUEST_CAPE_FILTER, "Teleport")
	),

	KANDARIN_MONASTERY_TELEPORT(
		0, new RSTile(2606, 3216, 0),
		() -> WearableItemTeleport.has(WearableItemTeleport.ARDOUGNE_CLOAK_FILTER),
		() -> WearableItemTeleport.teleport(WearableItemTeleport.ARDOUGNE_CLOAK_FILTER, ".*Monastery.*")
	),

	RIMMINGTON_TELEPORT_TAB(
		35, new RSTile(2954,3224, 0),
		() -> Inventory.getCount("Rimmington teleport") > 0,
		() -> RSItemHelper.click("Rimmington t.*", "Break")
	),

	TAVERLEY_TELEPORT_TAB(
		35, new RSTile(2894, 3465, 0),
		() -> Inventory.getCount("Taverley teleport") > 0,
		() -> RSItemHelper.click("Taverley t.*", "Break")
	),

	RELLEKKA_TELEPORT_TAB(
		35, new RSTile(2668, 3631, 0),
		() -> Inventory.getCount("Rellekka teleport") > 0,
		() -> RSItemHelper.click("Rellekka t.*", "Break")
	),

	BRIMHAVEN_TELEPORT_TAB(
		35, new RSTile(2758, 3178, 0),
		() -> Inventory.getCount("Brimhaven teleport") > 0,
		() -> RSItemHelper.click("Brimhaven t.*", "Break")
	),

	POLLNIVNEACH_TELEPORT_TAB(
		35, new RSTile(3340, 3004, 0),
		() -> Inventory.getCount("Pollnivneach teleport") > 0,
		() -> RSItemHelper.click("Pollnivneach t.*", "Break")
	),

	YANILLE_TELEPORT_TAB(
		35, new RSTile(2544, 3095, 0),
		() -> Inventory.getCount("Yanille teleport") > 0,
		() -> RSItemHelper.click("Yanille t.*", "Break")
	),

	HOSIDIUS_TELEPORT_TAB(
		35, new RSTile(1744, 3517, 0),
		() -> Inventory.getCount("Hosidius teleport") > 0,
		() -> RSItemHelper.click("Hosidius t.*", "Break")
	),

	CONSTRUCTION_CAPE_RIMMINGTON(
		0, new RSTile(2954,3224, 0),
		() -> WearableItemTeleport.has(WearableItemTeleport.CONSTRUCTION_CAPE_FILTER),
		() -> teleportWithScrollInterface(WearableItemTeleport.CONSTRUCTION_CAPE_FILTER,".*Rimmington")
	),

	CONSTRUCTION_CAPE_TAVERLEY(
		0, new RSTile(2894, 3465, 0),
		() -> WearableItemTeleport.has(WearableItemTeleport.CONSTRUCTION_CAPE_FILTER),
		() -> teleportWithScrollInterface(WearableItemTeleport.CONSTRUCTION_CAPE_FILTER,".*Taverley")
	),

	CONSTRUCTION_CAPE_RELLEKKA(
		0, new RSTile(2668, 3631, 0),
		() -> WearableItemTeleport.has(WearableItemTeleport.CONSTRUCTION_CAPE_FILTER),
		() -> teleportWithScrollInterface(WearableItemTeleport.CONSTRUCTION_CAPE_FILTER,".*Rellekka")
	),

	CONSTRUCTION_CAPE_BRIMHAVEN(
		0, new RSTile(2758, 3178, 0),
		() -> WearableItemTeleport.has(WearableItemTeleport.CONSTRUCTION_CAPE_FILTER),
		() -> teleportWithScrollInterface(WearableItemTeleport.CONSTRUCTION_CAPE_FILTER,".*Brimhaven")
	),

	CONSTRUCTION_CAPE_POLLNIVNEACH(
		0, new RSTile(3340, 3004, 0),
		() -> WearableItemTeleport.has(WearableItemTeleport.CONSTRUCTION_CAPE_FILTER),
		() -> teleportWithScrollInterface(WearableItemTeleport.CONSTRUCTION_CAPE_FILTER,".*Pollnivneach")
	),

	CONSTRUCTION_CAPE_YANILLE(
		0, new RSTile(2544, 3095, 0),
		() -> WearableItemTeleport.has(WearableItemTeleport.CONSTRUCTION_CAPE_FILTER),
		() -> teleportWithScrollInterface(WearableItemTeleport.CONSTRUCTION_CAPE_FILTER,".*Yanille")
	),

	CONSTRUCTION_CAPE_HOSIDIUS(
		0, new RSTile(1744, 3517, 0),
		() -> WearableItemTeleport.has(WearableItemTeleport.CONSTRUCTION_CAPE_FILTER),
		() -> teleportWithScrollInterface(WearableItemTeleport.CONSTRUCTION_CAPE_FILTER,".*Hosidius")
	),

	SLAYER_RING_GNOME_STRONGHOLD(
		35, new RSTile(2433, 3424, 0),
		() -> WearableItemTeleport.has(WearableItemTeleport.SLAYER_RING),
		() -> WearableItemTeleport.teleport(WearableItemTeleport.SLAYER_RING, ".*Stronghold")
	),

	SLAYER_RING_MORYTANIA(
		35, new RSTile(3422, 3537, 0),
		() -> WearableItemTeleport.has(WearableItemTeleport.SLAYER_RING),
		() -> WearableItemTeleport.teleport(WearableItemTeleport.SLAYER_RING, ".*Tower")
	),

	SLAYER_RING_RELLEKKA_CAVE(
		35, new RSTile(2801, 9999, 0),
		() -> WearableItemTeleport.has(WearableItemTeleport.SLAYER_RING),
		() -> WearableItemTeleport.teleport(WearableItemTeleport.SLAYER_RING, ".*Rellekka")
	),

	SALVE_GRAVEYARD_TAB(
		35, new RSTile(3432, 3460, 0),
		() -> Inventory.getCount("Salve graveyard teleport") > 0,
		() -> RSItemHelper.click("Salve graveyard t.*", "Break")
	),

	FENKENSTRAINS_CASTLE_TAB(
			35, new RSTile(3547, 3528, 0),
			() -> Inventory.getCount("Fenkenstrain's castle teleport") > 0,
			() -> RSItemHelper.click("Fenkenstrain's castle t.*", "Break")
	),

	BARROWS_TAB(
			35, new RSTile(3565, 3314, 0),
			() -> Inventory.getCount("Barrows teleport") > 0,
			() -> RSItemHelper.click("Barrows t.*", "Break")
	),

	ARCEUUS_LIBRARY_TAB(
			35, new RSTile(1632, 3838, 0),
			() -> Inventory.getCount("Arceuus library teleport") > 0,
			() -> RSItemHelper.click("Arceuus library t.*", "Break")
	),

	BATTLEFRONT_TAB(
			35, new RSTile(1349,3738,0),
			() -> Inventory.getCount("Battlefront teleport") > 0,
			() -> RSItemHelper.click("Battlefront t.*", "Break")
	),

	DRAYNOR_MANOR_TAB(
			35, new RSTile(3109,3352,0),
			() -> Inventory.getCount("Draynor manor teleport") > 0,
			() -> RSItemHelper.click("Draynor manor t.*", "Break")
	),

	MIND_ALTAR_TAB(
			35, new RSTile(2980, 3510, 0),
			() -> Inventory.getCount("Mind altar teleport") > 0,
			() -> RSItemHelper.click("Mind altar t.*", "Break")
	),

	ENCHANTED_LYRE_RELLEKA(
			35, new RSTile(2661, 3465, 0),
			() -> Inventory.find(Filters.Items.nameContains("Enchanted lyre")).length > 0,
			() -> {
				RSItem[] lyre = Inventory.find(Filters.Items.nameContains("Enchanted lyre"));
				return lyre.length > 0 && RSItemHelper.clickMatch(lyre[0], "Play|Rellekka.*");
			}
	),

	FARMING_CAPE_TELEPORT(
			0, new RSTile(1248, 3726, 0),
			() -> WearableItemTeleport.has(WearableItemTeleport.FARMING_CAPE_FILTER),
			() -> WearableItemTeleport.teleport(WearableItemTeleport.FARMING_CAPE_FILTER, "Teleport")
	),

	ROYAL_SEED_POD(
			0, new RSTile(2465, 3495, 0),
			() -> Inventory.getCount("Royal seed pod") > 0,
			() -> RSItemHelper.click("Royal seed.*", "Commune")
	),

	DRAKANS_MEDALLION_VER_SINHAZA(
			0, new RSTile(3649, 3230, 0),
			() -> WearableItemTeleport.has(WearableItemTeleport.DRAKANS_MEDALLION_FILTER),
			() -> RSItemHelper.click("Drakan's.*", "Ver Sinhaza")

	),

	DRAKANS_MEDALLION_DARKMEYER(
			0, new RSTile(3592, 3337, 0),
			() -> WearableItemTeleport.has(WearableItemTeleport.DRAKANS_MEDALLION_FILTER),
			() -> RSItemHelper.click("Drakan's.*", "Darkmeyer")

	),

	BARBARIAN_ASSASULT_MINIGAME(
			Minigame.BARBARIAN_ASSAULT,
			new RSTile(2532, 3577, 0),
			() -> RSVarBit.get(3251).getValue() > 0
	),
	BLAST_FURNACE_MINIGAME(
			Minigame.BLAST_FURNACE,
			new RSTile(0, 0 ,0),
			() -> RSVarBit.get(575).getValue() >= 1
	),
	BURTHROPE_GAMES_ROOM_MINIGAME(
			Minigame.BURTHORPE_GAMES_ROOM,
			new RSTile(2207, 4938, 0)
	),
	CASTLE_WARS_MINIGAME(
			Minigame.CASTLE_WARS,
			new RSTile(2442, 3092, 0),
			false
	),
	CLAN_WARS_MINIGAME(
			Minigame.CLAN_WARS,
			new RSTile(3149, 3635, 0),
			false
	),
	FISHING_TRAWLER_MINIGAME(
			Minigame.FISHING_TRAWLER,
			new RSTile(2660, 3158, 0),
			() -> Skills.SKILLS.FISHING.getActualLevel() >= 15
	),
	GIANTS_FOUNDRY_MINIGAME(
			Minigame.GIANTS_FOUNDRY,
			new RSTile(3361, 3147, 0)
	),
	GUARDIANS_OF_THE_RIFT_MINIGAME(
			Minigame.GUARDIANS_OF_THE_RIFT,
			new RSTile(3614, 9478, 0),
			() -> Quest.TEMPLE_OF_THE_EYE.getState() == Quest.State.COMPLETE
	),
	LAST_MAN_STANDING_MINIGAME(
			Minigame.LAST_MAN_STANDING,
			new RSTile(3151, 3635, 0),
			false
),
	NMZ_MINIGAME(
			Minigame.NIGHTMARE_ZONE,
			new RSTile(2611, 3122, 0),
			() -> QuestHelper.getNmzQuestsCompleted() >= 5
	),
	PEST_CONTROL_MINIGAME(
			Minigame.PEST_CONTROL,
			new RSTile(2653, 2656, 0),
			() -> Player.getRSPlayer().getCombatLevel() >= 40
	),
//	RAT_PITS_ARDOUGNE_MINIGAME(Minigame.RAT_PITS, new RSTile(0, 0, 0)),
//	RAT_PITS_VARROCK_MINIGAME(Minigame.RAT_PITS, new RSTile(0, 0, 0)),
//	RAT_PITS_KELDAGRIM_MINIGAME(Minigame.RAT_PITS, new RSTile(0, 0, 0)),
//	RAT_PITS_PORT_SARIM_MINIGAME(Minigame.RAT_PITS, new RSTile(3021, 3228, 0), "Port Sarim (wily cats)"),
	SHADES_OF_MORTTON_MINIGAME(
			Minigame.SHADES_OF_MORTON,
			new RSTile(3499, 3298, 0),
			() -> Quest.SHADES_OF_MORTTON.getState() == Quest.State.COMPLETE
	),
	SOUL_WARS_MINIGAME(
			Minigame.SOUL_WARS,
			new RSTile(2210, 2857, 0)
	),
//	TITHE_FARM_MINIGAME(Minigame.TITHE_FARM, new RSTile(0, 0, 0),) I didn't see a way to determine if we've unlocked it.
	TROUBLE_BREWING_MINIGAME(
			Minigame.TROUBLE_BREWING,
		new RSTile(3817, 3025, 0),
		() -> Quest.CABIN_FEVER.getState() == Quest.State.COMPLETE && Skills.SKILLS.COOKING.getActualLevel() >= 40,
		"No."
	),
	TZHAAR_FIGHT_PIT_MINIGAME(
			Minigame.TZHAAR_FIGHT_PIT,
			new RSTile(2402, 5181, 0),
			"No."
	),

	MOONCLAN_TELEPORT_TAB(
			35, new RSTile(2115, 3914, 0),
			() -> Quest.LUNAR_DIPLOMACY.getState() == Quest.State.COMPLETE && Inventory.getCount("Moonclan teleport") > 0,
			() -> RSItemHelper.click("Moonclan tele.*", "Break")
	),

	OURANIA_TELEPORT_TAB(
			35, new RSTile(2468, 3246, 0),
			() -> RSVarBit.get(5376).getValue() > 0 && Inventory.getCount("Ourania teleport") > 0,
			() -> RSItemHelper.click("Ourania t.*", "Break")
	),

	WATERBIRTH_TELEPORT_TAB(
			35, new RSTile(2546, 3757, 0),
			() -> Inventory.getCount("Waterbirth teleport") > 0,
			() -> RSItemHelper.click("Waterbirth t.*", "Break")
	),

	BARBARIAN_OUTPUT_TELEPORT_TAB(
			35, new RSTile(2544, 3568, 0),
			() -> Inventory.getCount("Barbarian teleport") > 0,
			() -> RSItemHelper.click("Barbarian tele.*", "Break")
	),

	KHAZARD_TELEPORT_TAB(
			35, new RSTile(2637, 3167, 0),
			() -> Inventory.getCount("Khazard teleport") > 0,
			() -> RSItemHelper.click("Khazard tele.*", "Break")
	),

	FISHING_GUILD_TELEPORT_TAB(
			35, new RSTile(2612, 3391, 0),
			() -> Inventory.getCount("Fishing guild teleport") > 0,
			() -> RSItemHelper.click("Fishing guild t.*", "Break")
	),

	CATHERBY_TELEPORT_TAB(
			35, new RSTile(2801, 3449, 0),
			() -> Inventory.getCount("Catherby teleport") > 0,
			() -> RSItemHelper.click("Catherby t.*", "Break")
	),

	LUMBRIDGE_HOME_TELEPORT(
			150, new RSTile(3225, 3219, 0),
			() -> canUseHomeTeleport() && SpellBook.getCurrentSpellBook() == SpellBook.Type.STANDARD,
			() -> {
				final RSTile myPos = Player.getPosition();
				return selectSpell("Lumbridge Home Teleport", "Cast") && Timing.waitCondition(() ->  !Player.getRSPlayer().isInCombat() &&
						!Player.getPosition().equals(myPos), 15000);
			},
			false
	),

	ARCEUUS_HOME_TELEPORT(
			150, new RSTile(1712, 3883, 0),
			() -> canUseHomeTeleport() && SpellBook.getCurrentSpellBook() == SpellBook.Type.ARCEUUS,
			() -> {
				final RSTile myPos = Player.getPosition();
				return selectSpell("Arceuus Home Teleport", "Cast") && Timing.waitCondition(() -> !Player.getRSPlayer().isInCombat() &&
						!Player.getPosition().equals(myPos), 15000);
			}
	),

	EDGEVILLE_HOME_TELEPORT(
			150, new RSTile(3087, 3496, 0),
			() -> canUseHomeTeleport() && SpellBook.getCurrentSpellBook() == SpellBook.Type.ANCIENT,
			() -> {
				final RSTile myPos = Player.getPosition();
				return selectSpell("Edgeville Home Teleport", "Cast") && Timing.waitCondition(() ->  !Player.getRSPlayer().isInCombat() &&
						!Player.getPosition().equals(myPos), 15000);
			}
	),

	LUNAR_HOME_TELEPORT(
			150, new RSTile(2095, 3913, 0),
			() -> canUseHomeTeleport() && SpellBook.getCurrentSpellBook() == SpellBook.Type.LUNAR,
			() -> {
				final RSTile myPos = Player.getPosition();
				return selectSpell("Lunar Home Teleport", "Cast") && Timing.waitCondition(() ->  !Player.getRSPlayer().isInCombat() &&
						!Player.getPosition().equals(myPos), 15000);
			}
	),

	;

	private final RSTile location;
	private final Requirement requirement;
	private final Action action;
	private final TeleportLimit teleportLimit;
	private final boolean requiresMembers;
	private final boolean canBeUsedInPvpWorlds;

	Teleport(int moveCost, RSTile location, Requirement requirement, Action action) {
		this(moveCost, location, requirement, action, true);
	}

	Teleport(int moveCost, RSTile location, Requirement requirement, Action action, boolean requiresMembers) {
		setMoveCost(moveCost);
		this.location = location;
		this.requirement = requirement;
		this.action = action;
		this.teleportLimit = TeleportConstants.LEVEL_20_WILDERNESS_LIMIT;
		this.requiresMembers = requiresMembers;
		this.canBeUsedInPvpWorlds = true;
	}

	Teleport(int moveCost, RSTile location, Requirement requirement, Action action, TeleportLimit limit) {
		this(moveCost, location, requirement, action, limit, true);
	}

	Teleport(int moveCost, RSTile location, Requirement requirement, Action action, TeleportLimit limit, boolean requiresMembers) {
		setMoveCost(moveCost);
		this.location = location;
		this.requirement = requirement;
		this.action = action;
		this.teleportLimit = limit;
		this.requiresMembers = requiresMembers;
		this.canBeUsedInPvpWorlds = true;
	}

	Teleport(int movecost, TeleportScrolls scroll) {
		setMoveCost(movecost);
		this.location = scroll.getLocation();
		this.requirement = () -> scroll.canUse();
		this.action = () -> scroll.teleportTo(false);
		this.teleportLimit = TeleportConstants.LEVEL_20_WILDERNESS_LIMIT;
		this.requiresMembers = true;
		this.canBeUsedInPvpWorlds = true;
	}

	Teleport(Minigame minigame, RSTile location, String... chatOptions){
		this(minigame, location, null, chatOptions);
	}

	Teleport(Minigame minigame, RSTile location, boolean requiresMembers, String... chatOptions){
		this(minigame, location, null, requiresMembers, chatOptions);
	}
	Teleport(Minigame minigame, RSTile location, Requirement requirement, String... chatOptions){
		this(minigame, location, requirement, true, chatOptions);
	}
	Teleport(Minigame minigame, RSTile location, Requirement requirement, boolean requiresMembers, String... chatOptions){
		setMoveCost(150);
		this.location = location;
		if(requirement != null){
			this.requirement = () -> canUseMinigameTeleport() && requirement.satisfies();
		} else{
			this.requirement = Teleport::canUseMinigameTeleport;
		}
		this.action = () -> {
			if(NPCInteraction.isConversationWindowUp()){
				Walking.walkTo(Player.getPosition());//exit chat by walking to our tile
				WaitFor.milliseconds(200, 600);
			}
			if(!minigame.teleport()){
				return false;
			}
			if(chatOptions.length > 0){
				NPCInteraction.handleConversation(chatOptions);
			}
			return true;
		};
		this.teleportLimit = TeleportConstants.LEVEL_20_WILDERNESS_LIMIT;
		this.requiresMembers = requiresMembers;
		this.canBeUsedInPvpWorlds = false;
	}

	public int getMoveCost() {
		return (int) ScriptCache.get().getOrDefault(
				"DaxWalkerTeleport." + this.name() + ".moveCost",
				0);
	}

	public void setMoveCost(int cost) {
		ScriptCache.get().put(
				"DaxWalkerTeleport." + this.name() + ".moveCost",
				cost);
	}

	public RSTile getLocation() {
		return location;
	}

	public Requirement getRequirement() {
		return requirement;
	}

	public boolean trigger() {
		return this.action.trigger();
	}

	public boolean isAtTeleportSpot(RSTile tile) {
		return tile.distanceTo(location) < 10;
	}

	public static void setMoveCosts(int moveCost){
		Arrays.stream(values()).forEach(t -> t.setMoveCost(moveCost));
	}

	public static List<RSTile> getValidStartingRSTiles(boolean membersWorld, boolean pvpWorld, List<Teleport> blacklist) {
		return Arrays.stream(values())
				.filter(t -> {
					if((!membersWorld && t.requiresMembers) || (pvpWorld && !t.canBeUsedInPvpWorlds))
						return false;
					return !blacklist.contains(t) && t.teleportLimit.canCast() && t.requirement.satisfies();
				})
				.map(Teleport::getLocation)
				.collect(Collectors.toList());
	}

	private interface Action {
		boolean trigger();
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



	private static boolean teleportWithScrollInterface(Predicate<RSItem> itemFilter, String regex){
		ArrayList<RSItem> items = new ArrayList<>();
		items.addAll(Arrays.asList(Inventory.find(itemFilter)));
		items.addAll(Arrays.asList(Equipment.find(itemFilter)));

		if (items.size() == 0) {
			return false;
		}

		if(!Interfaces.isInterfaceSubstantiated(TeleportConstants.SCROLL_INTERFACE_MASTER)){
			RSItem teleportItem = items.get(0);
			if (!RSItemHelper.clickMatch(teleportItem, "(Rub|Teleport|" + regex + ")") ||
				!Timing.waitCondition(() -> Interfaces.isInterfaceSubstantiated(
					TeleportConstants.SCROLL_INTERFACE_MASTER),2500)) {
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
				Keyboard.typeString(General.stripFormatting(txt).substring(0,1));
				return true;
			}
		}
		return false;
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

	private static boolean hasBeenToZeah(){
		return RSVarBit.get(4897).getValue() > 0;
	}

	private static boolean canUseHomeTeleport(){
		return !Player.getRSPlayer().isInCombat() &&
				((long) Game.getSetting(892) * 60 * 1000) + (30 * 60 * 1000) < Timing.currentTimeMillis();
	}

	private static boolean canUseMinigameTeleport(){
		return !Player.getRSPlayer().isInCombat() &&
				((long) Game.getSetting(888) * 60 * 1000) + (20 * 60 * 1000) < Timing.currentTimeMillis();
	}
}
