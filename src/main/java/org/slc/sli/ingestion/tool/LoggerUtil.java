/*
 * Copyright 2012 Shared Learning Collaborative, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.slc.sli.ingestion.tool;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.FileAppender;

import org.slf4j.LoggerFactory;



/**
 * Provides logging to console or file for the offline tool
 *
 * @author npandey
 */
public class LoggerUtil {
    private static Logger logger = null;
    private static LoggerContext loggerContext;
    private static PatternLayoutEncoder encoder;
    private static FileAppender<ILoggingEvent> fileAppender;

    public static FileAppender<ILoggingEvent> getFileAppender() {
        return fileAppender;
    }

    private static ConsoleAppender<ILoggingEvent> consoleAppender;

    static {
        loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        logger = loggerContext.getLogger(LoggerUtil.class);
        consoleAppender = (ConsoleAppender<ILoggingEvent>) logger.getAppender("ConsoleAppender");

        encoder = new PatternLayoutEncoder();
        encoder.setContext(loggerContext);
        encoder.setPattern("%date %-5level %msg%n");
        encoder.start();

        fileAppender = new FileAppender<ILoggingEvent>();
        fileAppender.setContext(loggerContext);
        fileAppender.setName("ToolLog");
        fileAppender.setEncoder(encoder);
    }
    public static Logger getLogger() {
        return logger;
    }

    public static void logToConsole() {
        logger.detachAndStopAllAppenders();
        consoleAppender.start();
        logger.addAppender(consoleAppender);
    }

    public static void logToFile(String file) {
        logger.detachAndStopAllAppenders();

        fileAppender.setFile(file);
        fileAppender.start();

        logger.addAppender(fileAppender);
    }

}
