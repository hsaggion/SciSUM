/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.upf.taln.scisumm.output;

import static edu.upf.taln.scisumm.utils.SimilarityToCPCitations.cpLoc;
import gate.Annotation;
import gate.AnnotationSet;
import gate.Corpus;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.creole.ResourceInstantiationException;
import gate.util.GateException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author horacio
 */
public class ExtractDataFromLogFile {
    
   // public static String locFiles="/home/horacio/work/data/SciSUMM-2016/Testing";
    public static String locFiles="/home/horacio/temp/SciSUM-2016/SciSumm2016_ClassifiedTestDatasets";
    public static String probFiles="/home/horacio/temp/SciSUM-2016/probabilities";
    public static String outAnnotations="/home/horacio/temp/SciSUM-2016/output_annotations/matches";
    
   // public static String logFileName="consolefacet.txt";
   // public static String classFileName="facet.arff";
      public static String logFileName="console.txt";
      public static String classFileNameFacet="facet.arff";
      
      public static String logFileNameFacet="consolefacet.txt";
      
    public static String classFileName="match.arff";
    
  //   public static String task="Parsing FacetTesting instance";
   public static String task="Parsing Testing instance";
    public static Map<Integer,String> cpNames;
    public static Map<Integer,String> rpNames;
    public static Map<Integer,String> rpIDs;
    public static Map<Integer,String> cpIDs;
    
    public static Map<Integer,String> classification;
    
    public static String[] outFeatures;
    
    public static Map<Integer,Double> probabilities;
    
