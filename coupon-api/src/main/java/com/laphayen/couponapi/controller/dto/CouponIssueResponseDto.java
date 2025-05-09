package com.laphayen.couponapi.controller.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(value = JsonInclude.Include.NON_NULL)
public record CouponIssueResponseDto(boolean isSuccess, String comment) {


}
