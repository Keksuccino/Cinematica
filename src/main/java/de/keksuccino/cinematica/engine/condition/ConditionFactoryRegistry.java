package de.keksuccino.cinematica.engine.condition;

import de.keksuccino.konkrete.input.CharacterFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class ConditionFactoryRegistry {

    private static final Logger LOGGER = LogManager.getLogger("cinematica/ConditionFactoryRegistry");

    protected static Map<String, ConditionFactory> factories = new LinkedHashMap<>();

    public static void init() {

        ConditionFactoryHandler.init();

    }

    public static void registerFactory(ConditionFactory trigger) {
        if (trigger.getIdentifier() != null) {
            if (isValidFactoryIdentifier(trigger.getIdentifier())) {
                if (factories.containsKey(trigger.getIdentifier())) {
                    LOGGER.warn("WARNING: There is already a factory registered with this identifier: " + trigger.getIdentifier());
                    LOGGER.warn("WARNING: Overriding registered factory with new one!");
                }
                factories.put(trigger.getIdentifier(), trigger);
            } else {
                LOGGER.error("ERROR: Unable to register factory: " + trigger.getIdentifier());
                LOGGER.error("ERROR: Factory uses unallowed characters in its identifier! Only basic chars (a-z, 0-9, -_) are supported!");
            }
        } else {
            LOGGER.error("ERROR: Unable to register trigger with NULLED identifier!");
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
