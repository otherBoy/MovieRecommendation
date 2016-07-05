
import java.util.regex.Pattern;

import org.apache.hadoop.mapred.JobConf;

public class Recommender {
	
    public static JobConf config() {
        JobConf conf = new JobConf(Recommender.class);
        conf.setJobName("Recommend");
        conf.addResource("classpath:/hadoop/core-site.xml");
        conf.addResource("classpath:/hadoop/hdfs-site.xml");
        conf.addResource("classpath:/hadoop/mapred-site.xml");
        return conf;
    }

}
