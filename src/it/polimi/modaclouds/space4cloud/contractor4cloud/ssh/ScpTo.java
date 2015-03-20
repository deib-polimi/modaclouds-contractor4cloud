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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

//this class is used to upload files on AMPL server
public class ScpTo {
	// main execution function
	// coping LFile on local machine in RFile on AMPL server
	public void sendfile(String LFile, String RFile) {
		
		if (Configuration.isRunningLocally())
			try {
				localSendfile(LFile, RFile);
				return;
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
		
		FileInputStream fis = null;
		try {
			String lfile = LFile;
			String rfile = RFile;

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

			boolean ptimestamp = true;
			// exec 'scp -t rfile' remotely
			String command = "scp " + (ptimestamp ? "-p" : "") + " -t " + rfile;
			Channel channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand(command);
			// get I/O streams for remote scp
			OutputStream out = channel.getOutputStream();
			InputStream in = channel.getInputStream();
			// connecting channel
			channel.connect();

			if (checkAck(in) != 0) {
				System.exit(0);
			}

			File _lfile = new File(lfile); 

			if (ptimestamp) {
				command = "T" + (_lfile.lastModified() / 1000) + " 0";
				command += (" " + (_lfile.lastModified() / 1000) + " 0\n");
				out.write(command.getBytes());
				out.flush();
				if (checkAck(in) != 0) {
					System.exit(0);
				}
			}
			// send "C0644 filesize filename", where filename should not include
			// '/'
			long filesize = _lfile.length();
			command = "C0644 " + filesize + " ";
			if (lfile.lastIndexOf('/') > 0) {
				command += lfile.substring(lfile.lastIndexOf('/') + 1);
			} else {
				command += lfile;
			}
			command += "\n";
			out.write(command.getBytes());
			out.flush();
			if (checkAck(in) != 0) {
				System.exit(0);
			}
			// send a content of lfile
			fis = new FileInputStream(lfile);
			byte[] buf = new byte[1024];
			while (true) {
				int len = fis.read(buf, 0, buf.length);
				if (len <= 0)
					break;
				out.write(buf, 0, len);
			}
			fis.close();
			fis = null;
			buf[0] = 0;
			out.write(buf, 0, 1);
			out.flush();
			if (checkAck(in) != 0) {
				System.exit(0);
			}
			out.close();

			channel.disconnect();
			session.disconnect();

		} catch (Exception e) {
			e.printStackTrace();
			try {
				if (fis != null)
					fis.close();
			} catch (Exception ee) {
			}
		}
	}

	static int checkAck(InputStream in) throws IOException {
		int b = in.read();
		// b may be 0 for success,
		// 1 for error,
		// 2 for fatal error,
		// -1
		if (b == 0)
			return b;
		if (b == -1)
			return b;

		if (b == 1 || b == 2) {
			StringBuffer sb = new StringBuffer();
			int c;
			do {
				c = in.read();
				sb.append((char) c);
			} while (c != '\n');
			if (b == 1) { // error
				System.out.print(sb.toString());
			}
			if (b == 2) { // fatal error
				System.out.print(sb.toString());
			}
		}
		return b;
	}

//	public static abstract class MyUserInfo implements UserInfo,
//			UIKeyboardInteractive {
//		public String getPassword() {
//			return null;
//		}
//
//		public boolean promptYesNo(String str) {
//			return false;
//		}
//
//		public String getPassphrase() {
//			return null;
//		}
//
//		public boolean promptPassphrase(String message) {
//			return false;
//		}
//
//		public boolean promptPassword(String message) {
//			return false;
//		}
//
//		public void showMessage(String message) {
//		}
//
//		public String[] promptKeyboardInteractive(String destination,
//				String name, String instruction, String[] prompt, boolean[] echo) {
//			return null;
//		}
//	}
	
	public void localSendfile(String LFile, String RFile) throws FileNotFoundException {
		if (!new File(LFile).exists())
			throw new FileNotFoundException("File " + LFile + " not found!");
		
		ExecSSH ex = new ExecSSH();
		
		if (new File(RFile).exists() && new File(RFile).isDirectory() && !RFile.endsWith(File.separator))
			RFile = RFile + File.separator;
		
		String command = String.format("cp %s %s", LFile, RFile);
		ex.localExec(command);
		
	}
}
