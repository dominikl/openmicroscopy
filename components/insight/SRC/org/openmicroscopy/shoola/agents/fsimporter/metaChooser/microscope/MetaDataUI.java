package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.microscope;

import info.clearthought.layout.TableLayout;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ome.units.quantity.Time;
import ome.xml.model.Annotation;
import ome.xml.model.Detector;
import ome.xml.model.Dichroic;
import ome.xml.model.Experiment;
import ome.xml.model.Experimenter;
import ome.xml.model.Filter;
import ome.xml.model.FilterSet;
import ome.xml.model.Image;
import ome.xml.model.ImagingEnvironment;
import ome.xml.model.Instrument;
import ome.xml.model.LightPath;
import ome.xml.model.LightSource;
import ome.xml.model.LightSourceSettings;
import ome.xml.model.MapAnnotation;
import ome.xml.model.OME;
import ome.xml.model.Objective;
import ome.xml.model.ObjectiveSettings;
import ome.xml.model.Pixels;
import ome.xml.model.Plane;
import ome.xml.model.StageLabel;
import ome.xml.model.StructuredAnnotations;
import omero.gateway.model.MapAnnotationData;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.MetaDataDialog;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.MetaDataControl;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.MetaDataModel;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.format.Sample;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model.ExperimentModel;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model.ImageEnvModel;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model.LightPathModel;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model.xml.Channel;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model.xml.DetectorSettings;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.view.ChannelViewer;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.view.DetectorViewer;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.view.ExperimentViewer;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.view.ImageEnvViewer;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.view.ImageViewer;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.view.LightPathViewer;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.view.LightSourceViewer;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.view.ModuleViewer;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.view.ObjectiveViewer;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.view.SampleViewer;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.ModuleConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.TagNames;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.ExceptionDialog;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.ImportUserData;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.MapAnnotationObject;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagData;
import org.openmicroscopy.shoola.util.MonitorAndDebug;
//import org.slf4j.LoggerFactory;
import org.slf4j.LoggerFactory;


/**
* Works for xsi:schemaLocation="http://www.openmicroscopy.org/Schemas/OME/2015-01 
* @author Susanne Kunis &nbsp;&nbsp;&nbsp;&nbsp; <a
*         href="mailto:susannekunis@gmail.com">susannekunis@gmail.com</a>
*/
public class MetaDataUI extends JPanel
{
	public static enum GUIPlaceholder
	{
		Pos_A,
		Pos_B,
		Pos_C,
		Pos_D,
		Pos_E,
		Pos_F,
		Pos_G,
		Pos_H,
		Pos_Bottom
	}

	private MetaDataModel model;

	private MetaDataControl control;

	private ImportUserData importUserData;

	private OME ome;

	/** container for viewer **/
	private JTabbedPane experimentPane;
	protected JPanel lightSrcPane;
	protected JPanel detectorPane;
	protected JPanel lightPathPane;
	protected JPanel objectivePane;
	protected JTabbedPane channelTab;
	private JTabbedPane imagePane;//tabbed pane with one element
	private JTabbedPane samplePane;

	GridBagLayout gbl;

	/** module viewer */
	private ImageViewer imageUI;
	private ExperimentViewer experimentUI;
	private SampleViewer sampleUI;
	private ObjectiveViewer objectiveUI;
	private ImageEnvViewer imgEnvViewer;
	private LightSourceViewer lightSrcViewer;
	private LightPathViewer lightPathViewer;
	private ChannelViewer channelViewer;
	private DetectorViewer detectorViewer;



	/** true if module is visible*/
	private boolean initChannelUI;
	private boolean initImageUI;
	private boolean initLightPathUI;
	private boolean initLightSrcUI;
	private boolean initDetectorUI;
	private boolean initObjectiveUI;
	private boolean initImageEnvUI;
	private boolean initSampleUI;
	private boolean initExperimentUI;

	private boolean componentsInit;


	private boolean detectorInput;
	private boolean channelInput;
	private boolean lightPathInput;
	private boolean lightSrcInput;

	private File file;
	private String dataToSave_Desc;
	private CustomViewProperties customSett;
	private MetaDataDialog parent;

	/** Logger for this class. */
	private static final org.slf4j.Logger LOGGER =
			LoggerFactory.getLogger(MetaDataUI.class);

	public static final String CHANGE_IMGDATA = "changeImageData";

	public static final String CHANGE_EXPDATA = "changesExperimentData";

	public static final String CHANGE_SAMPLEDATA = "changesSampleData";

	public static final String CHANGE_OBJDATA = "changesObjectiveData";

	public static final String CHANGE_IMGENVDATA = "changesImgEnvData";

	public static final String CHANGE_DETDATA = "changesDetectorData";

	public static final String CHANGE_LPDATA = "changesLightPathData";

	public static final String CHANGE_LSDATA = "changesLightSrcData";

	private boolean directoryPane;

	private int numChannelTabs;

	private ChangeListener channelChangeListener;

	private int lastChannelSelectionIndex;

	private boolean predefinitionsLoaded;

	private boolean showPreValues; 

	private MicroscopeProperties mic;

	/**
	 * Constructor
	 * @param parent panel
	 * @param isdir =true if selected node is a directory, else false
	 * @param showPreValues TODO
	 */
	public MetaDataUI(MetaDataDialog parent,boolean isdir, boolean showPreValues)
	{
		MonitorAndDebug.printConsole("# MetaDataUI::new Instance : "+isdir+", "+showPreValues+", parent: "+
				(parent==null?"null":"available"));
		this.setBorder(BorderFactory.createEmptyBorder());
		this.parent=parent;
		customSett=((MetaDataDialog) parent).getCustomViewProperties();
		mic=parent.getMicroscopeProperties();

		directoryPane=isdir;
		this.showPreValues=showPreValues;
		resetInitialisation();

		// create model
		model=new MetaDataModel();
		initModelComponents();
		control=new MetaDataControl(model,this);
	}

	/**
	 * Constructor
	 * @param parent panel
	 * @param dir =true if selected node is a directory, else false
	 * @param model
	 * @param showPreValues TODO
	 */
	public MetaDataUI(MetaDataDialog parent,boolean dir,MetaDataModel model, boolean showPreValues)
	{
		MonitorAndDebug.printConsole("# MetaDataUI::new Instance : "+dir+", model,"+showPreValues+", parent: "+
				(parent==null?"null":"available"));
		this.setBorder(BorderFactory.createEmptyBorder());
		customSett=((MetaDataDialog) parent).getCustomViewProperties();
		directoryPane=dir;
		this.showPreValues=showPreValues;
		resetInitialisation();
		mic=parent.getMicroscopeProperties();

		// create model
		this.model=model;
		// ensure, that a model exists
		if(this.model==null)
			this.model=new MetaDataModel();

		initModelComponents();
		control=new MetaDataControl(model,this);
	}

	private void resetInitialisation()
	{
		componentsInit=false;

		initChannelUI=false;
		initImageUI=false;
		initLightPathUI=false;
		initLightSrcUI=false;
		initDetectorUI=false;
		initObjectiveUI=false;
		initImageEnvUI=false;
		initSampleUI=false;
		initExperimentUI=false;
	}

	public void resetCustomSettings(CustomViewProperties sett)
	{
		MonitorAndDebug.printConsole("# MetaDataUI::resetCustomSettings()");
		customSett=sett;
		initModelComponents();
	}

	/** init modul components respective to settings*/ 
	private void initModelComponents() 
	{
		if(!componentsInit){
			if(customSett.getObjConf()!=null && customSett.getObjConf().isVisible()){
				LOGGER.info("[GUI] -- init OBJECTIVE modul");
				model.addToObjList(customSett.getMicObjList(),false);
				initObjectiveUI=true;
			}
			if(customSett.getDetectorConf()!=null && customSett.getDetectorConf().isVisible()){
				LOGGER.info("[GUI] -- init DETECTOR modul");
				model.addToDetectorList(customSett.getMicDetectorList(), false);
				initDetectorUI=true;
				detectorInput=false;
			}
			if(customSett.getLightSrcConf()!=null && customSett.getLightSrcConf().isVisible()){
				LOGGER.info("[GUI] -- init LIGHTSRC modul");
				model.addToLightSrcList(customSett.getMicLightSrcList(), false);
				initLightSrcUI=true;
			}
			if(customSett.getChannelConf()!=null && customSett.getChannelConf().isVisible()){
				LOGGER.info("[GUI] -- init CHANNEL modul");
				initChannelUI=true;
			}
			if(customSett.getLightPathConf()!=null && customSett.getLightPathConf().isVisible()){
				LOGGER.info("[GUI] -- init LIGHTPATH modul");
				model.addToLightPathList_Filter(customSett.getMicLightPathFilterList(), false);
				initLightPathUI=true;
				lightPathInput=false;
			}
			if(customSett.getImgEnvConf()!=null && customSett.getImgEnvConf().isVisible()){
				LOGGER.info("[GUI] -- init IMAGEENV modul");
				initImageEnvUI=true;
			}
			if(customSett.getExpConf()!=null && customSett.getExpConf().isVisible()){
				LOGGER.info("[GUI] -- init EXPERIMENT modul");
				initExperimentUI=true;
			}
			if(customSett.getSampleConf()!=null && customSett.getSampleConf().isVisible()){
				LOGGER.info("[GUI] -- init SAMPLE modul");
				initSampleUI=true;
			}
			if(customSett.getImageConf()!=null && customSett.getImageConf().isVisible()){
				LOGGER.info("[GUI] -- init IMAGE modul");
				initImageUI=true;
			}

			componentsInit=true;
		}
	}

