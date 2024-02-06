package com.example.todolist.controller;

import com.example.todolist.dto.LoginRequestDto;
import com.example.todolist.dto.LoginResponseDto;
import com.example.todolist.dto.SignupRequestDto;
import com.example.todolist.dto.SignupResponseDto;
import com.example.todolist.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class UserController {
    private final UserService userService;

    // 회원가입
    @PostMapping("/signup")
    public SignupResponseDto signup(@RequestBody @Valid SignupRequestDto signupRequestDto) {
        userService.signup(signupRequestDto);
        return new SignupResponseDto("회원가입 성공", 200);
    }
    // 로그인
    @ResponseBody
    @PostMapping("/login")
    public LoginResponseDto login(@RequestBody @Valid LoginRequestDto loginRequestDto, HttpServletResponse response) {
        userService.login(loginRequestDto, response);
        return new LoginResponseDto("로그인 성공", 200);
    }
}
