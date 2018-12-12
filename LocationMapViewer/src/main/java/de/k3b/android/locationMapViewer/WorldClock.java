package de.k3b.android.locationMapViewer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.lang.Math.abs;

public class WorldClock extends Activity {
    private static final Logger logger = LoggerFactory.getLogger(WorldClock.class);
    private static double Latitude;
    protected static double Longitude;
    protected static int UTC;
    protected static String TimeZone;
    protected static int timedif;
    private static SimpleDateFormat simpleDateFormat1;
    private static SimpleDateFormat simpleDateFormat2;
    private static Date mytime;
    protected static Date localtime;
    public static void show(Activity context, int resultID) {
        final Intent i = new Intent(context, WorldClock.class);
        if (logger.isDebugEnabled()) logger.debug("show(resultID"+resultID+")");

        context.startActivityForResult(i, resultID);
    }
    public static int setUTC(double Longitude) {
        double quotient=Longitude/15;
        UTC=(int)quotient;
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
    public static void setTimeZone() {
        UTC=setUTC(Longitude);
        if(UTC<0){
            TimeZone="UTC"+""+UTC;
        }
        else {
            TimeZone = "UTC+" + "" + UTC;
        }
    }
    public static void setTimedif() {
        timedif=-(8-UTC);
    }
    public static Date setLocaltime(Date mytime) {
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
        return localtime;
    }
    public static void setMytime() {
        simpleDateFormat1 = new SimpleDateFormat("HH:mm:ss");
        mytime = new Date(System.currentTimeMillis());
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worldclock);
        setTimeZone();
        setTimedif();
        setMytime();
        localtime=setLocaltime(mytime);
        TextView North=(TextView)findViewById(R.id.x);
        TextView East=(TextView)findViewById(R.id.y);
        TextView timezone=(TextView)findViewById(R.id.zone);
        TextView yourtime=(TextView)findViewById(R.id.title2);
        TextView loctime=(TextView)findViewById(R.id.title3);
        TextView timediff=(TextView)findViewById(R.id.title4);
        North.setText("North:\n"+Latitude);
        East.setText("East:\n"+Longitude);
        timezone.setText("TimeZone:\n"+TimeZone);
        yourtime.setText("Your Time:\n"+simpleDateFormat1.format(mytime));
        timediff.setText("Time Difference:\n"+abs(timedif)+"h");
        loctime.setText("Local Time:\n"+simpleDateFormat2.format(localtime));
        //mtv1 = (TextView) findViewById(R.id.title);
        //mtv1.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG); // set strike through style in the text
        //mtv1.getPaint().setAntiAlias(true); // get rid of the zigzag effect
    }
    public double getLatitude() {
        return Latitude;
    }
    public static void setLatitude(double latitude) {
        Latitude = latitude;
    }
    public double getLongitude() {
        return Longitude;
    }
    public static void setLongitude(double longitude) {
        Longitude = longitude;
    }
}
