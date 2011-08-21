/**
 * Copyright 2011 ChooChee,Inc. All rights reserved.
 * 
 * @author dev
 * @version $Revision: 1.0 $
 * @since 1.0
 */
package pigi.framework.general.descriptors.configuration;

import java.io.StringWriter;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.AnnotationIntrospector;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.introspect.JacksonAnnotationIntrospector;
import org.codehaus.jackson.xc.JaxbAnnotationIntrospector;

public class JacksonHelper {
	public static String toJsonStr(Object obj) {

		try {
			AnnotationIntrospector primary = new JacksonAnnotationIntrospector();
			AnnotationIntrospector secondary = new JaxbAnnotationIntrospector();
			AnnotationIntrospector pair = new AnnotationIntrospector.Pair(primary, secondary);

			ObjectMapper mapper = new ObjectMapper();

			mapper.getSerializationConfig().setAnnotationIntrospector(pair);

			mapper.getSerializationConfig().setSerializationInclusion(
					JsonSerialize.Inclusion.NON_NULL);

	        StringWriter sw = new StringWriter();
			mapper.writeValue(sw, obj);
	        return sw.toString();
		} catch (Exception e) {
			System.out.println(e.getMessage() + e);
            throw new RuntimeException("Internal Error", e);
		}

	}

	public static Object fromJsonStr(String jsonStr, Class<?> clazz) {

		try {
			return new ObjectMapper().readValue(jsonStr, clazz);
		} catch (JsonParseException e) {
			System.out.println("JsonParseException : " + e.toString());
	        throw new RuntimeException("Internal Error", e);
		} catch (Exception e) {
			System.out.println("IOException : " + e.toString());
            throw new RuntimeException("Internal Error", e);
		}
	}
}
