package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ome.units.quantity.ElectricPotential;
import ome.units.unit.Unit;
import ome.xml.model.Detector;
import ome.xml.model.enums.Binning;
import ome.xml.model.enums.DetectorType;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.view.ModuleViewer;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model.xml.DetectorSettings;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.view.DetectorViewer;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.TagNames;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagData;
import org.openmicroscopy.shoola.util.MonitorAndDebug;
import org.slf4j.LoggerFactory;

/**
 * Works for xsi:schemaLocation="http://www.openmicroscopy.org/Schemas/OME/2015-01 
 * @author Susanne Kunis &nbsp;&nbsp;&nbsp;&nbsp; <a
*         href="mailto:susannekunis@gmail.com">susannekunis@gmail.com</a>
 *
 */
public class DetectorModel 
{
	 private static final org.slf4j.Logger LOGGER =
	    	    LoggerFactory.getLogger(DetectorModel.class);
	
	private List<Detector> element;
	
	// settings
	private List<DetectorSettings> settings;
	
	private List<HashMap<String,String>> maps;
	
	// list of available detector (set by hardware definition)
//	private List<Detector> availableElem;
	
	public DetectorModel()
	{
		element=new ArrayList<Detector>();
		settings=new ArrayList<DetectorSettings>();
		maps=new ArrayList<HashMap<String,String>>();
	}
	
	//copy constructor
	public DetectorModel(DetectorModel orig)
	{
		element=orig.element;
		settings=orig.settings;
		maps=orig.maps;
	}
	
	/**
	 * Overwrite or complete data. Caller class has to handle notification about the changes.
	 * @param newElem
	 * @param overwrite
	 * @throws Exception
	 */
	public void addData(Detector newElem,boolean overwrite,int index) throws Exception
	{
		if(element.size()<=index){
			expandList(element.size(),index);
		}
		
		if(overwrite){
			replaceData(newElem,index);
			LOGGER.info("[DATA] -- replace Detector data");
		}else{
			completeData(newElem,index);
			LOGGER.info("[DATA] -- complete Detector data");
		}
	}
	
	/**
	 * Overwrite or complete settings data. Caller class has to handle notification about the changes.
	 * @param newElem
	 * @param overwrite
	 * @throws Exception
	 */
	public void addData(DetectorSettings newElem,boolean overwrite,int index) throws Exception
	{
		if(overwrite){
			replaceData(newElem,index);
			LOGGER.info("[DATA] -- replace Detector data");
		}else{
			completeData(newElem,index);
			LOGGER.info("[DATA] -- complete Detector data");
		}
	}
	
	/**
	 * Overwrite data with copy of given data
	 * @param newElem
	 */
	private void replaceData(Detector newElem,int index)
	{
		if(newElem!=null){
			element.set(index, new Detector(newElem));
			DetectorType t=newElem.getType();
			
			t=element.get(index).getType();
			
		}
		
	}
	
	/**
	 * If index exits size, expand elements and settings list
	 * @param size
	 * @param index
	 */
	private void expandList(int size,int index) 
	{
		for(int i=size;i<index+1;i++){
			element.add(new Detector());
			settings.add(new DetectorSettings());
			maps.add(new HashMap<String,String>());
		}
	}

	/**
	 * Overwrite data with given data
	 * @param newElem
	 */
	private void replaceData(DetectorSettings newElem,int index)
	{
		if(settings.size()<=index){
			expandList(settings.size(),index);
		}
		if(newElem!=null){
			settings.set(index,new DetectorSettings(newElem));
		}
	}
	
	/**
	 * Complete existing data with data from newElem
	 * @param newElem
	 * @throws Exception
	 */
	private void completeData(Detector newElem,int index) throws Exception
	{
		
		//copy input fields
		Detector copyIn=null;
		if(element!=null){
			copyIn=new Detector(element.get(index));
		}

		replaceData(newElem,index);
		// set input field values again
		if(copyIn!=null){
			String mo=copyIn.getModel();
			String ma=copyIn.getManufacturer();
			DetectorType t=copyIn.getType();
			ElectricPotential v=copyIn.getVoltage();
			Double o = copyIn.getOffset();
			Double z=copyIn.getZoom();
			Double a=copyIn.getAmplificationGain();
			Double g=copyIn.getGain();
			
			Detector d=element.get(index);
			if(mo!=null && !mo.equals("")) d.setModel(mo);
			if(ma!=null && !ma.equals("")) d.setManufacturer(ma);
			if(t!=null) d.setType(t);
			if(v!=null) d.setVoltage(v);
			if(o!=null) d.setOffset(o);
			if(z!=null) d.setZoom(z);
			if(a!=null) d.setAmplificationGain(a);
			if(g!=null) d.setGain(g);
		}
	}
	
