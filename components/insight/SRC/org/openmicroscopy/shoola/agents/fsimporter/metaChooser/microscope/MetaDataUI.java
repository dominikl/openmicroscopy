package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.microscope;

import info.clearthought.layout.TableLayout;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import loci.common.services.ServiceFactory;
import loci.formats.ImageReader;
import loci.formats.meta.MetadataRetrieve;
import loci.formats.meta.MetadataStore;
import loci.formats.services.OMEXMLService;
import ome.xml.meta.IMetadata;
import ome.xml.model.Annotation;
import ome.xml.model.Channel;
import ome.xml.model.Detector;
import ome.xml.model.DetectorSettings;
import ome.xml.model.Dichroic;
import ome.xml.model.Experiment;
import ome.xml.model.Experimenter;
import ome.xml.model.Filter;
import ome.xml.model.Image;
import ome.xml.model.ImagingEnvironment;
import ome.xml.model.Instrument;
import ome.xml.model.Laser;
import ome.xml.model.LightPath;
import ome.xml.model.LightSource;
import ome.xml.model.LightSourceSettings;
import ome.xml.model.MapAnnotation;
import ome.xml.model.OME;
import ome.xml.model.Objective;
import ome.xml.model.ObjectiveSettings;
import ome.xml.model.Pixels;
import ome.xml.model.Plane;
import ome.xml.model.Project;
import ome.xml.model.StructuredAnnotations;
import ome.xml.model.XMLAnnotation;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.ImportUserData;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.MetaDataDialog;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.UOSMetadataLogger;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.MetaDataControl;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.MetaDataModel;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.OMEStore;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.SaveMetadata;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.format.ExperimentModel;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.format.Sample;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules.ChannelCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules.DetectorCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules.ElementsCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules.ExperimentCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules.ImageCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules.ImagingEnvironmentCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules.LightPathCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules.LightSourceCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules.ObjectiveCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules.PlaneCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules.PlaneSliderCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules.SampleCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model.ImageModel;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.view.ExperimentViewer;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.view.ImageViewer;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.view.SampleViewer;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.xml.SampleAnnotationXML;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.ModuleConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.ExceptionDialog;
//import org.slf4j.LoggerFactory;
import org.slf4j.LoggerFactory;

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
	
	/** view **/
	private JTabbedPane experimentPane;
	protected JPanel lightSrcPane;
	protected JPanel detectorPane;
	protected JPanel objectivePane;
	protected JTabbedPane channelTab;
	protected JPanel lightPathCardPane;
	private JTabbedPane imagePane;//tabbed pane with one element
	private JTabbedPane samplePane;
	
	protected JDialog imgEnvDialog;
	protected JDialog planeDialog;
	
	private JButton loadBtn;
	private JButton saveBtn;
	private JButton saveAllBtn;
	private JButton loadProfileBtn;
	private JButton resetBtn;
	
	GridBagLayout gbl;
	
	
	private ImageViewer imageUI;
	private ExperimentViewer experimentUI;
	private SampleViewer sampleUI;
	
	/** modul props */
	private ModuleConfiguration expModul;
	private ModuleConfiguration detModul;
	private ModuleConfiguration chModul;
	private ModuleConfiguration objModul;
	private ModuleConfiguration imgModul;
	private ModuleConfiguration imgEnvModul;
	private ModuleConfiguration lightSModul;
	private ModuleConfiguration lightPModul;
	private ModuleConfiguration planeModul;
	private ModuleConfiguration sampleModul;
	
	
	private boolean initChannelUI;
	private boolean initImageUI;
	private boolean initLightPathUI;
	private boolean initLightSrcUI;
	private boolean initDetectorUI;
	private boolean initObjectiveUI;
	private boolean initPlanesUI;
	private boolean initImageEnvUI;
	private boolean initSampleUI;
	private boolean initExperimentUI;
	
	private boolean componentsInit;

	private File file;
	
	private CustomViewProperties customSett;
	
	 /** Logger for this class. */
