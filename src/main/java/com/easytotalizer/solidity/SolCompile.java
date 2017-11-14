package com.easytotalizer.solidity;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class SolCompile {
	
	private static String inputPath;
	private static String outputPath;

	public static void main(String[] args) {
		if(args.length >= 2) {
			inputPath = args[0];
			outputPath = args[1];
			
			File input = new File(inputPath);
			if(input.exists() && input.isDirectory()) {
				Arrays.stream(input.list((file, name) -> { return name.endsWith(".sol"); })).forEach(SolCompile::compileSolidity);
			}
		}
		else {
			throw new java.lang.IllegalArgumentException("Need 2 arguments (input path, output path)");
		}

	}
	
	private static void compileSolidity(String fileName) {
		ProcessBuilder compillerProcess = new ProcessBuilder(
					"solc",
					inputPath + "/" + fileName,
					"--overwrite",
					"--bin",
					"--abi",
					"--optimize",
					"-o",
					outputPath
				);
		Process solc;
		try {
			solc = compillerProcess.start();
			solc.waitFor();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

}
