/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.upf.taln.scisumm.utils;

import static edu.upf.taln.scisumm.utils.ComputeTitleSimilarity.computeTitleSimAll;
//import static edu.upf.taln.scisumm.utils.ComputeTitleSimilarity.firstSim;
import static edu.upf.taln.scisumm.utils.ComputeTitleSimilarity.rpGoldFeatLoc;
import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import gate.util.GateException;
import gate.util.InvalidOffsetException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author horacio
 */
public class ComputeAvgAbsSim {
    
     
    public static String cpLoc="/home/horacio/temp/SciSUM-2016/test-last/gate_files_processed";
   // public static String rpGoldLoc="/home/horacio/temp/SciSUM-2016/test-last/gold_scores_features";
   // public static String rpGoldFeatLoc="/home/horacio/temp/SciSUM-2016/test-last/gold_scores_features";
    public static String rpGoldLoc="/home/horacio/work/SciSUM-2017/TEST-DATA-VECS-RPS";
    public static String rpGoldFeatLoc="/home/horacio/work/SciSUM-2017/TEST-DATA-VECS-RPS";
   
    
    
  //  public static summa.resources.frequency.VectorComputation computeVecs;
 //   public static summa.analyser.NormalizeVector normVecs;
    public static summa.scorer.TitleSentenceSim absSim;
    
    public static void main(String[] args) {
        
        String abs_feature=args[1];
        String abs_vector=args[2];
        System.out.println(abs_feature);
        System.out.println(abs_vector);
        
        try {
            Gate.init();
            
      
            
            absSim=new summa.scorer.TitleSentenceSim();
            absSim.setAnnSet("Analysis");
            absSim.setSentAnn("Sentence");
            absSim.setTitleAnnSet("Original markups");
            absSim.setTitleFeature(abs_feature);
            absSim.setVector(abs_vector);
            absSim.init();
            
            computeAbstractSimAll(abs_feature,abs_vector);
        } catch(GateException ge) {
            ge.printStackTrace();
        } 
    }
    
    
     public static void computeAbstractSimAll(String abs_feature,String abs_vector)  {
        PrintWriter pw;
        File inDir=new File(rpGoldLoc);
        File[] rpList=inDir.listFiles();
        String rpName;
        String rpLoc;
        Document rp;
        String cluster;
        AnnotationSet all;
        AnnotationSet tokens;
        AnnotationSet original_markups;
        AnnotationSet abs_ann;
        Long startA,endA;
        AnnotationSet sentVecs;
        AnnotationSet sentVecsAbs;
        ArrayList<Annotation> sentVecAbsList;
        Annotation vector;
        FeatureMap vecfm;
        FeatureMap centroid;

        for(File file : rpList) {
            
                try {
                    rpName=file.getName();
                    cluster=rpName.replace(".xml", "");
                    
                
                        System.out.println(cluster+"...");
                        rpLoc =file.getAbsolutePath();
                        rp=Factory.newDocument(new URL("file:///"+rpLoc));
                        all=rp.getAnnotations("Analysis");
                        // copy tokens to abstract
                        original_markups=rp.getAnnotations("Original markups");
                        original_markups.removeAll(original_markups.get(abs_vector));
                        
                        // end
                        abs_ann=original_markups.get("ABSTRACT");
                        startA=abs_ann.firstNode().getOffset();
                        endA  =abs_ann.lastNode().getOffset();
                        sentVecs=all.get(abs_vector);
                        sentVecsAbs=sentVecs.get(startA,endA);
                        sentVecAbsList=new ArrayList(sentVecsAbs);
                        centroid=Factory.newFeatureMap();
                        for(int s=0;s<sentVecAbsList.size();s++) {
                            vector=sentVecAbsList.get(s);
                            vecfm=vector.getFeatures();
                            centroid=summa.centroid.Centroid.addVector(centroid, vecfm);
                        }
                        original_markups.add(startA,endA,abs_vector,centroid);
                       
                        
                        // compute similarity
                        absSim.setDocument(rp);
                        absSim.execute();
                        
                        
                        
                        pw=new PrintWriter(new FileWriter(rpGoldFeatLoc+File.separator+rpName));
                        pw.print(rp.toXml());
                        pw.flush();
                        pw.close();
                        // clean corpus
                        Factory.deleteResource(rp);
                       
              
                    
                } catch (ResourceInstantiationException ex) {
                    Logger.getLogger(SimilarityToCPCitations.class.getName()).log(Level.SEVERE, null, ex);
                } catch (MalformedURLException ex) {
                    Logger.getLogger(SimilarityToCPCitations.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(SimilarityToCPCitations.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ExecutionException ex) {
                Logger.getLogger(ComputeAbstractSimilarity.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvalidOffsetException ex) {
                Logger.getLogger(ComputeAvgAbsSim.class.getName()).log(Level.SEVERE, null, ex);
            }
                
                
              
                
            
            
        }
        
        
        
        
    }
    
    
    
}

