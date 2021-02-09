// Decompiled by DJ v3.8.8.85 Copyright 2005 Atanas Neshkov  Date: 2010-4-17 3:13:01
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   ImageViewer.java
package test.Julia.ImageViewer;

import java.applet.Applet;
import java.applet.AppletContext;
import java.awt.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.StringTokenizer;
import java.util.Vector;

public class ImageViewer extends Applet
    implements Runnable
{

    public ImageViewer()
    {
        WebAppstogo = "WebAppstogo.com";
        Author = "ImageViewer - Copyright(c) 2001 WebAppstogo";
        Registered = "1LFJFGI5KDE";
        hfont = new Font("TimesRoman", 2, 15);
        hfm = getFontMetrics(hfont);
        bTrialVersion = true;
        bloaded = false;
        bStatusWindow = false;
        vImageStats = new Vector();
        bDrawing = false;
        iImage = 0;
        iMode = 2;
        iSpeed = 0;
        iImageSize = 0;
        iTotalImages = 0;
        iCurrentImageCnt = 0;
        bSetHighlight = false;
        bUrlActive = false;
        bAuthor = false;
    }

    public void init()
    {
        Graphics g = getGraphics();
        Dimension dimension = size();
        offImage = createImage(size().width, size().height);
        offScreen = offImage.getGraphics();
    }

    public void loadImages()
    {
        int i = 0;
        int j = 0;
        int k = 0;
        int l = 0;
        int i1 = 0;
        Image image = null;
        Image image1 = null;
        szStatusWindow = getParameter("Status");
        if(szStatusWindow != null)
            bStatusWindow = true;
        String s7 = getParameter("Reg");
        if(s7 != null && s7.equalsIgnoreCase(Registered))
            bTrialVersion = false;
        String s4 = getParameter("Author");
        if(s4 == null)
            return;
        if(s4.equalsIgnoreCase(Author))
            bAuthor = true;
        else
            return;
        String s2;
        do
        {
            String s = "Image".concat(String.valueOf(String.valueOf(++i)));
            s2 = getParameter(s);
        } while(s2 != null);
        iTotalImages = i;
        i = 0;
        s4 = getParameter("Speed");
        if(s4 == null)
        {
            iImageSize = 15;
        } else
        {
            iSpeed = Integer.parseInt(s4);
            if(iSpeed > 50)
                iSpeed = 50;
            if(iSpeed < 1)
                iSpeed = 1;
        }
        s4 = getParameter("ImageSize");
        if(s4 == null)
        {
            iImageSize = 4;
        } else
        {
            iImageSize = Integer.parseInt(s4);
            if(iImageSize > 100)
                iImageSize = 100;
            if(iImageSize < 1)
                iImageSize = 1;
        }
        s4 = "background";
        String s8 = getParameter(s4);
        if(s8 == null)
            BackgroundColor = Color.black;
        else
            BackgroundColor = getColor(getParameter(s4));
        showStatus("Loading Images...");
        do
        {
            i++;
            String s5 = "Url".concat(String.valueOf(String.valueOf(i)));
            String s6 = getParameter(s5);
            String s1 = "Image".concat(String.valueOf(String.valueOf(i)));
            String s3 = getParameter(s1);
            iCurrentImageCnt = i - 1;
            if(s3 == null)
                break;
            try
            {
                image = getImage(new URL(getDocumentBase(), s3));
            }
            catch(MalformedURLException malformedurlexception) { }
            while(image.getWidth(this) == -1) ;
            if(image1 != null)
                vImageStats.addElement(new ImageStats(image, l, i1, image1.getWidth(this) / iImageSize, image1.getHeight(this) / iImageSize, s6));
            else
                vImageStats.addElement(new ImageStats(image, l, i1, image.getWidth(this) / iImageSize, image.getHeight(this) / iImageSize, s6));
            image1 = image;
            l = (image1.getWidth(this) / iImageSize) * ++j;
            if(l + image1.getWidth(this) / iImageSize > size().width)
            {
                l = 0;
                j = 0;
                i1 = (image1.getHeight(this) / iImageSize) * ++k;
            }
        } while(true);
        if(bStatusWindow && !bTrialVersion)
            showStatus(szStatusWindow);
        else
            showStatus(Author);
    }

    public void start()
    {
        if(loopThread == null)
        {
            loopThread = new Thread(this);
            loopThread.start();
        }
        if(!bloaded && loadThread == null)
        {
            loadThread = new Thread(this);
            loadThread.start();
        }
    }

    public void stop()
    {
        if(loopThread != null)
        {
            loopThread.stop();
            loopThread = null;
        }
        if(loadThread != null)
        {
            loadThread.stop();
            loadThread = null;
        }
    }

    public void run()
    {
        Thread.currentThread().setPriority(1);
        long l = System.currentTimeMillis();
        if(!bloaded && Thread.currentThread() == loadThread)
        {
            loadImages();
            bloaded = true;
            loadThread.stop();
        }
        do
        {
            if(Thread.currentThread() != loopThread)
                break;
            if(!bUrlActive && bDrawing)
            {
                bDrawing = false;
                startImage(iImage, iMode);
            }
            repaint();
            try
            {
                l += 10;
                Thread.sleep(Math.max(0L, l - System.currentTimeMillis()));
                continue;
            }
            catch(InterruptedException interruptedexception) { }
            break;
        } while(true);
    }

    Color getColor(String s)
    {
        StringTokenizer stringtokenizer = new StringTokenizer(s, ",");
        try
        {
            int i = Integer.parseInt(stringtokenizer.nextToken());
            int j = Integer.parseInt(stringtokenizer.nextToken());
            int k = Integer.parseInt(stringtokenizer.nextToken());
            Color color = new Color(i, j, k);
            return color;
        }
        catch(Exception exception)
        {
            Color color1 = Color.black;
            return color1;
        }
    }

    public boolean mouseExit(Event event, int i, int j)
    {
        bSetHighlight = false;
        return true;
    }

    public boolean mouseBrowse(int i, int j, int k, int l, String s, FontMetrics fontmetrics)
    {
        int i1 = k + fontmetrics.stringWidth(s);
        int k1 = l + (fontmetrics.getHeight() - 20);
        return i >= k && j >= l - fontmetrics.getHeight() && i <= i1 && j <= k1;
    }

    public boolean mouseMove(Event event, int i, int j)
    {
        int k = size().width - hfm.stringWidth(WebAppstogo) - 5;
        int l = size().height - (hfm.getHeight() - 15);
        if(mouseBrowse(i, j, k, l, WebAppstogo, hfm))
            bSetHighlight = true;
        else
            bSetHighlight = false;
        return true;
    }

    public boolean mouseDown(Event event, int i, int j)
    {
        if(!bloaded)
            return true;
        if(bUrlActive)
            return true;
        if(bSetHighlight && bTrialVersion)
        {
            try
            {
                AppletContext appletcontext = getAppletContext();
                appletcontext.showDocument(new URL("http://www.webappstogo.com"));
            }
            catch(MalformedURLException malformedurlexception) { }
            bUrlActive = true;
            return true;
        }
        if(bDrawing)
            return true;
        for(int k = 0; k < vImageStats.size(); k++)
        {
            ImageStats imagestats = (ImageStats)vImageStats.elementAt(k);
            if(imagestats.State == 1)
                if(imagestats.IsUrlAvailable())
                {
                    AppletContext appletcontext1 = getAppletContext();
                    appletcontext1.showDocument(imagestats.GetUrl());
                    bUrlActive = true;
                    return true;
                } else
                {
                    imagestats.State = iMode = 2;
                    bDrawing = true;
                    iImage = k;
                    return true;
                }
            if(imagestats.CheckBrowse(i, j))
            {
                bDrawing = true;
                iImage = k;
                imagestats.State = iMode = 1;
            }
        }

        return true;
    }

    public void startImage(int i, int j)
    {
        switch(j)
        {
        case 1: // '\001'
            scroll(i, 1, 0, 0);
            break;

        case 2: // '\002'
            scroll(i, 2, 0, 0);
            break;
        }
    }

    public void scroll(int i, int j, int k, int l)
    {
        ImageStats imagestats = (ImageStats)vImageStats.elementAt(i);
        int i1 = size().width;
        int k1 = size().height;
label0:
        switch(j)
        {
        default:
            break;

        case 1: // '\001'
            while(imagestats.xImageSize < i1 || imagestats.yImageSize < k1 || imagestats.xImagePos != 0 || imagestats.yImagePos != 0) 
            {
                if(imagestats.xImagePos > 0)
                    imagestats.xImagePos -= iSpeed * 2;
                else
                    imagestats.xImagePos = 0;
                if(imagestats.yImagePos > 0)
                    imagestats.yImagePos -= iSpeed * 2;
                else
                    imagestats.yImagePos = 0;
                if(imagestats.xImageSize < i1)
                    imagestats.xImageSize += iSpeed * 2;
                else
                    imagestats.xImageSize = i1;
                if(imagestats.yImageSize < k1)
                    imagestats.yImageSize += iSpeed * 2;
                else
                    imagestats.yImageSize = k1;
                repaint();
                pause(5);
            }
            break;

        case 2: // '\002'
            do
            {
                if(imagestats.xImageSize <= imagestats.xImageSizeOrg && imagestats.yImageSize <= imagestats.yImageSizeOrg && imagestats.xImagePos == imagestats.xImagePosOrg && imagestats.yImagePos == imagestats.yImagePosOrg)
                    break label0;
                if(imagestats.xImagePos < imagestats.xImagePosOrg)
                    imagestats.xImagePos += iSpeed * 2;
                else
                    imagestats.xImagePos = imagestats.xImagePosOrg;
                if(imagestats.yImagePos < imagestats.yImagePosOrg)
                    imagestats.yImagePos += iSpeed * 2;
                else
                    imagestats.yImagePos = imagestats.yImagePosOrg;
                if(imagestats.xImageSize > imagestats.xImageSizeOrg)
                    imagestats.xImageSize -= iSpeed * 2;
                else
                    imagestats.xImageSize = imagestats.xImageSizeOrg;
                if(imagestats.yImageSize > imagestats.yImageSizeOrg)
                    imagestats.yImageSize -= iSpeed * 2;
                else
                    imagestats.yImageSize = imagestats.yImageSizeOrg;
                repaint();
                pause(5);
            } while(true);
        }
    }

    public void pause(int i)
    {
        try
        {
            Thread.sleep(i);
        }
        catch(InterruptedException interruptedexception) { }
    }

    public void paint(Graphics g)
    {
        update(g);
    }

    public void update(Graphics g)
    {
        offScreen.setColor(BackgroundColor);
        offScreen.fillRect(0, 0, size().width, size().height);
        if(!bAuthor)
        {
            offScreen.setColor(Color.red);
            offScreen.drawString("Fill in author value with", 0, size().height / 2);
            offScreen.drawString(Author, 0, size().height / 2 + hfm.getHeight());
        } else
        if(!bloaded)
        {
            offScreen.setColor(Color.white);
            offScreen.drawString(String.valueOf(String.valueOf((new StringBuffer("Loading image ")).append(iCurrentImageCnt).append(" of ").append(iTotalImages - 1))), size().width / 3, size().height / 2);
        } else
        {
            for(int i = 0; i < vImageStats.size(); i++)
            {
                ImageStats imagestats1 = (ImageStats)vImageStats.elementAt(i);
                offScreen.drawImage(imagestats1.Img, imagestats1.xImagePos, imagestats1.yImagePos, imagestats1.xImageSize, imagestats1.yImageSize, this);
            }

            ImageStats imagestats = (ImageStats)vImageStats.elementAt(iImage);
            offScreen.drawImage(imagestats.Img, imagestats.xImagePos, imagestats.yImagePos, imagestats.xImageSize, imagestats.yImageSize, this);
            if(bTrialVersion)
            {
                if(bSetHighlight)
                {
                    setCursor(Cursor.getPredefinedCursor(12));
                    offScreen.setColor(Color.blue);
                } else
                {
                    setCursor(Cursor.getPredefinedCursor(0));
                    offScreen.setColor(Color.white);
                }
                int j = size().width - hfm.stringWidth(WebAppstogo) - 5;
                int k = size().height - (hfm.getHeight() - 10);
                offScreen.setFont(hfont);
                offScreen.drawString(WebAppstogo, j, k);
            }
        }
        g.drawImage(offImage, 0, 0, this);
    }

    String WebAppstogo;
    String Author;
    String Registered;
    String szStatusWindow;
    Font hfont;
    FontMetrics hfm;
    Color hColor;
    boolean bTrialVersion;
    boolean bloaded;
    boolean bStatusWindow;
    Thread loopThread;
    Thread loadThread;
    Image offImage;
    Graphics offScreen;
    Vector vImageStats;
    final int EXPAND = 1;
    final int SHRINK = 2;
    final int CS_WAIT = 1;
    boolean bDrawing;
    int iImage;
    int iMode;
    int iSpeed;
    int iImageSize;
    int iTotalImages;
    int iCurrentImageCnt;
    boolean bSetHighlight;
    boolean bUrlActive;
    boolean bAuthor;
    Color BackgroundColor;
    Image j1;
    Image j2;
    Image j3;
    Image j4;
}