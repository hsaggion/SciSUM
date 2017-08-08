/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.upf.taln.scisumm.utils;

import static edu.upf.taln.scisumm.utils.SimilarityToCPCitations.rpGoldFeatLoc;
import static edu.upf.taln.scisumm.utils.SimilarityToCPCitations.rpGoldLoc;
import gate.AnnotationSet;
import gate.Factory;
import gate.Document;
import gate.Gate;
import gate.creole.ResourceInstantiationException;
import gate.util.GateException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author horacio
 */
public class CleanAnnSets {
    
    public static String clusterLoc="/home/horacio/work/SciSUM-2016/DATA-2017-CLUSTERS-VEC";
    public static String outClusterLoc="/home/horacio/work/SciSUM-2016/DATA-2017-CLUSTERS-VEC-OUT";
    
    public static void main(String[] args) {
        try {
            String cluster;
            Gate.init();
            File inDir=new File(clusterLoc);
            File[] rpList=inDir.listFiles();
            File clusterDir;
            File aux;
            for(File file : rpList) {
                if(file.isDirectory()) {
                  
                   cluster=file.getName();
                   aux=new File(outClusterLoc+File.separator+cluster);
                   if(!aux.exists()) {
                        System.out.println("Processing "+cluster+"....");
                        clusterDir=new File(outClusterLoc+File.separator+cluster);
                        if(!clusterDir.exists()) clusterDir.mkdir();

                        CleanAnnSets(clusterLoc+File.separator+cluster,
                                outClusterLoc+File.separator+cluster);
                   } else {
                       System.out.println("Ignoring "+cluster+"....");
                   }
                }
                
            }
            
        } catch(GateException ge) {
            ge.printStackTrace();
        }
        
    }
    
    public static void CleanAnnSets(String inLoc,String outLoc) {
        String[] toClean=new String[3];
        
        PrintWriter pw;
        toClean[0]="CorefChains";
        toClean[1]="CorefSpot";
        toClean[2]="Word2Vec";
        
        File inDir=new File(inLoc);
        File[] flist=inDir.listFiles();
        File file;
        Document doc;
        AnnotationSet annSet;
        String floc;
        String fname;
        for(int f=0;f<flist.length;f++) {
            try {
                file=flist[f];
                floc=file.getAbsolutePath();
                fname=file.getName();
                System.out.println(fname);
                doc=Factory.newDocument(new URL("file:///"+floc));
                for(int as=0;as<3;as++) {
                   
                    doc.removeAnnotationSet(toClean[as]);
                    
                }
               
                pw=new PrintWriter(new FileWriter(outLoc+File.separator+fname));
                pw.print(doc.toXml());
                pw.flush();
                pw.close(); 
                Factory.deleteResource(doc);
            } catch (ResourceInstantiationException ex) {
                ex.printStackTrace();
            } catch (MalformedURLException ex) {
               ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        
        }
        
    }
    
}
