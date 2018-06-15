package Test.Test;

import java.io.FileInputStream;
import java.io.IOException;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.speech.v1.RecognitionAudio;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.RecognitionConfig.AudioEncoding;
import com.google.cloud.speech.v1.RecognizeResponse;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.SpeechSettings;
//Imports the Google Cloud client library
import com.google.protobuf.ByteString;


public class SpeechRecognizer {

	// request payload size limit 
	public static final long SIZE_LIMIT = 10485760;
	
	// The path to google credentials
	private static final String GOOGLE_CREDENTIAL_PATH = "data/google_credentials/subtitler project.json";
	
	public static RecognizeResponse extractSubtitles(ByteString audioBytes, int sampleRate) throws Exception {
		RecognizeResponse response = null;
		SpeechClient speechClient = null;
		
		try {
			
			// Get credentials
			SpeechSettings settings = authExplicit();
			
			// Instantiates a client
			speechClient = SpeechClient.create(settings);
			
			// Builds the sync recognize request
			RecognitionConfig config = RecognitionConfig.newBuilder()
					.setEncoding(AudioEncoding.LINEAR16)
					.setSampleRateHertz(sampleRate)
					.setLanguageCode("en-US")
					.setEnableWordTimeOffsets(true)
					.build();
			RecognitionAudio audio = RecognitionAudio.newBuilder()
					.setContent(audioBytes)
					.build();

			// Performs speech recognition on the audio file
			response = speechClient.recognize(config, audio);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(speechClient != null)
				speechClient.close();
		}
		
		return response;
	}
	
	
	static SpeechSettings authExplicit() throws IOException {
		CredentialsProvider credentialsProvider = FixedCredentialsProvider.create(ServiceAccountCredentials.fromStream(new FileInputStream(GOOGLE_CREDENTIAL_PATH)));
		SpeechSettings settings = SpeechSettings.newBuilder().setCredentialsProvider(credentialsProvider).build();
		return settings;
	}
	
}