package com.apiDemo.demo;

public class OrderList {

    /**
     * 委托单号
     */
    private String entrustNo;
    /**
     * 委托价格
     */
    private String price;
    /**
     * 委托数量
     */
    private String amount;
    /**
     * 成交数量
     */
    private String dealAmount;
    /**
     * 剩余数量
     */
    private String remainingNumber;
    /**
     * 1-买，2-卖
     */
    private Integer type;

    /**
     * 时间
     */
    private Long createTime;



    public String getEntrustNo() {
        return entrustNo;
    }

    public void setEntrustNo(String entrustNo) {
        this.entrustNo = entrustNo;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDealAmount() {
        return dealAmount;
    }

    public void setDealAmount(String dealAmount) {
        this.dealAmount = dealAmount;
    }

    public String getRemainingNumber() {
        return remainingNumber;
    }

    public void setRemainingNumber(String remainingNumber) {
        this.remainingNumber = remainingNumber;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }
}
