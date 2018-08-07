package org.openmicroscopy.shoola.util;

import org.apache.log4j.Logger;

public class MonitorAndDebug {
	
	
	
	static public void printConsole(String output)
	{
		System.out.println(output);
	}
	
	static public void printLogger(Logger logger,String output)
	{
		logger.info(output);
	}
	
	

}
