package org.thirdxiaozhu.data;

import org.thirdxiaozhu.Util;
import org.thirdxiaozhu.swing.MainForm;

import java.io.File;
import java.io.FileNotFoundException;
import java.security.PublicKey;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Analysis {
    //设置加速时间
    //标准速度为1000
    private final int ACCELERATION = 10;

    private final String filePath;
    private final MainForm mainForm;

    private GetInfosTask getInfosTask;
    private TimeTask timeTask;
    private final Map<String, Flight> flightMap;
    private Date currentTime;
    public static CopyOnWriteArrayList<Flight> flightQueue;
    public static CopyOnWriteArrayList<Flight> flightOnBoard;
    private final DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");

    public Analysis(String filePath, MainForm mainForm) throws ParseException {
        this.filePath = filePath;
        this.mainForm = mainForm;

        flightMap = new HashMap<>();
        flightQueue = new CopyOnWriteArrayList<>();
        flightOnBoard = new CopyOnWriteArrayList<>();
        getInfosTask = new GetInfosTask();
        timeTask = new TimeTask();
        getInfosTask.start();
        timeTask.start();
    }

    /**
     * 解析报文
     * @param message 信息
     * @param pattern 报文模式
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
         * @param message 信息
         * @param flight 实体类
         * @throws ParseException
         */
        public static void parseAirl(String message, Flight flight) {
            List<String> regulared = Util.regularMatch("\\[.*?\\]", message.substring(message.indexOf("[") + 1, message.lastIndexOf("]") + 1));
            for(String s : regulared){
                s = s.substring(s.indexOf("[") + 1, s.indexOf("]") + 1);
                flight.setApcd(s.split(", ")[0].split("=")[1], s.split(", ")[1].split("=")[1]);
            }
        }

        /**
         * 机位动态更新
         * @param message 信息
         * @param flight 实体类
         * @throws ParseException
         */
        public static void parseStls(String message, Flight flight) throws ParseException {
            String regulared = Util.regularMatch("\\[.*?\\]", message.substring(message.indexOf("[") + 1, message.indexOf("]") + 1)).get(0);
            String[] args = regulared.split(", ");
            flight.setStls_estr(Util.getTime(args[2].split("=")[1]));
            flight.setStls_eend(Util.getTime(args[3].split("=")[1]));
            flight.setStls_rstr(Util.getTime(args[4].split("=")[1]));
            flight.setStls_rend(Util.getTime(args[5].split("=")[1]));

            //如果是即将起飞的飞机，按estr进行排序
            if(flight.getRec_dep() == Flight.DEPART){
                Util.addNode(flight, flightQueue, Util.ESTR);
            }
        }

        /**
         * 开始值机
         * @param message 信息
         * @param flight 实体类
         * @throws ParseException
         */
        public static void parseCkie(String message, Flight flight) throws ParseException {
            message = message.substring(message.indexOf("[") + 1, message.indexOf("]"));
            String fcrs = message.split(", ")[5].split("=")[1];
            if(!"null".equals(fcrs)){
                flight.setCkie_fcrs(Util.getTime(fcrs));
                flight.setState(Flight.CKIE);
            }
        }

        /**
         * 结束值机
         * @param message 信息
         * @param flight 实体类
         * @throws ParseException
         */
        public static void parseCkoe(String message, Flight flight) throws ParseException {
            message = message.substring(message.indexOf("[") + 1, message.indexOf("]"));
            flight.setCkoe_fcre(Util.getTime(message.split(", ")[5].split("=")[1]));
            flight.setState(Flight.CKOE);
        }

        /**
         * 值机状态变更
         * @param message
         * @param flight
         * @throws ParseException
         */
        public static void parseCKLS(String message, Flight flight) throws ParseException{

        }
    }

    /**
     * 读取线程
     */
    public class GetInfosTask extends Thread{

        public GetInfosTask() throws ParseException {
            //从2018年9月23日0点2分25秒开始
            currentTime = Util.getTime("20180923000225");
        }

        @Override
        public void run() {
            try (Scanner scanner = new Scanner(new File(filePath))) {
                while (scanner.hasNextLine()){
                    String perMessage = scanner.nextLine();
                    String[] timeAndPattern = perMessage.split(" ");
                    Date nextTime = Util.getTime(timeAndPattern[0].split("=")[1]);

                    assert nextTime != null;
                    //强制对齐时间（如果加速过快可能会让下一时间早于上一时间）
                    nextTime = nextTime.before(currentTime) ? currentTime : nextTime;
                    sleep(Util.getTimeDiff(nextTime, currentTime) / (1000 / ACCELERATION));
                    parseMessage(perMessage, timeAndPattern[1].substring(5,9));
                }
            } catch (FileNotFoundException | ParseException | InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public class TimeTask extends Thread{
        /**
         * 时间变更任务
         */
        private final Runnable time = new Runnable() {
            @Override
            public void run() {
                try {
                    currentTime = df.parse(df.format(currentTime.getTime() + (long) 1000));
                    //System.out.println(currentTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        };

        /**
         * 每分钟任务
         */
        private final Runnable perMinute = new Runnable() {
            @Override
            public void run() {
                System.out.println(currentTime);
                //循环查看实体队列，如果当前时间大于队列里某一实体的estr，那么该实体进入OnBoard
                for (Flight f : flightQueue) {
                    if (currentTime.after(f.getStls_estr()) && !f.isOnBoard()) {
                        flightQueue.remove(f);
                        Util.addNode(f, flightOnBoard, Util.FORECAST);
                        f.setOnBoard(true);
                    }
                }

                Collections.sort(flightOnBoard, new Comparator<Flight>() {
                    @Override
                    public int compare(Flight o1, Flight o2) {
                        int cr = 0;

                        //先按状态排序，再按预计开始时间排序
                        int a = o1.getState() - o2.getState();
                        if(a != 0){
                            cr = (a > 0) ? -3: 1;
                        }else {
                            boolean after = o1.getForecast_fcrs().after(o2.getForecast_fcrs());
                            boolean equal = o1.getForecast_fcrs().equals(o2.getForecast_fcrs());
                            if(!equal){
                                cr = after ? 2 : -2;
                            }
                        }
                        return cr;
                    }
                });

                //循环查看OnBoard，如果当前时间大于队列里某一实体的eend，那么该实体移除OnBoard
                flightOnBoard.removeIf(f -> currentTime.after(f.getStls_eend()));

                System.out.println("BoardSize : " + flightOnBoard.size());
            }
        };

        /**
         * 每30秒切换一次
         */
        private final Runnable perHalfMinute = new Runnable() {
            private int i = 0;
            @Override
            public void run() {
                try {
                    //最后一页内容的数量
                    int temp = flightOnBoard.size() - i * 10;
                    List<Flight> flightList = new ArrayList<>();
                    for (int r = 0; r < Math.min(temp, 10); r++) {
                        flightList.add(flightOnBoard.get(i * 10 + r));
                    }
                    i = temp <= 10 ? 0 : i + 1;
                    mainForm.updateTable(flightList);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };

        @Override
        public void run() {
            ScheduledExecutorService service = Executors
                    .newSingleThreadScheduledExecutor();
            // 第二个参数为首次执行的延时时间，第三个参数为定时执行的间隔时间
            //service.scheduleAtFixedRate(runnable, 0, 60, TimeUnit.SECONDS);
            service.scheduleAtFixedRate(time, 0, ACCELERATION, TimeUnit.MILLISECONDS);
            service.scheduleAtFixedRate(perMinute, 0, ACCELERATION * 60, TimeUnit.MILLISECONDS);
            service.scheduleAtFixedRate(perHalfMinute, 0, ACCELERATION * 30, TimeUnit.MILLISECONDS);
        }
    }
}
