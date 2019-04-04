package com.example.myapplication;

import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.LineChartView;
import lecho.lib.hellocharts.view.PieChartView;

public class WifiActivity extends AppCompatActivity {
    private ColumnChartData mColumnChartData;
    private ColumnChartView mColumnChartView;
    private LineChartView mLineChartView;
    private PieChartView mPieChartView;
    private TrafficHelper helper;
    private List<TrafficInfo> trafficInfos;
    private List<Float> test;
    private List<String> daylist;
//    private String[] daylist;
    private int day;
    private long total;
    private Button btn1;
    private Button btn2;
    private Button btn3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifichart);
        mColumnChartView=(ColumnChartView)findViewById(R.id.chart);
        mLineChartView=(LineChartView)findViewById(R.id.linechart);
        mPieChartView=(PieChartView)findViewById(R.id.piechart);
        mColumnChartView.setVisibility(View.GONE);
        mLineChartView.setVisibility(View.GONE);
        mPieChartView.setVisibility(View.GONE);
        btn1=(Button)findViewById(R.id.chart1);
        btn2=(Button)findViewById(R.id.chart2);
        btn3=(Button)findViewById(R.id.chart3);
        day=getMonthday();
        daylist=new ArrayList<>();;
        helper=new TrafficHelper(this);
        test=new ArrayList<Float>();
