package model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;



/**
 * Models species growth and dispersal.
 *
 * @author  Tomé Neves
 */

public class Model {

	
	private Map[][] pop; //Population
	private final Map[][] env; //Environment - [a][b] a - Variable, b- month
	private final int[] matSta; //Stage species hit maturity (0 = Newborn)
	private final double[] migR; //Migration rate (Between 0-1, probability of individuals leaving their cell) 
	private int nCores; // Number of CPU Cores to use
	private final int[] staDur; //Duration of each stage;
	private final int[] nStages; // Number of stages
	private final int[] litSize; //Maximum litter size
	private final double[] disRan; //Maximum range of dispersal
	private final boolean[] swimFly; //true if species swims or flies
	private final int cycleLength; //length of each variable cycle
	private int maxPopulation; //The maximum number of individuals that can be present in a single cell

	private Double[] leftOverMov; //Amount of movement left over from previous step for each species 

	private int currentStep;	//Number of steps the model has ran

	private final Semaphore[][] mutex; //To sync the various threads

	private final Method[][] equations; //Equations that solve birth rate and mortality rate for each species - [0][species] Birth rate - [1][species] Mortality rate
	private final String[][] equationsBackup; //The string version of the equations to print if required - [0][species] Birth rate - [1][species] Mortality rate


	/**
	 * Constructor of the model Class
	 *
	 * @param initialPopulation Map The initial location of the population
	 * @param nStages int Number of stages the hierarchy has
	 * @param startPopValue int Initial number of individuals in each cell where they are present
	 * @param maxPopulation int The maximum number of individuals that can be present in a single cell
	 * @param matureStage int Stage at which species mature
	 * @param migrationRate double Rate at which species migrate 
	 * @param dispersalRange double Maximum distance in km that the species can cover in one step
	 * @param environment Map[][] Array of maps with the environmental variables for each month, [a][b] a - Variable, b- month
	 * @param equations String[][] String with the birth rate equations in [0][x] and mortality rate in [1][x] for each species.
	 *  <br /> Write the equation as java code. The variables names to use in the equations are: 
	 *  <br /> v[1],v[2],v[3],... = Environmental variables in the order they appear on the array
	 *  <br /> s[1],s[2],s[3],... = Size of the population of each species in the order they appear on the array
	 *  <br /> popSize = Current total population size in cell
	 *  <br /> Example: if(v[1] > 6) {br=0.5} else {br=s[1]*6 + popSize} 
	 *  @param nCores int Number of CPU cores to use
	 */

