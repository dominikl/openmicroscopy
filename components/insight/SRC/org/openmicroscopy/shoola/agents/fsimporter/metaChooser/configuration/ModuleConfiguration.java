package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ome.units.unit.Unit;

import org.apache.commons.lang.BooleanUtils;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.util.LightPathElement;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.microscope.MetaDataUI.GUIPlaceholder;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagConfiguration;
import org.openmicroscopy.shoola.util.MonitorAndDebug;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Holds a list of visible tags and their configuration
 * @author Susanne Kunis &nbsp;&nbsp;&nbsp;&nbsp; <a
*         href="mailto:susannekunis@gmail.com">susannekunis@gmail.com</a>
 *
 */
public class ModuleConfiguration 
{
	/** Logger for this class. */
	//    protected static Logger LOGGER = Logger.getLogger(UOSMetadataLogger.class.getName());
	private static final org.slf4j.Logger LOGGER =
			LoggerFactory.getLogger(ModuleConfiguration.class);

	public static final String TAG_NAME="Name";
	public static final String TAG_VALUE="Value";
	public static final String TAG_UNIT="Unit";
	public static final String TAG_PROP="Optional";
	public static final String TAG_VISIBLE="Visible";

	private List<TagConfiguration> tagConfList; 
	private List<TagConfiguration> settingsTagConfList;
	private boolean visible;
	private GUIPlaceholder position;
	private String width;

	private List<LightPathElement> elementList;

	public ModuleConfiguration(boolean visible,GUIPlaceholder pos,String width) 
	{
		this.position=pos;
		this.visible=visible;
		this.width=width;
		tagConfList=new ArrayList<TagConfiguration>();
		settingsTagConfList=new ArrayList<TagConfiguration>();
	}

	/** 
	 * 
	 * @return list of tags
	 */
	public List<TagConfiguration> getTagList()
	{
		return tagConfList;
	}
	public void setTagList(List<TagConfiguration> list)
	{
		tagConfList=list;
	}

	/**
	 * 
	 * @return list of settings tags
	 */
	public List<TagConfiguration> getSettingList()
	{
		return settingsTagConfList;
	}
	public void setSettingList(List<TagConfiguration> list)
	{
		settingsTagConfList=list;
	}

	private void setTag(String name, String val,String unit, Boolean prop, List<TagConfiguration> thisList, boolean visible, String[] enums) 
	{
		Unit u=null;
		String[] pU=null;
		String[] eVal=null;
		try {
			u = TagNames.parseUnit(unit,name);
			pU= TagNames.getUnits(name);
			if(enums!=null){
				eVal=enums;
			}else{
				eVal=TagNames.getEnumerationVal(name);
			}
			thisList.add(new TagConfiguration(name, val,u, prop, visible,pU,eVal));
		} catch (Exception e) {
			LOGGER.warn("[HARDWARE] can't parse unit of tag "+name+" ("+unit+")");
			e.printStackTrace();
		}


	}

	public void setTag(String name, String val,String unit, Boolean prop,int elemIndex) 
	{
		List<TagConfiguration> list=elementList.get(elemIndex).getTagList();
		if(list!=null)
			setTag(name, val, unit,prop,list , true, null) ;
	}

	public void setTag(String name, String val,String unit, Boolean prop, String[] enums, Boolean visible) 
	{
		setTag(name, val, unit,prop, tagConfList, visible, enums) ;
	}

	public void setSettingTag(String name, String val,String unit, Boolean prop, String[] enums, Boolean visible) 
	{
		setTag(name, val, unit, prop, settingsTagConfList, visible, enums);
	}


	public static boolean stringToBool(String s) {
		s = s.toLowerCase();
		Set<String> trueSet = new HashSet<String>(Arrays.asList("1", "true", "yes"));
		Set<String> falseSet = new HashSet<String>(Arrays.asList("0", "false", "no"));

		if (trueSet.contains(s))
			return true;
		if (falseSet.contains(s))
			return false;

		throw new IllegalArgumentException(s + " is not a boolean.");
	}


	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean selected) 
	{
		visible=selected;
	}

	public GUIPlaceholder getPosition() {
		return position;
	}

	public void setPosition(GUIPlaceholder pos)
	{
		position=pos;
	}

	public int getWidth()
	{
		return Integer.valueOf(width);
	}

	public void printConfig()
	{
		for(int i=0; i<tagConfList.size();i++){
			MonitorAndDebug.printConsole(tagConfList.get(i).getName()+" = "+tagConfList.get(i).getValue()+" "+tagConfList.get(i).getUnitSymbol());
		}
		for(int i=0; i<settingsTagConfList.size();i++){
			MonitorAndDebug.printConsole(settingsTagConfList.get(i).getName()+" = "+settingsTagConfList.get(i).getValue()+" "+settingsTagConfList.get(i).getUnitSymbol());
		}
	}

	public List<LightPathElement> getElementList() 
	{
		MonitorAndDebug.printConsole("# ModulConfiguration::getElementList(): "+(elementList==null? "null":elementList.size()));
		return elementList;
	}

	public void setElementList(List<LightPathElement> list) 
	{
		elementList=list;
	}

	/** add new filter element to list only if one parameter of the filter is not empty
	 * 
	 * @param clazz filter class
	 * @param list of parameters of the filter
	 * @return
	 */
	public int addNewElement(String clazz, List<TagConfiguration> list) 
	{
		if(elementList==null)
			elementList=new ArrayList<LightPathElement>();

		LightPathElement newElem=new LightPathElement(clazz, list);
		if(!newElem.isEmpty()){
			elementList.add(newElem);
		}
		return elementList.size()-1;
	}



}
