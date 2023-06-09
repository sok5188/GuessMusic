package com.example.guess_music.controller;

import com.example.guess_music.domain.auth.Member;
import com.example.guess_music.domain.auth.OAuthSingUpForm;
import com.example.guess_music.domain.auth.Role;
import com.example.guess_music.domain.auth.SignInForm;
import com.example.guess_music.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/auth")
public class LoginController {
    private final MemberService memberService;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public LoginController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/loginForm")
    public String createLoginForm(@RequestParam(value = "error", required = false) String error,
                                  @RequestParam(value = "exception", required = false) String exception,
                                  Model model){
        if(error!=null){
            log.warn("Login error occurred");
            model.addAttribute("error",error);
            model.addAttribute("exception",exception);
        }
        return "login/createLoginForm";
    }

    @GetMapping("/joinForm")
    public String createSignInForm(@RequestParam(value = "error", required = false) String error,Model model) {
        if(error!=null){
            log.warn("already exist id..");
            model.addAttribute("error",error);
        }

        return "login/createSignInForm";
    }
    @PostMapping("/signIn")
    public String signIn(SignInForm form){
        log.info("Sign in form: {}", form.getUsername()+"/"+form.getPassword());
        Member user = new Member();
        user.setUsername(form.getUsername());
        user.setName(form.getName());
        user.setEmail(form.getEmail());
        String encodePwd = bCryptPasswordEncoder.encode(form.getPassword());
        user.setPassword(encodePwd);
        if(user.getUsername().equals("admin"))
            user.setRole(Role.ROLE_ADMIN);
        else if(user.getUsername().equals("manager"))
            user.setRole(Role.ROLE_MANAGER);
        else
            user.setRole(Role.ROLE_USER);

        String join = memberService.join(user);
        if(join.equals("FAIL"))
            return "redirect:/auth/joinForm?error=true";
        else
            return "redirect:/auth/loginForm";
    }

    @GetMapping("/oAuthUserCheck")
    public String oAuthUsercheck(HttpSession session){
        String username = String.valueOf(session.getAttribute("username"));
        Optional<Member> opt = memberService.findOne(username);
        //해당 유저가 존재할 때 해당 유저의 이름이 설정되어 있지 않으면 설정 페이지로 이동
        //이미 존재한다면 홈으로 이동
        if(opt.isPresent()){
            if(opt.get().getName()==null)
                return "login/oAuthSignUp";
            else return "redirect:/";
        }else{
            log.error("OAuth user Not Found Error");
            return "OAuth User Check Error..";
        }

    }
    @PostMapping("/oAuthSignUp")
    public String oAuthSignUp(OAuthSingUpForm form,HttpSession session){
        String username = String.valueOf(session.getAttribute("username"));
        Optional<Member> opt = memberService.findOne(username);
        if(opt.isPresent()){
            //입력한 이름으로 해당 유저 설정 후 홈으로 이동
            memberService.updateName(form.getName(),username);
            //세션에 name 저장
            session.setAttribute("name",form.getName());
            return "redirect:/";
        }else{
            log.error("OAuth user Not Found Error in Sign Up");
            return "OAuth User Check Error..";
        }

    }
}
