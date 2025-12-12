package com.ohyes.GrowUpMoney.domain.auth.service;

import com.ohyes.GrowUpMoney.domain.auth.dto.request.GrantPointRequest;
import com.ohyes.GrowUpMoney.domain.auth.dto.response.MemberResponse;
import com.ohyes.GrowUpMoney.domain.auth.entity.Member;
import com.ohyes.GrowUpMoney.domain.auth.enums.PointType;
import com.ohyes.GrowUpMoney.domain.auth.exception.UserNotFoundException;
import com.ohyes.GrowUpMoney.domain.auth.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public Page<MemberResponse> getMembers(int page, int size){
        Pageable pageable = PageRequest.of(page,size);
        Page<Member> members = memberRepository.findAll(pageable);
        return members.map(MemberResponse::from);
    }

    @Transactional
    public void grantPoint(GrantPointRequest request) {
        Member member = memberRepository.findByUsername(request.getUsername())
                .orElseThrow(UserNotFoundException::new);

        if (request.getPointType() == PointType.TRADEABLE) {
            member.addPoint(request.getAmount());
        } else {
            member.addBoundPoint(request.getAmount());
        }

        memberRepository.save(member);
    }
}
