package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.view;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;

import ome.xml.model.Arc;
import ome.xml.model.Filament;
import ome.xml.model.enums.FilamentType;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules.ElementsCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules.LightSourceCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model.LightSourceModel;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.ModuleConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagData;

public class LS_FilamentViewer extends LightSourceSubViewer
{
	public LS_FilamentViewer(LightSourceModel model,ModuleConfiguration conf,int index,boolean showPreValues)
	{
		classification=LightSourceModel.FILAMENT;
		this.data=model;
		this.index=index;
		initComponents(conf);
		buildGUI();
		initTagList();
//		showPredefinitions(conf.getTagList(), showPreValues);
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
		try{ setManufact(((Filament)lightSrc).getManufacturer(), ElementsCompUI.REQUIRED);
		} catch (NullPointerException e) { }
		try{ setModel(((Filament)lightSrc).getModel(), ElementsCompUI.REQUIRED);
		} catch (NullPointerException e) { }
		try{ setPower(((Filament)lightSrc).getPower(), ElementsCompUI.REQUIRED);
		} catch (NullPointerException e) { }
		try{ setType(((Filament)lightSrc).getType(), ElementsCompUI.REQUIRED);
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
			System.out.println("\tATTENTION...overwrite lightSrc with another type of lightSrc.");
			String oldClass=data.getLightSource(index).getClass().getSimpleName();
			try{
				data.addData(new Filament(), true, index);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			lightSrc=(Filament) data.getLightSource(index);
			System.out.println("\t...replace "+oldClass+" by "+data.getLightSource(index).getClass().getSimpleName());
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
			((Filament)lightSrc).setPower(LightSourceCompUI.parsePower(power.getTagValue(),power.getTagUnit()));
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

}
