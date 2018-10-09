package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.microscope;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.ModuleConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.TagNames;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.microscope.MetaDataUI.GUIPlaceholder;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.microscope.hardware.LeicaLSMSP5;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.microscope.hardware.OlympusLSMFV1000;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.microscope.hardware.OlympusTIRF3Line;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.microscope.hardware.OlympusTIRF4Line_SMT;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.microscope.hardware.OlympusTIRF4Line_STORM;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.microscope.hardware.StandardMic;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.microscope.hardware.ZeissCellObserverSD;
import org.slf4j.LoggerFactory;

import ome.units.quantity.Length;
import ome.xml.model.Detector;
import ome.xml.model.Filter;
import ome.xml.model.LightSource;
import ome.xml.model.Objective;
import ome.xml.model.TransmittanceRange;
import ome.xml.model.enums.FilterType;
import ome.xml.model.enums.UnitsLength;
import ome.xml.model.enums.handlers.UnitsLengthEnumHandler;
/**
* @author Susanne Kunis &nbsp;&nbsp;&nbsp;&nbsp; <a
*         href="mailto:susannekunis@gmail.com">susannekunis@gmail.com</a>
*/
public abstract class MicroscopeProperties 
{
	protected final static String FLUOVIEW1000="Olympus LSM FV1000";
	protected final static String LEICASP5="Leica LSM SP5";
	protected final static String ZEISSSD="Zeiss Cell Observer SD";
	protected final static String TIRF3LINE="Olympus TIRF 3-Line";
	protected final static String TIRF4LINE_SMT="Olympus TIRF 4-LINE SMT";
	protected final static String TIRF4LINE_STORM="Olympus TIRF 4-LINE STORM";
	protected final static String ZEISSLSM="Zeiss LSM 510 META NLO";
	protected final static String DELTAVISION="DeltaVision Elite";
	protected final static String UNIVERSAL="Universal";


	public static final String[] availableMics={UNIVERSAL,FLUOVIEW1000,LEICASP5,ZEISSSD,TIRF3LINE,
			TIRF4LINE_SMT,TIRF4LINE_STORM};

	protected ModuleConfiguration imageConfiguration;
	protected ModuleConfiguration lightPathConfiguration;
	protected ModuleConfiguration sampleConfiguration;
	protected ModuleConfiguration channelConfiguration;
	protected ModuleConfiguration lightSrcConfiguration;
	protected ModuleConfiguration imgEnvConfiguration;
	protected ModuleConfiguration expConfiguration;
	protected ModuleConfiguration detectorConfiguration;
	protected ModuleConfiguration oConfiguration;

	protected List<Detector> detectors;
	protected List<LightSource> lightSources;
	protected List<Objective> objectives;
	protected List<Object> lightPathObjects;

	protected CustomViewProperties view;
	/** Logger for this class. */
	protected static final org.slf4j.Logger LOGGER =
			LoggerFactory.getLogger(CustomViewProperties.class);

	public CustomViewProperties getViewProperties()
	{
		if(view==null)
			initCustomView();
		return view;
	}

	public List<Detector> getDetectorList()
	{
		return detectors;
	}

	public List<Objective> getObjectiveList()
	{
		return objectives;
	}

	public List<LightSource> getLightSourceList()
	{
		return lightSources;
	}

	public List<Object> getLightPathList()
	{
		return lightPathObjects;
	}

	protected abstract List<LightSource> getMicLightSrcList();
	protected abstract List<Object> getMicLightPathFilterList();
	protected abstract List<Detector> getMicDetectorList();
	protected abstract List<Objective> getMicObjectiveList();

	/**
	 * Return class for hardware specification, if available.
	 * @param micName
	 * @return
	 */
	public static MicroscopeProperties getMicClass(String micName)
	{
		switch(micName){
		case FLUOVIEW1000:
			return new OlympusLSMFV1000();
		case LEICASP5:
			return new LeicaLSMSP5();
		case ZEISSSD:
			return new ZeissCellObserverSD();
		case TIRF3LINE:
			return new OlympusTIRF3Line();
		case TIRF4LINE_SMT:
			return new OlympusTIRF4Line_SMT();
		case TIRF4LINE_STORM:
			return new OlympusTIRF4Line_STORM();
		default:
			return new StandardMic();
		}	

	}

	protected void initCustomView(){
		view=new CustomViewProperties();
		view.init();
	}

