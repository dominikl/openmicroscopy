package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model;

import java.util.HashMap;
import java.util.List;

import ome.units.quantity.Length;
import ome.units.quantity.Time;
import ome.units.unit.Unit;
import ome.xml.model.Annotation;
import ome.xml.model.Dataset;
import ome.xml.model.Experiment;
import ome.xml.model.Experimenter;
import ome.xml.model.ExperimenterGroup;
import ome.xml.model.Image;
import ome.xml.model.ImagingEnvironment;
import ome.xml.model.Instrument;
import ome.xml.model.MicrobeamManipulation;
import ome.xml.model.ObjectiveSettings;
import ome.xml.model.Pixels;
import ome.xml.model.ROI;
import ome.xml.model.StageLabel;
import ome.xml.model.WellSample;
import ome.xml.model.enums.PixelType;
import ome.xml.model.primitives.PositiveInteger;
import ome.xml.model.primitives.Timestamp;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.view.ModuleViewer;
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
public class ImageModel 
{
	 private static final org.slf4j.Logger LOGGER =
	    	    LoggerFactory.getLogger(ImageModel.class);
	
	private Image element;

	private HashMap<String, String> map;
	
	public ImageModel()
	{
		element=new Image();
		element.setPixels(new Pixels());
	}
	
	public ImageModel(ImageModel orig)
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
	
