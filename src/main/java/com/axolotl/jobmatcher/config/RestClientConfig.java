package com.axolotl.jobmatcher.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
public class RestClientConfig {

    @Value("${ai.service.url:http://127.0.0.1:8000}")
    private String aiServiceUrl;

    @Value("${ai.service.key:random_api_key_for_testing}")
    private String apiKey;

    @Value("${ai.service.read-timeout:30s}")
    private Duration readTimeout;

    @Value("${ai.service.connect-timeout:5s}")
    private Duration connectTimeout;

    @Bean
    public RestClient fastApiRestClient() {
        HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1) // Force using http 1.1
                .connectTimeout(connectTimeout)
                .build();


        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(httpClient);
        factory.setReadTimeout(readTimeout);

        return RestClient.builder()
                .requestFactory(factory)
                .baseUrl(aiServiceUrl)
                .defaultHeader("X-API-KEY", apiKey)
                .defaultHeader("Accept", "application/json")
                .build();
    }
}