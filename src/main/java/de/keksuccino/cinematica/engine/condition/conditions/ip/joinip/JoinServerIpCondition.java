package de.keksuccino.cinematica.engine.condition.conditions.ip.joinip;

import de.keksuccino.cinematica.engine.condition.Condition;
import de.keksuccino.cinematica.engine.condition.ConditionFactory;
import de.keksuccino.konkrete.properties.PropertiesSection;
import net.minecraft.client.Minecraft;

import javax.annotation.Nullable;

public class JoinServerIpCondition extends Condition {

    protected boolean gotTriggered = false;

    public JoinServerIpCondition(@Nullable String identifier, ConditionFactory parent, PropertiesSection conditionMeta) {
        super(identifier, parent, conditionMeta);
    }

    @Override
    public boolean conditionsMet() {

        PropertiesSection conditionContext = this.parent.getConditionContext();

        if (conditionContext != null) {

            String curIp = conditionContext.getEntryValue("ip");

            String conIp = this.conditionMeta.getEntryValue("ip");

            if ((curIp != null) && curIp.equals(conIp)) {
                if (!this.gotTriggered) {
                    if ((Minecraft.getInstance().screen == null) && (Minecraft.getInstance().level != null) && (Minecraft.getInstance().player != null)) {
                        this.gotTriggered = true;
                        return true;
                    }
                }
            } else {
                this.gotTriggered = false;
            }

        } else {
            this.gotTriggered = false;
        }

        return false;

    }

}
