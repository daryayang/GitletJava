import java.io.*;
import java.util.*;

public class gitCheckout {

         //checkout – [filename]
    /*
      当执行完commit后，工作目录中存在1.txt，我想修改它的内容或者直接删除，
      更改完或者删除后，我后悔了，就可以调用这第一种命令将文件recover回来。
      所以思路是这样的，如果当前Commit追踪的文件包含filename，
      则将其写入到工作目录：如果同名文件存在，overwrite它，如果不存在，直接写入。
     */
    public static void checkout(String filename) throws IOException, ClassNotFoundException {
        String filePath = gitInit.workPath + File.separator + filename;
        String latest_commitid = gitAdd.fileContent(gitInit.hdPath);
        if(latest_commitid == null) System.out.println("没有当前commit记录");
        else{
            String commitfile = gitInit.obPath + File.separator + latest_commitid + ".txt";
            File file = new File(commitfile);
            if(!file.exists()) System.out.println("警告！当前commit文件丢失");
            else {
                Map commitmap = new HashMap<>();
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
                commitmap = (HashMap) ois.readObject();
                ois.close();
                String treeid = (String) commitmap.get("treeid");
                String treePath = gitInit.obPath + File.separator + treeid + ".txt";
                File file1 = new File(treePath);
                if(!file1.exists()) System.out.println("警告！当前tree文件丢失");
                else {
                    Map treemap = new HashMap<>();
                    ObjectInputStream ois2 = new ObjectInputStream(new FileInputStream(file1));
                    treemap = (HashMap)ois2.readObject();
                    ois2.close();
                    if(treemap.containsKey(filename)){
                    String blobid = (String)treemap.get(filename);
                    String blobPath = gitInit.obPath + File.separator + blobid +".txt";
                    File file2 = new File(blobPath);
                    if(!file2.exists()) System.out.println("警告！您要查看的文件对应的blob文件丢失");
                    else{
                        String content = gitAdd.fileContentOb(blobPath);
                        File file3 = new File(filePath);
                        if(!file3.exists())file3.createNewFile();
                        BufferedWriter bos = new BufferedWriter(new FileWriter(file3));
                        bos.write(content);
                        bos.close();
                        System.out.println("已经将" + filename + "按照上一次commit状态进行修改");
                    }
                    }else System.out.println("异常：上一次commit的文件中不包括该文件");
                }
            }
        }
    }
}
