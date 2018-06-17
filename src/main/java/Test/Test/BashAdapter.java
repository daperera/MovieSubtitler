package Test.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public final class BashAdapter {


	/**
	 * Execute a command using bash interpreter
	 * The output of the command is capture in a string and is send as the 
	 * return value of the function.
	 * This function wait for the command to terminate before returning.
	 * 
	 * @param cmd bash command as a string
	 * @return output of the command
	 */
	public static String executeAndWait(final String cmd) {
		String result = null;
		try {
			Process p = new ProcessBuilder("bash.exe", "-c", cmd).start();
			ThreadMessageListener listener = new BashAdapter().new ThreadMessageListener(p.getInputStream());
			listener.start();
			p.waitFor();
			result =  listener.getOutput(); // get output
			listener.terminate(); // stop thread
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		return result;
	}


	/**
	 * Execute a command using bash interpreter
	 * This function does not wait for the command to terminate
	 * and returns immediately.
	 * 
	 * @param cmd bash command as a string
	 */
	public static InterruptibleThread execute(final String cmd) {
		InterruptibleThread t = new InterruptibleThread() {
			@Override
			public void run() {
				try {
					Process p = new ProcessBuilder("bash.exe", "-c", cmd).start();
					while(running) {
						try {
							Thread.sleep(200);
							// test if process has terminated
							try {
								p.exitValue();
								running = false; // if p.exitValue() returns, then p has terminated and we can exit the main loop
							} catch (IllegalThreadStateException itse) {}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				} finally {
					mainLoopExited = true;
				}
			}
		};
		t.start();
		return t;
	}

	private class ThreadMessageListener extends Thread {
		private String output;
		private final BufferedReader reader;
		private volatile boolean running = true;

		public ThreadMessageListener(InputStream in) {
			this.reader = new BufferedReader(new InputStreamReader(in));
			output = "";
		}

		@Override
		public void run() {
			boolean firstMessage = true;
			while(running) {
				String message;
				try {
					while((message = reader.readLine()) != null) {
						if(!firstMessage) output += '\n';
						else firstMessage = false;
						output += message;
						//						System.out.println("message : " + message);
					}
					Thread.sleep(20);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}

		public void terminate() {
			running = false;
		}

		public String getOutput() {
			return output;
		}
	}

}