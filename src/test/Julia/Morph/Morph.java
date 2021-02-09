package test.Julia.Morph;

//Applet : Morph  -  Sep 11 '99.
//Author : Laxmi Narayana .Kota
// E-mail id: laxmikota@hotmail.com

import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.applet.*;


public class Morph extends Applet implements Runnable{
  boolean isStandalone = false;
  int w,h,cnt=0,cnt1=0,totalpictures,temp,max,delay;
  int buf[],clearbuf[],bufimg[][],imagewidth,imageheight;
  static int zoomout=1;
  int lsmargin[],rsmargin[],ldmargin[],rdmargin[],lscnt=0,rscnt=0,clear[],ldcnt=0,rdcnt=0;
  int srcdif,dstdif,tempdif,xdif,xinc;
  int tcnt=0, bcnt=0,tsmargin=0,bsmargin=0,tdmargin=0,bdmargin=0;
  int dsthdif,srchdif,preh,yinc;
// l - left ; r- right; ls - left source; rs - right source
// t- top of the image; b - bottom of the image
// ld - left margin of destination image
// rd - right margin 0f destination image

  Thread t;
  Image img[],img1;
  MediaTracker med;
  PixelGrabber pg[];
  String imagenames[],bgcolor,fontcolor,author;

  Graphics offscreengraphics;
  Image offscreenimage=null;

  public String getParameter(String key, String def) {
    return isStandalone ? System.getProperty(key, def) :
      (getParameter(key) != null ? getParameter(key) : def);
  }

  public Morph() {
  }

