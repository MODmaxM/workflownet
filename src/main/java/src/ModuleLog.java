package src;

import javafx.util.Pair;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.*;
import java.util.stream.Collectors;

public class ModuleLog {
    public static void getLogOfModel(String modelFile, String logFile){
        Pair<List<Place>,List<Arc>> pair= PnmlUtil.getFromXML(modelFile);
        List<Place> places=pair.getKey();
        List<Arc> arcs=pair.getValue();
        int[][] matrix=new int[places.size()][places.size()];
        Map<String,Integer> idMap=new HashMap<>();
        Map<Integer,String> nameMap=new HashMap<>();
        for(int i=0;i<places.size();i++){
            idMap.put(places.get(i).getId(),i);
            if(places.get(i).getType()==1)
                nameMap.put(i,places.get(i).getName());
        }
        arcs.forEach(arc -> {
            int i=idMap.get(arc.getSource());
            int j=idMap.get(arc.getEnd());
            matrix[i][j]=1;
        });

        List<List<Integer>> res= PathUtil.getPath(matrix);


        Set<String> stringRes=new HashSet<>();


        for(List<Integer> l : res){
            String s="";
            for(Integer i: l){
                String name=nameMap.getOrDefault(i,"");
                s+=name.equals("")?"":nameMap.getOrDefault(i,"")+",";
            }
            s=s.substring(0,s.length()-1);
            stringRes.add(s);
        }

        List<String> finalRes=stringRes.stream().collect(Collectors.toList());
        finalRes.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });

        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append("total path: "+finalRes.size()+"\n");
        finalRes.forEach(r -> stringBuilder.append(r+"\n"));

        System.out.println(stringBuilder.toString());

        try {
            File file = new File(logFile);

            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fileWriter = new FileWriter(file.getName(), false);

            BufferedWriter bufferWriter = new BufferedWriter(fileWriter);

            bufferWriter.write(stringBuilder.toString());

            bufferWriter.close();
        }catch (Exception e){
            System.out.println("写文件失败");
        }
    }

    static class PnmlUtil {
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
            File file = new File(Utils.PnmlUtil.class.getClassLoader().getResource(sourcePath).getPath());
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

    static class PathUtil {
        private static List<List<Integer>> res=new ArrayList<>();
        private static List<Integer> path=new ArrayList<>();

        public static List<List<Integer>> getPath(int[][] matrix){
            int[] counts=new int[matrix.length];
            int source=0,target=0;
            for(int i=0;i<matrix.length;i++){
                boolean tag=true;
                for(int j=0;j<matrix.length;j++){
                    if(matrix[j][i]==1){
                        tag=false;
                        break;
                    }
                }
                if(tag){
                    source=i;
                    break;
                }
            }

            for(int i=0;i<matrix.length;i++){
                boolean tag=true;
                for(int j=0;j<matrix.length;j++){
                    if(matrix[i][j]==1){
                        tag=false;
                        break;
                    }
                }
                if(tag){
                    target=i;
                    break;
                }
            }

            res.clear();
            path.clear();

            path.add(source);
            counts[source]++;

            findSimplePathAndCircle(matrix,counts,target);

            return res;
        }

        private static void findSimplePathAndCircle(int[][] matrix,int[] counts,int target){
            int current=path.get(path.size()-1);
            if(current==target){
                res.add(new ArrayList<>(path));
                path.remove(path.size()-1);
                counts[current]--;
                return;
            }

            for(int i=0;i<matrix.length;i++){
                if(matrix[current][i]==1){
                    if(counts[i]<2){
                        path.add(i);
                        counts[i]++;
                        findSimplePathAndCircle(matrix,counts,target);
                    }
                }
            }

            path.remove(path.size()-1);
            counts[current]--;
            return;
        }
    }

    static class Arc {
        private String source;
        private String end;

        public Arc(String source,String end){
            this.source=source;
            this.end=end;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getEnd() {
            return end;
        }

        public void setEnd(String end) {
            this.end = end;
        }
    }

    static class Place {
        private String name;
        private String id;
        private int type;

        public Place(String name,String id) {
            this.name = name;
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }
}
