package com.ohyes.GrowUpMoney.domain.user.service;

import com.ohyes.GrowUpMoney.domain.user.dto.request.SuspendMemberRequest;
import com.ohyes.GrowUpMoney.domain.user.dto.response.MemberStatusResponse;
import com.ohyes.GrowUpMoney.domain.user.entity.Member;
import com.ohyes.GrowUpMoney.domain.user.enums.MemberStatus;
import com.ohyes.GrowUpMoney.domain.user.exception.UserNotFoundException;
import com.ohyes.GrowUpMoney.domain.user.repository.MemberRepository;
import com.ohyes.GrowUpMoney.global.exception.NotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberStatusService {

    private final MemberRepository memberRepository;

    //회원 정지 여부 조회
    public MemberStatusResponse getMemberStatus(String username){
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);

        if (member.isSuspensionExpired()) {
            member.unsuspend();
            memberRepository.save(member);
        }

        return new MemberStatusResponse(
                member.getUsername(),
                member.getStatus(),
                member.getStatus().getDescription(),
                member.getSuspended_until(),
                member.getSuspension_reason(),
                member.isActive()
        );
    }

    //회원 정지
    @Transactional
    public void suspendMember(SuspendMemberRequest request){
        Member member = memberRepository.findByUsername(request.getUsername())
                .orElseThrow(UserNotFoundException::new);
        member.suspend(request.getSuspensionType().getDays(), request.getReason());
        memberRepository.save(member);
    }

    @Transactional
    public void unsuspendMember(String username){
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);

        member.unsuspend();
        memberRepository.save(member);
    }

    //회원 탈퇴
    @Transactional
    public void withdrawMember(String username){
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);
        member.withdraw();
        memberRepository.save(member);
    }

    @Transactional
    public void releaseExpiredSuspensions() {
        memberRepository.findAll().stream()
                .filter(Member::isSuspensionExpired)
                .forEach(member -> {
                    member.unsuspend();
                    memberRepository.save(member);
                });
    }

}

