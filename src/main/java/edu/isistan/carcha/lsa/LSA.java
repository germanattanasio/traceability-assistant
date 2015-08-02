package edu.isistan.carcha.lsa;

import java.io.IOError;
import java.io.IOException;

import edu.ucla.sspace.basis.BasisMapping;
import edu.ucla.sspace.basis.StringBasisMapping;
import edu.ucla.sspace.common.ArgOptions;
import edu.ucla.sspace.common.SemanticSpace;
import edu.ucla.sspace.common.SemanticSpaceIO.SSpaceFormat;
import edu.ucla.sspace.lsa.LatentSemanticAnalysis;
import edu.ucla.sspace.mains.GenericMain;
import edu.ucla.sspace.matrix.LogEntropyTransform;
import edu.ucla.sspace.matrix.MatrixFactorization;
import edu.ucla.sspace.matrix.SVD;
import edu.ucla.sspace.matrix.SVD.Algorithm;
import edu.ucla.sspace.matrix.Transform;
import edu.ucla.sspace.util.ReflectionUtil;
import edu.ucla.sspace.util.SerializableUtil;


// TODO: Auto-generated Javadoc
/**
 * An executable class for running {@link LatentSemanticAnalysis} (LSA) from the
 * command line.  This class takes in several command line arguments.
 *
 * <ul>
 *
 * <li><u>Required (at least one of)</u>:
 *   <ul>
 *
 *   <li> {@code -d}, {@code --docFile=FILE[,FILE...]} a file where each line is
 *        a document.  This is the preferred input format for large corpora
 *
 *   <li> {@code -f}, {@code --fileList=FILE[,FILE...]} a list of document files
 *        where each file is specified on its own line.
 *
 *   </ul>
 * 
 * <li><u>Algorithm Options</u>:
 *   <ul>
 *
 *   <li> {@code --dimensions=<int>} how many dimensions to use for the LSA
 *        vectors.  See {@link LatentSemanticAnalysis} for default value
 *
 *   <li> {@code --preprocess=<class name>} specifies an instance of {@link
 *        edu.ucla.sspace.lsa.MatrixTransformer} to use in preprocessing the
 *        word-document matrix compiled by LSA prior to computing the SVD.  See
 *        {@link LatentSemanticAnalysis} for default value
 *
 *   <li> {@code -F}, {@code --tokenFilter=FILE[include|exclude][,FILE...]}
 *        specifies a list of one or more files to use for {@link
 *        edu.ucla.sspace.text.TokenFilter filtering} the documents.  An option
 *        flag may be added to each file to specify how the words in the filter
 *        filter should be used: {@code include} if only the words in the filter
 *        file should be retained in the document; {@code exclude} if only the
 *        words <i>not</i> in the filter file should be retained in the
 *        document.
 *
 *   <li> {@code -S}, {@code --svdAlgorithm}={@link
 *        edu.ucla.sspace.matrix.SVD.Algorithm} species a specific {@code
 *        SVD.Algorithm} method to use when reducing the dimensionality in LSA.
 *        In general, users should not need to specify this option, as the
 *        default setting will choose the fastest algorithm available on the
 *        system.  This is only provided as an advanced option for users who
 *        want to compare the algorithms' performance or any variations between
 *        the SVD results.
 *
 *   </ul>
 *
 * <li><u>Program Options</u>:
 *   <ul>
 *
 *   <li> {@code -o}, {@code --outputFormat=}<tt>text|binary}</tt> Specifies the
 *        output formatting to use when generating the semantic space ({@code
 *        .sspace}) file.  See {@link edu.ucla.sspace.common.SemanticSpaceUtils
 *        SemanticSpaceUtils} for format details.
 *
 *   <li> {@code -t}, {@code --threads=INT} how many threads to use when
 *        processing the documents.  The default is one per core.
 * 
 *   <li> {@code -w}, {@code --overwrite=BOOL} specifies whether to overwrite
 *        the existing output files.  The default is {@code true}.  If set to
 *        {@code false}, a unique integer is inserted into the file name.
 *
 *   <li> {@code -v}, {@code --verbose}  specifies whether to print runtime
 *        information to standard out
 *
 *   </ul>
 *
 * </ul>
 *
 * <p>
 *
 * An invocation will produce one file as output {@code
 * lsa-semantic-space.sspace}.  If {@code overwrite} was set to {@code true},
 * this file will be replaced for each new semantic space.  Otherwise, a new
 * output file of the format {@code lsa-semantic-space<number>.sspace} will be
 * created, where {@code <number>} is a unique identifier for that program's
 * invocation.  The output file will be placed in the directory specified on the
 * command line.
 *
 * <p>
 *
 * This class is desgined to run multi-threaded and performs well with one
 * thread per core, which is the default setting.
 *
 * @see LatentSemanticAnalysis
 * @see edu.ucla.sspace.matrix.Transform Transform
 *
 */
