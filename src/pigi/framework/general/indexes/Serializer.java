package pigi.framework.general.indexes;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import pigi.framework.general.vo.DataObject;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class Serializer {


	public String serializeFields(DataObject objectVO) throws IndexException{		
		ByteArrayOutputStream output = new ByteArrayOutputStream();		
		try {
			ObjectOutputStream oo = new ObjectOutputStream(output);			
			oo.writeObject(objectVO);
		} catch (IOException e) {
			throw new IndexException(e);
		}		
		BASE64Encoder encoder = new BASE64Encoder();			
		return encoder.encode(output.toByteArray()); 
	}
	
	public DataObject deserialize(String serializedData) throws IndexException {
		BASE64Decoder decoder = new BASE64Decoder();					
		try {
			ByteArrayInputStream input = new ByteArrayInputStream(decoder.decodeBuffer(serializedData));
			ObjectInputStream oi = new ObjectInputStream(input);
	        Object o = oi.readObject();
	        if (o instanceof DataObject) {
	            return (DataObject) o;
	        }
		} catch (IOException e) {
			throw new IndexException(e);
		} catch (ClassNotFoundException e) {
			throw new IndexException(e);
		}
		throw new IndexException(" index field 'data:' has to be a serialized Map !!! "); 
	}
	
	
}
