package dax.api_lib.models;

import com.allatori.annotations.DoNotRename;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import dax.api_lib.DaxWalker;
import org.tribot.api2007.*;
import org.tribot.api2007.types.RSVarBit;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@DoNotRename
public class PlayerDetails {

    public static PlayerDetails generate() {

        List<IntPair> inventory = Arrays.stream(Inventory.getAll())
                .map(rsItem -> new IntPair(rsItem.getID(), rsItem.getStack())).collect(Collectors.toList());

        List<IntPair> equipment = Arrays.stream(Equipment.getItems())
                .map(rsItem -> new IntPair(rsItem.getID(), rsItem.getStack())).collect(Collectors.toList());

        List<IntPair> settings = Stream.of(10, 11, 17, 32, 63, 68, 71, 101, 111, 116, 131, 144, 145, 150, 165, 176,
            179, 188, 212, 226, 273, 299, 302, 307, 314, 317, 328, 335, 347, 351, 365, 371, 387, 399, 425, 437, 440, 482, 622, 655, 671, 705, 794, 810,
            823, 869, 896, 964, 970, 1429, 1570, 1630, 1671, 1672)
                                       .map(value -> new IntPair(value, Game.getSetting(value))).distinct().collect(Collectors.toList());

        List<IntPair> varbit = Arrays.stream(new int[]{
            192,
            199,
            357,
            386,//smoke dungeon boss room
            571,
            1990,
            2310,
            2328,
            2448,
            3313,
            3314,
            3534,
            3618, //dream mentor
            3741,
            4163, //teleblock
            4493,
            4494, //fremmy elite diary status
            4538,
            4566,
            4885,
            4895,
            4897,
            5027,
            5087,
            5088,
            5089,
            5090,
            5800, //fossil island- museum camp bank has been built if this is 1
            5810,
            6071,
            6104,
            7255,
            7796,//forsaken tower
            7928, //kourend elite diary
            8122,
            9016,
            9459, //fremennik exiles
            9632, //Children of the sun,
            9649, //Twilights Promise
            9650, //varlamore
            9655, //Defender of varrock
            9819,
            9956, //colossal wyrm remains quetzal landing site
            9957, //outer fortis quetzal landing site
            10150, //we've paid 1m to Andras for free boat rides to Slepe if this is 1
            12063, //below ice mountain quest
            13719, //unlocked the 93 agility blood altar shortcut by mining the other side (78 mining req)
            13738, // temple of the eye
            13903, //Sleeping Giants quest
        })
                .mapToObj(value -> new IntPair(value, RSVarBit.get(value).getValue())).distinct().collect(
				        Collectors.toList());

        List<IntPair> playerPrefs = DaxWalker.getWalkerPreferences();

        return new PlayerDetails(
                Skills.getActualLevel(Skills.SKILLS.ATTACK),
                Skills.getActualLevel(Skills.SKILLS.DEFENCE),
                Skills.getActualLevel(Skills.SKILLS.STRENGTH),
                Skills.getActualLevel(Skills.SKILLS.HITPOINTS),
                Skills.getActualLevel(Skills.SKILLS.RANGED),
                Skills.getActualLevel(Skills.SKILLS.PRAYER),
                Skills.getActualLevel(Skills.SKILLS.MAGIC),
                Skills.getActualLevel(Skills.SKILLS.COOKING),
                Skills.getActualLevel(Skills.SKILLS.WOODCUTTING),
                Skills.getActualLevel(Skills.SKILLS.FLETCHING),
                Skills.getActualLevel(Skills.SKILLS.FISHING),
                Skills.getActualLevel(Skills.SKILLS.FIREMAKING),
                Skills.getActualLevel(Skills.SKILLS.CRAFTING),
                Skills.getActualLevel(Skills.SKILLS.SMITHING),
                Skills.getActualLevel(Skills.SKILLS.MINING),
                Skills.getActualLevel(Skills.SKILLS.HERBLORE),
                Skills.getCurrentLevel(Skills.SKILLS.AGILITY),
                Skills.getActualLevel(Skills.SKILLS.THIEVING),
                Skills.getActualLevel(Skills.SKILLS.SLAYER),
                Skills.getActualLevel(Skills.SKILLS.FARMING),
                Skills.getActualLevel(Skills.SKILLS.RUNECRAFTING),
                Skills.getActualLevel(Skills.SKILLS.HUNTER),
                Skills.getActualLevel(Skills.SKILLS.CONSTRUCTION),
                settings,
                varbit,
                WorldHopper.isCurrentWorldMembers().orElse(false),
                equipment,
                inventory,
                playerPrefs
        );
    }


