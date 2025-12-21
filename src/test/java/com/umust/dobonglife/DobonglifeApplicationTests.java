package com.umust.dobonglife;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class DobonglifeApplicationTests {

	@Disabled("컨텍스트 로딩 테스트 임시 비활성화")
	@Test
	void contextLoads() {
	}

}
