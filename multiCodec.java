package encoding;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import com.bitmovin.api.sdk.BitmovinApi;
import com.bitmovin.api.sdk.common.BitmovinException;
import com.bitmovin.api.sdk.model.AacAudioConfiguration;
import com.bitmovin.api.sdk.model.Ac3AudioConfiguration;
import com.bitmovin.api.sdk.model.AclEntry;
import com.bitmovin.api.sdk.model.AclPermission;
import com.bitmovin.api.sdk.model.CodecConfiguration;
import com.bitmovin.api.sdk.model.Encoding;
import com.bitmovin.api.sdk.model.EncodingOutput;
import com.bitmovin.api.sdk.model.H264VideoConfiguration;
import com.bitmovin.api.sdk.model.H265VideoConfiguration;
import com.bitmovin.api.sdk.model.HttpInput;
import com.bitmovin.api.sdk.model.MessageType;
import com.bitmovin.api.sdk.model.Mp4Muxing;
import com.bitmovin.api.sdk.model.MuxingStream;
import com.bitmovin.api.sdk.model.Output;
import com.bitmovin.api.sdk.model.PresetConfiguration;
import com.bitmovin.api.sdk.model.ProgressiveTsMuxing;
import com.bitmovin.api.sdk.model.S3Input;
import com.bitmovin.api.sdk.model.S3Output;
import com.bitmovin.api.sdk.model.StartEncodingRequest;
import com.bitmovin.api.sdk.model.Status;
import com.bitmovin.api.sdk.model.Stream;
import com.bitmovin.api.sdk.model.StreamInput;
import com.bitmovin.api.sdk.model.StreamSelectionMode;
import com.bitmovin.api.sdk.model.Task;

public class multiCodec {
	
	
	private static BitmovinApi bitmovinApi;
	

	public static void main(String[] args) throws BitmovinException, InterruptedException {

		bitmovinApi =BitmovinApi.builder()
			    .withApiKey("213e5451-f908-485e-bc3e-07ecd5981f34")
			    .build();
	    Encoding encoding = createEncoding("Multi codec encoding", "Encoding with H.264, H.265 and VP9");
	    
	    S3Input input = new S3Input();
		input.setName("S3 Input");
		input.setAccessKey("AKIAIIPNGFJ3TAAXV4UA");
		input.setSecretKey("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");
		input.setBucketName("guty2");
		input = bitmovinApi.encoding.inputs.s3.create(input);
		String inputFilePath = "/boxSubtitles.mp4";
		
		Output output = createS3Output("guty-bitmovin","AKIAQQIAEDVWUP34CC7X","UhitJsQN6d2g5vBN/SvspmTnRDJFZ0g5WMNsuUZ3");
		
		// Add an H.264 video stream to the encoding
		H264VideoConfiguration h264Config = createH264VideoConfig();
		Stream h264VideoStream = createStream(encoding, input, inputFilePath, h264Config);
		
		// Add an H.265 video stream to the encoding
		H265VideoConfiguration h265Config = createH265VideoConfig();
		Stream h265VideoStream = createStream(encoding, input, inputFilePath, h265Config);
		
		// Add an AAC audio stream to the encoding
	    AacAudioConfiguration aacConfig = createAacAudioConfig();
	    Stream aacAudioStream = createStream(encoding, input, inputFilePath, aacConfig);
	    
	    // Add an AC3 audio stream to the encoding
	    Ac3AudioConfiguration ac3Config = createAc3AudioConfig();
	    Stream ac3AudioStream = createStream(encoding, input, inputFilePath, ac3Config);
	    
	    // Create an MP4 muxing with the H.264 and AAC streams
	    createMp4Muxing(
	        encoding,
	        output,
	        "mp4-h264-aac",
	        Arrays.asList(h264VideoStream, aacAudioStream),
	        "video.mp4");
	    
	 // Create an MP4 muxing with the H.265 and AC3 streams
	    createMp4Muxing(
	        encoding,
	        output,
	        "mp4-h265-ac3",
	        Arrays.asList(h265VideoStream, ac3AudioStream),
	        "video.mp4");

	    // Create a progressive TS muxing with the H.264 and AAC streams
	    createProgressiveTsMuxing(
	        encoding,
	        output,
	        "progressivets-h264-aac",
	        Arrays.asList(h264VideoStream, aacAudioStream),
	        "video.ts");
	    
	    executeEncoding(encoding);
		
		

	}
	
	private static Encoding createEncoding(String name, String description) throws BitmovinException {
	    Encoding encoding = new Encoding();
	    encoding.setName(name);
	    encoding.setDescription(description);

	    return bitmovinApi.encoding.encodings.create(encoding);
	  }
	
	private static S3Output createS3Output(String bucketName, String accessKey, String secretKey)
			throws BitmovinException {

		S3Output s3Output = new S3Output();
		s3Output.setBucketName(bucketName);
		s3Output.setAccessKey(accessKey);
		s3Output.setSecretKey(secretKey);

		return bitmovinApi.encoding.outputs.s3.create(s3Output);
	}
	
