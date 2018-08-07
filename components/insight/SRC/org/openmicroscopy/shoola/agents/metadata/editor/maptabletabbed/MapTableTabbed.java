package org.openmicroscopy.shoola.agents.metadata.editor.maptabletabbed;


import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelListener;

import org.openmicroscopy.shoola.agents.metadata.editor.maptable.MapTable;
import org.openmicroscopy.shoola.agents.metadata.editor.maptable.MapTableModel;
import org.openmicroscopy.shoola.util.ui.UIUtilities;

import omero.gateway.model.MapAnnotationData;
import omero.model.MapAnnotation;
import omero.model.MapAnnotationI;
import omero.model.NamedValue;






/**
 * Tabbed pane to display UOS MapAnnotation tables
 * @author Kunis
 *
 */
public class MapTableTabbed extends JTabbedPane
{
	/** Permissions bits */
	private int permissions = MapTable.PERMISSION_EDIT;
	
	private MapTable imageMapTable;
	private MapTable objectiveMapTable;
	private MapTable detectorMapTable;
	private MapTable lightSrcMapTable;
	private MapTable channelMapTable;
	private MapTable filterMapTable;
	private MapTable sampleMapTable;
	private MapTable experimentMapTable;
	private MapTable imageEnvMapTable;
	
	private MapAnnotationData imageMapData;
	private MapAnnotationData objectiveMapData;
	private MapAnnotationData detectorMapData;
	private MapAnnotationData lightSrcMapData;
	private MapAnnotationData channelMapData;
	private MapAnnotationData filterMapData;
	private MapAnnotationData sampleMapData;
	private MapAnnotationData experimentMapData;
	private MapAnnotationData imageEnvMapData;
	/**
	 * Creates a read-only tabbed pane
	 */
	public MapTableTabbed()
	{
		this(MapTable.PERMISSION_NONE);
	}
	
	/**
	 * Creates a new MapTableTabbed with certain actions enabled, see permissions parameter.
	 * @param permissions
	 */
	public MapTableTabbed(int permissions)
	{
		this.permissions = permissions;
		init();
	}
	
	/**
	 * Initializes the components. 
	 * Components could be: Image,Objective,Detector,LightSrc,
	 * Channel,LightPath,Sample,Experiment
	 */
	private void init()
	{
		imageMapTable = new MapTable(MapTable.PERMISSION_NONE);
		objectiveMapTable = new MapTable(MapTable.PERMISSION_NONE);
		detectorMapTable = new MapTable(MapTable.PERMISSION_NONE);
		lightSrcMapTable = new MapTable(MapTable.PERMISSION_NONE);
		channelMapTable = new MapTable(MapTable.PERMISSION_NONE);
		filterMapTable = new MapTable(MapTable.PERMISSION_NONE);
		sampleMapTable = new MapTable(MapTable.PERMISSION_NONE);
		experimentMapTable = new MapTable(MapTable.PERMISSION_NONE);
		imageEnvMapTable = new MapTable(MapTable.PERMISSION_NONE);
		
		
	}
	
