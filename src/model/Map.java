package model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Class that reads, stores and writes .asc (GRIDASCII) files.
 *
 * @author  Tomé Neves
 */

public class Map {

	private String name; //Name of the map
	private final int ncols; //Number of columns in the map
	private final int nrows; //Number of rows in the map
	private final double xllcorner; //Longitude of the leftmost column
	private final double yllcorner; //Latitude of topmost row
	private final double cellsize; //Size in degrees of each square
	private final double  NODATA_value; //Value when there is no data
	private double[][] map; //Holds the values of the map for x,y coordinates
	private double minValue; //Minimum value of the map
	private double maxValue; //Maximum value of the map
	private boolean minMaxInit = false; 

	/**
	 * Constructor that receives an .asc file location and transforms it into a two dimensional array to be read.
	 * 
	 * @param ascFile File Location of .asc file to be read.
	 */
	public Map(File ascFile) throws IOException {

		BufferedReader asc = new BufferedReader(new FileReader(ascFile));
		String temp;
		this.name = ascFile.getName().substring(0, ascFile.getName().length()-4);


		temp = asc.readLine();		
		//Clear spaces in the end
		while(temp.lastIndexOf(" ")==temp.length()-1) {
			temp = temp.substring(0,temp.length()-1);
		}
		this.ncols = Integer.valueOf(temp.substring(temp.lastIndexOf(" ")+1,temp.length()));

		temp = asc.readLine();
		//Clear spaces in the end
		while(temp.lastIndexOf(" ")==temp.length()-1) {
			temp = temp.substring(0,temp.length()-1);
		}
		this.nrows = Integer.valueOf(temp.substring(temp.lastIndexOf(" ")+1,temp.length()));

		temp = asc.readLine();
		//Clear spaces in the end
		while(temp.lastIndexOf(" ")==temp.length()-1) {
			temp = temp.substring(0,temp.length()-1);
		}
		this.xllcorner = Double.valueOf(temp.substring(temp.lastIndexOf(" ")+1,temp.length()));

		temp = asc.readLine();
		//Clear spaces in the end
		while(temp.lastIndexOf(" ")==temp.length()-1) {
			temp = temp.substring(0,temp.length()-1);
		}
		this.yllcorner = Double.valueOf(temp.substring(temp.lastIndexOf(" ")+1,temp.length()));

		temp = asc.readLine();
		//Clear spaces in the end
		while(temp.lastIndexOf(" ")==temp.length()-1) {
			temp = temp.substring(0,temp.length()-1);
		}
		this.cellsize = Double.valueOf(temp.substring(temp.lastIndexOf(" ")+1,temp.length()));

		temp = asc.readLine();
		//Clear spaces in the end
		while(temp.lastIndexOf(" ")==temp.length()-1) {
			temp = temp.substring(0,temp.length()-1);
		}
		this.NODATA_value = Integer.valueOf(temp.substring(temp.lastIndexOf(" ")+1,temp.length()));	

		double[][] mapa = new double[ncols][nrows];
		int space;
		int previousSpace;

		for(int row=0; row <nrows; row++) {
			previousSpace=0;
			temp = asc.readLine();
			//cuts initial space
			if (temp.startsWith(" ")) {		
				previousSpace ++;
			}

			for(int col=0; col <ncols; col++) {
				space = temp.indexOf(" ", previousSpace);
				if (space == -1) { //stops at end of line
					mapa[col][row] = Double.valueOf(temp.substring(previousSpace, temp.length()));
					checkMinMax(mapa[col][row]);
					break;
				}
				mapa[col][row] = Double.valueOf(temp.substring(previousSpace, space));
				checkMinMax(mapa[col][row]);
				previousSpace = space+1;
			}
		}

		asc.close();	

		this.map = mapa;
	}

	/**
	 * Constructor that receives the parameters of an .asc file to create a two dimensional array to be read.
	 * Initiates full of "No Data Value".
	 * 
	 * @param ncol int Number of columns 
	 * @param nrow int Number of rows
	 * @param xllcorner double Longitude at the left border
	 * @param yllcorner double Latitude at the lower border
	 * @param cellsize double Size of each cell
	 * @param nodatavalue int Value when there is no data
	 */

	public Map(String species, int ncols, int nrows, double xllcorner, double yllcorner, double cellsize, double nodatavalue ) {

		this.name = species;

		this.ncols = ncols;

		this.nrows = nrows;

		this.xllcorner = xllcorner;

		this.yllcorner = yllcorner;

		this.cellsize = cellsize;

		this.NODATA_value = nodatavalue;

		this.map = new double[ncols][nrows];

		for(int row=0; row <nrows; row++) {
			for(int col=0; col <ncols; col++) {
				map[col][row]=NODATA_value;
			}
		}
	}