	public boolean addData(Image img, boolean overwrite)
	{
		boolean conflicts=false;
		if(overwrite){
			replaceData(img);
			LOGGER.info("[DATA] -- replace IMAGE data");
		}else
			try {
				completeData(img);
				LOGGER.info("[DATA] -- complete IMAGE data");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return conflicts;
	}

	private void replaceData(Image i)
	{
		if(i!=null){
			element=copyImage(i);
		}
	}

	private void completeData(Image i) throws Exception
	{
		//copy input fields
		Image copyIn=null;
		if(element!=null){
			copyIn=copyImage(element);
		}

		replaceData(i);

		// set input field values again
		if(copyIn!=null){
			String name=copyIn.getName();
			String desc=copyIn.getDescription();
			PositiveInteger dimX=copyIn.getPixels().getSizeX();
			PositiveInteger dimY=copyIn.getPixels().getSizeY();
			PositiveInteger dimZ=copyIn.getPixels().getSizeZ();
			PositiveInteger dimT=copyIn.getPixels().getSizeT();
			PositiveInteger dimC=copyIn.getPixels().getSizeC();
			PixelType type=copyIn.getPixels().getType();
			Time timeInc=copyIn.getPixels().getTimeIncrement();
			Timestamp stamp=copyIn.getAcquisitionDate();
			Length pixelSizeX=copyIn.getPixels().getPhysicalSizeX();
			Length pixelSizeY=copyIn.getPixels().getPhysicalSizeY();
			//		//TODO wellNr,expRef
			StageLabel stageLabel=copyIn.getStageLabel();
			Pixels p=element.getPixels();

			if(name!=null && !name.equals("")) element.setName(name);
			if(desc!=null && !desc.equals("")) element.setDescription(desc);
			if(dimX!=null && !dimX.toString().equals("")) p.setSizeX(dimX);
			if(dimY!=null && !dimY.toString().equals("")) p.setSizeX(dimY);
			if(dimZ!=null && !dimZ.toString().equals("")) p.setSizeX(dimZ);
			if(dimT!=null && !dimT.toString().equals("")) p.setSizeX(dimT);
			if(dimC!=null && !dimC.toString().equals("")) p.setSizeX(dimC);
			if(type!=null && !type.toString().equals("")) p.setType(type);
			//TODO test ifEmpty
			if(timeInc!=null) p.setTimeIncrement(timeInc);
			if(stamp!=null) element.setAcquisitionDate(stamp);
			if(pixelSizeX!=null) p.setPhysicalSizeX(pixelSizeX);
			if(pixelSizeY!=null) p.setPhysicalSizeY(pixelSizeY);
			if(stageLabel!=null) element.setStageLabel(stageLabel);


		}
	}

	public Image getImage() {
		return element;
	}

	/**
	 * Update data model for given modified tags.
	 * @param changesImage list of modified tags
	 * @throws Exception
	 */
	public void update(List<TagData> changesImage) throws Exception 
	{
		if(changesImage==null){
			MonitorAndDebug.printConsole("\t no changes for image");
			return;
		}
		for(TagData t: changesImage){
			updateTag(t.getTagName(),t.getTagValue(),t.getTagUnit());
			if(t.getTagUnit()!=null)
				map.put(t.getTagName(), t.getTagValue()+" "+t.getTagUnit().getSymbol());
			else
				map.put(t.getTagName(), t.getTagValue());
		}
	}

	/**
	 * Update tag of this model with given value!="" and unit.
	 * @param tagName
	 * @param value
	 * @param tagUnit
	 * @throws Exception
	 */
	private void updateTag(String tagName, String value, Unit tagUnit) throws Exception 
	{
		// no delete of value possible?
		if(value.equals(""))
			return;
		
		
		switch (tagName) {
		case TagNames.IMG_NAME:
			element.setName(value);
			break;
		case TagNames.IMG_DESC:
			element.setDescription(value);
			break;
		case TagNames.ACQTIME:
			element.setAcquisitionDate(Timestamp.valueOf(value));
			break;
		case TagNames.DIMXY:
			String[] dimXY=parseArrayString(value,2);
			if(dimXY==null || dimXY.length<2){
				LOGGER.warn("WARNING: ImageModel::updateTag(): can't parse dimXY");
				return;
			}
			element.getPixels().setSizeX(PositiveInteger.valueOf(dimXY[0]));
			element.getPixels().setSizeY(PositiveInteger.valueOf(dimXY[1]));
			break;
		case TagNames.PIXELTYPE:
			element.getPixels().setType(PixelType.fromString(value));
			break;
		case TagNames.PIXELSIZE:
			String[] sizeXY=parseArrayString(value,2);
			if(sizeXY==null || sizeXY.length<2){
				LOGGER.warn("WARNING: ImageModel::updateTag(): can't parse sizeXY");
				return;
			}
			element.getPixels().setPhysicalSizeX(ModuleViewer.parseToLength(sizeXY[0], tagUnit, true));
			element.getPixels().setPhysicalSizeY(ModuleViewer.parseToLength(sizeXY[1], tagUnit, true));
			break;
		case TagNames.DIMZTC:
			String[] dimZTC=parseArrayString(value,3);
			if(dimZTC==null || dimZTC.length<3){
				LOGGER.warn("WARNING:  ImageModel::updateTag(): can't parse dimZTC");
				return;
			}
			element.getPixels().setSizeZ(PositiveInteger.valueOf(dimZTC[0]));
			element.getPixels().setSizeT(PositiveInteger.valueOf(dimZTC[1]));
			element.getPixels().setSizeC(PositiveInteger.valueOf(dimZTC[2]));
			break;
		case TagNames.STAGELABEL:
			String[] stagePos=parseArrayString(value,2);
			if(stagePos==null || stagePos.length<2){
				LOGGER.warn("WARNING: ImageModel::updateTag(): can't parse stage pos");
				return;
			}
			element.getStageLabel().setX(ModuleViewer.parseToLength(stagePos[0],tagUnit, false));
			element.getStageLabel().setY(ModuleViewer.parseToLength(stagePos[1],tagUnit, false));
			break;
		case TagNames.STEPSIZE:
			//TODO:
//			setStepSize(null, prop);
			break;
		case TagNames.TIMEINC:
			element.getPixels().setTimeIncrement(new Time(Double.valueOf(value),tagUnit));
			break;
		case TagNames.WELLNR:
			//TODO:
//			setWellNr(null, prop);
			break;
		default:
			LOGGER.warn("[CONF] unknown tag: "+tagName );break;
		}
		
	}

	public static String[] parseArrayString(String value, int i) 
	{
		String delims="[,]";
		String[] splitting =value.split(delims);	
	
		return splitting;
	}

	public void printValues()
	{

		if(element!=null){
			MonitorAndDebug.printConsole("\t...image model pixel depth = "+(element.getPixels()!=null ? element.getPixels().getType(): ""));
			MonitorAndDebug.printConsole("\t...image model stageX = "+(element.getStageLabel()!=null ? element.getStageLabel().getX(): ""));
			MonitorAndDebug.printConsole("\t...image model stageY = "+(element.getStageLabel()!=null ? element.getStageLabel().getY(): ""));
		}
	}

	/**
	 * The copy constructor of image doesn't make a deep copy , so i implement my own deep copy function
	 * @param orig
	 * @return
	 */
	private Image copyImage(Image orig)
	{
		Image result=new Image();
		
		result.setID(orig.getID());
		result.setName(orig.getName());
		result.setDescription(orig.getDescription());
		result.setAcquisitionDate(orig.getAcquisitionDate());
		result.setDescription(orig.getDescription());
		
		if(orig.getLinkedExperimenter()!=null)
			result.linkExperimenter(new Experimenter(orig.getLinkedExperimenter()));
		if(orig.getLinkedExperiment()!=null)
			result.linkExperiment(new Experiment(orig.getLinkedExperiment()));
		if(orig.getLinkedExperimenterGroup()!=null)
			result.linkExperimenterGroup(new ExperimenterGroup(orig.getLinkedExperimenterGroup()));
		if(orig.getPixels()!=null)
			result.setPixels(new Pixels(orig.getPixels()));
		if(orig.getLinkedInstrument()!=null)
			result.linkInstrument(new Instrument(orig.getLinkedInstrument()));
		if(orig.getObjectiveSettings()!=null)
			result.setObjectiveSettings(new ObjectiveSettings(orig.getObjectiveSettings()));
		if(orig.getImagingEnvironment()!=null)
			result.setImagingEnvironment(new ImagingEnvironment(orig.getImagingEnvironment()));
		if(orig.getStageLabel()!=null)
			result.setStageLabel(new StageLabel(orig.getStageLabel()));

		for(ROI roi:orig.copyLinkedROIList()){
			result.linkROI(new ROI(roi));
		}
		
		for(MicrobeamManipulation mb:orig.copyLinkedMicrobeamManipulationList())
			result.linkMicrobeamManipulation(new MicrobeamManipulation(mb));
		
		for(Annotation annot:orig.copyLinkedAnnotationList())
			result.linkAnnotation(annot);
		
		for(Dataset data:orig.copyLinkedDatasetList())
			result.linkDataset(new Dataset(data));
		
		for(WellSample well:orig.copyLinkedWellSampleList())
			result.linkWellSample(new WellSample(well));
		
		return result;
	}
	

}
