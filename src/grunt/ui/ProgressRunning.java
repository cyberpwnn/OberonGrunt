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
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;

import grunt.Client;

public class ProgressRunning extends JFrame
{
	private static final long serialVersionUID = 6867188309215936959L;
	private JPanel contentPane;
	private JLabel lblDownloading;
	public static CircularProgressBar panel;
	public static JLabel lblLog;
	public static JLabel label;

	/**
	 * Create the frame.
	 */
	public ProgressRunning()
	{
		setIconImage(Toolkit.getDefaultToolkit().getImage(ProgressRunning.class.getResource("/grunt/ui/gem_ruby.png")));
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
					lblLog.setForeground(UX.c);
					label.setForeground(UX.c);
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

		lblDownloading = new JLabel("Launching");
		lblDownloading.setBackground(new Color(255, 215, 0));
		lblDownloading.setForeground(UX.c);
		lblDownloading.setHorizontalAlignment(SwingConstants.CENTER);
		lblDownloading.setFont(new Font("Yu Gothic UI Light", Font.PLAIN, 26));

		lblLog = new JLabel("Log?");
		lblLog.setHorizontalAlignment(SwingConstants.CENTER);
		lblLog.setFont(new Font("Yu Gothic UI Light", Font.PLAIN, 12));
		lblLog.setBackground(new Color(255, 215, 0));
		lblLog.setForeground(UX.c);

		label = new JLabel("0%");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setForeground(UX.c);
		label.setFont(new Font("Yu Gothic UI Light", Font.PLAIN, 32));
		label.setBackground(new Color(255, 215, 0));
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(gl_panel.createParallelGroup(Alignment.LEADING).addGroup(Alignment.TRAILING, gl_panel.createSequentialGroup().addContainerGap(30, Short.MAX_VALUE).addComponent(label, GroupLayout.PREFERRED_SIZE, 266, GroupLayout.PREFERRED_SIZE).addGap(24)).addGroup(Alignment.TRAILING, gl_panel.createSequentialGroup().addGap(16).addComponent(lblDownloading, GroupLayout.PREFERRED_SIZE, 266, Short.MAX_VALUE).addContainerGap()).addGroup(Alignment.TRAILING, gl_panel.createSequentialGroup().addContainerGap(48, Short.MAX_VALUE).addComponent(lblLog, GroupLayout.PREFERRED_SIZE, 232, GroupLayout.PREFERRED_SIZE).addGap(40)));
		gl_panel.setVerticalGroup(gl_panel.createParallelGroup(Alignment.TRAILING).addGroup(Alignment.LEADING, gl_panel.createSequentialGroup().addGap(72).addComponent(label, GroupLayout.PREFERRED_SIZE, 51, GroupLayout.PREFERRED_SIZE).addGap(1).addComponent(lblDownloading).addPreferredGap(ComponentPlacement.RELATED).addComponent(lblLog).addContainerGap(121, Short.MAX_VALUE)));
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
