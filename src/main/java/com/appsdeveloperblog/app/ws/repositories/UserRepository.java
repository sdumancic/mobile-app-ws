package com.appsdeveloperblog.app.ws.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.appsdeveloperblog.app.ws.entity.UserEntity;

@Repository
public interface UserRepository extends PagingAndSortingRepository<UserEntity, Long> {

	UserEntity findByEmail(String email);
	UserEntity findByUserId(String userId);
	UserEntity findUserByEmailVerificationToken(String token);
	
	@Query(value="select * from users u where u.email_verification_status = 'true'", 
			countQuery="select count(*) from users u where u.email_verification_status = 'true'",
			nativeQuery = true)
	Page<UserEntity> findAllUsersWithConfirmedEmailAddress(Pageable pageableRequest);
	
	
	@Query(value="select * from users u where u.first_name = ?1", 
			countQuery="select count(*) from users u where u.first_name = ?1",
			nativeQuery = true)
	List<UserEntity> findUsersByFirstName(String firstName);
	
	@Query(value="select * from users u where u.last_name = :lastname", 
			countQuery="select count(*) from users u where u.first_name = :lastname",
			nativeQuery = true)
	Page<UserEntity> findUsersByLastName(@Param("lastname") String lastName,Pageable pageableRequest);
	
	@Query(value="select * from users u where u.first_name like :keyword% or u.last_name like :keyword%", 
			countQuery="select * from users u where u.first_name like :keyword% or u.last_name like :keyword%",
			nativeQuery = true)
	Page<UserEntity> findUsersByKeyword(@Param("keyword") String keyword,Pageable pageableRequest);
	
	@Query(value="select u.first_name, u.last_name from users u where u.first_name like :keyword% or u.last_name like :keyword%", 
			countQuery="select * from users u where u.first_name like :keyword% or u.last_name like :keyword%",
			nativeQuery = true)
	Page<Object[]> findUserFirstNameAndLastNameByKeyword(@Param("keyword") String keyword,Pageable pageableRequest);

	
	@Transactional
	@Modifying
	@Query(value="update users u set u.email_verification_status =:emailVerificationStatus where u.user_id = :userId", nativeQuery=true)
	void updateUserEmailVerificationStatus(@Param("emailVerificationStatus") boolean emailVerificationStatus,
										   @Param("userId") String userId);
	

	@Query("select user from UserEntity user where user.userId = :userId")
	UserEntity findUserEntityByUserId(@Param("userId") String userId);
	
	@Query("select user.firstName, user.lastName from UserEntity user where user.userId = :userId")
	List<Object[]> getUserEntityFullNameById(@Param("userId") String userId);
	
	@Transactional
	@Modifying
	@Query("update UserEntity u set u.emailVerificationStatus =:emailVerificationStatus where u.userId = :userId")
	void updateUserEntityEmailVerificationStatus(@Param("emailVerificationStatus") boolean emailVerificationStatus,
			   @Param("userId") String userId);


}
