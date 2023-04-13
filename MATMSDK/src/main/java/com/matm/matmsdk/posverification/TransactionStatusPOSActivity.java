package com.matm.matmsdk.posverification;


import static com.matm.matmsdk.permission.PermissionsActivity.PERMISSION_REQUEST_CODE;
import static com.matm.matmsdk.permission.PermissionsChecker.REQUIRED_PERMISSION;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;
import com.matm.matmsdk.FileUtils;
import com.matm.matmsdk.Service.BankResponse;
import com.matm.matmsdk.Utils.PAXScreen;
import com.matm.matmsdk.Utils.SdkConstants;
import com.matm.matmsdk.aepsmodule.unifiedaeps.UnifiedAepsTransactionActivity;
import com.matm.matmsdk.aepsmodule.unifiedaeps.UnifiedTxnStatusModel;
import com.matm.matmsdk.aepsmodule.utils.GetPosConnectedPrinter;
import com.matm.matmsdk.permission.PermissionsActivity;
import com.matm.matmsdk.permission.PermissionsChecker;
import com.matm.matmsdk.readfile.PreviewPDFActivity;
import com.matm.matmsdk.transaction_report.TransactionStatusActivity;
import com.matm.matmsdk.transaction_report.print.GetConnectToPrinter;
import com.matm.matmsdk.vriddhi.AEMPrinter;
import com.matm.matmsdk.vriddhi.AEMScrybeDevice;
import com.matm.matmsdk.vriddhi.CardReader;
import com.matm.matmsdk.vriddhi.IAemCardScanner;
import com.matm.matmsdk.vriddhi.IAemScrybe;
import com.pax.dal.entity.EFontTypeAscii;
import com.pax.dal.entity.EFontTypeExtCode;
import com.paxsz.easylink.api.EasyLinkSdkManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import isumatm.androidsdk.equitas.R;
import wangpos.sdk4.libbasebinder.Printer;

public class TransactionStatusPOSActivity extends AppCompatActivity implements IAemCardScanner, IAemScrybe {

    private static final String TAG = TransactionStatusActivity.class.getSimpleName();
    public Boolean isMerchant;
    public EasyLinkSdkManager manager;
    ImageView status_icon, card_Type;
    ImageButton backBtn;
    TextView merchant_Text, customer_Text;
    LinearLayout bkgLayout;
    //    private TextView rref_num;
    TextView balanceText, card_amount, accountholdername, time_TV, date_TV, accountnumber, transactionamount, date_time, txnID;
    Button txndetails;
    Button downloadReceiptBtn, printReceiptBtn, downloadReceiptBtnMerchant, printReceiptBtnMerchant;
    Button closeBtn;
    ProgressDialog progressDialog;
    BluetoothDevice bluetoothDevice;
    Context mContext;
    String printerName;
    BluetoothAdapter bluetoothAdapter;
    String statusTxt;
    String transaction_type;
    String mobile;
    String prefNum = "NA", MID = "NA", TID = "NA", CARD_TYPE = "NA", Card_NUM = "NA", balanceAmt = "NA", transactionAmt = "NA", TRANSACTION_ID = "NA", TXN_ID = "NA";
    String transactionTypeCheck;
    PermissionsChecker checker;
    ArrayList<String> printerList;
    //    for splitstring
    String midValue = " ";
    String tidValue = " ";
    String invNoValue = " ";
    String batchValue = " ";
    String authValue = " ";
    String rrnValue = " ";
    String cardTypeValue = " ";
    AEMScrybeDevice m_AemScrybeDevice;
    AEMPrinter m_AemPrinter = null;
    String creditData, tempdata, replacedData, data;
    char[] printerStatus = new char[]{0x1B, 0x7E, 0x42, 0x50, 0x7C, 0x47, 0x45, 0x54, 0x7C, 0x50, 0x52, 0x4E, 0x5F, 0x53, 0x54, 0x5E};
    CardReader.CARD_TRACK cardTrackType;
    CardReader m_cardReader = null;
    String responseString, response;
    String[] responseArray = new String[1];
    String currentDate, currentTime;
    private int STORAGE_PERMISSION_CODE = 1;
    private String bankName = "";
    private String merchantName = "";
    private String location = "";
    private String date = "";
    private String midTid = "";
    private String batch = "";
    private String sale = "";
    private String card = "";
    private String cardType = "";
    private String rrn = "";
    private String cardHolderName = "";
    private String pin = "";
    private String RESPONSE_CODE = "";
    private String applicationVersion = "";
    private String applicationCryptogram = "";
    private String txnStatus = "";
    private String dedicatedFileName = "";
    private String terminalVerResult = "";
    private String applPreferredName = "";
    private String statusCode = "";
    private wangpos.sdk4.libbasebinder.Printer mPrinter;
    private String currentDateTime;
    //void flag
    private boolean isVoid = false;
    private View view;
//    private String authValue = "";

    public static String[] splitString(String data, String splitText) {
        return data.split(splitText);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_pos);
        manager = EasyLinkSdkManager.getInstance(this);
        mContext = getApplicationContext();

        checker = new PermissionsChecker(this);
        m_AemScrybeDevice = new AEMScrybeDevice(TransactionStatusPOSActivity.this);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        status_icon = findViewById(R.id.status_icon);
        merchant_Text = findViewById(R.id.merchant_Text);
        customer_Text = findViewById(R.id.customer_Text);
        balanceText = findViewById(R.id.balanceText);
        card_amount = findViewById(R.id.card_amount);
        bkgLayout = findViewById(R.id.bkgLayout);
        accountnumber = findViewById(R.id.accountnumber);
        accountholdername = findViewById(R.id.accountholdername);
        transactionamount = findViewById(R.id.transactionamount);
        date_time = findViewById(R.id.date_time);
        card_Type = findViewById(R.id.card_type);
        date_TV = findViewById(R.id.date);
        time_TV = findViewById(R.id.time);
        txnID = findViewById(R.id.txnID);
        closeBtn = findViewById(R.id.closeBtn);
        backBtn = findViewById(R.id.backBtn);
        downloadReceiptBtn = findViewById(R.id.btnDownload);
        downloadReceiptBtnMerchant = findViewById(R.id.btnDownloadMerchant);
        printReceiptBtn = findViewById(R.id.btnPrint);
        printReceiptBtnMerchant = findViewById(R.id.btnPrintMerchant);
//        rref_num = findViewById(R.id.rref_num);
        printerList = new ArrayList<String>();

        Date date = Calendar.getInstance().getTime();
        // Display a date in day, month, year format
        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        currentDate = formatter.format(date);
        DateFormat formatter1 = new SimpleDateFormat("HH:mm:ss");
        currentTime = formatter1.format(date);
        date_time.setText("Date : " + currentDate + " | " + currentTime);
        date_TV.setText(currentDate);
        time_TV.setText(currentTime);


