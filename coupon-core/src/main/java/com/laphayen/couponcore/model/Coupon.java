package com.laphayen.couponcore.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "coupons")
public class Coupon extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private CouponType couponType;

    private Integer totalQuantity;

    @Column(nullable = false)
    private int issuedQuantity;

    @Column(nullable = false)
    private int discountAmount;

    @Column(nullable = false)
    private int minAvailableAmount;

    @Column(nullable = false)
    private LocalDateTime dateIssueStart;

    @Column(nullable = false)
    private LocalDateTime dateIssueEnd;

    // 발급 수량이 남아있는지 확인합니다.
    public boolean availableIssueQuantity() {
        if (totalQuantity == null) {
            return true;
        }
        return totalQuantity > issuedQuantity;

    }

    // 현재 시각이 발급 기간 내인지 확인합니다.
    public boolean availableIssueDate() {
        LocalDateTime now = LocalDateTime.now();

        return dateIssueStart.isBefore(now) && dateIssueEnd.isAfter(now);

    }

    // 쿠폰을 발급합니다.
    public void issue() {
        if (!availableIssueQuantity()) {
            throw new RuntimeException("쿠폰 발급 가능 수량을 초과했습니다.");
        }
        if (!availableIssueDate()) {
            throw new RuntimeException("쿠폰 발급 가능 기간이 아닙니다.");
        }
        issuedQuantity++;


    }


}
