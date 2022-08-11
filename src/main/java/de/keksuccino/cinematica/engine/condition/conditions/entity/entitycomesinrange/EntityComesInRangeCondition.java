package de.keksuccino.cinematica.engine.condition.conditions.entity.entitycomesinrange;

import de.keksuccino.cinematica.engine.condition.Condition;
import de.keksuccino.cinematica.engine.condition.ConditionFactory;
import de.keksuccino.cinematica.utils.WorldUtils;
import de.keksuccino.konkrete.input.StringUtils;
import de.keksuccino.konkrete.math.MathUtils;
import de.keksuccino.konkrete.properties.PropertiesSection;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class EntityComesInRangeCondition extends Condition {

    protected boolean gotTriggered = false;

    public EntityComesInRangeCondition(@Nullable String identifier, ConditionFactory parent, PropertiesSection conditionMeta) {
        super(identifier, parent, conditionMeta);
    }

    @Override
    public boolean conditionsMet() {

        try {

            if ((Minecraft.getInstance().level != null) && (Minecraft.getInstance().player != null)) {

                String conType = this.conditionMeta.getEntryValue("entity_type");
                String conName = StringUtils.convertFormatCodes(this.conditionMeta.getEntryValue("entity_name"), "§", "&");
                String conRange = this.conditionMeta.getEntryValue("range");

                if ((conRange != null) && MathUtils.isInteger(conRange)) {
                    int radius = Integer.parseInt(conRange);
                    List<Entity> entitiesInRange = WorldUtils.getEntitiesAroundPlayer(radius);
                    List<String> typesInRange = new ArrayList<>();
                    List<String> namesInRange = new ArrayList<>();
                    for (Entity e : entitiesInRange) {
                        if (e != null) {
                            typesInRange.add(Registry.ENTITY_TYPE.getKey(e.getType()).toString());
                            namesInRange.add(StringUtils.convertFormatCodes(e.getDisplayName().getString(), "§", "&"));
                        }
                    }

                    boolean isType = false;
                    if (!entitiesInRange.isEmpty()) {
                        if ((conType == null) || conType.replace(" ", "").equals("")) {
                            isType = true;
                        } else if (typesInRange.contains(conType)) {
                            isType = true;
                        }
                    }
                    boolean isName = false;
                    if (!entitiesInRange.isEmpty()) {
                        if ((conName == null) || conName.equals("")) {
                            isName = true;
                        } else if (namesInRange.contains(conName)) {
                            isName = true;
                        }
                    }

                    if (isType && isName) {
                        if (!this.gotTriggered) {
                            this.gotTriggered = true;
                            return true;
                        }
                    } else {
                        this.gotTriggered = false;
                    }
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;

    }

}