        transaction_type = getIntent().getStringExtra("TRANSACTION_TYPE");
        TRANSACTION_ID = getIntent().getStringExtra("TRANSACTION_ID");
        currentDateTime = formatter.format(date);

        String applName = getIntent().getStringExtra("APP_NAME");
        String aId = getIntent().getStringExtra("AID");
        prefNum = getIntent().getStringExtra("RRN_NO");
        MID = getIntent().getStringExtra("MID");
        TID = getIntent().getStringExtra("TID");
        TXN_ID = getIntent().getStringExtra("TXN_ID");
        String INVOICE = getIntent().getStringExtra("INVOICE");
        CARD_TYPE = getIntent().getStringExtra("CARD_TYPE");
        String APPR_CODE = getIntent().getStringExtra("APPR_CODE");
        Card_NUM = getIntent().getStringExtra("CARD_NUMBER");
        balanceAmt = getIntent().getStringExtra("AMOUNT");
        transactionAmt = getIntent().getStringExtra("TRANSACTION_AMOUNT");
//        balanceAmt = getDecimalString(balanceAmt);
        RESPONSE_CODE = getIntent().getStringExtra("RESPONSE_CODE");

        boolean isVoid = getIntent().getBooleanExtra("IsVoid", false);

        //new added for visa test cases
        bankName = getIntent().getStringExtra("bank_name");
        merchantName = getIntent().getStringExtra("merchant_name");
        location = getIntent().getStringExtra("location");
        midTid = getIntent().getStringExtra("mid_tid");
        batch = getIntent().getStringExtra("batch");
        sale = getIntent().getStringExtra("sale");
        card = getIntent().getStringExtra("card");
        cardType = getIntent().getStringExtra("card_type");
        rrn = getIntent().getStringExtra("rrn");
        cardHolderName = getIntent().getStringExtra("card_name");
        pin = getIntent().getStringExtra("pin");
        applicationVersion = getIntent().getStringExtra("application_version");
        applicationCryptogram = getIntent().getStringExtra("application_cryptogram");
        txnStatus = getIntent().getStringExtra("txn_status");
        dedicatedFileName = getIntent().getStringExtra("dedicated_file_name");
        terminalVerResult = getIntent().getStringExtra("terminal_result");
        applPreferredName = getIntent().getStringExtra("appl_preferred_name");
        statusCode = getIntent().getStringExtra("status_code");

            if (card != null) {
                accountholdername.setText(cardHolderName);
            } else {
                accountholdername.setText("N/A");
            }


        if (cardType == null) {
            card_Type.setVisibility(View.GONE);
        } else {
            if (cardType.contains("MASTER")) {
                card_Type.setImageDrawable(getResources().getDrawable(R.drawable.mastercard));
            } else if (cardType.contains("VISA")) {
                card_Type.setImageDrawable(getResources().getDrawable(R.drawable.visacard));
            } else if (cardType.contains("RUPAY")) {
                card_Type.setImageDrawable(getResources().getDrawable(R.drawable.rupaycard));
            }
        }

        if (balanceAmt != null) {
            if (balanceAmt.equalsIgnoreCase("0") || balanceAmt.equalsIgnoreCase("N/A") || balanceAmt.equalsIgnoreCase("NA")) {
                balanceAmt = "N/A";
            } else {
                balanceAmt = replaceWithZero(balanceAmt);
            }
        } else {
            balanceAmt = "NA";
        }
        if (!isVoid) {
            if (transactionAmt != null && transactionAmt.length() > 0) {
                if (transactionAmt.equalsIgnoreCase("0") || transactionAmt.equalsIgnoreCase("N/A") || transactionAmt.equalsIgnoreCase("NA") || transactionAmt.equalsIgnoreCase("null") || transactionAmt == null) {
                    transactionAmt = "N/A";
                } else {
                    transactionAmt = replaceWithZero(transactionAmt);
                }
            } else {
                transactionAmt = "NA";
            }
        } else {

            if (transactionAmt != null && transactionAmt.length() > 0) {
                if (transactionAmt.equalsIgnoreCase("0") || transactionAmt.equalsIgnoreCase("N/A") || transactionAmt.equalsIgnoreCase("NA") || transactionAmt.equalsIgnoreCase("null") || transactionAmt == null) {
                    transactionAmt = "N/A";
                } else {
                    transactionAmt = replaceWithZero(transactionAmt);
                }
            } else {
                transactionAmt = "NA";
            }
            card_amount.setText("₹" + balanceAmt);
            transactionamount.setText("₹" + transactionAmt);
        }


//        if (transactionAmt.equalsIgnoreCase("N/A")) {


        System.out.println(">>>----" + balanceAmt);

        if (Card_NUM != null && !Card_NUM.isEmpty()) {
            String[] splitAmount = Card_NUM.split("D");
            Card_NUM = splitAmount[0];

            String firstnum = Card_NUM.substring(0, 2);
            String middlenum = Card_NUM.substring(2, Card_NUM.length() - 2);
            String lastNum = Card_NUM.replace(firstnum + middlenum, "");

            System.out.println(">>>---" + firstnum);
            System.out.println(">>>---" + middlenum);
            System.out.println(">>>---" + lastNum);

        }
/*
        if (transaction_type.equalsIgnoreCase("cash")) {
            transactionTypeCheck = "Cash Withdrawal";
            card_amount.setText("Txn Amt : Rs. " + transactionAmt);

        }else if(isVoid){
            transactionTypeCheck = "VOID";
            card_amount.setText("Balance Amount: Rs. " + balanceAmt);
        }
        else if(transaction_type.equalsIgnoreCase("POS")){
            card_amount.setText("Rs. " + transactionAmt);
            transactionamount.setText("Rs. " + transactionAmt);


        }
        else {
            transactionTypeCheck = "Balance Enquiry";
            card_amount.setText("Balance Amount: Rs. " + balanceAmt);
            transactionAmt = "N/A";
        }*/

