package de.keksuccino.cinematica.engine.condition.conditions.entity.entitywatch;

import de.keksuccino.cinematica.engine.condition.Condition;
import de.keksuccino.cinematica.engine.condition.ConditionFactory;
import de.keksuccino.cinematica.utils.WorldUtils;
import de.keksuccino.konkrete.input.StringUtils;
import de.keksuccino.konkrete.math.MathUtils;
import de.keksuccino.konkrete.properties.PropertiesSection;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class EntityWatchCondition extends Condition {

    public EntityWatchCondition(@Nullable String identifier, ConditionFactory parent, PropertiesSection conditionMeta) {
        super(identifier, parent, conditionMeta);
    }

    @Override
    public boolean conditionsMet() {

        try {

            if ((Minecraft.getInstance().world != null) && (Minecraft.getInstance().player != null)) {

                String conType = this.conditionMeta.getEntryValue("entity_type");
                String conName = StringUtils.convertFormatCodes(this.conditionMeta.getEntryValue("entity_name"), "§", "&");
                String conRange = this.conditionMeta.getEntryValue("range");
                String conOffset = this.conditionMeta.getEntryValue("fov_offset");
                if ((conOffset == null) || !MathUtils.isDouble(conOffset)) {
                    conOffset = "0";
                }

                if ((conRange != null) && MathUtils.isInteger(conRange)) {
                    double fovOffset = Double.parseDouble(conOffset);
                    int radius = Integer.parseInt(conRange);
                    List<Entity> spottedEntities = WorldUtils.getEntitiesInPlayerFOV(WorldUtils.getEntitiesAroundPlayer(radius), fovOffset);
                    List<String> spottedTypes = new ArrayList<>();
                    List<String> spottedNames = new ArrayList<>();
                    for (Entity e : spottedEntities) {
                        if (e != null) {
                            spottedTypes.add(e.getType().getRegistryName().toString());
                            spottedNames.add(StringUtils.convertFormatCodes(e.getDisplayName().getString(), "§", "&"));
                        }
                    }

                    boolean isType = false;
                    if (!spottedEntities.isEmpty()) {
                        if ((conType == null) || conType.replace(" ", "").equals("")) {
                            isType = true;
                        } else if (spottedTypes.contains(conType)) {
                            isType = true;
                        }
                    }
                    boolean isName = false;
                    if (!spottedEntities.isEmpty()) {
                        if ((conName == null) || conName.equals("")) {
                            isName = true;
                        } else if (spottedNames.contains(conName)) {
                            isName = true;
                        }
                    }

                    if (isType && isName) {
                         return true;
                    }
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;

    }

}