	public HashMap getMapr(){
		return null;
	}

	/**
	 * 
	 * @param microscope name
	 * @return index of given microscope in array availableMics
	 */
	public static int getMicIndex(String microscope) {
		return Arrays.asList(availableMics).indexOf(microscope);
	}

	/**
	 * Override this configuration in hardware specification file if you want to change this
	 * default view (for hiding tags or something
	 * @param active
	 * @param pos
	 * @param width
	 * @return
	 */
	protected ModuleConfiguration loadImageConf(boolean active,GUIPlaceholder pos,String width)
	{
		ModuleConfiguration imageConf=new ModuleConfiguration(active, pos, width);
		imageConf.setTag(TagNames.IMG_NAME,null,null,true, null, true);
		imageConf.setTag(TagNames.IMG_DESC,null,null,true, null, true);
		imageConf.setTag(TagNames.ACQTIME,null,null,true, null, true);
		imageConf.setTag(TagNames.DIMXY,null,null,true, null, true);
		imageConf.setTag(TagNames.PIXELTYPE,null,null,true, null, true);
		imageConf.setTag(TagNames.PIXELSIZE,null,null,true, null, true);
		imageConf.setTag(TagNames.DIMZTC,null,null,true, null, true);
		imageConf.setTag(TagNames.STAGELABEL,null,null,true, null, true);
		imageConf.setTag(TagNames.STEPSIZE,null,null,true, null, true);
		imageConf.setTag(TagNames.TIMEINC,null,null,true, null, true);
		imageConf.setTag(TagNames.WELLNR,null,null,true, null, true);

		return imageConf;
	}

	/**
	 * No predefinitions possible
	 * @param active
	 * @param pos
	 * @param width
	 * @return
	 */
	protected ModuleConfiguration loadLightPathConf(boolean active,GUIPlaceholder pos,String width)
	{
		ModuleConfiguration lightPathConf= new ModuleConfiguration(active,pos,width);
		int index=lightPathConf.addNewElement("Filter",null);
		if(index==-1)
			return lightPathConf;
		lightPathConf.setTag(TagNames.MODEL, null, null, true, index);
		lightPathConf.setTag(TagNames.MANUFAC, null, null, true, index);
		lightPathConf.setTag(TagNames.LP_TYPE, null, null, true, index);
		lightPathConf.setTag(TagNames.FILTERWHEEL, null, null, true, index);

		return lightPathConf;
	}

	/**
	 * Override this configuration in hardware specification file if you want to change this
	 * default view (for hiding tags or something
	 */
	protected  ModuleConfiguration loadSampleConf(boolean active,GUIPlaceholder pos,String width){
		ModuleConfiguration sampleConf=new ModuleConfiguration(active,pos,width);
		sampleConf.setTag(TagNames.PREPDATE,null,null,true, null, true);
		sampleConf.setTag(TagNames.PREPDESC,null,null,true, null, true);
		sampleConf.setTag(TagNames.RAWCODE,null,null,true, null, true);
		sampleConf.setTag(TagNames.RAWDESC,null,null,true, null, true);
		sampleConf.setTag(TagNames.GRIDBOXNR,null,null,true, null, true);
		sampleConf.setTag(TagNames.GRIDBOXTYPE,null,null,true, null, true);
		sampleConf.setTag(TagNames.EXPGRID,null,null,true, null, true);
		sampleConf.setTag(TagNames.EXPOBJNR,null,null,true, null, true);
		sampleConf.setTag(TagNames.EXPOBJTYPE,null,null,true, null, true);
		return sampleConf;
	}


