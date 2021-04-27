package com.example.mmt.springboot.utility.timeutils;

public class TimeUtils {

    public static void main(String[] args) {
        System.out.println(timeDifference("1230","1430"));
    }
    /*
    * this function will take 2 arguments
    * startTime : HHMM format string
    * endTime : HHMM format string
    *
    * this function will return time difference between 2 times.
    * if time difference < 120 min then, ed time will be moved to next day after adding 1440 minutes
    *
    * */
    public static Integer timeDifference(String startTime, String endTime){
        Integer sTime = getTime(startTime);
        Integer dTime = getTime(endTime);
        return  (dTime - sTime) > 120 ? (dTime - sTime) : (1440 + (dTime - sTime));
    }

    private static String prependZeros(String time){
        if(time.length() == 1){
            return "000"+time;
        }
        if(time.length() == 2){
            return "00"+time;
        }
        if(time.length() == 3){
            return "0"+time;
        }
        return time;
    }

    private static Integer getTime(String time){
        String t = prependZeros(time);
        return (Integer.parseInt(t.substring(0,2)) * 60 )+ Integer.parseInt(t.substring(2,4));
    }

    public static Integer timeInMinutes(String startTime, String endTime){
        int st = getTime(startTime);
        int et = getTime(endTime);

        int totalTime = 0;
        if(st>et){
            totalTime = (1440-st)+et;
        }
        else {
            totalTime = et - st;
        }
        return  totalTime;
    }
}
