package code.modify.tool.tests;

import com.alibaba.dcm.DnsCacheManipulator;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

public class TestDNS {
    // 在主方法的静态代码块中加载dns-cache.properties的dns配置
    static {
        DnsCacheManipulator.loadDnsCacheConfig();
    }

    public static void main(String[] args){
        try {
            String ip = InetAddress.getByName("nexus.trunk.com").getHostAddress();
            System.out.println("nexus ip = " + ip);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
