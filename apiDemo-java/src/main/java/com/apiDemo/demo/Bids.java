package com.apiDemo.demo;

import java.math.BigDecimal;

public class Bids {
    /**
     * 委托价格
     */
    private BigDecimal trustPrice;
    /**
     * 委托数量
     */
    private BigDecimal lastNumber;

    public BigDecimal getTrustPrice() {
        return trustPrice;
    }

    public void setTrustPrice(BigDecimal trustPrice) {
        this.trustPrice = trustPrice;
    }

    public BigDecimal getLastNumber() {
        return lastNumber;
    }

    public void setLastNumber(BigDecimal lastNumber) {
        this.lastNumber = lastNumber;
    }
}
