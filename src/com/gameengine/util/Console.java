package com.gameengine.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Console extends PrintStream {
	
	private static final OutputStream consoleStream = createStream();

	public static final Console out = new Console("info", System.out, consoleStream);
	public static final Console err = new Console("error", System.err, consoleStream);
	public static final Console warn = new Console("warn", System.out, consoleStream);

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	private static final List<String> lastLines = new ArrayList<>();
	private static int maxLinesBufferSize = 300;
	
	private final String prefix;
	private BufferedWriter writer;
	private PrintStream system;

	private boolean isNewLine = true;

	@Nullable
	private static OutputStream createStream() {
		OutputStream output = null;
		try {
			// Use a simple logs directory instead of SecurityManager
			File logDir = new File("logs");
			if (!logDir.exists()) {
				logDir.mkdirs();
			}
			File log = new File(logDir, "lastest-log.txt");

			if (log.exists()) {
				try {
					// CREATES THE COPY FILE
					Date logModifiedDate = new Date(log.lastModified());
					SimpleDateFormat logDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
					File copyLog = new File(logDir, "log-" + logDateFormat.format(logModifiedDate) + ".log");
					copyLog.getParentFile().mkdirs();
					copyLog.createNewFile();

					// CREATES INPUT AND OUTPUT TO COPY DATA
					InputStream in = new FileInputStream(log);
					OutputStream out = new FileOutputStream(copyLog);

					// COPY THE DATA
					byte[] buffer = new byte[1024];
					int lengthRead;
					while ((lengthRead = in.read(buffer)) > 0) {
						out.write(buffer, 0, lengthRead);
						out.flush();
					}

					// CLOSE IN AND OUT
					in.close();
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			output = new FileOutputStream(log);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return output;
	}

	/**
	 * Creates a new Console
	 * 
	 * @param prefix
	 * @param systemStream
	 * @param stream
	 */
	private Console(@NotNull String prefix, @NotNull PrintStream systemStream, @NotNull OutputStream stream) {
		super(stream);

		writer = new BufferedWriter(new OutputStreamWriter(super.out));
		this.system = systemStream;
		this.prefix = "[" + prefix.toUpperCase() + "][";
	}

	/**
	 * Generate the log prefix
	 * 
	 * @return the prefix
	 */
	private String generatePrefix() {
		return isNewLine ? prefix + dateFormat.format(new Date()) + "]: " : "";
	}

	@Override
	public synchronized void print(@NotNull boolean x) {
		write(generatePrefix() + x);
	}

	@Override
	public synchronized void print(@NotNull char x) {
		write(generatePrefix() + x);
	}

	@Override
	public synchronized void print(@NotNull char[] x) {
		write(generatePrefix() + new String(x));
	}

	@Override
	public synchronized void print(@NotNull double x) {
		write(generatePrefix() + x);
	}

	@Override
	public synchronized void print(@NotNull float x) {
		write(generatePrefix() + x);
	}

	@Override
	public synchronized void print(@NotNull int x) {
		write(generatePrefix() + x);
	}

	@Override
	public synchronized void print(@NotNull long x) {
		write(generatePrefix() + x);
	}

	@Override
	public synchronized void print(@NotNull Object x) {
		write(generatePrefix() + x);
	}

	@Override
	public synchronized void print(@NotNull String x) {
		write(generatePrefix() + x);
	}

	@Override
	public synchronized void println() {
		writeln(generatePrefix());
	}

	@Override
	public synchronized void println(@NotNull boolean x) {
		writeln(generatePrefix() + x);
	}

	@Override
	public synchronized void println(@NotNull char x) {
		writeln(generatePrefix() + x);
	}

	@Override
	public synchronized void println(@NotNull char[] x) {
		writeln(generatePrefix() + new String(x));
	}

	@Override
	public synchronized void println(@NotNull double x) {
		writeln(generatePrefix() + x);
	}

	@Override
	public synchronized void println(@NotNull float x) {
		writeln(generatePrefix() + x);
	}

	@Override
	public synchronized void println(@NotNull int x) {
		writeln(generatePrefix() + x);
	}

	@Override
	public synchronized void println(@NotNull long x) {
		writeln(generatePrefix() + x);
	}

	@Override
	public synchronized void println(@NotNull Object x) {
		writeln(generatePrefix() + x);
	}

	@Override
	public synchronized void println(@NotNull String x) {
		writeln(generatePrefix() + x);
	}

	private void write(@NotNull String str) {
		synchronized (this) {
			try {
				isNewLine = str.endsWith("\n");
				writer.write(str);
				writer.flush();
				system.print(str);
				writeOnBuffer(str);
			} catch (IOException e) { }
		}
	}

	private void writeln(@NotNull String str) {
		synchronized (this) {
			try {
				isNewLine = true;
				str += "\n";
				writer.write(str);
				writer.flush();
				system.print(str);
				writeOnBuffer(str);
			} catch (IOException e) { }
		}
	}
	
	private static void writeOnBuffer(String str) {
		if(lastLines.size() >= maxLinesBufferSize) {
			lastLines.remove(0);
			lastLines.add(str);
		} else {
			lastLines.add(str);
		}
	}
	
	public static void clearLinesBuffer() {
		lastLines.clear();
	}
	
	public static int getMaxLinesBufferSize() {
		return maxLinesBufferSize;
	}
	
	public static void setMaxLinesBufferSize(int size) {
		if(size > maxLinesBufferSize)
			maxLinesBufferSize = size;
		else {
			while(lastLines.size() > size) {
				lastLines.remove(0);
			}
		}
	}
	
	public static void foreachLinesBuffer(Consumer<String> cons) {
		for(String line : lastLines) {
			cons.accept(line);
		}
	}

}
