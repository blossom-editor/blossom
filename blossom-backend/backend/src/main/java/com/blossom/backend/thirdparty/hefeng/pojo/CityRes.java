package com.blossom.backend.thirdparty.hefeng.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

/**
 * 城市信息
 */
@Data
public class CityRes {

    private String code;

    private List<Location> location;

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Location {
        String name;
        String id;
    }

}
