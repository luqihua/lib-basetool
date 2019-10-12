package com.lu.tool.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.annotation.RequiresPermission;
import android.util.Log;

import com.lu.tool.app.BaseApp;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import static android.Manifest.permission.INTERNET;

/**
 * 网络工具
 */
public class NetworkUtil {
    public enum NetworkType {
        NETWORK_ETHERNET,
        NETWORK_WIFI,
        NETWORK_4G,
        NETWORK_3G,
        NETWORK_2G,
        NETWORK_UNKNOWN,
        NETWORK_NO
    }

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
            boolean bOther = false;//其他网络
            for (Network network : networks) {
                NetworkInfo info = manager.getNetworkInfo(network);
                if (info == null) continue;
                if (info.getType() == ConnectivityManager.TYPE_ETHERNET) {
                    bEthernet = info.isConnected();
                } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                    bWifi = info.isConnected();
                } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                    bMobile = info.isConnected();
                } else {
                    //其他网络
                    bOther = info.isConnected();
                }
            }
            isConnect = bEthernet || bWifi || bMobile || bOther;
        } else {
            NetworkInfo info = manager.getActiveNetworkInfo();
            isConnect = info != null && info.isConnected();
        }

        return isConnect;
    }

    /**
     * 获取手机网络ip
     * 按照优先级获取    有线 > 无线 > 移动网或者其他
     *
     * @return
     */
    @RequiresPermission(INTERNET)
    public static String getIPAddress(final boolean useIPv4) {
        try {
            Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
            Map<String, LinkedList<InetAddress>> addsMap = new HashMap<>();
            while (nis.hasMoreElements()) {
                NetworkInterface ni = nis.nextElement();
                if (!ni.isUp() || ni.isLoopback()) continue;
                final String niName = ni.getName();
                addsMap.put(niName, new LinkedList<InetAddress>());

                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    LinkedList<InetAddress> list = addsMap.get(niName);
                    if (list == null) continue;
                    list.addFirst(addresses.nextElement());
                }
            }
            //先看看有没有有线网络
            Set<String> keySet = addsMap.keySet();
            LinkedList<InetAddress> addresses = null;
            for (String key : keySet) {
                if (key.startsWith("eth")) {
                    addresses = addsMap.get(key);
                }
            }
            //没有有线网络  再看看有没有无线网络
            if (addresses == null) {
                for (String key : keySet) {
                    if (key.startsWith("wlan")) {
                        addresses = addsMap.get(key);
                    }
                }
            }

            //有线无线都没有   可能是移动网络或者其他
            if (addresses == null) {
                addresses = new LinkedList<>();
                for (String key : keySet) {
                    addresses.addAll(addsMap.get(key));
                }
            }

            for (InetAddress add : addresses) {
                //回环地址过滤掉
                if (add.isLoopbackAddress()) continue;
                String hostAddress = add.getHostAddress();
                boolean isIPv4 = (add instanceof Inet4Address);
                if (useIPv4) {
                    if (isIPv4) return hostAddress;
                } else {
                    if (!isIPv4) {
                        int index = hostAddress.indexOf('%');
                        return index < 0
                                ? hostAddress.toUpperCase()
                                : hostAddress.substring(0, index).toUpperCase();
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
     *
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
        String mac = "";
        try {
            //所有的网口
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            //先找有线  再找无线
            while (en.hasMoreElements()) {
                NetworkInterface ni = en.nextElement();
                //找到eth0的网口  有线
                if (ni.getName().equals("eth0")) {
                    mac = array2mac(ni.getHardwareAddress());
                }
            }

            if (mac.length() > 0) {
                return mac;
            }

            en = NetworkInterface.getNetworkInterfaces();

            while (en.hasMoreElements()) {
                NetworkInterface ni = en.nextElement();
                //找到wlan0的网口 无线
                if (ni.getName().equals("wlan0")) {
                    mac = array2mac(ni.getHardwareAddress());
                }
            }

        } catch (SocketException e) {
            e.printStackTrace();
        }
        return mac;
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
//        Log.d("NetworkUtil", Arrays.toString(macArray));
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
