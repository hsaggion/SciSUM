/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.upf.taln.scisumm.utils;
//import static edu.upf.taln.scisumm.utils.ComputeTitleSimilarity.firstSim;
import static edu.upf.taln.scisumm.utils.ComputeTitleSimilarity.rpGoldFeatLoc;
import gate.*;
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
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author horacio
 */
public class CreateSentWordVecs {
    
    public static String rpLoc="/homedtic/hsaggion/SciSUM-2017/TEST-DATA";
 //   public static String summaryLoc="/home/horacio/work/SciSUM-2016/TEST-2017";
  //   public static String summaryLoc="/home/horacio/work/SciSUM-2016/gold-summaries-1";
     public static String summaryLoc="/homedtic/hsaggion/SciSUM-2016/bad-abstract-1";
    public static String rpOutLoc="/homedtic/hsaggion/SciSUM-2017/TEST-DATA-VECS";
   //  public static String rpOutLoc="/home/horacio/work/SciSUM-2016/gold-summaries-2";
    
    
    
    public static void createWordVecs(Document doc,String vecAnnSetName, String wordVecName,
            String sentWordVec) {
        AnnotationSet wordVecSet;
        AnnotationSet all;
        AnnotationSet tokens;
        AnnotationSet auxTokens;
        AnnotationSet words;
        AnnotationSet sents;
        Long startS, endS;
      
        
        Iterator<Annotation> ite;
        Annotation word;
        Annotation sentence;
        Annotation token;
        FeatureMap fm;
        Long startT, endT;
        Long startW, endW;
        FeatureMap wf;
        String str;
        String strTok;
        String values;
        StringTokenizer tokenizer;
        double[] numvec;
        double[] summVec;
        String val;
        double dval;
        int index;
        ArrayList<Annotation> tokenList;
        ArrayList<Annotation> sentList;
        AnnotationSet auxWrdVecSet;
        
        FeatureMap wrd_vec_fm;
                all=doc.getAnnotations("Analysis");
                tokens=all.get("Token");
                sents=all.get("Sentence");
                wordVecSet=doc.getAnnotations(vecAnnSetName);
                words=wordVecSet.get(wordVecName);
                // for each sentence
                sentList=new ArrayList(sents);
                int wrdCount;
                for(int s=0;s<sentList.size();s++) {
                    summVec=null;
                    sentence=sentList.get(s);
                    startS=sentence.getStartNode().getOffset();
                    endS  =sentence.getEndNode().getOffset();
                    auxTokens=tokens.get("Token",startS,endS);
                    tokenList=new ArrayList(auxTokens);
                    wrdCount=0;
                    for(int t=0;t<tokenList.size();t++) {
                        token=tokenList.get(t);
                        fm=token.getFeatures();
                        startT=token.getStartNode().getOffset();
                        endT  =token.getEndNode().getOffset();
                        auxWrdVecSet=words.get(startT,endT);
                        if(!auxWrdVecSet.isEmpty()) {
                            word=auxWrdVecSet.iterator().next();
                            wf=word.getFeatures();
                            str=(String)wf.keySet().iterator().next();
                            values=(String)wf.get(str);
                            tokenizer=new StringTokenizer(values," ");
                            numvec=new double[tokenizer.countTokens()];
                            index=0;
                            while(tokenizer.hasMoreTokens()) {
                                val=tokenizer.nextToken();
                                dval=(new Double(val)).doubleValue();
                                numvec[index]=dval;
                            //    System.out.println(dval);
                                index++;
                            }
                            summVec=addToVector(numvec,summVec);
                            wrdCount++;
                        }




                    }
                    wrd_vec_fm=Factory.newFeatureMap();
                    if(summVec!=null) {
                        for(int i=0;i<summVec.length;i++) {

                            summVec[i]=summVec[i]/wrdCount;
                            wrd_vec_fm.put("d_"+String.format("%03d", i), summVec[i]+"");


                        }
                    } 
                    try {
                        all.add(startS,endS,sentWordVec,wrd_vec_fm);
                    } catch (InvalidOffsetException ioe) {
                       ioe.printStackTrace();
                    }
                            
                         
    }
    }
    
