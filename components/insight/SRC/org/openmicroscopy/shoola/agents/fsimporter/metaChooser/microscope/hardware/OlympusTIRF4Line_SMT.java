package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.microscope.hardware;

import java.util.ArrayList;
import java.util.List;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.ModuleConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.TagNames;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.microscope.CustomViewProperties;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.microscope.MicroscopeProperties;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.microscope.MetaDataUI.GUIPlaceholder;

import ome.units.quantity.Length;
import ome.units.quantity.Power;
import ome.xml.model.Detector;
import ome.xml.model.Dichroic;
import ome.xml.model.Filter;
import ome.xml.model.FilterSet;
import ome.xml.model.Laser;
import ome.xml.model.LightPath;
import ome.xml.model.LightSource;
import ome.xml.model.Objective;
import ome.xml.model.enums.DetectorType;
import ome.xml.model.enums.FilterType;
import ome.xml.model.enums.Immersion;
import ome.xml.model.enums.LaserType;
import ome.xml.model.enums.UnitsLength;
import ome.xml.model.enums.UnitsPower;
import ome.xml.model.enums.handlers.UnitsLengthEnumHandler;
import ome.xml.model.enums.handlers.UnitsPowerEnumHandler;


/**
 * SFB-Olympus-TIRF-4-LINE
 * @author Kunis
 *
 */
public class OlympusTIRF4Line_SMT extends MicroscopeProperties{
	
	//filter and dichroics , define here because of reusing object in mapr function
	private Filter IX2_MDICT;
	private Dichroic D_ZT_405_488_561_640RPC;
	private Filter BRIGHTLINE_HC_466_523_500_677;
	private Filter BRIGHTLINE_HC_445_45;
	private Filter BRIGHTLINE_HC_525_50;
	private Filter BRIGHTLINE_HC_600_37;
	private Filter BRIGHTLINE_HC_697_58;

	private Filter D_480DCXR;
	private Filter D_565DCXR;
	private Filter D_640DCXR;
	private Filter BRIGHTLINE_HC_438_24;
	private Filter BRIGHTLINE_HC_520_35;
	private Filter BRIGHTLINE_HC_685_40;
	
	
	public OlympusTIRF4Line_SMT()
	{
		initLightPathFilter();
		detectors=this.getMicDetectorList();
		objectives=this.getMicObjectiveList();
		lightSources=this.getMicLightSrcList();
		lightPathObjects=this.getMicLightPathFilterList();
	}
	
	

	@Override
	public List<LightSource> getMicLightSrcList() {
		List<LightSource> list=new ArrayList<>();
		
		Laser l=new Laser();
//		l.setModel("");
//		l.setManufacturer("");
//		l.setType(LaserType.SOLIDSTATE);
//		l.setLaserMedium(LaserMedium.OTHER);
		l.setWavelength(new Length(405, UnitsLengthEnumHandler.getBaseUnit(UnitsLength.NM)));
//		l.setPulse(Pulse.CW);
		l.setPower(new Power(100, UnitsPowerEnumHandler.getBaseUnit(UnitsPower.MW)));
		list.add(l);
		
		l=new Laser();
//		l.setModel("LuxX 488-200");
//		l.setManufacturer("Omicron");
		l.setType(LaserType.SOLIDSTATE);
//		l.setLaserMedium(LaserMedium.OTHER);//??
		l.setWavelength(new Length(488, UnitsLengthEnumHandler.getBaseUnit(UnitsLength.NM)));
//		l.setPulse(Pulse.CW);//??
		l.setPower(new Power(150, UnitsPowerEnumHandler.getBaseUnit(UnitsPower.MW)));
		list.add(l);
		
		l=new Laser();
//		l.setModel("Cobolt Samba 532");
//		l.setManufacturer("Cobolt");
		l.setType(LaserType.SOLIDSTATE);
//		l.setLaserMedium(LaserMedium.OTHER);//??
		l.setWavelength(new Length(561, UnitsLengthEnumHandler.getBaseUnit(UnitsLength.NM)));
//		l.setPulse(Pulse.CW);//??
		l.setPower(new Power(150, UnitsPowerEnumHandler.getBaseUnit(UnitsPower.MW)));
		list.add(l);
		
		l=new Laser();
//		l.setModel("Cobolt Jive 561");
//		l.setManufacturer("Cobolt");
//		l.setType(LaserType.SOLIDSTATE);
//		l.setLaserMedium(LaserMedium.OTHER);//??
		l.setWavelength(new Length(640, UnitsLengthEnumHandler.getBaseUnit(UnitsLength.NM)));
//		l.setPulse(Pulse.CW);//??
		l.setPower(new Power(140, UnitsPowerEnumHandler.getBaseUnit(UnitsPower.MW)));
		list.add(l);
		
		
		
		return list;
	}

