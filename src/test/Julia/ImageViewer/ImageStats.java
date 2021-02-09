// Decompiled by DJ v3.8.8.85 Copyright 2005 Atanas Neshkov  Date: 2010-4-6 17:15:45
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   ImageStats.java
package test.Julia.ImageViewer;

import java.awt.Image;
import java.net.MalformedURLException;
import java.net.URL;

public class ImageStats
{

    ImageStats(Image image, int i, int j, int k, int l, String s)
    {
        Img = image;
        xImagePos = xImagePosOrg = i;
        yImagePos = yImagePosOrg = j;
        xImageSize = xImageSizeOrg = k;
        yImageSize = yImageSizeOrg = l;
        State = 2;
        xImg1 = i;
        yImg1 = j;
        xImg2 = i + k;
        yImg2 = j + l;
        try
        {
            UrlLink = new URL(s);
            bUrlLink = true;
        }
        catch(MalformedURLException malformedurlexception)
        {
            bUrlLink = false;
        }
    }

    boolean IsUrlAvailable()
    {
        return bUrlLink;
    }

    URL GetUrl()
    {
        return UrlLink;
    }

    boolean CheckBrowse(int i, int j)
    {
        return i >= xImg1 && i <= xImg2 && j >= yImg1 && j <= yImg2;
    }

    final int EXPAND = 1;
    final int SHRINK = 2;
    int xImagePos;
    int yImagePos;
    int xImageSize;
    int yImageSize;
    int xImageSizeOrg;
    int yImageSizeOrg;
    int xImagePosOrg;
    int yImagePosOrg;
    int State;
    Image Img;
    URL UrlLink;
    boolean bUrlLink;
    int xImg1;
    int yImg1;
    int xImg2;
    int yImg2;
}