package org.thirdxiaozhu.data;

import org.thirdxiaozhu.Util;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Analysis {
    //设置加速时间
    private final int ACCELERATION = 100;

    private String filePath;
    private GetInfosTask getInfosTask;
    private TimeTask timeTask;
    private Map<String, Flight> flightMap;
    private Date currentTime;
    private final DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");

    public Analysis(String filePath) throws ParseException {
        flightMap = new HashMap<>();
        this.filePath = filePath;
        getInfosTask = new GetInfosTask();
        timeTask = new TimeTask();
        getInfosTask.start();
        timeTask.start();
    }

    /**
     * 解析报文
     * @param message
     * @param pattern
     * @throws ParseException
     */
    public void parseMessage(String message, String pattern) throws ParseException {
        String regulared = Util.regularMatch("\\[.*\\]", message).get(0);
        String intercepted = regulared.substring(1, regulared.length()-1);
        String[] paras = intercepted.split(", ");

        Flight currentFlight = flightMap.get(paras[0]);

        if(currentFlight == null){
            currentFlight = new Flight(
                    paras[0].split("=")[1],
                    paras[1].split("=")[1],
                    "fatt".equals(paras[2].split("=")[0]) ? paras[2].split("=")[1] : "0000"
            );
            flightMap.put(paras[0], currentFlight);
        }


        switch (pattern){
            case "AIRL":
                ParseConctrete.parseAirl(regulared, currentFlight);
                break;
            case "STLS":
                ParseConctrete.parseStls(regulared, currentFlight);
                break;
            case "CKIE":
                ParseConctrete.parseCkie(regulared, currentFlight);
                break;
            case "CKOE":
                ParseConctrete.parseCkoe(regulared, currentFlight);
                break;
            case "BORE":
                break;
            default:
                break;
        }
    }


    /**
     * 获取具体数据
     */
    public static class ParseConctrete {
        /**
         * 目的地解析
         * @param message
         * @param flight
         * @throws ParseException
         */
        public static void parseAirl(String message, Flight flight) throws ParseException {
            List<String> regulared = Util.regularMatch("\\[.*?\\]", message.substring(message.indexOf("[") + 1, message.lastIndexOf("]") + 1));
            for(String s : regulared){
                s = s.substring(s.indexOf("[") + 1, s.indexOf("]") + 1);
                flight.setApcd(s.split(", ")[0].split("=")[1], s.split(", ")[1].split("=")[1]);
            }
            if(flight.getRec_dep() == Flight.DEPART) {
            }
        }

        /**
         * 机位动态更新
         * @param message
         * @param flight
         * @throws ParseException
         */
        public static void parseStls(String message, Flight flight) throws ParseException {
            String regulared = Util.regularMatch("\\[.*?\\]", message.substring(message.indexOf("[") + 1, message.indexOf("]") + 1)).get(0);
            String[] args = regulared.split(", ");
            flight.setStls_estr(Util.getTime(args[2].split("=")[1]));
            flight.setStls_eend(Util.getTime(args[3].split("=")[1]));
            flight.setStls_rstr(Util.getTime(args[4].split("=")[1]));
            flight.setStls_rend(Util.getTime(args[5].split("=")[1]));
        }

        /**
         * 开始值机
         * @param message
         * @param flight
         * @throws ParseException
         */
        public static void parseCkie(String message, Flight flight) throws ParseException {
            message = message.substring(message.indexOf("[") + 1, message.indexOf("]"));
            flight.setCkie_fcrs(Util.getTime(message.split(", ")[5].split("=")[1]));
            flight.setState(Flight.CKIE);
        }

        /**
         * 结束值机
         * @param message
         * @param flight
         * @throws ParseException
         */
        public static void parseCkoe(String message, Flight flight) throws ParseException {
            message = message.substring(message.indexOf("[") + 1, message.indexOf("]"));
            flight.setCkoe_fcre(Util.getTime(message.split(", ")[5].split("=")[1]));
            flight.setState(Flight.CKOE);
        }
    }

    /**
     * 读取线程
     */
    public class GetInfosTask extends Thread{

        public GetInfosTask() throws ParseException {
            currentTime = Util.getTime("20180923000225");
        }

        @Override
        public void run() {
            try (Scanner scanner = new Scanner(new File(filePath))) {
                //从2018年9月23日0点2分25秒开始
                while (scanner.hasNextLine()){
                    String perMessage = scanner.nextLine();
                    String[] timeAndPattern = perMessage.split(" ");
                    Date nextTime = Util.getTime(timeAndPattern[0].split("=")[1]);

                    sleep(Util.getTimeDiff(nextTime, currentTime) / (1000 / ACCELERATION));
                    System.out.println(perMessage);
                    parseMessage(perMessage, timeAndPattern[1].substring(5,9));
                    currentTime = nextTime;

                }
            } catch (FileNotFoundException | ParseException | InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public class TimeTask extends Thread{
        private int i = 0;
        private Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println(i);
                    currentTime = df.parse(df.format(currentTime.getTime() + (long) 1 * ACCELERATION));
                    System.out.println(currentTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                i++;
            }
        };

        @Override
        public void run() {
            ScheduledExecutorService service = Executors
                    .newSingleThreadScheduledExecutor();
            // 第二个参数为首次执行的延时时间，第三个参数为定时执行的间隔时间
            //service.scheduleAtFixedRate(runnable, 0, 60, TimeUnit.SECONDS);
            service.scheduleAtFixedRate(runnable, 0, ACCELERATION, TimeUnit.MILLISECONDS);
        }
    }
}
