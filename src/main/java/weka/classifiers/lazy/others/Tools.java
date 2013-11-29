package vpdrsa;

public class Tools {

	public static int countNumber(boolean [] data){
		int num=0;
		for(int i=0;i<data.length;i++){
			if(data[i]){
				num++;
			}
		}
		return num;
	}
	
	public static boolean[] intersection(boolean[] set1,boolean[] set2){
		boolean[] combinedSet;
		
		if(set1.length!=set2.length){
			return new boolean[0];
		}
		
		combinedSet = new boolean[set1.length];
		for(int i=0;i<set1.length;i++){
			if(set1[i] && set2[i]){
				combinedSet[i]=true;
			}else{
				combinedSet[i]=false;
			}
		}
		return combinedSet;
	}
	
	public static boolean[] union(boolean[] set1,boolean[] set2){
		boolean[] combinedSet;
		
		if(set1.length!=set2.length){
			return new boolean[0];
		}
		
		combinedSet = new boolean[set1.length];
		for(int i=0;i<set1.length;i++){
			if(set1[i] || set2[i]){
				combinedSet[i]=true;
			}else{
				combinedSet[i]=false;
			}
		}
		return combinedSet;
	}
	
	public static boolean[] negation(boolean[] set){
		boolean[] returnset;
		returnset = new boolean[set.length];
		for(int i=0;i<set.length;i++){
			returnset[i]=!set[i];
		}
		return returnset;
	}
	
	public static int classIndex(double classValue){
		int index=0;
		
		
		return index;
	}
}
