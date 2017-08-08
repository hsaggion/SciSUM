/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.upf.taln.scisumm.gold;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;
import gate.Gate;
import gate.util.GateException;
import gate.Factory;
import gate.creole.ResourceInstantiationException;
import gate.util.OffsetComparator;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Creates gold summaries for each cluster
 * @author horacio
 */
public class CreateGoldSummaries {
    
    
    public static String dataSetLoc="/home/horacio/temp/SciSUM-2016/test/gate_files_processed";
    public static String outLoc="/home/horacio/temp/SciSUM-2016/test/gold";
    
    
    public static void main(String[] args) {
        try {            
            Gate.init();
            generateGold();
        } catch(GateException ge) {
            
        }
    }
    public static void generateGold() {
        
        File inClusters=new File(dataSetLoc);
        File[] clusters=inClusters.listFiles();
        File file;
        String cname;
        String cloc;
        String gold;
        PrintWriter pw;
        for(File cluster: clusters) {
            cname=cluster.getName();
            cloc=cluster.getAbsolutePath();
            System.out.println(cname+"...");
            file=new File(cloc+File.separator+cname+".xml");
            if(file.exists()) {
                System.out.println("Processing "+file);
                gold=extractGoldSummary(file);
                try {
                    pw=new PrintWriter(new FileWriter(outLoc+File.separator+cname+".txt"));
                    pw.print(gold);
                    pw.flush();
                    pw.close();
                } catch (IOException ex) {
                    Logger.getLogger(CreateGoldSummaries.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
            
            
        }
        
        
    }
    
    public static String extractGoldSummary(File f) {
        String gold="";
        TreeSet<String> test;
        try {
            Document doc=Factory.newDocument(new URL("file:///"+f.getAbsolutePath()));
            AnnotationSet all=doc.getAnnotations("REFERENCES");
            String dc=doc.getContent().toString();
            ArrayList<Annotation> refList=new ArrayList(all);
            Collections.sort(refList,new OffsetComparator());
            test=new TreeSet();
            Long start,end;
            String check;
            for(Annotation ref : refList) {
                start=ref.getStartNode().getOffset();
                end  =ref.getEndNode().getOffset();
                check=start+"#"+end;
                if(!test.contains(check)) {
                    gold=gold+dc.substring(start.intValue(), end.intValue())+"\n";
                    test.add(check);
                    
                }
            }
       
                        
            
        } catch (MalformedURLException ex) {
            Logger.getLogger(CreateGoldSummaries.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ResourceInstantiationException ex) {
            Logger.getLogger(CreateGoldSummaries.class.getName()).log(Level.SEVERE, null, ex);
        }
        return gold;
    }
    
    
}
