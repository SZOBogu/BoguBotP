package aspects;

import bwapi.Game;
import bwapi.Player;
import bwapi.UnitType;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Aspect
@Component
public class LoggingAspect {
    private static final Logger logger = Logger.getLogger(LoggingAspect.class.getName());
    private Player player;
    private Game game;

    @Before("aspects.LoggingPointcuts.demandPlacedLog()")
    public void notifyAboutDemand(JoinPoint joinPoint){
        UnitType unitType = (UnitType)joinPoint.getArgs()[0];
        logger.info("DEMANDED: " + unitType +
                " | TIME: " + TimeUnit.SECONDS.toMinutes(game.elapsedTime()) + ":" + String.format("%02d", game.elapsedTime()%60) +
                " | SUPPLY: " + player.supplyUsed()/2 + "/" + player.supplyTotal()/2);
    }

    @Before("aspects.LoggingPointcuts.demandFulfilledLog()")
    public void notifyAboutFulfillingDemand(JoinPoint joinPoint){
        UnitType unitType = (UnitType)joinPoint.getArgs()[0];
        logger.info("FULFILLED DEMAND ON: " + unitType +
                " | TIME: " + TimeUnit.SECONDS.toMinutes(game.elapsedTime()) + ":" + String.format("%02d", game.elapsedTime()%60) +
                " | SUPPLY: " + player.supplyUsed()/2 + "/" + player.supplyTotal()/2);
    }

    @Before("aspects.LoggingPointcuts.militaryManagerDoesAnything()")
    public void notifyOnMilitaryAction(JoinPoint joinPoint){
        logger.info("MILITARY ACTION: " + joinPoint.getSignature());
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setGame(Game game) {
        this.game = game;
    }
}
