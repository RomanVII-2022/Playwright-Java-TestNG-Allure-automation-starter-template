package org.automation.config;

public class PlatformConfig {

    private String name;
    private WebConfig webConfig;
    private ApiConfig apiConfig;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setWebConfig(WebConfig webConfig){
        this.webConfig = webConfig;
    }

    public WebConfig getWebConfig(){
        return this.webConfig;
    }

    public void setApiConfig(ApiConfig apiConfig){
        this.apiConfig = apiConfig;
    }

    public ApiConfig getApiConfig(){
        return this.apiConfig;
    }
}
