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
import ome.units.UNITS;
import ome.units.quantity.Length;
import ome.units.quantity.Time;
import ome.units.unit.Unit;
import ome.xml.model.Image;
import ome.xml.model.Pixels;
import ome.xml.model.StageLabel;
import ome.xml.model.enums.EnumerationException;
import ome.xml.model.enums.PixelType;
import ome.xml.model.primitives.PositiveInteger;
import ome.xml.model.primitives.Timestamp;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model.ImageModel;
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
public class ImageViewer extends ModuleViewer{

	private static final org.slf4j.Logger LOGGER =
			LoggerFactory.getLogger(ImageViewer.class);

	private ImageModel data;
	private Box box;

	// available element tags
	private TagData name;
	private TagData acqTime;
	private TagData dimXY;
	private TagData pixelType;
	private TagData pixelSize;
	private TagData dimZTC;
	private TagData stagePos;
	private TagData stepSize;
	private TagData timeIncrement;
	private TagData wellNr;

	private TagData desc;

	/**
	 * Creates a new instance.
	 * @param model Reference to model.
	 */
	public ImageViewer(ImageModel model,ModuleConfiguration conf)
	{
		MonitorAndDebug.printConsole("# ImageViewer::newInstance("+(model!=null?"model":"null")+")");
		this.data=model;

		initComponents(conf);
		initTagList();
		buildGUI();
		resetInputEvent();
	}

	private void initTagList()
	{
		tagList=new ArrayList<TagData>();
		tagList.add(name);
		tagList.add(desc);
		tagList.add(acqTime);
		tagList.add(dimXY);
		tagList.add(pixelSize);
		tagList.add(pixelType);
		tagList.add(dimZTC);
		tagList.add(stagePos);
		tagList.add(stepSize);
		tagList.add(timeIncrement);
		tagList.add(wellNr);
	}

