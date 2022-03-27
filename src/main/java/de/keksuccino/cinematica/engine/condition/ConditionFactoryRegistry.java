package de.keksuccino.cinematica.engine.condition;

import de.keksuccino.cinematica.Cinematica;
import de.keksuccino.konkrete.input.CharacterFilter;

import java.util.*;

public class ConditionFactoryRegistry {

    protected static Map<String, ConditionFactory> factories = new LinkedHashMap<>();

    public static void init() {

        ConditionFactoryHandler.init();

    }

    public static void registerFactory(ConditionFactory trigger) {
        if (trigger.getIdentifier() != null) {
            if (isValidFactoryIdentifier(trigger.getIdentifier())) {
                if (factories.containsKey(trigger.getIdentifier())) {
                    Cinematica.LOGGER.warn("[CINEMATICA] WARNING: There is already a factory registered with this identifier: " + trigger.getIdentifier());
                    Cinematica.LOGGER.warn("[CINEMATICA] WARNING: Overriding registered factory with new one!");
                }
                factories.put(trigger.getIdentifier(), trigger);
            } else {
                Cinematica.LOGGER.error("[CINEMATICA] ERROR: Unable to register factory: " + trigger.getIdentifier());
                Cinematica.LOGGER.error("[CINEMATICA] ERROR: Factory uses unallowed characters in its identifier! Only basic chars (a-z, 0-9, -_) are supported!");
            }
        } else {
            Cinematica.LOGGER.error("[CINEMATICA] ERROR: Unable to register trigger with NULLED identifier!");
        }
    }

    //TODO remove identifier restrictions (not needed anymore)
    public static boolean isValidFactoryIdentifier(String identifier) {
        CharacterFilter f = CharacterFilter.getBasicFilenameCharacterFilter();
        if (identifier.equals(f.filterForAllowedChars(identifier))) {
            return true;
        }
        return false;
    }

    public static void unregisterFactory(String identifier) {
        factories.remove(identifier);
    }

    public static ConditionFactory getFactory(String identifier) {
        return factories.get(identifier);
    }

    public static List<ConditionFactory> getFactories() {
        List<ConditionFactory> l = new ArrayList<>();
        l.addAll(factories.values());
        return l;
    }

    /** ONLY GET FROM THIS MAP! DON'T MODIFY IT IN ANY WAY! **/
    public static Map<String, ConditionFactory> getFactoriesMap() {
        return factories;
    }

}
