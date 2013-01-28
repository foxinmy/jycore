package com.foxinmy.jycore.util;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.ImageIcon;

/**
 * @author jy.hu , 2012-10-26
 */
public class ImageUtil {

	// 获取图片格式
	public static String getFormatName(Object o) {
		try {
			ImageInputStream iis = ImageIO.createImageInputStream(o);
			Iterator<ImageReader> iter = ImageIO.getImageReaders(iis);
			if (!iter.hasNext()) {
				return null;
			}
			ImageReader reader = (ImageReader) iter.next();
			iis.close();
			return reader.getFormatName().toLowerCase();
		} catch (IOException e) {
		}
		return null;
	}

	// file资源转换为BufferedImage
	public static BufferedImage toBufferedImage(File file) {
		Image image = Toolkit.getDefaultToolkit().getImage(file.getPath());
		return imageToBufferedImage(image);
	}

	// url资源转换为BufferedImage
	public static BufferedImage toBufferedImage(URL url) {
		Image image = Toolkit.getDefaultToolkit().getImage(url);
		return imageToBufferedImage(image);
	}

	private static BufferedImage imageToBufferedImage(Image image) {
		BufferedImage bimage = null;
		if (image instanceof BufferedImage) {
			return (BufferedImage) image;
		}

		image = new ImageIcon(image).getImage();

		int width = image.getWidth(null);
		int height = image.getHeight(null);
		if (width < 0 || height < 0) {
			return null;
		}
		GraphicsEnvironment ge = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		try {
			int transparency = Transparency.OPAQUE;
			GraphicsDevice gs = ge.getDefaultScreenDevice();
			GraphicsConfiguration gc = gs.getDefaultConfiguration();
			bimage = gc.createCompatibleImage(width, height, transparency);
		} catch (HeadlessException e) {
			return null;
		} catch (Exception e) {
			return null;
		}

		if (bimage == null) {
			int type = BufferedImage.TYPE_INT_RGB;
			bimage = new BufferedImage(image.getWidth(null),
					image.getHeight(null), type);
		}
		Graphics2D g = bimage.createGraphics();
		g.drawImage(image, 0, 0, null);
		g.dispose();
		return bimage;
	}

	// 读取损坏的图片
	public static BufferedImage getBufferedImage(String format, URL url) {
		BufferedImage buff = null;
		try {
			Iterator<ImageReader> readers = ImageIO
					.getImageReadersByFormatName(format);
			ImageReader reader = null;
			while (readers.hasNext()) {
				reader = (ImageReader) readers.next();
				if (reader.canReadRaster()) {
					break;
				}
			}
			ImageInputStream input = ImageIO.createImageInputStream(url);
			if (input == null) {
				return toBufferedImage(url);
			}
			reader.setInput(input);
			Raster raster = reader.readRaster(0, null);

			int imageType;
			switch (raster.getNumBands()) {
			case 1:
				imageType = BufferedImage.TYPE_BYTE_GRAY;
				break;
			case 3:
				imageType = BufferedImage.TYPE_3BYTE_BGR;
				break;
			case 4:
				imageType = BufferedImage.TYPE_4BYTE_ABGR;
				break;
			default:
				throw new UnsupportedOperationException();
			}
			buff = new BufferedImage(raster.getWidth(), raster.getHeight(),
					imageType);

			buff.getRaster().setRect(raster);
			return buff;
		} catch (Exception e) {
			return null;
		}
	}
}
