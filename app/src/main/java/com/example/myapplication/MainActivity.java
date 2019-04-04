package com.example.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AppOpsManager;
import android.app.usage.NetworkStats;
//import android.app.usage.NetworkStatsManager;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;



import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private TrafficHelper helper;
    private List<TrafficInfo> trafficInfos;
//    private TrafficInfoAdapter adapter;
    private NetworkStatsManager networkStatsManager;
    private long nowTotal=0;
    private long lastTotal=0;
    private long nowGprsTotal =0;
    private long lastGprsTotal =0;
    private long lastTime=0;
    private DecimalFormat showFloatFormat =new DecimalFormat("0.00");
    private TextView speed;
    String subId;

    private Timer timer;

    private TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            if(is3G(getApplicationContext())){
                nowGprsTotal=getTotalGPRSRx()+getTotalGPRStx();
                long nowTimeStamp = System.currentTimeMillis();
                long speed = (Math.abs((nowGprsTotal-lastGprsTotal)) * 1000 / (nowTimeStamp - lastTime));
                Log.d("测试",String.valueOf(getTotalGPRSRx()+getTotalGPRStx()));
                lastTime = nowTimeStamp;
                lastGprsTotal = nowGprsTotal;
//                Message msg = new Message();
                Message msg = mHandler.obtainMessage();
                msg.what = 1;
                msg.obj=showSpeed(speed);
                mHandler.sendMessage(msg);
            }else if (isWifi(getApplicationContext())) {
                nowTotal=getTotalWifiRx()+getTotalWIFITx();
                Log.d("测试",String.valueOf(nowTotal-lastTotal));
                long nowTimeStamp = System.currentTimeMillis();
                long speed = (Math.abs((nowTotal-lastTotal)) * 1000 / (nowTimeStamp - lastTime));
                lastTime = nowTimeStamp;
                lastTotal = nowTotal;
//                Message msg = new Message();
                Message msg = mHandler.obtainMessage();
                msg.what = 2;
                msg.obj=showSpeed(speed);
                mHandler.sendMessage(msg);
            }else {
                String text="当前网络没有连接";
                long nowTimeStamp = System.currentTimeMillis();
                lastTime = nowTimeStamp;
//                Message msg = new Message();
                Message msg = mHandler.obtainMessage();
                msg.what = 3;
                msg.obj=text;
                mHandler.sendMessage(msg);
            }
        }
    };
    private Handler mHandler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    speed.setText("当前使用数据，网速为："+String.valueOf(msg.obj));
                    break;
                case 2:
                    speed.setText("当前使用WIFI，网速为："+String.valueOf(msg.obj));
                    break;
                case 3:
                    speed.setText(String.valueOf(msg.obj));
                    break;
            }
        };
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hasPermissionToReadNetworkStats();
        networkStatsManager = (NetworkStatsManager) getSystemService(NETWORK_STATS_SERVICE);
        NetworkStats.Bucket bucket = null;
        NetworkStats.Bucket bucket2 = null;
        TextView textView = (TextView) findViewById(R.id.view1);
        TextView textView_rx = (TextView) findViewById(R.id.view1_rx);
        TextView textView_tx = (TextView) findViewById(R.id.view1_tx);
        TextView textView2 = (TextView) findViewById(R.id.view2);
        TextView textView2_rx = (TextView) findViewById(R.id.view2_rx);
        TextView textView2_tx = (TextView) findViewById(R.id.view2_tx);
        TextView textView3 = (TextView) findViewById(R.id.speed);
        ListView listview = (ListView) findViewById(R.id.list);
        speed=(TextView)findViewById(R.id.speed);
        helper = new TrafficHelper(this);
        trafficInfos = helper.getInternetTrafficInfos();