	/**
	 * Complete existing data with data from newElem
	 * @param newElem
	 * @throws Exception
	 */
	private void completeData(DetectorSettings newElem,int index) throws Exception
	{
		if(settings.size()<=index){
			expandList(settings.size(),index);
		}
		//copy input fields
		DetectorSettings copyIn=null;
		if(settings!=null){
			copyIn=new DetectorSettings(settings.get(index));
		}

		replaceData(newElem,index);

		// set input field values again
		if(copyIn!=null){
			Double g=copyIn.getGain();
			ElectricPotential v=copyIn.getVoltage();
			Double o=copyIn.getOffset();
			Double z=copyIn.getZoom();
			Binning b=copyIn.getBinning();
			String s=copyIn.getSubarray();

			DetectorSettings sett=settings.get(index);
			if(g!=null) sett.setGain(g);
			if(v!=null) sett.setVoltage(v);
			if(o!=null) sett.setOffset(o);
			if(z!=null) sett.setZoom(z);
			if(b!=null) sett.setBinning(b);
			if(s!=null) sett.setSubarray(s);
		}
	}
	
	

	/**
	 * 
	 * @return
	 */
	public Detector getDetector(int index) {
		if(index>=element.size())
			return null;
		return element.get(index);
	}
	
	public DetectorSettings getSettings(int i)
	{
		if(i>=settings.size())
			return null;
		
		if(settings.get(i)!=null && settings.get(i).getDetector()==null)
			settings.get(i).setDetector(element.get(i));
		
		return settings.get(i);
	}
	
	public HashMap<String,String> getMap(int i)
	{
		
		if(i>=maps.size())
			return null;
		return maps.get(i);
	}
	
	public void setMap(HashMap<String,String> map,int i)
	{
		if(i>=maps.size())
			expandList(maps.size(),i);
		
		maps.set(i, map);
	}
	


	public int getNumberOfElements() {
		return element.size();
	}

	public void remove(int index) {
		if(element!=null && !element.isEmpty())
			element.remove(index);		
	}

	/**
	 * Update list of detectors with given modified tags.
	 * Do nothing if detector at index doesn't exists.
	 * @param changesDetector
	 * @throws Exception
	 */
	public void update(List<List<TagData>> changesDetector) throws Exception 
	{
		if(changesDetector==null){
			MonitorAndDebug.printConsole("\t no changes for detector");
			return;
		}
		int index=0;
		for(List<TagData> list :changesDetector){
			if(list!=null && element.size()>index 
					&& element.get(index)!=null){
				Detector detector=element.get(index);
				DetectorSettings sett=settings.get(index);
				for(TagData t: list){
					updateTag(detector,sett,t.getTagName(),t.getTagValue(),t.getTagUnit());
					if(t.getTagUnit()!=null)
						maps.get(index).put(t.getTagName(), t.getTagValue()+" "+t.getTagUnit().getSymbol());
					else
						maps.get(index).put(t.getTagName(), t.getTagValue());
				}
			}
			index++;
		}
	}

	/**
	 * Update tag of given channel if value!=""
	 * @param detector
	 * @param tagName
	 * @param tagValue
	 * @param tagUnit
	 * @throws Exception 
	 */
	private void updateTag(Detector detector, DetectorSettings sett,String tagName, String tagValue,
			Unit tagUnit) throws Exception 
	{
		if(tagValue.equals(""))
			return;
		
		switch (tagName) 
		{
		case TagNames.MODEL: 
			detector.setModel(tagValue);		
			break;
		case TagNames.MANUFAC: 
			detector.setManufacturer(tagValue);			
			break;
		case TagNames.D_TYPE:
			detector.setType(DetectorViewer.parseDetectorType(tagValue));
			break;
		case TagNames.ZOOM:
			detector.setZoom(ModuleViewer.parseToDouble(tagValue));
			break;
		case TagNames.AMPLGAIN:
			detector.setAmplificationGain(ModuleViewer.parseToDouble(tagValue));
			break;
			case TagNames.GAIN:
				if(sett!=null){
					sett.setGain(ModuleViewer.parseToDouble(tagValue));
				}
				break;
			case TagNames.VOLTAGE:
				if(sett!=null){
					sett.setVoltage(DetectorViewer.parseElectricPotential(tagValue,tagUnit));
				}
				break;
			case TagNames.OFFSET:
				if(sett!=null){
					sett.setOffset(ModuleViewer.parseToDouble(tagValue));
				}
				break;
			case TagNames.CONFZOOM:
				if(sett!=null){
					sett.setZoom(ModuleViewer.parseToDouble(tagValue));
				}
				break;
			case TagNames.BINNING:
				if(sett!=null){
					sett.setBinning(DetectorViewer.parseBinning(tagValue));
				}
				break;
			case TagNames.SUBARRAY:
				if(sett!=null){
					sett.setSubarray(tagValue);
				}
				break;
		default: LOGGER.warn("[CONF] unknown tag: "+tagName );break;
		}
	}
	
	public void printValues()
	{
		for(int i=0; i<element.size();i++){
			Detector d= element.get(i);
			if(d!=null){
				MonitorAndDebug.printConsole("\tDetector : "+i);
				MonitorAndDebug.printConsole("\t...detector model model = "+(d.getModel()!=null ? d.getModel(): ""));
				MonitorAndDebug.printConsole("\t...detector model type = "+(d.getType()!=null ? d.getType().getValue(): ""));
				MonitorAndDebug.printConsole("\t...detector model zoom = "+(d.getZoom()!=null ? d.getZoom(): ""));
			}
		}
	}

}
