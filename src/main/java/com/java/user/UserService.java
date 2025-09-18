package com.java.user;

import com.java.dto.AuthReqDTO;
import com.java.dto.ResDTO;
import com.java.dto.UserInfoReqDTO;
import com.java.dto.UserReqDTO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;

public interface UserService {

  public ResDTO userInfo(Authentication authentication);
  public ResDTO signIn(AuthReqDTO authReqDTO, HttpServletRequest request, HttpServletResponse response, HttpSession session);
  public ResDTO signUp(UserReqDTO userDto);
  public ResDTO delete(Authentication authentication);
  public ResDTO modify(UserInfoReqDTO userInfoReqDTO, Authentication authentication);
  public ResDTO email(UserReqDTO userDto);
  public ResDTO auth(AuthReqDTO authReqDTO);
  public ResDTO logout(HttpServletRequest request, HttpServletResponse response);
  
}
