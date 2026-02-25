package com.umust.dobonglife.domain.user.infrastructure;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.umust.dobonglife.domain.user.domain.repository.custom.UserRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.umust.dobonglife.domain.user.domain.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    // return 업데이트된 행의 수
    @Override
    public long decreaseBalance(Long userId, Long amount){
        return queryFactory.update(user)
                .set(user.balance, user.balance.subtract(amount))
                .where(user.id.eq(userId), user.balance.goe(amount))
                .execute();
    }
}
