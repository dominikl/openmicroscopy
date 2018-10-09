package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
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

import ome.xml.model.Objective;
import org.slf4j.LoggerFactory;
/**
 * Works for xsi:schemaLocation="http://www.openmicroscopy.org/Schemas/OME/2015-01 
 * @author Susanne Kunis &nbsp;&nbsp;&nbsp;&nbsp; <a
 *         href="mailto:susannekunis@gmail.com">susannekunis@gmail.com</a>
 *
 */
public class ObjectiveEditor extends JDialog implements ActionListener 
{
	/** Logger for this class. */
	private static final org.slf4j.Logger LOGGER =
			LoggerFactory.getLogger(ObjectiveEditor.class);

	private List<Objective> availableObjectives;
	private ObjectiveTable objectivTable;
	private Objective selectObjective;

	private List<Objective> hardwareDetectorList;

	private List<Objective> imgDataObjectives;

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
	}

	public ObjectiveEditor(JFrame parent, String title, 
			List<Objective> availableObjectives,List<Objective> linkHardwareList)
	{
		super(parent,title);
		this.imgDataObjectives=availableObjectives;
		this.hardwareDetectorList=linkHardwareList;
		createList();
		selectObjective=null;
		initGUI();
	}

	private void createList() {
		availableObjectives=new ArrayList<>();
		if(hardwareDetectorList!=null && hardwareDetectorList.size()>0){
			availableObjectives.addAll(hardwareDetectorList);
		}
		if(imgDataObjectives!=null && imgDataObjectives.size()>0){
			availableObjectives.addAll(imgDataObjectives);
		}
	}


	private void initGUI() 
	{

		setBounds(100, 100, 500, 600);
		getContentPane().setLayout(new BorderLayout());
		setModal(true);

		getContentPane().add(availableObjectivePane(), BorderLayout.CENTER);

		//Bottom
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));

		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				try {
					int row=objectivTable.getSelectedRow();
					if(row!=-1)
						selectObjective=availableObjectives.get(row);
				} catch (Exception e1) {
					LOGGER.error("can't read objective from table");
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
				selectObjective=null;
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





	private Component availableObjectivePane() 
	{
		JPanel pane=new JPanel();
		pane.setLayout(new BorderLayout(5, 5));
		pane.setBorder(new EmptyBorder(5,5,5,5));

		JLabel label = new JLabel("Available Elements:");
		label.setFont(new Font("Tahoma", Font.BOLD, 11));

		JScrollPane scrollPane = new JScrollPane();


		objectivTable = new ObjectiveTable();
		scrollPane.setViewportView(objectivTable);
		objectivTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		if(availableObjectives!=null){
			for(Objective o: availableObjectives){
				objectivTable.appendElem(o);
			}
		}
		pane.add(scrollPane, BorderLayout.CENTER);
		return pane;
	}



	class ObjectiveTable extends JTable
	{
		public ObjectiveTable()
		{
			setModel(new ObjectiveTableModel());
		}
		public boolean isCellEditable(int row,int column){  
			return false;  
		}

		public void appendElem(Objective o)
		{
			((ObjectiveTableModel) getModel()).addRow(o);
		}
	}

	class ObjectiveTableModel extends DefaultTableModel
	{
		Class[] columnTypes = new Class[] {
				String.class,String.class, String.class, String.class, String.class, 
				String.class,String.class,String.class,String.class
		};

		private ArrayList<TableColumn> tableColumns;

		public ObjectiveTableModel()
		{
			super(new Object[][] {},
					new String[] {"ID","Model", "Manufactur", "Nominal Magn.",
							"Calibration Magn.", "Lens NA","Immersion","Correction",
			"Working Distance"});

		}

		public Class getColumnClass(int columnIndex) {
			return columnTypes[columnIndex];
		}

		public void addRow(Objective o)
		{
			super.addRow(parseFromObjectiveLong(o));
		}

		public void insertRow(int index, Objective o)
		{
			super.insertRow(index, parseFromObjectiveLong(o));
		}

		private Object[] parseFromObjectiveLong(Objective e)
		{
			Object[] o=new Object[9];
			if(e!=null){
				try{o[0]=e.getID()!=null ? e.getID() : "";
				}catch(Exception err){
					LOGGER.error("[EDITOR] Can't parse objective::id");
				}
				try{o[1]=e.getModel()!=null ? e.getModel() : "";
				}catch(Exception err){
					LOGGER.error("[EDITOR] Can't parse objective::model");
				}
				try{o[2]=e.getManufacturer()!=null ? e.getManufacturer() : "";
				}catch(Exception err){
					LOGGER.error("[EDITOR] Can't parse objective::manufac");
				}
				try{o[3]=e.getNominalMagnification()!=null ? e.getNominalMagnification().toString() : "";
				}catch(Exception err){
					LOGGER.error("[EDITOR] Can't parse objective::nominalMagn");
				}
				try{o[4]=e.getCalibratedMagnification()!=null ? e.getCalibratedMagnification().toString() : "";
				}catch(Exception err){
					LOGGER.error("[EDITOR] Can't parse objective::calibrationMagn");
				}
				try{o[5]=e.getLensNA()!=null ? e.getLensNA().toString():"";
				}catch(Exception err){
					LOGGER.error("[EDITOR] Can't parse objective::lensNA");
				}
				try{o[6]= e.getImmersion()!=null ? e.getImmersion().getValue():"";
				}catch(Exception err){
					LOGGER.error("[EDITOR] Can't parse objective::immersion");
				}
				try{o[7]=e.getCorrection()!=null ? e.getCorrection().getValue():"";
				}catch(Exception err){
					LOGGER.error("[EDITOR] Can't parse objective::correction");
				}
				try{
					String val="";
					if(e.getWorkingDistance()!=null)
						val=e.getWorkingDistance().value().toString()+e.getWorkingDistance().unit().getSymbol();
					o[8]=val;
				}catch(Exception err){
					LOGGER.error("[EDITOR] Can't parse objective::workingDist");
				}
			}
			return o;
		}

	}

	public Objective getObjective() 
	{ 
		if(selectObjective==null)
			return null;

		return new Objective(selectObjective);
	}

}
