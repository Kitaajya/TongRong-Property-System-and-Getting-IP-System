package org.designer.tongrong_property_company_2nd.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentMessage {

    private int id;

    private String receiver;

    private String sender;

    @JsonProperty("product_name")
    private String productName;

    @JsonProperty("comment_id")
    private int commentId;

    private String content;

    @JsonProperty("is_read")
    private int isRead;

    @JsonProperty("create_time")
    private LocalDateTime createTime;
}
