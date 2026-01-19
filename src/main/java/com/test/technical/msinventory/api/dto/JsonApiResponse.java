package com.test.technical.msinventory.api.dto;

public class JsonApiResponse<T> {

    private T data;

    public JsonApiResponse() {}

    public JsonApiResponse(T data) {
        this.data = data;
    }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }

}
