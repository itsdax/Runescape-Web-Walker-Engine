package scripts.webwalker_logic.teleport_logic.magic;

import org.tribot.api2007.Magic;
import scripts.webwalker_logic.shared.Pair;
import scripts.webwalker_logic.teleport_logic.Validatable;


public enum Spell implements Validatable{

    VARROCK_TELEPORT ("Varrock Teleport", new Pair<>(1, RuneElement.LAW), new Pair<>(3, RuneElement.AIR), new Pair<>(1, RuneElement.FIRE)),
    LUMBRIDGE_TELEPORT ("Lumbridge Teleport", new Pair<>(1, RuneElement.LAW), new Pair<>(3, RuneElement.AIR), new Pair<>(1, RuneElement.EARTH)),
    FALADOR_TELEPORT ("Falador Teleport", new Pair<>(1, RuneElement.LAW), new Pair<>(3, RuneElement.AIR), new Pair<>(1, RuneElement.WATER)),
    CAMELOT_TELEPORT ("Camelot Teleport", new Pair<>(1, RuneElement.LAW), new Pair<>(5, RuneElement.AIR)),
    ARDOUGNE_TELEPORT ("Ardougne Teleport", new Pair<>(2, RuneElement.LAW), new Pair<>(3, RuneElement.AIR), new Pair<>(2, RuneElement.WATER)),
    ;

    private String spellName;
    private Pair<Integer, RuneElement>[] recipe;

    Spell(String spellName, Pair<Integer, RuneElement>... recipe){
        this.recipe = recipe;
    }

    public Pair<Integer, RuneElement>[] getRecipe(){
        return recipe;
    }

    @Override
    public boolean canUse(){
        for (Pair<Integer, RuneElement> pair : recipe){
            int amount = pair.getKey();
            RuneElement runeElement = pair.getValue();
            if (runeElement.getCount() < amount){
                return false;
            }
        }
        return true;
    }

    public String getSpellName() {
        return spellName;
    }

    public boolean cast() {
        return canUse() && Magic.selectSpell(getSpellName());
    }

}
