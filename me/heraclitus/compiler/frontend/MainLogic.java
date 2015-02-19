package me.heraclitus.compiler.frontend;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.prefs.Preferences;

import javax.swing.JFileChooser;
import javax.swing.UnsupportedLookAndFeelException;

public class MainLogic extends MainWindow {
	private String inputLastLocation = "inputLastLocation",
			outputLastLocation = "outputLastLocation";
	private Preferences prefs;

	public static void main(String[] args) {
		if (args.length != 2) {
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					try {
						MainLogic window = new MainLogic();
						window.frame.setVisible(true);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		} else {
			File input = new File(args[0]);
			File output = new File(args[1]);
			CompilerRunner.run(input, output);
		}
	}

	public MainLogic() throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, UnsupportedLookAndFeelException {
		super();
		initializeLogic();
	}

	private void initializeLogic() {
		prefs = Preferences.userRoot().node(getClass().getName());

		inputCh = new JFileChooser(prefs.get(inputLastLocation,
				new File(".").getAbsolutePath()));

		outputCh = new JFileChooser(prefs.get(outputLastLocation,
				new File(".").getAbsolutePath()));

		inputBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int returnVal = inputCh.showOpenDialog(frame);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					inputFile = inputCh.getSelectedFile();
					prefs.put(inputLastLocation, inputFile.getParent());
					inputLbl.setText("Input: " + inputFile.getName());
					frame.pack();
				}
			}
		});

		outputBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int returnVal = outputCh.showOpenDialog(frame);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					outputFile = outputCh.getSelectedFile();
					prefs.put(outputLastLocation, outputFile.getParent());
					outputLbl.setText("Output: " + outputFile.getName());
					frame.pack();
				}
			}
		});

		compileBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (inputFile == null || outputFile == null) {
					String errorLog = "Please select an input file and an output file";
					outputTA.setText(errorLog);
					return;
				}
				String output = CompilerRunner.run(inputFile, outputFile);
				outputTA.setText(output);
			}
		});
	}
}
