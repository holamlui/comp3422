import javax.imageio.*;
import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.geom.*;
import javax.swing.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Iterator;
import javax.imageio.*;
import javax.imageio.stream.*;

public class ImageComponent implements Runnable,ActionListener{
String Filename;// current fileName
BufferedImage image;// current image in editing proccess
BufferedImage original_image;// Original image
JFrame f; // current JFrame
JFrame f2; // current JFrame
JFrame f3;
JFrame f_option;//compression option frame
double filesize;
Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	public ImageComponent(String Filename){
		this.Filename = Filename;
		File file =new File(Filename);
		image=LoadImage(Filename);
		original_image=LoadImage(Filename);//a copy of original for comparision
		filesize = file.length()/1024;
	}
	// Load an image
	public static BufferedImage LoadImage(String Filename){
		BufferedImage image;
		BufferedImage newBufferedImage;
		try{
				image=ImageIO.read(new File(Filename));
				newBufferedImage = new BufferedImage(image.getWidth(),image.getHeight(), BufferedImage.TYPE_INT_RGB);
				newBufferedImage.createGraphics().drawImage(image, 0, 0, Color.WHITE, null);
		}catch(Exception e){
			//javax.swing.JOptionPane.showMessageDialog(null,"Error loading: "+Filename);
			newBufferedImage=new BufferedImage ( 1, 1, BufferedImage.TYPE_INT_RGB );
		}
	return newBufferedImage;
	}
	// Show an image
	public void Show(){
		SwingUtilities.invokeLater(this);
	}
	// 
	public void run(){
		f = new JFrame("");
		f2 = new JFrame("");
		f3 = new ScrollBarColorSelect(image,original_image,filesize);
		f3.show();
	// Step 1: If lager than screen, add scroll bar
		JScrollPane scrollPane = new JScrollPane(new JLabel(new ImageIcon(original_image)));
		JScrollPane scrollPane2 =  new JScrollPane(new JLabel(new ImageIcon(image)));
		
		scrollPane.setPreferredSize(new Dimension((int)screenSize.getWidth()/3,(int)screenSize.getHeight()/3));
		//scrollPane2.setPreferredSize(new Dimension((int)screenSize.getWidth()/3,(int)screenSize.getHeight()/3));
		f3.setPreferredSize(new Dimension((int)screenSize.getWidth()/3,(int)screenSize.getHeight()/3));
		//compressPanel.add(new JLabel("Compression Setting"));
		f.add(scrollPane);

		//f2.add(scrollPane2);	
		//f.getContentPane().add(p);		
		f.pack();
		f3.pack();
		//f2.pack();
		
	// Step 2: press x, close window
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f3.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	// Step 3: Add Title
		f.setTitle(Filename+" "+image.getWidth()+" x "+image.getHeight()+ "  "+filesize+"KB");
	//Step 4: Put window in the middle of the screen
		f.setLocation(0,(int)screenSize.getHeight()/2-f.getHeight()/2); 
		f3.setLocation(f.getWidth(),(int)screenSize.getHeight()/2-f3.getHeight()/2); 
	// Step 5: Show the panel
		f.setVisible(true);
		f3.setVisible(true);
		// Add menu bar
		JMenuBar menuBar = new JMenuBar();
		// Add "File" and "Operator" option
		JMenu File_Menu=new JMenu("File");
		JMenu Operator_Menu = new JMenu("Operator"); 
		// Add "File" and "Operator" to menu
		menuBar.add(File_Menu);
		menuBar.add(Operator_Menu);
		//Add "Grayscale" to Operator Menu
		
		JMenuItem item = new JMenuItem("GrayScale");
		item.addActionListener(this);
		JMenuItem item2 = new JMenuItem("NewFile");	
		item2.addActionListener(this);
		JMenuItem item3 = new JMenuItem("SaveFile");	
		item3.addActionListener(this);
			
		Operator_Menu.add(item);   
		File_Menu.add(item2);  
		File_Menu.add(item3); 
		f.setJMenuBar(menuBar);
	}
	//event lisitener for operations
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("GrayScale")){
			System.out.println("GrayScale");
			doGray();
			f2.repaint();//repaint the Jframe
			f3.repaint();//repaint the Jframe
		}
		if(e.getActionCommand().equals("NewFile")){
			System.out.println("NewFile");
			Open();
			f.dispose();//close old Jframe
			f3.dispose();//close old Jframe
		}
		if(e.getActionCommand().equals("SaveFile")){
			System.out.println("SaveFile");
			Save();
		}
	}
	public void Open(){	//open new files
		FileDialog fd = new FileDialog(f,"Open...",FileDialog.LOAD);
		fd.setVisible(true);
		if(fd!=null){
			Filename=fd.getDirectory()+System.getProperty("file.separator").charAt(0)+fd.getFile();
			ImageComponent image=new ImageComponent(Filename);// load image
			image.Show(); // show image
		}
	}
	public void Save(){//Save the file
		try{
			FileDialog fd2 = new FileDialog(f,"Save as...",FileDialog.SAVE);
			fd2.setVisible(true);
			if(fd2!=null){
			String SaveFilename;
			
			System.out.println("Save test!");
				SaveFilename=fd2.getDirectory()+System.getProperty("file.separator").charAt(0)+fd2.getFile()+".jpg";
				ImageIO.write(image,"jpg",new File(SaveFilename));
				
				InputStream inStream = null;
				OutputStream outStream = null;		
			try{		
				File afile =new File("temp.jpg");
				File bfile =new File(SaveFilename);    		
				inStream = new FileInputStream(afile);
				outStream = new FileOutputStream(bfile);
				
				byte[] buffer = new byte[1024];
				
				int length;
				//copy the file content in bytes 
				while ((length = inStream.read(buffer)) > 0){
						outStream.write(buffer, 0, length);    	 
				} 	 
				inStream.close();
				outStream.close();		
			}catch(IOException e){
    		e.printStackTrace();
			}
			}
		}catch(Exception e){
			javax.swing.JOptionPane.showMessageDialog(null,"Error saving: ");
			image=null;
		}
	}
	//Gray scale
	public void doGray(){  
		int Height=image.getHeight();
		int Width=image.getWidth();
		for(int y=0;y<Height;y++){
			for(int x=0;x<Width;x++){
				int rgb=image.getRGB(x,y);
				int r=(rgb&0x00ff0000)>>16;// Get Red
				int g=(rgb&0x0000ff00)>>8;// Get Green
				int b=rgb&0x000000ff;// Get Blue
				int gray=(r+g+b)/3;// Calculate Gray
				rgb=(0xff000000|(gray<<16)|(gray<<8)|gray);
				image.setRGB(x,y,rgb);
			}
		}         
	}
}
class ScrollBarColorSelect extends JFrame implements AdjustmentListener {

