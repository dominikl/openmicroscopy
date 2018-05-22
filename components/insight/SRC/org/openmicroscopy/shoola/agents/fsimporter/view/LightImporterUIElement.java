/*
 *------------------------------------------------------------------------------
 *  Copyright (C) 2006-2016 University of Dundee. All rights reserved.
 *
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *------------------------------------------------------------------------------
 */
package org.openmicroscopy.shoola.agents.fsimporter.view;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstraints;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.LineBorder;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.jdesktop.swingx.JXBusyLabel;
import org.openmicroscopy.shoola.agents.events.importer.BrowseContainer;
import org.openmicroscopy.shoola.agents.fsimporter.IconManager;
import org.openmicroscopy.shoola.agents.fsimporter.ImporterAgent;
import org.openmicroscopy.shoola.agents.fsimporter.util.CheckSumDialog;
import org.openmicroscopy.shoola.agents.fsimporter.util.FileImportComponent;
import org.openmicroscopy.shoola.agents.fsimporter.util.ImportStatus;
import org.openmicroscopy.shoola.agents.util.browser.TreeImageDisplay;
import org.openmicroscopy.shoola.env.Environment;
import org.openmicroscopy.shoola.env.LookupNames;
import org.openmicroscopy.shoola.env.data.model.DownloadAndLaunchActivityParam;
import org.openmicroscopy.shoola.env.data.model.FileObject;
import org.openmicroscopy.shoola.env.data.model.ImportableFile;
import org.openmicroscopy.shoola.env.data.model.ImportableObject;
import org.openmicroscopy.shoola.env.data.util.StatusLabel;
import org.openmicroscopy.shoola.env.event.EventBus;
import org.openmicroscopy.shoola.env.ui.UserNotifier;
import org.openmicroscopy.shoola.util.CommonsLangUtils;
import org.openmicroscopy.shoola.util.file.ImportErrorObject;
import org.openmicroscopy.shoola.util.ui.ClosableTabbedPaneComponent;
import org.openmicroscopy.shoola.util.ui.RotationIcon;
import org.openmicroscopy.shoola.util.ui.UIUtilities;

import omero.gateway.model.DataObject;
import omero.gateway.model.DatasetData;
import omero.gateway.model.FileAnnotationData;
import omero.gateway.model.FilesetData;
import omero.gateway.model.ProjectData;
import omero.gateway.model.ScreenData;

/** 
 * Component displaying an import.
 *
 * @author Jean-Marie Burel &nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="mailto:j.burel@dundee.ac.uk">j.burel@dundee.ac.uk</a>
 * @author Donald MacDonald &nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="mailto:donald@lifesci.dundee.ac.uk">donald@lifesci.dundee.ac.uk</a>
 * @version 3.0
 * @since 3.0-Beta4
 */
class LightImporterUIElement 
	extends ImporterUIElementBase
{

    LightImporterUIElement(ImporterControl controller, ImporterModel model,
            ImporterUI view, int id, int index, String name,
            ImportableObject object)
    {
        super(controller, model,
                view, id, index, name,
                object);
    }

    @Override
    boolean isDone() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    boolean hasImportToCancel() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    boolean hasToRefreshTree() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    List<DataObject> getExistingContainers() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    boolean isUploadComplete() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    void setUploadStarted(boolean b) {
        // TODO Auto-generated method stub
        
    }

    @Override
    Object uploadComplete(ImportableFile f, Object result) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    void cancelLoading() {
        // TODO Auto-generated method stub
        
    }

    @Override
    void resetContainers(Collection result) {
        // TODO Auto-generated method stub
        
    }

    @Override
    void setImportLogFile(Collection<FileAnnotationData> data, long id) {
        // TODO Auto-generated method stub
        
    }

    @Override
    void setImportResult(FileImportComponent fc, Object result) {
        // TODO Auto-generated method stub
        
    }

    @Override
    Object uploadComplete(FileImportComponent c, Object result) {
        // TODO Auto-generated method stub
        return null;
    }
    
    
}
