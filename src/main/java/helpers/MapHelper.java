package helpers;

import bwapi.Game;
import bwapi.Unit;
import bwapi.UnitType;
import bwem.BWEM;
import bwem.BWMap;
import bwem.Base;

import java.util.*;
import java.util.stream.Collectors;

public class MapHelper {
    private final BWMap map;
    private Unit mainNexus;

    public MapHelper(Game game){
        BWEM bwem = new BWEM(game);
        bwem.initialize();
        bwem.setFailOnError(false);
        map = bwem.getMap();
    }

    public BWMap getMap() {
        return map;
    }

    public void setMainNexus(Unit mainNexus) {
        this.mainNexus = mainNexus;
    }

    public Base getMainBase(){
        return this.getListOfBases().get(0);
    }

    public List<Base> getListOfBases(){
        List<Base> bases = this.map.getBases();
        Map<Base, Integer> baseDistanceMap = new HashMap<>();

        for (Base tempBase : bases) {
            int tempDistance = this.mainNexus.getTilePosition().getApproxDistance(tempBase.getLocation());
            baseDistanceMap.put(tempBase, tempDistance);
        }

        Comparator<Base> valueComparator = new Comparator<Base>() {
            @Override
            public int compare(Base o1, Base o2) {
                int comparison = baseDistanceMap.get(o1).compareTo(baseDistanceMap.get(o2));
                if (comparison == 0)
                    return 1;
                else
                    return comparison;
            }
        };

        Map<Base, Integer> allegedlySortedMap = new TreeMap<>(valueComparator);
        allegedlySortedMap.putAll(baseDistanceMap);

        return Arrays.stream(allegedlySortedMap.keySet().toArray()).map(i -> (Base)i).collect(Collectors.toList());
    }
}
