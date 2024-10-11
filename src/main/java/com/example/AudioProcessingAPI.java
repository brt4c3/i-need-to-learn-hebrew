package com.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.ws.rs.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;

@Path("/train-hebrew")
public class AudioProcessingAPI {

    private final ObjectMapper objectMapper = new ObjectMapper(); // Jackson ObjectMapper

    @POST
    @Path("/process")
    @Consumes("application/json")
    @Produces("application/json")
    public String processAudioFiles(String inputJson) {
        try {
            // Parse the JSON input to a Java object
            AudioProcessingRequest request = objectMapper.readValue(inputJson, AudioProcessingRequest.class);
            
            String trainDir = request.getTrainDir();
            String langDir = request.getLangDir();
            String modelDir = request.getModelDir();
            String praatScript = request.getPraatScript();
            String mongoUri = request.getMongoUri();
            String dbName = request.getDbName();
            String collectionName = request.getCollectionName();

            // Create the shell command to execute
            String command = String.format("for file in %s/*.wav; do "
                    + "output=\"${file%.wav}.wav\";"
                    + "ffmpeg -i \"$file\" -ar 16000 \"$output\";"
                    + "sox \"$output\" \"$output\" norm;"
                    + "done && "
                    + "/opt/kaldi/steps/make_mfcc.sh --nj 4 --mfcc-config conf/mfcc.conf %s exp/make_mfcc/train mfcc && "
                    + "/opt/kaldi/steps/compute_cmvn_stats.sh %s exp/make_mfcc/train mfcc && "
                    + "/opt/kaldi/steps/train_mono.sh --nj 4 --cmd utils/run.pl %s %s exp/mono && "
                    + "/opt/kaldi/steps/train_deltas.sh 2000 10000 %s %s exp/mono_ali exp/tri1 && "
                    + "/opt/kaldi/steps/train_lda_mllt.sh --cmd utils/run.pl 2500 15000 %s %s exp/tri1_ali %s",
                    trainDir, trainDir, langDir, trainDir, langDir, trainDir, langDir, modelDir, modelDir, modelDir);

            // Execute the command
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();

            return "{\"status\":\"success\", \"message\":\"Audio processing completed.\"}";

        } catch (Exception e) {
            e.printStackTrace();
            return "{\"status\":\"error\", \"message\":\"Error occurred during processing.\"}";
        }
    }
}