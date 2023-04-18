package com.apiDemo.demo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Ordering;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.util.DigestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ApiDemo {
    private static final Logger logger = LoggerFactory. getLogger(ApiDemo. class);
    //appKey, appSecret
    private static final String appKey = "MYStu87T";
    private static final String appSecret = "t10GzlT3mrOio6d3nktx784uNgkpep8w";
    //domain name
    private static final String URL = "https://test01.uexch.com/api";
    //Trading pair name
    private static final String COIN_MARKET = "SSV/USDT";
    //order path
    private static final String ORDER_PATH = "/order/market-user/add-market-user-entrust";
    //Cancellation path
    private static final String CANCEL_PATH = "/order/market-user/cancel-user-entrust";
    // Get the market
    private static final String TICKERS_PATH = "/market/tickers";
    // Get the market [depth] path
    private static final String DEPTH_PATH = "/market/depth";
    //Order order query path
    private static final String GET_ORDER_LIST = "/order/market-user/user-entrust-list";

    public static String sign(Map<String, Object> params){
        //key sorting
        List<String> keys = Ordering. usingToString(). sortedCopy(params. keySet());
        StringBuilder sb = new StringBuilder();
        for (String k : keys) {
            k = k.replace(" ","");
            sb.append(k).append("=").append(params.get(k).toString().replace(" ","")).append("&");
        }
        sb.append("appSecretKey").append("=").append(appSecret);
        //Encrypted signature verification parameters
        String signature = DigestUtils.md5DigestAsHex(sb.toString().getBytes());
        return signature;
    }



    public static void main(String[] args) {

        // Get the market
        Tickers tickers = getTickers();
        // get depth
        Depth depth = getDepth();
        // Get the order in progress
        List<OrderList> orderLists = getOrderList();

        List<String> entrustNoList = new ArrayList<>();

        for (OrderList order : orderLists) {

            entrustNoList.add(order.getEntrustNo());
        }
        if (!entrustNoList.isEmpty()) {
            // cancel the order
            cancelOrder(entrustNoList);

            try {
                TimeUnit. SECONDS. sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        //Order type: 1-buy, 2-sell
        String type = "1";
        //Order price type: 1-limit order, 2-market order
        String orderType = "1";
        //order quantity
        String bamount1 = "1.078";
        String samount1 = "2.078";

        //order price
        String buyPrice1 = "2.078";
        String sellPrice1 = "2.078";


        String buy1 = addOrder(type, orderType, bamount1, buyPrice1);

        String sell1 = addOrder("2", orderType, samount1, sellPrice1);

    }


    /**
     * post request
     *
     * @param path
     * @param params
     * @return
     */
    public static ResponseData testForm(String path, Map<String, Object> params) {
        // request header
        HttpHeaders headers = new HttpHeaders();
        //time stamp
        String time = String. valueOf(System. currentTimeMillis());
        if (params == null) {
            params = new HashMap<>();
        }
        params.put("appKey", appKey);
        params. put("time", time);
        params.put("coinMarket", COIN_MARKET);


        //Encrypted signature verification parameters
        String signature = sign(params);

        // Request header settings, data in application/json format
        headers.add("Content-Type", "application/json");
        headers. add("channel", "5");
        headers. add("signature", signature);
        headers.add("time", time);

        RestTemplate restTemplate = new RestTemplate();
        // send post request
        HttpEntity<Map<String, Object>> formEntity = new HttpEntity<Map<String, Object>>(params, headers);
        return restTemplate.postForObject(URL + path, formEntity, ResponseData.class);
    }

    /**
     * place an order
     *
     * @param type 1-buy, 2-sell
     * @param orderType 1- limit price, 2- market price
     * @param amount order quantity
     * @param price order price
     * @return
     */
    public static String addOrder(String type, String orderType, String amount, String price) {

        //request parameters
        Map<String, Object> params = new HashMap<>();
        params. put("type", type);
        params. put("orderType", orderType);
        params. put("amount", amount);
        params. put("price", price);

        ResponseData responseData = testForm(ORDER_PATH, params);

        // output return value
        logger.warn("order-return: {}", JSON.toJSONString(responseData));
        return responseData.getData().toString();
    }


    /**
     * cancel order
     *
     * @param entrustNoList entrustment number collection
     */
    public static void cancelOrder(List<String> entrustNoList) {
        if (entrustNoList.isEmpty()) {
            return;
        }

        //request parameters
        Map<String, Object> paramsCancel = new HashMap<>();
        //{"cancelEntrustList":[{"entrustNo":"160945950498754561"},{"entrustNo":"160945950498754562"}],"appKey":"6rUUFypA"}

        //A collection of order numbers, the order number will be returned when the order is placed, and the query order will also be returned
        List<JSONObject> cancelEntrustList = new ArrayList<>();
        for (String entrustNo : entrustNoList) {
            JSONObject paramsCancels = new JSONObject();
            paramsCancels. put("entrustNo", entrustNo);
            cancelEntrustList.add(paramsCancels);
        }

        paramsCancel.put("cancelEntrustList", cancelEntrustList);

        ResponseData responseData = testForm(CANCEL_PATH, paramsCancel);

        // output return value
        logger.warn("Cancel order - return: {}", JSON.toJSONString(responseData));

    }

    /**
     * Get quotes
     *
     * @return
     */
    public static Depth getDepth() {
        ResponseData responseData = testForm(DEPTH_PATH, null);
        logger.warn("Call depth - return: {}", JSON.toJSONString(responseData));
        return JSON.parseObject(JSONObject.toJSONString(responseData.getData()), Depth.class);
    }

    /**
     * get depth
     *
     * @return
     */
    public static Tickers getTickers() {
        ResponseData responseData = testForm(TICKERS_PATH, null);
        logger.warn("Call quotes - return: {}", JSON.toJSONString(responseData));
        List<JSONObject> orderLists = JSON.parseObject(JSONObject.toJSONString(responseData.getData()), List.class);
        for (JSONObject jsonObject : orderLists) {
            if (jsonObject. get("coinMarket"). equals(COIN_MARKET)) {
                return JSON.parseObject(jsonObject.toJSONString(), Tickers.class);
            }
        }
        return null;
    }

    /**
     * Get the list of orders in progress
     *
     * @return
     */
    public static List<OrderList> getOrderList() {
        ResponseData<List<OrderList>> responseData = testForm(GET_ORDER_LIST, null);
        // output return value
        logger.warn("query order - return: {}", JSON.toJSONString(responseData));
        List<OrderList> list = new ArrayList<>();
        List<JSONObject> orderLists = JSON.parseObject(JSONObject.toJSONString(responseData.getData()), List.class);
        for (JSONObject jsonObject : orderLists) {
            OrderList orderList = JSON.parseObject(jsonObject.toJSONString(), OrderList.class);
            list. add(orderList);
        }
        return list;
    }


}