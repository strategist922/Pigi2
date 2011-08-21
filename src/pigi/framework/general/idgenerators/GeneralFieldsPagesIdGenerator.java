package pigi.framework.general.idgenerators;

import java.util.Map;

import pigi.framework.general.GeneralConstants;
import pigi.framework.general.descriptors.IndexDescriptor;
import pigi.framework.general.vo.DataObject;


public class GeneralFieldsPagesIdGenerator<T extends DataObject> implements IdGeneratorForPages {
	private Keys idTool;
	private IndexDescriptor<T> indexDescriptor;
	private Map<String, String> propertyValues;

	public GeneralFieldsPagesIdGenerator(IndexDescriptor<T> indexDescriptor, Map<String, String> propertyValues) {
		this.indexDescriptor = indexDescriptor;
		this.idTool = new Keys();
		this.propertyValues = propertyValues;
	}

	public String getFirstPossibleId() {
		return generateFirstPossibleId();
	}

	public String getLastPossibleId() {
		return generateLastPossibleId();
	}

	public String getIdByOrderNo(int n) {
		return createId(n);
	}

	public String getNextId(String id) {
		return createId(extractPageNumberFromId(id) + 1);
	}

	public int getOrderNo(String id) {
		return extractPageNumberFromId(id);
	}

	public String getPrevId(String id) {
		return createId(extractPageNumberFromId(id) - 1);
	}

	private int extractPageNumberFromId(String id) {
		return Integer.parseInt(id.substring(id.lastIndexOf(GeneralConstants.ID_PART_SEPARATOR) + 1));
	}

	private String createId(int n) {
		return createId(n + "");
	}

	private String createId(String nr) {
		return GeneralConstants.ID_PART_SEPARATOR +
		       idTool.generateIdValuesPart(indexDescriptor, propertyValues) +
		       GeneralConstants.ID_PART_SEPARATOR + 
		       nr;
	}

	private String generateFirstPossibleId() {
		return createId(idTool.minPageNo());
	}

	private String generateLastPossibleId() {
		return createId(idTool.maxPageNo());
	}
}
