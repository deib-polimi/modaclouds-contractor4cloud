/**
 * Copyright ${year} deib-polimi
 * Contact: deib-polimi <giovannipaolo.gibilisco@polimi.it>
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package it.polimi.modaclouds.space4cloud.contractor4cloud.ssh;

import it.polimi.modaclouds.space4cloud.contractor4cloud.Configuration;
import it.polimi.modaclouds.space4cloud.contractor4cloud.Contractor;

//this class is used to create connection to AMPL server (wrapper)
public class SshConnector {
	
	// main execution function
	public static void run(int datas) {
		
		// this object runs bash-script on AMPL server
		ExecSSH newExecSSH = new ExecSSH();
		
		newExecSSH.mainExec(String.format("mkdir %s", Configuration.RUN_WORKING_DIRECTORY));
		
		// this object uploads files on AMPL server
		ScpTo newScpTo = new ScpTo();
		for (int i = 1; i <= datas; ++i) {
			newScpTo.sendfile(Configuration.RUN_DATA + "-" + i, Configuration.RUN_WORKING_DIRECTORY + "/" + Configuration.RUN_DATA + "-" + i);
			newScpTo.sendfile(Configuration.RUN_FILE + "-" + i, Configuration.RUN_WORKING_DIRECTORY + "/" + Configuration.RUN_FILE + "-" + i);
			
			newScpTo.sendfile(Configuration.DEFAULTS_BASH + "-" + i, Configuration.RUN_WORKING_DIRECTORY + "/" + Configuration.DEFAULTS_BASH + "-" + i);
			
			newExecSSH.mainExec(
					String.format("cd %1$s && tr -d '\r' < %2$s > %2$s-bak && mv %2$s-bak %2$s",
							Configuration.RUN_WORKING_DIRECTORY,
							Configuration.RUN_DATA + "-" + i));
			newExecSSH.mainExec(
					String.format("cd %1$s && tr -d '\r' < %2$s > %2$s-bak && mv %2$s-bak %2$s",
							Configuration.RUN_WORKING_DIRECTORY,
							Configuration.RUN_FILE + "-" + i));
			newExecSSH.mainExec(
					String.format("cd %1$s && tr -d '\r' < %2$s > %2$s-bak && mv %2$s-bak %2$s",
							Configuration.RUN_WORKING_DIRECTORY,
							Configuration.DEFAULTS_BASH + "-" + i));
		}
		newScpTo.sendfile(Configuration.RUN_MODEL, Configuration.RUN_WORKING_DIRECTORY + "/" + Configuration.RUN_MODEL);
		
		newExecSSH.mainExec(
				String.format("cd %1$s && tr -d '\r' < %2$s > %2$s-bak && mv %2$s-bak %2$s",
						Configuration.RUN_WORKING_DIRECTORY,
						Configuration.RUN_MODEL));
		
		for (int i = 1; i <= datas; ++i)
			newExecSSH.mainExec("bash " + Configuration.RUN_WORKING_DIRECTORY + "/" + Configuration.DEFAULTS_BASH + "-" + i);

		// this block downloads logs and results of AMPL
		ScpFrom newScpFrom = new ScpFrom();
		
		for (int i = 1; i <= datas; ++i) {
			newScpFrom.receivefile(Configuration.RUN_LOG + "-" + i, Configuration.RUN_WORKING_DIRECTORY + "/" + Configuration.RUN_LOG + "-" + i);
			newScpFrom.receivefile(Configuration.RUN_RES + "-" + i, Configuration.RUN_WORKING_DIRECTORY + "/" + Configuration.RUN_RES + "-" + i);
		}
		
		if (Contractor.removeTempFiles)
			newExecSSH.mainExec(String.format("rm -rf %s", Configuration.RUN_WORKING_DIRECTORY));
	}

}
