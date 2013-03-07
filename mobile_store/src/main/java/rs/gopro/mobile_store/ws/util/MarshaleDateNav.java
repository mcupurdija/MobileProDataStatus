package rs.gopro.mobile_store.ws.util;

import java.io.IOException;
import java.util.Date;

import org.ksoap2.serialization.Marshal;
import org.ksoap2.serialization.MarshalDate;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import rs.gopro.mobile_store.util.DateUtils;

public class MarshaleDateNav implements Marshal {
	
	public static Class<Date> DATE_CLASS = Date.class;
	
	@Override
	public Object readInstance(XmlPullParser parser, String namespace,
			String name, PropertyInfo expected) throws IOException,
			XmlPullParserException {
			return DateUtils.unMarshaleDate(parser.nextText());
	}

	@Override
	public void writeInstance(XmlSerializer writer, Object instance)
			throws IOException {
		writer.text(DateUtils.marshaleDate((Date) instance));	
	}

	@Override
	public void register(SoapSerializationEnvelope envelope) {
		envelope.addMapping(envelope.xsd, "dateTime", MarshalDate.DATE_CLASS, this);
	}

}
