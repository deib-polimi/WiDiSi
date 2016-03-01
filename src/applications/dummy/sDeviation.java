/*
 * Copyright (c) 2014-2015 SCUBE Joint Open Lab
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 2 as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 * 
 * Author: Naser Derakhshan
 * Politecnico di Milano
 *
 */
package applications.dummy;

import peersim.config.Configuration;
import peersim.core.Control;
import peersim.core.Network;
import visualization.Visualizer;

// TODO: Auto-generated Javadoc
/**
 * The Class sDeviation.
 */
public class sDeviation implements Control{

	/** The cycle. */
	private 			long 					cycle 			= 0;
	
	/** The mean value. */
	public static		double					meanValue		= 0;
	
	/** The s deviation. */
	private 			double					sDeviation		= 0;
	
	/** The Constant PAR_APPLICATION. */
	private static final String 					PAR_APPLICATION = "application";
	
	/** The application id. */
	private 			int 					applicationId;
	
	/** The Max_ con. */
	//private 			PrintWriter 			writer 			= null;
	private 			int						Max_Con			=0;
	
	/**
	 * Instantiates a new s deviation.
	 *
	 * @param prefix the prefix
	 */
	public sDeviation(String prefix){
		applicationId 	= Configuration.getPid(prefix + "." + PAR_APPLICATION);
//		try{
//			writer = new PrintWriter(new BufferedWriter(new FileWriter("log/deviation.txt", true)));
//		} catch (IOException e) {
//			System.out.println("File deviation.txt not found");
//		}
	}


	/* (non-Javadoc)
	 * @see peersim.core.Control#execute()
	 */
	@Override
	public boolean execute() {




		if (cycle == 3){
			// calculating the mean of all network devices' values

			meanValue = 0;
			sDeviation = 0;
			for (int i = 0; i<Network.size(); i++){
				newApplication newApp = (newApplication) Network.get(i).getProtocol(applicationId);
				meanValue = newApp.value+meanValue;
			}
			meanValue = meanValue/Network.size();
		//	Visualizer.textField13.setText(String.valueOf(meanValue));

		}
		if(cycle>3){
			for (int i = 0; i<Network.size(); i++){
				newApplication newApp = (newApplication) Network.get(i).getProtocol(applicationId);
				sDeviation = Math.pow((newApp.value-meanValue), 2) + sDeviation;
			}
			sDeviation = Math.sqrt(sDeviation/Network.size());
			if (sDeviation<0.15){
				return true;
			}
			//Visualizer.textField12.setText(String.valueOf(sDeviation));
//			if(cycle%100==0){
//				writer.println(CommonState.getTime() + "        " + sDeviation);
//			}
			//Max_Con = (Integer.parseInt(Visualizer.textField7.getText())> Max_Con)? Integer.parseInt(Visualizer.textField7.getText()):Max_Con;
		}
		if(cycle==3000) {
			System.out.println("Max_Con: " + Max_Con);
			return true;	
		}
		
		cycle++;
		return false;
	}

}
