package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ome.xml.model.Dichroic;
import ome.xml.model.Filter;
import ome.xml.model.FilterSet;
import ome.xml.model.LightPath;

import org.openmicroscopy.shoola.util.MonitorAndDebug;
import org.slf4j.LoggerFactory;


/**
 * Works for xsi:schemaLocation="http://www.openmicroscopy.org/Schemas/OME/2015-01 
 * @author Susanne Kunis &nbsp;&nbsp;&nbsp;&nbsp; <a
*         href="mailto:susannekunis@gmail.com">susannekunis@gmail.com</a>
 *
 */
public class LightPathModel 
{
	private static final org.slf4j.Logger LOGGER =
			LoggerFactory.getLogger(LightPathModel.class);

	private List<LightPath> element;
	
	private List<Boolean> input;
	
	/** Map of key-value pairs like [FilterType]:[Nr]:TagName, TagVal **/
	private List<HashMap<String,String>> maps;


	public LightPathModel()
	{
		element=new ArrayList<LightPath>();
		maps=new ArrayList<HashMap<String,String>>();
		input=new ArrayList<Boolean>();
	}

	public LightPathModel(LightPathModel orig)
	{
		element=orig.element;
		maps=orig.maps;
		input=orig.input;
	}
	
	/**
	 * Return map of changes
	 * @param i
	 * @return
	 */
	public HashMap<String,String> getMap(int i)
	{
		if(i>=maps.size())
			return null;
		return maps.get(i);
	}
	
	/**
	 * Set map of changes at index.
	 * @param map
	 * @param i
	 */
	public void setMap(HashMap<String,String> map,int i)
	{
		
		if(i>=maps.size())
			expandList(maps.size(),i);

		maps.set(i, map);
	}


	/**
	 * Replace lightpath data. Caller class has to handle notification about the changes.
	 * @param newElem
	 * @param overwrite =true -> lp will save as mapannotation
	 * @throws Exception
	 */
	public void addData(LightPath newElem,boolean overwrite,int i) throws Exception
	{
		replaceData(newElem,i);
		LOGGER.info("[DATA] -- replace LightPath data at index: "+i);

		if(overwrite) {
			LOGGER.info("[DATA] -- Save as map: LightPath "+i);
			setMap(getChangesAsMap(i),i);
		}
	}

	/**
	 * Overwrite data with given data
	 * @param newElem
	 */
	private void replaceData(LightPath newElem,int i)
	{
		if(newElem==null)
			return;
		
		if(element.size()<=i){
			expandList(element.size(),i);
		}
		element.set(i,new LightPath(newElem));
	}

	

	
	/** 
	 * @param i index of lightpath
	 * @return lightpath at index i in element list
	 */
	public LightPath getLightPath(int i) 
	{
		if(i>=element.size())
			return null;
		return element.get(i);
	}

	

	
	/**
	 * If index exits size, expand elements and settings list
	 * @param size
	 * @param index
	 */
	private void expandList(int size,int index) 
	{
		for(int i=size;i<index+1;i++){
			element.add(new LightPath());
			maps.add(new HashMap<String,String>());
			input.add(false);
		}
	}

	/**
	 * @return size of element list
	 */
	public int getNumberOfLightPaths() 
	{
		if(element==null)
			return 0;
		
		return element.size();
	}

	/**
	 * Remove lightpath at index from element list
	 * @param index
	 */
	public void remove(int index) {
		if(element!=null && !element.isEmpty())
			element.remove(index);		
	}


	
	/**
	 *  replace lightPaths with changed lightPaths
	 * @param changesLightPath
	 */
	public void update(List<LightPath> changesLightPath) 
	{
		if(changesLightPath==null){
			MonitorAndDebug.printConsole("\t no changes for lightPath");
			return;
		}
		int index=0;
		
		
		for(LightPath lp:changesLightPath)
		{
			replaceData(lp, index);
			HashMap thisMap=new HashMap<String,String>();
			//update map annotation
			int i=1;
			for(Filter f: lp.copyLinkedExcitationFilterList()){
				String id="[Excitation Filter]:["+i+"]:";
				thisMap.put(id+"Model", f.getModel());
				thisMap.put(id+"Manufactur", f.getManufacturer());
				thisMap.put(id+"Type", (f.getType()==null?"": f.getType().getValue()));
				thisMap.put(id+"FilterWheel", f.getFilterWheel());
				
				i++;
			}
			
			Dichroic d= lp.getLinkedDichroic();
			if(d!=null){
				thisMap.put("[Dichroic]:["+i+"]:", d.getModel());
				i++;
			}
			
			for(Filter f: lp.copyLinkedEmissionFilterList()){
				String id="[Emmission Filter]:["+i+"]:";
				thisMap.put(id+"Model", f.getModel());
				thisMap.put(id+"Manufactur", f.getManufacturer());
				thisMap.put(id+"Type",(f.getType()==null?"": f.getType().getValue()));
				thisMap.put(id+"FilterWheel", f.getFilterWheel());
				
				i++;
			}
			setMap(thisMap, index);
			
			index++;
		}
	}


	public void setInput(boolean changes,int index)
	{
		if(index>=input.size())
			return;
		input.set(index, changes);
	}
	
	public boolean hasInput(int index)
	{
		if(index>=input.size())
			return false;
		return input.get(index);
	}
	
	public HashMap<String,String> getChangesAsMap(int index)
	{
		if(hasInput(index)) {
			LightPath lp=getLightPath(index);
			HashMap thisMap=new HashMap<String,String>();
			//update map annotation
			int i=1;
			for(Filter f: lp.copyLinkedExcitationFilterList()){
				String id="[Excitation Filter]:["+i+"]:";
				thisMap.put(id+"Model", f.getModel());
				thisMap.put(id+"Manufactur", f.getManufacturer());
				thisMap.put(id+"Type", (f.getType()==null?"": f.getType().getValue()));
				thisMap.put(id+"FilterWheel", f.getFilterWheel());
				
				i++;
			}
			
			Dichroic d= lp.getLinkedDichroic();
			if(d!=null){
				thisMap.put("[Dichroic]:["+i+"]:", d.getModel());
				i++;
			}
			
			for(Filter f: lp.copyLinkedEmissionFilterList()){
				String id="[Emmission Filter]:["+i+"]:";
				thisMap.put(id+"Model", f.getModel());
				thisMap.put(id+"Manufactur", f.getManufacturer());
				thisMap.put(id+"Type",(f.getType()==null?"": f.getType().getValue()));
				thisMap.put(id+"FilterWheel", f.getFilterWheel());
				
				i++;
			}
			return thisMap;
		}
		return null;
	}
}
