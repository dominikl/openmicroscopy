package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javafx.embed.swing.JFXPanel;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;

import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.text.JTextComponent;

import loci.common.DateTools;
import ome.units.UNITS;
import ome.units.unit.Unit;
import ome.xml.model.Experimenter;
import ome.xml.model.primitives.Timestamp;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.time.DateUtils;
import org.joda.time.format.DateTimeFormat;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.ScrollablePanel;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.UOSMetadataLogger;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.microscope.MetaDataUI;
import org.openmicroscopy.shoola.util.ui.UIUtilities;
import org.slf4j.LoggerFactory;

public class TagData 
{
	/** Logger for this class. */
//	private static Logger LOGGER = Logger.getLogger(UOSMetadataLogger.class.getName());
	private static final org.slf4j.Logger LOGGER =
    	    LoggerFactory.getLogger(TagData.class);

	Color fillInfo=new Color(240,240,240);//Color.LIGHT_GRAY;
//	Color noInfo=new Color(217,229,220);//green;
	Color noInfo=Color.white;
	Color resetInfo=Color.white;
	
	public static final String[] DATE_FORMATS_TAGS = {
	    "yyyy:MM:dd HH:mm:ss",
	    "dd/MM/yyyy HH:mm:ss",
	    "MM/dd/yyyy hh:mm:ss aa",
	    "yyyyMMdd HH:mm:ss",
	    
	    "yyyy/MM/dd",
	    "yyyy/MM/dd HH:mm:ss",
	    
	    "yyyy-MM-dd HH:mm:ss",
	    "yyyy-MM-dd HH:mm:ss:SSS",
	    "yyyy-MM-dd'T'HH:mm:ssZ",
	    "yyyy-MM-dd",
	    
	    "dd.MM.yyyy",
	    "dd.MM.yyyy HH:mm:ss",
	    "dd.MM.yyyy HH:mm:ss:SSS",
	    
	    "dd-MM-yyyy HH:mm:ss",
	    "dd-MM-yyyy"
	   
	  };
	
