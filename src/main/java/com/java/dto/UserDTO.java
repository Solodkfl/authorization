package com.java.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.java.domain.role.RoleUserEntity;
import com.java.domain.user.UserEntity;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Setter
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

  private Long no;
  private String email;
  private String name;
  private Long fileNo;
  private char useYn;
  private String regUserName;
  private String modUserName;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "ASIA/Seoul")
  private LocalDateTime regDate;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "ASIA/Seoul")
  private LocalDateTime modDate;

  private List<RoleDTO> roles;

  public static UserDTO findByUser(UserEntity userEntity) {
    List<RoleDTO> arr = new ArrayList<>();
    userEntity.getRoles().forEach(role -> {
      boolean check = true;
      for(RoleUserEntity ru : userEntity.getRoleUsers()) {
        if(role.getNo() == ru.getRoleNo()) {
          if(ru.getUseYn() == 'N') check = false;
        }
      }
      if(check) {
        RoleDTO roleDTO = RoleDTO.builder()
            .no(role.getNo())
            .name(role.getName())
            .roleName(role.getRoleName())
            .build();
        arr.add(roleDTO);
      }
    });
    return (userEntity == null) ? null : UserDTO.builder()
        .no(userEntity.getNo())
        .email(userEntity.getEmail())
        .name(userEntity.getName())
        .fileNo(userEntity.getFileNo())
        .useYn(userEntity.getUseYn())
        .roles(arr)
        .regDate(userEntity.getRegDate())
        .regUserName((userEntity.getRegUser() == null) ? null : userEntity.getRegUser().getName())
        .modDate(userEntity.getModDate())
        .modUserName((userEntity.getModUser() == null) ? null : userEntity.getModUser().getName())
        .build();
  }

  public static Map<String, Object> findByUsers(Page<UserEntity> userEntities) {
    Map<String, Object> resultMap = new HashMap<>();
    List<UserDTO> users = new ArrayList<>();
    userEntities.forEach(user -> users.add(UserDTO.findByUser(user)));
    resultMap.put("list", users);
    resultMap.put("totalElements", userEntities.getTotalElements());
    resultMap.put("totalPages", userEntities.getTotalPages());
    resultMap.put("size", userEntities.getSize());
    return resultMap;
  }
}