  public void init() {
    //t=new Thread(this);
    try{
        totalpictures=Integer.parseInt(getParameter("totalpictures","0"));
        bgcolor=this.getParameter("bgcolor","0x000000");
        fontcolor=this.getParameter("fontcolor","0x00ff00");
        max=Integer.parseInt(getParameter("fadecount","30"));
	      delay=Integer.parseInt(getParameter("delay","30"));
        author=getParameter("author","");
    }catch(Exception exce){}
    if(!author.equalsIgnoreCase("Laxminarayana.kota"))
    System.exit(0);
    this.setBackground(new Color(Integer.parseInt(bgcolor.substring(2),16)));
    img=new Image[totalpictures];
    imagenames=new String[totalpictures];
    if(totalpictures >0 )
    { try{
           for(int i=0;i<totalpictures;i++)
           imagenames[i]=getParameter("imagenames["+i+"]","");
         }catch(Exception exce){}
    }

  for(int i=0;i<totalpictures;i++)
    img[i]=getImage(getCodeBase(),imagenames[i]);

  med=new MediaTracker(this);
  for(int i=0;i<img.length;i++)
  med.addImage(img[i],0);

  //t.start();
  }
  public void start() {
   t= new Thread(this);
   t.start();
  }
  public void run()
  {
    try{
     med.waitForAll();
     imagewidth=img[0].getWidth(null);imageheight=img[0].getHeight(null);
     buf=new int[imagewidth*imageheight];clearbuf=new int[imagewidth*imageheight];
     lsmargin=new int[imageheight];rsmargin=new int[imageheight];
     ldmargin=new int[imageheight];rdmargin=new int[imageheight];
     clear=new int[imageheight];

     for(int i=0;i<imageheight;i++)
     for(int j=0;j<imagewidth;j++)
      {buf[imagewidth*i+j]=0xffffff; clearbuf[imagewidth*i+j]=0xffffff;}

     for(int i=0; i<imageheight; ++i)
      clear[i]=0;

     bufimg=new int[img.length][imagewidth*imageheight];
     clear=new int[imageheight];pg=new PixelGrabber[img.length];

     for(int i=0;i<img.length;i++)
     {pg[i]=new PixelGrabber(img[i],0,0,imagewidth,imageheight,bufimg[i],0,imagewidth);
      pg[i].grabPixels();
     }

   }catch (InterruptedException exce){getAppletContext().showStatus("Loading images is failed");}

   try{
    for(;;){
     cnt++;
     if(cnt>max)
     { cnt1++; if(cnt1>img.length-1)cnt1=0;
       cnt=0;
     }
     temp=(cnt1 <(img.length-1)) ? 1+cnt1 : 0;



   System.arraycopy(clearbuf,0,buf,0,imagewidth*imageheight);
   tcnt=0;

  /* System.arraycopy(clear,0,lsmargin,0,imageheight);
   System.arraycopy(clear,0,rsmargin,0,imageheight);
   System.arraycopy(clear,0,ldmargin,0,imageheight);
   System.arraycopy(clear,0,rdmargin,0,imageheight);*/
  if(cnt>=0 && cnt<=1){
   for(int i=0; i<imageheight; ++i) // left margin of source image
   {lscnt=0;rscnt=0;
    for(int j=0; j<zoomout*imagewidth; ++j)
    { if(lscnt==0 && j>0){
       if( filter(bufimg[cnt1][imagewidth*i+j*imagewidth/(zoomout*imagewidth)],11) != filter(bufimg[cnt1][imagewidth*i],11) ){
       lsmargin[i]=j/zoomout;lscnt++;
       if(tcnt==0){tsmargin=i;tcnt++;}else tcnt++;}
      }
    }
   }
   bsmargin=tcnt + tsmargin;tcnt=0; // takes up the bottom margin of the image

   for(int i=0; i<imageheight; ++i) // for right margin of soure image
   {rscnt=0;
    for(int j=0; j<zoomout*imagewidth; ++j)
    { if (rscnt==0 && j<zoomout*imagewidth){
       if( filter(bufimg[cnt1][imagewidth*i+(zoomout*imagewidth-1-j)*imagewidth/(zoomout*imagewidth)],11) != filter(bufimg[cnt1][imagewidth*i+imagewidth-1],11) ){
       rsmargin[i]=j/zoomout;rscnt++;}
      }
     }
    }

   for(int i=0; i<imageheight; ++i)// left margin of destination image
   {ldcnt=0;rdcnt=0;
    for(int j=0; j<zoomout*imagewidth; ++j)
    { if(ldcnt==0 && j>0){
       if( filter(bufimg[temp][imagewidth*i+j*imagewidth/(zoomout*imagewidth)],11) != filter(bufimg[temp][imagewidth*i],11) ){
       ldmargin[i]=j/zoomout;ldcnt++;
        if(tcnt==0){tdmargin=i;tcnt++;}else tcnt++; }
      }
    }
   }
  bdmargin=tcnt+tdmargin;tcnt=0; // bottom margin of the destination image

  for(int i=0; i<imageheight; ++i) //right margin of destination image
  { rdcnt=0;
    for(int j=0; j<zoomout*imagewidth; ++j)
    {  if (rdcnt==0 && j<zoomout*imagewidth){
       if( filter(bufimg[temp][imagewidth*i+(zoomout*imagewidth-1-j)*imagewidth/(zoomout*imagewidth)],11) != filter(bufimg[temp][imagewidth*i+imagewidth-1],11) ){
       rdmargin[i]=j/zoomout;rdcnt++;}
      }
    }
  }
  }
  for(int k=0;k<imageheight;++k)
  { dsthdif=bdmargin-tdmargin; //132
    srchdif=bsmargin-tsmargin; // 76
    yinc=tsmargin - (tsmargin - tdmargin)*cnt/max;//27-25*30/30=2
    preh=srchdif - (srchdif - dsthdif)*cnt/max;//76- (-56)*30/30=134
   if(k>=yinc && k<=yinc+preh)
   {for(int j=0;j<imagewidth;++j)
    {int scalehsrc,scalehdst;
      scalehsrc=tsmargin+(k-yinc)*srchdif/preh;//27+(132)*76/132
      scalehdst=tdmargin+(k-yinc)*dsthdif/preh;//2+76*132/76
      dstdif=(imagewidth-1-rdmargin[scalehdst]) - ldmargin[scalehdst];
      srcdif=(imagewidth-1-rsmargin[scalehsrc]) - lsmargin[scalehsrc];
      xdif=lsmargin[scalehsrc] - ldmargin[scalehdst];
      xinc=lsmargin[scalehsrc] - xdif*(cnt)/max; //(max-cnt)/max;
      tempdif=srcdif - (srcdif-dstdif)*(cnt)/max;
      if(j>xinc && j<=xinc+tempdif)
      {int x1,x;
       x=imagewidth*scalehsrc+lsmargin[scalehsrc]+(j-xinc)*srcdif/tempdif;// for src image
       x1=imagewidth*scalehdst+ldmargin[scalehdst]+(j-xinc)*dstdif/tempdif;// for dst image
       buf[imagewidth*k+j]=0xff<<24 |( compositefilter(bufimg[cnt1][x],max-cnt)+compositefilter(bufimg[temp][x1],cnt));
      }else   buf[imagewidth*k+j]=0xff000000;
    }
   }
   }
	   img1=createImage(new MemoryImageSource(imagewidth,imageheight,buf,0,imagewidth));
     if(cnt==max)  t.sleep(1000);
     else  t.sleep(delay);
     repaint();
    }
  }catch(InterruptedException e){}

  }
   public synchronized void update(Graphics g)
  {
   Dimension d = getSize();
    if((offscreenimage == null))
    {
      offscreenimage = createImage(d.width, d.height);
      offscreengraphics= offscreenimage.getGraphics();
    }
    offscreengraphics.setColor(getBackground());
    offscreengraphics.fillRect(0, 0, d.width, d.height);
    paint(offscreengraphics);
    g.drawImage(offscreenimage, 0, 0,this);
  }

  public void paint(Graphics g)
  {
   if(med.statusAll(false)==med.COMPLETE)
    g.drawImage(img1,0,0,this);
   else
   { g.setColor(new Color(Integer.parseInt(fontcolor.substring(2),16)));
     g.drawString("Loading Images Please wait...",20,100);
   }
  }

  public void stop() {
  t.interrupt();
  }
  public void destroy() {
  if(t != null){t=null;}
  }

  public String getAppletInfo() {
    return "Morph  by LaxmiNarayana.Kota, Java vesion 1.0.2 - 11 Sep '99";
  }

  public String[][] getParameterInfo() {
    return null;
  }

  public int filter(int rgb,int percent)
  { int r,g,b,temp;
    r=rgb>>16 & 0xff; g=rgb>>8 & 0xff; b=rgb & 0xff;
    r=r*8/100;g=g*8/100;b=b*8/100;
    return (0xff<<24 | r<<16 | g<<8 | b) ;
  }
 public int compositefilter(int rgb, int percent)
 { int r,g,b;
   r=rgb>>16 & 0xff; g=rgb>>8 & 0xff; b=rgb & 0xff;
   r=r*percent/max;g=g*percent/max;b=b*percent/max;
   return (0xff<<24 | r<<16 | g<<8 | b) ;

 }

}

