package managers;

import bwapi.Unit;

public interface IUnitManager extends IBroodWarManager{
    void add(Unit unit);
    void remove(Unit unit);
}