	public void show()
	{
//		GridBagConstraints c = new GridBagConstraints();
//		c.anchor = GridBagConstraints.NORTHWEST;
//		c.insets = new Insets(2, 2, 4, 2);
//		c.gridx = 0;
//		c.gridy = 0;
//		c.weightx = 1;
//		c.weighty = 0;
//		c.fill = GridBagConstraints.HORIZONTAL;
//
//		JPanel tablePanelImage = new JPanel();
//		tablePanelImage.setLayout(new GridBagLayout());
//		tablePanelImage.setBackground(UIUtilities.BACKGROUND_COLOR);
//		
//		if(imageMapData!=null){
//			tablePanelImage.add(getPaneTable(imageMapTable,"Image"),c);
//			c.gridy++;
//		}
//		if(objectiveMapData!=null){
//			tablePanelImage.add(getPaneTable(objectiveMapTable,"Objective"),c);
//			c.gridy++;
//		}
//		if(imageEnvMapData!=null)
//			tablePanelImage.add(getPaneTable(imageEnvMapTable,"Added Image Env Data"),c);
		
		JPanel tablePanelImage = new JPanel();
		tablePanelImage.setLayout(new BoxLayout(tablePanelImage, BoxLayout.PAGE_AXIS));
		if(imageMapData!=null && !imageMapTable.isEmpty())
			tablePanelImage.add(getPaneTable(imageMapTable,"Image"));
		if(objectiveMapData!=null && !objectiveMapTable.isEmpty())
			tablePanelImage.add(getPaneTable(objectiveMapTable,"Objective"));
		if(imageEnvMapData!=null && !imageEnvMapTable.isEmpty())
			tablePanelImage.add(getPaneTable(imageEnvMapTable,"Image Env"));
		
		if(tablePanelImage.getComponentCount()>0)
			addTab("Image", tablePanelImage);
		
//		GridBagConstraints c2 = new GridBagConstraints();
//		c2.anchor = GridBagConstraints.NORTHWEST;
//		c2.insets = new Insets(0, 2, 4, 2);
//		c2.gridx = 0;
//		c2.gridy = 0;
//		c2.weightx = 1;
//		c2.weighty = 0;
//		c2.fill = GridBagConstraints.HORIZONTAL;
//		JPanel tablePanelChannel = new JPanel();
//		tablePanelChannel.setLayout(new GridBagLayout());
//		tablePanelChannel.setBackground(UIUtilities.BACKGROUND_COLOR);
//		
//		if(channelMapData!=null){
//			tablePanelChannel.add(channelMapTable,c2);
//			c2.gridy++;
//		}
//		if(detectorMapData!=null){
//		tablePanelChannel.add(getPaneTable(detectorMapTable,"Detector"),c2);
//		c2.gridy++;
//		}
//		if(lightPathMapData!=null){
//		tablePanelChannel.add(getPaneTable(lightPathMapTable,"LightPath"),c2);
//		c2.gridy++;
//		}
//		if(lightSrcMapData!=null){
//		tablePanelChannel.add(getPaneTable(lightSrcMapTable,"LightSrc"),c2);
//		}
		JPanel tablePanelChannel = new JPanel();
		tablePanelChannel.setLayout(new BoxLayout(tablePanelChannel, BoxLayout.PAGE_AXIS));
		if(channelMapData!=null && !channelMapTable.isEmpty())
			tablePanelChannel.add(getPaneTable(channelMapTable,"Channel"));
		if(detectorMapData!=null && !detectorMapTable.isEmpty())
			tablePanelChannel.add(getPaneTable(detectorMapTable,"Detector"));
		if(filterMapData!=null && !filterMapTable.isEmpty())
			tablePanelChannel.add(getPaneTable(filterMapTable,"Filter"));
		if(lightSrcMapData!=null && !lightSrcMapTable.isEmpty())
			tablePanelChannel.add(getPaneTable(lightSrcMapTable,"LightSrc"));
		
		if(tablePanelChannel.getComponentCount()>0)
			addTab("Channel",tablePanelChannel);
		if(sampleMapData!= null && !sampleMapTable.isEmpty())
			addTab("Sample",sampleMapTable);
		if(experimentMapData!=null && !experimentMapTable.isEmpty())
			addTab("Experiment",experimentMapTable);
	}
	
	private JPanel getPaneTable(MapTable m,String name)
	{
		JPanel p = new JPanel();
		p.setBackground(UIUtilities.BACKGROUND_COLOR);
		UIUtilities.setBoldTitledBorder(name, p);
		p.setToolTipText(name);
		p.setLayout(new BorderLayout());
		p.add(m,BorderLayout.NORTH);
		p.setAlignmentY(TOP_ALIGNMENT);
		return p;
	}
	