	/** return model object*/
	public MetaDataModel getModel() throws Exception
	{
		return model;
	}
	/** return model object*/
	public MetaDataModel getSavedModel() throws Exception
	{
		//read input data
		save();

		return model;
	}


	/**
	 * TODO
	 * add mapr values for specific microscopes
	 * @throws Exception
	 */
	public void addMapr() throws Exception
	{
		MonitorAndDebug.printConsole("#### MetaDataUI::addMapr(): add channelname - lightPath mapr#####");
		switch(customSett.getMicName()){
		case MicroscopeProperties.TIRF4LINE_SMT:
			HashMap mapr=customSett.getMapr();
			if(mapr!=null){
				MonitorAndDebug.printConsole("#### MetaDataUI::addMapr(): add channelname - lightPath mapr#####");
				for(int i=0; i<model.getNumberOfChannels();i++){
					String chName=model.getChannelData(i).getName();
					LightPath lp=(LightPath) mapr.get(chName);
					if(lp!=null) {
						addLightPathData(i, lp, null, true);
						model.getLightPathModel().setInput(true,i);
					}
				}
			}
			break;
		default:
			MonitorAndDebug.printConsole("# MetaDataUI::addMapr(): no mapr");
			break;

		}
	}

	/** add data from given parent model
	 * @throws Exception */
	public void addData(MetaDataModel parentModel) throws Exception
	{
		if(parentModel==null)
			return;

		if(model!=null){
			addExperimentData(parentModel.getExperimentModel(), true);

			addImageData(parentModel.getImageData(),true);
			addObjectiveData(parentModel.getObjectiveData(),parentModel.getObjectiveSettings(),true);
			addSampleData(parentModel.getSample(),true);
			addImageEnvData(parentModel.getImagingEnv(),true);
			//			addPlaneData();

			if(directoryPane){
				// inheritance for directory
				for(int i=0; i<parentModel.getNumberOfChannels();i++){
					addChannelData(i,parentModel.getChannelData(i),true);
				}
				for(int i=0; i<parentModel.getNumberOfDetectors();i++){
					addDetectorData(i,parentModel.getDetector(i),parentModel.getDetectorSettings(i),true);
				}
				for(int i=0; i<parentModel.getNumberOfLightSrc();i++){
					addLightSrcData(i,parentModel.getLightSourceData(i),parentModel.getLightSourceSettings(i),true);
				}
				for(int i=0; i<parentModel.getNumberOfLightPath();i++){
					addLightPathData(i,parentModel.getLightPath(i),/*parentModel.getFilterSet(i)*/null,true);
				}

			}else{
				//inheritance for file
				for(int i=0; i<parentModel.getNumberOfChannels();i++){
					addChannelData(i,parentModel.getChannelData(i),true);

				}
				for(int i=0; i<parentModel.getNumberOfDetectors();i++){
					addDetectorData(i, parentModel.getDetector(i),parentModel.getDetectorSettings(i), true);
				}

				for(int i=0; i<parentModel.getNumberOfLightSrc(); i++){
					addLightSrcData(i,parentModel.getLightSourceData(i),parentModel.getLightSourceSettings(i),true); 
				}
				for(int i=0; i<parentModel.getNumberOfLightPath();i++){
					addLightPathData(i,parentModel.getLightPath(i),/*parentModel.getFilterSet(i)*/null,true);
				}
			}

		}

	}


	private void addImageEnvData(ImagingEnvironment i, boolean overwrite)
	{
		if(i!=null){
			model.addData(i, overwrite);
		}
	}
	public void addSampleData(Sample s, boolean overwrite) 
	{
		if(s!=null){
			model.addData(s,overwrite);
		}
	}

	private void addObjectiveData(Objective o,ObjectiveSettings os, boolean overwrite) throws Exception 
	{
		if(o!=null) model.addData(o,overwrite);
		if(os!=null) model.addData(os, overwrite);
	}

	private void addLightSrcData(int i, LightSource ls,LightSourceSettings lss, boolean overwrite) throws Exception 
	{
		if(ls!=null) model.addData(ls, overwrite,i); 
		if(lss!=null) model.addData(lss,overwrite,i);
	}

	private void addChannelData(int index, Channel c, boolean overwrite) 
	{
		if(c!=null){
			model.addData(c,overwrite,index);
		}
	}

	/** add parent data
	 * @throws Exception */
	private void addDetectorData(int index,Detector d,DetectorSettings ds, boolean overwrite) throws Exception
	{
		if(d!=null){
			model.addData(d,overwrite,index);
		}
		if(ds!=null){
			model.addData(ds, overwrite,index);
		}
	}

	/**
	 * Add lightpath data to model
	 * @param i
	 * @param lightPath
	 * @param filterSet
	 * @param b
	 * @throws Exception
	 */
	private void addLightPathData(int i, LightPath lightPath, FilterSet filterSet, boolean b) throws Exception 
	{
		if(lightPath!=null){
			model.addData(lightPath, b,i);
		}
	}

	private void addImageData(Image i,boolean overwrite)  
	{
		if( i!=null){
			MonitorAndDebug.printConsole("# MetaDataModel::addImageData() - add Image ");	
			model.addData(i,overwrite);
		}
	}

	public void addExperimentData(ExperimentModel e,boolean overwrite) throws Exception
	{
		if(e!=null){
			MonitorAndDebug.printConsole("# MetaDataModel::addExperimentData() - add Experiment ");	
			model.addData(e, overwrite);
		}
	}


	/** linke image file */
	public void linkToFile(File file)
	{
		this.file=file;
	}

	/** read data from given metadata container 
	 * @param imageIndex TODO*/
	public void readData(OME o, Hashtable<String, Object> series,int imageIndex) throws Exception
	{
		if(o !=null)
		{		
			MonitorAndDebug.printConsole("# MetaDataUI::readFileData()...");
			ome=o;

			//TODO eigentlich imageList!!!!
			model.setOME(ome);
			model.setImageIndex(imageIndex);
			Image image=ome.getImage(imageIndex);

			if(image!=null){
				//TODO: jedes image hat sein eigenes Model
				model.setImageOMEData(image);

				List<Objective> objectives=null;
				List<Detector> detectors=null;
				List<LightSource> lightSources=null;
				List<Filter> filters=null;
				List<Dichroic> dichroics=null;
				List<FilterSet> filterSets=null;
				List<Channel> channels=null;
				List<Time> expTime=null;

				//TODO: no referenced instrument? -> createDummy
				Instrument instrument=image.getLinkedInstrument();
				if(instrument==null){
					LOGGER.warn("[DATA] NO INSTRUMENTS available, create new");
					MonitorAndDebug.printConsole("[DATA] NO INSTRUMENTS available, create new");
					model.createAndLinkNewInstrument(ome); 
				}else{
					objectives=instrument.copyObjectiveList();
					detectors=instrument.copyDetectorList();
					lightSources=instrument.copyLightSourceList();
					filters=instrument.copyFilterList();
					dichroics=instrument.copyDichroicList();
					filterSets=instrument.copyFilterSetList();
					List<Filter> filterList=instrument.copyFilterList();
					model.setFilterList(filterList);
					List<Dichroic> dichroicList=instrument.copyDichroicList();
					model.setDichroicList(dichroicList);
					model.setFilterSetList(filterSets);
				}
				StructuredAnnotations annot=ome.getStructuredAnnotations();

				//TODO: no Pixel data -> dummies ??
				Pixels pixels=image.getPixels();
				if(pixels==null){
					LOGGER.warn("[DATA] NO PIXEL object available");
					MonitorAndDebug.printConsole("[DATA] NO PIXEL object available");
				}else{
					List<ome.xml.model.Channel>ch=pixels.copyChannelList();
					// convert to uos channel type
					if(!ch.isEmpty())
						channels=new ArrayList<>();
					for(int c=0;c<ch.size(); c++ ){
						channels.add(new Channel(ch.get(c)));
					}
					// Java 8:
					//						channels=ch.stream().map(e -> (Channel) e).collect(Collectors.toList());
					expTime=getExposureTimes(pixels);
				}

				if(componentsInit){
					String subarray = getSubarray(series);
					readImageData(image,objectives,annot);
					readChannelData(channels,lightSources,detectors,filters,dichroics,expTime,subarray);

					readImageEnvData(image);
					readExperimentData(image);

					model.addToLightSrcList(lightSources,true);
					model.addToDetectorList(detectors,true);
					model.addToObjList(objectives,true);
					model.addToLightPathList_Filter(filters,true);
					model.addToLightPathList_Dichroic(dichroics,true);
					model.addToLightPathList_FilterSet(filterSets, true);
				}
				addMapr();
			}else{
				LOGGER.warn("[DATA] NO IMAGE object available in file");
				MonitorAndDebug.printConsole("[DATA] NO IMAGE object available in file");
			}
			MonitorAndDebug.printConsole("... end loadFileData()");

		}else{
			LOGGER.warn("[DATA] NOT available METADATA ");
			model.setImageOMEData(null);
		}

	}

	private String getSubarray(Hashtable<String, Object> series) {
		if(series==null)
			return null;
		if(series.containsKey("Detector Clipping #1"))
			return (String) series.get("Detector Clipping #1");
		else
			return null;
	}