  private JLabel luminanceLabel;

  private JLabel chrominanceLabel;

  private JLabel qualityLabel;

  private JScrollBar luminance;

  private JScrollBar chrominance;
  
  private double filesize;
  private double original_filesize;

  public JScrollBar quality;
  
  private JPanel colorPanel;
  
  private JScrollPane scrollPane2;

  public BufferedImage image2;
  public BufferedImage original_image2;//copy of original
  
  private float luminance_offset = 1f;

  public ScrollBarColorSelect(BufferedImage image, BufferedImage original_image,double original_filesize) {
	this.original_filesize = original_filesize;
	image2 = image;
	original_image2 = original_image;
    setTitle("Compression setting ");
    //setSize(300, 200);
    Container contentPane = getContentPane();

    JPanel p = new JPanel();
    p.setLayout(new GridLayout(3, 2,3,3));

	p.add(luminanceLabel = new JLabel("Brightness/contrast  0"));
    p.add(luminance = new JScrollBar(Adjustable.HORIZONTAL, 0, 0, -100, 100));
    luminance.setBlockIncrement(10);
    luminance.addAdjustmentListener(this);
	
/*
    p.add(chrominanceLabel = new JLabel("chrominancel 0"));
    p.add(chrominance = new JScrollBar(Adjustable.HORIZONTAL, 0, 0, -100, 100));
    chrominance.setBlockIncrement(10);
    chrominance.addAdjustmentListener(this);
*/
    p.add(qualityLabel = new JLabel("quality 100"));
    p.add(quality = new JScrollBar(Adjustable.HORIZONTAL, 100, 0, 0, 100));
    quality.setBlockIncrement(10);
    quality.addAdjustmentListener(this);

    contentPane.add(p, "South");
	scrollPane2 =  new JScrollPane(new JLabel(new ImageIcon(image)));
    contentPane.add(scrollPane2, "Center");
  }

  public void adjustmentValueChanged(AdjustmentEvent evt) {
    luminanceLabel.setText("Brightness/contrast " + luminance.getValue());
    //chrominanceLabel.setText("chrominance " + chrominance.getValue());
    qualityLabel.setText("quality " + quality.getValue());
   // colorPanel.setBackground(new Color(red.getValue(), green.getValue(), blue.getValue()));
	luminance_offset = (float)1+(float)luminance.getValue()/100;

	//System.out.println("luminance_offset = "+luminance_offset);
   RescaleOp op = new RescaleOp(luminance_offset, 0, null);
   //image2 = original_image2;
   image2.createGraphics().drawImage(original_image2, 0, 0, Color.WHITE, null);
	image2 = op.filter(image2, image2);
    
	try (OutputStream stream = new FileOutputStream("temp.jpg")){
		writeJPG(image2,stream,(float)quality.getValue()/200);	
		File file =new File("temp.jpg");
		filesize = file.length()/1024;
		setTitle("Compression setting | Expected filesize: "+filesize+"KB | Compression ratio: "+(double)original_filesize/filesize);
		BufferedImage temp=ImageIO.read(new File("temp.jpg"));
		image2.createGraphics().drawImage(temp, 0, 0, Color.WHITE, null);
	}catch (IOException e) {
	        }	
	scrollPane2.repaint();
  }
  public static void writeJPG(BufferedImage bufferedImage,OutputStream outputStream,float quality) throws IOException{
    Iterator<ImageWriter> iterator = ImageIO.getImageWritersByFormatName("jpg");
    ImageWriter imageWriter = iterator.next();
    ImageWriteParam imageWriteParam = imageWriter.getDefaultWriteParam();
    imageWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
    imageWriteParam.setCompressionQuality(quality);
    ImageOutputStream imageOutputStream = new MemoryCacheImageOutputStream(outputStream);
    imageWriter.setOutput(imageOutputStream);
    IIOImage iioimage = new IIOImage(bufferedImage, null, null);
    imageWriter.write(null, iioimage, imageWriteParam);
    imageOutputStream.flush();
}
}  