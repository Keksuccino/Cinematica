package de.keksuccino.cinematica.engine.condition.conditions;

import de.keksuccino.cinematica.engine.condition.ConditionFactoryRegistry;
import de.keksuccino.cinematica.engine.condition.conditions.area.enterarea.EnterAreaConditionFactory;
import de.keksuccino.cinematica.engine.condition.conditions.area.isarea.IsAreaConditionFactory;
import de.keksuccino.cinematica.engine.condition.conditions.dimension.enterdimension.EnterDimensionConditionFactory;
import de.keksuccino.cinematica.engine.condition.conditions.dimension.isdimension.IsDimensionConditionFactory;
import de.keksuccino.cinematica.engine.condition.conditions.entitydied.EntityDiedConditionFactory;
import de.keksuccino.cinematica.engine.condition.conditions.killentity.KillEntityConditionFactory;
import de.keksuccino.cinematica.engine.condition.conditions.multiplayer.MultiplayerConditionFactory;
import de.keksuccino.cinematica.engine.condition.conditions.serverip.ServerIpConditionFactory;
import de.keksuccino.cinematica.engine.condition.conditions.singleplayer.SingleplayerConditionFactory;

public class ConditionFactories {

    public static void registerAll() {

        ConditionFactoryRegistry.registerFactory(new EnterAreaConditionFactory());
        ConditionFactoryRegistry.registerFactory(new IsAreaConditionFactory());

        ConditionFactoryRegistry.registerFactory(new EnterDimensionConditionFactory());
        ConditionFactoryRegistry.registerFactory(new IsDimensionConditionFactory());

        //TODO try to improve this + reimplement it
//        ConditionFactoryRegistry.registerFactory(new KillEntityConditionFactory());

        ConditionFactoryRegistry.registerFactory(new ServerIpConditionFactory());

        ConditionFactoryRegistry.registerFactory(new MultiplayerConditionFactory());
        ConditionFactoryRegistry.registerFactory(new SingleplayerConditionFactory());

        ConditionFactoryRegistry.registerFactory(new EntityDiedConditionFactory());

    }

}