public class LSA extends GenericMain {

    /** The basis. */
    private BasisMapping<String, String> basis;

    /**
     * Instantiates a new lsa.
     */
    public LSA() {
    }

    /**
     * Adds all of the options to the {@link ArgOptions}.
     *
     * @param options the options
     */
    protected void addExtraOptions(ArgOptions options) {
        options.addOption('n', "dimensions", 
                          "the number of dimensions in the semantic space",
                          true, "INT", "Algorithm Options"); 
        options.addOption('p', "preprocess", "a MatrixTransform class to "
                          + "use for preprocessing", true, "CLASSNAME",
                          "Algorithm Options");
        options.addOption('S', "svdAlgorithm", "a specific SVD algorithm to use"
                          , true, "SVD.Algorithm", 
                          "Advanced Algorithm Options");
        options.addOption('B', "saveTermBasis",
                          "If true, the term basis mapping will be stored " +
                          "to the given file name",
                          true, "FILE", "Optional");
    }

    /**
     * The main method.
     *
     * @param args the arguments
     * @throws Exception the exception
     */
    public static void main(String[] args) throws Exception {
        LSA lsa = new LSA();
        lsa.run(args);
    }
    
    /* (non-Javadoc)
     * @see edu.ucla.sspace.mains.GenericMain#getSpace()
     */
    protected SemanticSpace getSpace() {
        try {
            int dimensions = argOptions.getIntOption("dimensions", 300);
            Transform transform = new LogEntropyTransform();
            if (argOptions.hasOption("preprocess"))
                transform = ReflectionUtil.getObjectInstance(
                        argOptions.getStringOption("preprocess"));
            String algName = argOptions.getStringOption("svdAlgorithm", "ANY");
            MatrixFactorization factorization = SVD.getFactorization(
                    Algorithm.valueOf(algName.toUpperCase()));
            basis = new StringBasisMapping();

            return new LatentSemanticAnalysis(
                false, dimensions, transform, factorization, false, basis);
        } catch (IOException ioe) {
            throw new IOError(ioe);
        }
    }

    /**
     * Returns the {@likn SSpaceFormat.BINARY binary} format as the default
     * format of a {@code LatentSemanticAnalysis} space.
     *
     * @return the space format
     */
    protected SSpaceFormat getSpaceFormat() {
        return SSpaceFormat.BINARY;
    }

    /* (non-Javadoc)
     * @see edu.ucla.sspace.mains.GenericMain#postProcessing()
     */
    protected void postProcessing() {
        if (argOptions.hasOption('B'))
            SerializableUtil.save(basis, argOptions.getStringOption('B'));
    }

    /**
     * {@inheritDoc}
     */
    protected String getAlgorithmSpecifics() {
        return 
            "The --svdAlgorithm provides a way to manually specify which " + 
            "algorithm should\nbe used internally.  This option should not be" +
            " used normally, as LSA will\nselect the fastest algorithm " +
            "available.  However, in the event that it\nis needed, valid" +
            " options are: SVDLIBC, SVDLIBJ, MATLAB, OCTAVE, JAMA and COLT\n";
    }
}
