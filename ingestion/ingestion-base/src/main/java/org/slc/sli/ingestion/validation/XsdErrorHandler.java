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


package org.slc.sli.ingestion.validation;

import java.io.File;

import org.springframework.context.MessageSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import org.slc.sli.ingestion.util.spring.MessageSourceHelper;

/**
 *
 * @author npandey
 *
 */
public class XsdErrorHandler implements XsdErrorHandlerInterface {

    private ErrorReport errorReport;

    private MessageSource messageSource;

    private String errorPrefix = "";

    /**
     * Report a SAX parsing warning.
     *
     * @param ex
     *            Parser exception thrown by SAX
     */
    @Override
    public void warning(SAXParseException ex) {
        String errorMessage = getErrorMessage(ex);
        errorReport.warning(errorPrefix + errorMessage, XsdErrorHandler.class);
    }

    /**
     * Report a SAX parsing error.
     *
     * @param ex
     *            Parser exception thrown by SAX
     */
    @Override
    public void error(SAXParseException ex) {
        String errorMessage = getErrorMessage(ex);
        errorReport.warning(errorPrefix + errorMessage, XsdErrorHandler.class);
    }

    /**
     * Report a fatal SAX parsing error.
     *
     * @param ex
     *            Parser exception thrown by SAX
     * @throws SAXParseException
     *             Parser exception thrown by SAX
     */
    @Override
    public void fatalError(SAXParseException ex) throws SAXException {
        String errorMessage = getErrorMessage(ex);
        errorReport.warning(errorPrefix + errorMessage, XsdErrorHandler.class);
        throw ex;
    }

    /**
     * Incorporate the SAX error message into an ingestion error message.
     *
     * @param saxErrorMessage
     *            Error message returned by SAX
     * @return Error message returned by Ingestion
     */
    private String getErrorMessage(SAXParseException ex) {
        // Create an ingestion error message incorporating the SAXParseException information.
        String fullParsefilePathname = (ex.getSystemId() == null) ? "" : ex.getSystemId();
        File parseFile = new File(fullParsefilePathname);

        // Return the ingestion error message.
        return MessageSourceHelper.getMessage(messageSource, "XSD_VALIDATION_ERROR", parseFile.getName(),
                String.valueOf(ex.getLineNumber()), String.valueOf(ex.getColumnNumber()), ex.getMessage());
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public void setErrorReport(ErrorReport errorReport) {
        this.errorReport = errorReport;
    }

    public void setErrorPrefix(String errorPrefix) {
        this.errorPrefix = errorPrefix;
    }

}
