package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.microscope;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JPanel;

import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.formats.FormatException;
import loci.formats.ImageReader;
import loci.formats.meta.MetadataRetrieve;
import loci.formats.meta.MetadataStore;
import loci.formats.services.OMEXMLService;
import ome.xml.meta.IMetadata;
import ome.xml.model.OME;
import omero.gateway.model.MapAnnotationData;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.MetaDataDialog;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.MetaDataModel;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.MetaDataModelObject;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.ExceptionDialog;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.ImportUserData;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.MapAnnotationObject;
import org.openmicroscopy.shoola.util.MonitorAndDebug;
import org.slf4j.LoggerFactory;

/**
 * Gui for metadata for selected node of filetree.
 * Series image file: holds one metadataui - tab for every series image of the selected file. 
 * If not a series file, than view is  an instance of jpanel.
 * 
 * 
 * Works for xsi:schemaLocation="http://www.openmicroscopy.org/Schemas/OME/2015-01  
 * @author Susanne Kunis &nbsp;&nbsp;&nbsp;&nbsp; <a
*         href="mailto:susannekunis@gmail.com">susannekunis@gmail.com</a>
 *
 */
public class MetaDataView extends JPanel
{
	/** Logger for this class. */
	private static final org.slf4j.Logger LOGGER =
			LoggerFactory.getLogger(MetaDataUI.class);

	private OME ome;
	Hashtable<String, Object> series;
	private File srcFile;

	// MetaDataUI
	private MetaDataUI singleView;
	private CardLayout seriesCard; 

	private DefaultListModel seriesListModel;
	private JPanel cardPane; 

	private List<String> cardNames;
	private int currentCardIndex;

	private boolean seriesData;

	private boolean fileDataLoaded;

	private JPanel parent;

	private boolean parentDataLoaded;



	/**
	 * Constructor for place holder / dummy component
	 */
	public MetaDataView() 
	{
		super(new BorderLayout());
		this.setBorder(BorderFactory.createEmptyBorder());
		parentDataLoaded=false;
		fileDataLoaded=false;
	}

	/**
	 * Metadata GUI for file.
	 * @param fName file name
	 * @param importData given import information for this data.
	 * @param parentData given parent information for this data.
	 * @param parentPanel parent JPanel of this component.
	 * @param showFileData TODO
	 * @param showPreValues TODO
	 */
	public MetaDataView(String fName,ImportUserData importData,
			MetaDataModel parentData, MetaDataDialog parentPanel, boolean showFileData, boolean showPreValues) throws Exception
	{
		super(new BorderLayout());
		this.setBorder(BorderFactory.createEmptyBorder());
		parent=parentPanel;

		LOGGER.info("### build view and load data for "+ fName+" ###");

		ImageReader reader = new ImageReader();
		IMetadata data=null;

		Cursor cursor=parentPanel.getCursor();
		parentPanel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		data = readMetadataFromFile(fName, reader);
		fileDataLoaded=true;


		parentPanel.setCursor(cursor);
		if(data==null || !showFileData){ 
			MonitorAndDebug.printConsole("\t...no file data loaded");
			fileDataLoaded=false;
			seriesData=false;
			singleView=new MetaDataUI(parentPanel,false, showPreValues); 

			//load parent data
			loadParentData(parentData, singleView);
			// load importData
			singleView.setImportData(importData);
			add(singleView,BorderLayout.CENTER);
			return ;
		}

		LOGGER.info("### read "+ fName+" ###");

		srcFile=new File(fName);

		if(reader.getSeriesCount()<2){
			LOGGER.info("[DATA] -- no series data");
			seriesData=false;
			// create with profile and hardware configurations
			singleView=new MetaDataUI(parentPanel,false, showPreValues);

			// load importData
			singleView.setImportData(importData);

			//load parent data
			loadParentData(parentData, singleView);

			//load data from file
			try{
				loadFileData(fName, ome, series,0, singleView);
			}catch(Exception e){
				LOGGER.error("[DATA] CAN'T read METADATA of "+fName);
				ExceptionDialog ld = new ExceptionDialog("Metadata Error!", 
						"Can't read given metadata  from "+fName,e,this.getClass().getSimpleName());
				ld.setVisible(true);
			}

			add(singleView,BorderLayout.CENTER);

		}else{
			seriesData=true;
			currentCardIndex=-1;

			seriesListModel=new DefaultListModel();
			seriesCard = new CardLayout();
			cardPane=new JPanel(seriesCard);
			for(int j=0; j< reader.getSeriesCount(); j++){
				LOGGER.info("[SERIE] ------------ read SERIE "+j+" of "+reader.getSeriesCount()+
						": "+data.getImageName(j)+"---------------------" );
				reader.setSeries(j);
				//new metaUI tab
				MetaDataUI metaUI=new MetaDataUI(parentPanel,false, showPreValues);

				//load importData
				metaUI.setImportData(importData);

				//load parent data
				loadParentData(parentData,metaUI);

				//load data from file
				loadFileData(fName, ome,series, j, metaUI);


				// add series to cardPane
				seriesListModel.addElement(data.getImageName(j));


				cardPane.add(metaUI,data.getImageName(j));
			}
			add(cardPane,BorderLayout.CENTER);
		}

	}

