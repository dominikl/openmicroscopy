package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
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
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import ome.units.quantity.Length;
import ome.units.unit.Unit;
import ome.xml.model.Arc;
import ome.xml.model.Detector;
import ome.xml.model.Filament;
import ome.xml.model.GenericExcitationSource;
import ome.xml.model.Laser;
import ome.xml.model.LightEmittingDiode;
import ome.xml.model.LightSource;
import ome.xml.model.LightSourceSettings;
import ome.xml.model.primitives.PercentFraction;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.MetaDataDialog;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model.LightSourceModel;
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
public class LightSourceViewer extends ModuleViewer{

	private static final org.slf4j.Logger LOGGER =
			LoggerFactory.getLogger(LightSourceViewer.class);

	private LightSourceModel data;
	private CardLayout lightSrcCard; 
	private Box box;
	private final String[] sourceTypeList={LightSourceModel.LASER,
			LightSourceModel.ARC,LightSourceModel.FILAMENT,
			LightSourceModel.GENERIC_EXCITATION,LightSourceModel.LIGHT_EMITTING_DIODE};
	private JComboBox<String> sourceType;

	private int index;

	//available element setting tags
	private TagData waveLengthSett;
	/**==Absorptionskoefizient a fraction, as a value from 0.0 to 1.0*/
	private TagData attenuation;

	private boolean setDataFromCode;

	private List<LightSource> availableElems;
	private MicroscopeProperties mic;

	/**
	 * Creates a new instance.
	 * @param model Reference to model.
	 */
	public LightSourceViewer(LightSourceModel model,ModuleConfiguration conf,
			int index,boolean showPreValues,List<LightSource> availableElems,MicroscopeProperties mic)
	{
		MonitorAndDebug.printConsole("# LightSrcViewer::newInstance("+(model!=null?"model":"null")+") "+index);

		this.index=index;
		this.data=model;
		this.availableElems=availableElems;
		this.mic =mic;

		initComponents(conf,showPreValues);

		buildGUI();
		resetInputEvent();

		// set data from model
		setGUIData();
		setSettingsGUIData();

		initTagList();
		((LightSourceSubViewer) globalPane.getComponent(sourceType.getSelectedIndex())).showPredefinitions(conf.getTagList(),showPreValues);
		showPredefinitions(conf.getSettingList(), showPreValues);
	}

	private void initTagList()
	{
		tagList=new ArrayList<TagData>();
		tagList.add(waveLengthSett);
		tagList.add(attenuation);
	}

	/**
	 * Builds and lay out GUI.
	 */
	private void buildGUI() 
	{
		for (Component comp : globalPane.getComponents() ) {
			((LightSourceSubViewer) comp).buildGUI();
		}

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
		addTagToGUI(waveLengthSett,labelsSett,compSett);
		addTagToGUI(attenuation,labelsSett,compSett);

		addLabelTextRows(labelsSett, compSett, gridbag, settingsPane);

		gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER; //last
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.weightx = 1.0;

		box.add(sourceType);
		box.add(Box.createVerticalStrut(5));
		box.add(globalPane);
		box.add(Box.createVerticalStrut(20));
		box.add(settingsPane);


		dataChanged=false;
	}

