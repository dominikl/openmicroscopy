package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

import java.awt.Component;

import javax.swing.BoxLayout;
import javax.swing.JLabel;

import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.MetaDataModel;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules.FilterCompUI;
import org.openmicroscopy.shoola.util.MonitorAndDebug;
import org.slf4j.LoggerFactory;

import ome.xml.model.Dichroic;
import ome.xml.model.Filter;
import ome.xml.model.FilterSet;
import ome.xml.model.LightPath;
import ome.xml.model.enums.FilterType;

import java.awt.Font;
/**
 * Works for xsi:schemaLocation="http://www.openmicroscopy.org/Schemas/OME/2015-01 
 * @author Susanne Kunis &nbsp;&nbsp;&nbsp;&nbsp; <a
 *         href="mailto:susannekunis@gmail.com">susannekunis@gmail.com</a>
 *
 */
public class LightPathEditor extends JDialog implements ActionListener 
{

	/** Logger for this class. */
	private static final org.slf4j.Logger LOGGER =
			LoggerFactory.getLogger(LightPathEditor.class);

	/**output: selected filters and dichroics for lightPath */
	private List<Object> lightPathList;

	private LightPath lightPath;

	private FilterCompUI filterUI;

	/** input: list for selection*/
	private List<Object> availableFilter;

	/** lightPath result table */
	private LightPathTable lightPathTable;

	private AvailableFilterTable avFilterTable;

	private boolean dataChanged;

	private List<Object> hardwareFilterList;

	private List<Object> imgDataFilterList;

	public LightPathEditor(JFrame parent,String title, List<Object> _availableFilter,
			LightPath l,List<Object> linkHardwareList)
	{
		super(parent,title);
		this.imgDataFilterList=_availableFilter;
		this.hardwareFilterList=linkHardwareList;
		createList();
		lightPath=l;
		lightPathList=new ArrayList<Object>();
		dataChanged=false;
		JPanel	panel=createGUIEditor();
		initGUI(panel);

	}

	private void createList() {
		availableFilter=new ArrayList<>();
		if(hardwareFilterList!=null && hardwareFilterList.size()>0){
			availableFilter.addAll(hardwareFilterList);
		}
		if(imgDataFilterList!=null && imgDataFilterList.size()>0){
			availableFilter.addAll(imgDataFilterList);
		}
	}


