package com.unister.semweb.apiontology.frontend;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Logo implements ServletContextListener {

	private static final transient Logger logger = LoggerFactory.getLogger(Logo.class);

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		logger.info("");
		logger.info("");
		logger.info("");
		logger.info("                                            __           ___                                ");
		logger.info("                         __                /\\ \\__       /\\_ \\                               ");
		logger.info("           __     _____ /\\_\\    ___     ___\\ \\ ,_\\   ___\\//\\ \\     ___      __    __  __    ");
		logger.info("         /'__`\\  /\\ '__`\\/\\ \\  / __`\\ /' _ `\\ \\ \\/  / __`\\\\ \\ \\   / __`\\  /'_ `\\ /\\ \\/\\ \\   ");
		logger.info("        /\\ \\L\\.\\_\\ \\ \\L\\ \\ \\ \\/\\ \\L\\ \\/\\ \\/\\ \\ \\ \\_/\\ \\L\\ \\\\_\\ \\_/\\ \\L\\ \\/\\ \\L\\ \\\\ \\ \\_\\ \\  ");
		logger.info("        \\ \\__/.\\_\\\\ \\ ,__/\\ \\_\\ \\____/\\ \\_\\ \\_\\ \\__\\ \\____//\\____\\ \\____/\\ \\____ \\\\/`____ \\ ");
		logger.info("         \\/__/\\/_/ \\ \\ \\/  \\/_/\\/___/  \\/_/\\/_/\\/__/\\/___/ \\/____/\\/___/  \\/___L\\ \\`/___/> \\");
		logger.info("                    \\ \\_\\                                                   /\\____/   /\\___/");
		logger.info("                     \\/_/                                                   \\_/__/    \\/__/ ");
		logger.info("                                  __                               ");
		logger.info("                                 /\\ \\                              ");
		logger.info("                                 \\_\\ \\     __    ___ ___     ___   ");
		logger.info("                                 /'_` \\  /'__`\\/' __` __`\\  / __`\\ ");
		logger.info("                                /\\ \\L\\ \\/\\  __//\\ \\/\\ \\/\\ \\/\\ \\L\\ \\");
		logger.info("                                \\ \\___,_\\ \\____\\ \\_\\ \\_\\ \\_\\ \\____/");
		logger.info("                                 \\/__,_ /\\/____/\\/_/\\/_/\\/_/\\/___/ ");
		logger.info("");
		logger.info("");
		logger.info("");
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// TODO Auto-generated method stub

	}

}