//    private static Logger LOGGER = Logger.getLogger(UOSMetadataLogger.class.getName());
    private static final org.slf4j.Logger LOGGER =
    	    LoggerFactory.getLogger(MetaDataUI.class);

	public static final String CHANGE_IMGDATA = "changeImageData";
	
	public MetaDataUI(CustomViewProperties sett)
	{
		
		this.setBorder(BorderFactory.createEmptyBorder());
		customSett=sett;
		
		componentsInit=false;
		
		initChannelUI=false;
		initImageUI=false;
		initLightPathUI=false;
		initLightSrcUI=false;
		initDetectorUI=false;
		initObjectiveUI=false;
		initPlanesUI=false;
		initImageEnvUI=false;
		initSampleUI=false;
		initExperimentUI=false;
		
		// create model
		model=new MetaDataModel();
		initModelComponents();
		control=new MetaDataControl(model,this);
	}
	
	/** init modul components respective to settings*/ 
	private void initModelComponents() 
	{
		if(!componentsInit){
			if(customSett.getObjConf()!=null && customSett.getObjConf().isVisible()){
				LOGGER.info("[GUI] -- init OBJECTIVE modul");
				ObjectiveCompUI oUI=new ObjectiveCompUI(customSett.getObjConf());
				oUI.addToList(customSett.getMicObjList());
				model.setObjectiveData(oUI); 
				initObjectiveUI=true;
				objModul=customSett.getObjConf();

			}
			if(customSett.getDetectorConf()!=null && customSett.getDetectorConf().isVisible()){
				LOGGER.info("[GUI] -- init DETECTOR modul");
				DetectorCompUI dUI=new DetectorCompUI(customSett.getDetectorConf());
				dUI.addToList(customSett.getMicDetectorList());
				model.addDetectorData(dUI);
				initDetectorUI=true;
				detModul=customSett.getDetectorConf();
			}
			if(customSett.getLightSrcConf()!=null && customSett.getLightSrcConf().isVisible()){
				LOGGER.info("[GUI] -- init LIGHTSRC modul");
				LightSourceCompUI lUI=new LightSourceCompUI(customSett.getLightSrcConf());
				lUI.addToList(customSett.getMicLightSrcList());
				model.addLightSrcModul(lUI);
				initLightSrcUI=true;
				lightSModul=customSett.getLightSrcConf();
			}
			if(customSett.getChannelConf()!=null && customSett.getChannelConf().isVisible()){
				LOGGER.info("[GUI] -- init CHANNEL modul");
				ChannelCompUI chUI=new ChannelCompUI(customSett.getChannelConf()); 
				model.addChannelData(chUI);
				initChannelUI=true;
				chModul=customSett.getChannelConf();
			}
			if(customSett.getLightPathConf()!=null && customSett.getLightPathConf().isVisible()){
				LOGGER.info("[GUI] -- init LIGHTPATH modul");
				LightPathCompUI lpUI=new LightPathCompUI();
				lpUI.addFilterToList(customSett.getMicLightPathFilterList());
				model.addLightPath(lpUI);
				initLightPathUI=true;
				lightPModul=customSett.getLightPathConf();
			}
			if(customSett.getImgEnvConf()!=null && customSett.getImgEnvConf().isVisible()){
				LOGGER.info("[GUI] -- init IMAGEENV modul");
				ImagingEnvironmentCompUI ieUI=new ImagingEnvironmentCompUI(customSett.getImgEnvConf());
				model.setImagingEnv(ieUI);
				initImageEnvUI=true;
				imgEnvModul=customSett.getImgEnvConf();
			}
			if(customSett.getPlaneConf()!=null && customSett.getPlaneConf().isVisible()){
				LOGGER.info("[GUI] -- init PLANE modul");
				PlaneCompUI pUI=new PlaneCompUI(null);
				model.addPlaneModul(pUI);
				initPlanesUI=true;
				planeModul=customSett.getPlaneConf();
			}
			if(customSett.getExpConf()!=null && customSett.getExpConf().isVisible()){
				LOGGER.info("[GUI] -- init EXPERIMENT modul");
				experimentUI=new ExperimentViewer(null,customSett.getExpConf());
				initExperimentUI=true;
				expModul=customSett.getExpConf();
			}
			if(customSett.getSampleConf()!=null && customSett.getSampleConf().isVisible()){
				LOGGER.info("[GUI] -- init SAMPLE modul");
				SampleCompUI sUI=new SampleCompUI(customSett.getSampleConf());
				model.setSampleData(sUI);
				initSampleUI=true;
				sampleModul=customSett.getSampleConf();
			}
			if(customSett.getImageConf()!=null && customSett.getImageConf().isVisible()){
				LOGGER.info("[GUI] -- init IMAGE modul");
				imageUI=new ImageViewer(null, customSett.getImageConf());
				initImageUI=true;
				imgModul =customSett.getImageConf();
			}

			componentsInit=true;
		}
	}

	/** return model object*/
	public MetaDataModel getModel() throws Exception
	{
		//read input data
		return model;
	}
	/** return model object*/
	public MetaDataModel getUpdatedModel() throws Exception
	{
		//read input data
		save();
		return model;
	}
	
	

	/** add data from a parent model
	 * @throws Exception */
	public void addData(MetaDataModel m) throws Exception
	{
		if(m!=null && model!=null){
			addExperimentData(m.getExperimentModel(), true);
//			addProjectPartner(m.getProjectPartner(),true);
			
			addImageData(m.getImageData(),true);
			addObjectData(m.getObjective(),m.getObjectiveSettings(),true);
			
			for(int i=0; i<model.getNumberOfChannels();i++){
				addChannelData(i,m.getChannel(i),true);
				addLightPathData(i,m.getLightPath(i),true);
			}
			for(int i=0; i<model.getNumberOfDetectors();i++){
				addDetectorData(i, m.getDetector(i),m.getDetectorSettings(i), true);
			}
			
			for(int i=0; i<model.getNumberOfLightSrc(); i++){
				addLightSrcData(i,m.getLightSourceData(i),m.getLightSourceSettings(i),true); 
			}
			
			
			
			addSampleData(m.getSample(),true);
			addImageEnvData(m.getImgagingEnv(),true);
//			addPlaneData();
		}
	}
	

	private void addImageEnvData(ImagingEnvironment i, boolean overwrite)
	{
		ImagingEnvironmentCompUI iUI=model.getImgEnvModel();
		if(iUI!=null && i!=null){
			iUI.addData(i, overwrite);
			iUI.setFieldsExtern(true);
		}
	}
	public void addSampleData(Sample s, boolean overwrite) 
	{
		SampleCompUI sUI=model.getSampleModul();
		if(sUI!=null && s!=null){
			sUI.addData(s,overwrite);
			sUI.setFieldsExtern(true);
		}
	}

	private void addObjectData(Objective o,ObjectiveSettings os, boolean overwrite) 
	{
		ObjectiveCompUI oUI=model.getObjectiveModul();
		if(oUI!=null){
			if(o!=null) oUI.addData(o,overwrite);
			if(os!=null) oUI.addData(os, overwrite);
			oUI.setFieldsExtern(true);
		}
	}

	private void addLightSrcData(int i, LightSource ls,LightSourceSettings lss, boolean overwrite) 
	{
		LightSourceCompUI lsUI=model.getLightSourceModul(i);
		if(lsUI!=null){
			if(ls!=null) lsUI.addData(ls, overwrite); 
			if(lss!=null) lsUI.addData(lss,overwrite);
			lsUI.setFieldsExtern(true);
		}
	}

	private void addChannelData(int index, Channel c, boolean overwrite) 
	{
		ChannelCompUI cUI=model.getChannelModul(index);
		if(cUI!=null && c!=null){
			cUI.addData(c,overwrite);
			cUI.setFieldsExtern(true);
		}
	}
	
	/** add parent data*/
	private void addDetectorData(int index,Detector d,DetectorSettings ds, boolean overwrite)
	{
		DetectorCompUI dUI=model.getDetectorModul(index);
		if(dUI!=null){
			if(d!=null){
				dUI.addData(d,overwrite);
			}
			if(ds!=null){
				dUI.addData(ds, overwrite);
			}
			dUI.setFieldsExtern(true);
		}
	}
	
	private void addLightPathData(int i, LightPath lightPath, boolean b) 
	{
		LightPathCompUI lUI=model.getLightPathModul(i);
		if(lUI!=null){
			if(lightPath!=null){
				lUI.addData(lightPath, b);
				lUI.setFieldsExtern(true);
			}
		}
	}

	private void addImageData(Image i,boolean overwrite)  
	{
		if( i!=null){
			model.addData(i,overwrite);
		}
	}
	
	public void addExperimentData(ExperimentModel e,boolean overwrite) throws Exception
	{
		if(e!=null){
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
	public void readData(OME o, int imageIndex) throws Exception
	{
		if(o !=null)
		{		
				ome=o;

				//TODO eigentlich imageList!!!!
				model.setOME(ome);
				model.setImageIndex(imageIndex);
				Image image=ome.getImage(imageIndex);
//				//TODO richtiges project!!
//				if(ome.sizeOfProjectList()>0){
//					Project project=ome.getProject(0);
//				}
				
				if(image!=null){
					//TODO: jedes image hat sein eigenes Model
//					model.setImageProp(imageIndex, image.getPixels().sizeOfChannelList());
					model.setImageOMEData(image);
					
					List<Objective> objectives=null;
					List<Detector> detectors=null;
					List<LightSource> lightSources=null;
					List<Filter> filters=null;
					List<Dichroic> dichroics=null;
					List<Channel> channels=null;
					List<Plane> planes=null;

					//TODO: no referenced instrument? -> createDummy
					Instrument instrument=image.getLinkedInstrument();
					if(instrument==null){
//						LOGGER.warning("[DATA] NO INSTRUMENTS available, create new");
						LOGGER.warn("[DATA] NO INSTRUMENTS available, create new");
						model.createAndLinkNewInstrument(ome); 
					}else{
						objectives=instrument.copyObjectiveList();
						detectors=instrument.copyDetectorList();
						lightSources=instrument.copyLightSourceList();
						filters=instrument.copyFilterList();
						dichroics=instrument.copyDichroicList();
						List<Filter> filterList=instrument.copyFilterList();
						model.setFilterList(filterList);
						List<Dichroic> dichroicList=instrument.copyDichroicList();
						model.setDichroicList(dichroicList);
					}
					StructuredAnnotations annot=ome.getStructuredAnnotations();

					//TODO: no Pixel data -> dummies ??
					Pixels pixels=image.getPixels();
					if(pixels==null){
						LOGGER.warn("[DATA] NO PIXEL object available");
					}else{
						channels=pixels.copyChannelList();
						planes=pixels.copyPlaneList();
					}
					
					if(componentsInit){
						readImageData(image,objectives,annot);
						readChannelData(channels,lightSources,detectors,filters,dichroics);
						
						readPlaneData(planes);
						readImageEnvData(image);
						readExperimentData(image);
					}
				}else{
					LOGGER.warn("[DATA] NO IMAGE object available");
					
				}
			
		}else{
			LOGGER.warn("[DATA] NOT available METADATA ");
			model.setImageOMEData(null);
		}
	}
	
	

	private void readPlaneData(List<Plane> planes) throws Exception
	{
		if(initPlanesUI && planes!=null && !planes.isEmpty())
		{
				for(int i=0; i<planes.size(); i++){
					PlaneCompUI pUI;
					if(i<model.getNumberOfPlanes()){
						pUI=model.getPlaneModul(i);
						pUI.addData(planes.get(i));
					}else{
						pUI = new PlaneCompUI(planes.get(i));
						model.addPlaneModul(pUI);
					}
					
				}
				LOGGER.info("[DATA] -- load PLANE ("+planes.size()+")");
			
		}
	}

	private void readImageEnvData(Image image) 
	{
		if(initImageEnvUI){
			ImagingEnvironment i=image.getImagingEnvironment();
			ImagingEnvironmentCompUI iUI=model.getImgEnvModel();
			if(i!=null){
				iUI.addData(i,false);
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
			List<Filter> filters, List<Dichroic> dichroics) 
	{
		if(initChannelUI)
		{
			for(int i=0; i<channels.size();i++){
				ChannelCompUI cUI=null;
				if(channels.get(i)!=null)
				{
					
					if(i<model.getNumberOfChannels()){
						cUI = model.getChannelModul(i); 
						cUI.addData(channels.get(i), false);
					}else{
						cUI = new ChannelCompUI(channels.get(i),customSett.getChannelConf()); 
						cUI.addData(channels.get(i), false);
						model.addChannelData(cUI);
					}
					
					
					LOGGER.info("[DATA] -- load CHANNEL data "+cUI.getName());

					readLightPathData(channels.get(i),i,filters,dichroics);
					readLightSource(channels.get(i),i,lightSources);
					readDetectorData(channels.get(i),i,detectors);
				}
			}
		}else{
			readLightPathData(channels.get(0),0,filters,dichroics);
			readLightSource(channels.get(0),0,lightSources);
			readDetectorData(channels.get(0),0,detectors);
		}
			
	}
	
	

//	private void readLightPathData(Channel channel, int i) 
//	{
//		if(initLightPathUI){
//			boolean dataAvailable=false;
//			LightPath lp=channel.getLightPath();
//			LightPathCompUI lpUI=null;
//			
//			if(i<model.getNumberOfLightPath()){
//				lpUI =model.getLightPathModul(i);
//
//			}else{
//				lpUI = new LightPathCompUI();
//				model.setLightPath(lpUI, i);
//			}
//			
//			if(lp!=null &&(
//					lp.sizeOfLinkedEmissionFilterList()!=0 || 
//					lp.sizeOfLinkedExcitationFilterList()!=0 ||
//					lp.getLinkedDichroic()!=null)){
//				dataAvailable=true;
//			}
//			
//			if(!dataAvailable){
//				LOGGER.info("[DATA] -- LIGHTPATH data not available");
//			}else{
//				lpUI.setModel(model); 
//				lpUI.addData(lp,false);
//				lpUI.buildComponents();
//			}
//		}
//	}
	
	private void readLightPathData(Channel channel, int i, List<Filter> filters, List<Dichroic> dichroics) 
	{
		if(initLightPathUI){
			if((filters!=null && !filters.isEmpty()) || (dichroics!=null && !dichroics.isEmpty()))
			{
				boolean dataAvailable=false;
				String[] linkedObj=null;
				
				// get linked lightpath for current channel
				LightPath lp=channel.getLightPath();
				LightPathCompUI lpUI=null;

				if(lp!=null &&(
						lp.sizeOfLinkedEmissionFilterList()!=0 || 
						lp.sizeOfLinkedExcitationFilterList()!=0 ||
						lp.getLinkedDichroic()!=null)){
					dataAvailable=true;
				}
				
				if(i<model.getNumberOfLightPath()){
					lpUI =model.getLightPathModul(i);
					if(!dataAvailable){
						LOGGER.info("[DATA] -- LIGHTPATH  data not available");
					}else{
						lpUI.addData(lp,false);
					}
				}else{
					lpUI = new LightPathCompUI();
					if(!dataAvailable){
						LOGGER.info("[DATA] -- LIGHTPATH  data not available");
					}else{
						lpUI.addData(lp,false);
					}
					model.addLightPath(lpUI);
				}

				lpUI.clearList();
				lpUI.addFilterToList(customSett.getMicLightPathFilterList());
				lpUI.addFilterToList(filters);
				lpUI.addDichroicToList(dichroics);
				
			}else{
				LOGGER.info("[DATA] -- LIGHTPATH  data not available");
			}
		}
	}

	private void readLightSource(Channel channel, int i,
			List<LightSource> lightSources) 
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
				LightSourceCompUI lUI=null;
				if(i<model.getNumberOfLightSrc()){
					lUI =model.getLightSourceModul(i);
					if(!dataAvailable){
						LOGGER.info("[DATA] -- LIGHTSOURCE  data not available");
					}else{
						lUI.addData(l,false);
						lUI.addData(ls,false);
					}

				}else{
					lUI = new LightSourceCompUI(customSett.getLightSrcConf());
					if(!dataAvailable){
						LOGGER.info("[DATA] -- LIGHTSOURCE  data not available");
					}else{
						lUI.addData(l,false);
						lUI.addData(ls,false);
					}
					model.addLightSrcModul(lUI);
				}
				// fill selection list
				lUI.clearList();
				lUI.addToList(customSett.getMicLightSrcList());
				lUI.addToList(lightSources);

			}
			else{
				LOGGER.info("[DATA] -- LIGHTSOURCE  data not available");
			}
			
		}
	}

	private void readDetectorData(Channel channel, int i,
			List<Detector> detectors) 
	{
		if(initDetectorUI){
			if(detectors!=null && !detectors.isEmpty())
			{
				boolean dDataAvailable=false;
				String linkedDet=null;
				
				DetectorSettings ds=channel.getDetectorSettings();
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
						dDataAvailable=true;
					}else{
						LOGGER.info("[DATA] -- more than one unlinked detectors available");
					}
				}

				DetectorCompUI dUI=null;

				if(i<model.getNumberOfDetectors()){
					dUI =model.getDetectorModul(i); 
					if(!dDataAvailable){

						LOGGER.info("[DATA] -- DETECTOR data not available");
					}else{
						dUI.addData(d, false);
						dUI.addData(ds,false);
					}

				}else{
					dUI = new DetectorCompUI(customSett.getDetectorConf());
					if(!dDataAvailable){

						LOGGER.info("[DATA] -- DETECTOR data not available");
					}else{
						dUI.addData(d, false);
						dUI.addData(ds,false);
					}
					model.addDetectorData(dUI);
				}
				dUI.clearList();
				dUI.addToList(customSett.getMicDetectorList());
				dUI.addToList(detectors);
			}else{
				LOGGER.info("[DATA] -- DETECTOR data not available");
			}
		}
	}

	private void readImageData(Image image, List<Objective> objList, StructuredAnnotations annot) 
	{
			model.addData(image, false);
			
			readObjectiveData(image,objList);
			readSampleData(image,annot);
		
	}

	private void readSampleData(Image image, StructuredAnnotations annot) 
	{
		if(initSampleUI && annot!=null){
			SampleCompUI sUI=model.getSampleModul();
			Sample s=getSampleAnnotation(image,annot);
			if(s!=null){
				sUI.addData(s, false);
			}else{
				LOGGER.info("[DATA] Sample data not available!");
			}
		}
	}

