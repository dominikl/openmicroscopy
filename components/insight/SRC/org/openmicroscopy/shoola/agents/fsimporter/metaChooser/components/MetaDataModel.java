package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import loci.formats.MetadataTools;

import ome.xml.model.Detector;
import ome.xml.model.Dichroic;
import ome.xml.model.Filter;
import ome.xml.model.FilterSet;
import ome.xml.model.Image;
import ome.xml.model.ImagingEnvironment;
import ome.xml.model.Instrument;
import ome.xml.model.LightPath;
import ome.xml.model.LightSource;
import ome.xml.model.LightSourceSettings;
import ome.xml.model.OME;
import ome.xml.model.Objective;
import ome.xml.model.ObjectiveSettings;
import ome.xml.model.Pixels;
import ome.xml.model.enums.FilterType;
import omero.gateway.model.MapAnnotationData;
import omero.model.MapAnnotation;
import omero.model.MapAnnotationI;
import omero.model.NamedValue;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.format.Sample;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules.ElementsCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model.ChannelModel;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model.DetectorModel;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model.ExperimentModel;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model.ImageEnvModel;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model.ImageModel;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model.LightPathModel;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model.LightSourceModel;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model.ObjectiveModel;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model.SampleModel;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model.xml.Channel;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model.xml.DetectorSettings;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.TagNames;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.microscope.MetaDataUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.MapAnnotationObject;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagData;
import org.openmicroscopy.shoola.util.MonitorAndDebug;
import org.slf4j.LoggerFactory;

/**
 * Works for xsi:schemaLocation="http://www.openmicroscopy.org/Schemas/OME/2015-01 
 * @author Susanne Kunis &nbsp;&nbsp;&nbsp;&nbsp; <a
 *         href="mailto:susannekunis@gmail.com">susannekunis@gmail.com</a>
 *
 */
public class MetaDataModel 
{
	
	/** Logger for this class. */
	 private static final org.slf4j.Logger LOGGER =
	    	    LoggerFactory.getLogger(MetaDataModel.class);
	
	/** TODO: module for planeSlider */
	private ElementsCompUI planeSliderUI;
	
	
	/** list of filter data  of current image*/
	private List<Filter> filterList;
	/** list of all dichroics for current image*/
	private List<Dichroic> dichroicList;
	/** list of all lightSrc for current image */
	private List<LightSource> lightSrcOrigList;
	
	
	
	//-----------------------------------------
	/** module for image data */ 
	private ImageModel imgModel;
	/** module for experiment data */
	private ExperimentModel expModel;
	/** module for objective data */
	private ObjectiveModel objModel;
	/** module for imgEnv data */
	private ImageEnvModel imgEnvModel;
	/** module for sample data */
	private SampleModel sampleModel;
	/** module for lightPath data link to channel of current image. List index of element== channelIndex*/
	private LightPathModel lightPathModel;
	/** module for channel data*/
	private ChannelModel channelModel;
	/** module for detector data link to channel of current image */
	private DetectorModel detectorModel;
	/** module for lightsource data link to channel of current image */
	private LightSourceModel lightSrcModel;
	
	private List<TagData> changesImg;
	//------------------------------------------------
	
	
	private Image imageOME;
	private int imageIndex;
	
	private OME ome;

	private List<TagData> changesImgEnv;

	private List<TagData> changesObj;

	private List<List<TagData>> changesDetector;

	private List<List<TagData>> changesLightSrc;

	private List<TagData> changesSample;
	private List<TagData> changesExperiment;

	private List<List<TagData>> changesChannel;

	private List<LightPath> changesLightPath;

	
	private List<Detector> availableDetectors;
	
	private List<LightSource> availableLightSources;
	
	private List<Objective> availableObjectives;
	
	private List<Object> availableLightPathElems;

	private List<FilterSet> filterSetList;

	
	public MetaDataModel()
	{
		imageIndex=-1;
		
		channelModel=null;
		
		lightSrcModel=null;
		
		lightPathModel=null;
		
		
		imgModel=new ImageModel();
		
	}

	
	
	public void resetData()
	{
		MonitorAndDebug.printConsole("#MetaDataModel::resetData()");
		expModel=null;
		sampleModel=null;
		objModel=null;
		imgEnvModel=null;
		imgModel=null;
		
		lightSrcModel=null;
		detectorModel=null;
		lightPathModel=null;
		
	}
	
	
	
	public void setOME(OME o)
	{
		LOGGER.info("[DEBUG] set ome");
		ome=o;
	}
	
	public OME getOME()
	{
		return ome;
	}
	public void setImageOMEData(Image i)
	{
		LOGGER.info("[DEBUG] set image ome");
		imageOME=i;
	}
	
	
	public int getPixelsDimZ()
	{
		return imageOME!=null ? imageOME.getPixels().getSizeZ().getValue() : 0;
	}
	
	public int getPixelsDimC()
	{
		return imageOME!=null ? imageOME.getPixels().getSizeC().getValue() : 0;
	}
	public int getPixelsDimT()
	{
		return imageOME!=null ? imageOME.getPixels().getSizeT().getValue() : 0;
	}
	
	//----------------------------------------------

	
	public SampleModel getSampleModel() 
	{
		if(sampleModel==null)
			sampleModel=new SampleModel();
		return sampleModel;
	}
	
	
	public Sample getSample() throws Exception
	{
		if(sampleModel==null) {
			MonitorAndDebug.printConsole("MetaDataModel::getSampleData - No Sample Data available");
			return null;
		}
		return sampleModel.getSample();
	}
	
	public void addData(Sample s, boolean overwrite) 
	{
		MonitorAndDebug.printConsole("# MetaDataModel::addData() - Sample ");
		if(sampleModel==null)
			sampleModel=new SampleModel();
		
		sampleModel.addData(s, overwrite);
	}
	

	
	//-------------------------------------
	public ExperimentModel getExperimentModel()
	{
		if(expModel==null)
			expModel=new ExperimentModel();
		return expModel;
	}
	
