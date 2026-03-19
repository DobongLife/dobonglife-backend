package com.umust.dobonglife.global.port.content;

import com.umust.dobonglife.global.common.response.CursorResponse;

import java.util.Map;

public interface CoursePort {
    Map<String, Object> createCourse(Long userId, Map<String, Object> request);
    Map<String, Object> updateCourse(Long userId, Long courseId, Map<String, Object> request);
    void deleteCourse(Long userId, Long courseId);
    Map<String, Object> getMyCourses(Long userId, Long lastId, int size);
    CursorResponse<Map<String, Object>> getAllCourses(Long userId, String theme, Long lastId, int size);
    Map<String, Object> getCourseDetail(Long courseId, Long userId, Long lastId, int size);
}
