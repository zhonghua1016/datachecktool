package com.example.datacheck.config;


/**
 * created by yuanjunjie on 2020/5/8 6:41 PM
 */
public class DBConfig {
    private String taskName;
    private String srcIp;
    private Integer srcPort;
    private String srcName;
    private String srcPassword;

    private String destIp;
    private Integer destPort;
    private String destName;
    private String destPassword="";


    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getSrcIp() {
        return srcIp;
    }

    public void setSrcIp(String srcIp) {
        this.srcIp = srcIp;
    }

    public Integer getSrcPort() {
        return srcPort;
    }

    public void setSrcPort(Integer srcPort) {
        this.srcPort = srcPort;
    }

    public String getSrcName() {
        return srcName;
    }

    public void setSrcName(String srcName) {
        this.srcName = srcName;
    }

    public String getSrcPassword() {
        return srcPassword;
    }

    public void setSrcPassword(String srcPassword) {
        this.srcPassword = srcPassword;
    }

    public String getDestIp() {
        return destIp;
    }

    public void setDestIp(String destIp) {
        this.destIp = destIp;
    }

    public Integer getDestPort() {
        return destPort;
    }

    public void setDestPort(Integer destPort) {
        this.destPort = destPort;
    }

    public String getDestName() {
        return destName;
    }

    public void setDestName(String destName) {
        this.destName = destName;
    }

    public String getDestPassword() {
        return destPassword;
    }

    public void setDestPassword(String destPassword) {
        this.destPassword = destPassword;
    }

    @Override
    public String toString() {
        return "DBConfig{" +
                "taskName='" + taskName + '\'' +
                ", srcIp='" + srcIp + '\'' +
                ", srcPort=" + srcPort +
                ", srcName='" + srcName + '\'' +
                ", srcPassword='" + srcPassword + '\'' +
                ", destIp='" + destIp + '\'' +
                ", destPort=" + destPort +
                ", destName='" + destName + '\'' +
                ", destPassword='" + destPassword + '\'' +
                '}';
    }
}
