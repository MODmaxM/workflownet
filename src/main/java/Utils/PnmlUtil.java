package Utils;

import javafx.util.Pair;
import model.Arc;
import model.Place;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PnmlUtil {
    public static Pair<List<Place>,List<Arc>> getFromXML(String path){
        Document dom=useDomReadXml(path);
        if(dom==null)
            dom=fromAbsSourcePath(path);
        Element element=dom.getRootElement();
        element=element.elementIterator().next();
        Iterator<Element> elementIterator=element.elementIterator("place");
        List<Place> places=fromPlaces(elementIterator);
        places.forEach(p -> p.setType(0));
        elementIterator= element.elementIterator("transition");
        List<Place> trans=fromPlaces(elementIterator);
        trans.forEach(t -> t.setType(1));
        places.addAll(trans);
        elementIterator=element.elementIterator("arc");
        List<Arc> arcs=fromArc(elementIterator);
        return new Pair<>(places,arcs);
    }

    private static List<Place> fromPlaces(Iterator<Element> elementIterator){
        String name,id;
        Place place;
        List<Place> res=new ArrayList<>();
        while (elementIterator.hasNext()){
            Element e=elementIterator.next();
            name=e.element("name").element("text").getText();
            id=e.attribute("id").getValue();
            place=new Place(name,id);
            res.add(place);
        }
        return res;
    }

    private static List<Arc> fromArc(Iterator<Element> elementIterator){
        List<Arc> res=new ArrayList<>();
        while (elementIterator.hasNext()){
            Element e=elementIterator.next();
            res.add(new Arc(e.attributeValue("source"),e.attributeValue("target")));
        }
        return res;
    }

    private static Document useDomReadXml(String sourcePath){
        File file = new File(PnmlUtil.class.getClassLoader().getResource(sourcePath).getPath());
        try {
            SAXReader reader = new SAXReader();
            // 读取XML文件结构
            Document doc = reader.read(file);
            return doc;
        } catch (Exception e) {
            System.out.println("读取该xml文件失败");
            e.printStackTrace();
        }
        return null;
    }

    private static Document fromAbsSourcePath(String absPath){
        File file = new File(absPath);
        try {
            SAXReader reader = new SAXReader();
            // 读取XML文件结构
            Document doc = reader.read(file);
            return doc;
        } catch (Exception e) {
            System.out.println("读取该xml文件失败");
            e.printStackTrace();
        }
        return null;
    }
}
