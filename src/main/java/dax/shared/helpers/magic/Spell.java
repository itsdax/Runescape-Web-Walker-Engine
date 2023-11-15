package dax.shared.helpers.magic;

import org.tribot.api.Timing;
import org.tribot.api2007.Game;
import org.tribot.api2007.Magic;
import org.tribot.api2007.Player;
import org.tribot.api2007.Skills;
import dax.shared.Pair;
import org.tribot.api2007.types.RSVarBit;


public enum Spell implements Validatable {

    LUMBRIDGE_HOME_TELEPORT(SpellBook.Type.STANDARD, 1, "Lumbridge Home Teleport"),
    ARCEUUS_HOME_TELEPORT(SpellBook.Type.ARCEUUS, 1, "Arceuus Home Teleport"),
    EDGEVILLE_HOME_TELEPORT(SpellBook.Type.ANCIENT, 1, "Edgeville Home Teleport"),
    LUNAR_HOME_TELEPORT(SpellBook.Type.LUNAR, 1, "Lunar Home Teleport"),
    VARROCK_TELEPORT    (SpellBook.Type.STANDARD, 25, "Varrock Teleport",    new Pair<>(1, RuneElement.LAW), new Pair<>(3, RuneElement.AIR),     new Pair<>(1, RuneElement.FIRE)),
    LUMBRIDGE_TELEPORT  (SpellBook.Type.STANDARD, 31, "Lumbridge Teleport",  new Pair<>(1, RuneElement.LAW), new Pair<>(3, RuneElement.AIR),     new Pair<>(1, RuneElement.EARTH)),
    FALADOR_TELEPORT    (SpellBook.Type.STANDARD, 37, "Falador Teleport",    new Pair<>(1, RuneElement.LAW), new Pair<>(3, RuneElement.AIR),     new Pair<>(1, RuneElement.WATER)),
    CAMELOT_TELEPORT    (SpellBook.Type.STANDARD, 45, "Camelot Teleport",    new Pair<>(1, RuneElement.LAW), new Pair<>(5, RuneElement.AIR)),
    ARDOUGNE_TELEPORT   (SpellBook.Type.STANDARD, 51, "Ardougne Teleport",   new Pair<>(2, RuneElement.LAW), new Pair<>(2, RuneElement.WATER)),
    KOUREND_TELEPORT	(SpellBook.Type.STANDARD, 69, "Kourend Castle Teleport",new Pair<>(2, RuneElement.LAW), new Pair<>(2, RuneElement.SOUL),new Pair<>(4, RuneElement.WATER), new Pair<>(5, RuneElement.FIRE)),

    ;

    private SpellBook.Type spellBookType;
    private int requiredLevel;
    private String spellName;
    private Pair<Integer, RuneElement>[] recipe;

    Spell(SpellBook.Type spellBookType, int level, String spellName, Pair<Integer, RuneElement>... recipe){
        this.spellBookType = spellBookType;
        this.requiredLevel = level;
        this.spellName = spellName;
        this.recipe = recipe;
    }

    public Pair<Integer, RuneElement>[] getRecipe(){
        return recipe;
    }

    public String getSpellName() {
        return spellName;
    }

    public boolean cast() {
        return canUse() && Magic.selectSpell(getSpellName());
    }

    @Override
    public boolean canUse(){
        if (SpellBook.getCurrentSpellBook() != spellBookType){
            return false;
        }
        if (requiredLevel > Skills.SKILLS.MAGIC.getCurrentLevel()){
            return false;
        }
        if (this == ARDOUGNE_TELEPORT && Game.getSetting(165) < 30){
            return false;
        }
        if(areTeleportsFiltered()){
            return false;
        }
        if(ordinal() <= 3 && !canUseHomeTeleport()){
            return false;
        }

        for (Pair<Integer, RuneElement> pair : recipe){
            int amountRequiredForSpell = pair.getKey();
            RuneElement runeElement = pair.getValue();
            if (runeElement.getCount() < amountRequiredForSpell){
                return false;
            }
        }
        return true;
    }

    public static boolean areTeleportsFiltered() {
        RSVarBit var = RSVarBit.get(6609);
        return var != null && var.getValue() == 1;
    }

    private static boolean canUseHomeTeleport(){
        return !Player.getRSPlayer().isInCombat() &&
                ((long) Game.getSetting(892) * 60 * 1000) + (30 * 60 * 1000) < Timing.currentTimeMillis();
    }

}
