package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model;

import java.util.List;

import ome.units.unit.Unit;
import ome.xml.model.primitives.Timestamp;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.format.ObservedSample;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.format.Sample;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.format.Sample.GridBox;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.TagNames;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagData;
import org.slf4j.LoggerFactory;

public class SampleModel 
{
	private static final org.slf4j.Logger LOGGER =
			LoggerFactory.getLogger(SampleModel.class);
	
	private Sample element;
	
	public SampleModel()
	{
		element=new Sample();
	}
	public SampleModel(SampleModel orig)
	{
		element=orig.element;
	}
	
	
	public boolean addData(Sample s, boolean overwrite)
	{
		boolean conflicts=false;
		if(overwrite){
			replaceData(s);
			LOGGER.info("[DATA] -- replace SAMPLE data");
		}else
			try {
				completeData(s);
				LOGGER.info("[DATA] -- complete SAMPLE data");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return conflicts;
	}

	private void replaceData(Sample s)
	{
		if(s!=null){
			element=s;
		}
	}

	private void completeData(Sample s) throws Exception
	{
		//copy input fields
		Sample copyIn=null;
		if(element!=null){
			copyIn=new Sample(element);
		}

		replaceData(s);

		// set input field values again
		if(copyIn!=null){
			String pdesc=copyIn.getPrepDescription();
			Timestamp pdate=copyIn.getPrepDate();
			String rc=copyIn.getRawMaterialCode();
			String rdesc=copyIn.getRawMaterialDesc();
			
			GridBox g=copyIn.getGridBox();
			String gNr=null;
			String gT=null;
			if(g!=null){
				gNr=g.getNr();
				gT=g.getType();
			}
			ObservedSample os=copyIn.getObservedSample(0);
			String osgx=null;
			String osgy=null;
			String ost=null;
			String osNr=null;
			if(os!=null){
				osgx=os.getGridNumberX();
				osgy=os.getGridNumberY();
				ost=os.getObjectType();
				osNr=os.getObjectNumber();
			}

			if(pdesc!=null && !pdesc.equals("")) element.setPrepDescription(pdesc);
			if(pdate!=null) element.setPrepDate(pdate);
			if(rc!=null && !rc.equals("")) element.setRawMaterialCode(rc);
			if(rdesc!=null && !rdesc.equals("")) element.setRawMaterialDesc(rdesc);
			if(gNr!=null) element.getGridBox().setNr(gNr);
			if(gT!=null && !gT.equals("")) element.getGridBox().setType(gT);
			if(osgx!=null && !osgx.equals("")) element.getObservedSample(0).setGridNumberY(osgx);
			if(osgy!=null && !osgy.equals("")) element.getObservedSample(0).setGridNumberY(osgy);
			if(ost!=null && !ost.equals("")) element.getObservedSample(0).setObjectType(ost);
			if(osNr!=null && !osNr.equals("")) element.getObservedSample(0).setObjectNumber(osNr);
		}
	}
	public Sample getSample()
	{
		return element;
	}
	public void update(List<TagData> changesSample) 
	{
		
		if(changesSample==null)
			return;
		System.out.println("# SampleModel::update()");
		for(TagData t: changesSample){
			setTag(t.getTagName(),t.getTagValue(),t.getTagUnit());
		}		
	}
	private void setTag(String name,String val,Unit unit)
	{
		System.out.println("\t...update "+name+" : "+val);
		switch (name) {
		case TagNames.PREPDATE:// no pre value possible
			element.setPrepDate(val.equals("")? 
					null : Timestamp.valueOf(val));
			break;
		case TagNames.PREPDESC:// no pre value possible
			element.setPrepDescription(val);
			break;
		case TagNames.RAWCODE:// no pre value possible
			element.setRawMaterialCode(val); 
			break;
		case TagNames.RAWDESC:// no pre value possible
			element.setRawMaterialDesc(val);
			break;
		case TagNames.GRIDBOXNR:// no pre value possible
			element.setGridBoxNr(val);
			break;
		case TagNames.GRIDBOXTYPE:// no pre value possible
			element.setGridBoxType(val);
			break;
		case TagNames.EXPGRID:// no pre value possible
//			TODO:
//			setExpGridNumber(new String[2], prop);
			break;
		case TagNames.EXPOBJNR:// no pre value possible
			ObservedSample sample=element.getObservedSample(0);
			if(sample==null)
				sample=new ObservedSample();
			sample.setObjectNumber(val);
			element.setObservedSample(sample);
			break;
		case TagNames.EXPOBJTYPE: // no pre value possible
			ObservedSample sample2=element.getObservedSample(0);
			if(sample2==null)
				sample2=new ObservedSample();
			sample2.setObjectType(val);
			element.setObservedSample(sample2);
			break;
		default:
			LOGGER.warn("[CONF] unknown tag: "+name );break;
		}
	}
}
