package features;

import common.IntPair;
import common.JobBuilder;
import common.NcdcRecordParser;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.io.IOException;

public class MaxTemperatureUsingSecondarySort extends Configured implements Tool {

    static class MaxTemperatureMapper extends Mapper<LongWritable, Text, IntPair, NullWritable> {
        private NcdcRecordParser parser = new NcdcRecordParser();

        // 연도 + 기온 으로 키 값 생성
        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            parser.parse(value);
            if (parser.isValidTemperature()) {
                context.write(new IntPair(parser.getYearInt(), parser.getAirTemperature()), NullWritable.get());
            }

        }
    }

    static class MaxTemperatureReducer extends Reducer<IntPair, NullWritable, IntPair, NullWritable> {
        @Override
        protected void reduce(IntPair key, Iterable<NullWritable> values, Context context) throws IOException, InterruptedException {
            context.write(key, NullWritable.get());
        }
    }

    public static class FirstPartitioner extends Partitioner<IntPair, NullWritable> {
        @Override
        public int getPartition(IntPair key, NullWritable value, int numPartitions) {
            // 섞기 위해 127 곱한다
            System.out.printf("numPartitions : %d, first value of key : %d\n",numPartitions,key.getFirst());
            return Math.abs(key.getFirst() * 127) % numPartitions;
        }
    }

    public static class KeyComparator extends WritableComparator {
        protected KeyComparator() {
            super(IntPair.class, true);
        }

        @Override
        public int compare(WritableComparable w1, WritableComparable w2) {
            IntPair ip1 = (IntPair) w1;
            IntPair ip2 = (IntPair) w2;
            int cmp = IntPair.compare(ip1.getFirst(), ip2.getFirst());
            if (cmp != 0) {
                // 연도 기준 오름차순
                return cmp;
            }
            // 기온 기준 내림차순
            return -IntPair.compare(ip1.getSecond(), ip2.getSecond()); // reverse
        }
    }

    public static class GroupComparator extends WritableComparator {
        protected GroupComparator() {
            super(IntPair.class, true);
        }

        // 연도 기준으로 오름차순 -> 연도 기준 그룹핑
        @Override
        public int compare(WritableComparable w1, WritableComparable w2) {
            IntPair ip1 = (IntPair) w1;
            IntPair ip2 = (IntPair) w2;
            return IntPair.compare(ip1.getFirst(), ip2.getFirst());
        }
    }

    @Override
    public int run(String[] args) throws Exception {
        Job job = JobBuilder.parseInputAndOutput(this, getConf(), args);
        if (job == null) {
            return -1;
        }

        job.setMapperClass(MaxTemperatureMapper.class);
        // set partitioner
        job.setPartitionerClass(FirstPartitioner.class);
        job.setSortComparatorClass(KeyComparator.class);
        job.setGroupingComparatorClass(GroupComparator.class);
        job.setReducerClass(MaxTemperatureReducer.class);
        job.setOutputKeyClass(IntPair.class);
        job.setOutputValueClass(NullWritable.class);

        return job.waitForCompletion(true) ? 0 : 1;
    }

    public static void main(String[] args) throws Exception {
        int exitCode = ToolRunner.run(new MaxTemperatureUsingSecondarySort(), args);
        System.exit(exitCode);
    }
}