    public static void loadProbabilities(String probLocation) {
        probabilities=new TreeMap();
        String line;
        BufferedReader reader;
        int pos;
        String inst;
        String prob;
        try {
            reader=new BufferedReader(new FileReader(probLocation));
            while((line=reader.readLine())!=null) {
                pos=line.indexOf(":");
                if(pos>0) {
                    inst=line.substring(0, pos-1);
                    inst=inst.replaceAll(" ", "");
                    prob=line.substring(pos+1,line.length());
                    prob=prob.replaceAll(" ", "");
                    System.out.println(inst+" = "+prob);
                    probabilities.put(new Integer(inst), new Double(prob));
                    
                }
                
            }
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
        
    }
    
    public static void loadFacetClassification(String arffLocation) {
        facetClassification=new TreeMap();
        String line;
        BufferedReader reader;
        String type;
        try {
            reader=new BufferedReader(new FileReader(arffLocation));
            while((line=reader.readLine())!=null) {
                if(line.startsWith("@data")) break;
            }
            int insNum=1;
            int pos1;
            int pos2;
            while((line=reader.readLine())!=null) {
                pos2=line.indexOf("}");
                pos1=line.lastIndexOf(" ");
                type=line.substring(pos1, pos2);
                System.out.println(insNum+"\t"+type);
                facetClassification.put(new Integer(insNum),type);
                insNum++;
            }
            
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
        
        
    }
    
    public static void loadMatchClassification(String arffLocation) {
        classification=new TreeMap();
        String line;
        BufferedReader reader;
        String type;
        try {
            reader=new BufferedReader(new FileReader(arffLocation));
            while((line=reader.readLine())!=null) {
                if(line.startsWith("@data")) break;
            }
            int insNum=1;
            int pos1;
            int pos2;
            while((line=reader.readLine())!=null) {
               
                pos1=line.lastIndexOf(",");
                type=line.substring(pos1+1, line.length());
                System.out.println(insNum+"\t"+type);
                classification.put(new Integer(insNum),type);
                insNum++;
            }
            
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
        
        
    }
    
    
    public static void loadLines(String logLocation) {
        cpNames=new TreeMap();
        rpNames=new TreeMap();
        cpIDs=new TreeMap();
        rpIDs=new TreeMap();
        
        String pattern1=task;
        int pos1;
        String pattern2="citance:";
        int pos2;
        String pattern3="reference";
        int pos3;
        String ref="ref:";
        int pos4;
        String cit="cit:";
        int pos5;
        int pos6;
        BufferedReader reader;
        String line;
        String instance;
        String cpName;
        String rpName;
        String rpID;
        String cpID;
        Integer insID;
        
        try {
            reader=new BufferedReader(new FileReader(logLocation));
            while((line=reader.readLine())!=null) {
           //     System.out.println(line);
                pos1=line.indexOf(pattern1);
                if(pos1>0) {
                  instance=line.substring(pos1+pattern1.length()+1,
                        line.indexOf(": (citance"));
              //    System.out.println(instance);
                  insID=new Integer(instance);
              //    System.out.println(instance);
                  pos2=line.indexOf("citance: ");
                  cpName=line.substring(pos2+9, pos2+17);
              //    System.out.println(cpName);
                  pos3=line.indexOf("reference: ");
                  rpName=line.substring(pos3+11, pos3+19);
               //   System.out.println(rpName);
                  pos4=line.indexOf("ref: ");
                  pos5=line.indexOf(" cit: ");
                  rpID=line.substring(pos4+5, pos5);
               //   System.out.println(rpID);
                  pos6=line.indexOf("):");
                  cpID=line.substring(pos5+6, pos6);
               //   System.out.println(cpID);
                  cpNames.put(insID,cpName);
                  rpNames.put(insID,rpName);
                  cpIDs.put(insID, cpID);
                  rpIDs.put(insID, rpID);
                  
                  
                }
                
                
                
            }
            
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        
    }
     public static Map<String,Document> cpDocs;
     public static Document rp;
     
     public static void loadCPs(String cpLoc,String cluster) {
        
            System.out.println(cpLoc);
            cpDocs=new TreeMap();
       
           
            File inDir=new File(cpLoc);
            File[] files=inDir.listFiles();
            String floc;
            String fname;
            Document document;
            String cpName;
            
            for(File file : files) {
                fname=file.getName();
                floc=file.getAbsolutePath();
                cpName=fname.replace(".xml", "");
                if(fname.endsWith(".xml") && !fname.startsWith(cluster)) {
                    try {
                        document=Factory.newDocument(new
                         URL("file:///"+floc));
                      cpDocs.put(cpName, document);
                      System.out.println(cpName);
                        
                    } catch (ResourceInstantiationException ex) {
                        ex.printStackTrace();
                    } catch (MalformedURLException ex) {
                        ex.printStackTrace();
                    }
                    
                }
            }   
       
        
        
    }
    
     
    public static void extractRPCPInfo(String cname) {
        PrintWriter pw=null;
        try {
           pw=new PrintWriter(new FileWriter(outAnnotations+File.separator+cname+"."+
                    "taln_upf"+".txt"));
        } catch (IOException ex) {
            Logger.getLogger(ExtractDataFromLogFile.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        Document cp;
        String cpName;
        String rpName;
        String rpSentID;
        String cpSentID;
        String annotator;
        AnnotationSet cpOriginal;
        AnnotationSet cpCitations;
        AnnotationSet rpOriginal;
        FeatureMap cpFilter;
        FeatureMap rpFilter;
        // instances
        Iterator<Integer> iteID=cpNames.keySet().iterator();
        Integer instance;
        AnnotationSet auxCPs;
        AnnotationSet auxRPs;
        Annotation sentCP;
        Annotation sentRP;
        String cpContent;
        String rpContent;
        Annotation citation;
        FeatureMap citationfm;
        AnnotationSet auxCitations;
        FeatureMap citingfm;
        FeatureMap sentRPfm;
        FeatureMap sentCPfm;
        String sentRPStr;
        String sentCPStr;
        String rpSID;
        String rpSSID;
        String cpSID;
        String cpSSID;
       
        String theFacet;
        String theClass;
        String citanceID;
        TreeSet<String> allIDs=new TreeSet();
        TreeSet<String> matchedIDs=new TreeSet();
        
        String out_feature;
        while(iteID.hasNext()) {
            
          //  try {
                instance=iteID.next();
                cpName=cpNames.get(instance);
                cpSentID=cpIDs.get(instance);
                rpSentID=rpIDs.get(instance);
                rpName=rpNames.get(instance);
                
                
                System.out.println(instance+" "+rpName+" ("+rpSentID+") "+cpName+"("+cpSentID+")");
                if(cpDocs.containsKey(cpName)) {
                    cp=cpDocs.get(cpName);

                    rpContent=rp.getContent().toString();
                    cpContent=cp.getContent().toString();


                    rpOriginal=rp.getAnnotations("Original markups");
                    cpOriginal=cp.getAnnotations("Original markups");
                    cpCitations=cp.getAnnotations("CITATIONS");
                    cpFilter=Factory.newFeatureMap();
                    rpFilter=Factory.newFeatureMap();
                    cpFilter.put("sid", cpSentID);
                    rpFilter.put("sid", rpSentID);

                    auxCPs=cpOriginal.get("S",cpFilter);
                    auxRPs=rpOriginal.get("S",rpFilter);

                    if(!auxCPs.isEmpty()) {
                        sentCP=auxCPs.iterator().next();
                        sentCPfm=sentCP.getFeatures();
                        sentCPStr=cpContent.substring(sentCP.getStartNode().getOffset().intValue(),
                                sentCP.getEndNode().getOffset().intValue());
                        System.out.println("CITING "+cpContent.substring(sentCP.getStartNode().getOffset().intValue(),
                                sentCP.getEndNode().getOffset().intValue()));
                        cpSID=(String)sentCPfm.get("sid");
                        cpSSID=(String)sentCPfm.get("ssid");

                        auxCitations=cpCitations.get(sentCP.getStartNode().getOffset(),
                                sentCP.getEndNode().getOffset());
                        if(!auxCitations.isEmpty()) {
                            citation=auxCitations.iterator().next();
                            citingfm=citation.getFeatures();
                            
                            
                            citanceID=(String)citingfm.get("id");
                            for(String feat : outFeatures) {
                            //System.out.println(citation.getFeatures());
                                System.out.println(feat+": "+citingfm.get(feat)); 
                            }

                        

                            
                            rpSID="NULL";
                            rpSSID="NULL";
                            sentRPStr="*****";
                        if(!auxRPs.isEmpty()) {
                            sentRP=auxRPs.iterator().next();
                            sentRPfm=sentRP.getFeatures();
                            rpSID=(String)sentRPfm.get("sid");
                            rpSSID=(String)sentRPfm.get("ssid");
                            sentRPStr=rpContent.substring(sentRP.getStartNode().getOffset().intValue(),
                                    sentRP.getEndNode().getOffset().intValue());
                            //System.out.println("REFER  "+rpContent.substring(sentRP.getStartNode().getOffset().intValue(),
                              //      sentRP.getEndNode().getOffset().intValue()));
                        }

                        theClass=classification.get(instance);
                        if(theClass.equals("MATCH")) {
                            matchedIDs.add(citanceID);
                            
                            // the output
                            
                            Double prob;
                            prob=new Double(0.0);
                            if(probabilities.containsKey(instance)) {
                                prob=probabilities.get(instance);
                            }
                            pw.print("PROB: "+prob+ "| ");
                            String feat;
                             for(int f=0; f<6; f++) {
                                 feat=outFeatures[f];
                            //System.out.println(citation.getFeatures());
                                out_feature=feat.replaceAll("_", " ");
                                if(out_feature.contains("Offset")) {
                                    pw.print(out_feature+": [\'"+citingfm.get(feat)+"\'] | "); 
                                } else if(out_feature.contains("Article")) {
                                  pw.print(out_feature+": "+citingfm.get(feat)+".xml"+" | "); 
                                } else                                
                                 {
                                    pw.print(out_feature+": "+citingfm.get(feat)+" | "); 
                                }
                            }
                             
                            pw.print("Citation Text: ");
                            pw.print("<S sid=\""+cpSID+"\" ssid=\""+cpSSID+"\">");
                            pw.print(sentCPStr);
                            pw.print("</S>"+" | ");
                            
                            pw.print("Reference Offset: ["+rpSID+ "] | ");
                            pw.print("Reference Text: ");
                            pw.print("<S sid=\""+rpSID+"\" ssid=\""+rpSSID+"\">");
                            pw.print(sentRPStr);
                            pw.print("</S>"+" | ");
                            
                            // extract match
                            String key=rpName+"#"+rpSID+"#"+cpName+"#"+cpSID;
                            theFacet="****";
                            if(fullFacetClassification.containsKey(key)) {
                                theFacet=fullFacetClassification.get(key);
                                theFacet=theFacet.replaceAll(" ", "");
                                if(theFacet.length()>1) {
                                    theFacet=theFacet.substring(0, 1)+
                                            theFacet.substring(1, theFacet.length()).toLowerCase()+
                                            "_Citation";
                                }
                                
                            }
                            
                            pw.print("Discourse Facet: "+theFacet+" | ");
                            pw.println(outFeatures[6]+": "+citingfm.get(outFeatures[6])+" |");
                            pw.flush();

                            
                            
                            
                        }
                        allIDs.add(citanceID);
                        System.out.println(theClass);
                    }
                    }
                    
                } else {
                    
                    System.out.println("Problems with Instance "+ instance+
                            "\t"+"CP "+cpName+" does not exist!");
                }
                
                // System.in.read();
         //  } catch (IOException ex) {
          //     ex.printStackTrace();
          //  }
                
               
            
        }
        pw.close();
        System.out.println("# OF IDs        = "+allIDs.size());
        System.out.println("# OF MATCHED IDs= "+matchedIDs.size());
        
        
    }
    
    
       
    public static Map<Integer,String> facetClassification;
    public static Map<Integer,String> cpNamesFacets;
    public static Map<Integer,String> rpNamesFacets;
    public static Map<Integer,String> rpIDsFacets;
    public static Map<Integer,String> cpIDsFacets;
    public static Map<String,String> fullFacetClassification;
    
    
     public static void loadLinesFacets(String logLocation) {
        cpNamesFacets=new TreeMap();
        rpNamesFacets=new TreeMap();
        cpIDsFacets=new TreeMap();
        rpIDsFacets=new TreeMap();
        fullFacetClassification=new TreeMap();
        
        String pattern1="Parsing FacetTesting instance";
        int pos1;
        String pattern2="citance:";
        int pos2;
        String pattern3="reference";
        int pos3;
        String ref="ref:";
        int pos4;
        String cit="cit:";
        int pos5;
        int pos6;
        BufferedReader reader;
        String line;
        String instance;
        String cpName;
        String rpName;
        String rpID;
        String cpID;
        Integer insID;
        
        try {
            reader=new BufferedReader(new FileReader(logLocation));
            while((line=reader.readLine())!=null) {
           //     System.out.println(line);
                pos1=line.indexOf(pattern1);
                if(pos1>0) {
                  instance=line.substring(pos1+pattern1.length()+1,
                        line.indexOf(": (citance"));
              //    System.out.println(instance);
                  insID=new Integer(instance);
              //    System.out.println(instance);
                  pos2=line.indexOf("citance: ");
                  cpName=line.substring(pos2+9, pos2+17);
              //    System.out.println(cpName);
                  pos3=line.indexOf("reference: ");
                  rpName=line.substring(pos3+11, pos3+19);
               //   System.out.println(rpName);
                  pos4=line.indexOf("ref: ");
                  pos5=line.indexOf(" cit: ");
                  rpID=line.substring(pos4+5, pos5);
               //   System.out.println(rpID);
                  pos6=line.indexOf("):");
                  cpID=line.substring(pos5+6, pos6);
               //   System.out.println(cpID);
                  cpNamesFacets.put(insID,cpName);
                  rpNamesFacets.put(insID,rpName);
                  cpIDsFacets.put(insID, cpID);
                  rpIDsFacets.put(insID, rpID);
                  
                  
                }
                
                
                
            }
            
            // combine information 
            String key;
            
            for(Integer inst: facetClassification.keySet()) {
                
                rpName=rpNamesFacets.get(inst);
                cpName=cpNamesFacets.get(inst);
                cpID=cpIDsFacets.get(inst);
                rpID=rpIDsFacets.get(inst);
                
                key=rpName+"#"+rpID+"#"+cpName+"#"+cpID;
                fullFacetClassification.put(key, facetClassification.get(inst));
                
                
                
            }
            
            
            // show combined
            
            for(String key1 : fullFacetClassification.keySet()) {
                
                System.out.println(key1+" => " + fullFacetClassification.get(key1));
            }
            
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        
    }
    
    
    
    
    public static void main(String[] args) {
        
        outFeatures=new String[7];
        outFeatures[0]="Citance_Number";
        outFeatures[1]="Reference_Article";
        outFeatures[2]="Citing_Article";
        outFeatures[3]="Citation_Marker_Offset";
        outFeatures[4]="Citation_Marker";
        outFeatures[5]="Citation_Offset";
        outFeatures[6]="Annotator";
        
        
        try {
            Gate.init();
            File inDir=new File(locFiles);
            File[] clusters=inDir.listFiles();
            String cname;
            for(File cluster : clusters) {
                cname=cluster.getName();
                System.out.println("CLUSTER "+cname);
                rp=Factory.newDocument(new 
                URL("file:///"+cluster+File.separator+"input"+File.separator+cname+".xml"));
             //   System.out.println(rp.getAnnotations("REFERENCES"));
               
                loadProbabilities(probFiles+File.separator+cname+".prob.txt");
                
            //    System.in.read();
                // if(classFileName.startsWith("match")) {
                    loadMatchClassification(cluster+File.separator+"Output"+File.separator+classFileName);
               //  } else {
                    loadFacetClassification(cluster+File.separator+"Output"+File.separator+classFileNameFacet);
               //  }

                    
                loadLines(cluster+File.separator+"Consoles"+File.separator+logFileName);
                loadLinesFacets(cluster+File.separator+"Consoles"+File.separator+logFileNameFacet);
                
               // System.in.read();
                
                loadCPs(cluster+File.separator+"input",cname);
                
                extractRPCPInfo(cname);
                Factory.deleteResource(rp);
                for(Document doc : cpDocs.values()) {
                    Factory.deleteResource(doc);
                }
               /* 
                try {
                    System.in.read();
                } catch(IOException ioe) {
                    ioe.printStackTrace();
                }
                       */
             

            }
        } catch(GateException ge) {
            ge.printStackTrace();
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            Logger.getLogger(ExtractDataFromLogFile.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    
 
    
    
}
