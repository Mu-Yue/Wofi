package com.wofi.constants;

public class Constants {
    //服务器地址
    public final static String URL="http://192.168.1.100:8000/";
    //API
    public final static String LOGIN_URL = URL+"/api-user-register/";   //userName
    public final static String RECHAGEBILL_URL =URL+"/api-user-queryRecharge/"; //userName
    public final static String RECHAGE_URL =URL+"/api-user-recharge/";  //{rechargeAmount}/{username}
    public final static String JOURNEY_URL =URL+"/api-user-queryBorrow/"; //userName
    public final static String USERINFO_URL =URL+"/api-user-userInfo/";   //userName
    public final static String CASH_URL =URL+"/api-user-submitUserCash/"; //userName
    public final static String GET_CASH_URL =URL+"/api-user-getUserCash/";//userName
    public final static String RETURN_CASH_URL =URL+"/api-user-returnUserCash/";//userName
    public final static String USERFEEDBACK_URL =URL+"/api-userFeedback-add/"; //反馈标题+内容+userName+bicycleId

    public final static String BICYCLE_URL =URL+"/api-bicycle-queryByLocation/"; //bicycleCurrentX（经度)+bicycleCurrentY（纬度）
    public final static String BORROWBICYCLE_URL=URL+"/api-borrow-borrowBicycle/";//{bicycleId}/{userName}
    public final static String RETURNBICYCLE_URL=URL+"/api-borrow-returnBicycle/";//{bicycleId}/{userName}/ex/ey/cost/end
    public final static String CURRENTBORROW_URL =URL+"/api-borrow-currentBorrow/"; //userName
}
