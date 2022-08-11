package de.keksuccino.cinematica.engine.condition.conditions;

import de.keksuccino.cinematica.engine.condition.ConditionFactoryRegistry;
import de.keksuccino.cinematica.engine.condition.conditions.area.enterarea.EnterAreaConditionFactory;
import de.keksuccino.cinematica.engine.condition.conditions.area.isarea.IsAreaConditionFactory;
import de.keksuccino.cinematica.engine.condition.conditions.biome.enterbiome.EnterBiomeConditionFactory;
import de.keksuccino.cinematica.engine.condition.conditions.biome.isbiome.IsBiomeConditionFactory;
import de.keksuccino.cinematica.engine.condition.conditions.block.standblock.StandBlockConditionFactory;
import de.keksuccino.cinematica.engine.condition.conditions.block.stepblock.StepBlockConditionFactory;
import de.keksuccino.cinematica.engine.condition.conditions.chat.receivechat.ReceiveChatConditionFactory;
import de.keksuccino.cinematica.engine.condition.conditions.dimension.enterdimension.EnterDimensionConditionFactory;
import de.keksuccino.cinematica.engine.condition.conditions.dimension.isdimension.IsDimensionConditionFactory;
import de.keksuccino.cinematica.engine.condition.conditions.entity.entitycomesinrange.EntityComesInRangeConditionFactory;
import de.keksuccino.cinematica.engine.condition.conditions.entity.entitydiedinrange.EntityDiedInRangeConditionFactory;
import de.keksuccino.cinematica.engine.condition.conditions.entity.entityisinrange.EntityIsInRangeConditionFactory;
import de.keksuccino.cinematica.engine.condition.conditions.entity.entityrightclick.EntityRightClickConditionFactory;
import de.keksuccino.cinematica.engine.condition.conditions.entity.entityspot.EntitySpotConditionFactory;
import de.keksuccino.cinematica.engine.condition.conditions.entity.entitywatch.EntityWatchConditionFactory;
import de.keksuccino.cinematica.engine.condition.conditions.ip.joinip.JoinServerIpConditionFactory;
import de.keksuccino.cinematica.engine.condition.conditions.item.dropitem.DropItemConditionFactory;
import de.keksuccino.cinematica.engine.condition.conditions.item.foodeaten.FoodEatenConditionFactory;
import de.keksuccino.cinematica.engine.condition.conditions.item.placeblock.PlaceBlockConditionFactory;
import de.keksuccino.cinematica.engine.condition.conditions.item.rightclickitem.RightClickItemConditionFactory;
import de.keksuccino.cinematica.engine.condition.conditions.multiplayer.MultiplayerConditionFactory;
import de.keksuccino.cinematica.engine.condition.conditions.item.addtoinventory.AddItemToInventoryConditionFactory;
import de.keksuccino.cinematica.engine.condition.conditions.ip.isip.IsServerIpConditionFactory;
import de.keksuccino.cinematica.engine.condition.conditions.singleplayer.SingleplayerConditionFactory;
import de.keksuccino.cinematica.engine.condition.conditions.tablist.becomestab.BecomesTabListConditionFactory;
import de.keksuccino.cinematica.engine.condition.conditions.tablist.istab.IsTabListConditionFactory;

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
        ConditionFactoryRegistry.registerFactory(new PlaceBlockConditionFactory());

        ConditionFactoryRegistry.registerFactory(new JoinServerIpConditionFactory());
        ConditionFactoryRegistry.registerFactory(new IsServerIpConditionFactory());

        ConditionFactoryRegistry.registerFactory(new BecomesTabListConditionFactory());
        ConditionFactoryRegistry.registerFactory(new IsTabListConditionFactory());

        //TODO try to improve this + reimplement it
//        ConditionFactoryRegistry.registerFactory(new KillEntityConditionFactory());

        ConditionFactoryRegistry.registerFactory(new MultiplayerConditionFactory());
        ConditionFactoryRegistry.registerFactory(new SingleplayerConditionFactory());

        ConditionFactoryRegistry.registerFactory(new EntityComesInRangeConditionFactory());
        ConditionFactoryRegistry.registerFactory(new EntityIsInRangeConditionFactory());
        ConditionFactoryRegistry.registerFactory(new EntitySpotConditionFactory());
        ConditionFactoryRegistry.registerFactory(new EntityWatchConditionFactory());
        ConditionFactoryRegistry.registerFactory(new EntityDiedInRangeConditionFactory());
        ConditionFactoryRegistry.registerFactory(new EntityRightClickConditionFactory());

        ConditionFactoryRegistry.registerFactory(new AddItemToInventoryConditionFactory());
        ConditionFactoryRegistry.registerFactory(new DropItemConditionFactory());
        ConditionFactoryRegistry.registerFactory(new FoodEatenConditionFactory());
        ConditionFactoryRegistry.registerFactory(new RightClickItemConditionFactory());

        ConditionFactoryRegistry.registerFactory(new ReceiveChatConditionFactory());

    }

}
