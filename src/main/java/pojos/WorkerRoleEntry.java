package pojos;

import bwapi.Unit;
import enums.WorkerRole;

public class WorkerRoleEntry {
    private Unit unit;
    private WorkerRole workerRole;

    public WorkerRoleEntry(Unit unit) {
        this.unit = unit;
        this.workerRole = WorkerRole.IDLE;
    }

    public Unit getUnit() {
        return unit;
    }


    public WorkerRole getUnitState() {
        return workerRole;
    }

    public void setUnitState(WorkerRole workerRole) {
        this.workerRole = workerRole;
    }

    @Override
    public String toString() {
        return "UnitStateEntry{" +
                "unit=" + unit +
                ", unitState=" + workerRole +
                '}';
    }
}