        // CARD_NUMBER =
        String flag = getIntent().getStringExtra("flag");
        if (flag.equalsIgnoreCase("failure")) {
            //hide download receipt button
            balanceText.setTextColor(getResources().getColor(R.color.redpos));
            status_icon.setImageResource(R.drawable.failed);
            if (card.contains("CHIP")) {
                card = card.replace("CHIP", " ");
            }
            if (TRANSACTION_ID != null && TRANSACTION_ID.length() != 0 && !TRANSACTION_ID.isEmpty()) {
                txnID.setText(TRANSACTION_ID);
            } else {
                txnID.setText("N/A");
            }
            accountnumber.setText(card);
            statusTxt = "Failed";
            bkgLayout.setVisibility(View.GONE);
            downloadReceiptBtn.setVisibility(View.GONE);
            printReceiptBtn.setVisibility(View.GONE);
            merchant_Text.setVisibility(View.GONE);
            customer_Text.setVisibility(View.GONE);
            downloadReceiptBtnMerchant.setVisibility(View.GONE);
            printReceiptBtnMerchant.setVisibility(View.GONE);
            if (RESPONSE_CODE.equals("00")) {
                balanceText.setText("Transaction Failed");
            } else if (statusCode != null && statusCode.length() != 0) {
                balanceText.setText(statusCode);
            } else {
                if (RESPONSE_CODE != null && !RESPONSE_CODE.isEmpty() && !TRANSACTION_ID.isEmpty()) {
                    BankResponse.showStatusMessage(manager, RESPONSE_CODE, balanceText);
                    PAXScreen.showFailure(manager);

                }
            }

            if (transaction_type.equalsIgnoreCase("cash")) {
                transactionTypeCheck = "Cash Withdrawal";
                card_amount.setText("₹" + transactionAmt);
                transactionamount.setText("₹" + transactionAmt);
            } else if (transaction_type.equalsIgnoreCase("POS")) {
                card_amount.setText(transactionAmt);
                transactionamount.setText(transactionAmt);

            } else if (isVoid) {
                transactionTypeCheck = "VOID";
                card_amount.setText("Balance Amount: Rs. " + balanceAmt);
            } else {
                transactionTypeCheck = "Balance Enquiry";
                card_amount.setText("Balance Amount: Rs. " + balanceAmt);
            }

            String transactionType = "";
            if (SdkConstants.transactionType.equalsIgnoreCase("0")) {
                transactionType = "BalanceEnquiry Failled!! ";
            } else {
                transactionType = "CashWithdraw Failled!! ";
            }

            String str = "";
            str = balanceText.getText().toString();

            if (Card_NUM != null && card_amount != null && !TID.isEmpty()) {
                String responseData = generateJsonData(transactionType, str, prefNum, Card_NUM, card_amount.getText().toString(), TID);
                SdkConstants.responseData = responseData;
                accountnumber.setText(Card_NUM);
            }

        } else {
            //Show Success
            //show download receipt button
            downloadReceiptBtn.setVisibility(View.VISIBLE);
            printReceiptBtn.setVisibility(View.VISIBLE);
            downloadReceiptBtnMerchant.setVisibility(View.VISIBLE);
            printReceiptBtnMerchant.setVisibility(View.VISIBLE);
            balanceText.setVisibility(View.VISIBLE);
            balanceText.setText(R.string.txn_success_str);
            statusTxt = "Success";
            if (card.contains("CHIP")) {
                card = card.replace("CHIP", " ");
            }
            accountnumber.setText(card);
            PAXScreen.showSuccess(manager);
            txnID.setText(TRANSACTION_ID);
            //  bank_name.setText(CARD_TYPE);
            if (transaction_type.equalsIgnoreCase("cash")) {
                transactionTypeCheck = "Cash Withdrawal";
                card_amount.setText("₹" + transactionAmt);
                transactionamount.setText("₹" + transactionAmt);
            } else if (isVoid) {
                transactionTypeCheck = "VOID";
                card_amount.setText(transactionAmt);
            } else {
                transactionTypeCheck = "Balance Enquiry";
                card_amount.setText("Balance Amount: Rs. " + balanceAmt);
            }

            String transactionType = "";
            if (SdkConstants.transactionType.equalsIgnoreCase("0")) {
                transactionType = "BalanceEnquiry Successful!! ";
            } else {
                transactionType = "CashWithdraw Successful!! ";
            }

            String str = "";
            str = statusTxt;

            String responseData = generateJsonData(transactionType, str, prefNum, Card_NUM, transactionAmt, TID);
            SdkConstants.responseData = responseData;


        }

