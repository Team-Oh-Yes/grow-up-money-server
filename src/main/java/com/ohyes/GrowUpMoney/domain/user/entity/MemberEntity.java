package com.ohyes.GrowUpMoney.domain.user.entity;

import com.ohyes.GrowUpMoney.domain.user.enums.MemberStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "tb_member")
public class MemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false, columnDefinition = "int default 0")
    private Integer point_balance = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(20) default 'ACTIVE'")
    private MemberStatus status = MemberStatus.ACTIVE;

    @Column
    private LocalDateTime suspended_until;  // 정지 종료 시간

    @Column
    private String suspension_reason;  // 정지 사유

    @Column(nullable = false, columnDefinition = "VARCHAR(255) default 'user'")
    private String role = "ROLE_USER";

    @CreationTimestamp
    private LocalDateTime created_at;

    @CreationTimestamp
    private LocalDateTime updated_at;

    public void suspend(int days, String reason) {
        this.status = MemberStatus.SUSPENDED;
        this.suspension_reason = reason;

        if (days == -1) {  // 영구 정지
            this.suspended_until = LocalDateTime.of(9999, 12, 31, 23, 59, 59);
        } else {
            this.suspended_until = LocalDateTime.now().plusDays(days);
        }
    }

    public void unsuspend() {
        this.status = MemberStatus.ACTIVE;
        this.suspended_until = null;
        this.suspension_reason = null;
    }

    public void withdraw() {
        this.status = MemberStatus.WITHDRAWN;
        this.suspended_until = null;
        this.suspension_reason = "회원 탈퇴";
    }

    public boolean isSuspensionExpired() {
        if (this.status != MemberStatus.SUSPENDED) {
            return false;
        }
        return this.suspended_until != null &&
                LocalDateTime.now().isAfter(this.suspended_until);
    }

    public boolean isActive() {
        return this.status == MemberStatus.ACTIVE;
    }
}
