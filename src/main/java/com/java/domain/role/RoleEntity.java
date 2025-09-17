package com.java.domain.role;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

import com.java.domain.BaseEntity;
import com.java.domain.user.UserEntity;

@Entity
@Table(name="role")
@Setter
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoleEntity extends BaseEntity {
  
  @Column(name = "name", nullable = false, length = 100, unique = true)
  private String name;

  @Column(name = "roleName", nullable = false, length = 100, unique = true)
  private String roleName;

  @OrderBy("no asc")
  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "role_user",
      joinColumns = @JoinColumn(name = "roleNo"),
      inverseJoinColumns = @JoinColumn(name = "userNo"))
  private List<UserEntity> users = new ArrayList<>();

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "regUserNo", insertable=false, updatable = false)
  private UserEntity regUser;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "modUserNo", insertable=false, updatable = false)
  private UserEntity modUser;

}
