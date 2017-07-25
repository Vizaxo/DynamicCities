/*
 * Copyright 2017 MovingBlocks
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
package org.terasology.dynamicCities.region;

import org.terasology.dynamicCities.facets.PopulationFacet;
import org.terasology.dynamicCities.settlements.SettlementConstants;
import org.terasology.math.TeraMath;
import org.terasology.math.geom.BaseVector2i;
import org.terasology.math.geom.Rect2i;
import org.terasology.utilities.procedural.Noise;
import org.terasology.utilities.procedural.WhiteNoise;
import org.terasology.world.generation.Border3D;
import org.terasology.world.generation.FacetProvider;
import org.terasology.world.generation.GeneratingRegion;
import org.terasology.world.generation.Produces;

@Produces(PopulationFacet.class)
public class PopulationProvider implements FacetProvider {

    private Noise sizeNoiseGen;

    @Override
    public void setSeed(long seed) {
        this.sizeNoiseGen = new WhiteNoise(seed ^ 0x2347928);
    }

    @Override
    public void process(GeneratingRegion region) {
        Border3D border = region.getBorderForFacet(PopulationFacet.class);
        PopulationFacet facet = new PopulationFacet(region.getRegion(), border);

        //TODO: shouldn't need to calculate every position
        Rect2i processRegion = facet.getWorldRegion();
        for (BaseVector2i position : processRegion.contents()) {
            facet.setWorld(position, TeraMath.fastAbs(Math.round(sizeNoiseGen.noise(position.x(), position.y()) *
                            (SettlementConstants.MAX_POPULATIONSIZE - SettlementConstants.MIN_POPULATIONSIZE)))
                    + SettlementConstants.MIN_POPULATIONSIZE);
        }

        region.setRegionFacet(PopulationFacet.class, facet);
    }
}
