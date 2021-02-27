package com.example.demo.tools;

import com.example.demo.Remote;

public class Bam {

    public static void pipe (String fileName1, String fileName2, int i) throws Exception {
        String cmd = "/data/home/vip494/bwa-0.7.17/bwa mem -t 1 -M ~/Zea_mays.AGPv4.dna.chromosome.7.fa " +
                "~/user0/fq/" + fileName1 + " ~/user0/fq/" + fileName2 +
                " | /data/home/vip494/samtools-1.11/samtools view -Sb | /data/home/vip494/samtools-1.11/samtools sort -o " +
                "~/user0/bam/" + i + ".sorted.bam";
        Remote.connect(cmd);
    }

    public static void index (int i) throws Exception {
        String cmd = "/data/home/vip494/samtools-1.11/samtools index " + "~/user0/bam/" + i + ".sorted.bam";
        Remote.connect(cmd);
    }

}
