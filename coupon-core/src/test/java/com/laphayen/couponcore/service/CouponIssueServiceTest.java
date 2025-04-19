package com.laphayen.couponcore.service;

import com.laphayen.couponcore.TestConfig;
import com.laphayen.couponcore.exception.CouponIssueException;
import com.laphayen.couponcore.exception.ErrorCode;
import com.laphayen.couponcore.model.Coupon;
import com.laphayen.couponcore.model.CouponIssue;
import com.laphayen.couponcore.model.CouponType;
import com.laphayen.couponcore.repository.CouponIssueRepository;
import com.laphayen.couponcore.repository.mysql.CouponIssueJpaRepository;
import com.laphayen.couponcore.repository.mysql.CouponJpaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CouponIssueServiceTest extends TestConfig {

    @Autowired
    CouponIssueService couponIssueService;

    @Autowired
    CouponIssueJpaRepository couponIssueJpaRepository;

    @Autowired
    CouponIssueRepository couponIssueRepository;

    @Autowired
    CouponJpaRepository couponJpaRepository;

    @BeforeEach
    void clean() {
        couponJpaRepository.deleteAllInBatch();
        couponIssueJpaRepository.deleteAllInBatch();

    }

    @Test
    @DisplayName("쿠폰 발급 내역이 존재하면 예외를 반환합니다.")
    void saveCouponIssue_1() {
        // given
        CouponIssue couponIssue = CouponIssue.builder()
                .couponId(1L)
                .userId(1L)
                .build();
        couponIssueJpaRepository.save(couponIssue);

        // when
        CouponIssueException exception = Assertions.assertThrows(CouponIssueException.class, () -> {
            couponIssueService.saveCouponIssue(couponIssue.getCouponId(), couponIssue.getUserId());
        });

        // then
        Assertions.assertEquals(exception.getErrorCode(), ErrorCode.DUPLICATE_COUPON_ISSUE);

    }

    @Test
    @DisplayName("쿠폰 발급 내역이 없으면 쿠폰을 발급합니다.")
    void saveCouponIssue_2() {
        // given
        long couponId = 1L;
        long userId = 1L;

        // when
        CouponIssue result = couponIssueService.saveCouponIssue(couponId, userId);

        // then
        Assertions.assertTrue(couponIssueJpaRepository.findById(result.getId()).isPresent());

    }

    @Test
    @DisplayName("발급 수량, 기한, 중복 발급 문제가 없다면 쿠폰을 발급합니다.")
    void issue_1() {
        // given
        long userId = 1;
        Coupon coupon = Coupon.builder()
                .couponType(CouponType.FIRST_COME_FIRST_SERVED)
                .title("선착순 테스트 쿠폰")
                .totalQuantity(100)
                .issuedQuantity(0)
                .dateIssueStart(LocalDateTime.now().minusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(1))
                .build();
        couponJpaRepository.save(coupon);

        // when
        couponIssueService.issue(coupon.getId(), userId);

        // then
        Coupon resultCoupon = couponJpaRepository.findById(coupon.getId()).get();
        Assertions.assertEquals(resultCoupon.getIssuedQuantity(), 1);

        CouponIssue couponIssueResult = couponIssueRepository.findFirstCouponIssue(coupon.getId(), userId);
        Assertions.assertNotNull(couponIssueResult);

    }

    @Test
    @DisplayName("발급 수량에 문제가 있으면 예외를 반환합니다.")
    void issue_2() {
        // given
        long userId = 1;
        Coupon coupon = Coupon.builder()
                .couponType(CouponType.FIRST_COME_FIRST_SERVED)
                .title("선착순 테스트 쿠폰")
                .totalQuantity(100)
                .issuedQuantity(100)
                .dateIssueStart(LocalDateTime.now().minusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(1))
                .build();
        couponJpaRepository.save(coupon);

        // when
        CouponIssueException exception = Assertions.assertThrows(CouponIssueException.class, () -> {
            couponIssueService.issue(coupon.getId(), userId);
        });

        // then
        Assertions.assertEquals(exception.getErrorCode(), ErrorCode.INVALID_COUPON_ISSUE_QUANTITY);

    }

    @Test
    @DisplayName("발급 기한에 문제가 있으면 예외를 반환합니다.")
    void issue_3() {
        // given
        long userId = 1;
        Coupon coupon = Coupon.builder()
                .couponType(CouponType.FIRST_COME_FIRST_SERVED)
                .title("선착순 테스트 쿠폰")
                .totalQuantity(100)
                .issuedQuantity(0)
                .dateIssueStart(LocalDateTime.now().minusDays(2))
                .dateIssueEnd(LocalDateTime.now().minusDays(1))
                .build();
        couponJpaRepository.save(coupon);

        // when
        CouponIssueException exception = Assertions.assertThrows(CouponIssueException.class, () -> {
            couponIssueService.issue(coupon.getId(), userId);
        });

        // then
        Assertions.assertEquals(exception.getErrorCode(), ErrorCode.INVALID_COUPON_ISSUE_DATE);

    }

    @Test
    @DisplayName("중복 발급에 문제가 있으면 예외를 반환합니다.")
    void issue_4() {
        // given
        long userId = 1;
        Coupon coupon = Coupon.builder()
                .couponType(CouponType.FIRST_COME_FIRST_SERVED)
                .title("선착순 테스트 쿠폰")
                .totalQuantity(100)
                .issuedQuantity(0)
                .dateIssueStart(LocalDateTime.now().minusDays(2))
                .dateIssueEnd(LocalDateTime.now().plusDays(1))
                .build();
        couponJpaRepository.save(coupon);

        CouponIssue couponIssue = CouponIssue.builder()
                .couponId(coupon.getId())
                .userId(userId)
                .build();
        couponIssueJpaRepository.save(couponIssue);

        // when
        CouponIssueException exception = Assertions.assertThrows(CouponIssueException.class, () -> {
            couponIssueService.issue(coupon.getId(), userId);
        });

        // then
        Assertions.assertEquals(exception.getErrorCode(), ErrorCode.DUPLICATE_COUPON_ISSUE);

    }

    @Test
    @DisplayName("쿠폰이 없으면 예외를 반환합니다.")
    void issue_5() {
        // given
        long userId = 1;
        long couponId = 1;

        // when
        CouponIssueException exception = Assertions.assertThrows(CouponIssueException.class, () -> {
            couponIssueService.issue(couponId, userId);
        });

        // then
        Assertions.assertEquals(exception.getErrorCode(), ErrorCode.COUPON_NOT_EXIST);

    }


}
