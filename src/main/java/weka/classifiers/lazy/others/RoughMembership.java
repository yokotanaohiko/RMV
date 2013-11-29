package vpdrsa;

import java.util.Enumeration;
import weka.core.Instance;
import weka.core.Instances;

public class RoughMembership{

	
	/** Constant set for integer attributes. */
	public static final int INTEGER1 = 5;
	/** Constant set for real number attributes. */
	public static final int REAL = 6;
	/** The dominance relation table*/
	boolean [][] m_dominanceMatrix;
	/** The Instances in data */
	Instances m_data;
	/** The upper rough membership values */
	private double [] m_upperRMV;
	/** The lower rough membership values */
	private double [] m_lowerRMV;
	/** The degree of inconsistency of the attributes */
	private double m_value;
	/** Using attributes */
	boolean [] m_useAttributes;
	/** The number of the used attributes */
	int m_attributesNum;
	/** UpperDominanceCorn for unseen instance */
	boolean [] m_upperCorn;
	/** LowerDominanceCorn for unseen instance */
	boolean [] m_lowerCorn;
	/** Bounds for discretization */
	double [] m_discreteBounds;
	/** The number of decision class */
	int m_numClass;
	/** The decision attribute's type */
	int m_Type;
	/*@ invariant m_Type == INTEGER1 ||
	 			  m_Type == REAL;
	 
	 */
	
	

	RoughMembership(){
		
	}
	
	RoughMembership(Instances instances,boolean [] useAttributes){
		m_useAttributes = useAttributes;
		m_attributesNum = Tools.countNumber(useAttributes);
		m_data = instances;
		m_dominanceMatrix = new boolean[0][0];
		m_value = -1;
		m_upperRMV = new double[instances.numClasses()];
		m_lowerRMV = new double[instances.numClasses()];
		m_upperCorn = new boolean[0];
		m_lowerCorn = new boolean[0];
	}
	
	protected void makeDominanceMatrix(){
		m_dominanceMatrix = new boolean[m_data.numInstances()][m_data.numInstances()];
		for(int i=0;i<m_data.numInstances();i++){
			for(int j=i;j<m_data.numInstances();j++){
				int equalN=0,largeN=0,smallN=0;
				for(int k=0;k<m_useAttributes.length;k++){
					if(m_useAttributes[k]){
						if(m_data.instance(i).value(k)==m_data.instance(j).value(k)){
							equalN++;
						}else if(m_data.instance(i).value(k)>m_data.instance(j).value(k)){
							largeN++;
						}else{
							smallN++;
						}
					}
				}
				
				if(m_attributesNum == (equalN+largeN)){
					m_dominanceMatrix[j][i]=false;
					m_dominanceMatrix[i][j]=true;
				}
				if(m_attributesNum == (equalN+smallN)){
					m_dominanceMatrix[i][j]=false;
					m_dominanceMatrix[j][i]=true;
				}
			}
		}
	}
	
	
	public double[] getDistribution(Instance instance){
		double[] dist = new double[m_numClass];
		allRMV(instance);
		for(int i=0;i<m_numClass;i++){
			dist[i] = Math.min(m_upperRMV[i], m_lowerRMV[i]);
		}
		
		return dist;
	}
	/**
	 * This method require makeDominanceCorn and discretizationInit.
	 * @param instance
	 */
	private void allRMV(Instance instance){
		for(int i=0;i<m_numClass;i++){
			setUpperRoughMembershipValue(instance,i);
			setLowerRoughMembershipValue(instance,i);
		}
	}
	
	/**
	 * Sets the upper rough membership values for the decision class of classValue.
	 * @param instance
	 * @param classValue
	 */
	public void setUpperRoughMembershipValue(Instance instance,int classValue){
		double upSupport,lowSupport;
		boolean[] tmpClass;
		if(classValue == 0){
			m_upperRMV[classValue]=1;
		}
		if(m_upperCorn.length==0){
			setDominanceCorn(instance);
		}
		tmpClass=upperClass(classValue);
		upSupport = (double)Tools.countNumber(
				Tools.intersection(m_lowerCorn,tmpClass)
				);
		lowSupport = (double)Tools.countNumber(
				Tools.intersection(m_upperCorn,Tools.negation(tmpClass))
				);
		if(upSupport == 0 && lowSupport == 0){
			m_upperRMV[classValue]=-1;
		}else{
			m_upperRMV[classValue]=upSupport/(upSupport+lowSupport);
		}
	}

