package com.test.technical.msinventory.api.dto;

import java.math.BigDecimal;

public class ProductJsonApiResponse {

    private Data data;

    public Data getData() { return data; }
    public void setData(Data data) { this.data = data; }

    public static class Data {
        private String type;
        private String id;
        private Attributes attributes;

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public Attributes getAttributes() { return attributes; }
        public void setAttributes(Attributes attributes) { this.attributes = attributes; }
    }

    public static class Attributes {
        private String name;
        private BigDecimal price;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }
    }

}
