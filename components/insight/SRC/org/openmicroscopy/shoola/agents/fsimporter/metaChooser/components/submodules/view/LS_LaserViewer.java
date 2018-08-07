package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.view;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;

import ome.xml.model.Laser;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model.LightSourceModel;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.ModuleConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagData;
import org.openmicroscopy.shoola.util.MonitorAndDebug;

/**
 * Works for xsi:schemaLocation="http://www.openmicroscopy.org/Schemas/OME/2015-01 
 * @author Susanne Kunis &nbsp;&nbsp;&nbsp;&nbsp; <a
 *         href="mailto:susannekunis@gmail.com">susannekunis@gmail.com</a>
 *
 */
public class LS_LaserViewer extends LightSourceSubViewer
{
	/**
	 * Creates a new instance.
	 * @param model Reference to model.
	 */
	public LS_LaserViewer(LightSourceModel model,ModuleConfiguration conf,int index,boolean showPreValues)
	{
		MonitorAndDebug.printConsole("# LS_LaserViewer::new Instance("+(model!=null?"model":"null")+") "+index);

		classification=LightSourceModel.LASER;
		this.data=model;
		this.index=index;
		initComponents(conf);
		buildGUI();
		resetInputEvent();
		initTagList();

	}

	@Override
	protected void initTagList() 
	{
		tagList=new ArrayList<TagData>();

		tagList.add(model);
		tagList.add(manufact);
		tagList.add(power);
		tagList.add(type);

		tagList.add(medium);
		tagList.add(freqMul);
		tagList.add(tunable);
		tagList.add(pulse);
		tagList.add(pockelCell);
		tagList.add(repRate);
		tagList.add(pump);
		tagList.add(waveLength);		
	}