	/**
	 * Sets the lower rough membership values for the decision class of classValue.
	 * @param instance
	 * @param classValue
	 */
	public void setLowerRoughMembershipValue(Instance instance,int classValue){
		double upSupport,lowSupport;
		boolean[] tmpClass;
		if(classValue == m_numClass){
			m_lowerRMV[classValue]=1;
		}
		if(m_upperCorn.length==0){
			setDominanceCorn(instance);
		}
		tmpClass=lowerClass(classValue);
		lowSupport = (double)Tools.countNumber(
				Tools.intersection(m_upperCorn,tmpClass)
				);
		upSupport = (double)Tools.countNumber(
				Tools.intersection(m_lowerCorn,Tools.negation(tmpClass))
				);
		if(upSupport == 0 && lowSupport == 0){
			m_lowerRMV[classValue]=-1;
		}else{
			m_lowerRMV[classValue]=lowSupport/(upSupport+lowSupport);
		}
	}
	/**
	 * Sets the set of upper dominance corn and the set of lower dominance corn.
	 * @param instance
	 */
	private void setDominanceCorn(Instance instance){
		m_upperCorn = new boolean[m_data.numInstances()];
		m_lowerCorn = new boolean[m_data.numInstances()];
		int equalN=0,largeN=0,smallN=0;
		
		for(int i=0;i<m_data.numInstances();i++){
			for(int j=0;j<m_data.numAttributes();j++){
				if(m_useAttributes[j]){
					if(m_data.instance(i).value(j)==instance.value(j)){
						equalN++;
					}else if(m_data.instance(i).value(j)>instance.value(j)){
						largeN++;
					}else{
						smallN++;
					}
				}
			}
			
			if(m_attributesNum == equalN){
				m_upperCorn[i]=true;
				m_lowerCorn[i]=true;
			}else{
				if(m_attributesNum == (equalN+largeN)){
					m_upperCorn[i]=true;
					m_lowerCorn[i]=false;
				}else if(m_attributesNum == (equalN+smallN)){
					m_upperCorn[i]=false;
					m_lowerCorn[i]=true;
				}else{
					m_upperCorn[i]=false;
					m_lowerCorn[i]=false;
				}
			}
		}
	}
	/**
	 * Return the set of upper class which has instances whose 
	 * decision attribute value is upper than classValue.
	 * @param classValue
	 * @return the set of upper class
	 */
	boolean[] upperClass(int classValue){
		boolean[] upperclass = new boolean[m_data.numInstances()];
		if(isIntegerInClass()){
			for(int i=0;i<m_data.numInstances();i++){
				if((double)classValue <= m_data.instance(i).classValue()){
					upperclass[i]=true;
				}else{
					upperclass[i]=false;
				}
			}
		}
		if(isRealInClass()){
			for(int i=0;i<m_data.numInstances();i++){
				if(m_discreteBounds[classValue] <= m_data.instance(i).classValue()){
					upperclass[i]=true;
				}else{
					upperclass[i]=false;
				}
			}
		}
		return upperclass;
	}
	/**
	 * 
	 * 
	 * Return the set of lower class which has instances whose 
	 * decision attribute value is lower than classValue.
	 * @param classValue
	 * @return the set of lower class
	 */
	boolean[] lowerClass(int classValue){
		boolean[] lowerclass = new boolean[m_data.numInstances()];
		
		if(isIntegerInClass()){
			for(int i=0;i<m_data.numInstances();i++){
				if(m_data.instance(i).classValue() <= (double)classValue){
					lowerclass[i]=true;
				}else{
					lowerclass[i]=false;
				}
			}
		}
		if(isRealInClass()){
			for(int i=0;i<m_data.numInstances();i++){
				if(m_data.instance(i).classValue() < m_discreteBounds[classValue+1]){
					lowerclass[i]=true;
				}else{
					lowerclass[i]=false;
				}
			}
		}
		return lowerclass;
	}
	

	
	
