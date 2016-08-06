/*
 * Copyright 2016 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.dynamicCities.settlements;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.dynamicCities.minimap.DistrictOverlay;
import org.terasology.dynamicCities.settlements.events.AddDistrictOverlayEvent;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.players.LocalPlayer;
import org.terasology.logic.players.MinimapSystem;
import org.terasology.network.ClientComponent;
import org.terasology.network.NetworkMode;
import org.terasology.network.NetworkSystem;
import org.terasology.registry.In;

import java.util.Iterator;

@RegisterSystem(RegisterMode.CLIENT)
public class DistrictFacetOverlaySystem extends BaseComponentSystem {


    @In
    private MinimapSystem minimapSystem;

    @In
    private LocalPlayer localPlayer;

    @In
    private EntityManager entityManager;

    @In
    private NetworkSystem networkSystem;

    private Logger logger = LoggerFactory.getLogger(DistrictFacetOverlaySystem.class);

    private EntityRef clientEntity;

    private boolean isOverlayAdded;

    @Override
    public void initialise() {
        if (networkSystem.getMode() == NetworkMode.CLIENT) {
            clientEntity = networkSystem.getServer().getClientEntity();
        }
    }
    @ReceiveEvent
    public void onAddOverlayEvent(AddDistrictOverlayEvent event, EntityRef entityRef) {
        if (networkSystem.getMode() == NetworkMode.CLIENT) {
            if (clientEntity.getComponent(ClientComponent.class).character.getId() == entityRef.getId() && !isOverlayAdded) {
                Iterator<EntityRef> entityRefs =  entityManager.getEntitiesWith(SettlementsCacheComponent.class).iterator();
                if (entityRefs.hasNext()) {
                    minimapSystem.addOverlay(new DistrictOverlay((entityRefs.next())));
                    isOverlayAdded = true;
                } else {
                    logger.error("No SettlementCache found! Unable to create district overlay");
                }
            }
        }
        if (networkSystem.getMode() == NetworkMode.DEDICATED_SERVER && !isOverlayAdded) {
            if (localPlayer.getCharacterEntity() == entityRef) {
                Iterator<EntityRef> entityRefs =  entityManager.getEntitiesWith(SettlementsCacheComponent.class).iterator();
                if (entityRefs.hasNext()) {
                    minimapSystem.addOverlay(new DistrictOverlay((entityRefs.next())));
                } else {
                    logger.error("No SettlementCache found! Unable to create district overlay");
                }
            }
        }
    }


}