	/**
	 * Builds and lay out GUI.
	 */
	private void buildGUI() 
	{
		List<JLabel> labels= new ArrayList<JLabel>();
		List<JComponent> comp=new ArrayList<JComponent>();
		addTagToGUI(name,labels,comp);
		addTagToGUI(desc,labels,comp);
		addTagToGUI(acqTime,labels,comp);
		addTagToGUI(dimXY,labels,comp);
		addTagToGUI(pixelType,labels,comp);
		addTagToGUI(pixelSize,labels,comp);
		addTagToGUI(dimZTC,labels,comp);
		addTagToGUI(stepSize,labels,comp);
		if(stepSize!=null) stepSize.setEnable(false);
		addTagToGUI(timeIncrement,labels,comp);
		addTagToGUI(stagePos,labels,comp);
		addTagToGUI(wellNr,labels,comp);
		if(wellNr!=null) wellNr.setEnable(false);

		addLabelTextRows(labels, comp, gridbag, globalPane);

		gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER; //last
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.weightx = 1.0;


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
		case TagNames.IMG_NAME:
			setName(null,prop);
			this.name.setVisible(vis);
			break;
		case TagNames.IMG_DESC:
			setDescription(null,prop);
			this.desc.setVisible(vis);
		case TagNames.ACQTIME:
			setAcqTime(null, prop);
			acqTime.setVisible(vis);
			break;
		case TagNames.DIMXY:
			setDimXY(new String[2], prop);
			dimXY.setVisible(vis);
			break;
		case TagNames.PIXELTYPE:
			setPixelType(null, prop);
			pixelType.setVisible(vis);
			break;
		case TagNames.PIXELSIZE:
			setPixelSizeXY(null, null, prop);
			pixelSize.setVisible(vis);
			break;
		case TagNames.DIMZTC:
			setDimZTC(new String[3], prop);
			dimZTC.setVisible(vis);
			break;
		case TagNames.STAGELABEL:
		case TagNames.STAGEPOS:
			setStagePos(null,null, prop);
			stagePos.setVisible(vis);
			break;
		case TagNames.STEPSIZE:
			setStepSize(null, prop);
			stepSize.setVisible(vis);
			break;
		case TagNames.TIMEINC:
			setTimeIncrement(null, prop);
			timeIncrement.setVisible(vis);
			break;
		case TagNames.WELLNR:
			setWellNr(null, prop);
			wellNr.setVisible(vis);
			break;
		default:
			LOGGER.warn("[CONF] unknown tag: "+name );break;
		}
	}

	@Override	
	protected void setPredefinedTag(TagConfiguration t) 
	{
		//no predefinitions possible
	}

	/**
	 * Show data of objective
	 */
	private void setGUIData() 
	{
		if(data==null)
			return;

		Image image=data.getImage();

		if(image!=null){ 
			try{setName(image.getName(),REQUIRED);
			} catch (NullPointerException e) { }

			try{setDescription(image.getDescription(),REQUIRED);
			} catch (NullPointerException e) { }

			try{
				String[] dimXY={image.getPixels().getSizeX().toString(),
						image.getPixels().getSizeY().toString()};
				setDimXY(dimXY,REQUIRED);
			} catch (NullPointerException e) { }

			try{setPixelType(image.getPixels().getType(),REQUIRED);
			} catch (NullPointerException e) { }

			try{ 
				String[] dimZTC={image.getPixels().getSizeZ().toString(),
						image.getPixels().getSizeT().toString(),
						image.getPixels().getSizeC().toString()};
				setDimZTC(dimZTC,REQUIRED);
			} catch (NullPointerException e) { }
			//TODO

			try{
				StageLabel stage=image.getStageLabel();
				if(stage!=null){
					setStagePos(stage.getX(),stage.getY(), REQUIRED);
				}
			} catch (NullPointerException e) { }

			try{
				setTimeIncrement(image.getPixels().getTimeIncrement(), REQUIRED);
			} catch (NullPointerException e) { }
			//TODO wellsample

			try{
				setWellNr(null, REQUIRED);
			} catch (NullPointerException e) { }

			try{ 
				setAcqTime(image.getAcquisitionDate(),REQUIRED);
			} catch (NullPointerException e) { }

			try{ 
				setPixelSizeXY(image.getPixels().getPhysicalSizeX(),image.getPixels().getPhysicalSizeY(),
						REQUIRED);
			} catch (NullPointerException e) { }
		}
	}





	/*------------------------------------------------------
	 * Set methods data Values
	 * -----------------------------------------------------*/
	private void setName(String value, boolean prop)
	{
		if(name == null) 
			name = new TagData(TagNames.IMG_NAME,value,prop,TagData.TEXTFIELD);
		else 
			name.setTagValue(value,prop);
	}
	private void setDescription(String value, Boolean prop) {
		if(desc == null) 
			desc = new TagData(TagNames.IMG_DESC,value,prop,TagData.TEXTFIELD);
		else 
			desc.setTagValue(value,prop);

	}
	//Datums- und Zeitfeld
	private void setAcqTime(Timestamp value, boolean prop)
	{
		String val= (value != null) ? value.getValue():"";
		if(acqTime == null) 
			acqTime = new TagData(TagNames.ACQTIME,val,prop,TagData.TIMESTAMP);
		else 
			acqTime.setTagValue(val,prop);
	}
	private void setDimXY(String[] value, boolean prop)
	{
		if(dimXY == null) 
			dimXY = new TagData(TagNames.DIMXY,value,prop,TagData.ARRAYFIELDS);
		else{ 
			dimXY.setTagValue(value[0],0,prop);
			dimXY.setTagValue(value[1],1,prop);
		}
	}
	private void setPixelType(PixelType value, boolean prop)
	{
		String val= (value != null) ? value.getValue():"";
		if(pixelType == null) 
			pixelType = new TagData(TagNames.PIXELTYPE,val,prop,TagData.TEXTFIELD);
		else 
			pixelType.setTagValue(val,prop);
	}
	private void setPixelSizeXY(Length valueX, Length valueY, boolean prop)
	{
		String valX = (valueX != null) ? String.valueOf(valueX.value()) : "";
		String valY = (valueY != null) ? String.valueOf(valueY.value()) : "";
		Unit unit=(valueX!=null) ? valueX.unit() : TagNames.PIXELSIZE_UNIT;
		String[] val= {valX,valY};
		if(pixelSize == null) {
			pixelSize = new TagData(TagNames.PIXELSIZE,val,unit,prop,TagData.ARRAYFIELDS);
			pixelSize.addDocumentListener(createDocumentListenerPosFloat(pixelSize,"Invalid input. Use float >0!"));
		}else {

			pixelSize.setTagValue(valX,0,prop);
			pixelSize.setTagValue(valY,1,prop);
			pixelSize.setTagUnit(unit);
		}

	}
	private void setDimZTC(String[] value, boolean prop)
	{
		if(dimZTC == null) 
			dimZTC = new TagData(TagNames.DIMZTC,value,prop,TagData.ARRAYFIELDS);
		else{ 
			dimZTC.setTagValue(value[0],0,prop);
			dimZTC.setTagValue(value[1],1,prop);
			dimZTC.setTagValue(value[2],2,prop);
		}
	}

	//TODO
	private void setStagePos(Length valueX, Length valueY, boolean prop)
	{
		String valX = (valueX != null) ? String.valueOf(valueX.value()) : "";
		String valY = (valueY != null) ? String.valueOf(valueY.value()) : "";
		Unit unit=(valueX!=null) ? valueX.unit() : TagNames.STAGEPOS_UNIT;
		String symbol = unit==UNITS.REFERENCEFRAME ? "rf" : unit.getSymbol();
		String[] val= {valX,valY};
		if(stagePos == null){ 
			stagePos = new TagData(TagNames.STAGELABEL,val,unit,prop,TagData.ARRAYFIELDS);
		}else {
			stagePos.setTagValue(valX,0,prop);
			stagePos.setTagValue(valY,1,prop);
		}
		stagePos.setTagUnit(unit);
	}


	//unit field
	private void setStepSize(String value, boolean prop)
	{
		if(stepSize == null) 
			stepSize = new TagData(TagNames.STEPSIZE,value,prop,TagData.TEXTFIELD);
		else 
			stepSize.setTagValue(value,prop);
	}
	/**
	 * used for time series that have a global
timing specification instead of per-timepoint timing info.
For example in a video stream.
	 * @param value
	 * @param prop
	 */
	//unit field
	private void setTimeIncrement(Time value, boolean prop)
	{
		String val= (value != null) ? String.valueOf(value.value()) :"";
		Unit unit=(value!=null) ? value.unit() : TagNames.TIMEINC_UNIT;
		if(timeIncrement == null) 
			timeIncrement = new TagData(TagNames.TIMEINC,val,unit,prop,TagData.TEXTFIELD);
		else 
			timeIncrement.setTagValue(val,unit,prop);
	}
	private void setWellNr(String value, boolean prop)
	{
		if(wellNr == null) 
			wellNr = new TagData(TagNames.WELLNR,value,prop,TagData.TEXTFIELD);
		else 
			wellNr.setTagValue(value,prop);
	}


	/**
	 * save all viewer data to model
	 */
	@Override
	public void saveData() 
	{
		if(data==null){
			data=new ImageModel();
		}

		if(data.getImage()==null){
			try {
				data.addData(new Image(),true);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		Image image=data.getImage();
		if(image.getPixels()==null)
			image.setPixels(new Pixels());

		try{
			image.setName(name.getTagValue());
		}catch(Exception e){
			LOGGER.error("[DATA] can't read IMAGE name input");
		}
		try{
			image.setDescription(desc.getTagValue());
		}catch(Exception e){
			LOGGER.error("[DATA] can't read IMAGE description input");
		}
		try{
			image.setAcquisitionDate(acqTime.getTagValue().equals("") ? 
					null : Timestamp.valueOf(acqTime.getTagValue()));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read IMAGE acquisition date input");
		}
		try{
			image.getPixels().setSizeX(dimXY.getTagValue(0).equals("") ?
					null : PositiveInteger.valueOf(dimXY.getTagValue(0)));

			image.getPixels().setSizeY(dimXY.getTagValue(1).equals("") ?
					null : PositiveInteger.valueOf(dimXY.getTagValue(1)));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read IMAGE dimension x,y input");
		}
		try{
			image.getPixels().setType(parseToPixelType(pixelType.getTagValue()));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read IMAGE pixel type input");
		}
		try{
			image.getPixels().setPhysicalSizeX(parseToLength(pixelSize.getTagValue(0),pixelSize.getTagUnit(), true));
			image.getPixels().setPhysicalSizeY(parseToLength(pixelSize.getTagValue(1),pixelSize.getTagUnit(), true));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read IMAGE pixel size input");
		}
		try{

			image.getPixels().setSizeZ(dimZTC.getTagValue(0).equals("")?
					null : PositiveInteger.valueOf(dimZTC.getTagValue(0)));
			image.getPixels().setSizeT(dimZTC.getTagValue(1).equals("")?
					null : PositiveInteger.valueOf(dimZTC.getTagValue(1)));
			image.getPixels().setSizeC(dimZTC.getTagValue(2).equals("")?
					null : PositiveInteger.valueOf(dimZTC.getTagValue(2)));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read IMAGE dimension z,t,c input");
		}
		try{
			image.getPixels().setTimeIncrement(timeIncrement.getTagValue().equals("")?
					null : new Time(Double.valueOf(timeIncrement.getTagValue()),timeIncrement.getTagUnit()));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read IMAGE time increment input");
		}

		//TODO: wellNr,stepSize

		if(image.getStageLabel()==null)
			image.setStageLabel(new StageLabel());

		try {
			image.getStageLabel().setX(parseToLength(stagePos.getTagValue(0),stagePos.getTagUnit(), false));
			image.getStageLabel().setY(parseToLength(stagePos.getTagValue(1),stagePos.getTagUnit(), false));
			//set name neccessary to parse to ome
			image.getStageLabel().setName("of "+stagePos.getTagValue(0)+","+stagePos.getTagValue(1));
		} catch (Exception e) {
			LOGGER.error("[DATA] can't read IMAGE stage position input");
		}

	}


	/**
	 * 
	 * @return list of tagdata with tagData.valueHasChanged()==true
	 */
	public List<TagData> getChangedTags()
	{
		List<TagData> list = new ArrayList<TagData>();
		if(name.valueHasChanged()) list.add(name);
		if(desc.valueHasChanged()) list.add(desc);
		if(acqTime.valueHasChanged()) list.add(acqTime);
		if(dimXY.valueHasChanged()) list.add(dimXY);
		if(pixelType.valueHasChanged()) list.add(pixelType);
		if(pixelSize.valueHasChanged()) list.add(pixelSize);
		if(dimZTC.valueHasChanged()) list.add(dimZTC);
		if(stepSize.valueHasChanged()) list.add(stepSize);
		if(timeIncrement.valueHasChanged()) list.add(timeIncrement);
		if(stagePos.valueHasChanged()) list.add(stagePos);
		if(wellNr.valueHasChanged()) list.add(wellNr);

		return list;
	}

	private PixelType parseToPixelType(String val) throws EnumerationException{

		if(val==null || val.equals(""))
			return null;

		return PixelType.fromString(pixelType.getTagValue());
	}


	/**
	 * @param map of changes
	 * @return map extended by all changes as [tagName,tagValue] where tagData.valueHasChanged()==true
	 */
	public HashMap<String, String> getMapValuesOfChanges(HashMap<String, String> map) 
	{
		if(map==null)
			map=new HashMap<String, String>();

		String id="";

		if(name.valueHasChanged()) map.put(id+name.getTagName(),name.getTagValue());
		if(desc.valueHasChanged()) map.put(id+desc.getTagName(),desc.getTagValue());
		if(acqTime.valueHasChanged()) map.put(id+acqTime.getTagName(),acqTime.getTagValue());
		if(dimXY.valueHasChanged()) map.put(id+dimXY.getTagName(),dimXY.getTagValue());
		if(pixelType.valueHasChanged()) map.put(id+pixelType.getTagName(),pixelType.getTagValue());
		if(pixelSize.valueHasChanged()) map.put(id+pixelSize.getTagName(),pixelSize.getTagValue()+" "+
				pixelSize.getTagUnit().getSymbol());
		if(dimZTC.valueHasChanged()) map.put(id+dimZTC.getTagName(),dimZTC.getTagValue());
		if(stepSize.valueHasChanged()) map.put(id+stepSize.getTagName(),stepSize.getTagValue());
		if(timeIncrement.valueHasChanged()) map.put(id+timeIncrement.getTagName(),timeIncrement.getTagValue()+" "+
				timeIncrement.getTagUnit().getSymbol());
		if(stagePos.valueHasChanged()) map.put(id+stagePos.getTagName(),stagePos.getTagValue()+" "+
				stagePos.getTagUnit().getSymbol());
		if(wellNr.valueHasChanged()) map.put(id+wellNr.getTagName(),wellNr.getTagValue());

		return map;
	}

	public HashMap getMapValueOfExtendedData() {
		String id="";
		HashMap map = new HashMap<String, String>();
		map.put(id+TagNames.STAGELABEL,stagePos.getTagValue());

		return map;
	}

}


