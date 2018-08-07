package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import ome.units.quantity.Length;
import ome.units.unit.Unit;
import ome.xml.model.Objective;
import ome.xml.model.ObjectiveSettings;
import ome.xml.model.enums.Correction;
import ome.xml.model.enums.EnumerationException;
import ome.xml.model.enums.Immersion;
import ome.xml.model.enums.Medium;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.MetaDataDialog;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model.ObjectiveModel;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.ModuleConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.TagNames;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.microscope.MicroscopeProperties;
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
public class ObjectiveViewer extends ModuleViewer 
{
	private static final org.slf4j.Logger LOGGER =
			LoggerFactory.getLogger(ObjectiveViewer.class);

	private ObjectiveModel data;
	private Box box;

	// available objective tags
	private TagData model;
	private TagData manufact;
	private TagData nomMagn;
	private TagData calMagn;
	private TagData lensNA;
	private TagData immersion;
	private TagData correction;
	private TagData workDist;
	private TagData iris;
	//available objective setting tags
	private TagData corCollar;
	private TagData medium;
	private TagData refractIndex;

	private List<Objective> availableElems;

	private MicroscopeProperties mic;

	/**
	 * Creates a new instance.
	 * @param model Reference to model.
	 */
	public ObjectiveViewer(ObjectiveModel objModel,ModuleConfiguration conf,boolean showPreValues,
			List<Objective> availableElems,MicroscopeProperties mic)
	{
		MonitorAndDebug.printConsole("# ObjectiveViewer::newInstance("+(objModel!=null?"model":"null")+")");
		this.data=objModel;
		this.availableElems=availableElems;
		this.mic =mic;
		initComponents(conf);
		initTagList();
		buildGUI();
		resetInputEvent();
		showPredefinitions(conf.getTagList(), showPreValues);
		showPredefinitions(conf.getSettingList(), showPreValues);
	}

	private void initTagList()
	{
		tagList=new ArrayList<TagData>();
		tagList.add(model);
		tagList.add(manufact);
		tagList.add(nomMagn);
		tagList.add(calMagn);
		tagList.add(lensNA);
		tagList.add(immersion);
		tagList.add(correction);
		tagList.add(workDist);
		tagList.add(iris);
		tagList.add(corCollar);
		tagList.add(medium);
		tagList.add(refractIndex);

	}

	/**
	 * Builds and lay out GUI.
	 */
	private void buildGUI() 
	{
		List<JLabel> labels= new ArrayList<JLabel>();
		List<JComponent> comp=new ArrayList<JComponent>();
		addTagToGUI(model,labels,comp);
		addTagToGUI(manufact,labels,comp);
		addTagToGUI(nomMagn,labels,comp);
		addTagToGUI(calMagn,labels,comp);
		addTagToGUI(lensNA,labels,comp);

		//		addTagToGUI(correction,labels,comp);
		addTagToGUI(workDist,labels,comp);

		addLabelTextRows(labels, comp, gridbag, globalPane);

		gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER; //last
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.weightx = 1.0;

		//Settings
		GridBagConstraints cSett=new GridBagConstraints();
		GridBagLayout gridbagSett = new GridBagLayout();
		List<JLabel> labelsSett= new ArrayList<JLabel>();
		List<JComponent> compSett=new ArrayList<JComponent>();
		JPanel settingsPane=new JPanel(gridbagSett);
		addLabelToGUI(new JLabel("Settings:"),labelsSett,compSett);
		addTagToGUI(corCollar,labelsSett,compSett);
		//		addTagToGUI(medium,labelsSett,compSett);
		addTagToGUI(immersion,labels,comp);
		addTagToGUI(refractIndex,labelsSett,compSett);

		addLabelTextRows(labelsSett, compSett, gridbag, settingsPane);

		gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER; //last
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.weightx = 1.0;

		box.add(Box.createVerticalStrut(20));
		box.add(settingsPane);

		// set data
		setGUIData();
		setSettingsGUIData();
		dataChanged=false;
	}

