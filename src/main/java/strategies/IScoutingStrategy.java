package strategies;

import bwapi.Game;
import bwapi.Unit;
import helpers.BaseInfoTracker;

public interface IScoutingStrategy {
    void scout(Game game, BaseInfoTracker baseInfoTracker, Unit scout);
    void isScoutingFinished(Game game, BaseInfoTracker baseInfoTracker);
}
