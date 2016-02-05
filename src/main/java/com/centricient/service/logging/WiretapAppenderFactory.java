package com.centricient.service.logging;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.Layout;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.common.base.Preconditions;
import io.dropwizard.logging.AbstractAppenderFactory;

@JsonTypeName("wiretap")
public class WiretapAppenderFactory extends AbstractAppenderFactory {
    @Override
    public Appender<ILoggingEvent> build(LoggerContext context, String s, Layout<ILoggingEvent> layout) {
        Preconditions.checkNotNull(context);
        WiretapAppender appender = new WiretapAppender();

        final PatternLayoutEncoder patternEncoder = new PatternLayoutEncoder();
        patternEncoder.setContext(context);
        patternEncoder.setPattern("%-5p [%thread] [%d] %c: %m%n%rEx");
        patternEncoder.start();

        appender.setContext(context);
        appender.setName("wiretap-appender");
        appender.setEncoder(patternEncoder);

        addThresholdFilter(appender, threshold);
        appender.start();

        return wrapAsync(appender);
    }
}