	public void setInconsistentValue(){
		double numInconsistency,numPairs;
		if(m_dominanceMatrix.length==0){
			makeDominanceMatrix();
		}
		numPairs = 0;
		numInconsistency = 0;
		for(int i=0;i<m_data.numInstances();i++){
			numPairs += Tools.countNumber(m_dominanceMatrix[i]);
			numInconsistency += Tools.countNumber(
					Tools.intersection(
							m_dominanceMatrix[i], upperClass(getIndex(m_data.instance(i).classValue())
					)));
		}
		if(numPairs==0){
			m_value = Double.MAX_VALUE;
		}else{
			m_value = numInconsistency/numPairs;
		}
	}
	public double getInconsistentValue(){
		if(m_value == -1){
			setInconsistentValue();
		}
		return m_value;
	}
	public int getNumClass(){
		return m_numClass;
	}
	public void setNumClass(int numClass){
		m_numClass = numClass;
	}
	/**
	 * Sets the number of decision class.
	 * Sets the bounds for discreted class.
	 * Sets the type of decision attributes.
	 */
	void discretizationInit(){
		
		if(isIntegerInClass(m_data)){
			/*
			 * 決定属性値が整数ならここに来るが、
			 * ・　整数だが、離散化が必要な場合(ex 1~122)
			 * ・　整数だが、飛び飛びな値をとる場合(ex 1,12,15,18)
			 * ・　整数で、飛びがない値をとる場合(ex 1,2,3,4,5)
			 * のうち、三番目の処理のみを行う。
			 * 後の二つの場合のデータ処理は、また今度する。
			 */
			m_Type = INTEGER1;
			double upperbound,lowerbound;
			upperbound=m_data.classAttribute().getUpperNumericBound();
			lowerbound=m_data.classAttribute().getLowerNumericBound();
			
			m_numClass = 1+(int)(upperbound-lowerbound);
			m_discreteBounds = new double[0];
		}else{
			m_Type = REAL;
			//Use The Sturges's formula to determine the number of class
			m_numClass=(int)(1+(Math.log(m_data.numInstances())/Math.log(2)));
			m_discreteBounds = new double[m_numClass+1];
			double upperbound,lowerbound;
			upperbound=m_data.classAttribute().getUpperNumericBound();
			lowerbound=m_data.classAttribute().getLowerNumericBound();
			
			m_discreteBounds[0]=lowerbound;
			for(int i=1;i<m_numClass;i++){
				m_discreteBounds[i]=m_discreteBounds[i-1]+(upperbound-lowerbound)/m_numClass;
			}
			m_discreteBounds[m_numClass]=upperbound;
			
		}
	}
	
	/**
	 * This method require discretizationInit().
	 * Get the real number class value. 
	 * Return the index of decision class.
	 * @param classValue
	 * @return index of decision class.
	 */
	int getIndex(double classValue){
		if(isIntegerInClass()){
			return (int)classValue;
		}
		if(isRealInClass()){
			if(classValue < m_discreteBounds[0]){
				m_discreteBounds[0]=classValue;
				return 0;
			}else if(m_discreteBounds[m_numClass] < classValue){
				m_discreteBounds[m_numClass]=classValue;
				return m_numClass-1;
			}
			for(int i=0;i<m_numClass;i++){
				if(m_discreteBounds[i] <= classValue && classValue < m_discreteBounds[i+1]){
					return i;
				}
			}
		}
		
		//never come
		return -1;
	}
	
	/**
	 * Return whether decision attribute value is real number or not.
	 * @param instances
	 * @return
	 */
	boolean isIntegerInClass(Instances instances){
		boolean bool = true;
		Enumeration<Instance> enu = instances.enumerateInstances();
		while(enu.hasMoreElements()){
			Instance instance = (Instance)enu.nextElement();
			if(instance.classValue()!=(int)instance.classValue()){
				bool = false;
				break;
			}
		}
		return bool;
	}
	/**
	 * Return whether decision attribute value is real number or not.
	 * @return
	 */
	boolean isIntegerInClass(){
		if(m_Type == INTEGER1){
			return true;
		}else{
			return false;
		}
	}
	/**
	 * Return whether decision attribute value is integer or not.
	 * @return
	 */
	boolean isRealInClass(){
		if(m_Type == REAL){
			return true;
		}else{
			return false;
		}
	}
	
}
