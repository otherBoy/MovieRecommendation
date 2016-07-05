package Recommendation;

import java.io.IOException;
import java.util.*;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

/* Data Preprocessing.
 * The input data is (userId, movieId, rating).
 * After this mapreduce, the output data(key, value) will be (userId, [movieA:rating,movieB:rating]).
 * One record contains a key(userId) and a value(all of movies and rating which the user had watched and marked). */
public class DataPre {

    public static class DataPreMapper extends Mapper<Object, Text, Text, Text> {
        private final static Text k = new Text();
        private final static Text v = new Text();

        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
        	// split the input data
            String[] tokens = Main.DELIMITER.split(value.toString());
            String userId = tokens[0];
            String movieId = tokens[1];
            int rating = Integer.parseInt(tokens[2]);
            k.set(userId);
            v.set(movieId + ":" + rating);
            context.write(k, v);
        }
    }

    public static class DataPreReducer extends Reducer<Text, Text, Text, Text> {
        private final static Text v = new Text();

        public void reduce(Text key, Iterator<Text> values, Context context) throws IOException, InterruptedException {
            StringBuilder movieList = new StringBuilder();
            while (values.hasNext()) {
            	movieList.append("," + values.next());
            }
            v.set(movieList.toString().replaceFirst(",", ""));
            context.write(key, v);
        }
    }

    public static void run(Map<String, String> path) throws IOException {
        JobConf conf = Recommend.config();

        String input = path.get("Step1Input");
        String output = path.get("Step1Output");

        HdfsDAO hdfs = new HdfsDAO(Recommend.HDFS, conf);
//        hdfs.rmr(output);
        hdfs.rmr(input);
        hdfs.mkdirs(input);
        hdfs.copyFile(path.get("data"), input);

        conf.setMapOutputKeyClass(IntWritable.class);
        conf.setMapOutputValueClass(Text.class);

        conf.setOutputKeyClass(IntWritable.class);
        conf.setOutputValueClass(Text.class);

        conf.setMapperClass(Step1_ToItemPreMapper.class);
        conf.setCombinerClass(Step1_ToUserVectorReducer.class);
        conf.setReducerClass(Step1_ToUserVectorReducer.class);

        conf.setInputFormat(TextInputFormat.class);
        conf.setOutputFormat(TextOutputFormat.class);

        FileInputFormat.setInputPaths(conf, new Path(input));
        FileOutputFormat.setOutputPath(conf, new Path(output));

        RunningJob job = JobClient.runJob(conf);
        while (!job.isComplete()) {
            job.waitForCompletion();
        }
    }

}

