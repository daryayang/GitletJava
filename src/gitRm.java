import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream;
import java.io.*;
import java.util.HashMap;

public class gitRm {

       //rm <file>操作的实现如下
     public static void rmFILE(String filename) throws IOException, ClassNotFoundException {
         gitRm.rmindex(filename);
         gitRm.rmfile(filename);

     }
     //rm cached操作的实现如下：
     public static void rmcached(String filename) throws IOException, ClassNotFoundException {
         gitRm.rmindex(filename);
     }


       //删除index中对应的条目
       public static void rmindex (String filename) throws IOException, ClassNotFoundException {

           gitAdd.Blob blob = new gitAdd.Blob(filename);
           //由于index设置成了一个内含hashMap的对象，所以rm的实现都在用对象输入输出流
           //将index文件中的hashmap读出来
           ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(gitInit.idPath));
            Object o = objectInputStream.readObject();
            //由于读出来是object类型，所以强转成Hashmap类型
            HashMap hashMap = (HashMap)o;
            gitAdd.INDEX indexmap = new gitAdd.INDEX(hashMap);
            //把流关掉！！要不会有大问题
            objectInputStream.close();
            //将这个filename对应的k-v都删掉
           if(indexmap.hashMap.containsKey(blob.exName)){
               indexmap.hashMap.remove(blob.exName,blob.hashValue);

               //将得到的hashMap重新输出到index.txt文件夹中

               ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(gitInit.idPath));
               objectOutputStream.writeObject(indexmap.hashMap);
               objectOutputStream.close();

               System.out.println("index文件中已经删除该文件信息");
           }else System.out.println("异常：暂存区中没有"+ filename + "相关信息");

             System.out.println("该命令抛出了IOException, ClassNotFoundException");
       }

       //1.在index对象中删除对应条目，在工作区中删除该文件 rm <file>
        public static  void rmfile(String filename){
              String filePath = gitInit.workPath + File.separator + filename;
              File file = new File(filePath);
              //检验一下文件是否存在，不存在直接提示
              if(!file.exists()){
                  System.out.println("警告！您要删除的文件在工作区中不存在！");
              }else{
                  if(file.delete()){
                      //在这一步再加一个判断，防止操作失误
                      System.out.println("文件已经删除成功");
                  }else{
                      System.out.println("文件删除失败");
                  }
              }
        }


}