	public void addData(ExperimentModel e, boolean overwrite) throws Exception 
	{
		
		if(expModel==null)
			expModel=new ExperimentModel();
		
		expModel.addData(e.getExperiment(), overwrite);
		expModel.addData(e.getExperimenter(), overwrite);	
		
		if(overwrite){
			expModel.setProjectPartner(e.getProjectPartnerName());
		}else{
			if(expModel.getProjectPartnerName()==null || expModel.getProjectPartnerName().equals(""))
				expModel.setProjectPartner(e.getProjectPartnerName());
		}
	}
	/**
	 * Set group, project name and experimenter list data
	 * @param expCont
	 */
	public void setExtendedData(ExperimentModel expCont) 
	{
		MonitorAndDebug.printConsole("# MetaDataModel::setExtendedData - Experiment");
		if(expModel==null)
			expModel=new ExperimentModel();
		
		expModel.setGroupName(expCont.getGroupName());
		expModel.setProjectName(expCont.getProjectName());
		expModel.setExperimenter(expCont.getExperimenter());
	}
	
	/*-------------------------------------------
	 * Image
	 -------------------------------------------*/
	
	
	public ImageModel getImageModel() 
	{
		if(imgModel==null)
			imgModel=new ImageModel();
		return imgModel;
	}
	
	public void addData(Image i,boolean overwrite)
	{
		MonitorAndDebug.printConsole("# MetaDataModel::addData - Image");
		if(imgModel==null)
			imgModel=new ImageModel();
		imgModel.addData(i, overwrite);
	}

	/**
	 * Get image modul data
	 * @return
	 * @throws Exception
	 */
	public Image getImageData() throws Exception
	{
		if(imgModel==null) {
			MonitorAndDebug.printConsole("MetaDataModel::getImageData - No Image Data available");
			return null;
		}
		return imgModel.getImage();
	}

	
	


	/*-------------------------------------------
	 * Channel
	 -------------------------------------------*/
	
	
	public void addData(Channel c,boolean overwrite,int index)
	{
		MonitorAndDebug.printConsole("# MetaDataModel::addData - Channel "+index+", overwrite= "+overwrite);
		if(channelModel==null)
			channelModel=new ChannelModel();
		
		channelModel.addData(c, overwrite, index);
	}
	
	public int getNumberOfChannels()
	{
		if(channelModel==null)
			return 0;
		return channelModel.getNumberOfChannels();
	}
	
	public Channel getChannelData(int index) throws Exception
	{
		if(channelModel==null || getNumberOfChannels()==0 )
			return null;

		return  channelModel.getChannel(index); 
	}
	
	public ChannelModel getChannelModel() 
	{
		if(channelModel==null )
			channelModel=new ChannelModel();
		
		return channelModel;
	}
	
	
	/*------------------------------------------------------
	 * Detector
	 -------------------------------------------------------*/
	public void addData(Detector d, boolean overwrite, int index) throws Exception 
	{
		MonitorAndDebug.printConsole("# MetaDataModel::addData - Detector "+index);
		if(detectorModel==null)
			detectorModel=new DetectorModel();
		detectorModel.addData(d, overwrite,index);
	}



	public void addData(DetectorSettings ds, boolean overwrite, int index) throws Exception 
	{
		MonitorAndDebug.printConsole("# MetaDataModel::addData - DetectorSettings "+index);
		if(detectorModel==null)
			detectorModel=new DetectorModel();
		detectorModel.addData(ds, overwrite,index);
	}



	public void addToDetectorList(List<Detector> micDetectorList, boolean append) 
	{
		if(micDetectorList==null || micDetectorList.size()==0)
			return;
		
		if(availableDetectors==null){
			availableDetectors=new ArrayList<Detector>();
		}else if(!append)
			availableDetectors.clear();
			
		for(int i=0; i<micDetectorList.size(); i++){
			availableDetectors.add(micDetectorList.get(i));
	}
	
	}
	
	public DetectorModel getDetectorModel() 
	{
		if(detectorModel==null ){
			detectorModel=new DetectorModel();
		}
		return 	detectorModel;
	}
	
	
	public int getNumberOfDetectors()
	{
		if(detectorModel==null)
			return 0;
		return detectorModel.getNumberOfElements();
	}
	
	public Detector getDetector(int index) throws Exception
	{
		Detector res=null;


		if(detectorModel==null || getNumberOfDetectors()==0 ){
			return res;
		}
		res=detectorModel.getDetector(index);
		DetectorSettings dSett=getDetectorSettings(index);
		// check if this detector is linked to channel
		if(res!=null && !detectorIsLinkedToChannel(dSett, res.getID())){
			LOGGER.info("[DEBUG] detector is not linked");
			if(imageOME!=null){
				if(imageOME.getLinkedInstrument()==null ){
					createAndLinkNewInstrument(ome);
				}
				linkDetector(dSett,res,imageIndex,imageOME.getLinkedInstrument().sizeOfDetectorList());
			}else{
				LOGGER.info("[DEBUG] can't link detector. ");
			}
		}
		return res;
	}
	
	
	private void linkDetector(DetectorSettings dSett, Detector d,
			int imgIdx, int sizeOfDetectorList) 
	{
		if(d==null)
			return;
		else{
			if(d.getID()==null || d.getID().equals("")){
				d.setID(MetadataTools.createLSID("Detector", imgIdx,sizeOfDetectorList));
			}
			
			// new objectiveSettings for this image
			if(dSett==null){
				LOGGER.info("[DEBUG] no detector settings available for current channel");
				dSett=new DetectorSettings();
			}
			dSett.setID(d.getID());
			// link to given object
			LOGGER.info("[DEBUG] link to detector id = "+d.getID());
			dSett.setDetector(d);
		}
	}

	private boolean detectorIsLinkedToChannel(DetectorSettings sett,String id) 
	{
		boolean res=false;
		if(sett!=null && sett.getDetector()!=null && sett.getDetector().getID()!=null && sett.getDetector().getID().equals(id))
			res=true;
		
		return res;
	}
	
	public DetectorSettings getDetectorSettings(int index) throws Exception
	{
		if(detectorModel==null)
			return null;
		
		return detectorModel.getSettings(index);
	}
	

	
	/*--------------------------------------------
	 * LightPath
	 *--------------------------------------------*/
	public void addData(LightPath lp, boolean overwrite, int i) throws Exception 
	{
		MonitorAndDebug.printConsole("# MetaDataModel::addData - Filter "+i+", overwrite= "+overwrite);
//		MetaDataUI.printLightPath(lp);
		if(lightPathModel==null)
			lightPathModel=new LightPathModel();
		
		lightPathModel.addData(lp, overwrite, i);
	}
	
