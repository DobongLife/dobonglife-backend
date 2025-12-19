package com.umust.dobonglife.domain.auth.service;

import org.springframework.stereotype.Service;

@Service
public class UserService {
    public boolean isCourseRemoved(Long id1, Long id2) {
        return id1 == id2;
    }
}