	private void initLightPathFilter(){
		//TIRF-Cube
		Dichroic d1= new Dichroic();
		d1.setModel("zt405/488/561/640rpc");
		d1.setManufacturer("Chroma");
		D_ZT_405_488_561_640RPC=d1;
		
		BRIGHTLINE_HC_466_523_500_677=getFilter("BrightLine HC 446/523/500/677",FilterType.BANDPASS,446,677,UnitsLength.NM, "Semrock");
				
		IX2_MDICT= getFilter("DIC Cube:IX2-MDICT",FilterType.OTHER,-1,-1,null,"Olympus");
		
		
		BRIGHTLINE_HC_445_45=getFilter("BrightLine HC 445/45",FilterType.BANDPASS,423,468,UnitsLength.NM, "Semrock");
		
		BRIGHTLINE_HC_525_50=getFilter("BrightLine HC 525/50",FilterType.BANDPASS,500,550,UnitsLength.NM, "Semrock");
		
		BRIGHTLINE_HC_600_37=getFilter("BrightLine HC 600/37",FilterType.BANDPASS,582,619,UnitsLength.NM, "Semrock");
		
		BRIGHTLINE_HC_697_58=getFilter("BrightLine HC 697/58",FilterType.BANDPASS,668,726,UnitsLength.NM, "Semrock");
		
		
		D_480DCXR=getFilter("Beamsplitter 480dcxr",FilterType.LONGPASS,480,-1,UnitsLength.NM, "Chroma");
		D_565DCXR=getFilter("Beamsplitter 565dcxr",FilterType.LONGPASS,565,-1,UnitsLength.NM, "Chroma");
		D_640DCXR=getFilter("Beamsplitter 640dcxr",FilterType.LONGPASS,640,-1,UnitsLength.NM, "Chroma");
		
		BRIGHTLINE_HC_438_24=getFilter("BrightLine HC 438/24 (blue:DAPI,BFP)",FilterType.BANDPASS,426,450,UnitsLength.NM, "Semrock");
		BRIGHTLINE_HC_520_35=getFilter("BrightLine HC 520/35 (green: GFP)",FilterType.BANDPASS,502,538,UnitsLength.NM, "Semrock");
		BRIGHTLINE_HC_600_37=getFilter("BrightLine HC 600/37 (orange: TMR,mCherry)",FilterType.BANDPASS,582,619,UnitsLength.NM,"Semrock");
		BRIGHTLINE_HC_685_40=getFilter("BrightLine HC 685/40 (red: Cy5,Atto 655)",FilterType.BANDPASS,665,705,UnitsLength.NM,"Semrock");
		
	}

	@Override
	public List<Object> getMicLightPathFilterList() {
		List<Object> list = new ArrayList<Object>();
		
		// TODO:
		//- cutIn, CutOut richtig?
		//-polarizer?		
		
		//TIRF-Cube
		FilterSet fs1= new FilterSet();
		fs1.setModel("TIRF-Cube");
		fs1.linkDichroic(D_ZT_405_488_561_640RPC);
		fs1.linkEmissionFilter(BRIGHTLINE_HC_466_523_500_677);
		list.add(fs1);
		
		
		//DIC
		list.add(IX2_MDICT);
		
		//405 und DAPI
		list.add(BRIGHTLINE_HC_445_45);
		
		//488 und GFP
		list.add(BRIGHTLINE_HC_525_50);
		
		//561 und TMR
		list.add(BRIGHTLINE_HC_600_37);
		
		//642 und Cy5
		list.add(BRIGHTLINE_HC_697_58);
		
		
		//405+488 (405+561, 405+561, 488+561, 488+561+642,...)
		fs1= new FilterSet();
		fs1.setModel("QuadView Filter Cube");
		//TODO
		fs1.linkEmissionFilter(D_480DCXR);
		fs1.linkEmissionFilter(D_565DCXR);
		fs1.linkEmissionFilter(D_640DCXR);
		
		fs1.linkEmissionFilter(BRIGHTLINE_HC_438_24);
		fs1.linkEmissionFilter(BRIGHTLINE_HC_520_35);
		fs1.linkEmissionFilter(BRIGHTLINE_HC_600_37);
		fs1.linkEmissionFilter(BRIGHTLINE_HC_685_40);
		list.add(fs1);
		
		
		
		return list;
	}



