package org.designer.tongrong_property_company_2nd.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {

    private int id;

    private String username;

    private String email;

    @JsonProperty("password_hash")
    private String passwordHash;

    private String salt;

    @JsonProperty("full_name")
    private String fullName;

    private String phone;

    private int status;

    @JsonProperty("is_ordinary_user")
    private int isOrdinaryUser;

    private String role;

    @JsonProperty("last_login_ip")
    private String lastLoginIp;

    @JsonProperty("last_login_time")
    private LocalDateTime lastLoginTime;

    @JsonProperty("login_count")
    private int loginCount;
}