	/**
	 * Override this configuration in hardware specification file if you want to change this
	 * default view (for hiding tags or something
	 */
	protected  ModuleConfiguration loadLightSrcConf(boolean active,GUIPlaceholder pos,String width){
		// laser module for lightSrc
		ModuleConfiguration lightSrcConf=new ModuleConfiguration(active,pos,width);
		lightSrcConf.setTag(TagNames.MODEL,null,null,true, null, true);
		lightSrcConf.setTag(TagNames.MANUFAC,null,null,true, null, true);
		lightSrcConf.setTag(TagNames.POWER,null,TagNames.POWER_UNIT.getSymbol(),true, null, true);
		lightSrcConf.setTag(TagNames.L_TYPE,null,null,true, null, true);
		lightSrcConf.setTag(TagNames.MEDIUM,null,null,true, null, true);
		lightSrcConf.setTag(TagNames.FREQMUL,null,null,true, null, true);
		lightSrcConf.setTag(TagNames.TUNABLE,null,null,true, null, true);
		lightSrcConf.setTag(TagNames.PULSE,null,null,true, null, true);
		lightSrcConf.setTag(TagNames.POCKELCELL,null,null,true, null, true);
		lightSrcConf.setTag(TagNames.REPRATE,null,TagNames.REPRATE_UNIT_HZ.getSymbol(),true, null, true);
		lightSrcConf.setTag(TagNames.PUMP,null,null,true, null, true);
		lightSrcConf.setTag(TagNames.WAVELENGTH,null,TagNames.WAVELENGTH_UNIT.getSymbol(),true, null, true);
		lightSrcConf.setSettingTag(TagNames.SET_WAVELENGTH,null,TagNames.WAVELENGTH_UNIT.getSymbol(),true, null, true);
		lightSrcConf.setSettingTag(TagNames.ATTENUATION,null,null,true, null, true);
		return lightSrcConf;
	}	



	/**
	 * Override this configuration in hardware specification file if you want to change this
	 * default view (for hiding tags or something
	 */
	protected  ModuleConfiguration loadImageEnvConf(boolean active,GUIPlaceholder pos,String width){
		ModuleConfiguration imgEnvConf=new ModuleConfiguration(active,pos,width);
		imgEnvConf.setTag(TagNames.TEMP,null,null,true, null, true);
		imgEnvConf.setTag(TagNames.AIRPRESS,null,null,true, null, true);
		imgEnvConf.setTag(TagNames.HUMIDITY,null,null,true, null, true);
		imgEnvConf.setTag(TagNames.CO2,null,null,true, null, true);
		return imgEnvConf;
	}

	/**
	 * Override this configuration in hardware specification file if you want to change this
	 * default view (for hiding tags or something
	 */
	protected  ModuleConfiguration loadExperimentConf(boolean active,GUIPlaceholder pos,String width){
		ModuleConfiguration expConf=new ModuleConfiguration(active,pos,width);
		expConf.setTag(TagNames.E_TYPE,null,null,true, null, true);
		expConf.setTag(TagNames.DESC,null,null,true, null, true);
		expConf.setTag(TagNames.EXPNAME,null,null,true, null, true);
		expConf.setTag(TagNames.PROJECTNAME,null,null,true, null, true);
		expConf.setTag(TagNames.GROUP,null,null,true, null, true);
		expConf.setTag(TagNames.PROJECTPARTNER,null,null,true, null, true);
		return expConf;
	}

	/**
	 * Override this configuration in hardware specification file if you want to change this
	 * default view (for hiding tags or something
	 */
	protected  ModuleConfiguration loadDetectorConf(boolean active,GUIPlaceholder pos,String width){
		ModuleConfiguration detectorConf=new ModuleConfiguration(active,pos,width);
		detectorConf.setTag(TagNames.MODEL,null,null,true, null, true);
		detectorConf.setTag(TagNames.MANUFAC,null,null,true, null, true);
		detectorConf.setTag(TagNames.D_TYPE,null,null,true, null, true);
		detectorConf.setTag(TagNames.ZOOM,null,null,true, null, true);
		detectorConf.setTag(TagNames.AMPLGAIN,null,null,true, null, true);
		detectorConf.setSettingTag(TagNames.GAIN,null,null,true, null, true);
		detectorConf.setSettingTag(TagNames.VOLTAGE,null,TagNames.VOLTAGE_UNIT.getSymbol(),true, null, true);
		detectorConf.setSettingTag(TagNames.OFFSET,null,null,true, null, true);
		detectorConf.setSettingTag(TagNames.CONFZOOM,null,null,true, null, true);
		detectorConf.setSettingTag(TagNames.BINNING,null,null,true, null, true);
		detectorConf.setSettingTag(TagNames.SUBARRAY,null,null,true, null, true);
		return detectorConf;
	}

