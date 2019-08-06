package com.appsdeveloperblog.app.ws.controller;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.appsdeveloperblog.app.ws.exceptions.UserServiceException;
import com.appsdeveloperblog.app.ws.service.AddressService;
import com.appsdeveloperblog.app.ws.service.UserService;
import com.appsdeveloperblog.app.ws.shared.dto.AddressDTO;
import com.appsdeveloperblog.app.ws.shared.dto.UserDto;
import com.appsdeveloperblog.app.ws.ui.model.request.PasswordResetModel;
import com.appsdeveloperblog.app.ws.ui.model.request.PasswordResetRequestModel;
import com.appsdeveloperblog.app.ws.ui.model.request.UserDetailsRequestModel;
import com.appsdeveloperblog.app.ws.ui.model.response.AddressRest;
import com.appsdeveloperblog.app.ws.ui.model.response.ErrorMessages;
import com.appsdeveloperblog.app.ws.ui.model.response.OperationStatusModel;
import com.appsdeveloperblog.app.ws.ui.model.response.RequestOperationName;
import com.appsdeveloperblog.app.ws.ui.model.response.RequestOperationStatus;
import com.appsdeveloperblog.app.ws.ui.model.response.UserRest;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@RequestMapping("/users")
public class UserController {

	@Autowired
	UserService userService;

	@Autowired
	AddressService addressService;

	@ApiImplicitParams({
		@ApiImplicitParam(name="authorization", value="${userController.authorizationHeader.description}", paramType="header")
	})
	@ApiOperation(value="The get User details web service endpoint",
				 notes="The web service endpoint returns User Details, User public id in URL Path")
	@GetMapping(path = "/{userId}", produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public UserRest getUser(@PathVariable String userId) {

		UserDto userDto = userService.getUserByUserId(userId);
		ModelMapper modelMapper = new ModelMapper();
		UserRest userRest = modelMapper.map(userDto, UserRest.class);
		return userRest;
	}

	
	@PostMapping(consumes = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE }, produces = {
			MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) throws Exception {
		UserRest returnValue = new UserRest();

		if (userDetails.getFirstName().isEmpty())
			throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());

		ModelMapper modelMapper = new ModelMapper();
		UserDto userDto = modelMapper.map(userDetails, UserDto.class);

		UserDto createdUser = userService.createUser(userDto);
		returnValue = modelMapper.map(createdUser, UserRest.class);

		return returnValue;

	}

	@ApiImplicitParams({
		@ApiImplicitParam(name="authorization", value="${userController.authorizationHeader.description}", paramType="header")
	})
	@PutMapping(path = "/{id}", consumes = { MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_XML_VALUE,
					MediaType.APPLICATION_JSON_VALUE })
	public UserRest updateUser(@PathVariable String id, @RequestBody UserDetailsRequestModel userDetails) {
		UserRest returnValue = new UserRest();

		if (userDetails.getFirstName().isEmpty())
			throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());

		ModelMapper modelMapper = new ModelMapper();
		UserDto userDto = modelMapper.map(userDetails, UserDto.class);

		UserDto updatedUser = userService.updateUser(id, userDto);
		returnValue = modelMapper.map(updatedUser, UserRest.class);