	public void update(List<LightPath> lp) throws Exception
	{
		MonitorAndDebug.printConsole("# MetaDataModel::update - Filter ");
		if(lightPathModel==null)
			lightPathModel=new LightPathModel();
		
		lightPathModel.update(lp);
	}


	public LightPath getLightPath(int index) throws Exception
	{
		if(lightPathModel==null || getNumberOfLightPath()<index){
			LOGGER.info("No lightPath available for channel "+index);
			MonitorAndDebug.printConsole("No lightPath available for channel "+index);
			return null;
		}
		MonitorAndDebug.printConsole("MetaDataModel::getLightPath("+index+") - available");
		return lightPathModel.getLightPath(index);
	}
	

	public LightPathModel getLightPathModel()
	{
		if(lightPathModel==null )
			lightPathModel=new LightPathModel();
		return lightPathModel;
	}
	
	public int getNumberOfLightPath() {
		if(lightPathModel==null)
			return 0;
		return lightPathModel.getNumberOfLightPaths();
	}
	
	
	/*--------------------------------------------
	 * LightSource
	 *--------------------------------------------*/
	public void addData(LightSource l, boolean overwrite, int i) throws Exception 
	{
		MonitorAndDebug.printConsole("# MetaDataModel::addData - LightSource "+i);
		if(lightSrcModel==null)
			lightSrcModel=new LightSourceModel();
		
		lightSrcModel.addData(l, overwrite, i);
	}

	public void addData(LightSourceSettings ls, boolean overwrite, int i) throws Exception {
		if(lightSrcModel==null)
			lightSrcModel=new LightSourceModel();
		
		lightSrcModel.addData(ls, overwrite, i);		
	}

	public void addToLightSrcList(List<LightSource> micLightSrcList, boolean append) 
	{
		if(micLightSrcList==null || micLightSrcList.size()==0)
			return;
		
		if(availableLightSources==null){
			availableLightSources=new ArrayList<LightSource>();
		}else if(!append)
			availableLightSources.clear();
		
		for(int i=0; i<micLightSrcList.size(); i++){
			availableLightSources.add(micLightSrcList.get(i));
	}
	}
	
	public LightSourceModel getLightSourceModel()
	{
		if(lightSrcModel==null)
			lightSrcModel=new LightSourceModel();
		
		return lightSrcModel;
	}
	
	
	
	public int getNumberOfLightSrc()
	{
		if(lightSrcModel==null)
			return 0;
		return lightSrcModel.getNumberOfLightSrc();
	}

	public LightSource getLightSourceData(int index) throws Exception
	{
		LightSource res=null;
		if(lightSrcModel==null || getNumberOfLightSrc()==0){
			return res;
		}
		res=lightSrcModel.getLightSource(index);
		LightSourceSettings lSett=getLightSourceSettings(index);
		// check if this detector is linked to channel
		if(res!=null && !lightSrcIsLinkedToChannel(lSett, res.getID())){
			LOGGER.info("[DEBUG] lightSrc is not linked");
			if(imageOME!=null){
				if(imageOME.getLinkedInstrument()==null ){
					createAndLinkNewInstrument(ome);
				}
				linkLightSrc(lSett,res,imageIndex,imageOME.getLinkedInstrument().sizeOfLightSourceList());
			}else{
				LOGGER.info("[DEBUG] can't link lightSrc. ");
			}
		}
		return res;
	}
	
	private void linkLightSrc(LightSourceSettings dSett, LightSource d,
			int imgIdx, int sizeOfList) 
	{
		if(d==null)
			return;
		else{
			if(d.getID()==null || d.getID().equals("")){
				d.setID(MetadataTools.createLSID("LightSource", imgIdx,sizeOfList));
			}
			
			// new objectiveSettings for this image
			if(dSett==null){
				LOGGER.info("[DEBUG] no lightSrc settings available for current channel");
				dSett=new LightSourceSettings();
			}
			dSett.setID(d.getID());
			// link to given object
			LOGGER.info("[DEBUG] link to lightSrc id = "+d.getID());
			dSett.setLightSource(d);
		}
	}

	private boolean lightSrcIsLinkedToChannel(LightSourceSettings sett,String id) 
	{
		boolean res=false;
		if(sett!=null && sett.getLightSource()!=null && sett.getLightSource().getID()!=null && sett.getLightSource().getID().equals(id))
			res=true;
		
		return res;
	}
	
	public LightSourceSettings getLightSourceSettings(int index) throws Exception
	{
		if(lightSrcModel==null ||getNumberOfLightSrc()==0){
			return null;
		}
		return lightSrcModel.getSettings(index);
	}
	
	
	
	public ImageEnvModel getImgEnvModel() 
	{
		if(imgEnvModel==null)
			imgEnvModel=new ImageEnvModel();
		return imgEnvModel;
	}
	
	public void addData(ImagingEnvironment i,boolean overwrite)
	{
		if(imgEnvModel==null)
			imgEnvModel=new ImageEnvModel();
		
		imgEnvModel.addData(i, overwrite);
	}
	
	public ImagingEnvironment getImagingEnv() throws Exception
	{
		if(imgEnvModel==null) {
			MonitorAndDebug.printConsole("MetaDataModel::getImgEnvData - No ImgEnv Data available");
			return null;
		}

		return imgEnvModel.getImgEnv();
	}
	
	
	
	public void createAndLinkNewInstrument(OME o)
	{
		 Instrument i=new Instrument();
		 i.setID(MetadataTools.createLSID("Instrument", o.sizeOfInstrumentList()));
		 imageOME.linkInstrument(i);
		 o.addInstrument(i);
		 LOGGER.info("[DEBUG] create new Instrument : "+i.getID());
	}
	
	
	/*--------------------------------------------
	 * Objective
	 *--------------------------------------------*/
	
	public void addData(Objective o,boolean overwrite) throws Exception
	{
		MonitorAndDebug.printConsole("# MetaDataModel::addData() - Objective");
		if(objModel==null)
			objModel=new ObjectiveModel();
		
		objModel.addData(o, overwrite);
	}

	public void addData(ObjectiveSettings os, boolean overwrite) throws Exception 
	{
		MonitorAndDebug.printConsole("# MetaDataModel::addData() - ObjectiveSettings");
		if(objModel==null)
			objModel=new ObjectiveModel();
		objModel.addData(os, overwrite);		
	}
	
	public ObjectiveModel getObjectiveModel()
	{
		if(objModel==null)
			objModel=new ObjectiveModel();
		return objModel;
	}
	
