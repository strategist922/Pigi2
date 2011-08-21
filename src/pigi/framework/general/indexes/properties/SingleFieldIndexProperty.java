package pigi.framework.general.indexes.properties;

import java.util.ArrayList;
import java.util.List;

import pigi.framework.general.descriptors.ValueField;
import pigi.framework.general.vo.DataObject;


/**
 * Single Field Index Property - index property based on single value
 * 
 * @author acure
 * 
 */
public class SingleFieldIndexProperty implements IndexProperty {
	private List<String> values;
    private ValueField valueField;

	public SingleFieldIndexProperty(ValueField valueField, DataObject object) {
		this.valueField = valueField;
		this.values = createValues(object);
	}

	private List<String> createValues(DataObject object) {
		List<String> res = new ArrayList<String>();
		res.add(object.getField(valueField.getName()));
		return res;
	}

	//@Override
	public List<String> values() {
		return values;
	}

	//@Override
	public String name() {
		return valueField.getName();
	}

}
