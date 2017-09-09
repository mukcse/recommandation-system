
import java.io.*;
import static java.lang.System.out;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Mansur Uddin
 */

public class Main {
    
    private static final boolean PRINT = true;
    static double[][] ans = new double[1683][944];
    static double[][] ans2 = new double[1683][944];
    static Vector<DoubleInt>[] Cluster = new Vector[1000];
    static Vector<Double> Centroid = new Vector();

    private static double deviation(double[] a, double[] b) {
        double sum = 0.0;
        int temp = 0;
        for (int i = 0; i < a.length; i++) {
            if (a[i] == 0 || b[i] == 0) {
                continue;
            }
            sum += b[i] - a[i];
            temp++;
        }
        if (temp == 0) {
            return 0.0;
        }
        return sum / temp;
    }

    private static void queryInput() {
        //giving input query(test data)
        Scanner in = new Scanner(System.in);
        while (true) {
            int user_id, movie_id;
            System.out.println("enter user id : ");
            user_id = in.nextInt();
            System.out.println("enter movie id : ");
            movie_id = in.nextInt();
            double x = pearsonCorrelation(ans[1], ans[movie_id]);
//            x = Math.acos(x);
            int SelectedCluster = 0;
            double mn = Double.MAX_VALUE;
            for (int i = 0; i < Centroid.size(); i++) {
                mn = Math.min(Centroid.get(i), x);
                if (mn > Math.abs(Centroid.get(i) - x)) {
                    mn = Math.abs(Centroid.get(i) - x);
                    SelectedCluster = i;
                }
            }
            double answer = weightedSlopeOne(SelectedCluster, movie_id, user_id);
            System.out.println("rating : " + answer);

        }

    }

    private static double weightedSlopeOne(int SelectedCluster, int movie_id, int user_id) {
        double temp2 = 0.0, temp3 = 0.0;
        for (int i = 0; i < Cluster[SelectedCluster].size(); i++) {
            DoubleInt temp = Cluster[SelectedCluster].get(i);
            double[] a = new double[1684];
            double[] b = new double[1684];
            a = ans[movie_id];
            b = ans[temp.a];
            double dev = deviation(b, a);
            double similarity = pearsonCorrelation(a, b);
            double temp4 = ans[temp.a][user_id];
            if (temp4 == 0.0) {
                continue;//not rated
            }
            temp2 += similarity * (dev + temp4);
            temp3 += similarity;
        }

        double result;
        if (temp3 == 0.0) {
            result = 0;
        } else {
            result = temp2 / temp3;
        }
//        System.out.println("The Glorious Result is ...........->>>  " + result);
        return result;
    }

    private static void fileInput(String InputFilePath, int type) throws FileNotFoundException, UnsupportedEncodingException {
//        String InputFilePath = "C:\\\\Users\\\\muk_58\\\\Desktop\\\\thesis\\\\1. dataset\\\\dataset\\\\ml-100k\\\\u.data";
        BufferedReader br = null;
//        String OutputFilePath="C:\\\\Users\\\\muk_58\\\\Desktop\\\\thesis\\\\1. dataset\\\\dataset\\\\ml-100k\\\\Output.txt";
//        FileOutputStream OutputFile = new FileOutputStream(OutputFilePath);
//        OutputStreamWriter outF2 = new OutputStreamWriter(OutputFile, "UTF-8");
//        BufferedWriter out = new BufferedWriter(outF2);
        if (type == 2) {
            for (int i = 0; i < 1683; i++) {
                Arrays.fill(ans2[i], 0);
            }
        }
        try {

            String sCurrentLine;

            br = new BufferedReader(new FileReader(InputFilePath));
            sCurrentLine = br.readLine();
            while ((sCurrentLine = br.readLine()) != null) {
                int userId, movieId, tmp;
                double rating;
//                sCurrentLine+=' ';
                String[] x = new String[5];
                for (int i = 0; i < 5; i++) {
                    x[i] = new String();
                }
                int now = 0;
                String s = "";
                for (int i = 0; i < sCurrentLine.length(); i++) {
                    if (!(sCurrentLine.charAt(i) >= '0' && sCurrentLine.charAt(i) <= '9')) {
                        if (s != "") {
                            x[now] = s;
//                            System.out.println("->"+s);
                            now++;
                        }
                        s = "";
                    } else {
                        s += sCurrentLine.charAt(i);
                    }
                }
//                System.out.println(""+x[0]);
                userId = convert(x[0]);
                movieId = convert(x[1]);
//                System.out.println(""+userId+";"+movieId);
                rating = convert2(x[2]);
                if (type == 1) {
                    ans[movieId][userId] = rating;
                } else {
                    ans2[movieId][userId] = rating;
                }
            }
        } catch (IOException x) {
            System.err.println(x);
        }
    }

