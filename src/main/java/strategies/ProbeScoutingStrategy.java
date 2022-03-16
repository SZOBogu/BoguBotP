package strategies;

import bwapi.Game;
import bwapi.Unit;
import bwem.Base;
import helpers.BaseInfoTracker;
import helpers.BaseState;

import java.util.List;
import java.util.stream.Collectors;

public class ProbeScoutingStrategy implements IScoutingStrategy{
    @Override
    public void scout(Game game, BaseInfoTracker baseInfoTracker, Unit scout) {
        Base nextBase = baseInfoTracker.getClosestBaseWithState(scout.getTilePosition(), BaseState.UNKNOWN);

        if(nextBase != null) {
            if (!scout.isMoving() && !scout.isStuck() || scout.isIdle()) {
                if (!game.isVisible(nextBase.getLocation())) {
                    scout.move(nextBase.getCenter());
                }
                else {
                    if (baseInfoTracker.checkBaseState(nextBase) != BaseState.MINE) {
                        List<Unit> buildingsNearScout = game.getUnitsInRadius(scout.getPosition(), 200).stream().filter(o -> o.getType().isBuilding()).collect(Collectors.toList());
                        List<Unit> mineBuildingsNearScout = buildingsNearScout.stream().filter(b -> game.self().getUnits().contains(b)).collect(Collectors.toList());
                        List<Unit> enemyBuildingsNearScout = buildingsNearScout.stream().filter(b -> game.enemy().getUnits().contains(b)).collect(Collectors.toList());

                        if(!buildingsNearScout.isEmpty() && !enemyBuildingsNearScout.isEmpty())
                            baseInfoTracker.markBaseAsEnemy(nextBase);
                        else if(!buildingsNearScout.isEmpty() && !mineBuildingsNearScout.isEmpty())
                            baseInfoTracker.markBaseAsMine(nextBase);
                        else
                            baseInfoTracker.markBaseAsNeutral(nextBase);

                    }
                    List<Base> unknownBases = baseInfoTracker.getClosestBasesWithState(scout.getTilePosition(), BaseState.UNKNOWN);
                    List<Base> enemyBases = baseInfoTracker.getClosestBasesWithState(scout.getTilePosition(), BaseState.ENEMY);
                    if(unknownBases.size() == 1 && enemyBases.size() < 1){
                        baseInfoTracker.markBaseAsEnemy(unknownBases.get(0));
                    }
                }
            }
            else if (scout.isStuck()) {
                scout = null;
            }
        }
        else
            baseInfoTracker.markAllNeutralBasesAsUnknown();
    }

    @Override
    public void isScoutingFinished(Game game, BaseInfoTracker baseInfoTracker) {

    }
}
