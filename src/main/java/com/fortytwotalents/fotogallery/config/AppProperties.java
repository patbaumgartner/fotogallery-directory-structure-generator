package com.fortytwotalents.fotogallery.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public record AppProperties(String csvInputPath, String outputPath) {

	public AppProperties {
		if (csvInputPath == null || csvInputPath.isBlank()) {
			csvInputPath = "codes.csv";
		}
		if (outputPath == null || outputPath.isBlank()) {
			outputPath = ".";
		}
	}

}
