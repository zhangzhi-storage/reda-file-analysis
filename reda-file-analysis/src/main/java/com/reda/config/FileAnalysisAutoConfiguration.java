package com.reda.config;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(value = "file.analysis.password")
@EnableConfigurationProperties(FileAnalysisProperties.class)
public class FileAnalysisAutoConfiguration {
    @Autowired
    FileAnalysisProperties fileAnalysisProperties;


}
