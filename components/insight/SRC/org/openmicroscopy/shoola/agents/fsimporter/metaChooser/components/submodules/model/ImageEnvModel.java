package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model;

import java.util.HashMap;
import java.util.List;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.TagNames;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagData;
import org.openmicroscopy.shoola.util.MonitorAndDebug;
import org.slf4j.LoggerFactory;

import ome.units.quantity.Pressure;
import ome.units.quantity.Temperature;
import ome.units.unit.Unit;
import ome.xml.model.ImagingEnvironment;
import ome.xml.model.primitives.PercentFraction;

/**
 * Works for xsi:schemaLocation="http://www.openmicroscopy.org/Schemas/OME/2015-01 
 * @author Susanne Kunis &nbsp;&nbsp;&nbsp;&nbsp; <a
*         href="mailto:susannekunis@gmail.com">susannekunis@gmail.com</a>
 *
 */
public class ImageEnvModel 
{
	 private static final org.slf4j.Logger LOGGER =
	    	    LoggerFactory.getLogger(ImageEnvModel.class);
	 
	ImagingEnvironment element;

	private HashMap<String, String> map;
	
	public ImageEnvModel()
	{
		element=new ImagingEnvironment();
	}
	
	public ImageEnvModel(ImageEnvModel orig)
	{
		element=orig.element;
	}
	
	public HashMap<String,String> getMap()
	{
		return map;
	}
	
	public void setMap(HashMap<String,String> newMap)
	{
		map=newMap;
	}
	
	public boolean addData(ImagingEnvironment img, boolean overwrite)
	{
		boolean conflicts=false;
		if(overwrite){
			replaceData(img);
			LOGGER.info("[DATA] -- replace IMAGE_ENV data");
		}else
			try {
				completeData(img);
				LOGGER.info("[DATA] -- complete IMAGE_ENV data");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return conflicts;
	}

	private void replaceData(ImagingEnvironment i)
	{
		if(i!=null){
			element=new ImagingEnvironment(i);
		}
	}

	private void completeData(ImagingEnvironment i) throws Exception
	{
		//copy input fields
		ImagingEnvironment copyIn=null;
		if(element!=null){
			copyIn=new ImagingEnvironment(element);
		}

		replaceData(i);

		// set input field values again
		if(copyIn!=null){
			Temperature t=copyIn.getTemperature();
			Pressure p=copyIn.getAirPressure();
			PercentFraction h=copyIn.getHumidity();
			PercentFraction co=copyIn.getCO2Percent();
				
			if(t!=null) element.setTemperature(t);
			if(p!=null) element.setAirPressure(p);
			if(h!=null) element.setHumidity(h);
			if(co!=null) element.setCO2Percent(co);
		}
	}

	
	public ImagingEnvironment getImgEnv()
	{
		return element;
	}

	public void update(List<TagData> changesImgEnv) {
		if(changesImgEnv==null){
			MonitorAndDebug.printConsole("\t no changes for imgEnv");
			return;
		}
		for(TagData t: changesImgEnv){
			setTag(t.getTagName(),t.getTagValue(),t.getTagUnit());
			if(t.getTagUnit()!=null)
				map.put(t.getTagName(), t.getTagValue()+" "+t.getTagUnit().getSymbol());
			else
				map.put(t.getTagName(), t.getTagValue());
		}
	}
	
	private void setTag(String name,String val,Unit unit)
	{
		if(val.equals(""))
			return;
		
		MonitorAndDebug.printConsole("\t...update "+name+" : "+val);
		switch (name) {
		case TagNames.TEMP:
			element.setTemperature(val.equals("") ?
					null : new Temperature(Double.valueOf(val), unit));
			break;
		case TagNames.AIRPRESS:
			element.setAirPressure(val.equals("") ? 
					null : new Pressure(Double.valueOf(val),unit));
			break;
		case TagNames.HUMIDITY:
			element.setHumidity(val.equals("")? 
					null : new PercentFraction(Float.valueOf(val)/100));
			break;
		case TagNames.CO2:
			element.setCO2Percent(val.equals("")?
					null : new PercentFraction(Float.valueOf(val)/100));
			break;
		default:
			LOGGER.warn("[CONF] unknown tag: "+name );break;
		}
	}
}