	/**
	 * Initialize components.
	 */
	private void initComponents(ModuleConfiguration conf,boolean showPreValues) 
	{
		// init view layout
		setLayout(new BorderLayout(5,5));
		setBorder(BorderFactory.createCompoundBorder(new TitledBorder(""),
				BorderFactory.createEmptyBorder(5,10,5,10)));

		gridbag = new GridBagLayout();
		gridBagConstraints = new GridBagConstraints();

		lightSrcCard=new CardLayout();
		globalPane=new JPanel(lightSrcCard);

		box=Box.createVerticalBox();
		box.add(globalPane);

		ActionListener aListener=new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				try {
					showSubCompForSelection( ((JComboBox)e.getSource()).getSelectedItem().toString());

				} catch (Exception e1) {
					LOGGER.error("[GUI] can't display selected source type");
				}
			}
		};

		sourceType=new JComboBox<String>(sourceTypeList);
		sourceType.addActionListener(aListener);


		JButton editBtn=new JButton("Choose...");
		editBtn.setAlignmentX(Component.RIGHT_ALIGNMENT);
		editBtn.setEnabled(true);
		editBtn.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				List<LightSource> linkHardwareList=null;
				if(mic!=null){
					linkHardwareList=mic.getLightSourceList();
				}

				LightSourceEditor editor = new LightSourceEditor(new JFrame(),
						"Select From Available LightSource", availableElems,linkHardwareList); 
				LightSource l=editor.getSelectedLightSource();
				if(l!=null ){
					inputKeyPressed();
					//					dataChanged=true;
					try {
						System.out.println("Replace LightSource");
						data.addData(l, true,index);
					} catch (Exception e1) {
						LOGGER.warn("Can't set data of selected lightSrc! "+e1);
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

		// init tags
		globalPane.add(new LS_LaserViewer(data, conf, index,showPreValues),LightSourceModel.LASER);
		globalPane.add(new LS_ArcViewer(data, conf, index,showPreValues),LightSourceModel.ARC);
		globalPane.add(new LS_FilamentViewer(data, conf, index,showPreValues),LightSourceModel.FILAMENT);
		globalPane.add(new LS_GESViewer(data, conf, index,showPreValues),LightSourceModel.GENERIC_EXCITATION);
		globalPane.add(new LS_LEDViewer(data, conf, index,showPreValues),LightSourceModel.LIGHT_EMITTING_DIODE);

		initTags(conf.getSettingList());
	}

	/** set for all tag fields from editor that not empty or null the valueHasChanged Flag
	 * tag fields are:   
	 * Laser: model,wavelength,power,repRate
	 * Arc:model,power
	 * LED:model
	 * Filament:model,power
	 * GES:model,power
	 * */
	private void noticeEditorInput()
	{
		((LightSourceSubViewer) globalPane.getComponent(sourceType.getSelectedIndex())).noticeEditorInput();
	}

	/**
	 * Show tags for selected lightSrc element. Doesn't save previously define values.
	 * @param sType
	 * @throws Exception
	 */
	protected void showSubCompForSelection(String sType) throws Exception 
	{
		lightSrcCard.show(globalPane,sType);

		//notice only user mouse/keybord action
		if(!setDataFromCode)
			dataChanged=true;
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
		case TagNames.SET_WAVELENGTH:
			setWavelength(null, prop);
			waveLengthSett.setVisible(vis);

			break;
		case TagNames.ATTENUATION:
			setAttenuation(null, prop);
			attenuation.setVisible(vis);

			break;
		default: 
			LOGGER.warn("[CONF] LIGHTSRC SETT unknown tag: "+name );
			break;
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
		case TagNames.SET_WAVELENGTH:
			if(waveLengthSett!=null && !waveLengthSett.getTagValue().equals(""))
				return;
			try {
				setWavelength(parseToLength(t.getValue(),t.getUnit(), true), prop);
				waveLengthSett.dataHasChanged(true);
			} catch (Exception e) {
				waveLengthSett.setTagInfo(ERROR_PREVALUE+t.getValue()+" ["+t.getUnitSymbol()+"]");
			}
			break;
		case TagNames.ATTENUATION:
			if(attenuation!=null && !attenuation.getTagValue().equals(""))
				return;
			try{
				setAttenuation(parseAttenuation(t.getValue()), prop);
				attenuation.dataHasChanged(true);
			}catch(Exception e){
				attenuation.setTagInfo(ERROR_PREVALUE+t.getValue());
			}
			break;
		default: 
			LOGGER.warn("[CONF] LIGHTSRC SETT unknown tag: "+name );
			break;
		}
	}

	/**
	 * Show data of objective
	 */
	private void setGUIData() 
	{
		if(data==null || data.getNumberOfLightSrc()==0 || data.getLightSource(index)==null)
			return;

		setDataFromCode=true;
		switch (data.getLightSource(index).getClass().getSimpleName()) {
		case LightSourceModel.LASER: sourceType.setSelectedIndex(0);break;
		case LightSourceModel.ARC: sourceType.setSelectedIndex(1);break;
		case LightSourceModel.FILAMENT: sourceType.setSelectedIndex(2);break;
		case LightSourceModel.GENERIC_EXCITATION:sourceType.setSelectedIndex(3);break;
		case LightSourceModel.LIGHT_EMITTING_DIODE: sourceType.setSelectedIndex(4);break;
		default: LOGGER.warn("[CONF] unknown type: "+data.getLightSource(index).getClass().getSimpleName() );break;
		}
		((LightSourceSubViewer) globalPane.getComponent(sourceType.getSelectedIndex())).setGUIData();
		setDataFromCode=false;
	}

	private void setSettingsGUIData()
	{
		if(data==null)
			return;
		LightSourceSettings settings = data.getSettings(index);

		if(settings!=null){
			try{setWavelength(settings.getWavelength(), REQUIRED);
			} catch (NullPointerException e) { }
			try{setAttenuation(settings.getAttenuation(), REQUIRED);
			}catch (NullPointerException e){}
		}
	}


	/*------------------------------------------------------
	 * Set methods settings Values
	 * -----------------------------------------------------*/
	public void setWavelength(Length value, boolean prop)
	{
		String val=(value!=null) ? String.valueOf(value.value()) :"";
		Unit unit=(value!=null) ? value.unit():TagNames.WAVELENGTH_UNIT;
		if(waveLengthSett == null){ 
			waveLengthSett = new TagData(TagNames.SET_WAVELENGTH,val,unit,prop,TagData.TEXTFIELD);
			waveLengthSett.addDocumentListener(createDocumentListenerPosFloat(waveLengthSett,"Invalid input. Use float>0!"));
		}else 
			waveLengthSett.setTagValue(val,unit,prop);

	}
	public void setAttenuation(PercentFraction value, boolean prop)
	{
		String val= (value != null) ? String.valueOf(value.getNumberValue()):"";
		if(attenuation == null) {
			attenuation = new TagData(TagNames.ATTENUATION,val,prop,TagData.TEXTFIELD);
			attenuation.addDocumentListener(createDocumentListenerPercentFraction(attenuation,"Invalid input. Use percent fraction as dezimal between 0.0 and 1.0!"));
		}else 
			attenuation.setTagValue(val,prop);
	}

	@Override
	public void saveData() 
	{		
		//		MonitorAndDebug.printConsole("\t... lightSrc dataChanged = "+dataChanged);
		//		if(tagList!=null){
		//			for(int i=0; i<tagList.size();i++){
		//				boolean val=tagList.get(i)!=null ? tagList.get(i).valueChanged() : false;
		//				MonitorAndDebug.printConsole("\t... lightSrc change "+tagList.get(i).getTagName()+" = "+val);
		//			}
		//		}

		//		MonitorAndDebug.printConsole("\t...save data for "+((LightSourceSubViewer) globalPane.getComponent(sourceType.getSelectedIndex())).getClassification());
		((LightSourceSubViewer) globalPane.getComponent(sourceType.getSelectedIndex())).saveData();

		// --- Settings --------------------
		if(data.getSettings(index)==null){
			try {
				data.addData(new LightSourceSettings(), true, index);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		LightSourceSettings settings=data.getSettings(index);

		try{
			settings.setWavelength(parseToLength(waveLengthSett.getTagValue(),waveLengthSett.getTagUnit(), true));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read LIGHTSRC SETT wavelength input");
		}
		try{
			//TODO input format hint: percentvalue elem of [0,100] or [0,1]
			settings.setAttenuation(parseToPercentFraction(attenuation.getTagValue()));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read LIGHTSRC SETT attenuation input");
		}

		dataChanged=false;
	}
	public static PercentFraction parseAttenuation(String c) throws Exception
	{
		if(c==null || c.equals(""))
			return null;

		return new PercentFraction(Float.valueOf(c));
	}

	@Override
	public boolean hasDataToSave() 
	{
		boolean result=false;
		if(tagList!=null){
			for(int i=0; i<tagList.size();i++){
				boolean val=tagList.get(i)!=null ? tagList.get(i).valueHasChanged() : false;
				result= result || dataChanged || 
						val || ((LightSourceSubViewer) globalPane.getComponent(sourceType.getSelectedIndex())).hasDataToSave();
			}
		}
		return (result);
	}

	@Override
	public boolean allDataWasStored()
	{
		boolean result=true;
		if(tagList!=null){
			for(TagData t:tagList){
				if(t!=null){
					boolean val=t!=null ? t.isDataSaved() :true;
					result= result && val;
				}
			}
		}
		result=result  &&((LightSourceSubViewer) globalPane.getComponent(sourceType.getSelectedIndex())).allDataWasStored();
		return result;
	}

	// Attention: wrong input( at saveData() use catch case) will not be save
	@Override
	public void afterSavingData() {
		resetInputEvent();
		if(tagList!=null){
			for(TagData t: tagList){
				if(t!=null) t.dataSaved(true);
			}
		}
		((LightSourceSubViewer) globalPane.getComponent(sourceType.getSelectedIndex())).resetInputEvent();
		((LightSourceSubViewer) globalPane.getComponent(sourceType.getSelectedIndex())).afterSavingData();
	}

	public List<TagData> getChangedTags() {
		List<TagData> result=new ArrayList<TagData>();
		for(TagData t:((LightSourceSubViewer) globalPane.getComponent(sourceType.getSelectedIndex())).getChangedTags()){
			result.add(t); 
		}
		if(inputAt(waveLengthSett)) result.add(waveLengthSett);
		if(inputAt(attenuation)) result.add(attenuation);

		result.add(new TagData("SourceType", sourceTypeList[sourceType.getSelectedIndex()], OPTIONAL, TagData.TEXTFIELD));

		return result;
	}

	public HashMap<String,String> getMapValuesOfChanges(HashMap<String,String> map,String chName)
	{
		if(map==null)
			map=new HashMap<String, String>();

		LightSource l=data.getLightSource(index);

		String kindOfLS="";
		if(l instanceof Laser){
			kindOfLS=LightSourceModel.LASER;
		}else if(l instanceof Arc){
			kindOfLS=LightSourceModel.ARC;
		}else if(l instanceof Filament){
			kindOfLS=LightSourceModel.FILAMENT;
		}else if(l instanceof GenericExcitationSource){
			kindOfLS=LightSourceModel.GENERIC_EXCITATION;
		}else if(l instanceof LightEmittingDiode){
			kindOfLS=LightSourceModel.LIGHT_EMITTING_DIODE;
		}
		String id="["+kindOfLS+"]:";
		map=((LightSourceSubViewer) globalPane.getComponent(sourceType.getSelectedIndex())).getMapValuesOfChanges(map,id);

		if(inputAt(waveLengthSett)) map.put(id+TagNames.SET_WAVELENGTH,waveLengthSett.getTagValue()+" "+waveLengthSett.getTagUnit().getSymbol());
		if(inputAt(attenuation)) map.put(id+TagNames.ATTENUATION,attenuation.getTagValue());


		return map;
	}

	public int getIndex() {
		return index;
	}

}


