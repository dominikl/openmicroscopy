package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.microscope.hardware;

import java.util.List;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.microscope.CustomViewProperties;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.microscope.MicroscopeProperties;

import ome.xml.model.Detector;
import ome.xml.model.Filter;
import ome.xml.model.LightSource;
import ome.xml.model.Objective;
/**
* @author Susanne Kunis &nbsp;&nbsp;&nbsp;&nbsp; <a
*         href="mailto:susannekunis@gmail.com">susannekunis@gmail.com</a>
*/
public class StandardMic extends MicroscopeProperties {

	public StandardMic() {
		detectors=this.getMicDetectorList();
		objectives=this.getMicObjectiveList();
		lightSources=this.getMicLightSrcList();
		lightPathObjects=this.getMicLightPathFilterList();
	}
	
	@Override
	protected void initCustomView(){
		view = new CustomViewProperties();
		LOGGER.info("[VIEW_PROP] Load Standard Mic view");
		view.setMicName(UNIVERSAL);
		view.setMicDesc(" ");
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
	@Override
	protected List<Object> getMicLightPathFilterList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<LightSource> getMicLightSrcList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<Detector> getMicDetectorList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<Objective> getMicObjectiveList() {
		// TODO Auto-generated method stub
		return null;
	}

}
