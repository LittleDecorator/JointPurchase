package com.acme.model.dto.sms;

/**
 * Created by nikolay on 07.05.17.
 */
public class LimitDto {

    private int code;
    private int leftCount;
    private int usedCount;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getLeftCount() {
        return leftCount;
    }

    public void setLeftCount(int leftCount) {
        this.leftCount = leftCount;
    }

    public int getUsedCount() {
        return usedCount;
    }

    public void setUsedCount(int usedCount) {
        this.usedCount = usedCount;
    }

    @Override
    public String toString() {
        return "LimitDto{" +
                "code=" + code +
                ", leftCount=" + leftCount +
                ", usedCount=" + usedCount +
                '}';
    }
}
