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
		try
		{
			YggdrasilUserAuthentication auth = (YggdrasilUserAuthentication) new YggdrasilAuthenticationService(Proxy.NO_PROXY, "1").createUserAuthentication(Agent.MINECRAFT);
			auth.setUsername(username);
			auth.setPassword(password);
			auth.logIn();
			token = auth.getAuthenticatedToken();
			uuid = auth.getSelectedProfile().getId().toString().replaceAll("-", "");
			profileName = auth.getSelectedProfile().getName();
			profileType = auth.getUserType().getName();
			profileSettings = auth.getSelectedProfile().getProperties();
			saveUserToken();

			return AuthenticationResponse.SUCCESS;
		}

		catch(Exception e)
		{
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
