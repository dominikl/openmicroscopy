package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ome.units.quantity.Frequency;
import ome.units.quantity.Length;
import ome.units.quantity.Power;
import ome.units.unit.Unit;
import ome.xml.model.LightSource;
import ome.xml.model.MapPair;
import ome.xml.model.enums.ArcType;
import ome.xml.model.enums.Enumeration;
import ome.xml.model.enums.EnumerationException;
import ome.xml.model.enums.FilamentType;
import ome.xml.model.enums.LaserMedium;
import ome.xml.model.enums.LaserType;
import ome.xml.model.enums.Pulse;
import ome.xml.model.primitives.PositiveInteger;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model.LightSourceModel;
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
public abstract class LightSourceSubViewer extends ModuleViewer 
{
	protected static final org.slf4j.Logger LOGGER =
			LoggerFactory.getLogger(LightSourceSubViewer.class);
	protected TagData manufact;
	protected TagData power;
	protected TagData model;
	protected TagData type;

	protected TagData description;//lightEmittingDiode
	protected TagData map;//genericExcitationSource

	//laser
	protected TagData medium;
	protected TagData freqMul;
	protected TagData tunable;
	protected TagData pulse;
	protected TagData pockelCell;
	protected TagData repRate;
	protected TagData pump;
	protected TagData waveLength;


	protected String classification;
	protected LightSourceModel data;
	protected int index;

	abstract protected void initTagList();
	abstract protected void setGUIData();
	abstract protected void noticeEditorInput();
	abstract protected void addTags(List<JLabel> labels, List<JComponent> comp);

	protected void initComponents(ModuleConfiguration conf)
	{
		setLayout(new BorderLayout(0,0));

		gridbag = new GridBagLayout();

		gridBagConstraints = new GridBagConstraints();
		globalPane=new JPanel();

		globalPane.setAlignmentX(Component.LEFT_ALIGNMENT);
		globalPane.setAlignmentY(Component.TOP_ALIGNMENT);

		globalPane.setLayout(gridbag);

		add(globalPane,BorderLayout.NORTH);
		setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

		// init tag layout
		List<TagConfiguration> list=conf.getTagList();
		initTags(list);

	}

	public void buildGUI() {
		List<JLabel> labels= new ArrayList<JLabel>();
		List<JComponent> comp=new ArrayList<JComponent>();
		addTags(labels,comp);

		addLabelTextRows(labels, comp, gridbag, globalPane);

		gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER; //last
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.weightx = 1.0;
		initTagList();
	}

	public void setManufact(String value, boolean prop)
	{
		if(manufact == null) 
			manufact = new TagData(TagNames.MANUFAC,value,prop,TagData.TEXTFIELD);
		else 
			manufact.setTagValue(value,prop);
	}
	public void setType(Enumeration value,boolean prop)
	{
		String val;

		switch(classification){
		case "Laser":
			val= (value != null)? ((LaserType) value).getValue() : "";
			if(type == null) 
				type = new TagData(TagNames.L_TYPE,val,prop,TagData.COMBOBOX,getNames(LaserType.class));
			else 
				type.setTagValue(val,prop);
			break;
		case "Arc":
			val= (value != null)? ((ArcType) value).getValue() : "";
			if(type == null) 
				type = new TagData(TagNames.A_TYPE,val,prop,TagData.COMBOBOX,getNames(ArcType.class));
			else 
				type.setTagValue(val,prop);
			break;
		case "Filament":
			val= (value != null)? ((FilamentType) value).getValue() : "";
			if(type == null) 
				type = new TagData(TagNames.F_TYPE,val,prop,TagData.COMBOBOX,getNames(FilamentType.class));
			else 
				type.setTagValue(val,prop);
			break;
		default:
			val= (value != null)? ((LaserType) value).getValue() : "";
			if(type == null) 
				type = new TagData(TagNames.L_TYPE,val,prop,TagData.COMBOBOX,getNames(LaserType.class));
			else 
				type.setTagValue(val,prop);
			break;
		}

	}

