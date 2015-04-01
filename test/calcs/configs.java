package calcs;

import java.math.BigDecimal;

public class configs {

	public static void main(String[] args){
		
		BigDecimal big = new BigDecimal(0);
		
		for(int n = 0; n <= 26; n++){
			for(int i = 1; i <= n; i++){
				System.out.println("ADD:"+ new BigDecimal((48-i+1)*800));
				if (big.toString().equals("0"))
					big = new BigDecimal((48-i+1)*800);
				else
					big = big.multiply(new BigDecimal((48-i+1)*800));
				System.out.println("RES:"+big.toEngineeringString());
			}
		}
		
		//System.out.println(big.to);
		
	}
	
}
