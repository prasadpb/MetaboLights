package uk.ac.ebi.metabolights.metabolightsuploader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.metabolights.checklists.CheckList;
import uk.ac.ebi.metabolights.checklists.SubmissionProcessCheckListSeed;
import uk.ac.ebi.metabolights.repository.accessionmanager.AccessionManager;
import uk.ac.ebi.metabolights.utils.FileUtil;
import uk.ac.ebi.metabolights.utils.StringUtils;

import javax.naming.ConfigurationException;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


/**
 * IsaTabReplacer
 * It replace StudyIds in ISATabFile by an accession number generated by accesionManager.
 * 
 *@author conesa
 */
public class IsaTabIdReplacer 
{
	static private Properties props = new Properties();
	static private String pubDateStr;			//Replace str to look for in i_Investigation.txt
	static private String subDateStr;			//Replace str to look for in i_Investigation.txt
	static private String metaboliteProfTypeStr;	//String to search for in i_Investigation.txt, only allow metabolite profiling
	static private String metaboliteProfValueStr;	//String to search for in i_Investigation.txt, only allow metabolite profiling
	static private String fileWithIds; 
	
	static final String PROP_IDS = "isatab.ids";
	static String[] idList;
	static final String PROP_FILE_WITH_IDS = "isatab.filewithids";
	
	private String publicDate; 		//Date from submitter form
	private String submissionDate;	//Date from submitter form
	private Integer singleStudy=0;	//Update when we find study ids in the file

    private static final Logger logger = LoggerFactory.getLogger(IsaTabIdReplacer.class);
    
    static private AccessionManager am = AccessionManager.getInstance();
       
	private String isaTabFolder;
    
    private HashMap<String,String> ids = new HashMap<String,String>();
    
	public String getPublicDate() {
		if (publicDate == null)
			publicDate = "";
		return publicDate;
	}

	public void setPublicDate(String publicDate) {
		this.publicDate = publicDate;
	}
	
    public String getSubmissionDate() {
		return submissionDate;
	}

	public void setSubmissionDate(String submissionDate) {
		this.submissionDate = submissionDate;
	}


	private CheckList cl;
    /**
	 * 
	 * @param args
	 * First param must be the file name to work with. It should be a ISATab folder.
     * @throws Exception 
	 * 
	 */
	public static void main( String[] args ) throws Exception{
		
		//Check the arguments. 2 is needed.
		if (args.length != 2){
			System.out.println("2 argument is required: 1st IsaFolder, 2dn Submission Date");
			return;
		}
		
		//There is 1 arguments
		IsaTabIdReplacer itr = new IsaTabIdReplacer();
		
		//Set the IsaTabArchive
		itr.setIsaTabFolder(args[0]);
		itr.setSubmissionDate(args[1]);
		
		//Run it
		itr.Execute();
		
	}
	
	public IsaTabIdReplacer(String isaTabFolder){
		this.isaTabFolder = isaTabFolder;
	}
	public IsaTabIdReplacer(){}
	
	//IsaTabArchive properties
	public String getIsaTabFolder(){return isaTabFolder;}
	public void setIsaTabFolder(String folder){isaTabFolder = folder;}
	
	//Ids property
	public HashMap<String,String> getIds(){return ids;}
	public String getIdsNotes(){
		String notes;
		
		notes = "File " + fileWithIds + " found.";
		//GO through the ids hash
		for (Map.Entry<String,String> entry :ids.entrySet()){
			notes = notes + " Initial Id (" + entry.getKey() + ") has been replaced with metabolights Id (" + entry.getValue() +").";
		}
		return notes;
	}
	//CheckList porperty
	public void setCheckList(CheckList newCl){cl= newCl;}
	
	
	private void loadProperties() throws FileNotFoundException, IOException, ConfigurationException{
		
		final String PROPS_FILE = "isatabidreplacer.properties";
		
		//If properties are loaded
		if (!props.isEmpty()) {return;}
		

		logger.info("Loading properties using getClassLoader().getResourceAsStream(" + PROPS_FILE + ")");
		
		//Load the properties from the property file
		props.load(IsaTabIdReplacer.class.getClassLoader().getResourceAsStream(PROPS_FILE));
		
		//If property file is empty
		if (props.size() ==0){
			
			//Dereference
			props = null;
			
			//Throw an exception
			throw new ConfigurationException("The isatabidreplacer.properties file has been found, but it is empty.");
		}
		
		//Initialise idList
		String ids = props.getProperty(PROP_IDS);
		pubDateStr = props.getProperty("isatab.publicReleaseDate");
		subDateStr = props.getProperty("isatab.studySubDate");
		metaboliteProfTypeStr  = props.getProperty("isatab.profilingType");
		metaboliteProfValueStr = props.getProperty("isatab.profilingValue");
		
		logger.info(PROP_IDS + " property retrieved :" + ids + "," + pubDateStr + "," + subDateStr);
		
		//Split it by ; to go through the array
	    idList = ids.split(";");

	    //Initialize fileWithIds
	    fileWithIds = props.getProperty(PROP_FILE_WITH_IDS);
	}
	private void updateCheckList (SubmissionProcessCheckListSeed spcls, String newNotes){
		
		//If we have a check list
		if (cl != null){
			cl.CheckItem(spcls.getKey(), newNotes);
		}
	}
	public void validateIsaTabArchive () throws IsaTabIdReplacerException{
		String[] msgs = new String[2];
		String msg;
		
		//Create a File object
		File isatab = new File(isaTabFolder);
		
		//If file does not exists
		if (!isatab.exists()) {
			//Add the error to msg
			msgs[1]="File " + isaTabFolder + " does not exists.";
		}
		
		//File must be a folder, if not
		if (!isatab.isDirectory()){
			//Add the error to msg
			msgs[0]= isatab.getName() + " is not a directory.\n";
		}

		
		//If there are messages (errors)
		msg = org.apache.commons.lang.StringUtils.join(msgs);
		
		//If there is any message...
		if ( !msg.equals("") ){
			
			//Throw customize exception...
			IsaTabIdReplacerException e = new IsaTabIdReplacerException("Invalid ISA Tab File:\n", msgs);
			throw e;
		}
		
		//Check CheckList Item
		updateCheckList(SubmissionProcessCheckListSeed.FILEVALIDATION, "File passed basic validation: correct extension and file found.");
		
	}

