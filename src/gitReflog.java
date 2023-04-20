import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class gitReflog {
    public static void Reflog() {
        //reflog操作用于记录HEAD文件内容的每次变化情况
        //因此commit命令以及reset命令对于HEAD文件的修改都要记录
        //reflog命令的输出是commitid + 第i次head记录 + message
        //建一个list用于接收从HEAD_B文件中读取的list
        List list = new ArrayList<>();

        //用io流将head_B文件中的list读取出来
        ObjectInputStream objectInputStream = null;
        try {
            objectInputStream = new ObjectInputStream(new FileInputStream(gitInit.hdPathB));
        } catch (IOException e) {
            System.out.println("异常：" + e.getMessage());
        }
        try {
            list = (List) objectInputStream.readObject();
        } catch (IOException e) {
            System.out.println("异常：" + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("异常：" + e.getMessage());
        }
        try {
            objectInputStream.close();
        } catch (IOException e) {
            System.out.println("异常：" + e.getMessage());
        }

        //遍历整个list输出
        for(int i = 0; i < list.size(); i++){
            String commitid = (String) list.get(i);
            System.out.println(commitid + " " + "HEAD{" + i +"}"
                                 + " " + message(commitid)  );
        }

    }
    //写一个方法通过commitid获取到对应的message
    public static String message(String commitid)  {

            //初始化message方便返回
            String message = null;
            String commitPath = gitInit.obPath + File.separator + commitid + ".txt";
            File file1 = new File(commitPath);
            if(file1.exists()){
                HashMap hashMap = new HashMap<>();
                ObjectInputStream objectInputStream = null;
                try {
                    objectInputStream = new ObjectInputStream(new FileInputStream(commitPath));
                } catch (IOException e) {
                   System.out.println("异常：" + e.getMessage());
                }
                try {
                    hashMap = (HashMap) objectInputStream.readObject();
                } catch (IOException e) {
                    System.out.println("异常：" + e.getMessage());
                } catch (ClassNotFoundException e) {
                    System.out.println("异常：" + e.getMessage());

                }
                 message = (String) hashMap.get("message");
                try {
                    objectInputStream.close();
                } catch (IOException e) {
                    System.out.println("异常：" + e.getMessage());

                }

            }else System.out.println("警告：commit文件丢失！");
                        return message;
    }
}

