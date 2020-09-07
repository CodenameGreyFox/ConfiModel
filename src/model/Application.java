package model;

import java.awt.EventQueue;

import javax.swing.JFrame;


import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.awt.Toolkit;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import java.awt.Color;

import javax.swing.SwingConstants;

import java.awt.Font;
import java.awt.Cursor;


/**
 * GUI of the model.
 *
 * @author  Tomé Neves
 */

public class Application implements PropertyChangeListener {

	private JFrame frmModel;
	private JTextField txtSpeciesFolder;
	private JTextField txtVariablesFolderPath;
	private JTextField txtEquationtxtPath;
	private JTextField txtOutputFolder;
	private JTextField txtMgr;
	private JTextField txtInitialpopulation;
	private JTextField txtNstages;
	private JTextField txtMatstage;
	private JTextField txtNsteps;
	private JTextField txtStageduration;
	private JTextField txtLitterSize;
	private JTextField txtDispersalRange;
	private JTextField txtCycleLength;
	private JTextField txtMaxPop;

	private JButton btnInitialize;
	private JButton btnRun;
	private JButton btnLoad;
	private JButton btnSaveCompiled;
	private JButton btnPaintSpecies;
	private JButton btnSave;
	private JButton btnSwimfly;
	private JButton btnListparameters;

	private JSpinner spinNCores;
	private JSpinner spinScaler;	

	private JLabel lblInitPop;
	private JLabel lblNStages;
	private JLabel lblMigR;
	private JLabel lblMatStage;	
	private JLabel lblNsteps;
	private JLabel lblCurrentmonth;
	private JLabel lblCurrentyear;
	private JLabel lblStduration;
	private JLabel lblCpuCores;
	private JLabel lblScale;
	private JLabel lblLitterSize;
	private JLabel lblLitsize;
	private JLabel lblDispersalrange;
	private JLabel lblDisran;
	private JLabel lblCycleLength;
	private JLabel lblMaxpopvalue;


	private JLabel[] lblMigRSeparate;	
	private JLabel[] lblNames;
	private JLabel[] lblLitsizeSeparate;
	private JLabel[] lblDisranSeparate;
	private JLabel[] lblStdurationSeparate;
	private JLabel[] lblNStagesSeparate;
	private JLabel[] lblMatStageSeparate;
	private JButton[] btnSwimflySeparate;

	private JTextField[] txtMigrSeparate;
	private JTextField[] txtNstagesSeparate;
	private JTextField[] txtMatstageSeparate;
	private JTextField[] txtStagedurationSeparate;
	private JTextField[] txtLitterSizeSeparate;
	private JTextField[] txtDispersalRangeSeparate;

	private JComboBox<String> comboBox;

	private static JProgressBar progressBar;

	private Model model;

	private boolean stop;
	private JButton btnSeparatenstages;
	private JButton btnSeparatematuritystage;
	private JButton btnSeparatestageduration;
	private JButton btnSeparatelittersize;
	private JButton btnSeparatedispersalrange;
	private JButton btnSeparateswimfly;
	private JButton btnSeparateMigration;

	private JFrame migrationWindow;
	private JFrame matureStageWindow;
	private JFrame litterSizeWindow;
	private JFrame stageDurationWindow;
	private JFrame swimFlyWindow;
	private JFrame nStageWindow;
	private JFrame dispersalRangeWindow;	
	
