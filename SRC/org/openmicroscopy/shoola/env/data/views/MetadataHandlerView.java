/*
 * org.openmicroscopy.shoola.env.data.views.MetadataHandlerView 
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
package org.openmicroscopy.shoola.env.data.views;


//Java imports
import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Set;

//Third-party libraries

//Application-internal dependencies
import org.openmicroscopy.shoola.env.data.util.FilterContext;
import org.openmicroscopy.shoola.env.data.views.calls.TagsLoader;
import org.openmicroscopy.shoola.env.event.AgentEventListener;
import pojos.AnnotationData;
import pojos.DataObject;
import pojos.ImageData;

/** 
 * Provides methods to handle the annotations.
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
public interface MetadataHandlerView
	extends DataServicesView
{

	/** Indicates to retrieve the tags. */
	public static final int LEVEL_TAG = TagsLoader.LEVEL_TAG;
	
	/** Indicates to retrieve the tag sets. */
	public static final int LEVEL_TAG_SET = TagsLoader.LEVEL_TAG_SET;

	/** Indicates to retrieve the tag sets and the tags. */
	public static final int LEVEL_ALL = TagsLoader.LEVEL_ALL;
	
	/**
	 * Loads the tags related to the object identified the the passed type
	 * and ID. Retrieves the tags created by the specified user if the 
	 * userID is not <code>-1</code>.
	 * 
	 * @param nodeType	The class identifying the object.
	 * 					Mustn't be <code>null</code>.
	 * @param nodeID	The id of the node.
	 * @param userID	Pass <code>-1</code> if no user specified.
	 * @param observer  Callback handler.
     * @return A handle that can be used to cancel the call.
	 */
	public CallHandle loadTextualAnnotations(Class nodeType, long nodeID, 
									long userID, AgentEventListener observer);
	
	/**
	 * Loads the tags related to the object identified the the passed type
	 * and ID. Retrieves the tags created by the specified user if the 
	 * userID is not <code>-1</code>.
	 * 
	 * @param nodeType	The class identifying the object.
	 * 					Mustn't be <code>null</code>.
	 * @param nodeID	The id of the node.
	 * @param userID	Pass <code>-1</code> if no user specified.
	 * @param observer  Callback handler.
     * @return A handle that can be used to cancel the call.
	 */
	public CallHandle loadTags(Class nodeType, long nodeID, long userID,
			AgentEventListener observer);
	
	/**
	 * Loads all the files attached by a given user to the specified object.
	 * Retrieves the files if the userID is not <code>-1</code>.
	 * 
	 * @param nodeType	The class identifying the object.
	 * 					Mustn't be <code>null</code>.
	 * @param nodeID	The id of the node.
	 * @param userID	Pass <code>-1</code> if no user specified.
	 * @param observer  Callback handler.
     * @return A handle that can be used to cancel the call.
	 */
	public CallHandle loadAttachments(Class nodeType, long nodeID, long userID,
			AgentEventListener observer);

	/**
	 * Loads all the containers containing the specified object.
	 * Retrieves the files if the userID is not <code>-1</code>.
	 * 
	 * @param imageID	The image's id. 
	 * @param pixelsID	The id of the pixels set.
	 * @param userID	Pass <code>-1</code> if no user specified.
	 * @param observer  Callback handler.
     * @return A handle that can be used to cancel the call.
	 */
	public CallHandle loadViewedBy(long imageID, long pixelsID,
									AgentEventListener observer);

	/**
	 * Loads all the containers containing the specified object.
	 * Retrieves the files if the userID is not <code>-1</code>.
	 * 
	 * @param nodeType	The class identifying the object.
	 * 					Mustn't be <code>null</code>.
	 * @param nodeID	The id of the node.
	 * @param userID	Pass <code>-1</code> if no user specified.
	 * @param observer  Callback handler.
     * @return A handle that can be used to cancel the call.
	 */
	public CallHandle loadContainers(Class nodeType, long nodeID, long userID,
							AgentEventListener observer);

	/**
	 * Loads all the Urls attached by a given user to the specified object.
	 * Retrieves the files if the userID is not <code>-1</code>.
	 * 
	 * @param nodeType	The class identifying the object.
	 * 					Mustn't be <code>null</code>.
	 * @param nodeID	The id of the node.
	 * @param userID	Pass <code>-1</code> if no user specified.
	 * @param observer  Callback handler.
     * @return A handle that can be used to cancel the call.
	 */
	public CallHandle loadUrls(Class nodeType, long nodeID, long userID, 
					AgentEventListener observer);
	
	/**
	 * Loads all the ratings attached by a given user to the specified object.
	 * Retrieves the files if the userID is not <code>-1</code>.
	 * 
	 * @param nodeType	The class identifying the object.
	 * 					Mustn't be <code>null</code>.
	 * @param nodeID	The id of the node.
	 * @param userID	Pass <code>-1</code> if no user specified.
	 * @param observer  Callback handler.
     * @return A handle that can be used to cancel the call.
	 */
	public CallHandle loadRatings(Class nodeType, long nodeID, long userID, 
			AgentEventListener observer);

	/**
	 * Loads all the ratings attached by a given user to the specified objects.
	 * Retrieves the files if the userID is not <code>-1</code>.
	 * 
	 * @param nodeType	The class identifying the object.
	 * 					Mustn't be <code>null</code>.
	 * @param nodeIDs	The collection of ids of the passed node type.
	 * @param userID	Pass <code>-1</code> if no user specified.
	 * @param observer  Callback handler.
     * @return A handle that can be used to cancel the call.
	 */
	public CallHandle loadRatings(Class nodeType, Set<Long> nodeIDs, 
								long userID, AgentEventListener observer);
	
	/**
	 * Loads the thumbnails associated to the passed image i.e. 
	 * one thumbnail per specified user.
	 * 
	 * @param image			The image to handle.
	 * @param userIDs		The collection of users.
	 * @param thumbWidth	The width of the thumbnail.
	 * @param thumbHeight	The height of the thumbnail.
	 * @param observer		Callback handler.
     * @return A handle that can be used to cancel the call.
     */
	public CallHandle loadThumbnails(ImageData image, Set<Long> userIDs, 
						int thumbWidth, int thumbHeight, 
						AgentEventListener observer);

	/**
	 * Loads all annotations related the specified object.
	 * Retrieves the files if the userID is not <code>-1</code>.
	 * 
	 * @param dataObject	The object to handle. Mustn't be <code>null</code>.
	 * @param userID		Pass <code>-1</code> if no user specified.
	 * @param observer  	Callback handler.
     * @return A handle that can be used to cancel the call.
	 */
	public CallHandle loadStructuredData(DataObject dataObject, long userID,
										AgentEventListener observer);
	
	/**
	 * Annotates the object identified by the passed type and id.
	 * 
	 * @param type			The type of object to annotate.
	 * @param id			The id of the object.
	 * @param annotation	The annotation to create.
	 * @param observer  	Callback handler.
     * @return A handle that can be used to cancel the call.
	 */
	public CallHandle annotate(Class type, long id, AnnotationData annotation,
							AgentEventListener observer);
	
	/**
	 * Loads the existing annotations defined by the annotation type
	 * linked to a given type of object.
	 * Loads all the annotations if the object's type is <code>null</code>.
	 * 
	 * @param annotation 	The annotation type. Mustn't be <code>null</code>.
	 * @param type			The type of object or <code>null</code>.
	 * @param userID		The id of the user the annotations are owned by,
	 * 						or <code>-1</code> if no user specified.
	 * @param observer  	Callback handler.
     * @return A handle that can be used to cancel the call.
	 */
	public CallHandle loadExistingAnnotations(Class annotation, 
									Class type, long userID, 
									AgentEventListener observer);

	/**
	 * Saves the object, adds (resp. removes) annotations to(resp. from)
	 * the object if any.
	 * 
	 * @param data		The data objects to handle.
	 * @param toAdd		Collection of annotations to add.
	 * @param toRemove	Collection of annotations to remove.
	 * @param userID	The id of the user.
	 * @param observer	Callback handler.
     * @return A handle that can be used to cancel the call.
	 */
	public CallHandle saveData(Collection<DataObject> data, 
					List<AnnotationData> toAdd, List<AnnotationData> toRemove, 
						long userID, AgentEventListener observer);
	
	/**
	 * Saves the objects contained in the passed <code>DataObject</code>s, 
	 * adds (resp. removes) annotations to(resp. from)
	 * the object if any.
	 * 
	 * @param data		The data objects to handle.
	 * @param toAdd		Collection of annotations to add.
	 * @param toRemove	Collection of annotations to remove.
	 * @param userID	The id of the user.
	 * @param observer	Callback handler.
     * @return A handle that can be used to cancel the call.
	 */
	public CallHandle saveBatchData(Collection<DataObject> data, 
					List<AnnotationData> toAdd, List<AnnotationData> toRemove, 
						long userID, AgentEventListener observer);
	
	/**
	 * Downloads a file previously uploaded to the server.
	 * 
	 * @param file		The file to copy the date into.
	 * @param fileID	The id of the original file.
	 * @param size		The size of the file.
	 * @param observer	Callback handler.
     * @return A handle that can be used to cancel the call.
	 */
	public CallHandle loadFile(File file, long fileID, long size, 
							AgentEventListener observer);
	
	/**
	 * Loads the original files related to a given pixels set.
	 * 
	 * @param pixelsID The id of the pixels set.
	 * @param observer	Callback handler.
     * @return A handle that can be used to cancel the call.
	 */
	public CallHandle loadOriginalFile(long pixelsID, 
							AgentEventListener observer);
	
	/**
	 * Filters by annotation.
	 * 
	 * @param nodeType			The type of node.
	 * @param nodeIds			The collection of nodes to filter.
	 * @param annotationType 	The type of annotation to filter by.
	 * @param terms				The terms to filter by.		
	 * @param userID			The ID of the user.
	 * @param observer			Callback handler.
     * @return A handle that can be used to cancel the call.
	 */
	public CallHandle filterByAnnotation(Class nodeType, Set<Long> nodeIds, 
			Class annotationType, List<String> terms, long userID,
			AgentEventListener observer);

	/**
	 * Filters the data.
	 * 
	 * @param nodeType	The type of node.
	 * @param nodeIds	The collection of nodes to filter.
	 * @param context	The filtering context.
	 * @param userID	The ID of the user.
	 * @param observer	Callback handler.
     * @return A handle that can be used to cancel the call.
	 */
	public CallHandle filterData(Class nodeType, Set<Long> nodeIds,
			FilterContext context, long userID, AgentEventListener observer);

	/** 
	 * Creates a new <code>Dataobject</code> and adds the children to the
	 * newly created node.
	 * 
	 * @param parent	The parent of the <code>DataObject</code> to create
	 * 					or <code>null</code> if no parent specified.
	 * @param data		The <code>DataObject</code> to create.
	 * @param children	The nodes to add to the newly created 
	 * 					<code>DataObject</code>.
	 * @param observer	Callback handler.
     * @return A handle that can be used to cancel the call.
	 */
	public CallHandle createDataObject(DataObject parent, DataObject data,
							Collection children, AgentEventListener observer);
	
	public CallHandle loadExistingTags(int level, long userID, 
			                  AgentEventListener observer);
}
