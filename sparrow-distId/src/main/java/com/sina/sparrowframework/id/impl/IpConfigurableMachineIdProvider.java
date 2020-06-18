package com.sina.sparrowframework.id.impl;

import com.sina.sparrowframework.id.MachineIdProvider;
import com.sina.sparrowframework.id.Utils;
import org.springframework.util.StringUtils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class IpConfigurableMachineIdProvider implements MachineIdProvider {
    private static final Logger log = LoggerFactory.getLogger(IpConfigurableMachineIdProvider.class);

    private Long machineId;
    private String applicationName;
    private List<String> applicationNameGroupList = new ArrayList();


    private Map<String, Long> ipsMap = new HashMap<String, Long>();
    private static char[] machineIdArray ={'A','B','C','D','E','F','G','H','I','J',
            'K','L','M','N','O','P','Q','R','S','T','U','V','W','S','Y','Z'};


    public IpConfigurableMachineIdProvider() {
        log.debug("IpConfigurableMachineIdProvider constructed.");
    }

    public IpConfigurableMachineIdProvider(String ips,String applicationName,String applicationNameGroup) {

        this.applicationName = applicationName;
        if (!StringUtils.isEmpty(applicationNameGroup)) {
            applicationNameGroupList.addAll(Arrays.asList(applicationNameGroup.split(",")));
        }
        setIps(ips);
        init();
    }

    public void init() {
        String ip = Utils.getHostIp();

//        if (StringUtils.isEmpty(ip)) {
//            String msg = "Fail to get host IP address. Stop to initialize the IpConfigurableMachineIdProvider provider.";
//
//            log.error(msg);
//            throw new IllegalStateException(msg);
//        }
//
//        if (!ipsMap.containsKey(ip)) {
//            String msg = String
//                    .format("Fail to configure ID for host IP address %s. Stop to initialize the IpConfigurableMachineIdProvider provider.",
//                            ip);
//
//            log.error(msg);
//            throw new IllegalStateException(msg);
//        }

        machineId = ipsMap.get(ip);

        log.info("IpConfigurableMachineIdProvider.init ip {} id {}", ip,
                machineId);
    }

    public void setIps(String ips) {
        log.info("IpConfigurableMachineIdProvider ips {}", ips);
        if (!StringUtils.isEmpty(ips)) {
            long maIdme = 0;
            for (int j = 0; j< applicationNameGroupList.size(); j++) {
                if (applicationName.equals(applicationNameGroupList.get(j))) {
                    maIdme = maIdme + machineIdArray[j];
                }
            }
            String[] ipArray = ips.split(",");
            for (int i = 0; i < ipArray.length; i++) {
                 maIdme = maIdme + machineIdArray[i];
                ipsMap.put(ipArray[i],maIdme);
            }
        }
    }

    public long getMachineId() {
        return Objects.isNull(machineId) ? 1 : machineId;
    }

    public void setMachineId(long machineId) {
        this.machineId = machineId;
    }
}
