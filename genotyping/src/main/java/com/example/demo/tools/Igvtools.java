package com.example.demo.tools;

import com.example.demo.connection.Remote;

public class Igvtools {
    public static void igvtools() throws Exception {
        String cmd = "/data/home/vip494/igvtools-2.3.98/igvtools count --bases -w" +
                " 1 result.sorted.bam result.wig Zea_mays.AGPv4.dna.chromosome.7.fa";
        Remote.connect(cmd);
    }
}
