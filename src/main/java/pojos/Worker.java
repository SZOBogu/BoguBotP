package pojos;

import bwapi.Unit;
import enums.WorkerRole;
import helpers.PositionPrinter;

import java.util.Objects;

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
/*
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Worker worker1 = (Worker) o;
        return Objects.equals(getWorker(), worker1.getWorker());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getWorker());
    }

 */
}
