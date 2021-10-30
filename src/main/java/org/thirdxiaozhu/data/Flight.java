package org.thirdxiaozhu.data;

import org.thirdxiaozhu.AirportMap;

import java.util.*;

public class Flight {
    //没有状态
    public static int NOSTATE = 0;
    //开始值机
    public static int CKIE = 1;
    //停止值机
    public static int CKOE = 3;

    //国内
    public static int DOMESTIC = 2403;
    //国际
    public static int INTERNATIONAL = 2401;
    //混合
    public static int BLEND = 2404;
    //地区
    public static int REGIONAL = 2402;


    public static int ARRIVE = 1001;
    public static int DEPART = 1002;


    public String flid;
    public String ffid;
    public String flightId;
    public String ckno;
    //预计占用航显开始时间
    public Date stls_estr;
    //预计占用航显结束时间
    public Date stls_eend;
    //实际占用航显开始时间
    public Date stls_rstr;
    //实际占用航显结束时间
    public Date stls_rend;
    //值机开始时间
    public Date ckie_fcrs;
    //值机结束时间
    public Date ckoe_fcre;
    //预测值机开始
    public Date forecast_fcrs;
    //预测值机结束时间
    public Date forecast_fcre;
    public int state;
    public int fatt;
    public int rec_dep;
    public boolean onBoard;

    public Map<Integer, String> apcds;


    public Flight(String flid, String ffid, String fatt) {
        apcds = new HashMap<>();
        setFlid(flid);
        setFfid(ffid);
        setFatt(Integer.parseInt(fatt));
        setFlightId((ffid.split("-")[0] + ffid.split("-")[1]).replace("-", ""));
        setRec_dep(ffid.split("-")[3]);
        setState(NOSTATE);
    }

    public String getFlid() {
        return flid;
    }

    public void setFlid(String flid) {
        this.flid = flid;
    }

    public String getFfid() {
        return ffid;
    }

    public void setFfid(String ffid) {
        this.ffid = ffid;
    }

    public String getFlightId() {
        return flightId;
    }

    public void setFlightId(String flightId) {
        this.flightId = flightId;
    }

    public int getFatt() {
        return fatt;
    }


    public void setFatt(int fatt) {
        this.fatt = fatt;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public Date getStls_estr() {
        return stls_estr;
    }

    public void setStls_estr(Date stls_estr) {
        this.stls_estr = stls_estr;
    }

    public Date getStls_eend() {
        return stls_eend;
    }

    public void setStls_eend(Date stls_eend) {
        this.stls_eend = stls_eend;

        if(stls_eend != null) {
            //开始办票时间（起飞时间-2小时）
            Calendar pre_Cal = Calendar.getInstance();
            pre_Cal.setTime(this.stls_eend);
            pre_Cal.add(Calendar.HOUR_OF_DAY, -2);

            //结束办票时间（起飞时间-40分钟）
            Calendar aft_Cal = Calendar.getInstance();
            aft_Cal.setTime(this.stls_eend);
            aft_Cal.add(Calendar.MINUTE, -40);

            setForecast_fcrs(pre_Cal.getTime());
            setForecast_fcre(aft_Cal.getTime());
        }
    }

    public Date getForecast_fcrs() {
        return forecast_fcrs;
    }

    public void setForecast_fcrs(Date forecast_fcrs) {
        this.forecast_fcrs = forecast_fcrs;
    }

    public Date getForecast_fcre() {
        return forecast_fcre;
    }

    public void setForecast_fcre(Date forecast_fcre) {
        this.forecast_fcre = forecast_fcre;
    }

    public Date getStls_rstr() {
        return stls_rstr;
    }

    public void setStls_rstr(Date stls_rstr) {
        this.stls_rstr = stls_rstr;
    }

    public Date getStls_rend() {
        return stls_rend;
    }

    public void setStls_rend(Date stls_rend) {
        this.stls_rend = stls_rend;
    }

    public Date getCkie_fcrs() {
        return ckie_fcrs;
    }

    public void setCkie_fcrs(Date ckie_fcrs) {
        this.ckie_fcrs = ckie_fcrs;
    }

    public Date getCkoe_fcre() {
        return ckoe_fcre;
    }

    public void setCkoe_fcre(Date ckoe_fcre) {
        this.ckoe_fcre = ckoe_fcre;
    }

    public int getRec_dep() {
        return rec_dep;
    }

    public void setRec_dep(String rec_dep) {
        this.rec_dep = "A".equals(rec_dep) ? ARRIVE : DEPART;
    }

    public void setApcd(String id, String apcd) {
        String code = AirportMap.AIRPORTS.get(apcd);
        if(code != null){
            this.apcds.put(Integer.parseInt(id), code);
        }else {
            this.apcds.put(Integer.parseInt(id), apcd);
        }
    }

    /**
     * 获取经停站、终点站
     * @return
     */
    public String getApcds() {

        if(apcds.isEmpty()){
            return "null";
        }else{
            StringBuilder ret = new StringBuilder();
            String[] apcdStrings = apcds.values().toArray(new String[apcds.size()]);
            //刨除起点站
            for(int i = 1; i < apcds.size(); i++){
                if(i == apcds.size() - 1){
                    ret.append(apcdStrings[i]);
                }else {
                    ret.append(apcdStrings[i]).append(", ");
                }
            }
            return ret.toString();
        }
    }

    public boolean isOnBoard() {
        return onBoard;
    }

    public void setOnBoard(boolean onBoard) {
        this.onBoard = onBoard;
    }

    public String getCkno() {
        return ckno;
    }

    public void setCkno(String ckno) {
        this.ckno = ckno;
    }

    @Override
    public String toString() {
        return "Flight{" +
                "flid='" + flid + '\'' +
                ", ffid='" + ffid + '\'' +
                ", flightId='" + flightId + '\'' +
                ", ckno='" + ckno + '\'' +
                ", stls_estr=" + stls_estr +
                ", stls_eend=" + stls_eend +
                ", stls_rstr=" + stls_rstr +
                ", stls_rend=" + stls_rend +
                ", ckie_fcrs=" + ckie_fcrs +
                ", ckoe_fcre=" + ckoe_fcre +
                ", forecast_fcrs=" + forecast_fcrs +
                ", forecast_fcre=" + forecast_fcre +
                ", state=" + state +
                ", fatt=" + fatt +
                ", rec_dep=" + rec_dep +
                ", onBoard=" + onBoard +
                ", apcds=" + apcds +
                '}';
    }
}
