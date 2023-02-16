package com.example.guess_music.service;

import com.example.guess_music.domain.auth.Member;
import com.example.guess_music.domain.auth.MemberDetail;
import com.example.guess_music.domain.auth.MemberForm;
import com.example.guess_music.repository.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
@Slf4j
@Transactional
public class MemberService implements UserDetailsService {
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    private final MemberRepository memberRepository;

    public String join(Member member){

        validateMember(member);
        //member.setPassword(getEncodedPassword(member.getPassword()));
        memberRepository.save(member);
        return member.getUsername();
    }
    private void validateMember(Member member){
        memberRepository.findbyUsername(member.getUsername()).ifPresent(m->{
            throw new IllegalStateException("already exist id");});
    }
    public List<Member> findMembers(){
        return memberRepository.findAll();
    }

    public Optional<Member> findOne(String id){ return memberRepository.findbyUsername(id); }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Member> byUsername = memberRepository.findbyUsername(username);
        if(byUsername.isPresent()){
            return new MemberDetail(byUsername.get());
        }
        throw new UsernameNotFoundException("cant find : "+username);
    }

    public void updateName(String name,String username){
        memberRepository.updateName(name,username);
    }

}