    private static void errorMeasure() {

        double rmse = 0.0,rmse2=0.0;
        double mae = 0.0,mae2=0.0;
        int c = 0;
        for (int i = 1; i <= 1682; i++) {
            for (int j = 1; j <= 943; j++) {
                if (ans2[i][j] != 0.0) {
                    double x = pearsonCorrelation(ans[1], ans[i]);
//            x = Math.acos(x);
                    int SelectedCluster = 0;
                    double mn = Double.MAX_VALUE;
                    for (int k = 0; k < Centroid.size(); k++) {
                        mn = Math.min(Centroid.get(k), x);
                        if (mn > Math.abs(Centroid.get(k) - x)) {
                            mn = Math.abs(Centroid.get(k) - x);
                            SelectedCluster = k;
                        }
                    }
                    //cluster as a point
                    double[] clusterAsPoint = new double[944];
                    for (int k = 1; k <= 943; k++) {
                        clusterAsPoint[k] = ans[1][k];
                    }
                    for (int k = 1; k <= 943; k++) {
                        clusterAsPoint[k] *= x / Centroid.get(SelectedCluster);
                    }
                    double newRating1 = weightedSlopeOne(clusterAsPoint, i, j);
//                    System.out.println(ans2[i][j]+":"+newRating1);
                    mae2 += Math.pow(ans2[i][j] - newRating1,2);
                    //one full cluster
                    double newRating2 = weightedSlopeOne(SelectedCluster, i, j);
                    mae += Math.pow(ans2[i][j] - newRating2, 2);
                    c++;
                }
            }
        }
        mae /= c;
        rmse = Math.sqrt(mae);
        
        mae2/=c;
        rmse2 = Math.sqrt(mae2);
        System.out.println("For clusterAsPoint RMSE: " + rmse2+", For whole one cluster RMSE: "+rmse);
    }

    private static double weightedSlopeOne(double[] clusterAsPoint, int movie_id, int user_id) {
        double temp2 = 0.0, temp3 = 0.0;
        double[] a = new double[1684];
        double[] b = new double[1684];
        a = ans[movie_id];
        b = clusterAsPoint;
        double dev = deviation(b, a);
        double similarity = pearsonCorrelation(a, b);
        double temp4 = clusterAsPoint[user_id];
        if (temp4 == 0.0) {
            return 0;
        }
        temp2 += similarity * (dev + temp4);
        temp3 += similarity;

        double result;
        if (temp3 == 0.0) {
            result = 0;
        } else {
            result = temp2 / temp3;
        }
//        System.out.println("The Glorious Result is ...........->>>  " + result);
        return result;
    }

    boolean cmp(DoubleInt a, DoubleInt b) {
        return a.b < b.b;
    }

    public static int convert(String s) {
        int x = 0;
        for (int i = 0; i < s.length(); i++) {
            x = x * 10 + (s.charAt(i) - '0');
        }
        return x;
    }

