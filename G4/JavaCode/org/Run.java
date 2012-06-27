package org;

import org.eredlab.g4.rif.server.G4Server;

/**
 * 系统启动
 * @author XiongChun
 * @since 2009-01-13
 */
public class Run {
	public static void main(String[] args) {
		G4Server server = new G4Server(
//				"E:\\G4\\svn\\web\\WebRoot",
				"G:\\tools\\G4Studio_V3.1\\G4Studio_V3.1\\WebRoot",
				"/G4Studio",
				8899
				);
		server.stop();
		try {
			server.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
