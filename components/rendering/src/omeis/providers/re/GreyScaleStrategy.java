/*
 * omeis.providers.re.GreyScaleStrategy
 *
 *   Copyright 2006 University of Dundee. All rights reserved.
 *   Use is subject to license terms supplied in LICENSE.txt
 */

package omeis.providers.re;

// Java imports
import java.io.IOException;

// Third-party libraries
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

// Application-internal dependencies
import ome.io.nio.PixelBuffer;
import ome.model.core.Pixels;
import ome.model.display.ChannelBinding;
import omeis.providers.re.codomain.CodomainChain;
import omeis.providers.re.data.PlaneFactory;
import omeis.providers.re.data.Plane2D;
import omeis.providers.re.data.PlaneDef;
import omeis.providers.re.quantum.QuantizationException;
import omeis.providers.re.quantum.QuantumStrategy;

/**
 * Transforms a plane within a given pixels set into a greyscale image.
 * 
 * @author Jean-Marie Burel &nbsp;&nbsp;&nbsp;&nbsp; <a
 *         href="mailto:j.burel@dundee.ac.uk">j.burel@dundee.ac.uk</a>
 * @author <br>
 *         Andrea Falconi &nbsp;&nbsp;&nbsp;&nbsp; <a
 *         href="mailto:a.falconi@dundee.ac.uk"> a.falconi@dundee.ac.uk</a>
 * @version 2.2 <small> (<b>Internal version:</b> $Revision$ $Date:
 *          2005/06/22 17:09:48 $) </small>
 * @since OME2.2
 */
class GreyScaleStrategy extends RenderingStrategy {

    /** The logger for this particular class */
    private static Log log = LogFactory.getLog(GreyScaleStrategy.class);
    
    /** The channel we're operating on */
    private int channel;
    
    /** The channel binding we're using */
    private ChannelBinding channelBinding;
    
    /**
     * Initializes the <code>sizeX1</code> and <code>sizeX2</code> fields
     * according to the specified {@link PlaneDef#getSlice() slice}.
     * 
     * @param pd
     *            Reference to the plane definition defined for the strategy.
     * @param pixels
     *            Dimensions of the pixels set.
     */
    private void initAxesSize(PlaneDef pd, Pixels pixels) {
        try {
            switch (pd.getSlice()) {
                case PlaneDef.XY:
                    sizeX1 = pixels.getSizeX().intValue();
                    sizeX2 = pixels.getSizeY().intValue();
                    break;
                case PlaneDef.XZ:
                    sizeX1 = pixels.getSizeX().intValue();
                    sizeX2 = pixels.getSizeZ().intValue();
                    break;
                case PlaneDef.ZY:
                    sizeX1 = pixels.getSizeZ().intValue();
                    sizeX2 = pixels.getSizeY().intValue();
            }
        } catch (NumberFormatException nfe) {
            throw new RuntimeException("Invalid slice ID: " + pd.getSlice()
                    + ".", nfe);
        }
    }

    /**
     * Implemented as specified by the superclass.
     * 
     * @see RenderingStrategy#render(Renderer ctx, PlaneDef planeDef)
     */
    @Override
    RGBBuffer render(Renderer ctx, PlaneDef planeDef) throws IOException,
            QuantizationException {
        // Set the context and retrieve objects we're gonna use.
        renderer = ctx;
        findFirstActiveChannelBinding();
        PixelBuffer pixels = renderer.getPixels();
        Pixels metadata = renderer.getMetadata();
        RenderingStats performanceStats = renderer.getStats();
        QuantumStrategy qs = 
        	renderer.getQuantumManager().getStrategyFor(channel);
        CodomainChain cc = renderer.getCodomainChain();
        
        // Retrieve the planar data to render
        performanceStats.startIO(channel);
        Plane2D plane =
        	PlaneFactory.createPlane(planeDef, channel, metadata, pixels);
        performanceStats.endIO(channel);

        // Initialize sizeX1 and sizeX2 according to the plane definition and
        // create the RGB buffer.
        initAxesSize(planeDef, metadata);
        RGBBuffer buf = getRgbBuffer();
        
        byte value;
        float alpha = channelBinding.getColor().getAlpha().floatValue() / 255;

        int x1, x2, discreteValue, pixelIndex;
        byte[] r = buf.getRedBand();
        byte[] g = buf.getBlueBand();
        byte[] b = buf.getGreenBand();
        if (plane.isXYPlanar())
        {
        	int planeSize = sizeX1 * sizeX2;
            for (int i = 0; i < planeSize; i++)
            {
                for (x1 = 0; x1 < sizeX1; ++x1)
                {
                    discreteValue = qs.quantize(plane.getPixelValue(i));
                    discreteValue = cc.transform(discreteValue);
                    value = (byte) (discreteValue * alpha);
                    r[i] = value;
                    g[i] = value;
                    b[i] = value;
                }
            }
        }
        else
        {
        	for (x2 = 0; x2 < sizeX2; ++x2) {
        		for (x1 = 0; x1 < sizeX1; ++x1) {
        			pixelIndex = sizeX1 * x2 + x1;
        			discreteValue = qs.quantize(plane.getPixelValue(x1, x2));
        			discreteValue = cc.transform(discreteValue);
        			value = (byte) (discreteValue * alpha);
        			r[pixelIndex] = value;
        			g[pixelIndex] = value;
        			b[pixelIndex] = value;
        		}
        	}
        }
        return buf;
    }
    
