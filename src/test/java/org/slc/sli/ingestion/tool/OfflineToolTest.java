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

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;

import junitx.util.PrivateAccessor;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Unit test for OfflineTool main
 *
 * @author tke
 *
 */


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext.xml" })
public class OfflineToolTest {
    File file;
    FileReader reader = null;
    LineNumberReader lreader = null;

    final String tempFile = "tempConsole.txt";

    FileAppender<ILoggingEvent> appd;

    @Autowired
    OfflineTool offlineTool;

    @Autowired
    ValidationController controller;

    org.slf4j.Logger logger;
    ValidationController cntlr;

    public void reset() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        logger = Mockito.mock(org.slf4j.Logger.class);
        PrivateAccessor.setField(offlineTool, "logger", logger);

        cntlr = Mockito.mock(ValidationController.class);
        PrivateAccessor.setField(offlineTool, "controller", cntlr);
    }

    /**
     *
     * @param dir
     *            : the path of the test data
     * @param target
     *            : the target string to be verify in the log file
     * @throws Throwable
     */
    void toolTest(String dir, String target) throws Throwable {
        Resource fileResource = new ClassPathResource(dir);
        String[] args = new String[1];
        try {
            args[0] = fileResource.getFile().toString();
        } catch (IOException e) {
            fail("IO Exception");
        }
        try {
            PrivateAccessor.invoke(offlineTool, "start", new Class[]{args.getClass()}, new Object[]{args});
        } catch (IOException e) {
            fail("IO Exception from main");
        }
    }

    @Test
    public void testMain() throws Throwable {
        // Testing valid zip file
        reset();

        toolTest("zipFile/Session1.zip", "processing is complete.");

        Mockito.verify(logger, Mockito.never()).error(Mockito.anyString());
        Mockito.verify(cntlr, Mockito.times(1)).doValidation((File) Mockito.any());

    }

    @Test
    public void negativeTestMain1() throws Throwable {

        // Testing more than 1 input arguments
        reset();
        String[] args = new String[2];
        PrivateAccessor.invoke(offlineTool, "start", new Class[]{args.getClass()}, new Object[]{args});
        Mockito.verify(logger, Mockito.times(1)).error("validationTool:Illegal options");


        // Testing doesn't existing file
        reset();
        String[] args4 = new String[1];
        args4[0] = "/invalid/nonExist.ctl";
        PrivateAccessor.invoke(offlineTool, "start", new Class[]{args4.getClass()}, new Object[]{args4});
        Mockito.verify(logger, Mockito.times(1)).error(args4[0] + " does not exist");

        //Passing a directory
        reset();
        Resource fileResource = new ClassPathResource("invalid/");
        args4[0] = fileResource.getFile().toString();
        PrivateAccessor.invoke(offlineTool, "start", new Class[]{args4.getClass()}, new Object[]{args4});
        Mockito.verify(logger, Mockito.times(1)).error("Illegal option - directory path. Expecting a Zip or a Ctl file");
    }

}
