package org.example;

import com.opencsv.CSVWriter;
import javafx.util.Pair;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class MatrixToCSV {

    public void convert(Pair<List<String>, List<List<Integer>>> info) throws IOException {
        for (int k = 0; k < info.getKey().size() - 2; k++) {
            File file = new File(info.getKey().get(k+2));
            FileWriter outputfile = new FileWriter(file);
            CSVWriter writer = new CSVWriter(outputfile);
            String[] header = {"Неделя", info.getKey().get(k + 2)};
            writer.writeNext(header);
            for (int i = 0; i < info.getValue().size(); i++) {
                Integer x = (info.getValue().get(i).get(0) - 2021) * 52 + info.getValue().get(i).get(1) - 51;
                String[] data = {x.toString(), info.getValue().get(i).get(k + 2).toString()};
                writer.writeNext(data);
            }
            writer.close();
        }
    }
}
