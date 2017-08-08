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
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author horacio
 */
public class ComputeGoldSciSummScore {
    
    public static String dataSetLoc="/home/horacio/temp/SciSUM-2016/train/gold_scores_features_2";
    
    public static String outLoc="/home/horacio/temp/SciSUM-2016/train/gold_scores_features_2";
    
    public static String goldLoc="home/horacio/temp/SciSUM-2016/train/gold_summaries";
    
    public static String gold="community";
    
    public static String feature="gs_community";
    
    public static void main(String[] args) {
        try {            
            Gate.init();
            generateGoldScores();
        } catch(GateException ge) {
            
        }
    }
    public static void generateGoldScores() {
        
        File inClusters=new File(dataSetLoc);
        File[] files=inClusters.listFiles();
       
        String fname;
        String cname;
        String floc;
        
        PrintWriter pw;
        Document doc;
        Document summary;
        for(File file: files) {
            fname=file.getName();
            floc=file.getAbsolutePath();
            cname=fname.replace(".xml", "");
            System.out.println(cname+"...");
          
            if(file.exists()) {
                try {
                    System.out.println("Processing "+file);
                    doc=Factory.newDocument(new URL("file:///"+floc));
                    summary=Factory.newDocument(new URL("file:///"+goldLoc+File.separator+
                            cname+"."+gold+".xml"));
                    System.out.println(summary.getAnnotations().get("Vector_Norm"));
                    GenerateGoldScore(doc,summary);
                    System.out.println(doc.getAnnotations().get("Sentence"));
                  
                    
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
    
    public static void GenerateGoldScore(Document doc,Document summary) {
        
            AnnotationSet references=summary.getAnnotations().get("Vector_Norm");
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
                    sentfm.put(feature,(new Double(sim))+"");
                    
                } else {
                    
                    sentfm.put(feature,"0.0");
                }
                        
                        
                
            }
            
            
          
           
           
                        
            
       
    }
    
}
