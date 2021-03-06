package com.zhoujie.test;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class TextMutiMapTest extends Configured implements Tool {

    public static class MutiMapMaper extends
            AbstractMutilOutputMapper<Text, Text> {

        @Override
        protected String getBaseOutputPath(String dirname) {
            StringBuilder sb = new StringBuilder();
            sb.append(dirname.toString());
            sb.append(Path.SEPARATOR);
            sb.append(uniquePrefix_);
            return sb.toString();
        }

        @Override
        protected void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {
            String line = value.toString();
            String[] lineArr = line.split(",");
            if (lineArr[0].equals("beijing")) {
                super.mos_.write("beijing", NullWritable.get(), line,
                        getBaseOutputPath("beijing"));
            } else if (lineArr[0].equals("shanghai")) {
                super.mos_.write("shanghai", NullWritable.get(), line,
                        getBaseOutputPath("shanghai"));
            }

        }

    }

    public int run(String[] args) throws Exception {

        if (args.length != 2) {
            System.err
                    .println(TextMutiMapTest.class.getSimpleName() + "<in> <out>");
            return 1;
        }

        Configuration conf = this.getConf();
        Job job = new Job(conf);
        job.setJarByClass(TextMutiMapTest.class);
        job.setJobName("MutiMapTest");
        job.setMapperClass(MutiMapMaper.class);
        job.setNumReduceTasks(0);

        // job.setInputFormatClass(LzoLubanLogProtobufB64LineInputFormat.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        job.setMapOutputKeyClass(NullWritable.class);
        job.setMapOutputValueClass(Text.class);

        FileInputFormat.setInputPaths(job, args[0]);
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        MultipleOutputs.addNamedOutput(job, "beijing", TextOutputFormat.class,
                NullWritable.class, Text.class);
        MultipleOutputs.addNamedOutput(job, "shanghai", TextOutputFormat.class,
                NullWritable.class, Text.class);

        return job.waitForCompletion(true) ? 0 : 1;
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            String usage = TextMutiMapTest.class.getSimpleName() + "<in> <out>";
            System.out.println(usage);
            return;
        }
        int res = ToolRunner.run(new TextMutiMapTest(), args);
        System.exit(res);
    }
}