	public void setPower(Power value, boolean prop)
	{
		String val= (value != null)? String.valueOf(value.value()) : "";
		Unit unit=(value!=null) ? value.unit() :TagNames.POWER_UNIT;
		if(power == null) {
			power = new TagData(TagNames.POWER,val,unit,prop,TagData.TEXTFIELD);
			power.addDocumentListener(createDocumentListenerDouble(power,"Invalid input. Use float!"));
		}else 
			power.setTagValue(val,unit,prop);
	}
	public void setModel(String value, boolean prop)
	{
		if(model == null) 
			model = new TagData(TagNames.MODEL,value,prop,TagData.TEXTFIELD);
		else 
			model.setTagValue(value,prop);
	}




	public void setMedium(LaserMedium value, boolean prop)
	{
		String val= (value != null)? value.getValue() : "";
		if(medium == null) 
			medium = new TagData(TagNames.MEDIUM,val,prop,TagData.COMBOBOX,getNames(LaserMedium.class));
		else 
			medium.setTagValue(val,prop);
	}
	public void setFreqMultiplication(PositiveInteger value, boolean prop)
	{
		String val= (value != null)? String.valueOf(value.getNumberValue()) : "";
		if(freqMul == null) 
			freqMul = new TagData(TagNames.FREQMUL,val,prop,TagData.TEXTFIELD);
		else 
			freqMul.setTagValue(val,prop);
	}


	public void setTunable(Boolean value, boolean prop)
	{
		String val=(value!=null) ? String.valueOf(value): "";
		if(tunable == null) 
			tunable = new TagData(TagNames.TUNABLE,val,prop,TagData.COMBOBOX,TagNames.BOOLEAN_COMBO);
		else 
			tunable.setTagValue(val,prop);
	}


	public void setPulse(Pulse value, boolean prop)
	{
		String val= (value != null)? value.getValue() : "";
		if(pulse == null) {
			pulse = new TagData(TagNames.PULSE,val,prop,TagData.COMBOBOX,getNames(Pulse.class));
		}else {
			pulse.setTagValue(val,prop);
		}
	}

	public void setPocketCell(Boolean value, boolean prop)
	{
		String val=(value!=null) ? String.valueOf(value): "";
		if(pockelCell == null) 
			pockelCell = new TagData(TagNames.POCKELCELL,val,prop,TagData.COMBOBOX,TagNames.BOOLEAN_COMBO);
		else 
			pockelCell.setTagValue(val,prop);
	}
	public void setRepititationRate(Frequency value, boolean prop)
	{
		String val=(value!=null) ? String.valueOf(value.value()) :"";
		Unit unit=(value!=null) ? value.unit():TagNames.REPRATE_UNIT_HZ;
		if(repRate == null) {
			repRate = new TagData(TagNames.REPRATE,val,unit,prop,TagData.TEXTFIELD);
			repRate.addDocumentListener(createDocumentListenerDouble(repRate,"Invalid input. Use float!"));
		}else {
			repRate.setTagValue(val,unit,prop);
		}

	}

	public void setPump(String value, boolean prop)
	{
		if(pump == null) 
			pump = new TagData(TagNames.PUMP,value,prop,TagData.TEXTFIELD);
		else 
			pump.setTagValue(value,prop);
	}
	public void setWavelength(Length value, boolean prop)
	{
		String val=(value!=null) ? String.valueOf(value.value()) :"";
		Unit unit=(value!=null) ? value.unit() :TagNames.WAVELENGTH_UNIT;
		if(waveLength == null) {
			waveLength = new TagData(TagNames.WAVELENGTH,val,unit,prop,TagData.TEXTFIELD);
			waveLength.addDocumentListener(createDocumentListenerDouble(waveLength,"Invalid input. Use float!"));
		}else 
			waveLength.setTagValue(val,unit,prop);
	}




	public void setDescription(String value, boolean prop)
	{
		if(description == null) 
			description = new TagData(TagNames.DESC,value,prop,TagData.TEXTAREA);
		else 
			description.setTagValue(value,prop);
	}

	//TODO
	public void setMap(List<MapPair> value, boolean prop)
	{
		String val="";
		if(map == null) 
			map = new TagData(TagNames.MAP,val,prop,TagData.TEXTFIELD);
		else 
			map.setTagValue(val,prop);
	}

