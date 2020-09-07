package model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

/**
 * Class that paints Map objects to a JFrame.
 *
 * @author  Tomé Neves
 */

public class Painter {

	JFrame f;
	BufferedImage resized;
	double scaler = 1;
	Color noDataColor = new Color(137,215,255);

	/**
	 * Paints Map objects to a JFrame.
	 *
	 * @param Mapa Map The Map Object to paint
	 * @param scaler int How much to amplify the image
	 */

	public void paintMap(Map mapa){
		paintMap(mapa, mapa.getName());
	}


	/**
	 * Paints Map objects to a JFrame.
	 *
	 * @param Mapa Map The Map Object to paint
	 * @param name String The name of the window
	 * @param scaler int How much to amplify the image
	 */

	public void paintMap(Map mapa, String name){
		f = new JFrame(name);
		int ncols = mapa.getNcols();
		int nrows = mapa.getNrows();
		int type = BufferedImage.TYPE_INT_ARGB;
		BufferedImage image = new BufferedImage(ncols, nrows, type);
		f.setIconImage(Toolkit.getDefaultToolkit().getImage(Application.class.getResource("/icon.png")));


		//Paints Map
		Color black = new Color(0,0,0);
		int red;
		int green;
		int blue;
		for(int x = 0; x < ncols; x++) {
			for(int y = 0; y < nrows; y++) {
				Color cor;
				if(mapa.getValue(x, y) != (double)mapa.getNoDataValue()) {	
					if (mapa.getValue(x, y) != 0) {
						red= (int)((mapa.getValue(x,y)-mapa.getMin())*195.0/(mapa.getMax()-mapa.getMin()))+30;
						green= 225 - red;
						blue = 0;
						cor = new Color(red,green,blue);
					} else {
						cor = black;
					}
				} else {
					cor = noDataColor;						
				}
				image.setRGB(x, y,cor.getRGB());
			}
		}



		resized = new BufferedImage((int)(ncols*scaler), (int)(nrows*scaler), image.getType());

		Graphics2D g = resized.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		g.drawImage(image, 0, 0, (int)(ncols*scaler), (int)(nrows*scaler), 0, 0, image.getWidth(), image.getHeight(), null);
		g.dispose();

		Icon icon = new ImageIcon(resized);
		JLabel label = new JLabel(icon);

		f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		f.getContentPane().add(label);
		f.pack();
		SwingUtilities.invokeLater(new Runnable(){
			public void run() {
				f.setLocationRelativeTo(null);
				f.setVisible(true);
			}
		});

	}

	/**
	 * Sets the how much the image is scaled.
	 * 
	 * @param scalerValue double Scaling value
	 */

	public void setScaler(double scalerValue) {
		scaler = scalerValue;
	}

	/**
	 * Sets the color of "No Data" values
	 * 
	 * @param red int Red RGB value
	 * @param green int Green RGB value
	 * @param blue int Blue RGB value
	 */

	public void setNoDataColor(int red, int green, int blue) {
		noDataColor = new Color(red,green,blue);
	}

}
