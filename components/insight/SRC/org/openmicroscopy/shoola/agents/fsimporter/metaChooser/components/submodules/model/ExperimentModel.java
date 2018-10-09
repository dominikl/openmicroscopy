package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import loci.formats.FormatException;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.MetaDataDialog;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.TagNames;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagData;
import org.openmicroscopy.shoola.util.MonitorAndDebug;
import org.slf4j.LoggerFactory;

import ome.units.unit.Unit;
import ome.xml.model.Experiment;
import ome.xml.model.Experimenter;
import ome.xml.model.MapAnnotation;
import ome.xml.model.MapPair;
import ome.xml.model.enums.EnumerationException;
import ome.xml.model.enums.ExperimentType;
import ome.xml.model.enums.handlers.ExperimentTypeEnumHandler;

/**
 * Works for xsi:schemaLocation="http://www.openmicroscopy.org/Schemas/OME/2015-01 
 * @author Susanne Kunis &nbsp;&nbsp;&nbsp;&nbsp; <a
*         href="mailto:susannekunis@gmail.com">susannekunis@gmail.com</a>
 *
 */
public class ExperimentModel {
	
	 private static final org.slf4j.Logger LOGGER =
	    	    LoggerFactory.getLogger(ExperimentModel.class);
	 
		public static final String PROJPARTNER_MAPLABEL="Project Partner";
		public static final String EXPERIMENT_DESC_MAPLABEL="Experiment Desc";
		public static final String EXPERIMENT_TYPE_MAPLABEL="Experiment Type";

	Experiment experiment;
	Experimenter projectPartner;
	String projectName;
	String group;
	Experimenter experimenter;

	private HashMap<String, String> map;
	
	
	public ExperimentModel()
	{
		experiment=null;
		projectPartner=null;
		experimenter=null;
	}
	
	//copy constructor
	public ExperimentModel(ExperimentModel orig)
	{
		experiment=orig.experiment;
		experimenter=orig.experimenter;
		projectPartner=orig.projectPartner;
	}
	
	
	public ExperimentModel(Experiment exp, Experimenter exper, String projPartner)
	{
		if(exp!=null)
			setExperiment(exp);
		if(exper!=null)
			setExperimenter(exper);
		if(projPartner!=null)
			setProjectPartner(projPartner);
	}

	
	public HashMap<String,String> getMap()
	{
		return map;
	}
	
	public void setMap(HashMap<String,String> newMap)
	{
		map=newMap;
	}
	
	public Experiment getExperiment() {
		return experiment;
	}

	public void setExperiment(Experiment experiment) {
		this.experiment = experiment;
	}
	
	public Experiment getContainerAsExp()
	{
		//TODO ids
		experiment.linkExperimenter(experimenter);
		experiment.linkExperimenter(projectPartner);
		return experiment;
	}

	public Experimenter getProjectPartner() {
		return projectPartner;
	}
	public String getProjectPartnerName()
	{
		if(projectPartner==null)
			return "";
		
		return projectPartner.getLastName();
	}

	public void setProjectPartner(String name)
	{
		if(name.equals(""))
			return;
		
		if(projectPartner==null)
			projectPartner=new Experimenter();
		projectPartner.setLastName(name);
	}

	public Experimenter getExperimenter() {
		return experimenter;
	}

	public void setExperimenter(Experimenter experimenter) {
		this.experimenter = experimenter;
	}
	

	public boolean testExperiment(Experiment e) 
	{
		if(experiment==null && e==null){
			experiment=new Experiment();
			return true;
		}
		if(experiment==null){
			experiment=e;
			return false;
		}else if(!experiment.getID().equals(e.getID())){
			experiment=e;
			return false;
		}
		return true;
	}

	public void setGroupName(String group) {
		this.group=group;		
	}

	public void setProjectName(String project) {
		this.projectName=project;		
	}

	public String getProjectName() {
		return projectName;
	}

	public String getGroupName() {
		return group;
	}

	public void createNew(String idxExp,String idxExper) 
	{
			//create new one
			Experiment experiment=new Experiment();
			experiment.setID(idxExp);
			Experimenter experimenter=new Experimenter();
			
			experimenter.setID(idxExper);
			experiment.linkExperimenter(experimenter);
			setExperiment(experiment);
			setExperimenter(experimenter);
	}

	
	
	/**
	 * If overwrite==true overwrite data, else only complete data
	 * @param exp
	 * @param overwrite
	 * @return
	 */
	public boolean addData(Experiment newElem, boolean overwrite)  throws Exception
	{
		boolean conflicts=false;
		
		if(overwrite){
			replaceData(newElem);
			LOGGER.info("[DATA] -- replace EXPERIMENT data");
		}else{
				completeData(newElem);
				LOGGER.info("[DATA] -- complete EXPERIMENT data");
		}
		return conflicts;
	}
	
