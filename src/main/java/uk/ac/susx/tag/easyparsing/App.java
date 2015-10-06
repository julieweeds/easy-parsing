//package uk.ac.susx.tag.easyparsing;
//import uk.ac.susx.tag.classificationframework.Util;
//import uk.ac.susx.tag.classificationframework.datastructures.DependencyTree;
//import uk.ac.susx.tag.classificationframework.datastructures.Document;
//import uk.ac.susx.tag.classificationframework.datastructures.Instance;
//import uk.ac.susx.tag.classificationframework.featureextraction.documentprocessing.TweetTagConverter;
//import uk.ac.susx.tag.classificationframework.featureextraction.pipelines.FeatureExtractionPipeline;
//import uk.ac.susx.tag.dependencyparser.Parser;
//import uk.ac.susx.tag.dependencyparser.datastructures.Token;
//
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.IOException;
//import java.io.Reader;
//import java.nio.charset.Charset;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.util.List;
//import java.util.Scanner;
//
//
///**
// * Created by juliewe on 02/10/2015.
// */
//
//public class App {
//
//    private final FeatureExtractionPipeline pipeline;  //dependency analysis pipeline
//    private final Parser myparser;
//
//    App() throws IOException {
//        pipeline = Util.buildParsingPipeline(false, false);
//        myparser=new Parser("full_wsj_pen_pos_stanford_dep");
//    }
//
//    public void help(){
//
//        try{
//            Parser.printHelpfileAndOptions();
//        }
//        catch(java.io.IOException e){
//            System.out.println(e.toString());
//        }
//    }
//
//
//    public static void displayParse(DependencyTree parse){
//        System.out.println(parse.toString());
//    }
//
//    private List<TweetTagConverter.Token> getParse(String sentence){
//        Instance instance = new Instance("",sentence,"1");
//        Document parsed = pipeline.processDocument(instance);
//        return (List<TweetTagConverter.Token>) parsed.getAttribute("ExpandedTokens");
//
//    }
//    private DependencyTree getDepTree(String textstring) {
//
//        //if (DEBUG) {System.err.println(textstring);}
//        //turn textstring into a Document and pass to pipeline
//        Instance instance = new Instance("", textstring, "1");
//        Document parsed = pipeline.processDocument(instance);
//        System.out.println(parsed.toString());
//        List<TweetTagConverter.Token> tokens =
//                (List<TweetTagConverter.Token>) parsed.getAttribute("ExpandedTokens");
//        DependencyTree depTree = new DependencyTree(tokens);
//        return depTree;
//    }
//    public void interactive(){
//        String text_to_parse="";
//        Scanner userinput=new Scanner(System.in);
//        Boolean doContinue=true;
//        while (doContinue){
//            System.out.println("What would you like to parse?");
//            System.out.print(">");
//            text_to_parse=userinput.nextLine();
//            if(text_to_parse.toLowerCase().startsWith("quit")){
//                doContinue=false;
//            } else {
//                displayParse(getDepTree(text_to_parse));
//            }
//        }
//    }
//
//    public void processfile(String filename) throws IOException {
//        Charset charset = Charset.forName("UTF8");
//        BufferedReader reader = new BufferedReader(Files.newBufferedReader(Paths.get(filename), charset));
//        DependencyTree.CoNLLSentenceWriter writer = new DependencyTree.CoNLLSentenceWriter(new File(filename+".conll"));
//        String line=reader.readLine();
//        List<TweetTagConverter.Token> tokens;
//        while (line!=null){
//            System.out.println(line);
//            tokens=getParse(line);
//            System.out.println(tokens.toString());
//            writer.writeSentence(tokens);
//            line=reader.readLine();
//        }
//        writer.close();
//
//    }
//
//    public static void main(String[] args) throws IOException{
//        System.out.println("Hello World!");
//        String option="help";
//        if (args.length>0){
//            option =args[0];
//        }
//        App parser = new App();
//
//        if (option.equals("help")) {
//            parser.help();
//
//        } else if (option.equals("interactive")) {
//            parser.interactive();
//
//        } else if (option.equals("file")) {
//            parser.processfile(args[1]);
//
//        } else {
//            parser.interactive();
//        }
//
//
//
//    }
//}