	public ObjectiveSettings getObjectiveSettings() throws Exception
	{
		if(objModel==null)
			return null;
		return objModel.getSettings();
	}
	
	public void addToObjList(List<Objective> list, boolean append) 
	{
		if(list==null || list.size()==0)
			return;
		
		if(availableObjectives==null){
			availableObjectives=new ArrayList<Objective>();
		}else if(!append)
			availableObjectives.clear();
		
		for(int i=0; i<list.size(); i++){
			availableObjectives.add(list.get(i));
	}
	}
	/**
	 * Get objective modul data, if exists else null.
	 * @return
	 * @throws Exception
	 */
	public Objective getObjectiveData() throws Exception
	{
		if(objModel==null) {
			MonitorAndDebug.printConsole("MetaDataModel::getObjectiveData - No Objective Data available");
			return null;
		}
		
		Objective obj=objModel.getObjective();
		ObjectiveSettings oSett=getObjectiveSettings();
		
		// check if this objective is linked to image
		if(obj!=null && !objectiveIsLinkedToImage(oSett,obj.getID())){
			LOGGER.info("[DEBUG] objective is not linked");
			if(imageOME!=null){
				if(imageOME.getLinkedInstrument()==null ){
					createAndLinkNewInstrument(ome);
				}
				linkObjective(oSett,obj,imageIndex,imageOME.getLinkedInstrument().sizeOfObjectiveList());
			}else{
				LOGGER.info("[DEBUG] can't link objective. ");
			}
		}
		
		return obj;
	}

	private void linkObjective(ObjectiveSettings oSett,
			Objective o, int imgIdx, int sizeOfObjectiveList) 
	{
		if(o==null)
			return;
		else{
			if(o.getID()==null || o.getID().equals("")){
				o.setID(MetadataTools.createLSID("Objective", imgIdx,sizeOfObjectiveList));
			}
			
			// new objectiveSettings for this image
			if(oSett==null){
				LOGGER.info("[DEBUG] no objective settings available for current image");
				oSett=new ObjectiveSettings();
			}
			oSett.setID(o.getID());
			// link to given object
			LOGGER.info("[DEBUG] link to objective id = "+o.getID());
			oSett.setObjective(o);
		}
	}

	private boolean objectiveIsLinkedToImage(ObjectiveSettings sett,String id) 
	{
		boolean res=false;
		if(sett!=null && sett.getObjective()!=null &&  sett.getObjective().getID()!=null && sett.getObjective().getID().equals(id))
			res=true;
		
		return res;
	}

	
	
	public void setFilterList(List<Filter> list)
	{
		filterList=list;
	}
	
	public void setDichroicList(List<Dichroic> list)
	{
		dichroicList=list;
	}
	

	public void setFilterSetList(List<FilterSet> list) {
		filterSetList=list;
	}
	
	
	
	private void printFilter(String s,Filter f)
	{
		try{
		MonitorAndDebug.printConsole(s+" Filter "+f.getID()!=null ? f.getID() : "");
		MonitorAndDebug.printConsole("Model "+f.getModel()!=null ? f.getModel() : "");
		MonitorAndDebug.printConsole("Type "+f.getType()!=null ? f.getType().toString() : "");
		}catch(Exception e){}
	}
	
	
	public List<Filter> getFilterList()
	{
		return filterList;
	}
	
	public List<Dichroic> getDichroicList()
	{
		return dichroicList;
	}
	
	public List<Object> getFilterAndDichroics()
	{
		List<Object> o=new ArrayList<Object>();
		if(filterList!=null){
			for(Filter f:filterList){
				o.add(f);
			}
		}
		if(dichroicList!=null){
			for(Dichroic d: dichroicList){
				o.add(d);
			}
		}
		return o;
	}
	
	
	

	public int getImageIndex() {
		return imageIndex;
	}
	
	public void setImageIndex(int idx){
		imageIndex=idx;
	}

		
	
	public static final Dichroic convertFilterToDichroic(Filter f)
	{
		Dichroic d=new Dichroic();
		d.setID(f.getID());
		d.setModel(f.getModel());
		d.setManufacturer(f.getManufacturer());
		d.setLotNumber(f.getLotNumber());
		d.setSerialNumber(f.getSerialNumber());
		d.setInstrument(f.getInstrument());
		//TODO
//		d.setLinkedAnnotation(index, o)
//		d.setLinkedFilterSet(index, o)
//		d.setLinkedLightPath(index, o)
		return d;
	}
	
	public static final Filter convertDichroicToFilter(Dichroic d)
	{
		Filter f=new Filter();
		f.setID(d.getID());
		f.setModel(d.getModel());
		f.setManufacturer(d.getManufacturer());
		f.setLotNumber(d.getLotNumber());
		f.setSerialNumber(d.getSerialNumber());
		f.setInstrument(d.getInstrument());
		f.setType(FilterType.DICHROIC);
		
		return f;
	}
	

	/**
	 * Save list of new modified tags for image module in MetaDataModel
	 * @param list
	 */
	public void setChangesImage(List<TagData> list)
	{
		changesImg=list;
	}
	
