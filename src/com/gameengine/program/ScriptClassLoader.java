package com.gameengine.program;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ScriptClassLoader extends URLClassLoader {

	private final Map<String, Class<?>> classes = new ConcurrentHashMap<>();

	private final JarFile jar;
	private final Manifest manifest;
	
	private final Class<? extends Script> scriptClass;

	ScriptClassLoader(@Nullable ClassLoader parent, @NotNull String mainClass, @NotNull File file)
			throws IOException, MalformedURLException, InvalidCodeException {
		super(new URL[] { file.toURI().toURL() }, parent);

		if (mainClass == null) {
			throw new NullPointerException("The main class cannot be null");
		}
		if(mainClass.isBlank()) {
			throw new InvalidCodeException("The main class cannot be black");
		}

		this.jar = new JarFile(file);
		this.manifest = jar.getManifest();

		Class<?> jarClass;

		try {
			jarClass = Class.forName(mainClass, true, this);
		} catch (ClassNotFoundException e) {
			throw new InvalidCodeException("Cannot find main class '" + mainClass + "'", e);
		}
		try {
			scriptClass = jarClass.asSubclass(Script.class);
		} catch (ClassCastException e) {
			throw new InvalidCodeException("The main class '" + mainClass + "' doesn't extend JavaCode", e);
		}
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		Class<?> result = this.classes.get(name);
		if (result == null)
			result = super.findClass(name);
		this.classes.put(name, result);
		return result;
	}

	@Override
	public void close() throws IOException {
		try {
			super.close();
		} finally {
			this.jar.close();
		}
	}

	Set<String> getClasses() {
		return this.classes.keySet();
	}

	@Nullable
	protected Manifest getManifest() {
		return manifest;
	}

	@Nullable
	public Class<? extends Script> getCodeClass() {
		return scriptClass;
	}

}
