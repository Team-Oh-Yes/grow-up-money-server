package com.ohyes.GrowUpMoney.domain.admin.member.service;

import com.ohyes.GrowUpMoney.domain.auth.dto.request.SuspendMemberRequest;
import com.ohyes.GrowUpMoney.domain.auth.dto.response.MemberStatusResponse;
import com.ohyes.GrowUpMoney.domain.member.entity.Member;
import com.ohyes.GrowUpMoney.domain.auth.enums.MemberStatus;
import com.ohyes.GrowUpMoney.domain.auth.exception.UserNotFoundException;
import com.ohyes.GrowUpMoney.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberSuspensionService {

    private final MemberRepository memberRepository;

    public MemberStatusResponse getMemberStatus(String username){
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);

        if (member.isSuspensionExpired()) {
            unsuspendMember(member.getUsername());
            member = memberRepository.findByUsername(username).get();
        }

        return new MemberStatusResponse(
                member.getUsername(),
                member.getStatus(),
                member.getStatus().getDescription(),
                member.getSuspendedUntil(),
                member.getSuspensionReason(),
                member.isActive()
        );
    }

    public void suspendMember(SuspendMemberRequest request){
        Member member = memberRepository.findByUsername(request.getUsername())
                .orElseThrow(UserNotFoundException::new);
        member.suspend(request.getSuspensionType().getDays(), request.getReason(),request.getSuspensionType());
        memberRepository.save(member);
    }

    public void unsuspendMember(String username){
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);

        if (member.getStatus() != MemberStatus.SUSPENDED) {
            throw new IllegalStateException("정지된 회원만 해제할 수 있습니다");
        }

        member.unsuspend();
        memberRepository.save(member);
    }

    public void withdrawMember(String username){
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);
        member.withdraw();
        memberRepository.save(member);
    }

    public void releaseExpiredSuspensions() {
        memberRepository.findAll().stream()
                .filter(Member::isSuspensionExpired)
                .forEach(member -> {
                    member.unsuspend();
                    memberRepository.save(member);
                });
    }


}
