package encoding;

import java.nio.file.Paths;

import com.bitmovin.api.sdk.BitmovinApi;
import com.bitmovin.api.sdk.common.BitmovinException;
import com.bitmovin.api.sdk.model.AacAudioConfiguration;
import com.bitmovin.api.sdk.model.AclEntry;
import com.bitmovin.api.sdk.model.AclPermission;
import com.bitmovin.api.sdk.model.AutoRepresentation;
import com.bitmovin.api.sdk.model.CodecConfiguration;
import com.bitmovin.api.sdk.model.DashManifest;
import com.bitmovin.api.sdk.model.DashManifestDefault;
import com.bitmovin.api.sdk.model.DashManifestDefaultVersion;
import com.bitmovin.api.sdk.model.Encoding;
import com.bitmovin.api.sdk.model.EncodingOutput;
import com.bitmovin.api.sdk.model.Fmp4Muxing;
import com.bitmovin.api.sdk.model.H264PerTitleConfiguration;
import com.bitmovin.api.sdk.model.H264VideoConfiguration;
import com.bitmovin.api.sdk.model.HlsManifest;
import com.bitmovin.api.sdk.model.HlsManifestDefault;
import com.bitmovin.api.sdk.model.HlsManifestDefaultVersion;
import com.bitmovin.api.sdk.model.Input;
import com.bitmovin.api.sdk.model.MessageType;
import com.bitmovin.api.sdk.model.MuxingStream;
import com.bitmovin.api.sdk.model.Output;
import com.bitmovin.api.sdk.model.PerTitle;
import com.bitmovin.api.sdk.model.PresetConfiguration;
import com.bitmovin.api.sdk.model.S3Input;
import com.bitmovin.api.sdk.model.S3Output;
import com.bitmovin.api.sdk.model.StartEncodingRequest;
import com.bitmovin.api.sdk.model.Status;
import com.bitmovin.api.sdk.model.Stream;
import com.bitmovin.api.sdk.model.StreamInput;
import com.bitmovin.api.sdk.model.StreamMode;
import com.bitmovin.api.sdk.model.StreamSelectionMode;
import com.bitmovin.api.sdk.model.Task;

public class perTitle {
	
	private static BitmovinApi bitmovinApi;

