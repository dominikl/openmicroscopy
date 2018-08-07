package org.openmicroscopy.shoola.agents.fsimporter.metaChooser;

import info.clearthought.layout.TableLayout;

import org.jdesktop.swingx.JXTaskPane;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import javax.swing.filechooser.FileFilter;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import ome.xml.meta.OMEXMLMetadataRoot;
import ome.xml.model.Experimenter;
import ome.xml.model.Project;
import omero.gateway.model.ExperimenterData;
import omero.gateway.model.MapAnnotationData;
import omero.gateway.model.ProjectData;
import omero.gateway.model.ScreenData;
import omero.model.MapAnnotation;
import omero.model.MapAnnotationI;
import omero.model.NamedValue;

import org.openmicroscopy.shoola.agents.fsimporter.actions.ImporterAction;
import org.openmicroscopy.shoola.agents.fsimporter.chooser.ImportDialog;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.MetaDataModel;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.MetaDataModelObject;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.microscope.CustomViewProperties;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.microscope.MetaDataView;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.microscope.MicroscopeProperties;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.ExceptionDialog;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.FNode;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.ImportUserData;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.MapAnnotationObject;
import org.openmicroscopy.shoola.agents.fsimporter.view.Importer;
import org.openmicroscopy.shoola.env.data.model.FileObject;
import org.openmicroscopy.shoola.env.data.model.ImportableFile;
import org.openmicroscopy.shoola.util.MonitorAndDebug;
import org.openmicroscopy.shoola.util.ui.ClosableTabbedPaneComponent;
import org.openmicroscopy.shoola.util.ui.UIUtilities;
import org.slf4j.LoggerFactory;


/**
 * 
 *  A {@link ClosableTabbedPaneComponent} Dialog used to control and specify metadata 
 *  for files at import queue.
 *  Designed based on xsi:schemaLocation="http://www.openmicroscopy.org/Schemas/OME/2015-01 
 *
 * @author Susanne Kunis &nbsp;&nbsp;&nbsp;&nbsp; <a
 *         href="mailto:susannekunis@gmail.com">susannekunis@gmail.com</a>
 * @version 1.0
 */
