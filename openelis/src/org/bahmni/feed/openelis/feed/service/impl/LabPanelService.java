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

package org.bahmni.feed.openelis.feed.service.impl;

import org.bahmni.feed.openelis.AtomFeedProperties;
import org.bahmni.feed.openelis.externalreference.dao.ExternalReferenceDao;
import org.bahmni.feed.openelis.externalreference.daoimpl.ExternalReferenceDaoImpl;
import org.bahmni.feed.openelis.externalreference.valueholder.ExternalReference;
import org.bahmni.feed.openelis.feed.contract.openerp.OpenERPLab;
import org.bahmni.feed.openelis.feed.service.LabService;
import us.mn.state.health.lims.common.action.IActionConstants;
import us.mn.state.health.lims.panel.dao.PanelDAO;
import us.mn.state.health.lims.panel.daoimpl.PanelDAOImpl;
import us.mn.state.health.lims.panel.valueholder.Panel;

import java.io.IOException;


public class LabPanelService extends LabService {

    private PanelDAO panelDAO = new PanelDAOImpl();
    private ExternalReferenceDao externalReferenceDao = new ExternalReferenceDaoImpl();
    private String labProductType;

    public LabPanelService() {
        labProductType = AtomFeedProperties.getInstance().getProductTypePanel();
    }

    LabPanelService(PanelDAO panelDAO, ExternalReferenceDao externalReferenceDao) {
        this();
        this.panelDAO = panelDAO;
        this.externalReferenceDao = externalReferenceDao;
    }

    @Override
    protected void save(OpenERPLab openERPLab) throws IOException {
        Panel panel = mapToPanel(openERPLab);
        String externalId = openERPLab.getExternalId();
        ExternalReference data = externalReferenceDao.getData(externalId, labProductType);
        if (data == null) {
            panelDAO.insertData(panel);
            if(hasId(panel)){
                ExternalReference externalReference = new ExternalReference(Long.parseLong(panel.getId()), externalId, labProductType);
                externalReferenceDao.insertData(externalReference);
            }
        } else {
            Panel panelById = panelDAO.getPanelById(String.valueOf(data.getItemId()));
            if (panelById != null) {
                updatePanelFieldsIfNotEmpty(panel, panelById);
                panelDAO.updateData(panelById);
            }
        }
    }

    @Override
    protected void delete(OpenERPLab openERPLab) {
        ExternalReference externalReference = getExternalReference(openERPLab);
        if(externalReference != null){
            externalReferenceDao.deleteData(externalReference);
            String panelId = String.valueOf(externalReference.getItemId());
            panelDAO.deleteById(panelId, openERPLab.getSysUserId());
        }
    }

    private ExternalReference getExternalReference(OpenERPLab openERPLab) {
        return externalReferenceDao.getData(openERPLab.getExternalId(), openERPLab.getCategory());
    }

    private boolean hasId(Panel panel) {
        return panel.getId() != null && !panel.getId().isEmpty();
    }

    private void updatePanelFieldsIfNotEmpty(Panel panel, Panel panelById) {
        if (isSet(panel.getPanelName())) {
            panelById.setPanelName(panel.getPanelName());
        }
        if (isSet(panel.getDescription())) {
            panelById.setDescription(panel.getDescription());
        }
        if (isSet(panel.getSysUserId())) {
            panelById.setSysUserId(panel.getSysUserId());
        }
        if(isSet(panel.getIsActive())){
            panelById.setIsActive(panel.getIsActive());
        }
    }

    private boolean isSet(String value) {
        return value != null && !value.isEmpty();
    }


    private Panel mapToPanel(OpenERPLab openERPLab) throws IOException {
        Panel panel = new us.mn.state.health.lims.panel.valueholder.Panel();
        panel.setPanelName(openERPLab.getName());
        String description = openERPLab.getDescription();
        if(description == null || description.isEmpty()){
            description = openERPLab.getName();
        }
        panel.setDescription(description);
        panel.setSysUserId(openERPLab.getSysUserId());
        setActiveStatus(panel, openERPLab.getStatus());
        return panel;
    }

    private void setActiveStatus(Panel panel, String status) {
        if(status == null || status.isEmpty() )
            return;
        panel.setIsActive(status.equalsIgnoreCase("active") ? IActionConstants.YES : IActionConstants.NO);
    }

}
