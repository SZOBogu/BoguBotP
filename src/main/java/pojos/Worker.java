package pojos;

import bwapi.Unit;
import enums.WorkerRole;
import helpers.PositionPrinter;

public class Worker {
    private Unit worker;
    private WorkerRole workerRole;

    public Worker(Unit worker) {
        this.worker = worker;
        this.workerRole = WorkerRole.IDLE;
    }

    public Unit getWorker() {
        return worker;
    }

    public void setWorker(Unit worker) {
        this.worker = worker;
    }

    public WorkerRole getWorkerRole() {
        return workerRole;
    }

    public void setWorkerRole(WorkerRole workerRole) {
        this.workerRole = workerRole;
    }

    @Override
    public String toString() {
        return "Worker{" +
                "worker=" + worker +
                ", workerRole=" + workerRole +
                ", worker position =" + PositionPrinter.toString(this.worker.getTilePosition()) +
                '}';
    }
}