	@Override
	protected List<Detector> getMicDetectorList() {
		
		List<Detector> list=new ArrayList<Detector>();
		
		Detector d=new Detector();
		d.setModel("Hamamatsu ORCA-Flash4.0");
		d.setManufacturer("Hamamatsu");
		d.setType(DetectorType.CMOS);
		list.add(d);
		
		
		
		return list;
	}



	@Override
	protected List<Objective> getMicObjectiveList() {
		List<Objective> list=new ArrayList<>();
		
		Objective o=new Objective();
		o.setModel("UPLAFLN 20x");
		o.setManufacturer("Olympus");
		o.setNominalMagnification(20.0);
		o.setCalibratedMagnification(20.0);
		o.setLensNA(0.75);
		o.setImmersion(Immersion.AIR);
//		o.setCorrection(Correction.PLANAPO);
//		o.setWorkingDistance(new Length(600, UnitsLengthEnumHandler.getBaseUnit(UnitsLength.MICROM)));
		list.add(o);

		o=new Objective();
		o.setModel("PLAPON 60x");
		o.setManufacturer("Olympus");
		o.setNominalMagnification(60.0);
		o.setCalibratedMagnification(60.0);
		o.setLensNA(1.49);
		o.setImmersion(Immersion.OIL);
//		o.setCorrection(Correction.PLANFLUOR);
		o.setWorkingDistance(new Length(100, UnitsLengthEnumHandler.getBaseUnit(UnitsLength.MICROM)));
		list.add(o);
		
		o=new Objective();
		o.setModel("UAPON OTIRF 100x / 1,49");
		o.setManufacturer("Olympus");
		o.setNominalMagnification(100.0);
		o.setCalibratedMagnification(100.0);
		o.setLensNA(1.49);
		o.setImmersion(Immersion.OIL);
//		o.setCorrection(Correction.PLANAPO);
		o.setWorkingDistance(new Length(100, UnitsLengthEnumHandler.getBaseUnit(UnitsLength.MICROM)));
		list.add(o);

		o=new Objective();
		o.setModel("UAPO OTIRFM 150x / 1,45");
		o.setManufacturer("Olympus");
		o.setNominalMagnification(150.0);
		o.setCalibratedMagnification(150.0);
		o.setLensNA(1.45);
		o.setImmersion(Immersion.OIL);
//		o.setCorrection(Correction.PLANAPO);
		o.setWorkingDistance(new Length(80, UnitsLengthEnumHandler.getBaseUnit(UnitsLength.MICROM)));
		list.add(o);
		
		
		return list;
	}
	


	@Override
	public ModuleConfiguration loadLightSrcConf(boolean active, GUIPlaceholder pos, String width) {
		ModuleConfiguration lightSrcConf=new ModuleConfiguration(active,pos,width);
		lightSrcConf.setTag(TagNames.MODEL,null,null,true, null, true);
		lightSrcConf.setTag(TagNames.MANUFAC,null,null,true, null, true);
		lightSrcConf.setTag(TagNames.POWER,null,TagNames.POWER_UNIT.getSymbol(),true, null, true);
		lightSrcConf.setTag(TagNames.L_TYPE,null,null,true, null, true);
		lightSrcConf.setTag(TagNames.MEDIUM,null,null,true, null, true);
		lightSrcConf.setTag(TagNames.FREQMUL,null,null,true, null, false);
		lightSrcConf.setTag(TagNames.TUNABLE,null,null,true, null, false);
		lightSrcConf.setTag(TagNames.PULSE,null,null,true, null, false);
		lightSrcConf.setTag(TagNames.POCKELCELL,null,null,true, null, false);
		lightSrcConf.setTag(TagNames.REPRATE,null,TagNames.REPRATE_UNIT_HZ.getSymbol(),true, null, false);
		lightSrcConf.setTag(TagNames.PUMP,null,null,true, null, false);
		lightSrcConf.setTag(TagNames.WAVELENGTH,null,TagNames.WAVELENGTH_UNIT.getSymbol(),true, null, true);
		lightSrcConf.setSettingTag(TagNames.SET_WAVELENGTH,null,TagNames.WAVELENGTH_UNIT.getSymbol(),true, null, false);
		lightSrcConf.setSettingTag(TagNames.ATTENUATION,null,null,true, null, true);
		return lightSrcConf;
	}


