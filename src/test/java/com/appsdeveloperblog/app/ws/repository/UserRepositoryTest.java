package com.appsdeveloperblog.app.ws.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.appsdeveloperblog.app.ws.entity.AddressEntity;
import com.appsdeveloperblog.app.ws.entity.UserEntity;
import com.appsdeveloperblog.app.ws.repositories.UserRepository;

import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UserRepositoryTest {

	/*
	 * This is actually integration test
	 */
	
	@Autowired
	UserRepository userRepository;
	
	static boolean recordsCreated = false;
	
	@BeforeEach
	void setUp() throws Exception {
		if (!recordsCreated)
			createRecords();
	}

	private void createRecords() {

		UserEntity userEntity = new UserEntity();
		userEntity.setFirstName("sanjin");
		userEntity.setLastName("dumancic");
		userEntity.setUserId("sd123");
		userEntity.setEncryptedPassword("123");
		userEntity.setEmail("sanjin.dumancic@yahoo.com");
		userEntity.setEmailVerificationStatus(true);
		
		AddressEntity shippingAddress = new AddressEntity();
		shippingAddress.setCity("Vancouver");
		shippingAddress.setCountry("Canada");
		shippingAddress.setStreetName("123 Street Name");
		shippingAddress.setPostalCode("ABCCBA");
		shippingAddress.setType("Shipping");
		shippingAddress.setAddressId("SDA123");
		
		
		List<AddressEntity> addresses = new ArrayList<>();
		addresses.add(shippingAddress);
		
		userEntity.setAddresses(addresses);
		
		userRepository.save(userEntity);
		
		
		UserEntity userEntity2 = new UserEntity();
		userEntity2.setFirstName("sanjin");
		userEntity2.setLastName("dumancic");
		userEntity2.setUserId("sd124");
		userEntity2.setEncryptedPassword("123");
		userEntity2.setEmail("sanjin.dumancic2@yahoo.com");
		userEntity2.setEmailVerificationStatus(true);
		
		AddressEntity shippingAddress2 = new AddressEntity();
		shippingAddress2.setCity("Vancouver");
		shippingAddress2.setCountry("Canada");
		shippingAddress2.setStreetName("123 Street Name");
		shippingAddress2.setPostalCode("ABCCBA");
		shippingAddress2.setType("Shipping");
		shippingAddress2.setAddressId("SDA124");
		
		
		List<AddressEntity> addresses2 = new ArrayList<>();
		addresses2.add(shippingAddress2);
		
		userEntity2.setAddresses(addresses2);
		
		userRepository.save(userEntity2);
		
		recordsCreated = true;
	}
	
	@Test
	final void testGetVerifiedUsers() {
		Pageable pageableRequest = PageRequest.of(0, 2);
		Page<UserEntity> page = userRepository.findAllUsersWithConfirmedEmailAddress(pageableRequest);
		assertNotNull(page);
		
		List<UserEntity> users = page.getContent();
		assertNotNull(users);
		assertTrue(users.size() ==2);
	}
	
	@Test
	final void testfindUserByFirstName() {
		String firstName = "sanjin";
		List<UserEntity> findUsersByFirstName = userRepository.findUsersByFirstName(firstName);
		assertNotNull(findUsersByFirstName);
		assertTrue(findUsersByFirstName.size()==2);
		
		UserEntity user = findUsersByFirstName.get(0);
		assertTrue(user.getFirstName().equals(firstName));
	}
	
	@Test
	final void testfindUserByLastName() {
		String lastName = "dumancic";
		Pageable pageableRequest = PageRequest.of(0, 2);
		Page<UserEntity> page  = userRepository.findUsersByLastName(lastName,pageableRequest);
		List<UserEntity> users = page.getContent();
		
		assertNotNull(users);
		assertTrue(users.size()==2);
		
		UserEntity user = users.get(0);
		assertTrue(user.getLastName().equals(lastName));
	}
	
	@Test
	final void testfindUserByKeyword() {
		String keyword = "dum";
		Pageable pageableRequest = PageRequest.of(0, 2);
		Page<UserEntity> page  = userRepository.findUsersByKeyword(keyword,pageableRequest);
		List<UserEntity> users = page.getContent();
		
		assertNotNull(users);
		assertTrue(users.size()==2);
		
		UserEntity user = users.get(0);
		assertTrue(user.getLastName().equals("dumancic"));
	}
	
	@Test
	final void testfindUserFirstNameAndLastNameByKeyword() {
		String keyword = "dum";
		Pageable pageableRequest = PageRequest.of(0, 2);
		Page<Object[]> page  = userRepository.findUserFirstNameAndLastNameByKeyword(keyword,pageableRequest);
		List<Object[]> users = page.getContent();
		
		assertNotNull(users);
		assertTrue(users.size()==2);
		
		Object[] user = users.get(0);
		String userFirstName = (String) user[0];
		String userLastName = (String) user[1];
		assertNotNull(userFirstName);
		assertNotNull(userLastName);
		assertTrue(userFirstName.equals("sanjin"));
		assertTrue(userLastName.equals("dumancic"));
	}

	@Test
	final void testUpdateUserEmailVerificationStatus() {
		userRepository.updateUserEmailVerificationStatus(false, "sd124");
		UserEntity storedDetails = userRepository.findByUserId("sd124");
		assertTrue(storedDetails.getEmailVerificationStatus() == false);
	}
	
	@Test
	final void testFindUserEntityByUserId() {
		String userId = "sd124";
		UserEntity userEntity = userRepository.findUserEntityByUserId(userId);
		assertNotNull(userEntity);
		assertTrue(userEntity.getUserId().equals(userId));
	}
	
	@Test
	final void testGetUserEntityFullNameById() {
		String userId = "sd124";
		List<Object[]> records = userRepository.getUserEntityFullNameById(userId);
		assertTrue(records.size()==1);
		Object[] user = records.get(0);
		String userFirstName = (String) user[0];
		String userLastName = (String) user[1];
		assertNotNull(userFirstName);
		assertNotNull(userLastName);
	}
	
	@Test
	final void testUpdateUserEntityEmailVerificationStatus() {
		userRepository.updateUserEntityEmailVerificationStatus(false, "sd124");
		UserEntity storedDetails = userRepository.findByUserId("sd124");
		assertTrue(storedDetails.getEmailVerificationStatus() == false);
	}

}

