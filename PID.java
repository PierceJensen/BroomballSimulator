
public class PID {
	double P;
	double I;
	double D;
	
	private double totalError;
	private double lastError;
	
 PID(double P, double I, double D){
	 this.P = P;
	 this.I = I;
	 this.D = D;
 }
 double runPID(double currentValue, double setPoint){
	 double value=0;
	 double pValue=0;
	 double dValue=0;
	 
	 double currentError = setPoint-currentValue;
	 pValue = currentError;
	 totalError+=currentError;
	 dValue = currentError-lastError;
	 lastError = currentError;
	 
	 return this.P*pValue+this.I*totalError+this.D*dValue;
 }
}