	public void Execute() throws Exception{
		
		logger.info("Starting submission upload");
		
		//Reset id List, it will be populated with the new accession numbers generated
		ids.clear();
		
		//Load properties
		logger.info("Loading properties");
		loadProperties();
		
		//Validate
		logger.info("Validating the archive");
		validateIsaTabArchive();
				
		//Replace id
		logger.info("Replace study id and study dates");
		replaceIdInFiles();
		
		//Update CheckList
		updateCheckList(SubmissionProcessCheckListSeed.IDREPLACEMENTS, getIdsNotes());
		
	}
	private void replaceIdInFiles () throws Exception{
		
		// Get the investigation file
		File isaTabFile = getInvestigationFile();
		
		
		logger.info("Loading investigation file");
		
		// Replace the id
		replaceInFile(isaTabFile);

	}

	/**
	 * @return
	 * @throws IOException 
	 * @throws ConfigurationException 
	 */
	private File getInvestigationFile() throws ConfigurationException, IOException {

		//Search for the investigation file
		File isaFolder = new File(isaTabFolder);
		File[] fileList;
		
		// Load properties
		loadProperties();
		
		//Define a filename filter
		FilenameFilter filter = new FilenameFilter() {
			
			public boolean accept(File arg0, String arg1) {
				
				//Accept only investigation files
				return (arg1.equals(fileWithIds));
			}
		};
		
		//Get the file list filtered
		fileList = isaFolder.listFiles(filter);
		
		//If there is not an investigation file...
		if (fileList.length ==0 || fileList == null) {
			throw new FileNotFoundException ("File with Ids (" + fileWithIds + ") not found");
		}
		
		//There must be only one, so take the first
		return fileList[0];
	}
	
	/**
	 * Replaces Id in a single file. Goes through each line and replace the id if it's the correct line.
	 * @param fileWithId
	 * @throws Exception 
	 */
	private void replaceInFile(File fileWithId) throws Exception{
		
		logger.info("Replacing ids in file -->" + fileWithId.getAbsolutePath());
		
		try {
			//Use a buffered reader
			BufferedReader reader = new BufferedReader(new FileReader(fileWithId));
			String line = "", text = "";
			
			//Go through the file
			while((line = reader.readLine()) != null)
			{

				if (!checkIfMetaboliteProfiling(line)){    //Check if this is metabolite profiling
					String errTxt = "Only metabolite profiling allowed";  //Todo, read error text from properties
					reader.close();
					logger.error(errTxt);
					System.err.println(errTxt);
					throw new Exception(errTxt); 
				}

				if (singleStudy>1){  //If we already have assigned a study, fail the upload
					String errTxt = "Only one study per submission allowed";  //Todo, read error text from properties
					reader.close();
					logger.error(errTxt);
					System.err.println(errTxt);
					throw new Exception(errTxt); 
				}
					
				//Replace Id in line (if necessary), also check for multiple studies reported
				line = replaceIdInLine(line);
				
				//Replace public release date for this study
				line = replacePubRelDateInLine(line);
				
				//Replace study submission date for this study
				line = replaceSubmitDateInLine(line);
				
			    //Add the final carriage return and line feed
				text += line + "\r\n";
			}
			
			//Close the reader
			reader.close();
			
			//Save the file
			FileUtil.String2File(text, fileWithId.getPath());
		} catch (Exception e) {
			throw e; 
		}
		
	}
	
