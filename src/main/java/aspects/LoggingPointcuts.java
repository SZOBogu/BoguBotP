package aspects;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class LoggingPointcuts {
    @Pointcut("execution(* managers.DemandManager.demandCreatingUnit(..))")
    public void demandPlacedLog() {}

    @Pointcut("execution(* managers.DemandManager.fulfillDemandCreatingUnit(..))")
    public void demandFulfilledLog() {}

    @Pointcut("execution(* managers.MilitaryManager.*(..))")
    public void militaryManagerDoesAnything() {}

    @Pointcut("execution(* managers.MilitaryManager.manage(..))")
    public void manageMilitary() {}

    @Pointcut("execution(* helpers.BaseInfoTracker.mark*(..))")
    public void markBase() {}

    @Pointcut("execution(* helpers.BaseInfoTracker.markBaseAsUnknown(..))")
    public void markBaseAsUnknown() {}

    @Pointcut("execution(* helpers.EnemyMilitaryInfoTracker.add(..))")
    public void recordEnemyUnit() {}

    @Pointcut("execution(* helpers.EnemyMilitaryInfoTracker.delete(..))")
    public void recordEnemyUnitDestruction() {}

    @Pointcut("execution(* managers.MilitaryManager.isScoutAvailable(..))")
    public void recordAskingForScout() {}

    @Pointcut("execution(* managers.MilitaryManager.getTextToWriteInGame(..))")
    public void recordGettingTextToWriteOnScreen() {}
}
