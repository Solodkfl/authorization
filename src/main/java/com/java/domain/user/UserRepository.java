package com.java.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
  
  UserEntity findByEmailAndUseYn(String email, char useYn);
  UserEntity findByEmail(String email);

}
