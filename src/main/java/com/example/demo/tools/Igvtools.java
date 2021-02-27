package com.example.demo.tools;

import com.example.demo.Remote;

public class Igvtools {
    public static void count(int i) throws Exception {
        String cmd = "/data/home/vip494/igvtools-2.3.98/igvtools count --bases -w 1 " +
                "~/user0/bam/"+i+".sorted.bam " + "~/user0/wig/"+i+".wig" +
                " ./Zea_mays.AGPv4.dna.chromosome.7.fa";
        Remote.connect(cmd);
    }
}
