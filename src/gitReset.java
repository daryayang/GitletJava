import java.io.*;
import java.util.HashMap;
import java.util.Set;

public class gitReset {




    public static void hardPP(String commitid,String filepath) throws IOException, ClassNotFoundException {
        //此处的filepath是接收端的地址是gitInit.gitPath
        if(filepath.equals(gitInit.gitPath)){
            gitReset.hard(commitid);
        } else if (filepath.equals(gitInit.depoPath)) {
            gitReset.pushresetHard(commitid);
        }

    }
    public static void soft(String commitid){
        try {
            gitReset.ResetSoft(commitid);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void mix(String commmitid) throws IOException, ClassNotFoundException {
        gitReset.ResetSoft(commmitid);
        gitReset.ResetMIX(commmitid);
    }
    public static void hard(String commitid) throws IOException, ClassNotFoundException {
        gitReset.ResetSoft(commitid);
        gitReset.ResetMIX(commitid);
        gitReset.ResetHard();
    }


    //
    public static void ResetSoft(String commitid) throws IOException {

        //在Phase3阶段将这些都改成object
        //向head文件中写入即可

        File file1 = new File(gitInit.hdPath);
        /*
        ObjectOutputStream oops = new ObjectOutputStream(new FileOutputStream(file1));
        oops.writeObject(commitid);
        oops.close();*/

        BufferedWriter  bufferedWriter = new BufferedWriter(new FileWriter(file1));
        bufferedWriter.write(commitid);
        bufferedWriter.close();

        //将head文件修改之后将记录存到HEAD_B文件中
        gitInit.AddtoRlist();
        System.out.println("RS操作完成：已经将HEAD文件按commitID进行修改！");
    }


      //这个方法用于实现在resetsoft基础上的mix操作，也就是不包括向head文件中写入东西
    public static void ResetMIX(String commitid) throws IOException, ClassNotFoundException {
        String commitpath = gitInit.obPath + File.separator + commitid +".txt";
          File file = new File(commitpath);
          HashMap commitMap = new HashMap<>();
          if(!file.exists()){
              System.out.println("警告！该commit文件不存在！无法实现 reset --mixed 操作");
          }
           //从commit文件当中获取treeid
          ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
          commitMap = (HashMap) ois.readObject();
          String treeid = (String) commitMap.get("treeid");
          //然后根据treeid找到tree文件，！！注意.txt
          String treepath = gitInit.obPath + File.separator + treeid + ".txt";
          File file1 = new File(treepath);
          if(!file1.exists()){
              System.out.println("警告！没有找到此次commit对应的tree文件！");
          }else {
              //用objectio流把tree文件当中内容提取出来
              ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(treepath));
              HashMap treeMap = new HashMap<>();
              treeMap = (HashMap) objectInputStream.readObject();
              //关闭io流
              objectInputStream.close();
              //从treemap当中读取content,那个就是要找的的内容

              //可喜可贺！实现了将index里面那个map直接弄到tree文件里面
              //尝试直接把那个从commit里面找到的

              //下面这一步string先不要了，暂时保留，之后不行就再拿出来，buffered流也不要了，换成高级的object
              //String indexcontent = (String)treeMap.get("content");
              //再开一个buffered流把上面从tree中还原出来的写进index.txt

              File file2 = new File(gitInit.idPath);
              //再判断一下子index.txt，担心抛出异常
              if (!file2.exists()) {
                  System.out.println("警告！没有找到Index.txt文件");
              }
              ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file2));
              //在phase2阶段修改之后treemap和index里面那个map一毛一样
              //感谢spgg，之前不知道为啥tree里面要和index一样，现在我懂了TT
              //因为treemap就是从index里面的map，所以在这一步直接再把treemap写进去
                oos.writeObject(treeMap);
                oos.close();
          }
          System.out.println("RM操作完成：已经将暂存区按照commitID进行修改！");
        }


              //这一步方法用于实现在resetSoft和mix基础上的hard操作
              //上两步都需要commitid，而这一步实际上是不需要的，所以这一步就不需要commitid了

            public static void ResetHard() throws IOException, ClassNotFoundException {
             //从index.txt当中把内容读出来（注意，此时index.txt中不是map是string）
             // 然后按照index里面的内容找到blob文件原名字，生成txt；
              //再找到blobid，就是blob文件名字（在object文件夹里），从那里面把内容读出来
              //从那里面找到file里面应该装的content
             File file3 = new File(gitInit.idPath);
             if(!file3.exists()) {System.out.println("警告！index.txt不存在！请重新执行init操作！");}
             else{
                 //这一步暂时考虑用keyset做
                 //用object流将index里面那个map读出来
                 //虽然可以直接图省事直接用treemap，但是还是按照git整体构架走更合理
                 ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file3));
                 HashMap indexmap = new HashMap<String,String>();
                 indexmap = (HashMap) objectInputStream.readObject();
                 objectInputStream.close();
                 //先通过map.size确定暂存区indexmap里有多少文件
                 if(indexmap.isEmpty()){
                     System.out.println("警告！index文件中没有blob文件相关信息");
                 } else{//第一次尝试时，使用判断indexmap的size的方法
                        //感觉太麻烦了，试试直接增强for循环输出
                     Set keyset = indexmap.keySet();
                     for(Object key : keyset){
                         String filepath = gitInit.workPath + File.separator + key;
                         File file4 = new File(filepath);
                         if(!file4.exists()){
                             //这里判断一下，如果文件已经不存在了就再生成一个文件
                             //如果文件已经存在了，就不用再创建了
                            file4.createNewFile();
                         }

                         //对应的blob文件的名字是indexmap.get(key);
                         //顺着indexmap里面得到的文件地址，找到文件，进而得出源文件里应该存放的content
                         String blobfilePath = gitInit.obPath + File.separator
                                                + (String) indexmap.get(key) + ".txt";

                         //写到这里发现需要修改blob文件的输入
                         //按照助教书培学长的要求，blob类里只要有对应源文件的内容即可
                         //所以在phase2阶段把blob文件当中要求之外的输入取消
                         //做到这一步的时候再次感受到了git的魅力


                         //在开io流之前先判读blob文件是否还存在，不存在就直接报错
                         File file5 = new File(blobfilePath);
                         if(!file5.exists()){
                             System.out.println("警告！对应blob文件不存在！无法完成Reset--Hard操作！");
                         }

                          //在phase3对这一步进行了修改，重新确定流
                         //首先file5-是blob文件，也就是object文件夹以哈希值来命名的文件
                         //file5的写入用的是object流，所以读它也用object流
                         // 可以直接使用gitadd里面写的专门针对object流写入文件的读出方法，返回的是string
                         //file4是源文件，最开始的文件，也就是fly.txt这种的文件
                         //所以用bw写入文件

                         String content = gitAdd.fileContentOb(blobfilePath);
                         BufferedWriter bw1 = null;
                         bw1 = new BufferedWriter(new FileWriter(filepath));
                         bw1.write(content);
                         //关闭流
                         bw1.close();



                     }

                 }
                 }

                System.out.println("RH操作完成：已经将工作区按照commitID进行修改！");
             }
             //专门为push操作，也就是在仓库depoGit执行resethard操作方法
            //实际上这个操作和上面的是几乎是一模一样的
            //但是这个的commitID不能通过main里面的参数传进来
            //因此这个方法是将commitid作为方法的参数传进去的一个方法
             public static void pushresetHard(String commitid) throws IOException, ClassNotFoundException {

                 File file1 = new File(gitInit.hdPath2);
                /*
                ObjectOutputStream oops = new ObjectOutputStream(new FileOutputStream(file1));
                oops.writeObject(commitid);
                oops.close();*/
                 BufferedWriter  bufferedWriter = new BufferedWriter(new FileWriter(file1));
                 bufferedWriter.write(commitid);
                 bufferedWriter.close();
                 System.out.println("RS操作完成：已经将HEAD文件按commitID进行修改！");

                 String commitpath = gitInit.obPath2 + File.separator + commitid +".txt";
                 File file = new File(commitpath);
                 HashMap commitMap = new HashMap<>();
                 if(!file.exists()){
                     System.out.println("警告！该commit文件不存在！无法实现 reset --mixed 操作");
                 }
                 //从commit文件当中获取treeid
                 ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
                 commitMap = (HashMap) ois.readObject();
                 String treeid = (String) commitMap.get("treeid");
                 //然后根据treeid找到tree文件，！！注意.txt
                 String treepath = gitInit.obPath2 + File.separator + treeid + ".txt";
                 File file2 = new File(treepath);
                 if(!file2.exists()){
                     System.out.println("警告！没有找到此次commit对应的tree文件！");
                 }else {
                     //用objectio流把tree文件当中内容提取出来
                     ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(treepath));
                     HashMap treeMap = new HashMap<>();
                     treeMap = (HashMap) objectInputStream.readObject();
                     //关闭io流
                     objectInputStream.close();

                     File file3 = new File(gitInit.idPath2);
                     //再判断一下子index.txt，担心抛出异常
                     if (!file3.exists()) {
                         System.out.println("警告！没有找到Index.txt文件");
                     }
                     ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file3));
                     oos.writeObject(treeMap);
                     oos.close();
                 }
                 System.out.println("RM操作完成：已经将暂存区按照commitID进行修改！");

                 File file4 = new File(gitInit.idPath2);
                 if(!file4.exists()) {System.out.println("警告！index.txt不存在！请重新执行init操作！");}
                 else{
                     ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file4));
                     HashMap indexmap = new HashMap<String,String>();
                     indexmap = (HashMap) objectInputStream.readObject();
                     objectInputStream.close();
                     if(indexmap.isEmpty()){
                         System.out.println("警告！index文件中没有blob文件相关信息");
                     } else{
                         //第一次尝试时，使用判断indexmap的size的方法
                         //感觉太麻烦了，试试直接增强for循环输出
                         Set keyset = indexmap.keySet();
                         for(Object key : keyset){
                             String filepath = gitInit.workPath2 + File.separator + key;
                             File file5 = new File(filepath);
                             if(!file5.exists()){
                                 //这里判断一下，如果文件已经不存在了就再生成一个文件
                                 //如果文件已经存在了，就不用再创建了
                                 file5.createNewFile();
                             }

                             //对应的blob文件的名字是indexmap.get(key);
                             //顺着indexmap里面得到的文件地址，找到文件，进而得出源文件里应该存放的content
                             String blobfilePath = gitInit.obPath2 + File.separator
                                     + (String) indexmap.get(key) + ".txt";

                             //在开io流之前先判读blob文件是否还存在，不存在就直接报错
                             File file6 = new File(blobfilePath);
                             if(!file6.exists()){
                                 System.out.println("警告！对应blob文件不存在！无法完成Reset--Hard操作！");
                             }

                             String content = gitAdd.fileContentOb(blobfilePath);
                             BufferedWriter bw1 = null;
                             bw1 = new BufferedWriter(new FileWriter(filepath));
                             bw1.write(content);
                             //关闭流
                             bw1.close();

                         }

                     }
                 }
                 System.out.println("RH操作完成：已经将工作区按照commitID进行修改！");





        }
}


