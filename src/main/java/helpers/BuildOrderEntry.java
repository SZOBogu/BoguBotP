package helpers;

import bwapi.UnitType;

public class BuildOrderEntry {
    private UnitType unitType;
    private boolean checked;

    public BuildOrderEntry(UnitType unitType) {
        this.unitType = unitType;
        this.checked = false;
    }

    public UnitType getUnitType() {
        return unitType;
    }

    public void setUnitType(UnitType unitType) {
        this.unitType = unitType;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @Override
    public String toString() {
        return "BuildOrderEntry{" +
                "unitType=" + unitType +
                ", checked=" + checked +
                '}';
    }
}
