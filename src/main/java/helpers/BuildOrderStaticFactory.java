package helpers;

import bwapi.Race;
import bwapi.UnitType;

import java.util.ArrayList;
import java.util.Arrays;

public class BuildOrderStaticFactory {
    //Source of build orderes: https://liquipedia.net/starcraft/Category:Protoss_Build_Orders

    /*
        8 - Pylon
        12 - Nexus
        13 - Gateway
        13 - Assimilator
        15 - Cybernetics Core
        16 or 17 - Gateway
     */

    //######################### P v T builds ####################################
    //%%%%%%% Fast Expand builds %%%%%%%%%%%%%
    public static BuildOrder nexus12vTDragoonFirst(){
        ArrayList<ProductionOrder> buildOrderList = new ArrayList<>(Arrays.asList(
                ProductionOrderFactory.createProbeOrder(), ProductionOrderFactory.createProbeOrder(),
                ProductionOrderFactory.createProbeOrder(), ProductionOrderFactory.createProbeOrder(),
                new ProductionOrder.ProductionOrderBuilder(UnitType.Protoss_Pylon).populationMark(16).build(),
                new ProductionOrder.ProductionOrderBuilder(UnitType.Protoss_Nexus).populationMark(24).build(),
                new ProductionOrder.ProductionOrderBuilder(UnitType.Protoss_Nexus).populationMark(24).build(),
                new ProductionOrder.ProductionOrderBuilder(UnitType.Protoss_Gateway).populationMark(26).build(),
                new ProductionOrder.ProductionOrderBuilder(UnitType.Protoss_Assimilator).populationMark(26).build(),
                new ProductionOrder.ProductionOrderBuilder(UnitType.Protoss_Cybernetics_Core).populationMark(30).build(),
                new ProductionOrder.ProductionOrderBuilder(UnitType.Protoss_Gateway).populationMark(32).build()
        ));
        BuildOrder buildOrder = new BuildOrder.BuildOrderBuilder(buildOrderList)
                .name("Nexus 12 vs. Terran, Dragoon First variant")
                .races(new ArrayList<>(Arrays.asList(Race.Terran)))
                .unitToProduceConstantly(UnitType.Protoss_Dragoon)
                .build();
        return buildOrder;
    }
    /*
    8 - Pylon
    12 - Nexus
    14 - Gateway
    15 - Assimilator
    15 - Zealot
    17 - Cybernetics Core
    17 - Gateway
     */
    public static BuildOrder nexus12vTZealotFirst(){
        ArrayList<ProductionOrder> buildOrderList = new ArrayList<>(Arrays.asList(
                ProductionOrderFactory.createProbeOrder(), ProductionOrderFactory.createProbeOrder(),
                ProductionOrderFactory.createProbeOrder(), ProductionOrderFactory.createProbeOrder(),
                new ProductionOrder.ProductionOrderBuilder(UnitType.Protoss_Pylon).populationMark(16).build(),
                new ProductionOrder.ProductionOrderBuilder(UnitType.Protoss_Nexus).populationMark(24).build(),
                new ProductionOrder.ProductionOrderBuilder(UnitType.Protoss_Nexus).populationMark(24).build(),
                new ProductionOrder.ProductionOrderBuilder(UnitType.Protoss_Gateway).populationMark(28).build(),
                new ProductionOrder.ProductionOrderBuilder(UnitType.Protoss_Assimilator).populationMark(30).build(),
                new ProductionOrder.ProductionOrderBuilder(UnitType.Protoss_Zealot).populationMark(30).build(),
                new ProductionOrder.ProductionOrderBuilder(UnitType.Protoss_Cybernetics_Core).populationMark(34).build(),
                new ProductionOrder.ProductionOrderBuilder(UnitType.Protoss_Gateway).populationMark(34).build()
        ));
        BuildOrder buildOrder = new BuildOrder.BuildOrderBuilder(buildOrderList)
                .name("Nexus 12 vs. Terran, Zealot First variant")
                .races(new ArrayList<>(Arrays.asList(Race.Terran)))
                .unitToProduceConstantly(UnitType.Protoss_Dragoon)
                .build();
        return buildOrder;
    }

