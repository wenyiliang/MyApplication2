package com.example.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;
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
}
