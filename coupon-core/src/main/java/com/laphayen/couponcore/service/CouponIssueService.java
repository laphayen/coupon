package com.laphayen.couponcore.service;

import com.laphayen.couponcore.exception.CouponIssueException;
import com.laphayen.couponcore.model.Coupon;
import com.laphayen.couponcore.model.CouponIssue;
import com.laphayen.couponcore.repository.CouponIssueRepository;
import com.laphayen.couponcore.repository.mysql.CouponIssueJpaRepository;
import com.laphayen.couponcore.repository.mysql.CouponJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.laphayen.couponcore.exception.ErrorCode.COUPON_NOT_EXIST;
import static com.laphayen.couponcore.exception.ErrorCode.DUPLICATE_COUPON_ISSUE;

@RequiredArgsConstructor
@Service
public class CouponIssueService {

    private final CouponJpaRepository couponJpaRepository;

    private final CouponIssueJpaRepository couponIssueJpaRepository;

    private final CouponIssueRepository couponIssueRepository;

    @Transactional
    public void issue(long couponId, long userId) {
        Coupon coupon = findCoupon(couponId);
        coupon.issue();
        saveCouponIssue(couponId, userId);

    }

    @Transactional(readOnly = true)
    public Coupon findCoupon(long couponId) {
        return couponJpaRepository.findById(couponId).orElseThrow(() -> new CouponIssueException(COUPON_NOT_EXIST, "쿠폰 정책이 존재하지 않습니다. %s".formatted(couponId)));

    }

    @Transactional
    public CouponIssue saveCouponIssue(long couponId, long userId) {
        checkAlreadyIssued(couponId, userId);
        CouponIssue issue = CouponIssue.builder()
                .couponId(couponId)
                .userId(userId)
                .build();
        return couponIssueJpaRepository.save(issue);

    }

    private void checkAlreadyIssued(long couponId, long userId) {
        CouponIssue issue = couponIssueRepository.findFirstCouponIssue(couponId, userId);
        if (issue != null) {
            throw new CouponIssueException(DUPLICATE_COUPON_ISSUE, "발급된 쿠폰입니다. user_id: %s, coupon_id: %s".formatted(userId, couponId));
        }


    }


}
