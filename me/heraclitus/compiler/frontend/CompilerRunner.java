package me.heraclitus.compiler.frontend;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.heraclitus.compiler.backend.CommandSpec;
import me.heraclitus.compiler.backend.Compiler;
import me.heraclitus.compiler.backend.Prepocessor;
import me.heraclitus.compiler.backend.Token;
import me.heraclitus.compiler.errors.AddressExpected;
import me.heraclitus.compiler.errors.AddressIncorrect;
import me.heraclitus.compiler.errors.CommandExpected;
import me.heraclitus.compiler.errors.CommandNotFound;
import me.heraclitus.compiler.errors.LabelUndefined;

public class CompilerRunner {
	public static String run(File inputFile, File outputFile) {
		String inputString;
		try {
			inputString = new String(Files.readAllBytes(inputFile.toPath()));
		} catch (IOException e) {
			String errorLog = "Unable to read file (" + inputFile.getName()
					+ "): " + e.getMessage();
			System.err.println(errorLog);
			e.printStackTrace();
			return errorLog;
		}

		Prepocessor pp = new Prepocessor();
		Compiler co = new Compiler();
		Map<String, CommandSpec> dict = new HashMap<String, CommandSpec>();
		dict.put("add", new CommandSpec("0000", false));
		dict.put("sub", new CommandSpec("0001", false));
		dict.put("ld", new CommandSpec("0010", true));
		dict.put("xchg", new CommandSpec("0011", false));
		dict.put("st", new CommandSpec("0100", true));
		co.setCommandSet(dict);
		List<Token> tokens = pp.preprocess(inputString);
		String outputString;
		try {
			outputString = co.compile(tokens);
		} catch (CommandNotFound | AddressExpected | CommandExpected
				| AddressIncorrect | LabelUndefined e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			return e.getMessage();
		}

		if (outputFile != null) {
			PrintWriter outputWriter;
			try {
				outputWriter = new PrintWriter(outputFile);
			} catch (FileNotFoundException e) {
				String errorLog = "Unable to write file ("
						+ outputFile.getName() + "): " + e.getMessage();
				System.err.println(errorLog);
				e.printStackTrace();
				return errorLog;
			}
			outputWriter.write(outputString);
			outputWriter.close();
		}
		System.out.println(outputString);

		String successLog = "Successfully compiled " + co.getBytes() + " bytes";
		System.err.println(successLog);
		return successLog;
	}
}
