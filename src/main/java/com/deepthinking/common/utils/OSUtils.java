package com.deepthinking.common.utils;

import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Maps;
import com.sun.management.OperatingSystemMXBean;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.RuntimeMXBean;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.*;

@Slf4j
public class OSUtils {


    public static LinkedHashMap<String, Object> getSystemInfo() {
        LinkedHashMap<String, Object> info = Maps.newLinkedHashMap();
        info.put("Hostname", processCommand("/", "hostname"));
        info.put("OSName", getReleaseName());
        info.put("IP", getIP(""));
        info.put("MAC", getMac());

        LinkedHashMap<String, Object> t = Maps.newLinkedHashMap();
        Runtime runtime = Runtime.getRuntime();
        long total = (runtime.totalMemory()) / (1024 * 1024);
        long max = (runtime.maxMemory()) / (1024 * 1024);
        long free = (runtime.freeMemory()) / (1024 * 1024);
        t.put("Version", System.getProperty("java.runtime.name") + " " + System.getProperty("java.runtime.version"));
        t.put("TotalMemory", total + "MB");
        t.put("MaxMemory", max + "MB");
        t.put("FreeMemory", free + "MB");
        t.put("RealUsage", (max - total + free) + "MB");
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        t.put("HeapUsage", memoryMXBean.getHeapMemoryUsage().getUsed() / (1024 * 1024) + "MB");
        t.put("NonHeapUsage", memoryMXBean.getNonHeapMemoryUsage().getUsed() / (1024 * 1024) + "MB");
        info.put("JVM", t);

        OperatingSystemMXBean operatingSystemMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        t = Maps.newLinkedHashMap();
        t.put("ThreadCount", ManagementFactory.getThreadMXBean().getThreadCount());
        t.put("AvailableProcessors", operatingSystemMXBean.getAvailableProcessors());
        t.put("TotalPhysicalMemory", operatingSystemMXBean.getTotalPhysicalMemorySize() / (1024 * 1024) + "MB");
        t.put("TotalSwapSpace", operatingSystemMXBean.getTotalSwapSpaceSize() / (1024 * 1024) + "MB");
        t.put("FreePhysicalMemory", operatingSystemMXBean.getFreePhysicalMemorySize() / (1024 * 1024) + "MB");
        t.put("FreeSwapSpace", operatingSystemMXBean.getFreeSwapSpaceSize() / (1024 * 1024) + "MB");
        t.put("CommittedVirtualMemory", operatingSystemMXBean.getCommittedVirtualMemorySize() / (1024 * 1024) + "MB");
//        t.put("ProcessCpuTime", operatingSystemMXBean.getProcessCpuTime());
//        t.put("ProcessCpuTime2", DateUtils.formatDateTime(operatingSystemMXBean.getProcessCpuTime()));
//        t.put("ProcessCpuLoad", operatingSystemMXBean.getProcessCpuLoad());
//        t.put("SystemCpuLoad", operatingSystemMXBean.getSystemCpuLoad());
        info.put("OperatingSystem", t);

        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        t = Maps.newLinkedHashMap();
        t.put("StartTime", DateUtils.format(DateUtils.toLocalDateTime(runtimeMXBean.getStartTime())));
        t.put("UpTime", DateUtils.formatDateTime(runtimeMXBean.getUptime()));
        t.put("TimeZone", TimeZone.getDefault().getID());
        info.put("Runtime", t);
        return info;
    }

    /**
     * 获取当前操作系统名称. return 操作系统名称 例如:windows,Linux,Unix等.
     */
    public static String getOSName() {
        return System.getProperty("os.name").toLowerCase();
    }

    public static String getReleaseName() {
        String str = processCommand("/","cat /etc/os-release | grep PRETTY_NAME");
        return StringUtil.contains(str, "\"") ? str.split("\"")[1] : getOSName();
    }

    public static boolean isWindows() {
        return getOSName().startsWith("windows");
    }

    public static boolean isLinux() {
        return getOSName().startsWith("linux");
    }


