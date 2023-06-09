package com.matm.matmsdk.transaction_report;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.RemoteException;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
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
import com.matm.matmsdk.FileUtils;
import com.matm.matmsdk.Service.BankResponse;
import com.matm.matmsdk.Utils.PAXScreen;
import com.matm.matmsdk.Utils.SdkConstants;
import com.matm.matmsdk.Utils.getToneGenerator;
import com.matm.matmsdk.aepsmodule.unifiedaeps.UnifiedAepsTransactionActivity;
import com.matm.matmsdk.aepsmodule.utils.GetPosConnectedPrinter;
import com.matm.matmsdk.permission.PermissionsActivity;
import com.matm.matmsdk.permission.PermissionsChecker;
import com.matm.matmsdk.readfile.PreviewPDFActivity;
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

import static com.matm.matmsdk.permission.PermissionsActivity.PERMISSION_REQUEST_CODE;
import static com.matm.matmsdk.permission.PermissionsChecker.REQUIRED_PERMISSION;

public class  TransactionStatusActivity extends AppCompatActivity implements IAemCardScanner, IAemScrybe {

    TextView card_amount, cardHolderName, cardNumber, transactionStatus, date_time, transactionAmount, txnID, trantypename;
    ImageView status_icon, card_Type;
    Button txndetailsBtn, downloadReceiptBtn, printReceiptBtn, finishBtn;
    LinearLayout bkgLayout;

    public EasyLinkSdkManager manager;
    private CheckBox mobileCheckBox;

    private ProgressDialog progressDialog;
    private PermissionsChecker checker;
    private Context mContext;
    private String statusTxt = "";
    private String transaction_type;

    private String prefNum = "NA";
    private String mid = "NA";
    private String tid = "NA";
    private String cardType = "NA";
    private String cardNum = "NA";
    private String cardholdername = "NA";
    private String balanceAmt = "NA";
    private String transactionAmt = "NA";
    private String transactionId = "NA";
    private String transactionTypeCheck;
    private String filePath = "";
    private String currentDateTime;
    private String responseCode;
    private String flagStatus;
    private String transactionType = "";
    private String statusDesc;
    private String responseData;
    private String statusCode;
//    private TextView tvStatus;
    private String brandName="";
    private String shopName="";

    String MID = "NA", TID = "NA", CARD_TYPE = "NA", Card_NUM = "NA", TRANSACTION_ID = "NA", TXN_ID = "NA";

    AEMScrybeDevice m_AemScrybeDevice;
    AEMPrinter m_AemPrinter = null;
    CardReader m_cardReader = null;
    CardReader.CARD_TRACK cardTrackType;
    String creditData, tempdata, replacedData, data;
    ArrayList<String> printerList;
    String responseString, response;
    int numChars;
    BluetoothAdapter bluetoothAdapter;
    String[] responseArray = new String[1];
    char[] printerStatus = new char[]{0x1B, 0x7E, 0x42, 0x50, 0x7C, 0x47, 0x45, 0x54, 0x7C, 0x50, 0x52, 0x4E, 0x5F, 0x53, 0x54, 0x5E};
    String printerName;
    private wangpos.sdk4.libbasebinder.Printer mPrinter;
    private static final String TAG = TransactionStatusActivity.class.getSimpleName();
    private View view;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_status);
        manager = EasyLinkSdkManager.getInstance(this);

        //Runtime permission request required if Android permission >= Marshmallow
        checker = new PermissionsChecker(this);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        m_AemScrybeDevice = new AEMScrybeDevice(TransactionStatusActivity.this);
        printerList = new ArrayList<String>();
        creditData = new String();
        mContext = getApplicationContext();
        new getToneGenerator();

        status_icon = findViewById(R.id.status_icon);
        card_amount = findViewById(R.id.amount);
        transactionStatus = findViewById(R.id.txtStatus);
        date_time = findViewById(R.id.date_time);
        bkgLayout = findViewById(R.id.bkgLayout);
        cardNumber = findViewById(R.id.accountnumber);
        transactionAmount = findViewById(R.id.transactionamount);
        cardHolderName = findViewById(R.id.accountholdername);
        card_Type = findViewById(R.id.card_type);
        txnID = findViewById(R.id.txnID);
        trantypename = findViewById(R.id.tranType_name);
        txndetailsBtn = findViewById(R.id.txndetailsBtn);
        downloadReceiptBtn = findViewById(R.id.downloadBtn);
        printReceiptBtn = findViewById(R.id.printBtn);
        finishBtn = findViewById(R.id.finishBtn);


