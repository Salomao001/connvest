package com.conninvest.backend.dto;

import com.conninvest.backend.model.Startup;
import com.conninvest.backend.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class SearchResultDTO {
    private List<User> users;
    private List<Startup> startups;
}
