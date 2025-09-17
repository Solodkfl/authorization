package com.java.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.java.domain.role.RoleEntity;
import com.java.domain.user.UserEntity;

import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Setter
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoleDTO {
  
  private Long no;
  private String name;
  private String roleName;
  private List<UserDTO> users;
  private String regUserName;
  private String modUserName;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "ASIA/Seoul")
  private LocalDateTime regDate;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "ASIA/Seoul")
  private LocalDateTime modDate;

  public static RoleDTO findByRole(RoleEntity roleEntity) {
    List<UserEntity> userEntities = roleEntity.getUsers();
    List<UserDTO> users = new ArrayList<>();
    if(userEntities != null) {
      userEntities.forEach(user -> users.add(UserDTO.findByUser(user)));
    }
    return (roleEntity == null) ? null : RoleDTO.builder()
        .no(roleEntity.getNo())
        .name(roleEntity.getName())
        .roleName(roleEntity.getRoleName())
        .users(users)
        .regDate(roleEntity.getRegDate())
        .regUserName((roleEntity.getRegUser() == null) ? null : roleEntity.getRegUser().getName())
        .modDate(roleEntity.getModDate())
        .modUserName((roleEntity.getModUser() == null) ? null : roleEntity.getModUser().getName())
        .build();
  }

  public static Map<String, Object> findByRole(List<RoleEntity> roleEntities) {
    Map<String, Object> resultMap = new HashMap<>();
    List<RoleDTO> roles = new ArrayList<>();
    roleEntities.forEach(role -> roles.add(RoleDTO.findByRole(role)));
    resultMap.put("list", roles);
    return resultMap;
  }

}
