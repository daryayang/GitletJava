
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.*;

public class gitCommit {

      public static void commitOperation (String message) throws IOException, ClassNotFoundException {



          //新建一个hashmap表用于存放tree及信息
         // HashMap treemap = new HashMap<>();
          HashMap treemap = gitCommit.getindexMap(gitInit.idPath);
          //将index中所有条目生成tree对象序列化到objects文件夹下；
          tree T = new tree(treemap,gitInit.idPath);
          File file = new File(T.treePath);

          //在phase2中直接把index的hashmap读出来作为treemap当中的一部分
          // （在indexHashMap基础上又加入了一个type，方便检验成果）

          if (!file.exists()) {//检验是否存在
              //如果不存在就创建新的文档
              file.createNewFile();
              //提示创建成功
              System.out.println("tree文件创建成功");
          } else
              System.out.println("目标tree文件已经存在");

          //  向tree文件中开始序列化
          ObjectOutputStream oos = null;
          oos = new ObjectOutputStream(new FileOutputStream(T.treePath));
          oos.writeObject(T.hashMap);
          //序列化结束之后关闭
          oos.close();

          System.out.println("tree文件内容输出完毕");


          //此操作为Phase2阶段最后实现的操作
          //因此建立在大幅度修改的基础上
          //下面执行"打印本次commit相对上一次commit的文件变动情况（增加、删除、修改）"
          //从head文件中得到上一次commit的文件名字

          //这里的就是用fileContent
          String precommitfile = gitAdd.fileContent(gitInit.hdPath);
          if (precommitfile.equals("null")||(precommitfile.length() == 0)) {
              System.out.println("此次是第一次执行commit命令！");

          } else{
              //这个else包括全部比较代码，因为只有确保不是第一次commit才会进行如下操作
             //对应的commit文件的地址应该是
             String pcfilepath = gitInit.obPath + File.separator + precommitfile + ".txt";
             File file6 = new File(pcfilepath);
             if(!file6.exists()){
              System.out.println("警告！上一次commit的文件已经丢失！");
              }else{
              //开一个io流从上一次的commit文件中读取出hashmap
              ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file6));
              //新建一个hashmap表用于存放用于比较的上次commit表
              HashMap comCommitMap = new HashMap<>();
              comCommitMap = (HashMap) ois.readObject();
              ois.close();
              //得到上一次commit的表格要从当中找到treeid也就是tree文件，因为tree里面的表格是通过index生成的
              String comparetree = (String) comCommitMap.get("treeid");
              String comtreepath = gitInit.obPath + File.separator + comparetree + ".txt";
              File file1 = new File(comtreepath);
              HashMap cptreemap = new HashMap<String, String>();
              //再重新开一个io流将tree当中的map读出来
              //在phase2修改之后，tree文件里面变成了map并且和index里面的map一样
              //再新建一个新的map用于存放需要比较的上一次tree的map（cp就是compare）
              if(!file1.exists()){// 判断一下，防止报错
                  System.out.println("警告！上一次commit操作中的tree文件遗失！请重新执行操作");
              }else {
                  ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(comtreepath));
                  cptreemap = (HashMap) objectInputStream.readObject();
                  objectInputStream.close();
              }

