package com.appsdeveloperblog.app.ws.shared;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UtilsTest {
	
	@Autowired
	Utils utils;

	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	final void testGenerateUserId() {
		String userId = utils.generateAddressId(30);
		assertNotNull(userId);
		assertTrue(userId.length()==30);
	}

	@Test
	final void testHasTokenNotExpired() {
		String token=utils.generateEmailVerificationToken("asdadd");
		assertNotNull(token);
		boolean hasTokenExpired = Utils.hasTokenExpired(token);
		
		assertFalse(hasTokenExpired);
		
	}
	
	

}
