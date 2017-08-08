/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.upf.taln.scisumm.utils;

import static edu.upf.taln.scisumm.utils.ComputeAbstractSimilarity.rpGoldFeatLoc;
import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;
import gate.FeatureMap;
import gate.Gate;
import gate.util.GateException;
import java.util.ArrayList;
import summa.scorer.Cosine;
import gate.Factory;
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
public class CompareRPwithModel {
    
    public static String rpInAnnSet="Analysis";
    public static String modelInAnnSet="Analysis";
    public static String wordVecAnnType1="aclSentVec";
    public static String wordVecAnnType2="googleSentVec";
    public static String wordVecAnnType3="Vector_Norm";
    public static String preFeature1="acl";
    public static String preFeature2="google";
    public static String absFeature1="abs";
    public static String absFeature2="hum";
    public static String absFeature3="com";
    public static String preFeature3="summa";
    
    public static String sentAnnType="Sentence";
    public static String refLoc="/home/horacio/work/SciSUM-2016/TEST-2017-VECTORS-1";
    public static String refLocBis="/home/horacio/work/SciSUM-2016/TEST-2017-VECTORS-1";
    public static String goldSummLoc="/home/horacio/work/SciSUM-2016/TEST-2017-VECTORS";
    
    public static void CompareRPwithSummary(Document rp, Document summary,String wordVecAnnType,String fname) {
        AnnotationSet rpAll=rp.getAnnotations(rpInAnnSet);
        AnnotationSet modelAll=summary.getAnnotations(modelInAnnSet);
        AnnotationSet rpSents=rpAll.get(sentAnnType);
        AnnotationSet rpVecs=rpAll.get(wordVecAnnType);
        AnnotationSet modelVecs=modelAll.get(wordVecAnnType);
        
        // compare sentences to each in abstract, compute max, min, avg
        
        Annotation rpVec, modelVec;
        ArrayList<Annotation> rpSentList=new ArrayList(rpSents);
        Annotation sentence;
        
        Long startS,endS;
        
        AnnotationSet auxVecs;
        
        ArrayList<Annotation> modelList=new ArrayList(modelVecs);
        Annotation sentVec;

        FeatureMap rpVector;
        FeatureMap modelVector;
        
        double val;
        double total, max, min, avg;
        double count=0.0;
        FeatureMap sentfm;
        for(int rs=0;rs<rpSentList.size();rs++) {
            
            sentence=rpSentList.get(rs);
            sentfm=sentence.getFeatures();
            startS=sentence.getStartNode().getOffset();
            endS  =sentence.getEndNode().getOffset();
            auxVecs=rpVecs.get(startS,endS);
            total=0;
            max=0.0;
            min=1.0;
            if(auxVecs.size()==1) {
                
                
                sentVec=auxVecs.iterator().next();
                rpVector=sentVec.getFeatures();
                count=0.0;
                for(int mv=0;mv<modelList.size();mv++) {
                    modelVec=modelList.get(mv);
                    modelVector=modelVec.getFeatures();
                    val=summa.scorer.Cosine.cosine1(rpVector, modelVector);
                    if(val>max) max=val;
                    if(val<min) min=val;
                    total=total+val;
                    count++;
                    
                    
                    
                }
                avg=total/count;
             //   System.out.println(min+" "+avg+" "+max);
                sentfm.put("gs_"+fname+"_vec_max", max+"");
                sentfm.put("gs_"+fname+"_vec_min", min+"");
                sentfm.put("gs_"+fname+"_vec_avg", avg+"");
                
            }
            
            
            
            
        }
        
        
        
        
        
    }
    
    public static void main(String[] args) {
        PrintWriter pw;
        String refPaper;
        String goldSummary;
        String rpName;
        Document rp;
        Document abs, community, human;
        
        
        File rpDir=new File(refLoc);
        File[] rpList=rpDir.listFiles();
        String summName;
        File testFile;
        try {
            
            Gate.init();
            
            for(File file : rpList) {
                
                rpName=file.getName();
               
                if(!rpName.contains("abstract") && !rpName.contains("community") && !rpName.contains("human") ) {
                    System.out.println(rpName);
                    summName=rpName.replaceAll(".xml", "");
                    rp=Factory.newDocument(new URL("file://"+refLoc+File.separator+rpName));
                    
                    // abstract
                    
                    testFile=new File(goldSummLoc+File.separator+summName+".abstract.xml-PreProcessed.xml");
                    if(testFile.exists()) {
                        abs=Factory.newDocument(new URL("file://"+goldSummLoc+File.separator+summName+".abstract.xml-PreProcessed.xml"));
                        CompareRPwithSummary(rp,abs,wordVecAnnType3,preFeature3+"_"+absFeature1);
                      //  CompareRPwithSummary(rp,abs,wordVecAnnType2,preFeature2+"_"+absFeature1);
                        
                    } else {
                        System.out.println(testFile.getName()+" MISSING!!!");
                    }

                    
                    
                    // community
                    
                    testFile=new File(goldSummLoc+File.separator+summName+".community.xml-PreProcessed.xml");
                    if(testFile.exists()) {
                        community=Factory.newDocument(new URL("file://"+goldSummLoc+File.separator+summName+".community.xml-PreProcessed.xml"));
                        CompareRPwithSummary(rp,community,wordVecAnnType3,preFeature3+"_"+absFeature2);
                       // CompareRPwithSummary(rp,community,wordVecAnnType2,preFeature2+"_"+absFeature3);
                    } else {
                        System.out.println(testFile.getName()+" MISSING!!!");
                    }

                    
                    // human
                    
                    testFile=new File(goldSummLoc+File.separator+summName+".human.xml-PreProcessed.xml");
                    if(testFile.exists()) {
                         human=Factory.newDocument(new URL("file://"+goldSummLoc+File.separator+summName+".human.xml-PreProcessed.xml"));
                        CompareRPwithSummary(rp,human,wordVecAnnType3,preFeature3+"_"+absFeature3);
                      //  CompareRPwithSummary(rp,human,wordVecAnnType2,preFeature2+"_"+absFeature2);
                    } else {
                        System.out.println(testFile.getName()+" MISSING!!!");
                    }
                    
                    
                    
                    
                   
            
                  
                    pw=new PrintWriter(new FileWriter(refLocBis+File.separator+rpName));
                    pw.print(rp.toXml());
                    pw.flush ();
                    pw.close();
                    
                    // clean corpus
                    Factory.deleteResource(rp);

                }
                
     
        
        
            }
            
        } catch(GateException ge) {
            ge.printStackTrace();
        } catch (MalformedURLException ex) {
            Logger.getLogger(CompareRPwithModel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CompareRPwithModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    
        
    
    }
        
}