	/**
	 * If overwrite==true overwrite data, else only complete data
	 * @param exp
	 * @param overwrite
	 * @return
	 */
	public boolean addData(Experimenter newElem, boolean overwrite)  throws Exception
	{
		boolean conflicts=false;
		
		if(overwrite){
			replaceData(newElem);
			LOGGER.info("[DATA] -- replace EXPERIMENT data");
		}else{
				completeData(newElem);
				LOGGER.info("[DATA] -- complete EXPERIMENT data");
		}
		return conflicts;
	}
	
	
	/**
	 * Overwrites only tags that are not set
	 * @param newElem
	 * @throws Exception 
	 */
	private List<TagData> completeData(Experiment newElem) throws Exception
	{
		List<TagData> conflictTags=new ArrayList<TagData>();
		
		// copy input fields
		Experiment copyIn=null;
		if(experiment!=null ){
			copyIn=new Experiment(experiment);
		}
		replaceData(newElem);
		
		// set input field values again
		if(copyIn!=null){
			ExperimentType type=copyIn.getType();
			String desc=copyIn.getDescription();

			if(desc!=null && !desc.equals("")){
				//					if(experiment.getDescription()!=null && !experiment.getDescription().equals(""))
				//						conflictTags.add(desc);

				experiment.setDescription(desc);
			}
			if(type!=null) experiment.setType(type);
		}
		
		return conflictTags;
	}
	
	private void completeData(Experimenter newElem)
	{
		Experimenter copyIn=null;
		if(experimenter!=null){
			copyIn=new Experimenter(experimenter);
		}
		
		replaceData(newElem);
		
		if(copyIn!=null){
			String name=copyIn.getLastName();
			if(name!=null && !name.equals("")) experimenter.setLastName(name);
		}
	}
	
	/**
	 * Replace intern experiment object by given experiment. All manuell input data are lost. 
	 * @param exper
	 */
	private void replaceData(Experiment newElem)
	{
		if(newElem!=null){
			experiment=new Experiment(newElem);
		}
	}
	
	/**
	 * Replace intern experimenter object by given experimenter. All manuell input data are lost. 
	 * @param exper
	 */
	private void replaceData(Experimenter newElem)
	{
		if(newElem!=null){
			experimenter=new Experimenter(newElem);
		}
	}
	
	public String parseProjectPartner(MapAnnotation map) 
	{
		List<MapPair> listMP=map.getValue();
		String result=null;
		switch (map.getNamespace()) {
		case MetaDataDialog.NS_2016_06_07:
			result=parseFromMapAnnotation2016_06_07(listMP);
			break;

		default:
			LOGGER.warn("[DATA] Namespace is not supported for parsing sample data");
			break;
		}
		return result;
	}

	private String parseFromMapAnnotation2016_06_07(List<MapPair> listMP) 
	{
		for(MapPair obj:listMP){
			switch (obj.getName()) {
			case PROJPARTNER_MAPLABEL:
				return obj.getValue();
			default:
				LOGGER.info("[DATA] unknown Label for Project Partner MapAnnotation: "+obj.getName());
				break;
			}
		}
		return null;
	}

	public void update(List<TagData> changesExperiment) throws Exception 
	{
		if(changesExperiment==null){
			MonitorAndDebug.printConsole("\t no changes for experiment");
			return;
		}
		for(TagData t: changesExperiment){
			setTag(t.getTagName(),t.getTagValue(),t.getTagUnit());
			if(t.getTagUnit()!=null)
				map.put(t.getTagName(), t.getTagValue()+" "+t.getTagUnit().getSymbol());
			else
				map.put(t.getTagName(), t.getTagValue());
		}
	}
	private void setTag(String name,String val,Unit unit) throws Exception
	{
		MonitorAndDebug.printConsole("\t...update "+name+" : "+val);
		switch (name) {
		case TagNames.E_TYPE:
			if(experiment==null)
				experiment=new Experiment();
			experiment.setType(getExperimentType(val));
			break;
		case TagNames.DESC:
			if(experiment==null)
				experiment=new Experiment();
			experiment.setDescription(val);
			break;
			//Set by system
		case TagNames.GROUP:
			setGroupName(val);
			break;
		case TagNames.EXPNAME:
			experimenter=parseExperimenter(val);
			break;
			//set by system
		case TagNames.PROJECTNAME:
			setProjectName(val);
			break;
		case TagNames.PROJECTPARTNER:
			
				setProjectPartner(val);
			break;
		default:
			LOGGER.warn("[CONF] unknown tag: "+name );break;
		}
	}

	private ExperimentType getExperimentType(String value)  throws FormatException
	  {
		if(value==null)
			return null;
		
	    ExperimentTypeEnumHandler handler = new ExperimentTypeEnumHandler();
	    try {
	      return (ExperimentType) handler.getEnumeration(value);
	    }
	    catch (EnumerationException e) {
	      throw new FormatException("ExperimentType creation failed", e);
	    }
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

	public void printValues()
	{
		MonitorAndDebug.printConsole("\nExperiment Modul Values::");
		if(projectPartner!=null)
			MonitorAndDebug.printConsole("\t...Project Partner = "+projectPartner.getFirstName()+" "+projectPartner.getLastName());
		if(experiment!=null){
			MonitorAndDebug.printConsole("\t...Type = "+experiment.getType());
			MonitorAndDebug.printConsole("\t...Desc = "+experiment.getDescription());
		}
	}
}
