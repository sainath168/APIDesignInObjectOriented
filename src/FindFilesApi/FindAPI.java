package FindFilesApi;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class FindAPI {
    public static void main(String[] args) {

        //create some data with extension
        File file1 = new File();
        file1.setName("sai.txt");

        File file2 = new File();
        file2.setName("abc.xml");

        Directory dir1 = new Directory();
        dir1.addEntry(file1);

        Directory dir2 = new Directory();
        dir2.addEntry(file2);
        dir2.addEntry(dir1);


        SearchParams params = new SearchParams();
        params.setExtension("xml");

        FileSearcher fileSearcher = new FileSearcher();
        List<File> files = fileSearcher.search(params, dir2);

        for (File res : files) {
            System.out.println("file full name - " + res.getName() + " and ext is - " + res.getExtension(res));
        }
    }

}

class FileSearcher {
    public List<File> search(SearchParams params, Directory dir) {

        List<File> result = new ArrayList<>();

        FileFilter fileFilter = new FileFilter();

        Queue<Directory> directoryQueue = new LinkedList<>();

        directoryQueue.add(dir);

        while (!directoryQueue.isEmpty()) {
            Directory entry = directoryQueue.poll();

            for (Entry fileOrDir : entry.getEntries()) {
                if (fileOrDir.isDirectory()) {
                    directoryQueue.add((Directory) fileOrDir);
                } else {
                    File file = (File) fileOrDir;
                    if (fileFilter.isValid(params, file)) {
                     result.add(file);
                    }
                }
            }
        }

        return result;
    }
}

interface Entry {
    String getName();
    void setName(String name);
    boolean isDirectory();
}

class File implements Entry {

    private String name;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    String getExtension(File file) {
        return this.getName().substring(file.getName().indexOf('.') + 1);
    }

    @Override
    public boolean isDirectory() {
        return false;
    }
}

class Directory implements Entry {

    private String name;
    private List<Entry> entries = null;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public List<Entry> getEntries() {
        return entries;
    }

    public void addEntry(Entry entry) {
        if (entries == null) {
            entries = new ArrayList<>();
        }
        this.entries.add(entry);
    }

    @Override
    public boolean isDirectory() {
        return true;
    }
}

class SearchParams {
    private String name;
    private String extension;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }
}

interface IFilter {
    boolean isValid(SearchParams params, File file);
}

class FileFilter implements IFilter {

    List<IFilter> filters = new ArrayList<>();


    @Override
    public boolean isValid(SearchParams params, File file) {
        this.filters.add(new NameFilter());
        this.filters.add(new ExtensionFilter());

        for (IFilter filter : filters) {
            if (!filter.isValid(params, file)) {
                return false;
            }
        }

        return true;
    }
}

class NameFilter implements IFilter {
    @Override
    public boolean isValid(SearchParams params, File file) {
        if (params.getName() == null) {
            return true;
        }
        if (file.getName().equals(params.getName())) {
            return true;
        }

        return false;
    }
}

class ExtensionFilter implements IFilter {
    @Override
    public boolean isValid(SearchParams params, File file) {
        if (params.getExtension() == null) {
            return true;
        }

        if (file.getExtension(file).equals(params.getExtension())) {
            return true;
        }

        return false;
    }
}