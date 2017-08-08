/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.upf.taln.scisumm.utils;

import static edu.upf.taln.scisumm.utils.SimilarityToCPCitations.cpLoc;
import static edu.upf.taln.scisumm.utils.SimilarityToCPCitations.processAllRPsStats;
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
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;
import java.util.TreeSet;

/**
 * 
 * @author horacio
 */
public class CreateFacetClassificationData {
    
    
     
      
    public static String cpLoc="/home/horacio/temp/SciSUM-2016/DIV-PROCESS/gate_files_processed_train";
    public static String rpGoldLoc="/home/horacio/temp/SciSUM-2016/DIV-PROCESS/gate_files_processed_train";
    public static String rpGoldFacets="/home/horacio/temp/SciSUM-2016/DIV-PROCESS/gate_files_train_facets";
    
    
    
    
    public static void main(String[] args) {
        try {
            Gate.init();
            processAllRPs();
          
        } catch(GateException ge) {
            ge.printStackTrace();
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
          //  try {
                try {
                    rpName=file.getName();
                    cluster=rpName.replace(".xml", "");
                 
                    if(!(new File(rpGoldFacets+File.separator+rpName)).exists()) {
                        
                  //      System.out.println(cluster+"...");
                        rpLoc =file.getAbsolutePath();
                       
                        rp=Factory.newDocument(new URL("file:///"+rpLoc+File.separator+cluster+".xml_DRI.xml"));
                        corpus=loadCPs(cluster,cpLoc);
                      //  System.out.println(corpus.size());
                        ExtractRPCPRelations(rp,corpus);
                       /* pw=new PrintWriter(new FileWriter(rpGoldFacets+File.separator+rpName));
                        pw.print(rp.toXml());
                        pw.flush();
                        pw.close();
                               */
                        // clean corpus
                        Factory.deleteResource(rp);
                        SimilarityToCPCitations.removeDocs(corpus);
                        Factory.deleteResource(corpus);
                    } else {
                       // System.out.println(cluster+"... skipping");
                    }
                    
                } catch (ResourceInstantiationException ex) {
                   ex.printStackTrace();
                } catch (MalformedURLException ex) {
                    ex.printStackTrace();
                } catch (IOException ex) {
                   ex.printStackTrace();
                }
                
                
             //   System.in.read();
                
           /* } catch (IOException ex) {
                ex.printStackTrace();
            }
                   */
            
        }
        
        
        
    }
    
    public static void ExtractRPCPRelations(Document rp, Corpus cps) {
        
        String rpContent=rp.getContent().toString();
        String cpContent;
        
        AnnotationSet references=rp.getAnnotations("REFERENCES");
       
        AnnotationSet citations;
        FeatureMap cpfm;
        FeatureMap rpfm;
        String cpID;
        String rpID;
        Long startCP, endCP;
        Long startRP, endRP;
        String citingStr;
        String citedStr;
        String disFacet;
        for(Document cp : cps) {
         //   System.out.println(cp.getName());
            citations=cp.getAnnotations("CITATIONS");
            cpContent=cp.getContent().toString();
            
            for(Annotation citation : citations) {
                startCP=citation.getStartNode().getOffset();
                endCP  =citation.getEndNode().getOffset();
                cpfm=citation.getFeatures();
                cpID=(String)cpfm.get("id");
              //  System.out.println("CIT :"+cpID);
                for(Annotation reference : references) {
                //    System.out.println(reference.getType());
                    startRP=reference.getStartNode().getOffset();
                    endRP  =reference.getEndNode().getOffset();
                    rpfm=reference.getFeatures();
                    rpID=(String)rpfm.get("id");
                   // System.out.println("REF :"+rpID);
                    if(rpID.equals(cpID)) {
                        citedStr=rpContent.substring(startRP.intValue(),
                                endRP.intValue());
                        citedStr = citedStr.replaceAll("[\u0000-\u001f]", "");
                        citedStr = citedStr.replaceAll("\'", "");
                        citingStr=cpContent.substring(startCP.intValue(),
                        endCP.intValue());
                        citingStr=  citingStr.replaceAll("[\u0000-\u001f]", "");
                        citingStr = citingStr.replaceAll("\'", "");
                        System.out.print("\'"+citedStr+"\'"+","+
                                "\'"+citingStr+"\'");
                        disFacet=(String)rpfm.get("Discourse_Facet");
                        System.out.println(","+disFacet);
                       
                        
                    }
                    
                }
                
            }
            
            
        }
        
        
        
        
    }
    
    
    
    
     public static Corpus loadCPs(String cluster,String cpLoc) {
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