    public static String getIP(String netName) {
        String localIpAddress = null;
        try {
            for (Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces(); interfaces.hasMoreElements(); ) {
                NetworkInterface networkInterface = interfaces.nextElement();
                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                if (StringUtil.isEmpty(netName)) {
                    while (addresses.hasMoreElements()) {
                        InetAddress ipAddress = addresses.nextElement();
                        if (isPublicIpAddress(ipAddress)) {
                            return ipAddress.getHostAddress();
                        }
                        if (isLocalIpAddress(ipAddress)) {
                            localIpAddress = ipAddress.getHostAddress();
                        }
                    }
                } else if (netName.equals(networkInterface.getName())) {
                    if (addresses.hasMoreElements()) {
                        return addresses.nextElement().getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
        }
        return localIpAddress;
    }

    private static boolean isPublicIpAddress(InetAddress ipAddress) {
        return !ipAddress.isSiteLocalAddress() && !ipAddress.isLoopbackAddress() && !isV6IpAddress(ipAddress);
    }

    private static boolean isLocalIpAddress(InetAddress ipAddress) {
        return ipAddress.isSiteLocalAddress() && !ipAddress.isLoopbackAddress() && !isV6IpAddress(ipAddress);
    }

    private static boolean isV6IpAddress(InetAddress ipAddress) {
        return ipAddress.getHostAddress().contains(":");
    }


    public static String getMac() {
        String mac;
        if (isWindows()) {
            mac = getWindowsMACAddress();
        } else if (isLinux()) {
            mac = getLinuxMACAddress();
        } else {
            mac = getUnixMACAddress();
        }
        return mac == null ? "" : mac;
    }

    public static String getWebContentPath() {
        String path = Thread.currentThread().getContextClassLoader().getResource("").toString();
        String temp = path.replaceFirst("file:/", "").replaceFirst("WEB-INF/classes/", "");
        String separator = System.getProperty("file.separator");
        String resultPath = temp.replaceAll("/", separator + separator);
        return resultPath;
    }

    public static String getClassPath() {
        String path = Thread.currentThread().getContextClassLoader().getResource("").toString();
        String temp = path.replaceFirst("file:/", "");
        String separator = System.getProperty("file.separator");
        String resultPath = temp.replaceAll("/", separator + separator);
        return resultPath;
    }

    public static String getSystempPath() {
        return System.getProperty("java.io.tmpdir");
    }

    public static String getSeparator() {
        return System.getProperty("file.separator");
    }

    public static String getUniquenessNO() {
        String re = "";
        try {
            String os = getOSName();
            if (os.startsWith("windows")) {
                Scanner scc = new Scanner(processCommand("/", "wmic", "cpu", "get", "ProcessorId"));
                scc.next();
                String processorId = scc.next();
                scc.close();
                scc = new Scanner(processCommand("/", "wmic", "baseboard", "get", "serialnumber"));
                scc.next();
                String serialnumber = scc.next();
                scc.close();
                re = serialnumber + processorId + InetAddress.getLocalHost().getHostName();
                re = re.replaceAll("-", "");
                re = NumberUtils.getOffsetString(re);
                re = re.length() > 32 ? re.substring(re.length() - 32) : re;
            } else {
                Scanner scc = new Scanner(processCommand("/", "/bin/sh", "-c", "dmidecode |grep UUID"));
                scc.next();
                String serial = scc.next().replaceAll("-", "");
                re = serial;
                scc.close();
            }

        } catch (Exception e) {
            System.out.println(e);
        }
        return re;
    }


    public static String processCommand(String path, List<String> command) {
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        return processCommand(path, processBuilder);
    }

    public static String processCommand(String path, String... command) {
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        return processCommand(path, processBuilder);
    }

    public static String processCommand(String path, ProcessBuilder processBuilder) {
        processBuilder.redirectErrorStream(true);
        processBuilder.directory(new File(path));
        StringBuilder builder = new StringBuilder();
        Scanner scanner = null;
        try {
            Process process = processBuilder.start();
            scanner = new Scanner(new InputStreamReader(process.getInputStream(), "GBK"));
            while (scanner.hasNext()) {
                builder.append(scanner.next());
            }
            process.waitFor();
            int exit = process.exitValue();
            if (exit != 0) {
                throw new IOException("failed to execute:" + processBuilder.command() + " with result:" + builder);
            }
        } catch (IOException | InterruptedException ignored) {

        } finally {
            if (scanner != null) {
                try {
                    scanner.close();
                } catch (Exception ignored) {

                }
            }
        }
        return builder.toString();
    }

    /**
     * @param strCmd
     * @param index  结果的位置，-1表示全部返回
     * @return
     * @throws IOException
     */
    public static String executeCmd(String strCmd, int index) {
        StringBuilder sbCmd = new StringBuilder();
        try {
            Process p = Runtime.getRuntime().exec(strCmd);
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream(), "GBK"));
            String line;
            for (int i = 0; (line = br.readLine()) != null; i++) {
                if (-1 == index || i == index)
                    sbCmd.append(line).append("\n");
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return sbCmd.toString();
    }

    /**
     * 获取Unix网卡的mac地址.
     *
     * @return mac地址
     */
    public static String getUnixMACAddress() {
        String mac = null;
        BufferedReader bufferedReader = null;
        Process process = null;
        try {
            //Unix下的命令，一般取eth0作为本地主网卡 显示信息中包含有mac地址信息
            process = Runtime.getRuntime().exec("ifconfig eth0");
            bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = null;
            int index = -1;
            while ((line = bufferedReader.readLine()) != null) {
                // 寻找标示字符串[hwaddr]
                index = line.toLowerCase().indexOf("hwaddr");
                if (index != -1) {
                    // 取出mac地址并去除2边空格
                    mac = line.substring(index + "hwaddr".length() + 1).trim();
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            bufferedReader = null;
            process = null;
        }

        return mac;
    }

    /**
     * 获取Linux网卡的mac地址.
     *
     * @return mac地址
     */
    public static String getLinuxMACAddress() {
        String mac = null;
        BufferedReader bufferedReader = null;
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("ifconfig eth0");
            bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = null;
            int index = -1;
            while ((line = bufferedReader.readLine()) != null) {
                index = line.toLowerCase().indexOf("ether");
                if (index != -1) {
                    mac = line.split(" ")[1];
                    break;
                }
                index = line.toLowerCase().indexOf("物理地址");
                if (index != -1) {
                    mac = line.split(" ")[1];
                    break;
                }
            }
        } catch (IOException ignored) {
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException ignored) {
            }
            bufferedReader = null;
            process = null;
        }
        // 取不到，试下Unix取发
        if (mac == null) {
            return getUnixMACAddress();
        }
        return mac;
    }

    /**
     * 获取 windows 网卡的mac地址.
     */
    public static String getWindowsMACAddress() {
        String mac = null;
        BufferedReader bufferedReader = null;
        Process process = null;
        try {
            // windows下的命令，显示信息中包含有mac地址信息
            process = Runtime.getRuntime().exec("ipconfig /all");
            bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = null;
            int index = -1;
            while ((line = bufferedReader.readLine()) != null) {
                // 寻找标示字符串[physical address]
//				index = line.toLowerCase().indexOf("physical address");
//				if (index != -1) {
                if (line.split("-").length == 6) {
                    index = line.indexOf(":");
                    if (index != -1) {
                        // 取出mac地址并去除2边空格
                        mac = line.substring(index + 1).trim();
                    }
                    break;
                }
                index = line.toLowerCase().indexOf("物理地址");
                if (index != -1) {
                    index = line.indexOf(":");
                    if (index != -1) {
                        // 取出mac地址并去除2边空格
                        mac = line.substring(index + 1).trim();
                    }
                    break;
                }
            }
        } catch (IOException ignored) {
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException ignored) {
            }
            bufferedReader = null;
            process = null;
        }

        return mac;
    }


    /**
     * 测试用的main方法.
     */
    public static void main(String[] argc) {
        System.out.println(JSONObject.toJSONString(getSystemInfo()));

//		System.out.println("os name: " + getOSName());
//		System.out.println("UID:     " + getUniquenessNO());
//		System.out.println("Length:  " + getUniquenessNO().length());
//        System.out.println(processCommand("/home", "ls", "-la", "|", "grep", "o"));
    }

}