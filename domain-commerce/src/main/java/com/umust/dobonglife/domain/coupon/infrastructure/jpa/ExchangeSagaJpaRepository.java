package com.umust.dobonglife.domain.coupon.infrastructure.jpa;

import com.umust.dobonglife.domain.coupon.domain.entity.ExchangeSaga;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExchangeSagaJpaRepository extends JpaRepository<ExchangeSaga, Long> {
}