    public static void mainSummary(String[] args) {
        PrintWriter pw;
        File inDir=new File(rpLoc);
      //   File inDir=new File(summaryLoc);
        File[] rpList=inDir.listFiles();
        String rpCluster;
        String rpName;
        String rpLoc;
        Document rp;
        String cluster;
        AnnotationSet wordVecSet;
        AnnotationSet all;
        AnnotationSet tokens;
        AnnotationSet auxTokens;
        AnnotationSet words;
        AnnotationSet sents;
        Long startS, endS;
        
        Iterator<Annotation> ite;
        Annotation word;
        Annotation sentence;
        Annotation token;
        FeatureMap fm;
        Long startT, endT;
        Long startW, endW;
        FeatureMap wf;
        String str;
        String strTok;
        String values;
        StringTokenizer tokenizer;
        double[] numvec;
        double[] summVec;
        String val;
        double dval;
        int index;
        ArrayList<Annotation> tokenList;
        ArrayList<Annotation> sentList;
        AnnotationSet auxWrdVecSet;
        
        Document summary;
        String summaryName;
        String summaryLoc;
        
        
        FeatureMap wrd_vec_fm;
        try {
            
            Gate.init();
            // for each directory
            for(File file : rpList) {
                summaryName=file.getName();
                System.out.println(summaryName);
                if(summaryName.contains("abstract") || summaryName.contains("community") || summaryName.contains("human")) {
                    
                    summaryLoc=file.getAbsolutePath();
                    summary=Factory.newDocument(new URL("file:///"+summaryLoc));
                    createWordVecs(summary,"Word2Vec", "GoogleNews",
            "googleSentVec");

                    createWordVecs(summary,"Word2Vec", "ACL",
        "aclSentVec");
                    pw=new PrintWriter(new FileWriter(rpOutLoc+File.separator+summaryName));
                    pw.print(summary.toXml());
                    pw.flush();
                    pw.close();

                    // clean corpus
                    Factory.deleteResource(summary);
                }
              
            
            }
              
            /**
            
            for(File file : rpList) {
                    if(file.isDirectory() && file.getName().contains("-")) {
                        rpCluster=file.getName();
                        System.out.println(rpCluster+"...");
                        rpLoc =file.getAbsolutePath();
                        rp=Factory.newDocument(new URL("file:///"+rpLoc+File.separator+rpCluster+"-PreProcessed.xml"));
                        createWordVecs(rp,"Word2Vec", "GoogleNews",
            "googleSentVec");

                        createWordVecs(rp,"Word2Vec", "ACL",
            "aclSentVec");
                        pw=new PrintWriter(new FileWriter(rpOutLoc+File.separator+rpCluster+".xml"));
                        pw.print(rp.toXml());
                        pw.flush();
                        pw.close();

                        // clean corpus
                        Factory.deleteResource(rp);
                    }
                       
              
                    
            }
            **/
                
        } catch(GateException ge) {
            ge.printStackTrace();
        } catch (MalformedURLException ex) {
            Logger.getLogger(CreateSentWordVecs.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CreateSentWordVecs.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    
}
    
    
     public static void main(String[] args) {
        PrintWriter pw;
        File inDir=new File(rpLoc);
     
        File[] rpList=inDir.listFiles();
        String rpCluster;
        String rpName;
        String rpLoc;
        Document rp;
        String cluster;
        AnnotationSet wordVecSet;
        AnnotationSet all;
        AnnotationSet tokens;
        AnnotationSet auxTokens;
        AnnotationSet words;
        AnnotationSet sents;
        Long startS, endS;
        
        Iterator<Annotation> ite;
        Annotation word;
        Annotation sentence;
        Annotation token;
        FeatureMap fm;
        Long startT, endT;
        Long startW, endW;
        FeatureMap wf;
        String str;
        String strTok;
        String values;
        StringTokenizer tokenizer;
        double[] numvec;
        double[] summVec;
        String val;
        double dval;
        int index;
        ArrayList<Annotation> tokenList;
        ArrayList<Annotation> sentList;
        AnnotationSet auxWrdVecSet;
        
        File aux;
        
        
        File[] inCluster;
        
        FeatureMap wrd_vec_fm;
        try {
            
            Gate.init();
            // for each directory
            for(File file : rpList) {
                
                cluster=file.getName();
                
                aux=new File(rpOutLoc+File.separator+cluster);
                if(!aux.exists()) { 
                    System.out.println("Processing cluster "+cluster+"....");
                    if(file.isDirectory()) {
                       inCluster=file.listFiles();
                       for(File file1 : inCluster) {
                            rpName=file1.getName();
                            System.out.println(rpName);
                            rpLoc=file1.getAbsolutePath();
                            rp=Factory.newDocument(new URL("file:///"+rpLoc));
                            createWordVecs(rp,"Word2Vec", "GoogleNews",
                    "googleSentVec");
                            createWordVecs(rp,"Word2Vec", "ACL",
                "aclSentVec");
                            aux=new File(rpOutLoc+File.separator+cluster);
                            if(!aux.exists()) aux.mkdir();
                            pw=new PrintWriter(new FileWriter(rpOutLoc+File.separator+cluster+File.separator+rpName));
                            pw.print(rp.toXml());
                            pw.flush();
                            pw.close();
                            // clean corpus
                            Factory.deleteResource(rp);

                       }

                    }
                } else {
                    System.out.println("Ignoring cluster "+cluster);
                }
            }
              
            /**
            
            for(File file : rpList) {
                    if(file.isDirectory() && file.getName().contains("-")) {
                        rpCluster=file.getName();
                        System.out.println(rpCluster+"...");
                        rpLoc =file.getAbsolutePath();
                        rp=Factory.newDocument(new URL("file:///"+rpLoc+File.separator+rpCluster+"-PreProcessed.xml"));
                        createWordVecs(rp,"Word2Vec", "GoogleNews",
            "googleSentVec"

                        createWordVecs(rp,"Word2Vec", "ACL",
            "aclSentVec");
                        pw=new PrintWriter(new FileWriter(rpOutLoc+File.separator+rpCluster+".xml"));
                        pw.print(rp.toXml());
                        pw.flush();
                        pw.close();

                        // clean corpus
                        Factory.deleteResource(rp);
                    }
                       
              
                    
            }
            **/
                
        } catch(GateException ge) {
            ge.printStackTrace();
        } catch (MalformedURLException ex) {
            Logger.getLogger(CreateSentWordVecs.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CreateSentWordVecs.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    
}
    
  public static double[]  addToVector(double[] numVec,double[] summVec) {
      if(summVec==null) {
          summVec=new double[numVec.length];
          for(int i=0;i<numVec.length;i++) {
              summVec[i]=0.0;
          }
          
      } 
      for(int i=0;i<numVec.length;i++) {
          summVec[i]=summVec[i]+numVec[i];
      }
      return summVec;
      
  }  
    
    
    
}
