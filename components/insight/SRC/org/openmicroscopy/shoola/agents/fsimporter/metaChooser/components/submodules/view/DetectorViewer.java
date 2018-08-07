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
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import ome.units.quantity.ElectricPotential;
import ome.units.unit.Unit;
import ome.xml.model.Detector;
import ome.xml.model.enums.Binning;
import ome.xml.model.enums.DetectorType;
import ome.xml.model.enums.EnumerationException;
import omero.model.MapAnnotation;
import omero.model.MapAnnotationI;
import omero.model.NamedValue;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.MetaDataDialog;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model.DetectorModel;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model.xml.DetectorSettings;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.ModuleConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.TagNames;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.microscope.MicroscopeProperties;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.MapAnnotationObject;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.MetaDataMapAnnotation;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagData;
import org.openmicroscopy.shoola.util.MonitorAndDebug;
import org.slf4j.LoggerFactory;


/**
 * Works for xsi:schemaLocation="http://www.openmicroscopy.org/Schemas/OME/2015-01 
 * @author Susanne Kunis &nbsp;&nbsp;&nbsp;&nbsp; <a
 *         href="mailto:susannekunis@gmail.com">susannekunis@gmail.com</a>
 */
public class DetectorViewer extends ModuleViewer{

	private static final org.slf4j.Logger LOGGER =
			LoggerFactory.getLogger(DetectorViewer.class);

	private DetectorModel data;
	private Box box;
	private int index;

	// available element tags
	private TagData model;
	private TagData manufact;
	private TagData type;
	/** fixed zoom. [used:PMT] */
	private TagData zoom;
	/** gain for this detector. [used:CCD,EMCCD,PMT]*/
	private TagData amplGain;//==emGain

	//available element setting tags
	/** variable value, that also can change during the acq.
	 * See also DetectorSettings. [used:CCD,EMCCD,PMT]*/
	private TagData gain;
	/** voltage of detector.  [used:PMT]*/
	private TagData voltage;
	/** offset of detector. [used:CCD,EMCCD]*/
	private TagData offset;
	/** confocal or scanning zoom. [used:PMT]*/
	private TagData confocalZoom;
	/**represents the number of pixels that are combined to form larger pixels*/
	private TagData binning;
	private TagData subarray;

	private List<Detector> availableElems;

	private MicroscopeProperties mic;

	/**
	 * Creates a new instance.
	 * @param model Reference to model.
	 */
	public DetectorViewer(DetectorModel model,ModuleConfiguration conf,int index,
			boolean showPreValues,List<Detector> availableElemsImgData,MicroscopeProperties mic)
	{
		MonitorAndDebug.printConsole("# DetectorViewer::newInstance("+(model!=null?"model":"null")+") "+index);

		this.data=model;
		this.index=index;
		this.availableElems=availableElemsImgData;
		this.mic=mic;

		initComponents(conf);
		initTagList();
		buildGUI();
		resetInputEvent();
		// set data from model
		resetAttributesForType();
		setGUIData();
		setSettingsGUIData();

		showPredefinitions(conf.getTagList(), showPreValues);
		showPredefinitions(conf.getSettingList(), showPreValues);
	}

