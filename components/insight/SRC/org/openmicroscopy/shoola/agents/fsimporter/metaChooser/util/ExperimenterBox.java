package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ome.xml.model.Experimenter;
/**
* @author Susanne Kunis &nbsp;&nbsp;&nbsp;&nbsp; <a
*         href="mailto:susannekunis@gmail.com">susannekunis@gmail.com</a>
*/
public class ExperimenterBox extends Box implements KeyListener
{
	private JList list;
	private JTextField txtField;
	private boolean userInput;

	private int editRow;

	public ExperimenterBox(int axis) {
		super(axis);
		userInput=false;
	}

	public ExperimenterBox(List<Experimenter> m)
	{
		super(BoxLayout.Y_AXIS);
		editRow=-1;
		userInput=false;
		txtField = new JTextField();
		txtField.addKeyListener(this);
		txtField.setToolTipText("<FirstName LastName> \n Please type enter after input!");

		ExperimenterListModel model=new ExperimenterListModel();
		model.setList(m);
		list=new JList(model);
		list.setVisibleRowCount(4);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e){
				if(SwingUtilities.isLeftMouseButton(e) && e.getClickCount()==2){
					JList list = (JList)e.getSource();
					int row = list.locationToIndex(e.getPoint());
					editElement(row);
					userInput=true;
				}
			}
		});

		list.addKeyListener(this);

		JScrollPane scrollPane = new JScrollPane(list);

		add(txtField);
		add(scrollPane);
	}

	protected void editElement(int row) 
	{
		txtField.setText((String) list.getSelectedValue());
		editRow=list.getSelectedIndex();
	}

	public void addElement(Experimenter e)
	{
		ExperimenterListModel m =(ExperimenterListModel) list.getModel();
		m.addElement(e);
		list.setModel(m);
		System.out.println("ExpBox::addExpElem");
	}

	public void addExperimenterList(List<Experimenter> newList)
	{
		if(newList==null)
			return;

		System.out.println("ExpBox::addExpList...");
		((ExperimenterListModel)list.getModel()).setList(newList);
	}

	public List<Experimenter> getExperimenterList()
	{
		return ((ExperimenterListModel) list.getModel()).getList();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int key=e.getKeyCode();
		userInput=true;
		if(e.getSource()==txtField)
		{
			if(key==KeyEvent.VK_ENTER)
			{ 
				boolean append=true;
				String item = txtField.getText().toLowerCase();

				ExperimenterListModel m=(ExperimenterListModel) list.getModel();
				int elementIndex=m.elementExists(item);
				append=!(elementIndex>-1);

				if(append ){
					Experimenter ex= null;
					String str=txtField.getText();
					if(str!=null && str.length()>0){
						String[] split=str.split("\\s+");
						if(split.length >1){
							ex=new Experimenter();
							ex.setFirstName(split[0]);
							ex.setLastName(split[1]);
						}else{
							return;
						}
					}
					if(editRow==-1){
						addElement(ex);
						txtField.setText("");
						list.setSelectedIndex(list.getModel().getSize()-1);
					}else{
						m.replace(editRow,ex);
						list.setModel(m);
						editRow=-1;
					}
				}else{
					list.setSelectedValue(m.getElementAt(elementIndex), true);
				}
			}//end key enter
		}
		if(e.getSource()==list){
			if(key==KeyEvent.VK_DELETE){
				ExperimenterListModel m=(ExperimenterListModel) list.getModel();
				m.removeExperimenter(list.getSelectedIndex()); 
				list.setModel(m);
			}// end key delete
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setBackground(Color c)
	{
		txtField.setBackground(c);
		list.setBackground(c);
	}

	public boolean valueChanged(){
		return userInput;
	}
}