	public List<TagData> getChangesImage()
	{
		return changesImg;
	}


/**
 * Adapt number of channel, detector, lightSrc and lightPath, check lightSrc types 
 * and update data
 * @param metaDataModel
 * @throws Exception
 */
	public void updateData(MetaDataModel metaDataModel) throws Exception 
	{
		if(metaDataModel==null)
			return;
		
		if(imageOME==null){
			//update all modules
			MonitorAndDebug.printConsole("# MetaDataModel::updateData() -- DIR");
			// adapt number of channels, detectors, lightPath and LightSrc
			
			//channels
			int childNumberOfObjects=this.getNumberOfChannels();
			int parentNumberOfObjects=metaDataModel.getNumberOfChannels();
			if(childNumberOfObjects < parentNumberOfObjects && 
					metaDataModel.getChangesChannel()!=null){
				for(int i=childNumberOfObjects;i<parentNumberOfObjects;i++){
					this.addData(metaDataModel.getChannelData(i), true, i);
					//this channel doesn't have to update, because it was added directly
					metaDataModel.getChangesChannel().set(i, null);
				}
			}
			else if(childNumberOfObjects > parentNumberOfObjects){
				//something deleted
				for(int i=parentNumberOfObjects; i<childNumberOfObjects;i++){
					this.getChannelModel().remove(i);
				}
			}
			
			//detector
			childNumberOfObjects=this.getNumberOfDetectors();
			parentNumberOfObjects=metaDataModel.getNumberOfDetectors();
			if(childNumberOfObjects < parentNumberOfObjects && 
					metaDataModel.getChangesDetector()!=null){
				for(int i=childNumberOfObjects;i<parentNumberOfObjects;i++){
					this.addData(metaDataModel.getDetector(i), true, i);
					this.addData(metaDataModel.getDetectorSettings(i),true,i);
					//this detector doesn't have to update, because it was added directly
					metaDataModel.getChangesDetector().set(i, null);
				}
			}else if(childNumberOfObjects > parentNumberOfObjects){
				//something deleted
				for(int i=parentNumberOfObjects; i<childNumberOfObjects;i++){
					this.getDetectorModel().remove(i);
				}
			}
			
			//lightSrc
			childNumberOfObjects=this.getNumberOfLightSrc();
			parentNumberOfObjects=metaDataModel.getNumberOfLightSrc();
			if(metaDataModel.getChangesLightSrc()!=null){
				//check first if available lightSrc of child the type has changed
				for(int i=0; i<childNumberOfObjects;i++){
					List<TagData> changes=metaDataModel.getChangesLightSrc().get(i);
					String type=changes.get(changes.size()-1).getTagValue();
					if(!this.getLightSourceModel().getLightSource(i).getClass().getSimpleName().equals(type)){
						this.addData(metaDataModel.getLightSourceData(i), true, i);
					}
				}
			}
			
			if(childNumberOfObjects < parentNumberOfObjects && 
					metaDataModel.getChangesLightSrc()!=null){
				for(int i=childNumberOfObjects;i<parentNumberOfObjects;i++){
					this.addData(metaDataModel.getLightSourceData(i), true, i);
					this.addData(metaDataModel.getLightSourceSettings(i), true, i);
					//this lightsrc doesn't have to update, because it was added directly
					metaDataModel.getChangesLightSrc().set(i, null);
				}
			}else if(childNumberOfObjects > parentNumberOfObjects){
				//something deleted
				for(int i=parentNumberOfObjects; i<childNumberOfObjects;i++){
					this.getLightSourceModel().remove(i);
				}
			}
			
			//lightPath
			childNumberOfObjects=this.getNumberOfLightPath();
			parentNumberOfObjects=metaDataModel.getNumberOfLightPath();
			if(childNumberOfObjects < parentNumberOfObjects && 
					metaDataModel.getChangesLightPath()!=null){
				for(int i=childNumberOfObjects;i<parentNumberOfObjects;i++){
					this.addData(metaDataModel.getLightPath(i), true, i);
					//this lightpath doesn't have to update, because it was added directly
					metaDataModel.getChangesLightPath().set(i, null);
				}
			}else if(childNumberOfObjects > parentNumberOfObjects){
				//something deleted
				for(int i=parentNumberOfObjects; i<childNumberOfObjects;i++){
					this.getLightPathModel().remove(i);
				}
			}
			
			
			updateDir(metaDataModel,0);
		}else{
			MonitorAndDebug.printConsole("# MetaDataModel::updateData() -- FILE");
			updateFile(metaDataModel,0);
		}
		
		
	}
	
	/**
	 * adopt changes of parent data. Inherit changes to inherit again to child
	 * @param metaDataModel
	 * @param index
	 * @throws Exception
	 */
	private void updateDir(MetaDataModel metaDataModel,int index) throws Exception
	{
		if(imgModel!=null){
			MonitorAndDebug.printConsole("# MetaDataModel::update(): image");
			if(metaDataModel.getChangesImage()!=null){
				//update model because no viewer exists
				imgModel.update(metaDataModel.getChangesImage());
				this.setChangesImage(new ArrayList<>(metaDataModel.getChangesImage()));
			}
		}
		if(imgEnvModel!=null){
			MonitorAndDebug.printConsole("# MetaDataModel::update(): imageEnv");
			if(metaDataModel.getChangesImgEnv()!=null){
				imgEnvModel.update(metaDataModel.getChangesImgEnv());
				this.setChangesImageEnv(new ArrayList<>(metaDataModel.getChangesImgEnv()));
			}
		}
		if(channelModel!=null){
			MonitorAndDebug.printConsole("# MetaDataModel::update(): channel");
			if(metaDataModel.getChangesChannel()!=null){
				channelModel.update(metaDataModel.getChangesChannel()); 
				this.setChangesChannel(metaDataModel.getChangesChannel());
			}
		}
		if(objModel!=null){
			MonitorAndDebug.printConsole("# MetaDataModel::update(): objective");
			if(metaDataModel.getChangesObject()!=null){
				objModel.update(metaDataModel.getChangesObject()); 
				this.setChangesObject(metaDataModel.getChangesObject());
			}
		}
		if(detectorModel!=null){
			MonitorAndDebug.printConsole("# MetaDataModel::update(): detector");
			if(metaDataModel.getChangesDetector()!=null){
				detectorModel.update(metaDataModel.getChangesDetector());
				this.setChangesDetector(metaDataModel.getChangesDetector());
			}
		}

		if(lightSrcModel!=null){
			MonitorAndDebug.printConsole("# MetaDataModel::update(): lightSrc");
			if(metaDataModel.getChangesLightSrc()!=null){
				lightSrcModel.update(metaDataModel.getChangesLightSrc());
				this.setChangesLightSrc(metaDataModel.getChangesLightSrc());
			}
		}

		if(lightPathModel!=null){
			MonitorAndDebug.printConsole("# MetaDataModel::update(): lightPath");
			if(metaDataModel.getChangesLightPath()!=null){
				lightPathModel.update(metaDataModel.getChangesLightPath());
				this.setChangesLightPath(metaDataModel.getChangesLightPath());
			}
		}

		if(sampleModel!=null){
			MonitorAndDebug.printConsole("# MetaDataModel::update(): sample");
			if(metaDataModel.getChangesSample()!=null){
				sampleModel.update(metaDataModel.getChangesSample());
				this.setChangesSample(metaDataModel.getChangesSample());
			}
		}
		if(expModel!=null){
			MonitorAndDebug.printConsole("# MetaDataModel::update(): experimenter");
			if(metaDataModel.getChangesExperiment()!=null){
				expModel.update(metaDataModel.getChangesExperiment());
				this.setChangesExperiment(metaDataModel.getChangesExperiment());
			}
		}

	}
	
	
	/**
	 * adopt changes of parent data. 
	 * @param metaDataModel
	 * @param index
	 * @throws Exception
	 */
	private void updateFile(MetaDataModel metaDataModel,int index) throws Exception
	{
		if(imgModel!=null){
			MonitorAndDebug.printConsole("# MetaDataModel::update(): image");
			imgModel.update(metaDataModel.getChangesImage());
		}
		if(imgEnvModel!=null){
			MonitorAndDebug.printConsole("# MetaDataModel::update(): imageEnv");
			imgEnvModel.update(metaDataModel.getChangesImgEnv());
		}
		if(channelModel!=null){
			MonitorAndDebug.printConsole("# MetaDataModel::update(): channel");
			channelModel.update(metaDataModel.getChangesChannel()); 
		}
		if(objModel!=null){
			MonitorAndDebug.printConsole("# MetaDataModel::update(): objective");
			objModel.update(metaDataModel.getChangesObject()); 
		}
		if(detectorModel!=null){
			MonitorAndDebug.printConsole("# MetaDataModel::update(): detector");
			detectorModel.update(metaDataModel.getChangesDetector());
		}

		if(lightSrcModel!=null){
			MonitorAndDebug.printConsole("# MetaDataModel::update(): lightSrc");
			lightSrcModel.update(metaDataModel.getChangesLightSrc());
		}
		
		if(lightPathModel!=null){
			MonitorAndDebug.printConsole("# MetaDataModel::update(): lightPath");
			lightPathModel.update(metaDataModel.getChangesLightPath());
		}
		
		if(sampleModel!=null){
			MonitorAndDebug.printConsole("# MetaDataModel::update(): sample");
			sampleModel.update(metaDataModel.getChangesSample());
		}
		if(expModel!=null){
			MonitorAndDebug.printConsole("# MetaDataModel::update(): experimenter");
			expModel.update(metaDataModel.getChangesExperiment());
		}
		
	}

