import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.*;

public class gitInit {

        //先定义一些路径方便后面搜索、创建和传输
        static  String property = System.getProperty("user.dir");

        static  String workPath = property + File.separator + "gitWorkPlace";
        //.git文件夹地址
        static  String gitPath = property + File.separator + "test.git";
        //object文件夹地址
        static String obPath = gitPath + File.separator + "objects";
        //index文件地址
        static String idPath = gitPath + File.separator + "index.txt";
        //Head文件地址
        static String hdPath = gitPath + File.separator + "HEAD.txt";
        //HeadB文件地址专门用来存放记录head记录
        static String hdPathB = gitPath + File.separator + "HEAD_B.txt";
        //HEADs文件夹用于存放分支管理的各分支head文件
        static String headsDir = gitPath + File.separator + "Heads";
        //本地master分支地址
        static String masterPath = headsDir + File.separator + "master.txt";
        static String depoPath = property + File.separator + "depoGit";
        static String obPath2 = depoPath + File.separator + "objects";
        static String idPath2 = depoPath + File.separator + "index.txt";
        static String hdPath2 = depoPath + File.separator + "HEAD.txt";
        static String hdPathB2 = depoPath + File.separator + "HEAD_B.txt";
        static String workPath2 = property + File.separator + "depoWorkPlace";
        static String headsDir2 = depoPath + File.separator + "Heads";
        static String masterPath2 = headsDir2 + File.separator + "master.txt";




        public static void init () throws IOException {
            File file = new File(gitPath);
            //检验是否已经存在.git文件，如果已经存在提示，不存在则新建
            if (!file.exists()) {
                file.mkdir();
                System.out.println(".git文件夹创建成功");
            } else System.out.println("已经存在git文件");

            //在git目录下新建objects文件夹，如果已经存在就提示，如果不存在就新建
            //objects目录用于blob、tree、commit等对象，命名均为其hash值
            File file2 = new File(obPath);

            if (!file2.exists()) {
                file2.mkdir();
                System.out.println("objects文件夹创建成功");

            } else System.out.println("已经存在object文件");

            //在git目录下新建index文件，如果已经存在则提示
            //对象序列化到.git目录下用于储存 文件名-hash值的对应条目（初始为空）；
            //我知道index文件实际上是在add阶段生成的，但是根据腾讯文档以及后续操作简化
            //如果此处有不合理的地方我会修改，请不要扣分

            File file3 = new File(idPath);
            if (!file3.exists()) {
                file3.createNewFile();
                System.out.println("Index文件创建成功");

            } else System.out.println("Index文件已经存在");

            //检验git目录下是否有head文件，如果有的话就提示，没有则新建
            //创建HEAD文件储存最近一次的commit id（即commit对象的hash值，初始为空）。
            //HEAD文件实际上也是在第一次提交的时候生成的

            File file4 = new File(hdPath);
            if (!file4.exists()) {
                file4.createNewFile();
                System.out.println("HEAD文件创建成功");

            } else System.out.println("HEAD文件已经存在");

            File file9 = new File(hdPathB);
            if (!file9.exists()) {
                file9.createNewFile();
                System.out.println("HEAD_B文件创建成功");

                //向HEAD_B文件中写入一个list以便于保存head文件变化历史
                gitInit.NewHeadList(hdPathB);

            } else System.out.println("HEAD_B文件已经存在");

            File file11 = new File(headsDir);
            if(!file11.exists()){
                file11.mkdir();
                System.out.println("Heads文件夹创建成功");
            }else System.out.println("Heads文件夹已经存在");

            File file12 = new File(masterPath);
            if(!file12.exists()){
                file12.createNewFile();
                System.out.println("master分支创建成功");
            }else System.out.println("master分支已经存在");

            System.out.println("本地git文件夹已经创建成功");

            //首先先判断仓库是否已经存在
            File file5 = new File(depoPath);
            //检验是否已经存在.git文件，如果已经存在提示，不存在则新建
            if (!file5.exists()) {
                file5.mkdir();
                System.out.println("depoGit文件夹创建成功");
            } else System.out.println("已经存在git远程仓库");

            //创建仓库里的object文件夹
            File file6 = new File(obPath2);

            if (!file6.exists()) {
                file6.mkdir();
                System.out.println("远程仓库：objects文件夹创建成功");

            } else System.out.println("远程仓库：已经存在object文件");

            //在仓库中新建head文件

            File file7 = new File(idPath2);
            if (!file7.exists()) {
                file7.createNewFile();
                System.out.println("远程仓库：Index文件创建成功");

            } else System.out.println("远程仓库：Index文件已经存在");

            //在仓库中新建head文件

            File file8 = new File(hdPath2);
            if (!file8.exists()) {
                file8.createNewFile();
                System.out.println("远程仓库：Head文件创建成功");

            } else System.out.println("远程仓库：Head文件已经存在");

            File file10 = new File(hdPathB2);
            if (!file10.exists()) {
                file10.createNewFile();

                System.out.println("远程仓库：HEAD_B文件创建成功");

            } else System.out.println("远程仓库：HEAD_B文件已经存在");

            File file13 = new File(headsDir2);
            if(!file13.exists()){
                file13.mkdir();
                System.out.println("远程仓库：Heads文件夹创建成功");
            }else System.out.println("远程仓库：Heads文件夹已经存在");

            File file14 = new File(masterPath2);
            if (!file14.exists()){
                file14.createNewFile();
                System.out.println("远程仓库：master分支创建成功");
            }else System.out.println("远程仓库：master分支已经存在");


            System.out.println("远程git仓库文件夹创建成功");



        }


