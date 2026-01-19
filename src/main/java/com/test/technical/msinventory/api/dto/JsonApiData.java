package com.test.technical.msinventory.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

public class JsonApiData<T> {

    @NotBlank
    private String type;

    private String id;

    @Valid
    private T attributes;

    public JsonApiData() {}

    public JsonApiData(String type, String id, T attributes) {
        this.type = type;
        this.id = id;
        this.attributes = attributes;
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public T getAttributes() { return attributes; }
    public void setAttributes(T attributes) { this.attributes = attributes; }

}
