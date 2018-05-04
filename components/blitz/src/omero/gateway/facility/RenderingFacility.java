package omero.gateway.facility;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import omero.api.RenderingEnginePrx;
import omero.gateway.Gateway;
import omero.gateway.SecurityContext;
import omero.gateway.exception.DSAccessException;
import omero.gateway.exception.DSOutOfServiceException;
import omero.gateway.model.ImageData;
import omero.gateway.model.PixelsData;
import omero.gateway.model.RenderingDefData;
import omero.model.RenderingDef;

public class RenderingFacility extends Facility {
    private BrowseFacility browse;

    public RenderingFacility(Gateway gateway) throws ExecutionException {
        super(gateway);
        this.browse = gateway.getFacility(BrowseFacility.class);
    }

    public void updateSettings(SecurityContext ctx, ImageData img,
            RenderingDefData rdef) throws DSOutOfServiceException,
            DSAccessException {
        try {
            if (!img.isLoaded())
                img = browse.getImage(ctx, img.getId());
            long pixelsId = img.getDefaultPixels().getId();
            RenderingEnginePrx re = gateway.getRenderingService(ctx, pixelsId);
            re.updateSettings((RenderingDef) rdef.asIObject());
        } catch (Throwable t) {
            handleException(this, t, "Could not update rendering settings.");
        }
    }

    public RenderingDefData getSettings(SecurityContext ctx, ImageData img)
            throws DSOutOfServiceException, DSAccessException {
        Map<Long, RenderingDefData> settings = getAllSettings(ctx, img);
        return settings.get(gateway.getLoggedInUser().getId());
    }

    public Map<Long, RenderingDefData> getAllSettings(SecurityContext ctx,
            ImageData img) throws DSOutOfServiceException, DSAccessException {
        try {
            if (!img.isLoaded())
                img = browse.getImage(ctx, img.getId());
            PixelsData pix = img.getDefaultPixels();
            List<RenderingDef> tmp = pix.asPixels().copySettings();
            Map<Long, RenderingDefData> rdefs = new HashMap<Long, RenderingDefData>();
            for (RenderingDef d : tmp)
                rdefs.put(d.getDetails().getOwner().getId().getValue(),
                        new RenderingDefData(d));
            return rdefs;
        } catch (Throwable t) {
            handleException(this, t, "Could not update rendering settings.");
        }
        return new HashMap<Long, RenderingDefData>();
    }
}