    /**
	 * Implemented as specified by the superclass.
	 * 
	 * @see RenderingStrategy#render(Renderer ctx, PlaneDef planeDef)
	 */
	@Override
	RGBIntBuffer renderAsPackedInt(Renderer ctx, PlaneDef planeDef)
	        throws IOException, QuantizationException {
        // Set the context and retrieve objects we're gonna use.
        renderer = ctx;
        findFirstActiveChannelBinding();
        PixelBuffer pixels = renderer.getPixels();
        Pixels metadata = renderer.getMetadata();
        RenderingStats performanceStats = renderer.getStats();
        QuantumStrategy qs = 
        	renderer.getQuantumManager().getStrategyFor(channel);
        CodomainChain cc = renderer.getCodomainChain();
        
        // Retrieve the planar data to render
        performanceStats.startIO(channel);
        Plane2D plane =
        	PlaneFactory.createPlane(planeDef, channel, metadata, pixels);
        performanceStats.endIO(channel);
	
	    // Initialize sizeX1 and sizeX2 according to the plane definition and
	    // create the RGB buffer.
	    initAxesSize(planeDef, metadata);
	    RGBIntBuffer dataBuf = getIntBuffer();
	    
        int alpha = channelBinding.getColor().getAlpha();
        int[] buf = ((RGBIntBuffer) dataBuf).getDataBuffer();
        int x1, x2, discreteValue, pixelIndex;
        if (plane.isXYPlanar())
        {
        	int planeSize = sizeX1 * sizeX2;
        	for (int i = 0; i < planeSize; i++)
        	{
                discreteValue = qs.quantize(plane.getPixelValue(i));
                // Right now we have no transforms being used so it's safe to
                // comment this out for the time being.
                //discreteValue = cc.transform(discreteValue);
                buf[i] = alpha << 24 | discreteValue << 16
                        | discreteValue << 8 | discreteValue;            		
        	}
        }
        else
        {
        	for (x2 = 0; x2 < sizeX2; ++x2) {
        		pixelIndex = sizeX1 * x2;
        		for (x1 = 0; x1 < sizeX1; ++x1) {
        			discreteValue = qs.quantize(plane.getPixelValue(x1, x2));
        			discreteValue = cc.transform(discreteValue);
        			buf[pixelIndex + x1] = alpha << 24 | discreteValue << 16
        			| discreteValue << 8 | discreteValue;
        		}
        	}
        }
	    return dataBuf;
	}

	/**
	 * Initializes the first active channel binding for the current rendering
	 * context.
	 */
	private void findFirstActiveChannelBinding()
	{
		ChannelBinding[] channelBindings = renderer.getChannelBindings();
		for (int i = 0; i < channelBindings.length; i++)
		{
			if (channelBindings[i].getActive())
			{
				channel = i;
				channelBinding = channelBindings[i];
				return;
			}
		}
		throw new IllegalArgumentException("No active channel bindings found.");
	}

    /**
     * Implemented as specified by the superclass.
     * 
     * @see RenderingStrategy#getImageSize(PlaneDef, Pixels)
     */
    @Override
    int getImageSize(PlaneDef pd, Pixels pixels) {
        initAxesSize(pd, pixels);
        return sizeX1 * sizeX2 * 3;
    }

    /**
     * Implemented as specified by the superclass.
     * 
     * @see RenderingStrategy#getPlaneDimsAsString(PlaneDef, Pixels)
     */
    @Override
    String getPlaneDimsAsString(PlaneDef pd, Pixels pixels) {
        initAxesSize(pd, pixels);
        return sizeX1 + "x" + sizeX2;
    }

}
