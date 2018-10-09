package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.view;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ome.xml.model.Detector;
import ome.xml.model.Experiment;
import ome.xml.model.Experimenter;
import ome.xml.model.enums.EnumerationException;
import ome.xml.model.enums.ExperimentType;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model.ExperimentModel;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.ModuleConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.TagNames;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagData;
import org.openmicroscopy.shoola.util.MonitorAndDebug;
import org.slf4j.LoggerFactory;

/**
 * Works for xsi:schemaLocation="http://www.openmicroscopy.org/Schemas/OME/2015-01 
 * @author Susanne Kunis &nbsp;&nbsp;&nbsp;&nbsp; <a
 *         href="mailto:susannekunis@gmail.com">susannekunis@gmail.com</a>
 *
 */
public class ExperimentViewer extends ModuleViewer{

	private static final org.slf4j.Logger LOGGER =
			LoggerFactory.getLogger(ExperimentViewer.class);

	private ExperimentModel data;
	private Box box;

	// available element tags
	private TagData type;
	private TagData description;
	//last name of experimenter
	private TagData expName;

	// Project
	private TagData projectName;
	private TagData group;
	private TagData projectPartner;

	public static final String PROJPARTNER_MAPLABEL="Project Partner";
	public static final String EXPERIMENT_DESC_MAPLABEL="Experiment Desc";
	public static final String EXPERIMENT_TYPE_MAPLABEL="Experiment Type";

	/**
	 * Creates a new instance.
	 * @param model Reference to model.
	 */
	public ExperimentViewer(ExperimentModel model,ModuleConfiguration conf,boolean showPreValues)
	{
		MonitorAndDebug.printConsole("# ExperimentModel::new Instance("+(model!=null?"model":"null")+")");
		this.data=model;

		initComponents(conf);
		initTagList();
		buildGUI();
		resetInputEvent();
		showPredefinitions(conf.getTagList(), showPreValues);
	}

	private void initTagList()
	{
		tagList=new ArrayList<TagData>();
		tagList.add(type);
		tagList.add(expName);
		tagList.add(description);
		tagList.add(projectName);
		tagList.add(group);
		tagList.add(projectPartner);

	}

	/**
	 * Builds and lay out GUI.
	 */
	private void buildGUI() 
	{
		List<JLabel> labels= new ArrayList<JLabel>();
		List<JComponent> comp=new ArrayList<JComponent>();
		addTagToGUI(projectName,labels,comp);
		addTagToGUI(group,labels,comp);
		addTagToGUI(projectPartner,labels,comp);
		addVSpaceToGui(10,labels,comp);
		addTagToGUI(expName,labels,comp);
		expName.setEnable(false);
		addTagToGUI(type,labels,comp);
		addTagToGUI(description,labels,comp);

		addLabelTextRows(labels, comp, gridbag, globalPane);

		gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER; //last
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.weightx = 1.0;

		if(group!=null)group.setEnable(false);
		if(projectName!=null)projectName.setEnable(false);
		// set data
		setGUIData();
	}

	/**
	 * Initialize components.
	 */
	private void initComponents(ModuleConfiguration conf) 
	{
		setLayout(new BorderLayout(5,5));

		gridbag = new GridBagLayout();
		gridBagConstraints = new GridBagConstraints();

		globalPane=new JPanel();
		globalPane.setLayout(gridbag);

		add(globalPane,BorderLayout.NORTH);

		setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

		// init tag layout
		List<TagConfiguration> list=conf.getTagList();
		initTags(list);
	}



	/**
	 * Init given tag and mark it as visible.
	 * @param t
	 */
	protected void initTag(TagConfiguration t) 
	{
		String name=t.getName();
		Boolean prop=t.getProperty();
		Boolean vis=t.isVisible();
		switch (name) {
		case TagNames.E_TYPE:

			setType(null, prop);
			type.setVisible(vis);
			type.setDefaultValues(t.getPossibleValues());
			break;
		case TagNames.DESC:
			setDescription(null, prop);
			description.setVisible(vis);
			break;
			//Set by system
		case TagNames.GROUP:
			setGroupName(null, prop);
			group.setVisible(vis);
			break;
		case TagNames.EXPNAME:
			setName("", prop);
			this.expName.setVisible(vis);
			break;
			//set by system
		case TagNames.PROJECTNAME:
			setProjectName(null, prop);
			projectName.setVisible(vis);
			break;
		case TagNames.PROJECTPARTNER:
			setProjectPartner(null, prop);
			projectPartner.setVisible(vis);
			break;
		default:
			LOGGER.warn("[CONF] unknown tag: "+name );break;
		}
	}