//        adapter = new TrafficInfoAdapter(this, R.layout.content, trafficInfos);
//        listview.setAdapter(adapter);
        TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        subId = tm.getSubscriberId();
        // 获取到目前为止设备的Wi-Fi流量统计
        try {
            bucket = networkStatsManager.querySummaryForDevice(ConnectivityManager.TYPE_WIFI, "", getTimesMonthMorning(), System.currentTimeMillis());
            bucket2 = networkStatsManager.querySummaryForDevice(ConnectivityManager.TYPE_MOBILE, subId, getTimesMonthMorning(), System.currentTimeMillis());
//            String rx1 = fileSizeConver(bucket.getRxBytes());
            String rx1 = fileSizeConver(bucket.getRxBytes());
            String tx1 = fileSizeConver(bucket.getTxBytes());
            String total1 = fileSizeConver(bucket.getRxBytes() + bucket.getTxBytes());
            String rx = fileSizeConver(bucket2.getRxBytes());
            String tx = fileSizeConver(bucket2.getTxBytes());
            String total = fileSizeConver(bucket2.getRxBytes() + bucket2.getTxBytes());
//            textView.setText("wifi使用的流量: " + total1 + "wifi使用的下载流量: " + rx1 + "wifi使用的上传流量: " + tx1);
            textView.setText(total1);
            textView_tx.setText(tx1);
            textView_rx.setText(rx1);
            textView2.setText(total);
            textView2_tx.setText(tx);
            textView2_rx.setText(rx);
//            textView.setText("wifi使用的下载流量: " + bucket2.getRxBytes() + "wifi使用的上传流量: "+ bucket2.getTxBytes());
//            textView2.setText("手机使用的流量: " + total + "手机使用的下载流量 " + rx + "手机使用的上传流量: " + tx);
            Log.i("Info", "Total: " + (bucket.getRxBytes() + bucket.getTxBytes()));
        } catch (RemoteException e) {

        }
//        if(isWifi(this)){
//            Toast.makeText(this,"wifi",Toast.LENGTH_SHORT).show();
//
//        }else if(is3G(this)) {
//            Toast.makeText(this,"数据",Toast.LENGTH_SHORT).show();
//        }else {
//            Toast.makeText(this,"没连接网络",Toast.LENGTH_SHORT).show();
//        }
        lastGprsTotal=getTotalGPRSRx()+getTotalGPRStx();
        lastTotal=getTotalWifiRx()+getTotalWIFITx();
        lastTime=System.currentTimeMillis();
        timer = new Timer();
        timer.schedule(timerTask, 1000,2000);


        Button button=(Button)findViewById(R.id.btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(MainActivity.this,WifiActivity.class);
                startActivity(intent);
            }
        });
        Button button_app=(Button)findViewById(R.id.btn_app);
        button_app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(MainActivity.this,WifiAppActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean hasPermissionToReadNetworkStats() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        final AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), getPackageName());
        if (mode == AppOpsManager.MODE_ALLOWED) {
            return true;
        }

        requestReadNetworkStats();
        return false;
    }

    // 打开“有权查看使用情况的应用”页面
    private void requestReadNetworkStats() {
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        startActivity(intent);
    }

    public static long getTimesMonthMorning() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        return cal.getTimeInMillis();
    }

    public static String getPrintSize(long size) {
        long rest = 0;
        if (size < 1024) {
            return String.valueOf(size) + "B";
        } else {
            size /= 1024;
        }

        if (size < 1024) {
            return String.valueOf(size) + "KB";
        } else {
            rest = size % 1024;
            size /= 1024;
        }

        if (size < 1024) {
            size = size * 100;
            return String.valueOf((size / 100)) + "." + String.valueOf((rest * 100 / 1024 % 100)) + "MB";
        } else {
            size = size * 100 / 1024;
            return String.valueOf((size / 100)) + "." + String.valueOf((size % 100)) + "GB";
        }
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
    public long getTotalWifiRx() {
        try {
            NetworkStats.Bucket bucket = null;
            bucket = networkStatsManager.querySummaryForDevice(ConnectivityManager.TYPE_WIFI, "", getTimesMonthMorning(), System.currentTimeMillis());
            return bucket.getRxBytes();
        } catch (RemoteException e) {
            return ' ';
        }
    }
    public long getTotalWIFITx() {
        try {
            NetworkStats.Bucket bucket = null;
            bucket = networkStatsManager.querySummaryForDevice(ConnectivityManager.TYPE_WIFI, "", getTimesMonthMorning(), System.currentTimeMillis());
            return bucket.getTxBytes();
        } catch (RemoteException e) {
            return ' ';
        }
    }
    public long getTotalGPRSRx() {
        try {
            NetworkStats.Bucket bucket = null;
            bucket = networkStatsManager.querySummaryForDevice(ConnectivityManager.TYPE_MOBILE, subId, getTimesMonthMorning(), System.currentTimeMillis());
            return bucket.getRxBytes();
        } catch (RemoteException e) {
            return ' ';
        }
    }
    public long getTotalGPRStx() {
        try {
            NetworkStats.Bucket bucket = null;
            bucket = networkStatsManager.querySummaryForDevice(ConnectivityManager.TYPE_MOBILE, subId, getTimesMonthMorning(), System.currentTimeMillis());
            return bucket.getTxBytes();
        } catch (RemoteException e) {
            return ' ';
        }
    }
    public class TrafficInfoAdapter extends ArrayAdapter<TrafficInfo> {
        private int resourceId;

        public TrafficInfoAdapter(Context context, int textViewResourceId,
                                  List<TrafficInfo> objects) {
            super(context, textViewResourceId, objects);
            resourceId = textViewResourceId;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TrafficInfo info = getItem(position); // 获取当前项的Fruit实例
            View view;
            ViewHolder viewHolder;
            if (convertView == null) {
                view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.appIcon = (ImageView) view.findViewById(R.id.icon);
                viewHolder.appName = (TextView) view.findViewById(R.id.name);
                viewHolder.total = (TextView) view.findViewById(R.id.total);
                viewHolder.progress = (ProgressBar) view.findViewById(R.id.progress);
                view.setTag(viewHolder); // 将ViewHolder存储在View中
            } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag(); // 重新获取ViewHolder
            }
            int uid = info.getUid();
            String total = getPackageTxDayBytesGPRS(uid);
            viewHolder.appIcon.setImageDrawable(info.getIcon());
            viewHolder.appName.setText(info.getAppname());
            viewHolder.total.setText(total);
            return view;
        }

        class ViewHolder {
            ImageView appIcon;
            TextView appName;
            ProgressBar progress;
            TextView total;
        }
    }

    public String getPackageTxDayBytesWifi(int packageUid) {
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
        return fileSizeConver(summaryTotal);
    }

    public String getPackageTxDayBytesGPRS(int packageUid) {
        NetworkStatsManager networkStatsManager = (NetworkStatsManager) getSystemService(NETWORK_STATS_SERVICE);
        TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        }
        String subId = tm.getSubscriberId();