	/**
	 * 
	 * @return listModel with names of series of given ome model
	 */
	public DefaultListModel getSeries()
	{
		return seriesListModel;
	}

	/**
	 * Read data from OME o into given MetaDataUI component metaUI and link given metaUI to the OME own file.
	 * @param fName given file
	 * @param o OME of the given file
	 * @param series globalSeriesMetaData
	 * @param j series index
	 * @param metaUI gui for filedata
	 * @throws Exception 
	 */
	private void loadFileData(String fName, OME o,Hashtable<String, Object> series,int j,
			MetaDataUI metaUI) throws Exception 
	{

		LOGGER.info("[DATA] -- read file data "+fName);
		metaUI.linkToFile(new File(fName));
		metaUI.readData(o,series, j);

	}


	/**
	 * Metadata GUI for directory.
	 * @param name directory name
	 * @param importData given import information for this dataset.
	 * @param parentData given parent information for this dataset.
	 * @param dirData metadata model for current directory
	 * @param showPreValues TODO
	 * @param sett GUI properties
	 */
	public MetaDataView(String name, 
			ImportUserData importData, MetaDataModel parentData, MetaDataModel dirData,
			MetaDataDialog parent, boolean showPreValues)
	{
		super(new BorderLayout());
		LOGGER.info("[GUI] -- select directory");
		this.setBorder(BorderFactory.createEmptyBorder());

		srcFile=null;
		seriesData=false;

		if(dirData==null){
			singleView= new MetaDataUI(parent,true, showPreValues);
		}else{
			singleView = new MetaDataUI(parent,true,dirData, showPreValues);
		}

		//set importData
		singleView.setImportData(importData);

		/**set parentData
		if model for this directory exists- load parent data not necessary, 
		because all child directories with model will be updated if parent change*/
		if(dirData==null){
			loadParentData(parentData,singleView);
		}

		add(singleView,BorderLayout.CENTER);
		revalidate();
		repaint();
	}



	/**
	 * Add parent metadata to given metaData GUI.
	 * @param parentData parent data model
	 * @param pane metaData GUI
	 */
	private void loadParentData(MetaDataModel parentData,MetaDataUI pane) 
	{
		MonitorAndDebug.printConsole("# MetaDataView::loadParentData()...");
		LOGGER.info("[DATA] -- Load parent data");
		if(parentData!=null){
			try {
				pane.addData(parentData);
				pane.addMapAnnotations(parentData);
				parentDataLoaded=true;
			} catch (Exception e) {
				parentDataLoaded=false;
				LOGGER.warn("[DATA] -- Can't add metadata from parent model");
				MonitorAndDebug.printConsole("... end loadParentData()");
				e.printStackTrace();
				return;
			}
		}else{
			LOGGER.info("[DATA]--- No parent data available ");
		}
		MonitorAndDebug.printConsole("... end loadParentData()");
	}

