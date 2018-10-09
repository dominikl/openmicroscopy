package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.view;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;

import ome.xml.model.Filament;
import ome.xml.model.enums.FilamentType;

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
public class LS_FilamentViewer extends LightSourceSubViewer
{
	public LS_FilamentViewer(LightSourceModel model,ModuleConfiguration conf,int index,boolean showPreValues)
	{
		classification=LightSourceModel.FILAMENT;
		this.data=model;
		this.index=index;
		initComponents(conf);
		buildGUI();
		resetInputEvent();
		initTagList();
	}

	@Override
	protected void initTagList() {
		tagList=new ArrayList<TagData>();

		tagList.add(model);
		tagList.add(manufact);
		tagList.add(power);
		tagList.add(type);		
	}

	@Override
	protected void setGUIData() {
		if(data==null)
			return;
		Filament lightSrc=(Filament) data.getLightSource(index);
		try{ setManufact(((Filament)lightSrc).getManufacturer(), REQUIRED);
		} catch (NullPointerException e) { }
		try{ setModel(((Filament)lightSrc).getModel(), REQUIRED);
		} catch (NullPointerException e) { }
		try{ setPower(((Filament)lightSrc).getPower(), REQUIRED);
		} catch (NullPointerException e) { }
		try{ setType(((Filament)lightSrc).getType(), REQUIRED);
		} catch (NullPointerException e) { }

	}

	@Override
	protected void addTags(List<JLabel> labels, List<JComponent> comp) {
		addTagToGUI(model,labels,comp);
		addTagToGUI(manufact,labels,comp);


		addTagToGUI(type,labels,comp);
		addTagToGUI(power,labels,comp);		
	}

	@Override
	public void saveData() 
	{
		if(data==null)
			data=new LightSourceModel();


		if(data.getLightSource(index)==null){
			try {
				data.addData(new Filament(), true, index);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		Filament lightSrc=null;
		try{
			lightSrc=(Filament) data.getLightSource(index);
		}catch(ClassCastException e){
			LOGGER.warn("\tATTENTION...overwrite lightSrc with another type of lightSrc.");
			String oldClass=data.getLightSource(index).getClass().getSimpleName();
			try{
				data.addData(new Filament(), true, index);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			lightSrc=(Filament) data.getLightSource(index);
			MonitorAndDebug.printConsole("\t...replace "+oldClass+" by "+data.getLightSource(index).getClass().getSimpleName());
		}

		try{
			((Filament)lightSrc).setManufacturer(manufact.getTagValue().equals("")? 
					null : manufact.getTagValue());
		}catch(Exception e){
			LOGGER.error("[DATA] can't read LIGHTSRC fila manufacturer input");
		}
		try{
			((Filament)lightSrc).setModel(model.getTagValue().equals("")? 
					null : model.getTagValue());
		}catch(Exception e){
			LOGGER.error("[DATA] can't read LIGHTSRC fila model input");
		}
		try{
			((Filament)lightSrc).setPower(parsePower(power.getTagValue(),power.getTagUnit()));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read LIGHTSRC fila power input");
		}
		try{
			((Filament)lightSrc).setType(type.getTagValue().equals("") ? 
					null : FilamentType.fromString(type.getTagValue()));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read LIGHTSRC fila type input");
		}
	}

	@Override
	protected void noticeEditorInput()
	{
		model.dataHasChanged(true);
		power.dataHasChanged(true);
	}
}
