package it.polimi.modaclouds.space4cloud.contractor4cloud.ssh;

import it.polimi.modaclouds.space4cloud.contractor4cloud.Configuration;
import it.polimi.modaclouds.space4cloud.contractor4cloud.Contractor;

public class SshConnectorAMPL extends SshConnector {
	
	@Override
	public void execute(int datas) throws Exception {
		exec(String.format("mkdir -p %s", Configuration.RUN_WORKING_DIRECTORY));
		
		for (int i = 1; i <= datas; ++i) {
			sendFileToWorkingDir(Configuration.RUN_DATA + "-" + i);
			sendFileToWorkingDir(Configuration.RUN_FILE + "-" + i);
			sendFileToWorkingDir(Configuration.DEFAULTS_BASH + "-" + i);
		}
		sendFileToWorkingDir(Configuration.RUN_MODEL);
		
		for (int i = 1; i <= datas; ++i)
			exec("bash " + Configuration.RUN_WORKING_DIRECTORY + "/" + Configuration.DEFAULTS_BASH + "-" + i);

		for (int i = 1; i <= datas; ++i) {
			receiveFileFromWorkingDir(Configuration.RUN_LOG + "-" + i);
			receiveFileFromWorkingDir(Configuration.RUN_RES + "-" + i);
		}
		
		if (Contractor.removeTempFiles)
			exec(String.format("rm -rf %s", Configuration.RUN_WORKING_DIRECTORY));
	}
	
	public static void run(int datas) throws Exception {
		SshConnectorAMPL ssh = new SshConnectorAMPL();
		ssh.execute(datas);
	}

}
