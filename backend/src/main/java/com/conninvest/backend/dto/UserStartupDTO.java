package com.conninvest.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserStartupDTO {
    private Long startupId;
    private String name;
    private String sector;
    private String stage;
    private String description;
    private String role;
}
