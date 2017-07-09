package com.macrovideo.demo;
 
 

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.StringTokenizer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
 
 import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
 
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.macrovideo.sdk.custom.DeviceInfo;
import com.macrovideo.sdk.defines.Defines;
import com.macrovideo.sdk.defines.ResultCode;
import com.macrovideo.sdk.media.LibContext;
import com.macrovideo.sdk.media.LoginHandle;
import com.macrovideo.sdk.media.LoginHelper;
import com.macrovideo.sdk.media.NVMediaPlayer;
import com.macrovideo.sdk.smartlink.SmarkLinkTool;
import com.macrovideo.sdk.stransport.TransData;
import com.macrovideo.sdk.stransport.TransportManager;
import com.macrovideo.sdk.tools.DeviceScanner;
import com.macrovideo.sdk.tools.Functions;
 
 
@SuppressLint({ "NewApi", "ClickableViewAccessibility" })
public class DemoPlayActivity extends Activity implements View.OnClickListener, OnTouchListener, OnItemClickListener{
	
	static final short SHOWCODE_LOADING=1001;//���ڼ���
	static final short SHOWCODE_NEW_IMAGE=1002;//��ͼƬ
	
//	static final short SHOWCODE_NOTICE=1003;//��ʾ
	
	static final short SHOWCODE_VIDEO=1004;//��ʾ
	static final short SHOWCODE_STOP=2001;//ֹͣ����
	static final short SHOWCODE_HAS_DATA=3001;//�����
	
	static final short STAT_CONNECTING=2001;//�������ӷ�����
	static final short STAT_LOADING=2002;//���ڼ�����Ƶ
	static final short STAT_DECODE=2003;//����
	static final short STAT_STOP=2004;//ֹͣ
	static final short STAT_DISCONNECT=2005;//���ӶϿ�
	static final short STAT_RESTART=2006;//��������
	static final short STAT_MR_BUSY=2007;//��������
	static final short STAT_MR_DISCONNECT=2008;//��������9

	private int m_loginID = 0;
	private DeviceInfo deviceTest = new DeviceInfo(-1, 36718239, "36718239",
			"192.168.10.17", 8800, "admin", "", "unkown mac addr",
			"36718239.nvdvr.net",Defines.SERVER_SAVE_TYPE_ADD);
	private LoginHandle _deviceParam=null;
	
	private String m_strName="36718239";
  	private boolean m_bPTZX = false;
 	
	private boolean m_bReversePRI=true;
 
 	private boolean mPlaySound=true;
	
 	
 	private Button mBtnSound;
	private Button mBtnSound2;   
 	private LinearLayout layoutTopBar=null;
 
 	private boolean mIsPlaying=false;
	private LinearLayout layoutBottomBar;
	private LinearLayout layoutCenter=null;
	
	private LinearLayout LayoutMicroPhoneBottom=null; //layoutMicroPhone=null, 
	
	private boolean bIsLeftPressed=false, bIsRightPressed=false, bIsUpPressed=false, bIsDownPressed=false;
	 
 
	private ImageView mBtnBack;
	private Button mBtnBack2;  // add by mai 2015-3-25
 
  	private boolean mIsSpeaking=false;
 	
	private boolean m_bFinish = false;
	int mScreenWidth = 0;//��Ļ��   
	int mScreenHeight = 0;//��Ļ�� 
 
	private boolean mQLHD=true;//�Ƿ�֧�ָ���
	private int mStreamType=0;//��ǰ���ŵ�����
	private LinearLayout layoutImageQL;//��๤����
	private LinearLayout mPTZPanel;//��๤����
	private RadioButton mBtnHD=null, mBtnSmooth=null;
	private Button btnHD=null, btnSmooth=null;
	private Button  mBtnImageQl=null, mBtnMic=null, mBtnReverse=null, mBtnScreenShot=null;
	
	private Button  mBtnImageQl2=null, mBtnMic2=null, mBtnReverse2=null, mBtnScreenShot2=null;  // add by mai 2015-3-25
	
	private RadioButton mBtnVerticalAuto=null, mBtnHorizontalAuto=null;
  	
	
	private LinearLayout llLandscapeDefinition, llVertical, llLandscape; // add by mai 2015-3-24
	private RadioGroup rgLandscapeDefinition;  // add by mai 2015-3-25
	
	NVMediaPlayer mvMediaPlayer = null;
 	
 	
	LinearLayout container =null;
 	private View parentContainer  = null;
	private View backgroundContainer = null;
	private ProgressBar loadingBar = null;
	
	private ImageView img_v[] = new ImageView[4];
	private int nScreenOrientation = Configuration.ORIENTATION_PORTRAIT;
	
	 
	
	private GestureDetector mGestureDetector=null;
	private ScaleGestureDetector mScaleGestureDetector = null;
	
	
     
	private boolean mIsReverse=false;//add  by luo 20141124
	
	//add by luo 20141217
	private Dialog iamgeViewDialog=null;
  	private Button btnCancelImageView=null;
  	//end add by luo 20141217
	
	private TextView tvPlayDeviceID; // add by mai 2015-3-13
	private LinearLayout llPlayTalkback; //add by mai 2015-3-14
	private RelativeLayout ptzCtrlPanel; //add by mai 2015-3-14
	private Button btnPTZLeft, btnPTZRight, btnPTZUP, btnPTZDown; //add by mai 2015-3-14
	private boolean bAnyway = true;
  
	private int FLING_MIN_DISTANCE = 10;  
    private int FLING_MIN_VELOCITY = 80;  
    private int FLING_MAX_DISTANCE = FLING_MIN_DISTANCE;
    
    private static int BTN_SCREENSHOT = 10010; // add by mai 2015-6-25 �ӳٸ��½�ͼ
    
     private PopupWindow popupWindowMore; // add by mai 2015-7-30
    private ImageView btnPresetConfig; // add by mai 2015-7-30 
     
 
    
     private ImageView ivPresetLandscape; // add by mai 2015-7-31 ����ʱ���Ԥ��λ��ť
    
	 
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// ����Ƿ��ؼ�,ֱ�ӷ��ص�����
		if(keyCode == KeyEvent.KEYCODE_BACK ){

		 
			exitPrompts();
		}
		
		return false;
	}
	
	//��ȡϵͳ����
	private void readSystemParam(){
   	 
        SharedPreferences ap =  getSharedPreferences(Defines._fileName, MODE_PRIVATE); 
        mPlaySound = ap.getBoolean("sound", true);
    }
    
   
	//����ϵͳ����
	 public  boolean writeSystemParam(){
		SharedPreferences ap =  getSharedPreferences(Defines._fileName, MODE_PRIVATE);
		SharedPreferences.Editor editer = ap.edit();
		editer.putBoolean("sound", mPlaySound);
		editer.commit();
		
		return true; 
	}
	 
	 int nTestThreadID = 0;
	 long lTransChannelHandle=0;
	//����ϵͳ����
    public  void testTrans(){
    	System.out.println("lTransChannelHandle  start");//add for test
    	DeviceInfo info = new DeviceInfo(-1, 31013255, "31013255",
    			"192.168.1.111", 8800, "admin", "", "unkown mac addr",
    			"31013255.nvdvr.net",Defines.SERVER_SAVE_TYPE_ADD);
    	
    	lTransChannelHandle = TransportManager.CreateTransportChannel(info);
    	
    	String strTest = "test";
    	System.out.println("lTransChannelHandle = "+lTransChannelHandle);//add for test
    	int nSendID = 0;
    	int nRecvID = 0;
    	if(lTransChannelHandle>0){
    		long lStart = System.currentTimeMillis();
    		int nTID = nTestThreadID;
    		while(nTestThreadID == nTID){//2min
    			
    			String strData = strTest+(nSendID++); 
    			
    			int nSendResult = TransportManager.SendData(lTransChannelHandle, strData.getBytes(), strData.length());
        		
        		System.out.println(">>>>  "+nSendID+" > "+strData);//add for test
        		
        		TransData data = TransportManager.RecvData(lTransChannelHandle, 3000);
        		if(data!=null && data.getnResultCode() == ResultCode.RESULT_CODE_SUCCESS){
        			nRecvID++;
        			try{
        				String strRecvResult = new String(data.getData());
            			System.out.println("<<<<"+nRecvID+" < "+strRecvResult+"\n");//add for test
        			}catch(Exception e){
        				
        			} 
        			
        		}
        		
        		if(System.currentTimeMillis()-lStart>1000*120){
        			break;
        		}
        		try {
    				Thread.sleep(1000);
    			} catch (InterruptedException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    		}
    		
    	}
    	System.out.println("lTransChannelHandle end");//add for test
  
    	TransportManager.DestroyTransportChannel(lTransChannelHandle);
    	
	}
		 
	//��ʾ����ʾ
	//msg����ʾ����Ϣ
	private void ShowAlert(String title, String msg){
		try{
			new AlertDialog.Builder(DemoPlayActivity.this)
			.setTitle(title).setMessage(msg)
			//.setTitle(getString(R.string.Alert_Title)).setMessage(msg)
			.setIcon(R.drawable.icon)
			.setPositiveButton(getString(R.string.alert_btn_OK), new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int whichButton) {

			  setResult(RESULT_OK);
			}
			}).show();

		}catch(Exception e){
			
		}
			
		}
		
	@Override
	public void onPause(){
		OnPlayersPuase();
		
		super.onPause();
	}
	
	
	@Override
	public void onResume(){
		
		System.out.println("  onResume 1");//add for test
		OnPlayersResume();
		nToolsViewShowTickCount = 8;
		 
//		timerThreadID++;
//		new TimerThread(timerThreadID).start();
 		
		System.out.println("  onResume 2");//add for test
		
//    	if(mIsPlaying){    		
//    		startPlay();
//    	}else{
//    		stopPlay(true);
//    	}
    	System.out.println("  onResume 1");//add for test
    	
		m_bFinish = false;
      
        this.loginDevice();
        
        
        nScreenOrientation =this.getResources().getConfiguration().orientation;
        
        //test
        nTestThreadID++;
	    new Thread(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				testTrans();
			}
	    	
	    }).start();
       
