package aspects;

import bwapi.Game;
import bwapi.Player;
import bwapi.Unit;
import helpers.ProductionOrder;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
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
        ProductionOrder productionOrder = (ProductionOrder)joinPoint.getArgs()[0];
        logger.info("DEMANDED: " + productionOrder.getUnitType() +
                " | TIME: " + TimeUnit.SECONDS.toMinutes(game.elapsedTime()) + ":" + String.format("%02d", game.elapsedTime() % 60) +
                " | SUPPLY: " + player.supplyUsed()/2 + "/" + player.supplyTotal()/2);
    }

    @Before("aspects.LoggingPointcuts.demandFulfilledLog()")
    public void notifyAboutFulfillingDemand(JoinPoint joinPoint){
        ProductionOrder productionOrder = (ProductionOrder)joinPoint.getArgs()[0];
        logger.info("FULFILLED DEMAND ON: " + productionOrder.getUnitType() +
                " | TIME: " + TimeUnit.SECONDS.toMinutes(game.elapsedTime()) + ":" + String.format("%02d", game.elapsedTime() % 60) +
                " | SUPPLY: " + player.supplyUsed()/2 + "/" + player.supplyTotal()/2);
    }

    @Before("aspects.LoggingPointcuts.militaryManagerDoesAnything() && " +
            "!aspects.LoggingPointcuts.manageMilitary() && " +
            "!aspects.LoggingPointcuts.recordAskingForScout()")
    public void notifyOnMilitaryAction(JoinPoint joinPoint){
        logger.info("MILITARY ACTION: " + joinPoint.getSignature());
    }
    @Before("aspects.LoggingPointcuts.markBase() && !aspects.LoggingPointcuts.markBaseAsUnknown()")
    public void logBaseMarking(JoinPoint joinPoint){
        logger.info("Base scouted: " + joinPoint.getSignature());
    }

    @Before("aspects.LoggingPointcuts.recordEnemyUnit()")
    public void logEnemyUnit(JoinPoint joinPoint){
        Unit spottedUnit = (Unit)joinPoint.getArgs()[0];
        logger.info(spottedUnit.getType() + " is recorded");
    }

    @Before("aspects.LoggingPointcuts.recordEnemyUnitDestruction()")
    public void logEnemyUnitDestruction(JoinPoint joinPoint){
        Unit spottedUnit = (Unit)joinPoint.getArgs()[0];
        logger.info(spottedUnit.getType() + " got shit on and is removed from record");
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setGame(Game game) {
        this.game = game;
    }
}
