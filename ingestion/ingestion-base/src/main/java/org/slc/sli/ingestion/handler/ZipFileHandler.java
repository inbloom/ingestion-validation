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


package org.slc.sli.ingestion.handler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.apache.commons.compress.archivers.zip.UnsupportedZipFeatureException;
import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;

import org.slc.sli.ingestion.FileProcessStatus;
import org.slc.sli.ingestion.landingzone.ZipFileUtil;
import org.slc.sli.ingestion.util.spring.MessageSourceHelper;
import org.slc.sli.ingestion.validation.ErrorReport;

/**
 * @author ablum
 *
 */
public class ZipFileHandler extends AbstractIngestionHandler<File, File> implements MessageSourceAware {
    private static final Logger LOG = LoggerFactory.getLogger(ZipFileHandler.class);

    private MessageSource messageSource;

    @Value("${sli.ingestion.file.timeout:600000}")
    private Long zipfileCompletionTimeout;

    @Value("${sli.ingestion.file.retryinterval:30000}")
    private Long zipfileCompletionPollInterval;

    File doHandling(File zipFile, ErrorReport errorReport) {
        return doHandling(zipFile, errorReport, null);
    }

    @Override
    protected File doHandling(File zipFile, ErrorReport errorReport, FileProcessStatus fileProcessStatus) {

        boolean done = false;
        long clockTimeout = System.currentTimeMillis() + zipfileCompletionTimeout;

        while (!done) {

            try {
                File dir = ZipFileUtil.extract(zipFile);
                LOG.info("Extracted zip file to {}", dir.getAbsolutePath());
                done = true;

                // Find manifest (ctl file)
                return ZipFileUtil.findCtlFile(dir);

            } catch (UnsupportedZipFeatureException ex) {
                // Unsupported compression method
                String message = MessageSourceHelper.getMessage(messageSource, "SL_ERR_MSG18", zipFile.getName());
                LOG.error(message, ex);
                errorReport.error(message, this);
                done = true;

            } catch (FileNotFoundException ex) {
                // DE1618 Gluster may have lost track of the file, or it has been deleted from under us so give up
                LOG.info(zipFile.getAbsolutePath() + " cannot be found. If the file was not processed by another ingestion service, please resubmit.", ex);
                String message = MessageSourceHelper.getMessage(messageSource, "SL_ERR_MSG4", zipFile.getName());
                errorReport.error(message, this);
                done = true;

            } catch (IOException ex) {

                if (System.currentTimeMillis() >= clockTimeout) {
                    String message = MessageSourceHelper.getMessage(messageSource, "SL_ERR_MSG4", zipFile.getName());
                    LOG.error(message, ex);
                    errorReport.error(message, this);
                    done = true;
                } else {
                    try {
                        LOG.info("Waiting for " + zipFile.getAbsolutePath() + "to move in handler.");
                        Thread.sleep(zipfileCompletionPollInterval);
                    } catch (InterruptedException e) {
                        // Restore the interrupted status
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        return null;
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    protected List<File> doHandling(List<File> items, ErrorReport errorReport, FileProcessStatus fileProcessStatus) {
        throw new NotImplementedException("Processing of multiple zip files is not currently supported.");
    }
}
