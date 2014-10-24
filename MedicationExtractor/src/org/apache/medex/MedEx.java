/***************************************************************************************************************************
 * This software package is developed by Center for Computational Biomedicine at UTHealth which is directed by Dr. Hua Xu. *
 * The participants of development include Hua Xu, Min Jiang, Yonghui Wu, Anushi Shah							           *
 * Version:  1.0                                                                                                           *
 * Date: 01/30/2012                                                                                                        *
 * Copyright belongs to Dr. Hua Xu , all right reserved                                                                    *
 ***************************************************************************************************************************/


package org.apache.medex;

import java.io.File;

import main.Main;



public class MedEx{ 
	static String location = Main.getResource("/resources").getAbsolutePath();
			
	
	static String lexicon_file = location+ File.separator + "lexicon.cfg";
	static String rxnorm_file = location+File.separator + "brand_generic.cfg";
	static String code_file = location+File.separator+"code.cfg";
	static String generic_file = location+File.separator +"rxcui_generic.cfg";
	static String norm_file = location+File.separator + "norm.cfg";

	
	static String word_file = location+File.separator +"word.txt";
	static String abbr_file = location+File.separator +"abbr.txt";
	static String grammar_file = location+File.separator + "grammar.txt";
	static String if_detect_sents = "y";
	static String if_freq_norm = "n";
	static String if_drool_engine = "n";
	static String if_offset_showed = "y";
	static String input_dir = "";
	static String output_dir = "";
	
	
	
	
	
	
	
	public static MedTagger getMedTagger(String inputDir, String outputDir) {
		MedTagger med = new MedTagger(lexicon_file, rxnorm_file, code_file, generic_file, inputDir, outputDir, word_file, abbr_file, grammar_file, if_detect_sents, norm_file, if_freq_norm, if_drool_engine, if_offset_showed);
		return med;
	}



}
