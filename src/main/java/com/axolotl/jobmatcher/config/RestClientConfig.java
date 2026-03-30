package com.axolotl.jobmatcher.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient fastApiRestClient() {
        HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1) // Force using http 1.1
                .connectTimeout(Duration.ofSeconds(5))
                .build();


        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(httpClient);

        return RestClient.builder()
                .requestFactory(factory)
                .baseUrl("http://127.0.0.1:8000")
                .defaultHeader("Accept", "application/json")
                .build();
    }
}