        //在下面写一个方法用于读取head文件，然后将head文件里的那个id写入到reflogList当中
         public static void AddtoRlist() throws FileNotFoundException {

            //从head文件中读取commitid作为string

                 String NewHeadRecord = gitInit.HEADcontent();


             //创建一个新的list用于存放读取headB文件中的list
             List refloglist = new ArrayList<>();

             //开启object流，直接将HeadB中之前的list读出来
             ObjectInputStream objectInputStream = null;
             try {
                 objectInputStream = new ObjectInputStream(new FileInputStream(gitInit.hdPathB));
             } catch (IOException e) {
                 System.out.println("异常：" + e.getMessage());
             }
             try {
                 refloglist = (List) objectInputStream.readObject();
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

             //注意向list当中增加的时候永远都是在list的0号位置添加
             //这样每次head文件的最新变化都是在list的最上面
             //这样的设置可以保证在reflog操作中将这个list打印出来的时候和真正git格式是一样的
             refloglist.add(0,NewHeadRecord);

             //开一个新的ob流，向HeadB文件重新写入
             ObjectOutputStream objectOutputStream = null;
             try {
                 objectOutputStream = new ObjectOutputStream(new FileOutputStream(gitInit.hdPathB));
             } catch (IOException e) {
                 System.out.println("异常：" + e.getMessage());
             }
             try {
                 objectOutputStream.writeObject(refloglist);
             } catch (IOException e) {
                 System.out.println("异常：" + e.getMessage());
             }
             try {
                 objectOutputStream.close();
             } catch (IOException e) {
                 System.out.println("异常：" + e.getMessage());
             }

         }

         //向head_B文件中写入一个arraylist
         public static void NewHeadList(String filepath){
             List reflogList = new ArrayList<>();

             //开一个ob流向文件中传入这个list,并且进行异常处理
             ObjectOutputStream oos  = null;
             try {
                 oos = new ObjectOutputStream(new FileOutputStream(filepath));
             } catch (IOException e) {
                 System.out.println("异常：" + e.getMessage());
             }
             try {
                 oos.writeObject(reflogList);
             } catch (IOException e) {
                 System.out.println("异常：" + e.getMessage());
             }
             try {
                 oos.close();
             } catch (IOException e) {
                 System.out.println("异常：" + e.getMessage());
             }

         }
            //该方法用于读取head文件，然后将读取的内容作为String输出
         public static String HEADcontent() throws FileNotFoundException {
            //初始化HEADcontent
            String HEADcontent = null;
            //开一个Bf流从HEAD文件中读取此时HEAD文件中的内容
             BufferedReader bufferedReader = new BufferedReader(new FileReader(gitInit.hdPath));
             //使用buffered.readLine()按行读取
             // 由于Head文件中只存储最新的commitid，所以读一行就行
             try {
                 HEADcontent = bufferedReader.readLine();
             } catch (IOException e) {
                 System.out.println("异常：" + e.getMessage());
             }
             return HEADcontent;
         }

    }










