package objdetection;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.text.html.parser.Element;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;



import java.awt.Color;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;

public class Homeobj extends JFrame {

	private JPanel contentPane;
	JButton btnStop = new JButton("STOP");
	JButton btnSTART = new JButton("START");
	JPanel objpanel = new JPanel();
	JLabel nonzerolbl = new JLabel("");
	JLabel oblbl = new JLabel("");
	
	private DaemonThread mythread = null;
	VideoCapture webSource = null;
	Mat vframe = new Mat();
	MatOfByte mem = new MatOfByte();
	
	Mat backimg=Mat.zeros(100,100, CvType.CV_8UC1);
	Mat prev=new Mat();
	boolean flag=true;
	
	
	List<Element> rectangles = new ArrayList<>();
	
	Mat erodeEle=Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3,3));  
	
	/**
	 * Launch the application.
	 */
	
	
	  class DaemonThread implements Runnable
	    {
	    protected volatile boolean runnable = false;

	    @Override
	    public  void run()
	    {
	        synchronized(this)
	        {
	            while(runnable)
	            {
	                if(webSource.grab())
	                {
			    	try
	                        {
			    		
	                            webSource.retrieve(vframe);
	                            objectd();
	                            Imgcodecs.imencode(".bmp", vframe, mem);
	                         
				    Image im = ImageIO.read(new ByteArrayInputStream(mem.toArray()));

				    BufferedImage buff = (BufferedImage) im;
				    Graphics g=objpanel.getGraphics();

				    if (g.drawImage(buff, 0, 0, getWidth(), getHeight() -150 , 0, 0, buff.getWidth(), buff.getHeight(), null))
				    
				    if(runnable == false)
	                            {
				    	System.out.println("Going to wait()");
				    	this.wait();
				    }
				 }
				 catch(Exception ex)
	                         {
				    System.out.println(ex);
	                         }
	                }
	            }
	        }
	     }
	   }
	  
	  
	  
	  public void objectd()
		{
		  List<MatOfPoint> points = new ArrayList<>();
		  
			 if(flag==true)
			 {
			Imgproc.cvtColor(prev, backimg, Imgproc.COLOR_RGB2GRAY);
			flag=false;
			 }
			
//		  Imgproc.cvtColor(prev, backimg, Imgproc.COLOR_RGB2GRAY);
		  
	        Mat dst=new Mat();
	   	        
	        Mat currimg=new Mat();
	        Imgproc.cvtColor(vframe, currimg, Imgproc.COLOR_RGB2GRAY);
//	        vframe.copyTo(prev);
	        Mat fgmask=Mat.zeros(currimg.rows(),currimg.cols(), CvType.CV_8UC1);                          
	        Core.absdiff(backimg,currimg, fgmask);
	       currimg.copyTo(backimg);
	        
	        Imgproc.threshold(fgmask, fgmask, 80, 255, Imgproc.THRESH_BINARY);

		       int nz=Core.countNonZero(fgmask);
		       nonzerolbl.setText(String.valueOf(nz));
	      
	       Mat hierarchy = new Mat();
	       
	       Imgproc.findContours(fgmask, points, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
	       
	       MatOfPoint2f[] contoursPoly  = new MatOfPoint2f[points.size()];
	        Rect[] boundRect = new Rect[points.size()];
	        System.out.println(boundRect.length);
	        int ocount=boundRect.length;
	        oblbl.setText(String.valueOf(ocount));
	        for (int i = 0; i < points.size(); i++) {
	            contoursPoly[i] = new MatOfPoint2f();
	            Imgproc.approxPolyDP(new MatOfPoint2f(points.get(i).toArray()), contoursPoly[i], 3, true);
	            boundRect[i] = Imgproc.boundingRect(new MatOfPoint(contoursPoly[i].toArray()));
//	            Imgproc.rectangle(vframe, boundRect[i].tl(), boundRect[i].br(), new Scalar(0,255,0), 2);
	            
	            Imgproc.rectangle(vframe,new Point(boundRect[i].x,boundRect[i].y), new Point(boundRect[i].x+boundRect[i].width,boundRect[i].y+boundRect[i].height), new Scalar(0,255,0), 2);
	           

	        }
	        

	      
		}
	
	
	public static void main(String[] args) {
		System.load("C:\\opencv\\build\\x64\\vc14\\bin\\opencv_videoio_ffmpeg412_64.dll");
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Homeobj frame = new Homeobj();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	
	
	/**
	 * Create the frame.
	 */
	public Homeobj() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 802, 737);
		contentPane = new JPanel();
		contentPane.setBackground(Color.DARK_GRAY);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		
		objpanel.setBounds(128, 129, 505, 503);
		contentPane.add(objpanel);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBackground(Color.LIGHT_GRAY);
		panel_1.setBounds(128, 28, 604, 81);
		contentPane.add(panel_1);
		panel_1.setLayout(null);
		
		
		
		btnSTART.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				btnSTART.setEnabled(false);
				webSource = new VideoCapture(0);
				mythread =   new DaemonThread();
				Thread th = new Thread(mythread);
				th.setDaemon(true);
				mythread.runnable = true;
				th.start();
				webSource.read(prev);
				btnStop.setEnabled(true);
			}
		});
		btnSTART.setFont(new Font("Times New Roman", Font.BOLD, 16));
		btnSTART.setBounds(28, 21, 89, 36);
		panel_1.add(btnSTART);
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mythread.runnable=false;
				webSource.release();
				btnStop.setEnabled(false);
				btnSTART.setEnabled(true);
				
			}
		});
		
		
		btnStop.setFont(new Font("Times New Roman", Font.BOLD, 16));
		btnStop.setBounds(163, 21, 100, 36);
		panel_1.add(btnStop);
		
		JLabel lblMovingPixels = new JLabel("Moving Pixels:");
		lblMovingPixels.setFont(new Font("Times New Roman", Font.BOLD, 22));
		lblMovingPixels.setBounds(277, 30, 161, 27);
		panel_1.add(lblMovingPixels);
		
		
		nonzerolbl.setFont(new Font("Times New Roman", Font.BOLD, 22));
		nonzerolbl.setBounds(459, 30, 96, 27);
		panel_1.add(nonzerolbl);
		
		JPanel panel = new JPanel();
		panel.setBackground(Color.LIGHT_GRAY);
		panel.setBounds(660, 145, 104, 205);
		contentPane.add(panel);
		panel.setLayout(null);
		
		JLabel lblObjects = new JLabel("Objects:");
		lblObjects.setFont(new Font("Times New Roman", Font.BOLD, 22));
		lblObjects.setBounds(10, 29, 93, 27);
		panel.add(lblObjects);
		
		
		oblbl.setFont(new Font("Times New Roman", Font.BOLD, 22));
		oblbl.setBounds(7, 85, 87, 27);
		panel.add(oblbl);
	}
}
