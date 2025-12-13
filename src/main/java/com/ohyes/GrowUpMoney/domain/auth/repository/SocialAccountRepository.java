package com.ohyes.GrowUpMoney.domain.auth.repository;

import com.ohyes.GrowUpMoney.domain.member.entity.Member;
import com.ohyes.GrowUpMoney.domain.auth.entity.SocialAccount;
import com.ohyes.GrowUpMoney.domain.auth.enums.SocialProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SocialAccountRepository extends JpaRepository<SocialAccount,Long> {
    Optional<SocialAccount>findByProviderAndProviderId(SocialProvider provider, String providerId);

    List<SocialAccount>findByMember(Member member);
}
