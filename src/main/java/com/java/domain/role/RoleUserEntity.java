package com.java.domain.role;

import jakarta.persistence.*;
import lombok.*;

import com.java.domain.BaseEntity;
import com.java.domain.user.UserEntity;

@Entity
@Table(name="role_user")
@Setter
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoleUserEntity extends BaseEntity {
  
  @Column(name = "roleNo", nullable = false)
  private Long roleNo;

  @Column(name = "userNo", nullable = false)
  private Long userNo;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "userNo", insertable=false, updatable = false)
  private UserEntity targetUser;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "regUserNo", insertable=false, updatable = false)
  private UserEntity regUser;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "modUserNo", insertable=false, updatable = false)
  private UserEntity modUser;

}
