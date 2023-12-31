package org.example.message.open.open_opt;

public abstract class BGPOpenOptAbc {

    protected int paramType;
    protected int paramLen;
    protected int capabilityCode;
    protected int capabilityLen;

    public int getParamType() {
        return paramType;
    }

    public void setParamType(int paramType) {
        this.paramType = paramType;
    }

    public int getParamLen() {
        return paramLen;
    }

    public void setParamLen(int paramLen) {
        this.paramLen = paramLen;
    }

    public int getCapabilityCode() {
        return capabilityCode;
    }

    public void setCapabilityCode(int capabilityCode) {
        this.capabilityCode = capabilityCode;
    }

    public int getCapabilityLen() {
        return capabilityLen;
    }

    public void setCapabilityLen(int capabilityLen) {
        this.capabilityLen = capabilityLen;
    }
}
