package com.java.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "사용자 수정 요청 데이터")
public class UserInfoReqDTO {

  private MultipartFile file;
  private String name;

}
