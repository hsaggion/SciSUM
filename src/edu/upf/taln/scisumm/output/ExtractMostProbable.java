/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.upf.taln.scisumm.output;

import static edu.upf.taln.scisumm.output.ExtractDataFromLogFile.probabilities;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author horacio
 */
public class ExtractMostProbable {
    
    
    public static String inLoc="/home/horacio/temp/SciSUM-2016/output_annotations/matches";
  //  public static String outLoc="/home/horacio/temp/SciSUM-2016/output_annotations/filtered_matches";
       public static String outLoc="/home/horacio/temp/SciSUM-2016/output_annotations/all_matches";
    public static int top=1;
    
    public static void extractAllMostProbable(String inFile, String outFile) {
        PrintWriter pw=null;
        try {
            pw=new PrintWriter(new FileWriter(outFile));
            Map<String,ArrayList<LineAndProb>> allLines=new TreeMap();
            ArrayList<LineAndProb> citanceList;
            String line;
            BufferedReader reader;
            int pos1;
            int pos2;
            int pos3;
            String prob;
            String outputLine;
            String citance;
            try {
                reader=new BufferedReader(new FileReader(inFile));
                while((line=reader.readLine())!=null) {
                    pos1=line.indexOf("PROB: ");
                    pos2=line.indexOf("| Citance Number:");
                    pos3=line.indexOf("| Reference Article:");
                    prob=line.substring(pos1+6, pos2);
                    outputLine=line.substring(pos2+2, line.length());
                    citance=line.substring(pos2+2, pos3-1);
                    /*
                    System.out.println(prob);
                    System.out.println(citance);
                    System.out.println(outputLine);
                    */
                    if(allLines.containsKey(citance)) {
                        citanceList=allLines.get(citance);
                    } else {
                        citanceList=new ArrayList();
                    }
                    citanceList.add(new LineAndProb(outputLine,new Double(prob)));
                    
                    allLines.put(citance, citanceList);
                    
                    
                }
                
                
                // output top probs
                double lastProb;
                double currProb;
                int count;
                for(String cit : allLines.keySet()) {
                    System.out.println(cit);
                    citanceList=allLines.get(cit);
                    System.out.println(citanceList.size());
                    Collections.sort(citanceList, LineAndProb.probComparator);
                    lastProb=citanceList.get(0).prob;
                    count=0;
                    for(LineAndProb lp : citanceList) {
                        currProb=lp.prob;
                        //if(count>top) {
                        if(count>citanceList.size()){
                            if(currProb!=lastProb) break;
                            
                        }
                        count++;
                        pw.println(lp.line);
                        pw.flush();
                        lastProb=currProb;
                        
                        
                    }
                    
                    
                    
                    
                }
                pw.close();
                
                
                
            } catch(IOException ioe) {                
                ioe.printStackTrace();
            }
            
                
            
        } catch(IOException ex) {
            Logger.getLogger(ExtractMostProbable.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            pw.close();
        }
    }
    
    public static void main(String[] args) {
        
        
        File inDir=new File(inLoc);
        File[] flist=inDir.listFiles();
        String fname;
        for(File file: flist) {
            fname=file.getName();
            if(fname.endsWith("taln_upf.txt")) {
                extractAllMostProbable(inLoc+File.separator+fname,
                        outLoc+File.separator+fname.replace(".txt", "")+".all.txt");
            }
        }
    }
    
}
