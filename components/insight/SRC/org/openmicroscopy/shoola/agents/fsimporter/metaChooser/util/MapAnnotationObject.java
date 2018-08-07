package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import omero.gateway.model.MapAnnotationData;
import omero.model.MapAnnotation;
import omero.model.MapAnnotationI;
import omero.model.NamedValue;
/**
* @author Susanne Kunis &nbsp;&nbsp;&nbsp;&nbsp; <a
*         href="mailto:susannekunis@gmail.com">susannekunis@gmail.com</a>
*/
public class MapAnnotationObject {

	private String fileName;
	private List<MapAnnotationData> mapAnnotation;

	public MapAnnotationObject(String fileName, MapAnnotationData map)
	{
		this.fileName=fileName;
		this.mapAnnotation=new ArrayList<>();
		this.mapAnnotation.add(map);
	}

	public MapAnnotationObject(String fileName, List<MapAnnotationData> maps)
	{
		this.fileName=fileName;
		this.mapAnnotation=maps;
	}

	public MapAnnotationObject(MapAnnotationObject orig)
	{
		this.fileName=orig.fileName;
		this.mapAnnotation=new ArrayList<>();
		// deep copy
		List<MapAnnotationData> origList=orig.getMapAnnotationList();
		for(MapAnnotationData m:origList){
			List<NamedValue> valuesOrig=(List<NamedValue>) m.getContent();
			MapAnnotation ma = new MapAnnotationI();
			//copy values
			List<NamedValue> values=new ArrayList<NamedValue>();
			for(NamedValue val:valuesOrig){
				values.add(new NamedValue(val.name, val.value));
			}
			ma.setMapValue(values);
			this.mapAnnotation.add(new MapAnnotationData(ma));
		}
	}


	public String getFileName() {
		return fileName;
	}

	public void setFileName(String name){
		fileName=name;
	}

	public MapAnnotationData getMapAnnotation(int i) {
		return mapAnnotation.get(i);
	}

	public List<MapAnnotationData> getMapAnnotationList() {
		return mapAnnotation;
	}

	static public void printMapAnnotation(MapAnnotationData map)
	{
		System.out.println("\t mapAnnotation: ");

		List<NamedValue> values=(List<NamedValue>) map.getContent();
		for(NamedValue val:values){
			System.out.println("\t\t"+ val.name+": "+val.value);
		}
	}

	static public void printMapAnnotations(Map<String,MapAnnotationObject> map)
	{
		for (Iterator i = map.entrySet().iterator(); i.hasNext(); ) {
			Map.Entry next = (Map.Entry)i.next();
			System.out.println("\t mapAnnotation - : "+next.getKey());
			printObject((MapAnnotationObject) next.getValue());
		}
	}

	static public void printObject(MapAnnotationObject o)
	{
		if(o==null)
			return;
		System.out.println("\t file : "+o.getFileName());
		List<MapAnnotationData> list=o.getMapAnnotationList();
		int index=0;
		for(MapAnnotationData m:list){
			System.out.println("Series_"+index++);
			printMapAnnotation(m);
		}
	}

	public void printObject() {
		printObject(this);

	}

}
