package com.javier.positiontracker.io;

import android.content.Context;
import android.location.Location;
import android.os.Environment;

import com.javier.positiontracker.databases.PositionTrackerDataSource;
import com.javier.positiontracker.model.LocationAddress;
import com.javier.positiontracker.model.UserLocation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Created by javie on 3/13/2017.
 */

public class FileManager {

    private File mStorage;

    public FileManager(File file) {

        mStorage = file;
    }

    public boolean createDirectory(String directory) {

        File documentsDir = new File(mStorage + "/" + directory);

        return documentsDir.exists() || documentsDir.mkdir();
    }

    public File createFile(String directory, String fileName, List<LocationAddress> data) throws IOException {

        File file = new File(mStorage + "/" + directory + "/" + fileName);
        FileWriter writer;
        try {

            writer = new FileWriter(file);

            for(LocationAddress location : data) {

                writer.append(location.getFullAddress());
            }

            writer.close();
        }
        catch (IOException e) {

            throw new IOException();
        }

        return file;
    }
}
