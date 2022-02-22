package helpers;

import bwapi.*;
import bwem.BWEM;
import bwem.BWMap;
import bwem.Base;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

public class MapHelper {
    private final BWEM bwem;
    private Game game;
    private Unit mainNexus;

    public MapHelper(Game game){
        this.bwem = new BWEM(game);
        bwem.initialize();
        bwem.setFailOnError(false);
    }

    public BWMap getMap() {
        return this.bwem.getMap();
    }

    public void setMainNexus(Unit mainNexus) {
        this.mainNexus = mainNexus;
    }

    public Base getMainBase(){
        return this.getListOfBases().get(0);
    }

    public List<Base> getListOfBases(){
        return this.getBasesClosestToTilePosition(this.mainNexus.getTilePosition());
    }

    public Base getBaseClosestToTilePosition(TilePosition tilePosition){
        return this.getBasesClosestToTilePosition(tilePosition).get(0);
    }

    public List<Base> getBasesClosestToTilePosition(TilePosition tilePosition) {
        List<Base> bases = this.getMap().getBases();
        Map<Base, Integer> baseDistanceMap = new HashMap<>();

        for (Base tempBase : bases) {
            int tempDistance = tilePosition.getApproxDistance(tempBase.getLocation());
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

        List<Base> sortedBases = Arrays.stream(allegedlySortedMap.keySet().toArray()).map(i -> (Base) i).collect(Collectors.toList());
        return sortedBases;
    }

    public void setGame(Game game) {
        this.game = game;
    }
}
