package de.k3b.android.locationMapViewer;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.lang.Math.abs;

public class MyTest {
    //public static int UTC;
    public static SimpleDateFormat simpleDateFormat2;
    public static Date localtime;
    public static int setUTC(double Longitude) {
        double quotient=Longitude/15;
        int UTC=(int)quotient;
        double reminder = abs(Longitude)/15;
        BigDecimal bd = new BigDecimal(reminder);
        reminder = bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        if(reminder>7.5){
            if(Longitude>0){
                UTC=UTC+1;
            }
            else if(Longitude<0){
                UTC=UTC-1;
            }
        }
        if(UTC==-12) {
            UTC = 12;
        }
        return UTC;
    }
    public static String setLocaltime(Date mytime, int UTC) {
        simpleDateFormat2=new SimpleDateFormat("HH:mm:ss");
        localtime=new Date();
        int hour=mytime.getHours()+UTC;
        if(hour<0){
            hour+=24;
        }
        else if(hour>=24){
            hour-=24;
        }
        localtime.setHours(hour);
        localtime.setMinutes(mytime.getMinutes());
        localtime.setSeconds(mytime.getSeconds());
        return simpleDateFormat2.format(localtime);
    }
}