	@Override
	public ModuleConfiguration loadDetectorConf(boolean active, GUIPlaceholder pos, String width) {
		ModuleConfiguration detectorConf=new ModuleConfiguration(active,pos,width);
		detectorConf.setTag(TagNames.MODEL,null,null,true, null, true);
		detectorConf.setTag(TagNames.MANUFAC,null,null,true, null, true);
		detectorConf.setTag(TagNames.D_TYPE,null,null,true, null, true);
		detectorConf.setTag(TagNames.ZOOM,null,null,true, null, false);
		detectorConf.setTag(TagNames.AMPLGAIN,null,null,true, null, false);
		detectorConf.setSettingTag(TagNames.GAIN,null,null,true, null, false);
		detectorConf.setSettingTag(TagNames.VOLTAGE,null,TagNames.VOLTAGE_UNIT.getSymbol(),true, null, false);
		detectorConf.setSettingTag(TagNames.OFFSET,null,null,true, null, false);
		detectorConf.setSettingTag(TagNames.CONFZOOM,null,null,true, null, false);
		detectorConf.setSettingTag(TagNames.BINNING,null,null,true, null, true);
		detectorConf.setSettingTag(TagNames.SUBARRAY,null,null,true, null, true);
		return detectorConf;
	}

	
	
	@Override
	protected ModuleConfiguration loadChannelConf(boolean active,GUIPlaceholder pos,String width) {
		String[] ILLUMINATIONTYPE_TIRF4LINE_SMT={"Transmitted","Epifluorescence","TIR","HILO"};
		String[] CONTRASTMETHOD={"Brightfield","DIC","Fluorescence"};
		String[] IMAGINGMODE={"Widefield","TIRF","SingleMoleculeImaging","PALM","STORM","Other"};
		
		
		ModuleConfiguration channelConf=new ModuleConfiguration(active,pos,width);
		channelConf.setTag(TagNames.CH_NAME,null,null,true, null, true);
		channelConf.setTag(TagNames.CONTRASTMETHOD,null,null,true, CONTRASTMETHOD, true);
		channelConf.setTag(TagNames.FLUOROPHORE,null,null,true, null, true);
		channelConf.setTag(TagNames.ILLUMTYPE,null,null,true, ILLUMINATIONTYPE_TIRF4LINE_SMT, true);
		channelConf.setTag(TagNames.EXPOSURETIME,null,TagNames.EMISSIONWL_UNIT.getSymbol(),true, null, true);
		channelConf.setTag(TagNames.EXCITWAVELENGTH,null,TagNames.EXCITATIONWL_UNIT.getSymbol(),true, null, true);
		channelConf.setTag(TagNames.EMISSIONWAVELENGTH,null,null,true, null, true);
		channelConf.setTag(TagNames.IMAGINGMODE,null,null,true, IMAGINGMODE, true);
		channelConf.setTag(TagNames.NDFILTER,null,null,true, null, false);
		channelConf.setTag(TagNames.PINHOLESIZE,null,TagNames.PINHOLESIZE_UNIT.getSymbol(),true, null, false);
		
		
		
		return channelConf;
	}
	
	
	@Override
	protected  ModuleConfiguration loadSampleConf(boolean active,GUIPlaceholder pos,String width){
		ModuleConfiguration sampleConf=new ModuleConfiguration(active,pos,width);
		sampleConf.setTag(TagNames.PREPDATE,null,null,true, null, true);
		sampleConf.setTag(TagNames.PREPDESC,null,null,true, null, true);
		sampleConf.setTag(TagNames.RAWCODE,null,null,true, null, false);
		sampleConf.setTag(TagNames.RAWDESC,null,null,true, null, true);
		sampleConf.setTag(TagNames.GRIDBOXNR,null,null,true, null, false);
		sampleConf.setTag(TagNames.GRIDBOXTYPE,null,null,true, null, true);
		sampleConf.setTag(TagNames.EXPGRID,null,null,true, null, false);
		sampleConf.setTag(TagNames.EXPOBJNR,null,null,true, null, false);
		sampleConf.setTag(TagNames.EXPOBJTYPE,null,null,true, null, false);
		return sampleConf;
	}
	