//	private Sample getSampleAnnotation(Image image,StructuredAnnotations annot) 
//	{
//		List<Annotation> list=image.copyLinkedAnnotationList();
//		String sampleID=null;
//		for(int i=0; i<list.size(); i++){
//			if(image.getLinkedAnnotation(i).getID().contains(SampleAnnotationXML.SAMPLE_ANNOT_ID))
//				sampleID= image.getLinkedAnnotation(i).getID();
//		}
//		if(sampleID!=null){
//			for(int i=0; i<annot.sizeOfXMLAnnotationList();i++){
//				if(sampleID.equals(annot.getXMLAnnotation(i).getID())){
//					SampleAnnotationXML s=new SampleAnnotationXML();
//					return s.getSample(annot.getXMLAnnotation(i).getValue());
//				}
//			}
//		}
//		return null;
//	}
	
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
			if(image.getLinkedAnnotation(i).getID().contains(OMEStore.MAP_ANNOT_ID))
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

	private void readObjectiveData(Image image, List<Objective> objList) 
	{
		
		if(initObjectiveUI && objList!=null && !objList.isEmpty())
		{
			ObjectiveCompUI oUI=model.getObjectiveModul();
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
				oUI.addData(o, false); 
				oUI.addData(os,false); 
			}
			oUI.clearList();
			oUI.addToList(customSett.getMicObjList());
			oUI.addToList(objList);
			
			// link object 
//			model.getImageModul().setObjectiveSettings(osUI);
		}
		
	}

	/** set ImportUserData */
	public void setImportData(ImportUserData data)
	{
		if(data!=null){
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
		
//		revalidateTableLayout();
		revalidate();
		repaint();
	}

	// hide empty col or empty row
//	private void revalidateTableLayout() 
//	{
//		TableLayout layout=(TableLayout) getLayout();
//		layout.
//	}

	private void showExperimentData() {
		if(initExperimentUI){
			experimentPane=new JTabbedPane();
			experimentPane.add("Experiment",new ExperimentViewer(model.getExperimentModel(), expModul));
			addToPlaceholder(experimentPane,expModul.getPosition(), expModul.getWidth());
		}
	}
	
	private void showImageData() throws Exception
	{
		imagePane=new JTabbedPane();
		if(initImageUI){
			imageUI=new ImageViewer(model.getImageModel(), imgModul);
			imagePane.add("Image",imageUI);
			JComponent img=imagePane;

			//TODO position and width from file
			if(initPlanesUI || initImageEnvUI){
				////			JScrollPane spImage =new GBScrollPane(imageUI);
				img=new JPanel();
				GridBagLayout myGBL=new GridBagLayout();
				img.setLayout(myGBL);
				addComponent(img,myGBL,imagePane,0,0,1,3,1.0,1.0,GridBagConstraints.BOTH);
				if(initImageEnvUI)
					addComponent(img,myGBL,initImgEnvironmentBtn(),0,3,1,1,1.0,0,GridBagConstraints.HORIZONTAL);
				if(initPlanesUI)
					addComponent(img,myGBL,initPlaneBtn(),0,4,1,1,1.0,0,GridBagConstraints.HORIZONTAL);
			}
			addToPlaceholder(img, imgModul.getPosition(), imgModul.getWidth());
		}
		String name=model.getImageData()!=null ? model.getImageData().getName() : null;

		CardLayout cl;
		cl=new CardLayout();
		objectivePane=new JPanel(cl);
		showOjectiveData(name);

		if(initObjectiveUI)
			addToPlaceholder(objectivePane, objModul.getPosition(), objModul.getWidth());
	}

	private void showOjectiveData(String name) 
	{
		if(initObjectiveUI){
			objectivePane.add(control.activateObjectiveModulView(name),name);

		}
	}
	
	private void showSampleData()
	{
		if(initSampleUI){
			samplePane=new JTabbedPane();
			samplePane.add("Sample",control.activateSampleModuleView());
			addToPlaceholder(samplePane, sampleModul.getPosition(), sampleModul.getWidth());
		}
	}

	private void showChannelData() throws Exception 
	{
		CardLayout cl;
		cl=new CardLayout();
		lightSrcPane=new JPanel(cl);
		cl=new CardLayout();
		detectorPane=new JPanel(cl);
		cl=new CardLayout();
		lightPathCardPane=new JPanel(cl);
		
		if(initChannelUI){
			channelTab=new JTabbedPane();
			channelTab.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent changeEvent) {
					if(model.getNumberOfChannels()>0){
						JTabbedPane sourceTabbedPane = (JTabbedPane) changeEvent.getSource();
						int index = sourceTabbedPane.getSelectedIndex();
						if(index >=0){
							selectChannel(index);
						}
					}
				}
			});
			
			for(int i=0; i<model.getNumberOfChannels();i++)
			{
				String name= model.getChannel(i)!=null ? model.getChannel(i).getName() : "Channel";
				name= (name==null || name.isEmpty()) ? ((i==0 ? "Channel " : "# ")+String.valueOf(i)) : name;
				detectorPane.add(control.activateDetectorModulView(i,name,customSett.getDetectorConf()),name);
				
				lightSrcPane.add(control.activateLightSrcModulView(i,name,customSett.getLightSrcConf()),name);
				lightPathCardPane.add(control.activateLightPathModul(i,name),name); 
				
				channelTab.add(name,control.activateChannelModulView(i));
			}
			addToPlaceholder(channelTab,chModul.getPosition(), chModul.getWidth()); 
		}else{
			String name="Channel";
			detectorPane.add(control.activateDetectorModulView(0,name,customSett.getDetectorConf()),name);
			lightSrcPane.add(control.activateLightSrcModulView(0,name,customSett.getLightSrcConf()),name);
			lightPathCardPane.add(control.activateLightPathModul(0,name),name); 
		}
		
		showLightSourceData();
		showDetectorData();
		showLightPathData();
	}

	private void showLightPathData()
	{
		if(initLightPathUI)
			addToPlaceholder(lightPathCardPane, lightPModul.getPosition(), lightPModul.getWidth());
	}
	private void showLightSourceData() 
	{
		if(initLightSrcUI){
			addToPlaceholder(lightSrcPane, lightSModul.getPosition(), lightSModul.getWidth());
		}
	}

	private void showDetectorData() 
	{
		if(initDetectorUI)
			addToPlaceholder(detectorPane, detModul.getPosition(), detModul.getWidth());
	}
	
	protected void selectChannel(int chNr) 
	{	
		if(model.getNumberOfChannels() <chNr )
			return;
		
		channelTab.setSelectedIndex(chNr);

		ChannelCompUI channel=model.getChannelModul(chNr);
		String chName=channel.getName();
		
		LOGGER.info("[GUI-ACTION] -- select Channel "+chName);
		
		
		CardLayout cl;
		
		// update submodules
		if(initLightSrcUI && lightSrcPane!=null){
			// show referenced lightSrc + settings
			cl=(CardLayout) lightSrcPane.getLayout();
			cl.show(lightSrcPane, chName);
		}

		if(initDetectorUI && detectorPane!=null){
			// show referenced detector + settings
			cl=(CardLayout) detectorPane.getLayout();
			cl.show(detectorPane,chName);
		}

		if(initLightPathUI && lightPathCardPane!=null){
			//update lightPath
			chName=channelTab.getTitleAt(chNr);
			cl=(CardLayout) lightPathCardPane.getLayout();
			cl.show(lightPathCardPane, chName);
		}
		revalidate();
		repaint();

	}
	

