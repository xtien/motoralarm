package com.loqli.motoralarm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Main {

	private static final Log log = LogFactory.getLog(Main.class);

	public static void main(final String[] args) throws Exception {

		try {
			new MotorAlarm().startIt();
		} catch (Exception e) {
			log.error(e);
			if (e.getCause() != null) {
				log.error(e.getCause());
				if (e.getCause().getCause() != null) {
					log.error(e.getCause().getCause().getMessage());
				}
			}
		}

	}
}
