package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util;

import java.io.File;

import javax.swing.tree.DefaultMutableTreeNode;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.MetaDataModel;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.MetaDataModelObject;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.microscope.MetaDataView;
import org.openmicroscopy.shoola.env.data.model.ImportableFile;
import org.openmicroscopy.shoola.util.MonitorAndDebug;
/**
* @author Susanne Kunis &nbsp;&nbsp;&nbsp;&nbsp; <a
*         href="mailto:susannekunis@gmail.com">susannekunis@gmail.com</a>
*/
public class FNode extends DefaultMutableTreeNode
{
	private ImportUserData importData;
	private MetaDataModelObject modelObj;
	private MetaDataView view;

	private MapAnnotationObject mapAnnot;

	public FNode(File file){
		this.importData=null;
		setUserObject(file);
	}
	public FNode(Object object)
	{
		this.importData=null;
		setUserObject(object);
	}

	public FNode(File file,ImportUserData importData){
		this.importData=importData;
		setUserObject(file);
	}

	public File getFile()
	{
		Object o=getUserObject();
		if(o instanceof File)
			return (File) o;
		else
			return null;
	}

	public String toString() {
		String ad="";
		if(importData!=null){
			ad=" [Group: "+importData.getGroup()+", Project: "+
					importData.getProject()+"]";
		}
		if(getFile()==null){
			return (String)getUserObject();
		}
		return getFile().getName()+ad;
	} 

	public String getAbsolutePath()
	{
		if(getFile()==null)
			return null;

		return getFile().getAbsolutePath();
	}


	public boolean hasImportData()
	{
		return (importData!=null);
	}

	public ImportUserData getImportData()
	{
		return importData;
	}

	public void setModelObject(MetaDataModelObject m)
	{
		this.modelObj=m;
	}

	public MetaDataModelObject getModelObject()
	{
		return modelObj;
	}

	public MetaDataModel getModelOfSeries(int index)
	{
		if(modelObj==null)
			return null;

		return modelObj.getList().get(index);
	}

	public boolean hasModelObject() {
		return modelObj!=null;
	}
	public MetaDataView getView() {
		return view;
	}
	public void setView(MetaDataView view) {
		this.view = view;
	}

	/**
	 * Save model if view exists.
	 */
	public void saveModel() throws Exception
	{
		if(view==null)
			return;
		try{
			view.saveModel();
		}catch(Exception e){

			e.printStackTrace();
		}
		modelObj = view.getModelObject();

	}
	public void saveExtendedData()
	{
		if(view==null)
			return;

		view.saveExtendedMetaData();
	}

	/**
	 * A node can have a mapannotation (inherit from parent) but not a view.
	 * If a node has a view, mapAnnotation of parent will be automated loaded to the view at creation time.
	 * @param map
	 */
	public void setMapAnnotation(MapAnnotationObject map)
	{
		mapAnnot=map;
	}

	/**
	 * A node can have a mapannotation (inherit from parent) but not a view.
	 * If a node has a view, mapAnnotation of parent will be automated loaded to the view at creation time.
	 * @param map
	 */
	public MapAnnotationObject getMapAnnotation()
	{
		if(view!=null){
			mapAnnot=view.getMapAnnotation();
		}
		return mapAnnot;
	}


	/**
	 * Function to control map data
	 */
	public void printMaps()
	{
		MonitorAndDebug.printConsole("FNODE :: "+getAbsolutePath());
		if(view!=null){
			MonitorAndDebug.printConsole("\t View Map:");
			view.getMapAnnotation().printObject();
		}
		if(mapAnnot!=null){
			MonitorAndDebug.printConsole("\t Intern Map:");	
			mapAnnot.printObject();
		}
	}

}
