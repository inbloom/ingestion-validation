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


package org.slc.sli.ingestion.util;

import org.slf4j.Logger;

/**
 * Utility class for writing exception information without PII to the log file
 *
 * @author tshewchuk
 *
 */
public class LogUtil {

    private static boolean includeExceptionMessage;

    /**
     * Write the appropriate trace message to the log file
     *
     * @param log
     *            logger to write the message
     * @param message
     *            specific message to write to the log file
     * @param exception
     *            the exception which caused the log file entry
     */
    public static void trace(Logger log, String message, Exception exception) {
        // Log the error with a message-safe exception.
        if (log.isTraceEnabled()) {
            Exception loggingException = createLoggingException(exception);
            log.trace(message, loggingException);
        }
    }

    /**
     * Write the appropriate debug message to the log file
     *
     * @param log
     *            logger to write the message
     * @param message
     *            specific message to write to the log file
     * @param exception
     *            the exception which caused the log file entry
     */
    public static void debug(Logger log, String message, Exception exception) {
        // Log the error with a message-safe exception.
        if (log.isDebugEnabled()) {
            Exception loggingException = createLoggingException(exception);
            log.debug(message, loggingException);
        }
    }

    /**
     * Write the appropriate info message to the log file
     *
     * @param log
     *            logger to write the message
     * @param message
     *            specific message to write to the log file
     * @param exception
     *            the exception which caused the log file entry
     */
    public static void info(Logger log, String message, Exception exception) {
        // Log the error with a message-safe exception.
        if (log.isInfoEnabled()) {
            Exception loggingException = createLoggingException(exception);
            log.info(message, loggingException);
        }
    }

    /**
     * Write the appropriate warning message to the log file
     *
     * @param log
     *            logger to write the message
     * @param message
     *            specific message to write to the log file
     * @param exception
     *            the exception which caused the log file entry
     */
    public static void warn(Logger log, String message, Exception exception) {
        // Log the error with a message-safe exception.
        if (log.isWarnEnabled()) {
            Exception loggingException = createLoggingException(exception);
            log.warn(message, loggingException);
        }
    }

    /**
     * Write the appropriate error message to the log file
     *
     * @param log
     *            logger to write the message
     * @param message
     *            specific message to write to the log file
     * @param exception
     *            the exception which caused the log file entry
     */
    public static void error(Logger log, String message, Exception exception) {
        // Log the error with a message-safe exception.
        if (log.isErrorEnabled()) {
            Exception loggingException = createLoggingException(exception);
            log.error(message, loggingException);
        }
    }

    /**
     * Create a message-safe exception from the original exception
     *
     * @param exception
     *            original exception from which the message-safe exception will be created
     *
     * @return Exception
     *         the new message-safe exception
     */
    private static Exception createLoggingException(Exception exception) {
        if (includeExceptionMessage) {
            return exception;
        }

        Exception loggingException;
        if (exception.getCause() == null) {
            loggingException = new Exception(exception.getClass().toString());
        } else {
            loggingException = new Exception(exception.getClass().toString(),
                    createLoggingException((Exception) exception.getCause()));
        }
        loggingException.setStackTrace(exception.getStackTrace());
        return loggingException;
    }

    public static boolean isIncludeExceptionMessage() {
        return includeExceptionMessage;
    }

    public static void setIncludeExceptionMessage(boolean includeExceptionMessage) {
        LogUtil.includeExceptionMessage = includeExceptionMessage;
    }

}