        if (isVoid) {
            card_amount.setText(getIntent().getStringExtra("TRANSACTION_AMOUNT"));
        }


        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(SdkConstants.onFinishListener != null){
                    SdkConstants.onFinishListener.onSDKFinish(statusTxt,SdkConstants.paramA
                            ,balanceText.getText().toString());
                }
                finish();
            }
        });



        downloadReceiptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isMerchant = false;

                if (checker.lacksPermissions(REQUIRED_PERMISSION)) {
                    PermissionsActivity.startActivityForResult(TransactionStatusPOSActivity.this, PERMISSION_REQUEST_CODE, REQUIRED_PERMISSION);
                } else {
                    Date date = new Date();
                    long timeMilli = date.getTime();
                    System.out.println("Time in milliseconds using Date class: " + String.valueOf(timeMilli));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        createPdf(FileUtils.commonDocumentDirPath("PDF") + timeMilli + "_" + getIntent().getStringExtra("RRN_NO") + ".pdf");
                    } else {
                        createPdf(FileUtils.getAppPath(mContext) + timeMilli + "_" + getIntent().getStringExtra("RRN_NO") + ".pdf");
                    }
                }


            }
        });
        downloadReceiptBtnMerchant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                isMerchant = true;
                if (checker.lacksPermissions(REQUIRED_PERMISSION)) {
                    PermissionsActivity.startActivityForResult(TransactionStatusPOSActivity.this, PERMISSION_REQUEST_CODE, REQUIRED_PERMISSION);
                } else {
                    Date date = new Date();
                    long timeMilli = date.getTime();
                    System.out.println("Time in milliseconds using Date class: " + String.valueOf(timeMilli));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        createPdf(FileUtils.commonDocumentDirPath("PDF") + timeMilli + "_" + getIntent().getStringExtra("RRN_NO") + ".pdf");
                    } else {
                        createPdf(FileUtils.getAppPath(mContext) + timeMilli + "_" + getIntent().getStringExtra("RRN_NO") + ".pdf");
                    }
                }


            }
        });


        printReceiptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isMerchant = false;
                String deviceModel = android.os.Build.MODEL;
                if (deviceModel.equalsIgnoreCase("A910")) {

                    getPrintData(TRANSACTION_ID, date_time.getText().toString(),
                            prefNum, MID, TID, CARD_TYPE, Card_NUM, transactionAmt);


                } else if (deviceModel.equalsIgnoreCase("WPOS-3")) {
                    //start printing with wiseasy internal printer
                    new PrintThread().start();
                } else {
                    view = v;
                    printBluetooth(v);
                }
            }
        });


        printReceiptBtnMerchant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isMerchant = true;
                String deviceModel = android.os.Build.MODEL;
                if (deviceModel.equalsIgnoreCase("A910")) {

                    getPrintData(TRANSACTION_ID, date_time.getText().toString(),
                            prefNum, MID, TID, CARD_TYPE, Card_NUM, transactionAmt);


                } else if (deviceModel.equalsIgnoreCase("WPOS-3")) {
                    //start printing with wiseasy internal printer
                    new PrintThread().start();
                } else {
                    view = v;
                    printBluetooth(v);
                }
            }
        });

    }

    public String replaceWithZero(String s) {
        float amount = Integer.valueOf(s) / 100F;
        DecimalFormat formatter = new DecimalFormat("##,##,##,##0.00");
        return formatter.format(Double.parseDouble(String.valueOf(amount)));
    }

    public String generateJsonData(String status, String statusDesc, String rrn, String cardno, String bal, String terminalId) {
        String jdata = "";
        JSONObject obj = new JSONObject();
        try {
            obj.put("TransactionStatus", status);
            obj.put("StatusDescription", statusDesc);
            obj.put("RRN", rrn);
            obj.put("CardNumber", cardno);
            obj.put("Balance", bal);
            obj.put("TerminalID", terminalId);
            jdata = obj.toString();


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jdata;
    }

    private void createPdf(String filePath) {
        createPdfGenericMethod(filePath);
    }

    private void createPdfGenericMethod(String dest) {
        if (new File(dest).exists()) {
            new File(dest).delete();
        }
        try {
/**
 * Creating Document
 */
            Document document = new Document();

            // Location to save
            PdfWriter.getInstance(document, new FileOutputStream(dest));

            // Open to write
            document.open();

            // Document Settings
            document.setPageSize(PageSize.A5);
            document.addCreationDate();
            document.addAuthor("");
            document.addCreator("");
            document.setMargins(0, 0, 50, 50);
            Rectangle rect = new Rectangle(577, 825, 18, 15);
            rect.enableBorderSide(1);
            rect.enableBorderSide(2);
            rect.enableBorderSide(4);
            rect.enableBorderSide(8);
            rect.setBorder(Rectangle.BOX);
            rect.setBorderWidth(2);
            rect.setBorderColor(BaseColor.BLACK);
            //document.add(rect);

            /*commit git test*/
            BaseColor mColorAccent = new BaseColor(0, 153, 204, 255);
            float mValueFontSize = 26.0f;
            BaseFont urName = BaseFont.createFont("assets/fonts/brandon_medium.otf", "UTF-8", BaseFont.EMBEDDED);

            // LINE SEPARATOR
            LineSeparator lineSeparator = new LineSeparator();
            lineSeparator.setLineColor(new BaseColor(0, 0, 0, 68));
            Font mHeadingFontSize = new Font(urName, 30.0f, Font.BOLD, BaseColor.BLACK);
            if (bankName != null && bankName.length() > 0) {
                Chunk mOrderDetailsTitleChunk = new Chunk(bankName.trim(), mHeadingFontSize);
                Paragraph mOrderDetailsTitleParagraph = new Paragraph(mOrderDetailsTitleChunk);
                mOrderDetailsTitleParagraph.setAlignment(Element.ALIGN_CENTER);
                document.add(mOrderDetailsTitleParagraph);
                document.add(new Paragraph("\n"));
            }
            if (merchantName != null && merchantName.length() > 0) {
                Chunk mMerchantName = new Chunk(merchantName, mHeadingFontSize);
                Paragraph mMerchanParagraph = new Paragraph(mMerchantName);
                mMerchanParagraph.setAlignment(Element.ALIGN_CENTER);
                document.add(mMerchanParagraph);
            }
            if (location != null && location.length() > 0) {
                Chunk mLocationName = new Chunk(location.trim(), mHeadingFontSize);
                Paragraph mLocationNameParagraph = new Paragraph(mLocationName);
                mLocationNameParagraph.setAlignment(Element.ALIGN_CENTER);
                document.add(mLocationNameParagraph);
                document.add(new Paragraph("\n"));
            }
            Font mOrderFont = new Font(urName, 20.0f, Font.NORMAL, BaseColor.BLACK);
            Paragraph p = new Paragraph();
            p.add(new Chunk("Date/Time : ", mOrderFont));
            p.add(new Chunk(currentDate + " | " + currentTime, mOrderFont));
            document.add(p);

            Chunk mTxnDetails = new Chunk("Transaction Details", mHeadingFontSize);
            Paragraph mTxnDetailsParagraph = new Paragraph(mTxnDetails);
            mTxnDetailsParagraph.setAlignment(Element.ALIGN_CENTER);
            document.add(mTxnDetailsParagraph);
            document.add(new Paragraph("\n"));
            Chunk glue = new Chunk(new VerticalPositionMark());

            if ((midTid != null && midTid.length() > 0)) {
//                String midTidValue = (midTid.replaceAll("\\s+"," ")).replaceAll(":\\s+",":");
//                String[] midTidArr =midTidValue.split("\\s",0);
//                String midValue = midTidArr[0];
//                String tidValue = midTidArr[1];
                if (midTid.contains("TID")) {
                    String[] midTidArr = splitString(midTid, "TID");
                    midValue = midTidArr[0];
                    tidValue = "TID" + midTidArr[1];
                }
                Chunk mMidChunk = new Chunk("", mOrderFont);
                Paragraph mMidParagraph = new Paragraph(mMidChunk);
                mMidParagraph.add(midValue);
                mMidParagraph.add(glue);
                mMidParagraph.add(tidValue);
                document.add(mMidParagraph);
            }

            if ((batch != null && batch.length() > 0) || (rrn != null && rrn.length() > 0)) {
//                String invNo = (batch.replaceAll("\\s+"," ")).replaceAll(":\\s+",":");
//                String[] batchInvArr =invNo.split("\\s",0);
//                String batchValue = batchInvArr[0];
//                String invNoValue =batchInvArr[2]+" "+ batchInvArr[3];
//
//                String rrnAuthValue = (rrn.replaceAll("\\s+"," ")).replaceAll(":\\s+",":");
//                String[] rrnAuthArr =rrnAuthValue.split("\\s",0);
//                String rrnValue = rrnAuthArr[0];
//                 authValue = rrnAuthArr[1] + " " + rrnAuthArr[2];
                if (batch.contains("INV")) {
                    String[] batchInvArr = splitString(batch, "INV");
                    String batchValue = batchInvArr[0];
                    invNoValue = "INV" + batchInvArr[1];
                }

                if (rrn.contains("AUTH")) {
                    String[] rrnAuthArr = splitString(rrn, "AUTH");
                    rrnValue = rrnAuthArr[0];
                    authValue = "AUTH" + rrnAuthArr[1];

                }
                Chunk mBatchNoChunk = new Chunk("", mOrderFont);
                Paragraph mBatchNoParagraph = new Paragraph(mBatchNoChunk);
                mBatchNoParagraph.add(invNoValue);
                mBatchNoParagraph.add(glue);
                mBatchNoParagraph.add(rrnValue);
                document.add(mBatchNoParagraph);
            }
            if (transactionAmt != null && transactionAmt.length() > 0) {

                if (getIntent().getBooleanExtra("IsVoid", false)) {
                    Chunk mBatchNoChunk = new Chunk("", mOrderFont);
                    Paragraph mBatchNoParagraph = new Paragraph(mBatchNoChunk);
                    mBatchNoParagraph.add("Rs." + getIntent().getStringExtra("TRANSACTION_AMOUNT"));
                    mBatchNoParagraph.add(glue);
                    mBatchNoParagraph.add(authValue);
                    document.add(mBatchNoParagraph);

                } else {
                    Chunk mOrdertxnAmtChunk = new Chunk("", mOrderFont);
                    Paragraph mOrdertxnAmtParagraph = new Paragraph(mOrdertxnAmtChunk);
                    mOrdertxnAmtParagraph.add("Transaction Amount: Rs." + transactionamount.getText().toString());
                    mOrdertxnAmtParagraph.add(glue);
                    mOrdertxnAmtParagraph.add(authValue);
                    document.add(mOrdertxnAmtParagraph);
                }
            }

            Chunk mTxnTypeMsgChunk = new Chunk("Transaction Type: " + "SALE@POS", mOrderFont);
            Paragraph mTxnTypeMsgParagraph = new Paragraph(mTxnTypeMsgChunk);
            document.add(mTxnTypeMsgParagraph);

            if (card != null && card.length() > 0) {
                if (card.contains("CHIP")) {
                    card = card.replace("CHIP", " ");
                }
                Chunk mCardNoChunk = new Chunk("", mOrderFont);
                Paragraph mCardNoParagraph = new Paragraph(mCardNoChunk);
                mCardNoParagraph.add("Card Number: " + card);
                document.add(mCardNoParagraph);
            }
           /* if (applPreferredName != null && applPreferredName.length() > 0) {
                Chunk mCardNoChunk = new Chunk("", mOrderFont);
                Paragraph mCardTypeParagraph = new Paragraph(mCardNoChunk);
                mCardTypeParagraph.add("Card Type: " + applPreferredName);
                document.add(mCardTypeParagraph);
            }*/
            if(cardType != null && cardType.length() >0){
                if (cardType.contains("Exp")) {
                    String[] rrnAuthArr = splitString(cardType, "Exp");
                    cardTypeValue = rrnAuthArr[0];
                    authValue = "Exp" + rrnAuthArr[1];
                }
                Chunk mCardNoChunk = new Chunk("", mOrderFont);
                Paragraph mCardTypeParagraph = new Paragraph(mCardNoChunk);
                mCardTypeParagraph.add(cardTypeValue);
                document.add(mCardTypeParagraph);
            }

            if (!dedicatedFileName.isEmpty() && !terminalVerResult.isEmpty()) {
                Chunk mAidMsgChunk = new Chunk("", mOrderFont);
                Paragraph mAidMsgParagraph = new Paragraph(mAidMsgChunk);
                mAidMsgParagraph.add("AID: " + dedicatedFileName);
                mAidMsgParagraph.add(glue);
                mAidMsgParagraph.add("TVR: " + terminalVerResult);
                document.add(mAidMsgParagraph);
            }
            if (applicationCryptogram != null && !applicationCryptogram.isEmpty()) {
                Chunk mTcMsgChunk = new Chunk("TC: " + applicationCryptogram, mOrderFont);
                Paragraph mTcMsgParagraph = new Paragraph(mTcMsgChunk);
                document.add(mTcMsgParagraph);
            }
            Font mOrderAcNameFont = new Font(urName, 24.0f, Font.NORMAL, mColorAccent);
            Chunk mOrderAcNameChunk = new Chunk("Thank You", mOrderAcNameFont);
            Paragraph mOrderAcNameParagraph = new Paragraph(mOrderAcNameChunk);
            mOrderAcNameParagraph.setAlignment(Element.ALIGN_RIGHT);
            document.add(mOrderAcNameParagraph);
            Font mOrderAcNameValueFont = new Font(urName, mValueFontSize, Font.NORMAL, BaseColor.BLACK);
            Chunk mOrderAcNameValueChunk = new Chunk(SdkConstants.BRAND_NAME, mOrderAcNameValueFont);
            Paragraph mOrderAcNameValueParagraph = new Paragraph(mOrderAcNameValueChunk);
            mOrderAcNameValueParagraph.setAlignment(Element.ALIGN_RIGHT);
            document.add(mOrderAcNameValueParagraph);

            Font mDetailsFont = new Font(urName, 20.0f, Font.NORMAL, BaseColor.BLACK);
            Chunk pinMsg = new Chunk(pin, mDetailsFont);
            Paragraph mPinMsgParagraph = new Paragraph(pinMsg);
            mPinMsgParagraph.setAlignment(Element.ALIGN_CENTER);
            document.add(mPinMsgParagraph);

            Font mCardHoldersFont = new Font(urName, 20.0f, Font.BOLD, BaseColor.BLACK);
            Chunk mCardHolderChunk = new Chunk(cardHolderName, mCardHoldersFont);
            Paragraph mCardHolderNameParagraph = new Paragraph(mCardHolderChunk);
            mCardHolderNameParagraph.setAlignment(Element.ALIGN_CENTER);
            document.add(mCardHolderNameParagraph);

            Chunk agreeMsg = new Chunk("I AGREE TO PAY AS PER\nCARD ISSUER AGREEMENT", mDetailsFont);
            Paragraph mAgreeMsgParagraph = new Paragraph(agreeMsg);
            mAgreeMsgParagraph.setAlignment(Element.ALIGN_CENTER);
            document.add(mAgreeMsgParagraph);

            if (isMerchant) {
                Chunk customerMsg = new Chunk("***** Merchant Copy *****", mDetailsFont);
                Paragraph mCustomerMsgParagraph = new Paragraph(customerMsg);
                mCustomerMsgParagraph.setAlignment(Element.ALIGN_CENTER);
                document.add(mCustomerMsgParagraph);
            } else {
                Chunk customerMsg = new Chunk("***** Customer Copy *****", mDetailsFont);
                Paragraph mCustomerMsgParagraph = new Paragraph(customerMsg);
                mCustomerMsgParagraph.setAlignment(Element.ALIGN_CENTER);
                document.add(mCustomerMsgParagraph);
            }


          /*  Chunk powerByMsg = new Chunk("Powered by iServeU Ver 1.0.0", mDetailsFont);
            Paragraph mPowerByMsgParagraph = new Paragraph(powerByMsg);
            mPowerByMsgParagraph.setAlignment(Element.ALIGN_CENTER);
            document.add(mPowerByMsgParagraph);*/

            document.close();
            Toast.makeText(mContext, "PDF saved in the internal storage", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(TransactionStatusPOSActivity.this, PreviewPDFActivity.class);
            intent.putExtra("filePath", dest);
            startActivity(intent);

        } catch (IOException | DocumentException ie) {
            Log.e("createPdf: Error ", "" + ie.getLocalizedMessage());
        } catch (ActivityNotFoundException ae) {
            Toast.makeText(mContext, "No application found to open this file.", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * @param txnId
     * @param date
     * @param reffNo
     * @param mid
     * @param terminalId
     * @param type
     * @param cardNumber
     * @param transactionAmt
     */
    private void getPrintData(final String txnId, final String date, final String reffNo,
                              final String mid, final String terminalId, final String type,
                              final String cardNumber, final String transactionAmt) {
        new Thread(new Runnable() {
            public void run() {
                GetConnectToPrinter.getInstance().init();

                //Shop Name Set
                GetConnectToPrinter.getInstance().fontSet(EFontTypeAscii.FONT_24_48,
                        EFontTypeExtCode.FONT_24_48);
                GetConnectToPrinter.getInstance().leftIndents(Short.parseShort("60"));
                GetConnectToPrinter.getInstance().setInvert(false);
                GetConnectToPrinter.getInstance().printStr(SdkConstants.SHOP_NAME, null);
                GetConnectToPrinter.getInstance().step(Integer.parseInt("20"));

                //Transaction Details Message
                GetConnectToPrinter.getInstance().fontSet(EFontTypeAscii.FONT_16_32,
                        EFontTypeExtCode.FONT_16_32);
                GetConnectToPrinter.getInstance().printStr(getString(R.string.txn_report_txt), null);
                GetConnectToPrinter.getInstance().step(Integer.parseInt("20"));

                //status Message
                GetConnectToPrinter.getInstance().fontSet(EFontTypeAscii.FONT_16_32,
                        EFontTypeExtCode.FONT_16_32);
                GetConnectToPrinter.getInstance().leftIndents(Short.parseShort("110"));
                GetConnectToPrinter.getInstance().printStr(statusTxt, null);
                GetConnectToPrinter.getInstance().step(Integer.parseInt("30"));

                GetConnectToPrinter.getInstance().fontSet(EFontTypeAscii.FONT_12_24,
                        EFontTypeExtCode.FONT_16_32);
                GetConnectToPrinter.getInstance().spaceSet((byte) 0, (byte) 0);
                GetConnectToPrinter.getInstance().leftIndents(Short.parseShort("0"));
                GetConnectToPrinter.getInstance().printStr(getString(R.string.transaction_id_txt) + txnId, null);
                GetConnectToPrinter.getInstance().step(Integer.parseInt("10"));
                GetConnectToPrinter.getInstance().printStr(getString(R.string.date_time_txt) + date, null);
                GetConnectToPrinter.getInstance().step(Integer.parseInt("10"));
                GetConnectToPrinter.getInstance().printStr(getString(R.string.rrn_txt) + reffNo, null);
                GetConnectToPrinter.getInstance().step(Integer.parseInt("10"));
                GetConnectToPrinter.getInstance().printStr(getString(R.string.mid_txt) + mid, null);
                GetConnectToPrinter.getInstance().step(Integer.parseInt("10"));
                GetConnectToPrinter.getInstance().printStr(getString(R.string.terminal_id_txt) + terminalId, null);
                GetConnectToPrinter.getInstance().step(Integer.parseInt("10"));
                GetConnectToPrinter.getInstance().printStr(getString(R.string.card_number_txt) + cardNumber, null);
                GetConnectToPrinter.getInstance().step(Integer.parseInt("10"));
                GetConnectToPrinter.getInstance().printStr(getString(R.string.balance_amt_txt) + balanceAmt, null);
                GetConnectToPrinter.getInstance().step(Integer.parseInt("10"));
                GetConnectToPrinter.getInstance().printStr(getString(R.string.transaction_type_txt) + transactionTypeCheck, null);
                GetConnectToPrinter.getInstance().step(Integer.parseInt("10"));

                //Transaction Amount
                GetConnectToPrinter.getInstance().fontSet(EFontTypeAscii.FONT_12_24,
                        EFontTypeExtCode.FONT_16_32);
                GetConnectToPrinter.getInstance().spaceSet((byte) 0, (byte) 0);
                GetConnectToPrinter.getInstance().leftIndents(Short.parseShort("0"));
                GetConnectToPrinter.getInstance().printStr(getString(R.string.txn_amt_txt) + transactionAmt, null);
                GetConnectToPrinter.getInstance().step(Integer.parseInt("25"));


                //Thank You Message
                GetConnectToPrinter.getInstance().fontSet(EFontTypeAscii.FONT_16_32,
                        EFontTypeExtCode.FONT_16_32);
                String thankYouString = getString(R.string.thanks_txt);
                if (thankYouString != null && thankYouString.length() > 0)
                    GetConnectToPrinter.getInstance().printStr(thankYouString, null);
                GetConnectToPrinter.getInstance().step(Integer.parseInt("15"));

                //Partner(Admin, MD etc.) Name Message
                GetConnectToPrinter.getInstance().fontSet(EFontTypeAscii.FONT_12_24,
                        EFontTypeExtCode.FONT_16_32);
                String brandName = SdkConstants.BRAND_NAME;
                if (brandName != null && brandName.length() > 0)
                    GetConnectToPrinter.getInstance().printStr(brandName, null);
                GetConnectToPrinter.getInstance().step(Integer.parseInt("100"));

                GetConnectToPrinter.getInstance().start();
            }
        }).start();
    }

    private void getConnectToPrinter(View view) {
        if(isMerchant){
            registerForContextMenu(printReceiptBtnMerchant);
        }else{
            registerForContextMenu(printReceiptBtn);
        }

        if (bluetoothAdapter == null) {
            Toast.makeText(TransactionStatusPOSActivity.this, "Bluetooth NOT supported", Toast.LENGTH_SHORT).show();
        } else {
            if (bluetoothAdapter.isEnabled()) {
                if (GetPosConnectedPrinter.aemPrinter == null) {
                    printerList = m_AemScrybeDevice.getPairedPrinters();
                    if (printerList.size() > 0) {
                        openContextMenu(view);
                    } else {
                        showAlert("No Paired Printers found");
                    }
                } else {
                    m_AemPrinter = GetPosConnectedPrinter.aemPrinter;
                    callBluetoothFunction(TRANSACTION_ID, prefNum, MID, TID, CARD_TYPE, Card_NUM, transactionAmt, view);

                }
            } else {
                GetPosConnectedPrinter.aemPrinter = null;
                Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(turnOn, 0);
            }
        }
    }

    private void callBluetoothFunction(final String txnId, final String reffNo, final String mid, final String terminalId, final String type, final String cardNumber, final String transactionAmt, View view) {
        try {
            m_AemPrinter.setFontType(AEMPrinter.DOUBLE_HEIGHT);
            m_AemPrinter.setFontType(AEMPrinter.FONT_NORMAL);
            m_AemPrinter.setFontType(AEMPrinter.FONT_002);
            m_AemPrinter.POS_FontThreeInchCENTER();
            m_AemPrinter.print(bankName.trim());
            m_AemPrinter.print("\n");
            m_AemPrinter.POS_FontThreeInchCENTER();
            m_AemPrinter.setFontType(AEMPrinter.DOUBLE_HEIGHT);
            m_AemPrinter.print(merchantName.trim());
            m_AemPrinter.print("\n");
            m_AemPrinter.POS_FontThreeInchCENTER();
            m_AemPrinter.setFontType(AEMPrinter.DOUBLE_HEIGHT);
            m_AemPrinter.print(location.trim());
//            m_AemPrinter.print("\n\n");
//            m_AemPrinter.print(txnId);
            m_AemPrinter.print("\n");
            m_AemPrinter.print("Date/Time: " + currentDate + " | " + currentTime);
            m_AemPrinter.print("\n");
            if ((midTid != null && midTid.length() > 0)) {
//                String midTidValue = (midTid.replaceAll("\\s+"," ")).replaceAll(":\\s+",":");
//                String[] midTidArr =midTidValue.split("\\s",0);
//                String midValue = midTidArr[0];
//                String tidValue = midTidArr[1];
                if (midTid.contains("TID")) {
                    String[] midTidArr = splitString(midTid, "TID");
                    midValue = midTidArr[0];
                    tidValue = "TID" + midTidArr[1];
                }
                m_AemPrinter.print(midValue);
                m_AemPrinter.print("\n");
                m_AemPrinter.print(tidValue);
                m_AemPrinter.print("\n");
            }

            if ((batch != null && batch.length() > 0) || (rrn != null && rrn.length() > 0)) {
//                String invNo = (batch.replaceAll("\\s+"," ")).replaceAll(":\\s+",":");
//                String[] batchInvArr =invNo.split("\\s",0);
//                String batchValue = batchInvArr[0]+ " " + batchInvArr[1];
//                String invNoValue =batchInvArr[2]+" "+ batchInvArr[3];
                if (batch.contains("INV")) {
                    String[] batchInvArr = splitString(batch, "INV");
                    batchValue = batchInvArr[0];
                    invNoValue = "INV" + batchInvArr[1];
                }

                m_AemPrinter.print(invNoValue);
                m_AemPrinter.print("\n");

            }
            m_AemPrinter.print("Transaction Type: Sale@POS");
            m_AemPrinter.print("\n");
            m_AemPrinter.print("Card No.:" + card.trim());
            m_AemPrinter.print("\n");
            if(cardType != null && cardType.length() >0){
                if (cardType.contains("Exp")) {
                    String[] rrnAuthArr = splitString(cardType, "Exp");
                    cardTypeValue = rrnAuthArr[0];
                    authValue = "Exp" + rrnAuthArr[1];
                }
                m_AemPrinter.print(cardTypeValue);
                m_AemPrinter.print("\n");
            }

            if ((rrn != null && rrn.length() > 0)) {
//                String rrnAuthValue = (rrn.replaceAll("\\s+"," ")).replaceAll(":\\s+",":");
//                String[] rrnAuthArr =rrnAuthValue.split("\\s",0);
//                String rrnValue = rrnAuthArr[0];
//                authValue = rrnAuthArr[1] + " " + rrnAuthArr[2];
                if (rrn.contains("AUTH")) {
                    String[] rrnAuthArr = splitString(rrn, "AUTH");
                    rrnValue = rrnAuthArr[0];
                    authValue = "AUTH" + rrnAuthArr[1];
                }
                m_AemPrinter.print(rrnValue);
                m_AemPrinter.print("\n");
                m_AemPrinter.print(authValue);
                m_AemPrinter.print("\n");

            }

            m_AemPrinter.print("Transaction Amount : Rs." + transactionAmt);
            m_AemPrinter.print("\n");
            if (applicationCryptogram != null && !applicationCryptogram.isEmpty()) {
                m_AemPrinter.print("TC : " + applicationCryptogram);
                m_AemPrinter.print("\n");
            }
            m_AemPrinter.print("AID : " + dedicatedFileName);
            m_AemPrinter.print("\n");
            m_AemPrinter.print("TVR: " + terminalVerResult);
            m_AemPrinter.print("\n");
//            m_AemPrinter.print("App Name : " + applPreferredName);
//            m_AemPrinter.print("\n");
//            m_AemPrinter.print("TSI: " + txnStatus);
//            m_AemPrinter.print("\n");
            m_AemPrinter.POS_FontThreeInchCENTER();
            m_AemPrinter.print(pin);
            m_AemPrinter.print("\n");
            m_AemPrinter.POS_FontThreeInchCENTER();
            m_AemPrinter.print(cardHolderName);
            m_AemPrinter.print("\n");
            m_AemPrinter.POS_FontThreeInchCENTER();
            m_AemPrinter.print("I AGREE TO PAY AS PER");
            m_AemPrinter.print("\n");
            m_AemPrinter.POS_FontThreeInchCENTER();
            m_AemPrinter.print("CARD ISSUER AGREEMENT");
            m_AemPrinter.print("\n");
            m_AemPrinter.POS_FontThreeInchCENTER();
            if (isMerchant) {
                m_AemPrinter.print("***** Merchant Copy *****");
            } else {
                m_AemPrinter.print("***** Customer Copy *****");
            }
           /* m_AemPrinter.print("\n");
            m_AemPrinter.POS_FontThreeInchCENTER();
            m_AemPrinter.print("Powered by iServeU Ver 1.0.0");*/
            m_AemPrinter.print("\n\n");
            m_AemPrinter.setCarriageReturn();
            m_AemPrinter.setCarriageReturn();
            m_AemPrinter.setCarriageReturn();
            m_AemPrinter.setCarriageReturn();
            data = printerStatus();
            m_AemPrinter.print(data);
            m_AemPrinter.print("\n");
        } catch (IOException e) {
//            e.printStackTrace();
            try {
                GetPosConnectedPrinter.aemPrinter = null;
                getConnectToPrinter(view);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    private String printerStatus() throws IOException {
        String data = new String(printerStatus);
        m_AemPrinter.print(data);
        return data;
    }

    @Override
    public void onScanMSR(String buffer, CardReader.CARD_TRACK cardtrack) {
        cardTrackType = cardtrack;
        creditData = buffer;
        TransactionStatusPOSActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                //                editText.setText(buffer.toString());

            }
        });

    }

    @Override
    public void onScanDLCard(String buffer) {
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
    public void onScanRCCard(String buffer) {
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
    public void onScanRFD(String buffer) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(buffer);
        String temp = "";
        try {
            temp = stringBuffer.deleteCharAt(8).toString();
        } catch (Exception e) {
            // TODO: handle exception
        }
        final String data = temp;

        TransactionStatusPOSActivity.this.runOnUiThread(new Runnable() {
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
            TransactionStatusPOSActivity.this.runOnUiThread(new Runnable() {
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
            TransactionStatusPOSActivity.this.runOnUiThread(new Runnable() {
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

    @Override
    public void onDiscoveryComplete(ArrayList<String> aemPrinterList) {
        printerList = aemPrinterList;
        for (int i = 0; i < aemPrinterList.size(); i++) {
            String Device_Name = aemPrinterList.get(i);
            String status = m_AemScrybeDevice.pairPrinter(Device_Name);
            Log.e("STATUS", status);
        }

    }

    private void checkPrinterStatus() {
        try {
            int[] status = new int[1];
            mPrinter.getPrinterStatus(status);
            Log.e(TAG, "Printer Status is " + status[0]);
            String msg = "";
            switch (status[0]) {
                case 0x00:
                    msg = "Printer status OK";
                    Log.e(TAG, "check printer status: " + msg);
                    startPrinting();
                    break;
                case 0x01:
                    msg = "Parameter error";
                    showLog(msg);
                    break;
                case 0x8A://----138 return
                    msg = "Out of Paper";
                    showLog(msg);
                    break;
                case 0x8B:
                    msg = "Overheat";
                    showLog(msg);
                    break;
                default:
                    msg = "Printer Error";
                    showLog(msg);
                    break;

            }

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void startPrinting() {
        int result = -1;
        try {
            result = mPrinter.printInit();
            Log.e(TAG, "startPrinting: Printer init result " + result);
            mPrinter.clearPrintDataCache();
            if (result == 0) {
                printReceipt();
            } else {
                Toast.makeText(this, "Printer initialization failed", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void printReceipt() {
        int result = -1;
        try {
            Log.e(TAG, "printReceipt: set density low 3");
            mPrinter.setGrayLevel(3);
            result = mPrinter.printStringExt("iServeU", 0, 0f, 2.0f, Printer.Font.SANS_SERIF, 35, Printer.Align.CENTER, true, false, true);
            result = mPrinter.printString("Transaction Report", 28, Printer.Align.CENTER, true, false);
            if (statusTxt.equalsIgnoreCase("Success")) {
                result = mPrinter.printString("Success", 28, Printer.Align.CENTER, true, false);
            } else {
                result = mPrinter.printString("Failure", 28, Printer.Align.CENTER, true, false);
            }
            result = mPrinter.printString("------------------------------------------", 30, Printer.Align.CENTER, true, false);
            result = mPrinter.printString("Transaction Id :" + TRANSACTION_ID, 25, Printer.Align.LEFT, false, false);
            result = mPrinter.printString("Date/Time : " + currentDateTime, 25, Printer.Align.LEFT, false, false);
            result = mPrinter.printString("RRN :" + prefNum, 25, Printer.Align.LEFT, false, false);
            result = mPrinter.printString("MID :" + MID, 25, Printer.Align.LEFT, false, false);
            result = mPrinter.printString("Terminal Id : " + TID, 25, Printer.Align.LEFT, false, false);
            result = mPrinter.printString("Card Number : " + Card_NUM, 25, Printer.Align.LEFT, false, false);
            result = mPrinter.printString("Balance Amount :" + balanceAmt, 25, Printer.Align.LEFT, false, false);
            if (transaction_type.equalsIgnoreCase("cash")) {
                result = mPrinter.printString("Transaction Type : CASH WITHDRAWAL", 25, Printer.Align.LEFT, false, false);
                result = mPrinter.printString("Transaction Amount :" + transactionAmt, 25, Printer.Align.LEFT, false, false);

            } else {
                result = mPrinter.printString("Transaction Type : BALANCE ENQUIRY", 25, Printer.Align.LEFT, false, false);
                result = mPrinter.printString("Transaction Amount :N/A", 25, Printer.Align.LEFT, false, false);
            }
            result = mPrinter.printStringExt("Thank You !", 0, 0f, 2.0f, Printer.Font.SANS_SERIF, 25, Printer.Align.RIGHT, true, true, false);
            result = mPrinter.printStringExt(SdkConstants.BRAND_NAME + "\n\n\n", 0, 0f, 2.0f, Printer.Font.SANS_SERIF, 25, Printer.Align.RIGHT, false, true, false);

            //result = mPrinter.printString("------------------------------------------\n", 30, Printer.Align.CENTER, false, false);
            Log.e(TAG, "printReceipt: print thank you result " + result);

            result = mPrinter.printPaper(27);
            Log.e(TAG, "printReceipt: print step result " + result);
            showPrinterStatus(result);

            result = mPrinter.printFinish();
            Log.e(TAG, "printReceipt: printer finish result " + result);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void showPrinterStatus(int result) {
        String msg = "";
        switch (result) {
            case 0x00:
                msg = "Print Finish";
                showLog(msg);
                break;
            case 0x01:
                msg = "Parameter error";
                showLog(msg);
                break;
            case 0x8A://----138 return
                msg = "Out of Paper";
                showLog(msg);
                break;
            case 0x8B:
                msg = "Overheat";
                showLog(msg);
                break;
            default:
                msg = "Printer Error";
                showLog(msg);
                break;
        }
    }

    private void showLog(String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(TransactionStatusPOSActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
        Log.e(TAG, "Printer status: " + msg);
    }

    public void showAlert(String alertMsg) {
        android.app.AlertDialog.Builder alertBox = new android.app.AlertDialog.Builder(TransactionStatusPOSActivity.this);
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
            Toast.makeText(TransactionStatusPOSActivity.this, "Connected with " + printerName, Toast.LENGTH_SHORT).show();

            //            String data=new String(batteryStatusCommand);
//            m_AemPrinter.print(data);
            //  m_cardReader.readMSR();


        } catch (IOException e) {
            if (e.getMessage().contains("Service discovery failed")) {
                Toast.makeText(TransactionStatusPOSActivity.this, "Not Connected\n" + printerName + " is unreachable or off otherwise it is connected with other device", Toast.LENGTH_SHORT).show();
            } else if (e.getMessage().contains("Device or resource busy")) {
                Toast.makeText(TransactionStatusPOSActivity.this, "the device is already connected", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(TransactionStatusPOSActivity.this, "Unable to connect", Toast.LENGTH_SHORT).show();
            }
        }
        return true;
    }

    //for wiseasy
    private class PrintThread extends Thread {
        @Override
        public void run() {
            mPrinter = new Printer(TransactionStatusPOSActivity.this);
            try {
                mPrinter.setPrintType(0);//Printer type 0 means it's an internal printer
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            checkPrinterStatus();
        }
    }
    @Override
    public void onBackPressed() {
        if(SdkConstants.onFinishListener != null){
            SdkConstants.onFinishListener.onSDKFinish(statusTxt,SdkConstants.paramA
                    ,balanceText.getText().toString());
        }
        finish();
    }


    public void printBluetooth(View v) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH}, UnifiedAepsTransactionActivity.PERMISSION_BLUETOOTH);
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_ADMIN}, UnifiedAepsTransactionActivity.PERMISSION_BLUETOOTH_ADMIN);
        } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, UnifiedAepsTransactionActivity.PERMISSION_BLUETOOTH_CONNECT);
        } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_SCAN}, UnifiedAepsTransactionActivity.PERMISSION_BLUETOOTH_SCAN);
        }else{
            getConnectToPrinter(v);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case UnifiedAepsTransactionActivity.PERMISSION_BLUETOOTH:
                case UnifiedAepsTransactionActivity.PERMISSION_BLUETOOTH_ADMIN:
                case UnifiedAepsTransactionActivity.PERMISSION_BLUETOOTH_CONNECT:
                case UnifiedAepsTransactionActivity.PERMISSION_BLUETOOTH_SCAN:
                    this.printBluetooth(view);
                    break;
            }
        }
    }

}