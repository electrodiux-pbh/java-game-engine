package com.gameengine.data;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.simple.parser.ParseException;

import com.gameengine.audio.Sound;
import com.gameengine.graphics.Icon;
import com.gameengine.graphics.Shader;
import com.gameengine.graphics.ShaderException;
import com.gameengine.graphics.SpriteSheet;
import com.gameengine.graphics.Texture;
import com.gameengine.languages.Language;
import com.gameengine.languages.Languages;

public class SourceManager {

	private Map<String, Texture> textures = new HashMap<>();
	private Map<String, SpriteSheet> spriteSheets = new HashMap<>();
	private Map<String, Shader> shaders = new HashMap<>();
	private Map<String, Sound> sounds = new HashMap<>();
	private Map<Languages, Language> languages = new EnumMap<>(Languages.class);
	
	private Icon icon;
	
	@Nullable
	public Texture getTexture(@NotNull String key) {
		return textures.getOrDefault(key, null);
	}
	
	@NotNull
	public Texture loadTexture(@NotNull String key, @NotNull String path) throws IOException {
		Texture texture = new Texture(path);
		textures.put(key, texture);
		return texture;
	}
	
	public void loadSpriteSheet(@NotNull String key, @Nullable SpriteSheet sprite) {
		spriteSheets.put(key, sprite);
	}
	
	@Nullable
	public SpriteSheet getSpriteSheet(@NotNull String key) {
		return spriteSheets.getOrDefault(key, null);
	}
	
	public void removeSpriteSheet(@NotNull String key) {
		spriteSheets.remove(key);
	}
	
	public void loadShader(@NotNull String key, @Nullable Shader shader) {
		shaders.put(key, shader);
	}
	
	public void loadShader(@NotNull String key, @NotNull String path) throws IOException, ShaderException {
		Shader shader = new Shader(path);
		shader.compile();
		shaders.put(key, shader);
	}
	
	@NotNull
	public Shader getShader(@NotNull String key) {
		return shaders.getOrDefault(key, shaders.get("default"));
	}
	
	public void removeShader(@NotNull String key) {
		shaders.remove(key).destroy();
	}
	
	public Collection<Shader> getShaders() {
		return shaders.values();
	}
	
	public void loadLanguage(@NotNull Languages key, @NotNull InputStream stream) throws ParseException, IOException {
		Language lang = new Language(stream, key.name());
		languages.put(key, lang);
	}

	public void loadLanguage(@NotNull Languages key) throws ParseException, IOException {
		Language lang = new Language(key);
		languages.put(key, lang);
	}
	
	public void loadSound(@NotNull String key, @Nullable Sound sound) {
		sounds.put(key, sound);
	}
	
	@Nullable
	public Sound getSound(@NotNull String key) {
		return sounds.getOrDefault(key, null);
	}
	
	public Collection<Sound> getAllSounds() {
		return sounds.values();
	}
	
	@Nullable
	public Language getLanguage(@NotNull Languages key) {
		return languages.getOrDefault(key, null);
	}
	
	public void removeLanguage(@NotNull Languages key) {
		if(key == Languages.getDefaultLanguage())
			return;
		languages.remove(key);
	}
	
	// Simple folder management without SecurityManager
	
	@NotNull
	public static File getDataFolder() {
		File dataDir = new File("data");
		if (!dataDir.exists()) {
			dataDir.mkdirs();
		}
		return dataDir;
	}
	
	@NotNull
	public static File requestDataFolder(@NotNull String path) throws IOException {
		File folder = new File(getDataFolder(), path);
		if (!folder.exists()) {
			folder.mkdirs();
		}
		return folder;
	}
	
	@NotNull
	public static File getCacheFolder() {
		File cacheDir = new File("cache");
		if (!cacheDir.exists()) {
			cacheDir.mkdirs();
		}
		return cacheDir;
	}
	
	@NotNull
	public static File getLogFolder() {
		File logDir = new File("logs");
		if (!logDir.exists()) {
			logDir.mkdirs();
		}
		return logDir;
	}
	
	@NotNull
	public static File getRootFolder() {
		return new File(".");
	}
	
	@NotNull
	public static File getScreenshotFolder() {
		File screenshotDir = new File("screenshots");
		if (!screenshotDir.exists()) {
			screenshotDir.mkdirs();
		}
		return screenshotDir;
	}
	
	@NotNull
	public static File createEmptyFile(@NotNull String localPath) throws IOException {
		File file = new File(getDataFolder(), localPath);
		file.getParentFile().mkdirs();
		file.createNewFile();
		return file;
	}
	
	public static void copyData(@NotNull InputStream in, @NotNull OutputStream out) throws IOException {
		byte[] buff = new byte[1024];
		int length = 0;
		while((length = in.read(buff)) > 0) {
			out.write(buff, 0, length);
		}
	}
	
	@NotNull
	public static byte[] readBytesStream(@NotNull InputStream stream) throws IOException {
		return stream.readAllBytes();
	}
	
	@NotNull
	public static String readStringStream(@NotNull InputStream stream) throws IOException {
		return new String(readBytesStream(stream));
	}
	
	public static void writeBytesStream(@NotNull OutputStream stream, byte[] bytes) throws IOException {
		BufferedOutputStream out = new BufferedOutputStream(stream);
		out.write(bytes);
		out.flush();
		out.close();
	}
	
	public static void writeStringStream(@NotNull OutputStream stream, String str) throws IOException {
		writeBytesStream(stream, str.getBytes());
	}

	public Icon getIcon() {
		return icon;
	}

	public void setIcon(Icon icon) {
		this.icon = icon;
	}
	
}
