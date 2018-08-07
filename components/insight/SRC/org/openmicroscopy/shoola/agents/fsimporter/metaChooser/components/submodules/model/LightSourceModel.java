package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ome.units.quantity.Frequency;
import ome.units.quantity.Length;
import ome.units.quantity.Power;
import ome.units.unit.Unit;
import ome.xml.model.Arc;
import ome.xml.model.Filament;
import ome.xml.model.GenericExcitationSource;
import ome.xml.model.Laser;
import ome.xml.model.LightEmittingDiode;
import ome.xml.model.LightSource;
import ome.xml.model.LightSourceSettings;
import ome.xml.model.enums.ArcType;
import ome.xml.model.enums.FilamentType;
import ome.xml.model.enums.LaserMedium;
import ome.xml.model.enums.LaserType;
import ome.xml.model.enums.Pulse;
import ome.xml.model.primitives.PercentFraction;
import ome.xml.model.primitives.PositiveInteger;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.view.LightSourceSubViewer;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.view.LightSourceViewer;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.view.ModuleViewer;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.TagNames;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.microscope.MetaDataUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.MetaDataMapAnnotation;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagData;
import org.openmicroscopy.shoola.util.MonitorAndDebug;
import org.slf4j.LoggerFactory;


/**
 * Works for xsi:schemaLocation="http://www.openmicroscopy.org/Schemas/OME/2015-01 
 * @author Susanne Kunis &nbsp;&nbsp;&nbsp;&nbsp; <a
*         href="mailto:susannekunis@gmail.com">susannekunis@gmail.com</a>
 *
 */
public class LightSourceModel
{
	private static final org.slf4j.Logger LOGGER =
			LoggerFactory.getLogger(LightSourceModel.class);

	public final static String LASER="Laser";
	public final static String ARC="Arc";
	public final static String FILAMENT="Filament";
	public final static String GENERIC_EXCITATION="GenericExcitationSource";
	public final static String LIGHT_EMITTING_DIODE="LightEmittingDiode";

	private List<LightSource> element;

	// settings
	private List<LightSourceSettings> settings;
	
	private List<HashMap<String,String>> maps;

	

	public LightSourceModel()
	{
		element=new ArrayList<LightSource>();
		settings=new ArrayList<LightSourceSettings>();
		maps=new ArrayList<HashMap<String,String>>();
	}

	//copy constructor
	public LightSourceModel(LightSourceModel orig)
	{
		element=orig.element;
		settings=orig.settings;
		maps=orig.maps;
	}

	
	public HashMap<String,String> getMap(int i)
	{
		if(i>=maps.size())
			return null;
		return maps.get(i);
	}
	
	public void setMap(HashMap<String,String> map,int i)
	{
		
		if(i>=maps.size()){
			 Map.Entry<String,String> entry=map.entrySet().iterator().next();
			 String key=entry.getKey();
			String type=MetaDataMapAnnotation.getLastSubstring(key.substring(0,key.length()-1),":");
			switch(type){
			case LightSourceModel.LASER:
				expandList(maps.size(),i,new Laser());	break;
			case LightSourceModel.ARC:
				expandList(maps.size(),i,new Arc());break;
			case LightSourceModel.FILAMENT:
				expandList(maps.size(),i,new Filament());break;
			case LightSourceModel.GENERIC_EXCITATION:
				expandList(maps.size(),i,new GenericExcitationSource());break;
			case LightSourceModel.LIGHT_EMITTING_DIODE:
				expandList(maps.size(),i,new LightEmittingDiode());	break;
				default:LOGGER.warn(" #LightSrcModel::setMap():\n\t...unknown type");break;
			}
		}
			maps.set(i, map);
	}
	/**
	 * Overwrite or complete data. Caller class has to handle notification about the changes.
	 * @param newElem
	 * @param overwrite
	 * @throws Exception
	 */
	public void addData(LightSource newElem,boolean overwrite,int i) throws Exception
	{
		if(element.size()<=i){
			switch(newElem.getClass().getSimpleName()){
			case LASER: expandList(element.size(),i,new Laser());break;
			case ARC: expandList(element.size(),i,new Arc());break;
			case FILAMENT: expandList(element.size(),i,new Filament());break;
			case GENERIC_EXCITATION:expandList(element.size(),i,new GenericExcitationSource());break;
			case LIGHT_EMITTING_DIODE: expandList(element.size(),i,new LightEmittingDiode());break;
			default: LOGGER.warn("\t...unknown type: "+newElem.getClass().getSimpleName());break;
			}
			
		}
	
		if(overwrite || (newElem.getClass()!= element.get(i).getClass())){
			MonitorAndDebug.printConsole("# LightSrc::replaceData() at "+i+": "+newElem.getClass().getSimpleName());
			replaceData(newElem,i);
			LOGGER.info("[DATA] -- replace LightSource data");
		}else{
			MonitorAndDebug.printConsole("# LightSrc::completeData() at "+i+": "+newElem.getClass().getSimpleName());
			completeData(newElem,i);
			LOGGER.info("[DATA] -- complete LightSource data");
		}
	}

