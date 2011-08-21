package pigi.framework.general.descriptors.configuration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class HbaseIndexConfigReader {

	
	String configFileName = "HBaseIndexConfig.txt";
	
	String jsonConfigString;
	
	public HbaseIndexConfigReader()
	{
		
	}
	
	public HbaseIndexConfigReader(String jsonConfigString)
	{
		this.jsonConfigString = jsonConfigString;
	}

	public IndexConfigurationList getIndexConfigurations() {
		
		if(this.jsonConfigString == null || this.jsonConfigString.isEmpty())
			getConfigFileContents();
		return getConfigrationList(this.jsonConfigString);
	}
	public IndexConfigurationList getConfigrationList(String configurationString)
	{
		try
		{
		return (IndexConfigurationList) JacksonHelper.fromJsonStr(
				configurationString, IndexConfigurationList.class);
		}catch(RuntimeException e)
		{
			throw new RuntimeException(e.getMessage());
		}
	}
	private void getConfigFileContents()
	{
		StringBuilder contents = new StringBuilder();
		InputStreamReader reader = new InputStreamReader(HbaseIndexConfigReader.class.getClassLoader().getResourceAsStream(configFileName));
		try {
			BufferedReader input = new BufferedReader(reader);
			try {
				String line = null; // not declared within while loop
				while ((line = input.readLine()) != null) {
					contents.append(line);
					contents.append(System.getProperty("line.separator"));
				}
			} finally {
				input.close();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex.getCause());
		}
		this.jsonConfigString = contents.toString();
	}
}
