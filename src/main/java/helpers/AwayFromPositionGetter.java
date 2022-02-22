package helpers;

import bwapi.Position;
import bwem.BWMap;

public class AwayFromPositionGetter {
    private AwayFromPositionGetter() {}

    public static Position getPositionAwayFromCenter(BWMap map, Position from, int x, int y){
        //Right half of map
        if(from.x >= map.getCenter().x){
            //Top right quarter
            if(from.y >= map.getCenter().y){
                return new Position(from.x + x, from.y - y);
            }
            //bottom right quarter
            else{
                return new Position(from.x + x, from.y + y);
            }

        }
        //Left half of map
        else{
            //Top left quarter
            if(from.y >= map.getCenter().y){
                return new Position(from.x - x, from.y - y);
            }
            //Bot left quarter
            else{
                return new Position(from.x - x, from.y + y);
            }
        }
    }
}
