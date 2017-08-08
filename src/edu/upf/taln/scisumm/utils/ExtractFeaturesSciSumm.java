/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.upf.taln.scisumm.utils;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.util.GateException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author horacio
 */
public class ExtractFeaturesSciSumm {
    
    //public static String target_feature="gs_acl_abs_vec_max";
    
     public static void main(String[] args) {
         String target_feature;
       
        
        //String inDir="/home/horacio/temp/div_training";
      // String inDir="/home/horacio/temp/SciSUM-2016/devel/gold_scores_features";
        // String inDir="/home/horacio/temp/SciSUM-2016/train/gold_scores_features_2";
        //  String inDir="/home/horacio/work/SciSUM-2016/TEST-FEATURES-1-OUT";
           String inDir="/home/horacio/work/SciSUM-2016/EVALUATE-TEST/TRAINING-2016";
        // summarization features
        ArrayList<String> featSets=new ArrayList();
       
      ArrayList<String> targets=new ArrayList();
      targets.add("gs_acl_abs_vec_max");
       targets.add("gs_acl_abs_vec_avg");
        targets.add("gs_acl_com_vec_avg");
        targets.add("gs_acl_com_vec_max");
       targets.add("gs_acl_hum_vec_avg");
        targets.add("gs_acl_hum_vec_max");
        
         targets.add("gs_google_abs_vec_max");
       targets.add("gs_google_abs_vec_avg");
        targets.add("gs_google_com_vec_avg");
        targets.add("gs_google_com_vec_max");
       targets.add("gs_google_hum_vec_avg");
        targets.add("gs_google_hum_vec_max");
        
         targets.add("gs_summa_abs_vec_max");
       targets.add("gs_summa_abs_vec_avg");
        targets.add("gs_summa_com_vec_avg");
        targets.add("gs_summa_com_vec_max");
       targets.add("gs_summa_hum_vec_avg");
        targets.add("gs_summa_hum_vec_max");
        
        /*    
        featSets.add("abs_sim");
         featSets.add("centroid_sim");
          featSets.add("cps_avg");
           featSets.add("cps_max");
            featSets.add("cps_sim");
             featSets.add("first_sim");
              featSets.add("in_sec");
               featSets.add("in_sec_sent");
                featSets.add("norm_cue");
                 featSets.add("position_score");
                  featSets.add("textrank_score_norm");
                  */
        
        
        featSets.add("PROB_DRI_Approach");
        featSets.add("PROB_DRI_Background");
        featSets.add("PROB_DRI_Challenge");
        featSets.add("PROB_DRI_FutureWork");
        featSets.add("PROB_DRI_Outcome");
        featSets.add("acl_avg");
        featSets.add("acl_max");
        featSets.add("acl_min");
        featSets.add("suma_avg");
        featSets.add("summa_max");
        featSets.add("summa_min");
        featSets.add("google_avg");
        featSets.add("google_max");
        featSets.add("google_min");
        featSets.add("centroid_sim_acl");
        featSets.add("centroid_sim_google");
        featSets.add("centroid_sim_summa");
        featSets.add("first_sim_acl");
        featSets.add("first_sim_google");
        featSets.add("first_sim_summa");
        featSets.add("in_sec");
        featSets.add("in_sec_sent");
        featSets.add("norm_cue");
        featSets.add("textrank_score");
        featSets.add("textrank_score_acl");
        featSets.add("textrank_score_google");
         featSets.add("abs_sim_acl");
        featSets.add("abs_sim_summa");
         featSets.add("abs_sim_google");
          featSets.add("citRatio");
           featSets.add("position_score");
            featSets.add("token_tf_idf");
        
        Iterator<String> featIte;
        
        String instance;
        
        File inFiles=new File(inDir);
        File[] flist=inFiles.listFiles();
        Document doc;
        String docLoc;
        String docName;
        AnnotationSet all;
        AnnotationSet sentences;
        FeatureMap fm;
        
        String feat_value;
        
        PrintWriter[] pw1=new PrintWriter[targets.size()];
        PrintWriter[] pw=new PrintWriter[targets.size()];
        // print cases and features
       try {
        for(int f=0;f<targets.size();f++) {
            target_feature=targets.get(f);
            pw[f]=new PrintWriter(new 
            FileWriter("/home/horacio/work/SciSUM-2016/EVALUATE-TEST/ARFFs"+File.separator+target_feature+".arff"));

            pw1[f]=new PrintWriter(new 
            FileWriter("/home/horacio/work/SciSUM-2016/EVALUATE-TEST/ARFFs"+File.separator+target_feature+".csv"));

            pw[f].println("@relation REL");
            pw[f].println();
            for(String feat_name : featSets) {
                    pw[f].println("@attribute "+feat_name+" Numeric");
                    pw1[f].print(feat_name+",");
            }

            pw1[f].println(target_feature);
            pw1[f].flush();


            pw[f].println("@attribute "+target_feature+" Numeric");
            pw[f].println();
            pw[f].println("@data");
        }
       
            Double gs;
            double gs_v;
          
            String final_instance;
            Gate.init();
            for(int f=0;f<flist.length  ;f++) {
                docLoc=flist[f].getAbsolutePath();
                docName=flist[f].getName();
                if(docName.endsWith(".xml")) {
                    System.out.println(docName+"....");
                   doc=Factory.newDocument(new URL("file:///"+docLoc),"UTF-8");
                   all=doc.getAnnotations("Analysis");
                   sentences=all.get("Sentence");
                   for(Annotation sentence : sentences) {
                       fm=sentence.getFeatures();
                       instance="";
                       for(String feat_name : featSets) {
                           if(fm.containsKey(feat_name)) {
                            feat_value=fm.get(feat_name).toString();
                            instance=instance+feat_value+",";
                           } else {
                               instance=instance+"0.0"+",";
                           }
                       }
                       for(int ff=0;ff<targets.size();ff++) {
                        target_feature=targets.get(ff);
                       
                        if(fm.containsKey(target_feature)) {
                           gs=new Double((String)fm.get(target_feature));
                           gs_v=gs.doubleValue();
                           //if(gs_v>=4) {
                             final_instance=instance+gs_v;
                          // } else {
                          //     instance=instance+"0.0";
                          // }

                           pw[ff].println(final_instance);
                           pw[ff].flush();
                           pw1[ff].println(final_instance);
                           pw1[ff].flush();
                        } else {
                            System.out.println("NO "+target_feature);
                        }
                       }
                   }
                  
                   
                   
                    Factory.deleteResource(doc);
                }
              
                   
                }
            
               for(int p=0;p<pw.length;p++) {
                pw[p].close();
                pw1[p].close();
               }
               
            } catch (GateException ex) {
                ex.printStackTrace();
            } catch (MalformedURLException ex) {
                 ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
        }
            
            
            
                
                
            }
            
            
    
}
