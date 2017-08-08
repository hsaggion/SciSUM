/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.upf.taln.scisumm.utils;

import static edu.upf.taln.scisumm.utils.ComputeTitleSimilarity.computeTitleSimAll;
//import static edu.upf.taln.scisumm.utils.ComputeTitleSimilarity.firstSim;
import static edu.upf.taln.scisumm.utils.ComputeTitleSimilarity.rpGoldFeatLoc;
import gate.AnnotationSet;
import gate.Document;
import gate.Factory;
import gate.Gate;
import gate.creole.ExecutionException;
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
public class ComputeAbstractSimilarity {
    
     
    public static String cpLoc="/home/horacio/temp/SciSUM-2016/test-last/gate_files_processed";
   // public static String rpGoldLoc="/home/horacio/temp/SciSUM-2016/test-last/gold_scores_features";
   // public static String rpGoldFeatLoc="/home/horacio/temp/SciSUM-2016/test-last/gold_scores_features";
    public static String rpGoldLoc="/home/horacio/work/SciSUM-2016/TEST-2017";
    public static String rpGoldFeatLoc="/home/horacio/work/SciSUM-2016/TEST-2017";
   
    
    
    public static summa.resources.frequency.VectorComputation computeVecs;
    public static summa.analyser.NormalizeVector normVecs;
    public static summa.scorer.TitleSentenceSim absSim;
    
    public static void main(String[] args) {
        try {
            Gate.init();
            
            computeVecs=new summa.resources.frequency.VectorComputation();
            
            
            
            computeVecs.setAnnSetName("Original markups");
            computeVecs.setSentAnn("ABSTRACT");
            computeVecs.setEncoding("UTF-8");
            computeVecs.setInitVectors(Boolean.TRUE);
            computeVecs.setLowercase(Boolean.FALSE);
            computeVecs.setStatistics("token_tf_idf");
            computeVecs.setStopFeature("string");
            computeVecs.setStopTag("kind");
            computeVecs.setStopTagLoc(new
         URL("file:///home/horacio/work/software/GATE-8.4.1/plugins/summa_plugin/resources/stop_kind.lst"));
            computeVecs.setStopWordLoc(new 
         URL("file:///home/horacio/work/SciSUM-2016/summa-resources/stop-word-list.txt"));
            computeVecs.setTokenAnn("Token");
            computeVecs.setTokenFeature("root");
            computeVecs.setVecAnn("Vector");
            computeVecs.init();
            
            normVecs=new summa.analyser.NormalizeVector();
            normVecs.setAnnSet("Original markups");
            normVecs.setVecAnn("Vector");
            normVecs.init();
            
            absSim=new summa.scorer.TitleSentenceSim();
            absSim.setAnnSet("Analysis");
            absSim.setSentAnn("Sentence");
            absSim.setTitleAnnSet("Original markups");
            absSim.setTitleFeature("abs_sim");
            absSim.setVector("Vector_Norm");
            absSim.init();
            
            computeAbstractSimAll();
        } catch(GateException ge) {
            ge.printStackTrace();
        } catch (MalformedURLException ex) {
            Logger.getLogger(ComputeAbstractSimilarity.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
     public static void computeAbstractSimAll()  {
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
                        all=rp.getAnnotations("Analysis");
                        // copy tokens to abstract
                        original_markups=rp.getAnnotations("Original markups");
                        // remove tokens from OM
                        original_markups.removeAll(original_markups.get("Token"));
                        // end
                        abs_ann=original_markups.get("ABSTRACT");
                        if(abs_ann.size()==1) {
                            tokens=all.get("Token", abs_ann.firstNode().getOffset(),
                                    abs_ann.lastNode().getOffset());
                            original_markups.addAll(tokens);
                            
                        }
                        
                        // compute vector abstract
                        
                        computeVecs.setDocument(rp);
                        computeVecs.execute();
                        
                        // normalize vector abstract
                        normVecs.setDocument(rp);
                        normVecs.execute();
                        
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
            }
                
                
              
                
            
            
        }
        
        
        
        
    }
    
    
    
}
