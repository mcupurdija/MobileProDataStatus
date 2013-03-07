package rs.gopro.mobile_store.ws.util;

import java.io.IOException;

import org.ksoap2.serialization.Marshal;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

public class MarshaleDouble implements Marshal {

	public static Class<Double> DOUBLE_CLASS = Double.class;
	
	public MarshaleDouble() {
	}

	public Object readInstance(XmlPullParser parser, String namespace, String name, PropertyInfo expected) 
			throws IOException, XmlPullParserException {
		return Double.parseDouble(parser.nextText());
	}

	public void register(SoapSerializationEnvelope envelope) {
		envelope.addMapping(envelope.xsd, "double", MarshaleDouble.DOUBLE_CLASS, this);

	}

	public void writeInstance(XmlSerializer writer, Object obj)
			throws IOException {
		writer.text(obj.toString());
	}
}
