package com.eseict.zoo.proc;

public class ServerInfo {

    private String id;  // db에서 처리되는 id 값
    private String mac; // mac address
//    private String key; // rino 시스템에서 처리되는 key 값 // oauth2 에서 발행되는 클라이언트 아이디 ??
//    private String secret;  // rino 시스템 에서 처리되는 secret 값 // oauth2에서도 발행됨
    private String host;    // server host , ip
    private String port;    // 구동 port
    private Double cpuUsage;    // cpu 사용량 ex)0.242323
    private Long memoryFree;    // memory free 메모리 잔여량 ex) 1241241214124
    private Long memoryTotal;   // memory time 전체 메모리 ex) 1241241214124
    private Long timestamp;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public Double getCpuUsage() {
        return cpuUsage;
    }

    public void setCpuUsage(Double cpuUsage) {
        this.cpuUsage = cpuUsage;
    }

    public Long getMemoryFree() {
        return memoryFree;
    }

    public void setMemoryFree(Long memoryFree) {
        this.memoryFree = memoryFree;
    }

    public Long getMemoryTotal() {
        return memoryTotal;
    }

    public void setMemoryTotal(Long memoryTotal) {
        this.memoryTotal = memoryTotal;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