	/**
	 * Clear list of changes if status=false
	 * @param changes
	 */
	public void setDataChange(boolean status)
	{
		if(!status){
			// reset state
			changesImg=null;
			changesImgEnv=null;
			changesObj=null;
			changesDetector=null;
			changesLightSrc=null;
			changesLightPath=null;
			changesChannel=null;
			changesSample=null;
			changesExperiment=null;
		}
	}
	
	public boolean noticUserInput()
	{
	
		boolean res=false;
		res=res || (changesImg!=null && !changesImg.isEmpty())
				|| (changesImgEnv!=null && !changesImgEnv.isEmpty())
				|| (changesObj!=null && !changesObj.isEmpty())
				|| (changesDetector!=null && !changesDetector.isEmpty())
				|| (changesLightSrc!=null && !changesLightSrc.isEmpty())
				|| (changesLightPath!=null && !changesLightPath.isEmpty())
				|| (changesChannel!=null && !changesChannel.isEmpty())
				|| (changesSample!=null && !changesSample.isEmpty())
				|| (changesExperiment!=null && !changesExperiment.isEmpty());
		
//		MonitorAndDebug.printConsole("\t...Changes Image: "+(changesImg!=null && !changesImg.isEmpty()));
//		MonitorAndDebug.printConsole("\t...Changes Objective: "+(changesObj!=null && !changesObj.isEmpty()));
//		MonitorAndDebug.printConsole("\t...Changes Detector: "+(changesDetector!=null && !changesDetector.isEmpty()));
//		MonitorAndDebug.printConsole("\t...Changes LightSrc: "+(changesLightSrc!=null && !changesLightSrc.isEmpty()));
//		MonitorAndDebug.printConsole("\t...Changes LightPath: "+(changesLightPath!=null && !changesLightPath.isEmpty()));
//		MonitorAndDebug.printConsole("\t...Changes Channel: "+(changesChannel!=null && !changesChannel.isEmpty()));
//		MonitorAndDebug.printConsole("\t...Changes Sample: "+(changesSample!=null && !changesSample.isEmpty()));
//		MonitorAndDebug.printConsole("\t...Changes Experiment: "+(changesExperiment!=null && !changesExperiment.isEmpty()));
//		MonitorAndDebug.printConsole("\t...Changes ImgEnv: "+(changesImgEnv!=null && !changesImgEnv.isEmpty()));
		MonitorAndDebug.printConsole("# MetaDataModel::noticeUserInput(): "+res);
		return res;
	}

	public void setChangesImageEnv(List<TagData> newValue) {
		changesImgEnv=newValue;
	}
	public List<TagData> getChangesImgEnv()
	{
		return changesImgEnv;
	}

	public void setChangesObject(List<TagData> newValue) {
		changesObj=newValue;
	}
	public List<TagData> getChangesObject()
	{
		return changesObj;
	}

	public List<List<TagData>> getChangesChannel()
	{
		return changesChannel;
	}
	
	
	public void setChangesChannel(List<List<TagData>> list)
	{
		changesChannel=list;
	}
	public void setChangesChannel(List<TagData> newValue,int index)
	{
		if(changesChannel==null)
			changesChannel=new ArrayList<List<TagData>>();
		
		if(index>=changesChannel.size()){
			//expand list
			while(changesChannel.size()<=index)
				changesChannel.add(null);
		}
		changesChannel.set(index, newValue);
	}
	
	public void setChangesDetector(List<TagData> newValue,int index) 
	{
		if(changesDetector==null)
			changesDetector=new ArrayList<List<TagData>>();
		if(index>=changesDetector.size()){
			while(changesDetector.size()<=index)
				changesDetector.add(null);
		}
		changesDetector.set(index, newValue);		
	}
	public List<List<TagData>> getChangesDetector()
	{
		return changesDetector;
	}

	public void setChangesLightSrc(List<TagData> newValue,int index) {
		if(changesLightSrc==null)
			changesLightSrc=new ArrayList<List<TagData>>();
		
		if(index>=changesLightSrc.size()){
			//expand list
			while(changesLightSrc.size()<=index)
				changesLightSrc.add(null);
		}
		changesLightSrc.set(index, newValue);	
	}
	public List<List<TagData>> getChangesLightSrc()
	{
		return changesLightSrc;
	}
	
