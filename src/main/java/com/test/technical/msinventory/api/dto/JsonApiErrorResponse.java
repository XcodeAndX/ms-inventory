package com.test.technical.msinventory.api.dto;


import java.util.List;

public class JsonApiErrorResponse {

    private List<JsonApiError> errors;

    public JsonApiErrorResponse() {}

    public JsonApiErrorResponse(List<JsonApiError> errors) {
        this.errors = errors;
    }

    public List<JsonApiError> getErrors() { return errors; }
    public void setErrors(List<JsonApiError> errors) { this.errors = errors; }

}