//        NetworkStats summaryStats;
//        long summaryRx = 0;
//        long summaryTx = 0;
//        NetworkStats.Bucket summaryBucket = new NetworkStats.Bucket();
//        long summaryTotal = 0;
//        try {
//            summaryStats = networkStatsManager.querySummary(ConnectivityManager.TYPE_MOBILE, subId, getTimesMonthMorning(), System.currentTimeMillis());
//            do {
//                summaryStats.getNextBucket(summaryBucket);
//                int summaryUid = summaryBucket.getUid();
//                if (packageUid == summaryUid) {
//                    summaryRx += summaryBucket.getRxBytes();
//                    summaryTx += summaryBucket.getTxBytes();
//                    summaryTotal += summaryBucket.getRxBytes() + summaryBucket.getTxBytes();
//                }
//            } while (summaryStats.hasNextBucket());
//        }catch (RemoteException e) {
//        }
//        return fileSizeConver(summaryTotal);
        NetworkStats networkStats = null;
        networkStats = networkStatsManager.queryDetailsForUid(
                    ConnectivityManager.TYPE_MOBILE,
                    subId,
                getTimesMonthMorning(),
                    System.currentTimeMillis(),
                    packageUid);

        long txBytes = 0L;
        long rxBytes = 0L;
        long total = 0L;
        NetworkStats.Bucket bucket = new NetworkStats.Bucket();;
        while (networkStats.hasNextBucket()) {
            networkStats.getNextBucket(bucket);
            txBytes += bucket.getTxBytes();
            rxBytes += bucket.getRxBytes();
        }
        total =txBytes+rxBytes;
        networkStats.close();
        return fileSizeConver(total);
    }
    private  boolean isWifi(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //获取连接的信息
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo != null &&  networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }
    public static boolean is3G(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            return true;
        }
        return false;
    }
    private String showSpeed(double speed) {
        String speedString;
        if (speed >=1048576d) {
            speedString =showFloatFormat.format(speed /1048576d) +"MB/s";
        }else {
            speedString =showFloatFormat.format(speed /1024d) +"KB/s";
        }
        return speedString;
    }
}
