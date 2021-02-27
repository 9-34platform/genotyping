package com.example.demo.tools;

import com.example.demo.Remote;

public class Samtools {
    public static void view(int i) throws Exception {
        String cmd = "/data/home/vip494/samtools-1.11/samtools view -Sb " + "~/user0/bam/"+i+".sam > " + "~/user0/bam/"+i+".bam";
        Remote.connect(cmd);
    }
    public static void sort(int i) throws Exception {
        String cmd = "/data/home/vip494/samtools-1.11/samtools sort -o " + "~/user0/bam/"+i+".sorted.bam " + "~/user0/bam/"+i+".bam";
        Remote.connect(cmd);
    }
    public static void index(int i) throws Exception {
        String cmd = "/data/home/vip494/samtools-1.11/samtools index " + "~/user0/bam/"+i+".sorted.bam";
        Remote.connect(cmd);
    }
}
