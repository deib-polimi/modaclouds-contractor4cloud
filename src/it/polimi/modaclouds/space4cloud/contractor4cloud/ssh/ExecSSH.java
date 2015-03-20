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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

//this class allows to execute commands on AMPL server
public class ExecSSH {
	
	public List<String> mainExec(String command) {
		if (Configuration.isRunningLocally())
			return localExec(command);
		
		List<String> res = new ArrayList<String>();
		try {
			// creating session with username, server's address and port (22 by
			// default)
			JSch jsch = new JSch();
			Session session = jsch.getSession(Configuration.SSH_USER_NAME, Configuration.SSH_HOST, 22);
			session.setPassword(Configuration.SSH_PASSWORD);

//			// this class sets visual forms for interactions with users
//			// required by implementation
//			UserInfo ui = new MyUserInfo() {
//				public void showMessage(String message) {
//					JOptionPane.showMessageDialog(null, message);
//				}
//
//				public boolean promptYesNo(String message) {
//					Object[] options = { "yes", "no" };
//					int foo = JOptionPane.showOptionDialog(null, message,
//							"Warning", JOptionPane.DEFAULT_OPTION,
//							JOptionPane.WARNING_MESSAGE, null, options,
//							options[0]);
//					return foo == 0;
//				}
//			};
//			session.setUserInfo(ui);

			// disabling of certificate checks
			session.setConfig("StrictHostKeyChecking", "no");
			// creating connection
			session.connect();

			// creating channel in execution mod
			Channel channel = session.openChannel("exec");
			// sending command which runs bash-script in UploadPath directory
			((ChannelExec) channel).setCommand(command);
			// taking input stream
			channel.setInputStream(null);
			((ChannelExec) channel).setErrStream(System.err);
			InputStream in = channel.getInputStream();
			// connecting channel
			channel.connect();
			// read buffer
			byte[] tmp = new byte[1024];

			// reading channel while server responses smth or until it does not
			// close connection
			while (true) {
				while (in.available() > 0) {
					int i = in.read(tmp, 0, 1024);
					if (i < 0)
						break;
					res.add(new String(tmp, 0, i));
				}
				if (channel.isClosed()) {
					res.add("exit-status: " + channel.getExitStatus());
					break;
				}
				try {
					Thread.sleep(1000);
				} catch (Exception ee) {
				}
			}
			// closing connection
			channel.disconnect();
			session.disconnect();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
	
	public List<String> localExec(String command) {
		List<String> res = new ArrayList<String>();
		ProcessBuilder pb = new ProcessBuilder(command.split(" "));
		pb.redirectErrorStream(true);
		try {
			Process p = pb.start();
			BufferedReader stream = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = stream.readLine(); 
			while (line != null) {
				res.add(line);
				line = stream.readLine();
			}
			stream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
}