//        mobileTextLayout = findViewById(R.id.mobileTextLayout);
//        bank_name = findViewById(R.id.cardtype);
//        mobileCheckBox = findViewById(R.id.mobileCheckBox);
//        closeBtn = findViewById(R.id.closeBtn);

//        backBtn = findViewById(R.id.backBtn);
////        mobileEditLayout = findViewById(R.id.mobileEditLayout);
////        editTextMobile = findViewById(R.id.editTextMobile);
//        mobileNo = getIntent().getStringExtra("MOBILE_NUMBER");
////        editTextMobile.setText(mobileNo);


        Date date = Calendar.getInstance().getTime();
        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        currentDateTime = formatter.format(date);
        date_time.setText(currentDateTime);

        transaction_type = getIntent().getStringExtra("TRANSACTION_TYPE");
        TRANSACTION_ID = getIntent().getStringExtra("TRANSACTION_ID");
        currentDateTime = formatter.format(date);

        String applName = getIntent().getStringExtra("APP_NAME");
        String aId = getIntent().getStringExtra("AID");
        prefNum = getIntent().getStringExtra("RRN_NO");
        MID = getIntent().getStringExtra("MID");
        if(MID.equals("null") || MID.equals("")){
            MID="NA";
        }
        Log.i("MID",mid);


        TID = getIntent().getStringExtra("TID");
        TXN_ID = getIntent().getStringExtra("TXN_ID");
        String INVOICE = getIntent().getStringExtra("INVOICE");
        cardType = getIntent().getStringExtra("CARD_TYPE");
        String APPR_CODE = getIntent().getStringExtra("APPR_CODE");
        Card_NUM = getIntent().getStringExtra("CARD_NUMBER");
        balanceAmt = getIntent().getStringExtra("AMOUNT");
        Log.i("balanceamt",balanceAmt);
        transactionAmt = getIntent().getStringExtra("TRANSACTION_AMOUNT");
//        balanceAmt = getDecimalString(balanceAmt);
        responseCode = getIntent().getStringExtra("RESPONSE_CODE");
        cardholdername = getIntent().getStringExtra("CARD_HOLDERNAME");
        if(cardholdername.equals("")){
            cardholdername="N/A";
        }
        Log.i("CARD_HOLDERNAME",cardholdername);
        Log.i("CARD_HOLDERNAME",cardType);

        brandName=getIntent().getStringExtra("brand_name");
        shopName=getIntent().getStringExtra("shop_name");
        statusCode = getIntent().getStringExtra("status_code");

