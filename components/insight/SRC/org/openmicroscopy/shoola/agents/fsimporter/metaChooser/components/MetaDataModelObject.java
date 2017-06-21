package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components;

import java.util.ArrayList;
import java.util.List;

/**
 * Model for a image could be a single model or (for series data) a list of models
 * @author Kunis
 *
 */
public class MetaDataModelObject 
{
	private boolean seriesData;
	private List<MetaDataModel> list;
	
	public MetaDataModelObject()
	{
		seriesData=false;
		list=new ArrayList<MetaDataModel>();
	}
	
	public MetaDataModelObject(boolean series, List<MetaDataModel> mList)
	{
		seriesData=series;
		list=mList;
	}

	public boolean isSeriesData() {
		return seriesData;
	}

	public void setSeriesData(boolean seriesData) {
		this.seriesData = seriesData;
	}

	public List<MetaDataModel> getList() {
		return list;
	}

	public void setList(List<MetaDataModel> list) {
		this.list = list;
	}
	
	

	// TODO: at the moment not for series data
	public void updateData(MetaDataModelObject newData) throws Exception 
	{
		List<MetaDataModel> modelList_new=newData.getList();
		list.get(0).updateData(modelList_new.get(0));
	}


	/**
	 * 
	 * @return true if model has notice user input/changes on tags
	 */
	public boolean hasBeenModified() 
	{
		
		boolean res=false;
		if(list==null || list.isEmpty()){
			System.out.println("# MetaDataModel::hasBeenModified(): false");
			return res;
		}
		for(MetaDataModel m: list){
			res= res || m.noticUserInput();
		}
		System.out.println("# MetaDataModel::hasBeenModified(): "+res);
		return res;
	}

	public void clearListOfModifications() 
	{
		if(list==null || list.isEmpty()){
			return;
		}
		System.out.println("## MetaDataModelObject::clearListOfModifications()");
		for(MetaDataModel m: list){
			m.setDataChange(false);
		}
		
	}
}
