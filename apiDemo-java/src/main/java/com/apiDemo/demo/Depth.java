package com.apiDemo.demo;

import java.util.List;

public class Depth {
    private String symbolKey;
    /**
     * 买盘
     */
    private List<Bids> bids;
    /**
     * 卖盘
     */
    private List<Bids> asks;

    public String getSymbolKey() {
        return symbolKey;
    }

    public void setSymbolKey(String symbolKey) {
        this.symbolKey = symbolKey;
    }

    public List<Bids> getBids() {
        return bids;
    }

    public void setBids(List<Bids> bids) {
        this.bids = bids;
    }

    public List<Bids> getAsks() {
        return asks;
    }

    public void setAsks(List<Bids> asks) {
        this.asks = asks;
    }
}
