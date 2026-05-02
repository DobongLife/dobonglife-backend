package com.umust.dobonglife.global.client.content;

import com.umust.dobonglife.global.client.config.InternalRestClientFactory;
import com.umust.dobonglife.global.port.content.CoursePort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class ContentCourseClient implements CoursePort {

    private final RestClient restClient;

    public ContentCourseClient(@Value("${service.content.url}") String baseUrl) {
        this.restClient = InternalRestClientFactory.create(baseUrl, "content-service");
    }

    @Override
    public Map<String, Object> createCourse(Long userId, Map<String, Object> request) {
        return restClient.post()
                .uri("/internal/course?userId={userId}", userId)
                .body(request)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    @Override
    public Map<String, Object> updateCourse(Long userId, Long courseId, Map<String, Object> request) {
        return restClient.patch()
                .uri("/internal/course/{courseId}?userId={userId}", courseId, userId)
                .body(request)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    @Override
    public void deleteCourse(Long userId, Long courseId) {
        restClient.delete()
                .uri("/internal/course/{courseId}?userId={userId}", courseId, userId)
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public Map<String, Object> getMyCourses(Long userId, Long lastId, int size) {
        return restClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.path("/internal/course/my")
                            .queryParam("userId", userId)
                            .queryParam("size", size);
                    if (lastId != null) uriBuilder.queryParam("lastId", lastId);
                    return uriBuilder.build();
                })
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    @Override
    public Map<String, Object> getAllCourses(Long userId, String theme, Long lastId, int size) {
        return restClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.path("/internal/course")
                            .queryParam("userId", userId)
                            .queryParam("size", size);
                    if (theme != null) uriBuilder.queryParam("theme", theme);
                    if (lastId != null) uriBuilder.queryParam("lastId", lastId);
                    return uriBuilder.build();
                })
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    @Override
    public Map<String, Object> getCourseDetail(Long courseId, Long userId, Long lastId, int size) {
        return restClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.path("/internal/course/{courseId}")
                            .queryParam("userId", userId)
                            .queryParam("size", size);
                    if (lastId != null) uriBuilder.queryParam("lastId", lastId);
                    return uriBuilder.build(courseId);
                })
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }
}
