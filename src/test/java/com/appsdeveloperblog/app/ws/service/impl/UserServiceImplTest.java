package com.appsdeveloperblog.app.ws.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.appsdeveloperblog.app.ws.entity.AddressEntity;
import com.appsdeveloperblog.app.ws.entity.UserEntity;
import com.appsdeveloperblog.app.ws.exceptions.UserServiceException;
import com.appsdeveloperblog.app.ws.repositories.UserRepository;
import com.appsdeveloperblog.app.ws.shared.AmazonSES;
import com.appsdeveloperblog.app.ws.shared.Utils;
import com.appsdeveloperblog.app.ws.shared.dto.AddressDTO;
import com.appsdeveloperblog.app.ws.shared.dto.UserDto;

class UserServiceImplTest {

	@InjectMocks
	UserServiceImpl userService;
	
	@Mock
	UserRepository userRepository;
	
	@Mock
	Utils utils;

	@Mock
	BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Mock
	AmazonSES amazonSES;

	String userId = "user1234";
	String firstName = "Sanjin";
	String lastName = "Dumančić";
	String email = "sanjin.dumancic@yahoo.com";
	String password = "12345";
	String encryptedPassword = "USER5678";
	UserEntity userEntity;
	
	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		userEntity = new UserEntity();
		userEntity.setId(1L);
		userEntity.setFirstName(firstName);
		userEntity.setLastName(lastName);
		userEntity.setUserId(userId);
		userEntity.setEncryptedPassword(encryptedPassword);
		userEntity.setEmailVerificationToken("token1234");
		userEntity.setEmail(email);		
		userEntity.setAddresses(getAddressesEntity());
	}

	@Test
	final void testGetUser() {
		
		
		when(userRepository.findByEmail(anyString())).thenReturn(userEntity);
		
		UserDto userDto = userService.getUser("test@test.com");
		
		assertNotNull(userDto);
		assertEquals( "Sanjin", userDto.getFirstName());
		assertEquals( userId, userDto.getUserId());
	}
	
	@Test
	final void testGetUser_UsernameNotFoundException() {
		when(userRepository.findByEmail(anyString())).thenReturn(null);
		 
		assertThrows(UsernameNotFoundException.class,
			()-> {
				userService.getUser("test@test.com");
			}
		);
	}
	
	@Test
	final void testCreateUser() {
		
		when(userRepository.findByEmail(anyString())).thenReturn(null);
		when(utils.generateAddressId(anyInt())).thenReturn("abcdefghij");
		when(utils.generateUserId(anyInt())).thenReturn(userId);
		when(bCryptPasswordEncoder.encode(anyString())).thenReturn(encryptedPassword);
		when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
		Mockito.doNothing().when(amazonSES).verifyEmail(any(UserDto.class));
				
		UserDto user = new UserDto();
		user.setAddresses(getAddressesDTO());
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setPassword(password);
		user.setEmail(email);
						
		
		UserDto storedUserDetails = userService.createUser(user);
		
		assertNotNull(storedUserDetails);
		assertEquals(userEntity.getFirstName(), storedUserDetails.getFirstName());
		assertEquals(userEntity.getLastName(), storedUserDetails.getLastName());
		assertNotNull(storedUserDetails.getUserId());
		assertEquals(storedUserDetails.getAddresses().size(),userEntity.getAddresses().size());
		
		verify(utils,times(2)).generateAddressId(30);
		verify(bCryptPasswordEncoder,times(1)).encode("12345");
		verify(userRepository, times(1)).save(any(UserEntity.class));
	}

	@Test
	final void testCreateUser_UserExists() {
		when(userRepository.findByEmail(anyString())).thenReturn(userEntity);
		
		UserDto user = new UserDto();
		user.setAddresses(getAddressesDTO());
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setPassword(password);
		user.setEmail(email);
		
		assertThrows(UserServiceException.class,
			()-> {
				userService.createUser(user);
			}
		);
	}
	
	
	private List<AddressDTO> getAddressesDTO(){
		AddressDTO addressDto = new AddressDTO();
		addressDto.setType("Shipping");
		addressDto.setCity("Čakovec");
		addressDto.setCountry("Croatia");
		addressDto.setPostalCode("00385");
		addressDto.setStreetName("Vukovarska 1");
		
		AddressDTO billingAddressDto = new AddressDTO();
		billingAddressDto.setType("Billing");
		billingAddressDto.setCity("Čakovec");
		billingAddressDto.setCountry("Croatia");
		billingAddressDto.setPostalCode("00385");
		billingAddressDto.setStreetName("Vukovarska 1");
		
		List<AddressDTO> addresses = new ArrayList<>();
		addresses.add(addressDto);
		addresses.add(billingAddressDto);
		
		return addresses;
	}
	
	private List<AddressEntity> getAddressesEntity(){
		
		List<AddressDTO> addresses = getAddressesDTO();
		
		java.lang.reflect.Type listType = new TypeToken<List<AddressEntity>>() {}.getType();
		return new ModelMapper().map(addresses, listType);
	};
}