//        Configuration.ORIENTATION_LANDSCAPE
        super.onResume();
	}
	

	
	public void OnPlayersResume()
    {
		mvMediaPlayer.onResume();
    	 
 
    }
	@Override
	public void onDestroy(){
 		
		mvMediaPlayer = null;
		container = null;
 		parentContainer = null;
		backgroundContainer = null;
		img_v = null;
		
		//add by mai 2015-7-9 ��ձ���ͼƬ
		BitmapDrawable llVerticals = (BitmapDrawable) llVertical.getBackground();
		llVertical.setBackgroundResource(0); // ������Ϊ��
		llVerticals.setCallback(null);
		llVerticals.getBitmap().recycle();
		
		BitmapDrawable llLandscapeDefinitions = (BitmapDrawable) llLandscapeDefinition.getBackground();
		llLandscapeDefinition.setBackgroundResource(0); // ������Ϊ��
		llLandscapeDefinitions.setCallback(null);
		llLandscapeDefinitions.getBitmap().recycle();
		
		BitmapDrawable btnPTZLefts = (BitmapDrawable) btnPTZLeft.getBackground();
		btnPTZLeft.setBackgroundResource(0); // ������Ϊ��
		btnPTZLefts.setCallback(null);
		btnPTZLefts.getBitmap().recycle();
		
		BitmapDrawable btnPTZRights = (BitmapDrawable) btnPTZRight.getBackground();
		btnPTZRight.setBackgroundResource(0); // ������Ϊ��
		btnPTZRights.setCallback(null);
		btnPTZRights.getBitmap().recycle();
		
		BitmapDrawable btnPTZUPs = (BitmapDrawable) btnPTZUP.getBackground();
		btnPTZUP.setBackgroundResource(0); // ������Ϊ��
		btnPTZUPs.setCallback(null);
		btnPTZUPs.getBitmap().recycle();
		
		BitmapDrawable btnPTZDowns = (BitmapDrawable) btnPTZDown.getBackground();
		btnPTZDown.setBackgroundResource(0); // ������Ϊ��
		btnPTZDowns.setCallback(null);
		btnPTZDowns.getBitmap().recycle();
		//end add by mai 2015-7-9 
		super.onDestroy();
	}
	
 	@Override
	public void onStop(){
		 
 		System.out.println("onStop");//add for test
		timerThreadID++;
 		if(!m_bFinish){//����ǰ�����home���µ�ֹͣ���ͱ��浱�ڵ����
			
		 
 
 
			LibContext.stopAll();//add by luo 20141219
		}else{
			LibContext.stopAll();//add by luo 20141219
			LibContext.ClearContext();
		}
 		System.out.println("onStop end");//add for test
		m_bFinish = true;
		nTestThreadID++;
		super.onStop();
	}
    	
	
 	//������ʾ
	private void ShowLandscapeView(){
		
		synchronized(this)
		{
			//@@System.out.println(Configuration.ORIENTATION_LANDSCAPE);//add for test
			
		//	 mIsZoom = true;
			 bAnyway = false; //add by mai 2015-3-23
			 nToolsViewShowTickCount=5;
 				
		    int nWidth = mScreenWidth;
   	        int nHeight = mScreenHeight;
   	        double dWidth = nHeight*1.7777777;
   	        if(dWidth<nWidth)nWidth=(int) dWidth;
 
  	  
   	     if(ptzCtrlPanel!=null){
			ptzCtrlPanel.setVisibility(View.GONE);
		}
   	  
   	     if(popupWindowMore !=null)
		{
			popupWindowMore.dismiss();
		}
   	      hideToolsViews();
   	     
   	     if(layoutCenter!=null){
			 RelativeLayout.LayoutParams rlp=new RelativeLayout.LayoutParams(nWidth, nHeight);
			 rlp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
			 layoutCenter.setLayoutParams(rlp);
			 layoutCenter.setPadding(0, 0, 0, 0);
		 }
   	  
   	     nScreenOrientation = Configuration.ORIENTATION_LANDSCAPE;
 
   	     
   	     mvMediaPlayer.onOreintationChange(nScreenOrientation);
		}
		
	}
	
	//������ʾ
	private void ShowPortrailView(){
		 
		synchronized(this)
		{
			 if(m_bPTZX)
			 {
				 btnPresetConfig.setVisibility(View.VISIBLE);
			 }else{
				 btnPresetConfig.setVisibility(View.INVISIBLE);
			 }
			 
			if(mScreenWidth > mScreenHeight)
			{
				ShowLandscapeView();
			}else{
			//dipת��Ϊpx
 			int padding_in_dp = 45;  // 6 dps
 		    final float scale = getResources().getDisplayMetrics().density;
 		    int padding_in_px = (int) (padding_in_dp * scale + 0.5f);
 		    bAnyway = true; //add by mai 2015-3-23
		//	mIsZoom = false;
			showToolsViews();
 
			 int nWidth = mScreenWidth;
			 int nHeight = (int) (nWidth*0.95);
			 
			 if(ptzCtrlPanel!=null){ 
					if(layoutImageQL!=null && layoutImageQL.getVisibility()==View.VISIBLE){
						 
						ptzCtrlPanel.setVisibility(View.GONE);
						 
					 }else{
						 
 						ptzCtrlPanel.setVisibility(View.VISIBLE);
						 
					 }
			 }
			 
			 
			 
			 
			 if(layoutCenter!=null){
				 RelativeLayout.LayoutParams rlp=new RelativeLayout.LayoutParams(nWidth, nHeight);
				 rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
				 layoutCenter.setLayoutParams(rlp);
				 layoutCenter.setPadding(0, padding_in_px, 0, 0);
				 
			 }
			
 			 nScreenOrientation = Configuration.ORIENTATION_PORTRAIT;
 
 			 this.mvMediaPlayer.onOreintationChange(nScreenOrientation);
		}
 		}
	}
	
	 

	
    @Override
    public void onConfigurationChanged(Configuration config)
    {
     
    	
    	
    	super.onConfigurationChanged(config);// 
    	 
    	 
     	 
    	DisplayMetrics dm = getResources().getDisplayMetrics();   
		
		mScreenWidth = dm.widthPixels;//��Ļ��   
		mScreenHeight = dm.heightPixels;//��Ļ�� 
     	 
		if(config.orientation == Configuration.ORIENTATION_LANDSCAPE)
		{
			/*
			 * If the screen is switched from portait mode to landscape mode
			 */
			ShowLandscapeView();
			
		}
		else if(config.orientation == Configuration.ORIENTATION_PORTRAIT)
		{
			/*
			 * If the screen is switched from landscape mode to portrait mode
			 */
			ShowPortrailView();
		}
      }
    
    
	 
    
    void InitGLViewCloseButton()
    {
    	img_v[0] = (ImageView) findViewById(R.id.close_but_0);
     

    }
    
    /**
     * ���ùرհ�ť���������
     * @param stat	true��ʾ��ʾ��false��ʾ����ʾ
     */
    void SetCloseButtonVisible(boolean isVisible)
    {
    	if(isVisible)
    	{
	    	img_v[0].setVisibility(View.VISIBLE);
	    	img_v[1].setVisibility(View.VISIBLE);
	    	img_v[2].setVisibility(View.VISIBLE);
	    	img_v[3].setVisibility(View.VISIBLE);
    	}
    	else
    	{
	    	img_v[0].setVisibility(View.GONE);
	    	img_v[1].setVisibility(View.GONE);
	    	img_v[2].setVisibility(View.GONE);
	    	img_v[3].setVisibility(View.GONE);
    	}
    }
    
    
    
    /**
     * ���� GLES2SurfaceView��ʵ��
     */
    public void ReleaseGLViewPlayer()
    {
    	if(mvMediaPlayer!=null){
    		mvMediaPlayer.DisableRender();
        	mvMediaPlayer = null;
    	}
    	
    }
    
    public void OnPlayersPuase()
    {
    	if(mvMediaPlayer!=null){
    		mvMediaPlayer.onPause();
    	}
    	
    	 
    }
    
   
     
	
	private void InitGLViewTouchEventEX(){
 
		
		if(layoutCenter==null)return;
		layoutCenter.setLongClickable(true);
		layoutCenter.setOnTouchListener(this);
		
	}
	 
 
    
	public void SetGLViewPlayerMessageHandler()
	{
		if(mvMediaPlayer!=null){
			mvMediaPlayer.GetHandler(handler);
		}
		
	}
	
	class DeviceLoginThread extends Thread {
 		DeviceInfo info=null;
		public DeviceLoginThread(DeviceInfo info,int nLoginID){
			this.info = info;
		}

 
		public void run() {
			System.out.println("login :"+info.getnDevID()+", "+info.getStrDomain()+", "+info.getStrUsername()+", "+info.getStrPassword());//add for test
			
			LoginHandle deviceParam= null;
			if(info.getnSaveType() == Defines.SERVER_SAVE_TYPE_DEMO){
				_deviceParam = LoginHelper.getDeviceParam(info, info.getStrMRServer(), info.getnMRPort());
			}else{
				deviceParam = LoginHelper.getDeviceParam(info);
			}
			
			
			if(deviceParam!=null && deviceParam.getnResult()==ResultCode.RESULT_CODE_SUCCESS){
				Message msg = handler.obtainMessage();
				msg.arg1 = HANDLE_MSG_CODE_LOGIN_RESULT;
				msg.arg2 = ResultCode.RESULT_CODE_SUCCESS;
				Bundle data = new Bundle();
				data.putParcelable("device_param", deviceParam);
				msg.setData(data);
//				System.out.println("login result : "+deviceParam.isMRMode()+", "+deviceParam.getStrIP()+", "+deviceParam.getnPort());//add for test
				handler.sendMessage(msg);
			}else{
				if(deviceParam==null){
					Message msg = handler.obtainMessage();
					msg.arg1 = HANDLE_MSG_CODE_LOGIN_RESULT;
					msg.arg2 = ResultCode.RESULT_CODE_FAIL_SERVER_CONNECT_FAIL;
					
					handler.sendMessage(msg);
				}else{
					Message msg = handler.obtainMessage();
					msg.arg1 = HANDLE_MSG_CODE_LOGIN_RESULT;
					msg.arg2 = deviceParam.getnResult();

					handler.sendMessage(msg);
				}
			}
 
		}
	
	}
	
	private void loginDevice(){
		m_loginID++;
		new DeviceLoginThread(deviceTest, m_loginID).start();
	}
	static final int HANDLE_MSG_CODE_LOGIN_RESULT = 0x10;// ��Ϣ�����ʾ�𣺵�¼���
	  
	private Handler handler = new Handler()
	{
		//@SuppressLint("HandlerLeak")
		public void handleMessage(Message msg) 
		{	
			
			if (msg.arg1 == HANDLE_MSG_CODE_LOGIN_RESULT) {// �Ƿ����Ե�¼������Ϣ
 
				// �����¼���
				switch (msg.arg2) {
				case ResultCode.RESULT_CODE_SUCCESS: {// ��¼�ɹ�

					
					Bundle data = msg.getData();
 
					_deviceParam = data.getParcelable("device_param");// data.putParcelable("device_param", _deviceParam);
		          	
 		          	if(_deviceParam!=null){
 		          		
 		          		System.out.println("login ok: ");//add for test
 		          		
 		          		m_bReversePRI= _deviceParam.isbReversePRI();//data.getBoolean("reverse", false);//add  by luo 20141219
 			      
 			         	 
 			         	//add by luo 20141014
 			        	if(_deviceParam.isbSpeak()){
 			        		findViewById(R.id.layoutMicBtn).setVisibility(View.VISIBLE);
 			        		findViewById(R.id.layoutMicBtn2).setVisibility(View.VISIBLE);  //add by mai 2015-3-25
 			        	}else{
 			        		findViewById(R.id.layoutMicBtn).setVisibility(View.GONE);
 			        		findViewById(R.id.layoutMicBtn2).setVisibility(View.GONE);  // add by mai 2015-3-25
 			        	}
 		          		startPlay();
 		          	}
		          	
		         	 
					

				}
					break;
 
 
				case ResultCode.RESULT_CODE_FAIL_SERVER_CONNECT_FAIL: {// ��¼ʧ��-������������
					ShowAlert(
							getString(R.string.alert_title_login_failed)
									+ "Login fail","");
				}
					break;
				case ResultCode.RESULT_CODE_FAIL_VERIFY_FAILED: {// ��¼ʧ��-�û�����������
					ShowAlert(getString(R.string.alert_title_login_failed),
							getString(R.string.notice_Result_VerifyFailed));
				}
					break;
				case ResultCode.RESULT_CODE_FAIL_USER_NOEXIST: {// ��¼ʧ��-�û�����������
					ShowAlert(getString(R.string.alert_title_login_failed),
							getString(R.string.notice_Result_UserNoExist));
				}
					break;
				case ResultCode.RESULT_CODE_FAIL_PWD_ERROR: {// ��¼ʧ��-�û�����������
					ShowAlert(getString(R.string.alert_title_login_failed),
							getString(R.string.notice_Result_PWDError));
				}
					break;
				case ResultCode.RESULT_CODE_FAIL_OLD_VERSON: {
					ShowAlert(getString(R.string.alert_title_login_failed),
							getString(R.string.notice_Result_Old_Version));
				}
					break;
				default:
					ShowAlert(
							getString(R.string.alert_title_login_failed)
									+ "  ("
									+ getString(R.string.notice_Result_ConnectServerFailed)
									+ ")","");
					break;
				}

				return;
			} 
			
			
			// add by mai 2015-6-25 ��ͼ��ɿ����ɵ��
			if(msg.arg1 == BTN_SCREENSHOT)
			{
				mBtnScreenShot.setEnabled(true);
				mBtnScreenShot2.setEnabled(true);
				return;
			}
			//end add by mai 2015-6-25
		 
			
			if(msg.arg1 == Defines.MSG_HIDE_TOOLVIEW){
				if(!mIsSpeaking && layoutImageQL.getVisibility()!=View.VISIBLE && mPTZPanel.getVisibility()!=View.VISIBLE && nScreenOrientation== Configuration.ORIENTATION_LANDSCAPE){
					if(popupWindowMore!=null && popupWindowMore.isShowing()){//����ҳ������������ض���
						nToolsViewShowTickCount=5;
						return;
					}
					hideToolsViews();  
				}
					 
				return;
			}
			if(msg.arg1 == 1)
			{
				loadingBar.setVisibility(View.VISIBLE);
			}
			else
			{
				loadingBar.setVisibility(View.GONE);
			}
		}
	};
	
	 
	static final int SIZE_CMDPACKET=128;
	static final int SEND_BUFFER_SIZE=512;
	static final int SESSION_FRAME_BUFFER_SIZE=65536;
	static final int SEND_BUFFER_DATA_SIZE=504; // 512 - 8 (header)
	static final int SEND_BUFFER_HEADER_SIZE=8;
	static final int SP_DATA = 0x7f;
	
	static final int  CMD_REQUEST=0x9101;
	static final int  CMD_AFFIRM=0x9102;
	static final int  CMD_EXIT=0x9103;
	static final int  CMD_ACCEPT=0x9104;
	static final int  CMD_CONNECTINFO=0x9105;
	static final int  CMD_STREAMHEAD=0x9106;
	static final int  CMD_UDPSHAKE=0x9107;
	static final int  CMD_ASKFORCNLNUM=0x9108;
	static final int  CMD_CHECKPSW=0x9109;


 	
	/** Called when the activity is first created. */
    @SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
 
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.playview);
        
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
         // �����µ�ͼƬ  
        DisplayMetrics dm = getResources().getDisplayMetrics();   
			
         
		mScreenWidth = dm.widthPixels;//��Ļ��   
		mScreenHeight = dm.heightPixels;//��Ļ�� 
 		
		
		FLING_MAX_DISTANCE = (int) (mScreenWidth/3);
		
     	System.out.println("Play activity 1");//add for test
     	
     	tvPlayDeviceID = (TextView) findViewById(R.id.tvPlayDeviceID);  //add by mai 2015-3-13
     	llPlayTalkback = (LinearLayout) findViewById(R.id.llPlayTalkback); // add by mai 2015-3-14
        
         
        LayoutMicroPhoneBottom=(LinearLayout)findViewById(R.id.MicroPhoneLayoutBottom);
    
        mGestureDetector = new GestureDetector(this, new PTZGestureListener(this));
        
        mScaleGestureDetector =new ScaleGestureDetector(this, new ScaleGestureListener());  
        tvPlayDeviceID.setText(m_strName);
        
         
        layoutTopBar = (LinearLayout)findViewById(R.id.linearLayoutTopBar);
        layoutCenter =  (LinearLayout)findViewById(R.id.playContainer);
        layoutBottomBar = (LinearLayout)findViewById(R.id.linearLayoutBottomBar);
         
         
        btnPTZLeft = (Button) findViewById(R.id.btnPTZLeft);
        btnPTZLeft.setBackgroundDrawable(new BitmapDrawable(Functions.readBitMap(this, R.drawable.play_left_1))); //add by mai 2015-7-9 ��̬��ӱ���
        btnPTZLeft.setOnTouchListener(this);
        btnPTZRight = (Button) findViewById(R.id.btnPTZRight);
        btnPTZRight.setBackgroundDrawable(new BitmapDrawable(Functions.readBitMap(this, R.drawable.play_right_1))); //add by mai 2015-7-9 ��̬��ӱ���
        btnPTZRight.setOnTouchListener(this);
        btnPTZUP = (Button) findViewById(R.id.btnPTZUP);
        btnPTZUP.setBackgroundDrawable(new BitmapDrawable(Functions.readBitMap(this, R.drawable.play_top_1))); //add by mai 2015-7-9 ��̬��ӱ���
        btnPTZUP.setOnTouchListener(this);
        btnPTZDown = (Button) findViewById(R.id.btnPTZDown);
        btnPTZDown.setBackgroundDrawable(new BitmapDrawable(Functions.readBitMap(this, R.drawable.play_bottom_1))); //add by mai 2015-7-9 ��̬��ӱ���
        btnPTZDown.setOnTouchListener(this);
     //   llParams = (RelativeLayout.LayoutParams) layoutMicroPhone.getLayoutParams();
         
         
        llLandscape = (LinearLayout) findViewById(R.id.llLandscape);
        llVertical = (LinearLayout) findViewById(R.id.llVertical);
        llVertical.setBackgroundDrawable(new BitmapDrawable(Functions.readBitMap(this, R.drawable.play_back_button_bg_alpha)));
        llLandscapeDefinition = (LinearLayout) findViewById(R.id.llLandscapeDefinition);
        llLandscapeDefinition.setBackgroundDrawable(new BitmapDrawable(Functions.readBitMap(this, R.drawable.play_back_button_bg_alpha)));
        rgLandscapeDefinition = (RadioGroup) findViewById(R.id.rgLandscapeDefinition);
        
        
        // add by mai 2015-7-30
        btnPresetConfig = (ImageView) findViewById(R.id.btnPresetConfig);
        btnPresetConfig.setOnClickListener(this);
        
        ivPresetLandscape = (ImageView) findViewById(R.id.ivPresetLandscape);
        ivPresetLandscape.setOnClickListener(this);
        // end add by mai 2015-7-30
        
         
         /////////////////////////////////////////////////
        parentContainer = findViewById(R.id.playContainerParent1);
    	backgroundContainer = findViewById(R.id.playContainer1background);
    	container = (LinearLayout)findViewById(R.id.playContainer1);
    	

    	//
    	loadingBar = (ProgressBar) findViewById(R.id.spinner_0);
 
         //����������
         mvMediaPlayer  = new NVMediaPlayer(getApplication(), nScreenOrientation, 0);
     	 mvMediaPlayer.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
         
         //��Ӳ�����������
         container.addView(mvMediaPlayer);
          
    
        SetGLViewPlayerMessageHandler();
 