	private static H264VideoConfiguration createH264VideoConfig() throws BitmovinException {
	    H264VideoConfiguration config = new H264VideoConfiguration();
	    config.setName("H.264 1080p 1.5 Mbit/s");
	    config.setPresetConfiguration(PresetConfiguration.VOD_STANDARD);
	    config.setHeight(1080);
	    config.setBitrate(1_500_000L);

	    return bitmovinApi.encoding.configurations.video.h264.create(config);
	  }
	
	private static H265VideoConfiguration createH265VideoConfig() throws BitmovinException {
	    H265VideoConfiguration config = new H265VideoConfiguration();
	    config.setName("H.265 video config");
	    config.setPresetConfiguration(PresetConfiguration.VOD_STANDARD);
	    config.setHeight(1080);
	    config.setBitrate(1_500_000L);

	    return bitmovinApi.encoding.configurations.video.h265.create(config);
	  }
	
	private static AacAudioConfiguration createAacAudioConfig() throws BitmovinException {
	    AacAudioConfiguration config = new AacAudioConfiguration();
	    config.setName("AAC 128 kbit/s");
	    config.setBitrate(128_000L);

	    return bitmovinApi.encoding.configurations.audio.aac.create(config);
	  }
	
	private static Ac3AudioConfiguration createAc3AudioConfig() throws BitmovinException {
	    Ac3AudioConfiguration config = new Ac3AudioConfiguration();
	    config.setName("AC3 128 kbit/s");
	    config.setBitrate(128_000L);

	    return bitmovinApi.encoding.configurations.audio.ac3.create(config);
	  }
	
	private static Mp4Muxing createMp4Muxing(
		      Encoding encoding, Output output, String outputPath, List<Stream> streams, String fileName)
		      throws BitmovinException {
		    Mp4Muxing muxing = new Mp4Muxing();
		    muxing.addOutputsItem(buildEncodingOutput(output, outputPath));
		    muxing.setFilename(fileName);

		    for (Stream stream : streams) {
		      MuxingStream muxingStream = new MuxingStream();
		      muxingStream.setStreamId(stream.getId());
		      muxing.addStreamsItem(muxingStream);
		    }

		    return bitmovinApi.encoding.encodings.muxings.mp4.create(encoding.getId(), muxing);
		  }
	
	private static ProgressiveTsMuxing createProgressiveTsMuxing(
		      Encoding encoding, Output output, String outputPath, List<Stream> streams, String fileName)
		      throws BitmovinException {
		    ProgressiveTsMuxing muxing = new ProgressiveTsMuxing();
		    muxing.addOutputsItem(buildEncodingOutput(output, outputPath));
		    muxing.setFilename(fileName);

		    for (Stream stream : streams) {
		      MuxingStream muxingStream = new MuxingStream();
		      muxingStream.setStreamId(stream.getId());
		      muxing.addStreamsItem(muxingStream);
		    }

		    return bitmovinApi.encoding.encodings.muxings.progressiveTs.create(encoding.getId(), muxing);
		  }
	
	
	private static Stream createStream(
		      Encoding encoding, S3Input input, String inputPath, CodecConfiguration codecConfiguration)
		      throws BitmovinException {
		    StreamInput streamInput = new StreamInput();
		    streamInput.setInputId(input.getId());
		    streamInput.setInputPath(inputPath);
		    streamInput.setSelectionMode(StreamSelectionMode.AUTO);

		    Stream stream = new Stream();
		    stream.addInputStreamsItem(streamInput);
		    stream.setCodecConfigId(codecConfiguration.getId());

		    return bitmovinApi.encoding.encodings.streams.create(encoding.getId(), stream);
		  }
	
	private static EncodingOutput buildEncodingOutput(Output output, String outputPath) {
	    AclEntry aclEntry = new AclEntry();
	    aclEntry.setPermission(AclPermission.PRIVATE);

	    EncodingOutput encodingOutput = new EncodingOutput();
	    encodingOutput.setOutputPath(buildAbsolutePath(outputPath));
	    encodingOutput.setOutputId(output.getId());
	    encodingOutput.addAclItem(aclEntry);
	    return encodingOutput;
	  }
	
	public static String buildAbsolutePath(String relativePath) {
	    String className = main.class.getSimpleName();
	    return Paths.get("/outputs", className, relativePath).toString();
	  }
	
	private static void executeEncoding(Encoding encoding)
		      throws InterruptedException, BitmovinException {
		    bitmovinApi.encoding.encodings.start(encoding.getId(), new StartEncodingRequest());

		    Task task;
		    do {
		      Thread.sleep(5000);
		      task = bitmovinApi.encoding.encodings.status(encoding.getId());
		      System.out.println("encoding status is " + task.getStatus() +" "+ task.getProgress());
		    } while (task.getStatus() != Status.FINISHED && task.getStatus() != Status.ERROR);

		    if (task.getStatus() == Status.ERROR) {
		      logTaskErrors(task);
		      throw new RuntimeException("Encoding failed");
		    }
		    System.out.println("encoding finished successfully");
		  }
	
	private static void logTaskErrors(Task task) {
	    task.getMessages().stream()
	        .filter(msg -> msg.getType() == MessageType.ERROR)
	        .forEach(msg -> System.out.println(msg.getText()));
	  }

}
