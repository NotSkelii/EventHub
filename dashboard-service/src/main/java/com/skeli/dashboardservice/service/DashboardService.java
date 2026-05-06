package com.skeli.dashboardservice.service;

import com.skeli.dashboardservice.dto.AnalyticsEventDto;
import com.skeli.dashboardservice.dto.AuthResponseDto;
import com.skeli.dashboardservice.dto.EventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DashboardService {
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${services.auth}")
    private String authServiceUrl;

    @Value("${services.event}")
    private String eventServiceUrl;

    @Value("${services.analytics}")
    private String analyticsServiceUrl;

    public AuthResponseDto register(String username, String email, String password){
        String url = authServiceUrl + "/api/v1/auth/register";
        RegisterRequest registerRequest = new RegisterRequest(username, email, password);
        try{
            ResponseEntity<AuthResponseDto> response = restTemplate.postForEntity(
                    url, registerRequest, AuthResponseDto.class
            );
            return response.getBody();
        }catch(Exception e){
            log.error("Registration failed: {}", e.getMessage());
            return null;
        }

    }

    public AuthResponseDto login(String username, String password) {
        String url = authServiceUrl + "/api/v1/auth/login";
        LoginRequest loginRequest = new LoginRequest(username, password);

        try {
            ResponseEntity<AuthResponseDto> response = restTemplate.postForEntity(
                    url, loginRequest, AuthResponseDto.class
            );
            return response.getBody();
        } catch (Exception e) {
            log.error("Login failed: {}", e.getMessage());
            return null;
        }
    }

    public List<EventDto> getEvents(String token) {
        String url = eventServiceUrl + "/api/v1/events?size=100";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<PageResponse<EventDto>> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity,
                    new ParameterizedTypeReference<PageResponse<EventDto>>() {
                    }
            );
            return response.getBody() != null ? response.getBody().content() : Collections.emptyList();
        } catch (Exception e) {
            log.error("Failed to fetch events: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<AnalyticsEventDto> getAnalytics(String token) {
        String url = analyticsServiceUrl + "/api/v1/analytics/events";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<List<AnalyticsEventDto>> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity,
                    new ParameterizedTypeReference<List<AnalyticsEventDto>>() {
                    }
            );
            return response.getBody() != null ? response.getBody() : Collections.emptyList();
        } catch (Exception e) {
            log.error("Failed to fetch analytics: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    public long getAnalyticsCount() {
        String url = analyticsServiceUrl + "/api/v1/analytics/stats/count";

        try {
            ResponseEntity<Long> response = restTemplate.getForEntity(url, Long.class);
            return response.getBody() != null ? response.getBody() : 0L;
        }catch(Exception e){
            log.error("Failed to fetch analytics count: {}", e.getMessage());
            return 0L;
        }
    }

    record RegisterRequest(String username, String email, String password) {
    }

    record LoginRequest(String username, String password) {
    }

    record PageResponse<T>(List<T> content, int totalPages, long totalElements) {
    }
}