	/**
	 * Overwrite or complete settings data. Caller class has to handle notification about the changes.
	 * @param newElem
	 * @param overwrite
	 * @throws Exception
	 */
	public void addData(LightSourceSettings newElem,boolean overwrite,int i) throws Exception
	{
		if(settings.size()<=i){
			for(int j=settings.size();j<i+1;j++){
				settings.add(new LightSourceSettings());
			}
		}
		if(overwrite){
			replaceData(newElem,i);
			LOGGER.info("[DATA] -- replace LightSource data");
		}else{
			completeData(newElem,i);
			LOGGER.info("[DATA] -- complete LightSource data");
		}
	}

	/**
	 * Overwrite data with given data
	 * @param newElem
	 */
	private void replaceData(LightSource newElem,int i)
	{
		
		if(newElem!=null){
			if(newElem instanceof Arc){
				element.set(i,new Arc((Arc) newElem));
			}else if(newElem instanceof Laser){
				element.set(i,new Laser((Laser) newElem));
			}else if(newElem instanceof GenericExcitationSource){
				element.set(i,new GenericExcitationSource((GenericExcitationSource) newElem));
			}else if(newElem instanceof Filament){
				element.set(i,new Filament((Filament) newElem));
			}else if(newElem instanceof LightEmittingDiode){
				element.set(i,new LightEmittingDiode((LightEmittingDiode) newElem));
			}
		}
	}

	/**
	 * Overwrite data with given data
	 * @param newElem
	 */
	private void replaceData(LightSourceSettings newElem,int i)
	{
		if(newElem!=null){
			settings.set(i, new LightSourceSettings(newElem));
		}
	}

	/**
	 * Complete existing data with data from newElem
	 * @param newElem
	 * @throws Exception
	 */
	private void completeData(LightSource newElem,int i) throws Exception
	{
		switch(newElem.getClass().getSimpleName())
		{
		case LASER: completeLaserData(newElem,i);break;
		case ARC: completeArcData(newElem,i);break;
		case FILAMENT: completeFilamentData(newElem,i);break;
		case GENERIC_EXCITATION: completeGESData(newElem,i);break;
		case LIGHT_EMITTING_DIODE: completeLEDData(newElem,i);break;
		default: break;
		}

	}

	private void completeLEDData(LightSource newElem,int i) 
	{
		//copy input fields
		LightSource copyIn=null;
		if(element!=null){
			copyIn=new LightEmittingDiode((LightEmittingDiode)element.get(i));
		}

		replaceData(newElem,i);

		// set input field values again
		if(copyIn!=null){
			String mo=copyIn.getModel();
			String ma=copyIn.getManufacturer();

			if(mo!=null && !mo.equals("")) element.get(i).setModel(mo);
			if(ma!=null && !ma.equals("")) element.get(i).setManufacturer(ma);
		}

	}

	private void completeGESData(LightSource newElem,int i) 
	{
		//copy input fields
		LightSource copyIn=null;
		if(element!=null){
			copyIn=new GenericExcitationSource((GenericExcitationSource)element.get(i));
		}

		replaceData(newElem,i);

		// set input field values again
		if(copyIn!=null){
			String mo=copyIn.getModel();
			String ma=copyIn.getManufacturer();
			Power p=copyIn.getPower();

			if(mo!=null && !mo.equals("")) element.get(i).setModel(mo);
			if(ma!=null && !ma.equals("")) element.get(i).setManufacturer(ma);
			if(p!=null) element.get(i).setPower(p);
		}		
	}

