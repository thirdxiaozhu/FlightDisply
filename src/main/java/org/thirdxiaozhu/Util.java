package org.thirdxiaozhu;

import org.thirdxiaozhu.data.Flight;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {
    public static final int ESTR = 0;
    public static final int FORECAST = 1;
    public static final int CKIE = 2;
    public static final int CKOE = 3;

    /**
     * 格式化时间
     * @param rowTime
     * @return
     */
    public static Date getTime(String rowTime) throws ParseException {
        try {
            final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            LocalDateTime ldt = LocalDateTime.parse(rowTime, dtf);
            DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return df.parse(ldt.format(dtf2));
        }catch (DateTimeParseException e){
            return null;
        }
    }

    /**
     * 计算时间差
     * @param var1
     * @param var2
     * @return
     */
    public static long getTimeDiff(Date var1, Date var2){
        return var1.getTime() - var2.getTime();
    }

    /**
     * 正则匹配
     * @param mod 模式
     * @param regex 参数
     * @return
     */
    public static List<String> regularMatch(String mod, String regex) {
        List<String> args = new ArrayList<>();
        Pattern p = Pattern.compile(mod, Pattern.DOTALL);
        Matcher m = p.matcher(regex);
        while (m.find()){
            args.add(m.group());
        }
        return args.isEmpty() ? null : args;
    }

    /**
     * 向特定的List里面插入实体
     * @param flight
     * @param flights
     * @param mode
     */
    public static void addNode(Flight flight, CopyOnWriteArrayList<Flight> flights, int mode){
        Date processed = Util.ProcessMode(flight, mode);
        if(processed == null) {
            return;
        }

        for(int i = 0; i == 0 || i < flights.size(); i++){
            if(flights.size() == 0){
                flights.add(flight);
            }else if(processed.after(Util.ProcessMode(flights.get(i), mode)) && (flights.size() == i+1 || processed.before(Util.ProcessMode(flights.get(i+1), mode)))){
                flights.add(i+1, flight);
            }
        }
    }

    /**
     * 根据模式进行插入操作
     * @param f
     * @param mode
     * @return
     */
    private static Date ProcessMode(Flight f, int mode){
        return switch (mode) {
            case ESTR -> f.getStls_estr();
            case FORECAST -> f.getForecast_fcre();
            case CKIE -> f.getStls_estr();
            case CKOE -> f.getStls_estr();
            default -> null;
        };
    }

    /**
     * 处理办票时间并返回
     * @param f
     * @return
     */
    public static String dealTime(Flight f){
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        return format.format(f.getForecast_fcrs()) + "-" + format.format(f.getForecast_fcre());
    }

    public static String getState(Flight f){
        if(f.getState() == Flight.CKIE){
            return "正在办票";
        }else if(f.getState() == Flight.CKOE){
            return "停止办票";
        }else {
            return "";
        }
    }

    /**
     * 字符串转十六进制
     * @param s
     * @return
     */
    public static String strTo16(String s) {
        String str = "";
        for (int i = 0; i < s.length(); i++) {
            int ch = (int) s.charAt(i);
            String s4 = Integer.toHexString(ch);
            str = str + s4;
        }
        return str;
    }
}
