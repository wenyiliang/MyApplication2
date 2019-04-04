package com.example.myapplication;

import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

public class WifiAppActivity extends AppCompatActivity {
    TrafficHelper helper;
    private List<TrafficInfo> trafficInfos;
    private TrafficInfoAdapter adapter;
    private ListView listview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifiapp);
        listview=(ListView)findViewById(R.id.applist);
        helper = new TrafficHelper(this);
        trafficInfos=helper.getInternetTrafficInfos();
        adapter = new TrafficInfoAdapter(this, R.layout.content, trafficInfos);
        listview.setAdapter(adapter);
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
            String total = getPackageTxDayBytesWifi(uid);
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
                    helper.getTimesMonthMorning(),
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
        return helper.fileSizeConver(summaryTotal);
    }
}