	private String replaceIdInLine(String line){
		
	    //For each id...
	    for (int i=0;i<idList.length;i++) {
	      
	      //Get the value (Study Identifier, Investigation Identifier)
	      String id = idList[i];
	      
	      //If the value is present in line, in the first position.
	      if (line.indexOf(id)==0){
	    	  
	    	  logger.info("Id found in line " + line);
	    	
	    	  //Get the accession number
	    	  String accession = am.getAccessionNumber();
	    	  
	    	  //Get the Id Value (i.e.: BII-1-S)
	    	  String idInitialValue = StringUtils.replace(line, id + "\t\"", "");
	    	  idInitialValue = StringUtils.truncate(idInitialValue);
	    	  
	    	  //Compose the line:         Study Identifier   "MTBL1"
	    	  line = id + "\t\"" + accession + "\"";
	    	  
	    	  //If the value is a study identifier
	    	  //This is necessary for the uploading using command line tools.
	    	  //The accession number list will be used to assign permissions.
	    	  //Permissions can only be done to Study Identifier elements.
	    	  //Only Study Identifier can be linked.
	    	  if ("Study Identifier".equals(id)){
	    		  
	    		  ++singleStudy;  //Count how many study id's we have processed
	    	  
	    		//Populate the list of new accession numbers (initialized in Execute method)
				//accessionNumberList = accessionNumberList + accession + " ";
				//initialIdValuesList = initialIdValuesList + idInitialValue + " ";
	    		ids.put(idInitialValue, accession);
	    		logger.info("Study identifier " + idInitialValue + " replaced with " +accession);
	    		
	    	  }
	    		  
	    	  return line;
	      }
	      
	    }
	    
	    return line;
		
	}
	
	/*
	 * String replace the public release date in i_investigation.txt file
	 */
	private String replacePubRelDateInLine(String line){
	      
	      //If the value is present in line, in the first position.
	      if (line.indexOf(pubDateStr)==0){   
	    	  
	    	  logger.info(pubDateStr + " found in line " + line);
	    	 
	    	  //Compose the line:Study Public Release Date	"10/03/2009"
	    	  String newLine = pubDateStr + "\t\"" + getPublicDate() + "\"";
	    		  
	    	  return newLine;
	    	  
	      } else {
	    	  return line;
	      }
		
	}

	/*
	 * String replace the MetaboLights submission date in i_investigation.txt file
	 */
	private String replaceSubmitDateInLine(String line){

		//If the value is present in line, in the first position.
		if (line.indexOf(subDateStr)==0){   

			logger.info(subDateStr + " found in line " + line);

			//Compose the line:Study Submission Date	"30/04/2007"
			String newLine = subDateStr + "\t\"" + getSubmissionDate() + "\"";

			return newLine;

		} else {
			return line;
		}
		
	}
    
	/*
	 * Check if the this is a metabolite profiled study, read from i_investigation.txt file
	 */
    private Boolean checkIfMetaboliteProfiling(String line){

    	//Is this metabolite profiling type and the value is metabolite profiling
    	if ( line.indexOf(metaboliteProfTypeStr + "\t")==0 && !line.contains(metaboliteProfValueStr) ){
    		    logger.error("'"+ metaboliteProfTypeStr + "\t" + "' found, but no '" +metaboliteProfValueStr+ "' in line: " + line);
    		    return false;
    	} 

        return true;  //Not the correct line or correct type/value combo

    }
    /**
     * Replaces values in an ISATab file using the replacementHash,
     *  <LI> it goes through the file</LI>
     *  <LI> search for any field in replacement.keys</LI>
     *  <LI> replaces it with correspondent value</LI>
     * @param replacementHash: Hash where the key is the Tag to search for and the value is the value to write.
     * @throws Exception 
     */
    public void replaceFields(HashMap<String,String> replacementHash) throws Exception{
 
		// Get the investigation file
		File isaTabFile = getInvestigationFile();
		
		// Replace the id
		replaceFieldsInFile(isaTabFile, replacementHash);

    	
    }
    private void replaceFieldsInFile(File fileWithId, HashMap<String,String> replacementHash) throws Exception{
		
		logger.info("Replacing fields in file -->" + fileWithId.getAbsolutePath());
		
		try {
			//Use a buffered reader
			BufferedReader reader = new BufferedReader(new FileReader(fileWithId));
			String line = "", text = "";
			
			//Go through the file
			while((line = reader.readLine()) != null)
			{

				//Replace fields in file
				line = replaceFieldsInLine(line, replacementHash);
				
			    //Add the final carriage return and line feed
				text += line + "\r\n";
			}
			
			//Close the reader
			reader.close();
			
			//Save the file
			FileUtil.String2File(text, fileWithId.getPath());
			
		} catch (Exception e) {
			throw e; 
		}
	}

    public String getFieldInLine(String line){
    	
    	int tabPos = line.indexOf("\t");
    	
    	// If there isn't any tab
    	if (tabPos == -1){
    		return null;
    	}else{
    		return line.substring(0, tabPos);
    	}
    	
    }
    	
    private String replaceFieldsInLine(String line, HashMap<String,String> replacementHash){
    
    	// Get the field of the line
    	String field = getFieldInLine(line);
    	
    	// If the line has a field
    	if (field != null){
    		
    		// If the field is present in the hash
    		if (replacementHash.containsKey(field)){
    			
    			// Get the value
    			String value = replacementHash.get(field);
    			    			
    			logger.info("Field found: " + field + " in line " + line + ". Replacing value with " + value );
    			
    			line = field + "\t\"" + value + "\""; 
    		}
    		
    	}
    	
    	// Return the line
    	return line;
		
	}
}