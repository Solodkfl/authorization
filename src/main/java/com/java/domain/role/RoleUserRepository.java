package com.java.domain.role;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoleUserRepository extends JpaRepository<RoleUserEntity, Long> {

  public Page<RoleUserEntity> findAllByUseYn(char useYn, Pageable pageable);
  public List<RoleUserEntity> findAllByUseYn(char useYn);
  public Optional<RoleUserEntity> findByNoAndUseYn(Long no, char useYn);
   
}