	/**
	 * Constructor that receives the parameters of an .asc file from another .asc file to create a two dimensional array to be read.
	 * 
	 * @param file File Location of .asc file to be read.
	 * @param newMap boolean If true, creates a new map with the parameters of "file", if false, loads "file"
	 */

	public Map(File ascFile, boolean newMap) throws IOException {


		BufferedReader asc = new BufferedReader(new FileReader(ascFile));
		String temp;
		this.name = ascFile.getName().substring(0, ascFile.getName().length()-4);


		temp = asc.readLine();		
		this.ncols = Integer.valueOf(temp.substring(temp.lastIndexOf(" ")+1,temp.length()));

		temp = asc.readLine();
		this.nrows = Integer.valueOf(temp.substring(temp.lastIndexOf(" ")+1,temp.length()));

		temp = asc.readLine();
		this.xllcorner = Double.valueOf(temp.substring(temp.lastIndexOf(" ")+1,temp.length()));

		temp = asc.readLine();
		this.yllcorner = Double.valueOf(temp.substring(temp.lastIndexOf(" ")+1,temp.length()));

		temp = asc.readLine();
		this.cellsize = Double.valueOf(temp.substring(temp.lastIndexOf(" ")+1,temp.length()));

		temp = asc.readLine();
		this.NODATA_value = Integer.valueOf(temp.substring(temp.lastIndexOf(" ")+1,temp.length()));


		double[][] mapa = new double[ncols][nrows];

		if (!newMap) {
			int espaco;
			for(int row=0; row <nrows; row++) {
				temp = asc.readLine();
				//cuts initial space
				if (temp.startsWith(" ")) {		
					temp = temp.substring(1);
				}
				for(int col=0; col <ncols; col++) {
					espaco = temp.indexOf(" ");
					if (espaco == -1) { //stops at end of line
						mapa[col][row] = Double.valueOf(temp.substring(0, temp.length()));
						checkMinMax(mapa[col][row]);
						break;
					}
					mapa[col][row] = Double.valueOf(temp.substring(0, espaco));
					checkMinMax(mapa[col][row]);
					temp = temp.substring(espaco+1);				
				}
			}
		} else {
			for(int row=0; row <nrows; row++) {
				for(int col=0; col <ncols; col++) {
					map[col][row]=NODATA_value;
				}
			}
		}

		asc.close();	

		this.map = mapa;
	}



	/**
	 * Returns the value of a certain pixel given row and column
	 * 
	 * @param x  int Column of the pixel
	 * @param y int Row of the pixel
	 * @return double Value of the pixel
	 */	
	public double getValue(int x, int y) {

		return map[x][y];
	}

	/**
	 * Returns the column given longitude 
	 * 
	 * @param lon  double longitude
	 * @return int Column number (-1 if out of bounds)
	 */	
	public int getLonCol(double lon) {
		int col = (int)((lon - xllcorner - cellsize*0.5)/cellsize);
		if (col >= ncols || col < 0) {
			col = -1;
		}
		return col;
	}

	/**
	 * Returns the row given latitude 
	 * 
	 * @param lat  double latitude
	 * @return int row number (-1 if out of bounds)
	 */	
	public int getLatRow(double lat) {
		int row = (int)((-lat + yllcorner + cellsize * ((double)nrows) - 0.5*cellsize)/cellsize+0.5);
		if (row >= nrows || row < 0) {
			row = -1;
		}
		return row;		
	}

	/**
	 * Sets the value of a certain pixel
	 * 
	 * @param x  int Column of the pixel
	 * @param y int Row of the pixel
	 * @param double value Value of the pixel
	 */	
	public void setValue(double value, int x, int y) {
		map[x][y] = value;
		checkMinMax(value);
	}


	/**
	 * Returns the number of columns in the map
	 * 
	 * @return int Number of columns
	 */	
	public int getNcols() {
		return ncols;

	}

	/**
	 * Returns the number of rows in the map
	 * 
	 * @return int Number of rows
	 */	
	public int getNrows() {
		return nrows;
	}

	/**
	 * Returns the minimum value in the map
	 * 
	 * @return double Minimum value
	 */	
	public double getMin() {
		return minValue;
	}

