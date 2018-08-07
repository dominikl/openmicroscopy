package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.view;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import ome.xml.model.LightPath;
import ome.xml.model.Dichroic;
import ome.xml.model.Filter;
import ome.xml.model.enums.FilterType;
import ome.xml.model.FilterSet;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.MetaDataModel;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules.FilterCompUI;
import org.slf4j.LoggerFactory;
/**
 * Table in LightPath module
 * 
 * @author Susanne Kunis &nbsp;&nbsp;&nbsp;&nbsp; <a
 *         href="mailto:susannekunis@gmail.com">susannekunis@gmail.com</a>
 */
public class LightPathTableSmall extends JTable
{
	/** Logger for this class. */
	private static final org.slf4j.Logger LOGGER =
			LoggerFactory.getLogger(LightPathTableSmall.class);

	private List<Object> lightPathMembers;
	private LightPath availableElem;

	public LightPathTableSmall()
	{
		setModel(new CustomModel());
		lightPathMembers=new ArrayList<Object>();
		availableElem=null;
	}

	public boolean isCellEditable(int row,int column)
	{  
		return false;  
	} 

	public void appendElem(Object o, String category)
	{
		if(o instanceof FilterCompUI){
			Filter f;
			try {
				f = (Filter) ((FilterCompUI) o).getData();
				if(f!=null){
					((CustomModel) getModel()).addRow(f,"");
					((FilterCompUI) o).clearDataValues();
				}
			} catch (Exception e) {
				LOGGER.error("Filter is not a valid element!!!");
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}else{
			((CustomModel) getModel()).addRow(o,category);
		}
		lightPathMembers.add(o);
	}

	/**
	 * Return a row from the table as a array of strings
	 * @param rowIndex The index of the row you would like
	 * @return Returns the row from the table as an array of strings or null if
	 * the index is invalid
	 */
	public Object getRowData(int rowIndex)
	{
		if(lightPathMembers!=null && !lightPathMembers.isEmpty()){
			return lightPathMembers.get(rowIndex);
		}
		else
			return null;
	}



	public void clearData()
	{
		lightPathMembers=new ArrayList<Object>();
		availableElem=null;
		setModel(new CustomModel());
	}


	public List<Object> getLightPathElementList() 
	{
		return lightPathMembers;
	}

	public LightPath getLightPath()
	{
		if(availableElem==null) {
			List<Object> list=getLightPathElementList();
			availableElem=createLightPath(list);
		}
		return availableElem;
	}

	private LightPath createLightPath(List<Object> list) {

		if(list!=null && !list.isEmpty()){

			LightPath newElement=new LightPath();
			int linkType=1;
			for(Object f : list)
			{
				if(f!=null){
					Dichroic pD=newElement.getLinkedDichroic();
					boolean primDNotExists= pD==null ? true : false ;

					// Dichroic
					if(f instanceof Dichroic){
						linkType=2;
						// primary dichroic exists?
						if(primDNotExists){
							newElement.linkDichroic((Dichroic) f);
						}else{
							LOGGER.warn("primary Dichroic still exists! [LightPathViewer::createLightPath]");
							newElement.linkEmissionFilter(MetaDataModel.convertDichroicToFilter((Dichroic)f));
						}

					}else if(f instanceof Filter){

						String	type= ((Filter) f).getType()!=null ? ((Filter) f).getType().toString() : "";
						//filters that comes before and dichroic are exitation filters by definition
						if(	!type.equals(FilterType.DICHROIC.getValue()) && 
								linkType==1){
							newElement.linkExcitationFilter((Filter) f);
						}else{// link additional dichroic as emission filter
							linkType=2;

							if( primDNotExists){
								newElement.linkDichroic(MetaDataModel.convertFilterToDichroic((Filter) f));
							}else{
								newElement.linkEmissionFilter((Filter) f);
							}
						}
					}else if(f instanceof FilterSet){
						//Exitations
						for(Filter subF:((FilterSet) f).copyLinkedExcitationFilterList()){
							newElement.linkExcitationFilter(subF);
						}
						//Dichroic
						if(primDNotExists){
							newElement.linkDichroic(((FilterSet) f).getLinkedDichroic());
						}else{
							LOGGER.warn("primary Dichroic still exists! [LightPathViewer::createLightPath]");
							newElement.linkEmissionFilter(MetaDataModel.convertDichroicToFilter(((FilterSet) f).getLinkedDichroic()));
						}
						//Emmisions
						linkType=2;
						for(Filter subF : ((FilterSet) f).copyLinkedEmissionFilterList()){
							newElement.linkEmissionFilter(subF);
						}
					}
				}//f!=null
			}
			return newElement;
		}
		return null;
	}


	private void setExFilter(List<Filter> exList) {
		if(exList!=null){
			for(Filter f:exList){
				appendElem(f,LightPathViewer.EXITATION);
			}
		}else{
			LOGGER.info("can't load EX Filter element");
		}
	}

	private void setEmFilter(List<Filter> emList) {
		if(emList!=null)
		{
			for(Filter f:emList)
			{
				String type="";
				try {
					type=f.getType().getValue();
				} catch (Exception e) {
				}
				String elemType=LightPathViewer.EMISSION;
				if(type.equals(FilterType.DICHROIC.toString()))					{
					elemType=LightPathViewer.DICHROIC;
				}
				appendElem(f,elemType);
			}
		}else{
			LOGGER.info("::ATTENTION:: can't load EM Filter element ");
		}
	}

	private void setDichroic(Dichroic d) {
		if(d!=null){
			appendElem(d,LightPathViewer.DICHROIC);
		}else{
			LOGGER.info("No dichroic element is given");
		}
	}

	public void setLightPath(List<Object> elements) {
		clearData();
		availableElem=createLightPath(elements);
		setExFilter(availableElem.copyLinkedExcitationFilterList());
		setDichroic(availableElem.getLinkedDichroic());
		setEmFilter(availableElem.copyLinkedEmissionFilterList());
	}

	public void setLightPath(LightPath lightPath)
	{
		clearData();
		availableElem=lightPath;
		//load primary dichroic of instrument
		Dichroic d=lightPath.getLinkedDichroic();
		List<Filter> emList=lightPath.copyLinkedEmissionFilterList();
		List<Filter> exList=lightPath.copyLinkedExcitationFilterList();

		setExFilter(exList);

		setDichroic(d);

		setEmFilter(emList);
	}




	class CustomModel extends DefaultTableModel
	{
		Class[] columnTypes = new Class[] {
				String.class,String.class, String.class,String.class
		};


		public CustomModel()
		{
			super(new Object[][] {},
					new String[] {"Model", "Manufacturer", "Type","FilterWheel"});

		}

		public Class getColumnClass(int columnIndex) {
			return columnTypes[columnIndex];
		}

		public void addRow(Object o, String category)
		{
			super.addRow(parseFromFilterLong(o,category));
		}

		private Object[] parseFromFilterLong(Object e,String cat)
		{
			Object[] o=new Object[4];
			if(e!=null){
				if(e instanceof Filter){
					Filter f=(Filter) e;
					String type=f.getType()!=null ? f.getType().toString() : "";
					o[0]=f.getModel()!=null ? f.getModel() : "";
					o[1]=f.getManufacturer()!=null ? f.getManufacturer() : "";
					o[2]=f.getType()!=null ? f.getType().toString() : "";
					o[3]=f.getFilterWheel()!=null ? f.getFilterWheel() :"";
				}else if (e instanceof Dichroic){
					Dichroic f=(Dichroic) e;
					o[0]=f.getModel()!=null ? f.getModel() : "";
					o[1]=f.getManufacturer()!=null ? f.getManufacturer() : "";
					o[2]="Dichroic";
					o[3]="";
				}
			}
			return o;
		}

	}

}
