package com.java.user;

import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.java.domain.role.RoleEntity;
import com.java.domain.role.RoleRepository;
import com.java.domain.role.RoleUserEntity;
import com.java.domain.role.RoleUserRepository;
import com.java.domain.user.UserEntity;
import com.java.domain.user.UserRepository;
import com.java.dto.AuthReqDTO;
import com.java.dto.FileDTO;
import com.java.dto.KeyDTO;
import com.java.dto.MailDTO;
import com.java.dto.ResDTO;
import com.java.dto.UserDTO;
import com.java.dto.UserInfoReqDTO;
import com.java.dto.UserReqDTO;
import com.java.file.FileService;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImp implements UserService {
  
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final RoleUserRepository roleUserRepository;
  private final BCryptPasswordEncoder passwordEncoder;
  private final FileService fileService;
  private final JwtEncoder jwtEncoder;
  private final char USEYN = 'Y';
  private final JavaMailSender mailSender;
  private static List<KeyDTO> KEYS = new ArrayList<>();

  @Value("${spring.mail.username}")
  private String emailFrom;

  @Override
  public ResDTO userInfo(Authentication authentication) {
    boolean status = false;
    String message = "존재하지 않는 사용자 입니다.";
    UserEntity userEntity = null;
    try {
      if (authentication != null) {
        userEntity = userRepository.findById(Long.parseLong(authentication.getName())).orElseThrow(() -> new RuntimeException("존재하지 않는 사용자 입니다."));
        if(userEntity.getUseYn() == USEYN) {
          message = null;
          status = true;
        } else {
          userEntity = null;
        }
      }
    } catch (Exception e) {
      message = e.getMessage();
    }
    return ResDTO.builder().status(status).result(UserDTO.findByUser(userEntity)).message(message).build();
  }

  @Override
  public ResDTO signIn(AuthReqDTO authReqDTO, HttpServletRequest request, HttpServletResponse response, HttpSession session) {
    boolean status = false;
    String message = "존재하지 않는 사용자 입니다.";
    String roles = null;
    String access_token = null;
    try {
      List<KeyDTO> KEYS2 = new ArrayList<>();
      String email = null;
      for(KeyDTO keyDTO : KEYS) {
        if(Duration.between( keyDTO.getRegTime(),  LocalTime.now() ).getSeconds() <= 180) {
          if(keyDTO.getKey().equals(authReqDTO.getCode())) {
            email = keyDTO.getEmail();
          } else {
            KEYS2.add(keyDTO);
          }
        }
      }
      KEYS = KEYS2;
      KEYS.forEach(System.out::println);

      if(email == null) {
        Cookie[] cookies = request.getCookies();
        if(cookies != null) {
          for(int i = 0; i < cookies.length; i++) {
            cookies[i].setMaxAge(0);
            cookies[i].setPath("/");
            response.addCookie(cookies[i]);
          }
        }
      } else {
        UserEntity userEntity = userRepository.findByEmailAndUseYn(email, USEYN); //.orElseThrow(() -> new RuntimeException("존재하지 않는 사용자 입니다."));
        UserDTO userDTO = UserDTO.findByUser(userEntity); // "RoleUser" 테이블 useYn 확인 후 DTO 생성
        Set<String> arr = new HashSet<>();
        userDTO.getRoles().forEach(d -> arr.add(d.getName()));
        roles = String.join(",", arr);
        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
            .issuer("Authorization_Server")
            .subject(userDTO.getName())
            .issuedAt(Instant.now())
            .expiresAt(Instant.now().plus(1, ChronoUnit.DAYS))
            .claim("role", roles)
            .claim("userNo", userDTO.getNo())
            .build();
        JwtEncoderParameters parameters = JwtEncoderParameters.from(claimsSet);
        access_token = jwtEncoder.encode(parameters).getTokenValue();
        Cookie cookie = new Cookie("access_token", access_token);
        // cookie.setHttpOnly(true);
        // cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(session.getMaxInactiveInterval());
        response.addCookie(cookie);
        status = true;
        message = userEntity.getName() + "님 환영합니다.";
      }
    } catch (Exception e) {
      message = e.getMessage();
    }
    return ResDTO.builder().status(status).result(access_token).message(message).build();
  }

  @Transactional
  @Override
  public ResDTO signUp(UserReqDTO userDto) {
    boolean status = false;
    String message = "정상적으로 가입이 되지 않았습니다.";
    // RoleEntity roleEntity = roleRepository.findById(userDto.getService()).orElseThrow(() -> new RuntimeException("존재하지 않는 권한 입니다."));
    UserEntity userEntity = UserEntity.builder()
        .email(userDto.getEmail())
        // .password(passwordEncoder.encode(userDto.getPassword()))
        .name(userDto.getName())
        .build();
    userEntity.setUseYn(USEYN);
    log.info("============================================================================================================");
    log.info("USER : {}", userEntity);
    // userEntity.setRole(Set.of(roleEntity));
    userEntity = userRepository.save(userEntity);
    log.info("USER : {}", userEntity);
    if(userEntity.getNo() > 0) {
      userEntity.setRegUserNo(userEntity.getNo());
      userRepository.save(userEntity);

      RoleUserEntity roleUserEntity = RoleUserEntity.builder()
        .roleNo(userDto.getService())
        .userNo(userEntity.getNo())
        .build();

      roleUserEntity.setUseYn(USEYN);
      roleUserEntity.setRegUserNo(userEntity.getNo());
      log.info("ROLE_USER : {}", roleUserEntity);

      roleUserRepository.save(roleUserEntity);

      status = true;
      message = "정상적으로 가입이 되었습니다.";
    }
    return ResDTO.builder().status(status).message(message).build();
  }

  @Transactional
  @Override
  public ResDTO delete(Authentication authentication) {
    boolean status = false;
    String message = "정상적으로 탈퇴 되지 않았습니다.";
    UserEntity userEntity = userRepository.findById(Long.parseLong(authentication.getName())).orElseThrow(() -> new RuntimeException("존재하지 않는 사용자 입니다."));
    userEntity.setModUserNo(userEntity.getNo());
    userEntity.setUseYn('N');
    userEntity = userRepository.save(userEntity);
    if(userEntity.getNo() > 0) {
      status = true;
      message = null;
    }
    return ResDTO.builder().status(status).message(message).build();
  }

  @Transactional
  @Override
  public ResDTO modify(UserInfoReqDTO userInfoReqDTO, Authentication authentication) {
    boolean status = false;
    String message = "정상적으로 수정 되지 않았습니다.";
    UserEntity userEntity = userRepository.findById(Long.parseLong(authentication.getName())).orElseThrow(() -> new RuntimeException("존재하지 않는 사용자 입니다."));
    if(userInfoReqDTO.getFile() != null) {
      ResDTO resDTO = fileService.upload(userInfoReqDTO.getFile(), authentication);
      if (resDTO.isStatus()) {
        FileDTO fileDTO = (FileDTO) resDTO.getResult();
        if (fileDTO != null) {
          userEntity.setFileNo(fileDTO.getNo());
        }
      }
    }
    userEntity.setModUserNo(Long.parseLong(authentication.getName()));
    userEntity.setName(userInfoReqDTO.getName());
    userEntity = userRepository.save(userEntity);
    if(userEntity.getNo() > 0) {
      status = true;
      message = null;
    }
    return ResDTO.builder().status(status).message(message).build();
  }

  @Transactional
  @Override
  public ResDTO email(UserReqDTO userDto) {
    boolean status = false;
    String message = "이미 사용 중인 이메일 입니다.";

    // 키 목록 확인
    List<KeyDTO> KEYS2 = new ArrayList<>();
    for(KeyDTO keyDTO : KEYS) {
      if(Duration.between( keyDTO.getRegTime(),  LocalTime.now() ).getSeconds() <= 180) {
        KEYS2.add(keyDTO);
      }
    }
    KEYS = KEYS2;
    // KEYS.forEach(System.out::println);

    UserEntity userEntity = userRepository.findByEmail(userDto.getEmail());
    if( "1".equals(userDto.getType()) ) {
      if (userEntity == null) {
        // 회원가입 신규 로직 동작
        status = setKey(userDto.getEmail());
        message = "";
      } else {
        if("N".equals(String.valueOf(userEntity.getUseYn()))) {
          userEntity.setUseYn('Y');
          userEntity.setModUserNo(userEntity.getNo());
          userEntity = userRepository.save(userEntity);
          message = userEntity.getName() + "님 다시 오셨군요!!";
        } else {
          message = userEntity.getName() + "님 회원 가입 중이십니다.";
        }
      }
    } else {
      if (userEntity != null) {
        if("Y".equals(String.valueOf(userEntity.getUseYn()))) {
          // 로그인 로직 동작
          status = setKey(userDto.getEmail());
          message = "";
        } else {
          message = "비활성화된 이메일 주소 입니다.";
        }
      } else {
        message = "존재하지 않는 이메일 주소입니다.";
      }
    }
    return ResDTO.builder().status(status).message(message).build();
  }

  @Override
  public ResDTO auth(AuthReqDTO authReqDTO) {
    boolean status = false;
    String message = "유효하지 않는 인증번호 입니다.";
    List<KeyDTO> KEYS2 = new ArrayList<>();
    for(KeyDTO keyDTO : KEYS) {
      if(Duration.between( keyDTO.getRegTime(),  LocalTime.now() ).getSeconds() <= 180) {
        if(keyDTO.getKey().equals(authReqDTO.getCode())) {
          status = true;
          message = "인증코드 확인 완료";
        } else {
          KEYS2.add(keyDTO);
        }
      }
    }
    KEYS = KEYS2;
    KEYS.forEach(System.out::println);
    return ResDTO.builder().status(status).message(message).build();
  }

  @Override
  public ResDTO logout(HttpServletRequest request, HttpServletResponse response) {
    boolean status = true;
    try {
      Cookie[] cookies = request.getCookies();
      if (cookies != null) {
        for (int i = 0; i < cookies.length; i++) {
          cookies[i].setMaxAge(0);
          cookies[i].setPath("/");
          response.addCookie(cookies[i]);
        }
      }
    } catch (Exception e) {
      status = false;
    }
    return ResDTO.builder().status(status).build();
  }

  private boolean setKey(String email) {
    String key = new BigInteger(130, new SecureRandom()).toString(32);
    log.info("KEY : {}", key);
    KEYS.add(KeyDTO.builder()
        .email(email)
        .key(key)
        .regTime(LocalTime.now())
        .build());
    return sendMail(MailDTO.builder()
        .emailFrom(emailFrom)
        .emailTo(email)
        .emailSubject("[HIEDU] 이메일 확인 인증코드 전송")
        .emailBody(generateEmailContent(key,"/mail.html"))
        .emailHtmlEnable(true)
        .build());
  }

  private boolean sendMail(MailDTO mailDTO) {
    MimeMessage message = mailSender.createMimeMessage();
    try {
      MimeMessageHelper helper = new MimeMessageHelper(message, true);
      helper.setFrom(mailDTO.getEmailFrom());
      helper.setTo(mailDTO.getEmailTo());
      helper.setSubject(mailDTO.getEmailSubject());
      helper.setText(mailDTO.getEmailBody(), mailDTO.isEmailHtmlEnable());
      mailSender.send(message);
      return true;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }

  // 템플릿을 이용한 이메일 내용 생성
  public String generateEmailContent(String key, String tempatePath) {
    String emailContent = loadEmailTemplate(tempatePath);
    emailContent = emailContent.replace("{key}", key);
    return emailContent;
  }

  // 리소스 디렉토리에서 이메일 템플릿을 읽어오기
  private String loadEmailTemplate(String tempatePath) {
    try {
      Resource resource = new ClassPathResource(tempatePath);
      // 파일 내용을 문자열로 읽어오기
      return new String(Files.readAllBytes(Paths.get(resource.getURI())));
    } catch (IOException e) {
      e.printStackTrace();
      return "이메일 템플릿 로드를 확인해주세요.";
    }
  }

}
