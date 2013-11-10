package fruit.g8Fruit;

import java.util.*;

public class Player extends fruit.sim.Player
{
	int nplayer;
	int[] pref;
	int[] record;
	int magic;
	int position;
	int magic_table[]={-1,0,0,1,1,2,2,2,3,3};
    public void init(int nplayers, int[] pref) {
    	this.nplayer=nplayers;
    	this.pref=pref;
    	this.position=this.getIndex();// position start from 0
    	this.record = new int[2*nplayer];
    	if(nplayers-position<=9)
        	magic=magic_table[nplayers-position];
        else
        	magic=(int) Math.round(0.369*(nplayers-position) );
    	info.clear();
    	bowlIds.clear();
    }
    int max=0;
    int counter=0;
    int bowSize=0;
    public boolean pass(int[] bowl, int bowlId, int round,
                        boolean canPick,
                        boolean musTake) {
    	counter++;
    	if(counter==1){
    		for(int i=0;i<12;i++){
        		bowSize+=bowl[i];
        	}
    		//update(bowSize*6);
    	}
    	update(bowl,bowlId,round);
    	
    	if (musTake){
			return true;
		}
    	if(canPick==false)
    		return false;
    	
    	//no enough information
    	if (info.size()<=1) {
			return false;
		}
    
    	if (round==0) {
    		futureBowls=nplayer-position-counter;
            //return round0(bowl,bowlId,round,canPick,musTake);
        } else {
        	futureBowls=nplayer-counter+1;
            //return round1(bowl,bowlId,round,canPick,musTake);
        }
    	double b=score(bowl);
    	
    	double PrB=1, p=probLessThan(b);
    	for (int i = 0; i < futureBowls; i++) {			PrB*=p;		}
    	double PrA=1-PrB;
    	double ExA=exptGreaterThan(b);
    	double ExB=exptLessThan(b);
    	double Ex2=PrA*ExA+PrB*ExB;
    	
    	double tStar=search();
    	double fTStar=f(tStar);	
    	//double fb=f(b,futureBowls);
    	//double fb1=f(b+1, futureBowls);
    	if(fTStar>b) { //
    		return false;
    	}
    	else {
			return true;
		}
    }
    int futureBowls=0;
    double mu,sigma;
    ArrayList<Integer> scores=new ArrayList<Integer>();
    private double search() {
		double l=mu-sigma*2;
		double r=mu+sigma*2;
		while (l+0.1<r){
			double t1=(r-l)/3+l;
			double t2=2*(r-l)/3+l;
			if(f(t1) < f(t2))
				l=t1;
			else r=t2;
		}
		return (l+r)/2;
	}
    private double f(double b) {
    	double PrB=1, p=probLessThan(b);
    	for (int i = 0; i < futureBowls; i++) {
			PrB*=p;
		}
    	double PrA=1-PrB;
    	double ExA=exptGreaterThan(b);
    	double ExB=exptLessThan(b);
    	double Ex2=PrA*ExA+PrB*ExB;
    	return Ex2;
	}
	private double exptLessThan(double b) {
    	double sum=0,interval=(b-12)/1000,val=12;
		for (int i = 0; i <1000; i++) {
			sum+=phi(val, mu, sigma)*val*interval;
			val+=interval;
		}
		return sum/probLessThan(b);
	}
	private double exptGreaterThan(double b) {
		double sum=0,interval=(bowSize*12-b)/1000,val=b;
		for (int i = 0; i <1000; i++) {
			sum+=phi(val, mu, sigma)*val*interval;
			val+=interval;
		}
		return sum/(1-probLessThan(b));
	}
	private double probLessThan(double b) {
		if (sigma<1e-8) {
			if (b>mu) {
				return 1;
			}else {
				return 0;
			}
		}
		return Phi((b - mu) / sigma);
	}
    
    public static double Phi(double z) {
        if (z < -8.0) return 0.0;
        if (z >  8.0) return 1.0;
        double sum = 0.0, term = z;
        int i=3;
        for (; sum + term != sum; i += 2) {
            sum  = sum + term;
            term = term * z * z / i;
        }
        return 0.5 + sum * phi(z);
    }
    
    // return phi(x) = standard Gaussian pdf
    public static double phi(double x) {
        return Math.exp(-x*x / 2) / Math.sqrt(2 * Math.PI);
    }
    public static double phi(double x, double mu, double sigma) {
        return phi((x - mu) / sigma) / sigma;
    }
    
	//static 
	ArrayList<int[]> info=new ArrayList<int[]>();
	//static 
	HashSet<Integer> bowlIds=new HashSet<>();
	private void update(int[] x, int bowlId, int round) {
		if (bowlIds.contains(bowlId+round*nplayer)==false) {
			info.add(x.clone());
			bowlIds.add(bowlId+round*nplayer);
		}
		mu=0;
		for(int[] y:info){
			mu+=score(y);
		}
		mu=mu/info.size();
		sigma=0;
		scores.clear();
		for(int[] y:info){
			int s=score(y);
			sigma+=(s-mu)*(s-mu);
			scores.add(s);
		}
		sigma=0;
		for(int i=0;i<12;i++){
			double mu=0;
			for(int[] y:info){
				mu+=y[i]*pref[i];
			}
			mu/=info.size();
			for(int[] y:info){
				sigma+=(mu-y[i]*pref[i])*(mu-y[i]*pref[i]);
			}
		}
		//sigma+=2*13*25*bowSize/12.0;
		sigma/=info.size();
		sigma=Math.sqrt(sigma);
	}

	private int score(int[] bowl){
    	int sum=0;
    	for(int i=0;i<12;i++){
    		sum+=pref[i]*bowl[i];
    	}
    	return sum;
    }

	private boolean pickduringobservation(int[] bowl, int[] record) {
		System.out.println("we are in the pickduring observation");
		System.out.printf("\n the score for this bow is %d\n", score(bowl));
		if (score(bowl)>average(record)*1.5 && score(bowl)>=max)
			return true;
		else
			return false;
	}
	
	private double average(int[] record){
		int sum=0;
		int i;
		double avg;
		int ct = 0;
		for (i=0;i<record.length;i++){
			sum = sum+record[i];
			System.out.printf("\n the %d th record is %d\n",i, record[i]);
			if (record[i]!=0)
				ct=i;
		}
		avg = sum/(ct+1);
		System.out.printf("\n the avg score is %f\n", avg);
		return avg;
	}
    private Random random = new Random();
}