    public static BuildOrder nexus14vT(){
        ArrayList<ProductionOrder> buildOrderList = new ArrayList<>(Arrays.asList(
                ProductionOrderFactory.createProbeOrder(), ProductionOrderFactory.createProbeOrder(),
                ProductionOrderFactory.createProbeOrder(), ProductionOrderFactory.createProbeOrder(),
                new ProductionOrder.ProductionOrderBuilder(UnitType.Protoss_Pylon).populationMark(16).build(),
                new ProductionOrder.ProductionOrderBuilder(UnitType.Protoss_Nexus).populationMark(26).build(),
                new ProductionOrder.ProductionOrderBuilder(UnitType.Protoss_Nexus).populationMark(26).build(),
                new ProductionOrder.ProductionOrderBuilder(UnitType.Protoss_Gateway).populationMark(28).build(),
                new ProductionOrder.ProductionOrderBuilder(UnitType.Protoss_Assimilator).populationMark(30).build(),
                new ProductionOrder.ProductionOrderBuilder(UnitType.Protoss_Cybernetics_Core).populationMark(34).build(),
                new ProductionOrder.ProductionOrderBuilder(UnitType.Protoss_Gateway).populationMark(34).build(),
                new ProductionOrder.ProductionOrderBuilder(UnitType.Protoss_Zealot).populationMark(34).build(),
                new ProductionOrder.ProductionOrderBuilder(UnitType.Protoss_Pylon).populationMark(42).build(),
                new ProductionOrder.ProductionOrderBuilder(UnitType.Protoss_Dragoon).populationMark(42).build(),
                new ProductionOrder.ProductionOrderBuilder(UnitType.Protoss_Dragoon).populationMark(42).build(),    //goon range should be here
                new ProductionOrder.ProductionOrderBuilder(UnitType.Protoss_Pylon).populationMark(52).build(),
                new ProductionOrder.ProductionOrderBuilder(UnitType.Protoss_Dragoon).populationMark(52).build(),
                new ProductionOrder.ProductionOrderBuilder(UnitType.Protoss_Dragoon).populationMark(52).build(),
                new ProductionOrder.ProductionOrderBuilder(UnitType.Protoss_Pylon).populationMark(66).build(),
                new ProductionOrder.ProductionOrderBuilder(UnitType.Protoss_Dragoon).populationMark(70).build(),
                new ProductionOrder.ProductionOrderBuilder(UnitType.Protoss_Dragoon).populationMark(70).build()
                ));
        BuildOrder buildOrder = new BuildOrder.BuildOrderBuilder(buildOrderList)
                .name("Nexus 14 vs. Terran")
                .races(new ArrayList<>(Arrays.asList(Race.Terran)))
                .unitToProduceConstantly(UnitType.Protoss_Dragoon)
                .build();
        return buildOrder;
    }

