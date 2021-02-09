/*
    Copyright (C) 2005 Dejvuth Suwimonteerabuth

    This file is part of jMoped.

    jMoped is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    jMoped is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with jMoped; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package jmoped;

import static java.io.File.separator;

import java.io.*;
import java.util.Hashtable;

import jmoped.policygen.PolicyInfo;
//import test.purse.*;////////////////suncong

public class jmoped {
	
	static void usage(String name) {
		
		System.err.println(
			"\n" + name + " -- java information flow security checker\n\n" +
			
			"Usage: " + name + " [-<options>] <classfile>\n\n" +
			
			"    Options:\n" +
			"    -m\t\t the method name\n" +
			"    -d\t\t the method signature\n" +
			"    -p\t\t the path of policy file (should be the same as the path of .class file)" +
			
			"    Options that overwrite jmoped.conf:\n" + 
			"    --heap=<n>\t set size of the heap\n" +
			"    --bits=<n>\t set number of bits of integer variables\n" +
			
			"Copyright (C) 2004-2006 \n" + 
			"Dejvuth Suwimonteerabuth " +
			"<suwimodh@fmi.uni-stuttgart.de>\n\n" +
			"See COPYING for legal information.\n\n" +
			
			"Version 2.2.3 (17.11.2006)\n"
			);
		
		System.exit(1);
	}
	
	static void optError() {
		System.err.println("Options -a and -r cannot mix together\n");
		System.exit(1);
	}
	
	public static void main(String[] args) throws Exception {

		/*not changed options*/
		String toolName = "jmoped";
		String inFileName = "";
		//String classFileName;
		String mopedForm = "error";//focused, suncong
		boolean chkAssert = false;
		boolean chkHeap = false;
		boolean chkNPE = false;
		boolean chkReach = true;//pattern we focus on
		boolean enableNPEChecks = false;
		boolean twoDims = false;
		/********************/
		int bits = 3; //default bits
		int heapSize = 0;

		PDSInfo.simplified=false;//simplify the symbols.suncong

		Hashtable<Integer, Integer> lineBitTable = new Hashtable<Integer, Integer>();
		
		String mtd_name="";
		String mtd_desc="";
		String policyFileName="";

		if (args.length == 0) {
			usage(toolName);
		}
		
		for (int i = 0; i < args.length; i++) {
			if (args[i].startsWith("-")) {
				for (int j = 1; j < args[i].length(); j++) {
					switch (args[i].charAt(j)) {
						case '-':
							if (args[i].substring(j+1, j+6).equals("bits=")) {
								try {
									bits = Integer.parseInt(args[i].substring(j+6));
									//System.out.println("BBB:"+bits);
								} catch (NumberFormatException nfe) {
									usage(toolName);
								}
								j = args[i].length();
							} else if (args[i].substring(j+1, j+6).equals("heap=")) {
								try {
									heapSize = Integer.parseInt(args[i].substring(j+6));
								} catch (NumberFormatException nfe) {
									usage(toolName);
								}
								j = args[i].length();
							} else {
								usage(toolName);
							}
							break;
						case 'm':
							mtd_name=args[++i];
							j = args[i].length();
							break;
						case 'd':
							mtd_desc=args[++i];
							j = args[i].length();
							break;
						case 'p':
							policyFileName=PDSDefault.JMOPED_HOME+args[++i];
							j = args[i].length();
							break;
						default: 
							usage(toolName);
							break;
					}
				}
			} else {
				if (inFileName.length() == 0) {
					inFileName = PDSDefault.JMOPED_HOME +args[i];
					if(policyFileName.equals("")){
						StringBuilder tmp=new StringBuilder( inFileName.substring(0, inFileName.lastIndexOf(".class")) );
						tmp.append(".xml");
						policyFileName=tmp.toString();
					}
				} else {
					System.out.println("b");
					usage(toolName);
				}
			}
		}
		
		
/*		System.out.println(inFileName);
		System.out.println(policyFileName);
		System.out.println(mtd_name);
		System.out.println(mtd_desc);
*/


		double timestart = System.currentTimeMillis();
		//Translation Info
		System.out.println("Translating ..");
		//***********************************************************************************

		PDS pds=new PDS(inFileName,mtd_name,mtd_desc,policyFileName);
		//************************************************************************************
		
		System.out.println("\nThe following methods are going to be translated:");
		System.out.println(pds.includedTableInfo());

		//Changes sizes of integer and heap
		if (bits > 0){
			pds.setBits(bits);
		}
		if (heapSize > 0)
			pds.setHeapSize(heapSize);
		
		//Translation
		pds.translate();

		String outFileName = inFileName.replaceAll(".class", "_"+mtd_name+"_bits"+Integer.toString(pds.getBits())+".rem");
		//Writes the resulting PDS to a file
		File outFile = new File(outFileName);
		Writer output = null;
		try {
			output = new BufferedWriter(new FileWriter(outFile));
			output.write(pds.toRemopla());
		}
		finally {
			if (output != null) output.close();
		}
		
		System.out.println("Translation completed.");
		
		double time_end = System.currentTimeMillis();
		System.out.println("Construction Time: "+Double.toString(time_end-timestart));
		
/*		//Runs moped
		if (chkAssert || chkHeap || chkNPE || chkReach)  {
			
			String[] mopedCmd = new String[3];
			mopedCmd[0] = pds.getMopedPath();
			mopedCmd[1] = outFileName;
			if (chkAssert) {
				mopedCmd[2] = pds.getLabelAssertError();
			} else if (chkHeap) {
				mopedCmd[2] = pds.getLabelHeapOverflow();
			} else if (chkNPE) {
				mopedCmd[2] = pds.getLabelNPE();
			} else if (chkReach) {
				mopedCmd[2] = mopedForm;
			}
			
			System.out.println("Running moped ..");
			System.out.println(mopedCmd[0] + " " + mopedCmd[1] + " " 
				+ mopedCmd[2]);
			Runtime runtime = Runtime.getRuntime();
			StringBuffer returnVal = new StringBuffer();
			String buffer;
			try {
				Process process = runtime.exec(mopedCmd);
				
				BufferedReader in = new BufferedReader(new InputStreamReader(
					process.getInputStream()));
				BufferedReader err = new BufferedReader(new InputStreamReader(
					process.getErrorStream()));
				
				while((buffer = in.readLine()) != null) {
					returnVal.append(buffer).append("\n");
				}
				while((buffer = err.readLine()) != null) {
					returnVal.append(buffer).append("\n");
				}
				
				System.out.println(returnVal);
			} catch (Exception e) {
				System.out.println(e);
			}
		}*/
	}
}