		return returnValue;
	}

	@ApiImplicitParams({
		@ApiImplicitParam(name="authorization", value="${userController.authorizationHeader.description}", paramType="header")
	})
	@DeleteMapping(path = "/{id}", produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public OperationStatusModel deleteUser(@PathVariable String id) {

		OperationStatusModel returnValue = new OperationStatusModel();
		returnValue.setOperationName(RequestOperationName.DELETE.name());
		userService.deleteUser(id);
		returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
		return returnValue;
	}

	@ApiImplicitParams({
		@ApiImplicitParam(name="authorization", value="${userController.authorizationHeader.description}", paramType="header")
	})
	@GetMapping(produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public List<UserRest> getUsers(@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "limit", defaultValue = "25") int limit) {
		List<UserRest> returnValue = new ArrayList<>();
		List<UserDto> users = userService.getUsers(page, limit);

		for (UserDto userDto : users) {

			ModelMapper modelMapper = new ModelMapper();
			UserRest userModel = modelMapper.map(userDto, UserRest.class);
			returnValue.add(userModel);
		}
		return returnValue;
	}

	@ApiImplicitParams({
		@ApiImplicitParam(name="authorization", value="${userController.authorizationHeader.description}", paramType="header")
	})
	@GetMapping(path = "/{userId}/addresses", 
				produces = { MediaType.APPLICATION_XML_VALUE,
							 MediaType.APPLICATION_JSON_VALUE,
							 "application/hal+json"})
	public Resources<AddressRest> getUserAddresses(@PathVariable String userId) {
		List<AddressRest> addressesListRestModel = new ArrayList<>();
		List<AddressDTO> addressesDTO = addressService.getAddresses(userId);

		if (addressesDTO != null && !addressesDTO.isEmpty()) {
			java.lang.reflect.Type listType = new TypeToken<List<AddressRest>>() {}.getType();
			addressesListRestModel = new ModelMapper().map(addressesDTO, listType);
			
			for (AddressRest addressRest:addressesListRestModel) {
				Link addressLink = linkTo(methodOn(UserController.class).getUserSingleAddress(userId, addressRest.getAddressId())).withSelfRel();
				addressRest.add(addressLink);
				
				Link userLink = linkTo(methodOn(UserController.class).getUser(userId)).withRel("user");
				addressRest.add(userLink);
			}
		}

		return new Resources<>(addressesListRestModel);
	}

	@ApiImplicitParams({
		@ApiImplicitParam(name="authorization", value="${userController.authorizationHeader.description}", paramType="header")
	})
	@GetMapping(path = "/{userId}/addresses/{addressId}", produces = { MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_JSON_VALUE,"application/hal+json" })
	public Resource<AddressRest> getUserSingleAddress(@PathVariable String userId, @PathVariable String addressId) {

		AddressDTO addressesDTO = addressService.getAddress(userId, addressId);
		
		Link addressLink = linkTo(methodOn(UserController.class).getUserSingleAddress(userId, addressId)).withSelfRel();
		Link userLink = linkTo(UserController.class).slash(userId).withRel("user");
		Link allAddressesLink = linkTo(methodOn(UserController.class).getUserAddresses(userId)).withRel("addresses");
		
		AddressRest addressRestModel = new ModelMapper().map(addressesDTO, AddressRest.class);
		addressRestModel.add(addressLink);
		addressRestModel.add(userLink);
		addressRestModel.add(allAddressesLink);
		
		return new Resource<>(addressRestModel);
	}
	
	/*
	 /users/email-verification?token=sdsfa
	*/
	
	@GetMapping(path="/email-verification", produces = { MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_JSON_VALUE})
	public OperationStatusModel verifyEmailToken(@RequestParam(value="token") String token) {
		OperationStatusModel returnValue = new OperationStatusModel();
		returnValue.setOperationName(RequestOperationName.VERIFY_EMAIL.name());
		
		boolean isVerified = userService.verifyEmailToken(token);
		
		if (isVerified) {
			returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
		}
		else {
			returnValue.setOperationResult(RequestOperationStatus.ERROR.name());
		}
		
		return returnValue;
	}
	
	/*
	 /users/password-reset-request
	*/
	@ApiImplicitParams({
		@ApiImplicitParam(name="authorization", value="${userController.authorizationHeader.description}", paramType="header")
	})
	@PostMapping(path="/password-reset-request",
			 	 produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
				 consumes = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}
	)
	public OperationStatusModel requestReset(@RequestBody PasswordResetRequestModel passwordResetRequestModel) {
		OperationStatusModel returnValue = new OperationStatusModel();
		
		boolean operationResult = userService.requestPasswordReset(passwordResetRequestModel.getEmail());
		
		returnValue.setOperationName(RequestOperationName.REQUEST_PASSWORD_RESET.name());
		returnValue.setOperationResult(RequestOperationStatus.ERROR.name());
		
		if (operationResult)
			returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
		
		return returnValue;
	}
	
	/*
	 /users/password-reset
	*/
	@ApiImplicitParams({
		@ApiImplicitParam(name="authorization", value="${userController.authorizationHeader.description}", paramType="header")
	})
	@PostMapping(path="/password-reset",
		 	 produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
			 consumes = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
	public OperationStatusModel resetPassword(@RequestBody PasswordResetModel passwordResetModel) {
		OperationStatusModel returnValue = new OperationStatusModel();
		
		boolean operationResult = userService.resetPassword(
				 passwordResetModel.getToken()
				,passwordResetModel.getPassword());
		
		returnValue.setOperationName(RequestOperationName.PASSWORD_RESET.name());
		returnValue.setOperationResult(RequestOperationStatus.ERROR.name());
		
		if (operationResult)
			returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
		
		return returnValue;
	}
	

}