//        Player.ClearGLESScreen(glViews,true,0);
        InitGLViewCloseButton();
        InitGLViewTouchEventEX();   //��ť����¼�����ʱ�����
 
        System.out.println("onCreate 1");
        
        //	���û�������
	 
	     
	    LibContext.SetContext(mvMediaPlayer,null,null,null);
 
        //
    	mBtnBack = (ImageView)findViewById(R.id.buttonBackToLogin);
    	mBtnBack.setOnClickListener(this);
    	
     
    	mBtnSound =  (Button)findViewById(R.id.buttonSound);
    	mBtnSound.setOnClickListener(this);
    	
    	mBtnMic =  (Button)findViewById(R.id.buttonMic);
    	mBtnMic.setOnTouchListener(this);
    	
    	mBtnReverse= (Button)findViewById(R.id.buttonReverse);
    	mBtnReverse.setOnClickListener(this);
    	
    	mBtnScreenShot= (Button)findViewById(R.id.buttonScreenShot);
    	mBtnScreenShot.setOnClickListener(this);
    	
    	
    	//add by mai 2015-3-25
    	mBtnScreenShot2= (Button)findViewById(R.id.buttonScreenShot2);
    	mBtnScreenShot2.setOnClickListener(this);
    	
    	mBtnMic2 =  (Button)findViewById(R.id.buttonMic2);
    	mBtnMic2.setOnTouchListener(this);
    	
    	mBtnReverse2= (Button)findViewById(R.id.buttonReverse2);
    	mBtnReverse2.setOnClickListener(this);
    	
    	mBtnSound2 =  (Button)findViewById(R.id.buttonSound2);
    	mBtnSound2.setOnClickListener(this);
    	
    	mBtnBack2 = (Button)findViewById(R.id.buttonBackToLogin2);
    	mBtnBack2.setOnClickListener(this);
     	
  
     	//end add by luo 20141014
     	ptzCtrlPanel = (RelativeLayout)findViewById(R.id.ptzCtrlPanel); //add by mai 2015-3-14
     
    	mPTZPanel = (LinearLayout)findViewById(R.id.toolPTZ); 
    	mPTZPanel.setVisibility(View.GONE);
     	mBtnImageQl =  (Button)findViewById(R.id.buttonImageGQL);
    	mBtnImageQl.setOnClickListener(this);
    	
    	mBtnImageQl2 =  (Button)findViewById(R.id.buttonImageGQL2); // add by mai 2015-3-25
    	mBtnImageQl2.setOnClickListener(this);  // add by mai 2015-3-25
     	
    	layoutImageQL = (LinearLayout)findViewById(R.id.layoutImageQL); 
    	
		setQLViewVisible(false);
		System.out.println("setQLViewVisible 1");//add for test
    	mBtnHD = (RadioButton)findViewById(R.id.rBtnHD);
    	btnHD = (Button)findViewById(R.id.btnHD);  // add by mai 2015-3-25
    	mBtnSmooth = (RadioButton)findViewById(R.id.rBtnSmooth);
    	btnSmooth = (Button)findViewById(R.id.btnSmooth);  // add by mai 2015-3-25
    	mBtnHD.setOnClickListener(this);
    	mBtnSmooth.setOnClickListener(this);
    	btnHD.setOnClickListener(this);  // add by mai 2015-3-25
    	btnSmooth.setOnClickListener(this);  // add by mai 2015-3-25
    	
    	if(mQLHD){
    		mBtnImageQl.setVisibility(View.VISIBLE);
    		mBtnImageQl2.setVisibility(View.VISIBLE); // add by mai 2015-3-25
    	}else{
    		mBtnImageQl.setVisibility(View.GONE);
    		mBtnImageQl2.setVisibility(View.GONE);  // add by mai 2015-3-25
    	}
    	
    	if(mStreamType==0){
    		mBtnSmooth.setChecked(true);
    		
    		btnSmooth.setTextColor(Color.BLACK);
    		btnHD.setTextColor(Color.BLUE);
    	}else{
    		mBtnHD.setChecked(true);
    		
    		btnSmooth.setTextColor(Color.BLUE);
    		btnHD.setTextColor(Color.BLACK);
    	}
     	 
    	readSystemParam();
    	if(mPlaySound){
     		mBtnSound.setBackgroundDrawable(getResources().getDrawable(R.drawable.play_back_sound_btn));
     		mBtnSound2.setBackgroundDrawable(getResources().getDrawable(R.drawable.play_back_sound_btn));  // add by mai 2015-3-25
     		mvMediaPlayer.playAudio();
    	}else{
    		mBtnSound.setBackgroundDrawable(getResources().getDrawable(R.drawable.play_back_sound_3));
    		mBtnSound2.setBackgroundDrawable(getResources().getDrawable(R.drawable.play_back_sound_3));  // add by mai 2015-3-25
    		mvMediaPlayer.pauseAudio();
    	}
    	
     	
    	mBtnVerticalAuto = (RadioButton)findViewById(R.id.rBtnVerticalAuto); 
    	mBtnHorizontalAuto = (RadioButton)findViewById(R.id.rBtnHorizontalAuto);
    	mBtnVerticalAuto.setOnClickListener(this);
    	mBtnHorizontalAuto.setOnClickListener(this);
    	
     

	    ShowPortrailView();
	    
	  

