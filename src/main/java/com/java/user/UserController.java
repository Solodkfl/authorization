package com.java.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.java.dto.AuthReqDTO;
import com.java.dto.ResDTO;
import com.java.dto.UserInfoReqDTO;
import com.java.dto.UserReqDTO;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController implements UserControllerDocs {
  
  private final UserService userService;

  @PreAuthorize("isAuthenticated()")
  @GetMapping
  public ResDTO userInfo(Authentication authentication) {
    return userService.userInfo(authentication);
  }

  @PostMapping
  public ResDTO signIn(@RequestBody @Valid AuthReqDTO authReqDTO, HttpServletRequest request, HttpServletResponse response, HttpSession session) {
    return userService.signIn(authReqDTO, request, response, session);
  }

  @PutMapping
  public ResDTO signUp(@RequestBody @Valid UserReqDTO userDto) {
    return userService.signUp(userDto);
  }
  
  @PreAuthorize("isAuthenticated()")
  @DeleteMapping
  public ResDTO delete(Authentication authentication) {
    return userService.delete(authentication);
  }

  @PreAuthorize("isAuthenticated()")
  @PatchMapping
  public ResDTO modify(@ModelAttribute @Valid UserInfoReqDTO userInfoReqDTO, Authentication authentication) {
    return userService.modify(userInfoReqDTO, authentication);
  }

  @PostMapping("/email")
  public ResDTO email(@RequestBody @Valid UserReqDTO userDto) {
    return userService.email(userDto);
  }

  @PostMapping("/auth")
  public ResDTO auth(@RequestBody @Valid AuthReqDTO authReqDTO) {
    return userService.auth(authReqDTO);
  }

  @PostMapping("/logout")
  public ResDTO logout(HttpServletRequest request, HttpServletResponse response) {
    return userService.logout(request, response);
  }

}
