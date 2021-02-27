package com.example.demo.tools;

import com.example.demo.Remote;

public class Bwa {
    public static void mem(String filename1, String filename2, int i) throws Exception {
        String cmd = "/data/home/vip494/bwa-0.7.17/bwa mem -t 1 -M ~/Zea_mays.AGPv4.dna.chromosome.7.fa " +"~/user0/fq/"+filename1 + " " + "~/user0/fq/"+filename2 + " > " + "~/user0/bam/"+i+".sam";
        Remote.connect(cmd);
    }
}
