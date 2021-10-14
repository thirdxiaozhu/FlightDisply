package org.thirdxiaozhu;

import org.thirdxiaozhu.data.Flight;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {


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

    public static void addNode(Flight flight, CopyOnWriteArrayList<Flight> flights){
        Date estr = flight.getStls_estr();
        if(estr == null) {
            return;
        }

        for(int i = 0; i == 0 || i < flights.size(); i++){
            if(flights.size() == 0){
                flights.add(flight);
            }else if(estr.after(flights.get(i).getStls_estr()) && (flights.size() == i+1 || estr.before(flights.get(i+1).getStls_estr()))){
                flights.add(i+1, flight);
            }
            System.out.println(flights.get(i).getStls_estr() + " " + flights.get(i).getStls_eend());
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
