package org.designer.tongrong_property_company_2nd.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Comment {

    private int id;

    @JsonProperty("product_name")
    private String productName;

    private String username;

    private String content;

    @JsonProperty("like_count")
    private int likeCount;

    @JsonProperty("create_time")
    private LocalDateTime createTime;
}