	private void completeFilamentData(LightSource newElem,int i) 
	{
		//copy input fields
		LightSource copyIn=null;
		if(element!=null){
			copyIn=new Filament((Filament)element.get(i));
		}

		replaceData(newElem,i);

		// set input field values again
		if(copyIn!=null){
			String mo=copyIn.getModel();
			String ma=copyIn.getManufacturer();
			Power p=copyIn.getPower();
			FilamentType t=((Filament)copyIn).getType();

			if(mo!=null && !mo.equals("")) element.get(i).setModel(mo);
			if(ma!=null && !ma.equals("")) element.get(i).setManufacturer(ma);
			if(p!=null) element.get(i).setPower(p);
			if(t!=null) ((Filament) element.get(i)).setType(t);
		}

	}

	private void completeArcData(LightSource newElem,int i) {
		//copy input fields
		LightSource copyIn=null;
		if(element!=null){
			copyIn=new Arc((Arc)element.get(i));
		}

		replaceData(newElem,i);

		// set input field values again
		if(copyIn!=null){
			String mo=copyIn.getModel();
			String ma=copyIn.getManufacturer();
			Power p=copyIn.getPower();
			ArcType t=((Arc)copyIn).getType();

			if(mo!=null && !mo.equals("")) element.get(i).setModel(mo);
			if(ma!=null && !ma.equals("")) element.get(i).setManufacturer(ma);
			if(p!=null) element.get(i).setPower(p);
			if(t!=null) ((Arc) element.get(i)).setType(t);
		}		
	}

	private void completeLaserData(LightSource newElem,int i) {
		//copy input fields
		LightSource copyIn=null;
		if(element!=null){
			copyIn=new Laser((Laser)element.get(i));
		}

		replaceData(newElem,i);

		// set input field values again
		if(copyIn!=null){
			String mo=copyIn.getModel();
			String ma=copyIn.getManufacturer();
			Power p=copyIn.getPower();
			LaserType t=((Laser)copyIn).getType();
			LaserMedium m=((Laser)copyIn).getLaserMedium();
			PositiveInteger fM=((Laser)copyIn).getFrequencyMultiplication();
			Boolean tu=((Laser)copyIn).getTuneable();
			Boolean po=((Laser)copyIn).getPockelCell();
			Frequency rr=((Laser)copyIn).getRepetitionRate();
			Length w=((Laser)copyIn).getWavelength();
			Pulse pu=((Laser)copyIn).getPulse();

			if(mo!=null && !mo.equals("")) element.get(i).setModel(mo);
			if(ma!=null && !ma.equals("")) element.get(i).setManufacturer(ma);
			if(p!=null) element.get(i).setPower(p);
			if(t!=null) ((Laser) element.get(i)).setType(t);
			if(m!=null) ((Laser)element.get(i)).setLaserMedium(m);
			if(fM!=null) ((Laser)element.get(i)).setFrequencyMultiplication(fM);
			if(tu!=null){
				((Laser)element.get(i)).setTuneable(tu);
			}
			if(po!=null) ((Laser)element.get(i)).setPockelCell(po);
			if(rr!=null) ((Laser)element.get(i)).setRepetitionRate(rr);
			if(w!=null) ((Laser)element.get(i)).setWavelength(w);
			if(pu!=null) ((Laser)element.get(i)).setPulse(pu);
			if(((Laser)newElem).getLinkedPump()!=null) ((Laser)element.get(i)).linkPump(((Laser)newElem).getLinkedPump());
		}		
	}

	/**
	 * Complete existing data with data from newElem
	 * @param newElem
	 * @throws Exception
	 */
	private void completeData(LightSourceSettings newElem,int i) throws Exception
	{
		//copy input fields
		LightSourceSettings copyIn=null;
		if(settings!=null){
			copyIn=new LightSourceSettings(settings.get(i));
		}

		replaceData(newElem,i);

		// set input field values again
		if(copyIn!=null){
			Length w=copyIn.getWavelength();
			PercentFraction p=copyIn.getAttenuation();
			if(w!=null) settings.get(i).setWavelength(w);
			if(p!=null) settings.get(i).setAttenuation(p);
		}
	}

	

