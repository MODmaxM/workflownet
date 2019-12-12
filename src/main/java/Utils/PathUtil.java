package Utils;

import java.util.ArrayList;
import java.util.List;

public class PathUtil {
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
