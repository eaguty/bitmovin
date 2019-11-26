package encoding;

import com.bitmovin.api.sdk.BitmovinApi;
import com.bitmovin.api.sdk.common.BitmovinException;
import com.bitmovin.api.sdk.model.AacAudioConfiguration;
import com.bitmovin.api.sdk.model.AclEntry;
import com.bitmovin.api.sdk.model.AclPermission;
import com.bitmovin.api.sdk.model.AudioAdaptationSet;
import com.bitmovin.api.sdk.model.AudioMediaInfo;
import com.bitmovin.api.sdk.model.CloudRegion;
import com.bitmovin.api.sdk.model.CodecConfiguration;
import com.bitmovin.api.sdk.model.DashFmp4Representation;
import com.bitmovin.api.sdk.model.DashManifest;
import com.bitmovin.api.sdk.model.DashRepresentationType;
import com.bitmovin.api.sdk.model.Encoding;
import com.bitmovin.api.sdk.model.EncodingOutput;
import com.bitmovin.api.sdk.model.Fmp4Muxing;
import com.bitmovin.api.sdk.model.GcsInput;
import com.bitmovin.api.sdk.model.GcsOutput;
import com.bitmovin.api.sdk.model.H264VideoConfiguration;
import com.bitmovin.api.sdk.model.HlsManifest;
import com.bitmovin.api.sdk.model.HttpsInput;
import com.bitmovin.api.sdk.model.MuxingStream;
import com.bitmovin.api.sdk.model.Output;
import com.bitmovin.api.sdk.model.Period;
import com.bitmovin.api.sdk.model.ProfileH264;
import com.bitmovin.api.sdk.model.S3Input;
import com.bitmovin.api.sdk.model.S3Output;
import com.bitmovin.api.sdk.model.StartEncodingRequest;
import com.bitmovin.api.sdk.model.Status;
import com.bitmovin.api.sdk.model.Stream;
import com.bitmovin.api.sdk.model.StreamInfo;
import com.bitmovin.api.sdk.model.StreamInput;
import com.bitmovin.api.sdk.model.StreamSelectionMode;
import com.bitmovin.api.sdk.model.Task;
import com.bitmovin.api.sdk.model.TsMuxing;
import com.bitmovin.api.sdk.model.VideoAdaptationSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class manifestDash {
	
	private static BitmovinApi bitmovinApi;

	public static void main(String[] args) throws BitmovinException, InterruptedException {
		bitmovinApi = BitmovinApi.builder().withApiKey("213e5451-f908-485e-bc3e-07ecd5981f34").build();

        //create the input
        S3Input input = new S3Input();
        input.setName("S3 Input");
        input.setAccessKey("access-key");
        input.setSecretKey("secret-key");
        input.setBucketName("bucket-name");
        
        input = bitmovinApi.encoding.inputs.s3.create(input);

        //create the output
        S3Output output = new S3Output();
        output.setAccessKey("access-key");
        output.setSecretKey("secret-key");
        output.setBucketName("bucket-name");
        
        output = bitmovinApi.encoding.outputs.s3.create(output);
        String outputId = output.getId();

        //create the video and audio codec configurations
        H264VideoConfiguration videoCodecConfiguration1 = new H264VideoConfiguration();
        videoCodecConfiguration1.setName("Getting Started H264 Codec Config 1");
        videoCodecConfiguration1.setBitrate(1500000L);
        videoCodecConfiguration1.setWidth(1024);
        videoCodecConfiguration1.setProfile(ProfileH264.HIGH);
        
        videoCodecConfiguration1 = bitmovinApi.encoding.configurations.video.h264.create(videoCodecConfiguration1);
        
        H264VideoConfiguration videoCodecConfiguration2 = new H264VideoConfiguration();
        videoCodecConfiguration2.setName("Getting Started H264 Codec Config 2");
        videoCodecConfiguration2.setBitrate(1000000L);
        videoCodecConfiguration2.setWidth(768);
        videoCodecConfiguration2.setProfile(ProfileH264.HIGH);
        
        videoCodecConfiguration2 = bitmovinApi.encoding.configurations.video.h264.create(videoCodecConfiguration2);
        
        H264VideoConfiguration videoCodecConfiguration3 = new H264VideoConfiguration();
        videoCodecConfiguration3.setName("Getting Started H264 Codec Config 3");
        videoCodecConfiguration3.setBitrate(750000L);
        videoCodecConfiguration3.setWidth(640);
        videoCodecConfiguration3.setProfile(ProfileH264.HIGH);
        
        videoCodecConfiguration3 = bitmovinApi.encoding.configurations.video.h264.create(videoCodecConfiguration3);
        
        H264VideoConfiguration videoCodecConfiguration4 = new H264VideoConfiguration();
        videoCodecConfiguration4.setName("Getting Started H264 Codec Config 4");
        videoCodecConfiguration4.setBitrate(550000L);
        videoCodecConfiguration4.setWidth(512);
        videoCodecConfiguration4.setProfile(ProfileH264.HIGH);
        
        videoCodecConfiguration4 = bitmovinApi.encoding.configurations.video.h264.create(videoCodecConfiguration4);
        
        H264VideoConfiguration videoCodecConfiguration5 = new H264VideoConfiguration();
        videoCodecConfiguration5.setName("Getting Started H264 Codec Config 5");
        videoCodecConfiguration5.setBitrate(375000L);
        videoCodecConfiguration5.setWidth(384);
        videoCodecConfiguration5.setProfile(ProfileH264.HIGH);
        
        videoCodecConfiguration5 = bitmovinApi.encoding.configurations.video.h264.create(videoCodecConfiguration5);
        
        H264VideoConfiguration videoCodecConfiguration6 = new H264VideoConfiguration();
        videoCodecConfiguration6.setName("Getting Started H264 Codec Config 6");
        videoCodecConfiguration6.setBitrate(240000L);
        videoCodecConfiguration6.setWidth(384);
        videoCodecConfiguration6.setProfile(ProfileH264.HIGH);
        
        videoCodecConfiguration6 = bitmovinApi.encoding.configurations.video.h264.create(videoCodecConfiguration6);

        AacAudioConfiguration audioCodecConfiguration = new AacAudioConfiguration();
        audioCodecConfiguration.setName("Getting Started Audio Codec Config");
        audioCodecConfiguration.setBitrate(128000L);

        audioCodecConfiguration = bitmovinApi.encoding.configurations.audio.aac.create(audioCodecConfiguration);

        //create the encoding resource
        Encoding encoding = new Encoding();
        encoding.setName("Create Manifest to s3");
        encoding.setCloudRegion(CloudRegion.GOOGLE_EUROPE_WEST_1);
        encoding.setEncoderVersion("2.32.0");

        encoding = bitmovinApi.encoding.encodings.create(encoding);

        //add the video and audio streams to the encoding
        String inputPath = "/demo.mp4";
        
        Stream videoStream1 = new Stream();
        
        StreamInput videoStreamInput1 = new StreamInput();
        videoStreamInput1.setInputId(input.getId());
        videoStreamInput1.setInputPath(inputPath);
        videoStreamInput1.setSelectionMode(StreamSelectionMode.AUTO);
        
        videoStream1.setCodecConfigId(videoCodecConfiguration1.getId());
        videoStream1.addInputStreamsItem(videoStreamInput1);
        
        videoStream1 = bitmovinApi.encoding.encodings.streams.create(encoding.getId(), videoStream1);
        
        Stream videoStream2 = new Stream();
        
        StreamInput videoStreamInput2 = new StreamInput();
        videoStreamInput2.setInputId(input.getId());
        videoStreamInput2.setInputPath(inputPath);
        videoStreamInput2.setSelectionMode(StreamSelectionMode.AUTO);
        
        videoStream2.setCodecConfigId(videoCodecConfiguration2.getId());
        videoStream2.addInputStreamsItem(videoStreamInput2);
        
        videoStream2 = bitmovinApi.encoding.encodings.streams.create(encoding.getId(), videoStream2);
        
        Stream videoStream3 = new Stream();
        
        StreamInput videoStreamInput3 = new StreamInput();
        videoStreamInput3.setInputId(input.getId());
        videoStreamInput3.setInputPath(inputPath);
        videoStreamInput3.setSelectionMode(StreamSelectionMode.AUTO);
        
        videoStream3.setCodecConfigId(videoCodecConfiguration3.getId());
        videoStream3.addInputStreamsItem(videoStreamInput3);
        
        videoStream3 = bitmovinApi.encoding.encodings.streams.create(encoding.getId(), videoStream3);
        
        Stream videoStream4 = new Stream();
        
        StreamInput videoStreamInput4 = new StreamInput();
        videoStreamInput4.setInputId(input.getId());
        videoStreamInput4.setInputPath(inputPath);
        videoStreamInput4.setSelectionMode(StreamSelectionMode.AUTO);
        
        videoStream4.setCodecConfigId(videoCodecConfiguration4.getId());
        videoStream4.addInputStreamsItem(videoStreamInput4);
        
        videoStream4 = bitmovinApi.encoding.encodings.streams.create(encoding.getId(), videoStream4);
        
        Stream videoStream5 = new Stream();
        
        StreamInput videoStreamInput5 = new StreamInput();
        videoStreamInput5.setInputId(input.getId());
        videoStreamInput5.setInputPath(inputPath);
        videoStreamInput5.setSelectionMode(StreamSelectionMode.AUTO);
        
        videoStream5.setCodecConfigId(videoCodecConfiguration5.getId());
        videoStream5.addInputStreamsItem(videoStreamInput5);
        
        videoStream5 = bitmovinApi.encoding.encodings.streams.create(encoding.getId(), videoStream5);
        
        Stream videoStream6 = new Stream();
        
        StreamInput videoStreamInput6 = new StreamInput();
        videoStreamInput6.setInputId(input.getId());
        videoStreamInput6.setInputPath(inputPath);
        videoStreamInput6.setSelectionMode(StreamSelectionMode.AUTO);
        
        videoStream6.setCodecConfigId(videoCodecConfiguration6.getId());
        videoStream6.addInputStreamsItem(videoStreamInput6);
        
        videoStream6 = bitmovinApi.encoding.encodings.streams.create(encoding.getId(), videoStream6);
        
        Stream audioStream = new Stream();
        
        StreamInput audioStreamInput = new StreamInput();
        audioStreamInput.setInputId(input.getId());
        audioStreamInput.setInputPath(inputPath);
        audioStreamInput.setSelectionMode(StreamSelectionMode.AUTO);

        audioStream.setCodecConfigId(audioCodecConfiguration.getId());
        audioStream.addInputStreamsItem(audioStreamInput);

        audioStream = bitmovinApi.encoding.encodings.streams.create(encoding.getId(), audioStream);

        AclEntry aclEntry = new AclEntry();
        aclEntry.setPermission(AclPermission.PRIVATE);
        List<AclEntry> aclEntries = new ArrayList<AclEntry>();
        aclEntries.add(aclEntry);
        
        double segmentLength = 4D;
        String outputPath = "/manifestDash";
        String segmentNaming = "seg_%number%.m4s";
        String initSegmentName = "init.mp4";
        
        Fmp4Muxing videoMuxing1 = new Fmp4Muxing();
        
        MuxingStream muxingStream1 = new MuxingStream();
        muxingStream1.setStreamId(videoStream1.getId());
        
        EncodingOutput videoMuxingOutput1 = new EncodingOutput();
        videoMuxingOutput1.setOutputId(outputId);
        videoMuxingOutput1.setOutputPath(String.format("%s%s", outputPath, "/video/1024_1500000/fmp4/"));
        videoMuxingOutput1.setAcl(aclEntries);
        
        videoMuxing1.setSegmentLength(segmentLength);
        videoMuxing1.setSegmentNaming(segmentNaming);
        videoMuxing1.setInitSegmentName(initSegmentName);
        videoMuxing1.addStreamsItem(muxingStream1);
        videoMuxing1.addOutputsItem(videoMuxingOutput1);
        
        videoMuxing1 = bitmovinApi.encoding.encodings.muxings.fmp4.create(encoding.getId(), videoMuxing1);
        
        Fmp4Muxing videoMuxing2 = new Fmp4Muxing();
        
        MuxingStream muxingStream2 = new MuxingStream();
        muxingStream2.setStreamId(videoStream2.getId());
        
        EncodingOutput videoMuxingOutput2 = new EncodingOutput();
        videoMuxingOutput2.setOutputId(outputId);
        videoMuxingOutput2.setOutputPath(String.format("%s%s", outputPath, "/video/768_1000000/fmp4/"));
        videoMuxingOutput2.setAcl(aclEntries);
        
        videoMuxing2.setSegmentLength(segmentLength);
        videoMuxing2.setSegmentNaming(segmentNaming);
        videoMuxing2.setInitSegmentName(initSegmentName);
        videoMuxing2.addStreamsItem(muxingStream2);
        videoMuxing2.addOutputsItem(videoMuxingOutput2);
        
        videoMuxing2 = bitmovinApi.encoding.encodings.muxings.fmp4.create(encoding.getId(), videoMuxing2);
        
        Fmp4Muxing videoMuxing3 = new Fmp4Muxing();
        
        MuxingStream muxingStream3 = new MuxingStream();
        muxingStream3.setStreamId(videoStream3.getId());
        
        EncodingOutput videoMuxingOutput3 = new EncodingOutput();
        videoMuxingOutput3.setOutputId(outputId);
        videoMuxingOutput3.setOutputPath(String.format("%s%s", outputPath, "/video/640_750000/fmp4/"));
        videoMuxingOutput3.setAcl(aclEntries);
        
        videoMuxing3.setSegmentLength(segmentLength);
        videoMuxing3.setSegmentNaming(segmentNaming);
        videoMuxing3.setInitSegmentName(initSegmentName);
        videoMuxing3.addStreamsItem(muxingStream3);
        videoMuxing3.addOutputsItem(videoMuxingOutput3);
        
        videoMuxing3 = bitmovinApi.encoding.encodings.muxings.fmp4.create(encoding.getId(), videoMuxing3);
        
        Fmp4Muxing videoMuxing4 = new Fmp4Muxing();
        
        MuxingStream muxingStream4 = new MuxingStream();
        muxingStream4.setStreamId(videoStream4.getId());
        
        EncodingOutput videoMuxingOutput4 = new EncodingOutput();
        videoMuxingOutput4.setOutputId(outputId);
        videoMuxingOutput4.setOutputPath(String.format("%s%s", outputPath, "/video/512_550000/fmp4/"));
        videoMuxingOutput4.setAcl(aclEntries);
        
        videoMuxing4.setSegmentLength(segmentLength);
        videoMuxing4.setSegmentNaming(segmentNaming);
        videoMuxing4.setInitSegmentName(initSegmentName);
        videoMuxing4.addStreamsItem(muxingStream4);
        videoMuxing4.addOutputsItem(videoMuxingOutput4);
        
        videoMuxing4 = bitmovinApi.encoding.encodings.muxings.fmp4.create(encoding.getId(), videoMuxing4);
        
        Fmp4Muxing videoMuxing5 = new Fmp4Muxing();
        
        MuxingStream muxingStream5 = new MuxingStream();
        muxingStream5.setStreamId(videoStream5.getId());
        
        EncodingOutput videoMuxingOutput5 = new EncodingOutput();
        videoMuxingOutput5.setOutputId(outputId);
        videoMuxingOutput5.setOutputPath(String.format("%s%s", outputPath, "/video/384_375000/fmp4/"));
        videoMuxingOutput5.setAcl(aclEntries);
        
        videoMuxing5.setSegmentLength(segmentLength);
        videoMuxing5.setSegmentNaming(segmentNaming);
        videoMuxing5.setInitSegmentName(initSegmentName);
        videoMuxing5.addStreamsItem(muxingStream5);
        videoMuxing5.addOutputsItem(videoMuxingOutput5);
        
        videoMuxing5 = bitmovinApi.encoding.encodings.muxings.fmp4.create(encoding.getId(), videoMuxing5);
        
        Fmp4Muxing videoMuxing6 = new Fmp4Muxing();
        
        MuxingStream muxingStream6 = new MuxingStream();
        muxingStream6.setStreamId(videoStream6.getId());
        
        EncodingOutput videoMuxingOutput6 = new EncodingOutput();
        videoMuxingOutput6.setOutputId(outputId);
        videoMuxingOutput6.setOutputPath(String.format("%s%s", outputPath, "/video/384_240000/fmp4/"));
        videoMuxingOutput6.setAcl(aclEntries);
        
        videoMuxing6.setSegmentLength(segmentLength);
        videoMuxing6.setSegmentNaming(segmentNaming);
        videoMuxing6.setInitSegmentName(initSegmentName);
        videoMuxing6.addStreamsItem(muxingStream6);
        videoMuxing6.addOutputsItem(videoMuxingOutput6);
        
        videoMuxing6 = bitmovinApi.encoding.encodings.muxings.fmp4.create(encoding.getId(), videoMuxing6);

        //add the audio muxing to the encoding
        Fmp4Muxing fmp4AudioMuxing = new Fmp4Muxing();
        
        MuxingStream fmp4AudioMuxingStream = new MuxingStream();
        fmp4AudioMuxingStream.setStreamId(audioStream.getId());
        
        EncodingOutput fmp4AudioEncodingOutput = new EncodingOutput();
        fmp4AudioEncodingOutput.setOutputId(outputId);
        fmp4AudioEncodingOutput.setOutputPath(String.format("%s%s", outputPath, "/audio/128000/fmp4/"));
        fmp4AudioEncodingOutput.setAcl(aclEntries);
        
        fmp4AudioMuxing.setSegmentLength(segmentLength);
        fmp4AudioMuxing.setSegmentNaming(segmentNaming);
        fmp4AudioMuxing.setInitSegmentName(initSegmentName);
        fmp4AudioMuxing.addStreamsItem(fmp4AudioMuxingStream);
        fmp4AudioMuxing.addOutputsItem(fmp4AudioEncodingOutput);
        
        fmp4AudioMuxing = bitmovinApi.encoding.encodings.muxings.fmp4.create(encoding.getId(), fmp4AudioMuxing);
        

        //create the manifest
        DashManifest manifest = new DashManifest();
        Period period = new Period();
        
        EncodingOutput encodingOutput = new EncodingOutput();
        encodingOutput.setOutputId(outputId);
        encodingOutput.setOutputPath(outputPath);
        encodingOutput.setAcl(aclEntries);
        
        manifest.setName("manifest.mpd");
        manifest.setOutputs(Arrays.asList(encodingOutput));
        
        manifest = bitmovinApi.encoding.manifests.dash.create(manifest);
        
        period = bitmovinApi.encoding.manifests.dash.periods.create(manifest.getId(), period);
        
        
        VideoAdaptationSet videoAdaptationSet = new VideoAdaptationSet();
        
        AudioAdaptationSet audioAdaptationSet = new AudioAdaptationSet();
        audioAdaptationSet.setLang("en");
        
        videoAdaptationSet = bitmovinApi.encoding.manifests.dash.periods.adaptationsets.video.create(
          manifest.getId(), period.getId(), videoAdaptationSet
        );
        
        audioAdaptationSet = bitmovinApi.encoding.manifests.dash.periods.adaptationsets.audio.create(
          manifest.getId(), period.getId(), audioAdaptationSet
        );
        
        
        DashFmp4Representation audioRepresentation = new DashFmp4Representation();
        audioRepresentation.setType(DashRepresentationType.TEMPLATE);
        audioRepresentation.setEncodingId(encoding.getId());
        audioRepresentation.setMuxingId(fmp4AudioMuxing.getId());
        audioRepresentation.setSegmentPath("audio/128000/fmp4");
        
        bitmovinApi.encoding.manifests.dash.periods.adaptationsets.representations.fmp4.create(
          manifest.getId(), period.getId(), audioAdaptationSet.getId(), audioRepresentation
        );
        
        DashFmp4Representation videoRepresentation1 = new DashFmp4Representation();
        videoRepresentation1.setType(DashRepresentationType.TEMPLATE);
        videoRepresentation1.setEncodingId(encoding.getId());
        videoRepresentation1.setMuxingId(videoMuxing1.getId());
        videoRepresentation1.setSegmentPath("video/1024_1500000/fmp4");
        
        bitmovinApi.encoding.manifests.dash.periods.adaptationsets.representations.fmp4.create(
          manifest.getId(), period.getId(), videoAdaptationSet.getId(), videoRepresentation1
        );
        
        DashFmp4Representation videoRepresentation2 = new DashFmp4Representation();
        videoRepresentation2.setType(DashRepresentationType.TEMPLATE);
        videoRepresentation2.setEncodingId(encoding.getId());
        videoRepresentation2.setMuxingId(videoMuxing2.getId());
        videoRepresentation2.setSegmentPath("video/768_1000000/fmp4");
        
        bitmovinApi.encoding.manifests.dash.periods.adaptationsets.representations.fmp4.create(
          manifest.getId(), period.getId(), videoAdaptationSet.getId(), videoRepresentation2
        );
        
        DashFmp4Representation videoRepresentation3 = new DashFmp4Representation();
        videoRepresentation3.setType(DashRepresentationType.TEMPLATE);
        videoRepresentation3.setEncodingId(encoding.getId());
        videoRepresentation3.setMuxingId(videoMuxing3.getId());
        videoRepresentation3.setSegmentPath("video/640_750000/fmp4");
        
        bitmovinApi.encoding.manifests.dash.periods.adaptationsets.representations.fmp4.create(
          manifest.getId(), period.getId(), videoAdaptationSet.getId(), videoRepresentation3
        );
        
        DashFmp4Representation videoRepresentation4 = new DashFmp4Representation();
        videoRepresentation4.setType(DashRepresentationType.TEMPLATE);
        videoRepresentation4.setEncodingId(encoding.getId());
        videoRepresentation4.setMuxingId(videoMuxing4.getId());
        videoRepresentation4.setSegmentPath("video/512_550000/fmp4");
        
        bitmovinApi.encoding.manifests.dash.periods.adaptationsets.representations.fmp4.create(
          manifest.getId(), period.getId(), videoAdaptationSet.getId(), videoRepresentation4
        );
        
        DashFmp4Representation videoRepresentation5 = new DashFmp4Representation();
        videoRepresentation5.setType(DashRepresentationType.TEMPLATE);
        videoRepresentation5.setEncodingId(encoding.getId());
        videoRepresentation5.setMuxingId(videoMuxing5.getId());
        videoRepresentation5.setSegmentPath("video/384_375000/fmp4");
        
        bitmovinApi.encoding.manifests.dash.periods.adaptationsets.representations.fmp4.create(
          manifest.getId(), period.getId(), videoAdaptationSet.getId(), videoRepresentation5
        );
        
        DashFmp4Representation videoRepresentation6 = new DashFmp4Representation();
        videoRepresentation6.setType(DashRepresentationType.TEMPLATE);
        videoRepresentation6.setEncodingId(encoding.getId());
        videoRepresentation6.setMuxingId(videoMuxing6.getId());
        videoRepresentation6.setSegmentPath("video/384_240000/fmp4");
        
        bitmovinApi.encoding.manifests.dash.periods.adaptationsets.representations.fmp4.create(
          manifest.getId(), period.getId(), videoAdaptationSet.getId(), videoRepresentation6
        );

        //start the encoding
        bitmovinApi.encoding.encodings.start(encoding.getId(), new StartEncodingRequest());

        System.out.println("Encoding started, check your encoding progress here:");  
        System.out.println("https://bitmovin.com/dashboard/getting-started/encoding/ui/5/" + encoding.getId());

        Task encodingStatusTask = bitmovinApi.encoding.encodings.status(encoding.getId());

        //wait for the encoding to be finished
        while (encodingStatusTask.getStatus() != Status.FINISHED && encodingStatusTask.getStatus() != Status.ERROR)
        {
            encodingStatusTask = bitmovinApi.encoding.encodings.status(encoding.getId());
            System.out.println(String.format("Encoding progress: %s", encodingStatusTask.getProgress()));
            Thread.sleep(2500);
        }

        //start and wait for the manifest to be finished
        bitmovinApi.encoding.manifests.dash.start(manifest.getId());
        
        
        Task manifestStatusTask = bitmovinApi.encoding.manifests.dash.status(manifest.getId());
        
        System.out.println("Generating DASH manifest...");
        while (manifestStatusTask.getStatus() != Status.FINISHED && manifestStatusTask.getStatus() != Status.ERROR)
        {
            manifestStatusTask = bitmovinApi.encoding.manifests.dash.status(manifest.getId());
            System.out.println(String.format("Manifest generation status: %s", manifestStatusTask.toString()));
            Thread.sleep(2500);
        }
        

        System.out.println("Everything finished, check your encoding here:");  
        System.out.println("https://bitmovin.com/dashboard/getting-started/encoding/ui/5/" + encoding.getId());

	}

}