    @DoNotRename
    private int attack;

    @DoNotRename
    private int defence;

    @DoNotRename
    private int strength;

    @DoNotRename
    private int hitpoints;

    @DoNotRename
    private int ranged;

    @DoNotRename
    private int prayer;

    @DoNotRename
    private int magic;

    @DoNotRename
    private int cooking;

    @DoNotRename
    private int woodcutting;

    @DoNotRename
    private int fletching;

    @DoNotRename
    private int fishing;

    @DoNotRename
    private int firemaking;

    @DoNotRename
    private int crafting;

    @DoNotRename
    private int smithing;

    @DoNotRename
    private int mining;

    @DoNotRename
    private int herblore;

    @DoNotRename
    private int agility;

    @DoNotRename
    private int thieving;

    @DoNotRename
    private int slayer;

    @DoNotRename
    private int farming;

    @DoNotRename
    private int runecrafting;

    @DoNotRename
    private int hunter;

    @DoNotRename
    private int construction;

    @DoNotRename
    private List<IntPair> setting;

    @DoNotRename
    private List<IntPair> varbit;

    @DoNotRename
    private boolean member;

    @DoNotRename
    private List<IntPair> equipment;

    @DoNotRename
    private List<IntPair> inventory;

    @DoNotRename
    private List<IntPair> walkerPreferences;

    public PlayerDetails() {

    }

    public PlayerDetails(int attack, int defence, int strength, int hitpoints, int ranged, int prayer, int magic, int cooking, int woodcutting, int fletching, int fishing, int firemaking, int crafting, int smithing, int mining, int herblore, int agility, int thieving, int slayer, int farming, int runecrafting, int hunter, int construction, List<IntPair> setting, List<IntPair> varbit, boolean member, List<IntPair> equipment, List<IntPair> inventory, List<IntPair> walkerPreferences) {
        this.attack = attack;
        this.defence = defence;
        this.strength = strength;
        this.hitpoints = hitpoints;
        this.ranged = ranged;
        this.prayer = prayer;
        this.magic = magic;
        this.cooking = cooking;
        this.woodcutting = woodcutting;
        this.fletching = fletching;
        this.fishing = fishing;
        this.firemaking = firemaking;
        this.crafting = crafting;
        this.smithing = smithing;
        this.mining = mining;
        this.herblore = herblore;
        this.agility = agility;
        this.thieving = thieving;
        this.slayer = slayer;
        this.farming = farming;
        this.runecrafting = runecrafting;
        this.hunter = hunter;
        this.construction = construction;
        this.setting = setting;
        this.varbit = varbit;
        this.member = member;
        this.equipment = equipment;
        this.inventory = inventory;
        this.walkerPreferences = walkerPreferences;
    }

    public int getAttack() {
        return attack;
    }

    public int getDefence() {
        return defence;
    }

    public int getStrength() {
        return strength;
    }

    public int getHitpoints() {
        return hitpoints;
    }

    public int getRanged() {
        return ranged;
    }

    public int getPrayer() {
        return prayer;
    }

    public int getMagic() {
        return magic;
    }

    public int getCooking() {
        return cooking;
    }

    public int getWoodcutting() {
        return woodcutting;
    }

    public int getFletching() {
        return fletching;
    }

    public int getFishing() {
        return fishing;
    }

    public int getFiremaking() {
        return firemaking;
    }

    public int getCrafting() {
        return crafting;
    }

    public int getSmithing() {
        return smithing;
    }

    public int getMining() {
        return mining;
    }

    public int getHerblore() {
        return herblore;
    }

    public int getAgility() {
        return agility;
    }

    public int getThieving() {
        return thieving;
    }

    public int getSlayer() {
        return slayer;
    }

    public int getFarming() {
        return farming;
    }

    public int getRunecrafting() {
        return runecrafting;
    }

    public int getHunter() {
        return hunter;
    }

    public int getConstruction() {
        return construction;
    }

    public List<IntPair> getSetting() {
        return setting;
    }

    public List<IntPair> getVarbit() {
        return varbit;
    }

    public boolean isMember() {
        return member;
    }

    public List<IntPair> getEquipment() {
        return equipment;
    }

    public List<IntPair> getInventory() {
        return inventory;
    }

    public List<IntPair> getWalkerPreferences() { return walkerPreferences; }

    public JsonElement toJson() {
        return new Gson().toJsonTree(this);
    }

}
