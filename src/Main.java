
import java.awt.Color;
import java.io.*;
import static java.lang.System.out;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import javax.swing.JFrame;
import org.math.plot.*;

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

    private static final boolean PRINT = false;
    private static final int NUMBER_OF_MOVIE = 1683;
    private static final int NUMBER_OF_USER = 944;
    static double[][] ans = new double[NUMBER_OF_MOVIE][NUMBER_OF_USER];
    static double[][] ans2 = new double[NUMBER_OF_MOVIE][NUMBER_OF_USER];
    static double[][] ans3 = new double[NUMBER_OF_MOVIE][NUMBER_OF_USER];
    static Vector<DoubleInt>[] Cluster = new Vector[1000];
    static Vector<Double> Centroid = new Vector();

    public static void main(String[] args) throws IOException {

        String InputFilePath = "C:\\\\Users\\\\user\\\\Desktop\\\\recommandationsystem-master\\\\recommandationsystem-master\\\\src\\\\DATASET\\\\ml-100k\\\\u1.base";
        fileInput(InputFilePath, 1);

        //Generate similarity matrix
        Vector<DoubleInt> SimilarityVector = new Vector();
        for (int i = 1; i < NUMBER_OF_MOVIE; i++) {
            double x = pearsonCorrelation(ans[1], ans[i]);
//            out.write(x + "  ");
//            out.flush();
            SimilarityVector.add(new DoubleInt(i, x));
        }

        if (PRINT) {
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

        if (PRINT) {
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

        InputFilePath = "C:\\\\Users\\\\user\\\\Desktop\\\\recommandationsystem-master\\\\recommandationsystem-master\\\\src\\\\DATASET\\\\ml-100k\\\\u1.test";
        fileInput(InputFilePath, 2);
        plotGraph(ans2);
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

    private static void fileInput(String InputFilePath, int type) throws FileNotFoundException, UnsupportedEncodingException {
//        String InputFilePath = "C:\\\\Users\\\\muk_58\\\\Desktop\\\\thesis\\\\1. dataset\\\\dataset\\\\ml-100k\\\\u.data";
        BufferedReader br = null;
//        String OutputFilePath="C:\\\\Users\\\\muk_58\\\\Desktop\\\\thesis\\\\1. dataset\\\\dataset\\\\ml-100k\\\\Output.txt";
//        FileOutputStream OutputFile = new FileOutputStream(OutputFilePath);
//        OutputStreamWriter outF2 = new OutputStreamWriter(OutputFile, "UTF-8");
//        BufferedWriter out = new BufferedWriter(outF2);
        if (type == 1) {
            for (int i = 0; i < NUMBER_OF_MOVIE; i++) {
                Arrays.fill(ans[i], 0);
            }
        }
        if (type == 2) {
            for (int i = 0; i < NUMBER_OF_MOVIE; i++) {
                Arrays.fill(ans2[i], 0);
                Arrays.fill(ans3[i], 0);
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
                    ans2[movieId][userId] = queryInput(userId, movieId);
                    ans3[movieId][userId] = rating;
                    if (PRINT) {
                        System.out.println("user id = " + userId + " movie id = " + movieId + " rating : " + ans2[movieId][userId] + " actual rating " + ans3[movieId][userId]);
                    }
                }
            }
        } catch (IOException x) {
            System.err.println(x);
        }
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

        if (PRINT) {
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

    private static double weightedSlopeOne(int SelectedCluster, int movie_id, int user_id) {
        double temp2 = 0.0, temp3 = 0.0;
        for (int i = 0; i < Cluster[SelectedCluster].size(); i++) {
            DoubleInt temp = Cluster[SelectedCluster].get(i);
            double[] a = new double[NUMBER_OF_MOVIE];
            double[] b = new double[NUMBER_OF_MOVIE];
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

    private static double weightedSlopeOneTotal(int movie_id, int user_id) {
        double temp2 = 0.0, temp3 = 0.0;
        for (int j = 0; j < Centroid.size(); j++) {
            for (int i = 0; i < Cluster[j].size(); i++) {
                DoubleInt temp = Cluster[j].get(i);
                double[] a = new double[NUMBER_OF_MOVIE];
                double[] b = new double[NUMBER_OF_MOVIE];
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

    private static double weightedSlopeOneClusterPoint(double[] clusterAsPoint, int movie_id, int user_id) {
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

    private static double queryInput(int userid, int movieid) {
        //giving input query(test data)
        //Scanner in = new Scanner(System.in);
        //while (true) {
        int user_id, movie_id;
//            System.out.println("enter user id : ");
//            user_id = in.nextInt();
//            System.out.println("enter movie id : ");
//            movie_id = in.nextInt();

        user_id = userid;
        movie_id = movieid;

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
        double answer;
        answer = weightedSlopeOne(SelectedCluster, movie_id, user_id);
        answer = weightedSlopeOneTotal(movie_id, user_id);

        //}
        return answer;

    }

    private static void errorMeasure() {

        double rmse = 0.0, rmse2 = 0.0;
        double mae = 0.0, mae2 = 0.0;
        int c = 0;
        for (int i = 1; i <= NUMBER_OF_MOVIE; i++) {
            for (int j = 1; j <= NUMBER_OF_USER; j++) {
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
                    for (int k = 1; k <= NUMBER_OF_USER; k++) {
                        clusterAsPoint[k] = ans[1][k];
                    }
                    for (int k = 1; k <= NUMBER_OF_USER; k++) {
                        clusterAsPoint[k] *= x / Centroid.get(SelectedCluster);
                    }
                    double newRating1 = weightedSlopeOneClusterPoint(clusterAsPoint, i, j);
//                    System.out.println(ans2[i][j]+":"+newRating1);
                    mae2 += Math.pow(ans2[i][j] - newRating1, 2);
                    //one full cluster
                    double newRating2 = weightedSlopeOne(SelectedCluster, i, j);
                    mae += Math.pow(ans2[i][j] - newRating2, 2);
                    c++;
                }
            }
        }
        mae /= c;
        rmse = Math.sqrt(mae);

        mae2 /= c;
        rmse2 = Math.sqrt(mae2);
        System.out.println("For clusterAsPoint RMSE: " + rmse2 + ", For whole one cluster RMSE: " + rmse);
    }

    double[] iToD(int[] array) {
        double[] db = new double[array.length];
        for (int a = 0; a < array.length; a++) {
            db[a] = array[a];
        }
        return db;
    }

    public static void plotGraph(double[][] array) {
        Plot3DPanel panel = new Plot3DPanel();
        //panel.addBarPlot("hello", array);
        //Plot2DPanel panel = new Plot2DPanel();
        int a = 10;
        int b = 10;
        int c = 10;
        double[] x = new double[a];
        double[] y = new double[b];
        double[] z = new double[c];
        double[][] list = new double[a][b];
        for (int i = 0; i < a; i++) {
            x[i] = y[i] = z[i] = i;
            for (int j = 0; j < b; j++) {
                list[i][j] = (i + j) % 5;
            }
        }
        panel.addLinePlot("df", Color.GREEN, x, y, z);
        //panel.addGridPlot("he", Color.darkGray, x, y, list);
        //panel.addLinePlot("hi", x, y);

        JFrame frame = new JFrame("Plot");
        frame.setContentPane(panel);
        frame.setSize(1000, 800);
        frame.setVisible(true);
    }
    //bar plot: looks like tall building
    //grid plot: with surface

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
}

/*

 */
