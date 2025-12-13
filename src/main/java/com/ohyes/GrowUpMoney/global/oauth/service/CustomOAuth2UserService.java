package com.ohyes.GrowUpMoney.global.oauth.service;

import com.ohyes.GrowUpMoney.domain.auth.dto.response.OAuth2Response;
import com.ohyes.GrowUpMoney.domain.auth.entity.CustomOAuth2User;
import com.ohyes.GrowUpMoney.domain.member.entity.Member;
import com.ohyes.GrowUpMoney.domain.auth.entity.SocialAccount;
import com.ohyes.GrowUpMoney.domain.auth.enums.SocialProvider;
import com.ohyes.GrowUpMoney.domain.member.repository.MemberRepository;
import com.ohyes.GrowUpMoney.domain.auth.repository.SocialAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;
    private final SocialAccountRepository socialAccountRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        Map<String,Object> attributes = oAuth2User.getAttributes();

        OAuth2Response response = OAuth2Response.builder()
                .providerId((String) attributes.get("sub"))
                .email((String) attributes.get("email"))
                .name((String) attributes.get("name"))
                .build();

        Member member = processOAuth2Login(response);

        return new CustomOAuth2User(member, attributes);
    }
    public Member processOAuth2Login(OAuth2Response response) {
        Optional<SocialAccount> existing = socialAccountRepository
                .findByProviderAndProviderId(SocialProvider.GOOGLE,response.getProviderId());

        if (existing.isPresent()) {
            return existing.get().getMember();
        }

        Member member = memberRepository.findByEmail(response.getEmail())
                .orElseGet(() -> createMember(response));

        SocialAccount socialAccount = new SocialAccount();
        socialAccount.setMember(member);
        socialAccount.setProvider(SocialProvider.GOOGLE);
        socialAccount.setProviderId(response.getProviderId());
        socialAccount.setEmail(response.getEmail());
        socialAccountRepository.save(socialAccount);

        return member;
    }

    private Member createMember(OAuth2Response response) {
        Member member = new Member();
        member.setEmail(response.getEmail());
        member.setUsername(response.getName());
        member.setPassword(null);
        return memberRepository.save(member);
    }

}
