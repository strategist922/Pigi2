package pigi.framework.general.indexes.properties;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import pigi.framework.general.descriptors.IndexedField;
import pigi.framework.general.descriptors.RangeField;
import pigi.framework.general.desriptors.fields.Field;
import pigi.framework.general.vo.DataObject;

/**
 * Implementation of range value property - based on two decimal numbers - generates
 * property value for every number from range
 * for example for numbers 10, 14 property values will be: 10, 11, 12, 13, 14
 *  
 * @author kgalecki
 *
 */
public class RangeValueIndexProperty implements IndexProperty {

	private List<String> values;
    private RangeField rangeField;

	public RangeValueIndexProperty(RangeField rangeField, DataObject object) {
	    this.rangeField = rangeField;
		this.values = createValues(object);
	}

	private List<String> createValues(DataObject object) {
		List<String> res = new ArrayList<String>();
		int from = object.getInt(rangeField.firstField().getName());
		int to = object.getInt(rangeField.lastField().getName());

		for (int i = from; i <= to; i++) {
			res.add(String.valueOf(i));
		}
		return res;
	}

	//@Override
	public String name() {
		return rangeField.getName();
	}

	//@Override
	public List<String> values() {
		return values;
	}

}