//        Log.i("statusCode",statusCode);


        boolean isVoid = getIntent().getBooleanExtra("IsVoid", false);
        if (cardType == null) {
            card_Type.setVisibility(View.GONE);
        } else {
            if (cardType.equalsIgnoreCase("MASTER")) {
                card_Type.setImageDrawable(getResources().getDrawable(R.drawable.mastercard));
            }
            else if(cardType.equalsIgnoreCase("mastercard")){
                card_Type.setImageDrawable(getResources().getDrawable(R.drawable.mastercard));
            }
            else if (cardType.equalsIgnoreCase("visa")) {
                card_Type.setImageDrawable(getResources().getDrawable(R.drawable.visacard));
            } else if (cardType.equalsIgnoreCase("RUPAY")) {
                card_Type.setImageDrawable(getResources().getDrawable(R.drawable.rupaycard));
            }
        }

        if (balanceAmt != null) {
            if (balanceAmt.equalsIgnoreCase("N/A") || balanceAmt.equalsIgnoreCase("NA")) {
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
            transactionAmount.setText("₹" + transactionAmt);
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
            transactionStatus.setTextColor(getResources().getColor(R.color.redpos));
            status_icon.setImageResource(R.drawable.failed);

            if (TRANSACTION_ID != null && TRANSACTION_ID.length() != 0 && !TRANSACTION_ID.isEmpty()) {
                txnID.setText(TRANSACTION_ID);
            } else {
                txnID.setText("N/A");
            }
            statusTxt = "Failed";
            bkgLayout.setVisibility(View.VISIBLE);
            downloadReceiptBtn.setVisibility(View.VISIBLE);
            printReceiptBtn.setVisibility(View.VISIBLE);

            if (responseCode.equals("00")) {
                card_amount.setText("Transaction Failed");
                card_amount.setTextColor(getResources().getColor(R.color.red));
            } else{
                card_amount.setText(statusCode);
                card_amount.setTextColor(getResources().getColor(R.color.red));
            } /*else {
                if (responseCode != null && !responseCode.isEmpty() && !TRANSACTION_ID.isEmpty()) {
                    BankResponse.showStatusMessage(manager, responseCode, transactionStatus);
                    PAXScreen.showFailure(manager);

                }
            }*/

            if (transaction_type.equalsIgnoreCase("cash")) {
                transactionTypeCheck = "Cash Withdrawal";
//                card_amount.setText("₹" + transactionAmt);
                transactionAmount.setText("₹" + transactionAmt);
                trantypename.setText("Cash Withdrawal");
            } else if (transaction_type.equalsIgnoreCase("POS")) {
                card_amount.setText(transactionAmt);
                transactionAmount.setText(transactionAmt);

            } else if (isVoid) {
                transactionTypeCheck = "VOID";
                card_amount.setText("Balance Amount: Rs. " + balanceAmt);
            } else {
                trantypename.setText("Balance Enquiry");
                transactionAmount.setText("₹ 0.00");
                transactionTypeCheck = "Balance Enquiry";
//                card_amount.setText("Balance Amount: Rs. " + balanceAmt);
            }

            String transactionType = "";
            if (SdkConstants.transactionType.equalsIgnoreCase("0")) {
                transactionType = "BalanceEnquiry Failled!! ";
            } else {
                transactionType = "CashWithdraw Failled!! ";
            }

            String str = "";
            str = card_amount.getText().toString();

            if (Card_NUM != null && card_amount != null && !TID.isEmpty()) {
                String responseData = generateJsonData(transactionType, str, prefNum, Card_NUM, card_amount.getText().toString(), TID);
                SdkConstants.responseData = responseData;
                cardNumber.setText(Card_NUM);
                cardHolderName.setText(cardholdername);

            }

        } else {
            //Show Success
            //show download receipt button
            status_icon.setImageResource(R.drawable.pos_success);
            downloadReceiptBtn.setVisibility(View.VISIBLE);
            printReceiptBtn.setVisibility(View.VISIBLE);
            card_amount.setVisibility(View.VISIBLE);
            cardNumber.setText(Card_NUM);
            cardHolderName.setText(cardholdername);
            statusTxt = "Success";
            PAXScreen.showSuccess(manager);
            txnID.setText(TRANSACTION_ID);
            //  bank_name.setText(CARD_TYPE);
            if (transaction_type.equalsIgnoreCase("cash")) {
                transactionTypeCheck = "Cash Withdrawal";
                card_amount.setText("₹" + transactionAmt);
                transactionAmount.setText("₹" + transactionAmt);
                trantypename.setText("Cash Withdrawal");
                transactionStatus.setText("TRANSACTION SUCCESSFUL");
            } else if (isVoid) {
                transactionTypeCheck = "VOID";
                card_amount.setText(transactionAmt);
            } else {
                transactionTypeCheck = "Balance Enquiry";
                card_amount.setText("Success");
                transactionStatus.setText("Balance Amount: Rs. " + balanceAmt);
                transactionStatus.setTextColor(getApplicationContext().getResources().getColor(R.color.black));
                trantypename.setText("Balance Enquiry");
                transactionAmount.setText("₹ 0.00");

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


        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (SdkConstants.onFinishListener != null) {
                    SdkConstants.onFinishListener.onSDKFinish(statusTxt, SdkConstants.paramA
                            , card_amount.getText().toString());

                }
                finish();
            }
        });

      /*  backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent mainActivity = new Intent(TransactionStatusActivity.this, MainActivity.class);
                mainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            }
        });*/

        txndetailsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTransactionDetails(TransactionStatusActivity.this);
            }
        });

        downloadReceiptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checker.lacksPermissions(REQUIRED_PERMISSION)) {
                    PermissionsActivity.startActivityForResult(TransactionStatusActivity.this, PERMISSION_REQUEST_CODE, REQUIRED_PERMISSION);
                } else {
                    Date date = new Date();
                    long timeMilli = date.getTime();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        createPdf(FileUtils.commonDocumentDirPath("PDF") + String.valueOf(timeMilli)
                                + "Order_Receipt.pdf");
                    } else {
                        createPdf(FileUtils.getAppPath(mContext) + String.valueOf(timeMilli) + "Order_Receipt.pdf");
                    }
                }
            }
        });

        printReceiptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String deviceModel = android.os.Build.MODEL;
                if (deviceModel.equalsIgnoreCase("A910")) {

                    getPrintData(txnID.getText().toString(), date_time.getText().toString(),
                            prefNum, MID, TID, cardType, cardNumber.getText().toString(), transactionAmount.getText().toString());


                } else if (deviceModel.equalsIgnoreCase("WPOS-3") || deviceModel.contains("P5")) {
                    //start printing with wiseasy internal printer
                    new PrintThread().start();
                } else {
                    view = v;
                    printBluetooth(v);
                }
            }
        });

       /* mobileCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mobileEditLayout.setVisibility(View.VISIBLE);
                } else {
                    mobileEditLayout.setVisibility(GONE);
                }
            }
        });*/

