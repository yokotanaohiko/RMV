package vpdrsa;

import java.util.Comparator;
public class SetComparator implements Comparator<boolean[]> {

	@Override
	public int compare(boolean[] cmp1,boolean[] cmp2) {
		int cmp,length;
		int [] tmpbit1,tmpbit2;
		if(cmp1.length != cmp2.length){return 0;}
		length = cmp1.length/30;
		tmpbit1 = new int[length];
		tmpbit2 = new int[length];
		for(int i=0;i<length;i++){
			tmpbit1[i]=0;
			tmpbit2[i]=0;
			for(int j=0;j<30;j++){
				if(cmp1[j]){
					tmpbit1[i] += Math.pow(2,j);
				}
				if(cmp2[j]){
					tmpbit2[i] += Math.pow(2,j);
				}
			}
		}
		cmp=0;
		for(int i=0;i<length;i++){
			if(tmpbit1[i]>tmpbit2[i]){
				cmp += Math.pow(2,i);
			}else if(tmpbit1[i]<tmpbit2[i]){
				cmp -= Math.pow(2,i);
			}
		}
		return cmp;
	}
	
	@Override
	public boolean equals(Object obj){
		
		return this==obj;
	}

}