	private void initTagList()
	{
		tagList=new ArrayList<TagData>();
		tagList.add(model);
		tagList.add(manufact);
		tagList.add(type);
		tagList.add(zoom);
		tagList.add(amplGain);


		tagList.add(gain);
		tagList.add(voltage);
		tagList.add(offset);
		tagList.add(confocalZoom);
		tagList.add(binning);
		tagList.add(subarray);
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
		addTagToGUI(type,labels,comp);
		addTagToGUI(zoom,labels,comp);
		addTagToGUI(amplGain,labels,comp);

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
		addTagToGUI(gain,labelsSett,compSett);
		addTagToGUI(voltage,labelsSett,compSett);
		addTagToGUI(offset,labelsSett,compSett);
		addTagToGUI(confocalZoom,labelsSett,compSett);
		addTagToGUI(binning,labelsSett,compSett);
		addTagToGUI(subarray,labelsSett,compSett);

		addLabelTextRows(labelsSett, compSett, gridbag, settingsPane);

		gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER; //last
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.weightx = 1.0;

		box.add(Box.createVerticalStrut(20));
		box.add(settingsPane);


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
				List<Detector> linkHardwareList=null;
				if(mic!=null){
					linkHardwareList=mic.getDetectorList();
				}
				DetectorEditor creator = new DetectorEditor(new JFrame(),"Choose Detector",
						availableElems,linkHardwareList); 
				Detector selected=creator.getDetector();  
				if(selected!=null ){
					inputKeyPressed();
					try {
						data.addData(selected, true,index);
					} catch (Exception e1) {
						LOGGER.warn("Can't set data of selected detector! "+e1);
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
	 * tag fields are: model, manufactur, type, zoom, amplification gain, gain, voltage
	 * */
	private void noticeEditorInput() 
	{
		model.dataHasChanged(true);
		manufact.dataHasChanged(true);
		type.dataHasChanged(true);
		zoom.dataHasChanged(true);
		amplGain.dataHasChanged(true);
		gain.dataHasChanged(true);
		voltage.dataHasChanged(true);
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
		case TagNames.MODEL: 
			setModel(null,OPTIONAL);
			model.setVisible(vis);
			break;
		case TagNames.MANUFAC: 
			setManufact(null, OPTIONAL);
			manufact.setVisible(vis);
			break;
		case TagNames.D_TYPE:
			setType(null,OPTIONAL);
			type.setVisible(vis);
			type.setDefaultValues(t.getPossibleValues());
			break;
		case TagNames.ZOOM:
			setZoom(null, OPTIONAL);
			zoom.setVisible(vis);
			break;
		case TagNames.AMPLGAIN:
			setAmplGain(null, OPTIONAL);
			amplGain.setVisible(vis);
			break;
		case TagNames.GAIN:
			setGain(null,OPTIONAL);
			gain.setVisible(vis);
			break;
		case TagNames.VOLTAGE:
			setVoltage(null, OPTIONAL);
			voltage.setVisible(vis);
			break;
		case TagNames.OFFSET:
			setOffset(null, OPTIONAL);
			offset.setVisible(vis);
			break;
		case TagNames.CONFZOOM:
			setConfocalZoom(null, prop);
			confocalZoom.setVisible(vis);
			break;
		case TagNames.BINNING:
			setBinning(null, prop);
			binning.setVisible(vis);
			binning.setDefaultValues(t.getPossibleValues());
			break;
		case TagNames.SUBARRAY:
			setSubarray(null, prop);
			subarray.setVisible(vis);
			break;
		default: LOGGER.warn("[CONF] unknown tag: "+name );break;
		}
	}

	/**
	 * set predefined tag value.
	 * @param t
	 */
	protected void setPredefinedTag(TagConfiguration t) 
	{
		if(t.getValue()==null || t.getValue().equals(""))
			return;

		predefinitionValLoaded=predefinitionValLoaded || (!t.getValue().equals(""));
		String name=t.getName();

		//		MonitorAndDebug.printConsole("# DetectorViewer::setPredefinedTag(): "+name);

		Boolean prop=t.getProperty();
		switch (name) {
		case TagNames.MODEL: 
			if(model!=null && !model.getTagValue().equals(""))
				return;
			setModel(t.getValue(),OPTIONAL);
			model.dataHasChanged(true);
			break;
		case TagNames.MANUFAC: 
			if(manufact!=null && !manufact.getTagValue().equals(""))
				return;
			setManufact(t.getValue(), OPTIONAL);
			manufact.dataHasChanged(true);
			break;
		case TagNames.D_TYPE:
			if(type!=null && !type.getTagValue().equals("")){
				return;
			}

			DetectorType d=parseDetectorType(t.getValue());
			if(d==null)
				type.setTagInfo(ERROR_PREVALUE+t.getValue());
			setType(d,OPTIONAL);
			type.dataHasChanged(true);
			break;
		case TagNames.ZOOM:
			if(zoom!=null && !zoom.getTagValue().equals(""))
				return;
			try{
				setZoom(ModuleViewer.parseToDouble(t.getValue()), OPTIONAL);
				zoom.dataHasChanged(true);
			}catch(NumberFormatException e){
				zoom.setTagInfo(ERROR_PREVALUE+t.getValue());
			}
			break;
		case TagNames.AMPLGAIN:
			if(amplGain!=null && !amplGain.getTagValue().equals(""))
				return;
			try{
				setAmplGain(ModuleViewer.parseToDouble(t.getValue()), OPTIONAL);
				amplGain.dataHasChanged(true);
			}catch(NumberFormatException e){
				amplGain.setTagInfo(ERROR_PREVALUE+t.getValue());
			}
			break;
		case TagNames.GAIN:
			if(gain!=null && !gain.getTagValue().equals(""))
				return;
			try{
				setGain(ModuleViewer.parseToDouble(t.getValue()),OPTIONAL);
				gain.dataHasChanged(true);
			}catch(NumberFormatException e){
				gain.setTagInfo(ERROR_PREVALUE+t.getValue());
			}
			break;
		case TagNames.VOLTAGE:
			if(voltage!=null && !voltage.getTagValue().equals(""))
				return;
			try{
				setVoltage(parseElectricPotential(t.getValue(),t.getUnit()), OPTIONAL);
				voltage.dataHasChanged(true);
			}catch(Exception e){
				voltage.setTagInfo(ERROR_PREVALUE+t.getValue()+" ["+t.getUnit()+"]");
			}
			break;
		case TagNames.OFFSET:
			if(offset!=null && !offset.getTagValue().equals(""))
				return;
			try{
				setOffset(ModuleViewer.parseToDouble(t.getValue()), OPTIONAL);
				offset.dataHasChanged(true);
			}catch(NumberFormatException e){
				offset.setTagInfo(ERROR_PREVALUE+t.getValue());
			}
			break;
		case TagNames.CONFZOOM:
			if(confocalZoom!=null && !confocalZoom.getTagValue().equals(""))
				return;
			try{
				setConfocalZoom(ModuleViewer.parseToDouble(t.getValue()), prop);
				confocalZoom.dataHasChanged(true);
			}catch(NumberFormatException e){
				confocalZoom.setTagInfo(ERROR_PREVALUE+t.getValue());
			}
			break;
		case TagNames.BINNING:
			if(binning!=null && !binning.getTagValue().equals(""))
				return;
			try {
				setBinning(parseBinning(t.getValue()), prop);
				binning.dataHasChanged(true);
			} catch (EnumerationException e) {
				binning.setTagInfo(ERROR_PREVALUE+t.getValue());
			}
			break;
		case TagNames.SUBARRAY:
			if(subarray!=null && !subarray.getTagValue().equals(""))
				return;
			setSubarray(t.getValue(), prop);
			subarray.dataHasChanged(true);
			break;
		default: LOGGER.warn("[CONF] unknown tag: "+name );break;
		}
	}


	/**
	 * Show data of detector
	 */
	private void setGUIData() 
	{
		if(data==null || data.getNumberOfElements()==0)
			return;
		Detector detector=data.getDetector(index);
		if(detector!=null){
			try{setModel(detector.getModel(), REQUIRED);
			} catch (NullPointerException e) { }
			try{setManufact(detector.getManufacturer(),  REQUIRED);
			} catch (NullPointerException e) { }
			try{
				setType(detector.getType(),  REQUIRED);
				if(detector.getType()!=null)
					activateAttributesForType(detector.getType());
			} catch (NullPointerException e) { }

			try{setZoom(detector.getZoom(), REQUIRED);
			} catch (NullPointerException e) { }
			try{setAmplGain(detector.getAmplificationGain(),  REQUIRED);
			} catch (NullPointerException e) { }

			try{setVoltage(detector.getVoltage(), REQUIRED);
			} catch (NullPointerException e) { }
			try{ setOffset(detector.getOffset(), REQUIRED);
			} catch (NullPointerException e) { }
		}
	}



	private void setSettingsGUIData()
	{
		if(data==null)
			return;
		DetectorSettings settings = data.getSettings(index);
		if(settings!=null){
			try{setGain(settings.getGain(), REQUIRED);
			} catch (NullPointerException e) { }
			try{
				if(settings.getVoltage()!=null)
					setVoltage(settings.getVoltage(), REQUIRED);
			} catch (NullPointerException e) { }
			try{
				if(settings.getOffset()!=null)
					setOffset(settings.getOffset(), REQUIRED);
			} catch (NullPointerException e) { }
			try{ setConfocalZoom(settings.getZoom(), REQUIRED);
			} catch (NullPointerException e) { }
			try{ setBinning(settings.getBinning(), REQUIRED);
			} catch (NullPointerException e) { }
			try{

				setSubarray(settings.getSubarray(), REQUIRED);
			} catch (NullPointerException e) { }
		}


	}

	/**certain tags are only used by certain detector type
	 * */
	private void activateAttributesForType(DetectorType type) 
	{
		if(type==null){
			resetAttributesForType();
			return;
		}
		MonitorAndDebug.printConsole("# DetectorType::activateAttributesFotType():type= "+type.getValue());
		if(type == DetectorType.PMT){
			offset.setEnable(false);
			binning.setEnable(false);
			voltage.setEnable(true);
			zoom.setEnable(true);
			gain.setEnable(true);
			subarray.setEnable(true);
		}else if(type ==DetectorType.CCD || type==DetectorType.EMCCD){
			voltage.setEnable(false);
			zoom.setEnable(false);
			offset.setEnable(true);
			gain.setEnable(true);
			binning.setEnable(true);
			subarray.setEnable(true);
		}else{
			voltage.setEnable(false);
			zoom.setEnable(false);
			offset.setEnable(false);
			gain.setEnable(false);
			binning.setEnable(true);
			subarray.setEnable(true);
		}
	}

	private void resetAttributesForType()
	{
		if(voltage != null) voltage.setEnable(true);
		if(zoom != null) zoom.setEnable(true);
		if(offset != null) offset.setEnable(true);
		if(gain != null) gain.setEnable(true);
		if(binning != null) binning.setEnable(true);
		if(subarray !=null) subarray.setEnable(true);
	}


	/*------------------------------------------------------
	 * Set methods data Values
	 * -----------------------------------------------------*/

	public void setModel(String value, boolean prop)
	{
		if(model == null) 
			model = new TagData(TagNames.MODEL,value,prop,TagData.TEXTFIELD);
		else 
			model.setTagValue(value,prop);
	}

	public void setManufact(String value, boolean prop)
	{
		if(manufact == null) 
			manufact = new TagData(TagNames.MANUFAC,value,prop,TagData.TEXTFIELD);
		else 
			manufact.setTagValue(value,prop);
	}

	public void setType(DetectorType value, boolean prop)
	{
		String val= (value != null)? value.getValue() : "";
		if(type == null){ 
			type = new TagData(TagNames.D_TYPE,val,prop,TagData.COMBOBOX,getNames(DetectorType.class));
			type.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					activateAttributesForType(parseDetectorType(type.getTagValue()));
				}
			});
		}else 
			type.setTagValue(val,prop);
	}

	//==em gain if type==PMT
	public void setAmplGain(Double value, boolean prop)
	{
		String val= (value != null) ? String.valueOf(value):"";
		if(amplGain == null) {
			amplGain = new TagData(TagNames.AMPLGAIN,val,prop,TagData.TEXTFIELD);
			amplGain.addDocumentListener(createDocumentListenerDouble(amplGain,"Invalid input. Use float!"));
		}else 
			amplGain.setTagValue(val,prop);
	}


	public void setZoom(Double value, boolean prop)
	{
		String val= (value != null) ? String.valueOf(value):"";
		if(zoom == null) {
			zoom = new TagData(TagNames.ZOOM,val,prop,TagData.TEXTFIELD);
			zoom.addDocumentListener(createDocumentListenerDouble(zoom,"Invalid input. Use float!"));
		}else 
			zoom.setTagValue(val,prop);
	}





	/*------------------------------------------------------
	 * Set methods settings Values
	 * -----------------------------------------------------*/
	public void setGain(Double value, boolean prop)
	{
		String val= (value != null) ? String.valueOf(value):"";
		if(gain == null){ 
			gain = new TagData(TagNames.GAIN,val,prop,TagData.TEXTFIELD);
			gain.addDocumentListener(createDocumentListenerDouble(gain,"Invalid input. Use float!"));
		}else 
			gain.setTagValue(val,prop);
	}
	//TODO
	public void setBinning(Binning value, boolean prop)
	{
		String val= (value != null)? value.getValue() : "";
		if(binning == null) 
			binning = new TagData(TagNames.BINNING,val,prop,TagData.COMBOBOX,getNames(Binning.class));
		else 
			binning.setTagValue(val,prop);
	}
	public void setVoltage(ElectricPotential value, boolean prop)
	{
		String val= (value != null)? String.valueOf(value.value()) : "";
		Unit unit=(value!=null) ? value.unit() :TagNames.VOLTAGE_UNIT;
		if(voltage == null) 
			voltage = new TagData(TagNames.VOLTAGE,val,unit,prop,TagData.TEXTFIELD);
		else 
			voltage.setTagValue(val,unit,prop);
	}
	public void setOffset(Double value, boolean prop)
	{
		String val= (value != null) ? String.valueOf(value):"";
		if(offset == null) {
			offset = new TagData(TagNames.OFFSET,val,prop,TagData.TEXTFIELD);
			offset.addDocumentListener(createDocumentListenerDouble(offset,"Invalid input. Use float!"));
		}else 
			offset.setTagValue(val,prop);
	}
	public void setConfocalZoom(Double value, boolean prop)
	{
		String val= (value != null) ? String.valueOf(value):"";
		if(confocalZoom == null) {
			confocalZoom = new TagData(TagNames.CONFZOOM,val,prop,TagData.TEXTFIELD);
			confocalZoom.addDocumentListener(createDocumentListenerDouble(confocalZoom,"Invalid input. Use float!"));
		}else 
			confocalZoom.setTagValue(val,prop);
	}
	public void setSubarray(String value, boolean prop)
	{
		if(subarray == null) 
			subarray = new TagData(TagNames.SUBARRAY,value,prop,TagData.TEXTFIELD);
		else 
			subarray.setTagValue(value,prop);
	}



	public String getGain()
	{
		return gain.getTagValue();
	}

	public String getBinning()
	{
		return binning.getTagValue();
	}

	@Override
	public void saveData() 
	{
		if(data==null)
			data=new DetectorModel();

		if(data.getDetector(index)==null)
			try {
				data.addData(new Detector(), true, index);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		Detector detector =data.getDetector(index);

		try{
			detector.setModel(model.getTagValue().equals("")?
					null : model.getTagValue());
		}catch(Exception e){
			LOGGER.error("[DATA] can't read DETECTOR model input");
		}
		try{

			detector.setManufacturer(manufact.getTagValue().equals("")?
					null : manufact.getTagValue());
		}catch(Exception e){
			LOGGER.error("[DATA] can't read DETECTOR manufacturer input");
		}
		try{
			detector.setType(parseDetectorType(type.getTagValue()));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read DETECTOR type input");
		}
		try{
			detector.setZoom(zoom.getTagValue().equals("")?
					null : Double.valueOf(zoom.getTagValue()));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read DETECTOR zoom input");
		}
		try{
			detector.setAmplificationGain(amplGain.getTagValue().equals("")?
					null : Double.valueOf(amplGain.getTagValue()));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read DETECTOR amplification gain input");
		}
		// --- Settings --------------------
		if(data.getSettings(index)==null){
			try {
				data.addData(new DetectorSettings(), true, index);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		DetectorSettings settings=data.getSettings(index);
		if(settings==null)
			settings = new DetectorSettings();

		try{
			settings.setGain(parseToDouble(gain.getTagValue()));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read DETECTOR SETT  gain input");
		}
		try{
			settings.setVoltage(parseElectricPotential(voltage.getTagValue(), voltage.getTagUnit()));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read DETECTOR SETT voltage input");
		}
		try{
			settings.setOffset(parseToDouble(offset.getTagValue()));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read DETECTOR SETT offset input");
		}


		try{
			settings.setZoom(parseToDouble(confocalZoom.getTagValue()));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read DETECTOR SETT zoom input");
		}
		try{
			settings.setBinning(parseBinning(binning.getTagValue()));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read DETECTOR SETT binning input");
		}
		try{
			settings.setSubarray(subarray.getTagValue());
		}catch(Exception e){
			LOGGER.error("[DATA] can't read DETECTOR SETT subarray input");
		}

		dataChanged=false;
	}
	public static DetectorType parseDetectorType(String c) 
	{
		if(c==null || c.equals(""))
			return null;

		DetectorType m=null;
		try{
			m=DetectorType.fromString(c);
		}catch(EnumerationException e){
			LOGGER.warn("DetectorType: "+c+" is not supported");
			//			m=DetectorType.OTHER;
		}
		return m;
	}
	public static ElectricPotential parseElectricPotential(String c, Unit unit) throws Exception
	{
		if(c==null || c.equals(""))
			return null;

		ElectricPotential p=null;

		return new ElectricPotential(Double.valueOf(c), unit);
	}
	public static Binning parseBinning(String c) throws EnumerationException
	{
		if(c==null || c.equals(""))
			return null;

		return Binning.fromString(c);
	}

	public List<TagData> getChangedTags()
	{
		List<TagData> list = new ArrayList<TagData>();
		if(inputAt(model)) list.add(model);
		if(inputAt(manufact)) list.add(manufact);
		if(inputAt(type)) list.add(type);
		if(inputAt(zoom)) list.add(zoom);
		if(inputAt(amplGain)) list.add(amplGain);

		// settings
		if(inputAt(gain))list.add(gain);
		if(inputAt(voltage))list.add(voltage);
		if(inputAt(offset))list.add(offset);
		if(inputAt(confocalZoom))list.add(confocalZoom);
		if(inputAt(binning))list.add(binning);
		if(inputAt(subarray))list.add(subarray);
		return list;
	}

	/**
	 * MetaDataEditor input will save as mapannotation
	 * @param map
	 * @param chName
	 * @return
	 */
	public HashMap<String,String> getMapValuesOfChanges(HashMap<String,String> map,String chName)
	{
		if(map==null)
			map=new HashMap<String, String>();

		Detector d=data.getDetector(index);
		String id="";

		if(inputAt(model)) map.put(id+TagNames.MODEL,model.getTagValue());
		if(inputAt(manufact)) map.put(id+TagNames.MANUFAC,manufact.getTagValue());
		if(inputAt(type)) map.put(id+TagNames.D_TYPE,type.getTagValue());
		if(inputAt(zoom)) map.put(id+TagNames.ZOOM,zoom.getTagValue());
		if(inputAt(amplGain)) map.put(id+TagNames.AMPLGAIN,amplGain.getTagValue());

		// settings
		if(inputAt(gain))map.put(id+TagNames.GAIN,gain.getTagValue());
		if(inputAt(voltage))map.put(id+TagNames.VOLTAGE,voltage.getTagValue()+" "+voltage.getTagUnit().getSymbol());
		if(inputAt(offset))map.put(id+TagNames.OFFSET,offset.getTagValue());
		if(inputAt(confocalZoom))map.put(id+TagNames.CONFZOOM,confocalZoom.getTagValue());
		if(inputAt(binning))map.put(id+TagNames.BINNING,binning.getTagValue());
		if(inputAt(subarray))map.put(id+TagNames.SUBARRAY,subarray.getTagValue());

		return map;
	}

	public HashMap<String,String> getMapValueOfExtendedData(){
		String id="";
		HashMap map = new HashMap<String, String>();
		map.put(id+TagNames.SUBARRAY,subarray.getTagValue());

		return map;
	}

	public int getIndex()
	{
		return index;
	}



}