//        trafficInfos=helper.getInternetTrafficInfos();
        for(int i=1;i<=day;i++){
            daylist.add(String.valueOf(i));
            total=(getTotalWifiRx(getTimesDayMorning(i),getTimesDayEvening(i))+getTotalWIFITx(getTimesDayMorning(i),getTimesDayEvening(i)))/1048576;
            test.add(Float.parseFloat(String.valueOf(total)));
        }
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getColumnChart();
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLineChart();
            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPieChart();
            }
        });
    }
    public void getColumnChart(){
        mPieChartView.setVisibility(View.GONE);
        mLineChartView.setVisibility(View.GONE);
        List<Column> columnList = new ArrayList<>(); //柱子列表
        List<SubcolumnValue> subcolumnValueList;     //子柱列表（即一个柱子，因为一个柱子可分为多个子柱）
        List<AxisValue> axisValues = new ArrayList<>();//自定义横轴坐标值
//        for(TrafficInfo info : trafficInfos) {
//            test.add(Float.parseFloat(String.valueOf(getPackageTxDayBytesWifi(info.getUid()))));
//        }
        int i=0;
        for (Float kk :test) {
            subcolumnValueList = new ArrayList<>();
            subcolumnValueList.add(new SubcolumnValue(kk, ChartUtils.pickColor()));

            Column column = new Column(subcolumnValueList);
            columnList.add(column);
            axisValues.add(new AxisValue(i).setLabel(daylist.get(i)));
//            column.setHasLabels(true);
            i=i+1;
        }
//        for (int i = 0; i < 7; ++i) {
//            subcolumnValueList = new ArrayList<>();
//            subcolumnValueList.add(new SubcolumnValue((float) Math.random() * 50f, ChartUtils.pickColor()));
//
//            Column column = new Column(subcolumnValueList);
//            columnList.add(column);
//        }
        mColumnChartData = new ColumnChartData(columnList);
        /*===== 坐标轴相关设置 =====*/
        Axis axisX = new Axis(axisValues);
        Axis axisY = new Axis().setHasLines(true);
        axisX.setName("日期");    //设置横轴名称
        axisY.setName("使用的流量/mb");    //设置竖轴名称
        mColumnChartData.setAxisXBottom(axisX); //设置横轴
        mColumnChartData.setAxisYLeft(axisY);   //设置竖轴

        //以上所有设置的数据、坐标配置都已存放到mColumnChartData中，接下来给mColumnChartView设置这些配置
        mColumnChartView.setColumnChartData(mColumnChartData);
        mColumnChartView.setVisibility(View.VISIBLE);

    }
    private void getLineChart(){
        mPieChartView.setVisibility(View.GONE);
        mColumnChartView.setVisibility(View.GONE);
        List<AxisValue> axisValues = new ArrayList<>();//自定义横轴坐标值
        List<PointValue> mPointValues = new ArrayList<PointValue>();

        int i=0;
        for (Float kk :test) {
            mPointValues.add(new PointValue(i,kk));
            axisValues.add(new AxisValue(i).setLabel(daylist.get(i)));
            i=i+1;
        }

        Line line = new Line(mPointValues).setColor(Color.parseColor("#FFCD41"));  //折线的颜色（橙色）
        List<Line> lines = new ArrayList<Line>();
        line.setShape(ValueShape.CIRCLE);//折线图上每个数据点的形状  这里是圆形 （有三种 ：ValueShape.SQUARE  ValueShape.CIRCLE  ValueShape.DIAMOND）
        line.setCubic(true);//曲线是否平滑，即是曲线还是折线
        line.setFilled(false);//是否填充曲线的面积
//        line.setHasLabels(true);//曲线的数据坐标是否加上备注
//      line.setHasLabelsOnlyForSelected(true);//点击数据坐标提示数据（设置了这个line.setHasLabels(true);就无效）
        line.setHasLines(true);//是否用线显示。如果为false 则没有曲线只有点显示
        line.setHasPoints(true);//是否显示圆点 如果为false 则没有原点只有点显示（每个数据点都是个大的圆点）
        lines.add(line);
        LineChartData data = new LineChartData();
        data.setLines(lines);

//        LineChartData data = new LineChartData();
        Axis axisX = new Axis(axisValues);
        Axis axisY = new Axis().setHasLines(true);
        axisX.setName("日期");    //设置横轴名称
        axisY.setName("使用的流量/mb");    //设置竖轴名称
        data.setAxisXBottom(axisX); //设置横轴
        data.setAxisYLeft(axisY);   //设置竖轴

        //以上所有设置的数据、坐标配置都已存放到mColumnChartData中，接下来给mColumnChartView设置这些配置
        mLineChartView.setLineChartData(data);
        mLineChartView.setVisibility(View.VISIBLE);
    }
    public void getPieChart(){
        mLineChartView.setVisibility(View.GONE);
        mColumnChartView.setVisibility(View.GONE);
        List<SliceValue> values = new ArrayList<>();
        int i=1;
        Calendar cal = Calendar.getInstance();
        int j=cal.get(Calendar.MONTH) + 1;
        int k=cal.get(Calendar.DAY_OF_MONTH);
        for (Float kk :test) {
            SliceValue sliceValue =new SliceValue(kk,ChartUtils.pickColor());
            if(i>k) {
                break;
            }
            String val=j+"月"+i;
//            Log.d("s", val);
            sliceValue.setLabel(val);
            values.add(sliceValue);
            i=i+1;
        }
        PieChartData mPieChartData=new PieChartData(values);
        mPieChartData.setHasLabels(true);
        mPieChartView.setPieChartData(mPieChartData);
        mPieChartView.setVisibility(View.VISIBLE);
    }
    public long getPackageTxDayBytesWifi(int packageUid) {
        long summaryRx = 0;
        long summaryTx = 0;
        long summaryTotal = 0;
        NetworkStats networkStats = null;
        NetworkStats.Bucket bucket = new NetworkStats.Bucket();
        NetworkStatsManager networkStatsManager = (NetworkStatsManager) getSystemService(NETWORK_STATS_SERVICE);
        try {
            networkStats = networkStatsManager.querySummary(
                    ConnectivityManager.TYPE_WIFI,
                    "",
                    getTimesMonthMorning(),
                    System.currentTimeMillis());
            do {
                networkStats.getNextBucket(bucket);
                int summaryUid = bucket.getUid();
                if (packageUid == summaryUid) {
                    summaryRx += bucket.getRxBytes();
                    summaryTx += bucket.getTxBytes();
//                summaryTotal += bucket.getRxBytes() + bucket.getTxBytes();
                }
            } while (networkStats.hasNextBucket());
        }catch(RemoteException e){

        }
        summaryTotal=summaryRx+summaryTx;
//        networkStats.getNextBucket(bucket);
        return summaryTotal;
    }
    public static long getTimesMonthMorning() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        return cal.getTimeInMillis();
    }
    public static int getMonthday(){
        Calendar cal = Calendar.getInstance();
        return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
    }
    public static long getTimesDayMorning(int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_MONTH, day);
        return cal.getTimeInMillis();
    }
    public static long getTimesDayEvening(int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
        cal.set(Calendar.DAY_OF_MONTH, day);
        return cal.getTimeInMillis();
    }
    private static String fileSizeConver(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }
        return fileSizeString;
    }
    public long getTotalWifiRx(long start,long end) {
        try {
            NetworkStatsManager networkStatsManager = (NetworkStatsManager) getSystemService(NETWORK_STATS_SERVICE);
            NetworkStats.Bucket bucket = null;
            bucket = networkStatsManager.querySummaryForDevice(ConnectivityManager.TYPE_WIFI, "", start, end);
            return bucket.getRxBytes();
        } catch (RemoteException e) {
            return ' ';
        }
    }
    public long getTotalWIFITx(long start,long end) {
        try {
            NetworkStatsManager networkStatsManager = (NetworkStatsManager) getSystemService(NETWORK_STATS_SERVICE);
            NetworkStats.Bucket bucket = null;
            bucket = networkStatsManager.querySummaryForDevice(ConnectivityManager.TYPE_WIFI, "", start, end);
            return bucket.getTxBytes();
        } catch (RemoteException e) {
            return ' ';
        }
    }
}
