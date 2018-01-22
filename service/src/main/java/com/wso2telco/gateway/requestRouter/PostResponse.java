package com.wso2telco.gateway.requestRouter;

import java.util.List;
import java.util.Map;

/**
 * Created by WSO2telco
 */

public class PostResponse {
    String response;
    Map<String,List<String>> headers;

    public PostResponse(){
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public Map<String,List<String>> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String,List<String>> headers) {
        this.headers = headers;
    }
}
