package grunt.ui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;

import grunt.Client;

public class ProgressGameCrash extends JFrame
{
	private static final long serialVersionUID = 6867188309215936959L;
	private JPanel contentPane;
	private JLabel lblDownloading;
	private CircularLoaderBar panel;

	/**
	 * Create the frame.
	 */
	public ProgressGameCrash()
	{
		setIconImage(Toolkit.getDefaultToolkit().getImage(ProgressGameCrash.class.getResource("/grunt/ui/gem_ruby.png")));
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

					lblDownloading.setForeground(new Color(255, 69, 0));
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

		panel = new CircularLoaderBar();
		panel.setForeground(Color.DARK_GRAY);
		panel.setBackground(Color.DARK_GRAY);
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(gl_contentPane.createParallelGroup(Alignment.LEADING).addComponent(panel, GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE));
		gl_contentPane.setVerticalGroup(gl_contentPane.createParallelGroup(Alignment.LEADING).addComponent(panel, GroupLayout.DEFAULT_SIZE, 293, Short.MAX_VALUE));

		lblDownloading = new JLabel(":/");
		lblDownloading.setBackground(new Color(255, 69, 0));
		lblDownloading.setForeground(new Color(255, 69, 0));
		lblDownloading.setHorizontalAlignment(SwingConstants.CENTER);
		lblDownloading.setFont(new Font("Yu Gothic UI Light", Font.PLAIN, 56));

		JLabel lblGameCrash = new JLabel("Game Crash");
		lblGameCrash.setHorizontalAlignment(SwingConstants.CENTER);
		lblGameCrash.setForeground(new Color(255, 69, 0));
		lblGameCrash.setFont(new Font("Yu Gothic UI Light", Font.PLAIN, 32));
		lblGameCrash.setBackground(new Color(255, 69, 0));

		JButton btnRelaunch = new JButton("Relaunch");
		btnRelaunch.setForeground(Color.BLACK);
		btnRelaunch.setFont(new Font("Yu Gothic UI Light", Font.PLAIN, 18));
		btnRelaunch.setBackground(Color.WHITE);
		btnRelaunch.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				EventQueue.invokeLater(new Runnable()
				{
					@Override
					public void run()
					{
						setVisible(false);
						Client c = new Client();
						c.logIn();
					}
				});
			}
		});

		JButton btnQuit = new JButton("Quit");
		btnQuit.setForeground(Color.BLACK);
		btnQuit.setFont(new Font("Yu Gothic UI Light", Font.PLAIN, 18));
		btnQuit.setBackground(Color.WHITE);
		btnQuit.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				System.exit(0);
			}
		});

		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(gl_panel.createParallelGroup(Alignment.LEADING).addGroup(gl_panel.createSequentialGroup().addGap(44).addGroup(gl_panel.createParallelGroup(Alignment.LEADING).addGroup(gl_panel.createSequentialGroup().addComponent(lblGameCrash, GroupLayout.PREFERRED_SIZE, 232, GroupLayout.PREFERRED_SIZE).addContainerGap()).addGroup(gl_panel.createSequentialGroup().addComponent(lblDownloading, GroupLayout.PREFERRED_SIZE, 232, Short.MAX_VALUE).addGap(44)))).addGroup(Alignment.TRAILING, gl_panel.createSequentialGroup().addContainerGap(120, Short.MAX_VALUE).addComponent(btnQuit, GroupLayout.PREFERRED_SIZE, 82, GroupLayout.PREFERRED_SIZE).addGap(118)).addGroup(gl_panel.createSequentialGroup().addGap(104).addComponent(btnRelaunch, GroupLayout.PREFERRED_SIZE, 111, GroupLayout.PREFERRED_SIZE).addContainerGap(105, Short.MAX_VALUE)));
		gl_panel.setVerticalGroup(gl_panel.createParallelGroup(Alignment.LEADING).addGroup(gl_panel.createSequentialGroup().addGap(26).addComponent(lblDownloading).addPreferredGap(ComponentPlacement.RELATED).addComponent(lblGameCrash).addGap(30).addComponent(btnRelaunch, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE).addPreferredGap(ComponentPlacement.RELATED).addComponent(btnQuit, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE).addContainerGap(50, Short.MAX_VALUE)));
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
