package com.example.demo.tools;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SampleSplitAlgo {
    public static void split(String resultDir, String barcodeFile, String forwardSeqFile, String reverseSeqFile) throws IOException {
        /*

        Split the input two Fastq files into a number of Fastq samples representing individual maize sequencing results
        according to the specific barcode sequence combinations
        which means every two adjacent barcode sequences in the barcode file.

        @param resultDir
            A directory to store the split results, which will be created automatically if it does not exist.
        @param barcodeFile
            A txt file contains several specific barcode combinations.
        @param forwardSeqFile
            Forward sequencing data of all maize samples.
        @param reverseSeqFile
            Reverse sequencing data of all maize samples.

         */

        String tmpStr;
        //build list for barcode sequences file
        List<String> barcodeList = new ArrayList<>();
        BufferedReader readBarcode = new BufferedReader(new FileReader(barcodeFile));
        while ((tmpStr = readBarcode.readLine()) != null) {
            barcodeList.add(tmpStr);
        }
        readBarcode.close();

        //build hashmap for forward sequencing file
        Map<String, List<String>> sequencingNumDict = new HashMap<>();
        List<String> forwardUnitList = new ArrayList<>(4);
        BufferedReader readForwardSeq = new BufferedReader(new FileReader(forwardSeqFile));
        int n = 1;
        while ((tmpStr = readForwardSeq.readLine()) != null) {
            forwardUnitList.add(tmpStr);
            if (n % 4 == 0) {
                sequencingNumDict.put(forwardUnitList.get(0), forwardUnitList);
                forwardUnitList = new ArrayList<>(4);
            }
            n++;
        }
        readForwardSeq.close();

        //find matching sequences and separate out 96 pairs of files
        File fqDir = new File(resultDir);
        if (!fqDir.exists()) {
            fqDir.mkdirs();
        }
        List<String> reverseUnitList = new ArrayList<>(4);
        n = 1;
        for (int i = 0; i < 96; i++) {
            File fqFile_1 = new File(resultDir, i + "_1.fq");
            File fqFile_2 = new File(resultDir, i + "_2.fq");
            fqFile_1.createNewFile();
            fqFile_2.createNewFile();
            BufferedReader readReverseSeq = new BufferedReader(new FileReader(reverseSeqFile));
            while ((tmpStr = readReverseSeq.readLine()) != null) {
                reverseUnitList.add(tmpStr);
                if (n % 4 == 0) {
                    String seqHead = reverseUnitList.get(0).replace("/2", "/1");
                    forwardUnitList = sequencingNumDict.get(seqHead);
                    Boolean case1 = barcodeList.get(2 * i).equals(reverseUnitList.get(1).substring(0, 8)) && barcodeList.get(2 * i + 1).equals(forwardUnitList.get(1).substring(0, 8));
                    Boolean case2 = barcodeList.get(2 * i).equals(forwardUnitList.get(1).substring(0, 8)) && barcodeList.get(2 * i + 1).equals(reverseUnitList.get(1).substring(0, 8));
                    if (case1 || case2) {
                        BufferedWriter writeSeq_1 = new BufferedWriter(new FileWriter(fqFile_1, true));
                        tmpStr = String.join("\n", forwardUnitList) + "\n";
                        writeSeq_1.write(tmpStr);
                        writeSeq_1.close();
                        BufferedWriter writeSeq_2 = new BufferedWriter(new FileWriter(fqFile_2, true));
                        tmpStr = String.join("\n", reverseUnitList) + "\n";
                        writeSeq_2.write(tmpStr);
                        writeSeq_2.close();
                    }
                    reverseUnitList = new ArrayList<>(4);
                }
                n++;
            }
            readReverseSeq.close();
        }

    }
}