	protected void initTag(TagConfiguration t)
	{
		String name=t.getName();
		Boolean prop=t.getProperty();
		Boolean vis=t.isVisible();
		switch(name){
		case TagNames.MODEL:
			setModel(null, prop);
			model.setVisible(vis);
			break;
		case TagNames.MANUFAC:
			setManufact(null, prop);
			manufact.setVisible(vis);
			break;
		case TagNames.A_TYPE:
		case TagNames.L_TYPE:
		case TagNames.F_TYPE:
			setType(null,prop);
			type.setVisible(vis);
			type.setDefaultValues(t.getPossibleValues());
			break;
		case TagNames.POWER:
			setPower(null, prop);
			power.setVisible(vis);
			break;
		case TagNames.MEDIUM:
			setMedium(null, prop);
			medium.setVisible(vis);
			medium.setDefaultValues(t.getPossibleValues());
			break;
		case TagNames.FREQMUL:
			setFreqMultiplication(null, prop);
			freqMul.setVisible(vis);
			break;
		case TagNames.TUNABLE:
			setTunable(null, prop);
			tunable.setVisible(vis);
			tunable.setDefaultValues(t.getPossibleValues());
			break;
		case TagNames.PULSE:
			setPulse(null,prop);
			pulse.setVisible(vis);
			pulse.setDefaultValues(t.getPossibleValues());
			break;
		case TagNames.POCKELCELL:
			setPocketCell(null, prop);
			pockelCell.setVisible(vis);
			pockelCell.setDefaultValues(t.getPossibleValues());
			break;
		case TagNames.REPRATE:
			setRepititationRate(null, prop);
			repRate.setVisible(vis);
			break;
		case TagNames.PUMP:
			setPump(null, prop);
			pump.setVisible(vis);
			break;
		case TagNames.WAVELENGTH:
			setWavelength(null, prop);
			waveLength.setVisible(vis);
			break;
		case TagNames.MAP:
			setMap(null, prop);
			map.setVisible(vis);
			break;
		case TagNames.DESC:
			setDescription(null, prop);
			description.setVisible(vis);
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

		MonitorAndDebug.printConsole("# LightSourceSubViewer::setPredefinedTag(): "+name+" - "+classification);

		Boolean prop=t.getProperty();
		switch(name){
		case TagNames.MODEL:
			if(model!=null && !model.getTagValue().equals(""))
				return;
			setModel(t.getValue(), prop);
			model.dataHasChanged(true);
			break;
		case TagNames.MANUFAC:
			if(manufact!=null && !manufact.getTagValue().equals(""))
				return;
			setManufact(t.getValue(), prop);
			manufact.dataHasChanged(true);
			break;
			//		case TagNames.TYPE:
			//			if(type!=null && !type.getTagValue().equals(""))
			//				return;
		case TagNames.L_TYPE:
			if(classification.equals(LightSourceModel.LASER)){
				LaserType d=parseLaserType(t.getValue());
				if(d==null)
					type.setTagInfo(ERROR_PREVALUE+t.getValue());
				setType(d, prop);
				type.dataHasChanged(true);
			}
			break;
		case TagNames.A_TYPE:
			if(classification.equals(LightSourceModel.ARC)){
				ArcType da=parseArcType(t.getValue());
				if(da==null)
					type.setTagInfo(ERROR_PREVALUE+t.getValue());
				setType(da, prop);
				type.dataHasChanged(true);
			}
			break;
		case TagNames.F_TYPE:
			if(classification.equals(LightSourceModel.FILAMENT)){
				FilamentType df=parseFilamentType(t.getValue());
				if(df==null)
					type.setTagInfo(ERROR_PREVALUE+t.getValue());
				setType(df, prop);
				type.dataHasChanged(true);
			}
			break;
		case TagNames.POWER:
			if(power!=null && !power.getTagValue().equals(""))
				return;
			try {
				setPower(parsePower(t.getValue(),t.getUnit()), prop);
				power.dataHasChanged(true);
			} catch (Exception e1) {
				power.setTagInfo(ERROR_PREVALUE+t.getValue()+" ["+t.getUnit()+"]");
			}
			break;
		case TagNames.MEDIUM:
			if(medium!=null && !medium.getTagValue().equals(""))
				return;
			LaserMedium m=parseMedium(t.getValue());
			if(m==null)
				medium.setTagInfo(ERROR_PREVALUE+t.getValue());
			setMedium(m, prop);
			medium.dataHasChanged(true);
			break;
		case TagNames.FREQMUL:
			if(freqMul!=null && !freqMul.getTagValue().equals(""))
				return;
			try {
				setFreqMultiplication(parseToPositiveInt(t.getValue()), prop);
				freqMul.dataHasChanged(true);
			} catch (Exception e1) {
				freqMul.setTagInfo(ERROR_PREVALUE+t.getValue());
			}
			break;
		case TagNames.TUNABLE:
			if(tunable!=null && !tunable.getTagValue().equals(""))
				return;
			try{
				setTunable(parseToBoolean(t.getValue()), prop);
				tunable.dataHasChanged(true);
			}catch(Exception e){
				tunable.setTagInfo(ERROR_PREVALUE+t.getValue());
			}
			break;
		case TagNames.PULSE:
			if(pulse!=null && !pulse.getTagValue().equals("")){
				return;
			}
			Pulse p=parsePulse(t.getValue());
			if(p==null){
				pulse.setTagInfo(ERROR_PREVALUE+t.getValue());
			}
			setPulse(p,prop);
			pulse.dataHasChanged(true);
			break;
		case TagNames.POCKELCELL:
			if(pockelCell!=null && !pockelCell.getTagValue().equals(""))
				return;
			try{
				setPocketCell(parseToBoolean(t.getValue()), prop);
				pockelCell.dataHasChanged(true);
			}catch(Exception e){
				pockelCell.setTagInfo(ERROR_PREVALUE+t.getValue());
			}
			break;
		case TagNames.REPRATE:
			if(repRate!=null && !repRate.getTagValue().equals(""))
				return;
			try {
				setRepititationRate(parseFrequency(t.getValue(),t.getUnit()), prop);
				repRate.dataHasChanged(true);
			} catch (Exception e) {
				repRate.setTagInfo(ERROR_PREVALUE+t.getValue()+" ["+t.getUnit()+"]");
			}
			break;
		case TagNames.PUMP:
			if(pump!=null && !pump.getTagValue().equals(""))
				return;
			setPump(t.getValue(), prop);
			pump.dataHasChanged(true);
			break;
		case TagNames.WAVELENGTH:
			if(waveLength!=null && !waveLength.getTagValue().equals(""))
				return;
			try {
				setWavelength(parseToLength(t.getValue(),t.getUnit(), true), prop);
				waveLength.dataHasChanged(true);
			} catch (Exception e) {
				waveLength.setTagInfo(ERROR_PREVALUE+t.getValue()+" ["+t.getUnit()+"]");
			}
			break;
		case TagNames.MAP:
			//			TODO
			//				setMap(t.getValue(), prop);
			break;
		case TagNames.DESC:
			if(description!=null && !description.getTagValue().equals(""))
				return;
			setDescription(t.getValue(), prop);
			description.dataHasChanged(true);
			break;
		default:
			LOGGER.warn("[CONF] unknown tag: "+name );break;
		}
	}

	public List<TagData> getChangedTags() {
		List<TagData> list = new ArrayList<TagData>();
		if(inputAt(model)) list.add(model);
		if(inputAt(manufact)) list.add(manufact);
		if(inputAt(type)) list.add(type);
		if(inputAt(power)) list.add(power);
		if(inputAt(description)) list.add(description);
		if(inputAt(map)) list.add(map);
		if(inputAt(medium)) list.add(medium);
		if(inputAt(freqMul)) list.add(freqMul);
		if(inputAt(tunable)) list.add(tunable);
		if(inputAt(pulse)) list.add(pulse);
		if(inputAt(pockelCell)) list.add(pockelCell);
		if(inputAt(repRate)) list.add(repRate);
		if(inputAt(pump)) list.add(pump);
		if(inputAt(waveLength)) list.add(waveLength);

		return list;


	}

	public HashMap<String,String> getMapValuesOfChanges(HashMap<String, String> mapAnnot, String refName) 
	{
		if(mapAnnot==null)
			mapAnnot=new HashMap<String, String>();


		if(inputAt(model)) mapAnnot.put(refName+TagNames.MODEL,model.getTagValue());
		if(inputAt(manufact)) mapAnnot.put(refName+TagNames.MANUFAC,manufact.getTagValue());
		if(inputAt(type)) mapAnnot.put(refName+"Type",type.getTagValue());
		if(inputAt(power)) mapAnnot.put(refName+TagNames.POWER,power.getTagValue()+" "+power.getTagUnit().getSymbol());
		if(inputAt(description)) mapAnnot.put(refName+TagNames.DESC,description.getTagValue());
		if(inputAt(map)) mapAnnot.put(refName+TagNames.MAP,map.getTagValue());
		if(inputAt(medium)) mapAnnot.put(refName+TagNames.MEDIUM,medium.getTagValue());
		if(inputAt(freqMul)) mapAnnot.put(refName+TagNames.FREQMUL,freqMul.getTagValue());
		if(inputAt(tunable)) mapAnnot.put(refName+TagNames.TUNABLE,tunable.getTagValue());
		if(inputAt(pulse)) mapAnnot.put(refName+TagNames.PULSE,pulse.getTagValue());
		if(inputAt(pockelCell)) mapAnnot.put(refName+TagNames.POCKELCELL,pockelCell.getTagValue());
		if(inputAt(repRate)) mapAnnot.put(refName+TagNames.REPRATE,repRate.getTagValue()+" "+repRate.getTagUnit().getSymbol());
		if(inputAt(pump)) mapAnnot.put(refName+TagNames.PUMP,pump.getTagValue());
		if(inputAt(waveLength)) mapAnnot.put(refName+TagNames.WAVELENGTH,waveLength.getTagValue()+" "+waveLength.getTagUnit().getSymbol());

		return mapAnnot;	
	}

	public static Power parsePower(String c,Unit<Power> unit) throws Exception
	{
		if(c==null || c.equals(""))
			return null;

		return new Power(Double.valueOf(c),unit);

	}
	public static LaserMedium parseMedium(String c) 
	{
		if(c==null || c.equals(""))
			return null;

		LaserMedium m=null;
		try{
			m=LaserMedium.fromString(c);
		}catch(EnumerationException e){
			LOGGER.warn("LaserMedium: "+c+" is not supported");
			//			m=LaserMedium.OTHER;
		}
		return m;
	}

	public static Pulse parsePulse(String c) 
	{
		if(c==null || c.equals(""))
			return null;
		Pulse m=null;
		try{
			m=Pulse.fromString(c);
		}catch(EnumerationException e){
			LOGGER.warn("Pulse: "+c+" is not supported");
			//			m=Pulse.OTHER;
		}
		return m;
	}
	public static Frequency parseFrequency(String c,Unit<Frequency> unit) throws Exception
	{
		if(c==null || c.equals(""))
			return null;

		return new Frequency(parseToDouble(c),unit);
	}

	public static LaserType parseLaserType(String c) 
	{
		if(c==null || c.equals(""))
			return null;

		LaserType a=null;
		try{
			a=LaserType.fromString(c);
		}catch(EnumerationException e){
			LOGGER.warn("LaserType: "+c+" is not supported");
			//			a=LaserType.OTHER;
		}
		return a;
	}

	public static FilamentType parseFilamentType(String c) {
		if(c==null || c.equals(""))
			return null;

		FilamentType a=null;
		try{
			a=FilamentType.fromString(c);
		}catch(EnumerationException e){
			LOGGER.warn("FilamentType: "+c+" is not supported");
			//			a=FilamentType.OTHER;
		}
		return a;
	}
	public static ArcType parseArcType(String c)  {
		if(c==null || c.equals(""))
			return null;

		ArcType a=null;
		try{
			a=ArcType.fromString(c);
		}catch(EnumerationException e){
			LOGGER.warn("ArcType: "+c+" is not supported");
			//			a=ArcType.OTHER;
		}
		return a;
	}
	public String getClassification() {
		return classification;
	}

}