	@Override
	protected void setGUIData() {
		if(data==null)
			return;

		if(data.getLightSource(index)==null){
			try {
				data.addData(new Laser(), true, index);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		MonitorAndDebug.printConsole("# LS_LaserViewer::setGUIData()");

		Laser lightSrc=(Laser) data.getLightSource(index);

		try{setManufact(((Laser)lightSrc).getManufacturer(), REQUIRED);
		} catch (NullPointerException e) { }
		try{setModel(((Laser)lightSrc).getModel(), REQUIRED);
		} catch (NullPointerException e) { }
		try{ setPower(((Laser)lightSrc).getPower(),REQUIRED);
		} catch (NullPointerException e) { }
		try{ setType(((Laser)lightSrc).getType(), REQUIRED);
		} catch (NullPointerException e) { }
		try{ setMedium(((Laser)lightSrc).getLaserMedium(), REQUIRED);
		} catch (NullPointerException e) { }
		try{ setFreqMultiplication(((Laser)lightSrc).getFrequencyMultiplication(),REQUIRED);
		} catch (NullPointerException e) { }
		try{ setTunable(((Laser)lightSrc).getTuneable(), REQUIRED);
		} catch (NullPointerException e) { }
		try{ setPulse(((Laser)lightSrc).getPulse(), REQUIRED);
		} catch (NullPointerException e) { }
		try{ setPocketCell(((Laser)lightSrc).getPockelCell(), REQUIRED);
		} catch (NullPointerException e) { }
		try{setRepititationRate(((Laser)lightSrc).getRepetitionRate(),REQUIRED);
		} catch (NullPointerException e) { }
		try{setPump(((Laser)lightSrc).getLinkedPump().getID(), REQUIRED);
		} catch (NullPointerException e) { }
		try{ setWavelength(((Laser)lightSrc).getWavelength(), REQUIRED);
		} catch (NullPointerException e) { }		
	}

	@Override
	protected void addTags(List<JLabel> labels, List<JComponent> comp) {
		addTagToGUI(model,labels,comp);
		addTagToGUI(manufact,labels,comp);

		addTagToGUI(type,labels,comp);
		addTagToGUI(power,labels,comp);
		addTagToGUI(medium,labels,comp);
		addTagToGUI(freqMul,labels,comp);
		addTagToGUI(tunable,labels,comp);
		addTagToGUI(pulse,labels,comp);
		addTagToGUI(pockelCell,labels,comp);
		addTagToGUI(repRate,labels,comp);
		addTagToGUI(pump,labels,comp);
		if(pump!=null)pump.setEnable(false);
		addTagToGUI(waveLength,labels,comp);		
	}

	@Override
	public void saveData() 
	{
		if(tagList!=null){
			for(int i=0; i<tagList.size();i++){
				boolean val=tagList.get(i)!=null ? tagList.get(i).valueHasChanged() : false;
			}
		}


		if(data==null)
			data=new LightSourceModel();


		if(data.getLightSource(index)==null){
			try {
				data.addData(new Laser(), true, index);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Laser lightSrc=null;
		try{
			lightSrc=(Laser) data.getLightSource(index);
		}catch(ClassCastException e){
			LOGGER.warn("\tATTENTION...overwrite lightSrc with another type of lightSrc.");
			String oldClass=data.getLightSource(index).getClass().getSimpleName();
			try{
				data.addData(new Laser(), true, index);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			lightSrc=(Laser) data.getLightSource(index);
			MonitorAndDebug.printConsole("\t...replace "+oldClass+" by "+data.getLightSource(index).getClass().getSimpleName());
		}

		try{
			((Laser)lightSrc).setManufacturer(manufact.getTagValue().equals("")? 
					null : manufact.getTagValue());
		}catch(Exception e){
			LOGGER.error("[DATA] can't read LIGHTSRC maunfacturer input");
		}
		try{
			((Laser)lightSrc).setModel(model.getTagValue().equals("")? 
					null : model.getTagValue());
		}catch(Exception e){
			LOGGER.error("[DATA] can't read LIGHTSRC model input");
		}
		try{
			if(power.getTagValue()!=null)
				((Laser)lightSrc).setPower(LightSourceSubViewer.parsePower(power.getTagValue(),power.getTagUnit()));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read LIGHTSRC power input");
		}
		try{
			((Laser)lightSrc).setType(LightSourceSubViewer.parseLaserType(type.getTagValue()));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read LIGHTSRC type input");
		}
		try{
			((Laser)lightSrc).setFrequencyMultiplication(parseToPositiveInt(freqMul.getTagValue()));
			freqMul.setTagInfo("");
		}catch(Exception e){
			LOGGER.error("[DATA] can't read LIGHTSRC freq multiplication input");
			freqMul.setTagInfo("Can't parse input value"+freqMul.getTagValue());
		}
		try{
			((Laser)lightSrc).setLaserMedium(LightSourceSubViewer.parseMedium(medium.getTagValue()));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read LIGHTSRC medium input");
		}
		try{
			((Laser)lightSrc).setTuneable(parseToBoolean(tunable.getTagValue()));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read LIGHTSRC tunable input");
		}
		try{

			((Laser)lightSrc).setPulse(LightSourceSubViewer.parsePulse(pulse.getTagValue()));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read LIGHTSRC pulse input");
		}
		try{
			if(!pockelCell.getTagValue().equals(""))
				((Laser)lightSrc).setPockelCell(parseToBoolean(pockelCell.getTagValue()));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read LIGHTSRC pockell cell input");
		}
		try{
			((Laser)lightSrc).setRepetitionRate(LightSourceSubViewer.parseFrequency(repRate.getTagValue(), repRate.getTagUnit()));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read LIGHTSRC repetition rate input");
		}

		//TODO: link pump object
		//		((Laser)lightSrc).linkPump(pump.getTagValue().equals("") ? 
		//				null : pump.getTagValue());
		try{
			((Laser)lightSrc).setWavelength(parseToLength(waveLength.getTagValue(),waveLength.getTagUnit(), true));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read LIGHTSRC wavelength input");
		}
	}

	@Override
	protected void noticeEditorInput()
	{
		model.dataHasChanged(true);
		waveLength.dataHasChanged(true);
		power.dataHasChanged(true);
		repRate.dataHasChanged(true);
	}

}
