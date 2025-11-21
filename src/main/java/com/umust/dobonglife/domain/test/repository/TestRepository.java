package com.umust.dobonglife.domain.test.repository;

import com.umust.dobonglife.domain.test.model.Test;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestRepository extends JpaRepository<Test, Long> {
}
