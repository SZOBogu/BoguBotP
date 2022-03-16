package helpers;

import pojos.Dice;

public class BuildOrderChooser {
    //TODO: do something about inflexibility
    public static BuildOrder getBuildOrder(){
        //int roll = new Dice(5).roll();
        int roll = 5;
        switch(roll){
            case 1:
                return BuildOrderStaticFactory.nexus12vTZealotFirst();
            case 2:
                return BuildOrderStaticFactory.nexus12vTDragoonFirst();
            case 3:
                return BuildOrderStaticFactory.nexus14vT();
            case 4:
                return BuildOrderStaticFactory.nexus14vTBisu();
            default:
                return BuildOrderStaticFactory.oldTest();
        }
    }
}