	protected void setPredefinedTag(TagConfiguration t) 
	{
		if(t.getValue()==null || t.getValue().equals(""))
			return;

		predefinitionValLoaded=predefinitionValLoaded || (!t.getValue().equals(""));
		String name=t.getName();
		Boolean prop=t.getProperty();
		switch (name) {
		case TagNames.E_TYPE: 
			if(type!=null && !type.getTagValue().equals(""))
				return;
			ExperimentType et=parseExperimentType(t.getValue());
			if(et==null)
				type.setTagInfo(ERROR_PREVALUE+t.getValue());
			setType(et, prop);
			type.dataHasChanged(true);
			break;
		case TagNames.DESC:
			if(description!=null && !description.getTagValue().equals(""))
				return;
			setDescription(t.getValue(), prop);
			description.dataHasChanged(true);
			break;
			/**Set by system*/
		case TagNames.GROUP:
			//			if(type!=null && !type.getTagValue().equals(""))
			//				return;
			//			setGroupName(t.getValue(), prop);
			//			type.dataSaved(false);
			break;
		case TagNames.EXPNAME:
			//			if(expName!=null && !expName.getTagValue().equals(""))
			//				return;
			//			setName(t.getValue(), prop);
			//			expName.dataSaved(false);
			break;
			//set by system
		case TagNames.PROJECTNAME:
			//			setProjectName(t.getValue(), prop);
			break;
		case TagNames.PROJECTPARTNER:
			if(projectPartner!=null && !projectPartner.getTagValue().equals(""))
				return;
			setProjectPartner(t.getValue(), prop);
			projectPartner.dataHasChanged(true);
			break;
		default:
			LOGGER.warn("[CONF] unknown tag: "+name );break;
		}
	}

	/**
	 * Show data of objective
	 */
	private void setGUIData() 
	{
		if(data!=null) {
			try{ 
				setDescription(data.getExperiment().getDescription(), REQUIRED);}
			catch(NullPointerException e){}
			try{ 
				setType(data.getExperiment().getType(), REQUIRED);
			}catch(NullPointerException e){}
			try{ 
				setProjectPartner(data.getProjectPartnerName(), REQUIRED);
			}catch(NullPointerException e){}
			try{
				//			setName(expContainer.getExperimenter(),REQUIRED);
				setName(data.getExperimenter(),REQUIRED);
			}catch(NullPointerException e){}
			try{
				setGroupName(data.getGroupName(), OPTIONAL);
			}catch(NullPointerException e){}
			try{setProjectName(data.getProjectName(), OPTIONAL);

			}catch(NullPointerException e){}
		}

	}




	/*------------------------------------------------------
	 * Set methods data Values
	 * -----------------------------------------------------*/
	private void setType(ExperimentType value, boolean prop)
	{
		String val= (value != null) ? value.getValue():null;
		if(type == null) 
			type = new TagData(TagNames.E_TYPE,val,prop,TagData.COMBOBOX,getNames(ExperimentType.class));
		else 
			type.setTagValue(val,prop);	
	}

	private ExperimentType parseExperimentType(String value)
	{
		if(value==null || value.equals(""))
			return null;

		ExperimentType t=null;
		try{
			t=ExperimentType.fromString(value);
		}catch(EnumerationException e){
			LOGGER.warn("ExperimentType: "+value+"is not supported");
		}
		return t;

	}

	private void setDescription(String value, boolean prop)
	{
		if(description == null) 
			description = new TagData(TagNames.DESC,value,prop,TagData.TEXTAREA);
		else 
			description.setTagValue(value,prop);	
	}


