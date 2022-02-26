package helpers;

import bwapi.Race;
import bwapi.UnitType;
import enums.BuildOrderType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BuildOrder {
    private String name;
    private List<ProductionOrder> buildOrder;
    private List<Race> racesItShouldWorkAgainst;
    private BuildOrderType type;
    private int militaryGroupTarget;
    private UnitType unitToProduceConstantly;

    public BuildOrder(){
        this.name = "";
        this.buildOrder = new ArrayList<>();
        this.racesItShouldWorkAgainst = new ArrayList<>(Arrays.asList(Race.Protoss, Race.Terran, Race.Zerg, Race.Random, Race.Unknown));
        this.type = BuildOrderType.BALANCED;
        this.militaryGroupTarget = 24;
        this.unitToProduceConstantly = UnitType.Protoss_Zealot;
    }

    public BuildOrder(BuildOrderBuilder builder){
        this.name = builder.name;
        this.buildOrder = builder.buildOrder;
        this.racesItShouldWorkAgainst = builder.racesItShouldWorkAgainst;
        this.type = builder.type;
        this.militaryGroupTarget = builder.militaryGroupTarget;
        this.unitToProduceConstantly = builder.unitToProduceConstantly;
    }

    public static class BuildOrderBuilder{
        private String name;
        private List<Race> racesItShouldWorkAgainst;
        private List<ProductionOrder> buildOrder;
        private BuildOrderType type;
        private int militaryGroupTarget;
        private UnitType unitToProduceConstantly;

        public BuildOrderBuilder(List<ProductionOrder> buildOrder){
            this.name = "";
            this.buildOrder = buildOrder;
            this.racesItShouldWorkAgainst = new ArrayList<>(Arrays.asList(Race.Protoss, Race.Terran, Race.Zerg, Race.Random, Race.Unknown));
            this.type = BuildOrderType.BALANCED;
            this.militaryGroupTarget = 24;
            this.unitToProduceConstantly = UnitType.Protoss_Zealot;

        }
        public BuildOrderBuilder name(String name){
            this.name = name;
            return this;
        }
        public BuildOrderBuilder races(List<Race> races){
            this.racesItShouldWorkAgainst = races;
            return this;
        }
        public BuildOrderBuilder type(BuildOrderType type){
            this.type = type;
            return this;
        }
        public BuildOrderBuilder militaryGroupTarget(int militaryGroupTarget){
            this.militaryGroupTarget = militaryGroupTarget;
            return this;
        }
        public BuildOrderBuilder unitToProduceConstantly(UnitType unitToProduceConstantly){
            this.unitToProduceConstantly = unitToProduceConstantly;
            return this;
        }
        public BuildOrder build(){
            return new BuildOrder(this);
        }
    }

    public List<Race> getRacesItShouldWorkAgainst() {
        return racesItShouldWorkAgainst;
    }

    public void setRacesItShouldWorkAgainst(List<Race> racesItShouldWorkAgainst) {
        this.racesItShouldWorkAgainst = racesItShouldWorkAgainst;
    }

    public List<ProductionOrder> getBuildOrder() {
        return buildOrder;
    }

    public void setBuildOrder(List<ProductionOrder> buildOrder) {
        this.buildOrder = buildOrder;
    }

    public BuildOrderType getType() {
        return type;
    }

    public void setType(BuildOrderType type) {
        this.type = type;
    }

    public int getMilitaryGroupTarget() {
        return militaryGroupTarget;
    }

    public void setMilitaryGroupTarget(int militaryGroupTarget) {
        this.militaryGroupTarget = militaryGroupTarget;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UnitType getUnitToProduceConstantly() {
        return unitToProduceConstantly;
    }

    public void setUnitToProduceConstantly(UnitType unitToProduceConstantly) {
        this.unitToProduceConstantly = unitToProduceConstantly;
    }
}
