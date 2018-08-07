package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model.xml;


/**
* @author Susanne Kunis &nbsp;&nbsp;&nbsp;&nbsp; <a
*         href="mailto:susannekunis@gmail.com">susannekunis@gmail.com</a>
*/
public class DetectorSettings extends ome.xml.model.DetectorSettings {
	private String subarray;

	/** Default constructor. */
	public DetectorSettings(){}
	
	/**Copy Constructor*/
	public DetectorSettings(DetectorSettings orig){
		super(orig);
		this.subarray=orig.subarray;
	}
	
	public DetectorSettings(ome.xml.model.DetectorSettings orig){
		super(orig);
		this.subarray=null;
	}
	
	public String getSubarray() {
		return subarray;
	}

	public void setSubarray(String subarray) {
		this.subarray = subarray;
	}
}