	private void initGUI(JPanel panel)
	{
		setBounds(100, 100, 500, 600);
		getContentPane().setLayout(new BorderLayout());
		setModal(true);

		getContentPane().add(panel, BorderLayout.CENTER);

		//Bottom
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));

		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				try {
					createLightPathListFromJTable();
				} catch (Exception e1) {
					LOGGER.error("CAN'T READ LIGHTPATH FROM TABLE");
					e1.printStackTrace();
				}

				setVisible(false);
				dispose();
			}
		});
		okButton.setActionCommand("OK");
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);

		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				lightPathList.clear();
				setVisible(false);
				dispose();
			}
		});
		cancelButton.setActionCommand("Cancel");
		buttonPane.add(cancelButton);


		getContentPane().add(buttonPane, BorderLayout.SOUTH);

		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		pack();
		setVisible(true);
	}

	/**
	 * create Pane: "New Element:"
	 * @return
	 */
	private JPanel newFilterElemPane()
	{
		JButton addBtn = new JButton("Add To Filter List");
		addBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				lightPathTable.appendElem(filterUI, "");
				MonitorAndDebug.printConsole("# LightPathEditor:: \n dataChanged=true");
				dataChanged=true;
			}
		});

		JPanel btnPane=new JPanel();
		btnPane.setLayout(new BoxLayout(btnPane, BoxLayout.X_AXIS));
		btnPane.add(Box.createHorizontalGlue());
		btnPane.add(addBtn);

		filterUI=new FilterCompUI(null);
		filterUI.createDummyPane(false);
		filterUI.buildComponents();

		JLabel label = new JLabel("New Element:");
		label.setFont(new Font("Tahoma", Font.BOLD, 11));


		JPanel topPane=new JPanel();
		topPane.setLayout(new BorderLayout(0, 0));
		topPane.add(label, BorderLayout.NORTH);
		topPane.add(filterUI,BorderLayout.CENTER);
		topPane.add(btnPane,BorderLayout.SOUTH);

		return topPane;
	}

	/**
	 * Create Pane: "Available Elements"
	 * @return
	 */
	private JPanel availableFilterPane()
	{
		JPanel pane=new JPanel();
		pane.setLayout(new BorderLayout(0, 0));

		JLabel label = new JLabel("Available Elements:");
		label.setFont(new Font("Tahoma", Font.BOLD, 11));

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.black, 1),
				BorderFactory.createEmptyBorder(5,5,5,5)));

		avFilterTable = new AvailableFilterTable();  
		scrollPane.setViewportView(avFilterTable);

		//load data
		if(availableFilter!=null)
		{
			for(Object f: availableFilter){
				avFilterTable.appendElem(f);
			}
		}

		JButton addBtn = new JButton("Add To Filter List");
		addBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				int[] rows = avFilterTable.getSelectedRows();
				for(int i=0; i<rows.length; i++){
					Object o;
					//test if prim dichroic still exists:
					Object selectedObj=availableFilter.get(rows[i]);
					o=selectedObj;
					if( selectedObj instanceof Dichroic){
						if(lightPath!=null && lightPath.getLinkedDichroic()!=null){
							o=MetaDataModel.convertDichroicToFilter((Dichroic) selectedObj);
						}
						lightPathTable.appendElem(o, "Dichroic");
					}else if(selectedObj instanceof Filter){
						if(lightPath!=null && lightPath.getLinkedDichroic()==null){
							o=MetaDataModel.convertFilterToDichroic((Filter) selectedObj);
						}
						lightPathTable.appendElem(o, "");
					}else if(selectedObj instanceof FilterSet){
						//TODO append elems
						for(Filter f: ((FilterSet) selectedObj).copyLinkedExcitationFilterList()){
							lightPathTable.appendElem(f, "Exitation");
						}
						if(((FilterSet) selectedObj).getLinkedDichroic()!=null) lightPathTable.appendElem(((FilterSet) selectedObj).getLinkedDichroic(),"Dichroic");
						for(Filter f: ((FilterSet) selectedObj).copyLinkedEmissionFilterList()){
							lightPathTable.appendElem(f, "Emission");
						}
					}
				}//for
				dataChanged=true;
			}

		});

		JPanel btnPane=new JPanel();
		btnPane.setLayout(new BoxLayout(btnPane, BoxLayout.X_AXIS));
		btnPane.add(Box.createHorizontalGlue());
		btnPane.add(addBtn);

		pane.add(label,BorderLayout.NORTH);
		pane.add(scrollPane,BorderLayout.CENTER);
		pane.add(btnPane,BorderLayout.SOUTH);

		return pane;
	}

	private JPanel createGUIEditor()
	{

		JPanel top=new JPanel();
		top.setLayout(new BoxLayout(top,BoxLayout.X_AXIS));
		top.setBorder(new EmptyBorder(5,5,5,5));
		top.add(newFilterElemPane());
		top.add(Box.createHorizontalStrut(5));
		top.add(availableFilterPane());

		JPanel panel=new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBorder(new EmptyBorder(5, 5, 5, 5));
		panel.add(top);


		panel.add(Box.createVerticalStrut(10));

		//bottom
		JPanel panel_2 = new JPanel();
		panel.add(panel_2);
		panel_2.setLayout(new BorderLayout(0, 0));
		panel_2.setBorder(new EmptyBorder(5, 5, 5, 5));

		JLabel label = new JLabel("Filter List:");
		label.setFont(new Font("Tahoma", Font.BOLD, 11));
		panel_2.add(label,BorderLayout.NORTH);

		JScrollPane scrollPane = new JScrollPane();
		panel_2.add(scrollPane,BorderLayout.CENTER);

		lightPathTable = new LightPathTable();
		scrollPane.setViewportView(lightPathTable);



		if(lightPath!=null){

			List<Filter> exL=lightPath.copyLinkedExcitationFilterList();
			for(Filter f:exL){
				try{lightPathTable.appendElem(f, "Exitation");}catch(Exception e){e.printStackTrace();};
			}

			if(lightPath.getLinkedDichroic()!=null)
				lightPathTable.appendElem(lightPath.getLinkedDichroic(), "Dichroic");

			List<Filter> emL=lightPath.copyLinkedEmissionFilterList();
			for(Filter f:emL){
				try{lightPathTable.appendElem(f, "Emission");}catch(Exception e){e.printStackTrace();};
			}
		}

		JMenu insert= new JMenu("Insert Filter before");
		createFilterListMenu(insert,availableFilter);
		lightPathTable.notifyAvFilter(insert);  


		Action action = new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				MonitorAndDebug.printConsole("# LightPathEditor::changes Table \n dataChanged=true");
				dataChanged=true;
				//	            TableCellListener tcl = (TableCellListener)e.getSource();
				//	            MonitorAndDebug.printConsole("Row   : " + tcl.getRow());
				//	            MonitorAndDebug.printConsole("Column: " + tcl.getColumn());
				//	            MonitorAndDebug.printConsole("Old   : " + tcl.getOldValue());
				//	            MonitorAndDebug.printConsole("New   : " + tcl.getNewValue());
			}
		};

		TableCellListener tcl = new TableCellListener(lightPathTable, action);

		return panel;

	}



	private void createFilterListMenu(JMenu insert,	List<Object> list) 
	{
		if(list==null || list.isEmpty()){
			JMenuItem item=new JMenuItem("Empty Filter list");
			insert.add(item);
			return;
		}

		int i=0;
		for(Object f: list){
			if(f!=null){
				JMenuItem item;

				if(f instanceof Dichroic){
					String id=((Dichroic) f).getID()!=null ? ((Dichroic) f).getID() : "#"+i;
					item=new JMenuItem(
							id+"["+((Dichroic) f).getModel()+", Dichroic]");
				}else if(f instanceof Filter){


					String id=((Filter) f).getID()!=null ? ((Filter) f).getID() : "#"+i;
					item=new JMenuItem(
							id+"["+((Filter) f).getModel()+", "+
									((Filter) f).getType()+"]");
				}else{
					//FilterSet
					String id=((FilterSet) f).getID()!=null ? ((FilterSet) f).getID() : "#"+i;
					item=new JMenuItem(	id+"["+((FilterSet) f).getModel());
				}
				item.addActionListener(this);
				insert.add(item);
				i++;
			}
		}

	}

	private void createLightPathListFromJTable() throws Exception
	{
		lightPathList=new ArrayList<Object>();
		for(int i=0; i<lightPathTable.getRowCount();i++){
			Object o=lightPathTable.getRowDataAsLightPathObject(i);
			lightPathList.add(o);
		}
	}

	/**
	 * 
	 * @return list of Filter for module table
	 */
	public List<Object> getLightPathList()
	{
		return lightPathList;
	}

	@Override
	public void actionPerformed(ActionEvent e) 
	{
		JMenuItem source = (JMenuItem)(e.getSource());
		try{
			JMenuItem menuItem = (JMenuItem) e.getSource(); 
			JPopupMenu popupMenu = (JPopupMenu) menuItem.getParent(); 
			Component invoker = popupMenu.getInvoker();  
			lightPathTable.insertElemAtSelection(availableFilter.get(popupMenu.getComponentZOrder(menuItem)));
			MonitorAndDebug.printConsole("# LightPathEditor:: \n dataChanged=true");
			dataChanged=true;
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}


	class AvailableFilterTable extends JTable
	{
		public AvailableFilterTable()
		{
			setModel(new AvailableFilterTableModel());
		}
		public boolean isCellEditable(int row,int column){  
			return false;  
		}

		public void appendElem(Object f)
		{
			((AvailableFilterTableModel) getModel()).addRow(f);
		}

	}

	class AvailableFilterTableModel extends DefaultTableModel
	{
		Class[] columnTypes = new Class[] {
				String.class, String.class, String.class
		};

		private ArrayList<TableColumn> tableColumns;

		public AvailableFilterTableModel()
		{
			super(new Object[][] {},
					new String[] {"ID","Model", "Type"}	); 
		}

		public Class getColumnClass(int columnIndex) {
			return columnTypes[columnIndex];
		}

		public void addRow(Object f)
		{
			Object[] o=parseFromFilterShort(f);
			super.addRow(o);
		}

		public void insertRow(int index, Object f)
		{
			Object[] o=parseFromFilterShort(f);
			super.insertRow(index, o);
		}

		private Object[] parseFromFilterShort(Object e)
		{
			Object[] o=new Object[3];
			if(e!=null){
				if(e instanceof Filter){
					Filter f=(Filter) e;
					o[0]=f.getID()!=null ? f.getID() : "";
					o[1]=f.getModel()!=null ? f.getModel() : "";
					o[2]=f.getType()!=null ? f.getType().toString() : "";
				}else if (e instanceof Dichroic){
					Dichroic f=(Dichroic) e;
					o[0]=f.getID()!=null ? f.getID() : "";
					o[1]=f.getModel()!=null ? f.getModel() : "";
					o[2]="Dichroic";
				}else if(e instanceof FilterSet){
					FilterSet f=(FilterSet)e;
					o[0]=f.getID()!=null ? f.getID() : "";
					o[1]=f.getModel()!=null ? f.getModel() : "";
					o[2]="FilterSet";
				}
			}

			//empty element?
			if(o[0].equals("") && o[1].equals("") && o[2].equals(""))
				return null;

			return o;
		}

	}

	public boolean hasDataChanged() {
		boolean result=dataChanged || lightPathTable.hasDataChanged();
		MonitorAndDebug.printConsole("# LightPathEditor::hasDataChanged(): "+result+": "+dataChanged+", "+lightPathTable.hasDataChanged());
		return result;
	}

	/*
	 *  This class listens for changes made to the data in the table via the
	 *  TableCellEditor. When editing is started, the value of the cell is saved
	 *  When editing is stopped the new value is saved. When the oold and new
	 *  values are different, then the provided Action is invoked.
	 *
	 *  The source of the Action is a TableCellListener instance.
	 */
	public class TableCellListener implements PropertyChangeListener, Runnable
	{
		private JTable table;
		private Action action;

		private int row;
		private int column;
		private Object oldValue;
		private Object newValue;

		/**
		 *  Create a TableCellListener.
		 *
		 *  @param table   the table to be monitored for data changes
		 *  @param action  the Action to invoke when cell data is changed
		 */
		public TableCellListener(JTable table, Action action)
		{
			this.table = table;
			this.action = action;
			this.table.addPropertyChangeListener( this );
		}

		/**
		 *  Create a TableCellListener with a copy of all the data relevant to
		 *  the change of data for a given cell.
		 *
		 *  @param row  the row of the changed cell
		 *  @param column  the column of the changed cell
		 *  @param oldValue  the old data of the changed cell
		 *  @param newValue  the new data of the changed cell
		 */
		private TableCellListener(JTable table, int row, int column, Object oldValue, Object newValue)
		{
			this.table = table;
			this.row = row;
			this.column = column;
			this.oldValue = oldValue;
			this.newValue = newValue;
		}

		/**
		 *  Get the column that was last edited
		 *
		 *  @return the column that was edited
		 */
		public int getColumn()
		{
			return column;
		}

		/**
		 *  Get the new value in the cell
		 *
		 *  @return the new value in the cell
		 */
		public Object getNewValue()
		{
			return newValue;
		}

		/**
		 *  Get the old value of the cell
		 *
		 *  @return the old value of the cell
		 */
		public Object getOldValue()
		{
			return oldValue;
		}

		/**
		 *  Get the row that was last edited
		 *
		 *  @return the row that was edited
		 */
		public int getRow()
		{
			return row;
		}

		/**
		 *  Get the table of the cell that was changed
		 *
		 *  @return the table of the cell that was changed
		 */
		public JTable getTable()
		{
			return table;
		}
		//
		//  Implement the PropertyChangeListener interface
		//
		@Override
		public void propertyChange(PropertyChangeEvent e)
		{
			//  A cell has started/stopped editing

			if ("tableCellEditor".equals(e.getPropertyName()))
			{
				if (table.isEditing())
					processEditingStarted();
				else
					processEditingStopped();
			}
		}

		/*
		 *  Save information of the cell about to be edited
		 */
		private void processEditingStarted()
		{
			//  The invokeLater is necessary because the editing row and editing
			//  column of the table have not been set when the "tableCellEditor"
			//  PropertyChangeEvent is fired.
			//  This results in the "run" method being invoked

			SwingUtilities.invokeLater( this );
		}
		/*
		 *  See above.
		 */
		@Override
		public void run()
		{
			row = table.convertRowIndexToModel( table.getEditingRow() );
			column = table.convertColumnIndexToModel( table.getEditingColumn() );
			oldValue = table.getModel().getValueAt(row, column);
			newValue = null;
		}

		/*
		 *	Update the Cell history when necessary
		 */
		private void processEditingStopped()
		{
			newValue = table.getModel().getValueAt(row, column);

			//  The data has changed, invoke the supplied Action

			if (! newValue.equals(oldValue))
			{
				//  Make a copy of the data in case another cell starts editing
				//  while processing this change

				TableCellListener tcl = new TableCellListener(
						getTable(), getRow(), getColumn(), getOldValue(), getNewValue());

				ActionEvent event = new ActionEvent(
						tcl,
						ActionEvent.ACTION_PERFORMED,
						"");
				action.actionPerformed(event);
			}
		}
	}


}

