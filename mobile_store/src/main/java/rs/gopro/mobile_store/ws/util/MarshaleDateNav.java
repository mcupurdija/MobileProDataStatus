package rs.gopro.mobile_store.ws.util;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.ksoap2.serialization.Marshal;
import org.ksoap2.serialization.MarshalDate;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

public class MarshaleDateNav implements Marshal {
	public static Class DATE_CLASS = Date.class;
	
	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	@Override
	public Object readInstance(XmlPullParser parser, String namespace,
			String name, PropertyInfo expected) throws IOException,
			XmlPullParserException {
		try {
			return simpleDateFormat.parse(parser.nextText());
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void writeInstance(XmlSerializer writer, Object instance)
			throws IOException {
		writer.text(simpleDateFormat.format(((Date) instance)));	
	}

	@Override
	public void register(SoapSerializationEnvelope envelope) {
		envelope.addMapping(envelope.xsd, "dateTime", MarshalDate.DATE_CLASS, this);
	}

}
