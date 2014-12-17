import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class IKDDhw6 {

	public static void main(String[] args) {
		String workingDir = System.getProperty("user.dir");
		ArrayList<ArrayList<Integer>>allData = new ArrayList<ArrayList<Integer>>();
		ArrayList<Integer>trueResult = readfile(allData,workingDir);
		int predicted[][] = predictMissingValue(allData);
		double maxDistance = 0;
		double sum = 0;
		for(int i = 0; i < allData.size(); i++)
			for(int j = 0; j < allData.size(); j++)
		if(computeDistance(predicted[i], predicted[j]) <= 1 )
			sum += computeDistance(predicted[i], predicted[j]);
		
		double average = sum/(allData.size()*allData.size());
		for(int i = 0; i < allData.size(); i++)
			for(int j = 0; j < allData.size(); j++)
				if(computeDistance(predicted[i], predicted[j]) > maxDistance)
					maxDistance = computeDistance(predicted[i], predicted[j]);
		
		
		ArrayList<Integer> maxNum = new ArrayList<Integer>();
		for(int i = 0; i < allData.size(); i++)
		{
			int temp = 0;
			for(int j = 0; j < allData.size(); j++)
				if(computeDistance(predicted[i], predicted[j]) == maxDistance)temp++;
			maxNum.add(temp);
		}
		
		int max = 0, rem = 0;
		double averageTrue = computeTrueAverage(predicted);
		double variance = computeVariance(predicted);
		for(int i = 0; i <  predicted.length; i++)
			if(maxNum.get(i) > max && deleteExtremeValue(predicted[i],averageTrue,variance))
				{
				max = maxNum.get(i);
				rem = i;
				}
		ArrayList<Integer> myPredict = new ArrayList<Integer>();
		for(int i = 0; i < predicted.length; i++)
		{
			if(computeDistance(predicted[rem],predicted[i]) < average){
				myPredict.add(1);
			}
			else myPredict.add(0);
		}
		
		//System.out.println(F1_score(trueResult,myPredict));
		
		try {
			FileWriter fw;
			fw = new FileWriter("cluster1.csv");
			BufferedWriter bw = new BufferedWriter(fw);
			for(int i = 0; i < myPredict.size(); i++)if(myPredict.get(i) == 1)bw.write(Integer.toString(i + 1)+"\n");
			bw.close();
			fw.close();
		}catch (IOException e) {}
		
		try {
			FileWriter fw1;
			fw1 = new FileWriter("cluster2.csv");
			BufferedWriter bw1 = new BufferedWriter(fw1);
			for(int i = 0; i < myPredict.size(); i++)if(myPredict.get(i) == 0)bw1.write(Integer.toString(i + 1)+"\n");
			bw1.close();
			fw1.close();
		}catch (IOException e) {}
		
	}
	public static ArrayList<Integer> readfile(ArrayList<ArrayList<Integer>>allData,String path)
	{
		FileReader fr;
		ArrayList<Integer> trueResult =  new ArrayList<Integer>() ;
		try {
			fr = new FileReader(path+"/house-votes-84.data");
			String oneLine;
			BufferedReader br = new BufferedReader(fr);
			ArrayList<Integer> temp =  new ArrayList<Integer>() ;
			
			while ((oneLine = br.readLine()) != null) {
				
				//System.out.println(oneLine);
				temp = new ArrayList<Integer>();
				int i = 0, j = 0;
				String oneItem;
				int k = oneLine.indexOf(",");
				oneItem = oneLine.substring(0, k);
				if(oneItem.equals("republican"))trueResult.add(1);
				else trueResult.add(0);
				for(;oneLine.indexOf(",", j + 1) != -1;)
				{
					i = oneLine.indexOf(",",j);
					
					i = i + 1;
					j = oneLine.indexOf(",",i);
				    oneItem = oneLine.substring(i, j);
					if(oneItem.equals("n") || oneItem.equals("?"))temp.add(0);
					else temp.add(1);
				}
				j = j + 1;
				 oneItem = oneLine.substring(j);
				 if(oneItem.equals("n"))temp.add(0);
				 else if(oneItem.equals("y"))temp.add(1);
				 else temp.add(-1);
				 //System.out.println(temp);
				 allData.add(temp);
			}
			
			}
		catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("123");
			e.printStackTrace();
		}
		return trueResult;
	}
	public static int[][] predictMissingValue(ArrayList<ArrayList<Integer>>allData)
	{
		int size =  allData.get(0).size();
		int outSize = allData.size();
		int predicted[][] = new int[outSize][size];
		for(int i = 0; i < size; i++)
		{
			int tempSum0 = 0;
			int tempSum1 = 0;
			int fake = 0;
			for(int j = 0; j < outSize; j++)
				{
				predicted[j][i] = allData.get(j).get(i);
				if(allData.get(j).get(i) != -1)
					{
						if(allData.get(j).get(i) == 1)tempSum1++;
						else tempSum0++;
					}
				else fake++;
				}
			Random ran = new Random();
	  		for(int k = 0; k < outSize; k++)
	  			{
	  			if(allData.get(k).get(i) == -1)	
	  			{
	  			int ranresult = ran.nextInt(outSize-fake);
	  			if(ranresult < tempSum1)predicted[k][i] = 1;
	  			else predicted[k][i] = 0;
	  			}
	  			}
		}
		return predicted;
	}
	public static double computeDistance(int[] a, int[] b)
	{
		
		double child = 0;
		double mother = 0;
		int size = a.length;
		for(int i = 0; i < size; i++)
		{
			mother += (a[i] | b[i]);
			if(a[i] != 1 || b[i] != 1)child += (a[i] | b[i]);
		}
		return child/mother;
	}
	public static boolean deleteExtremeValue(int[] a, double average, double variance)
	{
		int sum = 0;
		for(int i = 0; i < a.length; i++)sum += a[i];
		if(sum < average - Math.sqrt(variance) || sum > a.length - 2)return false;
		return true;
	}
	public static double F1_score(ArrayList<Integer> a, ArrayList<Integer> b)
	{
		double truePositive = 0;
		double falsePositive = 0;
		double falseNegative = 0;
		double Precision = 0;
		double recall = 0;
		double score = 0;
		
		for(int i = 0; i < a.size(); i++)
		{
			if(a.get(i) == 1 && b.get(i) == 1 )truePositive++;
			else if(a.get(i) == 0 && b.get(i) == 1)falsePositive++;
			else if(a.get(i) == 1 && b.get(i) == 0)falseNegative++;
		}		
		Precision = (truePositive)/(truePositive+falsePositive);
		recall = (truePositive)/(truePositive+falseNegative);
		score = 2*((Precision*recall)/(Precision+recall));
		
		return score;
	}
	public static double computeTrueAverage(int[][] a)
	{
		double oneTime = 0;
		for(int i = 0; i < a.length; i++)for(int j = 0; j < a[j].length; j++)
			{
			oneTime += a[i][j];
			}
			return oneTime/(double)a.length;
	}
	public static double computeVariance(int[][] a)
	{
		double average = computeTrueAverage(a);
		double sum = 0;
		for(int i = 0; i < a.length; i++){
			int temp = 0;
			for(int j = 0; j < a[j].length; j++)if(a[i][j] == 1)temp++;
			double temp2 = temp-average;
			sum += ((temp2*temp2)/a.length);
		}
		return sum;
	}
}
