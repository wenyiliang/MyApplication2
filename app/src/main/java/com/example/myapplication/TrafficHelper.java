package com.example.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TrafficHelper {
    private PackageManager pm;
    public TrafficHelper(Context context) {
        super();
        pm = context.getPackageManager();
    }
    public List<TrafficInfo> getInternetTrafficInfos(){
        List<TrafficInfo> trafficInfos = new ArrayList<TrafficInfo>();
        List<PackageInfo> installedPackages = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES | PackageManager.GET_PERMISSIONS);
        for(PackageInfo info : installedPackages){
            String[] permissions = info.requestedPermissions;
            if(permissions != null && permissions.length > 0){
                for(String permission : permissions){
                    if(permission.equals(Manifest.permission.INTERNET)){
                        ApplicationInfo applicationInfo = info.applicationInfo;
                        Drawable icon = applicationInfo.loadIcon(pm);
                        String appname = applicationInfo.loadLabel(pm).toString();
                        String packagename = applicationInfo.packageName;
                        int uid = applicationInfo.uid;
                        TrafficInfo trafficInfo = new TrafficInfo(icon, appname, packagename, uid);
                        trafficInfos.add(trafficInfo);
                    }
                }
            }
        }
        return trafficInfos;
    }
    public long getTimesMonthMorning() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        return cal.getTimeInMillis();
    }
    public  String fileSizeConver(long fileS) {
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
}
