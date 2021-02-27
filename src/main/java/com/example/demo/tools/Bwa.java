package com.example.demo.tools;

import com.example.demo.Remote;

public class Bwa {
    public static void bwa(String nameStr, String nameStr1) throws Exception {
        String cmd = "/data/home/vip494/bwa-0.7.17/bwa mem -t 1 -M Zea_mays.AGPv4.dna.chromosome.7.fa " + nameStr + " " + nameStr1 + " > result.sam";
        Remote.connect(cmd);
    }
}
