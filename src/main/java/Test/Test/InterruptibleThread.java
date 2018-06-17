package Test.Test;

public class InterruptibleThread extends Thread {
		protected volatile boolean running = true;
		protected volatile boolean mainLoopExited = false;

		@Override
		public void interrupt() {
			running = false;
			while(!mainLoopExited) {
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
