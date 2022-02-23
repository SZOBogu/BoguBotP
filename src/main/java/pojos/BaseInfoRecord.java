package pojos;

import bwem.Base;
import helpers.BaseState;

public class BaseInfoRecord {
    private Base base;
    private BaseState baseState;
    private int timeStamp;

    public BaseInfoRecord(Base base, BaseState baseState, int timeStamp) {
        this.base = base;
        this.baseState = baseState;
        this.timeStamp = timeStamp;
    }

    public Base getBase() {
        return base;
    }

    public void setBase(Base base) {
        this.base = base;
    }

    public BaseState getBaseState() {
        return baseState;
    }

    public void setBaseState(BaseState baseState) {
        this.baseState = baseState;
    }

    public int getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(int timeStamp) {
        this.timeStamp = timeStamp;
    }
}
