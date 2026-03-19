package com.umust.dobonglife.domain.user.application.port.out;

import com.umust.dobonglife.domain.user.domain.entity.User;

public interface SaveUserPort {

    User save(User user);

    User saveAndFlush(User user);

    void delete(User user);
}
