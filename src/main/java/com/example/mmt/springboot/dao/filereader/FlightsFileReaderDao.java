package com.example.mmt.springboot.dao.filereader;

import com.example.mmt.springboot.dao.BaseDao;
import com.example.mmt.springboot.domain.transport.Flight;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

// this will read the data from flights files
public class FlightsFileReaderDao implements BaseDao {

    private static Function<String, Flight> mapToItem = (line) -> {

        String[] p = line.split(",");// a CSV has comma separated lines

        Flight flight = new Flight(p[0],p[1],p[2],p[3],p[4]);

        return flight;
    };

    public List<Flight> processInputFile(String inputFilePath) {

        List<Flight> inputList = new ArrayList<Flight>();

        try{

            File inputF = new File(inputFilePath);
            InputStream inputFS = new FileInputStream(inputF);
            BufferedReader br = new BufferedReader(new InputStreamReader(inputFS));

            // skip the header of the csv
            inputList = br.lines().map(mapToItem).collect(Collectors.toList());
            br.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return inputList ;
    }
}
