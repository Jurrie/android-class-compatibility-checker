package org.jurr.java.android.classcompatibilitychecker;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Locale;

public final class ArtifactDownloader
{
	private ArtifactDownloader()
	{
	}

	public static void downloadArtifactToTempDirectoryIfNotExits(final URI uri, final Path fileName) throws IOException
	{
		if (Files.exists(fileName))
		{
			return;
		}

		final byte[] digest;
		try
		{
			final MessageDigest sha256MessageDigest = MessageDigest.getInstance("SHA-256");
			final DigestInputStream in = new DigestInputStream(uri.toURL().openStream(), sha256MessageDigest);
			Files.copy(in, fileName, StandardCopyOption.REPLACE_EXISTING);

			digest = sha256MessageDigest.digest();
		}
		catch (NoSuchAlgorithmException e)
		{
			throw new IOException("SHA-256 algorithm not available", e);
		}

		final Path sha256FileName = Paths.get(fileName.toString() + ".sha256");
		final URI sha256Uri = URI.create(uri.toString() + ".sha256");
		final InputStream sha256In = sha256Uri.toURL().openStream();
		Files.copy(sha256In, sha256FileName, StandardCopyOption.REPLACE_EXISTING);

		final List<String> sha256HashLinesFromServer = Files.readAllLines(sha256FileName, StandardCharsets.UTF_8);
		if (sha256HashLinesFromServer.isEmpty())
		{
			throw new IOException("SHA-256 hash file is empty for " + fileName);
		}
		final String sha256HashFromServer = sha256HashLinesFromServer.get(0).trim();
		final String sha256HashFromFile = formatHex(digest);
		if (!sha256HashFromServer.equalsIgnoreCase(sha256HashFromFile))
		{
			throw new IOException("SHA-256 hash mismatch for " + fileName + ": expected " + sha256HashFromServer + ", got " + sha256HashFromFile);
		}
	}

	static String formatHex(final byte[] bytes)
	{
		final StringBuilder hex = new StringBuilder(bytes.length * 2);
		for (final byte value : bytes)
		{
			hex.append(String.format(Locale.ROOT, "%02x", value & 0xff));
		}
		return hex.toString();
	}
}
