import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.*;

public class gitDiff {
    public static void diff(){

        String indexPath = gitInit.idPath;
        File file = new File(indexPath);
        Map map = new HashMap<>();
        String filename = null;
        String fileSHA = null;
        //diff命令是index中的文件（暂存区的文件）与工作区的文件针对文件内容进行比较
        if(!file.exists()) System.out.println("警告：Index文件丢失！无法查看文件状态");
        else {//在确定有index的文件的情况下再进行比较
            ObjectInputStream ois = null;

            try {
                ois = new ObjectInputStream(new FileInputStream(file));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                map = (HashMap) ois.readObject();
            } catch (IOException e) {
                throw new RuntimeException(e);

            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            try {
                ois.close();
            } catch (IOException e) {
                throw new RuntimeException(e);

            }
            //对index中的HashMap进行遍历
            //HashMap中的每一个key是文件名字，对应的value是文件的哈希值也是blob文件的命名
            Set keyset = map.keySet();
            for (Object key : keyset){
                filename = (String) key;
                fileSHA = (String) map.get(filename);
                String filePath = gitInit.workPath + File.separator + filename;
                String blobpath =gitInit.obPath + File.separator + fileSHA + ".txt";
                File file1 = new File(filePath);
                File file2 = new File(blobpath);
                if(file1.exists()){
                    if(file2.exists()){
                        String Last_content = null;
                        try {
                            try {
                                Last_content = gitAdd.fileContentOb(blobpath);
                            } catch (ClassNotFoundException e) {
                                throw new RuntimeException(e);
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        String New_content = null;
                        try {
                            New_content = gitAdd.fileContent(filePath);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        if(!Last_content.equals(New_content)){
                            System.out.println("diff " + filename);
                            System.out.println("index " + fileSHA);
                            System.out.println("-" + Last_content);
                            System.out.println("+" + New_content);
                        }

                    }else System.out.println("警告！暂存区文件对应blob文件丢失");

                }else System.out.println("警告！工作区文件丢失");
            }

        }
    }

}
