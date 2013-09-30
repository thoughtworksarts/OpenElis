/*
* The contents of this file are subject to the Mozilla Public License
* Version 1.1 (the "License"); you may not use this file except in
* compliance with the License. You may obtain a copy of the License at
* http://www.mozilla.org/MPL/ 
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations under
* the License.
* 
* The Original Code is OpenELIS code.
* 
* Copyright (C) The Minnesota Department of Health.  All Rights Reserved.
*/

package org.bahmni.feed.openelis.feed.client;

import org.apache.log4j.Logger;
import org.bahmni.feed.openelis.feed.event.PatientFeedWorker;
import org.bahmni.webclients.WebClient;
import org.ict4h.atomfeed.client.service.EventWorker;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;

@DisallowConcurrentExecution
public class OpenMRSPatientFeedReaderJob extends OpenMRSFeedReaderJob {
    private static Logger logger = Logger.getLogger(OpenMRSPatientFeedReaderJob.class);

    public OpenMRSPatientFeedReaderJob() {
        super(logger);
    }

    @Override
    protected void doExecute(JobExecutionContext jobExecutionContext) {
        processEvents(jobExecutionContext);
    }

    @Override
    protected String getFeedName() {
        return FeedNames.OPENMRS_PATIENT_FEED_NAME;
    }

    @Override
    protected EventWorker createWorker(WebClient authenticatedWebClient, String urlPrefix) {
        return new PatientFeedWorker(authenticatedWebClient, urlPrefix);
    }
}