	public LightSource getLightSource(int i) 
	{
		if(i>=element.size())
			return null;
		return element.get(i);
	}

	public LightSourceSettings getSettings(int i)
	{
		if(i>=settings.size())
			return null;
		
		if(settings.get(i)!=null && settings.get(i).getLightSource()==null)
			settings.get(i).setLightSource(element.get(i));
		
		return settings.get(i);
	}
	
	/**
	 * If index exits size, expand elements and settings list
	 * @param size
	 * @param index
	 */
	private void expandList(int size,int index,Object newElem) 
	{
		for(int i=size;i<index+1;i++){
			element.add((LightSource) newElem);
			maps.add(new HashMap<String,String>());
		}
	}
	

	
	
	public int getNumberOfLightSrc()
	{
		if(element==null)
			return 0;
		return element.size();
	}

	public void remove(int index) {
		if(element!=null && !element.isEmpty())
			element.remove(index);		
	}

	/**
	 * Update list of lightSrc with given modified tags.
	 * Do nothing if lightSrc at index doesn't exists.
	 * @param changesLightSrc
	 * @throws Exception
	 */
	public void update(List<List<TagData>> changesLightSrc) throws Exception 
	{
		if(changesLightSrc==null){
			MonitorAndDebug.printConsole("\t no changes for lightSource");
			return;
		}else{
			MonitorAndDebug.printConsole("\t changes LightSrc: "+changesLightSrc.size());
		}
		int index=0;
		for(List<TagData> list :changesLightSrc){
			if(list!=null && element.size()>index 
					&& element.get(index)!=null){
				
				LightSource lightSrc=element.get(index);
				LightSourceSettings sett=settings.get(index);
				String thisElemClass=lightSrc.getClass().getSimpleName();
				String listElemClass=list.get(list.size()-1).getTagValue();
				
				if(thisElemClass.equals(listElemClass)){
					for(TagData t: list){
						updateTag(lightSrc,sett,t.getTagName(),t.getTagValue(),t.getTagUnit());
						String id="["+listElemClass+"]:";
						if(t.getTagUnit()!=null)
							maps.get(index).put(id+t.getTagName(), t.getTagValue()+" "+t.getTagUnit().getSymbol());
						else
							maps.get(index).put(id+t.getTagName(), t.getTagValue());
					}
				}
			}
			index++;
		}		
	}

	
	private void updateTag(LightSource lightSrc, LightSourceSettings sett,
			String tagName, String tagValue, Unit tagUnit) throws Exception 
	{
		switch(lightSrc.getClass().getSimpleName())
		{
		case LASER: updateLaserData((Laser) lightSrc,tagName,tagValue,tagUnit);break;
		case ARC:updateArcData((Arc) lightSrc,tagName,tagValue,tagUnit);break;
		case FILAMENT: updateFilamentData((Filament) lightSrc,tagName,tagValue,tagUnit);break;
		case GENERIC_EXCITATION: updateGESData((GenericExcitationSource) lightSrc,tagName,tagValue,tagUnit);break;
		case LIGHT_EMITTING_DIODE: updateLEDData((LightEmittingDiode) lightSrc,tagName,tagValue,tagUnit);break;
		default: break;
		}
		
		switch(tagName){
		case TagNames.SET_WAVELENGTH:
			sett.setWavelength(ModuleViewer.parseToLength(tagValue, tagUnit, true));
			break;
		case TagNames.ATTENUATION:
			sett.setAttenuation(LightSourceViewer.parseAttenuation(tagValue));
			break;
		default:break;
		}
	}
	
	private void updateLEDData(LightEmittingDiode lightSrc, String tagName, String tagValue, Unit tagUnit) {
		switch(tagName){
		case TagNames.MODEL:
			lightSrc.setModel(tagValue);
			break;
		case TagNames.MANUFAC:
			lightSrc.setManufacturer(tagValue);
			break;
		default:
			LOGGER.warn("[UPDATE] unknown tag: "+tagName );break;
		}		
	}

