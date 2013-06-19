package com.marker.markcar.map.parse;

import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.content.Context;

import com.marker.markcar.map.item.Map;
import com.marker.markcar.map.item.MapItem;
import com.marker.markcar.map.item.ParkingSpace;

public class XMLMapParser implements MapParser {
    private static final String ELEMENT_MAP = "map";
    private static final String ELEMENT_PARK_GROUP = "park-group";
    private static final String ELEMENT_PARK = "park";

    private static final String ATTR_ID = "id";
    private static final String ATTR_NAME = "name";
    private static final String ATTR_WIDTH = "width";
    private static final String ATTR_HEIGHT = "height";
    private static final String ATTR_X = "x";
    private static final String ATTR_Y = "y";
    private static final String ATTR_DEGREE = "degree";

    private Context mContext;
    private String mPath;

    public XMLMapParser(Context context, String path) {
        mContext = context;
        mPath = path;
    }

    @Override
    public Map parse() {
        Map result = null;
        SAXParserFactory factory=SAXParserFactory.newInstance();
        try {
            SAXParser parser=factory.newSAXParser();
            XMLReader xmlReader = parser.getXMLReader();
            MapHandler handler = new MapHandler();
            xmlReader.setContentHandler(handler);
            xmlReader.parse(new InputSource(mContext.getAssets().open(mPath)));
            result = handler.getMap();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    private static class MapHandler extends DefaultHandler {

        private ArrayList<MapItem> mItemList = new ArrayList<MapItem>();
        private float mWidth = 0;
        private float mHeight = 0;
        private MapItem mItem;

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if (ELEMENT_MAP.equals(localName)) {
                mWidth = Float.parseFloat(attributes.getValue(ATTR_WIDTH));
                mHeight = Float.parseFloat(attributes.getValue(ATTR_HEIGHT));
            } else if (ELEMENT_PARK.equals(localName)) {
                String degree = attributes.getValue(ATTR_DEGREE);
                if (degree == null) {
                    degree = "0";
                }
                mItem = new ParkingSpace(Float.parseFloat(attributes.getValue(ATTR_X)), Float.parseFloat(attributes
                        .getValue(ATTR_Y)), Integer.parseInt(degree), attributes.getValue(ATTR_NAME));
                mItemList.add(mItem);
            }
        }

        public Map getMap() {
            return new Map(mItemList, mWidth, mHeight);
        }
    }
}