	public void setChangesLightPath(LightPath lightPath,int index) 
	{
		if(changesLightPath==null)
			changesLightPath=new ArrayList<LightPath>();
		
		if(index>=changesLightPath.size()){
			//expand list
			while(changesLightPath.size()<=index)
				changesLightPath.add(null);
		}
		changesLightPath.set(index, lightPath);
	}
	public List<LightPath> getChangesLightPath()
	{
		return changesLightPath;
	}
	
	private void setChangesLightPath(List<LightPath> list) {
		changesLightPath=list;
	}



	private void setChangesLightSrc(List<List<TagData>> list) {
		changesLightSrc=list;
	}



	private void setChangesDetector(List<List<TagData>> list) {
		changesDetector=list;
		
	}


	public void setChangesSample(List<TagData> newValue) {
		changesSample=newValue;
	}
	public List<TagData> getChangesSample()
	{
		return changesSample;
	}

	public void setChangesExperiment(List<TagData> newValue) {
		changesExperiment=newValue;
	}
	public List<TagData> getChangesExperiment()
	{
		return changesExperiment;
	}
	

	
	
	
	public List<Detector> getAvailableDetectorsImgData(){
		return availableDetectors;
	}



	public List<LightSource> getLightSrcHardwareList() {
		return availableLightSources;
	}
	


	public List<Object> getHardwareList_LightPath() {
		return availableLightPathElems;
	}

	

	public List<Objective> getObjList() {
		return availableObjectives;
	}

	public void addToLightPathList_FilterSet(List<FilterSet> filterSets, boolean append)
	{
		if(filterSets==null || filterSets.size()==0)
			return;

		if(availableLightPathElems==null){
			availableLightPathElems=new ArrayList<Object>();
		}else if(!append)
			availableLightPathElems.clear();

		for(int i=0; i<filterSets.size(); i++){
			availableLightPathElems.add(filterSets.get(i));
		}	
	}


	public void addToLightPathList_Filter(List<Filter> filters, boolean append) {
		if(filters==null || filters.size()==0)
			return;

		if(availableLightPathElems==null){
			availableLightPathElems=new ArrayList<Object>();
		}else if(!append)
			availableLightPathElems.clear();

		for(int i=0; i<filters.size(); i++){
			availableLightPathElems.add(filters.get(i));
		}	
	
	}



	public void addToLightPathList_Dichroic(List<Dichroic> dichroics, boolean append) {
		if(dichroics==null || dichroics.size()==0)
			return;
	
		if(availableLightPathElems==null){
			availableLightPathElems=new ArrayList<Object>();
		}else if(!append)
			availableLightPathElems.clear();

		for(int i=0; i<dichroics.size(); i++){
			availableLightPathElems.add(dichroics.get(i));
		}	
	
	}

	
	public HashMap<String,String> getMapAnnotationDetector(int index)
	{
		if(detectorModel==null)
			return null;
		else
			return detectorModel.getMap(index);
}

	/**
	 * Add or append [name,value] to map of changes in imageModel
	 * @param name
	 * @param value
	 */
	public void addToMapAnnotationImage(String name, String value) {
		if(imgModel==null){
			imgModel=new ImageModel();
		}
		HashMap<String, String> map=getMapAnnotationImage();
		if(map==null){
			map= new HashMap<String, String>();
		}
		map.put(name, value);
		imgModel.setMap(map);
		
	}

	
	public void addToMapAnnotationLightPath(String name, String value,int index) {
		if(lightPathModel==null){
			lightPathModel=new LightPathModel();
		}
		HashMap<String, String> map=getMapAnnotationLightPath(index);
		if(map==null){
			map= new HashMap<String, String>();
		}
		map.put(name, value);
		lightPathModel.setMap(map,index);
		
	}
	
	
	
	/**
	 * Add or append [name,value] to map of changes in detectorModel
	 * @param name
	 * @param value
	 * @param index
	 */
	public void addToMapAnnotationDetector(String name,String value,int index){
		if(detectorModel==null){
			detectorModel=new DetectorModel();
		}
		HashMap<String, String> map=getMapAnnotationDetector(index);
		if(map==null){
			map= new HashMap<String, String>();
		}
		map.put(name, value);
		detectorModel.setMap(map, index);
	}
	
	
	public void setMapAnnotationDetector(HashMap<String, String> mapValuesOfChanges, int index, boolean clone) 
	{
		if(detectorModel==null)
			detectorModel=new DetectorModel();
		if(mapValuesOfChanges!=null){
			if(clone)
				detectorModel.setMap((HashMap<String, String>) mapValuesOfChanges.clone(),index);
			else
				detectorModel.setMap(mapValuesOfChanges,index);
		}
	}
	

	
	public HashMap<String,String> getMapAnnotationLightPath(int index) {
		if(lightPathModel==null)
			return null;
		else
			return lightPathModel.getMap(index);
	}



	public void setMapAnnotationLightPath(HashMap<String, String> mapValuesOfChanges, int index, boolean clone) 
	{
		if(lightPathModel==null)
			lightPathModel=new LightPathModel();
		if(mapValuesOfChanges!=null){
			MonitorAndDebug.printConsole("# MetaDataModel::setMapAnnotationLightPath() - available, clone= "+clone);	
			if(clone)
				lightPathModel.setMap((HashMap<String, String>) mapValuesOfChanges.clone(),index);
			else
				lightPathModel.setMap(mapValuesOfChanges,index);
		}
	}
	
	
	


	public void setMapAnnotationLightSrc(HashMap<String, String> mapValuesOfChanges, int index, boolean clone) 
	{
		if(lightSrcModel==null)
			lightSrcModel=new LightSourceModel();
		if(mapValuesOfChanges!=null){
			if(clone)
				lightSrcModel.setMap((HashMap<String, String>) mapValuesOfChanges.clone(),index);
			else
				lightSrcModel.setMap(mapValuesOfChanges,index);
		}
	}



	public HashMap<String, String> getMapAnnotationLightSrc(int index) {
		if(lightSrcModel==null)
			return null;
		else
			return lightSrcModel.getMap(index);
	}



	public HashMap<String, String> getMapAnnotationChannel(int index) {
		if(channelModel==null)
			return null;
		else
			return channelModel.getMap(index);
	}



	public void setMapAnnotationChannel(HashMap<String, String> mapValuesOfChanges, int index, boolean clone) {
		if(channelModel==null)
			channelModel=new ChannelModel();
		if(mapValuesOfChanges!=null){
			if(clone)
				channelModel.setMap((HashMap<String, String>) mapValuesOfChanges.clone(),index);
			else
				channelModel.setMap(mapValuesOfChanges,index);
		}
	}