	/**
	 * Read meta data from given file into OMEXMLMetadata format and set it as the MetadataStore 
	 * for given reader. Set global ome as MetadataRetrieve OMEXMLRoot. 
	 * @param file source file
	 * @param reader of the source file
	 * @return metadata as OMEXMLMetadata format
	 * @throws DependencyException
	 * @throws ServiceException
	 */
	private IMetadata readMetadataFromFile(String file, 
			ImageReader reader) throws DependencyException, ServiceException 
	{
		//record metadata to ome-xml format
		ServiceFactory factory=new ServiceFactory();
		OMEXMLService service = factory.getInstance(OMEXMLService.class);
		IMetadata metadata =  service.createOMEXMLMetadata();
		reader.setMetadataStore((MetadataStore) metadata);

		try{
			reader.setId(file);
		}catch(FormatException | IOException e){
			//			WarningDialog ld=new WarningDialog("Not supported file format for MetaData Editor!", 
			//					"Can't read metadata of "+file+"! Format is not supported.","MetaDataView");
			//			ld.setVisible(true);
			e.printStackTrace();
			return null;
		}

		LOGGER.info("[DATA] -- use READER: "+reader.getReader().getClass().getName());
		System.out.println("Use Reader: "+reader.getReader().getClass().getSimpleName());
		series = reader.getSeriesMetadata();
		String xml = service.getOMEXML((MetadataRetrieve) metadata);
		ome = (OME) service.createOMEXMLRoot(xml);

		return metadata;
	}

