/*
 * org.openmicroscopy.shoola.agents.metadata.editor.EditorUI 
 *
 *------------------------------------------------------------------------------
 *  Copyright (C) 2006-2008 University of Dundee. All rights reserved.
 *
 *
 * 	This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *------------------------------------------------------------------------------
 */
package org.openmicroscopy.shoola.agents.metadata.editor;



//Java imports
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Collection;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

//Third-party libraries
import org.jdesktop.swingx.JXTaskPane;

//Application-internal dependencies
import org.openmicroscopy.shoola.agents.metadata.IconManager;
import pojos.DataObject;
import pojos.ExperimenterData;

/** 
 * Component hosting the various {@link AnnotationUI} entities.
 *
 * @author  Jean-Marie Burel &nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="mailto:j.burel@dundee.ac.uk">j.burel@dundee.ac.uk</a>
 * @author Donald MacDonald &nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="mailto:donald@lifesci.dundee.ac.uk">donald@lifesci.dundee.ac.uk</a>
 * @version 3.0
 * <small>
 * (<b>Internal version:</b> $Revision: $Date: $)
 * </small>
 * @since OME3.0
 */
public class EditorUI 
	extends JPanel
{
	
	/** Reference to the controller. */
	private EditorControl				controller;
	
	/** Reference to the Model. */
	private EditorModel					model;
	
	/** The UI component displaying the general metadata. */
	private GeneralPaneUI				generalPane;
	
	/** The UI component displaying the acquisition metadata. */
	private AcquisitionDataUI			acquisitionPane;
	
	/** The UI component displaying the user's information. */
	private UserUI						userUI;

	/** The tool bar with various controls. */
	private ToolBar						toolBar;

    /** 
     * Flag indicating that the data has already been saved and no new changes.
     */
    private boolean						saved;
	
    /** The tabbed pane hosting the metadata. */
    private JTabbedPane					tabbedPane;
    
	/** Initializes the UI components. */
	private void initComponents()
	{
		userUI = new UserUI(model, controller);
		toolBar = new ToolBar(model, controller);
		generalPane = new GeneralPaneUI(this, model, controller);
		acquisitionPane = new AcquisitionDataUI(this, model, controller);
		tabbedPane = new JTabbedPane();
		IconManager icons = IconManager.getInstance();
		tabbedPane.addTab("General", icons.getIcon(IconManager.ANNOTATION), 
				generalPane, "General Information");
		tabbedPane.addTab("Acquisition", icons.getIcon(IconManager.ANNOTATION), 
				acquisitionPane, "Acquisition Metadata");
	}
	
	/** Builds and lays out the components. */
	private void buildGUI()
	{
		setLayout(new BorderLayout(0, 0));
		//mainPane.getViewport().add(generalPaneUI);
		add(toolBar, BorderLayout.NORTH);
		add(tabbedPane, BorderLayout.CENTER);
	}
	
	/** Creates a new instance. */
	EditorUI() {}
    
    /**
     * Links this View to its Controller and its Model.
     * 
     * @param model         Reference to the Model. 
     * 						Mustn't be <code>null</code>.
     * @param controller	Reference to the Controller.
     * 						Mustn't be <code>null</code>.
     */
    void initialize(EditorModel model, EditorControl controller)
    {
    	if (controller == null)
    		throw new IllegalArgumentException("Controller cannot be null");
    	if (model == null)
    		throw new IllegalArgumentException("Model cannot be null");
        this.controller = controller;
        this.model = model;
        initComponents();
        buildGUI();
    }
    
    /** Lays out the UI when data are loaded. */
    void layoutUI()
    {
    	if (model.getRefObject() instanceof ExperimenterData)  {
    		toolBar.buildUI();
    		userUI.buildUI();
    		userUI.repaint();
    	} else {
    		setDataToSave(false);
        	toolBar.buildUI();
        	toolBar.setControls();
        	generalPane.layoutUI();
        	
    	}
    	revalidate();
    	repaint();
    }
    
    /**
     * Shows or hides the editor.
     * 
     * @param show Pass <code>true</code> to show it, <code>false</code> to hide
     * 			   it.
     */
    void showEditor(boolean show)
    {
    	/*
    	Component comp = getComponent(0);
    	if (show) {
    		if (comp instanceof JScrollPane) return;
    		removeAll();
    		add(mainPane, BorderLayout.CENTER);
    	} else {
    		if (comp instanceof JPanel) return;
    		removeAll();
    		add(emptyPane, BorderLayout.CENTER);
    	}
    	*/
    	repaint();
    }
    
    /** Updates display when the new root node is set. */
	void setRootObject()
	{
		generalPane.setRootObject();
	}
	
	/**
	 * Sets either to single selection or to multi selection.
	 * 
	 * @param single	Pass <code>true</code> when single selection, 
	 * 					<code>false</code> otherwise.
	 */
    void setSelectionMode(boolean single)
    {
    	Component comp = getComponent(0);
    	/*
    	Object refObject = model.getRefObject();
    	if (refObject instanceof DataObject) {
    		if (comp instanceof JPanel) {
        		removeAll();
        		add(emptyPane, BorderLayout.CENTER);
        	}
    	} else {
    		if (comp instanceof JScrollPane) {
        		removeAll();
        		add(mainPane, BorderLayout.CENTER);
        	}
    	}
    	*/
    	//layoutUI();
    	//modify layout
    	repaint();
    }
    
    /** Save data. */
	void saveData()
	{
		saved = true;
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		toolBar.setDataToSave(false);
		if (model.getRefObject() instanceof ExperimenterData) {
			ExperimenterData exp = userUI.getExperimenterToSave();
			model.fireDataObjectSaving(exp);
			return;
		}
		//if (!model.isMultiSelection()) propertiesUI.updateDataObject();
		generalPane.saveData();
	}

	/** Lays out the thumbnails. */
	void setThumbnails() { generalPane.setThumbnails(); }

	/** Sets the existing tags. */
	void setExistingTags()
	{
		generalPane.setExistingTags();
		revalidate();
    	repaint();
	}
	
	/**
	 * Displays the passed image.
	 * 
	 * @param thumbnail
	 */
	void setThumbnail(BufferedImage thumbnail)
	{
		/*
		ThumbnailCanvas canvas = new ThumbnailCanvas(model, thumbnail, null);
		if (topLeftPane != null) leftPane.remove(topLeftPane);
		topLeftPane = canvas;
		TableLayout layout = (TableLayout) leftPane.getLayout();
		layout.setRow(0, TableLayout.PREFERRED);
		leftPane.add(topLeftPane, "0, 0");
		leftPane.revalidate();
		revalidate();
    	repaint();
    	*/
	}

	/** Shows the image's info. */
    void showChannelData()
    { 
    	generalPane.showChannelData();
    }

    /**
     * Enables the saving controls depending on the passed value.
     * 
     * @param b Pass <code>true</code> to save the data,
     * 			<code>false</code> otherwise.
     */
    void setDataToSave(boolean b) { toolBar.setDataToSave(b); }
    
    /**
	 * Returns <code>true</code> if data to save, <code>false</code>
	 * otherwise.
	 * 
	 * @return See above.
	 */
	boolean hasDataToSave()
	{
		if (saved) return false;
		Object ref = model.getRefObject();
		if (!(ref instanceof DataObject)) return false;
		if (ref instanceof ExperimenterData)
			return userUI.hasDataToSave();
		if (model.isMultiSelection()) {
			//if (!propertiesUI.isNameValid()) {
			//	setDataToSave(false);
			//	return false;
			//}
		}
		
		return generalPane.hasDataToSave();

	}
    
	/** Clears data to save. */
	void clearData()
	{
		saved = false;
		generalPane.clearData();
		setCursor(Cursor.getDefaultCursor());
	}

	/**
	 * Adds the specified component.
	 * 
	 * @param c The component to add.
	 */
	void addTopLeftComponent(JComponent c)
	{
		/*
		added = true;
		if (topLeftPane != null) leftPane.remove(topLeftPane);
		topLeftPane = c;
		TableLayout layout = (TableLayout) leftPane.getLayout();
		layout.setRow(0, TableLayout.PREFERRED);
		leftPane.add(topLeftPane, "0, 0");
		leftPane.revalidate();
		revalidate();
    	repaint();
    	*/
	}
	
	/** Clears the password fields. */
	void passwordChanged() { userUI.passwordChanged(); }

	/** Displays the wizard with the collection of files already uploaded. */
	void setExistingAttachements() { }//attachmentsUI.showSelectionWizard(); }
	
	/** Displays the wizard with the collection of URLs already uploaded. */
	void setExistingURLs() { }//linksUI.showSelectionWizard(); }
	 
	/**
	 * Sets the disk space information.
	 * 
	 * @param space The value to set.
	 */
	void setDiskSpace(List space) { userUI.setDiskSpace(space); }

	/**
	 * Handles the expansion or collapsing of the passed component.
	 * 
	 * @param source The component to handle.
	 */
	void handleTaskPaneCollapsed(JXTaskPane source)
	{
		generalPane.handleTaskPaneCollapsed(source);
	}

	/**
	 * Shows or hides the component indicating the progresss.
	 * 
	 * @param busy Pass <code>true</code> to show, <code>false</code> to hide.
	 */
	void setStatus(boolean busy) { toolBar.setStatus(busy); }

	/**
	 * Returns the collection of existing tags.
	 * 
	 * @return See above.
	 */
	Collection getExistingTags() { return model.getExistingTags(); }

	void onTagsLoading(boolean wizard)
	{
		// TODO Auto-generated method stub
		
	}

	void attachFile(File file)
	{
		if (file == null) return;
		generalPane.attachFile(file);
	}

	void removeAttachedFile(File file)
	{
		if (file == null) return;
		generalPane.removeAttachedFile(file);
	}
	
}
