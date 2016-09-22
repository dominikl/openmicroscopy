package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.format;

import java.util.ArrayList;
import java.util.List;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model.DetectorModel;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.ExperimenterListModel;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagData;
import org.slf4j.LoggerFactory;

import ome.xml.model.Experiment;
import ome.xml.model.Experimenter;
import ome.xml.model.enums.ExperimentType;

public class ExperimentContainer {
	
	 private static final org.slf4j.Logger LOGGER =
	    	    LoggerFactory.getLogger(ExperimentContainer.class);

	Experiment experiment;
	Experimenter projectPartner;
	String projectName;
	String group;
	Experimenter experimenter;
//	ExperimenterListModel experimenterListModel;
//	List<Experimenter> expList;
	
	
	public ExperimentContainer()
	{
		experiment=null;
		projectPartner=null;
		experimenter=null;
	}
	
	//copy constructor
	public ExperimentContainer(ExperimentContainer orig)
	{
		experiment=orig.experiment;
		experimenter=orig.experimenter;
		projectPartner=orig.projectPartner;
	}
	
//	public ExperimentContainer(Experiment exp, Experimenter exper, Experimenter projPartner)
//	{
//		experiment=exp;
//		experimenter=exper;
//		projectPartner=projPartner;
//	}
	
	public ExperimentContainer(Experiment exp, Experimenter exper, String projPartner)
	{
		if(exp!=null)
			setExperiment(exp);
		if(exper!=null)
			setExperimenter(exper);
		if(projPartner!=null)
			setProjectPartner(projPartner);
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
		if(projectPartner==null)
			projectPartner=new Experimenter();
		projectPartner.setLastName(name);
	}
	public void setProjectPartner(Experimenter projectPartner) {
		this.projectPartner = projectPartner;
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
			experiment=newElem;
		}
	}
	
	/**
	 * Replace intern experimenter object by given experimenter. All manuell input data are lost. 
	 * @param exper
	 */
	private void replaceData(Experimenter newElem)
	{
		if(newElem!=null){
			experimenter=newElem;
		}
	}
	


	
}
