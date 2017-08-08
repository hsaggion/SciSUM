/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.upf.taln.scisumm.utils;

import static edu.upf.taln.scisumm.utils.CheckSimilarities.clusterName;
import static edu.upf.taln.scisumm.utils.CheckSimilarities.documents;
import static edu.upf.taln.scisumm.utils.CheckSimilarities.locCluster;
import gate.Annotation;
import gate.AnnotationSet;
import gate.Corpus;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.creole.ResourceInstantiationException;
import gate.util.GateException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author horacio
 */
public class SimilarityToCPCitations {
    
    
      
    /*public static String cpLoc="/home/horacio/temp/SciSUM-2016/test/gate_files_processed";
    public static String rpGoldLoc="/home/horacio/temp/SciSUM-2016/test/gold_scores_features";
    public static String rpGoldFeatLoc="/home/horacio/temp/SciSUM-2016/test/gold_scores_features";
    */
  /*
    public static String cpLoc="/home/horacio/temp/SciSUM-2016/test-last/gate_files_processed";
    public static String rpGoldLoc="/home/horacio/temp/SciSUM-2016/test-last/gold_scores_features";
    public static String rpGoldFeatLoc="/home/horacio/temp/SciSUM-2016/test-last/gold_scores_features";
    */
    
     public static String cpLoc="/home/horacio/work/SciSUM-2016/DATA-2017-CLUSTERS-VEC-TEST";
    public static String rpGoldLoc="/home/horacio/work/SciSUM-2016/DATA-2017-CLUSTERS-VEC-TEST";
    public static String rpGoldFeatLoc="/home/horacio/work/SciSUM-2016/DATA-2017-CLUSTERS-VEC-OUT";
    
