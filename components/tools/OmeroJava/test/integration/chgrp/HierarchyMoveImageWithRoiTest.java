/*
 *------------------------------------------------------------------------------
 *  Copyright (C) 2006-2012 University of Dundee & Open Microscopy Environment.
 *  All rights reserved.
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
package integration.chgrp;

import static omero.rtypes.rdouble;
import static omero.rtypes.rint;
import integration.AbstractServerTest;
import integration.DeleteServiceTest;

import java.util.ArrayList;
import java.util.List;

import omero.ServerError;
import omero.cmd.Chgrp;
import omero.model.ExperimenterGroup;
import omero.model.IObject;
import omero.model.Image;
import omero.model.Rect;
import omero.model.RectI;
import omero.model.Roi;
import omero.model.RoiI;
import omero.model.Shape;
import omero.sys.EventContext;
import omero.sys.ParametersI;

import org.testng.annotations.Test;

/**
 * 
 * 
 * @author Scott Littlewood, <a
 *         href="mailto:sylittlewood@dundee.ac.uk">sylittlewood@dundee.ac.uk</a>
 * @since Beta4.4
 */
public class HierarchyMoveImageWithRoiTest extends AbstractServerTest {

    @Test
    public void moveImageRWtoRWRW() throws Exception {
        EventContext privateGroupContext = createPrivateGroup();
        ExperimenterGroup rwGroup = createReadWriteGroupWithUser(privateGroupContext.userId);

        long rwGroupId = rwGroup.getId().getValue();

        // force an eventcontext?
        // I don't know why or how I'm meant to know to do this?
        // is this to force
        iAdmin.getEventContext();

        Image image = createSimpleImage();
        long originalImageId = image.getId().getValue();

        Roi serverROI = createSimpleRoiFor(image);
        long originalRoiId = serverROI.getId().getValue();

        List<Long> shapeIds = new ArrayList<Long>();

        for (int i = 0; i < serverROI.sizeOfShapes(); i++) {
            Shape shape = serverROI.getShape(i);
            shapeIds.add(shape.getId().getValue());
        }

        // Move the image.
        Chgrp command = new Chgrp(DeleteServiceTest.REF_IMAGE, originalImageId,
                null, rwGroupId);
        doChange(command);

        // check if the objects have been moved.
        Roi originalRoi = getRoiWithId(originalRoiId);
        assertNull(originalRoi);

        // check the shapes have been moved
        List<IObject> orginalShapes = getShapesWithIds(shapeIds);
        assertEquals(0, orginalShapes.size());

        // Move the user into the RW group!
        loginUser(rwGroup);

        // Check that the ROI has moved
        Roi movedRoi = getRoiWithId(originalRoiId);
        assertNotNull(movedRoi);

        List<IObject> movedShapes = getShapesWithIds(shapeIds);
        assertEquals(shapeIds.size(), movedShapes.size());
    }

    /**
     * Creates a new private group for the currently logged in user
     * 
     * @return
     * @throws Exception
     */
    private EventContext createPrivateGroup() throws Exception {
        String privateGroupPermissions = "rw----";
        return newUserAndGroup(privateGroupPermissions);
    }

    /**
     * Creates a new Read-Write group for the user
     * 
     * @param userId
     * @return
     * @throws Exception
     */
    private ExperimenterGroup createReadWriteGroupWithUser(long userId)
            throws Exception {
        ExperimenterGroup readWriteGroup = newGroupAddUser("rwrw--", userId);
        return readWriteGroup;
    }

    /**
     * Queries the server for the ROI with the id provided under the current
     * user/group security context
     * 
     * @param roiId
     * @return
     * @throws ServerError
     */
    private Roi getRoiWithId(long roiId) throws ServerError {
        ParametersI queryParameters = new ParametersI();
        queryParameters.addId(roiId);
        String queryForROI = "select d from Roi as d where d.id = :id";
        return (Roi) iQuery.findByQuery(queryForROI, queryParameters);
    }

    /**
     * Queries the server for all the shapes with matching ids under the current
     * user/group security context
     * 
     * @param shapeIds
     * @return
     * @throws ServerError
     */
    private List<IObject> getShapesWithIds(List<Long> shapeIds)
            throws ServerError {
        ParametersI queryParameters = new ParametersI();
        queryParameters.addIds(shapeIds);
        String queryForShapes = "select d from Shape as d where d.id in (:ids)";
        return iQuery.findAllByQuery(queryForShapes, queryParameters);
    }

    /**
     * Creates and returns a server created ROI on an image under the current
     * user/group security context
     * 
     * @param image
     * @return
     * @throws ServerError
     */
    private Roi createSimpleRoiFor(Image image) throws ServerError {
        Roi roi = new RoiI();
        roi.setImage(image);

        for (int i = 0; i < 3; i++) {
            Rect rect = new RectI();
            rect.setX(rdouble(10));
            rect.setY(rdouble(20));
            rect.setWidth(rdouble(40));
            rect.setHeight(rdouble(80));
            rect.setTheZ(rint(i));
            rect.setTheT(rint(0));
            roi.addShape(rect);
        }

        return (RoiI) iUpdate.saveAndReturnObject(roi);
    }

    /**
     * Creates and returns an image on the server under the current user/group
     * security context
     * 
     * @return
     * @throws ServerError
     */
    private Image createSimpleImage() throws ServerError {
        Image simpleImage = mmFactory.simpleImage(0);
        return (Image) iUpdate.saveAndReturnObject(simpleImage);
    }
}
