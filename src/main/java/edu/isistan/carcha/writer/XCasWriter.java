package edu.isistan.carcha.writer;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.impl.XmiCasSerializer;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.XMLSerializer;
import org.uimafit.component.JCasConsumer_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.factory.ConfigurationParameterFactory;
import org.xml.sax.SAXException;

// TODO: Auto-generated Javadoc
/**
 * A simple CAS consumer that generates XCAS (XML representation of the CAS) files in the
 * filesystem.
 * 
 * @author German Attanasio Ruiz
 */

public class XCasWriter extends JCasConsumer_ImplBase {

	/** The parameter name for the configuration parameter that specifies the output directory. */
	public static final String PARAM_OUTPUT_FILE_NAME = ConfigurationParameterFactory.createConfigurationParameterName(XCasWriter.class, "outputFileName");
	
	/** The output file name. */
	@ConfigurationParameter(mandatory = true, description = "takes a path to file into which the output file will be writted.")
	private String outputFileName;

	/** The output file. */
	private File outputFile;

	/* (non-Javadoc)
	 * @see org.uimafit.component.JCasConsumer_ImplBase#initialize(org.apache.uima.UimaContext)
	 */
	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
	}

	/* (non-Javadoc)
	 * @see org.apache.uima.analysis_component.JCasAnnotator_ImplBase#process(org.apache.uima.jcas.JCas)
	 */
	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		try {
			writeXmi(jcas.getCas());	
		}
		catch (IOException e) {
			throw new AnalysisEngineProcessException(e);
		}
		catch (SAXException e) {
			throw new AnalysisEngineProcessException(e);
		}
	}

	/**
	 * Write xmi.
	 *
	 * @param aCas the a cas
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws SAXException the sAX exception
	 */
	private void writeXmi(CAS aCas) throws IOException, SAXException {
		if (outputFile == null) {
			outputFile = new File(outputFileName);
    		if(!outputFile.exists()) { outputFile.createNewFile(); }
    	}
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(outputFile);
			XmiCasSerializer ser = new XmiCasSerializer(aCas.getTypeSystem());
			XMLSerializer xmlSer = new XMLSerializer(out, true);
			ser.serialize(aCas, xmlSer.getContentHandler());
		}
		finally {
			if (out != null) { out.close(); }
		}
	}

}
