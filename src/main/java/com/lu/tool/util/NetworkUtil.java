package com.lu.tool.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;

import com.lu.tool.app.BaseApp;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Enumeration;

/**
 * 网络监测工具
 */
public class NetworkUtil {
    /**
     * 网络是否可用
     *
     * @return
     */
    public static boolean isNetworkAvailable() {
        ConnectivityManager manager = BaseApp.getService(Context.CONNECTIVITY_SERVICE);
        if (manager == null) return false;
        boolean isConnect;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Network[] networks = manager.getAllNetworks();
            boolean bEthernet = false;//有线网是否可用
            boolean bWifi = false;//wifi无线是否可用
            boolean bMobile = false;//移动网是否可用
            for (Network network : networks) {
                NetworkInfo info = manager.getNetworkInfo(network);
                if (info == null) continue;
                if (info.getType() == ConnectivityManager.TYPE_ETHERNET) {
                    bEthernet = info.isConnected();
                } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                    bWifi = info.isConnected();
                } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                    bMobile = info.isConnected();
                }else {
                    //其他网络
                }
            }
            isConnect = bEthernet || bWifi || bMobile;
        } else {
            NetworkInfo info = manager.getActiveNetworkInfo();
            isConnect = info != null && info.isConnected();
        }

        return isConnect;
    }

    /**
     * 获取手机网络ip
     *
     * @return
     */
    public static String getIPAddress() {
        if (!isNetworkAvailable()) {
            Log.d("NetworkUtil", "无网络连接");
            return null;
        }

        try {
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            while (en.hasMoreElements()) {
                Enumeration<InetAddress> enumIpAddr = en.nextElement().getInetAddresses();
                while (enumIpAddr.hasMoreElements()) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();

                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }

        return "";
    }

    /**
     * 获取网络类型
     * @return
     */
    public static int getNetworkType() {
        ConnectivityManager manager = BaseApp.getService(Context.CONNECTIVITY_SERVICE);
        if (manager == null) return -1;
        NetworkInfo info = manager.getActiveNetworkInfo();

        if (info == null || !info.isConnected()) {
            Log.d("NetworkUtil", "无网络连接");
            return -1;
        }

        if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
            //移动网
        } else if (info.getType() == ConnectivityManager.TYPE_ETHERNET) {
            //有线网

        } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {
            //无线wlan
        } else {
            //其他网络
        }
        return 1;
    }


    /**
     * 获取本机mac地址   通过遍历网口   找到wlan0 拿到mac地址
     *
     * @return
     */
    public static String getMacAddress() {
        try {
            //所有的网口
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            while (en.hasMoreElements()) {
                NetworkInterface ni = en.nextElement();
                //找到wlan0的网口
//                if (ni.getName().equals("wlan0")) {
//                    return array2mac(ni.getHardwareAddress());
//                }
                //找到eth0的网口  有线
                if (ni.getName().equals("eth0")) {
                    return array2mac(ni.getHardwareAddress());
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 将ip的整数形式转换成ip形式
     *
     * @param ipInt
     * @return
     */
    public static String int2Ip(long ipInt) {
        StringBuilder sb = new StringBuilder();
        sb.append(ipInt & 0xFF).append(".");
        sb.append((ipInt >> 8) & 0xFF).append(".");
        sb.append((ipInt >> 16) & 0xFF).append(".");
        sb.append((ipInt >> 24) & 0xFF);
        return sb.toString();
    }

    /**
     * 将byte[]数组形式的mac地址转为 xx：xx形式
     *
     * @param macArray
     * @return
     */
    public static String array2mac(byte[] macArray) {
        Log.d("NetworkUtil", Arrays.toString(macArray));
        if (macArray == null) return "null";
        StringBuilder res1 = new StringBuilder();
        for (byte b : macArray) {
            res1.append(String.format("%02X:", b));
        }

        if (res1.length() > 0) {
            res1.deleteCharAt(res1.length() - 1);
        }
        return res1.toString();
    }
}
