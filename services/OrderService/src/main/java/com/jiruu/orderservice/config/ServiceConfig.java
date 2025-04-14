package com.jiruu.orderservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "services")
public class ServiceConfig {
    public static class Service {
        private String ip;
        private int port;

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }
    }

    public static class Multicast {
        private String group;
        private int port;

        public String getGroup() {
            return group;
        }

        public void setGroup(String group) {
            this.group = group;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }
    }

    public static class uniCast {
        private int port;

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }
    }

    private Service matchingEngine;
    private Multicast multicast;
    private uniCast uniCast;

    public Service getMatchingEngine() {
        return matchingEngine;
    }

    public void setMatchingEngine(Service matchingEngine) {
        this.matchingEngine = matchingEngine;
    }

    public Multicast getMulticast() {
        return multicast;
    }

    public void setMulticast(Multicast multicast) {
        this.multicast = multicast;
    }

    public uniCast getUniCast() {
        return uniCast;
    }

    public void setUniCast(uniCast uniCast) {
        this.uniCast = uniCast;
    }
}