              //具体比较过程如下
              //首先比较这次的treemap和上次的treemap的size是否一样
              //因为如果不一样说明两次add命令文件数量不同
              if(treemap.size() > cptreemap.size()){ //说明这次add操作文件数量增多
                  System.out.println("此次commit较上一次commit文件有所增加");
              } else if (treemap.size() < cptreemap.size()) {
                  System.out.println("此次commit较上一次commit文件有所删除");
              } else if (treemap.size() == cptreemap.size() && (!T.Hashvalue.equals(comparetree))) {
                   //如果两次生成的treemap中的size相同，但是产生了两次commit，说明文件发生了修改
                  System.out.println("此次commit较上一次commit文件有所修改");
              } else if (treemap.size() == cptreemap.size() && T.Hashvalue.equals(comparetree)){
                     System.out.println("重复commit！此次commit和上次commit文件完全相同");
              }


             }

          }

          //将commit对象序列化到objects文件夹下
          //id都是哈希值
          //实际上commit当中tree的id就是tree文件的哈希值，根据这个哈希值可以找到tree文件
          //并且进一步找到子目录


            HashMap commitmp =new HashMap<>();
            commitMap commitmap = new commitMap(commitmp,T.Hashvalue,message);
            commitmap.hashMap.put("type","commit");
            commitmap.hashMap.put("treeid",T.Hashvalue);
            commitmap.hashMap.put("time",commitmap.time);
            commitmap.hashMap.put("precommit",commitmap.precommit);
            commitmap.hashMap.put("message",commitmap.message);
            //在phase2阶段，由于将commit作为对象实现，并内置hashMap
            //所以在这阶段使用对象输出流，直接将commit作为对象输出到文件中
            String commitfile = gitInit.obPath + File.separator + commitmap.commitid + ".txt";
            File file2 = new File(commitfile);
            if(!file2.exists() ){//如果文件不存在则生成commit文件
             ObjectOutputStream objectOutputStream = new ObjectOutputStream( new FileOutputStream(commitfile));
             //将hashMap作为一个对象写入
             objectOutputStream.writeObject(commitmap.hashMap);
             //一定要把流关掉，否则运行错位
            objectOutputStream.close();
          }

            System.out.println("commit文件生成完毕");

          //更新HEAD文件中的commit id。
           FileWriter fileWriter = null;
          fileWriter = new FileWriter(gitInit.hdPath);
          //向head文件中更新的是commit对象的哈希值
          fileWriter.write(commitmap.commitid);
          fileWriter.close();

          //更新head记录
           gitInit.AddtoRlist();
          System.out.println("已更新head记录");

          System.out.println("该命令抛出了IOException, ClassNotFoundException");




      }






      //以下为Phase2阶段为了log重写commitOb类，并且设置成HashMap
     public static class commitMap implements Serializable{

          public HashMap <String, String> hashMap;
          public String commitid;
          public String type;
          public String treeid;
          public String message;
          public String time;
          public String precommit;
          public String content;

          public commitMap(HashMap<String, String> hashMap, String treeid , String message) throws IOException, ClassNotFoundException {
              this.type = "commit";
              this.hashMap = hashMap;
              this.precommit = gitAdd.fileContent(gitInit.hdPath);
              this.treeid = treeid;
              this.message = message;
              this.content = treeid + message;
              this.commitid = gitAdd.getHV(content);
              this.time = getTime();
          }
      }



    public static String getTime(){
        Date date = new Date();
        //改变一下格式看起来更美观
        SimpleDateFormat dF= new SimpleDateFormat("yyyy-MM-dd :hh:mm:ss");
        return dF.format(date);
    }



    //定义Tree类
    //虽然Tree对象和index文件当中内容相同，但是先确定一些变量备用
    public static class tree implements Serializable {

        public HashMap <String,String> hashMap;

        public String type;
        public int size;
        public String content;
        public String Hashvalue;
        public String treePath;
        public String filePath;

        public tree(HashMap<String, String> hashMap,String filePath) throws IOException, ClassNotFoundException {
            this.hashMap = hashMap;
            this.filePath = filePath;
            this.type = "Tree";
            //在Phase3阶段这里改成了fileContentOb，因为实际上已经是将index里面弄成hashmap了
            this.content = gitAdd.fileContentOb(filePath);
            this.size = content.getBytes().length;
            this.Hashvalue = gitAdd.getHV(content);
            this.treePath = gitInit.obPath + File.separator + Hashvalue + ".txt";
        }

    }




    //在phase2后半阶段尝试再写一次从index当中读取那个hashmap
          // 并且直接把内容读出来，读成一个String
         //写这个方法是为了读取index中的map的key，也就是blob文件的原名字

   public static  HashMap getindexMap(String filepath) throws IOException, ClassNotFoundException {

        HashMap hashMap = new HashMap<String,String>();
        ObjectInputStream  ois = new ObjectInputStream(new FileInputStream(filepath)) ;
        hashMap = (HashMap) ois.readObject();
        ois.close();

        return hashMap;

    }

}
