package com.laphayen.couponapi.service;

import com.laphayen.couponapi.controller.dto.CouponIssueRequestDto;
import com.laphayen.couponcore.service.CouponIssueService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CouponIssueRequestService {

    private final CouponIssueService couponIssueService;

    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());

    public void issueRequestV1(CouponIssueRequestDto requestDto) {
        synchronized (this) {
            couponIssueService.issue(requestDto.couponId(), requestDto.userId());
        }

        log.info("쿠폰이 발급되었습니다. couponId: %s, userId: %s".formatted(requestDto.couponId(), requestDto.userId()));
    }

}
