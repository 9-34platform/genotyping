package com.example.demo.tools;

import com.example.demo.connection.Remote;

public class Samtools {
    public static void view() throws Exception {
        String cmd = "/data/home/vip494/samtools-1.11/samtools view -Sb result.sam > result.bam";
        Remote.connect(cmd);
    }
    public static void sort() throws Exception {
        String cmd = "/data/home/vip494/samtools-1.11/samtools sort -o result.sorted.bam result.bam";
        Remote.connect(cmd);
    }
    public static void index() throws Exception {
        String cmd = "/data/home/vip494/samtools-1.11/samtools index result.sorted.bam";
        Remote.connect(cmd);
    }
}
