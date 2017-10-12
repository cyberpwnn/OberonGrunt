package grunt.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;

import grunt.Client;

public abstract class ProgressLogin extends JFrame
{
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JLabel lblDownloading;
	private CircularProgressBar panel;
	private JTextField txtE;
	private JTextField txtP;
	private JLabel lblPassword;

	/**
	 * Create the frame.
	 */
	public ProgressLogin()
	{
		setIconImage(Toolkit.getDefaultToolkit().getImage(ProgressLogin.class.getResource("/grunt/ui/gem_ruby.png")));
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
					lblPassword.setForeground(UX.c);
				}
			}
		}.start();

		getContentPane().setForeground(Color.WHITE);
		setBackground(Color.WHITE);
		getContentPane().setBackground(Color.WHITE);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setUndecorated(true);
		setAlwaysOnTop(false);
		setOpacity(0.9f);
		toFront();
		requestFocus();
		setBounds(100, 100, 320, 303);
		contentPane = new JPanel();
		contentPane.setForeground(Color.WHITE);
		contentPane.setOpaque(true);
		contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(null);
		getContentPane().add(contentPane);
		setLocation((int) Client.x, (int) Client.y);
		panel = new CircularProgressBar();
		panel.setBackground(Color.DARK_GRAY);
		panel.setForeground(Color.DARK_GRAY);
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(gl_contentPane.createParallelGroup(Alignment.LEADING).addComponent(panel, GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE));
		gl_contentPane.setVerticalGroup(gl_contentPane.createParallelGroup(Alignment.LEADING).addComponent(panel, GroupLayout.DEFAULT_SIZE, 293, Short.MAX_VALUE));

		lblDownloading = new JLabel("Email");
		lblDownloading.setBackground(UX.c);
		lblDownloading.setForeground(UX.c);
		lblDownloading.setHorizontalAlignment(SwingConstants.CENTER);
		lblDownloading.setFont(new Font("Yu Gothic UI Light", Font.PLAIN, 24));

		UX.c(null);
		txtE = new JTextField();
		txtE.setForeground(UX.c);
		txtE.setBackground(Color.DARK_GRAY);
		txtE.setHorizontalAlignment(SwingConstants.CENTER);
		txtE.setFont(new Font("Yu Gothic Light", Font.BOLD, 18));
		txtE.setColumns(10);
		txtE.requestFocusInWindow();

		lblPassword = new JLabel("Password");
		lblPassword.setForeground(UX.c);
		lblPassword.setHorizontalAlignment(SwingConstants.CENTER);
		lblPassword.setFont(new Font("Yu Gothic UI Light", Font.PLAIN, 24));

		txtP = new JPasswordField();
		txtP.setForeground(UX.c);
		txtP.setBackground(Color.DARK_GRAY);
		txtP.setHorizontalAlignment(SwingConstants.CENTER);
		txtP.setFont(new Font("Yu Gothic Light", Font.BOLD, 18));
		txtP.setColumns(10);

		JButton btnLogin = new JButton("Login");
		btnLogin.setForeground(Color.BLACK);
		btnLogin.setBackground(Color.WHITE);
		btnLogin.setFont(new Font("Yu Gothic UI Light", Font.PLAIN, 18));
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(gl_panel.createParallelGroup(Alignment.TRAILING).addGroup(gl_panel.createSequentialGroup().addContainerGap().addComponent(lblDownloading, GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE).addContainerGap()).addGroup(gl_panel.createSequentialGroup().addGap(113).addComponent(btnLogin, GroupLayout.PREFERRED_SIZE, 82, GroupLayout.PREFERRED_SIZE).addContainerGap(125, Short.MAX_VALUE)).addGroup(gl_panel.createSequentialGroup().addContainerGap(55, Short.MAX_VALUE).addComponent(txtE, GroupLayout.PREFERRED_SIZE, 215, GroupLayout.PREFERRED_SIZE).addGap(50)).addGroup(gl_panel.createSequentialGroup().addContainerGap(54, Short.MAX_VALUE).addComponent(txtP, GroupLayout.PREFERRED_SIZE, 215, GroupLayout.PREFERRED_SIZE).addGap(51)).addGroup(Alignment.LEADING, gl_panel.createSequentialGroup().addContainerGap().addComponent(lblPassword, GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE).addContainerGap()));
		gl_panel.setVerticalGroup(gl_panel.createParallelGroup(Alignment.LEADING).addGroup(gl_panel.createSequentialGroup().addGap(56).addComponent(lblDownloading).addPreferredGap(ComponentPlacement.RELATED).addComponent(txtE, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addPreferredGap(ComponentPlacement.RELATED).addComponent(lblPassword, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE).addPreferredGap(ComponentPlacement.RELATED).addComponent(txtP, GroupLayout.PREFERRED_SIZE, 36, GroupLayout.PREFERRED_SIZE).addGap(18).addComponent(btnLogin).addContainerGap(42, Short.MAX_VALUE)));
		panel.setLayout(gl_panel);
		contentPane.setLayout(gl_contentPane);
		panel.setProgress(100);
		btnLogin.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				btnLogin.setEnabled(false);
				btnLogin.setText("Logging In");

				if(!onSubmit(txtE.getText(), txtP.getText()))
				{
					setVisible(true);
					txtP.setText("");
					lblPassword.setForeground(Color.red);
					btnLogin.setEnabled(true);
					btnLogin.setText("Retry");
				}
			}
		});

		txtP.addKeyListener(new KeyListener()
		{
			@Override
			public void keyTyped(KeyEvent e)
			{

			}

			@Override
			public void keyReleased(KeyEvent e)
			{
				if(e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					btnLogin.doClick();
				}
			}

			@Override
			public void keyPressed(KeyEvent e)
			{

			}
		});

		FrameDragListener frameDragListener = new FrameDragListener(this);
		addMouseListener(frameDragListener);
		addMouseMotionListener(frameDragListener);

		requestFocusInWindow();
	}

	public abstract boolean onSubmit(String username, String password);

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
