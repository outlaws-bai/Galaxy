package org.m2sec.core.enums;

/**
 * @author: outlaws-bai
 * @date: 2024/7/10 20:11
 * @description:
 */

public enum Protocol {
    HTTP, HTTPS;

    public int defaultPort() {
        if (this.equals(HTTP)) return 80;
        else return 443;
    }


    public static Protocol of(boolean isSecure) {
        if (!isSecure) return HTTP;
        else return HTTPS;
    }

    public String toRaw() {
        return this.name().toLowerCase();
    }

}
