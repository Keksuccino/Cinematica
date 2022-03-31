package de.keksuccino.cinematica.engine.condition.conditions;

import de.keksuccino.cinematica.engine.condition.ConditionFactoryRegistry;
import de.keksuccino.cinematica.engine.condition.conditions.area.enterarea.EnterAreaConditionFactory;
import de.keksuccino.cinematica.engine.condition.conditions.area.isarea.IsAreaConditionFactory;
import de.keksuccino.cinematica.engine.condition.conditions.biome.enterbiome.EnterBiomeConditionFactory;
import de.keksuccino.cinematica.engine.condition.conditions.biome.isbiome.IsBiomeConditionFactory;
import de.keksuccino.cinematica.engine.condition.conditions.block.standblock.StandBlockConditionFactory;
import de.keksuccino.cinematica.engine.condition.conditions.block.stepblock.StepBlockConditionFactory;
import de.keksuccino.cinematica.engine.condition.conditions.dimension.enterdimension.EnterDimensionConditionFactory;
import de.keksuccino.cinematica.engine.condition.conditions.dimension.isdimension.IsDimensionConditionFactory;
import de.keksuccino.cinematica.engine.condition.conditions.entitydied.EntityDiedConditionFactory;
import de.keksuccino.cinematica.engine.condition.conditions.ip.joinip.JoinServerIpConditionFactory;
import de.keksuccino.cinematica.engine.condition.conditions.multiplayer.MultiplayerConditionFactory;
import de.keksuccino.cinematica.engine.condition.conditions.addtoinventory.AddToInventoryConditionFactory;
import de.keksuccino.cinematica.engine.condition.conditions.ip.isip.IsServerIpConditionFactory;
import de.keksuccino.cinematica.engine.condition.conditions.singleplayer.SingleplayerConditionFactory;

public class ConditionFactories {

    public static void registerAll() {

        ConditionFactoryRegistry.registerFactory(new EnterAreaConditionFactory());
        ConditionFactoryRegistry.registerFactory(new IsAreaConditionFactory());

        ConditionFactoryRegistry.registerFactory(new EnterDimensionConditionFactory());
        ConditionFactoryRegistry.registerFactory(new IsDimensionConditionFactory());

        ConditionFactoryRegistry.registerFactory(new EnterBiomeConditionFactory());
        ConditionFactoryRegistry.registerFactory(new IsBiomeConditionFactory());

        ConditionFactoryRegistry.registerFactory(new StepBlockConditionFactory());
        ConditionFactoryRegistry.registerFactory(new StandBlockConditionFactory());

        ConditionFactoryRegistry.registerFactory(new JoinServerIpConditionFactory());
        ConditionFactoryRegistry.registerFactory(new IsServerIpConditionFactory());

        //TODO try to improve this + reimplement it
//        ConditionFactoryRegistry.registerFactory(new KillEntityConditionFactory());

        ConditionFactoryRegistry.registerFactory(new MultiplayerConditionFactory());
        ConditionFactoryRegistry.registerFactory(new SingleplayerConditionFactory());

        ConditionFactoryRegistry.registerFactory(new EntityDiedConditionFactory());

        ConditionFactoryRegistry.registerFactory(new AddToInventoryConditionFactory());

    }

}
