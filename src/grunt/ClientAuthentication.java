package grunt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Proxy;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.mojang.authlib.Agent;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;

import grunt.ui.ProgressAuthenticating;

public class ClientAuthentication
{
	public enum AuthenticationResponse
	{
		SUCCESS, FAILED;
	}

	private String username;
	private String password;
	private String token;
	private String uuid;
	private String profileName;
	private String profileType;
	private PropertyMap profileSettings;
	private File authFile;

	public ClientAuthentication(File authFile, String username, String password)
	{
		this.username = username;
		this.password = password;
		this.authFile = authFile;
	}

	public ClientAuthentication(File authFile)
	{
		this.authFile = authFile;

		try
		{
			loadUserToken();
		}

		catch(IOException e)
		{

		}
	}

	public AuthenticationResponse authenticate()
	{
		System.out.println("Authenticating");
		ProgressAuthenticating authx = new ProgressAuthenticating();
		authx.setVisible(true);
		ProgressAuthenticating.panel.setProgress(0);

		try
		{
			YggdrasilUserAuthentication auth = (YggdrasilUserAuthentication) new YggdrasilAuthenticationService(Proxy.NO_PROXY, "1").createUserAuthentication(Agent.MINECRAFT);
			auth.setUsername(username);
			auth.setPassword(password);

			try
			{
				Thread.sleep(166);
			}

			catch(InterruptedException e1)
			{
				e1.printStackTrace();
			}

			ProgressAuthenticating.panel.setProgress(38);

			try
			{
				Thread.sleep(100);
			}

			catch(InterruptedException e1)
			{
				e1.printStackTrace();
			}

			auth.logIn();
			ProgressAuthenticating.panel.setProgress(46);
			token = auth.getAuthenticatedToken();
			uuid = auth.getSelectedProfile().getId().toString().replaceAll("-", "");
			profileName = auth.getSelectedProfile().getName();
			profileType = auth.getUserType().getName();
			profileSettings = auth.getSelectedProfile().getProperties();

			try
			{
				Thread.sleep(100);
			}

			catch(InterruptedException e1)
			{
				e1.printStackTrace();
			}

			ProgressAuthenticating.panel.setProgress(66);

			try
			{
				Thread.sleep(100);
			}

			catch(InterruptedException e1)
			{
				e1.printStackTrace();
			}

			saveUserToken();
			ProgressAuthenticating.panel.setProgress(100);

			try
			{
				Thread.sleep(100);
			}

			catch(InterruptedException e1)
			{
				e1.printStackTrace();
			}

			authx.setVisible(false);

			return AuthenticationResponse.SUCCESS;
		}

		catch(Exception e)
		{
			ProgressAuthenticating.panel.setProgress(0);

			try
			{
				Thread.sleep(500);
			}

			catch(InterruptedException e1)
			{
				e1.printStackTrace();
			}

			authx.setVisible(false);
			return AuthenticationResponse.FAILED;
		}
	}

	public void saveUserToken() throws IOException
	{
		FileOutputStream fos = new FileOutputStream(authFile);
		GZIPOutputStream gzo = new GZIPOutputStream(fos);
		DataOutputStream dos = new DataOutputStream(gzo);
		dos.writeUTF(username);
		dos.writeUTF(password);
		dos.close();
	}

	public void loadUserToken() throws IOException
	{
		FileInputStream fin = new FileInputStream(authFile);
		GZIPInputStream gzi = new GZIPInputStream(fin);
		DataInputStream dis = new DataInputStream(gzi);
		username = dis.readUTF();
		password = dis.readUTF();
		dis.close();
	}

	public String getToken()
	{
		return token;
	}

	public String getUuid()
	{
		return uuid;
	}

	public String getProfileName()
	{
		return profileName;
	}

	public String getProfileType()
	{
		return profileType;
	}

	public PropertyMap getProfileSettings()
	{
		return profileSettings;
	}

	public File getAuthFile()
	{
		return authFile;
	}
}
