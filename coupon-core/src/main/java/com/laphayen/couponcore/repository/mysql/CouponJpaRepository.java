package com.laphayen.couponcore.repository.mysql;

import com.laphayen.couponcore.model.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponJpaRepository extends JpaRepository<Coupon, Long> {


}
