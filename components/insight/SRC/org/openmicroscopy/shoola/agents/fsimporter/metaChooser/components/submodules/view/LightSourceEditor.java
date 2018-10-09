package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.view;

import java.awt.BorderLayout;
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














import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model.LightSourceModel;
import org.slf4j.LoggerFactory;

import ome.xml.model.Arc;
import ome.xml.model.Filament;
import ome.xml.model.GenericExcitationSource;
import ome.xml.model.Laser;
import ome.xml.model.LightEmittingDiode;
import ome.xml.model.LightSource;
/**
 * Works for xsi:schemaLocation="http://www.openmicroscopy.org/Schemas/OME/2015-01 
 * @author Susanne Kunis &nbsp;&nbsp;&nbsp;&nbsp; <a
 *         href="mailto:susannekunis@gmail.com">susannekunis@gmail.com</a>
 *
 */
public class LightSourceEditor extends JDialog
{
	/** Logger for this class. */
	private static final org.slf4j.Logger LOGGER =
			LoggerFactory.getLogger(LightSourceEditor.class);

	private LightSource lightSrc;

	private List<LightSource> availableLightSrc;

	private JTable lightSrcTable;

	private List<LightSource> hardwareLightSrcList;

	private List<LightSource> imgDataLightSrcList;

	public LightSourceEditor(JFrame parent,String title,
			List<LightSource> _availableLightSrc,List<LightSource> linkHardwareList)
	{
		super(parent,title);
		this.imgDataLightSrcList=_availableLightSrc;
		this.hardwareLightSrcList=linkHardwareList;
		createList();
		JPanel panel=createGUIEditor();
		initGUI(panel);
	}

	private void createList() {
		availableLightSrc=new ArrayList<>();
		if(hardwareLightSrcList!=null && hardwareLightSrcList.size()>0){
			availableLightSrc.addAll(hardwareLightSrcList);
		}
		if(imgDataLightSrcList!=null && imgDataLightSrcList.size()>0){
			availableLightSrc.addAll(imgDataLightSrcList);
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
					int idx=lightSrcTable.getSelectedRow();
					if(idx!=-1){
						lightSrc=availableLightSrc.get(idx);
					}else
						lightSrc=null;
				} catch (Exception e1) {
					LOGGER.error("can't read LIGHTPATH from table");
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
				lightSrc=null;
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

	private JPanel createGUIEditor() 
	{
		JLabel label = new JLabel("Available Elements:");
		label.setFont(new Font("Tahoma", Font.BOLD, 11));

		JScrollPane scrollPane = new JScrollPane();


		lightSrcTable = new JTable(){  
			public boolean isCellEditable(int row,int column){  
				return false;  
			}  };

			scrollPane.setViewportView(lightSrcTable);
			lightSrcTable.setModel(new LightSrcTableModel());
			lightSrcTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

			//fill table
			if(availableLightSrc!=null)
			{
				for(LightSource o: availableLightSrc){
					((LightSrcTableModel)lightSrcTable.getModel()).addRow(o);
				}
			}

			JPanel panel=new JPanel();
			panel.setLayout(new BorderLayout(0, 0));
			panel.setBorder(new EmptyBorder(5,5,5,5));
			panel.add(label,BorderLayout.NORTH);
			panel.add(scrollPane,BorderLayout.CENTER);
			return panel;
	}

	public LightSource getSelectedLightSource()
	{
		LightSource result=null;
		if(lightSrc!=null){
			switch (lightSrc.getClass().getSimpleName()) {
			case LightSourceModel.LASER:
				result=new Laser((Laser) lightSrc);
				break;
			case LightSourceModel.ARC:
				result=new Arc((Arc) lightSrc);
				break;
			case LightSourceModel.FILAMENT:
				result=new Filament((Filament) lightSrc);
				break;
			case LightSourceModel.GENERIC_EXCITATION:
				result=new GenericExcitationSource((GenericExcitationSource) lightSrc);
				break;
			case LightSourceModel.LIGHT_EMITTING_DIODE:
				result=new LightEmittingDiode((LightEmittingDiode) lightSrc);
				break;
			default:
				break;
			}
		}
		return result;
	}



	class LightSrcTableModel extends DefaultTableModel
	{
		Class[] columnTypes = new Class[] {
				String.class,String.class, String.class, String.class, String.class, String.class};

		public LightSrcTableModel()
		{
			super(new Object[][] {},
					new String[] {"ID","Model", "Type","Wavelength","Power","Repititation Rate"});
		}

		public Class getColumnClass(int columnIndex) {
			return columnTypes[columnIndex];
		}

		public void addRow(LightSource o)
		{
			super.addRow(parseFromLightSrc(o));
		}

		private Object[] parseFromLightSrc(LightSource l) 
		{
			Object[] o=new Object[6];
			if(l!=null){
				o[0]=l.getID()!=null ? l.getID():"";
				o[1]=l.getModel()!=null ? l.getModel() : "";
				o[2]=l.getClass().getName();
				if(l instanceof Laser){
					o[3]=((Laser)l).getWavelength()!=null ? 
							((Laser)l).getWavelength().value()+((Laser)l).getWavelength().unit().getSymbol() : "";
							o[4]=((Laser)l).getPower()!=null ? 
									((Laser)l).getPower().value()+((Laser)l).getPower().unit().getSymbol() : ""; 
									o[5]=((Laser)l).getRepetitionRate()!=null ? 
											((Laser)l).getRepetitionRate().value()+((Laser)l).getRepetitionRate().unit().getSymbol() : ""; 
				}else if(l instanceof Arc){
					o[3]="";
					o[4]=((Arc)l).getPower()!=null ? 
							((Arc)l).getPower().value()+((Arc)l).getPower().unit().getSymbol() : ""; 
							o[5]="";
				}	else if(l instanceof Filament){
					o[3]="";
					o[4]=((Filament)l).getPower()!=null ? 
							((Filament)l).getPower().value()+((Filament)l).getPower().unit().getSymbol() : ""; 
							o[5]="";
				}else if(l instanceof GenericExcitationSource){
					o[3]="";
					o[4]=((GenericExcitationSource)l).getPower()!=null ? 
							((GenericExcitationSource)l).getPower().value()+((GenericExcitationSource)l).getPower().unit().getSymbol() : ""; 
							o[5]="";
				}
				else if(l instanceof LightEmittingDiode){
					o[3]="";
					o[4]="";
					o[5]="";
				}
			}
			return o;
		}
	}
}
