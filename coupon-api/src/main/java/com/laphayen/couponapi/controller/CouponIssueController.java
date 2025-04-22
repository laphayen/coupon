package com.laphayen.couponapi.controller;


import com.laphayen.couponapi.controller.dto.CouponIssueRequestDto;
import com.laphayen.couponapi.controller.dto.CouponIssueResponseDto;
import com.laphayen.couponapi.service.CouponIssueRequestService;
import com.laphayen.couponcore.service.CouponIssueService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class CouponIssueController {

    private final CouponIssueRequestService couponIssueRequestService;

    @PostMapping("/v1/issue")
    public CouponIssueResponseDto issueV1(@RequestBody CouponIssueRequestDto body) {
        couponIssueRequestService.issueRequestV1(body);

        return new CouponIssueResponseDto(true, null);
    }


}
