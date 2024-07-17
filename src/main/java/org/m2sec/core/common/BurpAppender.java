package org.m2sec.core.common;

import burp.api.montoya.MontoyaApi;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

/**
 * @author: outlaws-bai
 * @date: 2024/7/17 20:55
 * @description:
 */

public class BurpAppender extends AppenderBase<ILoggingEvent> {

    private final MontoyaApi api;

    private final PatternLayoutEncoder encoder;

    public BurpAppender(MontoyaApi api, PatternLayoutEncoder encoder) {
        this.api = api;
        this.encoder = encoder;
    }

    @Override
    protected void append(ILoggingEvent eventObject) {
        String message = encoder.getLayout().doLayout(eventObject);
        if (eventObject.getLevel().toInt() <= Level.INFO_INT) {
            api.logging().logToOutput(message);
        } else {
            api.logging().logToError(message);
        }
    }
}
