package org.designer.tongrong_property_company_2nd.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Worker {

    private String id;

    private String name;

    private String department;

    private String gender;

    private String work;
}
