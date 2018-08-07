package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

import ome.xml.model.Dichroic;
import ome.xml.model.Filter;
import ome.xml.model.FilterSet;
import ome.xml.model.LightPath;
import ome.xml.model.enums.EnumerationException;
import ome.xml.model.enums.FilterType;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.MetaDataDialog;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.MetaDataModel;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model.LightPathModel;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.ModuleConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.TagNames;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.util.LightPathElement;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.microscope.MicroscopeProperties;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagConfiguration;
import org.openmicroscopy.shoola.util.MonitorAndDebug;
import org.slf4j.LoggerFactory;

/**
 * Works for xsi:schemaLocation="http://www.openmicroscopy.org/Schemas/OME/2015-01 
 * @author Susanne Kunis &nbsp;&nbsp;&nbsp;&nbsp; <a
 *         href="mailto:susannekunis@gmail.com">susannekunis@gmail.com</a>
 *
 */
public class LightPathViewer extends ModuleViewer{

	private static final org.slf4j.Logger LOGGER =
			LoggerFactory.getLogger(LightPathViewer.class);

	private LightPathModel data;
	private LightPathTableSmall lightPathTable;

	private int index;

	static final String EXITATION="Excitation Filter";
	static final String EMISSION="Emission Filter";
	static final String DICHROIC="Dichroic";

	private List<Object> availableElems;
	private boolean lightPathDataChanged;

	private MicroscopeProperties mic;

	/**
	 * Creates a new instance.
	 * @param model Reference to model.
	 * @param conf configuration (visible tags usw)
	 * @param index channel index
	 * @param availableElems hardwareConf
	 * @param parent
	 */
	public LightPathViewer(LightPathModel model,ModuleConfiguration conf,int index,
			List<Object> availableElems,MicroscopeProperties mic)
	{
		MonitorAndDebug.printConsole("# LightPathViewer::newInstance("+(model!=null?"model":"null")+") "+index);
		this.data=model;
		this.index=index;
		this.availableElems=availableElems;
		this.mic=mic;
		initComponents(conf);
		buildGUI();
		resetInputEvent();
		showPreDefinitions(conf);
		lightPathDataChanged=false;
	}

	public void showPreDefinitions(ModuleConfiguration conf) 
	{
		List<LightPathElement> list=conf.getElementList();
		if(list==null)
			return;

		if(lightPathTable.getRowCount()==0){
			for(LightPathElement t:list){
				lightPathTable.appendElem(parseObject(t), t.getClazz());
			}
		}

	}

	private Object parseObject(LightPathElement t) 
	{
		if(t.getClazz().equals(DICHROIC)){
			Dichroic d=new Dichroic();
			d.setModel(t.getProperty(TagNames.MODEL));
			d.setManufacturer(t.getProperty(TagNames.MANUFAC));
			return d;
		}else{
			Filter f= new Filter();
			f.setModel(t.getProperty(TagNames.MODEL));
			f.setManufacturer(t.getProperty(TagNames.MANUFAC));
			f.setType(parseFilterType(t.getProperty(TagNames.LP_TYPE)));
			f.setFilterWheel(t.getProperty(TagNames.FILTERWHEEL));
			return f;
		}
	}

	private FilterType parseFilterType(String c)
	{
		if(c==null || c.equals(""))
			return null;

		FilterType m=null;
		try{
			m=FilterType.fromString(c);
		}catch(EnumerationException e){
			LOGGER.warn("FilterType: "+c+" is not supported");
		}
		return m;
	}


	/**
	 * Builds and lay out GUI.
	 */
	private void buildGUI() 
	{
		// set data
		setGUIData();
		dataChanged=false;
	}

	/**
	 * Initialize components.
	 */
	private void initComponents(ModuleConfiguration conf) 
	{

		setLayout(new BorderLayout(5,5));

		JButton editBtn=new JButton("Choose...");
		editBtn.setAlignmentX(Component.RIGHT_ALIGNMENT);

		editBtn.addActionListener(new ActionListener() {


			public void actionPerformed(ActionEvent e) 
			{
				LightPath lpForSelection=lightPathTable.getLightPath();

				List<Object> linkHardwareList=null;
				if(mic!=null){
					linkHardwareList=mic.getLightPathList();
				}
				LightPathEditor creator = new LightPathEditor(new JFrame(),"Edit Filter List",
						availableElems,lpForSelection,linkHardwareList);
				// get result of editor
				List<Object> newList=creator.getLightPathList(); 
				lightPathDataChanged= creator.hasDataChanged();
				if(newList!=null && !newList.isEmpty() && lightPathDataChanged){
					inputKeyPressed();
					lightPathTable.setLightPath(newList);
					dataChanged=true;
					revalidate();
					repaint();
				}
			}
		});

		JScrollPane scrollPane = new JScrollPane();

		lightPathTable = new LightPathTableSmall();
		scrollPane.setViewportView(lightPathTable);
		scrollPane.setPreferredSize(new Dimension(this.getSize().width-5,50));

		add(editBtn,BorderLayout.SOUTH);
		add(scrollPane,BorderLayout.CENTER);
		setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		revalidate();
	}


	/**
	 * Show data of objective
	 */
	private void setGUIData() 
	{
		lightPathTable.clearData();

		if(data==null || data.getNumberOfLightPaths()==0)
			return;

		LightPath lightPath=data.getLightPath(index);
		if(lightPath!=null){
			lightPathTable.setLightPath(lightPath);
		}
	}


	/**
	 * Save GUI data to model.
	 */
	@Override
	public void saveData()  
	{
		LightPath l=lightPathTable.getLightPath();

		if(data==null)
			data=new LightPathModel();
		try {
			data.addData(l, true, index);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dataChanged=false;
		lightPathDataChanged=false;
		data.setInput(false,index);
	}




	@Override
	protected void initTag(TagConfiguration t) {
		// TODO Auto-generated method stub

	}
	@Override
	protected void setPredefinedTag(TagConfiguration t) 
	{
		lightPathDataChanged=true;
	}




	public int getIndex() {
		return index;
	}

	@Override
	public boolean hasDataToSave() 
	{
		return lightPathDataChanged || data.hasInput(index);
	}




	public HashMap<String,String> getMapValuesOfChanges(HashMap<String, String> map, int chIndex) 
	{
		LightPath lp=data.getLightPath(chIndex);

		if( lp==null)
			return null;

		if(map==null)
			map=new HashMap<String, String>();


		int i=1;
		for(Filter f: lp.copyLinkedExcitationFilterList()){
			String id="[Excitation Filter]:["+i+"]:";
			map.put(id+"Model", f.getModel());
			map.put(id+"Manufactur", f.getManufacturer());
			map.put(id+"Type", (f.getType()==null?"": f.getType().getValue()));
			map.put(id+"FilterWheel", f.getFilterWheel());

			i++;
		}

		Dichroic d= lp.getLinkedDichroic();
		if(d!=null){
			map.put("[Dichroic]:["+i+"]:", d.getModel());
			i++;
		}

		for(Filter f: lp.copyLinkedEmissionFilterList()){
			String id="[Emmission Filter]:["+i+"]:";
			map.put(id+"Model", f.getModel());
			map.put(id+"Manufactur", f.getManufacturer());
			map.put(id+"Type",(f.getType()==null?"": f.getType().getValue()));
			map.put(id+"FilterWheel", f.getFilterWheel());

			i++;
		}
		return map;
	}


}