	private void setName(Experimenter value, boolean prop)
	{
		if(expName == null){ 
			expName = new TagData(TagNames.EXPNAME,getExperimenterName(value),prop,TagData.TEXTFIELD);
		}
		else{ 
			expName.setTagValue(getExperimenterName(value));
		}
	}

	private String getExperimenterName(Experimenter e)
	{

		String res=null;
		if(e!=null){
			String fName= (e.getFirstName()!=null && !e.getFirstName().equals("")) ? e.getFirstName():"";
			String lName=(e.getLastName()!=null && !e.getLastName().equals("")) ? e.getLastName() : "";

			if(fName.equals(""))
				res=lName;
			else
				res=fName+" "+lName;
		}
		return res;
	}
	private void setName(String val, boolean prop) 
	{
		setName(parseExperimenter(val),prop);
	}


	private void setProjectName(String value, boolean prop)
	{
		if(projectName == null) 
			projectName = new TagData(TagNames.PROJECTNAME,value,prop,TagData.TEXTFIELD);
		else 
			projectName.setTagValue(value,prop);
	}

	private void setGroupName(String value, boolean prop)
	{
		if(group == null) 
			group = new TagData(TagNames.GROUP,value,prop,TagData.TEXTFIELD);
		else 
			group.setTagValue(value,prop);
	}

	private void setProjectPartner(String value, boolean prop)
	{
		if(projectPartner == null) 
			projectPartner = new TagData(TagNames.PROJECTPARTNER,value,prop,TagData.TEXTFIELD);
		else 
			projectPartner.setTagValue(value,prop);
	}



	@Override
	public void saveData() 
	{
		if(data==null){
			data=new ExperimentModel(new Experiment(),new Experimenter(),"");
		}
		if(data.getExperiment()==null)
			data.setExperiment(new Experiment());

		if(data.getExperimenter()==null)
			data.setExperimenter(new Experimenter());

		//TODO input checker
		try{
			data.getExperiment().setDescription(description.getTagValue());
		}catch(Exception e){
			LOGGER.error("[DATA] can't read EXPERIMENT description input");
		}
		try{
			data.getExperiment().setType(parseExperimentType(type.getTagValue()));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read EXPERIMENT type input");
		}
		try{
			data.setProjectPartner(projectPartner.getTagValue());
		}catch(Exception e){
			LOGGER.error("[DATA] can't read EXPERIMENT project partner input");
		}
		try{
			data.setProjectName(projectName.getTagValue());
		}catch(Exception e){
			LOGGER.error("[DATA] can't read EXPERIMENT project name input");
		}
		try{
			data.setGroupName(group.getTagValue());
		}catch(Exception e){
			LOGGER.error("[DATA] can't read EXPERIMENT group input");
		}
		try{
			data.setExperimenter(parseExperimenter(expName.getTagValue()));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read EXPERIMENT experimenter input");
		}

		//		data.printValues();
	}

	private Experimenter parseExperimenter(String str)
	{
		Experimenter ex= null;

		if(str!=null && str.length()>0){
			String[] split=str.split("\\s+");
			if(split.length >1){
				ex=new Experimenter();
				ex.setFirstName(split[0]);
				ex.setLastName(split[1]);
			}else{
				return null;
			}
		}
		return ex;
	}
	public List<TagData> getChangedTags() 
	{
		List<TagData> list = new ArrayList<TagData>();
		if(inputAt(projectName)) list.add(projectName);
		if(inputAt(projectPartner))list.add(projectPartner);
		if(inputAt(group))list.add(group);
		if(inputAt(type)) list.add(type);
		if(inputAt(description)) list.add(description);
		if(inputAt(expName)) list.add(expName);

		return list;
	}

	public HashMap<String,String> getMapValuesOfChanges(HashMap<String,String> map)
	{
		if(map==null)
			map=new HashMap<String, String>();

		String id="";
		if(inputAt(projectPartner)) map.put(id+projectPartner.getTagName(),projectPartner.getTagValue());
		if(inputAt(type)) map.put(id+type.getTagName(),type.getTagValue());
		if(inputAt(description)) map.put(id+description.getTagName(),description.getTagValue());
		if(inputAt(expName))map.put(id+expName.getTagName(),expName.getTagValue());


		return map;
	}

}


