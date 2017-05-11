package de.jkitberatung.recorder;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.JPanel;


public class ScreenshotPanel extends JPanel {


  /**
	 * 
	 */
	private static final long serialVersionUID = 5612574366024763757L;
private Rectangle rectangle;
  Point start;

  boolean dragging, isRectangleSet;
  private BufferedImage image;
  private ScreenshotAreaSelector frame;

  public ScreenshotPanel(ScreenshotAreaSelector screenshotAreaSelector, BufferedImage image) {
    this.frame = screenshotAreaSelector;
    this.image = image;
    init();
  }

  public void init() {
    
    setBackground(Color.black);
    setBorder(BorderFactory.createEtchedBorder());
    rectangle = new Rectangle();

    addListeners();
  }

  private void addListeners() {
    addMouseListener(new MouseAdapter() {

      @Override
      public void mousePressed(MouseEvent e) {
        if(isRectangleSet) {
          // clear the existing rectangle
          setRectangleSize(start, start);

          isRectangleSet = false;

        } else {// we start a new rectangle

          start = e.getPoint();

          dragging = true;
          isRectangleSet = true;
        }
      }

      @Override
      public void mouseReleased(MouseEvent e) {
        dragging = false;
      }
    });

    addMouseMotionListener(new MouseMotionAdapter() {
      @Override
      public void mouseDragged(MouseEvent e) {
        if(dragging)

          setRectangleSize(start, e.getPoint());
      }
    });

  }

  protected void paintComponent(Graphics g)

  {

    super.paintComponent(g);

    Graphics2D g2 = (Graphics2D)g;

    g2.drawImage(image, 0, 0, null);

//  Paint the rectangle with a translucent color.
    g2.setPaint(Color.red);
    g2.draw(rectangle);
    g2.setColor(new Color(255, 128, 128, 56));
    g2.fillRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);

    // Update frame title so the user get some feedback
//    frame.setTitle(rectangle.toString());

  }

  public void setRectangleSize(Point start, Point end)  {

	  if (end.x > image.getWidth())
		  end.x = image.getWidth();

	  if (end.y > image.getHeight())
		  end.y = image.getHeight();

	  if (end.x < 0)
		  end.x = 0;

	  if (end.y < 0)
		  end.y = 0;
		  
    rectangle.setFrameFromDiagonal(start, end);
    
    frame.scroll(rectangle);
    
    // Draw the image and the rectangle again
    repaint();

  }

  public Dimension getPreferredSize() {

    return new Dimension(image.getWidth(), image.getHeight());

  }
  
  @Override
  public Dimension getMaximumSize() {
    // TODO Auto-generated method stub
    return new Dimension(image.getWidth(), image.getHeight());
  }
  
 public Rectangle getRectangle() {
	 return rectangle;
 }
}
