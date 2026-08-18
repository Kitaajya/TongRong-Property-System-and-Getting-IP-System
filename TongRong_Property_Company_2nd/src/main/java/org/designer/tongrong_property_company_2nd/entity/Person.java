package org.designer.tongrong_property_company_2nd.entity;

import lombok.Data;

@Data
public class Person {

    @com.fasterxml.jackson.annotation.JsonProperty("card_no")
    private String cardNo;

    @com.fasterxml.jackson.annotation.JsonProperty("real_name")
    private String realName;
}