	/**
	 * Override this configuration in hardware specification file if you want to change this
	 * default view (for hiding tags or something
	 */
	protected  ModuleConfiguration loadObjectiveConf(boolean active,GUIPlaceholder pos,String width){
		ModuleConfiguration oConf=new ModuleConfiguration(active,pos,width);
		oConf.setTag(TagNames.MODEL,null,null,true, null, true);
		oConf.setTag(TagNames.MANUFAC,null,null,true, null, true);
		oConf.setTag(TagNames.NOMMAGN,null,null,true, null, true);
		oConf.setTag(TagNames.CALMAGN,null,null,true, null, true);
		oConf.setTag(TagNames.LENSNA,null,null,true, null, true);
		oConf.setTag(TagNames.IMMERSION,null,null,true, null, true);
		oConf.setTag(TagNames.CORRECTION,null,null,true, null, true);
		oConf.setTag(TagNames.WORKDIST,null,TagNames.WORKDIST_UNIT.getSymbol(),true, null, true);

		oConf.setSettingTag(TagNames.CORCOLLAR,null,null,true, null, true);
		oConf.setSettingTag(TagNames.OBJ_MEDIUM,null,null,true, null, true);
		oConf.setSettingTag(TagNames.REFINDEX,null,null,true, null, true);
		return oConf;
	}

	/**
	 * No predefinitions possible
	 */
	protected ModuleConfiguration loadChannelConf(boolean active,GUIPlaceholder pos,String width) {
		ModuleConfiguration channelConf=new ModuleConfiguration(active,pos,width);
		channelConf.setTag(TagNames.CH_NAME,null,null,true, null, true);
		channelConf.setTag(TagNames.COLOR,null,null,true, null, true);
		channelConf.setTag(TagNames.FLUOROPHORE,null,null,true, null, true);
		channelConf.setTag(TagNames.ILLUMTYPE,null,null,true, null, true);
		channelConf.setTag(TagNames.EXPOSURETIME,null,TagNames.EMISSIONWL_UNIT.getSymbol(),true, null, true);
		channelConf.setTag(TagNames.EXCITWAVELENGTH,null,TagNames.EXCITATIONWL_UNIT.getSymbol(),true, null, true);
		channelConf.setTag(TagNames.EMISSIONWAVELENGTH,null,null,true, null, true);
		channelConf.setTag(TagNames.IMAGINGMODE,null,null,true, null, true);
		channelConf.setTag(TagNames.CONTRASTMETHOD,null,null,true, null, true);
		channelConf.setTag(TagNames.NDFILTER,null,null,true, null, true);
		channelConf.setTag(TagNames.PINHOLESIZE,null,TagNames.PINHOLESIZE_UNIT.getSymbol(),true, null, true);
		return channelConf;
	}


	protected Filter getFilter(String model,FilterType type,int transRangeIn,int transRangeOut,UnitsLength unit, String manufac){
		TransmittanceRange t=new TransmittanceRange();
		if(transRangeIn!=-1){
			t.setCutIn(new Length(transRangeIn, UnitsLengthEnumHandler.getBaseUnit(unit)));
		}
		if(transRangeOut!=-1){
			t.setCutOut(new Length(transRangeOut, UnitsLengthEnumHandler.getBaseUnit(unit)));
		}


		Filter f= new Filter();
		f.setModel(model);
		if(manufac != null)
			f.setManufacturer(manufac);
		f.setType(type);
		f.setTransmittanceRange(t);
		return f;

	}

	public ModuleConfiguration getImageConf(){
		return loadImageConf(true, GUIPlaceholder.Pos_A, "1");
	}
	public ModuleConfiguration getChannelConf(){
		return loadChannelConf(true, GUIPlaceholder.Pos_E, "1");
	}
	public ModuleConfiguration getObjectiveConf(){
		return loadObjectiveConf(true, GUIPlaceholder.Pos_B, "1");
	}
	public ModuleConfiguration getDetectorConf(){
		return loadDetectorConf(true, GUIPlaceholder.Pos_C, "1");
	}
	public ModuleConfiguration getExperimentConf(){
		return loadExperimentConf(true, GUIPlaceholder.Pos_H, "1");
	}
	public ModuleConfiguration getImageEnvConf(){
		return loadImageEnvConf(true, GUIPlaceholder.Pos_A, "1");
	}
	public ModuleConfiguration getLightSrcConf(){
		return loadLightSrcConf(true, GUIPlaceholder.Pos_D, "1");
	}
	public ModuleConfiguration getSampleConf(){
		return loadSampleConf(true, GUIPlaceholder.Pos_G, "1");
	}
	public ModuleConfiguration getLightPathConf(){
		return loadLightPathConf(true, GUIPlaceholder.Pos_F, "1");
	}


}
