package helpers;

import bwapi.Position;
import bwapi.TilePosition;
import bwapi.WalkPosition;
import bwem.Base;

public class PositionPrinter {
    private PositionPrinter(){}

    public static String toString(Base base){
        return "Base: " + getCoordinates(base.getLocation());
    }

    public static String toString(TilePosition tilePosition){
        return "TilePosition: " + getCoordinates(tilePosition);
    }

    public static String toString(WalkPosition walkPosition){
        return "WalkPosition: " + getCoordinates(walkPosition.toTilePosition());
    }

    public static String toString(Position position){
        return "Position: " + getCoordinates(position.toTilePosition());
    }

    private static String getCoordinates(TilePosition tilePosition){
        return " X: " + tilePosition.x + " Y: " + tilePosition.y;
    }
}
