package com.centricient.service.logging;

import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import com.google.common.base.Joiner;
import com.google.common.collect.EvictingQueue;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Queue;

public class WiretapAppender extends AppenderBase<ILoggingEvent> {
    static int DEFAULT_LIMIT = 500;
    int limit = DEFAULT_LIMIT;
    private static Queue<String> logList;
    private static Boolean isLoggingOn;

    PatternLayoutEncoder encoder;
    ByteArrayOutputStream stream = new ByteArrayOutputStream();


    @SuppressWarnings("unused")
    public void setLimit(int limit) {
        this.limit = limit;
    }

    @SuppressWarnings("unused")
    public int getLimit() {
        return limit;
    }

    @Override
    public void start() {
        if (this.encoder == null) {
            addError("No encoder set for the appender named ["+ name +"].");
            return;
        }

        try {
            encoder.init(stream);
        } catch (IOException ignored) {
        }

        EvictingQueue<String> q = EvictingQueue.create(limit);
        logList = Queues.synchronizedQueue(q);

        isLoggingOn = true;

        super.start();
    }

    public void append(ILoggingEvent event) {
        if (!isLoggingOn) {
            return;
        }

        try {
            stream.reset();
            this.encoder.doEncode(event);
            logList.add(new String(stream.toByteArray(), StandardCharsets.UTF_8));
        } catch (IOException ignored) {
        }
    }

    public static String getLogData() {

        Joiner joiner = Joiner.on("").skipNulls();

        return joiner.join(Lists.reverse(Lists.newArrayList(logList.toArray())));
    }

    @SuppressWarnings("unused")
    public PatternLayoutEncoder getEncoder() {
        return encoder;
    }

    public void setEncoder(PatternLayoutEncoder encoder) {
        this.encoder = encoder;
    }

    public static Boolean getIsLoggingOn() {
        return isLoggingOn;
    }

    public static void setIsLoggingOn(Boolean isLoggingOn) {
        WiretapAppender.isLoggingOn = isLoggingOn;
    }
}