	public static void main(String[] args) throws Exception {
		bitmovinApi =
		        BitmovinApi.builder()
		            .withApiKey("213e5451-f908-485e-bc3e-07ecd5981f34")
		            .build();
		Encoding encoding =
		        createEncoding("Per-Title encoding", "Per-Title encoding with HLS and DASH manifest");
		
		S3Input input = new S3Input();
		input.setName("S3 Input");
		input.setAccessKey("ACCESS-KEY");
		input.setSecretKey("SECRET-KEY");
		input.setBucketName("guty2");
		input = bitmovinApi.encoding.inputs.s3.create(input);
		String inputFilePath = "/boxSubtitles.mp4";
		
		Output output = createS3Output("guty-bitmovin","ACCESS-KEY","SECRET-KEY");
		
		Stream videoStream =
		        createStream(
		            encoding,
		            input,
		            inputFilePath,
		            createBaseH264VideoConfig(),
		            StreamMode.PER_TITLE_TEMPLATE);
		
		createFmp4Muxing(encoding, output, "video/{height}/{bitrate}_{uuid}", videoStream);
		
		AacAudioConfiguration aacConfig = createAacAudioConfig();
	    Stream audioStream =
	        createStream(
	            encoding, input, inputFilePath, aacConfig, StreamMode.STANDARD);

	    createFmp4Muxing(encoding, output, "audio", audioStream);

	    StartEncodingRequest startEncodingRequest = new StartEncodingRequest();
	    startEncodingRequest.setPerTitle(buildPerTitleStartRequest());

	    executeEncoding(encoding, startEncodingRequest);

	    generateDashManifest(encoding, output, "/");
	    generateHlsManifest(encoding, output, "/");

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
	
	private static Stream createStream(
		      Encoding encoding,
		      Input input,
		      String inputPath,
		      CodecConfiguration codecConfiguration,
		      StreamMode streamMode)
		      throws BitmovinException {
		    StreamInput streamInput = new StreamInput();
		    streamInput.setInputId(input.getId());
		    streamInput.setInputPath(inputPath);
		    streamInput.setSelectionMode(StreamSelectionMode.AUTO);

		    Stream stream = new Stream();
		    stream.addInputStreamsItem(streamInput);
		    stream.setCodecConfigId(codecConfiguration.getId());
		    stream.setMode(streamMode);

		    return bitmovinApi.encoding.encodings.streams.create(encoding.getId(), stream);
		  }
	
	private static H264VideoConfiguration createBaseH264VideoConfig() throws BitmovinException {
	    H264VideoConfiguration config = new H264VideoConfiguration();
	    config.setName("Base H.264 video config");
	    config.setPresetConfiguration(PresetConfiguration.VOD_STANDARD);

	    return bitmovinApi.encoding.configurations.video.h264.create(config);
	  }
	
	private static Fmp4Muxing createFmp4Muxing(
		      Encoding encoding, Output output, String outputPath, Stream stream) throws BitmovinException {
		    MuxingStream muxingStream = new MuxingStream();
		    muxingStream.setStreamId(stream.getId());

		    Fmp4Muxing muxing = new Fmp4Muxing();
		    muxing.addOutputsItem(buildEncodingOutput(output, outputPath));
		    muxing.addStreamsItem(muxingStream);
		    muxing.setSegmentLength(4.0);

		    return bitmovinApi.encoding.encodings.muxings.fmp4.create(encoding.getId(), muxing);
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
	    String className = perTitle.class.getSimpleName();
	    return Paths.get("/outputs", className, relativePath).toString();
	  }
	
	private static AacAudioConfiguration createAacAudioConfig() throws BitmovinException {
	    AacAudioConfiguration config = new AacAudioConfiguration();
	    config.setName("AAC 128 kbit/s");
	    config.setBitrate(128_000L);

	    return bitmovinApi.encoding.configurations.audio.aac.create(config);
	  }
	
	private static PerTitle buildPerTitleStartRequest() {
	    H264PerTitleConfiguration perTitleConfiguration = new H264PerTitleConfiguration();
	    perTitleConfiguration.setAutoRepresentations(new AutoRepresentation());

	    PerTitle perTitle = new PerTitle();
	    perTitle.setH264Configuration(perTitleConfiguration);
	    return perTitle;
	  }
	
	
	private static void executeEncoding(Encoding encoding, StartEncodingRequest startEncodingRequest)
		      throws InterruptedException, BitmovinException {
		    bitmovinApi.encoding.encodings.start(encoding.getId(), startEncodingRequest);

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
	
	private static void generateHlsManifest(Encoding encoding, Output output, String outputPath)
		      throws Exception {
		    HlsManifestDefault hlsManifestDefault = new HlsManifestDefault();
		    hlsManifestDefault.setEncodingId(encoding.getId());
		    hlsManifestDefault.addOutputsItem(buildEncodingOutput(output, outputPath));
		    hlsManifestDefault.setName("master.m3u8");
		    hlsManifestDefault.setVersion(HlsManifestDefaultVersion.V1);

		    hlsManifestDefault = bitmovinApi.encoding.manifests.hls.defaultapi.create(hlsManifestDefault);
		    executeHlsManifestCreation(hlsManifestDefault);
		  }
	
	private static void generateDashManifest(Encoding encoding, Output output, String outputPath)
		      throws Exception {
		    DashManifestDefault dashManifestDefault = new DashManifestDefault();
		    dashManifestDefault.setEncodingId(encoding.getId());
		    dashManifestDefault.setManifestName("stream.mpd");
		    dashManifestDefault.setVersion(DashManifestDefaultVersion.V1);
		    dashManifestDefault.addOutputsItem(buildEncodingOutput(output, outputPath));
		    dashManifestDefault =
		        bitmovinApi.encoding.manifests.dash.defaultapi.create(dashManifestDefault);
		    executeDashManifestCreation(dashManifestDefault);
		  }
	
	private static void executeHlsManifestCreation(HlsManifest hlsManifest)
		      throws BitmovinException, InterruptedException {

		    bitmovinApi.encoding.manifests.hls.start(hlsManifest.getId());

		    Task task;
		    do {
		      Thread.sleep(1000);
		      task = bitmovinApi.encoding.manifests.hls.status(hlsManifest.getId());
		    } while (task.getStatus() != Status.FINISHED && task.getStatus() != Status.ERROR);

		    if (task.getStatus() == Status.ERROR) {
		      logTaskErrors(task);
		      throw new RuntimeException("HLS manifest creation failed");
		    }
		    System.out.println("HLS manifest creation finished successfully");
		  }
	
	private static void executeDashManifestCreation(DashManifest dashManifest)
		      throws BitmovinException, InterruptedException {
		    bitmovinApi.encoding.manifests.dash.start(dashManifest.getId());

		    Task task;
		    do {
		      Thread.sleep(1000);
		      task = bitmovinApi.encoding.manifests.dash.status(dashManifest.getId());
		    } while (task.getStatus() != Status.FINISHED && task.getStatus() != Status.ERROR);

		    if (task.getStatus() == Status.ERROR) {
		      logTaskErrors(task);
		      throw new RuntimeException("DASH manifest creation failed");
		    }
		    System.out.println("DASH manifest creation finished successfully");
		  }

	

}
