package com.laphayen.couponcore.repository.mysql;

import com.laphayen.couponcore.model.CouponIssue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponIssueJpaRepository extends JpaRepository<CouponIssue, Long> {


}
