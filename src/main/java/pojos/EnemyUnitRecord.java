package pojos;

import bwapi.Position;
import bwapi.Unit;

public class EnemyUnitRecord {
    private Unit unit;
    private int timestamp;
    private Position lastRecordedPosition;

    public EnemyUnitRecord() {
    }

    public EnemyUnitRecord(Unit unit, int timestamp, Position lastRecorded) {
        this.unit = unit;
        this.timestamp = timestamp;
        this.lastRecordedPosition = lastRecorded;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public Position getLastRecordedPosition() {
        return lastRecordedPosition;
    }

    public void setLastRecordedPosition(Position lastRecordedPosition) {
        this.lastRecordedPosition = lastRecordedPosition;
    }
}
