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

import org.bahmni.feed.openelis.AtomFeedProperties;
import org.bahmni.feed.openelis.utils.OpenElisConnectionProvider;
import org.bahmni.webclients.AnonymousAuthenticator;
import org.bahmni.webclients.ClientCookies;
import org.bahmni.webclients.ConnectionDetails;
import org.bahmni.webclients.HttpClient;
import org.bahmni.webclients.openmrs.OpenMRSLoginAuthenticator;
import org.ict4h.atomfeed.client.factory.AtomFeedClientBuilder;
import org.ict4h.atomfeed.client.service.AtomFeedClient;
import org.ict4h.atomfeed.client.service.EventWorker;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;

public class AtomFeedClientFactory {

    public AtomFeedClient getFeedClient(AtomFeedProperties atomFeedProperties, String feedName,
                                        EventWorker eventWorker, ClientCookies cookies) {
        String uri = atomFeedProperties.getProperty(feedName);
        try {
            return new AtomFeedClientBuilder().
                    forFeedAt(new URI(uri)).
                    processedBy(eventWorker).
                    usingConnectionProvider(new OpenElisConnectionProvider()).
                    with(createAtomFeedClientProperties(atomFeedProperties), cookies).
                    build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(String.format("Is not a valid URI - %s", uri));
        }
    }

    private org.ict4h.atomfeed.client.factory.AtomFeedProperties createAtomFeedClientProperties(AtomFeedProperties atomFeedProperties) {
        org.ict4h.atomfeed.client.factory.AtomFeedProperties feedProperties = new org.ict4h.atomfeed.client.factory.AtomFeedProperties();
        feedProperties.setConnectTimeout(Integer.parseInt(atomFeedProperties.getFeedConnectionTimeout()));
        feedProperties.setReadTimeout(Integer.parseInt(atomFeedProperties.getFeedReplyTimeout()));
        feedProperties.setMaxFailedEvents(Integer.parseInt(atomFeedProperties.getMaxFailedEvents()));
        feedProperties.setControlsEventProcessing(true);
        return feedProperties;
    }
    
}
