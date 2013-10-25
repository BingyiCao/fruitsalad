package fruit.g8;

import java.util.*;

public class Player extends fruit.sim.Player
{
	int nplayer;
	int[] pref;
	int magic;
	int position;
	int magic_table[]={-1,0,0,1,1,2,2,2,3,3};
    public void init(int nplayers, int[] pref) {
    	this.nplayer=nplayers;
    	this.pref=pref;
    	this.position=this.getIndex();// position start from 0
        if(nplayers-position<=9)
        	magic=magic_table[nplayers];
        else
        	magic=(int) Math.round(0.369*(nplayers-position) );
    }
    int max=0;
    int counter=0;
    public boolean pass(int[] bowl, int bowlId, int round,
                        boolean canPick,
                        boolean musTake) {
    	counter++;
    	if (musTake){
			return true;
		}
    	if (round==0) {
            return round0(bowl,bowlId,round,canPick,musTake);
        } else {
            return round1(bowl,bowlId,round,canPick,musTake);
        }
    	
    }
    
	private boolean round0(int[] bowl, int bowlId, int round,
            boolean canPick,
            boolean musTake) {
    	System.out.printf("magic is %d", magic);
        if(counter<=magic){
        	//System.out.println("we won't pick the bowl");
        	//System.out.printf("the score for %d bowl is %d\n", counter, score(bowl));
        	if (max<score(bowl)){
        		max=score(bowl);
        	}
        }else{
        	System.out.println("we are in the picking round");
        	if(score(bowl)>=max){
        		return true;
        	}else{
        		return false;
        	}
        }
        return false;
	}
	
	private boolean round1(int[] bowl, int bowlId, int round, boolean canPick,
				boolean musTake) {
			return false;
		}

	private int score(int[] bowl){
    	int sum=0;
    	for(int i=0;i<12;i++){
    		sum+=pref[i]*bowl[i];
    	}
    	return sum;
    }

    private Random random = new Random();
}
