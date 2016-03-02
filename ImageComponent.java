import javax.imageio.*;
import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.geom.*;
import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

class ImageComponent implements Runnable,ActionListener{
String Filename;// current fileName
BufferedImage image;// current image in editing proccess
BufferedImage original_image;// Original image
JFrame f; // current JFrame
JPanel p;
	public ImageComponent(String aFilename){
		Filename=aFilename;
		image=LoadImage(Filename);
		original_image=LoadImage(Filename);//a copy of original for comparision
	}
	// Load an image
	public static BufferedImage LoadImage(String Filename){
		BufferedImage image;
		try{
			image=ImageIO.read
			(new File(Filename));
		}catch(Exception e){
			javax.swing.JOptionPane.showMessageDialog(null,"Error loading: "+Filename);
			image=null;
		}
	return image;
	}
	// Show an image
	public void Show(){
		SwingUtilities.invokeLater(this);
	}
	// 
	public void run(){
		f = new JFrame("");
		p = new JPanel();
		JPanel compressPanel = new JPanel();
	// Step 1: If lager than screen, add scroll bar
		JScrollPane scrollPane = new JScrollPane(new JLabel(new ImageIcon(original_image)));
		JScrollPane scrollPane2 =  new JScrollPane(new JLabel(new ImageIcon(image)));
		
		
		compressPanel.add(new JLabel("Compression Setting"));
		p.add(scrollPane);
		p.add(compressPanel);
		p.add(scrollPane2);
		f.getContentPane().add(p);		
		f.pack();
	// Step 2: press x, close window
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	// Step 3: Add Title
		f.setTitle(Filename+" "+image.getWidth()+" x "+image.getHeight());
	//Step 4: Put window in the middle of the screen
		f.setLocationRelativeTo(null); 
	// Step 5: Show the panel
		f.setVisible(true);
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
			f.repaint();//repaint the Jframe
		}
		if(e.getActionCommand().equals("NewFile")){
			System.out.println("NewFile");
			Open();
			f.dispose();//close old Jframe
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
				ImageIO.write(image,"jpeg",new File(SaveFilename));
			}		
		System.out.println("File Saved");
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