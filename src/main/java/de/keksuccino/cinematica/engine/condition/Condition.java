package de.keksuccino.cinematica.engine.condition;

import de.keksuccino.cinematica.utils.MiscUtils;
import de.keksuccino.konkrete.properties.PropertiesSection;

import javax.annotation.Nullable;

public abstract class Condition {

    protected final String identifier;

    public final ConditionFactory parent;
    public PropertiesSection conditionMeta;

    public Condition(@Nullable String identifier, ConditionFactory parent, PropertiesSection conditionMeta) {
        if (identifier != null) {
            this.identifier = identifier;
        } else {
            this.identifier = MiscUtils.generateRandomUniqueId();
        }
        this.parent = parent;
        this.conditionMeta = conditionMeta;
    }

    /**
     * Used to check if the condition meta of this condition meets all conditions of the {@link ConditionFactory#getConditionContext()}.<br><br>
     */
    public abstract boolean conditionsMet();

    public String getIdentifier() {
        return this.identifier;
    }

    public static class SerializedCondition {

        public final String identifier;
        public final String factoryIdentifier;
        public PropertiesSection conditionMeta;

        public SerializedCondition(@Nullable String identifier, String factoryIdentifier) {
            if (identifier != null) {
                this.identifier = identifier;
            } else {
                this.identifier = MiscUtils.generateRandomUniqueId();
            }
            this.factoryIdentifier = factoryIdentifier;
        }

    }

}
