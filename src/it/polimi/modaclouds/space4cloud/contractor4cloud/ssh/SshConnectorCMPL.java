package it.polimi.modaclouds.space4cloud.contractor4cloud.ssh;

import it.polimi.modaclouds.space4cloud.contractor4cloud.Configuration;
import it.polimi.modaclouds.space4cloud.contractor4cloud.Contractor;

public class SshConnectorCMPL extends SshConnector {

	@Override
	public void execute(int datas) {
		exec(String.format("mkdir -p %s", Configuration.RUN_WORKING_DIRECTORY));
		
		for (int i = 1; i <= datas; ++i) {
			sendFileToWorkingDir(Configuration.RUN_DATA_CMPL + "-" + i);
			sendFileToWorkingDir(Configuration.RUN_FILE_CMPL + "-" + i);
			sendFileToWorkingDir(Configuration.DEFAULTS_BASH_CMPL + "-" + i);
			sendFileToWorkingDir(Configuration.RUN_MODEL_CMPL + "-" + i);
		}
		
		for (int i = 1; i <= datas; ++i)
			exec("bash " + Configuration.RUN_WORKING_DIRECTORY + "/" + Configuration.DEFAULTS_BASH_CMPL + "-" + i);

		for (int i = 1; i <= datas; ++i) {
			receiveFileFromWorkingDir(Configuration.RUN_LOG_CMPL + "-" + i);
			receiveFileFromWorkingDir(Configuration.RUN_RES_CMPL + "-" + i);
		}
		
		if (Contractor.removeTempFiles)
			exec(String.format("rm -rf %s", Configuration.RUN_WORKING_DIRECTORY));
	}
	
	public static void run(int datas) {
		SshConnectorCMPL ssh = new SshConnectorCMPL();
		ssh.execute(datas);
	}
	
}