//	    StartSearchDevice();//test
//        SmarkLinkTool.StartSmartConnection("assd", "sd");//test
     	mIsPlaying = false;
     	
     	 
     } 
    //ֹͣ����
    private void stopPlay(boolean bFlag){
 
      
    	ptzTimerThreadID++;

    	 
    	//end add by luo 20141007
    	 
    	 mIsPlaying = false;
    	 tvPlayDeviceID.setText(m_strName);
    	 if(mvMediaPlayer!=null){
    		 mvMediaPlayer.scale(1, 1);
   		     mvMediaPlayer.StopPlay();//add by luo 20141008
    	 }
		 
		 
	      loadingBar.setVisibility(View.GONE);
 		 
     }
    
    //��ʼ����
    private void startPlay(){
 
 
  
    	 if(_deviceParam==null || mvMediaPlayer==null) return;
    	 
    	 if(m_strName!=null && m_strName.trim().length()>0){
    		 tvPlayDeviceID.setText( getString(R.string.Notification_Playing_Chn)+" "+m_strName);
    	 }else{
    		 tvPlayDeviceID.setText( getString(R.string.Notification_Playing_Chn)+" "+m_strName);
    	 }
      
    		mvMediaPlayer.EnableRender(); 
    		mvMediaPlayer.StartPlay(0, 0, mStreamType, mPlaySound, _deviceParam);
    		mvMediaPlayer.setReverse(mIsReverse);//add by luo 20141219
			mvMediaPlayer.playAudio();
		 
    	
    	 
    	 ptzTimerThreadID++;
    	 if(_deviceParam.isbPTZ()){
    		new PTZTimerThread(ptzTimerThreadID).start();
    	 }
    	 
    	 
  		mIsPlaying = true;
    }
    
    
    
    @SuppressWarnings("deprecation")
	private void setQLViewVisible(boolean isVisible){
    	if(isVisible){
    		layoutImageQL.setVisibility(View.VISIBLE);
    		mBtnImageQl.setBackgroundDrawable(getResources().getDrawable(R.drawable.play_definition_2));
 
    	}else{
    		layoutImageQL.setVisibility(View.GONE);
    		mBtnImageQl.setBackgroundDrawable(getResources().getDrawable(R.drawable.play_definition_1));
      
    	}
    }
    
    //add by luo 20150820
   //������ѡ��ı��¼�
    private void onStreamTypeChange(int nType){
    	if(mStreamType==nType)return;
    	
    	mStreamType = nType;
    	
    	
    	if(mStreamType==0){
   

    		btnSmooth.setTextColor(Color.BLUE);
    		btnHD.setTextColor(Color.BLACK);
    		mBtnSmooth.setChecked(true);
             
              
    	}else if(mStreamType==1){
    		btnSmooth.setTextColor(Color.BLACK);
    	    btnHD.setTextColor(Color.BLUE);
    		mBtnHD.setChecked(true);
    	} 
    	
    	if(mIsPlaying){
      	  stopPlay(false);
	      	  try {
					Thread.sleep(200);
	      	  } catch (InterruptedException e) {
 					e.printStackTrace();
	      	  }
	 
	      	  startPlay();
    	 }
    }
	
    private void exitPrompts(){
    	 new AlertDialog.Builder(DemoPlayActivity.this)
			.setTitle(getString(R.string.alert_stop_play))
			.setIcon(R.drawable.icon)
			.setNegativeButton(getString(R.string.alert_btn_Cancel), null)
			.setPositiveButton(getString(R.string.alert_btn_OK), new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int whichButton) {

				
			  setResult(RESULT_OK);
			  
			  stopPlay(false);
	 		  LibContext.ClearContext();
	 		  ReleaseGLViewPlayer();
	 	      DemoPlayActivity.this.finish();   
			  
			}

			}).show();
    }
    @SuppressWarnings("deprecation")
	private void onSoundChange(){
    	  
		  mPlaySound = !mPlaySound;
		  if(mPlaySound){
	     		mBtnSound.setBackgroundDrawable(getResources().getDrawable(R.drawable.play_back_sound_btn));
	     		mBtnSound2.setBackgroundDrawable(getResources().getDrawable(R.drawable.play_back_sound_btn));
	     		mvMediaPlayer.playAudio();
	    	}else{
	    		mBtnSound.setBackgroundDrawable(getResources().getDrawable(R.drawable.play_back_sound_3));
	    		mBtnSound2.setBackgroundDrawable(getResources().getDrawable(R.drawable.play_back_sound_3));
	    		mvMediaPlayer.pauseAudio();
	    	}
		  
		 
		  writeSystemParam();
    }
    
    public void onClick(View v) {
		// TODO Auto-generated method stub
	  nToolsViewShowTickCount=5;
	 
	  
	 if(v==btnCancelImageView){
		 iamgeViewDialog.dismiss();
		 return;
	 }
		
	 
	 
	 if(v==mBtnReverse){//ͼ���� //add by luo 20141124
		  //modify by luo 20150106
			 if(m_bReversePRI){
 				 mvMediaPlayer.SetCamImageOrientation(Defines.NV_IPC_ORIENTATION_REVERSE);
			 }else{
				 mIsReverse=!mIsReverse;//add  by luo 20141124
				  mvMediaPlayer.setReverse(mIsReverse);
			 }
			//end modify by luo 20150106  
	 
			 
			return;
		  
	  }else if(v==mBtnReverse2){//ͼ���� //add by mai 2015-3-25
		  //modify by luo 20150106
			 if(m_bReversePRI){
				 
				 mvMediaPlayer.SetCamImageOrientation(Defines.NV_IPC_ORIENTATION_REVERSE);
			 }else{
				 mIsReverse=!mIsReverse;//add  by luo 20141124
				  mvMediaPlayer.setReverse(mIsReverse);
			 }
			//end modify by luo 20150106  
	 
			return;
		  
	  }else  // end add by mai 2015-3-25
	  if(v==mBtnImageQl){//��ʾ��ఴť�����¼�
 
		  
		mPTZPanel.setVisibility(View.GONE);
  		if(layoutImageQL.getVisibility()!=View.VISIBLE){
  			System.out.println("setQLViewVisible 2");//add for test
			setQLViewVisible(true);
		}else{
			setQLViewVisible(false);
			System.out.println("setQLViewVisible 3");//add for test
 		}
		
		return;

	  }
     
  
	 System.out.println("setQLViewVisible 4");//add for test
	  setQLViewVisible(false);
	 
	  if(v==mBtnHD){//����
		  onStreamTypeChange(1);
	  }else	 if(v==mBtnSmooth){//����
		  onStreamTypeChange(0);
		  
	  }else if(v==btnHD){  //��������
		  
		  onStreamTypeChange(1);
		  
	  }else	 if(v==btnSmooth){//��������
		  
		  onStreamTypeChange(0);
	  }else	if(v==mBtnSound){ //��Ƶ
		  
 		
			
		  v.setEnabled(false);
		  onSoundChange();
		  v.setEnabled(true);
	  }else if(v==mBtnSound2){  //������Ƶ
		  v.setEnabled(false);
		  onSoundChange();
		  v.setEnabled(true);
			
	  } else if(v==mBtnBack){//���ؼ�
		  
		 exitPrompts();

 		  
		  
	  }else if(v==mBtnBack2){//�������ؼ�
		  
		  exitPrompts();
	  }   
	  
	}

	 //������ʾ����
  	public void ShowNotic(String title, String msg){//add by luo 20141010
	  		
  		Toast toast = Toast.makeText(getApplicationContext(),
  				title, Toast.LENGTH_SHORT);
	    toast.setGravity(Gravity.CENTER, 0, 0);
			   toast.show();

  	}
  	
  	
  	/**
  	 * ��ʾ�豸�Ƿ���ҡͷ��
  	 */
  	public boolean hasPTZXPRI()
  	{
  		boolean bPTZPRI=_deviceParam.isbPTZ();
  		if(!_deviceParam.isbPTZ())   
		{
			ShowNotic(getString(R.string.deviceTurn),"");
			btnPTZDown.setEnabled(false);
			btnPTZLeft.setEnabled(false);
			btnPTZRight.setEnabled(false);
			btnPTZUP.setEnabled(false);
		}
		
  		return bPTZPRI;
  	}
  	
  	

	@SuppressWarnings("deprecation")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
	
		
 		
 	    if(v==this.layoutCenter){
 	    	mScaleGestureDetector.onTouchEvent(event);
 	    	
 	    	if(System.currentTimeMillis() - lScaleTime>500){
 	    		mGestureDetector.onTouchEvent(event);
 	    	}
	    	
	    	
	    	return true;
	    }else if(v==this.mBtnMic){
 	    		
	    		if(!mIsPlaying){
	    			return true;
	    		}
  
	    		switch(event.getAction())
				{
					case MotionEvent.ACTION_DOWN: 
						mBtnMic.setBackgroundResource(R.drawable.play_talkback_2);
						llPlayTalkback.setVisibility(View.VISIBLE);
						mIsSpeaking = true;
	 
						try {
							Thread.sleep(200);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					 
						
						mvMediaPlayer.StartSpeak();
						
						break;
					case MotionEvent.ACTION_CANCEL: 
						mBtnMic.setBackgroundResource(R.drawable.play_talkback_1);
						llPlayTalkback.setVisibility(View.GONE);
						mIsSpeaking = false; 
						
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
						 
						
						mvMediaPlayer.StopSpeak();
						
						break;
					
					case MotionEvent.ACTION_MOVE: 
						mIsSpeaking = true;
						mBtnMic.setBackgroundResource(R.drawable.play_talkback_2);
						llPlayTalkback.setVisibility(View.VISIBLE);
//						 
						break;
					case MotionEvent.ACTION_UP:
						mBtnMic.setBackgroundResource(R.drawable.play_talkback_1);
						llPlayTalkback.setVisibility(View.GONE);
//						 
						mIsSpeaking = false;
 
						
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					mvMediaPlayer.StopSpeak();
						 
						break;
					default:break;
				}
	    		
	    		
				return true;
	    }else if(v==this.mBtnMic2 ){  // add by mai 2015-3-25
    		
    		if(!mIsPlaying){
    			return true;
    		}
    	 
    		
    		switch(event.getAction())
			{
				case MotionEvent.ACTION_DOWN:

					mBtnMic2.setBackgroundResource(R.drawable.play_talkback_2);
					llPlayTalkback.setVisibility(View.VISIBLE);
					mIsSpeaking = true;
					
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					mvMediaPlayer.StartSpeak();
					
					
					
					break;
				case MotionEvent.ACTION_CANCEL:

					mBtnMic2.setBackgroundResource(R.drawable.play_talkback_1);
					llPlayTalkback.setVisibility(View.GONE);
					mIsSpeaking = false;

					
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				mvMediaPlayer.StopSpeak();
					
					
					
					break;
				
				case MotionEvent.ACTION_MOVE: 
					mIsSpeaking = true;
					mBtnMic2.setBackgroundResource(R.drawable.play_talkback_2);
					llPlayTalkback.setVisibility(View.VISIBLE);

					break;
				case MotionEvent.ACTION_UP:
					mBtnMic2.setBackgroundResource(R.drawable.play_talkback_1);
					llPlayTalkback.setVisibility(View.GONE);
					mIsSpeaking = false;
					
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				mvMediaPlayer.StopSpeak();
					 
					break;
				default:break;
			}
    		
    		
			return true;
    }else  if(v == this.btnPTZLeft)  //�������߰�ť,
    	{
    	 
    		//�豸�Ƿ���ҡͷ��
    		if(!hasPTZXPRI()){
    			return true;
    		}
    		
	    	switch(event.getAction())
			{
				case MotionEvent.ACTION_DOWN:
					if(_deviceParam.isbPTZ())
					{
						 btnPTZLeft.setBackgroundDrawable(new BitmapDrawable(Functions.readBitMap(this, R.drawable.play_left_2))); //add by mai 2015-7-9 ��̬��ӱ���
					}
					 bIsLeftPressed=true;
					
					
					break;
					
				case MotionEvent.ACTION_CANCEL:
					 btnPTZLeft.setBackgroundDrawable(new BitmapDrawable(Functions.readBitMap(this, R.drawable.play_left_1))); //add by mai 2015-7-9 ��̬��ӱ���
					bIsLeftPressed=false;
					
					break;
				
				case MotionEvent.ACTION_MOVE: 
 
					break;
				case MotionEvent.ACTION_UP:
					 btnPTZLeft.setBackgroundDrawable(new BitmapDrawable(Functions.readBitMap(this, R.drawable.play_left_1))); //add by mai 2015-7-9 ��̬��ӱ���
					bIsLeftPressed=false;
					break;
				default:break;
			}
	    	
	    }else if(v == this.btnPTZRight)  //������ұ߰�ť
	    {
	    	//�豸�Ƿ���ҡͷ��
    		if(!hasPTZXPRI()){
    			return true;
    		}
    		
	    	switch(event.getAction())
			{
				case MotionEvent.ACTION_DOWN:
					if(_deviceParam.isbPTZ())
					{
						btnPTZRight.setBackgroundDrawable(new BitmapDrawable(Functions.readBitMap(this, R.drawable.play_right_2))); //add by mai 2015-7-9 ��̬��ӱ���
					}
					 bIsRightPressed=true;
					 
					break;
					
				case MotionEvent.ACTION_CANCEL:
					btnPTZRight.setBackgroundDrawable(new BitmapDrawable(Functions.readBitMap(this, R.drawable.play_right_1))); //add by mai 2015-7-9 ��̬��ӱ���
					bIsRightPressed=false;
					
					break;
				
				case MotionEvent.ACTION_MOVE: 
 
					break;
				case MotionEvent.ACTION_UP:
					btnPTZRight.setBackgroundDrawable(new BitmapDrawable(Functions.readBitMap(this, R.drawable.play_right_1))); //add by mai 2015-7-9 ��̬��ӱ���
					bIsRightPressed=false;
					break;
				default:break;
			}
	    	
	    }else if(v == this.btnPTZUP)  //������ϱ߱߰�ť
	    {
	    	//�豸�Ƿ���ҡͷ��
    		if(!hasPTZXPRI()){
    			return true;
    		}
    		
	    	switch(event.getAction())
			{
				case MotionEvent.ACTION_DOWN:
					if(_deviceParam.isbPTZ())
					{
						 btnPTZUP.setBackgroundDrawable(new BitmapDrawable(Functions.readBitMap(this, R.drawable.play_top_2))); //add by mai 2015-7-9 ��̬��ӱ���
					}
					bIsUpPressed=true;
					
					break;
					
				case MotionEvent.ACTION_CANCEL:

					 btnPTZUP.setBackgroundDrawable(new BitmapDrawable(Functions.readBitMap(this, R.drawable.play_top_1))); //add by mai 2015-7-9 ��̬��ӱ���
					bIsUpPressed=false;
					break;
				
				case MotionEvent.ACTION_MOVE: 
 
					break;
				case MotionEvent.ACTION_UP:
					
					 btnPTZUP.setBackgroundDrawable(new BitmapDrawable(Functions.readBitMap(this, R.drawable.play_top_1))); //add by mai 2015-7-9 ��̬��ӱ���
					bIsUpPressed=false;
					break;
				default:break;
			}
	    	
	    }else if(v == this.btnPTZDown)  //������±߰�ť
	    {
	    	//�豸�Ƿ���ҡͷ��
    		if(!hasPTZXPRI()){
    			return true;
    		}
    		
	    	switch(event.getAction())
			{
				case MotionEvent.ACTION_DOWN:
					if(_deviceParam.isbPTZ())
					{
						btnPTZDown.setBackgroundDrawable(new BitmapDrawable(Functions.readBitMap(this, R.drawable.play_bottom_2))); //add by mai 2015-7-9 ��̬��ӱ���
					}
					bIsDownPressed=true;
					break;
					
				case MotionEvent.ACTION_CANCEL:
					
					btnPTZDown.setBackgroundDrawable(new BitmapDrawable(Functions.readBitMap(this, R.drawable.play_bottom_1))); //add by mai 2015-7-9 ��̬��ӱ���
					bIsDownPressed=false;
					break;
				
				case MotionEvent.ACTION_MOVE: 
					
					break;
				case MotionEvent.ACTION_UP:
					btnPTZDown.setBackgroundDrawable(new BitmapDrawable(Functions.readBitMap(this, R.drawable.play_bottom_1))); //add by mai 2015-7-9 ��̬��ӱ���
					bIsDownPressed=false;
					break;
				default:break;
			}
	    	
	    }
	    return false;
		 
 
	}
	
	 
	//��ʾ������
	private void showToolsViews(){
		System.out.println("showToolsViews");//add for test
		if(popupWindowMore !=null)
		{
			popupWindowMore.dismiss();
		}
		nToolsViewShowTickCount=5;
		layoutBottomBar.setVisibility(View.VISIBLE);
		if(bAnyway)
		{
			layoutTopBar.setVisibility(View.VISIBLE);
			llVertical.setVisibility(View.VISIBLE);
			rgLandscapeDefinition.setVisibility(View.VISIBLE);
		//	layoutImageQL.setVisibility(View.GONE);
			ivPresetLandscape.setVisibility(View.GONE);  //add  by mai 2015-7-31 ����ʱ����
			llLandscape.setVisibility(View.GONE);
			llLandscapeDefinition.setVisibility(View.GONE);
			
			 
		}else{
			layoutTopBar.setVisibility(View.GONE);
			llVertical.setVisibility(View.GONE);
			rgLandscapeDefinition.setVisibility(View.GONE);
			llLandscape.setVisibility(View.VISIBLE);
			llLandscapeDefinition.setVisibility(View.VISIBLE);
			
			 if(m_bPTZX)
			 {
				 ivPresetLandscape.setVisibility(View.VISIBLE);
			 }else{
				 ivPresetLandscape.setVisibility(View.INVISIBLE);
			 }
 		//	layoutImageQL.setVisibility(View.VISIBLE);
			
			
		}
		
		LayoutMicroPhoneBottom.setVisibility(layoutBottomBar.getVisibility());
		 

		
		if(nScreenOrientation == Configuration.ORIENTATION_LANDSCAPE){
			ptzCtrlPanel.setVisibility(View.GONE);  
		}else{
			ptzCtrlPanel.setVisibility(View.VISIBLE);  
		}
	}
	
	//Ӱ�ع�����
	private void hideToolsViews(){ 
		
		
		nToolsViewShowTickCount=0;
		layoutBottomBar.setVisibility(View.GONE);
		layoutTopBar.setVisibility(View.GONE);
		mPTZPanel.setVisibility(View.GONE);
		llLandscape.setVisibility(View.GONE);
		llLandscapeDefinition.setVisibility(View.GONE);
		ivPresetLandscape.setVisibility(View.GONE);  // add by mai 2015-7-31
		setQLViewVisible(false);
		System.out.println("setQLViewVisible 5");//add for test
		LayoutMicroPhoneBottom.setVisibility(layoutBottomBar.getVisibility());
	}
	
	//��������ʾ��ʱ��  
	private int nToolsViewShowTickCount = 8;
	private int timerThreadID=0;
	class TimerThread extends Thread{
		int mThreadID=0;
		 public TimerThread(int nThreadID){
			 mThreadID = nThreadID;
		 }
		 public void run(){
			 while(mThreadID == timerThreadID){
				 
				 nToolsViewShowTickCount-=1;
		    	  if(nToolsViewShowTickCount<=0){
			    	  Message message = new Message();      
			    	  message.arg1 = Defines.MSG_HIDE_TOOLVIEW;     
			    	  handler.sendMessage(message); 
		    		  nToolsViewShowTickCount=0;
		    	  } 
		    	  
		    	 try {
					sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 }
			
		 }
	}

	//��ť��̨���Ƽ���߳�
	private int ptzTimerThreadID=0;
	class PTZTimerThread extends Thread{
		int mThreadID=0;
		 public PTZTimerThread(int nThreadID){
			 mThreadID = nThreadID;
		 }
		 public void run(){
			 boolean bLeft=false, bRight=false, bUp=false, bDown=false;
			 while(mThreadID == ptzTimerThreadID){
				 bLeft=bIsLeftPressed;
				 bRight=bIsRightPressed;
				 bUp=bIsUpPressed;
				 bDown=bIsDownPressed; 
				 
				 
				 if(bLeft && bRight){
					 bLeft=false;
					 bRight=false;
				 }
				 
				 if(bUp && bDown){
					 bUp=false;
					 bDown=false;
				 }
		    	  
				 if(bLeft||bRight||bUp||bDown){
 					 
					 mvMediaPlayer.SendPTZAction(bLeft, bRight, bUp, bDown, 0);
					 ///
					 try {
							sleep(200);
					} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
					}
				 }else{
					
					 try {
							sleep(50);
					} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
					}
				 }
		    	
			 }
			
		 }
	}

	
	//��Ļ�������ƴ�����
	private float fScaleSize = 1.0f;
	private long lScaleTime = 0;
	class ScaleGestureListener implements ScaleGestureDetector.OnScaleGestureListener {


	 @Override
	 public boolean onScale(ScaleGestureDetector detector) 
	 {
		 System.out.println("Scale: "+detector.getScaleFactor()+", "+detector.getCurrentSpan());//add for test
		 
	              // TODO Auto-generated method stub
	              if(detector.getScaleFactor()>1){//�Ŵ�
	            	  fScaleSize = fScaleSize-0.005f;
	            	  if(fScaleSize<0.2){
	            		  fScaleSize = 0.2f;
	            	  }else{
	            		  mvMediaPlayer.scale(fScaleSize, fScaleSize); 
	            	  }
	            	  
	              }else if(detector.getScaleFactor()<1){//��С
	            	  fScaleSize = fScaleSize+0.025f;
	            	  if(fScaleSize>1){
	            		  fScaleSize = 1.0f;
	            	  }else{
	            		  mvMediaPlayer.scale(fScaleSize, fScaleSize); 
	            	  }
	              } 
	              lScaleTime = System.currentTimeMillis();
//	             
	              return false;
	}
	  
	          @Override
	          public boolean onScaleBegin(ScaleGestureDetector detector) 
	         {
	             // TODO Auto-generated method stub    
	             //һ��Ҫ����true�Ż����onScale()�������
	             return true;
	         }
	 
	         @Override
	         public void onScaleEnd(ScaleGestureDetector detector) 
	         {
	             // TODO Auto-generated method stub
	             
	         }
	    
	      
	 }
	//������̨���Ƽ���߳�	
	class PTZGestureListener extends SimpleOnGestureListener {
		  
		 public static final int MOVE_TO_LEFT = 0;  
		 public static final int MOVE_TO_RIGHT =1;  
		 public static final int MOVE_TO_UP = 2;  
		 public static final int MOVE_TO_DOWN = 3;  
		  
		 
		 private int nStep = 0;  
		  
		 boolean bTouchLeft=false, bTouchRight=false,bTouchUp=false,bTouchDown=false;
         double nVelocityX = 0;//ˮƽ�ƶ����ٶ�
         double nMoveDistanceX = 0;//ˮƽ�ƶ��ľ���
         
         double nVelocityY = 0;//��ֱ�ƶ����ٶ�
         double nMoveDistanceY = 0;//��ֱ�ƶ��ľ���
         
		 float x1 = 0;  
		 float x2 = 0;  
		 float y1 = 0;  
		 float y2 = 0;  
		    
//		  private Context mContext; 
		  PTZGestureListener(Context context) { 
//		       mContext = context; 
		  }
	/*	  
		  @Override
	        public boolean onSingleTapUp(MotionEvent e) {
	            return false;
	        }

	        @Override
	        public void onLongPress(MotionEvent e) {
	            
	        }
	*/        
	        
	        @Override  
	        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,  
	                float distanceY) {  
 
 
		            
	            return false;  
	        }  
	      
	         
	        @Override  
	        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,  
	                float velocityY) {  
 

	        	if(bIsLeftPressed || bIsRightPressed || bIsUpPressed || bIsDownPressed){
	        		return true;
	        	}
	             bTouchLeft=false;
	             bTouchRight=false;
	             bTouchUp=false;
	             bTouchDown=false;
	             nVelocityX = Math.abs(velocityX);//ˮƽ�ƶ����ٶ�
	             nMoveDistanceX = Math.abs(e1.getX() - e2.getX());//ˮƽ�ƶ��ľ���
	            
	            nVelocityY = Math.abs(velocityY);//��ֱ�ƶ����ٶ�
	            nMoveDistanceY = Math.abs(e1.getY() - e2.getY());//��ֱ�ƶ��ľ���
	            
	            if(nVelocityY>=nVelocityX){
	            	nVelocityX =0;
            	}else{
            		nVelocityY=0;
            	}
	            
	            
	            if(nVelocityY < nVelocityX){
	            	nStep = 0;
	            	
	            	if(nMoveDistanceX>FLING_MIN_DISTANCE){
	            		nStep=1;
	            		if(nMoveDistanceX>FLING_MAX_DISTANCE){
	            			nStep = (int) (nMoveDistanceX/FLING_MAX_DISTANCE);
	            		}
	            	}
	            	if(nVelocityX > FLING_MIN_VELOCITY && nMoveDistanceX > FLING_MIN_DISTANCE){
		            	if(e1.getX()>e2.getX()){
		            		//@@System.out.println("onFling: Left");
		            		
		            		bTouchLeft=true;
		            	}else{
		            		//@@System.out.println("onFling: Right");
		            		bTouchRight=true;
		            	}
		            }
	            	
            	}else if(nVelocityY > nVelocityX){

            		nStep = 0;
	            	
	            	if(nMoveDistanceX>FLING_MIN_DISTANCE){
	            		nStep=1;
	            		if(nMoveDistanceX>FLING_MAX_DISTANCE){
	            			nStep = (int) (nMoveDistanceX/FLING_MAX_DISTANCE);
	            		}
	            	}
            		
            		if(nVelocityY > FLING_MIN_VELOCITY && nMoveDistanceY > FLING_MIN_DISTANCE){
    	            	if(e1.getY()>e2.getY()){
    	            		//@@System.out.println("onFling: UP");
    	            		bTouchUp=true;
    	            	}else{
    	            		//@@System.out.println("onFling: Down");
    	            		bTouchDown=true;
    	            	}
    	            	
    	            }
            	}else{
            		
            		if(nMoveDistanceY>=nMoveDistanceX){
            			if(nVelocityY > FLING_MIN_VELOCITY && nMoveDistanceY > FLING_MIN_DISTANCE){
        	            	if(e1.getY()>e2.getY()){
        	            		//@@System.out.println("onFling: UP");
        	            		bTouchUp=true;
        	            	}else{
        	            		//@@System.out.println("onFling: Down");
        	            		bTouchDown=true;
        	            	}
        	            	
        	            }
                	}else{
                		if(nVelocityX > FLING_MIN_VELOCITY && nMoveDistanceX > FLING_MIN_DISTANCE){
    		            	if(e1.getX()>e2.getX()){
    		            		//@@System.out.println("onFling: Left");
    		            		bTouchLeft=true;
    		            	}else{
    		            		//@@System.out.println("onFling: Right");
    		            		bTouchRight=true;
    		            	}
    		            }
                	}
    	            
            	}
	            
	            if(nStep>5){
	            	nStep=5;
	            }
 	            
 	            mvMediaPlayer.SendPTZAction(bTouchLeft, bTouchRight, bTouchUp, bTouchDown, nStep);
	            return false;  
	        }  
	        /**
	         * ���������ͬ��onSingleTapUp��������GestureDetectorȷ���û��ڵ�һ�δ�����Ļ��û�н����ŵڶ��δ�����Ļ��Ҳ���ǲ��ǡ�˫������ʱ�򴥷�
	         * */
	        @Override
	        public boolean onSingleTapConfirmed(MotionEvent e) {
	        	Log.i("a", "onSingleTapConfirmed"); 
	        	
	        	if(layoutBottomBar.getVisibility()==View.VISIBLE){
 	        		
					if(layoutImageQL.getVisibility()!=View.VISIBLE && nScreenOrientation == Configuration.ORIENTATION_LANDSCAPE){

 						hideToolsViews();
					}
					 
				}else{
 					
					showToolsViews();
				}
	            return false;
	        }
	 
		  
		}
	
	 
 
	 
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
	}
    
	///
	// ��ʼ�豸����
	public boolean StartSearchDevice() {
    
		System.out.println("StartSearchDevice");//add for test
 
 
		closeMulticast();
		openMulticast();
		new DeviceSearchThread(1).start(); 
		return true;

	}

	// ֹͣ�豸����
	public void StopSearchDevice() {
		closeMulticast();
	 
  
	}
  	public void openMulticast(){
 		try
    	{ 			
    		WifiManager wifiManager=(WifiManager)getSystemService(Context.WIFI_SERVICE); 
    		multicastLock = wifiManager.createMulticastLock("multicast"); 
    		multicastLock.acquire();    		
    	}
    	catch(Exception e)
		{
    		//@@System.out.println("openMulticast error");//add for test
    		multicastLock = null;
		}
 	}
 	
  	public void closeMulticast(){
 		if(multicastLock!=null){
  			multicastLock.release();
 			multicastLock=null;
 		}
 		
 	}
  	MulticastLock multicastLock = null;
	// �豸�����߳�
	public class DeviceSearchThread extends Thread {

		static final int MAX_DATA_PACKET_LENGTH = 128;

		private byte buffer[] = new byte[MAX_DATA_PACKET_LENGTH];

		private int nTreadSearchID = 0;

		public DeviceSearchThread(int nSearchID) {
			nTreadSearchID = nSearchID;
	
		}


	 
	
		public void run() {
			System.out.println("DeviceSearchThread: run ");//add for test
			 
				ArrayList<DeviceInfo>  resultList = DeviceScanner.getDeviceListFromLan();
				if(resultList!=null){
					for(int i=0; i<resultList.size(); i++){
						DeviceInfo info = resultList.get(i);
						System.out.println(i+" : "+info.getnDevID()+", "+info.getStrMac());//add for test
					}
				}
			 

		}

	}

	
	
	DatagramSocket ipuStationudpSocket = null;
	DatagramSocket ipuAPudpSocket = null;

}

 
