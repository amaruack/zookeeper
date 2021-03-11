package com.eseict.zoo;

import com.google.common.collect.Lists;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;

public class Test {

    public static void main(String[] args) {
            List<String> list = Lists.newArrayList();
            try {
                for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                    NetworkInterface intf = en.nextElement();
                    for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()
                            /*&& !inetAddress.isSiteLocalAddress()*/) { // TODO isSiteLocalAddress 설정 여부 추후에 처리해야됨
                            System.out.println("inner : " + inetAddress.getHostAddress().toString());
//                            list.add(inetAddress.getHostAddress().toString());
//						return inetAddress.getHostAddress().toString();
                        } else {
                            System.out.println("outer : " + inetAddress.getHostAddress().toString());
                        }

                    }
                }
            } catch (SocketException ex) {
                System.out.println(ex.getMessage());
            }
    }

}
