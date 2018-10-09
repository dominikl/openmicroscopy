package org.openmicroscopy.shoola.agents.metadata.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.openmicroscopy.shoola.agents.metadata.MetadataViewerAgent;
import org.openmicroscopy.shoola.agents.metadata.editor.AnnotationTaskPaneUI.Filter;
import org.openmicroscopy.shoola.agents.metadata.editor.EditorModel.MapAnnotationType;
import org.openmicroscopy.shoola.agents.metadata.editor.maptable.MapTable;
import org.openmicroscopy.shoola.agents.metadata.editor.maptable.MapTableModel;
import org.openmicroscopy.shoola.agents.metadata.editor.maptabletabbed.MapTableTabbed;
import org.openmicroscopy.shoola.agents.util.EditorUtil;
import org.openmicroscopy.shoola.util.CommonsLangUtils;
import org.openmicroscopy.shoola.util.ui.UIUtilities;

import omero.gateway.model.AnnotationData;
import omero.gateway.model.MapAnnotationData;
import omero.model.NamedValue;

public class MetaDataTaskPaneUI extends AnnotationTaskPaneUI implements
ListSelectionListener
{

	/** Maximum width of the tables component before scrollbars are used */
	private static final int MAX_TABLES_COMPONENT_WIDTH = 200;

	/** Maximum height of the tables component before scrollbars are used */
	private static final int MAX_TABLES_COMPONENT_HEIGHT = 300;
	/** The layout constraints */
	private GridBagConstraints c;
	/** Component hosting the tables */
	private JPanel tablePanel=null;
	/** Scrollpane hosting the tables component */
	private JScrollPane sp;
	 /** Reference to the {@link MapTableTabbed} of UOS.import */
	private MapTableTabbed mapTabbedTable;

	MetaDataTaskPaneUI(EditorModel model, EditorUI view, EditorControl controller) {
		super(model, view, controller);
		buildUI();
	}

	private void buildUI() 
	{
		setLayout(new BorderLayout());
		setBackground(UIUtilities.BACKGROUND_COLOR);

		c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTHWEST;
		c.insets = new Insets(0, 2, 4, 2);
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 0;
		c.fill = GridBagConstraints.HORIZONTAL;

		tablePanel = new JPanel();
		tablePanel.setLayout(new GridBagLayout());
		tablePanel.setBackground(UIUtilities.BACKGROUND_COLOR);
		sp = new JScrollPane(tablePanel);
		sp.setBorder(BorderFactory.createEmptyBorder());
		tablePanel.addComponentListener(new ComponentListener() {
			@Override
			public void componentResized(ComponentEvent e) {
				if (view != null)
					adjustScrollPane();
			}

			@Override
			public void componentShown(ComponentEvent e) {
			}

			@Override
			public void componentMoved(ComponentEvent e) {
			}

			@Override
			public void componentHidden(ComponentEvent e) {
			}
		});
		add(sp, BorderLayout.CENTER);
		sp.setPreferredSize(null);

	}

	/**
	 * Adjusts the size of the scrollpane hosting the tables
	 */
	private void adjustScrollPane() {
		tablePanel.setPreferredSize(null);
		Dimension d = tablePanel.getPreferredSize();
		if (d.width > MAX_TABLES_COMPONENT_WIDTH)
			d.width = MAX_TABLES_COMPONENT_WIDTH;
		if (d.height > MAX_TABLES_COMPONENT_HEIGHT)
			d.height = MAX_TABLES_COMPONENT_HEIGHT;
		d.width += 5;
		d.height += 5;
		sp.setPreferredSize(d);
		view.revalidate();
	}



	@Override
	public void valueChanged(ListSelectionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	void refreshUI() 
	{
		List<MapAnnotationData> list = new ArrayList<MapAnnotationData>();

		if (filter == Filter.SHOW_ALL || filter == Filter.ADDED_BY_ME) {
			list.addAll(model.getMapAnnotations(MapAnnotationType.USER));
			if (list.isEmpty()) {
				MapAnnotationData newMA = new MapAnnotationData();
				newMA.setNameSpace(MapAnnotationData.NS_CLIENT_CREATED);
				list.add(newMA);
			}
		}

		if (filter == Filter.SHOW_ALL || filter == Filter.ADDED_BY_OTHERS) {
			list.addAll(model.getMapAnnotations(MapAnnotationType.OTHER_USERS));
		}

		list.addAll(model.getMapAnnotations(MapAnnotationType.OTHER));	
		for (MapAnnotationData ma : list) 
		{
			// metadata UOS.import annotation?
			if(!CommonsLangUtils.isEmpty(ma.getNameSpace()) && ma.getNameSpace().contains("UOS.importer")){
				System.out.println("#MapTaskPaneUI::refreshUI(): nameSpace Annotation: "+ma.getNameSpace());
				if(mapTabbedTable==null){

					String title = ma.getOwner() != null && !isUsers(ma) ? "Added by: "+ EditorUtil.formatExperimenter(ma.getOwner()): "";

					JPanel p = new JPanel();
					p.setBackground(UIUtilities.BACKGROUND_COLOR);
					UIUtilities.setBoldTitledBorder(title, p);
					p.setLayout(new BorderLayout());
					createTabbedPane(ma);

					if (mapTabbedTable != null) {
						p.add(mapTabbedTable, BorderLayout.CENTER);

//						// if the MapAnnotation has a custom namespace, display it
//						if (!CommonsLangUtils.isEmpty(ma.getNameSpace())
//								&& !MapAnnotationData.NS_CLIENT_CREATED.matches(ma
//										.getNameSpace())) {
//							JLabel ns = new JLabel(UIUtilities.formatPartialName(ma
//									.getNameSpace()));
//							ns.setFont(ns.getFont().deriveFont(Font.BOLD));
//							p.add(ns, BorderLayout.NORTH);
//						}
						System.out.println("#MapTaskPaneUI::refreshUI(): addTabbedTable");
						tablePanel.add(p, c);
						c.gridy++;
					}
				}
			}
		}
		setVisible(mapTabbedTable!=null);
		adjustScrollPane();
	}

	/**
	 * Creates a {@link MapTabbedTable} and adds it to the list of mapTabbedTables; Returns
	 * <code>null</code> if the {@link MapAnnotationData} is empty and not
	 * editable!
	 * 
	 * @param m
	 *            The data to show
	 * @return See above
	 */
	private void createTabbedPane(MapAnnotationData m) 
	{
		boolean editable = (isUsers(m) && (model.canAnnotate()))
				|| (model.canEdit(m) && MapAnnotationData.NS_CLIENT_CREATED
						.equals(m.getNameSpace()));

		if (!editable
				&& (m.getContent() == null || ((List<NamedValue>) m
						.getContent()).isEmpty())){
			mapTabbedTable=null;
			return;
		}
		int permissions = editable ? MapTable.PERMISSION_DELETE
				| MapTable.PERMISSION_MOVE | MapTable.PERMISSION_EDIT
				: MapTable.PERMISSION_NONE;

		final MapTableTabbed t = new MapTableTabbed(permissions);
		t.setSelectionModels(this);
		t.setData(m);

		t.setTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				MapTableModel m = (MapTableModel) t.getModel();
				if (m.isEmpty() && m.getMap().getId() >= 0) {
					view.deleteAnnotation(m.getMap());
					view.saveData(true);
				}
				adjustScrollPane();
			}
		});

		mapTabbedTable = t;
	}   

	/**
	 * Check if the given {@link MapAnnotationData} is the user's own annotation
	 * 
	 * @param data
	 *            The {@link MapAnnotationData}
	 * @return See above
	 */
	private boolean isUsers(MapAnnotationData data) {
		return MapAnnotationData.NS_CLIENT_CREATED.equals(data.getNameSpace())
				&& (data.getOwner() == null || MetadataViewerAgent
				.getUserDetails().getId() == data.getOwner().getId());
	}

	@Override
	void clearDisplay() {
		mapTabbedTable=null;
		tablePanel.removeAll();
		c.gridy = 0;
	}

	@Override
	List<AnnotationData> getAnnotationsToSave() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	List<Object> getAnnotationsToRemove() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	void onRelatedNodesSet() {
		clearDisplay();

	}

	@Override
	int getUnfilteredAnnotationCount() {
		// TODO Auto-generated method stub
		return 0;
	}

}
