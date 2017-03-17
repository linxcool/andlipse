package com.linxcool.andlipse.action.smali;

import java.io.File;
import java.io.IOException;

import org.jf.baksmali.baksmali;
import org.jf.baksmali.baksmaliOptions;
import org.jf.dexlib2.DexFileFactory;
import org.jf.dexlib2.analysis.InlineMethodResolver;
import org.jf.dexlib2.dexbacked.DexBackedDexFile;
import org.jf.dexlib2.dexbacked.DexBackedOdexFile;

import com.linxcool.andlipse.core.LResource;

import brut.androlib.ApkDecoder;

public class DxHelper {

	public static void decode(String inputApk) {
		decode(inputApk, inputApk.substring(0, inputApk.length() - LResource.FILE_APK.length()));
	}

	/**
	 * 解包
	 * @param inputApk
	 * @param toFolder
	 */
	public static void decode(String inputApk, String toFolder) {
		try {
			ApkDecoder decoder = new ApkDecoder();
			decoder.setApkFile(new File(inputApk));
			decoder.setOutDir(new File(toFolder));
			decoder.decode();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String dex(String inPath) {
		return dex(inPath, inPath.replace(LResource.FILE_JAR, LResource.FILE_DEX));
	}

	public static String dex(String inPath, String outPath) {
		File outFile = new File(outPath);
		if (outFile.exists() && outFile.isFile()) {
			outFile.delete();
		}
		com.android.dx.command.Main.main(new String[] { "--dex", "--no-strict", "--output=" + outPath, inPath });
		return outPath;
	}

	public static String smali(String inPath) {
		File inFile = new File(inPath);
		String outDir = inFile.getParent();
		return smali(inPath, outDir);
	}

	public static String smali(String inPath, String outDir) {
		try {
			outDir = outDir + File.separator + "smali/";
			LResource.deleteFile(outDir);

			baksmaliOptions options = new baksmaliOptions();

			options.deodex = false;
			options.outputDirectory = outDir;
			options.noParameterRegisters = false;
			options.useLocalsDirective = true;
			options.useSequentialLabels = true;
			options.outputDebugInfo = false;
			options.addCodeOffsets = false;
			options.noAccessorComments = false;
			options.registerInfo = 0;
			options.ignoreErrors = false;
			options.inlineResolver = null;
			options.checkPackagePrivateAccess = false;
			options.jobs = Runtime.getRuntime().availableProcessors();
			if (options.jobs > 6) {
				options.jobs = 6;
			}

			DexBackedDexFile dexFile = DexFileFactory.loadDexFile(
					new File(inPath),
					15, 
					false);

			if ((dexFile instanceof DexBackedOdexFile)) {
				options.inlineResolver = InlineMethodResolver.createInlineMethodResolver(
						((DexBackedOdexFile)dexFile).getOdexVersion());
			}
			baksmali.disassembleDexFile(dexFile, options);

			return outDir;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
