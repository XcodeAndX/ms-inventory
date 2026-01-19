package com.test.technical.msinventory.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class JsonApiRequest<T> {

    @NotNull
    @Valid
    private JsonApiData<T> data;

    public JsonApiRequest() {}

    public JsonApiRequest(JsonApiData<T> data) {
        this.data = data;
    }

    public JsonApiData<T> getData() {
        return data;
    }

    public void setData(JsonApiData<T> data) {
        this.data = data;
    }
}
