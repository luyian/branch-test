package com.example.enmu;

public enum AccountState {
    NORMAL(1, "正常"),
    LOCKED(0, "锁定");
    private Integer value;
    private String desc;

    AccountState(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public Integer getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }

    public static AccountState getByValue(Integer value) {
        for (AccountState state : AccountState.values()) {
            if (state.getValue().equals(value)) {
                return state;
            }
        }
        return null;
    }
}
