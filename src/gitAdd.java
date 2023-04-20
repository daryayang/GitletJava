import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class gitAdd {
     public static  void addOnce(String filename) throws IOException, ClassNotFoundException {
        /*在index对象中添加/修改/删除  文件名-hash值 条目；
        创建对应blob对象序列化到objects文件夹下；*/


         //phase 2 阶段修改 在这里补充一个检验add的文件是否存在
         //如果确定文件存在再继续进行
         String targetFilePath = gitInit.workPath + File.separator + filename;
         File file2 = new File(targetFilePath);
         if(!file2.exists()){
             System.out.println("警告！您要添加的文件工作区中没有！");
         }else {//确定需要检验的文件已经存在再进行操作
             gitAdd.newBlob(filename);
             gitAdd.INDEXMAP(filename); }
         System.out.println("该命令抛出了IOException");
     }

     public static void addALl() throws IOException, ClassNotFoundException {
         //  输入add . 时，对工作区的全部文件进行一次add操作；
         // 其中要注意在本gitprogram中对于blob文件的工作区默认为gitWorkPlace目录
         // 但是需要注意的是苹果电脑回自动生成.DS_Store文件夹，所以需要ignore掉这个文件夹

           /*
        输入add . 时，对工作区的全部文件进行一次add操作；
        输入add . 时，将只存在于暂存区中，而不存在于工作区的文件记录，从index中删除*/
         File dir=new File(gitInit.workPath);
         File[] files=dir.listFiles();
         HashMap hashMap = new HashMap();
         //遍历工作区所有文件
         for(File f:files){
             if(f.isFile()){
                 String filename=f.getName(); //获取文件名
                 //如果是Mac自动创建的.DS_Store文件，以及head、index、objects文件，则忽略
                 if(filename.equals(".DS_Store")){
                     continue;
                 }
                 //对遍历到的文件进行addOnce操作
                 gitAdd.newBlob(filename);
                 Blob blob1 = new Blob(filename);
                 hashMap.put(blob1.exName,blob1.hashValue);

                 //由于phase 2阶段将index设置成了一个对象，并且内置了一个hashmap
                 //因此将输入和输出方式改为object输入输出流
               }
           }
             String indexFile = gitInit.idPath;
             INDEX i = new INDEX(hashMap);
             ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(indexFile));
             oos.writeObject(i.hashMap);
             oos.close();
             System.out.println("该命令抛出了IOException");

    }


     //首先先执行将blob文件序列化到objects文件夹中
     //序列化的顺序为 blob size content
    //new一个blob对象
    public static void newBlob (String filename) throws IOException {



        Blob blob = new Blob(filename);
        File blobfile = new File(blob.filePath);
        if (!blobfile.exists()) {

            //如果blob文件不存在，那么就创建一个blob文件

            blobfile.createNewFile();

            System.out.println("创建blob文件成功");

        }else System.out.println("该blob文件已经存在");


             //在phase3阶段将blob文件的写入也改成object流
            ObjectOutputStream oops = new ObjectOutputStream(new FileOutputStream(blob.filePath));
            //向blob文件输入blob文件中应该输入的content
            oops.writeObject(blob.content);

            oops.close();



            System.out.println("blob文件内容已经更新");
         }



     //在index当中实现对于blob的增加和修改
    //在phase2 阶段实现对 INDEX里面hashmap的定义，有助于在接下来实现add.
        public  static  void INDEXMAP(String filename) throws IOException, ClassNotFoundException {

             Blob blob1 = new Blob(filename);
             HashMap hashMap = new HashMap<>();
             //将生成的blob的文件名字和该blob对应的哈希值（也就是blob文件的id）输入hashmap
             hashMap.put(blob1.exName,blob1.hashValue);
             //INDEX中是一张hashmap表
             INDEX i= new INDEX(hashMap);

             //向index.txt中输出index
            File indexFile = new File(gitInit.idPath);

            //由于phase 2阶段将index设置成了一个对象，并且内置了一个hashmap
            //因此将输入和输出方式改为object输入输出流

            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(indexFile));
            oos.writeObject(i.hashMap);
            oos.close();

            //下面考虑一种文件在index中有显示但是在工作区已经无了的情况
            //根据上面的方法，对于输入的filename，已经在index文件中添加<filename,blob文件名>
            //下面查询该文件是否还存在，如果不存在，就在哈希表中删除该元素
            // 重新向index中输入一次index

            String filePath = gitInit.workPath + File.separator + filename;
             File file = new File(filePath);
             if(!file.exists()){
                 //如果文件不存在，就从hashMap当中remove掉
                 hashMap.remove(blob1.exName,blob1.hashValue);

                 // 此步将它设计为if语句块呢，也就是如果这个文件存在那么就不需要执行index中的删除
                 // 将处理过的hashmap，向index.txt重新输出
                  ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(indexFile));
                  objectOutputStream.writeObject(hashMap);
                  objectOutputStream.close();
             }
             System.out.println("index文件内容已更新");

        }


    //blob类的定义
    //创建blob对象要注意，必须实现如下接口，这样才能实现序列化与反序列化
    //实际的git提交时，会生成blob类型的文件，所以我们也需要定义这个文件来模拟git的实现
    //值得注意的是：在blob文件中，也最好保留原始文件名这个属性，这样才方便后续的各种操作
    //例子blob文件1 blob 9 周杰伦
    //例子blob文件2 blob 43 Java可能是东半球最好的编程语言

    public static class Blob implements Serializable {
        public static String type;
        public static int size;
        public static String content;
        public static String exName;
        public static String filePath;//这个是生成blob文件的地址

        public static String OriginalfilePath;//这个是源文件地址
        public String hashValue;


        //command + N 构造器构造
        public Blob(String filename) throws IOException {
            //git中所有文件都被抽象成了blob
            this.type = "blob";
            //源文件地址为
            this.OriginalfilePath = gitInit.workPath + File.separator + filename;
            //通过文件地址查阅到文件的内容
            //这里确定使用filecontent方法
            this.content = fileContent(OriginalfilePath);
            //size是根据文件内容生成的
            this.size = content.getBytes().length;
            //并且通过内容返回哈希值（用于生成blob文件的名字）
            //哈希值是根据源文件内容也就是content生成的
            this.hashValue = getHV(content);
            //将要被抽象成blob类的文件的地址如下
            this.filePath = gitInit.obPath + File.separator + hashValue + ".txt";
            this.exName = filename;
        }

        public String getFilePath() {
            return filePath;
        }

        public int getSize() {
            return size;
        }

        public String getHashValue() {
            return hashValue;
        }

        public String getContent() {
            return content;
        }

        public String getType() {
            return type;
        }
    }


    //构造INDEX对象，方便在index.txt中进行操作
        public static class INDEX implements Serializable {
        ;
        public HashMap <String, String> hashMap;

            public INDEX(HashMap<String, String> hashMap) {
                this.hashMap = hashMap;
            }
        }







    //以下为从文件（会被抽象为blob的文件）中快速读取文件内容
    //查找的方式是找到文件地址，再从文件中找到内容
    //并把读取文件内容时需要抛出的异常抛出
      //该方法在phase3阶段改为当且仅当从源文件中读取出string
    public  static String fileContent(String filepath) throws IOException {
        String content = new String();
        //使用bufferedreader读取文件内容
        //创建缓冲流对象，套接在指定的节点流基础上
        //在此处初始化，对后续关闭外层流有帮助
        BufferedReader br = null;
        br = new BufferedReader(new FileReader(filepath));
        //读取时按行读取效率更高
        String line;
        while((line = br.readLine())!=null){ //读取一行
            content += line;
        }
        //关闭外层流
        br.close();
        return content;

    }

    //在phase3新写一个从文件内容中读取内容并返回string
    //在这个方法当中使用的是object流
     public static String fileContentOb(String filepath) throws IOException, ClassNotFoundException {
        String result = null;
        //用object流读出来文件内容
             ObjectInputStream oops = null;

             oops = new ObjectInputStream(new FileInputStream(filepath));

             result = oops.readObject().toString();

             oops.close();

         return result;
     }



    //以下为哈希值算法
    //命名为getHV，get hash value，返回一个string
    public static String getHV(String input){

        try {
            // Static getInstance method is called with hashing SHA-1
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            // digest() method is called to calculate message digest
            // of an input digest() return array of byte
            byte[] messageDigest = md.digest(input.getBytes());
            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);
            // Convert message digest into hex value
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }
        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

    }

    //以下为遍历默认文件夹根据文件名查找是否有目标文件
    public static Boolean fileexist(String filename){
            Boolean result ;
         //在工作区目录根据filename建立file对象
        File file = new File(gitInit.gitPath + File.separator + filename);
            if(file.exists()){
                result = true;
                }
            else{
                result = false;
            }
            return result;
}





}

