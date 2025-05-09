package com.aboc.payMyBuddy.exception;

import java.util.ArrayList;
import java.util.List;

public class ErrorEntity {
    List<String> errors;

    public ErrorEntity(List<String> errors) {
        this.errors = errors;
    }

    public ErrorEntity(String error) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(error);
        this.errors = arrayList;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
}