	/**
	 * Initialize components.
	 */
	private void initComponents(ModuleConfiguration conf) 
	{
		// init view layout
		setLayout(new BorderLayout(5,5));
		setBorder(BorderFactory.createCompoundBorder(new TitledBorder(""),
				BorderFactory.createEmptyBorder(5,10,5,10)));

		gridbag = new GridBagLayout();
		gridBagConstraints = new GridBagConstraints();

		globalPane=new JPanel();
		globalPane.setLayout(gridbag);

		box=Box.createVerticalBox();
		box.add(globalPane);

		JButton editBtn=new JButton("Choose...");
		editBtn.setAlignmentX(Component.RIGHT_ALIGNMENT);
		editBtn.setEnabled(true);
		editBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				List<Objective> linkHardwareList=null;
				if(mic!=null){
					linkHardwareList=mic.getObjectiveList();
				}
				ObjectiveEditor creator = new ObjectiveEditor(new JFrame(),"Select Objective",
						availableElems,linkHardwareList);
				Objective selectedObj=creator.getObjective();  
				if(selectedObj!=null ){
					inputKeyPressed();
					MonitorAndDebug.printConsole("ObjectiveViewer::Choose - addData()");
					try {
						data.addData(selectedObj, true);
					} catch (Exception e1) {
						LOGGER.warn("Can't set data of selected objective! "+e1);
						MonitorAndDebug.printConsole("Can't set data of selected objective! "+e1);
					}
					setGUIData();
					dataChanged=true;
					noticeEditorInput();
					revalidate();
					repaint();
				}		
			}
		});
		add(box,BorderLayout.NORTH);
		add(editBtn,BorderLayout.SOUTH);

		// init tag layout
		List<TagConfiguration> list=conf.getTagList();
		List<TagConfiguration> settList=conf.getSettingList();
		initTags(list);
		initTags(settList);
	}

	/** set for all tag fields from editor that not empty or null the valueHasChanged Flag
	 * tag fields are: model, manufactur, nominal Magn, cal magn, lens na,immersion,correction, workingDist
	 * */
	private void noticeEditorInput() 
	{
		model.dataHasChanged(true);
		manufact.dataHasChanged(true);
		nomMagn.dataHasChanged(true);
		calMagn.dataHasChanged(true);
		lensNA.dataHasChanged(true);
		immersion.dataHasChanged(true);
		correction.dataHasChanged(true);
		workDist.dataHasChanged(true);
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
		case TagNames.CORCOLLAR: 
			setCorCollar(null, prop);
			corCollar.setVisible(vis);
			break;
		case TagNames.OBJ_MEDIUM: 
			setMedium(null, prop);
			medium.setVisible(vis);
			break;
		case TagNames.REFINDEX:
			setRefractIndex(null, prop);
			refractIndex.setVisible(vis);
			break;
		case TagNames.MODEL:
			setModel(null,prop);
			model.setVisible(vis);
			break;
		case TagNames.MANUFAC:
			setManufact(null,prop);
			manufact.setVisible(vis);
			break;
		case TagNames.NOMMAGN:
			setNomMagnification(null, prop);
			nomMagn.setVisible(vis);
			break;
		case TagNames.CALMAGN:
			setCalMagnification(null,prop);
			calMagn.setVisible(vis);
			break;
		case TagNames.LENSNA:
			setLensNA(null,prop);
			lensNA.setVisible(vis);
			break;
		case TagNames.IMMERSION:
			setImmersion(null, prop);
			immersion.setVisible(vis);
			break;
		case TagNames.CORRECTION:
			setCorrection(null, prop);
			correction.setVisible(vis);
			break;
		case TagNames.WORKDIST:
			setWorkingDist(null,prop);
			workDist.setVisible(vis);
			break;
		default:LOGGER.warn("[CONF] OBJECTIVE  unknown tag: "+name );break;
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
		case TagNames.CORCOLLAR: 
			if(corCollar!=null && !corCollar.getTagValue().equals(""))
				return;
			try{
				setCorCollar(ModuleViewer.parseToDouble(t.getValue()), prop);
				corCollar.dataHasChanged(true);//handle as user input
			}catch(Exception e){
				corCollar.setTagInfo(ERROR_PREVALUE+t.getValue());
			}
			break;
		case TagNames.OBJ_MEDIUM: 
			if(medium!=null && !medium.getTagValue().equals(""))
				return;
			Medium m=parseMedium(t.getValue());
			if(m==null)
				medium.setTagInfo(ERROR_PREVALUE+t.getValue());
			setMedium(m,prop);
			medium.dataHasChanged(true);
			break;
		case TagNames.REFINDEX:
			if(refractIndex!=null && !refractIndex.getTagValue().equals(""))
				return;
			try{
				setRefractIndex(ModuleViewer.parseToDouble(t.getValue()), prop);
				refractIndex.dataHasChanged(true);
			}catch(Exception e){
				refractIndex.setTagInfo(ERROR_PREVALUE+t.getValue());
			}
			break;
		case TagNames.MODEL:
			if(model!=null && !model.getTagValue().equals(""))
				return;
			setModel(t.getValue(),prop);
			model.dataHasChanged(true);
			break;
		case TagNames.MANUFAC:
			if(manufact!=null && !manufact.getTagValue().equals(""))
				return;
			setManufact(t.getValue(),prop);
			manufact.dataHasChanged(true);
			break;
		case TagNames.NOMMAGN:
			if(nomMagn!=null && !nomMagn.getTagValue().equals(""))
				return;
			try{
				setNomMagnification(ModuleViewer.parseToDouble(t.getValue()), prop);
				nomMagn.dataHasChanged(true);
			}catch(Exception e){
				nomMagn.setTagInfo(ERROR_PREVALUE+t.getValue());
			}
			break;
		case TagNames.CALMAGN:
			if(calMagn!=null && !calMagn.getTagValue().equals(""))
				return;
			try{
				setCalMagnification(ModuleViewer.parseToDouble(t.getValue()),prop);
				calMagn.dataHasChanged(true);
			}catch(Exception e){
				calMagn.setTagInfo(ERROR_PREVALUE+t.getValue());
			}
			break;
		case TagNames.LENSNA:
			if(lensNA!=null && !lensNA.getTagValue().equals(""))
				return;
			try{
				setLensNA(ModuleViewer.parseToDouble(t.getValue()),prop);
				lensNA.dataHasChanged(true);
			}catch(Exception e){
				lensNA.setTagInfo(ERROR_PREVALUE+t.getValue());
			}
			break;
		case TagNames.IMMERSION:
			if(immersion!=null && !immersion.getTagValue().equals(""))
				return;
			Immersion i=parseImmersion(t.getValue());
			if(i==null)
				immersion.setTagInfo(ERROR_PREVALUE+t.getValue());
			setImmersion(i, prop);
			immersion.dataHasChanged(true);
			break;
		case TagNames.CORRECTION:
			if(correction!=null && !correction.getTagValue().equals(""))
				return;
			Correction c=parseCorrection(t.getValue());
			if(c==null)
				correction.setTagInfo(ERROR_PREVALUE+t.getValue());
			setCorrection(c, prop);
			correction.dataHasChanged(true);
			break;
		case TagNames.WORKDIST:
			if(workDist!=null && !workDist.getTagValue().equals(""))
				return;
			try {
				setWorkingDist(ModuleViewer.parseToLength(t.getValue(),t.getUnit(), false),prop);
				workDist.dataHasChanged(true);
			} catch (Exception e) {
				workDist.setTagInfo(ERROR_PREVALUE+t.getValue()+"["+t.getUnitSymbol()+"]");
			}
			break;
		default:LOGGER.warn("[CONF] OBJECTIVE  unknown tag: "+name );break;
		}
	}

	/**
	 * Show data of objective
	 */
	private void setGUIData() 
	{
		if(data==null)
			return;
		Objective objective=data.getObjective();
		try{setModel(objective.getModel());
		} catch (NullPointerException e) { }
		try{setManufact(objective.getManufacturer());
		} catch (NullPointerException e) { }
		try{setNomMagnification(objective.getNominalMagnification());
		} catch (NullPointerException e) { }
		try{setCalMagnification(objective.getCalibratedMagnification());
		} catch (NullPointerException e) { }
		try{setLensNA(objective.getLensNA());
		} catch (NullPointerException e) { }
		try{setImmersion(objective.getImmersion());
		} catch (NullPointerException e) { }
		try{setCorrection(objective.getCorrection());
		} catch (NullPointerException e) { }
		try{setWorkingDist(objective.getWorkingDistance());
		} catch (NullPointerException e) { }
		try{setIris(objective.getIris());
		} catch (NullPointerException e) { }

	}

	private void setSettingsGUIData()
	{
		if(data==null)
			return;
		ObjectiveSettings settings = data.getSettings();
		try{setRefractIndex(settings.getRefractiveIndex(), REQUIRED);
		} catch (NullPointerException e) { }
		try{ setMedium(settings.getMedium(), REQUIRED);
		} catch (NullPointerException e) { }
		try{ setCorCollar(settings.getCorrectionCollar(), REQUIRED);
		} catch (NullPointerException e) { }

	}


	private void setModel(String value,boolean prop)
	{
		if(model == null) 
			model = new TagData(TagNames.MODEL,value,prop,TagData.TEXTFIELD);
		else 
			model.setTagValue(value,prop);
	}
	private void setModel(String value)
	{
		setModel(value, REQUIRED);
	}

	private void setManufact(String value,boolean prop)
	{
		if(manufact == null) 
			manufact = new TagData(TagNames.MANUFAC,value,prop,TagData.TEXTFIELD);
		else 
			manufact.setTagValue(value,prop);
	}
	private void setManufact(String value)
	{
		setManufact(value, REQUIRED);
	}

	private void setNomMagnification(Double value,boolean prop)
	{
		String val= (value != null) ? String.valueOf(value):"";
		if(nomMagn == null) {
			nomMagn = new TagData(TagNames.NOMMAGN,val,prop,TagData.TEXTFIELD);
			nomMagn.addDocumentListener(createDocumentListenerDouble(nomMagn,"Invalid input. Use float!"));
		}else 
			nomMagn.setTagValue(val,prop);
	}



	private void setNomMagnification(Double value)
	{
		setNomMagnification(value, REQUIRED);
	}

	private void setCalMagnification(Double value,boolean prop)
	{
		String val= (value != null) ? String.valueOf(value):"";
		if(calMagn == null) {
			calMagn = new TagData(TagNames.CALMAGN,val,prop,TagData.TEXTFIELD);
			calMagn.addDocumentListener(createDocumentListenerDouble(calMagn,"Invalid input. Use float!"));
		}else 
			calMagn.setTagValue(val,prop);
	}
	private void setCalMagnification(Double value)
	{
		setCalMagnification(value, REQUIRED);
	}

	private void setLensNA(Double value,boolean prop)
	{
		String val= (value != null) ? String.valueOf(value):"";
		if(lensNA == null) {
			lensNA = new TagData(TagNames.LENSNA,val,prop,TagData.TEXTFIELD);
			lensNA.addDocumentListener(createDocumentListenerDouble(lensNA,"Invalid input. Use float!"));
		}else 
			lensNA.setTagValue(val,prop);
	}
	private void setLensNA(Double value)
	{
		setLensNA(value, REQUIRED);
	}

	private void setImmersion(Immersion value,boolean prop)
	{
		String val= (value != null) ? String.valueOf(value):"";
		if(immersion == null) 
			immersion = new TagData(TagNames.IMMERSION,val,prop,TagData.COMBOBOX,getNames(Immersion.class));
		else 
			immersion.setTagValue(val,prop);
	}
	private void setImmersion(Immersion value)
	{
		setImmersion(value, REQUIRED);
	}

	private void setCorrection(Correction value,boolean prop)
	{
		String val= (value != null) ? String.valueOf(value):"";
		if(correction == null) 
			correction = new TagData(TagNames.CORRECTION,val,prop,TagData.COMBOBOX,getNames(Correction.class));
		else 
			correction.setTagValue(val,prop);
	}
	private void setCorrection(Correction value)
	{
		setCorrection(value, REQUIRED);
	}

	private void setWorkingDist(Length value,boolean prop)
	{
		String val=(value!=null) ? String.valueOf(value.value()) :"";
		Unit unit=(value!=null) ? value.unit() : TagNames.WORKDIST_UNIT;
		if(workDist == null) {
			workDist = new TagData(TagNames.WORKDIST,val,unit,prop,TagData.TEXTFIELD);
			workDist.addDocumentListener(createDocumentListenerDouble(workDist,"Invalid input. Use float!"));
		}else 
			workDist.setTagValue(val,unit,prop);
	}
	private void setWorkingDist(Length value)
	{
		setWorkingDist(value, REQUIRED);
	}

	private void setIris(Boolean value,boolean prop)
	{
		String val= (value != null) ? String.valueOf(value):"";
		if(iris == null) 
			iris = new TagData("Iris",val,prop,TagData.TEXTFIELD);
		else 
			iris.setTagValue(val,prop);
	}

	private void setIris(Boolean value)
	{
		setIris(value, REQUIRED);
	}

	/*------------------------------------------------------
	 * Settings Values
	 * -----------------------------------------------------*/
	private void setCorCollar(Double value,boolean prop)
	{
		String val= (value != null) ? String.valueOf(value):"";
		if(corCollar == null) {
			corCollar = new TagData(TagNames.CORCOLLAR,val,prop,TagData.TEXTFIELD);
			corCollar.addDocumentListener(createDocumentListenerDouble(corCollar,"Invalid input. Use float!"));
		}else 
			corCollar.setTagValue(val,prop);
	}
	private void setMedium(Medium value,boolean prop)
	{
		String val= (value != null) ? String.valueOf(value):"";
		if(medium == null) 
			medium = new TagData(TagNames.OBJ_MEDIUM,val,prop,TagData.COMBOBOX,getNames(Medium.class));
		else 
			medium.setTagValue(val,prop);
	}
	private void setRefractIndex(Double value,boolean prop)
	{
		String val= (value != null) ? String.valueOf(value):"";
		if(refractIndex == null) {
			refractIndex = new TagData(TagNames.REFINDEX,val,prop,TagData.TEXTFIELD);
			refractIndex.addDocumentListener(createDocumentListenerDouble(refractIndex,"Invalid input. Use float!"));
		}else 
			refractIndex.setTagValue(val,prop);
	}

	@Override
	public void saveData() 
	{
		if(data==null){
			data=new ObjectiveModel();
		}

		if(data.getObjective()==null){
			try {
				data.addData(new Objective(),true);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Objective objective =data.getObjective();

		try{
			objective.setModel(model.getTagValue());
		}catch(Exception e){
			LOGGER.error("[DATA] can't read OBJECTIVE model input");
		}
		try{
			objective.setManufacturer(manufact.getTagValue());
		}catch(Exception e){
			LOGGER.error("[DATA] can't read OBJECTIVE manufacturer input");
		}
		try{objective.setNominalMagnification(nomMagn.getTagValue().equals("")? 
				null : Double.valueOf(nomMagn.getTagValue()));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read OBJECTIVE nominal magnification input");
		}
		try{
			objective.setCalibratedMagnification(calMagn.getTagValue().equals("")? 
					null : Double.valueOf(calMagn.getTagValue()));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read OBJECTIVE calibrated magnification input");
		}
		try{
			objective.setLensNA(lensNA.getTagValue().equals("")? 
					null : Double.valueOf(lensNA.getTagValue()));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read OBJECTIVE lensNa input");
		}
		try{
			objective.setImmersion(parseImmersion(immersion.getTagValue()));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read OBJECTIVE immersion input");
		}
		try{

			objective.setCorrection(parseCorrection(correction.getTagValue()));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read OBJECTIVE correction input");
		}
		try{
			objective.setWorkingDistance(ModuleViewer.parseToLength(workDist.getTagValue(),workDist.getTagUnit(), false));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read OBJECTIVE working distance input");
		}

		// --- Settings --------------------
		if(data.getSettings()==null){
			try {
				data.addData(new ObjectiveSettings(), true);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		ObjectiveSettings settings=data.getSettings();


		//TODO input checker
		try{
			settings.setRefractiveIndex(parseToDouble(refractIndex.getTagValue()));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read OBJECTIVE SETT refraction index input");
		}
		try{
			settings.setMedium(parseMedium(medium.getTagValue()));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read OBJECTIVE SETT medium input");
		}
		try{
			settings.setCorrectionCollar(parseToDouble(corCollar.getTagValue()));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read OBJECTIVE SETT correction collar input");
		}
		dataChanged=false;

	}

	public static Medium parseMedium(String c) 
	{
		if(c==null || c.equals(""))
			return null;

		Medium a=null;
		try{
			a=Medium.fromString(c);
		}catch(EnumerationException e){
			LOGGER.warn("Medium: "+c+" is not supported");
			//			a=Medium.OTHER;
		}
		return a;
	}

	public static Immersion parseImmersion(String c) 
	{
		if(c==null || c.equals(""))
			return null;
		Immersion m=null;
		try{
			m=Immersion.fromString(c);
		}catch(EnumerationException e){
			LOGGER.warn("Immersion: "+c+" is not supported");
			//			m=Immersion.OTHER;
		}
		return m;
	}

	public static Correction parseCorrection(String c) 
	{
		if(c==null || c.equals(""))
			return null;
		Correction m=null;
		try{
			m=Correction.fromString(c);
		}catch(EnumerationException e){
			LOGGER.warn("Correction: "+c+" is not supported");
			//			m=Correction.OTHER;
		}
		return m;
	}

	public List<TagData> getChangedTags() {

		List<TagData> list = new ArrayList<TagData>();
		if(inputAt(model)) list.add(model);
		if(inputAt(manufact)) list.add(manufact);
		if(inputAt(nomMagn)) list.add(nomMagn);
		if(inputAt(calMagn)) list.add(calMagn);
		if(inputAt(lensNA)) list.add(lensNA);
		if(inputAt(immersion)) list.add(immersion);
		if(inputAt(correction)) list.add(correction);
		if(inputAt(workDist)) list.add(workDist);

		//settings
		if(inputAt(iris))list.add(iris);
		if(inputAt(corCollar))list.add(corCollar);
		if(inputAt(medium))list.add(medium);
		if(inputAt(refractIndex))list.add(refractIndex);
		return list;
	}

	public HashMap<String,String> getMapValuesOfChanges(HashMap<String,String> map)
	{
		if(map==null)
			map=new HashMap<String, String>();

		String id="";

		if(inputAt(model)) map.put(id+TagNames.MODEL,model.getTagValue());
		if(inputAt(manufact)) map.put(id+TagNames.MANUFAC,manufact.getTagValue());
		if(inputAt(nomMagn)) map.put(id+TagNames.NOMMAGN,nomMagn.getTagValue());
		if(inputAt(calMagn))map.put(id+TagNames.CALMAGN,calMagn.getTagValue());
		if(inputAt(lensNA))map.put(id+TagNames.LENSNA,lensNA.getTagValue());
		if(inputAt(immersion))map.put(id+TagNames.IMMERSION,immersion.getTagValue());
		if(inputAt(correction))map.put(id+TagNames.CORRECTION,correction.getTagValue());
		if(inputAt(workDist))map.put(id+TagNames.WORKDIST,workDist.getTagValue()+" "+workDist.getTagUnit().getSymbol());

		//settings
		if(inputAt(iris))map.put(id+"Iris",iris.getTagValue());
		if(inputAt(corCollar))map.put(id+TagNames.CORCOLLAR,corCollar.getTagValue());
		if(inputAt(medium))map.put(id+TagNames.OBJ_MEDIUM,medium.getTagValue());
		if(inputAt(refractIndex))map.put(id+TagNames.REFINDEX,refractIndex.getTagValue());

		return map;
	}

}
