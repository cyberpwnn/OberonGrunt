package grunt.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import grunt.Client;

public class ProgressAuthenticating extends JFrame
{
	private static final long serialVersionUID = 6867188309215936959L;
	private JPanel contentPane;
	private JLabel lblDownloading;
	public static CircularProgressBar panel;

	/**
	 * Create the frame.
	 */
	public ProgressAuthenticating()
	{
		setIconImage(Toolkit.getDefaultToolkit().getImage(ProgressAuthenticating.class.getResource("/grunt/ui/gem_ruby.png")));
		new Thread()
		{
			@Override
			public void run()
			{
				while(!interrupted())
				{
					try
					{
						Thread.sleep(10);
					}

					catch(InterruptedException e)
					{
						e.printStackTrace();
					}

					if(!isVisible())
					{
						continue;
					}

					lblDownloading.setForeground(UX.c);
				}
			}
		}.start();

		getContentPane().setBackground(Color.DARK_GRAY);
		setForeground(Color.WHITE);
		setBackground(Color.DARK_GRAY);
		setBounds(100, 100, 320, 303);
		contentPane = new JPanel();
		contentPane.setForeground(Color.WHITE);
		contentPane.setBackground(Color.DARK_GRAY);
		contentPane.setBorder(null);
		setUndecorated(true);
		setAlwaysOnTop(true);
		setOpacity(0.9f);
		getContentPane().add(contentPane);
		setLocation((int) Client.x, (int) Client.y);

		panel = new CircularProgressBar();
		panel.setForeground(Color.DARK_GRAY);
		panel.setBackground(Color.DARK_GRAY);
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(gl_contentPane.createParallelGroup(Alignment.LEADING).addGroup(gl_contentPane.createSequentialGroup().addComponent(panel, GroupLayout.PREFERRED_SIZE, 320, GroupLayout.PREFERRED_SIZE).addContainerGap(258, Short.MAX_VALUE)));
		gl_contentPane.setVerticalGroup(gl_contentPane.createParallelGroup(Alignment.LEADING).addComponent(panel, GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE));

		lblDownloading = new JLabel("Authenticating");
		lblDownloading.setBackground(new Color(255, 215, 0));
		lblDownloading.setForeground(UX.c);
		lblDownloading.setHorizontalAlignment(SwingConstants.CENTER);
		lblDownloading.setFont(new Font("Yu Gothic UI Light", Font.PLAIN, 26));
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(gl_panel.createParallelGroup(Alignment.TRAILING).addGroup(gl_panel.createSequentialGroup().addGap(16).addComponent(lblDownloading, GroupLayout.PREFERRED_SIZE, 294, Short.MAX_VALUE).addContainerGap()));
		gl_panel.setVerticalGroup(gl_panel.createParallelGroup(Alignment.LEADING).addGroup(gl_panel.createSequentialGroup().addGap(124).addComponent(lblDownloading).addContainerGap(143, Short.MAX_VALUE)));
		panel.setLayout(gl_panel);
		contentPane.setLayout(gl_contentPane);
		setVisible(true);
		toFront();
		requestFocus();
		FrameDragListener frameDragListener = new FrameDragListener(this);
		addMouseListener(frameDragListener);
		addMouseMotionListener(frameDragListener);
	}

	public void updateProgress()
	{
		try
		{

		}

		catch(Exception e)
		{

		}
	}

	public static class FrameDragListener extends MouseAdapter
	{
		private final JFrame frame;
		private Point mouseDownCompCoords = null;

		public FrameDragListener(JFrame frame)
		{
			this.frame = frame;
		}

		@Override
		public void mouseReleased(MouseEvent e)
		{
			mouseDownCompCoords = null;
		}

		@Override
		public void mousePressed(MouseEvent e)
		{
			mouseDownCompCoords = e.getPoint();
		}

		@Override
		public void mouseDragged(MouseEvent e)
		{
			Point currCoords = e.getLocationOnScreen();
			frame.setLocation(currCoords.x - mouseDownCompCoords.x, currCoords.y - mouseDownCompCoords.y);
		}
	}
}
