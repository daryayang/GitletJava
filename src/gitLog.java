import java.io.*;
import java.util.HashMap;

public class gitLog {

    public static void commitLog() throws IOException, ClassNotFoundException  {
        String commitid = null;
        //从HEAD文件中读到最近一次的commit id，若HEAD为空打印提示信息。
        File file = new File(gitInit.hdPath);
        BufferedReader br = new BufferedReader(new FileReader(file));
        //创建bufferreader流从HEAD文件中读出，并将结果写到String中
        //因为Head文件中只存储这一次的commit ID，所以读一行就行
           commitid = br.readLine();
           br.close();
        // 结果是null，那就提示
        if(commitid == "null"){
            System.out.println("警告！HEAD文件中没有commit id！");
        }

            while(commitid != null){
                String logcommitPath = gitInit.obPath + File.separator + commitid + ".txt";
                File file1 = new File(logcommitPath);
                if(file1.exists()){
                HashMap hashMap;
                ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(logcommitPath));
                hashMap = (HashMap) objectInputStream.readObject();
                System.out.println(commitid + " " + hashMap.get("message") + " " + hashMap.get("time"));
                objectInputStream.close();
                commitid = (String) hashMap.get("precommit");
               }else break;
               }
            System.out.println("commit记录已经打印完毕");
            System.out.println("该命令抛出了IOException, ClassNotFoundException");
    }}











