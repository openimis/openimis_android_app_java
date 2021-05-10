//Copyright (c) 2016-%CurrentYear% Swiss Agency for Development and Cooperation (SDC)
//
//The program users must agree to the following terms:
//
//Copyright notices
//This program is free software: you can redistribute it and/or modify it under the terms of the GNU AGPL v3 License as published by the 
//Free Software Foundation, version 3 of the License.
//This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of 
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU AGPL v3 License for more details www.gnu.org.
//
//Disclaimer of Warranty
//There is no warranty for the program, to the extent permitted by applicable law; except when otherwise stated in writing the copyright 
//holders and/or other parties provide the program "as is" without warranty of any kind, either expressed or implied, including, but not 
//limited to, the implied warranties of merchantability and fitness for a particular purpose. The entire risk as to the quality and 
//performance of the program is with you. Should the program prove defective, you assume the cost of all necessary servicing, repair or correction.
//
//Limitation of Liability 
//In no event unless required by applicable law or agreed to in writing will any copyright holder, or any other party who modifies and/or 
//conveys the program as permitted above, be liable to you for damages, including any general, special, incidental or consequential damages 
//arising out of the use or inability to use the program (including but not limited to loss of data or data being rendered inaccurate or losses 
//sustained by you or third parties or a failure of the program to operate with any other programs), even if such holder or other party has been 
//advised of the possibility of such damages.
//
//In case of dispute arising out or in relation to the use of the program, it is subject to the public law of Switzerland. The place of jurisdiction is Berne.

package org.openimis.imispolicies;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.openimis.imispolicies.BuildConfig.APP_DIR;

public class Global extends Application {
    private static Global GlobalContext;

    private String OfficerCode;
    private String OfficerName;
    private int UserId;
    private int OfficerId;

    private String ImageFolder;

    private Token JWTToken;

    private String MainDirectory;
    private String AppDirectory;
    private Map<String, String> SubDirectories = new HashMap<>();

    private List<String> ProtectedDirectories = Arrays.asList("Authentications","Database");

    public static Global getGlobal() {
        return GlobalContext;
    }

    public static Context getContext() {
        return GlobalContext.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        GlobalContext = this;
    }

    public Token getJWTToken() {
        return JWTToken;
    }

    public void setJWTToken(Token token) {
        JWTToken = token;
    }

    public String getOfficerCode() {
        return OfficerCode;
    }

    public void setOfficerCode(String officerCode) {
        OfficerCode = officerCode;
    }

    public int getUserId() {
        return UserId;
    }

    public void setUserId(int userId) {
        UserId = userId;
    }

    public int getOfficerId() {
        return OfficerId;
    }

    public void setOfficerId(int officerId) {
        OfficerId = officerId;
    }

    public String getImageFolder() {
        return ImageFolder;
    }

    public void setImageFolder(String imageFolder) {
        ImageFolder = imageFolder;
    }

    private String CurrentUrl;

    public String getCurrentUrl() {
        return CurrentUrl;
    }

    public void setCurrentUrl(String currentUrl) {
        CurrentUrl = currentUrl;
    }

    public String getOfficerName() {
        return OfficerName;
    }

    public void setOfficerName(String officerName) {
        OfficerName = officerName;
    }

    private String createOrCheckDirectory(String path) {
        File dir = new File(path);

        if (dir.exists() || dir.mkdir()) {
            return path;
        } else {
            return "";
        }
    }

    public String getMainDirectory() {
        if (MainDirectory == null) {
            String documentsDir = createOrCheckDirectory(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString());
            MainDirectory = createOrCheckDirectory(documentsDir + File.separator + APP_DIR);

            if ("".equals(documentsDir) || "".equals(MainDirectory)) {
                Log.w("DIRS", "Main directory could not be created");
            }
        }
        return MainDirectory;
    }

    public String getAppDirectory() {
        if (AppDirectory == null) {
            AppDirectory = createOrCheckDirectory(getApplicationInfo().dataDir);

            if ("".equals(AppDirectory)) {
                Log.w("DIRS", "App directory could not be created");
            }
        }
        return AppDirectory;
    }

    public String getSubdirectory(String subdirectory) {
        if (!SubDirectories.containsKey(subdirectory)) {
            String directory;

            if (ProtectedDirectories.contains(subdirectory)) {
                directory = getAppDirectory();
            } else {
                directory = getMainDirectory();
            }

            String subDirPath = createOrCheckDirectory(directory + File.separator + subdirectory);

            if ("".equals(subDirPath)) {
                Log.w("DIRS", subdirectory + " directory could not be created");
                return null;
            } else {
                SubDirectories.put(subdirectory, subDirPath);
            }
        }
        return SubDirectories.get(subdirectory);
    }

    public String getFileText(String path)
    {
        String line;
        StringBuilder stringBuilder = new StringBuilder();
        File file = new File(path);
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }
}