	private static final Map<String, String> DATE_FORMAT_REGEXPS = new HashMap<String, String>() {{
	    put("^\\d{8}$", "yyyyMMdd");
	    put("^\\d{1,2}-\\d{1,2}-\\d{4}$", "dd-MM-yyyy");
	    put("^\\d{1,2}.\\d{1,2}.\\d{4}$", "dd.MM.yyyy");
	    put("^\\d{4}-\\d{1,2}-\\d{1,2}$", "yyyy-MM-dd");
	    put("^\\d{1,2}/\\d{1,2}/\\d{4}$", "MM/dd/yyyy");
	    put("^\\d{4}/\\d{1,2}/\\d{1,2}$", "yyyy/MM/dd");
	    put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}$", "dd MMM yyyy");
	    put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}$", "dd MMMM yyyy");
	    put("^\\d{12}$", "yyyyMMddHHmm");
	    put("^\\d{8}\\s\\d{4}$", "yyyyMMdd HHmm");
	    put("^\\d{1,2}-\\d{1,2}-\\d{4}\\s\\d{1,2}:\\d{2}$", "dd-MM-yyyy HH:mm");
	    put("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{2}$", "yyyy-MM-dd HH:mm");
	    put("^\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{2}$", "MM/dd/yyyy HH:mm");
	    put("^\\d{4}/\\d{1,2}/\\d{1,2}\\s\\d{1,2}:\\d{2}$", "yyyy/MM/dd HH:mm");
	    put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}\\s\\d{1,2}:\\d{2}$", "dd MMM yyyy HH:mm");
	    put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}\\s\\d{1,2}:\\d{2}$", "dd MMMM yyyy HH:mm");
	    put("^\\d{14}$", "yyyyMMddHHmmss");
	    put("^\\d{8}\\s\\d{6}$", "yyyyMMdd HHmmss");
	    put("^\\d{1,2}-\\d{1,2}-\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd-MM-yyyy HH:mm:ss");
	    put("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}$", "yyyy-MM-dd HH:mm:ss");
	    put("^\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "MM/dd/yyyy HH:mm:ss");
	    put("^\\d{4}/\\d{1,2}/\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}$", "yyyy/MM/dd HH:mm:ss");
	    put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd MMM yyyy HH:mm:ss");
	    put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd MMMM yyyy HH:mm:ss");
	    put("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}:\\d{3}$", "yyyy-MM-dd HH:mm:ss:SSS");
	}};
	
	private final String datePattern = DateTools.TIMESTAMP_FORMAT;

	//status of inputfield
	public static final int INACTIVE=0;
	public static final int EMPTY =1;
	public static final int SET=2;
	public static final int OVERWRITE=3;

	// kind of inputfields
	public static final int TEXTFIELD=0;
	public static final int COMBOBOX=1;
	public static final int TEXTPANE=2;
	public static final int CHECKBOX=3;
	public static final int ARRAYFIELDS=4;
	public static final int TIMESTAMP=5;
	public static final int LIST=6;
	public static final int TEXTAREA=7;

	private JLabel label;
	private JComponent inputField;
	private Unit unit;
	private int status=INACTIVE;
	private boolean prop;
	private int type;
	private boolean visible;

	private boolean valChanged;

	private String name;
	private Boolean markedToStore;

	private KeyListener fieldKeyListener;
	private ActionListener fieldActionListener;

	/**
	 * Constructor for TagData element for array fields
	 * @param name label for tagdata element
	 * @param val array of values
	 * @param prop property
	 * @param type type==ARRAYFILEDS
	 */
	public TagData(String name, String[] val, boolean prop,int type) 
	{
		if(val==null)
			val=new String[1];
		initListener();
		this.markedToStore=false;
		this.type=type;
		this.name=name;
		label = new JLabel(name+":");
		int size=val!=null ? val.length : 1;
		switch (type) {
		case ARRAYFIELDS:
			initArrayTextField(size);
			break;
		default:
			initTextField();
			break;
		}

		label.setLabelFor(inputField);
		setTagValue(val);
		setTagProp(prop);
		visible=false;
	}
	
	public TagData(String name, String[] val, Unit unit, boolean prop,
			int type) {
		if(val==null)
			val=new String[1];
		initListener();
		this.unit=unit;
		this.markedToStore=false;
		this.type=type;
		this.name=name;
		label = new JLabel(name+" ["+unit.getSymbol()+"]:");
		int size=val!=null ? val.length : 1;
		switch (type) {
		case ARRAYFIELDS:
			initArrayTextField(size);
			break;
		default:
			initTextField();
			break;
		}

		label.setLabelFor(inputField);
		setTagValue(val);
		setTagProp(prop);
		visible=false;
	}
	/**
	 * Constructor for TagData element for list fields
	 * @param name
	 * @param model list model
	 * @param prop
	 * @param type
	 */
	public TagData(String name, List<Experimenter> expList, boolean prop, int type)
	{
		initListener();
		this.markedToStore=false;
		this.type=type;
		this.name=name;
		label = new JLabel(name+":");
		
		switch (type) {
		case LIST:
			initListField(expList);
			break;
		default:
			initTextField();
			break;
		}

		label.setLabelFor(inputField);
		setTagProp(prop);
		visible=false;
	}

	public TagData(String name, String val, boolean prop,int type) 
	{
		this(name,val,prop,type,null);
	}
	
	public TagData(String name, String val,Unit unit, boolean prop,int type) 
	{
		this(name,val,prop,type,null);
		this.unit=unit;
		label= new JLabel(name+" ["+unit.getSymbol()+"]:");
		label.setLabelFor(inputField);
	}

	public TagData(String name, String val, boolean prop,int type, String[] defaultVal) 
	{
		initListener();
		this.markedToStore=false;
		this.type=type;
		this.name=name;
		label = new JLabel(name+":");
		switch (type) {
		case TEXTFIELD:
			initTextField();
			break;
		case COMBOBOX:
			initComboBox(defaultVal);
			break;
		case TEXTPANE:
			initTextPane();
			break;
		case TEXTAREA:
			initTextArea();
			break;
		case CHECKBOX:
			initCheckBox(val);
			break;
		case ARRAYFIELDS:
			initArrayTextField(1);
			break;
		case TIMESTAMP:
			initTimeStampField();
			break;
		default:
			initTextField();
			break;
		}

		label.setLabelFor(inputField);
		setTagValue(val);
		setTagProp(prop);
		visible=false;
	}

	private void initTimeStampField() 
	{
//		inputField = Box.createHorizontalBox();
//		
////		GridPane dateGrid=new GridPane();
//		DatePicker dPicker=new DatePicker();
//		MyStringConverter converter = new MyStringConverter();
//		dPicker.setConverter(converter);
//		dPicker.setPromptText(datePattern.toLowerCase());
//		JFXPanel fxPane=new JFXPanel();
////		dateGrid.add(dPicker,0,0);
//		fxPane.add(dPicker);
//		inputField.add(fxPane);
		
		
		inputField = new JTextField(10);
		inputField.setToolTipText("Format e.g: "+datePattern+" or dd.MM.yyyy");
		inputField.addKeyListener(fieldKeyListener);

	}
	
	private void initListField(List<Experimenter> expList)
	{
		inputField = new ExperimenterBox(expList);
		inputField.addKeyListener(fieldKeyListener);
	}

	private void initTextField()
	{
		inputField = new JTextField(10);
		inputField.addKeyListener(fieldKeyListener);
	}

	private void initTextPane()
	{
		inputField = new JTextPane();
		inputField.addKeyListener(fieldKeyListener);
	}
	
	private void initTextArea()
	{
		inputField = new ScrollableTextPane();
	}

	private void initCheckBox(String val)
	{
		inputField = new JCheckBox("",Boolean.parseBoolean(val));
		((JCheckBox) inputField).addActionListener(fieldActionListener);
	}

	private void initArrayTextField(int size)
	{
		inputField = Box.createHorizontalBox();
		for(int i=0; i<size; i++){
			JTextField txtF=new JTextField();
			txtF.addKeyListener(fieldKeyListener);
			((Box)inputField).add(txtF);
		}
	}

	private void initComboBox(String[] defaultVal)
	{
		if(defaultVal!=null){
			inputField = new JComboBox<String>(defaultVal);
			((JComboBox<String>) inputField).insertItemAt("",0);
			((JComboBox<String>) inputField).setSelectedItem(0);
		}else{
			inputField = new JComboBox<String>();
		}
		
//		 UIManager.put("ComboBox.background", new ColorUIResource(noInfo));
//		  UIManager.put("JTextField.background", new ColorUIResource(noInfo));
//		  ((JComboBox<String>) inputField).getEditor().getEditorComponent().setBackground(noInfo);
//		((JTextField) ((JComboBox<String>) inputField).getEditor().getEditorComponent()).setBackground(noInfo);
//		Color[] colors={noInfo};
//		DefaultComboBoxModel model = new DefaultComboBoxModel(colors);
//		((JComboBox<String>) inputField).setModel(model);
//		((JComboBox<String>) inputField).setRenderer(new CBoxRenderer());
		
		((JComboBox<String>) inputField).addActionListener(fieldActionListener);
		//			((JComboBox<String>) inputField).addActionListener(new ActionListener(){
		//				public void actionPerformed(ActionEvent evt) 
		//				{
		////					int commandId = Integer.parseInt(evt.getActionCommand());
		////					switch (commandId) {
		////					case SET_CB_VAL:
		////						JComboBox<String> cb = (JComboBox<String>)evt.getSource();
		////				        String chName = (String)cb.getSelectedItem();
		////				         int ch=cb.getSelectedIndex();
		////					}
		//				}
		//			});
	}

	private void initListener()
	{
		fieldKeyListener=new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
			}
			@Override
			public void keyReleased(KeyEvent e) {
				valChanged=true;	
				if(inputField instanceof JTextField){
					if(inputField.getForeground()==Color.gray ){
						if(	!((JTextField) inputField).getText().equals(datePattern)){
							inputField.setForeground(Color.black);
						}
					}
					
				}
			}
			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
			}
		};

		fieldActionListener=new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				valChanged=true;
			}
		};


	}

	/** Action Listener for tagData*/
	public void addActionListener(ActionListener l)
	{
		switch (type) {
		case TEXTFIELD:
			((JTextField)inputField).addActionListener(l);
			break;
		case COMBOBOX:
			((JComboBox)inputField).addActionListener(l); 
			break;
		case TEXTPANE:

			break;
		case TEXTAREA:
			break;
		case CHECKBOX:
			((JCheckBox)inputField).addActionListener(l);
			break;
		case ARRAYFIELDS:

			break;
		case TIMESTAMP:
			break;
		default:
			((JTextField)inputField).addActionListener(l);
			break;
		}
	}

	public void setDefaultValues(String[] list)
	{
		switch (type) {
		case COMBOBOX:
			DefaultComboBoxModel<String>model=(DefaultComboBoxModel<String>) ((JComboBox<String>) inputField).getModel();
			model.removeAllElements();
			model.addElement("");
			for(int c=0; c<list.length; c++){
				model.addElement(list[c]);
			}
			((JComboBox<String>) inputField).setModel(model);
			break;

		default:
			break;
		}

	}

	public JComponent getInputField()
	{
		return inputField;
	}
	
	public JLabel getTagLabel(){
		return label;
	}
	
	public String getTagName(){
		return this.name;
	}
	
	public String getTagValue() 
	{
		String val="";
		switch (type) {
		case TIMESTAMP:
			val = readTimestamp(val);
			break;
		case TEXTPANE:
			val=((JTextPane) inputField).getText();
			break;
		case TEXTAREA:
			val=((ScrollableTextPane)inputField).getText();
			break;
		case TEXTFIELD:
			val=((JTextField) inputField).getText();
			break;
		case COMBOBOX:
			val=(String) ((JComboBox<String>) inputField).getSelectedItem();
			break;
		case CHECKBOX:
			val= ((JCheckBox)inputField).isSelected()? "true" : "false";
			break;
		default:
			break;
		}
		return val!=null? val : "";
	}
	
	public List<Experimenter> getListValues()
	{
		if(type == LIST){
			
			List<Experimenter> list = ((ExperimenterBox) inputField).getExperimenterList();
			
			return list;
		}
		
		return null;
	}

	

	public String getTagValue(int index) 
	{
		String val="";
		switch (type) {
		case ARRAYFIELDS:
			Component comp=inputField.getComponent(index);
			if(comp instanceof JTextField){
				val=((JTextField) comp).getText();
			}else{
				ExceptionDialog ld = new ExceptionDialog("Tag Data Parse Error!", 
						"Can't parse tag value of "+label.getText());
				ld.setVisible(true);
				LOGGER.error("can't get value at "+index+" ");
			}

		default:
			getTagValue();
			break;
		}
		return val!=null? val : "";
	}

	public Unit getTagUnit()
	{
		return unit;
	}

	public void setTagUnit(Unit u)
	{
		unit=u;
	}
	
	public void setTagValue(Experimenter val)
	{
		if(val==null || val.equals("")){
			inputField.setBackground(noInfo);
		}else{
			inputField.setBackground(fillInfo);
		}
		if(type==LIST){
			((ExperimenterBox) inputField).addElement(val);
		}
	}
	
	public void setTagValue(List<Experimenter> val)
	{
		if(val==null || val.isEmpty()){
			inputField.setBackground(noInfo);
		}else{
			inputField.setBackground(fillInfo);
		}
		if(type==LIST){
			((ExperimenterBox) inputField).addExperimenterList(val);
		}
	}
	
	public void setTagValue(String val,Unit unit, boolean property)
	{
		if(this.unit!=unit){
			String unitSymbol=unit.equals(UNITS.REFERENCEFRAME)? "rf" : unit.getSymbol();
			label.setText(this.name+" ["+unitSymbol+"]:");
			this.unit=unit;
		}
		setTagValue(val);
		setTagProp(property);
		valChanged=false;
	}

	public void setTagValue(String val, boolean property)
	{
		setTagValue(val);
		setTagProp(property);
		valChanged=false;
	}

	public void setTagValue(String val, int index, boolean property)
	{
		setTagValue(val,index);
		setTagProp(property);
		valChanged=false;
	}

	public void setTagValue(String[] val, boolean property)
	{
		if(val== null)
			val=new String[1];
		setTagValue(val);
		setTagProp(property);
		valChanged=false;
	}

	private void setTagValue(String val, int index)
	{
		switch (type) {
		case ARRAYFIELDS:
			// split string
			setValArrayField(val, index);
			break;
		default:
			setTagValue(val);
			break;
		}
		setTagStatus( val.equals("") ? EMPTY : (status==EMPTY ? SET : OVERWRITE));
	}

	private void setTagValue(String[] val) 
	{

		switch (type) {
		case ARRAYFIELDS:
			// split string 
			for(int i=0; i<val.length; i++){
				setValArrayField(val[i], i);
			}
		default:
			break;
		}
		setTagStatus( (val.length==0) ? EMPTY : (status==EMPTY ? SET : OVERWRITE));
	}

	public void setTagValue(String val) 
	{
		if(val==null || val.equals("")){
			val="";
			inputField.setBackground(noInfo);
		}else{
			inputField.setBackground(fillInfo);
		}
		switch (type) {
		case TEXTFIELD:
			setValTextField(val);
			break;
		case COMBOBOX:
			setValComboBox(val);
			break;
		case TEXTPANE:
			setValTextPane(val);
			break;
		case TEXTAREA:
			setValTextArea(val);
			break;
		case CHECKBOX:
			setValCheckbox(val);
			break;
		case ARRAYFIELDS:
			setValArrayField(val);
			break;
		case TIMESTAMP:
			setValTimestamp(val);
			break;
		default:
			break;
		}
		setTagStatus( val.equals("") ? EMPTY : (status==EMPTY ? SET : OVERWRITE));
		valChanged=false;
	}
	
	private String readTimestamp(String val) 
	{
		String creationDate = ((JTextField)inputField).getText();
		try{
			// parse to yyyy-MM-ddT00:00:00
			String date = DateTools.formatDate(creationDate, DATE_FORMATS_TAGS);
			
			//parsing successfull?
			if(creationDate!= null && !creationDate.equals("") && !creationDate.equals(datePattern) && date ==null){
				date = parseDate(creationDate);
				
				
				// show warn dialog
				if(date==null){
					String formats="";
					for(String s: DATE_FORMATS_TAGS){
						formats=formats+s+"\n";
					}
					LOGGER.warn("unknown creation date format: {}", creationDate);
					WarningDialog ld = new WarningDialog("Unknown Timestamp Format!", 
							"Can't parse given timestamp ["+label.getText()+": "+creationDate+"] ! Please use one of the following date formats:\n"+formats);
					ld.setVisible(true);
				}
			}
			
			val=date;//DateTools.formatDate(((JTextField)inputField).getText(), DateTools.TIMESTAMP_FORMAT);
		}catch(Exception e){
			LOGGER.error("Wrong string input format timestamp: "+label.getText()+": "+creationDate);
			ExceptionDialog ld = new ExceptionDialog("Timestamp Format Error!", 
					"Wrong timestamp format at input at "+label.getText(),e);
			ld.setVisible(true);
		}
		return val;
	}
	
	//http://stackoverflow.com/questions/3389348/parse-any-date-in-java
	/**
	 * Determine SimpleDateFormat pattern matching with the given date string. Returns null if
	 * format is unknown. You can simply extend DateUtil with more formats if needed.
	 * @param dateString The date string to determine the SimpleDateFormat pattern for.
	 * @return The matching SimpleDateFormat pattern, or null if format is unknown.
	 * @see SimpleDateFormat
	 */
	public static String determineDateFormat(String dateString) {
	    for (String regexp : DATE_FORMAT_REGEXPS.keySet()) {
	        if (dateString.toLowerCase().matches(regexp)) {
	            return DATE_FORMAT_REGEXPS.get(regexp);
	        }
	    }
	    LOGGER.warn("Can't parse date: "+dateString+". Unknown date format!");
	   System.out.println("Can't parse date: "+dateString+". Unknown date format!");
	    return null; // Unknown format.
	}
	

	private String parseDate(String val) throws Exception
	{
		String dateformat= DateTools.ISO8601_FORMAT_MS;
		String s=DateTools.formatDate(val,dateformat);
		if(s==null){
			dateformat=DateTools.ISO8601_FORMAT;
			s=DateTools.formatDate(val, dateformat);
			
		}
		DateTimeFormatter formatter=DateTimeFormatter.ofPattern(dateformat);
		DateFormat df=new SimpleDateFormat(dateformat);
		
		Date d=null;
//		try {
			d=df.parse(s);
			SimpleDateFormat f=new SimpleDateFormat(DateTools.TIMESTAMP_FORMAT);
			return f.format(d);
//		} catch (ParseException | NullPointerException e1) {
//			// TODO Auto-generated catch block
//			LOGGER.error("Parse error date for format "+dateformat+"\n"+e1.toString());
//			return null;
//		}
	}

	private void setValTimestamp(String val) 
	{
//		if(val==null || val.equals("")){
//			((JTextField) inputField).setText(datePattern.toLowerCase());
//			((JTextField) inputField).setForeground(Color.gray);
//		}else{
		if(val!=null && !val.equals("")){
			((JTextField) inputField).setForeground(Color.black);
			String dateformat= DateTools.ISO8601_FORMAT_MS;
			String s=DateTools.formatDate(val,dateformat);
			if(s==null){
				dateformat=DateTools.ISO8601_FORMAT;
				s=DateTools.formatDate(val, dateformat);
				
			}
			DateTimeFormatter formatter=DateTimeFormatter.ofPattern(dateformat);
			DateFormat df=new SimpleDateFormat(dateformat);
			
			Date d=null;
			try {
				d=df.parse(s);
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				LOGGER.error("Parse error date for format "+dateformat);
				ExceptionDialog ld = new ExceptionDialog("Timestamp Format Error!", 
						"Wrong timestamp format at input at "+label.getText(),e1);
				ld.setVisible(true);
			}
			
			try {
				//			((JTextField) inputField).setText( d.toString());
				SimpleDateFormat f=new SimpleDateFormat(DateTools.TIMESTAMP_FORMAT);
				((JTextField) inputField).setText( f.format(d));
			} catch (Exception e) {
				LOGGER.error("Parse error for timestamp");
				ExceptionDialog ld = new ExceptionDialog("Timestamp Format Error!", 
						"Wrong timestamp format at input at "+label.getText(),e);
				ld.setVisible(true);
			}
			
		}
				
	}

	private void setValArrayField(String val) 
	{
		if(val.equals("")){
			for(Component c :inputField.getComponents()){
				((JTextField) c).setText("");
				c.setBackground(noInfo);
			}
		}else{
			// split string 
			String[] components=val.split("x");
			if(components.length!= inputField.getComponentCount()){
				LOGGER.error("Wrong input for "+getTagLabel());
			}
			for(int i=0; i<components.length;i++)
			{
				setValArrayField(components[i], i);
			}
		}
	}
	private void setValArrayField(String s,int i)
	{
		JTextField txtF=(JTextField) inputField.getComponent(i);
		txtF.setText(s);
		if(s==null || s.equals("")){
			s="";
			txtF.setBackground(noInfo);
		}else{
			txtF.setBackground(fillInfo);
		}
	}

	private void setValCheckbox(String val) 
	{
		boolean bVal=BooleanUtils.toBoolean(val);
		((JCheckBox) inputField).setSelected(bVal);
	}

	private void setValTextPane(String val) {
		((JTextPane) inputField).setText(val);
	}
	
	private void setValTextArea(String val){
		((ScrollableTextPane)inputField).setText(val);
	}

	private void setValComboBox(String val) {
		for(int c=0; c< ((JComboBox<String>) inputField).getItemCount(); c++)
		{
			if(((JComboBox<String>) inputField).getItemAt(c).equals(val)){
				((JComboBox<String>) inputField).setSelectedIndex(c);
//				  UIManager.put("ComboBox.background", new ColorUIResource(fillInfo));
//				  UIManager.put("JTextField.background", new ColorUIResource(fillInfo));
//				((JTextField) ((JComboBox<String>) inputField).getEditor().getEditorComponent()).setBackground(fillInfo); 
			}
		}
	}

	private void setValTextField(String val) {
		((JTextField) inputField).setText(val);
	}




	public int getTagStatus() {
		return status;
	}
	private void setTagStatus(int status) {
		this.status = status;
	}
	public boolean getTagProp() {
		return prop;
	}
	public void setTagProp(boolean prop) {
		this.prop = prop;
	}

	public void setEnable(boolean val)
	{
		label.setEnabled(val);
		inputField.setEnabled(val);
		if(!val)
			status=INACTIVE;
	}

	public void setMarkedToStore(boolean b)
	{
		markedToStore=b;
	}

	public boolean isMarkedToStore()
	{
		return markedToStore;
	}

	public boolean valueChanged()
	{
		if(type==LIST)
			return ((ExperimenterBox)inputField).valueChanged();
		
		return valChanged;
	}
	
	public void changeIsUpdated(boolean b){
		valChanged=b;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	class MyStringConverter extends StringConverter<LocalDate>
	{
//		
//		 String creationDate = getImageCreationDate();
//		    String date = DateTools.formatDate(creationDate, DATE_FORMATS, ".");
//		    if (creationDate != null && date == null) {
//		      LOGGER.warn("unknown creation date format: {}", creationDate);
//		    }
//		    creationDate = date;
		DateTimeFormatter dateFormatter = 
                DateTimeFormatter.ofPattern(datePattern);
            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }
            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }	
	}
	
	class ScrollableTextPane extends JScrollPane
	{
		JTextArea area;
		
		public ScrollableTextPane()
		{
			area=new JTextArea();
			area.setRows(2);
			area.setLineWrap(true);
			area.setWrapStyleWord(true);
			area.addKeyListener(fieldKeyListener);
			setViewportView(area);
			
		}
		
		public void setText(String val)
		{
			area.setText(val);
		}
		
		public String getText()
		{
			return area.getText();
		}

		@Override
		public void setBackground(Color bg) {
			// TODO Auto-generated method stub
			super.setBackground(bg);
			if(area!=null)
				area.setBackground(bg);
		}
		

	}
	
//	class CBoxRenderer extends JButton implements ListCellRenderer
//	{
//		boolean b=false;
//		
//		public CBoxRenderer() {  
//			setOpaque(true); 
//		}
//	
//
//		@Override
//		public void setBackground(Color bg) {
//			// TODO Auto-generated method stub
//			if(!b)
//				return;
//
//			super.setBackground(bg);
//		}
//
//		 @Override
//		 public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)  
//		 {  
//		     b=true;
//		     setBackground((Color)value);        
//		     b=false;
//		     return this;  
//		 }  
//	}

}
