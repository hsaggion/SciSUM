/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.upf.taln.scisumm.utils;

import static edu.upf.taln.scisumm.utils.ComputeAbstractSimilarity.absSim;
import static edu.upf.taln.scisumm.utils.ComputeAbstractSimilarity.computeAbstractSimAll;
import static edu.upf.taln.scisumm.utils.ComputeAbstractSimilarity.computeVecs;
import static edu.upf.taln.scisumm.utils.ComputeAbstractSimilarity.normVecs;
import static edu.upf.taln.scisumm.utils.ComputeAbstractSimilarity.rpGoldFeatLoc;
import static edu.upf.taln.scisumm.utils.ComputeAbstractSimilarity.rpGoldLoc;
import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.creole.ExecutionException;
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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author horacio
 */
public class DocStructureFeatures {
     public static String cpLoc="/home/horacio/temp/SciSUM-2016/test-last/gate_files_processed";
   //  public static String rpGoldLoc="/home/horacio/temp/SciSUM-2016/test-last/gold_scores_features";
  //   public static String rpGoldFeatLoc="/home/horacio/temp/SciSUM-2016/test-last/gold_scores_features";
    public static String rpGoldLoc="/home/horacio/work/SciSUM-2017/TEST-DATA-VECS-RPS";
    public static String rpGoldFeatLoc="/home/horacio/work/SciSUM-2017/TEST-DATA-VECS-RPS";
    
    public static summa.scorer.PositionScorer position;
    
    public static void main(String[] args) {
        try {
            Gate.init();
            
            position=new summa.scorer.PositionScorer();
            position.setAnnSetName("Analysis");
            position.setSentAnn("Sentence");
            position.setScoreName("position_score");
            position.init();
            
            computePosition();
           
        } catch(GateException ge) {
            ge.printStackTrace();
        } 
    }
    
    public static void computePositionInSection(Document doc) {
        
        AnnotationSet sections=doc.getAnnotations("Original markups").get("SECTION");
        
        AnnotationSet abs=doc.getAnnotations("Original markups").get("ABSTRACT");
        AnnotationSet sentences=doc.getAnnotations("Analysis").get("Sentence");
        AnnotationSet auxSents;
        Long startSec,endSec;
        ArrayList<Annotation> sentList;
       
        FeatureMap sentfm;
        FeatureMap secfm;
        String secnum;
        double secval;
        double insecval;
        
        
        
        for(Annotation section : sections) {
            
            secfm=section.getFeatures();
            if(secfm.containsKey("number")) {
                secnum=(String)secfm.get("number");
            } else {
                secnum="0.0";
            }
            System.out.println(secnum);
            secval=new Double(secnum).doubleValue();
            startSec=section.getStartNode().getOffset();
            endSec  =section.getEndNode().getOffset();
            auxSents=sentences.get(startSec,endSec);
            sentList=new ArrayList(auxSents);
            Collections.sort(sentList,new OffsetComparator());
            insecval=0.0;
            for(Annotation sentence : sentList) {
                insecval++;
                sentfm=sentence.getFeatures();
                sentfm.put("in_sec", (sections.size()+1-secval)/sections.size());
                sentfm.put("in_sec_sent",(sentList.size()+1-insecval)/sentList.size());
                
            }
            
            
            
        }
        
        auxSents=sentences.get(abs.firstNode().getOffset(),abs.lastNode().getOffset());
        sentList=new ArrayList(auxSents);
        Collections.sort(sentList,new OffsetComparator());
        insecval=0.0;
        for(Annotation sentence : sentList) {
            insecval++;
            sentfm=sentence.getFeatures();
            sentfm.put("in_sec", "1.0");
            sentfm.put("in_sec_sent",(sentList.size()+1-insecval)/sentList.size());

        }
        
        auxSents=sentences.get(new Long(0),abs.firstNode().getOffset());
        sentList=new ArrayList(auxSents);
        Collections.sort(sentList,new OffsetComparator());
        insecval=0.0;
        for(Annotation sentence : sentList) {
            insecval++;
            sentfm=sentence.getFeatures();
            sentfm.put("in_sec", "1.0");
            sentfm.put("in_sec_sent",(sentList.size()+1-insecval)/sentList.size());

        }
        
        
        
        
    }
    
    public static void computePosition()  {
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

        for(File file : rpList) {
            
                try {
                    rpName=file.getName();
                    cluster=rpName.replace(".xml", "");
                    System.out.println(cluster+"...");
                    rpLoc =file.getAbsolutePath();
                    rp=Factory.newDocument(new URL("file:///"+rpLoc));
                    position.setDocument(rp);
                    position.execute();
                    computePositionInSection(rp);
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
            }
                
                
              
                
            
            
        }
        
        
        
        
    }
    
    
}
