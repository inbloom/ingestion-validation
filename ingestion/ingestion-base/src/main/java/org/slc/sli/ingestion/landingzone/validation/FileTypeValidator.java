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


package org.slc.sli.ingestion.landingzone.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.ingestion.FileFormat;
import org.slc.sli.ingestion.FileType;
import org.slc.sli.ingestion.landingzone.FileEntryDescriptor;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.validation.ErrorReport;

/**
 * File Type validator.
 *
 */
public class FileTypeValidator extends IngestionFileValidator {

    private static final Logger LOG = LoggerFactory.getLogger(XmlFileValidator.class);

    @Override
    public boolean isValid(FileEntryDescriptor item, ErrorReport callback) {
        IngestionFileEntry entry = item.getFileItem();
        FileType fileType = entry.getFileType();

        if (fileType == null) {
            fail(callback, getFailureMessage("SL_ERR_MSG1", entry.getFileName(), "type"));

            return false;
        }

        if (isNotXMLFile(entry, callback)) {
            return false;
        }

        return true;
    }

    /**
     * This will assume it is an XML file to begin with.  It checks by simply looking
     * at the file extension.  If the data inside the file is incorrect, it is caught
     * further downstream as malformed XML.  There is already an acceptance test
     * covering this.
     * @param fileEntry
     * @param errorReport
     * @return
     */
    private boolean isNotXMLFile(IngestionFileEntry fileEntry, ErrorReport errorReport) {
        boolean isNotXML = false;

        String fileExtension = fileEntry.getFileName().substring(fileEntry.getFileName().lastIndexOf(".") + 1);
        if (!fileExtension.equalsIgnoreCase(FileFormat.EDFI_XML.getExtension())) {
            isNotXML = true;
            LOG.warn("File not XML: " + fileEntry.getFileName());
        }

        return isNotXML;
    }

}
