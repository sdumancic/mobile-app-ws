package com.appsdeveloperblog.app.ws.repositories;

import org.springframework.data.repository.CrudRepository;

import com.appsdeveloperblog.app.ws.entity.PasswordResetTokenEntity;

public interface PasswordResetTokenRepository extends CrudRepository<PasswordResetTokenEntity, Long> {

	PasswordResetTokenEntity findByToken(String token);

}
