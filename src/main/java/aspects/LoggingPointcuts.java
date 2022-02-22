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
}
