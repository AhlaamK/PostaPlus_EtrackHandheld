package webservice.JsonFuncClasses.JsonFuncClasses.JsonResponses;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import webservice.JsonFuncClasses.JsonFuncClasses.GetRunsheetVerifyDet;

/**
 * Created by ahlaam.kazi on 2/1/2018.
 */

public class GetRunsheetVerifyDetResponse {
    @JsonProperty("d")
    public List<GetRunsheetVerifyDet> GetRSTVERFYLDET;
}
