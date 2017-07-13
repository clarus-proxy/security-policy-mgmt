package eu.clarussecure.secpolmgmt;

import eu.clarussecure.datamodel.Policy;

public class CommandReturn {
    public final int returnValue;
    public final String returnInfo;
    public final Policy modifiedPolicy;

    public CommandReturn(int value, String info, Policy policy) {
        this.returnValue = value;
        this.returnInfo = info;
        this.modifiedPolicy = policy;
    }

    public int getReturnValue() {
        return this.returnValue;
    }

    public String getReturnInfo() {
        return this.returnInfo;
    }

    public Policy getModifiedPolicy() {
        return this.modifiedPolicy;
    }
}
