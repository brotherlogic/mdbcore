package uk.co.brotherlogic.mdb.cd;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import uk.co.brotherlogic.mdb.record.Record;

public class CDFileBuilder {

	public void buildFile(Record rec, File singleDirectory) throws IOException {

		PrintStream ps = new PrintStream(new File(singleDirectory, "CDOut.txt"));
		ps.print(rec.getNumber());
		ps.close();
	}

	public void buildFile(Record rec, List<File> orderedDirectories)
			throws IOException {
		int startCount = 1;
		int endCount = 0;

		for (File f : orderedDirectories) {
			// Subtract 2 to get the full count (excluding '.' and '..')
			int trackCount = f.listFiles().length - 2;
			endCount += trackCount;

			PrintStream ps = new PrintStream(new File(f, "CDOut.txt"));
			ps.println(rec.getNumber() + ":" + startCount + ":" + endCount);
			ps.close();

			startCount = endCount;
		}
	}

}
