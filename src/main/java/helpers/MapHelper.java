package helpers;

import bwapi.Game;
import bwem.BWEM;
import bwem.BWMap;
import bwem.Base;

import java.util.List;

public class MapHelper {
    private final BWMap map;

    public MapHelper(Game game){
        BWEM bwem = new BWEM(game);
        bwem.initialize();
        bwem.setFailOnError(false);
        map = bwem.getMap();
    }

    public BWMap getMap() {
        return map;
    }
}
