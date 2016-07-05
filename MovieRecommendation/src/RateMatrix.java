
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
        
public class RateMatrix {
        
	public static class Map extends Mapper<Object, Text, Text, Text> {
	    private final static Text k = new Text();
	    private Text v = new Text();
        
		public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
			String line = value.toString();
			StringTokenizer token = new StringTokenizer(line, ",", true);
        
			String userId  = token.nextToken();
			String movieId = token.nextToken();
			String score   = token.nextToken();
        
			k.set(movieId);
			v.set(userId + ":" + score);
			context.write(k, v);
		}
	}
 } 