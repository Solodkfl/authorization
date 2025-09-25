package com.java;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.is;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.dto.ResDTO;
import com.java.dto.UserReqDTO;
import com.java.user.UserController;
import com.java.user.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class TestUserController {
	@Autowired MockMvc mockMvc;
	@MockBean UserService userService; 

    @MockBean com.java.config.JwtAuthenticationFilter jwtAuthenticationFilter;
	
	String email="test@test.test";
	String type="1";
	@Test @DisplayName("/user")
	void test1() throws Exception{
		UserReqDTO userReqDTO = UserReqDTO.builder().email(email).type(type).build();
        //Log.info("user : {}", userDTO );
        log.info("user: {}", userReqDTO);
        ResDTO resDTO = ResDTO.builder()
                        .status(true)
                        .result(null)
                        .message(null)
                        .build();
        given(userService.signUp(any(UserReqDTO.class))).willReturn(resDTO);
        
        
        //2단계
        ObjectMapper objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(userReqDTO);

        ResultActions resultActions = mockMvc.perform(
            put("/user")
            .content(content)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
        );

        // //3 단계 (then)
        resultActions.andExpect(status().isOk())
        .andExpect(jsonPath("$.status", is(true)))
        .andExpect(jsonPath("$.result", nullValue()))
        .andExpect(jsonPath("$.message", nullValue()));


        verify(userService).signUp((any(UserReqDTO.class)));
	}
}