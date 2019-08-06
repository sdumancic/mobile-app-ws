package com.appsdeveloperblog.app.ws.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.appsdeveloperblog.app.ws.entity.AddressEntity;
import com.appsdeveloperblog.app.ws.entity.UserEntity;
import com.appsdeveloperblog.app.ws.repositories.AddressRepository;
import com.appsdeveloperblog.app.ws.repositories.UserRepository;
import com.appsdeveloperblog.app.ws.service.AddressService;
import com.appsdeveloperblog.app.ws.shared.dto.AddressDTO;

@Service
public class AddressServiceImpl implements AddressService {

	@Autowired
	UserRepository userRepository;
	
	@Autowired
	AddressRepository addressRepository;
	
	@Override
	public List<AddressDTO> getAddresses(String userId) {
		
		List<AddressDTO> returnValue = new ArrayList<>();
		UserEntity userEntity = userRepository.findByUserId(userId);
		ModelMapper mapper = new ModelMapper();
		
		if (userEntity==null) return returnValue;
		
		Iterable<AddressEntity> addresses = addressRepository.findAllByUserDetails(userEntity);
		for (AddressEntity addressEntity:addresses) {
			returnValue.add(mapper.map(addressEntity, AddressDTO.class));
		}
		
		return returnValue;
	}

	@Override
	public AddressDTO getAddress(String addressId) {
		AddressDTO returnValue = new AddressDTO();
		AddressEntity addressEntity = addressRepository.findByAddressId(addressId);
		if (addressEntity==null) return returnValue;
		
		return new ModelMapper().map(addressEntity, AddressDTO.class);
	}
	
	@Override
	public AddressDTO getAddress(String userId, String addressId) {
		AddressDTO returnValue = new AddressDTO();
		UserEntity userEntity = userRepository.findByUserId(userId);
		
		if (userEntity==null) return returnValue;
		
		AddressEntity addressEntity = addressRepository.findByUserDetailsAndAddressId(userEntity, addressId);
		if (addressEntity==null) return returnValue;
		
		return new ModelMapper().map(addressEntity, AddressDTO.class);
	}

}
