/*
 * tarent commons,
 * a set of common components and solutions
 * Copyright (c) 2006-2007 tarent GmbH
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License,version 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 *
 * tarent GmbH., hereby disclaims all copyright
 * interest in the program 'tarent commons'
 * Signature of Elmar Geese, 14 June 2007
 * Elmar Geese, CEO tarent GmbH.
 */

package de.tarent.commons.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.io.FileOutputStream;

/**
 * Used to convert a mandatory file to a java class source file containig a static byte array containig
 * the content of the first file.
 * <p>
 * The file name of the desination is constructed as follows:<br>
 * destinationDir + packagePath (according to the package name) + className + ".java"
 *
 * @author Tim Steffens
 *
 */
public class FileToClassWithByteArray {

	private final static char DIR_SEPERATOR = '/';
	private final static int BUFFER_SIZE = 1024;
	private URI sourceFileURI;
	private String destinationDir;
	private String packageName;
	private String className;
	private String fieldName;

	public FileToClassWithByteArray(URI sourceFileURI, String destinationDir, String packageName, String className, String fieldName) {
		this.sourceFileURI = sourceFileURI;
		setDestinationDir(destinationDir);
		setPackageName(packageName);
		setClassName(className);
		setFieldName(fieldName);
	}

	public static void main(String[] args) {
		if (args.length != 5) {
			System.out.println("Invalid arguent count, expected 5 arguments:");
			System.out.println(" sourceFile destinationDir packageName className fieldName");
		}
		else {
			try {
				System.out.println("Starting conversion ...");
				(new FileToClassWithByteArray(new URI("file://" + args[0]), args[1], args[2], args[3], args[4])).performConversion();
				System.out.println("Successfull");
			} catch (IOException e) {
				e.printStackTrace();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
	}

	public void performConversion() throws IOException {
		FileInputStream source = new FileInputStream(new File(sourceFileURI));
		PrintWriter writer = new PrintWriter(new FileOutputStream(new File(getDestinationFileName())));
		printHeader(writer);
		printContent(source, writer);
		printFooter(writer);
		source.close();
		writer.flush();
		writer.close();
	}

	private String getDestinationFileName() {
		StringBuffer buffer = new StringBuffer();
		if (!getDestinationDir().equals("")) {
			buffer.append(getDestinationDir());
		}
		if (!getPackageName().equals("")) {
			buffer.append(getPackageName().replace('.', getDirSeperator()));
			buffer.append(getDirSeperator());
		}
		buffer.append(getClassName());
		buffer.append(".java");
		return buffer.toString();
	}

	private void printHeader(PrintWriter writer) {
		writer.println("package " + getPackageName() + ";");
		writer.println();
		writer.println("/**");
		writer.println(" * Contains a byte array with the content of file '" + getSourceFileURI() + "'");
		writer.println(" *");
		writer.println(" * @author Tim Steffens' FileToClassWithByteArray");
		writer.println(" */");
		writer.println("class " + getClassName() + " {");
		writer.println();
	}

	private void printContent(FileInputStream source, PrintWriter writer) throws IOException {
		writer.println("\tpublic final static byte[] " + getFieldName() + " = new byte[] {");
		byte[] buffer = new byte[getBufferSize()];
		boolean firstRun = true;
		for (int length = source.read(buffer); length != -1; length = source.read(buffer)) {
			if (!firstRun) {
				writer.println(",");
			}
			else {
				firstRun = false;
			}
			writer.print("\t\t");
			for (int i = 0; i < length; i++) {
				if (i != 0) {
					writer.print(", ");
				}
				writer.print(buffer[i]);
			}
		}
		writer.println();
		writer.println("\t};");
	}

	private int getBufferSize() {
		return BUFFER_SIZE;
	}

	private void printFooter(PrintWriter writer) {
		writer.println("}");
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className.trim();
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName.trim();
	}

	public URI getSourceFileURI() {
		return sourceFileURI;
	}

	public void setSourceFileURI(URI sourceFileURI) {
		this.sourceFileURI = sourceFileURI;
	}

	public String getDestinationDir() {
		return destinationDir;
	}

	public void setDestinationDir(String destinationDir) {
		if (destinationDir != null) {
			this.destinationDir = destinationDir.trim().endsWith((new Character(getDirSeperator())).toString())
				? destinationDir.trim()
				: destinationDir.trim() + getDirSeperator();
		}
		else {
			this.destinationDir = "";
		}
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		if (packageName != null) {
			this.packageName = packageName.trim();
		}
		else {
			this.packageName = "";
		}
	}

	public char getDirSeperator() {
		return DIR_SEPERATOR;
	}

}
