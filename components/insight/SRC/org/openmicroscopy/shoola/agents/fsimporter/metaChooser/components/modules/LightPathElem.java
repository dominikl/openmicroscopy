package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules;

/**
* @author Susanne Kunis &nbsp;&nbsp;&nbsp;&nbsp; <a
*         href="mailto:susannekunis@gmail.com">susannekunis@gmail.com</a>
*/
public abstract class LightPathElem extends ElementsCompUI 
	implements Cloneable
{
	static final String EXITATION="Excitation Filter";
	static final String EMISSION="Emission Filter";
	static final String DICHROIC="Dichroic";
	
	String id;
//	protected TagData name;
	protected String specification;
	
	public void setID(String value)
	{
		String val= (value != null) ? String.valueOf(value):"";
		id=val;
	}
	public String getID(){
		return id;
	}
	
	public String getIDNumber(){
		String idNum="";
		if(id!=null && !id.equals(""))
			idNum=id.substring(id.indexOf(":"));
		return idNum;
	}
	
	public String specificName()
	{
		String name="F"+getIDNumber();
		if(specification!=null){
			switch (specification) {
			case EXITATION:
				name="Ex"+getIDNumber();
				break;
			case EMISSION:
				name="Em"+getIDNumber();
				break;
			default:
				name="D"+getIDNumber();
				break;
			}
		}
		return name;
	}
	public void setSpecification(String val){
		specification=val;
	}
	
	/* Implementierte Methode aus dem Interface Cloneable */
    /* Diese Methode kann eine CloneNotSupportedException werfen */
    public abstract Object clone() throws CloneNotSupportedException;
}