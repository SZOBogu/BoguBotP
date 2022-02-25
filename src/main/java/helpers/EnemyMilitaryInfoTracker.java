package helpers;

import bwapi.Unit;
import exceptions.StarcraftException;
import pojos.EnemyUnitRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EnemyMilitaryInfoTracker {
    private static final List<EnemyUnitRecord> enemyUnitRecordList = new ArrayList<>();

    private EnemyMilitaryInfoTracker() {}

    private static boolean containsRecordForUnit(Unit unit){
        List<EnemyUnitRecord> records = enemyUnitRecordList.stream().filter(r -> r.getUnit().equals(unit)).collect(Collectors.toList());
        return !records.isEmpty();
    }

    private static EnemyUnitRecord getRecord(Unit unit){
        List<EnemyUnitRecord> records = enemyUnitRecordList.stream().filter(r -> r.getUnit().equals(unit)).collect(Collectors.toList());
        if(records.size() > 1){
            throw new StarcraftException("Redundant records: " + records.size());
        }
        if(containsRecordForUnit(unit))
            return records.get(0);
        else
            return new EnemyUnitRecord();
    }

    public static void add(EnemyUnitRecord record){
        try {
            if (containsRecordForUnit(record.getUnit())) {
                EnemyUnitRecord enemyUnitRecord = getRecord(record.getUnit());
                enemyUnitRecord.setLastRecordedPosition(record.getLastRecordedPosition());
                enemyUnitRecord.setTimestamp(record.getTimestamp());
            }
            else{
                enemyUnitRecordList.add(record);
            }
        }
        catch(StarcraftException ex){
            System.out.println(ex.getMessage());
        }
    }

    public static void delete(EnemyUnitRecord record){
        try{
            if (containsRecordForUnit(record.getUnit())) {
                EnemyUnitRecord recordToDelete = getRecord(record.getUnit());
                enemyUnitRecordList.remove(recordToDelete);
            }
        }
        catch(StarcraftException ex){
            System.out.println(ex.getMessage());
            EnemyUnitRecord recordToDelete = getRecord(record.getUnit());
            enemyUnitRecordList.remove(recordToDelete);
        }
    }
}
