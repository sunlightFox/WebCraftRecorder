package com.javafox.support;

import java.io.Serializable;

public class Action implements Serializable {
    private String actionType;
    private String locatorType;
    private String locatorValue;
    private String inputValue;

    public Action(String actionType, String locatorType, String locatorValue, String inputValue) {
        this.actionType = actionType;
        this.locatorType = locatorType;
        this.locatorValue = locatorValue;
        this.inputValue = inputValue;
    }

    public String getActionType() {
        return actionType;
    }

    public String getLocatorType() {
        return locatorType;
    }

    public String getLocatorValue() {
        return locatorValue;
    }

    public String getInputValue() {
        return inputValue;
    }

    @Override
    public String toString() {
        return actionType + "," + locatorType + "," + locatorValue + "," + inputValue;
    }
}