    public static BuildOrder nexus14vTBisu(){
        ArrayList<ProductionOrder> buildOrderList = new ArrayList<>(Arrays.asList(
                ProductionOrderFactory.createProbeOrder(), ProductionOrderFactory.createProbeOrder(),
                ProductionOrderFactory.createProbeOrder(), ProductionOrderFactory.createProbeOrder(),
                new ProductionOrder.ProductionOrderBuilder(UnitType.Protoss_Pylon).populationMark(16).build(),
                new ProductionOrder.ProductionOrderBuilder(UnitType.Protoss_Nexus).populationMark(26).build(),
                new ProductionOrder.ProductionOrderBuilder(UnitType.Protoss_Nexus).populationMark(26).build(),
                new ProductionOrder.ProductionOrderBuilder(UnitType.Protoss_Gateway).populationMark(26).build(),
                new ProductionOrder.ProductionOrderBuilder(UnitType.Protoss_Assimilator).populationMark(28).build(),
                new ProductionOrder.ProductionOrderBuilder(UnitType.Protoss_Cybernetics_Core).populationMark(34).build(),//goon range should be on 52
                new ProductionOrder.ProductionOrderBuilder(UnitType.Protoss_Gateway).populationMark(34).build(),
                new ProductionOrder.ProductionOrderBuilder(UnitType.Protoss_Nexus).populationMark(36).build(),          //no exact population mark
                new ProductionOrder.ProductionOrderBuilder(UnitType.Protoss_Pylon).populationMark(52).build(),
                new ProductionOrder.ProductionOrderBuilder(UnitType.Protoss_Pylon).populationMark(62).build(),
                new ProductionOrder.ProductionOrderBuilder(UnitType.Protoss_Assimilator).populationMark(64).build(),
                new ProductionOrder.ProductionOrderBuilder(UnitType.Protoss_Robotics_Facility).populationMark(72).build(),    //goon range should be here
                new ProductionOrder.ProductionOrderBuilder(UnitType.Protoss_Pylon).populationMark(76).build(),
                new ProductionOrder.ProductionOrderBuilder(UnitType.Protoss_Pylon).populationMark(90).build(),
                new ProductionOrder.ProductionOrderBuilder(UnitType.Protoss_Robotics_Support_Bay).populationMark(100).build(),
                new ProductionOrder.ProductionOrderBuilder(UnitType.Protoss_Reaver).populationMark(120).build(),
                new ProductionOrder.ProductionOrderBuilder(UnitType.Protoss_Reaver).populationMark(120).build()
        ));
        BuildOrder buildOrder = new BuildOrder.BuildOrderBuilder(buildOrderList)
                .name("Nexus 14 vs. Terran, Bisu transition variant")
                .races(new ArrayList<>(Arrays.asList(Race.Terran)))
                .unitToProduceConstantly(UnitType.Protoss_Dragoon)
                .build();
        return buildOrder;
    }
    //%%%%%%%%%%%% Delayed fast expand builds %%%%%%%%%%%%%%%%%%%%%%%%%%%%

    //%%%%%%%%%%% General Openings %%%%%%%%%%%%%%%%%%%%%

    //######################### P v P builds ####################################

    //######################### P v Z builds ####################################


    //######################### universal builds ####################################


    public static BuildOrder oldTest(){
         ArrayList<ProductionOrder> buildOrderList = new ArrayList<>(Arrays.asList(
                ProductionOrderFactory.createProbeOrder(), ProductionOrderFactory.createProbeOrder(),
                ProductionOrderFactory.createProbeOrder(), ProductionOrderFactory.createProbeOrder(),
                new ProductionOrder.ProductionOrderBuilder(UnitType.Protoss_Pylon).populationMark(16).build(),
                new ProductionOrder.ProductionOrderBuilder(UnitType.Protoss_Gateway).populationMark(20).build(),
                new ProductionOrder.ProductionOrderBuilder(UnitType.Protoss_Assimilator).populationMark(22).build(),
                new ProductionOrder.ProductionOrderBuilder(UnitType.Protoss_Zealot).populationMark(24).build(),
                new ProductionOrder.ProductionOrderBuilder(UnitType.Protoss_Gateway).populationMark(30).build(),
                new ProductionOrder.ProductionOrderBuilder(UnitType.Protoss_Pylon).populationMark(32).build(),
                new ProductionOrder.ProductionOrderBuilder(UnitType.Protoss_Pylon).populationMark(38).build(),
                new ProductionOrder.ProductionOrderBuilder(UnitType.Protoss_Forge).populationMark(42).build(),
                new ProductionOrder.ProductionOrderBuilder(UnitType.Protoss_Pylon).populationMark(46).build()
        ));
         BuildOrder buildOrder = new BuildOrder.BuildOrderBuilder(buildOrderList).name("Test suboptimal speedlots").build();
         return buildOrder;
    }
}
