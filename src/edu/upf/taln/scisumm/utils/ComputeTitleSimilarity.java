/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.upf.taln.scisumm.utils;


//import summa.scorer.FirstSentenceSimilarity;
import gate.Corpus;
import gate.Document;
import gate.Factory;
import gate.Gate;
import gate.ProcessingResource;
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
public class ComputeTitleSimilarity {
    
    
    public static String cpLoc="/home/horacio/temp/SciSUM-2016/test-last/gate_files_processed";
   // public static String rpGoldLoc="/home/horacio/temp/SciSUM-2016/test-last/gold_scores_features";
 //  public static String rpGoldFeatLoc="/home/horacio/temp/SciSUM-2016/test-last/gold_scores_features";
    public static String rpGoldLoc="/home/horacio/work/SciSUM-2017/TEST-DATA-VECS-RPS";
    public static String rpGoldFeatLoc="/home/horacio/work/SciSUM-2017/TEST-DATA-VECS-RPS";
    
    public static summa.scorer.FirstSentenceSimilarity firstSimSumma;
      public static summa.scorer.FirstSentenceSimilarity firstSimGoogle;
        public static summa.scorer.FirstSentenceSimilarity firstSimACL;
    
    public static void main(String[] args) {
        try {
            Gate.init();
            firstSimSumma=new summa.scorer.FirstSentenceSimilarity();
            firstSimSumma.setAnnSetName("Analysis");
            firstSimSumma.setSentAnn("Sentence");
            firstSimSumma.setVecAnn("Vector_Norm");
            firstSimSumma.setFeature("first_sim_summa");
            firstSimSumma.init();
            
            firstSimGoogle=new summa.scorer.FirstSentenceSimilarity();
            firstSimGoogle.setAnnSetName("Analysis");
            firstSimGoogle.setSentAnn("Sentence");
            firstSimGoogle.setVecAnn("Vector_Norm");
            firstSimGoogle.setFeature("first_sim_google");
             firstSimGoogle.init();
            
            firstSimACL=new summa.scorer.FirstSentenceSimilarity();
            firstSimACL.setAnnSetName("Analysis");
            firstSimACL.setSentAnn("Sentence");
            firstSimACL.setVecAnn("Vector_Norm");
            firstSimACL.setFeature("first_sim_acl");
             firstSimACL.init();
            computeTitleSimAll();
        } catch(GateException ge) {
            ge.printStackTrace();
        }
    }
    
    
    
      public static void computeTitleSimAll()  {
        PrintWriter pw;
        File inDir=new File(rpGoldFeatLoc);
        File[] rpList=inDir.listFiles();
        String rpName;
        String rpLoc;
        Document rp;
        String cluster;
  
        
        for(File file : rpList) {
         
                try {
                    rpName=file.getName();
                    cluster=rpName.replace(".xml", "");
                    
                
                        System.out.println(cluster+"...");
                        rpLoc =file.getAbsolutePath();
                        rp=Factory.newDocument(new URL("file:///"+rpLoc));
                        
                        firstSimSumma.setDocument(rp);
                        firstSimSumma.execute();
                       
                        firstSimGoogle.setDocument(rp);
                        firstSimGoogle.execute();
                        
                        firstSimACL.setDocument(rp);
                        firstSimACL.execute();
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
                }
                
                
              
                
            
            
        }
        
        
        
        
    }
    
}
