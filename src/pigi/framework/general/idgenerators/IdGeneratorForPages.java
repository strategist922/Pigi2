package pigi.framework.general.idgenerators;

/**
 * Interface of id generator for pages table
 */
public interface IdGeneratorForPages {

	public String getIdByOrderNo(int n);

	public String getFirstPossibleId();

	public String getLastPossibleId();

	public String getNextId(String id);

	public String getPrevId(String id);

	public int getOrderNo(String id);

}
