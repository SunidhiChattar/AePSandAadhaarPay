package com.matm.matmsdk.posverification;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validate {

    static int[][] d  = new int[][]
            {
                    {0, 1, 2, 3, 4, 5, 6, 7, 8, 9},
                    {1, 2, 3, 4, 0, 6, 7, 8, 9, 5},
                    {2, 3, 4, 0, 1, 7, 8, 9, 5, 6},
                    {3, 4, 0, 1, 2, 8, 9, 5, 6, 7},
                    {4, 0, 1, 2, 3, 9, 5, 6, 7, 8},
                    {5, 9, 8, 7, 6, 0, 4, 3, 2, 1},
                    {6, 5, 9, 8, 7, 1, 0, 4, 3, 2},
                    {7, 6, 5, 9, 8, 2, 1, 0, 4, 3},
                    {8, 7, 6, 5, 9, 3, 2, 1, 0, 4},
                    {9, 8, 7, 6, 5, 4, 3, 2, 1, 0}
            };


    static int[][] p = new int[][]
            {
                    {0, 1, 2, 3, 4, 5, 6, 7, 8, 9},
                    {1, 5, 7, 6, 2, 8, 3, 0, 9, 4},
                    {5, 8, 0, 3, 7, 9, 6, 1, 4, 2},
                    {8, 9, 1, 6, 0, 4, 3, 5, 2, 7},
                    {9, 4, 5, 3, 1, 2, 6, 8, 7, 0},
                    {4, 2, 8, 6, 5, 7, 3, 9, 0, 1},
                    {2, 7, 9, 3, 8, 0, 6, 4, 1, 5},
                    {7, 0, 4, 6, 9, 1, 3, 2, 5, 8}
            };

    public static boolean isValidPhone(String ph) {
        boolean check = false;
        if (ph.contains("+")) {
            ph = ph.replace("+", "");
        }
        if (ph.contains(" ")) {
            ph = ph.replace(" ", "");
        }
        if (ph.length() >= 10 && ph.length() <= 12) {
            check = android.util.Patterns.PHONE.matcher(ph).matches();
        }
        return check;

    }

    public static boolean isPassword(String password){
        Matcher matcher = Pattern.compile("((?=.*[a-z])(?=.*\\d)(?=.*[A-Z])(?=.*[@#$%]).{4,20})").matcher(password);
        return matcher.matches();
    }

    public static String getValidPhone(String phone) {
        String number = phone;
        if (number.contains("+")) {
            number = number.replace("+", "");
        }
        if (number.contains(" ")) {
            number = number.replace(" ", "");
        }
        if (phone.length() > 10) {
            int len = number.length();
            number = number.substring(len - 10);
        }
        return number;
    }

    //6370991241

    public static boolean isAddress(String address) {
        String regex = "^[/:#.0-9a-zA-Z\\s,-]+$";
//        String regex = "\\d+\\s+([a-zA-Z]+|[a-zA-Z]+\\s[a-zA-Z]+)";
        return address.matches(regex);
    }

    public static boolean isPhone(String p) {
        String regex = "^[6-9][0-9]{9}$";
        return p.matches(regex);
    }

    public static boolean isValidName(String name) {
        String regx = "^[\\p{L} .'-]+$";
        Pattern pattern = Pattern.compile(regx, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(name);
        return matcher.find();
    }

    public static boolean isValidIfscCode(String str)
    {
        // Regex to check valid
        // GST (Goods and Services Tax) number
        String regex="^[A-Z]{4}0[A-Z0-9]{6}$";
        // Compile the ReGex
        Pattern p = Pattern.compile(regex);
        if (str == null)
        {
            return false;
        }

        Matcher m = p.matcher(str);
        return m.matches();
    }



    public static boolean isValidMail(String email) {
        String regex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        return email.matches(regex);
    }

    public static boolean isValidPan(String pan) {
        String regex = "[A-Z]{5}[0-9]{4}[A-Z]{1}";

        return pan.matches(regex);
    }

    public static boolean isValidGSTNo(String str)
    {
        // Regex to check valid
        // GST (Goods and Services Tax) number
        String regex = "^[0-9]{2}[A-Z]{5}[0-9]{4}"
                + "[A-Z]{1}[1-9A-Z]{1}"
                + "Z[0-9A-Z]{1}$";

        // Compile the ReGex
        Pattern p = Pattern.compile(regex);

        if (str == null)
        {
            return false;
        }

        Matcher m = p.matcher(str);

        return m.matches();
    }


    public static void showAlert(Context context, String statusDesc) {
        try {
            AlertDialog.Builder alertbuilderupdate;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                alertbuilderupdate = new AlertDialog.Builder(context, android.R.style.Theme_Material_Light_Dialog_Alert);
            } else {
                alertbuilderupdate = new AlertDialog.Builder(context);
            }
            alertbuilderupdate.setCancelable(false);
            // String message = "Session is already running !!! Please login after sometimes.";
            alertbuilderupdate.setTitle("Alert!")
                    .setMessage(statusDesc)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                            dialog.dismiss();
                            //finish();
                        }
                    });

//                    .show();
            AlertDialog alert11 = alertbuilderupdate.create();
            alert11.show();
        } catch (Exception e) {

        }
    }


    public static boolean validateAadharNumber(String aadharNumber){
        Pattern aadharPattern = Pattern.compile("\\d{12}");
        boolean isValidAadhar = aadharPattern.matcher(aadharNumber).matches();
        if(isValidAadhar){
            isValidAadhar = validateVerhoeff(aadharNumber);
        }
        return isValidAadhar;
    }

    public static boolean validateVerhoeff(String num){

        int c = 0;
        int[] myArray = StringToReversedIntArray(num);

        for (int i = 0; i < myArray.length; i++)
        {
            c = d[c][p[(i % 8)][myArray[i]]];
        }

        return (c == 0);
    }
    private static int[] StringToReversedIntArray(String num){

        int[] myArray = new int[num.length()];

        for(int i = 0; i < num.length(); i++)
        {
            myArray[i] = Integer.parseInt(num.substring(i, i + 1));
        }

        myArray = Reverse(myArray);

        return myArray;

    }
    private static int[] Reverse(int[] myArray)
    {
        int[] reversed = new int[myArray.length];

        for(int i = 0; i < myArray.length ; i++)
        {
            reversed[i] = myArray[myArray.length - (i + 1)];
        }

        return reversed;
    }

    public static String encode64(String s){
        String e = "";
        try {
            byte[] data = s.getBytes("UTF-8");
            e = Base64.encodeToString(data, Base64.DEFAULT);
        } catch (UnsupportedEncodingException unsupportedEncodingException) {
            unsupportedEncodingException.printStackTrace();
        }
        return e;
    }
    public static String decode64(String s){
        String d = "";
        try {
            byte[] data = Base64.decode(s, Base64.DEFAULT);
            d = new String(data, "UTF-8");;
        } catch (UnsupportedEncodingException unsupportedEncodingException) {
            unsupportedEncodingException.printStackTrace();
        }
        return d;
    }

}
