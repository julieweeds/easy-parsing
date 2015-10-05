package uk.ac.susx.tag.easyparsing;


import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Extension to SimpleFileVisitor<Path> which assumes existence of a processFile(String filename) method
 * In this class, processFile(filename) simply prints the filename of the filename visited on the walk through the file tree
 * However, the expectation is that this class will be extended and processFile(filename) will be overridden.
 * Hence a class which knows how to do something to a file (specified in filename), can easily be extended in to a class which knows how to do something to directory
 *
 * This is currently used in TaggedDataConverter (to convert the format of all files in a directory) and illinoisMain (to run NER on all files in a directory)
 *
 * Created by juliewe on 09/02/2015.
 */

public class EasyFileVisitor extends SimpleFileVisitor<Path> {
    // Print information about
    // each type of file.
    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attr){
        if (attr.isSymbolicLink()) {
            System.out.format("Following symbolic link: %s ", file);
            try {
                processFile(file.toString());
            } catch (Exception e) {
                System.err.println("Error reading/parsing file " + e.toString());
            }

        } else if (attr.isRegularFile()) {

            System.out.format("Processing file: %s ", file);
            try {
                processFile(file.toString());
            } catch (Exception e) {
                System.err.println("Error reading/parsing file " +file.toString()+ " : " + e.toString());
            }


        } else {
            System.out.format("Ignoring Other: %s ", file);
        }
        System.out.println("(" + attr.size() + "bytes)");
        return FileVisitResult.CONTINUE;
    }

    // Print each directory visited.
    @Override
    public FileVisitResult postVisitDirectory(Path dir,
                                              IOException exc) {
        System.out.format("Processed directory: %s%n", dir);
        return FileVisitResult.CONTINUE;
    }

    // If there is some error accessing
    // the file, let the user know.
    // If you don't override this method
    // and an error occurs, an IOException
    // is thrown.
    @Override
    public FileVisitResult visitFileFailed(Path file,
                                           IOException exc) {
        System.err.println(exc);
        return FileVisitResult.CONTINUE;
    }


    //this can be called in extending class to walk the filetree and call processFile on each file found.
    public void processDir(String directoryname) throws IOException {
        Files.walkFileTree(Paths.get(directoryname), this);
    }

    //default method
    //this should be overridden
    public void processFile(String name){
        System.out.println(name);
        System.err.println("You haven't told me what to do with this file");
    }

}
