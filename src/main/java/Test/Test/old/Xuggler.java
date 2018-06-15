package Test.Test.old;

import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.MediaListenerAdapter;
import com.xuggle.mediatool.MediaToolAdapter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.mediatool.event.IAddStreamEvent;
import com.xuggle.mediatool.event.IAudioSamplesEvent;
import com.xuggle.mediatool.event.ICloseEvent;
import com.xuggle.mediatool.event.IOpenCoderEvent;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IStreamCoder;

public class Xuggler {

    public static void main(String[] args) throws Exception {
        String file = "data/video/chihaya.mkv";
        String to = "data/audio/chihaya.mp3";
        convert(file, to);
    }

    public static void convert(String from, final String to) {
        IMediaReader mediaReader = ToolFactory.makeReader(from);
        final int mySampleRate = 44100;
        final int myChannels = 2;

        mediaReader.addListener(new MediaToolAdapter() {

            private IContainer container;
            private IMediaWriter mediaWriter;

            @Override
            public void onOpenCoder(IOpenCoderEvent event) {
                container = event.getSource().getContainer();
                mediaWriter = null;
            }

            @Override
            public void onAudioSamples(IAudioSamplesEvent event) {
            if (container != null) {
                  if (mediaWriter == null) {
                    mediaWriter = ToolFactory.makeWriter(to);

                    mediaWriter.addListener(new MediaListenerAdapter() {

                          @Override
                          public void onAddStream(IAddStreamEvent event) {
                              IStreamCoder streamCoder = event.getSource().getContainer().getStream(event.getStreamIndex()).getStreamCoder();
                              streamCoder.setFlag(IStreamCoder.Flags.FLAG_QSCALE, false);
                              streamCoder.setBitRate(128);
                              streamCoder.setChannels(myChannels);
                              streamCoder.setSampleRate(mySampleRate);
                              streamCoder.setBitRateTolerance(0);
                          }
                      });
                    mediaWriter.addAudioStream(0, 0, myChannels, mySampleRate);
                }
                    mediaWriter.encodeAudio(0, event.getAudioSamples());
                    //System.out.println(event.getTimeStamp() / 1000);
                }
            }

            @Override
            public void onClose(ICloseEvent event) {
                if (mediaWriter != null) {
                    mediaWriter.close();
                }
            }
        });

        while (mediaReader.readPacket() == null) {
        }
    }
}