	public HashMap<String, String> getMapAnnotationObjective() {
		if(objModel==null)
			return null;
		return objModel.getMap();
	}



	public void setMapAnnotationObjective(HashMap<String, String> mapValuesOfChanges, boolean clone) {
		if(objModel==null)
			objModel=new ObjectiveModel();
		if(mapValuesOfChanges!=null){
			if(clone)
				objModel.setMap((HashMap<String, String>) mapValuesOfChanges.clone());
			else
				objModel.setMap(mapValuesOfChanges);
		}
	}



	public HashMap<String, String> getMapAnnotationSample() {
		if(sampleModel==null)
			return null;
		return sampleModel.getMap();
	}



	public void setMapAnnotationSample(HashMap<String, String> mapValuesOfChanges, boolean clone) {
		if(sampleModel==null)
			sampleModel=new SampleModel();
		if(mapValuesOfChanges!=null){
			if(clone)
				sampleModel.setMap((HashMap<String, String>) mapValuesOfChanges.clone());
			else
				sampleModel.setMap(mapValuesOfChanges);
		}
	}



	public HashMap<String, String> getMapAnnotationExperiment() {
		if(expModel==null)
			return null;
		return expModel.getMap();
	}



	public void setMapAnnotationExperiment(HashMap<String, String> mapValuesOfChanges, boolean clone) {
		if(expModel==null)
			expModel=new ExperimentModel();
		if(mapValuesOfChanges!=null){
			if(clone)
				expModel.setMap((HashMap<String, String>) mapValuesOfChanges.clone());
			else
				expModel.setMap(mapValuesOfChanges);
		}
		
	}


	/**
	 * 
	 * @return map of changes [tagName,tagValue]
	 */
	public HashMap<String, String> getMapAnnotationImage() {
		if(imgModel==null)
			return null;
		return imgModel.getMap();
	}


	/**
	 * Save map of changes to image model.
	 * @param mapValuesOfChanges map of changes [tagName,tagValue]
	 * @param clone
	 */
	public void setMapAnnotationImage(HashMap<String, String> mapValuesOfChanges, boolean clone) {
		if(imgModel==null)
			imgModel=new ImageModel();
		
		if(mapValuesOfChanges!=null){
			if(clone)
				imgModel.setMap((HashMap<String, String>) mapValuesOfChanges.clone());
			else
				imgModel.setMap(mapValuesOfChanges);
		}
	}
	
	public HashMap<String, String> getMapAnnotationImgEnv() {
		if(imgEnvModel==null)
			return null;
		return imgEnvModel.getMap();
	}

	public void setMapAnnotationImgEnv(HashMap<String, String> mapValuesOfChanges, boolean clone) {
		if(imgEnvModel==null)
			imgEnvModel=new ImageEnvModel();
		
		if(mapValuesOfChanges!=null){
			if(clone)
				imgEnvModel.setMap((HashMap<String, String>) mapValuesOfChanges.clone());
			else
		imgEnvModel.setMap(mapValuesOfChanges);
		}
	}

	
	public MapAnnotationData getAnnotation()
	{
		System.out.println("# MetaDataModel:: Collect all MapAnnotations");
		MapAnnotation ma = new MapAnnotationI();
		List<NamedValue> values = new ArrayList<NamedValue>();
		
		ma.setMapValue(values);
		
		if(getNumberOfChannels()>0){
			for(int i=0; i<getNumberOfChannels();i++){
				String id="[Channel]:";
				String id2="";
				if(channelModel!=null){
					if(channelModel.getChannel(i).getName()==null || channelModel.getChannel(i).getName().equals("")){
						id2="[Channel "+String.valueOf(i)+"]:";
					}else{
						id2="["+channelModel.getChannel(i).getName()+"]:";
					}
				}
				values=hashMapToValueList(getMapAnnotationChannel(i), values,id+id2);
				
				values=hashMapToValueList(getMapAnnotationDetector(i), values,id2+"[Detector]:");
				values=hashMapToValueList(getMapAnnotationLightPath(i), values,id2+"[Filter]:");
				values=hashMapToValueList(getMapAnnotationLightSrc(i), values,id2+"[LightSrc]:");
				System.out.println("# MetaDataModel:: Collect all MapAnnotations: Channel & Co: "+i);
			}
		}else{// no channel available, don't link to any channel
			String id="";
			
			for(int i=0;i<getNumberOfDetectors();i++)
				values=hashMapToValueList(getMapAnnotationDetector(i), values,id+"[Detector]:");

			for(int i=0;i<getNumberOfLightPath();i++)
				values=hashMapToValueList(getMapAnnotationLightPath(i), values,id+"[Filter]:");
			for(int i=0;i<getNumberOfLightSrc();i++)
				values=hashMapToValueList(getMapAnnotationLightSrc(i), values,id);
		}
		
		values=hashMapToValueList(getMapAnnotationObjective(), values,"[Objective]:");
		values=hashMapToValueList(getMapAnnotationSample(), values,"[Sample]:");
		values=hashMapToValueList(getMapAnnotationExperiment(), values,"[Experiment]:");
		if(imgModel!=null){
			String id="";
			String name=imgModel.getImage().getName();
			id="[Image]:";
			if(name!=null && !name.equals(""))
				id+="["+name+"]:";
			else if(imgModel.getImage().getID()!=null && !imgModel.getImage().getID().equals("")){
				id+="["+imgModel.getImage().getID()+"]:";
			}
			values=hashMapToValueList(getMapAnnotationImage(), values,id);
		}
		values=hashMapToValueList(getMapAnnotationImgEnv(),values,"[ImageEnv]:");
		MapAnnotationData result= new MapAnnotationData(ma);
		result.setDescription("Metadata UOS.importer");
		result.setNameSpace("Metadata UOS.importer");
		return new MapAnnotationData(ma);
	}
	
	private List<NamedValue> hashMapToValueList(HashMap<String,String> map,List<NamedValue> list,String id)
	{
		if(list==null)
			list = new ArrayList<NamedValue>();
		
		if(map!=null){
			for (Iterator i = map.entrySet().iterator(); i.hasNext(); ) {
				Map.Entry next = (Map.Entry)i.next();
				if(next.getValue()!=null)
					list.add(new NamedValue(id+next.getKey().toString(),next.getValue().toString()));
			}
		}
		return list;
	}

}
