package com.macrovideo.demo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import android.widget.Toast;

import com.macrovideo.sdk.custom.DeviceInfo;
import com.macrovideo.sdk.custom.RecordFileInfo;
import com.macrovideo.sdk.defines.Defines;
import com.macrovideo.sdk.defines.ResultCode;
import com.macrovideo.sdk.media.IRecFileCallback;
import com.macrovideo.sdk.media.LoginHandle;
import com.macrovideo.sdk.media.RecordFileHelper;
import com.macrovideo.sdk.tools.Functions;

public class DemoSearchRecFileActivity extends Activity implements OnClickListener,
		OnItemClickListener, IRecFileCallback {

	private DeviceInfo deviceTest = new DeviceInfo(-1, 36718293, "36718293",
			"192.168.10.17", 8800, "admin", "", "unkown mac addr",
			"36718293.nvdvr.net",Defines.SERVER_SAVE_TYPE_ADD);
	private LoginHandle _deviceParam=null;
	
	static final int DATETIME_MODE_UNDEFINE = 000;
	static final int DATETIME_MODE_DATE = 100;
	static final int DATETIME_MODE_STARTTIME = 101;
	static final int DATETIME_MODE_ENDTIME = 102;
	// /
	private TabBroadcastReceiver receiver;
	private IntentFilter intentFilter;
  
	private LinearLayout layoutSearchParam = null, layoutRecFileList = null;
	private Button btnListVisible = null, btnStartSearch = null;
	private boolean isListVisible = false;

	private ProgressBar searchingProgressBar = null;
	private LinearLayout layoutDevice = null, layoutSearchDate = null,
			layoutSearchEndTime = null, layoutSearchStartTime = null;

	private ListView serverlistView = null;

	private int m_recFileSearchID = 0;// 登陆ID，用于表示当前登录的标志，避免开启多条登录线程
	private boolean bIsRecFileSearching = false;
	private ListView recFileListView = null;
	private int m_nLoginExID = 0;
	private ArrayList<RecordFileInfo> fileList = new ArrayList<RecordFileInfo>();

	private View datetimeSelectConctentView = null;
	private Dialog datetimeSelectDialog = null;

	private TextView textViewDevice = null, textViewDate = null,
			textViewStartTime = null, textViewEndTime = null;
	private Button btnDeviceSelectCancel = null;

	// ===add by mai 2015-1-23===============
	private ImageView ivNvplayerBack,ivPlayerBackType,
			btnDeviceSelectBack;
	private LinearLayout llPlayerBackListView, llPlayerBackType, llSearchType;
	private TextView tvPlayerBackType;
	private boolean bSearchType = true;
	private LinearLayout llAll;
	private int optOf;
	// ===end add by mai 2015-1-23===========

	//
	private int nDatetimeMode = DATETIME_MODE_UNDEFINE;
	private TextView tvDateTimeTitle = null, tvDateTimeCurrent = null;
	private DatePicker mSelectDatePicker = null;
	private TimePicker mSelectTimePicker = null;
	private LinearLayout layoutDatePicker = null, layoutTimePicker = null;
	private Button btnDatetimeSelectCancel = null, btnDatetimeSelectOK = null;

	private RadioButton rBtnTypeAll = null, rBtnTypeAuto = null,
			rBtnTypeAlarm = null;
	
	private LinearLayout llPlayBackTitle; // add by mai 2015-7-9 用于动态设置背景图片

	// search param
	private int nSearchChn = 0;
	private int nSearchType = Defines.FILE_TYPE_ALL;

	boolean isInit = false;
	// 搜索年月日（日期）
	private short nYear = 2000;
	private short nMonth = 0;
	private short nDay = 0;
	// 搜索开始时分秒（时间）
	private short nStartHour = 0;
	private short nStartMin = 0;
	private short nStartSec = 0;
	// 搜索结束时分秒（时间）
	private short nEndHour = 23;
	private short nEndMin = 59;
	private short nEndSec = 0;

	private boolean isActive = false;
	 
	// /
	
	  @SuppressWarnings("deprecation")
	  @Override
	     public void onCreate(Bundle savedInstanceState) {
	         super.onCreate(savedInstanceState);
 
	         requestWindowFeature(Window.FEATURE_NO_TITLE);
//	  	     getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//	         setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
		 setContentView(R.layout.activity_nvplayer_playback);
		 
			Calendar calendar = Calendar.getInstance();
			// 搜索年月日（日期）
			nYear = (short) calendar.get(Calendar.YEAR);
			nMonth = (short) calendar.get(Calendar.MONTH);
			nDay = (short) calendar.get(Calendar.DAY_OF_MONTH);
			// 搜索开始时分秒（时间）
			nStartHour = 0;
			nStartMin = 0;
			nStartSec = 0;

			// 搜索结束时分秒（时间）
			nEndHour = 23;
			nEndMin = 59;
			nEndSec = 0;
			isInit = true;
	 
			InitSubView();
			createDialogs();
	}
 
	@Override
	public void onPause() {

		super.onPause();
		this.unregisterReceiver(receiver);
		isActive = false;
		bIsRecFileSearching = false;
	}

	@Override
	public void onResume() {

		super.onResume();
		receiver = new TabBroadcastReceiver();
		this.registerReceiver(receiver, getIntentFilter());
		isActive = true;
	}

	
	
	
	@Override
	public void onDestroy() {
		
		//add by mai 2015-7-9 清空当前背景图片
		BitmapDrawable llPlayBackTitles = (BitmapDrawable) llPlayBackTitle.getBackground();
		llPlayBackTitle.setBackgroundResource(0);
		llPlayBackTitles.setCallback(null);
		llPlayBackTitles.getBitmap().recycle();
		
		//end add by mai 2015-7-9
		
		super.onDestroy();
	}

	private IntentFilter getIntentFilter() {
		if (intentFilter == null) {
			intentFilter = new IntentFilter();
			intentFilter.addAction("TAB1_ACTION");
		}
		return intentFilter;
	}

	class TabBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals("TAB1_ACTION")) {

			}
		}

	}

	// /

	@SuppressWarnings("deprecation")
	private void InitSubView() {
 		
		llPlayBackTitle = (LinearLayout) findViewById(R.id.llPlayBackTitle);
		llPlayBackTitle.setBackgroundDrawable(new BitmapDrawable(Functions.readBitMap(this, R.drawable.device_config_device_choose_bg_1)));

		// ===add by mai 2015-1-23===========================
		ivNvplayerBack = (ImageView) findViewById(R.id.ivNvplayerBack);
		ivNvplayerBack.setOnClickListener(this);
		
		ivPlayerBackType = (ImageView) this
				.findViewById(R.id.ivPlayer_back_type);
		llAll = (LinearLayout) findViewById(R.id.llAll);
		
		serverlistView = (ListView) findViewById(R.id.lvPlayer_back);
		
		llPlayerBackListView = (LinearLayout) this
				.findViewById(R.id.llPlayer_back_listView);
		llPlayerBackType = (LinearLayout) this
				.findViewById(R.id.llPlayerBackType);
		llSearchType = (LinearLayout) this
				.findViewById(R.id.llSearchType);
		llSearchType.setOnClickListener(this);
		tvPlayerBackType = (TextView) this
				.findViewById(R.id.tvPlayer_back_type);
		tvPlayerBackType.setText(getString(R.string.AllPlayBack));
		// ===end add by mai 2015-1-23=======================

 		
		layoutSearchParam = (LinearLayout) this
				.findViewById(R.id.layoutSearchParam);
		layoutRecFileList = (LinearLayout) this
				.findViewById(R.id.layoutRecFileList);
		btnStartSearch = (Button) findViewById(R.id.btnStartSearch);
		btnStartSearch.setOnClickListener(this);

		btnListVisible = (Button) findViewById(R.id.btnListVisible);
		btnListVisible.setOnClickListener(this);

		btnDeviceSelectBack = (ImageView) 
				findViewById(R.id.btnDeviceSelectBack);
		btnDeviceSelectBack.setOnClickListener(this);

		recFileListView = (ListView) findViewById(R.id.recfile_list);
		recFileListView.setOnItemClickListener(this);
		 

		searchingProgressBar = (ProgressBar) this
				.findViewById(R.id.searchingProgressBar);
		searchingProgressBar.setVisibility(View.GONE);

		layoutDevice = (LinearLayout) this
				.findViewById(R.id.layoutDevice);
		layoutDevice.setOnClickListener(this);
		layoutSearchDate = (LinearLayout) this
				.findViewById(R.id.layoutSearchDate);
		layoutSearchDate.setOnClickListener(this);
		layoutSearchEndTime = (LinearLayout)
				findViewById(R.id.layoutSearchEndTime);
		layoutSearchEndTime.setOnClickListener(this);
		layoutSearchStartTime = (LinearLayout)
				findViewById(R.id.layoutSearchStartTime);
		layoutSearchStartTime.setOnClickListener(this);

		textViewDevice = (TextView) this
				.findViewById(R.id.textViewDevice);
		textViewDate = (TextView) findViewById(R.id.textViewDate);
		textViewStartTime = (TextView) this
				.findViewById(R.id.textViewStartTime);
		textViewEndTime = (TextView) this
				.findViewById(R.id.textViewEndTime);

 			if (textViewDevice != null) {
				textViewDevice.setText(deviceTest.getStrName());
			 
			}
	 
//
//		if (isListVisible) {
//			layoutSearchParam.setVisibility(View.GONE);
//			layoutRecFileList.setVisibility(View.VISIBLE);
//			 
//			if (isLoadFromDatabase) {// 需要从数据库拿数据
//				GetRecFileListFromDatabase();
//			} else {
//				refleshRecFileList();
//			}
//
//		} else {
//			layoutSearchParam.setVisibility(View.VISIBLE);
//			layoutRecFileList.setVisibility(View.GONE);
//
//			if (isLoadFromDatabase) {// 需要从数据库拿数据
//				GetRecFileListFromDatabase();
//			}
//		}
 		//
		if (nMonth < 9 && nDay < 10) {
			textViewDate
					.setText("" + nYear + "-0" + (nMonth + 1) + "-0" + nDay);
		} else if (nMonth >= 9 && nDay < 10) {
			textViewDate.setText("" + nYear + "-" + (nMonth + 1) + "-0" + nDay);
		} else if (nMonth < 9 && nDay >= 10) {
			textViewDate.setText("" + nYear + "-0" + (nMonth + 1) + "-" + nDay);
		} else {
			textViewDate.setText("" + nYear + "-" + (nMonth + 1) + "-" + nDay);
		}

		if (nStartHour <= 9 && nStartMin <= 9) {
			textViewStartTime.setText("0" + nStartHour + ":0" + nStartMin);
		} else if (nStartHour <= 9 && nStartMin > 9) {
			textViewStartTime.setText("0" + nStartHour + ":" + nStartMin);
		} else if (nStartHour > 9 && nStartMin <= 9) {
			textViewStartTime.setText("" + nStartHour + ":0" + nStartMin);
		} else {
			textViewStartTime.setText("" + nStartHour + ":" + nStartMin);
		}

		if (nEndHour <= 9 && nStartMin <= 9) {
			textViewEndTime.setText("0" + nEndHour + ":0" + nEndMin);
		} else if (nEndHour <= 9 && nStartMin > 9) {
			textViewEndTime.setText("0" + nEndHour + ":" + nEndMin);
		} else if (nEndHour > 9 && nEndMin <= 9) {
			textViewEndTime.setText("" + nEndHour + ":0" + nEndMin);
		} else {
			textViewEndTime.setText("" + nEndHour + ":" + nEndMin);
		}
		//
 		rBtnTypeAll = (RadioButton) findViewById(R.id.rBtnTypeAll);
		rBtnTypeAuto = (RadioButton)findViewById(R.id.rBtnTypeAuto);
		rBtnTypeAlarm = (RadioButton)findViewById(R.id.rBtnTypeAlarm);
		//
		switch (nSearchType) {
		case Defines.FILE_TYPE_ALL:
			rBtnTypeAll.setChecked(true);
			rBtnTypeAuto.setChecked(false);
			rBtnTypeAlarm.setChecked(false);
			break;
		case Defines.FILE_TYPE_NORMAL:
			rBtnTypeAll.setChecked(false);
			rBtnTypeAuto.setChecked(true);
			rBtnTypeAlarm.setChecked(false);
			break;
		case Defines.FILE_TYPE_ALARM:
			rBtnTypeAll.setChecked(false);
			rBtnTypeAuto.setChecked(false);
			rBtnTypeAlarm.setChecked(true);
			break;
		}
 		RadioGroup group = (RadioGroup) this
				.findViewById(R.id.rGroupRecType);
		// 绑定一个匿名监听器
		group.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				// 获取变更后的选中项的ID
				int radioButtonId = arg0.getCheckedRadioButtonId();
				if (radioButtonId == R.id.rBtnTypeAll) {
					nSearchType = Defines.FILE_TYPE_ALL;
					// ===add by mai 5015-1-26========
					tvPlayerBackType.setText(getString(R.string.AllPlayBack));
					// ===end add by mai 2015-1-26======
				} else if (radioButtonId == R.id.rBtnTypeAuto) {
					nSearchType = Defines.FILE_TYPE_NORMAL;
					// ===add by mai 5015-1-26========
					tvPlayerBackType.setText(getString(R.string.record_auto_record_title));
					// ===end add by mai 2015-1-26======
				} else if (radioButtonId == R.id.rBtnTypeAlarm) {
					nSearchType = Defines.FILE_TYPE_ALARM;
					// ===add by mai 5015-1-26========
					tvPlayerBackType.setText(getString(R.string.record_alarm_record_title));
					// ===end add by mai 2015-1-26======
				}

			}
		});
 	}

	private void createDialogs() { // 创建登陆时 ，进度条对话框

	 
		 
		//
		datetimeSelectConctentView = LayoutInflater.from(this)
				.inflate(R.layout.datetime_select_dialog, null);
		datetimeSelectDialog = new Dialog(this,
				R.style.dialog_bg_transparent);
		datetimeSelectDialog.setContentView(datetimeSelectConctentView);
		datetimeSelectDialog
				.setOnShowListener(new DialogInterface.OnShowListener() {

					@Override
					public void onShow(DialogInterface dialog) {
						// TODO Auto-generated method stub

						tvDateTimeTitle = (TextView) datetimeSelectConctentView
								.findViewById(R.id.tvDateTimeTitle);
						tvDateTimeCurrent = (TextView) datetimeSelectConctentView
								.findViewById(R.id.tvDateTimeCurrent);

						mSelectDatePicker = (DatePicker) datetimeSelectConctentView
								.findViewById(R.id.mSelectDatePicker);
						mSelectTimePicker = (TimePicker) datetimeSelectConctentView
								.findViewById(R.id.mSelectTimePicker);

						layoutDatePicker = (LinearLayout) datetimeSelectConctentView
								.findViewById(R.id.layoutDatePicker);
						layoutTimePicker = (LinearLayout) datetimeSelectConctentView
								.findViewById(R.id.layoutTimePicker);

						btnDatetimeSelectCancel = (Button) datetimeSelectConctentView
								.findViewById(R.id.btnDatetimeSelectCancel);
						btnDatetimeSelectOK = (Button) datetimeSelectConctentView
								.findViewById(R.id.btnDatetimeSelectOK);

						btnDatetimeSelectOK
								.setOnClickListener(DemoSearchRecFileActivity.this);
						btnDatetimeSelectCancel
								.setOnClickListener(DemoSearchRecFileActivity.this);

						// Calendar calendar = Calendar.getInstance();
						if (nDatetimeMode == DATETIME_MODE_DATE) {
							tvDateTimeTitle.setText(R.string.lblDate);
							layoutDatePicker.setVisibility(View.VISIBLE);
							layoutTimePicker.setVisibility(View.GONE);

							mSelectDatePicker.init(nYear, nMonth, nDay,
									new DatePicker.OnDateChangedListener() {

										@Override
										public void onDateChanged(
												DatePicker view, int year,
												int monthOfYear, int dayOfMonth) {
											// TODO Auto-generated method stub
											if ((mSelectDatePicker.getMonth() + 1) < 10
													&& mSelectDatePicker
															.getDayOfMonth() < 10) {
												tvDateTimeCurrent.setText(""
														+ mSelectDatePicker
																.getYear()
														+ "-0"
														+ (mSelectDatePicker
																.getMonth() + 1)
														+ "-0"
														+ mSelectDatePicker
																.getDayOfMonth());
											} else if ((mSelectDatePicker
													.getMonth() + 1) >= 10
													&& mSelectDatePicker
															.getDayOfMonth() < 10) {
												tvDateTimeCurrent.setText(""
														+ mSelectDatePicker
																.getYear()
														+ "-"
														+ (mSelectDatePicker
																.getMonth() + 1)
														+ "-0"
														+ mSelectDatePicker
																.getDayOfMonth());
											} else if ((mSelectDatePicker
													.getMonth() + 1) < 10
													&& mSelectDatePicker
															.getDayOfMonth() >= 10) {
												tvDateTimeCurrent.setText(""
														+ mSelectDatePicker
																.getYear()
														+ "-0"
														+ (mSelectDatePicker
																.getMonth() + 1)
														+ "-"
														+ mSelectDatePicker
																.getDayOfMonth());
											} else {
												tvDateTimeCurrent.setText(""
														+ mSelectDatePicker
																.getYear()
														+ "-"
														+ (mSelectDatePicker
																.getMonth() + 1)
														+ "-"
														+ mSelectDatePicker
																.getDayOfMonth());
											}

										}
									});

							if ((mSelectDatePicker.getMonth() + 1) < 10
									&& mSelectDatePicker.getDayOfMonth() < 10) {
								tvDateTimeCurrent.setText(""
										+ mSelectDatePicker.getYear() + "-0"
										+ (mSelectDatePicker.getMonth() + 1)
										+ "-0"
										+ mSelectDatePicker.getDayOfMonth());
							} else if ((mSelectDatePicker.getMonth() + 1) >= 10
									&& mSelectDatePicker.getDayOfMonth() < 10) {
								tvDateTimeCurrent.setText(""
										+ mSelectDatePicker.getYear() + "-"
										+ (mSelectDatePicker.getMonth() + 1)
										+ "-0"
										+ mSelectDatePicker.getDayOfMonth());
							} else if ((mSelectDatePicker.getMonth() + 1) < 10
									&& mSelectDatePicker.getDayOfMonth() >= 10) {
								tvDateTimeCurrent.setText(""
										+ mSelectDatePicker.getYear() + "-0"
										+ (mSelectDatePicker.getMonth() + 1)
										+ "-"
										+ mSelectDatePicker.getDayOfMonth());
							} else {
								tvDateTimeCurrent.setText(""
										+ mSelectDatePicker.getYear() + "-"
										+ (mSelectDatePicker.getMonth() + 1)
										+ "-"
										+ mSelectDatePicker.getDayOfMonth());
							}

						} else if (nDatetimeMode == DATETIME_MODE_STARTTIME) {
							tvDateTimeTitle.setText(R.string.lblStartTime);
							layoutDatePicker.setVisibility(View.GONE);
							layoutTimePicker.setVisibility(View.VISIBLE);

							mSelectTimePicker.setIs24HourView(true);
							mSelectTimePicker.setCurrentHour((int) nStartHour);// 设置timePicker小时数
							mSelectTimePicker.setCurrentMinute((int) nStartMin); // 设置timePicker分钟数
							mSelectTimePicker
									.setOnTimeChangedListener(new OnTimeChangedListener() {

										@Override
										public void onTimeChanged(
												TimePicker view, int hourOfDay,
												int minute) {
											// TODO Auto-generated method stub
											if (hourOfDay < 10 && minute < 10) {
												tvDateTimeCurrent.setText("0"
														+ hourOfDay + ":0"
														+ minute);
											} else if (hourOfDay >= 10
													&& minute < 10) {
												tvDateTimeCurrent.setText(""
														+ hourOfDay + ":0"
														+ minute);
											} else if (hourOfDay < 10
													&& minute >= 10) {
												tvDateTimeCurrent.setText("0"
														+ hourOfDay + ":"
														+ minute);
											} else {
												tvDateTimeCurrent.setText(""
														+ hourOfDay + ":"
														+ minute);
											}
											// tvDateTimeCurrent.setText(""+hourOfDay+":"+minute);
										}

									});

							if (mSelectTimePicker.getCurrentHour() < 10
									&& mSelectTimePicker.getCurrentMinute() < 10) {
								tvDateTimeCurrent.setText("0"
										+ mSelectTimePicker.getCurrentHour()
										+ ":0"
										+ mSelectTimePicker.getCurrentMinute());
							} else if (mSelectTimePicker.getCurrentHour() >= 10
									&& mSelectTimePicker.getCurrentMinute() < 10) {
								tvDateTimeCurrent.setText(""
										+ mSelectTimePicker.getCurrentHour()
										+ ":0"
										+ mSelectTimePicker.getCurrentMinute());
							} else if (mSelectTimePicker.getCurrentHour() < 10
									&& mSelectTimePicker.getCurrentMinute() >= 10) {
								tvDateTimeCurrent.setText("0"
										+ mSelectTimePicker.getCurrentHour()
										+ ":"
										+ mSelectTimePicker.getCurrentMinute());
							} else {
								tvDateTimeCurrent.setText(""
										+ mSelectTimePicker.getCurrentHour()
										+ ":"
										+ mSelectTimePicker.getCurrentMinute());
							}

						} else if (nDatetimeMode == DATETIME_MODE_ENDTIME) {
							tvDateTimeTitle.setText(R.string.lblEndTime);
							layoutDatePicker.setVisibility(View.GONE);
							layoutTimePicker.setVisibility(View.VISIBLE);

							//@@System.out.println();// add for test
							mSelectTimePicker.setIs24HourView(true);
							mSelectTimePicker.setCurrentHour((int) nEndHour);// 设置timePicker小时数
							mSelectTimePicker.setCurrentMinute((int) nEndMin); // 设置timePicker分钟数
							mSelectTimePicker
									.setOnTimeChangedListener(new OnTimeChangedListener() {

										@Override
										public void onTimeChanged(
												TimePicker view, int hourOfDay,
												int minute) {
											// TODO Auto-generated method stub
											if (hourOfDay < 10 && minute < 10) {
												tvDateTimeCurrent.setText("0"
														+ hourOfDay + ":0"
														+ minute);
											} else if (hourOfDay >= 10
													&& minute < 10) {
												tvDateTimeCurrent.setText(""
														+ hourOfDay + ":0"
														+ minute);
											} else if (hourOfDay < 10
													&& minute >= 10) {
												tvDateTimeCurrent.setText("0"
														+ hourOfDay + ":"
														+ minute);
											} else {
												tvDateTimeCurrent.setText(""
														+ hourOfDay + ":"
														+ minute);
											}
											// tvDateTimeCurrent.setText(""+hourOfDay+":"+minute);
										}

									});

							if (mSelectTimePicker.getCurrentHour() < 10
									&& mSelectTimePicker.getCurrentMinute() < 10) {
								tvDateTimeCurrent.setText("0"
										+ mSelectTimePicker.getCurrentHour()
										+ ":0"
										+ mSelectTimePicker.getCurrentMinute());
							} else if (mSelectTimePicker.getCurrentHour() >= 10
									&& mSelectTimePicker.getCurrentMinute() < 10) {
								tvDateTimeCurrent.setText(""
										+ mSelectTimePicker.getCurrentHour()
										+ ":0"
										+ mSelectTimePicker.getCurrentMinute());
							} else if (mSelectTimePicker.getCurrentHour() < 10
									&& mSelectTimePicker.getCurrentMinute() >= 10) {
								tvDateTimeCurrent.setText("0"
										+ mSelectTimePicker.getCurrentHour()
										+ ":"
										+ mSelectTimePicker.getCurrentMinute());
							} else {
								tvDateTimeCurrent.setText(""
										+ mSelectTimePicker.getCurrentHour()
										+ ":"
										+ mSelectTimePicker.getCurrentMinute());
							}
						}

					}

				});

		datetimeSelectDialog
				.setOnDismissListener(new DialogInterface.OnDismissListener() {

					@Override
					public void onDismiss(DialogInterface dialog) {
						// TODO Auto-generated method stub

					}

				});

	}
	
 
	private static final int  HANDLE_MSG_CODE_SEARCH = 0x8001;
	private static final int  HANDLE_MSG_CODE_SEARCH_RESULT = 0x8002;
	private static final int SEARCH_RESULT_START = 0x2001;
	private static final int SEARCH_RESULT_ENDT = 0x2002;
 
	
	private Handler handler = new Handler() {

		@SuppressLint("HandlerLeak")
		public void handleMessage(Message msg) {
			
			if (msg.arg1 == HANDLE_MSG_CODE_SEARCH_RESULT) {//获取句柄失败
				// 处理登录结果
				switch (msg.arg2) {
				  
 
				case ResultCode.RESULT_CODE_FAIL_SERVER_CONNECT_FAIL: {// 登录失败-服务器不在线
					ShowAlert(
							getString(R.string.alert_title_login_failed)
									+ "Login fail","");
				}
					break;
				case ResultCode.RESULT_CODE_FAIL_VERIFY_FAILED: {// 登录失败-用户名或密码错误
					ShowAlert(getString(R.string.alert_title_login_failed),
							getString(R.string.notice_Result_VerifyFailed));
				}
					break;
				case ResultCode.RESULT_CODE_FAIL_USER_NOEXIST: {// 登录失败-用户名或密码错误
					ShowAlert(getString(R.string.alert_title_login_failed),
							getString(R.string.notice_Result_UserNoExist));
				}
					break;
				case ResultCode.RESULT_CODE_FAIL_PWD_ERROR: {// 登录失败-用户名或密码错误
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
			} else if(msg.arg1 == HANDLE_MSG_CODE_SEARCH) {//搜索文件
					System.out.println("HANDLE_MSG_CODE_SEARCH ");//add for test
					if(msg.arg2==SEARCH_RESULT_ENDT){
						System.out.println("SEARCH_RESULT_ENDT "+fileList.size());//add for test
						searchingProgressBar.setVisibility(View.GONE);
						if (fileList==null || fileList.size() <= 0) {
							// 隐藏列表
							isListVisible = false;
							layoutSearchParam.setVisibility(View.VISIBLE);
							layoutRecFileList.setVisibility(View.GONE); 
						}
					}
			} else if (msg.arg1 == Defines.HANDLE_MSG_CODE_RECORD_FILES_RECV) {
				
				System.out.println("HANDLE_MSG_CODE_RECORD_FILES_RECV ");//add for test
				Bundle data  = msg.getData();
				if(data==null){
					return;
				}
				ArrayList<RecordFileInfo> recList= data.getParcelableArrayList(Defines.RECORD_FILE_RETURN_MESSAGE);
				System.out.println("HANDLE_MSG_CODE_RECORD_FILES_RECV "+fileList.size());//add for test
				if(recList!=null && recList.size()>0){
					
//					for(int i=0; i<recList.size(); i++)
//					{
//						 
//						fileList.add(recList.get(i));
//					}
					fileList.addAll(recList);
				}
				 
				if (isActive) {
					try {
						refleshRecFileList();
					} catch (Exception e) {

					}

				}

			}/* else if (msg.arg1 == Defines.HANDLE_MSG_CODE_GET_RECORD_FILES_END) {

				searchingProgressBar.setVisibility(View.GONE);

			//	System.out.println("msg.arg2 = "+msg.arg2);//add for test
 
				Toast toast = null;
				switch (msg.arg2) {
				case Defines.REC_FILE_SEARCH_RESULT_CODE_SUCCESS:
					SaveRecFileListToDatabase();
					if (fileList.size() <= 0) {
						toast = Toast.makeText(
								getApplicationContext(),
								getString(R.string.noticRecOKNOFiles),
								Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
					} else {

						if (isActive) {
							try {
								refleshRecFileList();
							} catch (Exception e) {

							}

						}
					}
					break;
				case Defines.REC_FILE_SEARCH_RESULT_CODE_FAIL_SERVER_OFFLINE:
 					if (isActive) {
						try {
							toast = Toast.makeText(
									getApplicationContext(),
									getString(R.string.noticRecConnectFail),
									Toast.LENGTH_SHORT);
							toast.setGravity(Gravity.CENTER, 0, 0);
							toast.show();
						} catch (Exception e) {

						}

					}

					break;
				case Defines.REC_FILE_SEARCH_RESULT_CODE_FAIL_VERIFY_FAILED:
 					if (isActive) {
						try {
							toast = Toast.makeText(
									getApplicationContext(),
									getString(R.string.notice_Result_VerifyFailed),
									Toast.LENGTH_SHORT);
							toast.setGravity(Gravity.CENTER, 0, 0);
							toast.show();
						} catch (Exception e) {

						}

					}

					break;
				case Defines.REC_FILE_SEARCH_RESULT_CODE_FAIL_USER_NOEXIST:
 					if (isActive) {
						try {
							toast = Toast.makeText(
									getApplicationContext(),
									getString(R.string.notice_Result_UserNoExist),
									Toast.LENGTH_SHORT);
							toast.setGravity(Gravity.CENTER, 0, 0);
							toast.show();
						} catch (Exception e) {

						}

					}

					break;
				case Defines.REC_FILE_SEARCH_RESULT_CODE_FAIL_PWD_ERROR:
					if (isActive) {
						try {
							toast = Toast.makeText(
									getApplicationContext(),
									getString(R.string.notice_Result_PWDError),
									Toast.LENGTH_SHORT);
							toast.setGravity(Gravity.CENTER, 0, 0);
							toast.show();
						} catch (Exception e) {

						}

					}

					break;
 
				case Defines.REC_FILE_SEARCH_RESULT_CODE_FAIL_NET_DOWN:

					if (isActive) {
						try {
							toast = Toast.makeText(
									getApplicationContext(),
									getString(R.string.noticRecConnectFail),
									Toast.LENGTH_SHORT);
							toast.setGravity(Gravity.CENTER, 0, 0);
							toast.show();
						} catch (Exception e) {

						}

					}

					break;
				case Defines.REC_FILE_SEARCH_RESULT_CODE_FAIL_NET_POOL:

					if (isActive) {
						try {
							toast = Toast.makeText(
									getApplicationContext(),
									getString(R.string.noticRecConnectFail),
									Toast.LENGTH_SHORT);
							toast.setGravity(Gravity.CENTER, 0, 0);
							toast.show();
						} catch (Exception e) {

						}

					}
					break;
					default:
						if (isActive) {
							try {
								toast = Toast.makeText(
										getApplicationContext(),
										getString(R.string.noticRecConnectFail),
										Toast.LENGTH_SHORT);
								toast.setGravity(Gravity.CENTER, 0, 0);
								toast.show();
							} catch (Exception e) {

							}

						}
						break;
				}

				if (fileList.size() <= 0) {
					// 隐藏列表
					isListVisible = false;
					layoutSearchParam.setVisibility(View.VISIBLE);
					layoutRecFileList.setVisibility(View.GONE);

					try {
						Thread.sleep(400);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}*/
		}
	};

	//提示框显示
		//msg：提示的消息
		private void ShowAlert(String title, String msg){
			try{
				new AlertDialog.Builder(DemoSearchRecFileActivity.this)
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
	public void onClick(View v) {

		//@@System.out.println("onClick");// add for test

		switch (v.getId()) {
		case R.id.btnDatetimeSelectOK:

			switch (nDatetimeMode) {
			case DATETIME_MODE_DATE:

				nYear = (short) mSelectDatePicker.getYear();
				nMonth = (short) mSelectDatePicker.getMonth();
				nDay = (short) mSelectDatePicker.getDayOfMonth();

				if (nMonth < 9 && nDay < 10) {
					textViewDate.setText("" + nYear + "-0" + (nMonth + 1)
							+ "-0" + nDay);
				} else if (nMonth >= 9 && nDay < 10) {
					textViewDate.setText("" + nYear + "-" + (nMonth + 1) + "-0"
							+ nDay);
				} else if (nMonth < 9 && nDay >= 10) {
					textViewDate.setText("" + nYear + "-0" + (nMonth + 1) + "-"
							+ nDay);
				} else {
					textViewDate.setText("" + nYear + "-" + (nMonth + 1) + "-"
							+ nDay);
				}

				break;
			case DATETIME_MODE_STARTTIME:

				if ((mSelectTimePicker.getCurrentHour() > nEndHour)
						|| (mSelectTimePicker.getCurrentHour() == nEndHour && mSelectTimePicker
								.getCurrentMinute() > nEndMin)) {

					Toast toast = Toast.makeText(
							getApplicationContext(),
							getString(R.string.noticStartlargeThanEnd),
							Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				} else {
					nStartHour = (short) (int) (mSelectTimePicker
							.getCurrentHour());
					nStartMin = (short) (int) (mSelectTimePicker
							.getCurrentMinute());
					nStartSec = 0;

					String strHour = "";
					String strMinute = "";

					if (nStartHour < 10) {
						strHour = "0" + nStartHour;
					} else {
						strHour = "" + nStartHour;
					}

					if (nStartMin < 10) {
						strMinute = "0" + nStartMin;
					} else {
						strMinute = "" + nStartMin;
					}

					textViewStartTime.setText(strHour + ":" + strMinute);

				}

				break;

			case DATETIME_MODE_ENDTIME:

				if ((mSelectTimePicker.getCurrentHour() < nStartHour)
						|| (mSelectTimePicker.getCurrentHour() == nStartHour && mSelectTimePicker
								.getCurrentMinute() < nStartMin)) {

					Toast toast = Toast.makeText(
							getApplicationContext(),
							getString(R.string.noticEndLessThanStart),
							Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				} else {

					nEndHour = (short) (int) (mSelectTimePicker
							.getCurrentHour());
					nEndMin = (short) (int) (mSelectTimePicker
							.getCurrentMinute());
					nEndSec = 0;

					String strHour = "";
					String strMinute = "";

					if (nEndHour < 10) {
						strHour = "0" + nEndHour;
					} else {
						strHour = "" + nEndHour;
					}

					if (nEndMin < 10) {
						strMinute = "0" + nEndMin;
					} else {
						strMinute = "" + nEndMin;
					}

					textViewEndTime.setText(strHour + ":" + strMinute);
				}

				break;

			}

			nDatetimeMode = DATETIME_MODE_UNDEFINE;
			if (datetimeSelectDialog != null) {
				if (datetimeSelectDialog.isShowing()) {
					datetimeSelectDialog.dismiss();
				}
			}
			break;

		case R.id.btnDatetimeSelectCancel:

			nDatetimeMode = DATETIME_MODE_UNDEFINE;
			if (datetimeSelectDialog != null) {
				if (datetimeSelectDialog.isShowing()) {
					datetimeSelectDialog.dismiss();
				}
			}
			break;
 

		case R.id.btnDeviceSelectBack:
			isListVisible = false;
			//@@System.out.println("btnDeviceSelectBack");// add for test

			layoutSearchParam.setVisibility(View.VISIBLE);
			layoutRecFileList.setVisibility(View.GONE);

			break;
		case R.id.btnStartSearch:
			
			  
					isListVisible = true;
					layoutSearchParam.setVisibility(View.GONE);
					layoutRecFileList.setVisibility(View.VISIBLE);
					searchingProgressBar.setVisibility(View.VISIBLE);

					GetRecFileList();
				 
			 
			
			break;

	 
		case R.id.llSearchType:

			if (bSearchType) {
				bSearchType = false;
				llPlayerBackType.setVisibility(View.VISIBLE);
				llPlayerBackListView.setVisibility(View.GONE);
				ivPlayerBackType
						.setImageResource(R.drawable.play_back_video_back_1);

			} else {

				bSearchType = true;
				llPlayerBackType.setVisibility(View.GONE);
				ivPlayerBackType
				.setImageResource(R.drawable.play_back_video_back_2);

			}

			break;

		 
		// =====end add by mai 2015-1-26========
		case R.id.layoutSearchDate:
			ShowDateSelectView();
			break;
		case R.id.layoutSearchEndTime: // alter by mai 2015-1-26=====
			ShowEndTimeSelectView();
			break;
		case R.id.layoutSearchStartTime: // alter by mai 2015-1-26===
			ShowStartTimeSelectView();
			break;
		case R.id.btnListVisible:
 
				isListVisible = true;
				layoutSearchParam.setVisibility(View.GONE);
				layoutRecFileList.setVisibility(View.VISIBLE);

				refleshRecFileList();
			 
			
			

			break;
		}

	}

	public void ShowRecFileList() {
		if (layoutSearchParam != null) {
			layoutSearchParam.setVisibility(View.GONE);
		}

		if (layoutRecFileList != null) {
			layoutRecFileList.setVisibility(View.VISIBLE);
		}

	}

	public void HideRecFileList() {

		if (layoutSearchParam != null) {
			layoutSearchParam.setVisibility(View.VISIBLE);
		}

		if (layoutRecFileList != null) {
			layoutRecFileList.setVisibility(View.GONE);
		}
	}

	public boolean isListVisible() {
		return isListVisible;
	}

	public void setListVisible(boolean isListVisible) {
		this.isListVisible = isListVisible;
		try {
			if (this.isListVisible) {
				ShowRecFileList();
			} else {
				HideRecFileList();
			}
		} catch (Exception e) {

		}

	}
 
	private void ShowDateSelectView() {
		nDatetimeMode = DATETIME_MODE_DATE;
		datetimeSelectDialog.show();
	}

	private void ShowStartTimeSelectView() {
		nDatetimeMode = DATETIME_MODE_STARTTIME;
		datetimeSelectDialog.show();
	}

	private void ShowEndTimeSelectView() {
		nDatetimeMode = DATETIME_MODE_ENDTIME;
		datetimeSelectDialog.show();
	}

	// /rec file api
	public void refleshRecFileList() {

		//
		//@@System.out.println("refleshRecFileList: " + fileList.size());// add for
																		// test

		int nFileSize = 0;
		double fFileSize = 0.0;
		double nTimeLen = 0;

		String strInfo = "";
		String strSize = null;
		String strStartTime = null;
		String strTimeLen = null;

		if (fileList.size() > 0) {
			ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();

			RecordFileInfo fileInfo = null;
			for (int i = 0; i < fileList.size(); i++) {
				fileInfo = fileList.get(i);

				HashMap<String, Object> map = new HashMap<String, Object>();

				map.put("ItemTitleName", R.id.ItemFileName);
				map.put("ItemTitleInfo", R.id.ItemFileInfo);

				map.put("FileName", fileInfo.getStrFileName());

				nFileSize = fileInfo.getnFileSize();

				strSize = getString(R.string.strFileSize);
				if (nFileSize > 1024000) {
					fFileSize = nFileSize / 1048576.0;
					if (fFileSize >= 100) {
						strSize = strSize + String.format("%.0f", fFileSize)
								+ " MB";
					} else if (fFileSize >= 1) {
						strSize = strSize + String.format("%.1f", fFileSize)
								+ " MB";
					} else {
						strSize = strSize + String.format("%.2f", fFileSize)
								+ " MB";
					}

				} else if (nFileSize > 1024) {
					fFileSize = nFileSize / 1024.0;
					strSize = strSize + String.format("%.0f", fFileSize)
							+ " KB";
				} else {
					strSize = strSize + nFileSize + " B";
				}

				strStartTime = getString(R.string.strStartTime);

				if (fileInfo.getuStartHour() >= 10) {
					strStartTime = strStartTime + fileInfo.getuStartHour();
				} else {
					strStartTime = strStartTime + "0"
							+ fileInfo.getuStartHour();
				}

				if (fileInfo.getuStartMin() >= 10) {
					strStartTime = strStartTime + ":" + fileInfo.getuStartMin();
				} else {
					strStartTime = strStartTime + ":0"
							+ fileInfo.getuStartMin();
				}

				if (fileInfo.getuStartSec() >= 10) {
					strStartTime = strStartTime + ":" + fileInfo.getuStartSec();
				} else {
					strStartTime = strStartTime + ":0"
							+ fileInfo.getuStartSec();
				}

				strTimeLen = getString(R.string.strTimeLen);
				if (fileInfo.getuFileTimeLen() >= 3600) {
					nTimeLen = fileInfo.getuFileTimeLen() / 3600.0;
					strTimeLen = strTimeLen + String.format("%.1f", nTimeLen)
							+ getString(R.string.strHour);
				} else if (fileInfo.getuFileTimeLen() >= 60) {
					nTimeLen = fileInfo.getuFileTimeLen() / 60.0;
					strTimeLen = strTimeLen + String.format("%.1f", nTimeLen)
							+ getString(R.string.strMin);
					// //@@System.out.println("MIN : "+String.format("%.0f",
					// nTimeLen)
					// +" , "+nTimeLen+", "+fileInfo.getuFileTimeLen());//add
					// for test
				} else {
					nTimeLen = fileInfo.getuFileTimeLen();
					strTimeLen = strTimeLen + String.format("%.0f", nTimeLen)
							+ getString(R.string.strSec);
				}

				strInfo = strStartTime + " " + strTimeLen + " " + strSize;
				map.put("FileInfo", strInfo);
				// ===add by mai 2015-1-26=======
				map.put("FileSize", strSize);
				map.put("FileStartTime", strStartTime);
				map.put("FileTimeLen", strTimeLen);
				// ===end add by mai 2015-1-26====
				listItem.add(map);

			}

			// 生成适配器的Item和动态数组对应的元素

			RecFileListViewAdapter recFileListItemAdapter = new RecFileListViewAdapter(
					this, listItem,// 数据源

					R.layout.recfile_list_item,// ListItem的XML实现

					// 动态数组与ImageItem对应的子项

					new String[] { "ItemTitleName", "ItemTitleInfo",
							"ItemSize", "ItemTimeLen" }, // add by mai 2015-1-26

					// ImageItem的XML文件里面的一个ImageView,两个TextView ID

					new int[] { R.id.ItemFileName, R.id.ItemFileInfo,
							R.id.tvSize, R.id.tvTimeLen } // add by mai
															// 2015-1-26

			);

			if (recFileListView == null) {
				recFileListView = (ListView) this
						.findViewById(R.id.recfile_list);
			}
			recFileListView.setAdapter(recFileListItemAdapter);
	 

		} else {
			recFileListView.setAdapter(null);
		}

	}

	private class RecFileListViewAdapter extends BaseAdapter {

		private class ItemViewHolder {
			TextView tvName;
			TextView tvInfo;
			TextView tvSize; // add by mai 2015-1-26
			TextView tvTimeLen; // add by mai 2015-1-26

		}

		private ArrayList<HashMap<String, Object>> mAppList;
		private LayoutInflater mInflater;
		private Context mContext;
		private String[] keyString;
		private int[] valueViewID;
		private ItemViewHolder holder;

		public RecFileListViewAdapter(Context c,
				ArrayList<HashMap<String, Object>> appList, int resource,
				String[] from, int[] to) {
			mAppList = appList;
			mContext = c;
			mInflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			keyString = new String[from.length];
			valueViewID = new int[to.length];
			System.arraycopy(from, 0, keyString, 0, from.length);
			System.arraycopy(to, 0, valueViewID, 0, to.length);
		}

		@Override
		public int getCount() {
			return mAppList.size();
		}

		@Override
		public Object getItem(int position) {
			return mAppList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if (convertView != null) {
				holder = (ItemViewHolder) convertView.getTag();
			} else {
				convertView = mInflater.inflate(R.layout.recfile_list_item,
						null);
				holder = new ItemViewHolder();

				convertView.setTag(holder);
			}

			holder.tvName = (TextView) convertView.findViewById(valueViewID[0]);
			holder.tvInfo = (TextView) convertView.findViewById(valueViewID[1]);
			// ===add by mai 2015-1-26=====
			holder.tvSize = (TextView) convertView.findViewById(valueViewID[2]);
			holder.tvTimeLen = (TextView) convertView
					.findViewById(valueViewID[3]);
			// ===end add by mai 2015-1-26===
 
			HashMap<String, Object> map = mAppList.get(position);
			if (map != null) {
				// ===add by mai 2015-1-26=====================
				String startTime = (String) map.get("FileStartTime");
				String strSize = (String) map.get("FileSize");
				String strTimeLen = (String) map.get("FileTimeLen");

				String info = (String) map.get("FileName");

				holder.tvName.setText(startTime);
				holder.tvTimeLen.setText(strTimeLen);
				holder.tvSize.setText(strSize);
				holder.tvInfo.setText(info);

				// ===end by mai 2015-1-26=====================

			}
			return convertView;
		}

	}

	/*
	 if(fileList.size() > 0 && position < fileList.size()){
				RecordFileInfo fileInfo = fileList.get(position);

				HashMap<String, Object> map = new HashMap<String, Object>();

				map.put("ItemTitleName", R.id.ItemFileName);
				map.put("ItemTitleInfo", R.id.ItemFileInfo);

				map.put("FileName", fileInfo.getStrFileName());

				int nFileSize = fileInfo.getnFileSize();
				double fFileSize = 0.0;
				double nTimeLen = 0;
				
				String strSize = getString(R.string.strFileSize);
				if (nFileSize > 1024000) {
					fFileSize = nFileSize / 1048576.0;
					if (fFileSize >= 100) {
						strSize = strSize + String.format("%.0f", fFileSize)
								+ " MB";
					} else if (fFileSize >= 1) {
						strSize = strSize + String.format("%.1f", fFileSize)
								+ " MB";
					} else {
						strSize = strSize + String.format("%.2f", fFileSize)
								+ " MB";
					}

				} else if (nFileSize > 1024) {
					fFileSize = nFileSize / 1024.0;
					strSize = strSize + String.format("%.0f", fFileSize)
							+ " KB";
				} else {
					strSize = strSize + nFileSize + " B";
				}

				String strStartTime = getString(R.string.strStartTime);

				if (fileInfo.getuStartHour() >= 10) {
					strStartTime = strStartTime + fileInfo.getuStartHour();
				} else {
					strStartTime = strStartTime + "0"
							+ fileInfo.getuStartHour();
				}

				if (fileInfo.getuStartMin() >= 10) {
					strStartTime = strStartTime + ":" + fileInfo.getuStartMin();
				} else {
					strStartTime = strStartTime + ":0"
							+ fileInfo.getuStartMin();
				}

				if (fileInfo.getuStartSec() >= 10) {
					strStartTime = strStartTime + ":" + fileInfo.getuStartSec();
				} else {
					strStartTime = strStartTime + ":0"
							+ fileInfo.getuStartSec();
				}

				String strTimeLen = getString(R.string.strTimeLen);
				if (fileInfo.getuFileTimeLen() >= 3600) {
					nTimeLen = fileInfo.getuFileTimeLen() / 3600.0;
					strTimeLen = strTimeLen + String.format("%.1f", nTimeLen)
							+ getString(R.string.strHour);
				} else if (fileInfo.getuFileTimeLen() >= 60) {
					nTimeLen = fileInfo.getuFileTimeLen() / 60.0;
					strTimeLen = strTimeLen + String.format("%.1f", nTimeLen)
							+ getString(R.string.strMin);
					// //@@System.out.println("MIN : "+String.format("%.0f",
					// nTimeLen)
					// +" , "+nTimeLen+", "+fileInfo.getuFileTimeLen());//add
					// for test
				} else {
					nTimeLen = fileInfo.getuFileTimeLen();
					strTimeLen = strTimeLen + String.format("%.0f", nTimeLen)
							+ getString(R.string.strSec);
				}

				String strInfo = strStartTime + " " + strTimeLen + " " + strSize;
				holder.tvName.setText(strStartTime);
				holder.tvTimeLen.setText(strTimeLen);
				holder.tvSize.setText(strSize);
				holder.tvInfo.setText(strInfo);
			}
	 */
	// 录像文件 搜索线程
	public class RecFileSearcher extends Thread {
		 
		private int m_nSearchID = 0; 
		private DeviceInfo deviceInfo=null;
 
		public RecFileSearcher(int nSearchID, DeviceInfo info) {
			 
			this.m_nSearchID = nSearchID;
			this.deviceInfo = info;
		}

	 
		public void run() {
		 
			//start
			Message msg = handler.obtainMessage();
			msg.arg1 = HANDLE_MSG_CODE_SEARCH;
			msg.arg2 = SEARCH_RESULT_START;
			handler.sendMessage(msg);
			
			
			 
			_deviceParam = RecordFileHelper.getRecordOPHandle(deviceTest);
			
			if(_deviceParam!=null && _deviceParam.getnResult()==ResultCode.RESULT_CODE_SUCCESS){
			
				int nResult = RecordFileHelper.getRecordFiles(_deviceParam, handler, nSearchChn, m_nSearchID, nYear, nMonth, nDay, nStartHour, nStartMin, nStartSec, nEndHour, nEndMin, nEndSec); 
				System.out.println("**************FU");//add for test
			}else{
				if(_deviceParam!=null){
					msg = handler.obtainMessage();
					msg.arg1 = HANDLE_MSG_CODE_SEARCH_RESULT;
					msg.arg2 = _deviceParam.getnResult();
					handler.sendMessage(msg);
				}else{
					msg = handler.obtainMessage();
					msg.arg1 = HANDLE_MSG_CODE_SEARCH_RESULT;
					msg.arg2 = ResultCode.RESULT_CODE_FAIL_COMMUNICAT_FAIL;
					handler.sendMessage(msg);
				}
				
				return;
			
			}
			
			//end
			msg = handler.obtainMessage();
			msg.arg1 = HANDLE_MSG_CODE_SEARCH;
			msg.arg2 = SEARCH_RESULT_ENDT;
			 
			handler.sendMessage(msg);
 
	}

	}

	public void GetRecFileListFromDatabase() {
//		//@@System.out.println("GetRecFileListFromDatabase");//
//		fileList.clear();
//		RecordFileInfo list[] = DatabaseManager.GetAllRecInfo();
//		if (list != null && list.length > 0) {
//			for (int i = 0; i < list.length; i++) {
//				RecordFileInfo info = list[i];
//				if (info != null) {
//					fileList.add(info);
//				}
//
//			}
//
//		}
//
//		refleshRecFileList();
	}

	public void SaveRecFileListToDatabase() {
		 

//		DatabaseManager.ClearRecInfos();
//
//		if (fileList != null && fileList.size() > 0) {
//			DatabaseManager.SaveRecInfos(fileList);
//		}
	}

//	public boolean isLoadFromDatabase() {
//		if (fileList != null && fileList.size() > 0) {
//			isLoadFromDatabase = true;
//		} else {
//			isLoadFromDatabase = false;
//		}
//		return isLoadFromDatabase;
//	}
//
//	public void setLoadFromDatabase(boolean isLoadFromDatabase) {
//		this.isLoadFromDatabase = isLoadFromDatabase;
//	}

	public boolean isbIsRecFileSearching() {
		return bIsRecFileSearching;
	}

	public void setbIsRecFileSearching(boolean bIsRecFileSearching) {
		this.bIsRecFileSearching = bIsRecFileSearching;
	}

	public int getM_nLoginExID() {
		return m_nLoginExID;
	}

	public void setM_nLoginExID(int m_nLoginExID) {
		this.m_nLoginExID = m_nLoginExID;
	}

	public int getnSearchChn() {
		return nSearchChn;
	}

	public void setnSearchChn(int nSearchChn) {
		this.nSearchChn = nSearchChn;
	}

	public int getnSearchType() {
		return nSearchType;
	}

	public void setnSearchType(int nSearchType) {
		this.nSearchType = nSearchType;
	}
 

	// 显示录像文件列表
	public void GetRecFileList() {
		
			if (deviceTest != null) {
				
				m_recFileSearchID++;
				bIsRecFileSearching = true;

				fileList.clear();
				recFileListView.setAdapter(null);
 

				searchingProgressBar.setVisibility(View.VISIBLE);
 
				new RecFileSearcher(m_recFileSearchID, deviceTest).start();
			}
	 

	}

	// 重新继承getview，重写ListView Adapter,以处理listview中按钮的点击事件
	private class DeviceListViewAdapter extends BaseAdapter {

		private class ItemViewHolder {
			// ImageView btnFace;
			TextView tvName;
			ImageView ivDeviceSelect;
			// TextView tvInfo;

		}

		private ArrayList<HashMap<String, Object>> mAppList;
		private LayoutInflater mInflater;
		private Context mContext;
		private String[] keyString;
		private int[] valueViewID;
		private ItemViewHolder holder;

		public DeviceListViewAdapter(Context c,
				ArrayList<HashMap<String, Object>> appList, int resource,
				String[] from, int[] to) {
			mAppList = appList;
			mContext = c;
			mInflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			keyString = new String[from.length];
			valueViewID = new int[to.length];
			System.arraycopy(from, 0, keyString, 0, from.length);
			System.arraycopy(to, 0, valueViewID, 0, to.length);
		}

		@Override
		public int getCount() {
			return mAppList.size();
		}

		@Override
		public Object getItem(int position) {
			return mAppList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if (convertView != null) {
				holder = (ItemViewHolder) convertView.getTag();
			} else {
				convertView = mInflater.inflate(
						R.layout.player_back_device_select_item, null);
				holder = new ItemViewHolder();
				holder.tvName = (TextView) convertView
						.findViewById(valueViewID[0]);
				holder.ivDeviceSelect = (ImageView) convertView
						.findViewById(valueViewID[1]);
	
				convertView.setTag(holder);
			}

			HashMap<String, Object> map = mAppList.get(position);
			if (map != null) {
				
				if(position == optOf)
				{
					holder.tvName.setTextColor(Color.BLUE);
					holder.ivDeviceSelect.setImageResource(R.drawable.play_back_choose_2);
				}else{
					holder.tvName.setTextColor(Color.BLACK);
					holder.ivDeviceSelect.setImageResource(R.drawable.play_back_choose_1);
				}
				
				String name = (String) map.get("ItemTitleName");
				holder.tvName.setText(name);

			}
			return convertView;
		}

	}

	 
	private void StartPlayFile(int nIndex) {
		 
		System.out.println("StartPlayFile: "+nIndex);//add for test
		Intent intent = new Intent(this,
				DemoPlaybackActivity.class);
		Bundle data = new Bundle(); 
 
	 
		data.putParcelable("login_handle", _deviceParam);
		data.putInt("play_index", nIndex);

		intent.putExtras(data);
		startActivity(intent);
		DemoSearchRecFileActivity.this.finish(); 
		this.overridePendingTransition(R.anim.zoomin,
				R.anim.zoomout);
 
			
 
		 
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int nSelectIndex,
			long arg3) {
		// TODO Auto-generated method stub
 
		 if (arg0.getId() == R.id.recfile_list) {
			if (nSelectIndex >= 0 && nSelectIndex < fileList.size()) {
//				SaveRecFileListToDatabase();
 
//				TempDefines.listMapPlayerBackFile.clear();
//				TempDefines.listMapPlayerBackFile.addAll(fileList);
				 
				TempDefines.listMapPlayerBackFile = fileList;
				
				StartPlayFile(nSelectIndex);
 				
			}
		}

	}

	@Override
	public void onReceiveFile(int arg0, int arg1, ArrayList<RecordFileInfo> arg2) {
		// TODO Auto-generated method stub
		
	}

	 
}
