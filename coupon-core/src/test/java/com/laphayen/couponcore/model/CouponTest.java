package com.laphayen.couponcore.model;

import com.laphayen.couponcore.exception.CouponIssueException;
import com.laphayen.couponcore.exception.ErrorCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CouponTest {

    @Test
    @DisplayName("발급 수량이 남아 있으면 true를 반환합니다.")
    void availableIssueQuantity_1() {

        // given
        Coupon coupon = Coupon.builder()
                .totalQuantity(100)
                .issuedQuantity(99)
                .build();

        // when
        boolean result = coupon.availableIssueQuantity();

        // then
        Assertions.assertTrue(result);

    }

    @Test
    @DisplayName("발급 수량이 없으면 false를 반환합니다.")
    void availableIssueQuantity_2() {

        // given
        Coupon coupon = Coupon.builder()
                .totalQuantity(100)
                .issuedQuantity(100)
                .build();

        // when
        boolean result = coupon.availableIssueQuantity();

        // then
        Assertions.assertFalse(result);

    }

    @Test
    @DisplayName("최대 발급 수량이 설정되지 않으면 true를 반환합니다.")
    void availableIssueQuantity_3() {

        // given
        Coupon coupon = Coupon.builder()
                .totalQuantity(null)
                .issuedQuantity(99)
                .build();

        // when
        boolean result = coupon.availableIssueQuantity();

        // then
        Assertions.assertTrue(result);

    }

    @Test
    @DisplayName("발급 기간이 시작되지 않으면 false를 반환합니다.")
    void availableIssueDate_1() {

        // given
        Coupon coupon = Coupon.builder()
                .dateIssueStart(LocalDateTime.now().plusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(2))
                .build();

        // when
        boolean result = coupon.availableIssueDate();

        // then
        Assertions.assertFalse(result);

    }

    @Test
    @DisplayName("발급 기간이면 true를 반환합니다.")
    void availableIssueDate_2() {

        // given
        Coupon coupon = Coupon.builder()
                .dateIssueStart(LocalDateTime.now().minusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(2))
                .build();

        // when
        boolean result = coupon.availableIssueDate();

        // then
        Assertions.assertTrue(result);

    }

    @Test
    @DisplayName("발급 기간이 종료되면 false를 반환합니다.")
    void availableIssueDate_3() {

        // given
        Coupon coupon = Coupon.builder()
                .dateIssueStart(LocalDateTime.now().minusDays(2))
                .dateIssueEnd(LocalDateTime.now().minusDays(1))
                .build();

        // when
        boolean result = coupon.availableIssueDate();

        // then
        Assertions.assertFalse(result);

    }

    @Test
    @DisplayName("발급 수량과 발급 기간이 유효하면 쿠폰을 발급합니다.")
    void issue_1() {

        // given
        Coupon coupon = Coupon.builder()
                .totalQuantity(100)
                .issuedQuantity(99)
                .dateIssueStart(LocalDateTime.now().minusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(2))
                .build();

        // when
        coupon.issue();

        // then
        Assertions.assertEquals(coupon.getIssuedQuantity(), 100);

    }

    @Test
    @DisplayName("발급 수량을 초과하면 예외를 반환합니다.")
    void issue_2() {

        // given
        Coupon coupon = Coupon.builder()
                .totalQuantity(100)
                .issuedQuantity(100)
                .dateIssueStart(LocalDateTime.now().minusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(2))
                .build();

        // when
        CouponIssueException exception = Assertions.assertThrows(CouponIssueException.class, coupon::issue);

        // then
        Assertions.assertEquals(exception.getErrorCode(), ErrorCode.INVALID_COUPON_ISSUE_QUANTITY);

    }

    @Test
    @DisplayName("발급 기간을 초과하면 예외를 반환합니다.")
    void issue_3() {

        // given
        Coupon coupon = Coupon.builder()
                .totalQuantity(100)
                .issuedQuantity(99)
                .dateIssueStart(LocalDateTime.now().plusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(2))
                .build();

        // when
        CouponIssueException exception = Assertions.assertThrows(CouponIssueException.class, coupon::issue);

        // then
        Assertions.assertEquals(exception.getErrorCode(), ErrorCode.INVALID_COUPON_ISSUE_DATE);

    }


}
