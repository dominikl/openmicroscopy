package org.openmicroscopy.shoola.agents.fsimporter.view;

import java.awt.Dimension;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;

import omero.gateway.model.DataObject;
import omero.gateway.model.FileAnnotationData;

import org.jdesktop.swingx.JXBusyLabel;
import org.openmicroscopy.shoola.agents.fsimporter.util.FileImportComponent;
import org.openmicroscopy.shoola.env.data.model.ImportableFile;
import org.openmicroscopy.shoola.env.data.model.ImportableObject;
import org.openmicroscopy.shoola.util.ui.ClosableTabbedPaneComponent;
import org.openmicroscopy.shoola.util.ui.RotationIcon;

public abstract class ImporterUIElementBase extends ClosableTabbedPaneComponent{

    /** The identifier of the component. */
    int id;
    
    /** The object hosting information about files to import. */
    ImportableObject object;

    /** Reference to the view. */
    ImporterUI view;
    
    /** Reference to the controller. */
    ImporterControl controller;
    
    /** Reference to the controller. */
    ImporterModel model;
    
    /** The busy label. */
    private JXBusyLabel busyLabel;
    
    boolean uploadStarted = false;
    
    public ImporterUIElementBase(ImporterControl controller, ImporterModel model,
            ImporterUI view, int id, int index, String name,
            ImportableObject object) {
        super(index, name, "Closing will cancel imports that have not yet started.");
        if (object == null) 
            throw new IllegalArgumentException("No object specified.");
        if (controller == null)
            throw new IllegalArgumentException("No Control.");
        if (model == null)
            throw new IllegalArgumentException("No Model.");
        if (view == null)
            throw new IllegalArgumentException("No View.");
        this.controller = controller;
        this.model = model;
        this.view = view;
        this.id = id;
        this.object = object;
        
        init();
    }

    private void init() {
        busyLabel = new JXBusyLabel(new Dimension(16, 16));
    }
    
    /**
     * Returns the icon indicating the status of the import.
     * 
     * @return See above.
     */
    Icon getImportIcon()
    { 
        return busyLabel.getIcon();
    }
    
    /** 
     * Indicates that the import has started. 
     * 
     * @param component The component of reference of the rotation icon.
     */
    Icon startImport(JComponent component)
    {
        uploadStarted = true;
        setClosable(false);
        busyLabel.setBusy(true);
        repaint();
        return new RotationIcon(busyLabel.getIcon(), component, true);
    }
    
    /** Invokes when the import is finished. */
    void onImportEnded()
    { 
        busyLabel.setBusy(false);
        setClosable(true);
    }
    
    /**
     * Returns the identifier of the component.
     * 
     * @return See above.
     */
    int getID() { return id; }
    
    /**
     * Returns the object to import.
     * 
     * @return See above.
     */
    ImportableObject getData() { return object; }
    
    /**
     * Returns <code>true</code> if the import has started, <code>false</code>
     * otherwise.
     * 
     * @return See above.
     */
    boolean hasStarted() { return uploadStarted; }
    
    abstract boolean isDone();
    
    abstract boolean hasImportToCancel();
    
   abstract boolean hasToRefreshTree();
   
   abstract List<DataObject> getExistingContainers();
   
   abstract boolean isUploadComplete();
   
   abstract void setUploadStarted(boolean b);
   
   abstract Object uploadComplete(ImportableFile f, Object result);
   
   abstract void cancelLoading();
   
   abstract void resetContainers(Collection result);
   
   abstract void setImportLogFile(Collection<FileAnnotationData> data, long id);
   
   abstract void setImportResult(FileImportComponent fc, Object result);
   
   abstract Object uploadComplete(FileImportComponent c, Object result);
}
