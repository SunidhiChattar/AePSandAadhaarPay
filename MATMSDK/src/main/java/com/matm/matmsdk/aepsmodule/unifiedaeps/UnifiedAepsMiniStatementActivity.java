package com.matm.matmsdk.aepsmodule.unifiedaeps;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.matm.matmsdk.Utils.SdkConstants;
import com.matm.matmsdk.aepsmodule.AEPS2HomeActivity;
import com.matm.matmsdk.aepsmodule.utils.GetPosConnectedPrinter;
import com.matm.matmsdk.aepsmodule.utils.Session;
import com.matm.matmsdk.vriddhi.AEMPrinter;
import com.matm.matmsdk.vriddhi.AEMScrybeDevice;
import com.matm.matmsdk.vriddhi.CardReader;
import com.matm.matmsdk.vriddhi.IAemCardScanner;
import com.matm.matmsdk.vriddhi.IAemScrybe;
import java.io.IOException;
import java.util.ArrayList;

import isumatm.androidsdk.equitas.R;
import wangpos.sdk4.libbasebinder.Printer;


public class UnifiedAepsMiniStatementActivity extends AppCompatActivity implements IAemScrybe, IAemCardScanner {
    public static final String TAG = UnifiedAepsMiniStatementActivity.class.getSimpleName();
    RelativeLayout failureLayout;
    LinearLayout successLayout;
    Button okButton, okSuccessButton, printBtnSuccess;
    TextView statusDescTxt, failtxnID, aadhar_number, date_time, bank_name, card_transaction_type;
    TextView transaction_details_header_txt, aadhar_num_txt, account_balance_txt, transaction_id_txt, bank_name_txt;
    Session session;
    ProgressDialog pd;
    RecyclerView statement_list;
    String statusTxt;
    private UnifiedTxnStatusModel transactionStatusModel;
    UnifiedStatementListAdapter statementList_adapter;
    BluetoothAdapter bluetoothAdapter;
    ArrayList<String> printerList;
    AEMScrybeDevice m_AemScrybeDevice;
    AEMPrinter m_AemPrinter = null;
    String creditData, tempdata, replacedData;
    String printerName;
    CardReader m_cardReader = null;
    CardReader.CARD_TRACK cardTrackType;
    String responseString, response;
    String[] responseArray = new String[1];
    private wangpos.sdk4.libbasebinder.Printer mPrinter;
    private ArrayList<UnifiedTxnStatusModel.MiniStatement> miniStatementsList;
    private TransactionType transactionType;
    private String txnType;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SdkConstants.statementLayout == 0) {
            setContentView(R.layout.activity_statement_transaction);
        } else {
            setContentView(SdkConstants.statementLayout);
        }

        session = new Session(UnifiedAepsMiniStatementActivity.this);
        pd = new ProgressDialog(UnifiedAepsMiniStatementActivity.this);
        statement_list = findViewById(R.id.statement_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(UnifiedAepsMiniStatementActivity.this);
        statement_list.setLayoutManager(linearLayoutManager);
        statusDescTxt = findViewById(R.id.statusDescTxt);
        transaction_details_header_txt = findViewById(R.id.transaction_details_header_txt);
        failtxnID = findViewById(R.id.txnID);
        aadhar_number = findViewById(R.id.midNo);
        date_time = findViewById(R.id.date_time);
        bank_name = findViewById(R.id.bank_name);
        card_transaction_type = findViewById(R.id.terminalId);
        successLayout = findViewById(R.id.successLayout);
        failureLayout = findViewById(R.id.failureLayout);
        okButton = findViewById(R.id.okButton);
        okSuccessButton = findViewById(R.id.okSuccessButton);
        printBtnSuccess = findViewById(R.id.success_print_button);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        m_AemScrybeDevice = new AEMScrybeDevice(UnifiedAepsMiniStatementActivity.this);
        aadhar_num_txt = findViewById(R.id.aadhar_num_txt);
        account_balance_txt = findViewById(R.id.account_balance_txt);
        transaction_id_txt = findViewById(R.id.transaction_id_txt);
        bank_name_txt = findViewById(R.id.bank_name_txt);
        transactionStatusModel = getIntent().getParcelableExtra(SdkConstants.TRANSACTION_STATUS_KEY);
        failureLayout.setVisibility(View.GONE);
        successLayout.setVisibility(View.VISIBLE);
        aadhar_num_txt.setText(transactionStatusModel.getAadharCard());
        transaction_id_txt.setText(transactionStatusModel.getTxnID());
        bank_name_txt.setText(transactionStatusModel.getBankName());
        account_balance_txt.setText("Rs." + transactionStatusModel.getBalanceAmount());
        miniStatementsList = transactionStatusModel.getMinistatement();

        if(miniStatementsList == null || miniStatementsList.size() == 0  ){
            printBtnSuccess.setVisibility(View.INVISIBLE);
        }
        try {
            pd.show();
            pd.setMessage("Loading..");
            statementList_adapter = new UnifiedStatementListAdapter(UnifiedAepsMiniStatementActivity.this, transactionStatusModel.getMinistatement(), pd);
            statement_list.setAdapter(statementList_adapter);
            pd.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        okSuccessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent respIntent = new Intent();
                setResult(Activity.RESULT_OK, respIntent);
                finish();
            }
        });
        printBtnSuccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(miniStatementsList == null){
                    Toast.makeText(UnifiedAepsMiniStatementActivity.this, "No Data Found", Toast.LENGTH_SHORT).show();

                }else{
                    String deviceModel = android.os.Build.MODEL;
                    if (deviceModel.equalsIgnoreCase("A910")) {
//                    getPrintData(txnID.getText().toString(), aadharCard,date_time.getText().toString(),bank_name.getText().toString(),referenceNo, transactionType );
                    } else if (deviceModel.equalsIgnoreCase("WPOS-3")) {
                        //start printing with wiseasy internal printer
                        new UnifiedAepsMiniStatementActivity.UnifiedPrintReceiptThread().start();
                    } else {
                        registerForContextMenu(printBtnSuccess);
                        if (bluetoothAdapter == null) {
                            Toast.makeText(UnifiedAepsMiniStatementActivity.this, "Bluetooth NOT supported", Toast.LENGTH_SHORT).show();
                        } else {
                            if (bluetoothAdapter.isEnabled()) {
                                if (GetPosConnectedPrinter.aemPrinter == null) {
                                    printerList = m_AemScrybeDevice.getPairedPrinters();
                                    if (printerList.size() > 0) {
                                        openContextMenu(v);
                                    } else {
                                        showAlert("No Paired Printers found");
                                    }
                                } else {
                                    m_AemPrinter = GetPosConnectedPrinter.aemPrinter;
                                    callBluetoothFunction(transaction_id_txt.getText().toString(), aadhar_num_txt.getText().toString(), account_balance_txt.getText().toString(), v);
                                }
                            } else {
                                GetPosConnectedPrinter.aemPrinter = null;
                                Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                startActivityForResult(turnOn, 0);
                            }
                        }
                    }
                }

            }
        });
    }
    //    WPOS-3 PRINT
    private class UnifiedPrintReceiptThread  extends Thread{
        @Override
        public void run() {
            mPrinter=new Printer(UnifiedAepsMiniStatementActivity.this);
            try {
                mPrinter.setPrintType(0);//Printer type 0 means it's an internal printer
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            checkPrinterStatus();
        }
    }
    private void checkPrinterStatus() {
        try {
            int[] status = new int[1];
            mPrinter.getPrinterStatus(status);
            Log.e(TAG,"Printer Status is "+status[0]);
            String msg="";
            switch (status[0]){
                case 0x00:
                    msg="Printer status OK";
                    Log.e(TAG, "check printer status: "+msg );
                    startPrinting();
                    break;
                case 0x01:
                    msg="Parameter error";
                    showLog(msg);
                    break;
                case 0x8A://----138 return
                    msg="Out of Paper";
                    showLog(msg);
                    break;
                case 0x8B:
                    msg="Overheat";
                    showLog(msg);
                    break;
                default:
                    msg="Printer Error";
                    showLog(msg);
                    break;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    private void showLog(String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(UnifiedAepsMiniStatementActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
        Log.e(TAG, "Printer status: "+msg );
    }
    /**
     * First initialize the printer and clear if any catch data is present
     *
     * After initialization of printer then start printing
     * */
    private void startPrinting() {
        int result = -1;
        try {
            result = mPrinter.printInit();
            Log.e(TAG, "startPrinting: Printer init result "+result );
            mPrinter.clearPrintDataCache();
            if (result==0){
                printReceipt();
            }else {
                Toast.makeText(this, "Printer initialization failed", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void printReceipt() {
        int result=-1;
        try {
            Log.e(TAG, "printReceipt: set density low 3" );
            mPrinter.setGrayLevel(3);
            result = mPrinter.printStringExt(SdkConstants.SHOP_NAME, 0,0f,2.0f, Printer.Font.SANS_SERIF, 25, Printer.Align.CENTER,true,false,true);
            result = mPrinter.printString("Success", 25, Printer.Align.CENTER, true, false);
            result = mPrinter.printString("Transaction Id :"+ transaction_id_txt.getText(),20, Printer.Align.LEFT,false,false);
            result = mPrinter.printString("Balance Amount :"+ account_balance_txt.getText(),20, Printer.Align.LEFT,false,false);
            result = mPrinter.printString("Aadhaar Number :"+ aadhar_num_txt.getText(),20, Printer.Align.LEFT,false,false);
            result = mPrinter.printString("STATEMENT",23, Printer.Align.CENTER,true,false);
            if(miniStatementsList.size() == 0){
            }else {
                for (int i = 0; i< miniStatementsList.size(); i++){
                    UnifiedTxnStatusModel.MiniStatement mSList = miniStatementsList.get(i);
                    txnType=mSList.getDebitCredit();
                    transactionType=new TransactionType(DebitCredit.valueOf(txnType));
                    result = mPrinter.printString(mSList.getDate() + "                              "+transactionType.transactionType()+mSList.getAmount(), 20, Printer.Align.LEFT, false,false);
                    result = mPrinter.printString(mSList.getType(), 20, Printer.Align.LEFT, false, false);

                }
            }
            result = mPrinter.printStringExt("Thank You", 0,0f,2.0f, Printer.Font.SANS_SERIF, 20, Printer.Align.RIGHT,true,true,false);
            result = mPrinter.printStringExt(SdkConstants.BRAND_NAME, 0,0f,2.0f, Printer.Font.SANS_SERIF, 20, Printer.Align.RIGHT,true,true,false);
            result = mPrinter.printString(" ",25, Printer.Align.CENTER,false,false);
            result = mPrinter.printString(" ",25, Printer.Align.CENTER,false,false);
            Log.e(TAG, "printReceipt: print thank you result "+result );
            result = mPrinter.printPaper(30);
            Log.e(TAG, "printReceipt: print step result "+result );
            showPrinterStatus(result);
            result = mPrinter.printFinish();
            Log.e(TAG, "printReceipt: printer finish result "+result );
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void showPrinterStatus(int result) {
        String msg="";
        switch (result){
            case 0x00:
                msg="Print Finish";
                showLog(msg);
                break;
            case 0x01:
                msg="Parameter error";
                showLog(msg);
                break;
            case 0x8A://----138 return
                msg="Out of Paper";
                showLog(msg);
                break;
            case 0x8B:
                msg="Overheat";
                showLog(msg);
                break;
            default:
                msg="Printer Error";
                showLog(msg);
                break;
        }
    }
    public String getRemarks(ArrayList<String> tempRemark) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tempRemark.size(); i++) {
            if (tempRemark.get(i).toString().length() != 0) {
                sb.append(" " + tempRemark.get(i));
            }
        }
        return sb.toString().trim();
    }
    @Override
    public void onBackPressed() {
        try {
            if (statusTxt.equalsIgnoreCase("FAILED")) {
                Intent intent = new Intent(UnifiedAepsMiniStatementActivity.this, AEPS2HomeActivity.class);
                intent.putExtra("FAILEDVALUE", "FAILEDDATA");
                startActivity(intent);
                finish();
            } else {
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void onDiscoveryComplete(ArrayList<String> aemPrinterList) {
        printerList = aemPrinterList;
        for (int i = 0; i < aemPrinterList.size(); i++) {
            String Device_Name = aemPrinterList.get(i);
            String status = m_AemScrybeDevice.pairPrinter(Device_Name);
            Log.e("STATUS", status);
        }
    }
    public void showAlert(String alertMsg) {
        android.app.AlertDialog.Builder alertBox = new android.app.AlertDialog.Builder(UnifiedAepsMiniStatementActivity.this);
        alertBox.setMessage(alertMsg).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                return;
            }
        });
        android.app.AlertDialog alert = alertBox.create();
        alert.show();
    }
    private void callBluetoothFunction(final String txnId, final String aadharNo, String accountBlance, View view) {
        try {
            m_AemPrinter.POS_FontThreeInchCENTER();
            m_AemPrinter.POS__FontThreeInchDOUBLEHIEGHT();
            m_AemPrinter.print(SdkConstants.SHOP_NAME.trim());
            m_AemPrinter.print("\n");
            m_AemPrinter.POS_FontThreeInchCENTER();
            m_AemPrinter.POS__FontThreeInchDOUBLEHIEGHT();
            m_AemPrinter.print("SUCCESS");
            m_AemPrinter.print("\n");
            m_AemPrinter.print("\n");
            m_AemPrinter.print("Txn id: " + txnId);
            m_AemPrinter.print("\n");
            m_AemPrinter.print("Available Balance: ");
            m_AemPrinter.print(accountBlance);
            m_AemPrinter.print("\n");
            m_AemPrinter.print("Aadhar No: " + aadharNo);
            m_AemPrinter.print("\n");
            m_AemPrinter.print("\n");
            m_AemPrinter.POS_FontThreeInchCENTER();
            m_AemPrinter.print("Statement");
            m_AemPrinter.print("\n");
            m_AemPrinter.print("\n");
            if (miniStatementsList == null || miniStatementsList.size() == 0) {
            } else {
                for (int i = 0; i < miniStatementsList.size(); i++) {
                    UnifiedTxnStatusModel.MiniStatement mSList = miniStatementsList.get(i);
                    txnType = mSList.getDebitCredit();
                    transactionType = new TransactionType(DebitCredit.valueOf(txnType));
                    if (mSList.getDate() != null || mSList.getType() != null) {
                        if (mSList.getDate() == null || mSList.getType() == null) {
                            m_AemPrinter.print(" " + "             " + transactionType.transactionType() + mSList.getAmount() + "\n" + " ");

                        } else
                            m_AemPrinter.print(mSList.getDate().trim() + "             " + transactionType.transactionType() + mSList.getAmount() + "\n" + mSList.getType().trim());
                        m_AemPrinter.print("\n");
                    }
                }
            }
            m_AemPrinter.print("Thank You");
            m_AemPrinter.POS_FontThreeInchRIGHT();
            m_AemPrinter.print("\n");
            m_AemPrinter.print(SdkConstants.BRAND_NAME.trim());
            m_AemPrinter.POS_FontThreeInchRIGHT();
            m_AemPrinter.print("\n");
            m_AemPrinter.print("\n");
            m_AemPrinter.print("\n");
            m_AemPrinter.print("\n");
            m_AemPrinter.POS_FontThreeInchCENTER();
            m_AemPrinter.print(" ");
            m_AemPrinter.print(" ");
            m_AemPrinter.print("\n");
        } catch (IOException e) {
//            e.printStackTrace();
            try {
                GetPosConnectedPrinter.aemPrinter = null;
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Select Printer to connect");
        for (int i = 0; i < printerList.size(); i++) {
            menu.add(0, v.getId(), 0, printerList.get(i));
        }
    }
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        super.onContextItemSelected(item);
        printerName = item.getTitle().toString();
        try {
            m_AemScrybeDevice.connectToPrinter(printerName);
            m_cardReader = m_AemScrybeDevice.getCardReader(this);
            m_AemPrinter = m_AemScrybeDevice.getAemPrinter();
            GetPosConnectedPrinter.aemPrinter = m_AemPrinter;
            Toast.makeText(UnifiedAepsMiniStatementActivity.this, "Connected with " + printerName, Toast.LENGTH_SHORT).show();
            //            String data=new String(batteryStatusCommand);
//            m_AemPrinter.print(data);
            //  m_cardReader.readMSR();
        } catch (IOException e) {
            if (e.getMessage().contains("Service discovery failed")) {
                Toast.makeText(UnifiedAepsMiniStatementActivity.this, "Not Connected\n" + printerName + " is unreachable or off otherwise it is connected with other device", Toast.LENGTH_SHORT).show();
            } else if (e.getMessage().contains("Device or resource busy")) {
                Toast.makeText(UnifiedAepsMiniStatementActivity.this, "the device is already connected", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(UnifiedAepsMiniStatementActivity.this, "Unable to connect", Toast.LENGTH_SHORT).show();
            }
        }
        return true;
    }
    @Override
    public void onScanMSR(final String buffer, CardReader.CARD_TRACK cardTrack) {
        cardTrackType = cardTrack;
        creditData = buffer;
        this.runOnUiThread(new Runnable() {
            public void run() {
//                editText.setText(buffer.toString());
            }
        });
    }
    @Override
    public void onScanDLCard(final String buffer) {
        CardReader.DLCardData dlCardData = m_cardReader.decodeDLData(buffer);
        String name = "NAME:" + dlCardData.NAME + "\n";
        String SWD = "SWD Of: " + dlCardData.SWD_OF + "\n";
        String dob = "DOB: " + dlCardData.DOB + "\n";
        String dlNum = "DLNUM: " + dlCardData.DL_NUM + "\n";
        String issAuth = "ISS AUTH: " + dlCardData.ISS_AUTH + "\n";
        String doi = "DOI: " + dlCardData.DOI + "\n";
        String tp = "VALID TP: " + dlCardData.VALID_TP + "\n";
        String ntp = "VALID NTP: " + dlCardData.VALID_NTP + "\n";
        final String data = name + SWD + dob + dlNum + issAuth + doi + tp + ntp;
        runOnUiThread(new Runnable() {
            public void run() {
//                editText.setText(data);
            }
        });
    }
    @Override
    public void onScanRCCard(final String buffer) {
        CardReader.RCCardData rcCardData = m_cardReader.decodeRCData(buffer);
        String regNum = "REG NUM: " + rcCardData.REG_NUM + "\n";
        String regName = "REG NAME: " + rcCardData.REG_NAME + "\n";
        String regUpto = "REG UPTO: " + rcCardData.REG_UPTO + "\n";
        final String data = regNum + regName + regUpto;
        runOnUiThread(new Runnable() {
            public void run() {
//                editText.setText(data);
            }
        });
    }
    @Override
    public void onScanRFD(final String buffer) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(buffer);
        String temp = "";
        try {
            temp = stringBuffer.deleteCharAt(8).toString();
        } catch (Exception e) {
            // TODO: handle exception
        }
        final String data = temp;
        this.runOnUiThread(new Runnable() {
            public void run() {
                //rfText.setText("RF ID:   " + data);
//                editText.setText("ID " + data);
                try {
                    m_AemPrinter.print(data);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    @Override
    public void onScanPacket(String buffer) {
        if (buffer.equals("PRINTEROK")) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(buffer);
            String temp = "";
            try {
                temp = stringBuffer.toString();
            } catch (Exception e) {
                // TODO: handle exception
            }
            tempdata = temp;
            final String strData = tempdata.replace("|", "&");
            //Log.e("BufferData",data);
            final String[][] formattedData = {strData.split("&", 3)};
            // Log.e("Response Data",formattedData[2]);
            responseString = formattedData[0][2];
            responseArray[0] = responseString.replace("^", "");
            Log.e("Response Array", responseArray[0]);
            this.runOnUiThread(new Runnable() {
                public void run() {
                    replacedData = tempdata.replace("|", "&");
                    formattedData[0] = replacedData.split("&", 3);
                    response = formattedData[0][2];
                    if (response.contains("BAT")) {
//                        txtBatteryStatus.setText(response.replace("^","").replace("BAT","")+"%");
                    }
//                    editText.setText(response.replace("^",""));
                }
            });
        } else {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(buffer);
            String temp = "";
            try {
                temp = stringBuffer.toString();
            } catch (Exception e) {
                // TODO: handle exception
            }
            tempdata = temp;
            final String strData = tempdata.replace("|", "&");
            //Log.e("BufferData",data);
            final String[][] formattedData = {strData.split("&", 3)};
            // Log.e("Response Data",formattedData[2]);
            responseString = formattedData[0][2];
            responseArray[0] = responseString.replace("^", "");
            Log.e("Response Array", responseArray[0]);
            this.runOnUiThread(new Runnable() {
                public void run() {
                    replacedData = tempdata.replace("|", "&");
                    formattedData[0] = replacedData.split("&", 3);
                    response = formattedData[0][2];
                    if (response.contains("BAT")) {
//                        txtBatteryStatus.setText(response.replace("^","").replace("BAT","")+"%");
                    }
//                    editText.setText(response.replace("^",""));
                }
            });
        }
    }
}