	/**
	 * Returns the maximum value in the map
	 * 
	 * @return double Maximum value
	 */	
	public double getMax() {
		return maxValue;
	}

	/**
	 * Returns the xllcorner of the map
	 * 
	 * @return double xllcorner
	 */	
	public double getXcorner() {
		return xllcorner;
	}

	/**
	 * Returns the yllcorner of the map
	 * 
	 * @return double yllcorner
	 */	
	public double getYcorner(){
		return yllcorner;
	}


	/**
	 * Returns the cell size of the map
	 * 
	 * @return double Cell size
	 */	
	public double getCellSize(){
		return cellsize;
	}


	/**
	 * Returns the value used when there is no data
	 * 
	 * @return double No data value
	 */	
	public double getNoDataValue(){
		return NODATA_value;
	}

	/**
	 * Returns the longitude of a certain column
	 * 
	 * @param x int The column number
	 * 
	 * @return double Longitude
	 */	
	public double getLon(int x) {
		return xllcorner + cellsize * (x + 0.5);
	}

	/**
	 * Returns the latitude of a certain row
	 * 
	 * @param y int The row number
	 * 
	 * @return double Latitude
	 */	
	public double getLat(int y) {
		return yllcorner + cellsize * ((nrows - y)-0.5);
	}

	/**
	 * Returns the map's name
	 * 
	 * @return String The map's name
	 */	
	public String getName() {
		return name;
	}

	/**
	 * Private method that compares a new value with min and max and substitutes if needed.
	 * 
	 * @param value double New value to be compared
	 */
	private void checkMinMax(double value) {
		if (value != (double)NODATA_value) {
			if (minMaxInit) {			
				if (value > maxValue) {
					maxValue = value;
				} else if (value < minValue) {
					minValue = value;
				}
			} else {
				minMaxInit = true;
				maxValue = value;
				minValue = value;
			}
		}
	}


	/**
	 * Writes this Map object as .asc (GRIDASCII) files.
	 * 
	 * @param file String Location to save .asc file.
	 */

	public void saveToFile(String file) {

		try {

			String tempFile = file;
			File f = new File(tempFile);
			int fileNumber = 1;
			while (f.exists() == true) { // Check if the file already exists. If so, changes file name.
				file = tempFile.substring(0, tempFile.length()-4) + "(" + fileNumber + ")" + tempFile.substring(tempFile.length()-4,tempFile.length());
				f = new File(file);
				fileNumber++;
			}

			BufferedWriter writer = new BufferedWriter (new FileWriter(new File(file)));

			writer.write("ncols        "+ncols+"\n");
			writer.write("nrows        "+nrows+"\n");
			writer.write("xllcorner    "+xllcorner+"\n");
			writer.write("yllcorner    "+yllcorner+"\n");
			writer.write("cellsize     "+cellsize+"\n");
			writer.write("NODATA_value "+NODATA_value);

			for(int row=0; row <nrows; row++) {
				writer.write("\n");
				for(int col=0; col <ncols; col++) {
					writer.write(" "+getValue(col,row));			
				}
			}

			writer.close();
		//	System.out.println("File saved at "+file+".");



		} catch (IOException e) {
			System.out.println("Failed to write to file.");
			e.printStackTrace();
		}		
	}

	/**
	 * Clones this Map
	 * 
	 * @return Map Clone of this Map
	 */

	@Override
	public Map clone() {	

		Map clone = new Map(name, ncols, nrows, xllcorner, yllcorner, cellsize, NODATA_value );

		for(int x = 0; x < ncols; x++ ) {
			for (int y = 0; y < nrows; y++) {
				clone.setValue(this.getValue(x, y),x,y);
			}
		}

		return clone;			
	}


	/**
	 * Converts this map from presences to abundance
	 * @param populationSize int Abundance of population to substitute presences
	 * @return Map This map with the specified species abundance instead of presences
	 */

	public Map convertFromPresences(int populationSize) {	

		Map converted = new Map(name, ncols, nrows, xllcorner, yllcorner, cellsize, NODATA_value );		

		for(int x = 0; x < ncols; x++ ) {
			for (int y = 0; y < nrows; y++) {
				if (this.getValue(x,y) == 1) {
					converted.setValue(populationSize, x, y);
				} else {
					converted.setValue(this.getValue(x, y),x,y);
				}
			}
		}

		return converted;			
	}
	
	/**
	 * Changes the map's name
	 * @param name String The map's name
	 */

	public void setName(String name) {
		this.name= name;
	}	


}


