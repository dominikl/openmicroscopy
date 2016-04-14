package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import ome.xml.model.Detector;
import ome.xml.model.Objective;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.UOSMetadataLogger;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.ObjectiveEditor.ObjectiveTable;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.ObjectiveEditor.ObjectiveTableModel;

public class DetectorEditor extends JDialog implements ActionListener 
{
	/** Logger for this class. */
    private static Logger LOGGER = Logger.getLogger(UOSMetadataLogger.class.getName());
    
    private List<Detector> availableDetectors;
    private DetectorTable detectorTable;
    private Detector selectDetector;
    
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public DetectorEditor(JFrame parent, String title, List<Detector> availableDetectors)
	{
		super(parent,title);
		this.availableDetectors=availableDetectors;
		selectDetector=null;
		initGUI();
	}
	
	
	
	private void initGUI() 
	{

		setBounds(100, 100, 500, 600);
		getContentPane().setLayout(new BorderLayout());
		setModal(true);

		getContentPane().add(availableDetectorPane(), BorderLayout.CENTER);

		//Bottom
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		
		JButton loadBtn= new JButton("Load Workstation List");
		loadBtn.setActionCommand("Load");
		loadBtn.setEnabled(false);
		buttonPane.add(loadBtn);
		buttonPane.add(Box.createHorizontalGlue());

		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
					try {
						int row=detectorTable.getSelectedRow();
						if(row!=-1)
							selectDetector=availableDetectors.get(row);
					} catch (Exception e1) {
						LOGGER.severe("can't read detector from table");
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
				selectDetector=null;
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

	



	private Component availableDetectorPane() 
	{
		JPanel pane=new JPanel();
		pane.setLayout(new BorderLayout(5, 5));
		pane.setBorder(new EmptyBorder(5,5,5,5));

		JLabel label = new JLabel("Available Elements:");
		label.setFont(new Font("Tahoma", Font.BOLD, 11));
		
		JScrollPane scrollPane = new JScrollPane();
		

		detectorTable = new DetectorTable();
		scrollPane.setViewportView(detectorTable);
//		detectorTable.setPreferredScrollableViewportSize(detectorTable.getPreferredSize());
//		detectorTable.setFillsViewportHeight(true);
		detectorTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		if(availableDetectors!=null){
			for(Detector o: availableDetectors){
				detectorTable.appendElem(o);
			}
		}
		pane.add(scrollPane, BorderLayout.CENTER);
		return pane;
	}



	class DetectorTable extends JTable
	{
		public DetectorTable()
		{
			setModel(new DetectorTableModel());
//		
		}
		public boolean isCellEditable(int row,int column){  
	        return false;  
	      }
		
		public void appendElem(Detector o)
		{
			((DetectorTableModel) getModel()).addRow(o);
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
		
		public void clearData()
		{
			DetectorTableModel model = (DetectorTableModel)getModel();
			for(int i=0; i<model.getRowCount(); i++){
				model.removeRow(i);
			}
		}
	}
	
	class DetectorTableModel extends DefaultTableModel
	{
		Class[] columnTypes = new Class[] {
				String.class,String.class, String.class, String.class, String.class, 
				String.class,String.class,String.class
		};

		private ArrayList<TableColumn> tableColumns;

		public DetectorTableModel()
		{
			super(new Object[][] {},
					new String[] {"ID","Model", "Manufactur", "Type",
					"Zoom", "Amplification Gain","Gain","Voltage"});

		}

		public Class getColumnClass(int columnIndex) {
			return columnTypes[columnIndex];
		}

		public void addRow(Detector o)
		{
			super.addRow(parseFromDetectorLong(o));
		}

		public void insertRow(int index, Detector o)
		{
			super.insertRow(index, parseFromDetectorLong(o));
		}
		
		private Object[] parseFromDetectorLong(Detector e)
		{
			Object[] o=new Object[8];
			if(e!=null){
				try{o[0]=e.getID()!=null ? e.getID() : "";
				}catch(Exception err){
					LOGGER.severe("[EDITOR] Can't parse Detector::id");
				}
				try{o[1]=e.getModel()!=null ? e.getModel() : "";
				}catch(Exception err){
					LOGGER.severe("[EDITOR] Can't parse Detector::model");
				}
				try{o[2]=e.getManufacturer()!=null ? e.getManufacturer() : "";
				}catch(Exception err){
					LOGGER.severe("[EDITOR] Can't parse Detector::manufac");
				}
				try{o[3]=e.getType()!=null ? e.getType().toString() : "";
				}catch(Exception err){
					LOGGER.severe("[EDITOR] Can't parse Detector::type");
				}
				try{o[4]=e.getZoom()!=null ? e.getZoom().toString() : "";
				}catch(Exception err){
					LOGGER.severe("[EDITOR] Can't parse Detector::zoom");
				}
				try{o[5]=e.getAmplificationGain()!=null ? e.getAmplificationGain().toString():"";
				}catch(Exception err){
					LOGGER.severe("[EDITOR] Can't parse Detector::amplGain");
				}
				try{o[6]=e.getGain()!=null ? e.getGain().toString():"";
				}catch(Exception err){
					LOGGER.severe("[EDITOR] Can't parse Detector::gain");
				}
				try{
					String val="";
					if(e.getVoltage()!=null){
						val=e.getVoltage().value().toString()+e.getVoltage().unit().getSymbol();
					}
					o[7]=val;
				}catch(Exception err){
					LOGGER.severe("[EDITOR] Can't parse Detector::voltage");
					err.printStackTrace();
				}
				
			}
			return o;
		}
		
	}

	public Detector getDetector() {
		return selectDetector;
	}

}

