package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules.FilterCompUI;
import org.openmicroscopy.shoola.util.MonitorAndDebug;
import org.slf4j.LoggerFactory;

import ome.xml.model.Dichroic;
import ome.xml.model.Filter;
import ome.xml.model.enums.FilterType;
/**
 * Works for xsi:schemaLocation="http://www.openmicroscopy.org/Schemas/OME/2015-01 
 * @author Susanne Kunis &nbsp;&nbsp;&nbsp;&nbsp; <a
 *         href="mailto:susannekunis@gmail.com">susannekunis@gmail.com</a>
 *
 */
public class LightPathTable extends JTable
{

	/** Logger for this class. */
	private static final org.slf4j.Logger LOGGER =
			LoggerFactory.getLogger(LightPathTable.class);

	private JPopupMenu popupMenu;
	private boolean dataChanged; 

	public LightPathTable()
	{
		setModel(new LightPathTableModel());

		popupMenu = new JPopupMenu();
		JMenuItem removeItem=new JMenuItem("Remove");
		removeItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removeSelectionFromTable();
				MonitorAndDebug.printConsole("# LightPathTable:: \n dataChanged=true");
				dataChanged=true;
			}
		});
		JMenuItem mvUpItem=new JMenuItem("Move up");
		mvUpItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				moveRowUp();
				MonitorAndDebug.printConsole("# LightPathTable:: \n dataChanged=true");
				dataChanged=true;
			}
		});

		JMenuItem mvDownItem=new JMenuItem("Move down");
		mvDownItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				moveRowDown();
				MonitorAndDebug.printConsole("# LightPathTable:: \n dataChanged=true");
				dataChanged=true;
			}
		});

		popupMenu.add(mvUpItem);
		popupMenu.add(mvDownItem);
		popupMenu.add(new JPopupMenu.Separator());
		popupMenu.add(removeItem);
		popupMenu.add(new JPopupMenu.Separator());




		addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent me) {
				if (SwingUtilities.isRightMouseButton(me))
					popupMenu.show(getParent(), me.getX(), me.getY());
			}
		});




		TableColumn classColumn = getColumnModel().getColumn(LightPathTableModel.CLASS_IDX);
		classColumn.setCellEditor(new DefaultCellEditor(new JComboBox(FilterCompUI.classificList)));
		classColumn = getColumnModel().getColumn(LightPathTableModel.TYPE_IDX);
		classColumn.setCellEditor(new DefaultCellEditor(new JComboBox(FilterCompUI.typeList)));
		classColumn = getColumnModel().getColumn(LightPathTableModel.CAT_IDX);
		classColumn.setCellEditor(new DefaultCellEditor(new JComboBox(new String[]{"","Exitation","Dichroic","Emission"})));
	}



	public boolean isCellEditable(int row,int column){  
		if(column==0) return false;  
		return true;  
	} 


	public void notifyAvFilter(JMenu insert)
	{
		popupMenu.add(insert);
	}


	public void insertElemAtSelection(Object o)
	{
		LightPathTableModel model =  (LightPathTableModel)getModel();
		int[] rows = getSelectedRows();
		try{
			//insert before first selection
			model.insertRow(rows[0], o);
		}catch(Exception e){
			LOGGER.error("Can't insert filter into LIGHTPATH");
		}
	}

	public void appendElem(Object o, String category)
	{
		if(o instanceof FilterCompUI){
			Filter f;
			try {
				f = (Filter) ((FilterCompUI) o).getData();//((FilterCompUI) o).copyData();
				if(f!=null){
					((LightPathTableModel) getModel()).addRow(f,"");
					((FilterCompUI) o).clearDataValues();
				}
			} catch (Exception e) {
				LOGGER.error("Filter is not a valid element!!!");
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}else{
			((LightPathTableModel) getModel()).addRow(o,category);
		}
	}

	/**
	 * Return a row from the table as a array of strings
	 * @param rowIndex The index of the row you would like
	 * @return Returns the row from the table as an array of strings or null if
	 * the index is invalid
	 */
	public String[] getRowData(int rowIndex)
	{
		//test the index
		if ( (rowIndex  >  getRowCount()) || rowIndex  <  0)
			return null;

		ArrayList<String> data = new ArrayList<String>();
		for (int c = 0; c  <  getColumnCount(); c++)
		{
			data.add((String) getValueAt(rowIndex, c));
		}
		String[] retVal = new String[data.size()];
		for (int i = 0; i  <  retVal.length; i++)
		{
			retVal[i] = data.get(i);
		}
		return retVal;
	}

	/**
	 * 
	 * @param rowIndex
	 * @return row as Dichroic element or Filter element
	 * @throws Exception
	 */
	public Object getRowDataAsLightPathObject(int rowIndex) throws Exception {
		String[] s=getRowData(rowIndex);

		System.out.println("editor: Filter element: "+s[LightPathTableModel.MODEL_IDX]);
		String type="Filter";

		if(s[0]!=null ){
			if( s[LightPathTableModel.TYPE_IDX].contains("Dichroic"))
				type="Dichroic";
		}
		switch (type) {
		case "Dichroic":
			Dichroic d= new Dichroic();
			d.setID(s[LightPathTableModel.ID_IDX]);
			d.setModel(s[LightPathTableModel.MODEL_IDX]);
			d.setManufacturer(s[LightPathTableModel.MANUFAC_IDX]);
			return d;
		default:
			Filter o=new Filter();
			if(s[3].equals(FilterType.DICHROIC.toString())){
				((Filter) o).setID(s[LightPathTableModel.ID_IDX]);
				((Filter) o).setModel(s[LightPathTableModel.MODEL_IDX]);
				((Filter) o).setManufacturer(s[LightPathTableModel.MANUFAC_IDX]);
				((Filter) o).setType(FilterType.DICHROIC);
			}else{
				((Filter) o).setID(s[LightPathTableModel.ID_IDX]);
				((Filter) o).setModel(s[LightPathTableModel.MODEL_IDX]);
				((Filter) o).setManufacturer(s[LightPathTableModel.MANUFAC_IDX]);
				((Filter) o).setType(s[LightPathTableModel.TYPE_IDX].equals("")? null : FilterType.fromString(s[LightPathTableModel.TYPE_IDX]));
				((Filter) o).setFilterWheel(s[LightPathTableModel.FILTERW_IDX]);
			}
			return o;

		}
	}

	private void removeSelectionFromTable()
	{
		LightPathTableModel model = (LightPathTableModel)getModel();
		int numRows = getSelectedRows().length;
		for(int i=0; i<numRows ; i++ ) {
			model.removeRow(getSelectedRow());
		}
	}

	private void moveRowUp()
	{
		LightPathTableModel model =  (LightPathTableModel)getModel();
		int[] rows = getSelectedRows();
		try{
			model.moveRow(rows[0],rows[rows.length-1],rows[0]-1);
			setRowSelectionInterval(rows[0]-1, rows[rows.length-1]-1);
		}catch(Exception e){}
	}
	private void moveRowDown()
	{
		LightPathTableModel model =  (LightPathTableModel)getModel();
		int[] rows = getSelectedRows();
		try{
			model.moveRow(rows[0],rows[rows.length-1],rows[0]+1);
			setRowSelectionInterval(rows[0]+1, rows[rows.length-1]+1);
		}catch(Exception e){}
	}

	public void clearData()
	{
		LightPathTableModel model = (LightPathTableModel)getModel();
		for(int i=0; i<model.getRowCount(); i++){
			model.removeRow(i);
		}
	}

	public boolean hasDataChanged()
	{
		return dataChanged;
	}




	class LightPathTableModel extends DefaultTableModel
	{
		Class[] columnTypes = new Class[] {
				String.class,String.class, String.class, String.class, String.class, String.class,String.class
		};

		private ArrayList<TableColumn> tableColumns;

		public static final int ID_IDX=0;
		public static final int MODEL_IDX=2;
		public static final int MANUFAC_IDX=3;
		public static final int TYPE_IDX=4;
		public static final int FILTERW_IDX=5;
		public static final int CLASS_IDX=1;
		public static final int CAT_IDX=6;

		public LightPathTableModel()
		{
			super(new Object[][] {},
					new String[] {"ID","Classification","Model", "Manufacturer", "Type", "Filterwheel", "Category"});

		}

		public Class getColumnClass(int columnIndex) {
			return columnTypes[columnIndex];
		}

		public void addRow(Object o, String category)
		{
			super.addRow(parseFromFilterLong(o,category));
		}

		public void insertRow(int index, Object o)
		{
			super.insertRow(index, parseFromFilterLong(o, ""));
		}

		private Object[] parseFromFilterLong(Object e,String cat)
		{
			Object[] o=new Object[7];
			if(e!=null){
				if(e instanceof Filter){
					Filter f=(Filter) e;
					String type=f.getType()!=null ? f.getType().toString() : "";
					o[ID_IDX]=f.getID()!=null ? f.getID() : "";
					o[MODEL_IDX]=f.getModel()!=null ? f.getModel() : "";
					o[MANUFAC_IDX]=f.getManufacturer()!=null ? f.getManufacturer() : "";
					o[TYPE_IDX]=f.getType()!=null ? f.getType().toString() : "";
					o[FILTERW_IDX]=f.getFilterWheel()!=null ? f.getFilterWheel() : "";
					o[CLASS_IDX]= "";
					o[CAT_IDX]= type.equals(FilterType.DICHROIC.toString())? "Dichroic" : (cat!=null ?cat : "");
				}else if (e instanceof Dichroic){
					Dichroic f=(Dichroic) e;
					o[ID_IDX]=f.getID()!=null ? f.getID() : "";
					o[MODEL_IDX]=f.getModel()!=null ? f.getModel() : "";
					o[MANUFAC_IDX]=f.getManufacturer()!=null ? f.getManufacturer() : "";
					o[TYPE_IDX]="Dichroic";
					o[FILTERW_IDX]="";
					o[CLASS_IDX]="";
					o[CAT_IDX]="Dichroic";
				}
			}
			return o;
		}

	}

}
