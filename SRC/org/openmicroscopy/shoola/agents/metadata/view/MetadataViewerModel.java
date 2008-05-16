/*
 * org.openmicroscopy.shoola.agents.metadata.view.MetadataViewerModel 
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
package org.openmicroscopy.shoola.agents.metadata.view;


//Java imports
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

//Third-party libraries

//Application-internal dependencies
import org.openmicroscopy.shoola.agents.metadata.DataBatchSaver;
import org.openmicroscopy.shoola.agents.metadata.DataSaver;
import org.openmicroscopy.shoola.agents.metadata.MetadataLoader;
import org.openmicroscopy.shoola.agents.metadata.ContainersLoader;
import org.openmicroscopy.shoola.agents.metadata.StructuredDataLoader;
import org.openmicroscopy.shoola.agents.metadata.browser.Browser;
import org.openmicroscopy.shoola.agents.metadata.browser.BrowserFactory;
import org.openmicroscopy.shoola.agents.metadata.browser.TreeBrowserDisplay;
import org.openmicroscopy.shoola.agents.metadata.browser.TreeBrowserSet;
import org.openmicroscopy.shoola.agents.metadata.editor.Editor;
import org.openmicroscopy.shoola.agents.metadata.editor.EditorFactory;
import org.openmicroscopy.shoola.env.data.util.StructuredDataResults;

import pojos.AnnotationData;
import pojos.DataObject;
import pojos.DatasetData;
import pojos.ExperimenterData;
import pojos.ImageData;
import pojos.ProjectData;

/** 
 * The Model component in the <code>MetadataViewer</code> MVC triad.
 * This class tracks the <code>MetadataViewer</code>'s state and knows how to
 * initiate data retrievals. It also knows how to store and manipulate
 * the results. This class provides a suitable data loader.
 * The {@link MetadataViewerComponent} intercepts the results of data loadings, 
 * feeds them back to this class and fires state transitions as appropriate.
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
class MetadataViewerModel 
{
	
	/** Holds one of the state flags defined by {@link MetadataViewer}. */
	private int										state;

	/** Reference to the component that embeds this model. */
	private MetadataViewer							component;

	/** The ref object for the viewer i.e. the root. */
	private Object									refObject;
	
	/** The collection of objects related to the object of reference. */
	private Collection<DataObject>					siblings;
	
	/** The object hosting the various annotations linked to an object. */
	private StructuredDataResults					data;
	
	/** Reference to the browser. */
	private Browser									browser;
	
	/** Reference to the editor. */
	private Editor									editor;
	
	/** The active data loaders. */
	private Map<TreeBrowserDisplay, MetadataLoader>	loaders;
	
	/** 
	 * Flag indicating if the {@link MetadataViewer} is in a single view
	 * context.
	 */
	private boolean									singleViewMode;
	
	/** The collection of visible images. */
	private Collection<DataObject>					visibleImages;
	
	/**
	 * Creates a new object and sets its state to {@link MetadataViewer#NEW}.
	 * 
	 * @param refObject	The reference object.
	 */
	MetadataViewerModel(Object refObject)
	{
		state = MetadataViewer.NEW;
		this.refObject = refObject;
		loaders = new HashMap<TreeBrowserDisplay, MetadataLoader>();
		data = null;
	}
	
	/**
	 * Called by the <code>MetadataViewer</code> after creation to allow this
	 * object to store a back reference to the embedding component.
	 * 
	 * @param component 		The embedding component.
	 * @param thumbnailRequired Pass <code>true</code> to indicate to load the
	 * 							thumbnail, <code>false</code> otherwise.
	 * @param layout			One of the layout constants defined by 
	 * 							{@link MetadataViewer}.
	 */
	void initialize(MetadataViewer component, boolean thumbnailRequired, int
					layout)
	{ 
		this.component = component;
		browser = BrowserFactory.createBrowser(component, refObject);
		editor = EditorFactory.createEditor(component, refObject, 
										thumbnailRequired, layout);
	}
	
	/**
	 * Returns <code>true</code> if the {@link MetadataViewer} is in a single
	 * view context, <code>false</code> otherwise.
	 * 
	 * @return See above.
	 */
	boolean isSingleViewMode() { return singleViewMode; }
	
	/**
	 * Sets to <code>true</code> if the {@link MetadataViewer} is in a single
	 * view context, <code>false</code> otherwise.
	 * 
	 * @param singleViewMode The value to set.
	 */
	void setSingleViewMode(boolean singleViewMode)
	{ 
		this.singleViewMode = singleViewMode;
	}
	
	/**
	 * Returns the collection of objects related to the object of reference.
	 * 
	 * @return See above.
	 */
	Collection<DataObject> getSiblings()
	{ 
		if (siblings == null) siblings = new ArrayList<DataObject>();
		return siblings; 
	}
	
	/**
	 * Returns the current state.
	 * 
	 * @return One of the flags defined by the {@link MetadataViewer} interface.  
	 */
	int getState() { return state; }
	
	/**
	 * Sets the object in the {@link MetadataViewer#DISCARDED} state.
	 * Any ongoing data loading will be cancelled.
	 */
	void discard()
	{
		state = MetadataViewer.DISCARDED;
		Iterator<TreeBrowserDisplay> i = loaders.keySet().iterator();
		MetadataLoader loader;
		while (i.hasNext()) {
			loader = loaders.get(i.next());
			if (loader != null) loader.cancel();
		}
		loaders.clear();
	}
	
	/**
	 * Sets the object of reference.
	 * 
	 * @param refObject	The value to set.
	 */
	void setRootObject(Object refObject)
	{ 
		this.refObject = refObject; 
		browser.setRootObject(refObject);
		editor.setRootObject(refObject);
		data = null;
	}
	
	/** 
	 * Returns the object of reference.
	 * 
	 * @return See above.
	 */
	Object getRefObject() { return refObject; }
	
	/**
	 * Returns the <code>Browser</code> displaying the metadata.
	 * 
	 * @return See above.
	 */
	Browser getBrowser() { return browser; }
	
	/**
	 * Returns the <code>Editor</code> displaying the metadata.
	 * 
	 * @return See above.
	 */
	Editor getEditor() { return editor; }

	/** 
	 * Cancels any ongoing data loading. 
	 * 
	 * @param refNode The node of reference.
	 */
	void cancel(TreeBrowserDisplay refNode)
	{
		MetadataLoader loader = loaders.get(refNode);
		if (loader != null) {
			loader.cancel();
			loaders.remove(loader);
		}
	}

	/**
	 * Invokes when the data are loaded, the loader is then 
	 * removed from the map.
	 * 
	 * @param refNode
	 */
	void notifyLoadingEnd(TreeBrowserDisplay refNode)
	{
		MetadataLoader loader = loaders.get(refNode);
		if (loader != null) loaders.remove(loader);
	}
	
	/**
	 * Starts the asynchronous retrieval of the attachments reladed 
	 * to the parent node.
	 * 
	 * @param refNode 	The menu node of reference.
	 * @param rootType	The type of reference.
	 */
	void fireParentLoading(TreeBrowserSet refNode)
	{
		cancel(refNode);
		//Object ho = getParentObject(refNode);
		Object ho = refNode.getUserObject();
		if (ho instanceof DataObject) {
			ContainersLoader loader = new ContainersLoader(
					component, refNode, ho.getClass(), 
					((DataObject) ho).getId());
			loaders.put(refNode, loader);
			loader.load();
		}
	}


	/**
	 * Starts the asynchronous retrieval of the structured data related
	 * to the passed node.
	 * 
	 * @param refNode The node to handle.
	 */
	void fireStructuredDataLoading(TreeBrowserDisplay refNode)
	{
		Object uo = refNode.getUserObject();
		if (!(uo instanceof DataObject)) return;
		
		if (uo instanceof ExperimenterData) {
			
			
			return;
		}
		cancel(refNode);
		StructuredDataLoader loader = new StructuredDataLoader(component, 
								refNode, (DataObject) uo);
		loaders.put(refNode, loader);
		loader.load();
	}
	
	/**
	 * Returns <code>true</code> if the passed object is the reference object,
	 * <code>false</code> otherwise.
	 * 
	 * @param uo The object to compare.
	 * @return See above.
	 */
	boolean isSameObject(DataObject uo)
	{
		if (uo == null || !(refObject instanceof DataObject)) return false;
		if (!uo.getClass().equals(refObject.getClass()))
			return false;
		DataObject object = (DataObject) refObject;
		if (uo.getId() != object.getId()) return false;
		if (data == null) return false;
		Object o = data.getRelatedObject();
		if (!(o instanceof DataObject)) return false;
		object = (DataObject) o;
		if (!uo.getClass().equals(object.getClass()))
			return false;
		if (uo.getId() != object.getId()) return false;
		return true;
	}
	
	/**
	 * Returns the name of the object if any.
	 * 
	 * @return See above.
	 */
	String getRefObjectName() 
	{
		if (refObject instanceof ImageData)
			return ((ImageData) refObject).getName();
		else if (refObject instanceof DatasetData)
			return ((DatasetData) refObject).getName();
		else if (refObject instanceof ProjectData)
		return ((ProjectData) refObject).getName();
		return "";
	}

	/**
	 * Fires an asynchronous call to save the data, add (resp. remove)
	 * annotations to (resp. from) the object.
	 * 
	 * @param toAdd		Collection of annotations to add.
	 * @param toRemove	Collection of annotations to remove.
	 * @param data		The object to update.
	 */
	void fireSaving(List<AnnotationData> toAdd, List<AnnotationData> toRemove, 
					Collection<DataObject> data)
	{
		DataSaver loader = new DataSaver(component, data, toAdd, toRemove);
		loader.load();
	}
	
	/**
	 * Sets the structured data.
	 * 
	 * @param data The value to set.
	 */
	void setStructuredDataResults(StructuredDataResults data)
	{
		this.data = data;
	}
	
	/**
	 * Returns <code>true</code> if the imported set of pixels has been 
	 * archived, <code>false</code> otherwise.
	 * 
	 * @return See above.
	 */
	boolean isArchived()
	{ 
		if (data == null) return false;
		return data.isArchived(); 
	}

	/**
	 * Sets the collection of objects related to the reference objects.
	 * 
	 * @param siblings The collection to set.
	 */
	void setSiblings(Collection<DataObject> siblings)
	{
		this.siblings = siblings;
	}

	/**
	 * Sets the collection of visible nodes.
	 * 
	 * @param visibleImages The collection to set.
	 */
	void setVisibleImages(Collection visibleImages)
	{
		this.visibleImages = visibleImages;
	}
	
	/**
	 * Returns the collection of objects related to the object of reference.
	 * 
	 * @return See above.
	 */
	Collection getVisibleImages()
	{ 
		if (visibleImages == null) visibleImages = new ArrayList();
		return visibleImages; 
	}
	
	/**
	 * Fires an asynchronous call to save the objects contained
	 * in the passed <code>DataObject</code> to save, add (resp. remove)
	 * annotations to (resp. from) the object.
	 * 
	 * @param toAdd		Collection of annotations to add.
	 * @param toRemove	Collection of annotations to remove.
	 * @param toSave		The object to update.
	 */
	void fireBatchSaving(List<AnnotationData> toAdd, List<AnnotationData> 
						toRemove, List<DataObject> toSave)
	{
		DataBatchSaver loader = new DataBatchSaver(component, toSave, toAdd, 
				toRemove);
		loader.load();
	}
	
}