	/**
	 * 
	 * @return metaData model that holds a list of all series data models of current img data.
	 */
	public MetaDataModelObject getModelObject() 
	{
		try{
			List<MetaDataModel> list=new ArrayList<MetaDataModel>();
			if(seriesData){
				for(Component comp:cardPane.getComponents()){
					list.add(((MetaDataUI) comp).getModel());
				}
			}else{
				list.add(singleView.getModel());
			}
			MetaDataModelObject obj=new MetaDataModelObject(seriesData,list);

			return obj;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}



	/**
	 * Save data from GUI to file.
	 */
	public void saveModel() throws Exception
	{
		savePreValues();
		if(ome!=null){

			//file
			if(seriesData){
				updateGlobalSeriesData();
				LOGGER.info("[SAVE] -- save input for series data: "+srcFile.getAbsolutePath());
				MonitorAndDebug.printConsole("Save series data");
				for(Component comp:cardPane.getComponents()){
					((MetaDataUI) comp).save();
				}
			}else{
				if(singleView!=null){
					LOGGER.info("[SAVE] -- save input for single data "+srcFile.getAbsolutePath());
					singleView.save();
				}
			}
		}else{
			//dir
			if(singleView!=null){
				LOGGER.info("[SAVE] -- save model for directory");
				singleView.save();
			}
		}
	}

	public void saveExtendedMetaData() {
		if(seriesData){
			int index=0;
			for(Component comp : cardPane.getComponents()){
				System.out.println("Series_"+index++);
				((MetaDataUI) comp).saveExtendedMetaData();
			}
		}else{
			singleView.saveExtendedMetaData();
		}
	}


	private void savePreValues()
	{
		if(seriesData){
			for(Component comp : cardPane.getComponents()){
				((MetaDataUI) comp).savePreValues();
			}
		}else{
			singleView.savePreValues();
		}
	}

	/**
	 * Shows all GUI data.
	 */
	public void setVisible()
	{
		if(seriesData){
			int i=0;
			for(Component comp : cardPane.getComponents()){
				try {
					((MetaDataUI) comp).showData();
				} catch (Exception e) {
					LOGGER.error("[DATA] CAN'T load METADATA gui");
					ExceptionDialog ld = new ExceptionDialog("Metadata GUI Error!", 
							"Can't load metadata gui for series "+i,e,
							this.getClass().getSimpleName());
					ld.setVisible(true);
				}
				i++;
			}
		}else{
			try {
				singleView.showData();
			} catch (Exception e) {
				LOGGER.error("[DATA] CAN'T load METADATA gui");
				ExceptionDialog ld = new ExceptionDialog("Metadata GUI Error!", 
						"Can't load metadata gui",e,
						this.getClass().getSimpleName());
				ld.setVisible(true);
			}
		}
		revalidate();
		repaint();
	}

	/**
	 * Shows card pane of given name on the top. Holds Sample and ExperimentCompUI
	 * as global informnations.
	 * @param name
	 */
	public void showSeries(String name)
	{
		seriesCard.show(cardPane,name);
		currentCardIndex = seriesListModel.indexOf(name);
	}

	/**
	 * Update global data for all series, if data has changed in current selection. 
	 * Global data are sample, experiment data.
	 */
	public void updateGlobalSeriesData() 
	{
		try {
			if(currentCardIndex==-1)
				currentCardIndex=getCurrentCardIndex();
			if(currentCardIndex!=-1){
				MetaDataUI currentComp=(MetaDataUI) cardPane.getComponent(currentCardIndex);
				MetaDataModel currentModel=currentComp.getSavedModel();
				if(currentComp.experimentUIInput() || currentComp.sampleUIInput())
				{
					for(Component comp:cardPane.getComponents()){
						if(currentComp.experimentUIInput())
							((MetaDataUI) comp).addExperimentData(currentModel.getExperimentModel(),true);
						if(currentComp.sampleUIInput())
							((MetaDataUI) comp).addSampleData(currentModel.getSample(),true);
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	public int getCurrentCardIndex()
	{
		int index = -1;

		int i=0;
		for (Component comp : cardPane.getComponents() ) {
			if (comp.isVisible() == true) {
				try {
					index=i;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			i++;
		}
		return index;
	}

	public boolean isLoaded() {
		return fileDataLoaded;
	}

	public boolean allDataWasStored()
	{
		if(seriesData){
			boolean result=false;
			for(Component comp:cardPane.getComponents()){
				result=result && ((MetaDataUI) comp).allDataWasStored();
			}
			return result;
		}else{
			if(singleView!=null){
				return singleView.allDataWasStored(); 
			}
		}
		return true;
	}

	public boolean hasDataToSave()
	{
		if(seriesData){
			boolean result=false;
			for(Component comp:cardPane.getComponents()){
				result=result ||((MetaDataUI) comp).hasDataToSave();
			}
			return result;
		}else{
			if(singleView!=null){
				return singleView.hasDataToSave(); 
			}
		}
		return false;
	}

	public String getDataToSave_Desc()
	{
		String result="";
		if(seriesData) 
		{
			int index=0;
			for(Component comp:cardPane.getComponents()) {
				result="["+index+"]: "+((MetaDataUI)comp).getDataToSave_Desc()+"\n";
				index++;
			}
			return result;
		}else {
			if(singleView != null) {
				return singleView.getDataToSave_Desc();
			}
		}
		return result;
	}

	public boolean hasUserInput()
	{
		if(seriesData){
			boolean result=false;
			for(Component comp:cardPane.getComponents()){
				result=result ||((MetaDataUI) comp).userInput();
			}
			return result;
		}else{
			if(singleView!=null){
				return singleView.userInput();
			}
		}
		return false;
	}

	public boolean fileDataAreLoaded()
	{
		return fileDataLoaded;
	}

	public boolean parentDataAreLoaded()
	{
		return parentDataLoaded;
	}

	public boolean predefineDataLoaded()
	{
		if(seriesData){
			boolean result=false;
			for(Component comp:cardPane.getComponents()){
				result=result ||((MetaDataUI) comp).predefinitionsAreLoaded();
			}
			return result;
		}else{
			if(singleView!=null){
				return singleView.predefinitionsAreLoaded();
			}
		}
		return false;
	}

	public void setParentDataLoaded(boolean b) {
		parentDataLoaded=b;		
	}



	/** return mapAnnotationData for singleView and List<MapAnnotationData> for seriesData*/ 
	public MapAnnotationObject getMapAnnotation() 
	{
		String name="";
		if(srcFile!=null)
			name=srcFile.getAbsolutePath();

		if(seriesData){
			List<MapAnnotationData> result=new ArrayList<>();
			for(Component comp:cardPane.getComponents()){
				result.add(((MetaDataUI) comp).getMapAnnotation());
			}
			return new MapAnnotationObject(name,result);
		}else{
			if(singleView!=null){
				return new MapAnnotationObject(name,singleView.getMapAnnotation());
			}
		}
		return null;
	}
}
