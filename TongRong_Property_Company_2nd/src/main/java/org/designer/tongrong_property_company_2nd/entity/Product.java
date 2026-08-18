package org.designer.tongrong_property_company_2nd.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Product {

    private int id;

    private String name;

    private double price;

    private String description;

    private String supplier;

    private String category;

    private int stock;

    private String status;

    @JsonProperty("image_url")
    private String imageUrl;

    @JsonProperty("create_time")
    private LocalDateTime createTime;

    @JsonProperty("update_time")
    private LocalDateTime updateTime;
}
