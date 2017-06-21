package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.microscope.CustomViewProperties;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagConfiguration;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class UOSProfileWriter 
{
	private static final org.slf4j.Logger LOGGER =
			LoggerFactory.getLogger(UOSProfileWriter.class);
	
	private CustomViewProperties view;
	
	public UOSProfileWriter()
	{
	}
	
	/**
	 * Save to file if available.
	 * @param file
	 * @param prop
	 */
	public void save(File file, CustomViewProperties prop)
	{
		if(file==null)
			return;
		
		if(!file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
			
		if(!file.isFile()){
			System.out.println("Can't access file "+(file==null?"!":file.getAbsolutePath()));
			LOGGER.warn("[PROFILE EDITOR]: Can't access file "+(file==null?"!":file.getAbsolutePath()));
			return;
		}


		LOGGER.info("[PROFILE EDITOR]: Save to "+file.getAbsolutePath());
		System.out.println("# UOSProfileWriter::save() "+file.getAbsolutePath());
		
		view=prop;
		view.setFile(file);
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder db=dbf.newDocumentBuilder();
			Document doc = db.newDocument();
			
			writeConfiguration(doc);
			
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(view.getFile());

			// Output to console for testing
			// StreamResult result = new StreamResult(System.out);

			transformer.transform(source, result);

			System.out.println("\t...File saved!");
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void writeConfiguration(Document doc) 
	{
		// root element
		Element root=doc.createElement(UOSProfileReader.PROFILE);
		doc.appendChild(root);
		
		// mic
		Element mic=doc.createElement(UOSProfileReader.MICROSCOPE);
//		Attr attr=doc.createAttribute(UOSProfileReader.MIC_NAME);
//		attr.setValue(view.getMicName());
		root.appendChild(mic);
		mic.setAttribute(UOSProfileReader.MIC_NAME, view.getMicName()!=null ? view.getMicName():"Unspecific");
		
		//submodules
		Element sub=doc.createElement(UOSProfileReader.MODULE);
		root.appendChild(sub);
		if(view.getImageConf()!=null)
			sub.appendChild(view.getImageConf().toXML(doc, UOSProfileReader.MODULE_IMG));
		if(view.getObjConf()!=null)
			sub.appendChild(view.getObjConf().toXML(doc, UOSProfileReader.MODULE_OBJECTIVE));
		if(view.getDetectorConf()!=null)
			sub.appendChild(view.getDetectorConf().toXML(doc, UOSProfileReader.MODULE_DETECTOR));
		if(view.getLightSrcConf()!=null)
			sub.appendChild(view.getLightSrcConf().toXML(doc, UOSProfileReader.MODULE_LIGHTSRC));
		if(view.getChannelConf()!=null)
			sub.appendChild(view.getChannelConf().toXML(doc, UOSProfileReader.MODULE_CHANNEL));
		if(view.getLightPathConf()!=null)
			sub.appendChild(view.getLightPathConf().toXML_LP(doc, UOSProfileReader.MODULE_LIGHTPATH));
		if(view.getSampleConf()!=null)
			sub.appendChild(view.getSampleConf().toXML(doc, UOSProfileReader.MODULE_SAMPLE));
		if(view.getExpConf()!=null)
			sub.appendChild(view.getExpConf().toXML(doc, UOSProfileReader.MODULE_EXPERIMENTER));
		if(view.getImgEnvConf()!=null)
			sub.appendChild(view.getImgEnvConf().toXML(doc, UOSProfileReader.MODULE_IMGENV));
		if(view.getPlaneConf()!=null)
			sub.appendChild(view.getPlaneConf().toXML(doc, UOSProfileReader.MODULE_PLANE));
	}


}
