package com.appsdeveloperblog.app.ws.controller;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.appsdeveloperblog.app.ws.repositories.UserRepository;
import com.appsdeveloperblog.app.ws.service.UserService;
import com.appsdeveloperblog.app.ws.shared.dto.AddressDTO;
import com.appsdeveloperblog.app.ws.shared.dto.UserDto;
import com.appsdeveloperblog.app.ws.ui.model.response.UserRest;

class UserControllerTest {
	
	@InjectMocks
	UserController userController;
	
	@Mock
	UserService userService;

	final String USER_ID = "user1234";
	final String FIRST_NAME = "Sanjin";
	final String LAST_NAME = "Dumančić";
	final String email = "sanjin.dumancic@yahoo.com";
	final String password = "12345";
	final String encryptedPassword = "USER5678";
	
	UserDto userDto;
	
	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		userDto = new UserDto();
		userDto.setAddresses(getAddressesDTO());
		userDto.setFirstName(FIRST_NAME);
		userDto.setLastName(LAST_NAME);
		userDto.setPassword(password);
		userDto.setEmail(email);
		userDto.setUserId(USER_ID);
	}

	@Test
	final void testGetUser() {
		when(userService.getUserByUserId(anyString())).thenReturn(userDto);
		
		UserRest userRest = userController.getUser(USER_ID);
		
		assertNotNull(userRest);
		assertEquals(USER_ID, userRest.getUserId());
		assertEquals(userDto.getFirstName(), userRest.getFirstName());
		assertEquals(userDto.getLastName(), userRest.getLastName());
		assertTrue(userDto.getAddresses().size() == userRest.getAddresses().size());
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

}