    public static double convert2(String s) {
        double x = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '.') {
                double a = 1, b = 0;
                for (int j = i + 1; j < s.length(); j++) {
                    b = b * 10 + (s.charAt(i) - '0');
                    a = a * 10;
                }
                b /= a;
                return x + b;
            }
            x = x * 10 + (s.charAt(i) - '0');
        }
        return x;
    }

    public static void main(String[] args) throws IOException {

//        Graph graph = new Graph();
//        List<Double> scores = new ArrayList<>();
//        int maxDataPoints = 40;
//        for (int i = 0; i < maxDataPoints; i++) {
//            scores.add((double) i);
//        }
//        graph.createAndShowGui(scores);
        for (int i = 0; i < 1683; i++) {
            Arrays.fill(ans[i], 0);
        }

        String InputFilePath = "F:\\\\PROJECT\\\\recommandationsystem\\\\src\\\\DATASET\\\\ml-100k\\\\u1.base";
        fileInput(InputFilePath, 1);

       //Generate similarity matrix
        Vector<DoubleInt> SimilarityVector = new Vector();
        for (int i = 1; i < 1683; i++) {
            double x = pearsonCorrelation(ans[1], ans[i]);
//            out.write(x + "  ");
//            out.flush();
            SimilarityVector.add(new DoubleInt(i, x));
        }
     
if(PRINT){
        //Display pearsonCorrelation similarity value 
        for (int i = 0; i < SimilarityVector.size(); i++) {
//            out.write(SimilarityVector.get(i).a + " -- " + SimilarityVector.get(i).b + " : ");
//            out.flush();
            System.out.print(SimilarityVector.get(i).a + "-" + SimilarityVector.get(i).b + " : ");
        }
        System.out.println();
}
//        Collections.sort(SimilarityVector,cmp); bubble sort
        for (int i = 0; i < SimilarityVector.size(); i++) {
            for (int j = i + 1; j < SimilarityVector.size(); j++) {
                if (SimilarityVector.get(i).b > SimilarityVector.get(j).b) {
                    DoubleInt t = new DoubleInt();
                    t = SimilarityVector.get(i);
                    SimilarityVector.set(i, SimilarityVector.get(j));
                    SimilarityVector.set(j, t);
                }
            }
        }
        
if(PRINT){
        //Display the sorted SimilarityVectorilarity values
        for (int i = 0; i < SimilarityVector.size(); i++) {
//            out.write(SimilarityVector.get(i).a + " -- " + SimilarityVector.get(i).b + " : ");
//            out.flush();
            System.out.print(SimilarityVector.get(i).a + "-" + SimilarityVector.get(i).b + " : ");
        }
//        out.newLine();
//        out.flush();
        System.out.println();
}

        // run kmeans on training data
        kmeans(SimilarityVector);
        queryInput();
/*
        InputFilePath = "C:\\\\Users\\\\muk_58\\\\Desktop\\\\thesis\\\\1. dataset\\\\dataset\\\\ml-100k\\\\u1.test";
        fileInput(InputFilePath, 2);
        errorMeasure();
*/
//
//        InputFilePath = "C:\\\\Users\\\\muk_58\\\\Desktop\\\\thesis\\\\1. dataset\\\\dataset\\\\ml-100k\\\\u2.test";
//        fileInput(InputFilePath, 2);
//        errorMeasure();
//        
//        InputFilePath = "C:\\\\Users\\\\muk_58\\\\Desktop\\\\thesis\\\\1. dataset\\\\dataset\\\\ml-100k\\\\u3.test";
//        fileInput(InputFilePath, 2);
//        errorMeasure();
//        
//        InputFilePath = "C:\\\\Users\\\\muk_58\\\\Desktop\\\\thesis\\\\1. dataset\\\\dataset\\\\ml-100k\\\\u4.test";
//        fileInput(InputFilePath, 2);
//        errorMeasure();
//        
//        InputFilePath = "C:\\\\Users\\\\muk_58\\\\Desktop\\\\thesis\\\\1. dataset\\\\dataset\\\\ml-100k\\\\u5.test";
//        fileInput(InputFilePath, 2);
//        errorMeasure();
//        
//        InputFilePath = "C:\\\\Users\\\\muk_58\\\\Desktop\\\\thesis\\\\1. dataset\\\\dataset\\\\ml-100k\\\\ub.test";
//        fileInput(InputFilePath, 2);
//        errorMeasure();
    }

    private static double cosineSimilarity(double[] a, double[] b) {
        double pd = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (int i = 0; i < b.length; i++) {
            pd += a[i] * b[i];
            normA += Math.pow(a[i], 2);
            normB += Math.pow(b[i], 2);
        }
        return pd / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    private static double pearsonCorrelation(double[] a, double[] b) {
        double pd = 0.0;
        double normA = 0.0;
        double normB = 0.0, avgA = 0.0, avgB = 0.0, sum;
        int cnt = 0;
        sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            if (a[i] == 0.0) {
                continue;
            }
            sum += a[i];
            cnt++;
        }
        if (cnt == 0) {
            avgA = 0;
        } else {
            avgA = sum / cnt;
        }
        sum = 0.0;
        cnt = 0;
        for (int i = 0; i < b.length; i++) {
            if (b[i] == 0.0) {
                continue;
            }
            sum += b[i];
            cnt++;
        }
        if (cnt == 0) {
            avgB = 0;
        } else {
            avgB = sum / cnt;
        }
        for (int i = 0; i < b.length; i++) {
            if (a[i] == 0.0 || b[i] == 0.0) {
                continue;
            }
            pd += (a[i] - avgA) * (b[i] - avgB);
            normA += Math.pow((a[i] - avgA), 2);
            normB += Math.pow((b[i] - avgB), 2);
        }
        double temp = (Math.sqrt(normA) * Math.sqrt(normB));
        if (temp == 0.0) {
            return 0.0;
        }
        return pd / temp;
    }

    private static void kmeans(Vector<DoubleInt> v) {
        DoubleInt a, b, c;
        int n, sz;
        sz = v.size();

//        Scanner in = new Scanner(System.in);
//        n = in.nextInt();

        //By rules of thumb , determine the optimal value of K for kmeans
        n = (int) Math.ceil(Math.sqrt(1682 / 2));
//        n = silhoutte(v);

//        a = v.get(0);
//        b = v.get(sz / 2);
//        c = v.get(sz - 1);        
        Vector<Double> tmp = new Vector();
        Random rnd = new Random();
        Map m2 = new HashMap();
        for (int i = 0; i < n;) {
            int t;
            t = ((rnd.nextInt() % sz) + sz) % sz;
            if (m2.get(t) == null) {
                Centroid.add(v.get(t).b);
                m2.put(t, t);
                i++;
                //System.out.println("ki holo --------->>>> " + t);
            }
        }
        Collections.sort(Centroid);
        tmp = Centroid;

        for (int i = 0; i < 1000; i++) {
            Cluster[i] = new Vector();
        }
        while (true) {
            for (int i = 0; i < n; i++) {
                Cluster[i].clear();
            }
            for (int i = 0; i < sz; i++) {
                int t = 0;
                double mn = Double.MAX_VALUE;
                for (int j = 0; j < n; j++) {
                    double t2;
                    t2 = Math.abs(Centroid.get(j) - v.get(i).b);
                    if (mn > t2) {
                        mn = t2;
                        t = j;
                    }
                }
                Cluster[t].add(v.get(i));
            }
            for (int i = 0; i < n; i++) {
                double sum = 0.0;
                for (int j = 0; j < Cluster[i].size(); j++) {
                    sum += Cluster[i].get(j).b;
                }
                //   System.out.println(i+":sum="+sum);
                Centroid.set(i, sum / (double) Cluster[i].size());
            }
            boolean fl = true;
            for (int i = 0; i < Centroid.size(); i++) {
                if (Centroid.get(i) != tmp.get(i)) {
                    fl = false;
                    break;
                }
            }
            if (fl == true) {
                break;
            }
            tmp = Centroid;
        }

if(PRINT){        
        for (int i = 0; i < Centroid.size(); i++) {
            System.out.println();
            System.out.println("Cluster no : " + i + "  Cluser Size : " + Cluster[i].size());
            System.out.println("Centroid is " + Centroid.get(i));
            System.out.println("Elements of Cluster");

            for (int j = 0; j < Cluster[i].size(); j++) {
                System.out.print(Cluster[i].get(j).a + " | ");
            }
            System.out.println();
            System.out.println();
        }
}        
        return;
    }
}

/*

 */
