package com.wso2telco.gateway.requestRouter.model;

/**
 * Created by WSO2telco
 */
public class HeaderModel {

    int id;
    String domain;
    String urlPrefix;
    String header;
    String headerValue;
    HeaderModes mode;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getUrlPrefix() {
        return urlPrefix;
    }

    public void setUrlPrefix(String urlPrefix) {
        this.urlPrefix = urlPrefix;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getHeaderValue() {
        return headerValue;
    }

    public void setHeaderValue(String headerValue) {
        this.headerValue = headerValue;
    }

    public HeaderModes getMode() {
        return mode;
    }

    public void setMode(HeaderModes mode) {
        this.mode = mode;
    }
}
