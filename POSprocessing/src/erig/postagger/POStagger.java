package erig.postagger;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;



/**
 * 
 * @author Ludovic Moncla
 */
public abstract class POStagger {
	
	
	protected String _installDirectory = null;
	protected String _lang = null;
	protected String _POStaggerName = null;
	
	private Hashtable<String, String> _tags = new Hashtable<String, String>();
	
	/**
	 * 
	 * @param installDirectory
	 * @param lang
	 */
	public POStagger(String installDirectory, String lang, String POStaggerName)
	{
		_installDirectory = installDirectory;
		_lang = lang;
		_POStaggerName = POStaggerName;
		
		
	}
	
	/**
	 * 
	 * @param input
	 * @param outputFile
	 * @throws Exception
	 */
	public abstract void run(String input, String outputFile) throws Exception;
	
	
	
	/**
	 * 
	 * @param inputFile
	 * @throws Exception
	 */
	public abstract String tagger2pivot(String inputFile) throws Exception;

	
	

	/**
	 * load the list of POS tags
	 * @param uri 		path of the containing the POS tags
	 */
	protected void loadTags() throws Exception
	{

		Class<?> currentClass = new Object() { }.getClass().getEnclosingClass();
		InputStream ips = currentClass.getResourceAsStream("/resources/Tag_"+_POStaggerName+"_"+_lang);
		
		//InputStream ips = new FileInputStream("Tag_"+_POStaggerName+"_"+_lang);
	
		InputStreamReader ipsr = new InputStreamReader(ips,"UTF-8");
		BufferedReader br = new BufferedReader(ipsr);

		String line = "";
		
		while ((line = br.readLine()) != null) 
		{
		//	System.out.println("line : "+line);
			String str[] = line.split(";");
			
			if(!str[1].equals("null"))
				_tags.put(str[0], str[1]+";"+str[2]);
			else
				_tags.put(str[0], str[1]);	
		}
		ips.close();	 
	}
	
	
	/**
	 * load the list of POS tags
	 * @param tag 		Hashtable of POS tags
	 */
	protected void loadTags(Hashtable<String, String> tag) throws Exception
	{
		_tags = tag;
	}

	
	/**
	 * get the list of POS tags
	 * @return Hashtable<String, String>		Hashtable of POS tags
	 */
	public Hashtable<String, String> getTags()
	{
		return _tags;
	}
	
	

	public String tagger2unitex(InputStreamReader inputFile) throws Exception {
		
		String line = "";
		String content = "";
		
		//InputStream ips = new FileInputStream(input);
		//InputStreamReader ipsr = new InputStreamReader(inputFile,"UTF-8");
		BufferedReader br = new BufferedReader(inputFile);
		
		while ((line = br.readLine()) != null) {	
			content += line + "\n";
		}
		
		br.close();
		
		return tagger2unitex(content);
	}

	

	public String tagger2unitex(String inputContent) throws Exception {
		
		//System.out.println("Begin tagger2unitex");
		
		
		this.loadTags();
		
		String result = "";
		String line[] = inputContent.split("\n");
		
		String token ="", lemma = "", pos = "";
		
		for(int i=0; i<line.length;i++) {
			
			
			String str[] = line[i].split("\t");

			//System.out.println("ligne : "+line[i]);
			
			
			token = str[0];
			pos = str[1];
			lemma = str[2];
			
			
			if(!_tags.get(pos).equals("null")) 
			{
				
				//System.out.println("pass : "+_tags.get(pos));
				String st[]=_tags.get(pos).split(";");
				
				//valeur = StringTools.filtreString(valeur);
				//lemme = StringTools.filtreString(lemme);
				
				
				if(st!=null) 
				{	
					if(token.equals("-"))
						st[1] = "PUN";
					
					
					if(st[1].equals("PUN"))// || st[1].equals("SEN"))
					{
						result += token;
					}
					else
					{
						result += "{";
						
						result += token+",";
						if(!lemma.equals("") && !lemma.equals(".") && !lemma.equals("null") && !lemma.equals("_"))
							result += lemma;
						result +="."+st[1];
						
						result += "} ";
					}
				}
			}
			else
			{
				System.err.println("Unknown tag");
			}

		}
		
		
		//System.out.println("End tagger2unitex");
		
		return result;
		
	}


}


