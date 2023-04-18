# -*- coding: UTF-8 -*-
#demo
API_KEY="Yox1FyXb"
API_SECRET="2GaAD2Op3E8W7rvNpbLVNWIojhsJRBb6"
base_url = "https://test01.uexch.com/api/"

# 下单逻辑
day_trading_volume = 100
trading_frequence = 1
min_amount = 10
amountPrecision = 4
# 价格精度
pricePrecision = 2
# 变量
volume_change_rate = 0.01
# price=23000
ask1 = 1626
bid1 = 1625
middle_price = (ask1 + bid1) / 2
symbol = 'ETH/USDT'
import math, random, time, hashlib, requests
import pandas as pd

# 下单部分
api_dict = {"order_api": "order/market-user/add-market-user-entrust",
            "check_order": "order/market-user/user-entrust-list",
            "order_history": "order/entrust-detail-list",
            "check_depth": "market/depth",
            "market_data": "market/tickers",
            "cancel_api": "order/market-user/cancel-user-entrust",
            "query_account": "account/query/coin-account"}

# 获取最新价
def get_signature(param_dict):
    keys = sorted(param_dict.keys())
    qs0 = ""
    for key in keys:
        qs0 = qs0 + str(key) + '=' + str(param_dict[key]) + '&'
    qs0 += f"appSecretKey={API_SECRET}"
    qs0 = qs0.replace("'", "\"")
    # 去空格
    qs0 = ''.join(qs0.split())
    # print(f"qs0:{qs0}")
    hl = hashlib.md5()
    # print(qs0)
    hl.update(qs0.encode(encoding='utf-8'))
    signature = hl.hexdigest()
    return signature


def get_ticker_data(param_dict):
    req_time = int(time.time())
    signature = get_signature(param_dict)
    header = {"signature": signature, "channel": "5", "time": str(req_time),
              "api-version": "v1", "Content-Typ": "application/json"}
    # print(header)
    # del param_dict["time"]
    res = requests.post(base_url + api_dict["market_data"], json=param_dict, headers=header)
    # print(res.url)
    # print(res.json())
    data = res.json()['data']
    # print(data)
    return data

def get_symbol_dic(param_dict):
    data = get_ticker_data(param_dict)
    symbol_dic = {}
    n = len(data)
    for i in range(n):
        symbol = data[i]['coinMarket']
        symbol_dic[symbol] = data[i]
    return symbol_dic


def get_last_price(symbol):
    req_time = int(time.time())
    param_dict = {"appKey": API_KEY, "time": req_time}
    symbol_dic = get_symbol_dic(param_dict)

    price = float(symbol_dic[symbol]['lastPrice'])
    return price


def create_order(syboml, type_, amount, order_type, price):
    """
    type_：1买入 2卖出
    order_type=1限价单
    """
    req_time = int(time.time())
    param_dict = {"appKey": API_KEY, "time": req_time, "type": type_, "coinMarket": syboml, "amount": amount,
                  "orderType": order_type, "price": price}
    signature = get_signature(param_dict)
    # print(f"signature:{signature}")
    header = {"signature": signature, "channel": "5", "time": str(req_time), "api-version": "v1",
              "Content-Typ": "application/json"}
    order_api = api_dict['order_api']
    res = requests.post(base_url + order_api, json=param_dict, headers=header)
    # print(f"res.url：{res.url}")
    data = res.json()
    print(f"下单:{data}")
    return data