	@Override
	protected  ModuleConfiguration loadImageEnvConf(boolean active,GUIPlaceholder pos,String width){
		ModuleConfiguration imgEnvConf=new ModuleConfiguration(active,pos,width);
		imgEnvConf.setTag(TagNames.TEMP,null,null,true, null, true);
		imgEnvConf.setTag(TagNames.AIRPRESS,null,null,true, null, false);
		imgEnvConf.setTag(TagNames.HUMIDITY,null,null,true, null, true);
		imgEnvConf.setTag(TagNames.CO2,null,null,true, null, true);
		return imgEnvConf;
	}
	
	
	@Override
	protected void initCustomView(){
		view = new CustomViewProperties();
		LOGGER.info("[VIEW_PROP] Load OlympusTIRF4Line_SMT view");
		view.setMicName(TIRF4LINE_SMT);
		view.setImageConf(getImageConf());
    	
		view.setObjConf(getObjectiveConf());
		view.setDetectorConf(getDetectorConf());
		view.setLightSrcConf(getLightSrcConf());
		view.setChannelConf(getChannelConf());
		view.setLightPathConf(getLightPathConf());
		view.setSampleConf(getSampleConf());
		view.setExperimenterConf(getExperimentConf());
		view.setImgEnvConf(getImageEnvConf());
	}
	
	
	// map key: channelname and return lightPath
	@Override
	public Object getMapr(String key){
		LightPath lp=null;
		switch(key){
		case "DIC":
			lp=new LightPath();
			lp.linkEmissionFilter(IX2_MDICT);
			break;
		case "405":
		case "DAPI":
			lp=new LightPath();
			lp.linkDichroic(D_ZT_405_488_561_640RPC);
			lp.linkEmissionFilter(BRIGHTLINE_HC_466_523_500_677);
			lp.linkEmissionFilter(BRIGHTLINE_HC_445_45);
			break;
		case "488":
		case "GFP":
			lp=new LightPath();
			lp.linkDichroic(D_ZT_405_488_561_640RPC);
			lp.linkEmissionFilter(BRIGHTLINE_HC_466_523_500_677);
			lp.linkEmissionFilter(BRIGHTLINE_HC_525_50);
			break;
		case "561":
		case "TMR":
			lp=new LightPath();
			lp.linkDichroic(D_ZT_405_488_561_640RPC);
			lp.linkEmissionFilter(BRIGHTLINE_HC_466_523_500_677);
			lp.linkEmissionFilter(BRIGHTLINE_HC_600_37);
			break;
		case "642":
		case "Cy5":
			lp=new LightPath();
			lp.linkDichroic(D_ZT_405_488_561_640RPC);
			lp.linkEmissionFilter(BRIGHTLINE_HC_466_523_500_677);
			lp.linkEmissionFilter(BRIGHTLINE_HC_697_58);
			break;
		default:
			//TODO: if + in channel name:... else return null
			if(key.contains("+")){
				lp=new LightPath();
				lp.linkDichroic(D_ZT_405_488_561_640RPC);
				lp.linkEmissionFilter(BRIGHTLINE_HC_466_523_500_677);
				lp.linkEmissionFilter(D_480DCXR);
				lp.linkEmissionFilter(D_565DCXR);
				lp.linkEmissionFilter(D_640DCXR);
				lp.linkEmissionFilter(BRIGHTLINE_HC_438_24);
				lp.linkEmissionFilter(BRIGHTLINE_HC_520_35);
				lp.linkEmissionFilter(BRIGHTLINE_HC_600_37);
				lp.linkEmissionFilter(BRIGHTLINE_HC_685_40);
			}
			break;
		}
		return lp;
		
	}

}