	private String location;



	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Application window = new Application();
					window.frmModel.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Application() {
		startUp();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void startUp() {
		location = "";
		try {
			File fileToLocate = new File (Application.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
			location = fileToLocate.getParent();
		} catch (URISyntaxException e2) {
			e2.printStackTrace();
		}

		stop = false;

		frmModel = new JFrame();
		frmModel.getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		frmModel.setIconImage(Toolkit.getDefaultToolkit().getImage(Application.class.getResource("/icon.png")));
		frmModel.setTitle("Configure Model");
		frmModel.setBounds(100, 100, 692, 471);
		frmModel.setResizable(false);
		frmModel.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmModel.getContentPane().setLayout(null);

		btnRun = new JButton("Run");
		btnRun.setEnabled(false);
		btnRun.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {								
				if (btnRun.getText().equals("Run")) {
					btnInitialize.setEnabled(false);
					btnLoad.setEnabled(false);
					btnRun.setText("Stop");
					runModel(); 
				} else {
					stop = true;
					btnRun.setText("Stopping...");
					btnRun.setEnabled(false);
				}
			}
		});

		btnRun.setBounds(499, 383, 166, 40);
		frmModel.getContentPane().add(btnRun);

		JLabel lblMigrationRate = new JLabel("Migration Rate:");
		lblMigrationRate.setBounds(10, 36, 97, 14);
		frmModel.getContentPane().add(lblMigrationRate);


		lblInitPop = new JLabel("100");
		lblInitPop.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblInitPop.setBounds(72, 287, 46, 14);
		frmModel.getContentPane().add(lblInitPop);

		lblNStages = new JLabel("3");
		lblNStages.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblNStages.setBounds(72, 92, 35, 14);
		frmModel.getContentPane().add(lblNStages);

		lblMatStage = new JLabel("1");
		lblMatStage.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblMatStage.setBounds(72, 131, 35, 14);
		frmModel.getContentPane().add(lblMatStage);

		lblMigR = new JLabel("0.2");
		lblMigR.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblMigR.setForeground(new Color(0, 0, 0));
		lblMigR.setBounds(72, 53, 35, 14);
		frmModel.getContentPane().add(lblMigR);

		JLabel lblNumberOfSteps = new JLabel("Iterations to run:");
		lblNumberOfSteps.setBounds(402, 383, 108, 14);
		frmModel.getContentPane().add(lblNumberOfSteps);

		lblNsteps = new JLabel("1");
		lblNsteps.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblNsteps.setBounds(454, 404, 46, 14);
		frmModel.getContentPane().add(lblNsteps);

		txtSpeciesFolder = new JTextField();
		//txtSpeciesFolder.setText("M:\\Doutoramento\\Modelo Java\\Species");
		txtSpeciesFolder.setText(location + System.getProperty("file.separator")+ "Species");
		txtSpeciesFolder.setBounds(335, 36, 330, 20);
		frmModel.getContentPane().add(txtSpeciesFolder);
		txtSpeciesFolder.setColumns(10);

		JButton btnBrowseSpecies = new JButton("Browse...");
		btnBrowseSpecies.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fileChooser.setCurrentDirectory(new File(location + System.getProperty("file.separator")));
				fileChooser.showOpenDialog(new JFrame());
				if (fileChooser.getSelectedFile() != null) {
					txtSpeciesFolder.setText(fileChooser.getSelectedFile().getAbsolutePath());
				}
			}
		});
		btnBrowseSpecies.setBounds(577, 11, 88, 23);
		frmModel.getContentPane().add(btnBrowseSpecies);

		JButton btnBrowseVariables = new JButton("Browse...");
		btnBrowseVariables.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setCurrentDirectory(new File(location + System.getProperty("file.separator")));
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fileChooser.showOpenDialog(new JFrame());
				if (fileChooser.getSelectedFile() != null) {
					txtVariablesFolderPath.setText(fileChooser.getSelectedFile().getAbsolutePath());
				}
			}
		});
		btnBrowseVariables.setBounds(577, 75, 88, 23);
		frmModel.getContentPane().add(btnBrowseVariables);

		JLabel lblSpeciesFolder = new JLabel("Species Folder:");
		lblSpeciesFolder.setBounds(335, 16, 97, 14);
		frmModel.getContentPane().add(lblSpeciesFolder);

		JLabel lblVariablesFolder = new JLabel("Variables Folder:");
		lblVariablesFolder.setBounds(335, 79, 116, 14);
		frmModel.getContentPane().add(lblVariablesFolder);

		txtVariablesFolderPath = new JTextField();
		//txtVariablesFolderPath.setText("M:\\Doutoramento\\Modelo Java\\Variables");
		txtVariablesFolderPath.setText(location + System.getProperty("file.separator") + "Variables");
		txtVariablesFolderPath.setBounds(335, 100, 330, 20);
		frmModel.getContentPane().add(txtVariablesFolderPath);
		txtVariablesFolderPath.setColumns(10);

		JLabel lblEquationTxt = new JLabel("Equations txt:");
		lblEquationTxt.setBounds(335, 143, 116, 14);
		frmModel.getContentPane().add(lblEquationTxt);		

		txtEquationtxtPath = new JTextField();
		//txtEquationtxtPath.setText("M:\\Doutoramento\\Modelo Java\\Equations.txt");
		txtEquationtxtPath.setText(location + System.getProperty("file.separator") +"Equations.txt");
		txtEquationtxtPath.setBounds(335, 168, 330, 20);
		frmModel.getContentPane().add(txtEquationtxtPath);
		txtEquationtxtPath.setColumns(10);

		JButton btnBrowseEquation = new JButton("Browse...");
		btnBrowseEquation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fileChooser.setCurrentDirectory(new File(location + System.getProperty("file.separator")));
				fileChooser.showOpenDialog(new JFrame());
				if (fileChooser.getSelectedFile() != null) {
					txtEquationtxtPath.setText(fileChooser.getSelectedFile().getAbsolutePath());
				}
			}
		});
		btnBrowseEquation.setBounds(577, 139, 89, 23);
		frmModel.getContentPane().add(btnBrowseEquation);

		JLabel lblOutputFolder = new JLabel("Output Folder:");
		lblOutputFolder.setBounds(334, 327, 116, 14);
		frmModel.getContentPane().add(lblOutputFolder);

		txtOutputFolder = new JTextField();
		//txtOutputFolder.setText("M:\\Doutoramento\\Modelo Java\\Output");
		txtOutputFolder.setText(location + System.getProperty("file.separator") + "Output");
		txtOutputFolder.setBounds(334, 352, 330, 20);
		frmModel.getContentPane().add(txtOutputFolder);
		txtOutputFolder.setColumns(10);

		JButton btnBrowseOutput = new JButton("Browse...");
		btnBrowseOutput.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fileChooser.setCurrentDirectory(new File(location + System.getProperty("file.separator")));
				fileChooser.showOpenDialog(new JFrame());
				if (fileChooser.getSelectedFile() != null) {
					txtOutputFolder.setText(fileChooser.getSelectedFile().getAbsolutePath());
				}
			}
		});
		btnBrowseOutput.setBounds(576, 323, 89, 23);
		frmModel.getContentPane().add(btnBrowseOutput);

		txtMgr = new JTextField();
		txtMgr.setHorizontalAlignment(SwingConstants.RIGHT);
		txtMgr.setText("");
		txtMgr.setBounds(10, 51, 46, 20);
		frmModel.getContentPane().add(txtMgr);
		txtMgr.setColumns(10);
		txtMgr.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if (Double.parseDouble(txtMgr.getText().replace(',','.')) > 1) {
						lblMigR.setText("1");
						txtMgr.setText("");
					} else if (Double.parseDouble(txtMgr.getText().replace(',','.')) < 0) {
						lblMigR.setText("0");
						txtMgr.setText("");
					} else {
						lblMigR.setText(txtMgr.getText().replace(',', '.'));
						txtMgr.setText("");
					}

					//Sets all the species equally (Might throw NullPointerException if model still not initialized)
					for (int i = 0; i < lblMigRSeparate.length; i++) {
						lblMigRSeparate[i].setText(lblMigR.getText());
						model.setMigrationRate(i, Double.parseDouble(lblMigR.getText()));
					}
				} catch (NumberFormatException exception) {
					txtMgr.setText("Invalid!");
				} catch (NullPointerException exception) {
					//ignore
				}
			}
		});

		JLabel lblInitialPopulation = new JLabel("Initial Population:");
		lblInitialPopulation.setBounds(10, 270, 97, 14);
		frmModel.getContentPane().add(lblInitialPopulation);

		txtInitialpopulation = new JTextField();
		txtInitialpopulation.setHorizontalAlignment(SwingConstants.RIGHT);
		txtInitialpopulation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if (Integer.parseInt(txtInitialpopulation.getText()) < 1) {
						lblInitPop.setText("1");
						txtInitialpopulation.setText("");
					} else {
						lblInitPop.setText(txtInitialpopulation.getText());
						txtInitialpopulation.setText("");
					}
				} catch (NumberFormatException exception) {
					txtInitialpopulation.setText("Invalid!");
				}
			}
		});
		txtInitialpopulation.setText("");
		txtInitialpopulation.setBounds(10, 285, 46, 20);
		frmModel.getContentPane().add(txtInitialpopulation);

		JLabel lblStagesInHierarchy = new JLabel("Maximum Age:");
		lblStagesInHierarchy.setBounds(10, 75, 145, 14);
		frmModel.getContentPane().add(lblStagesInHierarchy);

		txtNstages = new JTextField();
		txtNstages.setHorizontalAlignment(SwingConstants.RIGHT);
		txtNstages.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if (Integer.parseInt(txtNstages.getText()) < 1 ) {
						txtNstages.setText("1");
					} 
					lblNStages.setText(txtNstages.getText());
					txtNstages.setText("");					

					if (lblMatStage.getText().equals("Sep.")) {
						for (int i = 0; i < lblMatStageSeparate.length; i++) {
							if (Integer.parseInt(lblMatStageSeparate[i].getText()) > Integer.parseInt(lblNStages.getText())) {
								lblMatStageSeparate[i].setText(lblNStages.getText());
							}
						}
					} else {
						if (Integer.parseInt(lblMatStage.getText()) > Integer.parseInt(lblNStages.getText())) {
							lblMatStage.setText(lblNStages.getText());

						}
					}					

					//Sets all the species equally (Might throw NullPointerException if model still not initialized)
					for (int i = 0; i < lblNStagesSeparate.length; i++) {
						lblNStagesSeparate[i].setText(lblNStages.getText());
						model.setNumberOfStages(i, Integer.parseInt(lblNStages.getText()), Integer.parseInt(lblInitPop.getText()));
					}
				} catch (NumberFormatException exception) {
					txtNstages.setText("Invalid!");
				} catch (NullPointerException exception) {
					//ignore
				}
			}
		});
		txtNstages.setText("");
		txtNstages.setBounds(10, 90, 46, 20);
		frmModel.getContentPane().add(txtNstages);
		txtNstages.setColumns(10);

		JLabel lblMaturityStage = new JLabel("Maturity Age:");
		lblMaturityStage.setBounds(10, 114, 97, 14);
		frmModel.getContentPane().add(lblMaturityStage);

		txtMatstage = new JTextField();
		txtMatstage.setHorizontalAlignment(SwingConstants.RIGHT);
		txtMatstage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					//Has to check every number of stages to see if it breaks one
					if (lblNStages.getText().equals("Sep.")){
						for (int i = 0; i < lblNStagesSeparate.length; i++) {
							if (Integer.parseInt(txtMatstage.getText())  > Integer.parseInt(lblNStagesSeparate[i].getText())) {
								txtMatstage.setText(lblNStagesSeparate[i].getText());
							}
						}
					} else {
						if (Integer.parseInt(txtMatstage.getText()) > Integer.parseInt(lblNStages.getText())) {
							txtMatstage.setText(lblNStages.getText());
						} 
					}						
					if (Integer.parseInt(txtMatstage.getText()) < 0 ) {
						lblMatStage.setText("0");
						txtMatstage.setText("");
					} else {
						lblMatStage.setText(txtMatstage.getText());
						txtMatstage.setText("");
					}

					//Sets all the species equally (Might throw NullPointerException if model still not initialized)
					for (int i = 0; i < lblMatStageSeparate.length; i++) {
						lblMatStageSeparate[i].setText(lblMatStage.getText());
						model.setMatureStage(i, Integer.parseInt(lblMatStage.getText()));
					}					
				} catch (NumberFormatException exception) {
					txtMatstage.setText("Invalid!");
				} catch (NullPointerException exception) {
					//ignore
				}
			}
		});
		txtMatstage.setText("");
		txtMatstage.setBounds(10, 129, 46, 20);
		frmModel.getContentPane().add(txtMatstage);
		txtMatstage.setColumns(10);



		txtNsteps = new JTextField();
		txtNsteps.setHorizontalAlignment(SwingConstants.RIGHT);
		txtNsteps.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if (Integer.parseInt(txtNsteps.getText()) < 1) {
						lblNsteps.setText("1");
						txtNsteps.setText("");
					} else {
						lblNsteps.setText(txtNsteps.getText());
						txtNsteps.setText("");
					}
				} catch (NumberFormatException exception) {
					txtNsteps.setText("Invalid!");
				}
			}
		});
		txtNsteps.setText("");
		txtNsteps.setBounds(402, 402, 46, 20);
		frmModel.getContentPane().add(txtNsteps);

		btnInitialize = new JButton("Initialize");
		btnInitialize.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				btnInitialize.setEnabled(false);
				btnRun.setEnabled(false);
				btnLoad.setEnabled(false);
				btnSave.setEnabled(false);
				btnSaveCompiled.setEnabled(false);
				btnListparameters.setEnabled(false);
				btnPaintSpecies.setEnabled(false);
				initializeModel();

			}
		});
		btnInitialize.setBounds(535, 255, 130, 23);
		frmModel.getContentPane().add(btnInitialize);

		JLabel lblYear = new JLabel("Cycle:");
		lblYear.setBounds(10, 358, 81, 14);
		frmModel.getContentPane().add(lblYear);

		lblCurrentyear = new JLabel("0");
		lblCurrentyear.setBounds(94, 358, 46, 14);
		frmModel.getContentPane().add(lblCurrentyear);

		JLabel lblMonth = new JLabel("Iteration in Cycle:");
		lblMonth.setBounds(150, 358, 150, 14);
		frmModel.getContentPane().add(lblMonth);

		lblCurrentmonth = new JLabel("0");
		lblCurrentmonth.setBounds(252, 358, 46, 14);
		frmModel.getContentPane().add(lblCurrentmonth);

		progressBar = new JProgressBar();
		progressBar.setBounds(10, 383, 388, 40);
		progressBar.setStringPainted(true);
		progressBar.setString("To Start Load or Initialize Model");
		frmModel.getContentPane().add(progressBar);

		comboBox = new JComboBox<String>();		
		comboBox.setModel(new DefaultComboBoxModel<String>(new String[] {""}));
		comboBox.setBounds(112, 332, 215, 20);
		frmModel.getContentPane().add(comboBox);

		btnPaintSpecies = new JButton("Show Species");
		btnPaintSpecies.setEnabled(false);
		btnPaintSpecies.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (comboBox.getSelectedIndex() != -1 ){
					model.paintSpecies(comboBox.getSelectedIndex(),(double) spinScaler.getValue());
				}
			}
		});
		btnPaintSpecies.setBounds(10, 307, 130, 23);
		frmModel.getContentPane().add(btnPaintSpecies);

		btnSave = new JButton("Save All");
		btnSave.setEnabled(false);
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveModel(new File(txtOutputFolder.getText()),true);
			}
		});
		btnSave.setBounds(535, 289, 130, 23);
		frmModel.getContentPane().add(btnSave);

		JLabel lblStagesDuration = new JLabel("Age Class' Duration:");
		lblStagesDuration.setBounds(10, 153 , 200, 14);
		frmModel.getContentPane().add(lblStagesDuration);

		lblStduration = new JLabel("1");
		lblStduration.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblStduration.setBounds(72, 170, 46, 14);
		frmModel.getContentPane().add(lblStduration);

		txtStageduration = new JTextField();
		txtStageduration.setHorizontalAlignment(SwingConstants.RIGHT);
		txtStageduration.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if (Integer.parseInt(txtStageduration.getText()) < 1) {
						lblStduration.setText("1");
						txtStageduration.setText("");
					} else {
						lblStduration.setText(txtStageduration.getText());
						txtStageduration.setText("");
					}

					//Sets all the species equally (Might throw NullPointerException if model still not initialized)
					for (int i = 0; i < lblStdurationSeparate.length; i++) {
						lblStdurationSeparate[i].setText(lblStduration.getText());
						model.setStageDuration(i, Integer.parseInt(lblStduration.getText()));
					}						
				} catch (NumberFormatException exception) {
					txtStageduration.setText("Invalid!");
				} catch (NullPointerException exception) {
					//ignore
				}
			}
		});
		txtStageduration.setText("");
		txtStageduration.setBounds(10, 168, 46, 20);
		frmModel.getContentPane().add(txtStageduration);
		txtStageduration.setColumns(10);

		spinNCores = new JSpinner();
		spinNCores.setModel(new SpinnerNumberModel((int)Math.round(((double)Runtime.getRuntime().availableProcessors())/2+((double)Runtime.getRuntime().availableProcessors())/4), 1, Runtime.getRuntime().availableProcessors(), 1));
		spinNCores.setBounds(636, 224, 29, 20);
		frmModel.getContentPane().add(spinNCores);

		lblCpuCores = new JLabel("CPU Cores:");
		lblCpuCores.setBounds(561, 227, 65, 14);
		frmModel.getContentPane().add(lblCpuCores);

		btnLoad = new JButton("Load");
		btnLoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fileChooser.setCurrentDirectory(new File(location + System.getProperty("file.separator")));
				fileChooser.showOpenDialog(new JFrame());
				if (fileChooser.getSelectedFile() != null) {
					loadFromFolder(fileChooser.getSelectedFile());
				}
			}
		});
		btnLoad.setBounds(394, 255, 130, 23);
		frmModel.getContentPane().add(btnLoad);

		btnSaveCompiled = new JButton("Save Compiled");
		btnSaveCompiled.setEnabled(false);
		btnSaveCompiled.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveModel(new File(txtOutputFolder.getText()),false);		
			}
		});
		btnSaveCompiled.setBounds(394, 289, 130, 23);
		frmModel.getContentPane().add(btnSaveCompiled);

		spinScaler = new JSpinner();
		spinScaler.setModel(new SpinnerNumberModel(1.0, 0.0, 3.0, 0.25));
		spinScaler.setBounds(63, 332, 46, 20);
		frmModel.getContentPane().add(spinScaler);

		lblScale = new JLabel("Scale By:");
		lblScale.setBounds(10, 334, 73, 14);
		frmModel.getContentPane().add(lblScale);

		lblLitterSize = new JLabel("Maximum Litter Size:");
		lblLitterSize.setBounds(10, 192, 130, 14);
		frmModel.getContentPane().add(lblLitterSize);

		lblLitsize = new JLabel("5");
		lblLitsize.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblLitsize.setBounds(72, 209, 46, 14);
		frmModel.getContentPane().add(lblLitsize);

		txtLitterSize = new JTextField();
		txtLitterSize.setHorizontalAlignment(SwingConstants.RIGHT);
		txtLitterSize.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if (Integer.parseInt(txtLitterSize.getText()) < 1) {
						lblLitsize.setText("1");
						txtLitterSize.setText("");
					} else {
						lblLitsize.setText(txtLitterSize.getText());
						txtLitterSize.setText("");
					}

					//Sets all the species equally (Might throw NullPointerException if model still not initialized)
					for (int i = 0; i < lblLitsizeSeparate.length; i++) {
						lblLitsizeSeparate[i].setText(lblLitsize.getText());
						model.setLitterSize(i, Integer.parseInt(lblLitsize.getText()));
					}						
				} catch (NumberFormatException exception) {
					txtLitterSize.setText("Invalid!");
				} catch (NullPointerException exception) {
					//ignore
				}
			}
		});
		txtLitterSize.setText("");
		txtLitterSize.setBounds(10, 207, 46, 20);
		frmModel.getContentPane().add(txtLitterSize);
		txtLitterSize.setColumns(10);

		lblDispersalrange = new JLabel("Dispersal Range (km): ");
		lblDispersalrange.setBounds(10, 0, 145, 14);
		frmModel.getContentPane().add(lblDispersalrange);

		lblDisran = new JLabel("2.5");
		lblDisran.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblDisran.setBounds(72, 17, 46, 14);
		frmModel.getContentPane().add(lblDisran);

		txtDispersalRange = new JTextField();
		txtDispersalRange.setHorizontalAlignment(SwingConstants.RIGHT);
		txtDispersalRange.setText("");
		txtDispersalRange.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {					
					if (Double.parseDouble(txtDispersalRange.getText().replace(',','.')) < 0) {
						lblDisran.setText("0");
						txtDispersalRange.setText("");
					} else {
						lblDisran.setText(txtDispersalRange.getText().replace(',','.'));
						txtDispersalRange.setText("");
					}

					//Sets all the species equally (Might throw NullPointerException if model still not initialized)
					for (int i = 0; i < lblDisranSeparate.length; i++) {
						lblDisranSeparate[i].setText(lblDisran.getText());
						model.setDispersalRange(i, Double.parseDouble(lblDisran.getText()));
					}		
				} catch (NumberFormatException exception) {
					txtDispersalRange.setText("Invalid!");
				} catch (NullPointerException exception) {
					//ignore
				}
			}
		});
		txtDispersalRange.setBounds(10, 15, 46, 20);
		frmModel.getContentPane().add(txtDispersalRange);
		txtDispersalRange.setColumns(10);

		btnSwimfly = new JButton("No");
		btnSwimfly.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try{
					if (btnSwimfly.getText().equals("Yes")) {
						btnSwimfly.setText("No");					
					} else if (btnSwimfly.getText().equals("No") ){
						btnSwimfly.setText("Yes");
					} else {
						btnSwimfly.setText("No");
					}

					//Might throw nullpointerexception if the model still hasn't been initialized
					for (int i = 0; i < btnSwimflySeparate.length; i++) {
						btnSwimflySeparate[i].setText(btnSwimfly.getText());
						model.setFlyswim(i, btnSwimfly.getText().equals("Yes"));
					}
				} catch (NullPointerException exception) {
					//Ignore
				}
			}
		});
		btnSwimfly.setBounds(10, 246, 73, 23);
		frmModel.getContentPane().add(btnSwimfly);

		JLabel lblSwimsOrFlies = new JLabel("Swims or flies:");
		lblSwimsOrFlies.setBounds(10, 231, 87, 14);
		frmModel.getContentPane().add(lblSwimsOrFlies);

		btnListparameters = new JButton("List Parameters");
		btnListparameters.setEnabled(false);
		btnListparameters.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				listParameters();
			}
		});
		btnListparameters.setBounds(254, 289, 130, 23);
		frmModel.getContentPane().add(btnListparameters);

		JButton btnHelp = new JButton("Help");
		btnHelp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showHelp();
			}
		});
		btnHelp.setBounds(254, 255, 130, 23);
		frmModel.getContentPane().add(btnHelp);

		btnSeparateMigration = new JButton("Separate");
		btnSeparateMigration.setEnabled(false);
		btnSeparateMigration.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				migrationWindow.setVisible(true);
			}
		});
		btnSeparateMigration.setBounds(112, 51, 89, 23);
		frmModel.getContentPane().add(btnSeparateMigration);

		btnSeparatenstages = new JButton("Separate");
		btnSeparatenstages.setEnabled(false);
		btnSeparatenstages.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				nStageWindow.setVisible(true);
			}
		});
		btnSeparatenstages.setBounds(112, 90, 89, 23);
		frmModel.getContentPane().add(btnSeparatenstages);

		btnSeparatematuritystage = new JButton("Separate");
		btnSeparatematuritystage.setEnabled(false);
		btnSeparatematuritystage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				matureStageWindow.setVisible(true);
			}
		});
		btnSeparatematuritystage.setBounds(112, 129, 89, 23);
		frmModel.getContentPane().add(btnSeparatematuritystage);

		btnSeparatestageduration = new JButton("Separate");
		btnSeparatestageduration.setEnabled(false);
		btnSeparatestageduration.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stageDurationWindow.setVisible(true);
			}
		});
		btnSeparatestageduration.setBounds(112, 168, 89, 23);
		frmModel.getContentPane().add(btnSeparatestageduration);

		btnSeparatelittersize = new JButton("Separate");
		btnSeparatelittersize.setEnabled(false);
		btnSeparatelittersize.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				litterSizeWindow.setVisible(true);
			}
		});
		btnSeparatelittersize.setBounds(112, 207, 89, 23);
		frmModel.getContentPane().add(btnSeparatelittersize);

		btnSeparatedispersalrange = new JButton("Separate");
		btnSeparatedispersalrange.setEnabled(false);
		btnSeparatedispersalrange.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispersalRangeWindow.setVisible(true);
			}
		});
		btnSeparatedispersalrange.setBounds(112, 15, 89, 23);
		frmModel.getContentPane().add(btnSeparatedispersalrange);

		btnSeparateswimfly = new JButton("Separate");
		btnSeparateswimfly.setEnabled(false);
		btnSeparateswimfly.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				swimFlyWindow.setVisible(true);
			}
		});
		btnSeparateswimfly.setBounds(112, 246, 89, 23);
		frmModel.getContentPane().add(btnSeparateswimfly);

		JLabel lblCycleLengthBox = new JLabel("Cycle length:");
		lblCycleLengthBox.setBounds(232, 79, 74, 14);
		frmModel.getContentPane().add(lblCycleLengthBox);

		txtCycleLength = new JTextField();
		txtCycleLength.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if (Integer.parseInt(txtCycleLength.getText()) < 1) {
						lblCycleLength.setText("1");
						txtCycleLength.setText("");
					} else {
						lblCycleLength.setText(txtCycleLength.getText());
						txtCycleLength.setText("");
					}					
				} catch (NumberFormatException exception) {
					txtCycleLength.setText("Invalid!");
				}
			}
		});
		txtCycleLength.setHorizontalAlignment(SwingConstants.RIGHT);
		txtCycleLength.setBounds(234, 100, 46, 20);
		frmModel.getContentPane().add(txtCycleLength);
		txtCycleLength.setColumns(10);

		lblCycleLength = new JLabel("12");
		lblCycleLength.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblCycleLength.setBounds(288, 103, 46, 14);
		frmModel.getContentPane().add(lblCycleLength);

		JLabel lblMaximumPopulation = new JLabel("Maximum Population:");
		lblMaximumPopulation.setBounds(117, 270, 125, 14);
		frmModel.getContentPane().add(lblMaximumPopulation);

		lblMaxpopvalue = new JLabel("10000");
		lblMaxpopvalue.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblMaxpopvalue.setBounds(178, 287, 46, 14);
		frmModel.getContentPane().add(lblMaxpopvalue);

		txtMaxPop = new JTextField();
		txtMaxPop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try{
					if (Integer.parseInt(txtMaxPop.getText()) < 0) {
						lblMaxpopvalue.setText("0");
						txtMaxPop.setText("");

					} else {
						lblMaxpopvalue.setText(txtMaxPop.getText());
						txtMaxPop.setText("");
					}	
					model.setMaximumPopulation(Integer.parseInt(lblMaxpopvalue.getText()));

				} catch (NumberFormatException exception) {
					txtMaxPop.setText("Invalid!");		

				} catch (NullPointerException exception) {
					//ignore
				}
			}
		});
		txtMaxPop.setBounds(115, 285, 55, 20);
		frmModel.getContentPane().add(txtMaxPop);
		txtMaxPop.setColumns(10);


	}

	/**
	 * Runs the model
	 */

	protected void runModel() {			
		try {
			RunModel rm = new RunModel();
			rm.addPropertyChangeListener(this);
			rm.execute();
		} catch (OutOfMemoryError e) {
			progressBar.setString("Out of memory error! Increase RAM allocation.");			
		}
	}

	/**
	 * (Re)Initializes the model
	 */

	private void initializeModel() {

		InitializeModel im = new InitializeModel();
		im.addPropertyChangeListener(this);
		im.execute();	    
	}

	/**
	 * Receives a folder and returns the variables found
	 * 
	 * @param file The folder to check for the variables
	 * @return Map[][] A Map[][] where [a][b], a- Variables in alphabetical order, b- months 
	 */

	private Map[][] getEnvironment(File file) {
		ArrayList<File> folders = listFoldersInFolder(file);
		Map[][] environment = new Map[folders.size()][Integer.parseInt(lblCycleLength.getText())];
		progressBar.setMaximum(folders.size());
		int index = 0;
		Map[] temp;
		for (File fileEntry : folders) {	
			progressBar.setValue(index);
			if (fileEntry.isDirectory()) {
				temp = getFolderMaps(fileEntry,Integer.parseInt(lblCycleLength.getText()));
				if (temp.length == Integer.parseInt(lblCycleLength.getText())) {
					environment[index] = temp;
					index++;
				} else {
					System.out.println("Skipped (Didn't have " + lblCycleLength.getText() + " files):" + fileEntry);
				}				
			}
		}
		if (index != folders.size()) {
			Map[][] newEnvironment = new Map[index][Integer.parseInt(lblCycleLength.getText())];
			for (int i = 0; i < index; i++) {
				newEnvironment[i] = environment[index];
			}
			environment = newEnvironment;
		}
		progressBar.setValue(0);
		return environment;
	}

	/**
	 * Receives a folder and loads the current state of the files present there
	 * 
	 * @param folder File The folder that contains the files
	 */

	private void loadFromFolder(File folder) {

		if (!folder.getName().startsWith("Current Step")) {
			progressBar.setString("Not an appropriate folder");
			return;
		}		

		btnInitialize.setEnabled(false);
		btnRun.setEnabled(false);
		btnPaintSpecies.setEnabled(false);
		btnSave.setEnabled(false);
		btnLoad.setEnabled(false);
		btnSaveCompiled.setEnabled(false);
		btnListparameters.setEnabled(false);

		LoadModel lm = new LoadModel(folder);
		lm.addPropertyChangeListener(this);
		lm.execute();	
	}

	/**
	 * Class to load the model in another thread 
	 */	

	class LoadModel extends SwingWorker<Void, Void> {

		File folder;
		boolean error = false;

		public LoadModel(File folder) {
			this.folder = folder;
		}

		@Override
		public Void doInBackground() {

			progressBar.setString("Loading Parameters");
			ArrayList<File> files = listFilesInFolder(folder);
			//Finds parameters
			int currentStep = 0;
			if (folder.getName().contains("(")){
				currentStep = Integer.parseInt(folder.getName().substring(13, folder.getName().indexOf(" ", 13)));
			} else {
				currentStep = Integer.parseInt(folder.getName().substring(13, folder.getName().length()));
			}
			int[] nStages= new int[1];
			int[] matSta=new int[1];
			double[] migR=new double[1];
			int[] staDur=new int[1];
			int[] litSize=new int[1];
			double[] dispersalRange=new double[1];
			boolean[] flySwim = new boolean[1];
			int maxPopulation = 0;

			int nSpecies = 0;
			boolean allEqual = true;

			BufferedReader reader;
			for (File fileEntry : files) {
				if (fileEntry.getName().equals("Parameters.txt")) {					
					try {
						reader = new BufferedReader(new FileReader(fileEntry));

						reader.readLine();
						maxPopulation = Integer.parseInt(reader.readLine());
						lblMaxpopvalue.setText(Integer.toString(maxPopulation));

						reader.readLine();					
						nSpecies = Integer.parseInt(reader.readLine());

						lblMigRSeparate = new JLabel[nSpecies];		
						lblNames = new JLabel[nSpecies];
						lblLitsizeSeparate = new JLabel[nSpecies];	
						lblDisranSeparate = new JLabel[nSpecies];	
						lblStdurationSeparate = new JLabel[nSpecies];	
						lblNStagesSeparate = new JLabel[nSpecies];	
						lblMatStageSeparate = new JLabel[nSpecies];	
						txtMigrSeparate = new JTextField[nSpecies];
						txtNstagesSeparate = new JTextField[nSpecies];
						txtLitterSizeSeparate = new JTextField[nSpecies];
						txtStagedurationSeparate = new JTextField[nSpecies];
						txtLitterSizeSeparate = new JTextField[nSpecies];
						txtDispersalRangeSeparate = new JTextField[nSpecies];
						txtMatstageSeparate = new JTextField[nSpecies];
						btnSwimflySeparate = new JButton[nSpecies];
						nStages= new int[nSpecies];
						matSta=new int[nSpecies];
						migR=new double[nSpecies];
						staDur=new int[nSpecies];
						litSize=new int[nSpecies];
						dispersalRange=new double[nSpecies];
						flySwim = new boolean[nSpecies];						

						for (int i = 0; i < nSpecies; i++) {
							lblNames[i] = new JLabel(reader.readLine()+":");
						}

						reader.readLine();	
						for (int i = 0; i < nSpecies; i++) {
							nStages[i] = Integer.parseInt(reader.readLine());
							lblNStagesSeparate[i] = new JLabel(Integer.toString(nStages[i]));
							lblNStagesSeparate[i].setFont(new Font("Tahoma", Font.BOLD, 14));
							lblNStagesSeparate[i].setForeground(new Color(0, 0, 0));
							txtNstagesSeparate[i] = new JTextField();
							txtNstagesSeparate[i].setHorizontalAlignment(SwingConstants.RIGHT);
							txtNstagesSeparate[i].setText("");							

							lblNStagesSeparate[i].setText(Integer.toString(nStages[i]));

							if (i > 0 && nStages[i] != nStages[i-1]) {
								allEqual=false;
							}
						}
						if (allEqual){
							lblNStages.setText(lblNStagesSeparate[0].getText());
						} else {
							lblNStages.setText("Sep.");
							allEqual = true;
						}


						reader.readLine();
						for (int i = 0; i < nSpecies; i++) {
							matSta[i] = Integer.parseInt(reader.readLine());
							lblMatStageSeparate[i] = new JLabel(Integer.toString(matSta[i]));
							lblMatStageSeparate[i].setFont(new Font("Tahoma", Font.BOLD, 14));
							lblMatStageSeparate[i].setForeground(new Color(0, 0, 0));
							txtMatstageSeparate[i] = new JTextField();
							txtMatstageSeparate[i].setHorizontalAlignment(SwingConstants.RIGHT);
							txtMatstageSeparate[i].setText("");

							lblMatStageSeparate[i].setText(Integer.toString(matSta[i]));

							if (i > 0 && matSta[i] != matSta[i-1]) {
								allEqual=false;
							}
						}
						if (allEqual){
							lblMatStage.setText(lblMatStageSeparate[0].getText());
						} else {
							lblMatStage.setText("Sep.");
							allEqual = true;
						}

						reader.readLine();
						for (int i = 0; i < nSpecies; i++) {
							migR[i] = Double.parseDouble(reader.readLine());
							lblMigRSeparate[i] = new JLabel(Double.toString(migR[i]));					
							lblMigRSeparate[i].setFont(new Font("Tahoma", Font.BOLD, 14));
							lblMigRSeparate[i].setForeground(new Color(0, 0, 0));	
							txtMigrSeparate[i] = new JTextField();
							txtMigrSeparate[i].setHorizontalAlignment(SwingConstants.RIGHT);
							txtMigrSeparate[i].setText("");

							lblMigRSeparate[i].setText(Double.toString(migR[i]));

							if (i > 0 && migR[i] != migR[i-1]) {
								allEqual=false;
							}
						}
						if (allEqual){
							lblMigR.setText(lblMigRSeparate[0].getText());
						} else {
							lblMigR.setText("Sep.");
							allEqual = true;
						}

						reader.readLine();
						for (int i = 0; i < nSpecies; i++) {
							staDur[i] = Integer.parseInt(reader.readLine());
							lblStdurationSeparate[i] = new JLabel(Integer.toString(staDur[i]));
							lblStdurationSeparate[i].setFont(new Font("Tahoma", Font.BOLD, 14));
							lblStdurationSeparate[i].setForeground(new Color(0, 0, 0));
							txtStagedurationSeparate[i] = new JTextField();
							txtStagedurationSeparate[i].setHorizontalAlignment(SwingConstants.RIGHT);
							txtStagedurationSeparate[i].setText("");

							lblStdurationSeparate[i].setText(Integer.toString(staDur[i]));

							if (i > 0 && staDur[i] != staDur[i-1]) {
								allEqual=false;
							}

						}
						if (allEqual){
							lblStduration.setText(lblStdurationSeparate[0].getText());
						} else {
							lblStduration.setText("Sep.");
							allEqual = true;
						}

						reader.readLine();
						for (int i = 0; i < nSpecies; i++) {
							litSize[i] = Integer.parseInt(reader.readLine());
							lblLitsizeSeparate[i] = new JLabel(Integer.toString(litSize[i]));	
							lblLitsizeSeparate[i].setFont(new Font("Tahoma", Font.BOLD, 14));
							lblLitsizeSeparate[i].setForeground(new Color(0, 0, 0));
							txtLitterSizeSeparate[i] = new JTextField();
							txtLitterSizeSeparate[i].setHorizontalAlignment(SwingConstants.RIGHT);
							txtLitterSizeSeparate[i].setText("");

							lblLitsizeSeparate[i].setText(Integer.toString(litSize[i]));

							if (i > 0 && litSize[i] != litSize[i-1]) {
								allEqual=false;
							}

						}
						if (allEqual){
							lblLitsize.setText(lblLitsizeSeparate[0].getText());
						} else {
							lblLitsize.setText("Sep.");
							allEqual = true;
						}						

						reader.readLine();
						for (int i = 0; i < nSpecies; i++) {
							dispersalRange[i] = Double.parseDouble(reader.readLine());
							lblDisranSeparate[i] = new JLabel(Double.toString(dispersalRange[i]));
							lblDisranSeparate[i].setFont(new Font("Tahoma", Font.BOLD, 14));
							lblDisranSeparate[i].setForeground(new Color(0, 0, 0));
							txtDispersalRangeSeparate[i] = new JTextField();
							txtDispersalRangeSeparate[i].setHorizontalAlignment(SwingConstants.RIGHT);
							txtDispersalRangeSeparate[i].setText("");	

							lblDisranSeparate[i].setText(Double.toString(dispersalRange[i]));

							if (i > 0 && dispersalRange[i] != dispersalRange[i-1]) {
								allEqual=false;
							}

						}
						if (allEqual){
							lblDisran.setText(lblDisranSeparate[0].getText());
						} else {
							lblDisran.setText("Sep.");
							allEqual = true;
						}

						reader.readLine();
						for (int i = 0; i < nSpecies; i++) {
							flySwim[i] = Boolean.parseBoolean(reader.readLine());
							if(flySwim[i]){								
								btnSwimflySeparate[i] = new JButton("Yes");
							} else {
								btnSwimflySeparate[i] = new JButton("No");
							}
							if (i > 0 && flySwim[i] != flySwim[i-1]) {
								allEqual=false;
							}
						}
						if (allEqual){
							btnSwimfly.setText(btnSwimflySeparate[0].getText());
						} else {
							btnSwimfly.setText("Sep.");
							allEqual = true;
						}

						reader.close();
						break;

					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

			progressBar.setString("Loading Species");
			int nStagesMax = 0;
			for (int i = 0; i <nStages.length; i++) {
				if (nStagesMax < nStages[i]) {
					nStagesMax = nStages[i];
				}
			}
			Map[][] species  = new Map[nSpecies][nStagesMax];

			progressBar.setMaximum(files.size());
			int index = 0;
			int stage = 0;
			int currentSpecies = -1;

			for (File fileEntry : files) {	
				if (fileEntry.getName().equals("Parameters.txt")||fileEntry.getName().endsWith("Stages.asc")) {
					stage = 0;
					currentSpecies++;
					index++;
					continue;
				}		
				if (!fileEntry.getName().endsWith(".asc")) {
					continue;
				}

				progressBar.setValue(index);
				try {
					species[currentSpecies][stage] = new Map(fileEntry);
					species[currentSpecies][stage].setName(lblNames[currentSpecies].getText().substring(0,lblNames[currentSpecies].getText().length()-1));
				} catch (IOException e) {					
					e.printStackTrace();
				}
				stage++;
				index++;

			}

			progressBar.setString("Fetching Variables");
			Map[][] environment = getEnvironment(new File(txtVariablesFolderPath.getText())); 
			progressBar.setString("Fetching Equations");
			String[][] equations = null;
			for (File fileEntry : files) {
				if (fileEntry.getName().equals("Equations.txt")) {					
					equations = getEquations(fileEntry);						
					break;
				}
			}

			progressBar.setString("Initializing Model");
			int nCores = (int) spinNCores.getValue();

			try {
				model = new Model(currentStep, maxPopulation, species, nStages, staDur , matSta, litSize, migR, dispersalRange, flySwim, environment, equations, nCores);	
			} catch (Exception e) {
				error = true;
				progressBar.setString("Equation is malformed");
				return null;
			}

			if (!model.checkEquationValidity()) {
				error = true;
				progressBar.setString("Species/Variables in equation do not exist");
				return null;
			}
			String[] comboString = new String[species.length];
			for (int i = 0; i < species.length; i++) {
				comboString[i] = species[i][0].getName().substring(0, species[i][0].getName().length());
			}
			comboBox.setModel(new DefaultComboBoxModel<String>(comboString));

			int year = 0;
			year = model.getCurrentStep() / Integer.parseInt(lblCycleLength.getText());
			int month = model.getCurrentStep()%Integer.parseInt(lblCycleLength.getText());
			if (month == 0) {
				lblCurrentyear.setText(Integer.toString(year-1));
				month= Integer.parseInt(lblCycleLength.getText());
			} else {
				lblCurrentyear.setText(Integer.toString(year));
			}
			lblCurrentmonth.setText(Integer.toString(month));

			setSeparateDispersalRange(model.getNSpecies());
			setSeparateLitterSize(model.getNSpecies());
			setSeparateMatureStage(model.getNSpecies());
			setSeparateMigration(model.getNSpecies());
			setSeparateNumberOfStages(model.getNSpecies());
			setSeparateStageDuration(model.getNSpecies());
			setSeparateSwimFly(model.getNSpecies());

			progressBar.setString("Loaded");			

			return null;
		}


		@Override
		public void done() {
			if (!error) {				
				btnRun.setEnabled(true);
				btnPaintSpecies.setEnabled(true);
				btnSave.setEnabled(true);
				btnSaveCompiled.setEnabled(true);
				btnListparameters.setEnabled(true);
				btnPaintSpecies.setEnabled(true);
				btnSeparatenstages.setEnabled(true);
				btnSeparatematuritystage.setEnabled(true);
				btnSeparatestageduration.setEnabled(true);
				btnSeparatelittersize.setEnabled(true);
				btnSeparatedispersalrange.setEnabled(true);
				btnSeparateswimfly.setEnabled(true);
				btnSeparateMigration.setEnabled(true);
			}
			btnInitialize.setEnabled(true);
			btnLoad.setEnabled(true);
			try {
				get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				if (e.getCause() instanceof OutOfMemoryError) {
					if (Integer.parseInt(System.getProperty("sun.arch.data.model")) == 64) { 
						progressBar.setString("Out Of Memory! Needs more allocated RAM");
					} else {
						progressBar.setString("Out Of Memory! Consider installing the 64bit version of java");
					}
				}
				e.printStackTrace();
			} catch (NumberFormatException e) {
				progressBar.setString("Error! Parameter.txt is badly defined");
			}		
			Toolkit.getDefaultToolkit().beep();
		}
	}

	/**
	 *  Receives a folder and return an ArrayList of the files present there in alphabetical order.
	 * 
	 * @param folder File The folder to check for files
	 * @return ArrayList<File> An ArrayList with the files in the folder
	 */

	private  ArrayList<File> listFilesInFolder(File folder) {
		ArrayList<File> filesInFolder = new ArrayList<File>();
		File[] orderedFiles = folder.listFiles();
		Arrays.sort(orderedFiles);
		for (File fileEntry : orderedFiles) {
			if (!fileEntry.isDirectory()) {
				filesInFolder.add(fileEntry);
			}
		}	    
		return filesInFolder;
	}

	/**
	 *  Receives a folder and return an ArrayList of the folders present there in alphabetical order.
	 * 
	 * @param folder File The folder to check for folders
	 * @return ArrayList<File> An ArrayList with the folders in the folder
	 */

	private static ArrayList<File> listFoldersInFolder(File folder) {
		ArrayList<File> foldersInFolder = new ArrayList<File>();
		File[] orderedFiles = folder.listFiles();
		Arrays.sort(orderedFiles);
		for (File fileEntry : orderedFiles) {
			if (fileEntry.isDirectory()) {
				foldersInFolder.add(fileEntry);
			}
		}	    
		return foldersInFolder;
	}

	/**
	 * 	Receives a folder and returns all .asc in that folder as a Map[]
	 * 
	 * @param folder File The folder
	 * @param nFiles int Number of files to fetch (0 if all)
	 * @return Map[] A Map[] with the .asc contained in the folder
	 */

	private  Map[] getFolderMaps (File folder, int nFiles) {
		ArrayList<File> filesInFolder = listFilesInFolder(folder);
		ArrayList<Map> mapsList = new ArrayList<Map>();
		progressBar.setMaximum(filesInFolder.size());

		int fileCount = 0;
		for (int i = 0; i < filesInFolder.size(); i++) {
			progressBar.setValue(i);
			if(filesInFolder.get(i).getName().endsWith(".asc")) {
				if (nFiles == 0 || (fileCount < nFiles)) {
					try {
						progressBar.setString("Fetching: "+ filesInFolder.get(i).getName());
						mapsList.add(new Map(filesInFolder.get(i)));
						fileCount++;
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}


		if (mapsList.size() == 0) {
			progressBar.setValue(0);
			progressBar.setString("No .asc found!");
			return null;
		} else if (nFiles != 0 && mapsList.size() != nFiles) {
			progressBar.setValue(0);
			progressBar.setString("Not enough .ascs found!");
			return null;
		}
		else {
			progressBar.setValue(0);
			return mapsList.toArray(new Map[mapsList.size()]);
		}
	}

	/**
	 * Receives a .txt file with the equations and returns them in a String[][].
	 * The .txt should be organized as such:
	 * <s1 birth rate equation>
	 * <s1 mortality rate equation>
	 * <s2 birth rate equation>
	 * ....
	 * Species should be in alphabetic order.
	 * 
	 * @param file File .txt with the equations
	 * @return String[][] String with the birth rate equations in [0][x] and mortality rate in [1][x]
	 */

	private static String[][] getEquations (File file) {
		BufferedReader equationsTxt;
		ArrayList<String> birthEq = new ArrayList<String>();
		ArrayList<String> mortEq = new ArrayList<String>();
		try {
			equationsTxt = new BufferedReader(new FileReader(file));
			String line = null;
			while((line = equationsTxt.readLine()) != null) {
				if (line.length() == 0) {
					while ((line = equationsTxt.readLine()) != null && line.length() == 0) { //To skip empty lines
						continue;
					}
				}
				if (line != null) {
					birthEq.add(line.trim());
					while ((line = equationsTxt.readLine()) != null && line.length() == 0) {
						continue;
					}
					mortEq.add(line.trim());
				}
			}		
			equationsTxt.close();
		} catch (IOException e) {
			e.printStackTrace();
		}	

		String[][] equations = new String[2][birthEq.size()];
		for(int i = 0; i< birthEq.size(); i++) {
			equations[0][i] = birthEq.get(i);
			equations[1][i] = mortEq.get(i);
		}

		return equations;		
	}

	/**
	 * Invoked when task's progress property changes.
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		if ("progress" == evt.getPropertyName()) {
			int progress = (Integer) evt.getNewValue();
			progressBar.setValue(progress);
		}
	}

	/**
	 * Class to initialize the model in another thread 
	 */	

	private class InitializeModel extends SwingWorker<Void, Void> {

		boolean error = false;

		@Override
		public Void doInBackground() {

			//erases previous model from memory to avoid memory leak
			model = null;
			System.gc();

			progressBar.setString("Fetching Species");
			Map[] initialPopulation = getFolderMaps(new File(txtSpeciesFolder.getText()),0);
			String[] names = new String[initialPopulation.length];
			for (int i = 0; i < initialPopulation.length; i++) {
				names[i] = initialPopulation[i].getName();
			}
			double[][] parameters = getFolderParameters(new File(txtSpeciesFolder.getText()), names);

			//Initialize separate Labels and Fields
			lblMigRSeparate = new JLabel[initialPopulation.length];		
			lblNames = new JLabel[initialPopulation.length];
			lblLitsizeSeparate = new JLabel[initialPopulation.length];	
			lblDisranSeparate = new JLabel[initialPopulation.length];	
			lblStdurationSeparate = new JLabel[initialPopulation.length];	
			lblNStagesSeparate = new JLabel[initialPopulation.length];	
			lblMatStageSeparate = new JLabel[initialPopulation.length];	
			txtMigrSeparate = new JTextField[initialPopulation.length];
			txtNstagesSeparate = new JTextField[initialPopulation.length];
			txtLitterSizeSeparate = new JTextField[initialPopulation.length];
			txtStagedurationSeparate = new JTextField[initialPopulation.length];
			txtLitterSizeSeparate = new JTextField[initialPopulation.length];
			txtDispersalRangeSeparate = new JTextField[initialPopulation.length];
			txtMatstageSeparate = new JTextField[initialPopulation.length];
			btnSwimflySeparate = new JButton[initialPopulation.length];

			for (int i = 0; i < initialPopulation.length; i++) {
				lblNames[i] = new JLabel(names[i]+":");

				lblMigRSeparate[i] = new JLabel(lblMigR.getText());					
				lblMigRSeparate[i].setFont(new Font("Tahoma", Font.BOLD, 14));
				lblMigRSeparate[i].setForeground(new Color(0, 0, 0));

				lblLitsizeSeparate[i] = new JLabel(lblLitsize.getText());	
				lblLitsizeSeparate[i].setFont(new Font("Tahoma", Font.BOLD, 14));
				lblLitsizeSeparate[i].setForeground(new Color(0, 0, 0));

				lblDisranSeparate[i] = new JLabel(lblDisran.getText());
				lblDisranSeparate[i].setFont(new Font("Tahoma", Font.BOLD, 14));
				lblDisranSeparate[i].setForeground(new Color(0, 0, 0));


				lblStdurationSeparate[i] = new JLabel(lblStduration.getText());
				lblStdurationSeparate[i].setFont(new Font("Tahoma", Font.BOLD, 14));
				lblStdurationSeparate[i].setForeground(new Color(0, 0, 0));

				lblNStagesSeparate[i] = new JLabel(lblNStages.getText());
				lblNStagesSeparate[i].setFont(new Font("Tahoma", Font.BOLD, 14));
				lblNStagesSeparate[i].setForeground(new Color(0, 0, 0));

				lblMatStageSeparate[i] = new JLabel(lblMatStage.getText());
				lblMatStageSeparate[i].setFont(new Font("Tahoma", Font.BOLD, 14));
				lblMatStageSeparate[i].setForeground(new Color(0, 0, 0));

				btnSwimflySeparate[i] = new JButton(btnSwimfly.getText());				

				txtMigrSeparate[i] = new JTextField();
				txtMigrSeparate[i].setHorizontalAlignment(SwingConstants.RIGHT);
				txtMigrSeparate[i].setText("");

				txtNstagesSeparate[i] = new JTextField();
				txtNstagesSeparate[i].setHorizontalAlignment(SwingConstants.RIGHT);
				txtNstagesSeparate[i].setText("");

				txtMatstageSeparate[i] = new JTextField();
				txtMatstageSeparate[i].setHorizontalAlignment(SwingConstants.RIGHT);
				txtMatstageSeparate[i].setText("");

				txtStagedurationSeparate[i] = new JTextField();
				txtStagedurationSeparate[i].setHorizontalAlignment(SwingConstants.RIGHT);
				txtStagedurationSeparate[i].setText("");

				txtLitterSizeSeparate[i] = new JTextField();
				txtLitterSizeSeparate[i].setHorizontalAlignment(SwingConstants.RIGHT);
				txtLitterSizeSeparate[i].setText("");

				txtDispersalRangeSeparate[i] = new JTextField();
				txtDispersalRangeSeparate[i].setHorizontalAlignment(SwingConstants.RIGHT);
				txtDispersalRangeSeparate[i].setText("");			
			}

			setSeparateDispersalRange(initialPopulation.length);
			setSeparateLitterSize(initialPopulation.length);
			setSeparateMatureStage(initialPopulation.length);
			setSeparateMigration(initialPopulation.length);
			setSeparateNumberOfStages(initialPopulation.length);
			setSeparateStageDuration(initialPopulation.length);
			setSeparateSwimFly(initialPopulation.length);


			progressBar.setString("Fetching Variables");
			Map[][] environment = getEnvironment(new File(txtVariablesFolderPath.getText())); 
			progressBar.setString("Fetching Equations");
			String[][] equations = getEquations(new File(txtEquationtxtPath.getText()));	
			progressBar.setString("Initializing Model");
			int nCores = (int) spinNCores.getValue();

			boolean allEqual = true;
			int[] nStages = new int[initialPopulation.length];
			if (lblNStages.getText().equals("Sep.")) {
				for (int i = 0; i < initialPopulation.length; i++ ) {
					if (parameters[i][0] != -1) {
						nStages[i] = (int)parameters[i][0];
						lblNStagesSeparate[i].setText(Integer.toString(nStages[i]));
					} else {
						nStages[i] = Integer.parseInt(lblNStagesSeparate[i].getText());
					}
				}
			} else {	
				for (int i = 0; i < initialPopulation.length; i++ ) {
					if (parameters[i][0] != -1) {
						nStages[i] = (int)parameters[i][0];
						lblNStagesSeparate[i].setText(Integer.toString(nStages[i]));
					} else {
						nStages[i] = Integer.parseInt(lblNStages.getText());
					}				}
			}
			for (int i = 1; i< lblNStagesSeparate.length; i++) { //If different, sets the main one to "Sep."
				if (!lblNStagesSeparate[i-1].getText().equals(lblNStagesSeparate[i].getText())) {
					allEqual = false;
					lblNStages.setText("Sep.");
					break;
				}
			}
			if(allEqual) { //If all equals sets the main one to the value
				lblNStages.setText(lblNStagesSeparate[0].getText());				
			}
			allEqual= true;

			int startPopValue = Integer.parseInt(lblInitPop.getText());

			int[] matureStage = new int[initialPopulation.length];			
			if (lblMatStage.getText().equals("Sep.")) {
				for (int i = 0; i < initialPopulation.length; i++ ) {
					if (parameters[i][1] != -1) {
						matureStage[i] = (int)parameters[i][1];
						lblMatStageSeparate[i].setText(Integer.toString(matureStage[i]));
					} else {
						matureStage[i] = Integer.parseInt(lblMatStageSeparate[i].getText());
					}
				}
			} else {	
				for (int i = 0; i < initialPopulation.length; i++ ) {
					if (parameters[i][1] != -1) {
						matureStage[i] = (int)parameters[i][1];
						lblMatStageSeparate[i].setText(Integer.toString(matureStage[i]));
					} else {
						matureStage[i] = Integer.parseInt(lblMatStage.getText());
					}				}
			}
			for (int i = 1; i< lblMatStageSeparate.length; i++) { //If different, sets the main one to "Sep."
				if (!lblMatStageSeparate[i-1].getText().equals(lblMatStageSeparate[i].getText())) {
					allEqual = false;
					lblMatStage.setText("Sep.");
					break;
				}
			}
			if(allEqual) { //If all equals sets the main one to the value
				lblMatStage.setText(lblMatStageSeparate[0].getText());				
			}
			allEqual= true;

			double[] migrationRate = new double[initialPopulation.length]; 				
			if (lblMigR.getText().equals("Sep.")) {
				for (int i = 0; i < initialPopulation.length; i++ ) {
					if (parameters[i][2] != -1) {
						migrationRate[i] = parameters[i][2];
						lblMigRSeparate[i].setText(Double.toString(migrationRate[i]));
					} else {
						migrationRate[i] = Double.parseDouble(lblMigRSeparate[i].getText());
					}
				}
			} else {	
				for (int i = 0; i < initialPopulation.length; i++ ) {
					if (parameters[i][2] != -1) {
						migrationRate[i] = parameters[i][2];
						lblMigRSeparate[i].setText(Double.toString(migrationRate[i]));
					} else {
						migrationRate[i] = Double.parseDouble(lblMigR.getText());
					}				}
			}
			for (int i = 1; i< lblMigRSeparate.length; i++) { //If different, sets the main one to "Sep."
				if (!lblMigRSeparate[i-1].getText().equals(lblMigRSeparate[i].getText())) {
					allEqual = false;
					lblMigR.setText("Sep.");
					break;
				}
			}
			if(allEqual) { //If all equals sets the main one to the value
				lblMigR.setText(lblMigRSeparate[0].getText());				
			}
			allEqual= true;

			int[] stageDuration = new int[initialPopulation.length];			
			if (lblStduration.getText().equals("Sep.")) {
				for (int i = 0; i < initialPopulation.length; i++ ) {
					if (parameters[i][3] != -1) {
						stageDuration[i] = (int) parameters[i][3];
						lblStdurationSeparate[i].setText(Integer.toString(stageDuration[i]));
					} else {
						stageDuration[i] = Integer.parseInt(lblStdurationSeparate[i].getText());
					}
				}
			} else {	
				for (int i = 0; i < initialPopulation.length; i++ ) {
					if (parameters[i][3] != -1) {
						stageDuration[i] = (int)parameters[i][3];
						lblStdurationSeparate[i].setText(Integer.toString(stageDuration[i]));
					} else {
						stageDuration[i] = Integer.parseInt(lblStduration.getText());
					}				}
			}
			for (int i = 1; i< lblStdurationSeparate.length; i++) { //If different, sets the main one to "Sep."
				if (!lblStdurationSeparate[i-1].getText().equals(lblStdurationSeparate[i].getText())) {
					allEqual = false;
					lblStduration.setText("Sep.");
					break;
				}
			}
			if(allEqual) { //If all equals sets the main one to the value
				lblStduration.setText(lblStdurationSeparate[0].getText());				
			}
			allEqual= true;

			int[] litterSize = new int[initialPopulation.length];			
			if (lblLitsize.getText().equals("Sep.")) {
				for (int i = 0; i < initialPopulation.length; i++ ) {
					if (parameters[i][4] != -1) {
						litterSize[i] = (int) parameters[i][4];
						lblLitsizeSeparate[i].setText(Integer.toString(litterSize[i]));
					} else {
						litterSize[i] = Integer.parseInt(lblLitsizeSeparate[i].getText());
					}
				}
			} else {	
				for (int i = 0; i < initialPopulation.length; i++ ) {
					if (parameters[i][4] != -1) {
						litterSize[i] = (int)parameters[i][4];
						lblLitsizeSeparate[i].setText(Integer.toString(litterSize[i]));
					} else {
						litterSize[i] = Integer.parseInt(lblLitsize.getText());
					}				}
			}
			for (int i = 1; i< lblLitsizeSeparate.length; i++) { //If different, sets the main one to "Sep."
				if (!lblLitsizeSeparate[i-1].getText().equals(lblLitsizeSeparate[i].getText())) {
					allEqual = false;
					lblLitsize.setText("Sep.");
					break;
				}
			}
			if(allEqual) { //If all equals sets the main one to the value
				lblLitsize.setText(lblLitsizeSeparate[0].getText());				
			}
			allEqual= true;

			double[] dispersalRange =  new double[initialPopulation.length];
			if (lblDisran.getText().equals("Sep.")) {
				for (int i = 0; i < initialPopulation.length; i++ ) {
					if (parameters[i][5] != -1) {
						dispersalRange[i] = parameters[i][5];
						lblDisranSeparate[i].setText(Double.toString(dispersalRange[i]));
					} else {
						dispersalRange[i] = Double.parseDouble(lblDisranSeparate[i].getText());
					}
				}
			} else {	
				for (int i = 0; i < initialPopulation.length; i++ ) {
					if (parameters[i][5] != -1) {
						dispersalRange[i] = parameters[i][5];
						lblDisranSeparate[i].setText(Double.toString(dispersalRange[i]));
					} else {
						dispersalRange[i] = Double.parseDouble(lblDisran.getText());
					}				}
			}
			for (int i = 1; i< lblDisranSeparate.length; i++) { //If different, sets the main one to "Sep."
				if (!lblDisranSeparate[i-1].getText().equals(lblDisranSeparate[i].getText())) {
					allEqual = false;
					lblDisran.setText("Sep.");
					break;
				}
			}
			if(allEqual) { //If all equals sets the main one to the value
				lblDisran.setText(lblDisranSeparate[0].getText());				
			}
			allEqual= true;

			boolean[] flySwim = new boolean[initialPopulation.length];
			if (btnSwimfly.getText().equals("Sep.")) {
				for (int i = 0; i < initialPopulation.length; i++ ) {
					if (parameters[i][6] != -1) {
						flySwim[i] = (parameters[i][6] == 1);
						if (flySwim[i]) {
							btnSwimflySeparate[i].setText("Yes");
						} else {
							btnSwimflySeparate[i].setText("No");
						}
					} else {
						flySwim[i] = btnSwimflySeparate[i].getText().equals("Yes");
					}
				}
			} else {
				for (int i = 0; i < initialPopulation.length; i++ ) {
					if (parameters[i][0] != -1) {
						flySwim[i] = (parameters[i][6] == 1);
						if (flySwim[i]) {
							btnSwimflySeparate[i].setText("Yes");
						} else {
							btnSwimflySeparate[i].setText("No");
						}
					} else {
						flySwim[i] = btnSwimfly.getText().equals("Yes");
					}					
				}
			}
			for (int i = 1; i< btnSwimflySeparate.length; i++) { //If different, sets the main one to "Sep."
				if (!btnSwimflySeparate[i-1].getText().equals(btnSwimflySeparate[i].getText())) {
					allEqual = false;
					btnSwimfly.setText("Sep.");
					break;
				}
			}
			if(allEqual) { //If all equals sets the main one to the value
				btnSwimfly.setText(btnSwimflySeparate[0].getText());				
			}
			allEqual= true;	

			int maxPopulation = Integer.parseInt(lblMaxpopvalue.getText());
			try {
				model = new Model(initialPopulation, nStages, stageDuration ,startPopValue, maxPopulation, matureStage, litterSize, migrationRate, dispersalRange, flySwim, environment, equations, nCores);
			} catch (Exception e) {
				error = true;
				progressBar.setString("Equation is malformed");
				return null;
			}

			if (!model.checkEquationValidity()) {
				error = true;
				progressBar.setString("Species/Variables in equation do not exist");
				return null;
			}	

			String[] comboString = new String[initialPopulation.length];
			for (int i = 0; i < initialPopulation.length; i++) {
				comboString[i] = initialPopulation[i].getName();
			}
			comboBox.setModel(new DefaultComboBoxModel<String>(comboString));



			progressBar.setString("Initialization Complete");

			lblCurrentyear.setText("0");
			lblCurrentmonth.setText("0");
			return null;
		}


		@Override
		public void done() {

			if (!error) {				
				btnRun.setEnabled(true);
				btnPaintSpecies.setEnabled(true);
				btnSave.setEnabled(true);
				btnSaveCompiled.setEnabled(true);
				btnListparameters.setEnabled(true);
				btnPaintSpecies.setEnabled(true);
				btnSeparatenstages.setEnabled(true);
				btnSeparatematuritystage.setEnabled(true);
				btnSeparatestageduration.setEnabled(true);
				btnSeparatelittersize.setEnabled(true);
				btnSeparatedispersalrange.setEnabled(true);
				btnSeparateswimfly.setEnabled(true);
				btnSeparateMigration.setEnabled(true);
			}
			btnInitialize.setEnabled(true);
			btnLoad.setEnabled(true);
			Toolkit.getDefaultToolkit().beep();
			try {
				get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				if (e.getCause() instanceof OutOfMemoryError) {
					if (Integer.parseInt(System.getProperty("sun.arch.data.model")) == 64) { 
						progressBar.setString("Out Of Memory! Needs more allocated RAM");
					} else {
						progressBar.setString("Out Of Memory! Consider installing the 64bit version of java");
					}
				}
				e.printStackTrace();
			} catch (NumberFormatException e) {
				progressBar.setString("Error! Parameter.txt is badly defined");
			}
		}
	}

	/**
	 * Class to run the model in another thread 
	 */	

	private class RunModel extends SwingWorker<Void, Void> {

		@Override
		public Void doInBackground() {
			
			int year;
			int month;
			progressBar.setMaximum(Integer.parseInt(lblNsteps.getText()));
			progressBar.setString("Running Model...");
			for (int i = 0; i < Integer.parseInt(lblNsteps.getText());i++) {
				model.setCores((int)spinNCores.getValue());
				//Avoid memory leak
				System.gc();
				progressBar.setValue(i);
				model.run(1);
				year = 0;
				year = model.getCurrentStep() / Integer.parseInt(lblCycleLength.getText());
				month = model.getCurrentStep()%Integer.parseInt(lblCycleLength.getText());
				if (month == 0) {
					lblCurrentyear.setText(Integer.toString(year-1));
					month= Integer.parseInt(lblCycleLength.getText());
				} else {
					lblCurrentyear.setText(Integer.toString(year));
				}
				lblCurrentmonth.setText(Integer.toString(month));

				if (stop) {
					stop = false;
					break;
				}
			}

			progressBar.setString("Done!");
			progressBar.setValue(0);

			return null;
		}


		@Override
		public void done() {
			btnRun.setText("Run");
			btnInitialize.setEnabled(true);
			btnRun.setEnabled(true);
			btnLoad.setEnabled(true);
			Toolkit.getDefaultToolkit().beep();
			try {
				get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				if (e.getCause() instanceof OutOfMemoryError) {
					if (Integer.parseInt(System.getProperty("sun.arch.data.model")) == 64) { 
						progressBar.setString("Out Of Memory! Needs more allocated RAM");
					} else {
						progressBar.setString("Out Of Memory! Consider installing the 64bit version of java");
					}
				}
				e.printStackTrace();
			}
		}
	}


	/**
	 * Saves the model
	 * 
	 * @param folder File Folder to save the model in
	 * @param all Boolean True if all stages are to be saved, false if only the compiled one.
	 */

	private void saveModel(File folder, boolean all) {
		btnSave.setEnabled(false);
		btnSaveCompiled.setEnabled(false);
		btnSaveCompiled.setEnabled(false);

		SaveModel sm = new SaveModel(folder,all);
		sm.addPropertyChangeListener(this);
		sm.execute();	
	}

	/**
	 * Returns an array with the parameters for the species that have a .txt with them
	 * @param folder File The folder to check for parameters
	 * @param names String[] An array with the names of the species
	 * @return Double[][] An array with the parameters for the species. [a][b] - a, the species - b, 0-number of stages, 1-mature stage, 2-migration rate, 3-stage duration, 4-maximum litter size, 5-dispersal range, 6-swims or flies
	 */

	public double[][] getFolderParameters(File folder, String[] names) {
		double[][] parameters = new double[names.length][7]; //7 is the number of parameters
		for (int i = 0; i < names.length; i++) {
			for (int z = 0; z < 7; z++) {
				parameters[i][z] = -1;
			}
		}
		ArrayList<File> filesInFolder = listFilesInFolder(folder);
		progressBar.setString("Fetching species specific parameters");
		progressBar.setMaximum(filesInFolder.size());
		BufferedReader reader;
		for (int i = 0; i < filesInFolder.size(); i++) {
			progressBar.setValue(i);
			for (int z = 0; z < names.length; z++) {
				if(filesInFolder.get(i).getName().equals(names[z]+".txt")) {
					try {
						reader = new BufferedReader(new FileReader(filesInFolder.get(i)));
						reader.readLine();
						parameters[z][0] = Double.parseDouble(reader.readLine().replace(",","."));
						reader.readLine();
						parameters[z][1] = Double.parseDouble(reader.readLine().replace(",","."));
						reader.readLine();
						parameters[z][2] = Double.parseDouble(reader.readLine().replace(",","."));
						reader.readLine();
						parameters[z][3] = Double.parseDouble(reader.readLine().replace(",","."));
						reader.readLine();
						parameters[z][4] = Double.parseDouble(reader.readLine().replace(",","."));
						reader.readLine();
						parameters[z][5] = Double.parseDouble(reader.readLine().replace(",","."));
						reader.readLine();
						if (reader.readLine().toUpperCase().equals("YES")) {
							parameters[z][6] = 1;
						} else {
							parameters[z][6] = 0;
						}
						reader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

		return parameters;
	}

	/**
	 * Class that saves the model in another thread
	 */

	private class SaveModel extends SwingWorker<Void, Void> {

		File folder;
		boolean all;
		public SaveModel(File folder, boolean all) {
			this.folder = folder;
			this.all = all;
		}

		@Override
		public Void doInBackground() {
			progressBar.setString("Saving Files...");
			model.saveSpeciesToFile(folder, all);	
			progressBar.setString("");		

			return null;
		}


		@Override
		public void done() {
			btnSave.setEnabled(true);
			btnSaveCompiled.setEnabled(true);
			btnSaveCompiled.setEnabled(true);
			Toolkit.getDefaultToolkit().beep();
		}
	}

	/**
	 * Generates a separate window with information about how to configure the model
	 */

	public void showHelp() {
		String helpText =   "********************************************************************************************************************\r\n"+
				"* Copyright 2020 Tomé Neves (CC BY-NC-SA 3.0)\r\n " +
				"* https://creativecommons.org/licenses/by-nc-sa/3.0/\r\n"+
				"* This program is free software. It comes without any warranty.\r\n"+
				"* \r\n"+
				"* Author: Tomé Neves\r\n"+
				"* Address: tome_neves@hotmail.com (Contact for any questions)\r\n"+	
				"********************************************************************************************************************\r\n\r\n"+
				"To setup the model, define the parameters, choose the folders, and \"Initialize\". Alternatively, you can load a folder (named Current Step X) generated by \"Save All\".\r\n" +
				"To check the parameters of the current model as well as the variables, use \"List Parameters\".\r\n" +
				"You can change the parameters at any time after initializing the model. Use the Separate button to set them separately for each species. WARNING: Changing the number of stages will set the number of individuals the same in every cell where the species is currently present.\r\n\r\n"+	
				"********************************************************************************************************************\r\n\r\n"+
				"**Migration Rate: (0-1)\r\n Probability of each individual leaving their home cell during a month\r\n\r\n" +
				"**Maximum Age: (2-Infinity)\r\n Maximum age class an individual can reach before dying \r\nWARNING: Changing the number of age classes during simulation will set the number of individuals the same in every cell where the species is currently present.\r\n\r\n" +
				"**Maturity Age: (1-Stages in Hierarchy)\r\n The age at which species start to be able to reproduce\r\n\r\n" +
				"**Age Class' Duration: (1-Infinity)\r\n The number of iterations after which individuals are moved up an age class. All individuals born during this length are moved up, even if they were just born.\r\n\r\n" +
				"**Maximum Litter Size: (1-Infinity)\r\n The maximum number of individuals that can be born from a birth event (the precise number is randomly chosen from an uniform distribution starting in 1 and ending in the chosen maximum)\r\n\r\n" +
				"**Dispersal Range (km): (0-Infinity)\r\n Maximum distance in km that and individual can cover in one iteration\r\n\r\n"+
				"**Swims or Flies: (Yes/No)\r\n If set to \"Yes\" individuals can jump over No Data zones\r\n\r\n"+
				"**Initial Population: (0-Infinity)\r\n The number of individuals present at each cell where the species is present at the start of the model\r\n\r\n" +
				"**Maximum Population: (0-Infinity)\r\n The maximum number of individuals that can be present at each cell. 0 for no limit\r\n\r\n" +
				"********************************************************************************************************************\r\n\r\n"+
				"**CPU Cores:\r\n Number of CPU cores to use. \r\n\r\n" +
				"**Output Folder:\r\n Folder where the model saves the .asc files\r\n\r\n" + 
				"**Species Folder:\r\n A folder with the .asc files representing the initial presences for each species \r\nIt can also include .txt files with the same name as the asc files to set one or more species parameters a priori.\r\n" +
				"The .txt shoud be created as such:\r\n" +
				"<Name of species(same as .asc)>.txt\r\n" +
				"Maximum Age:\r\n" +
				"<2-infinity>\r\n" +
				"Maturity Age:\r\n" +
				"<0-Number of Stages>\r\n" +
				"Migration rate:\r\n" +
				"<0-1>\r\n" +
				"Age Class' Duration:\r\n" +
				"<1-infinity>\r\n" +
				"Maximum Litter Size:\r\n" +
				"<1-infinity>\r\n" +
				"Dispersal Range:\r\n" +
				"<0-infinity in km>\r\n" +
				"Swims or Flies:\r\n" +
				"<Yes\\No>\r\n\r\n" +

				"**Cycle Length:\r\n The number of steps each cycle has. For example, 12 if using one .asc for each month." +
				"**Variables Folder:\r\n A folder which contains one folder for each of the variables to use. Each of these folders should contain one or more .asc files, one for each cycle step (alphabetical order) \r\n\r\n" +
				"**Equation txt:\r\n A .txt file containing the equations for birth rate (probability of each individual having offspring during a month) and mortality rate (probability of each individual dying during a month).\r\n" +
				" The .txt should be organized as such:\r\n\r\n br=<equation for birth rate of species 1>\r\n mr=<equation for mortality rate for species 1>\r\n br=<equation br species 2>\r\n etc...\r\n For multiple equations depending on variable values, use:\r\n if(condition using == / <= / >= / < / >) {br=<equation>} else {br=<equation>}\r\n\r\n"+
				" The equations should be written as java code (without \";\").\n Always use at least one decimal place (i.e. Use 0.0 and never 0).\n\n "+
				" The variables names to use in the equations are: \r\n" + 
				" v[1],v[2],v[3],... = Environmental variables values in the current cell, in alphabetical order (based on the names of the folders)\r\n" +
				" s[1],s[2],s[3],... = Size of the population of each species in the current cell, in alphabetical order (based on the names of the .asc files)\r\n" +
				" popSize = Current total population size of this species in the cell. Equivalent to s[x], where x is the current species\r\n\r\n" +
				" Example: if(v[1] > 6.0) {br=0.5} else {br=s[1]*6.0 + popSize} \r\n\r\n" + 
				" x^y -> Math.pow(x,y)\r\n y root of x -> Math.pow(x,-1.0/y)\r\n Log(e) of x -> Math.log(x)\r\n Log(10) of x -> Math.log10(x)";



		JFrame helpWindow = new JFrame();
		helpWindow.getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		helpWindow.setIconImage(Toolkit.getDefaultToolkit().getImage(Application.class.getResource("/icon.png")));
		helpWindow.setBounds(100, 100, 655, 611);
		helpWindow.setTitle("Current Model Parameters");
		helpWindow.setResizable(false);
		helpWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		helpWindow.getContentPane().setLayout(null);	
		JTextArea txtHelp= new JTextArea();
		txtHelp.setText(helpText);
		txtHelp.setBounds(0, 0, 650, 600);
		txtHelp.setEditable(false);
		txtHelp.setLineWrap(true);
		txtHelp.setWrapStyleWord(true);

		JScrollPane scroller = new JScrollPane(txtHelp,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scroller.setBounds(0, 0, 650, 600);		
		helpWindow.getContentPane().add(scroller);
		txtHelp.setSelectionStart(0);
		txtHelp.setSelectionEnd(0);
		helpWindow.setVisible(true);


	}

	/**
	 * Generates a separate window with the current model's parameters
	 */

	public void listParameters() {
		int[] nStages = new int[model.getNSpecies()];
		int[] matureStage = new int[model.getNSpecies()];
		double[] migrationRate =  new double[model.getNSpecies()];
		int[] stageDuration = new int[model.getNSpecies()];
		int[] litterSize = new int[model.getNSpecies()];
		double[] dispersalRange = new double[model.getNSpecies()];

		for (int i = 0; i < model.getNSpecies(); i++) {
			nStages[i] = model.getNumberStages(i);
			matureStage[i] = model.getMatureStage(i);
			migrationRate[i] = model.getMigrationRate(i);
			stageDuration[i] = model.getStageDuration(i);
			litterSize[i] = model.getLitterSize(i);
			dispersalRange[i] = model.getDispersalRange(i);
		}

		String[] flySwim =  new String[model.getNSpecies()];
		for (int i = 0; i < model.getNSpecies(); i++) {
			if (model.getSwimFly(i)) {
				flySwim[i] = "Yes";
			} else {
				flySwim[i] = "No";
			}
		}

		String[] speciesNames = model.getSpeciesNames();
		String[][] equations = model.getEquations();

		String parameterList = "***************************************\r\n*****Current Model Parameters*****\r\n***************************************\r\n";
		for (int i = 0; i < speciesNames.length; i++ ){
			parameterList += "\r\ns["+(i+1)+"] = "+ speciesNames[i] + "\r\n\r\n";
			parameterList += "Number of stages:\r\n" + nStages[i] +"\r\n";
			parameterList += "Mature stage:\r\n"+matureStage[i]+"\r\n";
			parameterList += "Stage duration:\r\n"+ stageDuration[i]+"\r\n";
			parameterList += "Maximum litter size:\r\n"+litterSize[i]+"\r\n";
			parameterList += "Migration rate:\r\n"+migrationRate[i]+"\r\n";
			parameterList += "Dispersal range:\r\n"+dispersalRange[i]+"\r\n";
			parameterList += "Swims or flies:\r\n"+flySwim[i]+"\r\n";
			parameterList += "\r\n***************************************";
		}

		parameterList += "\r\nEquations:\r\n";

		for (int i = 0; i < speciesNames.length; i++ ){
			parameterList += "\r\ns["+(i+1)+"] = "+ speciesNames[i] + "\r\n\r\n";
			parameterList += equations[0][i] + "\r\n\r\n";
			parameterList += equations[1][i] + "\r\n\r\n";
		}
		parameterList += "***************************************";
		parameterList += "\r\nVariables (name of first month):\r\n";
		String[] variableNames = model.getVariableNames();
		for (int i = 0; i < variableNames.length; i++ ){
			parameterList += "v["+(i+1)+"] ="+ variableNames[i] + "\r\n\r\n";

		}

		JFrame parameterWindow = new JFrame();
		parameterWindow.getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		parameterWindow.setIconImage(Toolkit.getDefaultToolkit().getImage(Application.class.getResource("/icon.png")));
		parameterWindow.setBounds(100, 100, 606, 626);
		parameterWindow.setTitle("Current Model Parameters");
		parameterWindow.setResizable(false);
		parameterWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		parameterWindow.getContentPane().setLayout(null);	
		JTextArea txtParameters= new JTextArea();
		txtParameters.setText(parameterList);
		txtParameters.setBounds(0, 0, 600, 600);
		txtParameters.setEditable(false);

		JScrollPane scroller = new JScrollPane(txtParameters,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scroller.setBounds(0, 0, 600, 600);
		parameterWindow.getContentPane().add(scroller);
		txtParameters.setSelectionStart(0);
		txtParameters.setSelectionEnd(0);
		parameterWindow.setVisible(true);

	}

	/**
	 * Generates a separate window to set migration
	 * @param nSpecies int Number of species
	 */

	public void setSeparateMigration(int nSpecies) {
		JLabel[] lblNames = new JLabel[this.lblNames.length];
		for (int i = 0; i < lblNames.length; i++) {
			lblNames[i] = new JLabel(this.lblNames[i].getText());
		}		
		migrationWindow = new JFrame();
		migrationWindow.getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		migrationWindow.setIconImage(Toolkit.getDefaultToolkit().getImage(Application.class.getResource("/icon.png")));
		int windowHeight = 10 + nSpecies * 60 ;
		migrationWindow.setBounds(100, 100, 365, windowHeight);
		migrationWindow.setTitle("Migration By Species");
		migrationWindow.setResizable(false);
		migrationWindow.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		migrationWindow.getContentPane().setLayout(null);

		ActionListener listener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (int i = 0; i < model.getNSpecies(); i++ ){
					if (e.getSource() == txtMigrSeparate[i]) {
						try {
							if (Double.parseDouble(txtMigrSeparate[i].getText().replace(',','.')) > 1) {
								lblMigRSeparate[i].setText("1");
								txtMigrSeparate[i].setText("");
							} else if (Double.parseDouble(txtMigrSeparate[i].getText().replace(',','.')) < 0) {
								lblMigRSeparate[i].setText("0");
								txtMigrSeparate[i].setText("");
							} else {
								lblMigRSeparate[i].setText(txtMigrSeparate[i].getText().replace(',', '.'));
								txtMigrSeparate[i].setText("");
							}							
							model.setMigrationRate(i,Double.parseDouble(lblMigRSeparate[i].getText()));
							lblMigR.setText("Sep.");
						} catch (NumberFormatException exception) {
							lblMigRSeparate[i].setText("Invalid!");
						}
						break;
					}
				}                    
			}
		};

		for (int i = 0; i < lblMigRSeparate.length; i++) {			
			lblNames[i].setBounds(10, 3+40*i, 300, 14);
			migrationWindow.getContentPane().add(lblNames[i]);

			lblMigRSeparate[i].setBounds(72, 19+i*40, 45, 14);
			migrationWindow.getContentPane().add(lblMigRSeparate[i]);

			txtMigrSeparate[i].setBounds(10, 17+i*40, 46, 20);
			txtMigrSeparate[i].addActionListener(listener);			
			migrationWindow.getContentPane().add(txtMigrSeparate[i]);		
		}
	}

	/**
	 * Generates a separate window to set the mature stage
	 * @param nSpecies int Number of species
	 */

	public void setSeparateMatureStage(int nSpecies) {
		JLabel[] lblNames = new JLabel[this.lblNames.length];
		for (int i = 0; i < lblNames.length; i++) {
			lblNames[i] = new JLabel(this.lblNames[i].getText());
		}		
		matureStageWindow = new JFrame();
		matureStageWindow.getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		matureStageWindow.setIconImage(Toolkit.getDefaultToolkit().getImage(Application.class.getResource("/icon.png")));
		int windowHeight = 10 + nSpecies * 60 ;
		matureStageWindow.setBounds(100, 100, 365, windowHeight);
		matureStageWindow.setTitle("Mature Stage By Species");
		matureStageWindow.setResizable(false);
		matureStageWindow.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		matureStageWindow.getContentPane().setLayout(null);

		ActionListener listener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (int i = 0; i < model.getNSpecies(); i++ ){
					if (e.getSource() == txtMatstageSeparate[i]) {
						try {					
							if (lblNStages.getText().equals("Sep.")) {
								if (Integer.parseInt(txtMatstageSeparate[i].getText()) > Integer.parseInt(lblNStagesSeparate[i].getText())) {
									txtMatstageSeparate[i].setText(lblNStagesSeparate[i].getText());
								} 
							} else {
								if (Integer.parseInt(txtMatstageSeparate[i].getText()) > Integer.parseInt(lblNStages.getText())) {
									txtMatstageSeparate[i].setText(lblNStages.getText());
								} 
							}

							if (Integer.parseInt(txtMatstageSeparate[i].getText()) < 0 ) {
								lblMatStageSeparate[i].setText("0");
								txtMatstageSeparate[i].setText("");
							} else {
								lblMatStageSeparate[i].setText(txtMatstageSeparate[i].getText());
								txtMatstageSeparate[i].setText("");
							}

							model.setMatureStage(i,Integer.parseInt(lblMatStageSeparate[i].getText()));	
							lblMatStage.setText("Sep.");
						} catch (NumberFormatException exception) {
							txtMatstageSeparate[i].setText("Invalid!");
						}
						break;
					}
				}                    
			}
		};

		for (int i = 0; i < lblMatStageSeparate.length; i++) {			
			lblNames[i].setBounds(10, 3+40*i, 300, 14);
			matureStageWindow.getContentPane().add(lblNames[i]);

			lblMatStageSeparate[i].setBounds(72, 19+i*40, 45, 14);
			matureStageWindow.getContentPane().add(lblMatStageSeparate[i]);

			txtMatstageSeparate[i].setBounds(10, 17+i*40, 46, 20);
			txtMatstageSeparate[i].addActionListener(listener);			
			matureStageWindow.getContentPane().add(txtMatstageSeparate[i]);		
		}
	}

	/**
	 * Generates a separate window to set litter size
	 * @param nSpecies int Number of species 
	 */

	public void setSeparateLitterSize(int nSpecies) {
		JLabel[] lblNames = new JLabel[this.lblNames.length];
		for (int i = 0; i < lblNames.length; i++) {
			lblNames[i] = new JLabel(this.lblNames[i].getText());
		}		
		litterSizeWindow = new JFrame();
		litterSizeWindow.getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		litterSizeWindow.setIconImage(Toolkit.getDefaultToolkit().getImage(Application.class.getResource("/icon.png")));
		int windowHeight = 10 + nSpecies * 60 ;
		litterSizeWindow.setBounds(100, 100, 365, windowHeight);
		litterSizeWindow.setTitle("Litter Size By Species");
		litterSizeWindow.setResizable(false);
		litterSizeWindow.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		litterSizeWindow.getContentPane().setLayout(null);

		ActionListener listener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (int i = 0; i < model.getNSpecies(); i++ ){
					if (e.getSource() == txtLitterSizeSeparate[i]) {
						try {						
							if (Integer.parseInt(txtLitterSizeSeparate[i].getText()) < 1) {
								lblLitsizeSeparate[i].setText("1");
								txtLitterSizeSeparate[i].setText("");
							} else {
								lblLitsizeSeparate[i].setText(txtLitterSizeSeparate[i].getText());
								txtLitterSizeSeparate[i].setText("");
							}
							model.setMatureStage(i,Integer.parseInt(lblLitsizeSeparate[i].getText()));		
							lblLitsize.setText("Sep.");
						} catch (NumberFormatException exception) {
							txtLitterSizeSeparate[i].setText("Invalid!");
						}
						break;
					}
				}                    
			}
		};

		for (int i = 0; i < lblLitsizeSeparate.length; i++) {			
			lblNames[i].setBounds(10, 3+40*i, 300, 14);
			litterSizeWindow.getContentPane().add(lblNames[i]);

			lblLitsizeSeparate[i].setBounds(72, 19+i*40, 45, 14);
			litterSizeWindow.getContentPane().add(lblLitsizeSeparate[i]);

			txtLitterSizeSeparate[i].setBounds(10, 17+i*40, 46, 20);
			txtLitterSizeSeparate[i].addActionListener(listener);			
			litterSizeWindow.getContentPane().add(txtLitterSizeSeparate[i]);		
		}
	}


	/**
	 * Generates a separate window to set the stage duration
	 * @param nSpecies int Number of species
	 */

	public void setSeparateStageDuration(int nSpecies) {
		JLabel[] lblNames = new JLabel[this.lblNames.length];
		for (int i = 0; i < lblNames.length; i++) {
			lblNames[i] = new JLabel(this.lblNames[i].getText());
		}		
		stageDurationWindow = new JFrame();
		stageDurationWindow.getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		stageDurationWindow.setIconImage(Toolkit.getDefaultToolkit().getImage(Application.class.getResource("/icon.png")));
		int windowHeight = 10 + nSpecies * 60 ;
		stageDurationWindow.setBounds(100, 100, 365, windowHeight);
		stageDurationWindow.setTitle("Stage Duration By Species");
		stageDurationWindow.setResizable(false);
		stageDurationWindow.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		stageDurationWindow.getContentPane().setLayout(null);

		ActionListener listener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (int i = 0; i < model.getNSpecies(); i++ ){
					if (e.getSource() == txtStagedurationSeparate[i]) {
						try {						
							if (Integer.parseInt(txtStagedurationSeparate[i].getText()) < 1) {
								lblStdurationSeparate[i].setText("1");
								txtStagedurationSeparate[i].setText("");
							} else {
								lblStdurationSeparate[i].setText(txtStagedurationSeparate[i].getText());
								txtStagedurationSeparate[i].setText("");
							}
							model.setStageDuration(i,Integer.parseInt(lblStdurationSeparate[i].getText()));	
							lblStduration.setText("Sep.");
						} catch (NumberFormatException exception) {
							txtStagedurationSeparate[i].setText("Invalid!");
						}
						break;
					}
				}                    
			}
		};

		for (int i = 0; i < lblStdurationSeparate.length; i++) {			
			lblNames[i].setBounds(10, 3+40*i, 300, 14);
			stageDurationWindow.getContentPane().add(lblNames[i]);

			lblStdurationSeparate[i].setBounds(72, 19+i*40, 45, 14);
			stageDurationWindow.getContentPane().add(lblStdurationSeparate[i]);

			txtStagedurationSeparate[i].setBounds(10, 17+i*40, 46, 20);
			txtStagedurationSeparate[i].addActionListener(listener);			
			stageDurationWindow.getContentPane().add(txtStagedurationSeparate[i]);		
		}
	}

	/**
	 * Generates a separate window to set the dispersal range
	 * @param nSpecies int Number of species
	 */

	public void setSeparateDispersalRange(int nSpecies) {
		JLabel[] lblNames = new JLabel[this.lblNames.length];
		for (int i = 0; i < lblNames.length; i++) {
			lblNames[i] = new JLabel(this.lblNames[i].getText());
		}				
		dispersalRangeWindow = new JFrame();
		dispersalRangeWindow.getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		dispersalRangeWindow.setIconImage(Toolkit.getDefaultToolkit().getImage(Application.class.getResource("/icon.png")));
		int windowHeight = 10 + nSpecies * 60 ;
		dispersalRangeWindow.setBounds(100, 100, 365, windowHeight);
		dispersalRangeWindow.setTitle("Dispersal Range By Species");
		dispersalRangeWindow.setResizable(false);
		dispersalRangeWindow.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		dispersalRangeWindow.getContentPane().setLayout(null);

		ActionListener listener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (int i = 0; i < model.getNSpecies(); i++ ){
					if (e.getSource() == txtDispersalRangeSeparate[i]) {
						try {						
							if (Double.parseDouble(txtDispersalRangeSeparate[i].getText().replace(',','.')) < 0) {
								lblDisranSeparate[i].setText("0.0");
								txtDispersalRangeSeparate[i].setText("");
							} else {
								lblDisranSeparate[i].setText(txtDispersalRangeSeparate[i].getText().replace(',','.'));
								txtDispersalRangeSeparate[i].setText("");
							}
							model.setDispersalRange(i,Double.parseDouble(lblDisranSeparate[i].getText()));	
							lblDisran.setText("Sep.");
						} catch (NumberFormatException exception) {
							lblDisranSeparate[i].setText("Invalid!");
						}
						break;
					}
				}                    
			}
		};

		for (int i = 0; i < lblDisranSeparate.length; i++) {			
			lblNames[i].setBounds(10, 3+40*i, 300, 14);
			dispersalRangeWindow.getContentPane().add(lblNames[i]);

			lblDisranSeparate[i].setBounds(72, 19+i*40, 45, 14);
			dispersalRangeWindow.getContentPane().add(lblDisranSeparate[i]);

			txtDispersalRangeSeparate[i].setBounds(10, 17+i*40, 46, 20);
			txtDispersalRangeSeparate[i].addActionListener(listener);			
			dispersalRangeWindow.getContentPane().add(txtDispersalRangeSeparate[i]);		
		}
	}

	/**
	 * Generates a separate window to set if species swim or fly
	 * @param nSpecies int Number of species
	 */

	public void setSeparateSwimFly(int nSpecies) {
		JLabel[] lblNames = new JLabel[this.lblNames.length];
		for (int i = 0; i < lblNames.length; i++) {
			lblNames[i] = new JLabel(this.lblNames[i].getText());
		}		
		swimFlyWindow = new JFrame();
		swimFlyWindow.getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		swimFlyWindow.setIconImage(Toolkit.getDefaultToolkit().getImage(Application.class.getResource("/icon.png")));
		int windowHeight = 10 + nSpecies * 60 ;
		swimFlyWindow.setBounds(100, 100, 365, windowHeight);
		swimFlyWindow.setTitle("Swim/Fly By Species");
		swimFlyWindow.setResizable(false);
		swimFlyWindow.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		swimFlyWindow.getContentPane().setLayout(null);

		ActionListener listener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (int i = 0; i < model.getNSpecies(); i++ ){
					if (e.getSource() == btnSwimflySeparate[i]) {
						if (btnSwimflySeparate[i].getText().equals("Yes")) {
							btnSwimflySeparate[i].setText("No");
							model.setFlyswim(i, false);
							btnSwimfly.setText("Sep.");
						} else {
							btnSwimflySeparate[i].setText("Yes");
							model.setFlyswim(i, true);
							btnSwimfly.setText("Sep.");
						}
						break;
					}
				}                    
			}
		};

		for (int i = 0; i < btnSwimflySeparate.length; i++) {			
			lblNames[i].setBounds(10, 3+40*i, 300, 14);
			swimFlyWindow.getContentPane().add(lblNames[i]);

			btnSwimflySeparate[i].setBounds(10, 17+40*i, 55, 23);
			btnSwimflySeparate[i].addActionListener(listener);
			swimFlyWindow.getContentPane().add(btnSwimflySeparate[i]);

		}
	}

	/**
	 * Generates a separate window to set the number of stages
	 * @param nSpecies int Number of species
	 */

	public void setSeparateNumberOfStages(int nSpecies) {
		JLabel[] lblNames = new JLabel[this.lblNames.length];
		for (int i = 0; i < lblNames.length; i++) {
			lblNames[i] = new JLabel(this.lblNames[i].getText());
		}			
		nStageWindow = new JFrame();
		nStageWindow.getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		nStageWindow.setIconImage(Toolkit.getDefaultToolkit().getImage(Application.class.getResource("/icon.png")));
		int windowHeight = 10 + nSpecies * 60 ;
		nStageWindow.setBounds(100, 100, 365, windowHeight);
		nStageWindow.setTitle("Mature Stage By Species");
		nStageWindow.setResizable(false);
		nStageWindow.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		nStageWindow.getContentPane().setLayout(null);

		ActionListener listener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (int i = 0; i < model.getNSpecies(); i++ ){
					if (e.getSource() == txtNstagesSeparate[i]) {
						try {	
							if (Integer.parseInt(txtNstagesSeparate[i].getText()) < 1 ) {								
								lblNStagesSeparate[i].setText("1");
								txtNstagesSeparate[i].setText("");
							} else {
								lblNStagesSeparate[i].setText(txtNstagesSeparate[i].getText());
								txtNstagesSeparate[i].setText("");
							}

							if (lblMatStage.getText().equals("Sep.")) {							
								if (Integer.parseInt(lblNStagesSeparate[i].getText()) < Integer.parseInt(lblMatStageSeparate[i].getText())) {
									lblMatStageSeparate[i].setText(txtNstagesSeparate[i].getText());		
								}
							} else {
								if (Integer.parseInt(lblNStagesSeparate[i].getText()) < Integer.parseInt(lblMatStage.getText())) {
									lblMatStage.setText(txtNstagesSeparate[i].getText());									
								}
							}


							model.setNumberOfStages(i, Integer.parseInt(lblNStagesSeparate[i].getText()), Integer.parseInt(lblInitPop.getText()));

							System.gc();

							lblNStages.setText("Sep.");


						} catch (NumberFormatException exception) {
							txtNstagesSeparate[i].setText("Invalid!");
						}
						break;
					}
				}                    
			}
		};

		for (int i = 0; i < lblMatStageSeparate.length; i++) {			
			lblNames[i].setBounds(10, 3+40*i, 300, 14);
			nStageWindow.getContentPane().add(lblNames[i]);

			lblNStagesSeparate[i].setBounds(72, 19+i*40, 45, 14);
			nStageWindow.getContentPane().add(lblNStagesSeparate[i]);

			txtNstagesSeparate[i].setBounds(10, 17+i*40, 46, 20);
			txtNstagesSeparate[i].addActionListener(listener);			
			nStageWindow.getContentPane().add(txtNstagesSeparate[i]);		
		}
	}
}