# 查询订单
def query_order(syboml):
    req_time = int(time.time())
    param_dict = {"appKey": API_KEY, "time": req_time, "coinMarket": syboml}
    signature = get_signature(param_dict)
    # print(f"signature:{signature}")
    header = {"signature": signature, "channel": "5", "time": str(req_time), "api-version": "v1",
              "Content-Typ": "application/json"}
    res = requests.post(base_url + api_dict['check_order'], json=param_dict, headers=header)
    # print(f"res.url：{res.url}")
    # print(f"res.json():{res.json()}")
    # 获取订单列表
    data = res.json()['data']
    data = res.json()['data']
    orderNo_df = pd.DataFrame(data=data)
    print(f"orderNo_df:{orderNo_df}")
    if len(orderNo_df) > 0:
        orderNo_list = [x for x in orderNo_df.entrustNo]
    else:
        orderNo_list = []
    print(f"orderNo_list:{orderNo_list}")
    return orderNo_list

# 撤单
def cancel_order(orderNo_list):
    print(f"执行撤单：{orderNo_list}")
    req_time = int(time.time())
    cancelEntrustList = []
    for order_no in orderNo_list:
        order_dic = {}
        order_dic['entrustNo'] = order_no
        cancelEntrustList.append(order_dic)
    # cancelEntrustList=[{ "entrustNo":"500820963103772672"},{"entrustNo":"500819439992606720"}]
    param_dict = {"appKey": API_KEY, "time": req_time, "cancelEntrustList": cancelEntrustList}
    print(f"param_dict:{param_dict}")
    signature = get_signature(param_dict)
    # signature="d21e6e84c8a0dbe06b0041716dfe7e94"
    print(f"signature:{signature}")
    header = {"signature": signature, "channel": "5", "time": str(req_time), "api-version": "v1",
              "Content-Typ": "application/json"}
    # print(f"cancel_api:{url+cancel_api}")
    # url="https://test01.bifinance.com/api/order/market-user/add-market-user-entrust"
    # res = requests.post(url=url, json=param_dict, headers=header)
    res = requests.post(base_url + api_dict['cancel_api'], json=param_dict, headers=header)
    # print(f"res.url：{res.url}")
    print(f"撤单:{res.json()}")

def query_account(coinName):
    """
    coinName:USDT、BTC
    """
    req_time = int(time.time())
    param_dict = {"appKey": API_KEY, "time": req_time, "coinName": coinName, "accountType": 1}
    signature = get_signature(param_dict)

    # print(f"signature:{signature}")

    header = {"signature": signature, "channel": "5", "time": str(req_time), "api-version": "v1",
              "Content-Typ": "application/json"}

    res = requests.post(base_url + api_dict['query_account'], json=param_dict, headers=header)

    # print(f"res.url：{res.url}")
    # print(f"res.json():{res.json()}")
    # 获取订单列表
    data = res.json()['data']
    account_coin = data['amount']
    print(f"{coinName}数量:{account_coin}")
    return account_coin


# 获取盘口
def get_depth(symbol):
    # symbol="PETH/USDT"
    req_time = int(time.time())
    param_dict = {"appKey": API_KEY, "time": req_time, "coinMarket": symbol}
    signature = get_signature(param_dict)
    # print(f"signature:{signature}")
    header = {"signature": signature, "channel": "5", "time": str(req_time), "api-version": "v1",
              "Content-Typ": "application/json"}
    _api = api_dict['check_depth']
    res = requests.post(base_url + _api, json=param_dict, headers=header)
    # print(f"res.url：{res.url}")
    data = res.json()
    # print(f"depth:{data}")
    if data['data'] is not None:
        ask_df = pd.DataFrame(data['data']['asks'])
        bid_df = pd.DataFrame(data['data']['bids'])
        # print(f"ask_df:{ask_df}")
        # print(f"bid_df:{bid_df}")
        ask1 = float(ask_df["trustPrice"].iloc[0])
        bid1 = float(bid_df["trustPrice"].iloc[0])
        dict = {"ask1": ask1, "bid1": bid1}
        print(f"{dict}")
        return dict
    else:
        print(f"orderbook为空")


if __name__ == '__main__':
    req_time = int(time.time())
    param_dict = {"appKey": API_KEY, "time": req_time}
    data = get_ticker_data(param_dict)
    print(data)

