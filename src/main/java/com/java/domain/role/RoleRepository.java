package com.java.domain.role;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

  public Page<RoleEntity> findAllByUseYn(char useYn, Pageable pageable);
  public List<RoleEntity> findAllByUseYn(char useYn);
  public Optional<RoleEntity> findByNoAndUseYn(Long no, char useYn);

}
