package Test.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import Test.Test.subtitle.Time;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    public void testExecute()
    {
        String cmd = "mkdir TESTDIR1; cd TESTDIR1; echo 0 > file1; echo 0 > file2; cat file1; ls";
        String output = BashAdapter.executeAndWait(cmd);
        String expectedOutput = "0\nfile1\nfile2";
    	assertEquals( expectedOutput, output );
    }
    
    // WARNING : this test rely on executeAndWait validity !
    public void testExecuteThread()
    {
        // create some file
    	String cmd1 = "mkdir TESTDIR2; cd TESTDIR2; echo 0 > file1; echo 1 > file2;";
        BashAdapter.execute(cmd1);
        
        try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        
        // print content and ls
        String cmd2 = "cd TESTDIR2; ls; cat file1; cat file2";
        String output = BashAdapter.executeAndWait(cmd2); 
        
        String expectedOutput = "file1\nfile2\n0\n1";
    	assertEquals( expectedOutput, output );
    }
    
    public void testTimeHMSMConstructor() {
    	Time t1 = new Time(1, 30, 45, 150);
    	assertEquals( "01:30:45,150", t1.toString() );
    	
    	Time t2 = new Time(105, 65, 65, 1005);
    	assertEquals( "05:05:05,005", t2.toString() );
    }
    
    public void testTimeEquals() {
    	Time t1 = new Time(0,1,1,500);
    	Time t2 = new Time(61500);
    	Time t3 = Time.timeFromSrt("00:01:01,500");
    	Time t4 = Time.timeFromSrt("00:01:01.500");
    	
    	assertEquals( t1, t2 );
    	assertEquals( t1, t3 );
    	assertEquals( t1, t4 );
    }
    
    
    public void testFindAudioSample() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    	
    	// load private method with reflection
    	Method method = SubtitleSampleExtractor.class.getDeclaredMethod("findAudioSample", String.class);
        method.setAccessible(true);
    	
    	
    	// test with video
    	int audioSample1 = (int)method.invoke(new SubtitleSampleExtractor(), "data/video/chihaya.mkv");
    	assertEquals( 44100, audioSample1 );
    	int audioSample2 = (int)method.invoke(new SubtitleSampleExtractor(),"data/video/Paterson.2016.1080p.BRRip.x264.AAC-ETRG.mp4");
    	assertEquals( 48000, audioSample2 );
    	
    	// test with audio
    	//int audioSample3 = (int)method.invoke(new SubtitleSampleExtractor(),"data/audio/out001.wav");
    	//assertEquals( 48000, audioSample3 );
    	
    }
    
    public void testFindDuration() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    	
    	// load private method with reflection
    	Method method = SubtitleSampleExtractor.class.getDeclaredMethod("findDuration", String.class);
        method.setAccessible(true);
    	
    	// test with video
    	Time t1 = (Time)method.invoke(new SubtitleSampleExtractor(), "data/video/chihaya.mkv");
    	assertEquals( new Time(0, 22, 52, 7), t1 );
    	Time t2 = (Time)method.invoke(new SubtitleSampleExtractor(), "data/video/Paterson.2016.1080p.BRRip.x264.AAC-ETRG.mp4");
    	assertEquals( new Time(1, 57, 58, 15), t2 );
    	
    	// test with audio
    	//Time t3 = (Time)method.invoke(new SubtitleSampleExtractor(), "data/audio/out001.wav");
    	//assertEquals( new Time(0, 0, 55, 0), t3 );
    }
    
    public void testShift() {
    	Time t1 = Time.timeFromSrt("00:01:56,518");
    	t1.shift(136);
    	Time t2 = Time.timeFromSrt("00:01:56,654");
    	assertEquals(t2, t1);
    	
    	Time t3 = Time.timeFromSrt("00:02:04,651");
    	t3.shift(100);
    	Time t4 = Time.timeFromSrt("00:02:04,751");
    	assertEquals(t4, t3);
    }
}
