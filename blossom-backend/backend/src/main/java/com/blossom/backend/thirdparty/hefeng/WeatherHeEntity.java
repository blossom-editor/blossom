package com.blossom.backend.thirdparty.hefeng;

import lombok.Data;

/**
 * @author xzzz
 */
@Data
public class WeatherHeEntity {

    private String locationId;
    private String locationEName;
    private String locationCName;
    private String countryCode;
    private String countryEName;
    private String countryCName;
    private String adm1EName;
    private String adm1CName;
    private String adm2EName;
    private String adm2CName;
    private String lat;
    private String lng;
    private String adCode;

}
