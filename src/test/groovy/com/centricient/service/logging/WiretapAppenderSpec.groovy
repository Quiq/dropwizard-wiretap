package com.centricient.service.logging

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.spi.LoggingEvent
import com.google.common.base.Splitter
import spock.lang.Specification


public class WiretapAppenderSpec extends Specification {
    WiretapAppender appender
    LoggerContext context = new LoggerContext()
    Logger logger = new Logger("fake logger", null, context)

    def "Logged data works and can be fetched"(){
        setupWiretapAppender(1)

        appender.append(new LoggingEvent("class", logger, Level.DEBUG, "My Message", null))

        def result = WiretapAppender.getLogData()

        expect:
        result.contains("My Message")
    }

    def "If logging is turned off nothing is logged"(){
        setupWiretapAppender(1)

        WiretapAppender.setIsLoggingOn(false)
        appender.append(new LoggingEvent("class", logger, Level.DEBUG, "My Message", null))

        def result = WiretapAppender.getLogData()

        expect:
        result.isEmpty()
    }

    def "Log data returned in reverse order"() {
        setupWiretapAppender(2)

        appender.append(new LoggingEvent("class", logger, Level.DEBUG, "First", null))
        appender.append(new LoggingEvent("class", logger, Level.DEBUG, "Second", null))

        def result = Splitter.on("\n").split(WiretapAppender.getLogData())

        expect:
        result[0].contains("Second")
        result[1].contains("First")
    }

    void setupWiretapAppender(int limit) {
        appender = new WiretapAppender()
        appender.setLimit(limit)

        final PatternLayoutEncoder patternEncoder = new PatternLayoutEncoder();
        patternEncoder.setContext(context);
        patternEncoder.setPattern("%-5p [%thread] [%d] %c: %m%n%rEx");
        patternEncoder.start();

        appender.setEncoder(patternEncoder)
        appender.start()
    }
}