public class MetaDataDialog extends ClosableTabbedPaneComponent
implements ActionListener, PropertyChangeListener, TreeSelectionListener, TreeExpansionListener, ListSelectionListener, ItemListener
{

	/** Logger for this class. */
	private static final org.slf4j.Logger LOGGER =
			LoggerFactory.getLogger(MetaDataDialog.class);


	//** Namespaces for parsing from xml */
	public static final String NS_2016_06_07="uos.de/omero/metadata/cellnanos/2015-06-07";
	public static final String CELLNANOS_NS="uos.de/omero/metadata/cellnanos/2015-06-07";
	public static final String MAP_ANNOT_ID = "Annotation:CellNanOs";

	private boolean DEBUG=false;

	/** The title of the dialog. */
	private static final String TITLE = "Specify Metadata";

	/** The owner related to the component. */
	private JFrame owner;
	/** Reference to the model.*/
	private Importer importer;

	/** The type associated to the import.The type of dialog e.g. screen view. */
	private int type;

	/** The approval option the user chose. */
	//private int option;
	JTabbedPane tp;

	/** Button to cancel all imports. */
	private JButton cancelImportButton;

	/** Button to import the files. */
	private JButton importButton;

	/** reset metadata to data from image file*/
	private JButton resetFileDataButton;

	/** save data for current image*/
	private JButton saveDataButton;

	/** save data for all image files in selected directory*/
	private JButton saveAllDataButton;

	private JCheckBox showFileData;
	private JCheckBox showDirData;
	private boolean enabledPredefinedData;
	private JComboBox<String> mics;

	/** Test text area*/
	public JTextArea textArea; 

	private JTree fileTree;
	private JList seriesList;

	private JPanel metaPanel;
	private CustomViewProperties customSettings;

	/** Identifies the style of the document.*/
	private static final String STYLE = "StyleName";
	/** The maximum number of characters in the debug text.*/
	private static final int 	MAX_CHAR = 200000;

	/** lastSelection types*/
	public static final int DIR=0;
	public static final int FILE=1;

	/** type of last selection in tree */
	private int lastSelectionType;

	private FNode lastNode;

	private String micName;
	private String micDesc;

	private boolean holdData;

	private FileFilter fileFilter;

	private boolean disableItemListener;
	private boolean disableTreeListener;

	/** Bound property indicating that the cancel button is pressed. */
	public static final String CANCEL_SELECTION_PROPERTY = "cancelSelection";
	/** Action id indicating to import the selected files. */
	private static final int CMD_IMPORT = 1;
	/** Action id indicating to close the dialog. */
	private static final int CMD_CLOSE = 2;
	/** Action id indicating to reset the names. */
	private static final int LOAD_MIC_SETTINGS=4;

	private static final int CMD_SAVEALL = 5;

	private static final int CMD_RESET = 6;

	private static final int CMD_SAVE = 7;

	private static final int CMD_VIEWFILE=10;
	private static final int CMD_VIEWDIR=11;
	private static final int CHOOSE_MIC=13;

	private MicroscopeProperties currentMic;



	/**
	 * Creates a new instance.
	 * 
	 * @param owner
	 *            The owner of the dialog.
	 * @param filters
	 *            The list of filters.
	 * @param type TODO: necessary?
	 *            The type of dialog e.g. screen view.
	 * @param importerAction
	 *            The cancel-all-imports action.
	 * @param microscope TODO
	 * @param selectedContainer
	 *            The selected container if any.
	 * @param objects
	 *            The possible objects.
	 */
	public MetaDataDialog(JFrame owner, FileFilter[] filters, int type,
			ImporterAction importerAction, Importer importer,JButton importBtn,JButton cancelImportBtn, String microscope)
	{
		super(1, TITLE, TITLE);

		this.owner = owner;
		this.type = type;
		this.importer = importer;
		addImportButtonLink(importBtn);
		addCancelImportButtonLink(cancelImportBtn);
		setClosable(false);
		setCloseVisible(false);
		System.out.println("Microscope conf: "+microscope);
		if(microscope==null || microscope.isEmpty())
			initComponents(filters, importerAction, null);
		else
			initComponents(filters, importerAction, microscope);
		buildGUI();
	}


	private void addNode(FNode parent, FileObject f,ImportableFile fileObj)
	{

		FNode dir = null;
		FNode file = null;
		ImportUserData data=null;

		if(fileObj!=null){
			createParentOME(fileObj);
			data = new ImportUserData(fileObj.getGroup(), fileObj.getParent(), fileObj.getUser());
		}

		if(f.isDirectory()){

			dir=new FNode(new File(f.getAbsolutePath()),data);
			parent.add(dir);
			LOGGER.info("[TREE] Append Dir "+f.getAbsolutePath());
			File[] files=(new File(f.getAbsolutePath()).listFiles((java.io.FileFilter)fileFilter));

			if(files != null && files.length>0){
				for(int i=0; i<files.length;i++){
					addNode(dir,new FileObject(files[i]),null);
				}
			}

			//add files, attention: only image files?
		}else{
			try {
				file=new FNode(new File(f.getAbsolutePath()),data);
				LOGGER.info("[TREE] Append File "+f.getAbsolutePath());
				parent.add(file);
			} catch (Exception e) {
				LOGGER.info("[TREE] Wrong import format "+f.getAbsolutePath());
			}
		}
	}

	private void createParentOME(ImportableFile fileObj) 
	{
		OMEXMLMetadataRoot ome=new OMEXMLMetadataRoot();

		//Experimenter/User/Importer
		ome.addExperimenter(convertExperimenter(fileObj.getUser()));
		// TODO input Partner as ExperimenterRef
		// TODO input partner working group as ExperimenterGroupRef


		//import project
		if(fileObj.isFolderAsContainer() && fileObj.getParent() instanceof ProjectData){
			Project p=new Project();//==import project
			p.setName(fileObj.getParent().asProject().getName().getValue());
			if(fileObj.getParent().asProject().getDescription()!=null){
				p.setDescription(fileObj.getParent().asProject().getDescription().toString());
			}else{
				p.setDescription("");
			}
			ome.addProject(p);
		}else{
			//screen import object
		}
		//TODO: input experiment data

		//TODO: input probe data as XMLAnnotation link by project

	}


	private Experimenter convertExperimenter(ExperimenterData expData) 
	{
		Experimenter exp=new Experimenter();
		try{exp.setFirstName(expData.getFirstName());}catch(Exception e){};
		try{exp.setLastName(expData.getLastName());}catch(Exception e){};

		return exp;
	}


	private void createNodes(List<ImportableFile> files){

		DefaultTreeModel treeModel=(DefaultTreeModel)fileTree.getModel();
		FNode root =(FNode)treeModel.getRoot();

		root.setModelObject(null);

		if(!holdData){
			root.removeAllChildren();

			if(files==null){
				return;
			}
			ImportableFile f;
			Iterator<ImportableFile> j=files.iterator();

			while (j.hasNext()) {
				f = j.next();
				addNode(root,f.getFile(),f);

				String name="";
				if(f.getParent() instanceof ProjectData)
					name=f.getParent().asProject().getName().getValue();
				else if(f.getParent() instanceof ScreenData)
					name=f.getParent().asScreen().getName().getValue();

				LOGGER.info("BUILD FILE TREE: add "+f.getFile().getAbsolutePath()+
						"[group: "+f.getGroup().getName()+", project: "+
						name+"]");
			}
			treeModel.reload();
		}else{
			TreePath path=fileTree.getSelectionPath();

			FNode node = (FNode)fileTree.getLastSelectedPathComponent();

			String dirName=node.getFile().getName();
			FNode dirNode=node;
			if(node!=null && node.isLeaf()){
				LOGGER.info("[DEBUG] node is Leaf");
				dirName=node.getFile().getParentFile().getName();
				dirNode=(FNode) node.getParent();
			}

			insertNodes(files,dirName,dirNode);
			fileTree.updateUI();
			fileTree.expandPath(path);
		}
	}



	/**
	 * Reload node. If node is a directory, load all files of the directory, if is importable file. 
	 * If node==null or node.getFile()==null -> The import queue holds single files. Import only files from list.
	 * @param files
	 * @param dirName
	 * @param dir
	 */
	private void insertNodes(List<ImportableFile> files,String dirName,FNode node) 
	{
		if(node==null || node.getFile()==null){
			node=(FNode)fileTree.getModel().getRoot();
			LOGGER.info("[DEBUG] select root as dir");
			ImportableFile f;
			Iterator<ImportableFile> j=files.iterator();
			node.removeAllChildren();
			while (j.hasNext()) {
				f = j.next();
				LOGGER.info("[DEBUG] insert only file");
				// single file in the importQueue, only insert this and their ome file
				addNode(node,f.getFile(),f);
			}
		}else{
			LOGGER.info("[DEBUG] update node "+dirName);
			node.removeAllChildren();
			File[] fileList=(new File(node.getFile().getAbsolutePath())).listFiles((java.io.FileFilter)fileFilter);

			if(fileList != null && fileList.length>0){
				for(int i=0; i<fileList.length;i++){
					addNode(node,new FileObject(fileList[i]),null);
				}
			}
		}
	}



	private void initComponents(FileFilter[] filters,
			ImporterAction importerAction, String microscope)
	{
		holdData=false;
		disableTreeListener=false;

		resetFileDataButton=new JButton("Clear Input");
		resetFileDataButton.setBackground(UIUtilities.BACKGROUND);
		resetFileDataButton.setToolTipText("Reset metadata. Show only metadata of selected image file.");
		resetFileDataButton.setActionCommand("" + CMD_RESET);
		resetFileDataButton.addActionListener(this);
		resetFileDataButton.setEnabled(false);

		saveDataButton=new JButton("Save");
		saveDataButton.setBackground(UIUtilities.BACKGROUND);
		saveDataButton.setToolTipText("Save input to selected file.");
		saveDataButton.setActionCommand("" + CMD_SAVE);
		saveDataButton.addActionListener(this);
		saveDataButton.setEnabled(false);

		saveAllDataButton=new JButton("Save To All");
		saveAllDataButton.setBackground(UIUtilities.BACKGROUND);
		saveAllDataButton.setToolTipText("Save input to all images of selected directory.");
		saveAllDataButton.setActionCommand("" + CMD_SAVEALL);
		saveAllDataButton.addActionListener(this);
		saveAllDataButton.setEnabled(false);

		mics=new JComboBox<String>(MicroscopeProperties.availableMics);
		mics.setActionCommand(""+CHOOSE_MIC);
		mics.addActionListener(this);
		int indexMic=MicroscopeProperties.getMicIndex(microscope);
		if(indexMic!=-1){
			mics.setSelectedIndex(indexMic); 
		}else {
			mics.setSelectedIndex(0);
		}

		initFilterViewBar();

		if(customSettings==null){
			currentMic=MicroscopeProperties.getMicClass(MicroscopeProperties.availableMics[mics.getSelectedIndex()]);
			customSettings=currentMic.getViewProperties();
			customSettings.setMapr(currentMic.getMapr());
		}
		if(customSettings==null){
			customSettings=new CustomViewProperties();
			customSettings.init();
		}        

		micName=customSettings.getMicName();
		micDesc=customSettings.getMicDesc();
		MetaDataView view=new MetaDataView();

		metaPanel=new JPanel(new BorderLayout());
		metaPanel.add(view,BorderLayout.CENTER);


		initFileTree();

		initSeriesList();

		lastSelectionType=DIR;
	}


	/**
	 * 
	 */
	public void initSeriesList() {
		seriesList = new JList();
		seriesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		seriesList.setSelectedIndex(0);
		seriesList.addListSelectionListener(this);
		seriesList.setVisibleRowCount(5);
	}


	/**
	 * Create a tree that allows one selection at a time
	 */
	public void initFileTree() 
	{
		FNode rootNode=new FNode("ImportQueue");

		fileTree = new JTree(rootNode);
		fileTree.setRootVisible(true);
		fileTree.setShowsRootHandles(true);
		fileTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

		//Listen for when the selection changes.
		fileTree.addTreeSelectionListener(this);
		fileTree.addTreeExpansionListener(this);
	}

	/**
	 * Init GUI of filter view panel to filter which data should displayed
	 */
	private void initFilterViewBar() 
	{
		String tooltipText="<html>Show file data for selection:<br>"
				+ "Attention: file data will overwrite by dir data and pre data.</html>";
		showFileData=new JCheckBox("File Data");
		showFileData.setToolTipText(tooltipText);
		showFileData.addItemListener(this);
		showFileData.setEnabled(false);

		tooltipText="<html>Show parent directory data for selection(inherit from parent):<br>"
				+ "Attention: dir data overwrites file data and pre data.</html>";
		showDirData=new JCheckBox("Dir Data");
		showDirData.addItemListener(this);
		showDirData.setToolTipText(tooltipText);
		showDirData.setEnabled(false);

		enabledPredefinedData=true;
	}


	/**
	 * Builds and lays out file view right side
	 * @return JPanel
	 */
	private JPanel buildFileView(){
		JPanel fileView=new JPanel();
		fileView.setLayout(new BorderLayout(0,0));

		//Create the scroll pane and add the tree to it
		JScrollPane treeView = new JScrollPane(fileTree);

		fileView.add(treeView);

		return fileView;
	}
	
	/**
	 * Builds and lays out file view right side and the seriesbrowser
	 * @return JPanel
	 */
	private JPanel buildFileViewExtended()
	{
		//Create the list and put it in a scroll pane.
		JPanel pane=new JPanel();
		pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));


		JScrollPane listScrollPane=new JScrollPane(seriesList);
		pane.add(new JLabel("Series:"));
		pane.add(listScrollPane);

		JSplitPane splitPane;		
		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,buildFileView(),pane);
		splitPane.setResizeWeight(0.5);
		splitPane.setDividerLocation(200);
		JPanel fileView=new JPanel();
		fileView.setLayout(new BorderLayout(0,0));

		fileView.add(splitPane);

		return fileView;
	}

	/**
	 * Builds and lays out the tool bar.
	 * @return See above.
	 */
	private JPanel buildToolBarRight() {
		JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		bar.add(resetFileDataButton);
		bar.add(Box.createHorizontalStrut(5));
		bar.add(cancelImportButton);
		bar.add(Box.createHorizontalStrut(5));
		bar.add(importButton);
		bar.add(Box.createHorizontalStrut(10));
		return bar;
	}

	private JPanel buildToolBarMetaUI()
	{
		JPanel bar = new JPanel();
		bar.setLayout(new BoxLayout(bar, BoxLayout.X_AXIS));

		JPanel barM = buildFilterViewBar();

		JPanel barR=new JPanel(new FlowLayout(FlowLayout.RIGHT));
		//mics
		barR.add(new JLabel("Load Hardware Specification:"));
		barR.add(Box.createHorizontalStrut(2));
		barR.add(mics);

		JPanel barRR=buildToolBarRight();

		bar.add(barM);
		bar.add(barR);
		bar.add(barRR);
		return bar;
	}


	/**
	 * @return
	 */
	private JPanel buildFilterViewBar() 
	{
		JPanel barM=new JPanel(new FlowLayout(FlowLayout.LEFT));
		barM.add(new JLabel("View: "));
		barM.add(Box.createHorizontalStrut(5));
		barM.add(showFileData);
		barM.add(Box.createHorizontalStrut(5));
		barM.add(showDirData);
		barM.add(Box.createHorizontalStrut(10));
		return barM;
	}


	/**
	 * Init and layout gui components 
	 */
	private void buildGUI()
	{
		setLayout(new BorderLayout(0,0));

		JSplitPane splitPane;		
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,buildFileViewExtended(),metaPanel);
		splitPane.setResizeWeight(0.5);
		splitPane.setDividerLocation(150);

		JPanel mainPanel=new JPanel();
		double[][] mainPanelDesign = { { TableLayout.FILL },
				{ TableLayout.PREFERRED, TableLayout.FILL } };
		mainPanel.setLayout(new TableLayout(mainPanelDesign));
		mainPanel.setBackground(UIUtilities.BACKGROUND);
		mainPanel.add(splitPane, "0, 1");

		this.add(mainPanel, BorderLayout.CENTER);

		JPanel controls = new JPanel();
		controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));

		// Lays out the buttons.
		JPanel bar = new JPanel();
		bar.setLayout(new BoxLayout(bar, BoxLayout.X_AXIS));
		bar.add(buildToolBarMetaUI());
		controls.add(new JSeparator());
		controls.add(bar);

		this.add(controls, BorderLayout.SOUTH);
	}

	/**
	 * 
	 * @return name of microscope of loaded hardware or predefined values.
	 */
	public String getMicName() {
		return micName;
	}


	public void setMicName(String micName) {
		this.micName = micName;
	}

	/**
	 * @return description for selected microscope hardware specification
	 */
	public String getMicDesc() {
		return micDesc;
	}

	public void setMicDesc(String micDesc) {
		this.micDesc =micDesc;
	}

	/**
	 * Generate MetaDataView for selected node and load predefined value, parent, import and model data
	 * if there was selected in the bottom filter bar.
	 * @param node selected node in the filetree
	 * @param parentVisible =true shows also parent data
	 * @param fileVisible = true shows also file data
	 * @param predefinedVisible = true shows also predefine values
	 */
	private void loadAndShowFilteredDataForSelection(FNode node,
			boolean parentVisible,boolean fileVisible,boolean predefinedVisible)
	{
		MonitorAndDebug.printConsole("#MetaDataDialog::loadAndShowFilteredDataForSelection(): "
				+ "parent="+parentVisible+", file="+fileVisible+", predefined="+predefinedVisible);
		String file=null;
		if(node==null || (file=getSelectedFilePath(node))==null)
			return;

		LOGGER.debug("[TREE] -- FILTER VIEW Node: "+node.toString()+" ##############################################");

		//import user data
		ImportUserData importData = getImportData();

		//get parent dir model data 
		MetaDataModel parentModel=null;
		if(parentVisible)
			parentModel=getParentMetaDataModel(node);

		MetaDataView view=null;

		// is selection a file or directory
		if(file.equals("")){
			view = loadAndShowDataForDirectory(node, file, importData,
					parentModel, view, true, predefinedVisible);
		}else{
			try{
				view = loadAndShowDataForFile(file, importData, parentModel, view, fileVisible, predefinedVisible);
			}catch(Exception e){
				LOGGER.error("[DATA] CAN'T read METADATA");
				ExceptionDialog ld = new ExceptionDialog("Metadata Error!", 
						"Can't read given metadata of "+file,e,
						this.getClass().getSimpleName());
				ld.setVisible(true);
				fileTree.setSelectionPath(fileTree.getSelectionPath().getParentPath());
				this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				return;
			}
		}
		showMetaDataView(view);

		setFilterView(view);

		revalidate();
		repaint();
	}

	/**
	 * Generate MetaDataView for selected node and load predefined value, parent, import and model data.
	 * @param node selected node in the filetree
	 * @param reload TODO
	 */
	private void loadAndShowDataForSelection(FNode node, boolean reload)
	{
		String file=null;
		if(node==null || (file=getSelectedFilePath(node))==null)
			return;

		LOGGER.debug("[TREE] -- Node: "+node.toString()+" ##############################################");

		//import user data
		ImportUserData importData = getImportData();

		//get parent dir model data 
		MetaDataModel parentModel=getParentMetaDataModel(node);

		// TODO: new View with available model
		// if a view still available, load it 
		MetaDataView view=node.getView();
		// an update of parent data was happend?
		boolean loadParentDataAtUpdate=false;
		if(view!=null){
			loadParentDataAtUpdate=view.parentDataAreLoaded();
		}
		// is selection a file or directory
		if(file.equals("")){
			view = loadAndShowDataForDirectory(node, file, importData,
					parentModel, view, true, true && enabledPredefinedData);

			boolean pLoad=loadParentDataAtUpdate || view.parentDataAreLoaded();
			MonitorAndDebug.printConsole("# MetaDataView::setParentDataLoaded(): "+pLoad+" - of node "+node.getAbsolutePath());
			view.setParentDataLoaded(pLoad);
		}else{
			try{
				//if model still exists, it was still updated by parent data at deselectedNodeAction()
				if(node.hasModelObject() && !reload){
					System.out.println("\n USE AVAILABLE VIEW\n");
					view = node.getView();
					view.setVisible();
				}else{
					System.out.println("\n NEW VIEW\n");
					view = loadAndShowDataForFile(file, importData, parentModel, view, true, true && enabledPredefinedData);
				}
			}catch(Exception e){
				LOGGER.error("[DATA] CAN'T read METADATA");
				ExceptionDialog ld = new ExceptionDialog("Metadata Error!", 
						"Can't read given metadata of "+file,e,
						this.getClass().getSimpleName());
				ld.setVisible(true);
				fileTree.setSelectionPath(fileTree.getSelectionPath().getParentPath());
				this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				return;
			}
		}
		//    	view.getModelObject().isUpToDate(true);

		showMetaDataView(view);
		setFilterView(view);

		revalidate();
		repaint();
	}


	/**
	 * Add MetaDataView to main panel and show list of series if exists.
	 * @param panel
	 * @param view
	 */
	public void showMetaDataView(MetaDataView view) {
		JComponent panel=null;
		panel=view;
		metaPanel.removeAll();
		if(panel!=null){
			metaPanel.add(panel,BorderLayout.CENTER);
			DefaultListModel list=view.getSeries();
			if(list!=null){
				seriesList.setModel(list);
				seriesList.setSelectedIndex(0);
			}else{
				seriesList.setModel(new DefaultListModel());
			}
		}
	}

	/**
	 * Set values of filter view bar according of data that will be set inside the given view.
	 * While the values are set, the listener is disabled.
	 * @param view
	 */
	private void setFilterView(MetaDataView view) 
	{
		disableItemListener=true;
		showDirData.setSelected(view.parentDataAreLoaded());
		showFileData.setSelected(view.fileDataAreLoaded());
		disableItemListener=false;
	}


	/**
	 * Generate MetaDataView for selected file and load predefined value, parent, import and model data.
	 * @param file
	 * @param importData
	 * @param parentModel
	 * @param view
	 * @param showFileData TODO
	 * @param showPreValues TODO
	 * @return
	 */
	public MetaDataView loadAndShowDataForFile(String file,
			ImportUserData importData, MetaDataModel parentModel,
			MetaDataView view, boolean showFileData, boolean showPreValues) throws Exception
	{
		lastSelectionType=FILE;

		MonitorAndDebug.printConsole("# MetaDataDialog::loadAndShowDataForFile() : showFileData = "+showFileData+
				", showPreVal = "+showPreValues+", enablePreVal= "+enabledPredefinedData);

		String hasParentModel=parentModel==null ? "null" : "available";
		LOGGER.debug("load and show data for file: parentModel="+hasParentModel);

		view = new MetaDataView(file, importData, parentModel, this, showFileData, showPreValues);
		view.setVisible();
		return view;
	}


	/**
	 * Generate MetaDataView for selected directory and load predefined value, parent, import and model data.
	 * @param node
	 * @param file
	 * @param importData
	 * @param parentModel
	 * @param view
	 * @param useCurrentModel TODO
	 * @param showPreValues TODO
	 * @return
	 */
	public MetaDataView loadAndShowDataForDirectory(FNode node, String file,
			ImportUserData importData, MetaDataModel parentModel,
			MetaDataView view, boolean useCurrentModel, boolean showPreValues) 
	{
		lastSelectionType=DIR;

		MonitorAndDebug.printConsole("# MetaDataDialog::loadAndShowDataForDir() : "+
				", showPreVal = "+showPreValues+", enablePreVal= "+enabledPredefinedData);

		MetaDataModel currentDirModel=null;
		if(useCurrentModel)
			currentDirModel=getCurrentSelectionMetaDataModel(node);

		String hasParentModel=parentModel==null ? "null" : "available";
		String hasCurrentModel=currentDirModel==null ? "null" : "available";
		LOGGER.debug("load and show data for directory: parentModel="+hasParentModel
				+", current model = "+hasCurrentModel);

		try {
			view = new MetaDataView(file, importData, parentModel, currentDirModel,this, showPreValues);
			view.setVisible();
		} catch (Exception e) {
			//				catch (DependencyException | ServiceException e) {
			LOGGER.error("[DATA] CAN'T read METADATA");
			ExceptionDialog ld = new ExceptionDialog("Metadata Error!", 
					"Can't read given metadata of "+file,e,this.getClass().getSimpleName());
			ld.setVisible(true);
		}
		return view;
	}




	/**
	 * Call routines after deselect a node.
	 * This routines are: save view, save input to model and update childs of the node, notice deselected node.
	 */
	public void deselectNodeAction(FNode node) {

		if(node!=null){
			MonitorAndDebug.printConsole("# MetaDataDialog::deselectNodeAction("+node.getAbsolutePath()+")");
			LOGGER.debug("MetaDataDialog::Deselect node action for "+node.getAbsolutePath());
			//save input
			saveInputToModel(node,true);
			//reset series list
			((DefaultListModel) seriesList.getModel()).removeAllElements();

			lastNode=node;
			node.printMaps();
			MonitorAndDebug.printConsole("...# MetaDataDialog::deselectNodeAction("+node.getAbsolutePath()+")");
		}
	}

	/**
	 * 
	 * @param panel
	 * @return active MetaDataView component from metapanel
	 */
	private MetaDataView getMetaDataView(JPanel panel)
	{
		if(panel.getComponentCount()>0){
			Component c=panel.getComponent(0);
			if(c instanceof MetaDataView){
				MonitorAndDebug.printConsole("SET VIEW :"+panel.getComponentCount());
				return (MetaDataView) c;
			}else{
				MonitorAndDebug.printConsole("NO VIEW AVAILABLE :");
			}
		}else{
			MonitorAndDebug.printConsole("NO VIEW AVAILABLE :");
		}
		return null;
	}

	

	/**
	 * save data model of  node, if any user input available and update all childs 
	 * that still have a model if deselected node== directory
	 */
	private void saveInputToModel(FNode node,boolean showSaveDialog) 
	{
		if(node!=null){
			MonitorAndDebug.printConsole("# MetaDataDialog::saveInputToModel():"+node.getAbsolutePath());

			//save current view to deselect node object
			node.setView(getMetaDataView(metaPanel));
			//    		MonitorAndDebug.printConsole("# MetaDataDialog::saveInputToModel(): GUI INPUT: "+node.getView().hasUserInput());

			// changes available?
			boolean saveToAll =false;

			if(node.getView()!=null){
				node.saveExtendedData();
				if(node.getView().allDataWasStored() ){
					MonitorAndDebug.printConsole("DATA FOR THIS VIEW STORED: TRUE");
				}else if(node.getView().hasDataToSave()){
					MonitorAndDebug.printConsole("DATA FOR THIS VIEW STORED: FALSE");
					saveToAll =true;
				}
			}

			if(saveToAll){
				try{
					node.saveModel();
				}catch (Exception e){
					LOGGER.warn("Can't save model for this node: "+node.getAbsolutePath());
				}
				if(!node.isLeaf())
					updateChildsOfDirectory(node, null);
				else{
					if(node.hasModelObject()){
						MonitorAndDebug.printConsole("\t clear list for "+node.getAbsolutePath());
						node.getModelObject().clearListOfModifications();
					}
				}
			}
		}
	}


	/**
	 * GUI input : Update all child views of type directory with EXISTING MODEL with tags changes
	 * @param node
	 */
	private void updateChildsOfDirectory(FNode node,MetaDataModelObject modelToInherit) 
	{
		MonitorAndDebug.printConsole("# MetaDataDialog::updateChildsOfDirectories of "+node.getAbsolutePath());
		LOGGER.debug("Update childs of "+node.getAbsolutePath());

		int numChilds=node.getChildCount();
		MetaDataModelObject nodeModel=null;

		if(node.hasModelObject()){
			MonitorAndDebug.printConsole("\t Use own model");
			nodeModel=node.getModelObject();
		}
		else if(modelToInherit!=null){
			MonitorAndDebug.printConsole("\t Use parent model");
			nodeModel=modelToInherit;
		}else
			return;

		for(int i=0; i<numChilds;i++){
			FNode child = (FNode) node.getChildAt(i);

			if(child.hasModelObject() ){
				MonitorAndDebug.printConsole("\t ...update existing model/view of "+child.getAbsolutePath());
				LOGGER.debug("[DEBUG] Update "+child.getAbsolutePath());
				try {
					child.getModelObject().updateData(nodeModel);
					if(child.getView()!=null){
						child.getView().setParentDataLoaded(true);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//for all subdirectories updateChilds
				if(!child.isLeaf()){
					updateChildsOfDirectory(child,null);
				}
			}else{
				MonitorAndDebug.printConsole("\t ...don't update view of "+child.getAbsolutePath());
				//for all subdirectories updateChilds
				if(!child.isLeaf()){
					updateChildsOfDirectory(child,nodeModel);
				}
			}
		}//for
		if(node.hasModelObject()){
			MonitorAndDebug.printConsole("# MetaDataDialog::updateChildsOfDirectory(): clear list for "+node.getAbsolutePath());
			node.getModelObject().clearListOfModifications();
		}
	}

	private MetaDataModel getParentMetaDataModel(FNode node) 
	{
		if(node!=null){
			FNode parent=(FNode) node.getParent();

			if(parent!=null){
				if(parent.hasModelObject()){
					// parent is a directory with only one metadatamodel
					return parent.getModelOfSeries(0);
				}else{
					return getParentMetaDataModel(parent);
				}
			}
		}
		return null;
	}

	/**
	 * 
	 * @return model of current selected directory, if exist's. If selection is a file, return null.
	 */
	private MetaDataModel getCurrentSelectionMetaDataModel(FNode node)
	{
		if(node!=null && !node.isLeaf()){
			LOGGER.info("[GUI] -- Load current selection model");
			return node.getModelOfSeries(0);
		}else{
			LOGGER.info("[GUI] -- No model for current selection");
		}

		return null;
	}


	/**
	 * 
	 * @return absolute path of selected file. If selected component is a dir return empty string 
	 */
	private String getSelectedFilePath(FNode node)
	{
		String fname="";

		if (node!=null && node.isLeaf()) {
			fname=node.getAbsolutePath();
		} 
		return fname;
	}



	private ImportUserData getImportData()
	{
		ImportUserData data=null;
		try{
			FNode node = (FNode)fileTree.getLastSelectedPathComponent();

			if(node == null) return null;

			while(!node.hasImportData()){
				node=(FNode) node.getParent();
			}
			data=node.getImportData();
		}catch(Exception e){
			LOGGER.warn("No import data available");
			return null;
		}
		return data;
	}



	public void refreshFileView(List<ImportableFile> files, FileFilter fileFilter)
	{

		this.fileFilter=fileFilter;
		if(files==null || files.size()==0){
			LOGGER.info("No data select");
			// TODO: changes should be save
			MonitorAndDebug.printConsole("# MetaDataDialog::resfreshFileView(): Filelist is null -> IMPORT ?");
			//        	disableTreeListener=true;
		}else
			MonitorAndDebug.printConsole("# MetaDataDialog::refreshFileView(): list= "+files.size());

		metaPanel.removeAll();
		createNodes(files);
		disableTreeListener=false;
	}


	/**
	 * Shows the metaDataChooser dialog.
	 * 
	 * @return The option selected.
	 */
	public int showDialog() {
		UIUtilities.setLocationRelativeToAndShow(getParent(), this);
		return 0;//option;
	}

	/**
	 * Shows the metaDataChooser dialog.
	 * 
	 * @return The option selected.
	 */
	public int centerDialog() {
		UIUtilities.centerAndShow(this);
		return 0;//option;
	}

	/**
	 * Reacts to property fired by the table.
	 * 
	 * @see PropertyChangeListener#propertyChange(PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		String name = evt.getPropertyName();
		LOGGER.info("[DEBUG] MetaDataDialog notice propertyChange "+name);
	}

	/**
	 * Cancels or imports the files.
	 * 
	 * @see ActionListener#actionPerformed(ActionEvent)
	 */
	public void actionPerformed(ActionEvent evt) {
		int commandId = Integer.parseInt(evt.getActionCommand());

		switch (commandId) {
		case CMD_CLOSE:
			LOGGER.info("[GUI-ACTION] -- close");
			firePropertyChange(CANCEL_SELECTION_PROPERTY,
					Boolean.valueOf(false), Boolean.valueOf(true));
			break;

		case CHOOSE_MIC:
			String newSelection=MicroscopeProperties.availableMics[mics.getSelectedIndex()];
			System.out.println("--- LOAD "+newSelection+" HARDWARE SETTINGS ---");

			currentMic=MicroscopeProperties.getMicClass(newSelection);
			// TODO: refresh view


			customSettings=currentMic.getViewProperties();
			customSettings.setMapr(currentMic.getMapr());
			if(fileTree!=null){
				deselectNodeAction((FNode)fileTree.getLastSelectedPathComponent());

				//TODO reload current view if changes
				loadAndShowDataForSelection((FNode)fileTree.getLastSelectedPathComponent(), true);
			}
			// inform ImporterControl about this changes
			String newTitle=customSettings.getMicName()+(customSettings.getMicDesc()!=null?(": "+customSettings.getMicDesc()): "");
			firePropertyChange(ImportDialog.REFRESH_TITLE,null,newTitle);
			break;
		case CMD_SAVE:
			LOGGER.info("[GUI-ACTION] -- save");
			MonitorAndDebug.printConsole("\n+++ EVENT: SAVE ++++\n");
			String fileName=getSelectedFilePath((FNode)fileTree.getLastSelectedPathComponent());
			if(fileName!=null){
				String srcFile=fileName.equals("") ? "" : fileName;
				try {
					saveInputToModel((FNode) fileTree.getLastSelectedPathComponent(),false);
					//					saveCurrentNodeAndUpdate(path,srcFile);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			revalidate();
			repaint();
			break;
		case CMD_SAVEALL:
			LOGGER.info("[GUI-ACTION] -- save all");
			MonitorAndDebug.printConsole("\n+++ EVENT: SAVE ALL ++++\n");
			//only for directory
			FNode parentNode = (FNode)fileTree.getLastSelectedPathComponent();
			saveInputToModel(parentNode,false);
			break;
		case CMD_RESET:
			LOGGER.info("[GUI-ACTION] -- reset");
			MonitorAndDebug.printConsole("\n +++ EVENT RESET INPUT +++\n");
			FNode selection=(FNode)fileTree.getLastSelectedPathComponent();
			//TODO: profile default data eliminate
			//file
			String file = getSelectedFilePath(selection);
			//clear node model data
			selection.setModelObject(null);
			MetaDataView view=null;
			try {

				if(!selection.isLeaf()){
					view = loadAndShowDataForDirectory(selection, file, null,
							null, view, true, true && enabledPredefinedData);
				}else{
					view = loadAndShowDataForFile(file, null, null, view, true, true && enabledPredefinedData);
				}
				//        		((MetaDataView)metaPanel.getComponent(0)).reset();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			showMetaDataView(view);
			setFilterView(view);
			revalidate();
			repaint();

			break;
		case CMD_VIEWFILE:
			Border redline = BorderFactory.createLineBorder(Color.red);
			Border compound= BorderFactory.createRaisedBevelBorder();
			break;

		}
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) 
	{
		if(!disableTreeListener){
			FNode selectedNode=null;
			FNode lastSelectedNode=null;

			TreePath[] paths = e.getPaths();

			// maximum 2 paths in the list -> last and current
			for (int i = 0; i < paths.length; i++) {
				if (e.isAddedPath(i)) {
					selectedNode=(FNode)paths[i].getLastPathComponent();
				} else {
					lastSelectedNode = (FNode)paths[i].getLastPathComponent();
				}
			}

			String action=lastSelectedNode==null?"\n+++ INIT TREE +++":"\n+++ EVENT TREE DESELECT "+lastSelectedNode.getAbsolutePath()+"+++\n";
			MonitorAndDebug.printConsole(action);
			deselectNodeAction(lastSelectedNode);

			action=selectedNode==null?"":"\n+++ EVENT TREE SELECT "+selectedNode.getAbsolutePath()+"+++\n";
			MonitorAndDebug.printConsole(action);
			selectNodeAction(selectedNode);
		}
	}

	/**
	 * Call methods for selected node in the tree.
	 * @param selectedNode
	 */
	private void selectNodeAction(FNode selectedNode) 
	{
		if(selectedNode!=null ){
			MonitorAndDebug.printConsole("\n# MetaDataDialog::selectNodeAction("+selectedNode.getAbsolutePath()+")");
			seriesList.setModel(new DefaultListModel());
			//		   selectedNode.printMaps();
			LOGGER.debug("Select node action for "+selectedNode.getAbsolutePath());

			enableSaveButtons(selectedNode.isLeaf());
			enableViewButtons();
			resetFileDataButton.setEnabled(true);
			loadAndShowDataForSelection(selectedNode, false);

			revalidate();
			repaint();
		}		
	}


	/**
	 * If selected node is a leaf (file or dir without childs) set save enabled and disabled save all.
	 * @param isLeaf
	 */
	public void enableSaveButtons(boolean isLeaf) 
	{
		if(isLeaf){
			saveDataButton.setEnabled(true);
			saveAllDataButton.setEnabled(false);
		}else{
			saveDataButton.setEnabled(false);
			saveAllDataButton.setEnabled(true);
		}
	}

	public void enableViewButtons()
	{
		showFileData.setEnabled(true);
		showDirData.setEnabled(true);
	}


	@Override
	public void treeCollapsed(TreeExpansionEvent arg0) 
	{
	}


	@Override
	public void treeExpanded(TreeExpansionEvent arg0) 
	{
	}


	/**
	 * Show selected series
	 */
	@Override
	public void valueChanged(ListSelectionEvent e) 
	{
		if (e.getValueIsAdjusting() == false) {
			if (seriesList.getSelectedIndex() != -1) {
				if(metaPanel.getComponentCount()>0){
					Component c=metaPanel.getComponent(0);
					if(c instanceof MetaDataView){
						((MetaDataView) c).updateGlobalSeriesData();
						((MetaDataView) c).showSeries((String)seriesList.getSelectedValue());
					}
				}
			}
		}
	}


	@Override
	public void itemStateChanged(ItemEvent e) 
	{
		MonitorAndDebug.printConsole("\n+++EVENT : SET FILTER FOR VIEW +++\n");
		if(!disableItemListener)
			showFilteredData(showDirData.isSelected(),showFileData.isSelected(),
					false);
	}


	private void showFilteredData(boolean dirData, boolean fileData,
			boolean customData) 
	{
		MonitorAndDebug.printConsole("# MetaDataDialog::showFilteredData(): "+dirData+", "+fileData+", "+customData+", enablePreVal= "+enabledPredefinedData);

		loadAndShowFilteredDataForSelection((FNode)fileTree.getLastSelectedPathComponent(),
				dirData,fileData,customData);
	} 

	public CustomViewProperties getCustomViewProperties()
	{
		return customSettings;
	}


	public void addImportButtonLink(JButton importButton) 
	{
		this.importButton=importButton;

	}

	private void addCancelImportButtonLink(JButton cancelImportBtn) {
		this.cancelImportButton=cancelImportBtn;
	}


	/**
	 * Save all input of editor as mapannotation for import
	 * @param text
	 */
	public void saveChanges(String text) 
	{
		MonitorAndDebug.printConsole("\n+++ EVENT: IMPORT SAVE CHANGES ++++\n");
		FNode node=(FNode)fileTree.getLastSelectedPathComponent();
		saveInputToModel(node, true);
		saveMapAnnotations();
	}

	private void saveMapAnnotations() {
		DefaultTreeModel treeModel=(DefaultTreeModel)fileTree.getModel();
		FNode root =(FNode)treeModel.getRoot();

		// walk trough tree
		saveMapAnnotationOfSubDir(root, null);
	}

	private void saveMapAnnotationOfSubDir(FNode node,MapAnnotationObject parentMap)
	{
		if(node.isLeaf()){
			MapAnnotationObject maps=node.getMapAnnotation();

			// no view exists and no changes input for node
			if(maps==null && parentMap!=null){
				MonitorAndDebug.printConsole("\t"+node.getAbsolutePath()+"\t use parent mapAnnotation");
				maps=new MapAnnotationObject(parentMap);
				maps.setFileName(node.getAbsolutePath());
			}
			if(maps!=null){
				firePropertyChange(ImportDialog.ADD_MAP_ANNOTATION,null,maps);
				MonitorAndDebug.printConsole("\t"+maps.getMapAnnotationList());
			}else{
				MonitorAndDebug.printConsole("\t"+node.getAbsolutePath()+"\t mapAnnotation is null");
			}
		}else{
			Enumeration children =node.children();
			while(children.hasMoreElements()){
				FNode subNode=(FNode)children.nextElement();
				MapAnnotationObject maps=subNode.getMapAnnotation();

				// no view exists and no changes input for node
				if(maps==null && parentMap!=null){
					MonitorAndDebug.printConsole("\t"+subNode.getAbsolutePath()+"\t use parent mapAnnotation");
					maps=new MapAnnotationObject(parentMap);
				}
				saveMapAnnotationOfSubDir(subNode, maps);
			}
		}
	}


	private boolean showSaveInputDialog(String text)
	{
		int reply = JOptionPane.showConfirmDialog(null, 
				text,"Save Input", JOptionPane.YES_NO_OPTION);
		if (reply == JOptionPane.YES_OPTION) {
			return true;
		}else {
			return false;
		}
	}


	public MicroscopeProperties getMicroscopeProperties()
	{
		return currentMic;
	}

	public void setMicroscopeProperties(MicroscopeProperties m)
	{
		currentMic=m;
	}

}
