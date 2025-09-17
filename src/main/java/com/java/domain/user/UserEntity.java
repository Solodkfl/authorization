package com.java.domain.user;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.java.domain.BaseEntity;
import com.java.domain.role.RoleEntity;
import com.java.domain.role.RoleUserEntity;

@Entity
@Table(name="user")
@Setter
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity extends BaseEntity {
  
  @Column(name = "email", nullable = false, length = 100, unique = true)
  private String email;

  @Column(name = "name", nullable = false, length = 100)
  private String name;

  @Column(name = "fileNo", nullable = true)
  private Long fileNo;

  @OrderBy("no asc")
  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "role_user",
    joinColumns = @JoinColumn(name = "userNo"),
    inverseJoinColumns = @JoinColumn(name = "roleNo"))
  private Set<RoleEntity> roles = new HashSet<>();

  @OneToMany(mappedBy = "targetUser")
  private Set<RoleUserEntity> roleUsers = new HashSet<>();

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "regUserNo", insertable=false, updatable = false)
  private UserEntity regUser;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "modUserNo", insertable=false, updatable = false)
  private UserEntity modUser;

}
