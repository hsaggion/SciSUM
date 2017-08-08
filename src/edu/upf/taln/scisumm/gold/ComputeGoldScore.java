/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.upf.taln.scisumm.gold;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.creole.ResourceInstantiationException;
import gate.util.GateException;
import gate.util.OffsetComparator;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author horacio
 */
public class ComputeGoldScore {
    
    
   /* 
    public static String dataSetLoc="/home/horacio/temp/SciSUM-2016/test/gate_files_processed";
    public static String outLoc="/home/horacio/temp/SciSUM-2016/test/gold_scores";
    */
       
    public static String dataSetLoc="/home/horacio/temp/SciSUM-2016/test-last/gate_files_processed";
    public static String outLoc="/home/horacio/temp/SciSUM-2016/test-last/gold_scores";
    
    public static void main(String[] args) {
        try {            
            Gate.init();
            generateGoldScores();
        } catch(GateException ge) {
            
        }
    }
    public static void generateGoldScores() {
        
        File inClusters=new File(dataSetLoc);
        File[] clusters=inClusters.listFiles();
        File file;
        String cname;
        String cloc;
        String gold;
        PrintWriter pw;
        Document doc;
        for(File cluster: clusters) {
            cname=cluster.getName();
            cloc=cluster.getAbsolutePath();
            System.out.println(cname+"...");
            file=new File(cloc+File.separator+cname+".xml");
            if(file.exists()) {
                try {
                    System.out.println("Processing "+file);
                    doc=Factory.newDocument(new URL("file:///"+file.getAbsolutePath()));
                    GenerateGoldScore(doc);
                    try {
                        pw=new PrintWriter(new FileWriter(outLoc+File.separator+cname+".xml"));
                        pw.print(doc.toXml());
                        pw.flush();
                        pw.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    
                } catch (ResourceInstantiationException ex) {
                    Logger.getLogger(ComputeGoldScore.class.getName()).log(Level.SEVERE, null, ex);
                } catch (MalformedURLException ex) {
                    Logger.getLogger(ComputeGoldScore.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
            
            
        }
        
        
    }
    
    public static void GenerateGoldScore(Document doc) {
        
            AnnotationSet references=doc.getAnnotations("REFERENCES");
            AnnotationSet vectors=doc.getAnnotations().get("Vector_Norm");
            AnnotationSet sentences=doc.getAnnotations().get("Sentence");
            FeatureMap refCentroid;
            
            Long startRef, endRef;
            Annotation vector;
            FeatureMap vecfm;
            Set allVecs=new HashSet();
            AnnotationSet auxVecs;
            for(Annotation ref :references) {
                startRef=ref.getStartNode().getOffset();
                endRef  =ref.getEndNode().getOffset();
                auxVecs=vectors.get(startRef, endRef);
                if(auxVecs.size()==1) {
                    
                    vector=auxVecs.iterator().next();
                    vecfm=vector.getFeatures();
                    allVecs.add(vecfm);
                }
            }
            
            refCentroid=summa.centroid.Centroid.Centroid1(allVecs);
            Annotation sentVectorAnn;
            FeatureMap sentVector;
            Long startS, endS;
            double sim;
            FeatureMap sentfm;
            for(Annotation sentence : sentences) {
                startS=sentence.getStartNode().getOffset();
                endS  =sentence.getEndNode().getOffset();
                auxVecs=vectors.get(startS,endS);
                sentfm=sentence.getFeatures();
                if(auxVecs.size()==1) {
                    sentVectorAnn=auxVecs.iterator().next();
                    sentVector=sentVectorAnn.getFeatures();
                    sim=summa.scorer.Cosine.cosine1(sentVector, refCentroid);
                    sentfm.put("gs",(new Double(sim))+"");
                    
                } else {
                    
                    sentfm.put("gs","0.0");
                }
                        
                        
                
            }
            
            
          
           
           
                        
            
       
    }
    
    
}
