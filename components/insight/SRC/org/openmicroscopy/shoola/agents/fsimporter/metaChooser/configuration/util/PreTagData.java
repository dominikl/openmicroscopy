package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.util;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.TagNames;

import ome.units.unit.Unit;
/**
* @author Susanne Kunis &nbsp;&nbsp;&nbsp;&nbsp; <a
*         href="mailto:susannekunis@gmail.com">susannekunis@gmail.com</a>
*/
public class PreTagData {

	String name;
	Unit defaultUnit;
	String[] unitsList;
	String settings;

	public PreTagData(String name, Unit defaultUnit, String isSett)
	{
		this.name=name;
		this.defaultUnit=defaultUnit;
		this.settings=isSett;
		this.unitsList=null;
		if(defaultUnit!=null){
			unitsList=TagNames.getUnits(name);
		}
	}
}

