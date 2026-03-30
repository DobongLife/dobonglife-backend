package com.umust.dobonglife.domain.exchange.infrastructure.jpa;

import com.umust.dobonglife.domain.exchange.domain.entity.ExchangeSaga;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExchangeSagaJpaRepository extends JpaRepository<ExchangeSaga, Long> {
}
