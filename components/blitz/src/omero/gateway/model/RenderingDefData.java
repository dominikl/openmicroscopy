package omero.gateway.model;

import java.util.regex.Pattern;

import omero.rtypes;
import omero.model.ChannelBinding;
import omero.model.ChannelBindingI;
import omero.model.RenderingDef;
import omero.model.RenderingDefI;

import org.apache.commons.lang.StringUtils;

public class RenderingDefData extends DataObject {

    private static final Pattern hex = Pattern.compile("[0-9A-F]{6}");

    public RenderingDefData() {
        setDirty(true);
        setValue(new RenderingDefI());
    }
    
    public RenderingDefData(RenderingDef rdef) {
        if (rdef == null) {
            throw new IllegalArgumentException("Object cannot null.");
        }
        setValue(rdef);
    }

    public void set(int channelIndex, String lut, String color, double min,
            double max, Boolean active) {
        if (!hex.matcher(color).matches())
            throw new IllegalArgumentException(
                    "Color string is not an RGB hex string");

        String s = color.substring(0, 2);
        int red = Integer.parseInt(s, 16);
        s = color.substring(2, 4);
        int green = Integer.parseInt(s, 16);
        s = color.substring(4, 6);
        int blue = Integer.parseInt(s, 16);

        set(channelIndex, lut, red, green, blue, min, max, active);
    }

    public void set(int channelIndex, String lut, int red, int green, int blue,
            double min, double max, Boolean active) {
        if (channelIndex < 0)
            throw new IllegalArgumentException("No channel index provided.");

        RenderingDefI r = (RenderingDefI) asIObject();

        ChannelBinding cb = r.getChannelBinding(channelIndex);
        if (cb == null) {
            cb = new ChannelBindingI();
            r.setChannelBinding(channelIndex, cb);
        }

        if (StringUtils.isNotEmpty(lut))
            cb.setLookupTable(rtypes.rstring(lut));

        if (red >= 0)
            cb.setRed(rtypes.rint(red));

        if (green >= 0)
            cb.setGreen(rtypes.rint(green));

        if (blue >= 0)
            cb.setBlue(rtypes.rint(blue));

        if (active != null)
            cb.setActive(rtypes.rbool(active));
    }

    public String getLookupTable(int channelIndex) {
        RenderingDefI r = (RenderingDefI) asIObject();
        ChannelBinding cb = r.getChannelBinding(channelIndex);
        if (cb != null)
            return cb.getLookupTable().getValue();
        else
            return null;
    }

    public int getRed(int channelIndex) {
        RenderingDefI r = (RenderingDefI) asIObject();
        ChannelBinding cb = r.getChannelBinding(channelIndex);
        if (cb != null)
            return cb.getRed().getValue();
        else
            return -1;
    }

    public int getGreen(int channelIndex) {
        RenderingDefI r = (RenderingDefI) asIObject();
        ChannelBinding cb = r.getChannelBinding(channelIndex);
        if (cb != null)
            return cb.getGreen().getValue();
        else
            return -1;
    }

    public int getBlue(int channelIndex) {
        RenderingDefI r = (RenderingDefI) asIObject();
        ChannelBinding cb = r.getChannelBinding(channelIndex);
        if (cb != null)
            return cb.getBlue().getValue();
        else
            return -1;
    }
    
    public double getMin(int channelIndex) {
        RenderingDefI r = (RenderingDefI) asIObject();
        ChannelBinding cb = r.getChannelBinding(channelIndex);
        if (cb != null)
            return cb.getInputStart().getValue();
        else
            return -1;
    }
    
    public double getMax(int channelIndex) {
        RenderingDefI r = (RenderingDefI) asIObject();
        ChannelBinding cb = r.getChannelBinding(channelIndex);
        if (cb != null)
            return cb.getInputEnd().getValue();
        else
            return -1;
    }

    public boolean isActive(int channelIndex) {
        RenderingDefI r = (RenderingDefI) asIObject();
        ChannelBinding cb = r.getChannelBinding(channelIndex);
        if (cb != null)
            return cb.getActive().getValue();
        else
            return false;
    }
}