//        editTextMobile.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//            }
//            @Override
//            public void afterTextChanged(Editable editable) {
//                if (editable.length() < 10) {
//                    editTextMobile.setError(getResources().getString(R.string.mobileerror));
//                }
//                if (editable.length() > 0) {
//                    editTextMobile.setError(null);
//                    String value = editable.toString();
//                    if (value.startsWith("0") || Util.isValidMobile(editTextMobile.getText().toString().trim()) == false) {
//                        editTextMobile.setError(getResources().getString(R.string.mobilevaliderror));
//                    }
//                }
//            }
//        });

//        sendButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (editTextMobile.getText() == null || editTextMobile.getText().toString().trim().matches("") || Util.isValidMobile(editTextMobile.getText().toString().trim()) == false) {
//                    editTextMobile.setError(getResources().getString(R.string.mobileerror));
//                } else {
//                    showLoader();
//                    mobileNumberSMS(cardNum);
//                }
//
//            }
//        });
    }

    private String replaceWithZero(String amount) {
        float finalAmount = Integer.parseInt(amount) / 100F;
        DecimalFormat formatter = new DecimalFormat("##,##,##,##0.00");
        return formatter.format(Double.parseDouble(String.valueOf(finalAmount)));
    }

    private String generateJsonData(String status, String statusDesc, String rrn, String cardno, String bal, String terminalId) {
        String jsonData = "";
        JSONObject obj = new JSONObject();
        try {
            obj.put("TransactionStatus", status);
            obj.put("StatusDescription", statusDesc);
            obj.put("RRN", rrn);
            obj.put("CardNumber", cardno);
            obj.put("Balance", bal);
            obj.put("TerminalID", terminalId);
            jsonData = obj.toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonData;
    }

    private void createPdf(String s) {
        filePath = s;
        createPdfGenericMethod(s);
    }

    /**
     * @param dest
     */
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
            document.setPageSize(PageSize.A3);
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
            document.add(rect);


            /*commit git test*/

            BaseColor mColorAccent = new BaseColor(0, 153, 204, 255);
            float mHeadingFontSize = 24.0f;
            float mValueFontSize = 26.0f;

            BaseFont urName = BaseFont.createFont("assets/fonts/brandon_medium.otf", "UTF-8", BaseFont.EMBEDDED);

            // LINE SEPARATOR
            LineSeparator lineSeparator = new LineSeparator();
            lineSeparator.setLineColor(new BaseColor(0, 0, 0, 68));

            Font mOrderDetailsTitleFont = new Font(urName, 36.0f, Font.NORMAL, BaseColor.BLACK);
            Chunk mOrderDetailsTitleChunk = new Chunk(shopName, mOrderDetailsTitleFont);
            Log.i("mOrderDetailsTitleChunk",mOrderDetailsTitleChunk.toString());
            Paragraph mOrderDetailsTitleParagraph = new Paragraph(mOrderDetailsTitleChunk);
            mOrderDetailsTitleParagraph.setAlignment(Element.ALIGN_CENTER);
            document.add(mOrderDetailsTitleParagraph);
            document.add(new Paragraph("\n"));
            Font mOrderShopTitleFont = new Font(urName, 25.0f, Font.NORMAL, BaseColor.BLACK);
            Chunk mOrderShopTitleChunk = new Chunk("Receipt", mOrderShopTitleFont);
            Paragraph mOrderShopTitleParagraph = new Paragraph(mOrderShopTitleChunk);
            mOrderShopTitleParagraph.setAlignment(Element.ALIGN_CENTER);
            document.add(mOrderShopTitleParagraph);
            Font mOrderDetailsTitleFont11;
            if (statusTxt.equalsIgnoreCase("FAILED")) {
                mOrderDetailsTitleFont11 = new Font(urName, 40.0f, Font.NORMAL, BaseColor.RED);

            } else {
                mOrderDetailsTitleFont11 = new Font(urName, 40.0f, Font.NORMAL, BaseColor.GREEN);
            }

            Chunk mOrderDetailsTitleChunk1 = new Chunk(statusTxt, mOrderDetailsTitleFont11);
            Paragraph mOrderDetailsTitleParagraph1 = new Paragraph(mOrderDetailsTitleChunk1);
            mOrderDetailsTitleParagraph1.setAlignment(Element.ALIGN_CENTER);
            document.add(mOrderDetailsTitleParagraph1);
            document.add(new Paragraph("\n"));

            Font mOrderDateFont = new Font(urName, mHeadingFontSize, Font.NORMAL, mColorAccent);
            Font mOrderDateValueFont = new Font(urName, mValueFontSize, Font.NORMAL, BaseColor.BLACK);

            Paragraph p = new Paragraph();
            p.add(new Chunk("Date/Time : ", mOrderDateFont));
            p.add(new Chunk(date_time.getText().toString().trim(), mOrderDateValueFont));
            document.add(p);
            document.add(new Paragraph("\n\n"));
            Paragraph p1 = new Paragraph();
            p1.add(new Chunk("Operation Performed : ", mOrderDateFont));
            p1.add(new Chunk("mATM", mOrderDateValueFont));
            document.add(p1);

            document.add(new Paragraph("\n\n"));


            Font mOrderDetailsFont = new Font(urName, 30.0f, Font.BOLD, mColorAccent);
            Chunk mOrderDetailsChunk = new Chunk("Transaction Details", mOrderDetailsFont);
            Paragraph mOrderDetailsParagraph = new Paragraph(mOrderDetailsChunk);
            mOrderDetailsParagraph.setAlignment(Element.ALIGN_CENTER);
            document.add(mOrderDetailsParagraph);
            document.add(new Paragraph("\n"));
            Font mOrderIdFont = new Font(urName, mValueFontSize, Font.NORMAL, BaseColor.BLACK);
            Chunk mOrderIdChunk = new Chunk("Txn ID: " + txnID.getText().toString(), mOrderIdFont);
            Paragraph mOrderTxnParagraph = new Paragraph(mOrderIdChunk);
            document.add(mOrderTxnParagraph);
            Chunk mOrderIdValueChunk = new Chunk("MID: " + MID, mOrderIdFont);
            Paragraph mOrderaadharParagraph = new Paragraph(mOrderIdValueChunk);
            document.add(mOrderaadharParagraph);
            Chunk mBankNameChunk = new Chunk("Terminal ID:  " + TID, mOrderIdFont);
            Paragraph mBankNameParagraph = new Paragraph(mBankNameChunk);
            document.add(mBankNameParagraph);
            Chunk mOrderrrnChunk = new Chunk("RRN: " + prefNum, mOrderIdFont);
            Paragraph mOrderrnParagraph = new Paragraph(mOrderrrnChunk);
            document.add(mOrderrnParagraph);
            Chunk mOrdertxnTypeChunk = new Chunk("Card No.: " + cardNumber.getText().toString(), mOrderIdFont);
            Paragraph mOrdertxnTypeParagraph = new Paragraph(mOrdertxnTypeChunk);
            document.add(mOrdertxnTypeParagraph);
            Chunk mOrderbalanceChunk = new Chunk("Balance Amount: " + balanceAmt, mOrderIdFont);
            Paragraph mOrderbalanceParagraph = new Paragraph(mOrderbalanceChunk);
            document.add(mOrderbalanceParagraph);
            Chunk mOrderbalanceChunk1 = new Chunk("Txn Type: " + trantypename.getText().toString(), mOrderIdFont);
            Paragraph mOrderbalanceParagraph1 = new Paragraph(mOrderbalanceChunk1);
            document.add(mOrderbalanceParagraph1);
            Chunk mOrdertxnAmtChunk = new Chunk("Txn Amount: " + transactionAmount.getText().toString(), mOrderIdFont);
            Paragraph mOrdertxnAmtParagraph = new Paragraph(mOrdertxnAmtChunk);
            document.add(mOrdertxnAmtParagraph);

            Font mOrderAcNameFont = new Font(urName, mHeadingFontSize, Font.NORMAL, mColorAccent);
            Chunk mOrderAcNameChunk = new Chunk("Thank You", mOrderAcNameFont);
            Paragraph mOrderAcNameParagraph = new Paragraph(mOrderAcNameChunk);
            mOrderAcNameParagraph.setAlignment(Element.ALIGN_RIGHT);
            document.add(mOrderAcNameParagraph);
            Font mOrderAcNameValueFont = new Font(urName, mValueFontSize, Font.NORMAL, BaseColor.BLACK);
            Chunk mOrderAcNameValueChunk = new Chunk(brandName, mOrderAcNameValueFont);
            Paragraph mOrderAcNameValueParagraph = new Paragraph(mOrderAcNameValueChunk);
            mOrderAcNameValueParagraph.setAlignment(Element.ALIGN_RIGHT);
            document.add(mOrderAcNameValueParagraph);

            document.close();

            Toast.makeText(mContext, "PDF saved in the internal storage", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(TransactionStatusActivity.this, PreviewPDFActivity.class);
            intent.putExtra("filePath", dest);
            startActivity(intent);

        } catch (IOException | DocumentException ie) {
            Log.e("createPdf: Error ", "" + ie.getLocalizedMessage());
        } catch (ActivityNotFoundException ae) {
            Toast.makeText(mContext, "No application found to open this file.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2296) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    createPdfGenericMethod(filePath);
                } else {
                    Toast.makeText(this, "Allow permission for storage access!", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (resultCode == PermissionsActivity.PERMISSIONS_GRANTED) {
            Toast.makeText(mContext, "Permission Granted to Save", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, "Permission not granted, Try again!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * @param cardNumber
     */
    private void mobileNumberSMS(String cardNumber) {
        String msgValue = "Thanks for visiting " + shopName+ ". Current balance for " + cardNumber + " account seeded with balance  is Rs " + balanceAmt + ". Dated " + date_time.getText().toString() + ".  Thanks ITPL";
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("user_name", SdkConstants.userNameFromCoreApp);
//            jsonObject.put("MobileNumber", editTextMobile.getText().toString());
            jsonObject.put("smsFor", "transaction");
            jsonObject.put("message", msgValue);

            AndroidNetworking.post("https://wallet-deduct-sms-vn3k2k7q7q-uc.a.run.app/")
                    .addJSONObjectBody(jsonObject)
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONObject obj = new JSONObject(response.toString());
                                String status = obj.getString("status");
                                String msg = obj.optString("message");
                                if (status.equalsIgnoreCase("0")) {

                                    JSONObject results = obj.getJSONObject("results");
                                    String statusMsg = results.getString("status");
                                    String message = results.getString("message");
                                    hideLoader();
                                    Toast.makeText(TransactionStatusActivity.this, "Message Sent Successfully . ", Toast.LENGTH_SHORT).show();

                                } else {

                                    hideLoader();
                                    Toast.makeText(TransactionStatusActivity.this, msg, Toast.LENGTH_SHORT).show();

                                }


                            } catch (JSONException e) {
                                hideLoader();
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            anError.getErrorBody();
                            hideLoader();
                            Toast.makeText(TransactionStatusActivity.this, "Message Error", Toast.LENGTH_SHORT).show();
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showLoader() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(TransactionStatusActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Please Wait..");
        }
        progressDialog.show();
    }

    /**
     *
     */
    private void hideLoader() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    /**
     * @param activity
     */
    private void showTransactionDetails(Activity activity) {
        try {
            final Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.transaction_matm_details_layout);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

            TextView mid_No = (TextView) dialog.findViewById(R.id.midNo);
            TextView rrnNo = (TextView) dialog.findViewById(R.id.rrnNo);
            TextView terminalId = (TextView) dialog.findViewById(R.id.terminalId);
            TextView txnType = (TextView) dialog.findViewById(R.id.txnType);
            TextView card_number = (TextView) dialog.findViewById(R.id.card_number);
            TextView card_transaction_amount = (TextView) dialog.findViewById(R.id.card_transaction_amount);
//            TextView balanceAmtId = (TextView) dialog.findViewById(R.id.balanceAmtID);
            if (!MID.equals("null")) {
                mid_No.setText(MID);
            } else {
                mid_No.setText("NA");
            }

            rrnNo.setText(prefNum);
            terminalId.setText(TID);
            card_number.setText(cardNumber.getText().toString());

            if (transaction_type.equalsIgnoreCase("cash")) {
                card_transaction_amount.setText((transactionAmount.getText().toString()));
                txnType.setText(trantypename.getText().toString());
                transactionTypeCheck = txnType.getText().toString();
            } else {
                txnType.setText("Balance Enquiry");
                transactionAmt = "N/A";
                card_transaction_amount.setText((transactionAmount.getText().toString()));
                transactionTypeCheck = txnType.getText().toString();
            }

            Button dialogBtn_close = (Button) dialog.findViewById(R.id.close_Btn);
            dialogBtn_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();

                }
            });

            dialog.show();

        } catch (Exception e) {
            e.printStackTrace();
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
                GetConnectToPrinter.getInstance().printStr(shopName, null);
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
                GetConnectToPrinter.getInstance().printStr(getString(R.string.transaction_type_txt) + trantypename.getText().toString(), null);
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
//                String brandName = SdkConstants.BRAND_NAME;
                if (brandName != null && brandName.length() > 0)
                    GetConnectToPrinter.getInstance().printStr(brandName, null);
                GetConnectToPrinter.getInstance().step(Integer.parseInt("100"));

                GetConnectToPrinter.getInstance().start();
            }
        }).start();
    }

    private void getConnectToPrinter(View view) {
        registerForContextMenu(printReceiptBtn);
        if (bluetoothAdapter == null) {
            Toast.makeText(TransactionStatusActivity.this, "Bluetooth NOT supported", Toast.LENGTH_SHORT).show();
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
                    callBluetoothFunction(txnID.getText().toString(), date_time.getText().toString(), prefNum, MID, TID, cardType, cardNumber.getText().toString(), transactionAmount.getText().toString(), view);

                }
            } else {
                GetPosConnectedPrinter.aemPrinter = null;
                Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(turnOn, 0);
            }
        }
    }

    public void showAlert(String alertMsg) {
        android.app.AlertDialog.Builder alertBox = new android.app.AlertDialog.Builder(TransactionStatusActivity.this);
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


    private void callBluetoothFunction(final String txnId, final String date, final String reffNo, final String mid, final String terminalId, final String type, final String cardNumber, final String transactionAmt, View view) {
        try {
            m_AemPrinter.setFontType(AEMPrinter.DOUBLE_HEIGHT);
            m_AemPrinter.setFontType(AEMPrinter.TEXT_ALIGNMENT_CENTER);
            m_AemPrinter.setFontType(AEMPrinter.FONT_NORMAL);
            m_AemPrinter.setFontType(AEMPrinter.FONT_002);
            m_AemPrinter.POS_FontThreeInchCENTER();
            m_AemPrinter.print(shopName);
            m_AemPrinter.print("\n");
            m_AemPrinter.setFontType(AEMPrinter.DOUBLE_HEIGHT);
            m_AemPrinter.setFontType(AEMPrinter.TEXT_ALIGNMENT_CENTER);
            m_AemPrinter.print("-----Transaction Report-----\n");
            m_AemPrinter.POS_FontThreeInchCENTER();
            m_AemPrinter.print(statusTxt);
//            m_AemPrinter.print("\n\n");
//            m_AemPrinter.print(txnId);
            m_AemPrinter.print("\n");
            m_AemPrinter.print("Txn ID: " + txnId);
            m_AemPrinter.print("\n");
            m_AemPrinter.print("Date/Time: " + date);
            m_AemPrinter.print("\n");
            m_AemPrinter.print("RRN: " + reffNo);
            m_AemPrinter.print("\n");
            m_AemPrinter.print("Mid : " + mid);
            m_AemPrinter.print("\n");
            m_AemPrinter.print("Terminal ID: " + terminalId);
            m_AemPrinter.print("\n");
            m_AemPrinter.print("Card No.: " + cardNumber);
            m_AemPrinter.print("\n");
            m_AemPrinter.print("Balance Amount : " + balanceAmt);
            m_AemPrinter.print("\n");
            m_AemPrinter.print("Txn Type : " + trantypename.getText().toString());
            m_AemPrinter.print("\n");
            m_AemPrinter.print("Txn Amount : " + transactionAmt);
            m_AemPrinter.print("\n\n");
            m_AemPrinter.setFontType(AEMPrinter.FONT_002);
            m_AemPrinter.POS_FontThreeInchRIGHT();
            m_AemPrinter.print("Thank you \n");
            m_AemPrinter.POS_FontThreeInchRIGHT();
            m_AemPrinter.setFontType(AEMPrinter.TEXT_ALIGNMENT_RIGHT);
            m_AemPrinter.print(brandName);
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

    public String printerStatus() throws IOException {
        String data = new String(printerStatus);
        m_AemPrinter.print(data);
        return data;
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
            Toast.makeText(TransactionStatusActivity.this, "Connected with " + printerName, Toast.LENGTH_SHORT).show();

            //            String data=new String(batteryStatusCommand);
//            m_AemPrinter.print(data);
            //  m_cardReader.readMSR();


        } catch (IOException e) {
            if (e.getMessage().contains("Service discovery failed")) {
                Toast.makeText(TransactionStatusActivity.this, "Not Connected\n" + printerName + " is unreachable or off otherwise it is connected with other device", Toast.LENGTH_SHORT).show();
            } else if (e.getMessage().contains("Device or resource busy")) {
                Toast.makeText(TransactionStatusActivity.this, "the device is already connected", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(TransactionStatusActivity.this, "Unable to connect", Toast.LENGTH_SHORT).show();
            }
        }
        return true;
    }

    CardReader.MSRCardData creditDetails;

    public void onScanMSR(final String buffer, CardReader.CARD_TRACK cardTrack) {
        cardTrackType = cardTrack;
        creditData = buffer;
        TransactionStatusActivity.this.runOnUiThread(new Runnable() {
            public void run() {
//                editText.setText(buffer.toString());
            }
        });
    }

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

        TransactionStatusActivity.this.runOnUiThread(new Runnable() {
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

    public void onDiscoveryComplete(ArrayList<String> aemPrinterList) {
        printerList = aemPrinterList;
        for (int i = 0; i < aemPrinterList.size(); i++) {
            String Device_Name = aemPrinterList.get(i);
            String status = m_AemScrybeDevice.pairPrinter(Device_Name);
            Log.e("STATUS", status);
        }
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
            TransactionStatusActivity.this.runOnUiThread(new Runnable() {
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
            TransactionStatusActivity.this.runOnUiThread(new Runnable() {
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

    //for wiseasy
    private class PrintThread extends Thread {
        @Override
        public void run() {
            mPrinter = new Printer(TransactionStatusActivity.this);
            try {
                mPrinter.setPrintType(0);//Printer type 0 means it's an internal printer
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            checkPrinterStatus();
        }
    }

    /**
     * first check the printer status i.e if paper is present or not,or check different condition
     * <p>
     * if printer status is OK then start printing
     */
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

    private void showLog(String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(TransactionStatusActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
        Log.e(TAG, "Printer status: " + msg);
    }

    /**
     * First initialize the printer and clear if any catch data is present
     * <p>
     * After initialization of printer then start printing
     */
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
            Log.e(TAG, "printReceipt: set density low 1");
            mPrinter.setGrayLevel(1);
            result = mPrinter.printStringExt(shopName, 0, 0f, 2.0f, Printer.Font.SANS_SERIF, 28, Printer.Align.CENTER, true, false, true);
            result = mPrinter.printString("Transaction Report", 24, Printer.Align.CENTER, true, false);
            if (statusTxt.equalsIgnoreCase("Success")) {
                result = mPrinter.printString("Success", 24, Printer.Align.CENTER, true, false);
            } else {
                result = mPrinter.printString("Failure", 24, Printer.Align.CENTER, true, false);
            }
            result = mPrinter.printString("", 15, Printer.Align.CENTER, true, false);
            result = mPrinter.printString("Txn Id :" + txnID.getText().toString(), 20, Printer.Align.LEFT, false, false);
            result = mPrinter.printString("Date/Time : " + date_time.getText().toString(), 20, Printer.Align.LEFT, false, false);
            result = mPrinter.printString("RRN :" + prefNum, 20, Printer.Align.LEFT, false, false);
            result = mPrinter.printString("MID :" + MID, 20, Printer.Align.LEFT, false, false);
            result = mPrinter.printString("Terminal Id : " + TID, 20, Printer.Align.LEFT, false, false);
            result = mPrinter.printString("Card Number : " + cardNumber.getText().toString(), 20, Printer.Align.LEFT, false, false);
            result = mPrinter.printString("Balance Amount :" + balanceAmt, 20, Printer.Align.LEFT, false, false);
            if (transaction_type.equalsIgnoreCase("cash")) {
                result = mPrinter.printString("Txn Type : Cash Withdrawal", 20, Printer.Align.LEFT, false, false);
                result = mPrinter.printString("Txn Amount :" + transactionAmount.getText().toString(), 20, Printer.Align.LEFT, false, false);

            } else {
                result = mPrinter.printString("Txn Type : Balance Enquiry", 20, Printer.Align.LEFT, false, false);
                result = mPrinter.printString("Txn Amount :" + transactionAmount.getText().toString(), 20, Printer.Align.LEFT, false, false);
            }
            result = mPrinter.printStringExt("Thank You !", 0, 0f, 2.0f, Printer.Font.SANS_SERIF, 22, Printer.Align.RIGHT, true, true, false);
            result = mPrinter.printStringExt(brandName + "\n\n\n", 0, 0f, 2.0f, Printer.Font.SANS_SERIF, 20, Printer.Align.RIGHT, false, true, false);

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

    /**
     * in between printing if any error occur then this method will show the toast
     */
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
