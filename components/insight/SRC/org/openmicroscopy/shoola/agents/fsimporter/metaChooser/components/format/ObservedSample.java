package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.format;

import java.util.logging.Logger;

import ome.xml.model.primitives.Timestamp;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.UOSMetadataLogger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Object for observed object metadata like
 * 
 * <ObservedObject namespace="">
 * 		<SampleRef ID="">
 * 		<Grid GridBoxRef="" Number=""/ oder PosX="" PosY="">
 * 		<Object Type="" Number="">
 * </ObservedObject>
 * @author kunis
 *
 */
public class ObservedSample 
{
	
	/** Logger for this class. */
    private static Logger LOGGER = Logger.getLogger(UOSMetadataLogger.class.getName());
	//--Fields
	private String sampleID;
	private String gridboxID;
	private String gridNumberX;
	private String gridNumberY;
	private String objectType;
	private String objectNumber;
	
	public static String OBS_SAMPLE="ObservedSample";
	public static String OBS_ID="ID";
	public static String SAMPLE_REF="SampleRef";
	public static String GRID="Grid";
	public static String GRID_REF="GridBoxRefID";
	public static String GRID_NUMBERX="NumberX";
	public static String GRID_NUMBERY="NumberY";
	public static String OBJECT="Object";
	public static String OBJECT_TYPE="Type";
	public static String OBJECT_NUMBER="Number";
	
	public ObservedSample()
	{
		
	}
	
	public ObservedSample(String id,String gridBoxID, String gridNumberx,String gridNumbery, 
			String objType, String objNumber)
	{
		sampleID=id;
		gridboxID=gridBoxID;
		this.gridNumberX=gridNumberx;
		this.gridNumberY=gridNumbery;
		objectType=objType;
		objectNumber=objNumber;
		
		
	}
	
	//copy constructor
	public ObservedSample(ObservedSample orig)
	{
		sampleID=orig.sampleID;
		gridboxID=orig.gridboxID;
		gridNumberX=orig.gridNumberX;
		gridNumberY=orig.gridNumberY;
		objectNumber=orig.objectNumber;
		objectType=orig.objectType;
	}
	
	//parse string of xmlannotation object to sample object
	public ObservedSample(Element element)
	{
		String tagName=element.getTagName();
		if(!OBS_SAMPLE.equals(tagName)){
			LOGGER.warning("Expecting node name of ObservedSample - but this is "+tagName);
		}
		if(element.hasAttribute("namespace")){
			//TODO test right namespace
		}
		NodeList list = element.getElementsByTagName(SAMPLE_REF);
		
		if(list!=null && list.getLength()>0){
			Node node=list.item(0);
			if(node.getNodeType()==Node.ELEMENT_NODE){
				if(((Element) node).hasAttribute("ID")){
					setSampleID(String.valueOf(
							((Element) node).getAttribute("ID")));
				}
			}
		}
		list = element.getElementsByTagName(GRID);
		
		if(list!=null && list.getLength()>0){
			Node node=list.item(0);
			if(node.getNodeType()==Node.ELEMENT_NODE){
				if(((Element) node).hasAttribute(GRID_REF)){
					setGridboxID(String.valueOf(
							((Element) node).getAttribute(GRID_REF)));
				}
				
				if(((Element)node).hasAttribute(GRID_NUMBERX)){
					setGridNumberX(String.valueOf(((Element)node).getAttribute(GRID_NUMBERX)));
				}
				if(((Element)node).hasAttribute(GRID_NUMBERY)){
					setGridNumberY(String.valueOf(((Element)node).getAttribute(GRID_NUMBERY)));
				}
			}
		}
		
		list=element.getElementsByTagName(OBJECT);
		if(list!=null && list.getLength()>0){
			Node node=list.item(0);
			if(node.getNodeType()==Node.ELEMENT_NODE){
				if(((Element) node).hasAttribute(OBJECT_NUMBER)){
					setObjectNumber(String.valueOf(
							((Element) node).getAttribute(OBJECT_NUMBER)));
				}
				
				if(((Element)node).hasAttribute(OBJECT_TYPE)){
					setObjectType(String.valueOf(((Element)node).getAttribute(OBJECT_TYPE)));
				}
			}
		}
	}
	
	/**
	 * @return the sampleID
	 */
	public String getSampleID() {
		return sampleID;
	}

	/**
	 * @param sampleID the sampleID to set
	 */
	public void setSampleID(String sampleID) {
		this.sampleID = sampleID;
	}

	/**
	 * @return the gridboxID
	 */
	public String getGridboxID() {
		return gridboxID;
	}

	/**
	 * @param gridboxID the gridboxID to set
	 */
	public void setGridboxID(String gridboxID) {
		this.gridboxID = gridboxID;
	}

	/**
	 * @return the gridNumber
	 */
	public String getGridNumberX() {
		return gridNumberX;
	}
	public String getGridNumberY() {
		return gridNumberY;
	}

	/**
	 * @param gridNumber the gridNumber to set
	 */
	public void setGridNumberX(String gridNumber) {
		this.gridNumberX = gridNumber;
	}
	public void setGridNumberY(String gridNumber) {
		this.gridNumberY = gridNumber;
	}

	/**
	 * @return the objectType
	 */
	public String getObjectType() {
		return objectType;
	}

	/**
	 * @param objectType the objectType to set
	 */
	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	/**
	 * @return the objectID
	 */
	public String getObjectNumber() {
		return objectNumber;
	}

	/**
	 * @param objectID the objectID to set
	 */
	public void setObjectNumber(String objectID) {
		this.objectNumber = objectID;
	}

	public String toXMLAnnotation()
	{
		StringBuffer xml=new StringBuffer("<"+OBS_SAMPLE+" namespace=\"uos/Schemas/Additions/2016-02\">");
		
		if(sampleID!=null){
			xml.append("<"+SAMPLE_REF+" ID=\""+sampleID+"\" />");
		}
		
		xml=appendGridBoxProp(xml);
		
		xml=appendObjectProp(xml);
		
		xml.append("</"+OBS_SAMPLE+">");
		
		return xml.toString();
	}

	private StringBuffer appendObjectProp(StringBuffer xml)
	{
		boolean write=false;
		if(gridboxID!=null || gridNumberX!=null || gridNumberY!=null){
			write=true;
			xml.append("<"+GRID);
		}
		
		if(gridboxID!=null)
			xml.append(" "+GRID_REF+"=\""+gridboxID+"\"");
		
		if(gridNumberX!=null)
			xml.append(" "+GRID_NUMBERX+"=\""+gridNumberX+"\"");
		
		if(gridNumberY!=null)
			xml.append(" "+GRID_NUMBERY+"=\""+gridNumberY+"\"");
		
		if(write)
			xml.append("/>");
		return xml;
	}

	private StringBuffer appendGridBoxProp(StringBuffer xml) 
	{
		boolean write=false;
		if(objectNumber!=null || objectType!=null){
			write=true;
			xml.append("<"+OBJECT);
		}
		
		if(objectType!=null)
			xml.append(" "+OBJECT_TYPE+"=\""+objectType+"\"");
		
		if(objectNumber!=null)
			xml.append(" "+OBJECT_NUMBER+"=\""+objectNumber+"\"");
		
		if(write)
			xml.append("/>");
		return xml;
	}
	
	public final static String getTagName()
	{
		return OBS_SAMPLE;
	}
	
	
}
