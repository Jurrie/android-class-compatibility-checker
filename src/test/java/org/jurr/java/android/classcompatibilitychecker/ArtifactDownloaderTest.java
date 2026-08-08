package org.jurr.java.android.classcompatibilitychecker;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ArtifactDownloaderTest
{
	@Test
	void testFormatHex()
	{
		// Arrange
		final byte[] data = new byte[] { 0x01, 0x02, 0x03, 0x04, 0x05 };

		// Act
		final String hexString = ArtifactDownloader.formatHex(data);

		// Assert
		assertEquals("0102030405", hexString);
	}
}
