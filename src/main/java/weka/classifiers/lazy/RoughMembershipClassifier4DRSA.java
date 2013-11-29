package vpdrsa;

import weka.classifiers.AbstractClassifier;
import weka.core.Instance;
import weka.core.Instances;

import java.util.Enumeration;
import java.util.TreeMap;

public class RoughMembershipClassifier4DRSA
	extends AbstractClassifier{

	/** for serialization */
	private static final long serialVersionUID = 201311251403L;


	/** The training instances used for classification. */
	  protected Instances m_Train;
	  
	/** Use all Attributes */
	private boolean [] m_useAttributes;
	
	/** search tree for rough membership value */
	TreeMap<boolean[],RoughMembership> RMVTree;
	
	/** The number of decision class */
	int m_numClass;
	
	/** 
	 * 
	 * @param argv
	 */
	public static void main(String[] argv) {
		runClassifier(new RoughMembershipClassifier4DRSA(),argv);
	}

	@Override
	public void buildClassifier(Instances instances) throws Exception {
		// can classifier handle the data?
		getCapabilities().testWithFail(instances);
		
		//remove instances with missing class
		instances = new Instances(instances);
		instances.deleteWithMissingClass();
		m_Train = new Instances(instances, 0, instances.numInstances());
		
		
		m_useAttributes = new boolean[instances.numAttributes()];
		for(int i=0;i<m_useAttributes.length;i++){m_useAttributes[i]=true;}
		m_useAttributes[instances.classIndex()]=false;
		
		RMVTree = new TreeMap<boolean[],RoughMembership>(
				new SetComparator()
				);
		
		RoughMembership rmv = new  RoughMembership(instances,m_useAttributes);
		RMVTree.put(m_useAttributes, rmv);
		rmv.discretizationInit();
		m_numClass = rmv.getNumClass();
	}

	@Override
	public double[] distributionForInstance(Instance instance)throws Exception{
		double[] dist = new double[0];
		double MinInconsistentValue = Double.MAX_VALUE;
		Enumeration enu = m_Train.enumerateInstances();
		while(enu.hasMoreElements()){
			Instance knownInstance = (Instance)enu.nextElement();
			boolean [] useAttribute = attributeSet(instance,knownInstance);
			
			RoughMembership rmv;
			if(!RMVTree.containsKey(useAttribute)){
				rmv = new RoughMembership(m_Train,useAttribute);
				rmv.setNumClass(m_numClass);
				
				RMVTree.put(useAttribute,rmv);
			}else{
				rmv = RMVTree.get(useAttribute);
			}
			
			double tmp = rmv.getInconsistentValue();
			if(MinInconsistentValue > tmp){
				MinInconsistentValue = tmp;
				
				if(dist.length==0){
					dist = rmv.getDistribution(instance);
				}else{
					double[] tmpDist = new double[m_numClass];
					tmpDist= rmv.getDistribution(instance);
					double sum = 0;
					for(int i=0;i<m_numClass;i++){
						sum+=tmpDist[i];
					}
					if(sum > 0){
						dist = tmpDist;
					}
				}
			}
		}
		return dist;
	}
	
	private boolean[] attributeSet(Instance cmp1,Instance cmp2){
		boolean[] attributeSet;
		int length;
		if(cmp1.numAttributes() != cmp2.numAttributes()){
			return new boolean[0];
		}
		length = cmp1.numAttributes();
		
		attributeSet = new boolean[length];
		
		for(int i=0;i<length;i++){
			if(i != cmp1.classIndex()){
				attributeSet[i]=(cmp1.value(i)>=cmp2.value(i));
			}else{
				attributeSet[i]=false;
			}
		}
		
		return attributeSet;
	}
	
	@Override
	public String toString(){
	    if (m_Train == null) {
	        return "IBk: No model built yet.";
	    }
	    
	    if (m_Train.numInstances() == 0) {
	    	return "Warning: no training instances.";
	    }    

	    String result = "variable precision rough membership value's classifier\n";

	    return result;
	}
}
