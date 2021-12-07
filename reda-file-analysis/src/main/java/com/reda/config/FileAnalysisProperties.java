package com.reda.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("file.analysis")
public class FileAnalysisProperties {
    /**
     * 压缩包密码
     */
    private String password;


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}
