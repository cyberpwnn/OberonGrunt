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
import grunt.util.F;

public class ProgressStart extends JFrame
{
	private static final long serialVersionUID = 7742163455707976168L;
	private JPanel contentPane;
	private JLabel lblDownloading;
	private CircularProgressBar panel;
	private JLabel lblSomeFile;
	private JLabel lblRemaining;

	/**
	 * Create the frame.
	 */
	public ProgressStart()
	{
		setIconImage(Toolkit.getDefaultToolkit().getImage(ProgressStart.class.getResource("/grunt/ui/gem_ruby.png")));
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
					lblRemaining.setForeground(UX.c);
					lblSomeFile.setForeground(UX.c);
				}
			}
		}.start();

		getContentPane().setBackground(Color.DARK_GRAY);
		setForeground(Color.WHITE);
		setBackground(Color.DARK_GRAY);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 320, 303);
		contentPane = new JPanel();
		contentPane.setForeground(Color.WHITE);
		contentPane.setBackground(Color.DARK_GRAY);
		contentPane.setBorder(null);
		setUndecorated(true);
		setAlwaysOnTop(false);
		setOpacity(0.9f);
		getContentPane().add(contentPane);
		setLocation((int) Client.x, (int) Client.y);

		panel = new CircularProgressBar();
		panel.setForeground(Color.DARK_GRAY);
		panel.setBackground(Color.DARK_GRAY);
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(gl_contentPane.createParallelGroup(Alignment.LEADING).addComponent(panel, GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE));
		gl_contentPane.setVerticalGroup(gl_contentPane.createParallelGroup(Alignment.LEADING).addComponent(panel, GroupLayout.DEFAULT_SIZE, 293, Short.MAX_VALUE));

		lblDownloading = new JLabel("0%");
		lblDownloading.setBackground(UX.c);
		lblDownloading.setForeground(UX.c);
		lblDownloading.setHorizontalAlignment(SwingConstants.CENTER);
		lblDownloading.setFont(new Font("Yu Gothic UI Light", Font.PLAIN, 32));

		lblSomeFile = new JLabel("Grabbing Memes");
		lblSomeFile.setForeground(UX.c);
		lblSomeFile.setBackground(UX.c);
		lblSomeFile.setHorizontalAlignment(SwingConstants.CENTER);
		lblSomeFile.setFont(new Font("Yu Gothic UI Light", Font.PLAIN, 12));

		lblRemaining = new JLabel("0 of 0");
		lblRemaining.setHorizontalAlignment(SwingConstants.CENTER);
		lblRemaining.setForeground(UX.c);
		lblRemaining.setFont(new Font("Yu Gothic UI Light", Font.PLAIN, 18));
		lblRemaining.setBackground(UX.c);
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(gl_panel.createParallelGroup(Alignment.LEADING).addGroup(gl_panel.createSequentialGroup().addGap(44).addGroup(gl_panel.createParallelGroup(Alignment.LEADING).addGroup(gl_panel.createSequentialGroup().addComponent(lblSomeFile, GroupLayout.PREFERRED_SIZE, 222, GroupLayout.PREFERRED_SIZE).addContainerGap()).addGroup(gl_panel.createParallelGroup(Alignment.LEADING).addGroup(gl_panel.createSequentialGroup().addComponent(lblRemaining, GroupLayout.PREFERRED_SIZE, 222, GroupLayout.PREFERRED_SIZE).addContainerGap()).addGroup(gl_panel.createSequentialGroup().addComponent(lblDownloading, GroupLayout.PREFERRED_SIZE, 232, Short.MAX_VALUE).addGap(44))))));
		gl_panel.setVerticalGroup(gl_panel.createParallelGroup(Alignment.LEADING).addGroup(gl_panel.createSequentialGroup().addGap(94).addComponent(lblDownloading, GroupLayout.PREFERRED_SIZE, 47, GroupLayout.PREFERRED_SIZE).addPreferredGap(ComponentPlacement.RELATED).addComponent(lblRemaining, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE).addPreferredGap(ComponentPlacement.RELATED).addComponent(lblSomeFile).addContainerGap(111, Short.MAX_VALUE)));
		panel.setLayout(gl_panel);
		contentPane.setLayout(gl_contentPane);
		setVisible(true);
		toFront();
		requestFocus();
		FrameDragListener frameDragListener = new FrameDragListener(this);
		addMouseListener(frameDragListener);
		addMouseMotionListener(frameDragListener);
	}

	public void updateProgress(double pct, String f, int done, int of, long left)
	{
		try
		{
			lblDownloading.setText(F.pc(pct));
			panel.setProgress((int) (pct * 100));
			lblSomeFile.setText(f);
			lblRemaining.setText(done + " of " + of + " (" + F.fileSize(left) + " left)");
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