	private List<Time> getExposureTimes(Pixels pixels){
		List<Time> exposureTimes=new ArrayList<>();
		List<Plane> planes = pixels.copyPlaneList();
		if(planes!=null && !planes.isEmpty()){
			for(int i=0; i<planes.size(); i++){
				if(planes.get(i).getExposureTime()!=null){
					exposureTimes.add(planes.get(i).getExposureTime());
				}
			}
		}
		return exposureTimes;
	}


	private void readImageEnvData(Image image) 
	{
		if(initImageEnvUI){
			ImagingEnvironment i=image.getImagingEnvironment();
			if(i!=null){
				model.addData(i,false);
			}
		}
	}


	// TODO: read project Partner from Map annotation
	private void readExperimentData(Image image) 
	{
		if(initExperimentUI)
		{
			// load image linked experiment and experimenter
			Experiment e=image.getLinkedExperiment();
			Experimenter exper=image.getLinkedExperimenter();
			ExperimentModel expModel = model.getExperimentModel();
			String pP=null;

			MapAnnotation map=getLinkedCellNanOsAnnotation(image, ome.getStructuredAnnotations());
			if(map!=null){
				pP=expModel.parseProjectPartner(map); 
			}

			ExperimentModel expCont=new ExperimentModel(e,exper,pP);

			try {
				model.addData(expCont, false);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	private void readChannelData(List<Channel> channels,
			List<LightSource> lightSources, List<Detector> detectors, 
			List<Filter> filters, List<Dichroic> dichroics,
			List<Time> exposureTimes,String subarray) 
	{
		if(initChannelUI)
		{
			for(int i=0; i<channels.size();i++){
				Channel ch =channels.get(i);
				if(ch!=null)
				{
					if(exposureTimes!=null && !exposureTimes.isEmpty()){
						if(i<exposureTimes.size()){
							ch.setDefaultExposureTime(exposureTimes.get(i));
						}else{
							ch.setDefaultExposureTime(exposureTimes.get(0));
						}
					}
					model.addData(ch, false,i);
					LOGGER.info("[DATA] -- load CHANNEL data "+ch.getName());

					try {
						readLightPathData(ch,i,filters,dichroics);
					} catch (Exception e2) {
						LOGGER.warn("Can't read lightpath data of channel "+i+"! "+e2);
						e2.printStackTrace();
					}
					try {
						readLightSource(ch,i,lightSources);
					} catch (Exception e1) {
						LOGGER.warn("Can't read lightSrc data of channel "+i+"! "+e1);
						e1.printStackTrace();
					}
					try {
						readDetectorData(ch,i,detectors,subarray);
					} catch (Exception e) {
						LOGGER.warn("Can't read detector data of channel "+i+"! "+e);
						e.printStackTrace();
					}
				}
			}
		}else{
			try {
				readLightPathData(channels.get(0),0,filters,dichroics);
			} catch (Exception e2) {
				LOGGER.warn("Can't read lightpath data of channel 0!"+e2);
				e2.printStackTrace();
			}
			try {
				readLightSource(channels.get(0),0,lightSources);
			} catch (Exception e1) {
				LOGGER.warn("Can't read lightSrc data of channel 0! "+e1);
				e1.printStackTrace();
			}
			try {
				readDetectorData(channels.get(0),0,detectors,subarray);
			} catch (Exception e) {
				LOGGER.warn("Can't read detector data of channel 0! "+e);
				e.printStackTrace();
			}
		}

	}


	private void readLightPathData(Channel channel, int i, List<Filter> filters, List<Dichroic> dichroics) throws Exception 
	{
		if(initLightPathUI){
			if((filters!=null && !filters.isEmpty()) || (dichroics!=null && !dichroics.isEmpty()))
			{
				boolean dataAvailable=false;
				String[] linkedObj=null;

				// get linked lightpath for current channel
				LightPath lp=channel.getLightPath();

				if(lp!=null &&(
						lp.sizeOfLinkedEmissionFilterList()!=0 || 
						lp.sizeOfLinkedExcitationFilterList()!=0 ||
						lp.getLinkedDichroic()!=null)){
					dataAvailable=true;
				}

				if(!dataAvailable){
					LOGGER.info("[DATA] -- LIGHTPATH  data not available");
				}else{
					model.addData(lp,false,i);
				}
			}else{
				LOGGER.info("[DATA] -- LIGHTPATH  data not available");
			}
		}
	}

	private void readLightSource(Channel channel, int i,
			List<LightSource> lightSources) throws Exception 
	{
		if(initLightSrcUI){

			if(	lightSources!=null && !lightSources.isEmpty())
			{
				boolean dataAvailable=false;
				String linkedObj=null;

				// get linked lightSrc for current channel
				LightSourceSettings ls=channel.getLightSourceSettings();
				LightSource l=null;

				// get linked lightSource from lightSrc list
				if(ls!=null){
					linkedObj=ls.getID();
					int idx=getLightSrcByID(lightSources, linkedObj);
					if(idx!=-1){
						l=lightSources.get(idx);
						dataAvailable=true;
					}
				}else{
					// if only one lightSrc in instruments available, show this
					if(lightSources.size()==1){
						l=lightSources.get(0);
						dataAvailable=true;
					}else{
						LOGGER.info("[DATA] -- more than one unlinked lightSrc available");
					}
				}

				// visualization data
				if(!dataAvailable){
					LOGGER.info("[DATA] -- LIGHTSOURCE  data not available");
				}else{
					model.addData(l,false,i);
					model.addData(ls,false,i);
				}
			}
			else{
				LOGGER.info("[DATA] -- LIGHTSOURCE  data not available");
			}

		}
	}

	private void readDetectorData(Channel channel, int i,
			List<Detector> detectors,String subarray) throws Exception 
	{
		if(initDetectorUI){
			if(detectors!=null && !detectors.isEmpty())
			{
				boolean dDataAvailable=false;
				String linkedDet=null;

				DetectorSettings ds=channel.getDetectorSettings()!=null? 
						new DetectorSettings(channel.getDetectorSettings()):null;
						Detector d=null;

						// get linked detector from list
						if(ds!=null){
							linkedDet=ds.getID();
							int idx=getDetectorByID(detectors, linkedDet);
							if(idx!=-1){
								d=detectors.get(idx);
								dDataAvailable=true;
							}
						}else{
							// if only one detector in instruments available, show this
							if(detectors.size()==1){
								d=detectors.get(0);
								ds=new DetectorSettings();
								dDataAvailable=true;
							}else{
								LOGGER.info("[DATA] -- more than one unlinked detectors available");
							}
						}


						if(!dDataAvailable){
							LOGGER.info("[DATA] -- DETECTOR data not available");
						}else{
							ds.setSubarray(subarray);
							model.addData(d,false,i);
							model.addData(ds,false,i);
							//save in each case to key-value pair: subarray 
							if(subarray!=null && !subarray.equals("")){
								model.addToMapAnnotationDetector(TagNames.SUBARRAY, subarray, i);
							}
						}
			}else{
				LOGGER.info("[DATA] -- DETECTOR data not available");
			}
		}
	}

	private void readImageData(Image image, List<Objective> objList, StructuredAnnotations annot) 
	{
		model.addData(image, false);
		//save in each case to key-value pair: subarray 
		if(image!=null){
			StageLabel label =image.getStageLabel();


			if(label!=null &&label.getX()!=null && label.getY()!=null) {
				model.addToMapAnnotationImage(TagNames.STAGELABEL,label.getX().value()+
						", "+label.getY().value()+" "+label.getX().unit().getSymbol() );

			}
		}
		try{
			readObjectiveData(image,objList);
		}catch(Exception e){
			LOGGER.warn("Can't read objective data! "+e);
		}
		readSampleData(image,annot);

	}

	private void readSampleData(Image image, StructuredAnnotations annot) 
	{
		if(initSampleUI && annot!=null){
			Sample s=getSampleAnnotation(image,annot);
			if(s!=null){
				model.addData(s, false);
			}else{
				LOGGER.info("[DATA] Sample data not available!");
			}
		}
	}

	/**
	 * Read sample data from *.ome file. Sample data should be stored as MapAnnotation of id <MAP_ANNOT_ID>
	 * and linked o current image.
	 * @param image
	 * @param annot
	 * @return
	 */
	private Sample getSampleAnnotation(Image image,StructuredAnnotations annot) 
	{
		MapAnnotation map=getLinkedCellNanOsAnnotation(image, annot);
		Sample s=null;
		if(map!=null)
			s= new Sample(map);

		return s;
	}

	private MapAnnotation getLinkedCellNanOsAnnotation(Image image, StructuredAnnotations annot)
	{
		MapAnnotation map =null;
		List<Annotation> list=image.copyLinkedAnnotationList();
		String sampleID=null;
		for(int i=0; i<list.size(); i++){
			if(image.getLinkedAnnotation(i).getID().contains(MetaDataDialog.MAP_ANNOT_ID))
				sampleID= image.getLinkedAnnotation(i).getID();
		}
		if(sampleID!=null){
			for(int i=0; i<annot.sizeOfMapAnnotationList();i++){
				if(sampleID.equals(annot.getMapAnnotation(i).getID())){
					map= annot.getMapAnnotation(i);
				}
			}
		}
		return map;
	}

	private void readObjectiveData(Image image, List<Objective> objList) throws Exception
	{

		if(initObjectiveUI && objList!=null && !objList.isEmpty())
		{
			boolean oDataAvailable=false;
			String linkedObj=null;


			ObjectiveSettings os=image.getObjectiveSettings();
			Objective o=null;

			// get linked object from objList
			if(os!=null){
				linkedObj=os.getID();
				int idx=getObjectiveByID(objList, linkedObj);
				if(idx!=-1){
					o=objList.get(idx);
					oDataAvailable=true;
				}
			}else{
				// if only one objective in instruments available, show this
				if(objList.size()==1){
					o=objList.get(0);
					oDataAvailable=true;
				}else{
					LOGGER.info("[DATA] -- more than one unlinked objectives available");
				}
			}

			if(!oDataAvailable){
				LOGGER.info("[DATA] -- file: OBJECTIVE data not available");
			}else{
				model.addData(o, false); 
				model.addData(os,false); 
			}
		}

	}

	/** set ImportUserData */
	public void setImportData(ImportUserData data)
	{
		if(data!=null){
			MonitorAndDebug.printConsole("# MetaDataUI::setImportData()");
			LOGGER.info("[DATA] -- add IMPORT USER data");
			importUserData=data;
			try {
				ExperimentModel expCont=new ExperimentModel();
				expCont.setExperimenter(importUserData.getUser());
				expCont.setGroupName(importUserData.getGroup());
				expCont.setProjectName(importUserData.getProject());
				model.setExtendedData(expCont);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}


	public void showData() throws Exception
	{
		initLayout();

		showExperimentData();
		showImageData();
		showChannelData();
		showSampleData();
		revalidate();
		repaint();
	}


	private void showExperimentData() {
		if(initExperimentUI){
			experimentPane=new JTabbedPane();
			ModuleConfiguration expModul=customSett.getExpConf();
			experimentUI=new ExperimentViewer(model.getExperimentModel(), expModul,showPreValues);

			JPanel all=new JPanel();
			all.setLayout(new BoxLayout(all,BoxLayout.Y_AXIS));
			all.add(experimentUI);

			ModuleConfiguration imgEnvModul=customSett.getImgEnvConf();
			if(imgEnvModul!=null){
				imgEnvViewer=new ImageEnvViewer(model.getImgEnvModel(), imgEnvModul,showPreValues);
				all.add(imgEnvViewer);
			}
			experimentPane.add("Experiment",all);
			addToPlaceholder(experimentPane,expModul.getPosition(), expModul.getWidth());
		}
	}



	private void showImageData() throws Exception
	{
		imagePane=new JTabbedPane();
		if(initImageUI){
			ModuleConfiguration imgModul =customSett.getImageConf();
			imageUI=new ImageViewer(model.getImageModel(), imgModul);
			imagePane.add("Image",imageUI);
			JComponent img=imagePane;

			addToPlaceholder(img, imgModul.getPosition(), imgModul.getWidth());
		}
		String name=model.getImageData()!=null ? model.getImageData().getName() : null;

		showObjectiveData(name);

	}

	private void showObjectiveData(String name) 
	{
		if(initObjectiveUI){
			objectivePane=new JPanel(new CardLayout());
			ModuleConfiguration objModul=customSett.getObjConf();
			objectiveUI=new ObjectiveViewer(model.getObjectiveModel(), objModul,showPreValues,model.getObjList(),mic);

			JPanel pane= control.createPropPane(objectiveUI, "Objective", "for image "+name);
			objectivePane.add(pane,name);
			addToPlaceholder(objectivePane, objModul.getPosition(), objModul.getWidth());
		}
	}

	private void showSampleData()
	{
		if(initSampleUI){
			samplePane=new JTabbedPane();
			ModuleConfiguration sampleModul=customSett.getSampleConf();
			sampleUI=new SampleViewer(model.getSampleModel(), sampleModul,showPreValues);
			samplePane.add("Sample",sampleUI);
			addToPlaceholder(samplePane, sampleModul.getPosition(), sampleModul.getWidth());
		}
	}

	private void showChannelData() throws Exception 
	{
		channelTab=new JTabbedPane();
		numChannelTabs=0;
		lastChannelSelectionIndex=0;
		channelChangeListener=new ChangeListener() {
			public void stateChanged(ChangeEvent changeEvent) {
				JTabbedPane sourceTabbedPane = (JTabbedPane) changeEvent.getSource();
				int index = sourceTabbedPane.getSelectedIndex();
				if(directoryPane && index==(numChannelTabs-1)){
					addNewChannelTab();
				}else{
					if(model.getNumberOfChannels()>0){
						if(index >=0){
							selectChannel(index);
						}
					}
				}
			}
		};


		if(initChannelUI){
			ModuleConfiguration chModul=customSett.getChannelConf();
			if(model.getNumberOfChannels()==0){
				channelTab.add(new ChannelViewer(model.getChannelModel(), chModul, 0),"Channel 0",numChannelTabs++);
			}else{
				for(int i=0; i<model.getNumberOfChannels();i++)
				{
					String name= model.getChannelData(i)!=null ? model.getChannelData(i).getName() : "Channel";
					if(directoryPane){
						name= (name==null || name.isEmpty()) ? "Channel "+String.valueOf(i) : name;
					}else{
						name= (name==null || name.isEmpty()) ? ((i==0 ? "Channel " : "# ")+String.valueOf(i)) : name;
					}
					channelTab.add(new ChannelViewer(model.getChannelModel(), chModul, i),name,numChannelTabs++);
				}
				// set last channel as removable
				if(directoryPane && numChannelTabs>2){
					channelTab.setTabComponentAt(numChannelTabs-2, new DemoCustomTab(channelTab));
				}
			}

			if(directoryPane){
				// add tab to add new tab when click
				channelTab.add(new JPanel(),"+",numChannelTabs++);
			}

			addToPlaceholder(channelTab,chModul.getPosition(), chModul.getWidth()); 
		}

		String chname= model.getChannelData(0)!=null ? model.getChannelData(0).getName() : "Channel 0";
		showLightSourceData(chname,0);
		showDetectorData(chname,0);
		showLightPathData(chname,0);

		channelTab.addChangeListener(channelChangeListener);
	}

	/**
	 * Add new ChannelTab for directory view. Add also new Detector, LightSrc and LightPath pane.
	 */
	private void addNewChannelTab()
	{
		MonitorAndDebug.printConsole("# MetaDataUI::addNewChannelTab()");
		int index = numChannelTabs - 1;
		if (channelTab.getSelectedIndex() == index) { /* if click new tab */
			// add new channel model
			model.getChannelModel().addData(null, true, index);
			channelTab.removeChangeListener(channelChangeListener);
			/* add new tab */
			channelTab.add(new ChannelViewer(model.getChannelModel(), customSett.getChannelConf(), index),
					"Channel " + String.valueOf(index), index);
			/* set last channel tab as custom tab and remove delete sign from tab before*/
			if(directoryPane){
				if(index>1){
					DemoCustomTab lastTab=(DemoCustomTab) channelTab.getComponentAt(index-1);
					if(lastTab!=null) lastTab.hideRemove();
				}
				channelTab.setTabComponentAt(index, new DemoCustomTab(channelTab));
			}

			selectChannel(index);
			//	        	channelTab.setSelectedIndex(index);
			channelTab.addChangeListener(channelChangeListener);
			numChannelTabs++;
		}
	}

	private void setLightPathVisible(String name, int index) 
	{
		if(lightPathPane==null){
			showLightPathData(name,index);
		}else{
			lightPathPane.removeAll();
			lightPathViewer=new LightPathViewer(model.getLightPathModel(),customSett.getLightPathConf(),
					index,model.getHardwareList_LightPath(),mic);
			lightPathPane.add(control.createPropPane(lightPathViewer, "Filter", "for "+name));
		}
	}

	private void setDetectorVisible(String name, int index)
	{
		if(detectorPane==null){
			MonitorAndDebug.printConsole("\t...Show Detector for Channel "+name);
			showDetectorData(name,index);
		}else{
			MonitorAndDebug.printConsole("\t...Set visible Detector for Channel "+name);
			detectorPane.removeAll();
			detectorViewer=new DetectorViewer(model.getDetectorModel(),customSett.getDetectorConf(),
					index,showPreValues,model.getAvailableDetectorsImgData(),mic);
			detectorPane.add(control.createPropPane(detectorViewer, "Detector", "for "+ name));
		}
	}

	private void setLightSrcVisible(String name,int index)
	{
		if(lightSrcPane==null){
			showLightSourceData(name,index);
		}else{
			lightSrcPane.removeAll();
			ModuleConfiguration lightSModul=customSett.getLightSrcConf();
			lightSrcViewer=new LightSourceViewer(model.getLightSourceModel(), lightSModul, 
					index,showPreValues,model.getLightSrcHardwareList(),mic);
			lightSrcPane.add(control.createPropPane(lightSrcViewer, "LightSource", "for "+name));
		}
	}

	private void showLightPathData(String name,int index)
	{
		if(initLightPathUI){
			lightPathPane=new JPanel(new CardLayout());
			ModuleConfiguration lightPModul=customSett.getLightPathConf();
			lightPathViewer=new LightPathViewer(model.getLightPathModel(), customSett.getLightPathConf(),
					index,model.getHardwareList_LightPath(),mic);
			lightPathPane.add(control.createPropPane(lightPathViewer, "Filter", "for "+name));
			addToPlaceholder(lightPathPane, lightPModul.getPosition(), lightPModul.getWidth());
		}
	}
	private void showLightSourceData(String name,int index) 
	{
		if(initLightSrcUI){
			lightSrcPane=new JPanel(new CardLayout());
			ModuleConfiguration lightSModul=customSett.getLightSrcConf();
			lightSrcViewer=new LightSourceViewer(model.getLightSourceModel(), customSett.getLightSrcConf(),
					index,showPreValues,model.getLightSrcHardwareList(),mic);
			lightSrcPane.add(control.createPropPane(lightSrcViewer, "LightSource", "for "+name));
			addToPlaceholder(lightSrcPane, lightSModul.getPosition(), lightSModul.getWidth());
		}
	}

	private void showDetectorData(String name,int index) 
	{
		if(initDetectorUI){
			detectorPane=new JPanel(new CardLayout());
			ModuleConfiguration detModul=customSett.getDetectorConf();
			detectorViewer=new DetectorViewer(model.getDetectorModel(),customSett.getDetectorConf(),
					index,showPreValues,model.getAvailableDetectorsImgData(),mic);
			detectorPane.add(control.createPropPane(detectorViewer, "Detector", "for "+ name));
			addToPlaceholder(detectorPane, detModul.getPosition(), detModul.getWidth());
		}
	}

	/**
	 * Shows/brings on top channel number chNr and his linked lightSrc, detector and lightPath.
	 * @param chNr
	 */
	protected void selectChannel(int chNr) 
	{	
		MonitorAndDebug.printConsole("# MetaDataUI::selectChannel() : old = "+lastChannelSelectionIndex+", new = "+chNr);
		if(model.getNumberOfChannels() <chNr )
			return;

		// something to save at channel?
		ChannelViewer lastSelection=null;
		if( channelTab.getComponentAt(lastChannelSelectionIndex) instanceof ChannelViewer){
			lastSelection=(ChannelViewer) channelTab.getComponentAt(lastChannelSelectionIndex);
		}
		if(lastSelection!=null && lastSelection.hasDataToSave()){
			List<TagData> list=lastSelection.getChangedTags();
			lastSelection.saveData();
			model.setChangesChannel(list, lastSelection.getIndex());
			model.setMapAnnotationChannel(lastSelection.getMapValuesOfChanges(model.getMapAnnotationChannel(lastSelection.getIndex())),lastSelection.getIndex(), true); 
		}

		channelTab.setSelectedIndex(chNr);
		String chName=channelTab.getTitleAt(chNr);

		LOGGER.info("[GUI-ACTION] -- select Channel "+chName);
		MonitorAndDebug.printConsole("\t...select Channel "+chName);

		// update submodules
		if(initLightSrcUI ){
			boolean input=lightSrcViewer.hasDataToSave();
			int elemIndex=lightSrcViewer.getIndex();
			if(input){
				lightSrcInput=true;
				List<TagData> list=lightSrcViewer.getChangedTags();
				lightSrcViewer.saveData();
				model.setChangesLightSrc(list,elemIndex);
				model.setMapAnnotationLightSrc(
						lightSrcViewer.getMapValuesOfChanges(model.getMapAnnotationLightSrc(elemIndex),lastSelection.getName()), elemIndex, true); 
			}
			// show referenced lightSrc + settings
			setLightSrcVisible(chName, chNr);
			MonitorAndDebug.printConsole("\t...select lightSrc "+chNr+" of "+model.getNumberOfLightSrc());
		}

		if(initDetectorUI ){
			boolean input=detectorViewer.hasDataToSave();
			if(input){
				detectorInput=true;
				int elemIndex=detectorViewer.getIndex();
				List<TagData> list=detectorViewer.getChangedTags();
				HashMap<String,String> map=model.getMapAnnotationDetector(elemIndex);
				detectorViewer.saveData();
				model.setChangesDetector(list,elemIndex);
				model.setMapAnnotationDetector(detectorViewer.getMapValuesOfChanges(map,lastSelection.getName()),elemIndex, true);
			}
			// show referenced detector + settings
			setDetectorVisible(chName, chNr);
			MonitorAndDebug.printConsole("\t...select detector "+chNr+" of "+model.getNumberOfDetectors());
		}

		if(initLightPathUI ){
			boolean input=lightPathViewer.hasDataToSave() ;
			if(input){
				//				lightPathInput=true;
				int index=lightPathViewer.getIndex();
				try {
					lightPathViewer.saveData();
					//printLightPath(model.getLightPath(index));
					model.setChangesLightPath(model.getLightPath(index),index);
					//overwrite the whole lightpath for this channel
					model.setMapAnnotationLightPath(lightPathViewer.getMapValuesOfChanges(null,index),index, true);


				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			//update lightPath
			setLightPathVisible(chName, chNr);
			MonitorAndDebug.printConsole("\t...select lightPath "+chNr+" of "+model.getNumberOfLightPath());
		}
		revalidate();
		repaint();

		lastChannelSelectionIndex=chNr;
	}


	private void initLayout()
	{
	
		double[][] layoutDesign=new double[][]{
			//X-achse
			{TableLayout.FILL},
			//Y-Achse
			//				{0.5,0.5}
			{TableLayout.FILL}
		};
		TableLayout layout=new TableLayout(layoutDesign);

		setLayout(layout);
	}


	static void addComponent( Container cont,
			GridBagLayout layout,
			JComponent c,
			int x, int y,
			int width, int height,
			double weightx, double weighty,int fill )
	{
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor=GridBagConstraints.NORTHWEST;
		gbc.fill = fill;
		gbc.gridx = x; gbc.gridy = y;
		gbc.gridwidth = width; gbc.gridheight = height;
		gbc.weightx = weightx; gbc.weighty = weighty;
		layout.setConstraints( c, gbc );
		cont.add( c );
	}


	private void addToPlaceholder(JComponent comp1,GUIPlaceholder place, int width )
	{
		JScrollPane comp=new JScrollPane(comp1);
		TableLayout layout=(TableLayout) getLayout();
		switch (place) {
		case Pos_A:
			add(comp,"0,0");
			break;
		case Pos_B:
			insertModule(0,1,comp);
			break;
		case Pos_C:
			insertModule(0,2,comp);
			break;
		case Pos_D:
			insertModule(0,3,comp);
			break;
			//----------------------------------------------
		case Pos_E:
			insertModule(1,0,comp);
			break;
		case Pos_F:
			insertModule(1,1,comp);
			break;
		case Pos_G:
			insertModule(1,2,comp);
			break;
		case Pos_H:
			insertModule(1,3,comp);
			break;
		default:
			LOGGER.error("[GUI] Unknown position for element");
			ExceptionDialog ld = new ExceptionDialog("Property File Error!", 
					"Use unknown position ["+place+"] in given property file.",
					this.getClass().getSimpleName());
			ld.setVisible(true);
			break;
		}
	}

	private void insertModule(int rowIdx, int columnIdx, JComponent comp)
	{
		LOGGER.info("Insert Module at col: "+columnIdx+", row: "+rowIdx);
		//		MonitorAndDebug.printConsole("Insert Module at col: "+columnIdx+", row: "+rowIdx);
		TableLayout layout=(TableLayout) getLayout();
		//expand rows?
		int numRow=layout.getNumRow();
		for(int i=numRow;i<rowIdx+1;i++ ){
			layout.insertRow(i, TableLayout.FILL);
		}

		int numCol=layout.getNumColumn();
		for(int i=numCol; i<columnIdx+1; i++){
			layout.insertColumn(i, TableLayout.FILL);
		}
		add(comp,columnIdx+","+rowIdx);
	}


	protected int getObjectiveByID(List<Objective> list,String id)
	{
		int result=-1;
		if(id==null || id.equals("") || list==null)
			return result;
		for(int i=0; i<list.size(); i++){
			if(list.get(i).getID().equals(id)){
				return i;
			}
		}
		return result;
	}

	protected int getLightSrcByID(List<LightSource> list,String id)
	{
		int result=-1;
		if(id==null || id.equals("") || list==null)
			return result;
		for(int i=0; i<list.size(); i++){
			if(list.get(i).getID().equals(id)){
				return i;
			}
		}
		return result;
	}
	protected int getDetectorByID(List<Detector> list,String id)
	{
		int result=-1;
		if(id==null || id.equals("") || list==null)
			return result;
		for(int i=0; i<list.size(); i++){
			if(list.get(i).getID().equals(id)){
				return i;
			}
		}
		return result;
	}


	/**
	 * Notice user gui input
	 * @return
	 */
	public boolean userInput()
	{
		MonitorAndDebug.printConsole("# MetaDataUI::userInput()");
		boolean result=false;
		if(initImageUI && imageUI!=null){
			MonitorAndDebug.printConsole("\t ... image : changed data - "+imageUI.inputEvent());
			result=result || imageUI.inputEvent();
		}
		if(initExperimentUI && experimentUI!=null){
			MonitorAndDebug.printConsole("\t ... experiment : changed data - "+experimentUI.inputEvent());
			result=result || experimentUI.inputEvent();
		}
		if(initObjectiveUI && objectiveUI!=null){
			MonitorAndDebug.printConsole("\t ... objective : changed data - "+objectiveUI.inputEvent());
			result=result || objectiveUI.inputEvent();
		}
		if(initSampleUI && sampleUI!=null){
			MonitorAndDebug.printConsole("\t ... sample : changed data - "+sampleUI.inputEvent());
			result=result || sampleUI.inputEvent();
		}
		if(initImageEnvUI && imgEnvViewer!=null){
			MonitorAndDebug.printConsole("\t ... imgEnv : changed data - "+imgEnvViewer.inputEvent());
			result=result || imgEnvViewer.inputEvent();
		}

		if(initChannelUI  && model.getNumberOfChannels()>0){
			if(channelTab!=null){
				for(int i=0; i< channelTab.getTabCount(); i++){
					if(channelTab.getComponentAt(i)!=null){
						MonitorAndDebug.printConsole("\t ... channel "+i+" : changed data - "+((ChannelViewer) channelTab.getComponentAt(i)).inputEvent());
						result=result || ((ChannelViewer) channelTab.getComponentAt(i)).inputEvent();
					}
				}
			}
			if(initDetectorUI && detectorViewer!=null){
				MonitorAndDebug.printConsole("\t ... detector : changed data - "+detectorViewer.inputEvent());
				result=result || detectorViewer.inputEvent();
			}
			if(initLightPathUI && lightPathViewer!=null){
				MonitorAndDebug.printConsole("\t ... lightPath : changed data - "+lightPathViewer.inputEvent());
				result=result || lightPathViewer.inputEvent();
			}
			if(initLightSrcUI && lightSrcViewer!=null){
				MonitorAndDebug.printConsole("\t ... lightSrc : changed data - "+lightSrcViewer.inputEvent());
				result=result || lightSrcViewer.inputEvent();
			}
		}else{
			if(initChannelUI && channelTab!=null && channelTab.getTabCount()>0){
				if(channelTab.getComponentAt(0)!=null){
					MonitorAndDebug.printConsole("\t ... channel 0 : changed data - "+((ChannelViewer) channelTab.getComponentAt(0)).inputEvent());
					result=result || ((ChannelViewer) channelTab.getComponentAt(0)).inputEvent();
				}
			}
			if(initDetectorUI && detectorViewer!=null){
				MonitorAndDebug.printConsole("\t ... detector : changed data - "+detectorViewer.inputEvent());
				result=result || detectorViewer.inputEvent();
			}
			if(initLightPathUI && lightPathViewer!=null){
				MonitorAndDebug.printConsole("\t ... lightPath : changed data - "+lightPathViewer.inputEvent());
				result=result || lightPathViewer.inputEvent();
			}
			if(initLightSrcUI && lightSrcViewer!=null){
				MonitorAndDebug.printConsole("\t ... lightSrc : changed data - "+lightSrcViewer.inputEvent());
				result=result || lightSrcViewer.inputEvent();
			}
		}

		MonitorAndDebug.printConsole("########## INPUT available: "+result+" ##################");

		return result;
	}


	/**
	 * True if all data of viewer was stored
	 * @return
	 */
	public boolean allDataWasStored()
	{
		boolean result=true;
		if(initImageUI && imageUI!=null){ 
			result=result && imageUI.allDataWasStored();
			MonitorAndDebug.printConsole("\t ... Image data stored - "+imageUI.allDataWasStored());
		}
		if(initExperimentUI && experimentUI!=null){
			result=result && experimentUI.allDataWasStored();
			MonitorAndDebug.printConsole("\t ... experiment data stored - "+experimentUI.allDataWasStored());
		}
		if(initObjectiveUI && objectiveUI!=null){
			result=result && objectiveUI.allDataWasStored();
			MonitorAndDebug.printConsole("\t ... objective data stored - "+objectiveUI.allDataWasStored());
		}
		if(initSampleUI && sampleUI!=null){
			result=result && sampleUI.allDataWasStored();
			MonitorAndDebug.printConsole("\t ... sample data stored - "+sampleUI.allDataWasStored());
		}
		if(initImageEnvUI && imgEnvViewer!=null){
			result=result && imgEnvViewer.allDataWasStored();
			MonitorAndDebug.printConsole("\t ... imgEnv data stored - "+imgEnvViewer.allDataWasStored());
		}
		if(initDetectorUI && detectorViewer!=null){
			result=result && detectorViewer.allDataWasStored();
			MonitorAndDebug.printConsole("\t ... detector data stored - "+detectorViewer.allDataWasStored());
		}
		if(initLightPathUI && lightPathViewer!=null){
			result=result && !lightPathViewer.inputEvent();
			MonitorAndDebug.printConsole("\t ... Filter data stored - "+(!lightPathViewer.inputEvent()));
		}
		if(initLightSrcUI && lightSrcViewer!=null){
			result=result && lightSrcViewer.allDataWasStored();
			MonitorAndDebug.printConsole("\t ... LightSrc data stored - "+lightSrcViewer.allDataWasStored());
		}

		if(initChannelUI  && model.getNumberOfChannels()>0){
			if(channelTab!=null){
				for(int i=0; i< channelTab.getTabCount(); i++){
					if(channelTab.getComponentAt(i)!=null && channelTab.getComponentAt(i) instanceof ChannelViewer){
						result=result && ((ChannelViewer) channelTab.getComponentAt(i)).allDataWasStored();
						MonitorAndDebug.printConsole("\t ... Channel data stored - "+((ChannelViewer) channelTab.getComponentAt(i)).allDataWasStored());
					}else{
						MonitorAndDebug.printConsole("\t ... Channel no tabcomponent init");
					}
				}
			}else{
				MonitorAndDebug.printConsole("\t ... Channel no channeltab");
			}
		}else{
			MonitorAndDebug.printConsole("\t ... Channel no data init");
		}
		return result;
	}

	/** notice all added data (input and predefinition values)
	 * @return
	 */
	public boolean hasDataToSave() 
	{
		String changesDesc="";
		boolean result=false;
		boolean changes=false;
		MonitorAndDebug.printConsole("# MetaDataUI::hasDataToSave()");
		if(initImageUI && imageUI!=null){
			changes=imageUI.hasDataToSave() || model.getChangesImage()!=null;
			result=result || changes;
			if(changes) changesDesc+="Image Modul \n";
			MonitorAndDebug.printConsole("\t ... image : changed data - "+changes);
		}
		if(initExperimentUI && experimentUI!=null){
			changes=experimentUI.hasDataToSave() || model.getChangesExperiment()!=null;
			result=result || changes;
			if(changes) changesDesc+="Experiment Modul \n";
			MonitorAndDebug.printConsole("\t ... Experiment : changed data - "+changes);
		}
		if(initObjectiveUI && objectiveUI!=null){
			changes=objectiveUI.hasDataToSave()|| model.getChangesObject()!=null;
			result=result || changes;
			if(changes) changesDesc+="Objective Modul \n";
			MonitorAndDebug.printConsole("\t ... Objective : changed data - "+changes);
		}
		if(initSampleUI && sampleUI!=null){
			changes=sampleUI.hasDataToSave()|| model.getChangesSample()!=null;
			result = result || changes;
			if(changes) changesDesc+="Sample Modul \n";
			MonitorAndDebug.printConsole("\t ... Sample : changed data - "+changes);
		}
		if(initImageEnvUI && imgEnvViewer!=null){
			changes= imgEnvViewer.hasDataToSave() || model.getChangesImgEnv()!=null;
			result=result ||changes;
			if(changes) changesDesc+="Image Env Modul \n";
			MonitorAndDebug.printConsole("\t ... ImageEnv : changed data - "+changes);
		}

		if(initChannelUI  && model.getNumberOfChannels()>0){
			if(channelTab!=null){

				for(int i=0; i< channelTab.getTabCount(); i++){
					if(channelTab.getComponentAt(i)!=null && channelTab.getComponentAt(i) instanceof ChannelViewer){
						changes=((ChannelViewer) channelTab.getComponentAt(i)).hasDataToSave();
						result=result || changes;
						if(changes) changesDesc+="Channel Modul \n";
						MonitorAndDebug.printConsole("\t ... Channel : changed data - "+changes);
					}else{
						MonitorAndDebug.printConsole("\t ... Channel : no data ");
					}
				}
			}

			if(initDetectorUI && detectorViewer!=null){
				changes=detectorViewer.hasDataToSave() || model.getChangesDetector()!=null;
				result=result || changes;
				if(changes) changesDesc+="Detector Modul \n";
				MonitorAndDebug.printConsole("\t ... Detector : changed data - "+changes);
			}
			if(initLightPathUI && lightPathViewer!=null){
				changes=lightPathViewer.hasDataToSave()|| model.getChangesLightPath()!=null;
				result=result || changes ;
				if(changes) changesDesc+="Filter Modul \n";
				MonitorAndDebug.printConsole("\t ... Filter : changed data - "+changes);
			}
			if(initLightSrcUI && lightSrcViewer!=null){
				changes=lightSrcViewer.hasDataToSave()||model.getChangesLightSrc()!=null;
				result=result || changes;
				if(changes) changesDesc+="LightSrc Modul \n";
				MonitorAndDebug.printConsole("\t ... LightSrc : changed data - "+changes);
			}

		}else{
			if(initChannelUI && channelTab!=null && channelTab.getTabCount()>0){
				MonitorAndDebug.printConsole("\t ... Channel tabs: "+channelTab.getTabCount());
				if(channelTab.getComponentAt(0)!=null && channelTab.getComponentAt(0) instanceof ChannelViewer){
					changes=((ChannelViewer) channelTab.getComponentAt(0)).hasDataToSave();
					result=result || changes;
					if(changes) changesDesc+="Channel Modul \n";
					MonitorAndDebug.printConsole("\t ... Channel : changed data - "+changes);
				}else{
					MonitorAndDebug.printConsole("\t ... Channel : no data - ??");
				}
			}
			if(initDetectorUI && detectorViewer!=null){
				changes=detectorViewer.hasDataToSave() || model.getChangesDetector()!=null;
				result=result || changes;
				if(changes) changesDesc+="Detector Modul \n";
				MonitorAndDebug.printConsole("\t ... Detector 2: changed data - "+changes);
			}
			if(initLightPathUI && lightPathViewer!=null){
				changes=lightPathViewer.hasDataToSave()|| model.getChangesLightPath()!=null;
				result=result || changes;
				if(changes) changesDesc+="Filter Modul \n";
				MonitorAndDebug.printConsole("\t ... Filter : changed data - "+changes);
			}
			if(initLightSrcUI && lightSrcViewer!=null){
				changes=lightSrcViewer.hasDataToSave()||model.getChangesLightSrc()!=null;
				result=result || changes;
				if(changes) changesDesc+="LightSrc Modul \n";
				MonitorAndDebug.printConsole("\t ... LightSrc : changed data - "+changes);
			}
		}
		dataToSave_Desc=changesDesc;
		model.setDataChange(result);
		return result;
	}

	public String getDataToSave_Desc()
	{
		if(dataToSave_Desc==null)
			return "";
		return dataToSave_Desc;
	}

	// save current viewer input 
	public void save() 
	{
		String chName=null;

		// save current module viewer changes
		MonitorAndDebug.printConsole("# MetaDataUI::save()");
		if(imageUI!=null){
			if( imageUI.hasDataToSave()){
				//get new changes
				List<TagData> list=imageUI.getChangedTags();
				printList("Image",list);
				//get all changes
				HashMap<String, String> map=imageUI.getMapValuesOfChanges(model.getMapAnnotationImage());

				//save all changes back to image model
				model.setMapAnnotationImage(map, false); 
				//save viewer data to image model
				imageUI.saveData();
				//save new changes to model
				model.setChangesImage(list);
				imageUI.afterSavingData();
			}
		}
		if(experimentUI!=null){
			if( experimentUI.hasDataToSave()){
				List<TagData> list=experimentUI.getChangedTags();
				model.setMapAnnotationExperiment(experimentUI.getMapValuesOfChanges(model.getMapAnnotationExperiment()), false); 
				printList("Experiment",list);
				experimentUI.saveData();
				model.setChangesExperiment(list);
				experimentUI.afterSavingData();
			}
		}
		if(sampleUI!=null ){
			if( sampleUI.hasDataToSave()){
				List<TagData> list=sampleUI.getChangedTags();
				model.setMapAnnotationSample(sampleUI.getMapValuesOfChanges(model.getMapAnnotationSample()), false); 
				printList("Sample",list);
				sampleUI.saveData();
				model.setChangesSample(list);
				sampleUI.afterSavingData();
			}
		}
		if(objectiveUI!=null){
			if( objectiveUI.hasDataToSave()){
				List<TagData> list=objectiveUI.getChangedTags();
				printList("Objective",list);
				objectiveUI.saveData();
				model.setChangesObject(list);
				model.setMapAnnotationObjective(objectiveUI.getMapValuesOfChanges(model.getMapAnnotationObjective()), false);
				objectiveUI.afterSavingData();
			}
		}
		if(imgEnvViewer!=null){
			if( imgEnvViewer.hasDataToSave()){
				List<TagData> list=imgEnvViewer.getChangedTags();
				model.setMapAnnotationImgEnv(imgEnvViewer.getMapValuesOfChanges(model.getMapAnnotationImgEnv()), false);
				printList("ImgEnv",list);
				imgEnvViewer.saveData();
				model.setChangesImageEnv(list);
				imgEnvViewer.afterSavingData();
			}
		}


		//save current selected channel
		if(channelTab!=null && 
				channelTab.getSelectedComponent()!=null && 
				channelTab.getSelectedComponent() instanceof ChannelViewer)
		{
			ChannelViewer chViewer=(ChannelViewer) channelTab.getSelectedComponent();
			chName=chViewer.getName();
			if(chViewer.hasDataToSave())
			{
				List<TagData> list=chViewer.getChangedTags();
				printList("Channel "+chViewer.getIndex(),list);
				chViewer.saveData();
				printMap(chViewer.getMapValuesOfChanges(model.getMapAnnotationChannel(chViewer.getIndex())));
				model.setChangesChannel(list, chViewer.getIndex());
				model.setMapAnnotationChannel(chViewer.getMapValuesOfChanges(model.getMapAnnotationChannel(chViewer.getIndex())),chViewer.getIndex(), false);
				chViewer.afterSavingData();
			}
		}
		// save selected component, other saved by deselect the channel
		if(detectorViewer!=null && detectorViewer.hasDataToSave()){
			int thisIndex=detectorViewer.getIndex();
			List<TagData> list=detectorViewer.getChangedTags();
			HashMap<String,String> map=model.getMapAnnotationDetector(thisIndex);

			printList("Detector "+thisIndex,list);
			detectorViewer.saveData();
			model.setChangesDetector(list,thisIndex);
			model.setMapAnnotationDetector(detectorViewer.getMapValuesOfChanges(map,chName),thisIndex, false);
			detectorViewer.afterSavingData();
		}
		if(lightPathViewer!=null && lightPathViewer.hasDataToSave()  ){

			int index=lightPathViewer.getIndex();
			HashMap<String,String> map=model.getMapAnnotationLightPath(index);
			//save current viewer data - but was is about the mapping data without viewer??
			lightPathViewer.saveData();
			try {
				printLightPath(model.getLightPath(index));
				model.setChangesLightPath(model.getLightPath(index),index);
				//overwrite the whole lightpath for this channel
				//printMap(lightPathViewer.getMapValuesOfChanges(null,index));
				model.setMapAnnotationLightPath(lightPathViewer.getMapValuesOfChanges(null,index),index, true);
				lightPathViewer.afterSavingData();

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if(lightSrcViewer!=null && lightSrcViewer.hasDataToSave()){
			List<TagData> list=lightSrcViewer.getChangedTags();
			printList("LightSrc "+lightSrcViewer.getIndex(),list);
			lightSrcViewer.saveData();
			model.setChangesLightSrc(list,lightSrcViewer.getIndex());
			model.setMapAnnotationLightSrc(lightSrcViewer.getMapValuesOfChanges(model.getMapAnnotationLightSrc(lightSrcViewer.getIndex()),chName),lightSrcViewer.getIndex(), false); 
			lightSrcViewer.afterSavingData();
		}

		//save mapr changes
		saveModelChanges();

		detectorInput=false;
		lightPathInput=false;
		lightSrcInput=false;
		dataToSave_Desc="";
	}

	private void saveModelChanges()
	{
		LightPathModel lp=model.getLightPathModel();
		if(lp!=null ){

			for(int i=0; i<=lp.getNumberOfLightPaths(); i++) {

				if(lp.hasInput(i)) {
					try {
						MonitorAndDebug.printConsole("# MetaDataUI::saveModelChanges()::Filter of CH "+i);
						model.setChangesLightPath(model.getLightPath(i),i);
						model.setMapAnnotationLightPath(lp.getChangesAsMap(i), i, true);

						printMap(model.getMapAnnotationLightPath(i));

						lp.setInput(false, i);
					}catch (Exception e) {
						e.printStackTrace();
					}
				}else {
					MonitorAndDebug.printConsole("# MetaDataUI::saveModelChanges(): Filter nothing to save: "+i);
				}
			}
		}else {
			MonitorAndDebug.printConsole("# MetaDataUI::saveModelChanges(): Filter nothing to save");
		}

	}



	private HashMap<String, String> wrapListToMap(List<TagData> list, HashMap<String, String> map,
			String id) 
	{
		if(map==null)
			map=new HashMap<String, String>();
		for(TagData t:list){
			map.put(id+t.getTagName(), t.getTagValue());
		}
		return map;
	}

	public static void printList(String string, List<TagData> list) 
	{
		MonitorAndDebug.printConsole("\t Changes in "+string);
		for(TagData t:list){
			MonitorAndDebug.printConsole("\t\t "+t.getTagName()+" = "+t.getTagValue());
		}
	}

	public static void printLightPath(LightPath lp){
		System.out.println("\t Filter:");
		for(Filter f: lp.copyLinkedExcitationFilterList()){
			System.out.println("\t\t ExF: "+f.getModel());
		}
		if(lp.getLinkedDichroic()!=null)
			System.out.println("\t\t D: "+lp.getLinkedDichroic().getModel());

		for(Filter f: lp.copyLinkedEmissionFilterList()){
			System.out.println("\t\t EmF: "+f.getModel());
		}
	}

	public static void printMap(HashMap<String,String> map){
		System.out.println("\t HashMap Values:");
		Iterator iterator = map.keySet().iterator();

		while (iterator.hasNext()) {
			String key = iterator.next().toString();
			String value = map.get(key)!=null? map.get(key).toString():"";

			System.out.println("\t\t"+key + ": " + value);
		}
	}

	public boolean experimentUIInput()
	{
		return experimentUI.hasDataToSave();
	}
	public boolean sampleUIInput()
	{
		return sampleUI.hasDataToSave();
	}




	class DemoCustomTab extends JPanel {

		JTabbedPane customJTabbedPane;
		CustomButton removeButton;

		/** JPanel contain a JLabel and a JButton to close */
		public DemoCustomTab(JTabbedPane customJTabbedPane) {
			this.customJTabbedPane = customJTabbedPane;
			setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
			setBorder(new EmptyBorder(5, 2, 2, 2));
			setOpaque(false);
			addLabel();
			removeButton=new CustomButton("x");
			add(removeButton);
		}

		public void hideRemove() 
		{
			removeButton.setVisible(false);
		}

		public void showRemove() 
		{
			removeButton.setVisible(true);
		}

		private void addLabel() {
			JLabel label = new JLabel() {
				/** set text for JLabel, it will title of tab */
				public String getText() {
					int index = customJTabbedPane.indexOfTabComponent(DemoCustomTab.this);
					if (index != -1) {
						return customJTabbedPane.getTitleAt(index);
					}
					return null;
				}
			};
			/** add more space between the label and the button */
			label.setBorder(new EmptyBorder(0, 0, 0, 10));
			add(label);
		}

		class CustomButton extends JButton implements MouseListener {
			public CustomButton(String text) {
				int size = 15;
				setText(text);
				/** set size for button close */
				setPreferredSize(new Dimension(size, size));

				setToolTipText("close the Tab");

				/** set transparent */
				setContentAreaFilled(false);

				/** set border for button */
				setBorder(new EtchedBorder());
				/** don't show border */
				setBorderPainted(false);

				setFocusable(false);

				/** add event with mouse */
				addMouseListener(this);

			}

			/** when click button, tab will close */
			@Override
			public void mouseClicked(MouseEvent e) {
				int index = customJTabbedPane.indexOfTabComponent(DemoCustomTab.this);
				if (index != -1) {
					// remove channel and his linked detector, lightSrc, lightPath
					MetaDataUI.this.model.getChannelModel().remove(index); 
					MetaDataUI.this.model.getDetectorModel().remove(index);
					MetaDataUI.this.model.getLightSourceModel().remove(index);
					MetaDataUI.this.model.getLightPathModel().remove(index);
					customJTabbedPane.remove(index);
					numChannelTabs--;

					MonitorAndDebug.printConsole("\t...Remove tab- index: "+index+", numTabs: "+numChannelTabs);
					//	                 set last channel as removable
					if(directoryPane && numChannelTabs>2){
						DemoCustomTab lastTab=(DemoCustomTab) channelTab.getComponentAt(index-1);
						if(lastTab!=null) 
							lastTab.showRemove();
						else
							channelTab.setTabComponentAt(index-1, new DemoCustomTab(channelTab));
					}

					if (index == numChannelTabs - 1 && index > 0) {
						customJTabbedPane.setSelectedIndex(numChannelTabs - 2);
					} else {
						customJTabbedPane.setSelectedIndex(index);
					}

					if (numChannelTabs == 1) {
						addNewChannelTab();
					}

				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}

			/** show border button when mouse hover */
			@Override
			public void mouseEntered(MouseEvent e) {
				setBorderPainted(true);
				setForeground(Color.RED);
			}

			/** hide border when mouse not hover */
			@Override
			public void mouseExited(MouseEvent e) {
				setBorderPainted(false);
				setForeground(Color.BLACK);
			}
		}
	}

	public boolean predefinitionsAreLoaded()
	{
		if(customSett==null )
			return false;

		boolean result=false;
		if(imageUI!=null){
			result=result || imageUI.predefinitionValAreLoaded();
			MonitorAndDebug.printConsole("-- Image : predata loaded - "+imageUI.predefinitionValAreLoaded());
		}

		if(imgEnvViewer!=null){
			result=result|| imgEnvViewer.predefinitionValAreLoaded();
			MonitorAndDebug.printConsole("-- ImgEnv : predata loaded - "+imgEnvViewer.predefinitionValAreLoaded());
		}
		if(objectiveUI!=null){
			result=result|| objectiveUI.predefinitionValAreLoaded();
			MonitorAndDebug.printConsole("-- Objective : predata loaded - "+objectiveUI.predefinitionValAreLoaded());
		}
		if(detectorViewer!=null){
			result=result|| detectorViewer.predefinitionValAreLoaded();
			MonitorAndDebug.printConsole("-- Detector : predata loaded - "+detectorViewer.predefinitionValAreLoaded());
		}
		if(lightSrcViewer!=null){
			result=result|| lightSrcViewer.predefinitionValAreLoaded();
			MonitorAndDebug.printConsole("-- LightSrc : predata loaded - "+lightSrcViewer.predefinitionValAreLoaded());
		}
		if(lightPathViewer!=null){
			result=result|| lightPathViewer.predefinitionValAreLoaded();
			MonitorAndDebug.printConsole("-- Filter : predata loaded - "+lightPathViewer.predefinitionValAreLoaded());
		}
		if(sampleUI!=null){
			result=result|| sampleUI.predefinitionValAreLoaded();
			MonitorAndDebug.printConsole("-- Sample : predata loaded - "+sampleUI.predefinitionValAreLoaded());
		}
		if(experimentUI!=null){
			result=result|| experimentUI.predefinitionValAreLoaded();
			MonitorAndDebug.printConsole("-- Experiment : predata loaded - "+experimentUI.predefinitionValAreLoaded());
		}
		if(initChannelUI  && model.getNumberOfChannels()>0){
			if(channelTab!=null){
				for(int i=0; i< channelTab.getTabCount(); i++){
					if(channelTab.getComponentAt(i)!=null){
						result=result || ((ChannelViewer) channelTab.getComponentAt(i)).predefinitionValAreLoaded();
						MonitorAndDebug.printConsole("-- Image : predata loaded - "+((ChannelViewer) channelTab.getComponentAt(i)).predefinitionValAreLoaded());
					}
				}
			}
		}else{
			if(initChannelUI && channelTab!=null && channelTab.getTabCount()>0){
				if(channelTab.getComponentAt(0)!=null){
					result=result || ((ChannelViewer) channelTab.getComponentAt(0)).predefinitionValAreLoaded();
					MonitorAndDebug.printConsole("-- Image : predata loaded - "+((ChannelViewer) channelTab.getComponentAt(0)).predefinitionValAreLoaded());
				}
			}
		}
		//TODO channel
		return result;
	}

	/**
	 * Save extended metadata (all data that should be additional visible for CellNanOs Insight client, but not parsed
	 * by the standard client.
	 */
	public void saveExtendedMetaData() 
	{
		if(imageUI!=null) {
			HashMap map=imageUI.getMapValueOfExtendedData();
			System.out.println("Image extended data: "+map.size());
			if(map!= null && map.size()>0)
				model.addToMapAnnotationImage(TagNames.STAGELABEL,
						(String) map.get(TagNames.STAGELABEL));
		}
		if(detectorViewer!=null) {
			HashMap map=detectorViewer.getMapValueOfExtendedData();
			System.out.println("Detector extended data: "+map.size());
			if(map!= null && map.size()>0)
				model.addToMapAnnotationDetector(TagNames.SUBARRAY,
						(String) map.get(TagNames.SUBARRAY), 0);
		}
	}

	public void savePreValues()
	{
		if(imageUI!=null && imageUI.predefinitionValAreLoaded()){
			imageUI.saveData();
		}
		if(imgEnvViewer!=null && imgEnvViewer.predefinitionValAreLoaded()){
			imgEnvViewer.saveData();
		}
		if(objectiveUI!=null && objectiveUI.predefinitionValAreLoaded()){
			objectiveUI.saveData();
		}
		if(detectorViewer!=null && detectorViewer.predefinitionValAreLoaded()){
			detectorViewer.saveData();
		}
		if(lightSrcViewer!=null && lightSrcViewer.predefinitionValAreLoaded()){
			lightSrcViewer.saveData();
		}
		if(lightPathViewer!=null && lightPathViewer.predefinitionValAreLoaded()){
			lightPathViewer.saveData();
		}
		if(sampleUI!=null && sampleUI.predefinitionValAreLoaded()){
			sampleUI.saveData();
		}
		if(experimentUI!=null && experimentUI.predefinitionValAreLoaded()){
			experimentUI.saveData();
		}
	}

	public MapAnnotationData getMapAnnotation() 
	{
		MapAnnotationData ma=model.getAnnotation();
		return ma;
	}

	public void addMapAnnotations(MetaDataModel m)
	{
		MonitorAndDebug.printConsole("# MetaDataUI::addMapAnnotations()");
		model.setMapAnnotationImage(m.getMapAnnotationImage(), true);
		model.setMapAnnotationImgEnv(m.getMapAnnotationImgEnv(), true);
		model.setMapAnnotationExperiment(m.getMapAnnotationExperiment(), true);
		model.setMapAnnotationSample(m.getMapAnnotationSample(), true);
		model.setMapAnnotationObjective(m.getMapAnnotationObjective(), true);

		for(int i=0; i<m.getNumberOfChannels();i++){
			model.setMapAnnotationChannel(m.getMapAnnotationChannel(i), i, true);
		}
		for(int i=0; i<m.getNumberOfDetectors();i++){
			model.setMapAnnotationDetector(m.getMapAnnotationDetector(i), i, true);
		}
		for(int i=0; i<m.getNumberOfLightPath();i++){

			model.setMapAnnotationLightPath(m.getMapAnnotationLightPath(i), i, true);
		}
		for(int i=0; i<m.getNumberOfLightSrc();i++){
			model.setMapAnnotationLightSrc(m.getMapAnnotationLightSrc(i), i, true);
		}
	}
}