//	public void saveViewData()
//	{
//		if(model!=null)
//			model.save(); 
//	}
	
	
	
	
	
	private void initLayout()
	{
		//layout
//		gbl = new GridBagLayout();
//		setLayout( gbl );
//
//		gbl.columnWidths = new int[] {50, 50,50,50};
//		
//		double[][] layoutDesign=new double[][]{
//				//X-achse
//				{0.25,0.25,0.25,TableLayout.FILL},
//				//Y-Achse
//				{0.5,0.5}
//		};
		double[][] layoutDesign=new double[][]{
				//X-achse
				{TableLayout.FILL},
				//Y-Achse
//				{0.5,0.5}
				{TableLayout.FILL}
		};
		TableLayout layout=new TableLayout(layoutDesign);
	
//		GridLayout layout=new GridLayout(4,4);
		setLayout(layout);
	}
	


	protected JPanel getButtonPane()
	{
		JPanel buttonPane = new JPanel();
		GridBagLayout layout=new GridBagLayout();
		buttonPane.setLayout(layout);
		Box buttonBox = Box.createHorizontalBox();
		buttonBox.add(resetBtn);
		buttonBox.add(Box.createHorizontalGlue());
		buttonBox.add(loadProfileBtn);
		buttonBox.add(Box.createHorizontalStrut(40));
		buttonBox.add(loadBtn);
		buttonBox.add(Box.createHorizontalStrut(20));
		buttonBox.add(saveBtn);
		buttonBox.add(saveAllBtn);
		addComponent(buttonPane, layout, buttonBox, 2, 4, 1, 1, 0.0, 0.0, GridBagConstraints.NONE);

		return buttonPane;
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
//			if(layout.getNumRow()<1)
//				layout.insertRow(1, TableLayout.FILL);
//			
//			if(layout.getNumColumn()<2)
//				layout.insertColumn(1, TableLayout.FILL);
//			
//			add(comp,"1,0");
//			System.out.println("PosB: ");
			insertModule(0,1,comp);
			break;
		case Pos_C:
//			if(layout.getNumRow()<1)
//				layout.insertRow(1, TableLayout.FILL);
//			
//			if(layout.getNumColumn()<3)
//				layout.insertColumn(2, TableLayout.FILL);
//			
//			add(comp,"2,0");
//			System.out.println("PosC: ");
			insertModule(0,2,comp);
			break;
		case Pos_D:
//			if(layout.getNumRow()<1)
//				layout.insertRow(1, TableLayout.FILL);
//			
//			if(layout.getNumColumn()<4)
//				layout.insertColumn(3, TableLayout.FILL);
//			
//			add(comp,"3,0");
//			System.out.println("PosD: ");
			insertModule(0,3,comp);
			break;
			//----------------------------------------------
		case Pos_E:
//			if(layout.getNumRow()<2)
//				layout.insertRow(1, TableLayout.FILL);
//			
//			add(comp,"0,1");
//			System.out.println("PosE: ");
			insertModule(1,0,comp);
			break;
		case Pos_F:
//			if(layout.getNumRow()<2)
//				layout.insertRow(1, TableLayout.FILL);
//			
//			if(layout.getNumColumn()<2)
//				layout.insertColumn(1, TableLayout.FILL);
//			add(comp,"1,1");
//			System.out.println("PosF: ");
			insertModule(1,1,comp);
			break;
		case Pos_G:
//			if(layout.getNumRow()<2)
//				layout.insertRow(1, TableLayout.FILL);
//			
//			if(layout.getNumColumn()<3)
//				layout.insertColumn(2, TableLayout.PREFERRED);
//			add(comp,"2,1");
//			System.out.println("PosG: ");
			insertModule(1,2,comp);
			break;
		case Pos_H:
//			if(layout.getNumRow()<2)
//				layout.insertRow(1, TableLayout.FILL);
//			
//			if(layout.getNumColumn()<4)
//				layout.insertColumn(3, TableLayout.PREFERRED);
//			add(comp,"3,1");
//			System.out.println("PosH: ");
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
//		System.out.println("Insert Module at col: "+columnIdx+", row: "+rowIdx);
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
	 * @return
	 */
	protected JButton initPlaneBtn() 
	{
		JButton btnPlanePos=new JButton("Plane/Stage Positions");
		btnPlanePos.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PlaneSliderCompUI plane=null;
				if(planeDialog==null){
					if(model.getNumberOfPlanes()>0){
						plane=new PlaneSliderCompUI(model.getPlaneModulList(), 
								model.getPixelsDimT(),model.getPixelsDimZ(),model.getPixelsDimC());
						//Tab of planes
						planeDialog=createPlaneDialog(plane,"Plane/Stage Positions",400,800);
						
					}
				}
				planeDialog.setVisible(true);
			}
		});
		return btnPlanePos;
	}

	/**
	 * @return
	 */
	protected JButton initImgEnvironmentBtn() 
	{
		JButton btnImgEnv=new JButton("Imaging Environment");
		btnImgEnv.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// create new Dialog with dummy pane
				if(imgEnvDialog==null){
					if(model.getImgEnvModel()!=null){ 
						imgEnvDialog=createImgEnvDialog(model.getImgEnvModel(), "Imaging Environment",350,150);	
					}else{
						LOGGER.warn("[DATA] IMAGE ENVIRONMENT not available");
					}
				}
				imgEnvDialog.setVisible(true);
//				JDialog diag=createDialog(imgEnvUI);
//				diag.setVisible(true);
			}
		});
		return btnImgEnv;
	}
	
	
	protected JDialog createPlaneDialog(JComponent p,String title, int width, int height)
	{
		JDialog d=new JDialog();
		d.setTitle(title);
		d.setSize(width,height);
		d.setModal(true);
		d.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
//		centerOnParent(d, true);
		d.add(p);
		d.pack();
//		d.setVisible(true);
		return d;
	}
	
	protected JDialog createSaveDialog(JComponent p,String title, int width, int height)
	{
		JDialog d=new JDialog();
		d.setTitle(title);
		d.setSize(width,height);
		d.setModal(true);
		d.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
//		centerOnParent(d, true);
		d.add(p);
		d.setVisible(true);
		d.setResizable(false);
		return d;
	}
	protected JDialog createImgEnvDialog(ImagingEnvironmentCompUI p,String title, int width, int height)
	{
		JDialog d=new JDialog();
		d.setTitle(title);
		d.setSize(width,height);
		d.setModal(true);
		d.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		p.buildComponents();
//		centerOnParent(d, true);
		d.add(p);
		d.pack();
//		d.setVisible(true);
		return d;
	}

	public boolean hasUserInput() 
	{
		boolean result=false;
		if(initImageUI)
			result=result || imageUI.hasDataToSave();
		if(initExperimentUI)
			result=result || experimentUI.hasDataToSave();
//		if(initObjectiveUI)
//			result=result || 
		return result;
	}

	public void save() 
	{
		if(imageUI.hasDataToSave()){
			imageUI.saveData();
			firePropertyChange(CHANGE_IMGDATA, null, model.getImageModel());
		}
		if(experimentUI.hasDataToSave()){
			experimentUI.saveData();
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

	/**
	 * 
	 */
//	public void save() 
//	{
//			if(ome!=null && model!=null){
//				model.save();
//				//					SaveMetadataUserDefinedUI pane = new SaveMetadataUserDefinedUI(ome,model,null,file);
//				//					JDialog diag=createSaveDialog(pane,"Save MetaData", 600,600);
//				if(file!=null){
//					LOGGER.info("[SAVE] -- save to "+file.getAbsolutePath());
//					SaveMetadata saver=new SaveMetadata(ome, model, null, file);
//					saver.save();
//				}else{
////					LOGGER.severe("[SAVE] -- no destination file is given");
//					LOGGER.error("[SAVE] -- no destination file is given");
//					ExceptionDialog ld = new ExceptionDialog("Save File Error!", 
//							"No file is given!");
//					ld.setVisible(true);
//				}
//			}else{
//				String b1=(ome!=null) ? "available": "not available";
//				String b2=(model!=null) ? "available": "not available";
//				String fileName=file!=null ? file.getName() : "";
//				LOGGER.error("--CAN'T SAVE "+fileName+" : OME = "+b1+", MODEL = "+b2);
//				ExceptionDialog ld = new ExceptionDialog("Can't save "+fileName+"!",
//						"OME = "+b1+", MODEL = "+b2); 
//
//				ld.setVisible(true);
//			}
//		
//	}
	
	
	
}
