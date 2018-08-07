package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JLayer;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.plaf.LayerUI;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules.ElementsCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.microscope.MetaDataUI;
/**
 * @author Susanne Kunis &nbsp;&nbsp;&nbsp;&nbsp; <a
 *         href="mailto:susannekunis@gmail.com">susannekunis@gmail.com</a>
 *
 */
public class MetaDataControl implements PropertyChangeListener
{
	private MetaDataModel model;
	private MetaDataUI view;
	
	public MetaDataControl(MetaDataModel model, MetaDataUI view) 
	{
		if (model == null) throw new NullPointerException("No model.");
		this.model = model;
		this.view=view;
	}



	//TODO: see ImporterControl::propertyChange
	@Override
	public void propertyChange(PropertyChangeEvent evt) 
	{
		 String name = evt.getPropertyName();
//		 if(ElementsCompUI.ADD_NEW_LIGHTSRC.equals(name)){
//			 model.createNewLightSrc((String)evt.getNewValue());
//		 }
		 handlePropertyChangedEvent(evt);
		 
	}

	private void handlePropertyChangedEvent(PropertyChangeEvent evt) 
	{
		String name=evt.getPropertyName();
//		switch(name){
//		case MetaDataUI.CHANGE_IMGDATA:
//			model.setChangesImage((List<TagData>) evt.getNewValue());
//			break;
//		case MetaDataUI.CHANGE_IMGENVDATA:
//			model.setChangesImageEnv((List<TagData>) evt.getNewValue());
//			break;
//		case MetaDataUI.CHANGE_OBJDATA:
//			model.setChangesObject((List<TagData>) evt.getNewValue());
//			break;
//		case MetaDataUI.CHANGE_DETDATA:
//			model.setChangesDetector((List<TagData>) evt.getNewValue());
//			break;
//		case MetaDataUI.CHANGE_LSDATA:
//			model.setChangesLightSrc((List<TagData>) evt.getNewValue());
//			break;
//		case MetaDataUI.CHANGE_SAMPLEDATA:
//			model.setChangesSample((List<TagData>) evt.getNewValue());
//			break;
//		case MetaDataUI.CHANGE_EXPDATA:
//			model.setChangesExperiment((List<TagData>) evt.getNewValue());
//			break;
////		case MetaDataUI.CHANGE_LPGDATA:
////			model.setChangesImage((List<TagData>) evt.getNewValue());
////			break;
//		case MetaDataDialog.CHANGE_CUSTOMSETT:
//			view.resetCustomSettings((CustomViewProperties) evt.getNewValue());
//			default:break;
//		}
		
			
	}


	
	
	
	
	

	
	
	
	
	/**
	 * Create pane labeled tabbedPane with component main on top and sub on bottom
	 * @param main element on top
	 * @param sub element bottom
	 * @param name tabName
	 * @param labelText label beside to tabName
	 * @return
	 */
	protected JPanel createPropPane(ElementsCompUI main, ElementsCompUI sub, String name,String labelText)
	{
		JTabbedPane lTab=new JTabbedPane();
		JPanel lPanel=new JPanel();
		lPanel.setLayout(new BoxLayout(lPanel, BoxLayout.PAGE_AXIS));
		
		lPanel.add(main);
		lPanel.add(sub);
		lTab.add(name,lPanel);
		return buildTabbedPaneWithLabel(lTab,labelText);
	}
	
	public JPanel createPropPane(JComponent main, String name,String labelText)
	{
		JTabbedPane lTab=new JTabbedPane();
		JPanel lPanel=new JPanel();
		lPanel.setLayout(new BoxLayout(lPanel, BoxLayout.PAGE_AXIS));
		
		lPanel.add(main);
		lTab.add(name,lPanel);
		return buildTabbedPaneWithLabel(lTab,labelText);
	}
	
	protected JPanel buildTabbedPaneWithLabel(JTabbedPane tab, String label)
	{
		JPanel panel = new JPanel(new BorderLayout());
		TopRightCornerLabelLayerUI labelUI=new TopRightCornerLabelLayerUI(label);
	    panel.add(new JLayer<JComponent>(tab,labelUI ));
		
		return panel;
	}
	
	class TopRightCornerLabelLayerUI extends LayerUI<JComponent> {
		  private JLabel l = new JLabel("A Label at right corner");
		
		  private JPanel rubberStamp = new JPanel();
		  
		  public TopRightCornerLabelLayerUI(String labelText) 
		  {
			  l.setText(labelText);
		  }
		  @Override public void paint(Graphics g, JComponent c) {
		    super.paint(g, c);
		    Dimension d = l.getPreferredSize();
		    int x = c.getWidth() - d.width - 5;
		    SwingUtilities.paintComponent(g, l, rubberStamp, x, 2, d.width, d.height);
		  }
	}

	

	
	

	

	

	

}
