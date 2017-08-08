/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.upf.taln.scisumm.utils;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;
import gate.Gate;
import gate.util.GateException;
import gate.Factory;
import gate.FeatureMap;
import gate.creole.ResourceInstantiationException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
        

/**
 *
 * @author UPF
 */
public class CheckSimilarities {
    
    public static String locCluster="C:\\work\\TALN\\SUMMAScientifica\\experiments\\gate-docs-processed\\gate_files";
    public static String outLoc="C:\\work\\TALN\\SUMMAScientifica\\experiments\\gate-docs-processed\\gate_files_out";
    public static String clusterName="C94-2154";
    public static Map<String,Document> documents;
    public static void loadCPs() {
        File inDir=new File(locCluster+File.separator+clusterName);
        File[] files=inDir.listFiles();
        String floc;
        String fname;
        Document document;
        documents=new TreeMap();
        for(File file : files) {
            fname=file.getName();
            floc=file.getAbsolutePath();
            if(fname.endsWith(".xml") && !fname.startsWith(clusterName)) {
                try {
                    document=Factory.newDocument(new
                URL("file:///"+floc));
                    documents.put(fname.replace(".xml", ""), document);
                    
                } catch (ResourceInstantiationException ex) {
                    ex.printStackTrace();
                } catch (MalformedURLException ex) {
                   ex.printStackTrace();
                }
                
            }
        }
        
    }
    public static void main(String[] args) {
        // annotator similarity + max id
        Map<String,Integer> best_matches=new TreeMap();
        Map<String,Double> max_matches=new TreeMap();
        Map<String,ArrayList<ValueID>> all_matches=new TreeMap();
        try {
            Gate.init();
            loadCPs();
            Document doc=Factory.newDocument(
                new URL("file:///"+locCluster+File.separator+clusterName+File.separator+clusterName+".xml"));
            AnnotationSet sentences=doc.getAnnotations().get("Sentence");
            FeatureMap sentfm;
            String fname;
            Double fval;
            Double currMax;
            Integer sentID;
            String annotator;
            ArrayList<ValueID> listOfValues;
            for(Annotation sentence: sentences) {
                sentfm=sentence.getFeatures();
                sentID=sentence.getId();
                for(Object feature : sentfm.keySet()) {
                    fname=feature.toString();
                    if(fname.startsWith("sim")) {
                        annotator=fname.replace("sim_", "");
                        if(max_matches.containsKey(annotator)) {
                            currMax=max_matches.get(annotator);
                            
                        } else {
                            currMax=new Double(0);
                            max_matches.put(annotator, currMax);
                        }
                        
                        fval=(Double)sentfm.get(feature);
                        
                        if(all_matches.containsKey(feature)) {
                            listOfValues=all_matches.get(feature);
                        } else {
                            listOfValues=new ArrayList();
                        }
                        listOfValues.add(new ValueID(sentID,fval));
                        all_matches.put(fname, listOfValues);
                        
                        if(fval.doubleValue()>currMax.doubleValue()) {                            
                            max_matches.put(annotator, fval);
                            best_matches.put(annotator, sentID);
                        } 
                    
                    }
                }
            }
            String cp_key;
            int pos;
            int pos1;
            int pos2;
            Document cp;
            AnnotationSet citations;
            AnnotationSet matchingAnns;
            AnnotationSet cpVectors;
            String annotatorKey;
            FeatureMap filter;
            String citID;
            Long startC, endC;
            Long startS, endS;
            AnnotationSet rpVectors=doc.getAnnotations().get("Vector_Norm");
            Annotation rpVector;
            Annotation rpSentence;
            Annotation cpVector;
            for(String key: best_matches.keySet()) {
                System.out.println(key+" = "+best_matches.get(key));
                sentID=best_matches.get(key);
                rpSentence=sentences.get(sentID);
                startS=rpSentence.getStartNode().getOffset();
                endS  =rpSentence.getEndNode().getOffset();
                rpVector=rpVectors.get(startS,endS).iterator().next();
                System.out.println("RP VECTOR");
                System.out.println(rpVector);
                pos=key.indexOf(clusterName);
                pos2=key.lastIndexOf("_");
                citID=key.substring(0, pos2);
                cp_key=key.substring(pos+9, pos+9+8);
                annotatorKey=key.substring(pos+9+9, key.length());
                pos1=annotatorKey.lastIndexOf("_");
                annotatorKey=annotatorKey.substring(0, pos1);
                System.out.println(annotatorKey);
                System.out.println(citID);
                cp=documents.get(cp_key);
                citations=cp.getAnnotations("CITATIONS");
                cpVectors=cp.getAnnotations().get("Vector_Norm");
                filter=Factory.newFeatureMap();
                filter.put("id",citID);
                matchingAnns=citations.get(annotatorKey,filter);
                System.out.println("CP VECTORS");
                for(Annotation ann: matchingAnns) {
                    startC=ann.getStartNode().getOffset();
                    endC  =ann.getEndNode().getOffset();
                    cpVector=cpVectors.get(startC,endC).iterator().next();
                    System.out.println(summa.scorer.Cosine.cosine1(rpVector.getFeatures(), cpVector.getFeatures()));
                    
                }
               
            }
            
            
            
            // extract top best scores
            
            int top=3;
            Comparator comp=ValueID.valueComparator();
            Iterator iteAnnotator=all_matches.keySet().iterator();
            ValueID valID;
            AnnotationSet TopMatches=doc.getAnnotations("TopMatches");
            Annotation sent;
            FeatureMap best_match_fm;
            while(iteAnnotator.hasNext()) {
                annotator=iteAnnotator.next().toString();
                listOfValues=all_matches.get(annotator);
                Collections.sort(listOfValues,comp);
                System.out.println("ANNOTATOR = "+annotator);
                for(int k=0;k<top;k++) {
                    valID=listOfValues.get(k);
                    System.out.println(valID.id+"="+valID.value);
                    sent=sentences.get(valID.id);
                    startS=sent.getStartNode().getOffset();
                    endS  =sent.getEndNode().getOffset();
                    best_match_fm=Factory.newFeatureMap();
                    best_match_fm.put("MatchCitanceID",annotator.replace("sim_", ""));
                    best_match_fm.put("MatchSimValue",valID.value);
                    TopMatches.add(startS,endS,"Match",best_match_fm);
                }
                
                
            }
            
            PrintWriter pw;
            File outDir=new File(outLoc+File.separator+clusterName);
            if(!outDir.exists()) outDir.mkdir();
            pw=new PrintWriter(new FileWriter(outLoc+File.separator+clusterName+File.separator+clusterName+".xml"));
            pw.print(doc.toXml());
            pw.flush();
            pw.close();
            
            
            
            
            
        } catch(GateException ge) {
            ge.printStackTrace();
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            Logger.getLogger(CheckSimilarities.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    
    
}
