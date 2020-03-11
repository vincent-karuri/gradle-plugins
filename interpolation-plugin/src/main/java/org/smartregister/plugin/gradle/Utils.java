package org.smartregister.plugin.gradle;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

/**
 * Created by Vincent Karuri on 11/03/2020
 */
public class Utils {

	public static Logger getLogger(Class clazz) {
		Logger logger = Logger.getLogger(clazz);
		BasicConfigurator.configure();
		return logger;
	}
}
