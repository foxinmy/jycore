package com.foxinmy.jycore.url;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import com.foxinmy.jycore.util.ImageUtil;

/**
 * @author jy.hu , 2012-10-26
 */
public class ImageHolder {

	private static final String DEFAULT_DIR = "D:\\images";
	private static final String REQUIRE_PNT = "(^\\s*)|(\\s*$)";
	private static final String URL_PNT = "http://([\\w-]+\\.)+[\\w-]+(/[\\w- ./@?%&=]*)?";
	private static final String IMG_PNT = "(?<=<(\\s)?img(\\s)?(.*)?(src|file)(\\s)?=(\\s)?('|\"))((?!('|\")).)*";

	private static final String DEST_FORMAT_NAME = "jpg";

	private String url;// 链接的网址
	private String dir;// 存储的目录
	private String ptn;// 提取特定模块的正则表达式

	public ImageHolder(String url) {
		this.url = url;
		this.dir = DEFAULT_DIR;
	}

	public ImageHolder(String url, String dir) {
		this.url = url;
		this.dir = dir;
	}

	public ImageHolder(String url, String dir, String ptn) {
		this.url = url;
		this.dir = dir;
		this.ptn = ptn;
	}

	private boolean isPatten(String regex, String input) {
		Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE
				| Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(input);
		return matcher.matches();
	}

	public String getUrl() {
		if (url != null && isPatten(REQUIRE_PNT, url))
			return null;
		return url;
	}

	public String getDir() {
		if (url != null && isPatten(REQUIRE_PNT, dir))
			return null;
		File file = new File(dir);
		if (!file.exists())
			file.mkdir();
		return dir;
	}

	public String getPtn() {
		if (ptn != null && isPatten(REQUIRE_PNT, ptn))
			return null;
		return ptn;
	}

	// 抓取网页上的图片资源
	public void imageHolder() {
		url = getUrl();
		dir = getDir();
		ptn = getPtn();

		// 非空验证
		if (url == null || dir == null) {
			System.out.println("链接地址或者存储目录不能为空!");
			return;
		}

		// 网站验证

		if (!isPatten(URL_PNT, url)) {
			System.out.println("链接地址格式不正确!");
			return;
		}

		BufferedReader reader = null;
		Pattern pattern = null;
		Matcher matcher = null;
		URL u = null;
		URLConnection c = null;
		BufferedImage image = null;
		String format = null;
		File file = null;
		InputStream inputStream = null;
		StringBuilder sb = null;
		String s = null;
		String prefix = null;
		String path = null;
		String name = null;
		String fpath = null;
		String urlPath = null;
		String hostUrl = null;
		int indexOf = 0;

		// 解析开始--------
		System.out.println(String.format("--------解析网址开始-------------%1$s\n",
				System.currentTimeMillis()));
		try {
			u = new URL(url);

			c = u.openConnection();
			reader = new BufferedReader(new InputStreamReader(
					c.getInputStream(), "GBK"));

			sb = new StringBuilder();
			for (; (s = reader.readLine()) != null;) {
				sb.append(s);// .append(System.getProperty("line.separator"));
			}
		} catch (Exception e) {
			System.out.println("打开连接时异常!!");
		}
		if (ptn != null) {
			pattern = Pattern.compile(ptn, Pattern.CASE_INSENSITIVE
					| Pattern.MULTILINE);
			matcher = pattern.matcher(sb.toString());
			sb.setLength(0);
			while (matcher.find()) {
				sb.append(matcher.group());
			}
		}
		pattern = Pattern.compile(IMG_PNT, Pattern.CASE_INSENSITIVE
				| Pattern.MULTILINE);
		matcher = pattern.matcher(sb.toString());
		
		hostUrl = String.format("%1$s://%2$s", u.getProtocol(),
				u.getHost());
		
		urlPath = u.getPath();
		// 下载开始------
		int index = 1;
		while (matcher.find()) {
			// System.out.println(String.format("%1$s,%2$d,%3$d",matcher.group(),
			// matcher.start(), matcher.end()));
			path = matcher.group();
			if (!path.startsWith("http")) {
				if (path.charAt(0) != '/') {
					indexOf = urlPath.lastIndexOf("/");
					if (indexOf < 0)
						indexOf = urlPath.length();
					prefix = String.format("%1$s%2$s/", hostUrl, urlPath
							.substring(0, indexOf));
				}else{
					prefix = hostUrl;
				}
				if (!isPatten(URL_PNT, path)) {
					path = String.format("%1$s%2$s", prefix, path);
				}
			}
			System.out.println(String.format("开始下载第%1$d个图片....", index));
			try {
				u = new URL(path);
				c = u.openConnection();
				inputStream = c.getInputStream();
				format = ImageUtil.getFormatName(inputStream);
				if (format == null)
					format = DEST_FORMAT_NAME;
				name = String.valueOf(System.currentTimeMillis());
				fpath = String.format("%1$s\\%2$s.%3$s", dir, name, format);

				file = new File(fpath);

				image = ImageIO.read(u);
				if (image == null)
					image = ImageUtil.toBufferedImage(u);
				if (image == null)
					image = ImageUtil.getBufferedImage(format, u);

			} catch (MalformedURLException e) {
				System.out.println("嗷嗷嗷,图片路径不正确!");
			} catch (SocketException e) {
				System.out.println("连接被重置,什么破网络!");
			} catch (FileNotFoundException e) {
				System.out.println("404,你应该懂得!");
			} catch (IOException e) {
				image = ImageUtil.getBufferedImage(format, u);
			} catch (IllegalArgumentException e) {
				System.out.println("传入的参数错误!");
			} catch (Exception e) {
				image = ImageUtil.getBufferedImage(format, u);
			} finally {
				try {
					ImageIO.write(image, format, file);
					System.out.println(String.format("下载完成,保存路径为 %1$s\r\n", fpath));
				} catch (Exception e) {
					if (file != null && file.exists())
						file.delete();
					System.out.println("下载错误:写入图片时异常!\r\n");
				}
				index += 1;
			}
		}

		if (reader != null) {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (inputStream != null) {
			try {
				inputStream.close();
			} catch (IOException e) {
				System.out.println("关闭流异常!");
			}
		}
		System.out.println(String.format("\n--------解析网址结束-------------%1$s",
				System.currentTimeMillis()));
	}

	// 方法入口
	public static void main(String[] args) {
	    /* //(/)
		final String URL = "http://qq.yhcgo.com/html/127.html";
		final String DIR = "D:\\images";
		final String PTN = "<p(\\s*)style=('|\")text-align(.*?)('|\")?[^>]*?>.*?<\\/p>";
		new ImageHolder(URL, DIR, PTN).imageHolder();
		*/
		/* //(http)
		final String URL  = "http://www.sz5983.com/bbs2/forum.php?mod=viewthread&tid=3768";
		final String DIR = "D:\\images";
		final String PTN = "<td(\\s*)class=('|\")t_f('|\")?[^>]*?>.*?<\\/td>";
		new ImageHolder(URL,DIR,PTN).imageHolder();
		*/
		/* //(img)
		final String URL  = "http://www.sz5983.com/bbs2/thread-32245-1-1.html";
		final String DIR = "D:\\images";
		final String PTN = "<td(\\s*)class=('|\")t_f('|\")?[^>]*?>.*?<\\/td>";
		new ImageHolder(URL,DIR,PTN).imageHolder();
		*/
	}
}