	public void setData(MapAnnotationData data)
	{
		getAnnotationForSubPanel(data);
		
		if(imageMapData!=null){
			((MapTableModel) imageMapTable.getModel()).setData(imageMapData);
		}
		if(objectiveMapData!=null){
			((MapTableModel) objectiveMapTable.getModel()).setData(objectiveMapData);
		}
		if(detectorMapData!=null){
			((MapTableModel) detectorMapTable.getModel()).setData(detectorMapData);
		}
		if(lightSrcMapData!=null){
			((MapTableModel) lightSrcMapTable.getModel()).setData(lightSrcMapData);
		}
		if(channelMapData!=null){
			((MapTableModel) channelMapTable.getModel()).setData(channelMapData);
		}
		if(filterMapData!=null){
			((MapTableModel) filterMapTable.getModel()).setData(filterMapData);
		}
		if(sampleMapData!=null){
			((MapTableModel) sampleMapTable.getModel()).setData(sampleMapData);
		}
		if(experimentMapData!=null){
			((MapTableModel) experimentMapTable.getModel()).setData(experimentMapData);
		}
		if(imageEnvMapData!=null){
			((MapTableModel) imageEnvMapTable.getModel()).setData(imageEnvMapData);
		}
	show();
	}
	
	/**
	 * separate MapAnnotationData for image, objective, detector, lightSrc, lightPath, channel, sample, experiment
	 * to standalone MapAnnotations.
	 * TODO: key's
	 * @param data
	 */
	private void getAnnotationForSubPanel(MapAnnotationData data)
	{
		// for keys see also agents.fsimporter.metaChooser.components.MetaDataModel.java::getAnnotation()
		imageMapData=extractMapAnnotation(data,"[Image]:");
		objectiveMapData = extractMapAnnotation(data,"[Objective]:");
		detectorMapData  = extractMapAnnotation(data,"[Detector]:");
		lightSrcMapData  = extractMapAnnotation(data,"[LightSrc]:");
		channelMapData  = extractMapAnnotation(data,"[Channel]:");
		filterMapData = extractMapAnnotation(data,"[Filter]:");
		sampleMapData  = extractMapAnnotation(data,"[Sample]:");
		experimentMapData  = extractMapAnnotation(data,"[Experiment]:");
		imageEnvMapData = extractMapAnnotation(data, "[ImageEnv]:");
		
	}

	/**
	 * extract MapAnnotationData
	 * @param data
	 */
	private MapAnnotationData extractMapAnnotation(MapAnnotationData data,String key) 
	{
		MapAnnotationData result=null;
		
		List<NamedValue> values=(List<NamedValue>) data.getContent();
		List<NamedValue> subVal=new ArrayList();
		for(NamedValue val : values){
			if(val.name.contains(key))
				subVal.add(val);
		}
		if(subVal.size()>0){
			MapAnnotation ma= new MapAnnotationI();
			ma.setMapValue(subVal);
			result=new MapAnnotationData(ma);
		}
		return result;
	}

	public void setSelectionModels(ListSelectionListener l) 
	{
		imageMapTable.getSelectionModel().addListSelectionListener(l);
		objectiveMapTable.getSelectionModel().addListSelectionListener(l);
		detectorMapTable.getSelectionModel().addListSelectionListener(l);
		lightSrcMapTable.getSelectionModel().addListSelectionListener(l);
		filterMapTable.getSelectionModel().addListSelectionListener(l);
		channelMapTable.getSelectionModel().addListSelectionListener(l);
		sampleMapTable.getSelectionModel().addListSelectionListener(l);
		experimentMapTable.getSelectionModel().addListSelectionListener(l);
		imageEnvMapTable.getSelectionModel().addListSelectionListener(l);
	}

	public void setTableModelListener(TableModelListener l) 
	{
		imageMapTable.getModel().addTableModelListener(l);
		objectiveMapTable.getModel().addTableModelListener(l);
		detectorMapTable.getModel().addTableModelListener(l);
		filterMapTable.getModel().addTableModelListener(l);
		lightSrcMapTable.getModel().addTableModelListener(l);
		channelMapTable.getModel().addTableModelListener(l);
		sampleMapTable.getModel().addTableModelListener(l);
		experimentMapTable.getModel().addTableModelListener(l);
		imageEnvMapTable.getModel().addTableModelListener(l);
	}

	
}