    /*
    public static String cpLoc="SciSUM-2017/TEST-DATA-VECS";
    public static String rpGoldLoc="SciSUM-2017/TEST-DATA-VECS";
    public static String rpGoldFeatLoc="SciSUM-2017/TEST-DATA-VECS-RPS";
    */
    public static void main(String[] args) {
     /*   cpLoc=args[0];
        rpGoldLoc=args[1];
        rpGoldFeatLoc=args[2];
        */
        cpLoc=args[1]+File.separator+cpLoc;
        rpGoldLoc=args[1]+File.separator+rpGoldLoc;
        rpGoldFeatLoc=args[1]+File.separator+rpGoldFeatLoc;
        
        try {
            Gate.init();
            //processAllRPs();
            processAllRPsStats();
        } catch(GateException ge) {
            ge.printStackTrace();
        }
    }
    
    
        public static void processAllRPsStats()  {
        PrintWriter pw;
        File inDir=new File(rpGoldLoc);
        System.out.println(inDir.getAbsoluteFile());
        File[] rpList=inDir.listFiles();
        String rpName;
        String rpLoc;
        Document rp;
        String cluster;
        Corpus corpus;
        ArrayList<String> cpNames;
        int count=0;
        File aux;
        String posfix="-PreProcessed.xml";
        // String posfix=".xml";
        for(File file : rpList) {
          
                try {
                    cluster=file.getName();
                    cluster=cluster.replaceAll(".xml", "");
                    aux=new File(rpGoldFeatLoc+File.separator+cluster+posfix);
                    if(!aux.exists()) {
                    //rpName=cluster+".xml";
                //    if(!(new File(rpGoldFeatLoc+File.separator+rpName)).exists()) {
                        
                        System.out.println(cluster+"...");
                        rpLoc =(new File(file.getAbsoluteFile()+File.separator+cluster+posfix)).getAbsolutePath();
                        System.out.println(rpLoc);
                      //  rpLoc=file.getAbsolutePath();
                        rp=Factory.newDocument(new URL("file:///"+rpLoc));
                        
                        //corpus=loadCPs(cluster);
                        cpNames=loadCPNames(cluster);
                      //  System.out.println(corpus.size());
                        
                        //CompareCPsWithRPMaxMinAvg(rp,corpus);
                        /*
                        CompareCPsWithRPMaxMinAvg(rp,
                                corpus,
                                "Sentence",
                                "Analysis",
                                "Vector_Norm",
                                "CITATIONS",
                                "summa");
                         CompareCPsWithRPMaxMinAvg(rp,
                                corpus,
                                "Sentence",
                                "Analysis",
                                "aclSentVec",
                                "CITATIONS",
                                "acl");
                           CompareCPsWithRPMaxMinAvg(rp,
                                corpus,
                                "Sentence",
                                "Analysis",
                                "googleSentVec",
                                "CITATIONS",
                                "google");
                           */
                           CompareCPsWithRPMaxMinAvgBis(rp,
                                cpNames,
                                "Sentence",
                                "Analysis",
                                "Vector_Norm",
                                "CITATIONS",
                                "summa");
                         CompareCPsWithRPMaxMinAvgBis(rp,
                                cpNames,
                                "Sentence",
                                "Analysis",
                                "aclSentVec",
                                "CITATIONS",
                                "acl");
                           CompareCPsWithRPMaxMinAvgBis(rp,
                                cpNames,
                                "Sentence",
                                "Analysis",
                                "googleSentVec",
                                "CITATIONS",
                                "google");
                        pw=new PrintWriter(new FileWriter(rpGoldFeatLoc+File.separator+cluster+posfix));
                        pw.print(rp.toXml());
                        pw.flush();
                        pw.close();
                        // clean corpus
                        Factory.deleteResource(rp);
                  //      removeDocs(corpus);
                    //    Factory.deleteResource(corpus);
              //      } else {
               //         System.out.println(cluster+"... skipping");
                //    }
                    } else {
                        
                          System.out.println(cluster+" already done!");
                    }
                    
                } catch (ResourceInstantiationException ex) {
                    Logger.getLogger(SimilarityToCPCitations.class.getName()).log(Level.SEVERE, null, ex);
                } catch (MalformedURLException ex) {
                    Logger.getLogger(SimilarityToCPCitations.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(SimilarityToCPCitations.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            
                
              
                
            
            
        }
        
        
        
    }
    
    public static void processAllRPs()  {
        PrintWriter pw;
        File inDir=new File(rpGoldLoc);
        File[] rpList=inDir.listFiles();
        String rpName;
        String rpLoc;
        Document rp;
        String cluster;
        Corpus corpus;
        for(File file : rpList) {
         //   try {
                try {
                    rpName=file.getName();
                    cluster=rpName.replace(".xml", "");
              //      if(!(new File(rpGoldFeatLoc+File.separator+rpName)).exists()) {
                        
                        System.out.println(cluster+"...");
                        rpLoc =file.getAbsolutePath();
                        rp=Factory.newDocument(new URL("file:///"+rpLoc));
                        corpus=loadCPs(cluster);
                        System.out.println(corpus.size());
                        CompareCPsWithRP(rp,corpus);
                        pw=new PrintWriter(new FileWriter(rpGoldFeatLoc+File.separator+rpName));
                        pw.print(rp.toXml());
                        pw.flush();
                        pw.close();
                        // clean corpus
                        Factory.deleteResource(rp);
                        removeDocs(corpus);
                        Factory.deleteResource(corpus);
                //    } else {
                //        System.out.println(cluster+"... skipping");
                //    }
                    
                } catch (ResourceInstantiationException ex) {
                    Logger.getLogger(SimilarityToCPCitations.class.getName()).log(Level.SEVERE, null, ex);
                } catch (MalformedURLException ex) {
                    Logger.getLogger(SimilarityToCPCitations.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(SimilarityToCPCitations.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                
               // System.in.read();
                
         //   } catch (IOException ex) {
          //      Logger.getLogger(SimilarityToCPCitations.class.getName()).log(Level.SEVERE, null, ex);
           // }
            
        }
        
        
        
    }
    public static void removeDocs(Corpus c) {
        Object[] objs=c.toArray();
        Document d;
        for(int o=0;o<objs.length;o++) {
            d=(Document)objs[o];
            Factory.deleteResource(d);
        }
        
        
    }
    
    public static void CompareCPsWithRP(Document rp, Corpus cps) {
        
        AnnotationSet citations;
        AnnotationSet cpVectors;
        AnnotationSet rpVectors;
        AnnotationSet rpSentences;
       
        Long startS,endS;
        Annotation cit;
        Long citS, citE;
        FeatureMap rpVec;
        Annotation rpVecAnn;
        FeatureMap sentfm;
        FeatureMap citVec;
        
        
        rpSentences=rp.getAnnotations().get("Sentence");
        rpVectors=rp.getAnnotations().get("Vector_Norm");
        
        // collect all citation vectors anc create a centroid
        
        Set allCPVecs=new HashSet();
        AnnotationSet auxVecs;
        for(Document cp : cps) {
            citations=cp.getAnnotations("CITATIONS");
            cpVectors=cp.getAnnotations().get("Vector_Norm");
            for(Annotation citation : citations) {
                citS=citation.getStartNode().getOffset();
                citE=citation.getEndNode().getOffset();
                auxVecs=cpVectors.get(citS,citE);
                if(auxVecs.size()==1) {
                    cit=auxVecs.iterator().next();
                    citVec=cit.getFeatures();
                    allCPVecs.add(citVec);
                }
                
            }
            
            
        }
        
        // centroid of citations in citing paper
        FeatureMap cpCentroid=summa.centroid.Centroid.Centroid1(allCPVecs);
        AnnotationSet auxRPVecs;
        double cosine;
        for(Annotation sentence : rpSentences){
            sentfm=sentence.getFeatures();
            startS=sentence.getStartNode().getOffset();
            endS  =sentence.getEndNode().getOffset();
            auxRPVecs=rpVectors.get(startS,endS);
            if(auxRPVecs.size()==1) {
                rpVecAnn=auxRPVecs.iterator().next();
                rpVec=rpVecAnn.getFeatures();
                cosine=summa.scorer.Cosine.cosine1(rpVec, cpCentroid);
                sentfm.put("cps_sim", cosine+"");
                
            } else {
                sentfm.put("cps_sim","0.0");
            }
        }
        
        
        
        
        
    }
    
    
    
    public static void CompareCPsWithRPMaxMinAvg(Document rp, Corpus cps) {
        
        AnnotationSet citations;
        AnnotationSet cpVectors;
        AnnotationSet rpVectors;
        AnnotationSet rpSentences;
       
        Long startS,endS;
        Annotation cit;
        Long citS, citE;
        FeatureMap rpVec;
        Annotation rpVecAnn;
        FeatureMap sentfm;
        FeatureMap citVec;
        
        
        rpSentences=rp.getAnnotations().get("Sentence");
        rpVectors=rp.getAnnotations().get("Vector_Norm");
        
        // collect all citation vectors anc create a centroid
        
        Set<FeatureMap> allCPVecs=new HashSet();
        AnnotationSet auxVecs;
        for(Document cp : cps) {
            citations=cp.getAnnotations("CITATIONS");
            cpVectors=cp.getAnnotations().get("Vector_Norm");
            for(Annotation citation : citations) {
                citS=citation.getStartNode().getOffset();
                citE=citation.getEndNode().getOffset();
                auxVecs=cpVectors.get(citS,citE);
                if(auxVecs.size()==1) {
                    cit=auxVecs.iterator().next();
                    citVec=cit.getFeatures();
                    allCPVecs.add(citVec);
                }
                
            }
            
            
        }
        
        
        AnnotationSet auxRPVecs;
        double cosine;
        double max=0.0;
        double min=1.0;
        double total;
        double avg;
        for(Annotation sentence : rpSentences){
            sentfm=sentence.getFeatures();
            startS=sentence.getStartNode().getOffset();
            endS  =sentence.getEndNode().getOffset();
            auxRPVecs=rpVectors.get(startS,endS);
            if(auxRPVecs.size()==1) {
                rpVecAnn=auxRPVecs.iterator().next();
                rpVec=rpVecAnn.getFeatures();
                total=0.0;
                max=0.0;
                min=1.0;
                
                for(FeatureMap cpfm : allCPVecs) {
                    cosine=summa.scorer.Cosine.cosine1(rpVec, cpfm);
                    if(cosine>max) {
                        max=cosine;
                    } 
                    if(cosine<min) {
                        min=cosine;
                    }
                    total=total+cosine;
                    
                }
                
                avg=total/(allCPVecs.size());
                sentfm.put("cps_max",max+"");
                sentfm.put("cps_min",min+"");
                sentfm.put("cps_avg",avg+"");
            } else {
                sentfm.put("cps_max","0.0");
                sentfm.put("cps_min", "0.0");
                sentfm.put("cps_avg","0.0");
            }
        }
        
        
        
        
        
    }
    
    
     public static void CompareCPsWithRPMaxMinAvg(Document rp, Corpus cps,
             String sentAnn,
             String inAnnSet, 
             String vecName, 
             String cpRefAnnSet,
             String feature) {
        
        AnnotationSet citations;
        AnnotationSet cpVectors;
        AnnotationSet rpVectors;
        AnnotationSet rpSentences;
       
        Long startS,endS;
        Annotation cit;
        Long citS, citE;
        FeatureMap rpVec;
        Annotation rpVecAnn;
        FeatureMap sentfm;
        FeatureMap citVec;
        
        
        rpSentences=rp.getAnnotations(inAnnSet).get(sentAnn);
        rpVectors=rp.getAnnotations(inAnnSet).get(vecName);
        if(rpVectors.isEmpty()) {
            
            System.out.println(rp.getName()+" NO "+vecName+" vectors!!!");
            return;
        }
        
        // collect all citation vectors anc create a centroid
        
        Set<FeatureMap> allCPVecs=new HashSet();
        AnnotationSet auxVecs;
        for(Document cp : cps) {
            citations=cp.getAnnotations(cpRefAnnSet);
            cpVectors=cp.getAnnotations(inAnnSet).get(vecName);
            for(Annotation citation : citations) {
                citS=citation.getStartNode().getOffset();
                citE=citation.getEndNode().getOffset();
                auxVecs=cpVectors.get(citS,citE);
                if(auxVecs.size()==1) {
                    cit=auxVecs.iterator().next();
                    citVec=cit.getFeatures();
                    allCPVecs.add(citVec);
                }
                
            }
            
            
        }
        
        
        AnnotationSet auxRPVecs;
        double cosine;
        double max=0.0;
        double min=1.0;
        double total;
        double avg;
        for(Annotation sentence : rpSentences){
            sentfm=sentence.getFeatures();
            startS=sentence.getStartNode().getOffset();
            endS  =sentence.getEndNode().getOffset();
            auxRPVecs=rpVectors.get(startS,endS);
            if(auxRPVecs.size()==1) {
                rpVecAnn=auxRPVecs.iterator().next();
                rpVec=rpVecAnn.getFeatures();
                total=0.0;
                max=0.0;
                min=1.0;
                
                for(FeatureMap cpfm : allCPVecs) {
                    cosine=summa.scorer.Cosine.cosine1(rpVec, cpfm);
                    if(cosine>max) {
                        max=cosine;
                    } 
                    if(cosine<min) {
                        min=cosine;
                    }
                    total=total+cosine;
                    
                }
                
                avg=total/(allCPVecs.size());
                sentfm.put(feature+"_max",max+"");
                sentfm.put(feature+"_min",min+"");
                sentfm.put(feature+"_avg",avg+"");
            } else {
                sentfm.put(feature+"_max","0.0");
                sentfm.put(feature+"_min", "0.0");
                sentfm.put(feature+"_avg","0.0");
            }
        }
        
        
        
        
        
    }
    
     
     
     
     public static void CompareCPsWithRPMaxMinAvgBis(Document rp, ArrayList<String> cpList,
             String sentAnn,
             String inAnnSet, 
             String vecName, 
             String cpRefAnnSet,
             String feature) {
        
        AnnotationSet citations;
        AnnotationSet cpVectors;
        AnnotationSet rpVectors;
        AnnotationSet rpSentences;
       
        Long startS,endS;
        Annotation cit;
        Long citS, citE;
        FeatureMap rpVec;
        Annotation rpVecAnn;
        FeatureMap sentfm;
        FeatureMap citVec;
        
        
        rpSentences=rp.getAnnotations(inAnnSet).get(sentAnn);
        rpVectors=rp.getAnnotations(inAnnSet).get(vecName);
        if(rpVectors.isEmpty()) {
            
            System.out.println(rp.getName()+" NO "+vecName+" vectors!!!");
            return;
        }
        
        // collect all citation vectors anc create a centroid
        
        Set<FeatureMap> allCPVecs=new HashSet();
        AnnotationSet auxVecs;
        Document cp;
        for(String cpLoc : cpList) {
            try {
                cp=Factory.newDocument(new URL("file:///"+cpLoc));
                System.out.println(cpLoc);
                citations=cp.getAnnotations(cpRefAnnSet);
                cpVectors=cp.getAnnotations(inAnnSet).get(vecName);
                for(Annotation citation : citations) {
                    citS=citation.getStartNode().getOffset();
                    citE=citation.getEndNode().getOffset();
                    auxVecs=cpVectors.get(citS,citE);
                    if(auxVecs.size()==1) {
                        cit=auxVecs.iterator().next();
                        citVec=cit.getFeatures();
                        allCPVecs.add(citVec);
                    }
                    
                }
                Factory.deleteResource(cp);
            } catch (MalformedURLException ex) {                
               ex.printStackTrace();
            } catch (ResourceInstantiationException ex) {
               ex.printStackTrace();
            }
            
            
        }
        
        
        AnnotationSet auxRPVecs;
        double cosine;
        double max=0.0;
        double min=1.0;
        double total;
        double avg;
        for(Annotation sentence : rpSentences){
            sentfm=sentence.getFeatures();
            startS=sentence.getStartNode().getOffset();
            endS  =sentence.getEndNode().getOffset();
            auxRPVecs=rpVectors.get(startS,endS);
            if(auxRPVecs.size()==1) {
                rpVecAnn=auxRPVecs.iterator().next();
                rpVec=rpVecAnn.getFeatures();
                total=0.0;
                max=0.0;
                min=1.0;
                
                for(FeatureMap cpfm : allCPVecs) {
                    cosine=summa.scorer.Cosine.cosine1(rpVec, cpfm);
                    if(cosine>max) {
                        max=cosine;
                    } 
                    if(cosine<min) {
                        min=cosine;
                    }
                    total=total+cosine;
                    
                }
                
                avg=total/(allCPVecs.size());
                sentfm.put(feature+"_max",max+"");
                sentfm.put(feature+"_min",min+"");
                sentfm.put(feature+"_avg",avg+"");
            } else {
                sentfm.put(feature+"_max","0.0");
                sentfm.put(feature+"_min", "0.0");
                sentfm.put(feature+"_avg","0.0");
            }
        }
        
        
        
        
        
    }
     
        public static ArrayList loadCPNames(String cluster) {
        ArrayList<String> list=new ArrayList();
        
           
            File inDir=new File(cpLoc+File.separator+cluster);
            File[] files=inDir.listFiles();
            String floc;
            String fname;
            //Document document;
            
            for(File file : files) {
                fname=file.getName();
                floc=file.getAbsolutePath();
                if(fname.endsWith(".xml") && !fname.startsWith(cluster)) {
                   list.add(floc);
                    
                }
            }   
         
        return list;
        
    }
   public static Corpus loadCPs(String cluster) {
        Corpus corpus=null;
        try {
            corpus=Factory.newCorpus("");
            File inDir=new File(cpLoc+File.separator+cluster);
            File[] files=inDir.listFiles();
            String floc;
            String fname;
            Document document;
            
            for(File file : files) {
                fname=file.getName();
                floc=file.getAbsolutePath();
                if(fname.endsWith(".xml") && !fname.startsWith(cluster)) {
                    try {
                        document=Factory.newDocument(new
                    URL("file:///"+floc));
                        corpus.add(document);
                        
                    } catch (ResourceInstantiationException ex) {
                        ex.printStackTrace();
                    } catch (MalformedURLException ex) {
                        ex.printStackTrace();
                    }
                    
                }
            }   
        } catch (ResourceInstantiationException ex) {
           ex.printStackTrace();
        }
        return corpus;
        
    }
    
}