	public Model(Map[] initialPopulation, int[] nStages, int[] stageDuration,int startPopValue, int maxPopulation, int[] matureStage, int[] litterSize, double[] migrationRate, double[] dispersalRange, boolean[] swimOrFly, Map[][] environment, String[][] equations, int nCores) throws Exception{

		env = environment;
		currentStep = 0;
		migR = migrationRate;
		matSta = matureStage;
		staDur = stageDuration;
		this.nCores = nCores;
		this.nStages = nStages;
		litSize = litterSize;
		disRan = dispersalRange;
		swimFly = swimOrFly;
		equationsBackup = equations;
		cycleLength = environment[0].length;
		this.maxPopulation = maxPopulation;

		//Initialize left over movement with 0
		leftOverMov = new Double[initialPopulation.length];
		for (int i = 0; i < initialPopulation.length; i++){
			leftOverMov[i] = 0.0;
		}

		//Initialize population (Multithreaded)
		int nStagesMax = 0;
		for (int i = 0; i <nStages.length; i++) {
			if (nStagesMax < nStages[i]) {
				nStagesMax = nStages[i];
			}
		}

		pop = new Map[initialPopulation.length][nStagesMax];
		for (int species = 0; species < pop.length; species ++) {
			for (int stage = 0; stage < pop[species].length; stage ++) {
				pop[species][stage] = initialPopulation[species].clone();
			}
		}

		List<processInitPop> workersIP = new ArrayList<processInitPop>();

		for (int core = 0; core < nCores; core++) {
			workersIP.add(new processInitPop(core,startPopValue));
		}
		for (processInitPop worker : workersIP)   {
			while (worker.running)   {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		//Initialize semaphore
		//gets longest columns and rows
		int hCol=0;
		int hRow=0;
		for (int species = 0; species < pop.length; species ++) {
			if (hCol < pop[species][0].getNcols()) {
				hCol = pop[species][0].getNcols();
			}

			if (hRow <pop[species][0].getNrows()) {
				hRow = pop[species][0].getNrows(); 
			}
		}

		mutex =  new Semaphore[hCol][hRow];
		for (int x = 0; x < mutex.length; x ++ ) {
			for (int y = 0; y < mutex[x].length; y ++) {
				mutex[x][y] = new Semaphore(1, true);
			}
		}

		//Compile methods
		this.equations = new Method[2][pop.length];
		CodeCompiler comp = new CodeCompiler();
		for (int species = 0; species < pop.length; species ++ ) {
			this.equations[0][species] = comp.compile( equations[0][species], "br");			
			this.equations[1][species] = comp.compile( equations[1][species], "mr");
		}
	}

	/**
	 * Constructor that loads a previously existing model
	 * 
	 * @param currentStep Step at which the model is currently at
	 * @param maxPopulation int The maximum number of individuals that can be present in a single cell
	 * @param initialPopulation Map The initial location of the population
	 * @param nStages int Number of stages the hierarchy has
	 * @param matureStage int Stage at which species mature
	 * @param migrationRate double Rate at which species migrate 
	 * @param dispersalRange double Maximum distance in km that the species can cover in one step
	 * @param environment Map[][] Array of maps with the environmental variables for each month, [a][b] a - Variable, b- month
	 * @param equations String[][] String with the birth rate equations in [0][x] and mortality rate in [1][x] for each species.
	 *  <br /> Write the equation as java code. The variables names to use in the equations are: 
	 *  <br /> v[1],v[2],v[3],... = Environmental variables in the order they appear on the array
	 *  <br /> s[1],s[2],s[3],... = Size of the population of each species in the order they appear on the array
	 *  <br /> popSize = Current total population size in cell
	 *  <br /> Example: if(v[1] > 6) {br=0.5} else {br=s[1]*6 + popSize} 
	 *  @param nCores int Number of CPU cores to use
	 */


	public Model(int currentStep,  int maxPopulation, Map[][] initialPopulation, int[] nStages, int[] stageDuration,int[] matureStage, int[] litterSize, double[] migrationRate, double[] dispersalRange, boolean[] swimOrFly, Map[][] environment,String[][] equations, int nCores) throws Exception{
		env = environment;
		this.currentStep = currentStep;
		migR = migrationRate;
		matSta = matureStage;
		staDur = stageDuration;
		this.nCores = nCores;
		this.nStages = nStages;
		litSize = litterSize;
		disRan = dispersalRange;
		swimFly = swimOrFly;
		equationsBackup = equations;
		cycleLength = environment[0].length;
		this.maxPopulation = maxPopulation;

		//Initialize population
		pop = initialPopulation;


		//Initialize left over movement with 0
		leftOverMov = new Double[initialPopulation.length];
		for (int i = 0; i < initialPopulation.length; i++){
			leftOverMov[i] = 0.0;
		}


		//Initialize semaphore
		//gets longest columns and rows
		int hCol=0;
		int hRow=0;
		for (int species = 0; species < pop.length; species ++) {
			if (hCol < pop[species][0].getNcols()) {
				hCol = pop[species][0].getNcols();
			}

			if (hRow <pop[species][0].getNrows()) {
				hRow = pop[species][0].getNrows(); 
			}
		}

		mutex =  new Semaphore[hCol][hRow];
		for (int x = 0; x < mutex.length; x ++ ) {
			for (int y = 0; y < mutex[x].length; y ++) {
				mutex[x][y] = new Semaphore(1, true);
			}
		}

		//Compile methods
		this.equations = new Method[2][pop.length];
		CodeCompiler comp = new CodeCompiler();
		for (int species = 0; species < pop.length; species ++ ) {
			this.equations[0][species] = comp.compile( equations[0][species], "br");			
			this.equations[1][species] = comp.compile( equations[1][species], "mr");
		}
	}

	/**
	 * Returns the stage at which a species is able to reproduce
	 * @param species int The species
	 * @return int The stage at which species are able to reproduce
	 */
	public int getMatureStage(int species) {
		return matSta[species];
	}

	/**
	 * Returns the migration rate of a species
	 * @param species int The species
	 *
	 * @return double The migration rate
	 */
	public double getMigrationRate(int species) {
		return migR[species];
	}

	/**
	 * Returns the duration of each stage of a species
	 * @param species int The species
	 * @return int The stage's duration
	 */
	public int getStageDuration(int species) {
		return staDur[species];
	}

	/**
	 * Returns the number of stages of a species
	 * @param species int The species
	 * @return int The number of stages
	 */
	public int getNumberStages(int species) {
		return nStages[species];
	}

	/**
	 * Returns the maximum litter size of a species
	 * @param species int The species
	 * @return int The maximum litter size
	 */
	public int getLitterSize(int species) {
		return litSize[species];
	}

	/**
	 * Returns the maximum dispersal range in km of a species
	 * @param species int The species
	 * @return double The maximum dispersal range (km)
	 */
	public double getDispersalRange(int species) {
		return disRan[species];
	}

	/**
	 * Returns the names of the species present
	 *
	 * @return String[] Array with the names of the species listed
	 */
	public String[] getSpeciesNames() {
		String[] speciesNames = new String[pop.length];
		for (int i = 0 ; i < pop.length; i++) {
			speciesNames[i] = pop[i][0].getName();
		}
		return speciesNames;
	}

	/**
	 * Returns true if the the species swims or flies, false otherwise
	 * @param species int The species
	 * @return boolean True if the species swims or flies, false otherwise
	 */
	public boolean getSwimFly(int species) {
		return swimFly[species];
	}

	/**
	 * Returns an array with the equations for each species [a][b] - a=0 birth and b=1 mortality, b = the species number 
	 * 
	 * @return String[][] The equations for each species [a][b] - a=0 birth and b=1 mortality, b = the species number 
	 */

	public String[][] getEquations() {
		return equationsBackup;
	}

	/**
	 * Returns an array the names of the first month of each variable
	 * 
	 * @return String[] The name of the first month of each variable
	 */

	public String[] getVariableNames() {
		String[] variableNames = new String[env.length];
		for (int i = 0 ; i < env.length; i++) {
			variableNames[i] = env[i][0].getName();
		}		
		return variableNames;
	}

	/**
	 *  Makes the model perform one or more steps.
	 * 
	 * @param nSteps int Number of steps to perform
	 * @throws ScriptException 
	 */

	public void run(int nSteps) {

		Map [][] previousPop;
		for (int step = 0; step < nSteps; step++) {
			currentStep ++;
			//	System.out.println("Current Step:" + currentStep);


			//Backs up Population so that synchronization can be simultaneous.
			previousPop = new Map[pop.length][pop[0].length];
			for (int species = 0; species < pop.length; species ++) {
				for (int stage = 0; stage < nStages[species]; stage ++) { 
					previousPop[species][stage] = pop[species][stage].clone();
				}
			}

			//Randomizes the lines for each core
			@SuppressWarnings("unchecked")
			ArrayList<Integer>[] randOrder = (ArrayList<Integer>[])new ArrayList[previousPop.length];
			for(int species = 0; species < previousPop.length; species ++) {
				randOrder[species] = new ArrayList<Integer>();
				for(int x = 0; x < previousPop[species][0].getNcols(); x++) {
					randOrder[species].add(x);
				}
			}	
			for(int species = 0; species < previousPop.length; species ++) {
				Collections.shuffle(randOrder[species]);
			}

			//Runs the class that deals with births and deaths
			List<processBirthsAndDeaths> workersBD = new ArrayList<processBirthsAndDeaths>();
			for (int core = 0; core < nCores; core++) {
				workersBD.add(new processBirthsAndDeaths(core,previousPop, randOrder));
			}
			for (processBirthsAndDeaths worker : workersBD)   {
				while (worker.running)   {
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}


			//Backs up Population so that synchronization can be simultaneous.
			previousPop = new Map[pop.length][pop[0].length];
			for (int species = 0; species < pop.length; species ++) {
				for (int stage = 0; stage < nStages[species]; stage ++) { 
					previousPop[species][stage] = pop[species][stage].clone();
				}
			}

			//Calculates how far the individuals of each species will disperse
			Integer[] actualDispersalRange = new Integer[pop.length];
			for (int i = 0; i < pop.length; i++){
				actualDispersalRange[i] = 0;
			}
			double tempDisRan;
			double cellDistance;

			for (int species = 0; species < pop.length; species ++) {

				//How much the species will travel this step depends on how much was left from the previous ones
				tempDisRan = disRan[species] + leftOverMov[species];

				// 111km*degrees = approximately the distance in km of the degrees
				cellDistance = 111*previousPop[species][0].getCellSize();	

				while (cellDistance < tempDisRan) {
					tempDisRan -= cellDistance;
					actualDispersalRange[species] ++;
				}

				leftOverMov[species] = tempDisRan;
			}

			//Runs the class that deals with migration
			List<processMigrations> workersM = new ArrayList<processMigrations>();
			for (int core = 0; core < nCores; core++) {
				workersM.add(new processMigrations(core,previousPop,actualDispersalRange, randOrder));
			}
			for (processMigrations worker : workersM)   {
				while (worker.running)   {
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

			for (int species = 0; species < previousPop.length; species ++) {
				//If the appropriate number of steps have been done 
				if (currentStep%staDur[species] == 0) {

					//Climbs the individuals up a stage
					for (int stage = nStages[species]-1; stage > 0; stage --) { 
						pop[species][stage]=pop[species][stage-1].clone();
					}
				}

				//Runs the class that empties stage 0
				List<processEmptying> workersE = new ArrayList<processEmptying>();
				for (int core = 0; core < nCores; core++) {
					workersE.add(new processEmptying(core,species,randOrder));
				}
				for (processEmptying worker : workersE)   {
					while (worker.running)   {
						try {
							Thread.sleep(1);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}

	}



	/** 
	 * Paints the chosen rank of the chosen species 
	 * 
	 * @param speciesNumber int Number of the species to paint (based on the order in the array)
	 * @param rank int Rank of the species to print
	 * @param scaler double How much to change the size of the picture by
	 */

	public void paintSpecies(int speciesNumber, int rank, double scaler ) {
		Painter newPainter = new Painter();
		newPainter.setScaler(scaler);
		newPainter.paintMap(pop[speciesNumber][rank]);		
	}


	/**
	 * Returns the model's current step
	 * 
	 * @return int The model's current step
	 */
	public int getCurrentStep() {		
		return currentStep;	
	}

	/**
	 * Saves the .ascs of each species and rank, as well as the parameters, to a folder named with the current step
	 * 
	 * @param folder File Folder to save the .asc to
	 * @param all Boolean True if all stages are to be saved, false if only the compiled one
	 */

	public void saveSpeciesToFile(File folder, boolean all) {
		String pathNameBase;
		if (all) {
			pathNameBase = folder.getAbsolutePath()+System.getProperty("file.separator")+"Current Step "+currentStep+System.getProperty("file.separator");
		} else {
			pathNameBase = folder.getAbsolutePath()+System.getProperty("file.separator")+"Compiled Only - Current Step "+currentStep+System.getProperty("file.separator");
		}
		String pathName;
		Path path = Paths.get(pathNameBase);
		int repeats = 0;
		while (Files.exists(path)) {
			repeats++;
			pathNameBase = folder.getAbsolutePath()+System.getProperty("file.separator")+"Current Step "+currentStep+" ("+repeats+")"+System.getProperty("file.separator");
			path = Paths.get(pathNameBase);
		}
		try {
			Files.createDirectories(path);
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (all) {
			String stageFormatted;
			for	(int species = 0; species <pop.length; species ++) {
				for (int stage = 0; stage < nStages[species]; stage ++) {
					stageFormatted = String.format("%03d", stage); 
					pathName = pathNameBase + pop[species][stage].getName()+"_Stage " + stageFormatted +".asc";
					pop[species][stage].saveToFile(pathName);
				}
			}
		}
		for	(int speciesNumber = 0; speciesNumber <pop.length; speciesNumber ++) {
			Map compiledMap = new Map (pop[speciesNumber][0].getName(), pop[speciesNumber][0].getNcols(), pop[speciesNumber][0].getNrows(), pop[speciesNumber][0].getXcorner(), pop[speciesNumber][0].getYcorner(), pop[speciesNumber][0].getCellSize(), pop[speciesNumber][0].getNoDataValue() );
			double summedValues;
			for(int x = 0; x < pop[speciesNumber][0].getNcols(); x++) {
				for(int y = 0; y < pop[speciesNumber][0].getNrows(); y++) {	
					if (pop[speciesNumber][0].getValue(x,y)!= pop[speciesNumber][0].getNoDataValue()) {
						summedValues = 0;
						for(int stage = 0; stage < nStages[speciesNumber]; stage ++ ) {
							summedValues += pop[speciesNumber][stage].getValue(x,y);
						}				
						compiledMap.setValue(summedValues, x, y);			
					}
				}
			}
			pathName = pathNameBase + compiledMap.getName()+"_CompiledStages.asc";
			compiledMap.saveToFile(pathName);
		}

		try {
			BufferedWriter paramTxt = new BufferedWriter(new FileWriter(new File(pathNameBase + "Parameters.txt")));
			paramTxt.write("Maximum population:\r\n" + maxPopulation +"\r\n");
			paramTxt.write("Number of species:\r\n" + pop.length +"\r\n");
			for (int species = 0; species < pop.length; species++) {
				paramTxt.write(pop[species][0].getName() +"\r\n");
			}

			paramTxt.write("Number of stages:\r\n");
			for (int species = 0; species < pop.length; species++) {
				paramTxt.write(nStages[species] +"\r\n");
			}
			paramTxt.write("Mature stage:\r\n");
			for (int species = 0; species < pop.length; species++) {
				paramTxt.write(matSta[species]+"\r\n");
			}
			paramTxt.write("Migration rate:\r\n");
			for (int species = 0; species < pop.length; species++) {
				paramTxt.write(migR[species]+"\r\n");
			}
			paramTxt.write("Stage duration:\r\n");
			for (int species = 0; species < pop.length; species++) {
				paramTxt.write(staDur[species]+"\r\n");
			}
			paramTxt.write("Maximum litter size:\r\n");
			for (int species = 0; species < pop.length; species++) {
				paramTxt.write(litSize[species]+"\r\n");
			}
			paramTxt.write("Dispersal range:\r\n");
			for (int species = 0; species < pop.length; species++) {
				paramTxt.write(disRan[species]+"\r\n");
			}
			paramTxt.write("Swims or flies:\r\n");
			for (int species = 0; species < pop.length; species++) {
				paramTxt.write(swimFly[species] + "\r\n");
			}
			paramTxt.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			BufferedWriter equationTxt = new BufferedWriter(new FileWriter(new File(pathNameBase + "Equations.txt")));

			for (int species = 0; species < pop.length; species++) {
				equationTxt.write(equationsBackup[0][species] +"\r\n");
				equationTxt.write(equationsBackup[1][species] +"\r\n\r\n");
			}

			equationTxt.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}


	/** 
	 * Paints the chosen species 
	 * 
	 * @param speciesNumber int Number of the species to paint (based on the order in the array)
	 * @param scaler double How much to change the size of the picture by
	 */

	public void paintSpecies(int speciesNumber, double scaler ) {		

		//Compiles all stages into one map
		Map compiledMap = new Map (pop[speciesNumber][0].getName(), pop[speciesNumber][0].getNcols(), pop[speciesNumber][0].getNrows(), pop[speciesNumber][0].getXcorner(), pop[speciesNumber][0].getYcorner(), pop[speciesNumber][0].getCellSize(), pop[speciesNumber][0].getNoDataValue() );
		double summedValues;
		for(int x = 0; x < pop[speciesNumber][0].getNcols(); x++) {
			for(int y = 0; y < pop[speciesNumber][0].getNrows(); y++) {	
				if (pop[speciesNumber][0].getValue(x,y)!= pop[speciesNumber][0].getNoDataValue()) {
					summedValues = 0;
					for(int stage = 0; stage < nStages[speciesNumber]; stage ++ ) {
						summedValues += pop[speciesNumber][stage].getValue(x,y);
					}				
					compiledMap.setValue(summedValues, x, y);			
				}
			}
		}
		Painter newPainter = new Painter();
		newPainter.setScaler(scaler);
		newPainter.paintMap(compiledMap,("Current Step: " + currentStep + " - " + compiledMap.getName()));		
	}

	/**
	 * Sets the number of CPU Cores to use
	 * @param nCores int Number of CPU cores to use
	 */

	public void setCores(int nCores) {
		this.nCores = nCores;		
	}

	/**
	 * Class that processes births and deaths in another thread
	 *
	 */

	private class processBirthsAndDeaths implements Runnable {
		public boolean running = true;
		int coreN;
		ArrayList<Integer>[] randOrder;
		Map[][] prevPop;
		
		processBirthsAndDeaths(int cn, Map [][] prevPop, ArrayList<Integer>[] randOrder) {
			coreN = cn;
			this.randOrder = randOrder;
			this.prevPop = prevPop; //So competition can be calculated based on the previous population and not on the changing one.
			
			Thread thread = new Thread(this);
			thread.start();				
		}
		public void run() {
			double mortalityR;
			double birthR;
			int newBorns;
			int deaths;
			int startCol;
			int endCol;
			Integer[] popSize = new Integer[pop.length];


			Random randomizer = new Random();
			Double[] speciesInCell = new Double[pop.length+1];
			Double[] variablesInCell = new Double[env.length+1];

			for (int species = 0; species < pop.length; species ++) {

				startCol = pop[species][0].getNcols()/nCores*coreN;
				endCol = startCol + pop[species][0].getNcols()/nCores;

				if (nCores == coreN+1) {
					endCol = pop[species][0].getNcols();
				}

				//Runs through all cells and applies birth/mortality rate		
				int x= 0;
				for (int z = startCol; z < endCol; z++) {
					x = randOrder[species].get(z);
					for(int y = 0; y < pop[species][0].getNrows(); y++) {						

						//Skips if out of bounds
						if (pop[species][0].getValue(x,y) == pop[species][0].getNoDataValue()) {
							continue;
						}

						for (int countSpecies = 0; countSpecies < pop.length; countSpecies ++) {	


							//Counts current population size of species
							popSize[countSpecies] = 0;
							for (int stage = 0; stage < nStages[countSpecies]; stage ++) {
								popSize[countSpecies] += (int) prevPop[countSpecies][stage].getValue(x, y);					
							}		

							speciesInCell[countSpecies+1] = (double) popSize[countSpecies];								

						}

						//Kills some population to keep limits (if there are any)
						if (maxPopulation != 0) {
							double toKill = 0;
							int newPopSize = popSize[species];
							while (newPopSize > maxPopulation) {
								newPopSize = (int)((double)newPopSize*0.9);
								toKill++;
							}
							if (newPopSize != popSize[species]) {
								for (int s = 0; s < nStages[species]; s++) {
									pop[species][s].setValue((int)((double)pop[species][s].getValue(x,y)*Math.pow(0.9,toKill)), x, y);
								}
							}
						}

						//Checks what month it is
						int	month = currentStep%cycleLength;
						if (month == 0) {
							month= cycleLength;
						}
						//Feeds the engine the climate values
						for( int climate = 0; climate < env.length; climate ++) { 
							//Gets the latitude/longitude of the x/y in the species and fetches the corresponding row/col in the variables
							variablesInCell[climate+1] = (double) env[climate][month-1].getValue(env[climate][month-1].getLonCol(pop[species][0].getLon(x)), env[climate][month-1].getLatRow(pop[species][0].getLat(y)));														
						}

						newBorns = 0;

						for (int stage = 0; stage < nStages[species]; stage ++) {

							//Randomly decides how many individuals die			
							try {
								//Calculates mortality rate from the compiled code
								mortalityR = (double) equations[1][species].invoke(null, new Object[]{speciesInCell, variablesInCell, popSize[species]});
								deaths = 0;
								for (int i = 0; i < pop[species][stage].getValue(x, y); i ++) {
									if (randomizer.nextDouble() < mortalityR) {
										deaths ++;
									}
								}								

								//Randomly decides how many individuals will be born (if mature stage)
								//Calculates birth rate from the compiled code
								birthR = (double) equations[0][species].invoke(null, new Object[]{speciesInCell, variablesInCell, popSize[species]});
								if (stage >= matSta[species]){
									for (int i = 0; i < pop[species][stage].getValue(x, y); i ++) {
										if (randomizer.nextDouble() < birthR) {
											newBorns += randomizer.nextInt(litSize[species])+1; 
										}
									}	
								}

								//Kills individuals
								pop[species][stage].setValue(pop[species][stage].getValue(x,y) - deaths, x, y);
							} catch (IllegalAccessException e) {
								e.printStackTrace();
							} catch (IllegalArgumentException e) {
								e.printStackTrace();
							} catch (InvocationTargetException e) {
								e.printStackTrace();
							}

						}

						//Adds newborns to stage 0
						pop[species][0].setValue(pop[species][0].getValue(x,y)+newBorns, x,y);
					}
				}						
			}
			running = false;

		}
	}

	/**
	 * Class that processes migrations in another thread
	 */

	private class processMigrations implements Runnable {
		public boolean running = true;
		int coreN;
		Map[][] previousPop;
		Integer[] migrants;
		Integer[] dispersalRange;
		ArrayList<Integer>[] randOrder;

		processMigrations(int cn, Map [][] prevPop, Integer[] actualDispersalRange, ArrayList<Integer>[] randOrder) {
			coreN = cn;
			previousPop = prevPop;
			dispersalRange = actualDispersalRange;
			this.randOrder = randOrder;
			Thread thread = new Thread(this);
			thread.start();				
		}
		public void run() {
			Random randomizer = new Random();
			int startCol;
			int endCol;
			List<Integer[]> availableCells;
			int totalInd;

			for (int species = 0; species < previousPop.length; species ++) {

				startCol = previousPop[species][0].getNcols()/nCores*coreN;
				endCol = startCol + previousPop[species][0].getNcols()/nCores;

				if (nCores == coreN+1) {
					endCol = previousPop[species][0].getNcols();
				}

				//If no migration occurs this step, skip it
				if (dispersalRange[species] == 0) {
					continue;
				}

				int x = 0;
				//Runs through cells again for migration
				for (int z = startCol; z < endCol; z++) {
					for(int y = 0; y < previousPop[species][0].getNrows(); y++) {
						x = randOrder[species].get(z);
						//Skip if no individuals present
						totalInd = 0;
						for (int stage = 0; stage < nStages[species]; stage ++) {								
							totalInd += previousPop[species][stage].getValue(x,y);
						}
						if (totalInd == 0) {
							continue;
						}

						availableCells = new ArrayList<Integer[]>();
						//Different behaviour if the species flies or swims as it can skip bodies of water
						if(swimFly[species]) {
							//Counts available cells
							for (int rx = -dispersalRange[species]; rx <= dispersalRange[species]; rx++) {
								for (int ry = -dispersalRange[species]+Math.abs(rx); ry <= dispersalRange[species]-Math.abs(rx); ry++) {
									if (y+ry < previousPop[species][0].getNrows() && y+ry > 0 && x+rx < previousPop[species][0].getNcols()&& x+rx > 0 && previousPop[species][0].getValue(x+rx, y+ry) != previousPop[species][0].getNoDataValue()) {
										availableCells.add(new Integer[] {(x+rx),(y+ry)}) ;
									}
								}
							}


						} else {
							//Runs through all cells the individuals can reach and adds the migrants and does it seperately for each direction, so it can stop if it hits water
							//Up/Right
							both:
								for (int rx = 0; rx <= dispersalRange[species]; rx++) {
									for (int ry = 1; ry <= dispersalRange[species]-Math.abs(rx); ry++) {
										//Checks if out of bounds
										if (y+ry < previousPop[species][0].getNrows() && y+ry > 0 && x+rx < previousPop[species][0].getNcols()&& x+rx > 0) {
											//Checks if water, if so, stops
											if ( previousPop[species][0].getValue(x+rx, y+ry) == previousPop[species][0].getNoDataValue()) {
												if (ry == 1) { // Hit water on the main line, so stops
													break both;
												}
												break; 
											}
											availableCells.add(new Integer[] {(x+rx),(y+ry)}) ;
										}
									}
								}

						//Up/left	
						both:
							for (int rx = -1; rx >= -dispersalRange[species]; rx--) {
								for (int ry = 0; ry <= dispersalRange[species]-Math.abs(rx); ry++) {
									//Checks if out of bounds
									if (y+ry < previousPop[species][0].getNrows() && y+ry > 0 && x+rx < previousPop[species][0].getNcols()&& x+rx > 0) {
										//Checks if water, if so, stops
										if ( previousPop[species][0].getValue(x+rx, y+ry) == previousPop[species][0].getNoDataValue()) {
											if (ry == 0) { // Hit water on the main line, so stops
												break both;
											}
											break; 
										}
										availableCells.add(new Integer[] {(x+rx),(y+ry)}) ;
									}
								}
							}

								//Down/left		
								both:
									for (int rx = 0; rx >= -dispersalRange[species]; rx--) {
										for (int ry = -1; ry >= -dispersalRange[species]+Math.abs(rx); ry--) {
											//Checks if out of bounds
											if (y+ry < previousPop[species][0].getNrows() && y+ry > 0 && x+rx < previousPop[species][0].getNcols()&& x+rx > 0) {
												//Checks if water, if so, stops
												if ( previousPop[species][0].getValue(x+rx, y+ry) == previousPop[species][0].getNoDataValue()) {
													if (ry == -1) { // Hit water on the main line, so stops
														break both;
													}
													break; 
												}
												availableCells.add(new Integer[] {(x+rx),(y+ry)}) ;
											} 
										}
									}
							//Down/right	
							both:
								for (int rx = 1; rx <= dispersalRange[species]; rx++) {
									for (int ry = 0; ry >= -dispersalRange[species]+Math.abs(rx); ry--) {
										//Checks if out of bounds
										if (y+ry < previousPop[species][0].getNrows() && y+ry > 0 && x+rx < previousPop[species][0].getNcols() && x+rx > 0) {
											//Checks if water, if so, stops
											if ( previousPop[species][0].getValue(x+rx, y+ry) == previousPop[species][0].getNoDataValue()) {
												if (ry == 0) { // Hit water on the main line, so stops
													break both;
												}
												break; 
											}
											availableCells.add(new Integer[] {(x+rx),(y+ry)}) ;
										}
									}
								}
						}


						for (int stage = 0; stage < nStages[species]; stage ++) {

							//Skips if out of bounds
							if (pop[species][0].getValue(x,y) == pop[species][0].getNoDataValue()) {
								continue;
							}

							//Skip if no individuals present
							if (previousPop[species][stage].getValue(x,y) == 0) {
								continue;
							}						


							//Skips it if is in a dead end
							if (availableCells.size() == 0) {
								continue;
							}

							//Randomly decides how many individuals migrate, as well as to where.
							migrants = new Integer[availableCells.size()];
							for (int i = 0; i < availableCells.size(); i++) {
								migrants[i]=0;	
							}

							for (int i = 0; i < previousPop[species][stage].getValue(x, y); i ++) {
								if (randomizer.nextDouble()<migR[species]) {
									migrants[randomizer.nextInt(availableCells.size())] ++;
								}
							}


							try{
								for ( int loc = 0; loc < availableCells.size(); loc++) {
									while (!mutex[availableCells.get(loc)[0]][availableCells.get(loc)[1]].tryAcquire(100, TimeUnit.MILLISECONDS)) {/*Thread waiting*/}	
									pop[species][stage].setValue((pop[species][stage].getValue(availableCells.get(loc)[0],availableCells.get(loc)[1])+migrants[loc]),availableCells.get(loc)[0],availableCells.get(loc)[1]);
									mutex[availableCells.get(loc)[0]][availableCells.get(loc)[1]].release();

									//Removes the ones that migrated
									//Waits for the space to be unlocked
									while (!mutex[x][y].tryAcquire(100, TimeUnit.MILLISECONDS)) {/*Thread waiting*/}	
									pop[species][stage].setValue((pop[species][stage].getValue(x,y) - migrants[loc]),x,y);
									mutex[x][y].release();
								}
							} catch (InterruptedException e) {
								System.out.println("Thread interrupted.");
							}

						}								
					}
				}
			}
			running = false;
		}
	}


	/**	 
	 * Class that empties stage 0 in another thread.
	 */

	private class processEmptying implements Runnable {
		public boolean running = true;
		int coreN;
		int species;
		ArrayList<Integer>[] randOrder;

		processEmptying(int cn, int species,  ArrayList<Integer>[] randOrder) {
			coreN = cn;
			this.species = species;
			this.randOrder = randOrder;
			Thread thread = new Thread(this);
			thread.start();				
		}
		public void run() {
			int startCol;
			int endCol;
			startCol = pop[species][0].getNcols()/nCores*coreN;
			endCol = startCol + pop[species][0].getNcols()/nCores;
			if (nCores == coreN+1) {
				endCol = pop[species][0].getNcols();
			}

			int x = 0;
			//Runs through cells again for emptying
			for (int z = startCol; z < endCol; z++) {
				for(int y = 0; y < pop[species][0].getNrows(); y++) {
					x = randOrder[species].get(z);
					//Skips if out of bounds
					if (pop[species][0].getValue(x,y) == pop[species][0].getNoDataValue()) {
						continue;
					}
					pop[species][0].setValue(0,x,y);
				}
			}
			running = false;
		}
	}

	/**	 
	 * Class that initializes the population in another thread
	 */

	private class processInitPop implements Runnable {
		public boolean running = true;
		int coreN;
		int startPopValue;

		processInitPop(int cn, int startPopValue) {
			coreN = cn;
			this.startPopValue = startPopValue;
			Thread thread = new Thread(this);
			thread.start();				
		}
		public void run() {
			Random randomizer = new Random();
			int startCol;
			int endCol;
			Integer[] indDist;
			int rand;
			for (int species = 0; species < pop.length; species ++) {

				startCol = pop[species][0].getNcols()/nCores*coreN;
				endCol = startCol + pop[species][0].getNcols()/nCores;
				if (nCores == coreN+1) {
					endCol = pop[species][0].getNcols();
				}

				//Runs through cells again for initializing
				for (int x = startCol; x < endCol; x++) {
					for(int y = 0; y < pop[species][0].getNrows(); y++) {
						//Skips if out of bounds or if no presences
						if (pop[species][0].getValue(x,y) == pop[species][0].getNoDataValue() || pop[species][0].getValue(x,y) == 0) {
							continue;
						}

						//Distribute the individuals randomly through all stages
						indDist = new Integer[pop[species].length];
						for (int i = 0; i < pop[species].length; i++) {
							indDist[i]=0;
						}
						for( int i = 0; i < startPopValue; i++) {
							rand = (int)(randomizer.nextDouble()*((double)(nStages[species])));
							indDist[rand] ++;								
						}


						for (int stage = 0; stage < pop[species].length; stage ++) {
							pop[species][stage].setValue(indDist[stage],x,y);
						}
					}
				}
			}
			running = false;
		}
	}

	/**
	 * Checks the validity of the given equations
	 *  
	 * @return boolean TRUE if valid, FALSE otherwise
	 */

	public boolean checkEquationValidity() {
		Double[] speciesInCell = new Double[pop.length+1];
		Double[] variablesInCell = new Double[env.length+1];

		for (int species = 0; species < pop.length+1; species++) {
			speciesInCell[species] = 1.0;
		}

		for (int environment = 0; environment < env.length+1; environment++) {
			variablesInCell[environment] = 1.0;
		}

		double testValue;
		try {
			for (int species = 0; species < pop.length; species++) {		
				testValue = (double) equations[1][species].invoke(null, new Object[]{speciesInCell, variablesInCell, 1});
				if (testValue == -123456789) {
					return false;
				}
				testValue = (double) equations[0][species].invoke(null, new Object[]{speciesInCell, variablesInCell, 1});
				if (testValue == -123456789) {
					return false;
				}
			}			
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			return false;			
		}
		return true;
	}
	/**
	 *  Returns the number of species
	 * @return int The number of species
	 */

	public int getNSpecies() {
		return pop.length;
	}

	/**
	 * Sets the migration rate of a certain species
	 * @param species int The Species
	 * @param migrationRate double The migration rate 
	 */

	public void setMigrationRate(int species, double migrationRate) {
		migR[species] = migrationRate; 
	}

	/**
	 * Sets the mature stage of a certain species
	 * @param species int The Species
	 * @param matureStage int The mature stage 
	 */

	public void setMatureStage(int species, int matureStage) {
		matSta[species] = matureStage;
	}

	/**
	 * Sets the dispersal range of a certain species
	 * @param species int The Species
	 * @param dispersalRange double The dispersal range (km) 
	 */

	public void setDispersalRange(int species, double dispersalRange) {
		disRan[species] = dispersalRange;
	}

	/**
	 * Sets the maximum litter size of a certain species
	 * @param species int The Species
	 * @param litterSize int The litter size 
	 */
	public void setLitterSize(int species, int litterSize) {
		litSize[species] = litterSize;
	}

	/**
	 * Sets if species fly/swim
	 * @param species int The Species
	 * @param Flyswim boolean TRUE if they swim/fly, FALSE otherwise 
	 */
	public void setFlyswim(int species, boolean flySwim) {
		swimFly[species] = flySwim;
	}

	/**
	 * Sets the number of stages for a species. Redistributes individuals of all species randomly through the stages and resets the number of individuals in each cell to given value.
	 * @param speciesN int The Species
	 * @param newNStages int The number of stages
	 * @param startPopValue int Number of individuals to create on each cell they are present
	 */

	public void setNumberOfStages(int speciesN, int newNStages, int startPopValue) {	
		nStages[speciesN] = newNStages;		
		//Initialize population (Multithreaded)
		int nStagesMax = 0;
		for (int i = 0; i <nStages.length; i++) {
			if (nStagesMax < nStages[i]) {
				nStagesMax = nStages[i];
			}
		}

		Map[] compiledMap = new Map[pop.length];
		for	(int speciesNumber = 0; speciesNumber <pop.length; speciesNumber ++) {
			compiledMap[speciesNumber] = new Map (pop[speciesNumber][0].getName(), pop[speciesNumber][0].getNcols(), pop[speciesNumber][0].getNrows(), pop[speciesNumber][0].getXcorner(), pop[speciesNumber][0].getYcorner(), pop[speciesNumber][0].getCellSize(), pop[speciesNumber][0].getNoDataValue() );
			double summedValues;
			for(int x = 0; x < pop[speciesNumber][0].getNcols(); x++) {
				for(int y = 0; y < pop[speciesNumber][0].getNrows(); y++) {	
					if (pop[speciesNumber][0].getValue(x,y)!= pop[speciesNumber][0].getNoDataValue()) {
						summedValues = 0;
						for(int stage = 0; stage < pop[speciesNumber].length; stage ++ ) {
							summedValues += pop[speciesNumber][stage].getValue(x,y);
						}				
						compiledMap[speciesNumber].setValue(summedValues, x, y);			
					}
				}
			}
		}


		Map[][] newPop = new Map[pop.length][nStagesMax];
		for (int species = 0; species < newPop.length; species ++) {
			for (int stage = 0; stage < nStagesMax; stage ++) {
				newPop[species][stage] = compiledMap[species].clone();
			}
		}

		pop = newPop;

		List<processInitPop> workersIP = new ArrayList<processInitPop>();

		for (int core = 0; core < nCores; core++) {
			workersIP.add(new processInitPop(core,startPopValue));
		}
		for (processInitPop worker : workersIP)   {
			while (worker.running)   {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		compiledMap = null;
		System.gc();
	}

	/**
	 * Sets if the stage duration of a species
	 * @param species int The Species
	 * @param stageDuration int The duration of the stage 
	 */

	public void setStageDuration(int species, int stageDuration) {
		staDur[species] = stageDuration;		
	}

	/**
	 * Sets the maximum population in a cell
	 * @param maxPopulation int The maximum number of individuals present in a cell
	 */

	public void setMaximumPopulation(int maxPopulation) {
		this.maxPopulation = maxPopulation;	
	}
}
