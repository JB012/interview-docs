package com.interviewdocs.server;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.context.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;

@SpringBootTest
class ServerApplicationTests {

    @Autowired
    private ApplicationContext context;

	@Test
	void contextLoads() {
		assertThat(context).isNotNull();
	}

}
