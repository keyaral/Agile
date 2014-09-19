package swen302.analysis;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

public class JarLoader {
    public static JarData loadJarFile(File file) {
    	JarData fileData = null;

		ArrayList<Class<?>> classData = new ArrayList<Class<?>>();

		JarFile zip = null;
		try
		{
			zip = new JarFile(file.getAbsoluteFile());

			URLClassLoader zipClassLoader = new URLClassLoader(new URL[] {file.toURI().toURL()});

		    Enumeration<?> enu = zip.entries();
			while (enu.hasMoreElements()) {
				ZipEntry zipEntry = (ZipEntry) enu.nextElement();

			    if(zipEntry.getName().endsWith(".class") && !zipEntry.isDirectory())
			    {
			    	String className = zipEntry.getName().replace("/", ".");
			    	className = className.substring(0, className.length() - 6);

			    	Class<?> cls = zipClassLoader.loadClass(className);

			    	classData.add(cls);
			    }
			}

			fileData = new JarData(file.getName(), file, zip.getManifest(), classData);

			zipClassLoader.close();
			zip.close();
		}
		catch (IOException | ClassNotFoundException exception)
		{
			exception.printStackTrace();
		}

		return fileData;
		//createNodes(top, classData);
		//doTraceAndAnalysis();
	}

    public static class JarData
    {
    	public String name;
    	public File file;
    	public Manifest manifest;
    	public ArrayList<Class<?>> data;

    	public JarData(String name, File file, Manifest manifest, ArrayList<Class<?>> data)
    	{
    		this.name = name;
    		this.file = file;
    		this.manifest = manifest;
    		this.data = data;
    	}
    }
}
