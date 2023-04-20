import  java.io.*;
import java.util.*;

public class gitStatus {
     public static void checkStatus() throws IOException, ClassNotFoundException {

         //git Status命令用于实现查看工作区文件及上一次add的文件的状态
         //具体可以将文件的状态分为5种
         /*
         Untracked: 未跟踪, 此文件在文件夹中, 但并没有加入到git库, 不参与版本控制.
                    通过git add 状态变为Staged.
         Unmodify: 文件已经入库, 未修改, 即版本库中的文件快照内容与文件夹中完全一致.
                    这种类型的文件有两种去处, 如果它被修改, 而变为Modified.
                    如果使用git rm移出版本库, 则成为Untracked文件。
         Modified: 文件已修改, 仅仅是修改, 并没有进行其他的操作.
                   这个文件也有两个去处, 通过git add可进入暂存staged状态。
         Staged: 暂存状态. 执行git commit则将修改同步到库中, 这时库中的文件和本地文件又变为一致,
                 文件为Unmodify状态. 执行git reset HEAD filename取消暂存, 文件状态为Modified
         Delete：删除状态，在上一次add的index文件中有这个文件，但是在工作区中已经找不到这个文件了
         */

         String indexFilePath = gitInit.idPath;
         File file = new File(indexFilePath);
         Map map =new HashMap<>();
         Map commitMap = new HashMap<>();
         Map treeMap = new HashMap<>();
         String filename = null;
         String fileSHA = null;
         File dir=new File(gitInit.workPath);//工作区路径
         List Dlist = new ArrayList<>(); //用于存放Delete状态的文件
         List Unlist = new ArrayList<>();//用于存放Untracked状态的文件
         List Staged_Mlist = new ArrayList<>();//用于存放Staged状态的modified文件
         List Staged_newfile = new ArrayList<>();//用于存放staged状态的untracked状态文件进化版：new file
         List Stage_Dlist = new ArrayList<>();
         List NS_Mlist = new ArrayList<>();//用于存放not staged for commit里的modified的文件
         //每一次操作之前都清空一下所有list，保险又健壮
         Dlist.clear();Unlist.clear();NS_Mlist.clear();
         Stage_Dlist.clear();Staged_Mlist.clear();Staged_newfile.clear();

         if(!file.exists()) System.out.println("警告：Index文件丢失！无法查看文件状态");
            else {
             ObjectInputStream ois = null;
                 ois = new ObjectInputStream(new FileInputStream(file));
                 map = (HashMap) ois.readObject();
                 ois.close();

                String excommit = gitAdd.fileContent(gitInit.hdPath);
                if(excommit != null){
                   String  excommitPath = gitInit.obPath + File.separator +excommit + ".txt";
                   Map commitMap2 = new HashMap<>();
                    ObjectInputStream ois4 = null;
                    ois4 = new ObjectInputStream(new FileInputStream(excommitPath));
                    commitMap2 = (HashMap)ois4.readObject();
                    ois4.close();
                    String treeid2 = (String)commitMap2.get("treeid");
                    String treePath2 = gitInit.obPath + File.separator + treeid2 + ".txt";
                    Map treeMap2 = new HashMap<>();
                    ObjectInputStream ois5 =new ObjectInputStream(new FileInputStream(treePath2));
                    treeMap2 = (HashMap)ois5.readObject();
                    ois5.close();
                    Set keyset2 = treeMap2.keySet();
                    for(Object key : keyset2 ){
                        if(!key.equals(null)){
                            String keyPath = gitInit.workPath + File.separator + (String) key;
                            File file10 = new File(keyPath);
                            boolean a = !file10.exists();
                            boolean b = !map.containsKey(key);
                            if(a);{
                                if(b){
                                    Stage_Dlist.add(0,key);
                                }
                                }


                        }

                    }



                }
              Set keyset = map.keySet();
              for (Object key : keyset){
                  //文件名是key，文件对应的哈希值是对应的value
                  filename = (String)key;
                  String filepath = gitInit.workPath + File.separator + filename;
                  File file2 = new File(filepath);
                  if (!file2.exists()){//Delete：在上一次的index中有这个文件，但是工作区中没有这个文件
                      //如果这个文件名在index文件的hashmap当中有
                      //但是在工作区中不存在这样一个文件
                      //说明这个文件被delete了
                      //将这个文件名加入到Dlist当中方便最后输出
                      Dlist.add(0,filename);
                  }else{
                      //接下来通过hash值判断这个文件是否是modified
                          String filecontent1 = null;
                          filecontent1 = gitAdd.fileContent(filepath);
                         fileSHA = gitAdd.getHV(filecontent1);
                         String Last_fileSHA = (String) map.get(filename);
                        if(!fileSHA.equals(Last_fileSHA)){//Modified：在index的hashmap中有这个key，但是相同的key对应的value不同
                          //只有这一次和上一次的哈希值不同说明这个文件是modified状态
                            NS_Mlist.add(0,filename);
                      }else{
                          //从HEAD.txt中将上一次的commitID读出来，然后找到tree文件
                             String Last_commitID = null;
                              Last_commitID = gitAdd.fileContent(gitInit.hdPath);
                          //如果head文件此时内容为空说明之前没有commit过
                          //那么这个文件一定是newfile
                          if(Last_commitID == null) Staged_newfile.add(0,filename);

                          //只有在确定有上一次commitid的情况时
                          else{
                              //commit文件的地址是
                              String  commitPath = gitInit.obPath + File.separator + Last_commitID + ".txt";
                              //通过object流将commit文件中的treeid找到
                                  ObjectInputStream ois2 = null;
                                  ois2 = new ObjectInputStream(new FileInputStream(commitPath));
                                  commitMap = (HashMap)ois2.readObject();
                                  ois2.close();

                              //treeID的获得方法及地址
                              String Last_treeID = (String) commitMap.get("treeid");
                              String treePath = gitInit.obPath + File.separator + Last_treeID + ".txt";
                              //通过object流从tree文件中找到里面的map，就能判断是否是staged-modified
                                 ObjectInputStream ois3 = null;
                                  ois3 = new ObjectInputStream(new FileInputStream(treePath));
                                  treeMap = (HashMap)ois3.readObject();
                                  ois3.close();


                              if((!treeMap.containsKey(filename))){
                                  Staged_newfile.add(0,filename);
                              } else if (treeMap.containsKey(filename)&&(!treeMap.containsValue(fileSHA))) {
                                    Staged_Mlist.add(0,filename);
                              }

                          }


                      }

                  }



              }
             //遍历工作区所有文件
             File[] files=dir.listFiles();
             for(File f:files){//Untracked：在index的key中没有这个文件，但是工作区中有
                 if(f.isFile()){
                     String filename1=f.getName();
                     //获取文件名
                     //如果是Mac自动创建的.DS_Store文件，以及head、index、objects文件，则忽略
                     if(!filename1.equals(".DS_Store")) {
                         if (!map.containsKey(filename1)) {
                             //如果index中的map不包含这个key，说明这个文件是未追踪状态
                             Unlist.add(0, filename1);
                         }
                     }

                 }
             }

         //准备输出
                //
             if(Staged_Mlist.size()!=0 || Stage_Dlist.size()!=0 || Staged_newfile.size()!=0){
                 System.out.println("Changes to be committed");
                 if(Staged_Mlist.size()!=0){
                     System.out.println("modified:");
                     for(int i = 0; i < Staged_Mlist.size();i++){
                         System.out.print(Staged_Mlist.get(i));i++;
                     }
                 }
                if (Stage_Dlist.size()!= 0){
                    System.out.println();
                    System.out.println("deleted:");
                    for(int i = 0; i < Stage_Dlist.size();i++){
                        System.out.print(Stage_Dlist.get(i) + " ");
                    }
                }
                if(Staged_newfile.size()!=0){
                    System.out.println();
                    System.out.println("new file:");
                    for(int i = 0; i < Staged_newfile.size();i++){
                        System.out.println(Staged_newfile.get(i)+ " ");
                    }
                }
             }

             if(NS_Mlist.size()!=0|| Dlist.size()!=0){
                 System.out.println();
                 System.out.println("Changes not staged for commit");
                 if(NS_Mlist.size()!=0){
                     System.out.println("modified:");
                     for (int i = 0; i < NS_Mlist.size();i++){
                         System.out.print(NS_Mlist.get(i) + " ");
                     }
                 if (Dlist.size()!=0){
                       System.out.println();
                         System.out.println("deleted:");
                         for (int i = 0; i < Dlist.size(); i++) {
                             System.out.print(Dlist.get(i) + " ");
                         }
                     }
                 }
             }

             if(Unlist.size()!=0){
                 System.out.println();
                 System.out.println("Untracked files:");
                 for (int i = 0; i < Unlist.size();i++){
                     System.out.print(Unlist.get(i) + " ");
                 }
                 System.out.println();
             }

         }


     }
}
