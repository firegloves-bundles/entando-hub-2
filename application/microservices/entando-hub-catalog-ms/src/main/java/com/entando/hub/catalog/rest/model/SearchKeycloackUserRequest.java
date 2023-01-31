/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.entando.hub.catalog.rest.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

/**
 * @author E.Santoboni
 */
@Getter
@Setter
public class SearchKeycloackUserRequest {
    
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    
    @JsonIgnore
    public Map<String, String> getParams() {
        Map<String, String> map = new HashMap<>();
        if (!StringUtils.isBlank(this.getEmail())) {
            map.put("email", this.getEmail());
        }
        if (!StringUtils.isBlank(this.getFirstName())) {
            map.put("firstName", this.getFirstName());
        }
        if (!StringUtils.isBlank(this.getLastName())) {
            map.put("lastName", this.getLastName());
        }
        if (!StringUtils.isBlank(this.getUsername())) {
            map.put("username", this.getUsername());
        }
        return map;
    }
    
}
