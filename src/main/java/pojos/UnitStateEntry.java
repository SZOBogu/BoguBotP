package pojos;

import bwapi.Unit;
import enums.UnitState;

public class UnitStateEntry {
    private Unit unit;
    private UnitState unitState;

    public UnitStateEntry(Unit unit) {
        this.unit = unit;
        this.unitState = UnitState.IDLE;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public UnitState getUnitState() {
        return unitState;
    }

    public void setUnitState(UnitState unitState) {
        this.unitState = unitState;
    }

    @Override
    public String toString() {
        return "UnitStateEntry{" +
                "unit=" + unit +
                ", unitState=" + unitState +
                '}';
    }
}
