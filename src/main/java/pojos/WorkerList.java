package pojos;

import bwapi.Unit;
import enums.WorkerRole;

import java.util.ArrayList;
import java.util.List;

public class WorkerList {
    private final List<Worker> workerList;

    public WorkerList() {
        this.workerList = new ArrayList<>();
    }

    public Worker get(int index){
        return this.workerList.get(index);
    }

    public Worker get(Unit unit){
        for(Worker entry : this.workerList){
            if(entry.getWorker().equals(unit)){
                return entry;
            }
        }
        return null;
    }

    public void add(Worker worker){
        boolean isWorkerPresentAlready = false;

        for(Worker tempWorker: this.workerList){
            if (tempWorker.getWorker().equals(worker.getWorker())){
                isWorkerPresentAlready = true;
                break;
            }
        }
        if(!isWorkerPresentAlready)
            this.workerList.add(worker);
    }

    public void remove(Unit worker){
        List<Worker> workersToRemove = new ArrayList<>();

        for(Worker tempWorker : this.workerList){
            if(tempWorker.getWorker().equals(worker)){
                workersToRemove.add(tempWorker);
            }
        }

        this.workerList.removeAll(workersToRemove);
        System.out.println(workersToRemove.size() + " Worker successfully removed");
//        this.workerList.removeIf(entry -> entry.getWorker().equals(worker));
    }

    public List<Worker> getWorkerList() {
        return workerList;
    }

    public int size(){
        return this.workerList.size();
    }

    public List<Worker> getWorkersWithState(WorkerRole workerRole){
        List<Worker> entriesWithGivenState = new ArrayList<>();

        for(Worker entry : this.workerList){
            if(entry.getWorkerRole() == workerRole){
                entriesWithGivenState.add(entry);
            }
        }
        return entriesWithGivenState;
    }

    public int countWorkersWithState(WorkerRole state){
        return this.getWorkersWithState(state).size();
    }

    @Override
    public String toString() {
        return "WorkerList{" +
                "workerList=" + workerList +
                '}';
    }
}
