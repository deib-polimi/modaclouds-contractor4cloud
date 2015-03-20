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

//this class is used to create connection to AMPL server (wrapper)
public abstract class SshConnector {
	
	// this object runs bash-script on AMPL server
	private ExecSSH newExecSSH;
	
	// this object uploads files on AMPL server
	private ScpTo newScpTo;
	
	// this block downloads logs and results of AMPL
	private ScpFrom newScpFrom;
	
	public SshConnector() {
		newExecSSH = new ExecSSH();
		newScpTo = new ScpTo();
		newScpFrom = new ScpFrom();
	}
	
	private void sendFile(String localFile, String remoteFile) throws Exception {
		newScpTo.sendfile(localFile, remoteFile);
	}
	
	public void sendFileToWorkingDir(String file) throws Exception {
		sendFile(file, Configuration.RUN_WORKING_DIRECTORY + "/" + file);
		fixFile(Configuration.RUN_WORKING_DIRECTORY, file);
	}
	
	public void exec(String command) {
		newExecSSH.mainExec(command);
	}
	
	private void receiveFile(String localFile, String remoteFile) throws Exception {
		newScpFrom.receivefile(localFile, remoteFile);
	}
	
	public void receiveFileFromWorkingDir(String file) throws Exception {
		receiveFile(file, Configuration.RUN_WORKING_DIRECTORY + "/" + file);
	}
	
	private void fixFile(String folder, String file) {
		exec(String.format("cd %1$s && tr -d '\r' < %2$s > %2$s-bak && mv %2$s-bak %2$s",
						folder,
						file));
	}
	
	// main execution function
	public static void run(int datas) throws Exception {
		switch (Configuration.MATH_SOLVER) {
		case AMPL:
			SshConnectorAMPL.run(datas);
			break;
		case CMPL:
			SshConnectorCMPL.run(datas);
			break;
		}
		
	}
	
	public abstract void execute(int datas) throws Exception;

}
