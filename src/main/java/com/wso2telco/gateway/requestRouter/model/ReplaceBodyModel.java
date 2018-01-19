package com.wso2telco.gateway.requestRouter.model;

/**
 * Created by WSO2telco
 */
public class ReplaceBodyModel {

    int id;
    String urlKey;
    String jsonPath;
    String find;
    String replace;
    Boolean needURLDecodeRest;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrlKey() {
        return urlKey;
    }

    public void setUrlKey(String urlKey) {
        this.urlKey = urlKey;
    }

    public String getJsonPath() {
        return jsonPath;
    }

    public void setJsonPath(String jsonPath) {
        this.jsonPath = jsonPath;
    }

    public String getFind() {
        return find;
    }

    public void setFind(String find) {
        this.find = find;
    }

    public String getReplace() {
        return replace;
    }

    public void setReplace(String replace) {
        this.replace = replace;
    }

    public Boolean getNeedURLDecodeRest() {
        return needURLDecodeRest;
    }

    public void setNeedURLDecodeRest(Boolean needURLDecodeRest) {
        this.needURLDecodeRest = needURLDecodeRest;
    }
}