	private void updateGESData(GenericExcitationSource lightSrc, String tagName, String tagValue, Unit tagUnit) {
		switch(tagName){
		case TagNames.MODEL:
			lightSrc.setModel(tagValue);
			break;
		case TagNames.MANUFAC:
			lightSrc.setManufacturer(tagValue);
			break;
		case TagNames.POWER:
			try {
				lightSrc.setPower(LightSourceSubViewer.parsePower(tagValue,tagUnit));
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			break;
		default:
			LOGGER.warn("[UPDATE] unknown tag: "+tagName );break;
		}		
	}

	private void updateFilamentData(Filament lightSrc, String tagName, String tagValue, Unit tagUnit) {
		switch(tagName){
		case TagNames.MODEL:
			lightSrc.setModel(tagValue);
			break;
		case TagNames.MANUFAC:
			lightSrc.setManufacturer(tagValue);
			break;
		case TagNames.F_TYPE:
				lightSrc.setType(LightSourceSubViewer.parseFilamentType(tagValue));
			break;
		case TagNames.POWER:
			try {
				lightSrc.setPower(LightSourceSubViewer.parsePower(tagValue,tagUnit));
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			break;
		default:
			LOGGER.warn("[UPDATE] unknown tag: "+tagName );break;
		}		
	}

	private void updateArcData(Arc lightSrc, String tagName, String tagValue, Unit tagUnit) {
		switch(tagName){
		case TagNames.MODEL:
			lightSrc.setModel(tagValue);
			break;
		case TagNames.MANUFAC:
			lightSrc.setManufacturer(tagValue);
			break;
		case TagNames.A_TYPE:
				lightSrc.setType(LightSourceSubViewer.parseArcType(tagValue));
			break;
		case TagNames.POWER:
			try {
				lightSrc.setPower(LightSourceSubViewer.parsePower(tagValue,tagUnit));
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			break;
		default:
			LOGGER.warn("[UPDATE] unknown tag: "+tagName );break;
		}
		
	}

	private void updateLaserData(Laser lightSrc, String tagName, String tagValue, Unit tagUnit) {
		
		switch(tagName){
		case TagNames.MODEL:
			lightSrc.setModel(tagValue);
			break;
		case TagNames.MANUFAC:
			lightSrc.setManufacturer(tagValue);
			break;
		case TagNames.L_TYPE:
				lightSrc.setType(LightSourceSubViewer.parseLaserType(tagValue));
			break;
		case TagNames.POWER:
			try {
				lightSrc.setPower(LightSourceSubViewer.parsePower(tagValue,tagUnit));
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			break;
		case TagNames.MEDIUM:
			lightSrc.setLaserMedium(LightSourceSubViewer.parseMedium(tagValue));
			break;
		case TagNames.FREQMUL:
			try {
				lightSrc.setFrequencyMultiplication(ModuleViewer.parseToPositiveInt(tagValue));
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			break;
		case TagNames.TUNABLE:
			lightSrc.setTuneable(ModuleViewer.parseToBoolean(tagValue));
			break;
		case TagNames.PULSE:
			lightSrc.setPulse(LightSourceSubViewer.parsePulse(tagValue));
			break;
		case TagNames.POCKELCELL:
			lightSrc.setPockelCell(ModuleViewer.parseToBoolean(tagValue));
			break;
		case TagNames.REPRATE:
			try {
				lightSrc.setRepetitionRate(LightSourceSubViewer.parseFrequency(tagValue,tagUnit));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case TagNames.PUMP:
			//TODO
			break;
		case TagNames.WAVELENGTH:
			try {
				lightSrc.setWavelength(ModuleViewer.parseToLength(tagValue, tagUnit, true));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		default:
			LOGGER.warn("[UPDATE] unknown tag: "+tagName );break;
		}
		
	}

	public void printValues()
	{
		for(int i=0; i<element.size();i++){
			LightSource d= element.get(i);
			if(d!=null){
				MonitorAndDebug.printConsole("\t"+d.getClass().getSimpleName()+" : "+i);
				MonitorAndDebug.printConsole("\t...lightSrc model model = "+(d.getModel()!=null ? d.getModel(): ""));
				MonitorAndDebug.printConsole("\t...lightSrc model power = "+(d.getPower()!=null ? d.getPower(): ""));
				MonitorAndDebug.printConsole("\t...lightSrc model tunable = "+(((Laser) d).getTuneable()!=null ? ((Laser) d).getTuneable(): ""));
			}
		}
	}

}
