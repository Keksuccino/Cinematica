package de.keksuccino.cinematica.engine.condition.conditions;

import de.keksuccino.cinematica.engine.condition.ConditionFactoryRegistry;
import de.keksuccino.cinematica.engine.condition.conditions.area.enterarea.EnterAreaConditionFactory;
import de.keksuccino.cinematica.engine.condition.conditions.area.isarea.IsAreaConditionFactory;
import de.keksuccino.cinematica.engine.condition.conditions.dimension.enterdimension.EnterDimensionConditionFactory;
import de.keksuccino.cinematica.engine.condition.conditions.dimension.isdimension.IsDimensionConditionFactory;

public class ConditionFactories {

    public static void registerAll() {

        ConditionFactoryRegistry.registerFactory(new EnterAreaConditionFactory());
        ConditionFactoryRegistry.registerFactory(new IsAreaConditionFactory());

        ConditionFactoryRegistry.registerFactory(new EnterDimensionConditionFactory());
        ConditionFactoryRegistry.registerFactory(new IsDimensionConditionFactory());

    }

}
